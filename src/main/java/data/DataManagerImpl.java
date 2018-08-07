package data;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import domain.DataManager;
import model.FireNode;
import model.ObserveContract;
import model.protocol.UpdateType;
import util.FyreLogger;
import util.PathExtractor;
import util.SnapshotParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataManagerImpl extends ObserveContract.FireObservable implements DataManager {
    private FirebaseManager firebaseManager;
    private FyreLogger logger;
    private ValueEventListener rootListener;
    private boolean rootListenerAttached = false;
    private DataSnapshot lastRootSnapshot;
    private PathExtractor pathExtractor;
    private SnapshotParser snapshotParser;

    public DataManagerImpl(FirebaseManager firebaseManager) {
        this(new FyreLogger("DataManagerImpl"), firebaseManager, new SnapshotParser(), new PathExtractor());
    }

    public DataManagerImpl(FyreLogger logger, FirebaseManager firebaseManager, SnapshotParser snapshotParser, PathExtractor pathExtractor) {
        this.logger = logger;
        this.firebaseManager = firebaseManager;
        this.snapshotParser = snapshotParser;
        this.pathExtractor = pathExtractor;
        rootListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                logger.log("Received a root!");
                FireNode root = new FireNode("Root");
                buildFireTree(root, snapshot);
                updateAll(UpdateType.ROOT_DATA_LOADED, root);
                lastRootSnapshot = snapshot;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        };
    }

    public void configureFirebase(String pathToConfig) throws IOException {
        this.firebaseManager.init(pathToConfig, "https://breathe-a7606.firebaseio.com");
        this.update(UpdateType.FIREBASE_INIT_SUCCESS, null);
    }

    @Override
    public ObserveContract.FireObservable getObservable() {
        return this;
    }

    private void buildFireTree(FireNode parent, DataSnapshot snapshot) {
        FireNode node;
        if (!snapshot.hasChildren()) {
            parent.setValue(snapshot.getValue().toString());
        }

        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            node = new FireNode(dataSnapshot.getKey());
            parent.addChild(node);
            this.buildFireTree(node, dataSnapshot);
        }
    }

    @Override
    public FireNode getRoot() {
        DatabaseReference ref = this.firebaseManager.getDatabase().getReference();
        logger.log("Getting Root!");
        if (!rootListenerAttached)
            ref.addValueEventListener(rootListener);
        else {
            rootListener.onDataChange(lastRootSnapshot);
        }
        return null;
    }

    @Override
    public FireNode getNode(String node) {

        return null;
    }

    private void updateTree(String currentPath, Map<String, Object> valueMap, DataSnapshot currentSnapshot) {
        String newPath = currentPath + "/" + currentSnapshot.getKey();
        valueMap.put(newPath, currentSnapshot.getValue());
    }

    @Override
    public FireNode updateNode(String pathToNode, String value) {
        logger.log("updateNode - Path to Node: " + pathToNode + " - value: " + value);
        this.firebaseManager.getDatabase().getReference(pathToNode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            logger.log("Obtained node: " + snapshot.getKey());
                            String parentReference = pathExtractor.removeLastPath(pathToNode);
                            logger.log("New Parent Reference: " + parentReference);
                            DatabaseReference newRef = firebaseManager.getDatabase().getReference().child(parentReference).child(value);
                            Map<String, Object> valueMap = new HashMap<>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                updateTree("", valueMap, child);
                            }

                            newRef.updateChildren(valueMap, (error, ref) -> {
                                if (error != null) {
                                    logger.log("Error with merging: " + error.getMessage());
                                } else {
                                    logger.log("Successfully merged!");
                                    firebaseManager.getDatabase().getReference(pathToNode)
                                            .setValueAsync(null);
                                }
                            });
                        } else {
                            try {
                                String snapShotValue = snapshot.getValue().toString();
                                logger.log("Editing a Leaf. Value:  " + snapShotValue);
                                firebaseManager.getDatabase()
                                        .getReference(pathExtractor.removeLastPath(pathToNode)).child(value).setValue(snapshot.getValue(),
                                        (error, ref) -> {
                                            firebaseManager.getDatabase().getReference(pathToNode)
                                                    .setValueAsync(null);

                                        });

                            } catch (Exception e) {
                                logger.log("Editing a Value!");
                                DatabaseReference newRef = firebaseManager.getDatabase().getReference(pathExtractor.removeLastPath(pathToNode));
                                newRef.setValueAsync(value);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

        return null;
    }

    @Override
    public void addNode(String pathToParent, Map<String, Object> value) {
        firebaseManager.getDatabase().getReference(pathToParent).updateChildren(value, (error, ref) ->
                update(error != null ? UpdateType.ADD_NODE_FAIL : UpdateType.ADD_NODE_SUCCESS, null)
        );
    }

    @Override
    public void deleteNode(String pathToNode) {
        firebaseManager.getDatabase().getReference(pathToNode).setValue(null, (error, ref) ->
                update(error != null ? UpdateType.DELETE_NODE_FAIL : UpdateType.DELETE_NODE_SUCCESS, null)
        );
    }

    @Override
    public void moveNode(String from, String to) {
        firebaseManager.getDatabase().getReference(from).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                firebaseManager.getDatabase().getReference(to).updateChildren(snapshotParser.parseDataSnapshotToMap(snapshot),
                        (error, ref) -> {

                        });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void update(UpdateType type, FireNode data) {
        this.updateAll(type, data);
    }
}
