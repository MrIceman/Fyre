package domain;


import data.FirebaseManager;
import data.impl.DataManagerImpl;
import model.FireNode;
import model.ObserveContract;
import util.FyreLogger;

import java.io.IOException;
import java.util.Map;

public class VisualFire extends ObserveContract.FireObservable implements ObserveContract.FireObserver {
    private String pathToCredentials;
    private DataManager dataManager;
    private FyreLogger fyreLogger;
    private static VisualFire instance;
    private boolean initialized;

    public static VisualFire getInstance() {
        if (instance == null)
            instance = new VisualFire(new FyreLogger());
        return instance;
    }

    public VisualFire(FyreLogger fyreLogger) {
        this(new DataManagerImpl(fyreLogger, new FirebaseManager(fyreLogger)), fyreLogger);
    }

    public VisualFire(DataManager dataManager, FyreLogger fyreLogger) {
        this.fyreLogger = fyreLogger;
        this.dataManager = dataManager;
    }

    public void setUp(String pathToCredentials) {
        try {
            this.pathToCredentials = pathToCredentials;
            this.dataManager.configureFirebase(this.pathToCredentials);
            this.dataManager.getObservable().addObserver(this);
            initialized = true;
        } catch (IOException e) {
            fyreLogger.log(e.getMessage());
            initialized = false;
        }
    }

    public void load() {
        this.dataManager.getRoot();
    }

    public FireNode getData(String node) {
        return this.dataManager.getNode(node);
    }

    public FireNode setData(String key, String value) {
        return this.dataManager.updateNode(key, value);
    }

    public FireNode updateData(String path, String oldValue, String value) {
        return this.dataManager.updateNode(path, value, oldValue);
    }

    public void renameNode(String pathToNode, String newName) {
        this.dataManager.renameNode(pathToNode, newName);
    }

    @Override
    public void update(FireNode data) {
        this.updateAll(data);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
