package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import domain.VisualFire;
import model.FireNode;
import model.ObserveContract;
import util.FyreLogger;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

public class GetRootDataAction extends AnAction implements ObserveContract.FireObserver {
    private VisualFire visualFire;
    private FireTree dialog;
    private FyreLogger logger;
    private final String TAG = "GetRootDataAction";


    public GetRootDataAction() {
        super("Getting Root Data");
        this.logger = new FyreLogger(TAG);
        visualFire = VisualFire.getInstance();
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        dialog = new FireTree();
        dialog.pack();
        visualFire.addObserver(this);
        ApplicationManager.getApplication().runWriteAction(() -> {
            logger.log("Loading Root");
            visualFire.load();
        });


        dialog.setVisible(true);

    }

    @Override
    public void update(FireNode data) {
        logger.log("Updating Data ");
        FireNode.printTree(data);
        updateUi(data);
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

    private void updateUi(FireNode data) {
        ProgressManager.progress("Updating UI Tree");
        DefaultTreeModel model = (DefaultTreeModel) dialog.getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        DefaultMutableTreeNode newRoot = rebuildTreeNode(data);
        root.add(newRoot);
        model.reload(root);
        StringBuilder currentSelectedNode = new StringBuilder();
        dialog.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                logger.log("Current Selected Node: " + e.getPath().getLastPathComponent().toString());
                currentSelectedNode.delete(0, currentSelectedNode.length());
                currentSelectedNode.append(e.getPath().getLastPathComponent().toString());
            }
        });
        if (model.getTreeModelListeners() != null) {
            model.addTreeModelListener(new TreeModelListener() {
                @Override
                public void treeNodesChanged(TreeModelEvent e) {
                    Object currentVal = e.getChildren()[0];
                    logger.log("getTreepath To String<: " + e.getTreePath().toString());
                    logger.log("getPath To String<: " + e.getPath().toString());
                    Object[] pathToSelectedElement = e.getPath();
                    StringBuilder path = new StringBuilder();
                    for (int i = 0; i < pathToSelectedElement.length; i++) {
                        if (i == 0 || i == 1)
                            continue;
                        path.append(pathToSelectedElement[i].toString()).append("/");

                    }
                    if (path.length() > 0)
                        path.deleteCharAt(path.length() - 1);
                    visualFire.updateData(path.toString(), currentSelectedNode.toString(), currentVal.toString());

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
        ProgressManager.progress("Done!");
    }

    private DefaultMutableTreeNode rebuildTreeNode(FireNode rootFyreNode) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootFyreNode.getKey());
        rootNode.setUserObject(rootFyreNode);
        buildTreeRecursively(rootNode, rootFyreNode.getChildren());
        return rootNode;
    }

}
