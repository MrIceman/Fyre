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
    private JButton refreshButton;
    private JLabel statusText;
    private JPanel contentPanel;
    private JLabel pathLabel;
    private JTextField newNodeInput;
    private Project project;
    private boolean pluginConfigured = false;
    private VisualFire app;
    private AddNodeListener addNodeListener;

    public void setAddNodeListener(AddNodeListener addNodeListener) {
        this.addNodeListener = addNodeListener;

        addButton.addActionListener((e) -> {
                    if (newNodeInput.getText().length() > 0)
                        addNodeListener.onAddNode(newNodeInput.getText());
                }
        );
    }

    public interface AddNodeListener {
        void onAddNode(String node);

        void onDeleteNode(String path);
    }

    private void createUIComponents() {

    }

    public VFContent(Project p, VisualFire app) {
        this.project = p;
        pluginConfigured = PluginConfigs.getInstance(p).getConfigFilePath() != null;
        if (pluginConfigured) {
            pathLabel.setText(PluginConfigs.getInstance(p).getConfigFilePath());
            statusText.setVisible(false);
            dataTree.setVisible(true);
        }

        setPathButton.addActionListener(e -> {
            VirtualFile file = FileChooser.chooseFile(new FileChooserDescriptor(true, false, false, false, false, false), project, null);
            if (file != null) {
                PluginConfigs.getInstance(project).setConfigFilePath(file.getPath());
                app.setUp(file.getPath());
                pathLabel.setText(PluginConfigs.getInstance(p).getConfigFilePath());
                statusText.setVisible(false);
                dataTree.setVisible(true);
                pluginConfigured = true;
                app.load();
            }
        });

        refreshButton.addActionListener(e -> {
            if (app.isInitialized())
                app.load();
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

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public void setRefreshButton(JButton refreshButton) {
        this.refreshButton = refreshButton;
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
