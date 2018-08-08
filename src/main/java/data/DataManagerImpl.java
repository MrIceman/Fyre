package data;


import com.google.firebase.database.*;
import com.google.firebase.database.core.SnapshotHolder;
import domain.DataManager;
import model.FireNode;
import model.ObserveContract;
import model.protocol.UpdateType;
import org.jetbrains.annotations.NotNull;
import util.FyreLogger;
import util.PathExtractor;
import util.SnapshotParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class DataManagerImpl extends ObserveContract.FireObservable implements DataManager {
    private FirebaseManager firebaseManager;
    private FyreLogger logger;
    private ValueEventListener rootListener;
    private boolean rootListenerAttached = false;
    private DataSnapshot lastRootSnapshot;
    private PathExtractor pathExtractor;
    private SnapshotParser snapshotParser;
    private boolean runningTransaction = false;

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

    private void updateTree(String currentPath, Map<String, Object> valueMap, MutableData currentSnapshot) {
        String newPath = currentPath + "/" + currentSnapshot.getKey();
        valueMap.put(newPath, currentSnapshot.getValue());
    }

    @Override
    public FireNode updateNode(String pathToNode, String value) {
        String pathOfParent = pathExtractor.removeLastPath(pathToNode);
        String oldValue = pathExtractor.getLastPath(pathToNode);

        Map<String, Object> valueMap = new HashMap<>();
        logger.log("The Path to the editing Node is : " + pathOfParent);

        this.firebaseManager.getDatabase().getReference(pathOfParent).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                runningTransaction = true;
                MutableData mutableOldChild = currentData.child(oldValue);
                logger.log("Transaction Running! " + currentData.getKey() + " with " + currentData.getChildrenCount() + " Children");
                if (mutableOldChild.hasChildren()) {
                    MutableData mutableNewChild = currentData.child(value);
                    for (MutableData child : currentData.getChildren()) {
                        updateTree("", valueMap, child);
                    }
                    mutableOldChild.setValue(null);
                    mutableNewChild.setValue(valueMap);

                } else {
                    try {
                        String snapShotValue = currentData.child(oldValue).getValue().toString();
                        logger.log("Editing a Leaf. Value:  " + snapShotValue);
                        currentData.child(value).setValue(snapShotValue);
                        currentData.child(oldValue).setValue(null);

                    } catch (Exception e) {
                        logger.log("Editing a Value!");
                        DatabaseReference newRef = firebaseManager.getDatabase().getReference(pathExtractor.removeLastPath(pathToNode));
                        newRef.setValueAsync(null).addListener(() -> runningTransaction = false, Runnable::run);
                    }
                }

                //  waitUntilTransactionFinishes();
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                logger.log("Transaction Commit Status: " + committed + " currentData:" + currentData.getKey());
            }
        }, false);
        logger.log("updateNode - Path to Node: " + pathToNode + " - value: " + value);

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

    private void waitUntilTransactionFinishes() {
        while (runningTransaction) ;
    }
}
