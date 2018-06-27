package plugin.forms;

import com.intellij.openapi.project.Project;
import domain.VisualFire;

import javax.swing.*;
import java.awt.event.*;

public class VFDialog extends JDialog {
    private JPanel contentPanel;
    private VFContent content;
    private JTree customTree;

    public VFDialog(Project p, VisualFire app) {
        content = new VFContent(p, app);
        customTree = content.getDataTree();
        contentPanel = content.getContentPanel();
        setContentPane(contentPanel);
        setModal(true);
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public JTree getTree() {
        return customTree;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
