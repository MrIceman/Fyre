package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import model.FireNode;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class GetRootDataAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        FirebaseTreeDialog dialog = new FirebaseTreeDialog(e.getProject());
        dialog.show();
    }


    class FirebaseTreeDialog extends DialogWrapper {


        protected FirebaseTreeDialog(@Nullable Project project) {
            super(project);
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root");
            createNodes(top);
            JTree tree = new Tree(top);
            JScrollPane pane = new JBScrollPane(tree);
            return pane;
        }

        private void createNodes(DefaultMutableTreeNode top) {
            DefaultMutableTreeNode category = null;
            DefaultMutableTreeNode book = null;

            category = new DefaultMutableTreeNode("Books for Java Programmers");
            top.add(category);

            //original Tutorial
            book = new DefaultMutableTreeNode(new FireNode("The Java Tutorial: A Short Course on the Basics"));
            category.add(book);

            //Tutorial Continued
            book = new DefaultMutableTreeNode(new FireNode
                    ("The Java Tutorial Continued: The Rest of the JDK"));
            category.add(book);

            //Swing Tutorial
            book = new DefaultMutableTreeNode(new FireNode("The Swing Tutorial: A Guide to Constructing GUIs"));
            category.add(book);

            //...add more books for programmers...

            category = new DefaultMutableTreeNode(new FireNode("Books for Java Implementers"));
            top.add(category);

            //VM
            book = new DefaultMutableTreeNode(new FireNode
                    ("The Java Virtual Machine Specification"));
            category.add(book);

            //Language Spec
            book = new DefaultMutableTreeNode(new FireNode("The Java Language Specification"));
            category.add(book);
        }
    }
}
