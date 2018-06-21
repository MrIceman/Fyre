package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import domain.VisualFire;
import util.FyreLogger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartFyreAction extends AnAction {
    private VisualFire app;

    public StartFyreAction(){
        super("Starting Fyre");
        app = new VisualFire(new FyreLogger());
    }
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

        VirtualFile file = FileChooser.chooseFile(new FileChooserDescriptor(true, false, false, false, false, false), e.getProject(), null);
        if (file != null)
            app.setUp(file.getPath());
        app.load();
        Messages.showInputDialog(project, "Please specify the path to your credentials JSON file", "Fyre - Setup", Messages.getInformationIcon());
        Map<String, String> map = new HashMap();
        map.put("date", new Date() + "");
        app.setData("Fyre_Plugin_Launch", map);
    }
}
