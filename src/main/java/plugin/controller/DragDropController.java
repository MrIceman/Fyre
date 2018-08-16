package plugin.controller;

import util.FyreLogger;

import javax.swing.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;

public class DragDropController implements DragGestureListener {
    private JTree tree;
    private FyreLogger logger;

    public DragDropController(JTree tree) {
        this(tree, new FyreLogger("DragDropController"));
    }

    public DragDropController(JTree tree, FyreLogger logger) {
        this.tree = tree;
        this.logger = logger;
    }


    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {

    }
}


