import model.FireNode;
import org.junit.BeforeClass;
import org.junit.Test;
import util.FireDataJSONConverter;

import static org.junit.Assert.assertEquals;

public class JsonTreeConverterTest {
    private static FireDataJSONConverter subject;

    @BeforeClass
    public static void setup() {
        subject = new FireDataJSONConverter();
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
        assertEquals("{\"childB\":\"valB\",\"childA\":\"valA\"}", result);
    }

    @Test
    public void testNodeWithSingleValueGetsTranslatedToJson() {
        FireNode root = createNode("root");
        FireNode childA = createNode("childA");
        childA.setValue("valA");
        root.addChild(childA);
        String result = subject.convertFireNodeToJson(root);
        assertEquals("{\"childA\":\"valA\"}", result);
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
        assertEquals("{" +
                "\"childA\":{" +
                "\"attrA\":\"valA\"" +
                "}" +
                "}", result);

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
        assertEquals("{" +
                "\"childB\":{" +
                "\"attrB\":\"valB\"" +
                "}," +
                "\"childA\":{" +
                "\"attrA\":\"valA\"" +
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

        assertEquals("{" +
                "\"childA\":{" +
                "\"attrA\":{" +
                "\"propertyA\":\"variableA\"" +
                "}" +
                "}" +
                "}", result);
    }

    private FireNode createNode(String key) {
        return new FireNode(key);
    }
}
