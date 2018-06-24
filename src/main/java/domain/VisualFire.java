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
            this.pathToCredentials = pathToCredentials;
            this.dataManager.configureFirebase(this.pathToCredentials);
            this.dataManager.getObservable().addObserver(this);
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

    public FireNode setData(String key, String value) {
        return this.dataManager.updateNode(key, value);
    }

    @Override
    public void update(FireNode data) {
        this.updateAll(data);
    }
}
