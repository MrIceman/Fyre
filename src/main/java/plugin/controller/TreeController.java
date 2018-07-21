package plugin.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import domain.VisualFire;
import model.FireNode;
import model.ObserveContract;
import plugin.configs.PluginConfigs;
import plugin.forms.VFContent;
import util.FyreLogger;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

public class TreeController implements ObserveContract.FireObserver, VFContent.AddNodeListener {
    private JTree tree;
    private VisualFire app;
    private FyreLogger logger;
    private DefaultTreeModel model;
    private PluginConfigs configs;
    private String lastSelectedPath;

    public TreeController(Project project, JTree tree, VisualFire app) {
        this(project, tree, app, new FyreLogger("TreeController"));
    }

    public TreeController(Project project, JTree tree, VisualFire app, FyreLogger logger) {
        this(tree, app, logger, PluginConfigs.getInstance(project));
    }

    public TreeController(JTree tree, VisualFire app, FyreLogger logger, PluginConfigs configs) {
        this.tree = tree;
        this.app = app;
        this.logger = logger;
        this.configs = configs;
    }

    public void init() {
        app.addObserver(this);
        if (!app.isInitialized()) {
            String cachedFilePath = configs.getConfigFilePath();
            if (cachedFilePath != null) {
                app.setUp(cachedFilePath);
                logger.log("Loading Root");
                app.load();
            }
        }
        configureTree();
    }

    private void configureTree() {
        this.tree.addTreeSelectionListener(e -> {
            lastSelectedPath = getPath(e.getPath().getPath());
            logger.log("Selected path: " + lastSelectedPath);
        });

        this.model = (DefaultTreeModel) this.tree.getModel();

        model.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                if (e.getPath().length <= 2)
                    return;

                Object currentVal = e.getChildren()[0];
                updateNode(currentVal.toString());
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {

            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                logger.log("Removed a tree node");
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) { 
                logger.log("Tree structure changed!");

            }
        });
    }

    private void updateNode(String value) {
        String path = lastSelectedPath;
        logger.log("updating path: " + path + " /  value: " + value);
        app.updateData(path, value);
    }

    private String getPath(Object[] pathElements) {
        // <= 2 to ignore the first 2 default nodes which come with Swing
        if (pathElements.length <= 2)
            return "";
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < pathElements.length; i++) {
            if (i == 0 || i == 1)
                continue;
            path.append(pathElements[i].toString()).append("/");

        }
        if (path.lastIndexOf("/") == path.length() - 1) {
            path.deleteCharAt(path.lastIndexOf("/"));
        }

        return path.toString();
    }

    public void updateTree(FireNode data) {
        ProgressManager.progress("Updating UI Tree");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        DefaultMutableTreeNode newRoot = rebuildTreeNode(data);
        root.add(newRoot);
        model.reload(root);
        //if (this.lastSelectedPath != null)
        //   tree.expandPath(lastSelectedPath);
    }


    private DefaultMutableTreeNode rebuildTreeNode(FireNode rootFyreNode) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootFyreNode.getKey());
        rootNode.setUserObject(rootFyreNode);
        buildTreeRecursively(rootNode, rootFyreNode.getChildren());
        return rootNode;
    }

    private void buildTreeRecursively(DefaultMutableTreeNode parentNode,
                                      ArrayList<FireNode> children) {
        for (FireNode node : children) {
            DefaultMutableTreeNode childNode;
            childNode = new DefaultMutableTreeNode(node.getKey());
            childNode.setUserObject(node);
            buildTreeRecursively(childNode, node.getChildren());
            parentNode.add(childNode);

        }
    }

    @Override
    public void update(FireNode data) {
        updateTree(data);
    }

    @Override
    public void onAddNode(String node) {
        this.app.insert(lastSelectedPath, node);
    }

    @Override
    public void onDeleteNode(String path) {

    }
}
