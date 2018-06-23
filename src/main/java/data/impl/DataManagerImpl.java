package data.impl;


import com.google.firebase.database.*;
import data.FirebaseManager;
import domain.DataManager;
import model.FireNode;
import model.ObserveContract;
import util.FyreLogger;

import java.io.IOException;
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
            this.buildFireTree(node, dataSnapshot);

            parent.appendNode(node);

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
    public FireNode updateNode(String node, Map<String, String> values) {
        DatabaseReference ref = this.firebaseManager.getDatabase().getReference(node);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        ref.setValueAsync(values);
        return null;
    }

    @Override
    public void update(FireNode data) {
        this.updateAll(data);
    }
}
