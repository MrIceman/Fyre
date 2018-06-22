package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import domain.VisualFire;
import model.FireNode;
import model.ObserveContract;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class GetRootDataAction extends AnAction implements ObserveContract.FireObserver {
    private VisualFire visualFire;
    private FirebaseTreeDialog dialog;
    private JBScrollPane pane;
    private Tree tree;


    public GetRootDataAction() {
        super("Getting Root Data");
        visualFire = VisualFire.getInstance();
        visualFire.addObserver(this);
    }
    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        dialog = new FirebaseTreeDialog(e.getProject());
        dialog.show();
        VisualFire.getInstance().load();
    }

    @Override
    public void update(FireNode data) {
        System.out.println("Root Data Action updating!");
        dialog.updateUi(data);
    }


    class FirebaseTreeDialog extends DialogWrapper {

        protected FirebaseTreeDialog(@Nullable Project project) {
            super(project);
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            System.out.println("Creating Cenger Panel");
            DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root");
            createNodes(top);
            pane = new JBScrollPane(tree);
            return pane;
        }

        private void buildTreeRecursively(DefaultMutableTreeNode parentNode,
                                                            ArrayList<FireNode> children) {
            for(FireNode node : children) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node.getKey());
                parentNode.add(childNode);
                buildTreeRecursively(childNode, node.getChildren());
            }
        }

        public void updateUi(FireNode data) {
            tree.add(rebuildTree(data));
            pane.repaint();
        }

        private Tree rebuildTree(FireNode rootFyreNode) {
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootFyreNode.getKey());
            buildTreeRecursively(rootNode, rootFyreNode.getChildren());

            return new Tree(rootNode);
        }

        private void createNodes(DefaultMutableTreeNode top) {

        }
    }
}
