package plugin.tools;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import domain.VisualFire;
import model.FireNode;
import model.ObserveContract;
import org.jetbrains.annotations.NotNull;
import plugin.configs.PluginConfigs;
import plugin.forms.VFContent;
import util.FyreLogger;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

@SuppressWarnings("Duplicates")
public class VFToolWindowFactory implements ToolWindowFactory, ObserveContract.FireObserver {
    private VFContent content;
    private VisualFire app;
    private FyreLogger logger;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        logger = new FyreLogger();
        app = VisualFire.getInstance();
        app.addObserver(this);
        content = new VFContent(project, VisualFire.getInstance());
        toolWindow.setTitle("VisualFire");
        if (!app.isInitialized()) {
            String cachedFilePath = PluginConfigs.getInstance(project).getConfigFilePath();
            if (cachedFilePath != null) {
                app.setUp(cachedFilePath);
                ApplicationManager.getApplication().runWriteAction(() -> {
                    logger.log("Loading Root");
                    app.load();
                });
            }
        }
    }

    @Override
    public void init(ToolWindow window) {
        Content vfContent = window.getContentManager().getFactory().createContent(content.getContentPanel(),
                "VisualFire", true);
        window.getContentManager().addContent(vfContent);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }

    @Override
    public void update(FireNode data) {
        updateUi(data);
    }

    @SuppressWarnings("Duplicates")
    private void updateUi(FireNode data) {
        ProgressManager.progress("Updating UI Tree");
        DefaultTreeModel model = (DefaultTreeModel) content.getDataTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        DefaultMutableTreeNode newRoot = rebuildTreeNode(data);
        root.add(newRoot);
        model.reload(root);
        StringBuilder currentSelectedNode = new StringBuilder();
        content.getDataTree().addTreeSelectionListener(new TreeSelectionListener() {
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
        ProgressManager.progress("Done!");
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

}
