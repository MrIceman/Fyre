package commands;

import domain.VisualFire;

public class UpdateNodeCommand implements Command{
    private VisualFire app;

    public UpdateNodeCommand(VisualFire app){
        this.app = app;
    }

    @Override
    public void execute(String... data) {

    }
}
