package plugin.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import domain.VisualFire;
import org.jetbrains.annotations.NotNull;
import plugin.controller.TreeController;
import plugin.forms.VFContent;

public class VFToolWindowFactory implements ToolWindowFactory{
    private VFContent content;
    private TreeController treeController;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        content = new VFContent(project, VisualFire.getInstance());
        treeController = new TreeController(project, content.getDataTree(), VisualFire.getInstance());
        content.setAddNodeListener(treeController);
        treeController.init();
        toolWindow.setTitle("VisualFire");
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
}
