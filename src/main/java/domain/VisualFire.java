package domain;


import data.DataManagerImpl;
import data.FirebaseManager;
import model.FireNode;
import model.ObserveContract;
import model.protocol.UpdateType;
import util.FyreLogger;

import java.io.IOException;
import java.util.Map;

public class VisualFire extends ObserveContract.FireObservable implements ObserveContract.FireObserver {
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
        this(new DataManagerImpl(new FirebaseManager(fyreLogger)), fyreLogger);
    }

    public VisualFire(DataManager dataManager, FyreLogger fyreLogger) {
        this.fyreLogger = fyreLogger;
        this.dataManager = dataManager;
    }

    public void setUp(String pathToCredentials) {
        try {
            this.dataManager.getObservable().addObserver(this);
            this.dataManager.configureFirebase(pathToCredentials);
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

    public void insert(String path, Map<String, Object> value) {
        this.dataManager.addNode(path, value);
    }

    public FireNode updateData(String path, String value) {
        return this.dataManager.updateNode(path, value);
    }

    @Override
    public void update(UpdateType type, FireNode data) {
        if (type != UpdateType.FIREBASE_INIT_SUCCESS)
            this.updateAll(type, data);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
