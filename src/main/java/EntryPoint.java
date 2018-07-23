import domain.VisualFire;
import util.FyreLogger;

public class EntryPoint {

    // todo
    // make visual tree copy as JSON
    // update node without removing children
    public static void main(String[] args) {

        System.out.println("Hello World");
        VisualFire app = new VisualFire(new FyreLogger());
        app.setUp("src/main/java/firebase-config.json");
        app.load();

       //  app.insert("this/is/a/test/launch", "hello");
        while (true) {
        }
    }
}
