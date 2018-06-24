import domain.VisualFire;
import util.FyreLogger;

import java.util.HashMap;
import java.util.Map;

public class EntryPoint {

    public static void main(String[] args) {

        System.out.println("Hello World");
        VisualFire app = new VisualFire(new FyreLogger());
        app.setUp("src/main/java/firebase-config.json");
        app.load();

        app.setData("this/is/a/test/launch", "hello");
        while(true){
        }
        }
}
