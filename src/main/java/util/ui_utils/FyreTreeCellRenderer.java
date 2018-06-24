package util.ui_utils;

import com.intellij.ui.JBColor;
import model.FireNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class FyreTreeCellRenderer extends DefaultTreeCellRenderer {

    public FyreTreeCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (leaf) {
            DefaultMutableTreeNode leafNode = (DefaultMutableTreeNode) value;
            FireNode node = (FireNode) leafNode.getUserObject();
            leafNode.add(new DefaultMutableTreeNode(node.getValue()));
            setBackground(JBColor.MAGENTA);
        }
        return this;
    }
}
