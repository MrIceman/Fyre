package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import domain.VisualFire;
import model.FireNode;
import model.ObserveContract;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

public class GetRootDataAction extends AnAction implements ObserveContract.FireObserver {
    private VisualFire visualFire;
    private FireTree dialog;


    public GetRootDataAction() {
        super("Getting Root Data");
        visualFire = VisualFire.getInstance();
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        dialog = new FireTree();
        dialog.pack();
        visualFire.addObserver(this);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                System.out.println("UI: Loading Root");
                visualFire.load();
            }
        });
        dialog.setVisible(true);

    }

    @Override
    public void update(FireNode data) {
                System.out.println("Root Data Action updating! ");
                FireNode.printTree(data);
                updateUi(data);
            }

    private void buildTreeRecursively(DefaultMutableTreeNode parentNode,
                                      ArrayList<FireNode> children) {
        for (FireNode node : children) {
            DefaultMutableTreeNode childNode;
            if(node.getChildren().isEmpty() && node.getValue() != null) {
                childNode = new DefaultMutableTreeNode(node.getValue());
            }
            else {
                childNode = new DefaultMutableTreeNode(node.getKey());
                buildTreeRecursively(childNode, node.getChildren());
            }
            parentNode.add(childNode);

        }
    }

    public void updateUi(FireNode data) {
        ProgressManager.progress("Updating UI Tree");
        DefaultTreeModel model = (DefaultTreeModel) dialog.getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        DefaultMutableTreeNode newRoot = rebuildTreeNode(data);
        root.add(newRoot);
        model.reload(root);
        ProgressManager.progress("Done!");
    }

    private DefaultMutableTreeNode rebuildTreeNode(FireNode rootFyreNode) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootFyreNode.getKey());
        buildTreeRecursively(rootNode, rootFyreNode.getChildren());

        return rootNode;
    }

}
