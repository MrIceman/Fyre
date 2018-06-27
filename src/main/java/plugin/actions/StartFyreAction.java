package plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import domain.VisualFire;
import plugin.configs.PluginConfigs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartFyreAction extends AnAction {
    private VisualFire app;

    public StartFyreAction() {
        super("Starting Fyre");
        app = VisualFire.getInstance();
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = FileChooser.chooseFile(new FileChooserDescriptor(true, false, false, false, false, false), e.getProject(), null);
        if (file != null) {
            app.setUp(file.getPath());
            PluginConfigs.getInstance(e.getProject()).setConfigFilePath(file.getPath());
        }
    }
}

