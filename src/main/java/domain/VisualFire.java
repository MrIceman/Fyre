package domain;


import data.FirebaseManager;
import data.impl.DataManagerImpl;
import model.FireNode;
import model.ObserveContract;
import util.FyreLogger;

import java.io.IOException;
import java.util.Map;

public class VisualFire implements ObserveContract.FireObserver {
    private String pathToCredentials;
    private DataManager dataManager;
    private FyreLogger fyreLogger;

    public VisualFire(FyreLogger fyreLogger) {
        this("src/main/java/firebase-config.json", new DataManagerImpl(new FirebaseManager(fyreLogger)), fyreLogger);
    }

    public VisualFire(String pathToCredentials, DataManager dataManager, FyreLogger fyreLogger) {
        this.fyreLogger = fyreLogger;
        this.pathToCredentials = pathToCredentials;
        this.dataManager = dataManager;
    }

    public void setUp() {
        try {
            this.dataManager.configureFirebase(this.pathToCredentials);
        } catch (IOException e) {
            fyreLogger.log(e.getMessage());
        }
    }

    public void load() {
        this.dataManager.getRoot();
    }
    public FireNode getData(String node) {
        return this.dataManager.getNode(node);
    }

    public FireNode setData(String key, Map<String, String> values) {
        return this.dataManager.updateNode(key, values);
    }

    @Override
    public void update(FireNode data) {

    }
}
