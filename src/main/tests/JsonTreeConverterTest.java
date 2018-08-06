import model.FireNode;
import org.junit.BeforeClass;
import org.junit.Test;
import util.FireDataJSONConverter;
import util.FyreLogger;

import static org.junit.Assert.assertEquals;

public class JsonTreeConverterTest {
    private static FireDataJSONConverter subject;

    @BeforeClass
    public static void setup() {
        subject = new FireDataJSONConverter(new FyreLogger("JsonTreeTest"));
    }

    @Test
    public void testNodeWithNoChildrenOnlyValueGetsTranslatedToJson() {
        FireNode root = createNode("root");
        root.setValue("what?");
        String result = subject.convertFireNodeToJson(root);
        assertEquals("{\"root\":\"what?\"}", result);
    }

    @Test
    public void testNodeWithPrimitiveChildrenGetsTranslatedToJson() {
        FireNode root = createNode("root");
        FireNode childA = createNode("childA");
        FireNode childB = createNode("childB");
        childA.setValue("valA");
        childB.setValue("valB");
        root.addChild(childB);
        root.addChild(childA);
        String result = subject.convertFireNodeToJson(root);
        assertEquals("{\"root\":{\"childB\":\"valB\",\"childA\":\"valA\"}}", result);
    }

    @Test
    public void testNodeWithSingleValueGetsTranslatedToJson() {
        FireNode root = createNode("root");
        FireNode childA = createNode("childA");
        childA.setValue("valA");
        root.addChild(childA);
        String result = subject.convertFireNodeToJson(root);
        assertEquals("{\"root\":{\"childA\":\"valA\"}}", result);
    }

    @Test
    public void test2LevelNodeTranslatedToJson() {
        FireNode root = createNode("root");
        FireNode childA = createNode("childA");
        FireNode attrA = createNode("attrA");
        attrA.setValue("valA");

        childA.addChild(attrA);
        root.addChild(childA);

        String result = subject.convertFireNodeToJson(root);
        assertEquals("{\"root\":{" +
                "\"childA\":{" +
                "\"attrA\":\"valA\"" +
                "}" +
                "}}", result);

    }

    @Test
    public void testMultiple2LevelNodeTranslatedToJson() {
        FireNode root = createNode("root");
        FireNode childA = createNode("childA");
        FireNode attrA = createNode("attrA");
        attrA.setValue("valA");
        childA.addChild(attrA);
        root.addChild(childA);

        FireNode childB = createNode("childB");
        FireNode attrB = createNode("attrB");
        attrB.setValue("valB");
        childB.addChild(attrB);
        root.addChild(childB);

        String result = subject.convertFireNodeToJson(root);
        assertEquals("{\"root\":{" +
                "\"childB\":{" +
                "\"attrB\":\"valB\"" +
                "}," +
                "\"childA\":{" +
                "\"attrA\":\"valA\"" +
                "}" +
                "}" +
                "}", result);

    }

    @Test
    public void test3LevelNodeTranslatedToJson() {
        FireNode root = createNode("root");
        FireNode childA = createNode("childA");
        FireNode attrA = createNode("attrA");
        FireNode propertyA = createNode("propertyA");
        propertyA.setValue("variableA");

        attrA.addChild(propertyA);
        childA.addChild(attrA);
        root.addChild(childA);

        String result = subject.convertFireNodeToJson(root);

        assertEquals("{\"root\":{" +
                "\"childA\":{" +
                "\"attrA\":{" +
                "\"propertyA\":\"variableA\"" +
                "}" +
                "}" +
                "}" +
                "}", result);
    }

    @Test
    public void testMultiple3LevelNodeTranslatedToJson() {
        FireNode root = createNode("root");
        FireNode childA = createNode("childA");
        FireNode attrA = createNode("attrA");
        FireNode propertyA = createNode("propertyA");
        propertyA.setValue("variableA");
        attrA.addChild(propertyA);
        childA.addChild(attrA);
        root.addChild(childA);

        FireNode childB = createNode("childB");
        FireNode attrB = createNode("attrB");
        FireNode propertyB = createNode("propertyB");
        propertyB.setValue("variableB");
        attrB.addChild(propertyB);
        childB.addChild(attrB);
        root.addChild(childB);

        String result = subject.convertFireNodeToJson(root);

        assertEquals("{\"root\":{" +
                "\"childB\":{" +
                "\"attrB\":{" +
                "\"propertyB\":\"variableB\"" +
                "}}" +
                "," +
                "\"childA\":{" +
                "\"attrA\":{" +
                "\"propertyA\":\"variableA\"" +
                "}" +
                "}" +
                "}}", result);
    }

    @Test
    public void test4LevelNodeTranslatedToJson() {
        FireNode root = createNode("root");
        FireNode childA = createNode("childA");
        FireNode attrA = createNode("attrA");
        FireNode propertyA = createNode("propertyA");
        FireNode variableA = createNode("variableA");
        variableA.setValue("will this stop or no");
        propertyA.addChild(variableA);
        attrA.addChild(propertyA);
        childA.addChild(attrA);
        root.addChild(childA);

        String result = subject.convertFireNodeToJson(root);

        assertEquals("{\"root\":{" +
                "\"childA\":{" +
                "\"attrA\":{" +
                "\"propertyA\":{\"variableA\":\"will this stop or no\"}" +
                "}" +
                "}" +
                "}}", result);
    }

    private FireNode createNode(String key) {
        return new FireNode(key);
    }
}
