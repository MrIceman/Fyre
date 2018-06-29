package domain;


import model.FireNode;
import model.ObserveContract;

import java.io.IOException;

public interface DataManager extends ObserveContract.FireObserver {
    FireNode getRoot();
    FireNode getNode(String node);
    FireNode updateNode(String pathToNode, String value);
    void addNode(String pathToParent, String value);
    void deleteNode(String pathToNode);
    void configureFirebase(String pathToConfig) throws IOException;
    ObserveContract.FireObservable getObservable();
}
