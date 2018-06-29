package data;


import com.google.firebase.database.*;
import domain.DataManager;
import model.FireNode;
import model.ObserveContract;
import util.FyreLogger;
import util.PathExtractor;

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

    public DataManagerImpl(FyreLogger logger, FirebaseManager firebaseManager) {
        // todo - decouple initialzors from constructor for tests
        this.logger = logger;
        this.firebaseManager = firebaseManager;
        pathExtractor = new PathExtractor();
        rootListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                logger.log("Received a root!");
                FireNode root = new FireNode("Root");
                buildFireTree(root, snapshot);
                updateAll(root);
                lastRootSnapshot = snapshot;
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        };
    }

    public void configureFirebase(String pathToConfig) throws IOException {
        this.firebaseManager.init(pathToConfig, "https://breathe-a7606.firebaseio.com");
    }

    @Override
    public ObserveContract.FireObservable getObservable() {
        return this;
    }

    private void buildFireTree(FireNode parent, DataSnapshot snapshot) {
        FireNode node;
        if (!snapshot.hasChildren()) {
            node = new FireNode(snapshot.getValue().toString());
            parent.appendNode(node);

        }

        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            node = new FireNode(dataSnapshot.getKey());
            parent.appendNode(node);
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
        this.firebaseManager.getDatabase().getReference(pathToNode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (snapshot.hasChildren()) {
                            DatabaseReference newRef = firebaseManager.getDatabase().getReference(pathExtractor.removeLastPath(pathToNode)).child(value);
                            Map<String, Object> valueMap = new HashMap<>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                updateTree("", valueMap, child);
                            }

                            newRef.updateChildren(valueMap, (error, ref) -> {
                                if (error != null) {
                                    logger.log("Error with merging: " + error.getMessage());
                                } else {
                                    logger.log("Successfully merged!");
                                    FirebaseDatabase.getInstance().getReference(pathToNode)
                                            .setValueAsync(null);
                                }
                            });
                        } else {
                            try {
                                // We are editing a leaf key!
                                String snapShotValue = snapshot.getValue().toString();
                                logger.log("Value: " + snapShotValue);
                                FirebaseDatabase.getInstance()
                                        .getReference(pathExtractor.removeLastPath(pathToNode)).child(value).setValue(snapshot.getValue(),
                                        (error, ref) -> FirebaseDatabase.getInstance().getReference(pathToNode)
                                                .setValueAsync(null));

                            } catch (Exception e) {
                                // We are editing a leaf value!
                                DatabaseReference newRef = firebaseManager.getDatabase().getReference(pathExtractor.removeLastPath(pathToNode));
                                newRef.setValueAsync(value);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

        //  ref.updateChildrenAsync(updatedMap);
        return null;
    }

    @Override
    public void addNode(String pathToParent, String value) {

    }

    @Override
    public void deleteNode(String pathToNode) {

    }

    @Override
    public void update(FireNode data) {
        this.updateAll(data);
    }
}
