package data;


import com.google.firebase.database.*;
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

    private void mergeTrees(MutableData oldData, MutableData newData) {
        logger.log("MergeTrees");
        for (MutableData mergingData : oldData.getChildren()) {
            logger.log("Merging to the new Node  " + mergingData.getKey());
            newData.child(mergingData.getKey()).setValue(mergingData.getValue());
        }
    }

    @Override
    public FireNode updateNode(String pathToNode, String newValue) {
        String pathOfParent = pathExtractor.removeLastPath(pathToNode);
        String oldValue = pathExtractor.getLastPath(pathToNode);
        logger.log("The Path to the editing Node is: " + pathOfParent + ", oldValue: " + oldValue + ", new Value: " + newValue);
        logger.log(pathOfParent.equals("") ? "path of parents is empty" : "not empty bud");
        DatabaseReference transactionReference = pathOfParent.equals("") ? this.firebaseManager.getDatabase().getReference("")
                : this.firebaseManager.getDatabase().getReference(pathOfParent);

        transactionReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                MutableData mutableOldChild = currentData.child(oldValue);
                logger.log("Transaction Running! Key of Editing Node is " + mutableOldChild.getKey() + " with " + mutableOldChild.getChildrenCount() + " Children" + ", a value: " + mutableOldChild.getValue());
                if (mutableOldChild.hasChildren()) {
                    MutableData mutableNewChild = currentData.child(newValue);
                    mergeTrees(mutableOldChild, mutableNewChild);
                    mutableOldChild.setValue(null);
                    logger.log("Set NUll Value to mutable old child");

                } else {
                    try {
                        String snapShotValue = currentData.child(oldValue).getValue().toString();
                        logger.log("Editing a Leaf. Value:  " + snapShotValue);
                        currentData.child(newValue).setValue(snapShotValue);
                        currentData.child(oldValue).setValue(null);

                    } catch (Exception e) {
                        logger.log("Editing a Value: " + currentData.getValue());

                        if (currentData.getValue() == null) {
                            // we're editing a root node, not a leaf value
                            // currentData.child(newValue).setValue(mutableOldChild);
                            mergeTrees(currentData, currentData.child(newValue));
                            logger.log("Priority: " + currentData.getPriority());
                        } else
                            currentData.child(oldValue).setValue(newValue);
                    }
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null)
                    logger.log(error.getMessage());
                logger.log("Transaction Commit Status: " + committed + " currentData:" + currentData.getKey());

                if (error != null && !committed && pathOfParent.equals("")) {
                    /* we're handling a root value. unfortunately the firebase SDK does not support a transaction on a root node
                       and grab direct nodes. Workaround:
                     */
                    logger.log("We're most certainly editing a root value");
                    firebaseManager.getDatabase().getReference().child(oldValue).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot oldDataSnapshot) {
                            logger.log("Received a snapshot : " + oldDataSnapshot.toString());
                            // We have to grab the values
                            // 1. map all the children to a hashmap
                            // 2.insert them iterately to the new value
                            if (oldDataSnapshot.hasChildren()) {
                                logger.log("Old Data Snapshot has children");
                                Map<String, Object> valueMap = new HashMap<>();

                                for (DataSnapshot child : oldDataSnapshot.getChildren()) {
                                    updateTree("", valueMap, child);
                                }
                                logger.log("About to set a new value and new Value is " + newValue);

                                firebaseManager.getDatabase().getReference().child(newValue).setValue(valueMap, (error12, ref) -> {
                                    // remove old branch
                                    firebaseManager.getDatabase().getReference().child(oldValue).setValueAsync(null);

                                });
                            } else {
                                logger.log("no children here");
                                firebaseManager.getDatabase().getReference().child(newValue).setValue(oldDataSnapshot.getValue(),
                                        (error1, ref) -> firebaseManager.getDatabase().getReference().child(oldValue).setValueAsync(null));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            logger.log("Received an Error: " + error.getMessage());

                        }
                    });
                }
            }
        }, false);
        logger.log("updateNode - Path to Node: " + pathToNode + " - value: " + newValue);

        return null;
    }

    private void updateTree(String currentPath, Map<String, Object> valueMap, DataSnapshot currentSnapshot) {
        String newPath = currentSnapshot.getKey();
        logger.log("Current Snapshot Value: " + currentSnapshot.getValue());
        valueMap.put(newPath, currentSnapshot.getValue());
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
