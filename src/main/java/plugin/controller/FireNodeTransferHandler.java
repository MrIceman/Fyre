package plugin.controller;

import model.FireNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.DataFlavor;

public class FireNodeTransferHandler extends TransferHandler {
    private DataFlavor dataFlavor = new DataFlavor(DefaultMutableTreeNode.class, "FireNode");

    public FireNodeTransferHandler() {
    }

    @Override
    public boolean canImport(TransferSupport support) {
        System.out.println("Dropped yo");

    /*    JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
        System.out.println("Dropped yo");
        if (support.getTransferable() != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) support.getTransferable();
            FireNode item = (FireNode) node.getUserObject();
            System.out.println("Node Key: " + item.toString());

        }*/
        return true;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        System.out.println("Import data");
        return true;
            /*
    if (!canImport(support)) {
        return false;
    }
    JTree.DropLocation dropLocation =
            (JTree.DropLocation)support.getDropLocation();
    TreePath path = dropLocation.getPath();
    Transferable transferable = support.getTransferable();
    String transferData;
    try {
        transferData = (String)transferable.getTransferData(DataFlavor.stringFlavor);
    } catch (IOException e) {
        return false;
    } catch (UnsupportedFlavorException e) {
        return false;
    }
    int childIndex = dropLocation.getChildIndex();
    if (childIndex == -1) {
        childIndex = model.getChildCount(path.getLastPathComponent());
    }
    DefaultMutableTreeNode newNode =
            new DefaultMutableTreeNode(transferData);
    DefaultMutableTreeNode parentNode =
            (DefaultMutableTreeNode)path.getLastPathComponent();
    model.insertNodeInto(newNode, parentNode, childIndex);
    TreePath newPath = path.pathByAddingChild(newNode);
    tree.makeVisible(newPath);
    tree.scrollRectToVisible(tree.getPathBounds(newPath));
    return true;*/
    }

}
