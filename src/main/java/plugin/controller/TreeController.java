package plugin.controller;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import domain.VisualFire;
import model.FireNode;
import model.ObserveContract;
import plugin.configs.PluginConfigs;
import util.FyreLogger;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class TreeController implements ObserveContract.FireObserver {
    private JTree tree;
    private VisualFire app;
    private FyreLogger logger;
    private StringBuilder currentSelectedNode = new StringBuilder();
    private DefaultTreeModel model;
    private PluginConfigs configs;
    private TreePath lastSelectedPath;

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
            setCurrentSelectedNode(e.getPath().getLastPathComponent().toString());
            lastSelectedPath = e.getPath();
            logger.log("Last selected Path: " + lastSelectedPath.toString());
        });

        this.model = (DefaultTreeModel) this.tree.getModel();

        model.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                Object currentVal = e.getChildren()[0];
                Object[] pathToSelectedElement = e.getPath();
                updateNode(pathToSelectedElement, currentVal.toString());
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

    private void setCurrentSelectedNode(String node) {
        currentSelectedNode.delete(0, currentSelectedNode.length());
        currentSelectedNode.append(node);
    }

    private void updateNode(Object[] pathToSelectedElement, String value) {
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < pathToSelectedElement.length; i++) {
            if (i == 0 || i == 1)
                continue;
            path.append(pathToSelectedElement[i].toString()).append("/");

        }
        path.append(currentSelectedNode.toString());
        logger.log("updating path: " + path + " /  value: " + value);

        app.updateData(path.toString(), value);
    }

    public void updateTree(FireNode data) {
        ProgressManager.progress("Updating UI Tree");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        DefaultMutableTreeNode newRoot = rebuildTreeNode(data);
        root.add(newRoot);
        model.reload(root);
        if (this.lastSelectedPath != null)
            tree.expandPath(lastSelectedPath);
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
}
