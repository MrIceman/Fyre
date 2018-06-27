package plugin.forms;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import domain.VisualFire;
import plugin.configs.PluginConfigs;

import javax.swing.*;

public class VFContent {
    private JTabbedPane tabPane;
    private JPanel databasePanel;
    private JTree dataTree;
    private JTextField treeSearchInput;
    private JButton setPathButton;
    private JCheckBox realtimeUpdateCheckBox;
    private JButton addButton;
    private JButton deleteNodeButton;
    private JLabel statusText;
    private JPanel contentPanel;
    private JLabel pathLabel;
    private Project project;
    private boolean pluginConfigured = false;
    private VisualFire app;

    private void createUIComponents() {

    }

    public VFContent(Project p, VisualFire app) {
        pluginConfigured = PluginConfigs.getInstance(p).getConfigFilePath() != null;
        if (pluginConfigured) {
            pathLabel.setText(PluginConfigs.getInstance(p).getConfigFilePath());
            statusText.setVisible(false);
            dataTree.setVisible(true);
        }

        setPathButton.addActionListener(e -> {
            VirtualFile file = FileChooser.chooseFile(new FileChooserDescriptor(true, false, false, false, false, false), project, null);
            if (file != null) {
                app.setUp(file.getPath());
                PluginConfigs.getInstance(project).setConfigFilePath(file.getPath());
                pathLabel.setText(PluginConfigs.getInstance(p).getConfigFilePath());
                statusText.setVisible(false);
                dataTree.setVisible(true);
                pluginConfigured = true;
            }
        });
    }

    public JTabbedPane getTabPane() {
        return tabPane;
    }

    public void setTabPane(JTabbedPane tabPane) {
        this.tabPane = tabPane;
    }

    public JPanel getDatabasePanel() {
        return databasePanel;
    }

    public void setDatabasePanel(JPanel databasePanel) {
        this.databasePanel = databasePanel;
    }

    public JTree getDataTree() {
        return dataTree;
    }

    public void setDataTree(JTree dataTree) {
        this.dataTree = dataTree;
    }

    public JTextField getTreeSearchInput() {
        return treeSearchInput;
    }

    public void setTreeSearchInput(JTextField treeSearchInput) {
        this.treeSearchInput = treeSearchInput;
    }

    public JButton getSetPathButton() {
        return setPathButton;
    }

    public void setSetPathButton(JButton setPathButton) {
        this.setPathButton = setPathButton;
    }

    public JCheckBox getRealtimeUpdateCheckBox() {
        return realtimeUpdateCheckBox;
    }

    public void setRealtimeUpdateCheckBox(JCheckBox realtimeUpdateCheckBox) {
        this.realtimeUpdateCheckBox = realtimeUpdateCheckBox;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public void setAddButton(JButton addButton) {
        this.addButton = addButton;
    }

    public JButton getDeleteNodeButton() {
        return deleteNodeButton;
    }

    public void setDeleteNodeButton(JButton deleteNodeButton) {
        this.deleteNodeButton = deleteNodeButton;
    }

    public JLabel getStatusText() {
        return statusText;
    }

    public void setStatusText(JLabel statusText) {
        this.statusText = statusText;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }
}
