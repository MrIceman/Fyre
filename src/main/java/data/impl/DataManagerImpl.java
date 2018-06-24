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

    @Override
    public FireNode updateNode(String pathToNode, String value) {
        DatabaseReference ref = this.firebaseManager.getDatabase().getReference();
        Map<String, Object> updatedMap = new HashMap<>();
        updatedMap.put(pathToNode, value);
        ref.updateChildrenAsync(updatedMap);
        return null;
    }

    @Override
    public void renameNode(String pathToNode, String name) {

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
