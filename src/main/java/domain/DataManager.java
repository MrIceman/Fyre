package domain;


import model.FireNode;
import model.ObserveContract;

import java.io.IOException;
import java.util.Map;

public interface DataManager extends ObserveContract.FireObserver {
    FireNode getRoot();
    FireNode getNode(String node);
    FireNode updateNode(String node, Map<String, String> values);
    void configureFirebase(String pathToConfig) throws IOException;
    ObserveContract.FireObservable getObservable();
}
