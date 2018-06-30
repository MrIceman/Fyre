package domain;


import data.DataManagerImpl;
import data.FirebaseManager;
import model.FireNode;
import model.ObserveContract;
import util.FyreLogger;

import java.io.IOException;

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

    public void insert(String path, String value) {
        this.dataManager.addNode(path, value);
    }

    public FireNode updateData(String path, String value) {
        return this.dataManager.updateNode(path, value);
    }

    @Override
    public void update(FireNode data) {
        this.updateAll(data);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
