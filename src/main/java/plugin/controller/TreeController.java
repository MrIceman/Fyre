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
import java.util.ArrayList;

public class TreeController implements ObserveContract.FireObserver {
    private JTree tree;
    private VisualFire app;
    private FyreLogger logger;
    private StringBuilder currentSelectedNode = new StringBuilder();
    private DefaultTreeModel model;
    private Project project;
    private PluginConfigs configs;

    public TreeController(Project project, JTree tree, VisualFire app) {
        this(project, tree, app, new FyreLogger("TreeController"));
    }

    public TreeController(Project project, JTree tree, VisualFire app, FyreLogger logger) {
        this(project, tree, app, logger, PluginConfigs.getInstance(project));
    }

    public TreeController(Project project, JTree tree, VisualFire app, FyreLogger logger, PluginConfigs configs) {
        this.tree = tree;
        this.app = app;
        this.logger = logger;
        this.project = project;
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
            logger.log("Current Selected Node: " + e.getPath().getLastPathComponent().toString());
            currentSelectedNode.delete(0, currentSelectedNode.length());
            currentSelectedNode.append(e.getPath().getLastPathComponent().toString());
        });

        this.model = (DefaultTreeModel) this.tree.getModel();

        model.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                Object currentVal = e.getChildren()[0];
                Object[] pathToSelectedElement = e.getPath();
                StringBuilder path = new StringBuilder();
                for (int i = 0; i < pathToSelectedElement.length; i++) {
                    if (i == 0 || i == 1)
                        continue;
                    path.append(pathToSelectedElement[i].toString()).append("/");

                }
                if (path.length() > 0)
                    path.deleteCharAt(path.length() - 1);
                app.updateData(path.toString(), currentSelectedNode.toString(), currentVal.toString());

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

    public void updateTree(FireNode data) {

        ProgressManager.progress("Updating UI Tree");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        DefaultMutableTreeNode newRoot = rebuildTreeNode(data);
        root.add(newRoot);
        model.reload(root);

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
