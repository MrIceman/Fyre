package domain;


import model.FireNode;
import model.ObserveContract;

import java.io.IOException;
import java.util.Map;

public interface DataManager extends ObserveContract.FireObserver {
    FireNode getRoot();
    public FireNode getNode(String node);
    public FireNode updateNode(String node, Map<String, String> values);
    public void configureFirebase(String pathToConfig) throws IOException;
    public ObserveContract.FireObservable getObservable();
}
