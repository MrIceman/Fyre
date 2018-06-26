package data.impl;


import com.google.firebase.database.*;
import data.FirebaseManager;
import domain.DataManager;
import model.FireNode;
import model.ObserveContract;
import util.FyreLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataManagerImpl extends ObserveContract.FireObservable implements DataManager {
    private FirebaseManager firebaseManager;
    private FyreLogger logger;

    public DataManagerImpl(FirebaseManager firebaseManager) {
        this.logger = new FyreLogger();
        this.firebaseManager = firebaseManager;
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

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                logger.log("Received a root!");
                FireNode root = new FireNode("Root");
                buildFireTree(root, snapshot);
                updateAll(root);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
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
        DatabaseReference ref = this.firebaseManager.getDatabase().getReference();
        this.firebaseManager.getDatabase().getReference(pathToNode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        logger.log("Current node:" + snapshot.getKey());
                        logger.log("Updating Node has : " + snapshot.getChildrenCount() + " values");
                        Map<String, Object> valueMap = new HashMap<>();
                        valueMap.put(pathToNode, value);
                        for (DataSnapshot child : snapshot.getChildren()) {
                            updateTree(pathToNode, valueMap, child);
                        }

                        FirebaseDatabase.getInstance().getReference().updateChildren(valueMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                if (error != null) {
                                    logger.log("Error with merging: " + error.getMessage());
                                    logger.log(error.toString());
                                } else
                                    logger.log("Successfully merged!");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

        //  ref.updateChildrenAsync(updatedMap);
        return null;
    }

    @Override
    public FireNode updateNode(String pathToNode, String value, String oldValue) {
        //noinspection Duplicates
        this.firebaseManager.getDatabase().getReference(pathToNode).child(oldValue)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressWarnings("Duplicates")
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (snapshot.hasChildren()) {
                            DatabaseReference newRef = firebaseManager.getDatabase().getReference(pathToNode).child(value);
                            Map<String, Object> valueMap = new HashMap<>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                updateTree("", valueMap, child);
                            }

                            newRef.updateChildren(valueMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    if (error != null) {
                                        logger.log("Error with merging: " + error.getMessage());
                                        logger.log(error.toString());
                                    } else
                                        logger.log("Successfully merged!");
                                }
                            });
                        } else
                            snapshot.getRef().setValueAsync(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

        //  ref.updateChildrenAsync(updatedMap);
        return null;
    }

    @Override
    public void renameNode(String pathToNode, String name) {
        DatabaseReference root = this.firebaseManager.getDatabase().getReference(pathToNode);
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                FireNode root = new FireNode(name);
                root.setPath(pathToNode);
                for (DataSnapshot child : snapshot.getChildren()) {
                    buildFireTree(root, child);
                }

                updateNode(pathToNode, name);

                for (FireNode node : root.getChildren()) {
                    updateNode(node.getPath(), node.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
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
