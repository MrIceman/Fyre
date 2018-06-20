import domain.VisualFire;
import util.FyreLogger;

import java.util.HashMap;
import java.util.Map;

public class EntryPoint {

    public static void main(String[] args) {

        System.out.println("Hello World");
        VisualFire app = new VisualFire(new FyreLogger());
        app.setUp();
        Map<String, String> testData = new HashMap();
        testData.put("Material", "Wood");
        testData.put("Bought", "12-06-1993");
        app.load();

        app.setData("instruments/guitar/acoustic", testData);
        while(true){
        }
        }
}
