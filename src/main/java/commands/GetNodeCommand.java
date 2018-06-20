package commands;


import domain.VisualFire;

public class GetNodeCommand implements Command{
    private VisualFire app;

    public GetNodeCommand(VisualFire app){
        this.app = app;
    }

    @Override
    public void execute(String... data) {
    }
}
