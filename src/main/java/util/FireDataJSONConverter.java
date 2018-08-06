package util;

import model.FireNode;
import org.json.JSONObject;

public class FireDataJSONConverter {
    private FyreLogger logger;

    public FireDataJSONConverter() {
        this(new FyreLogger("FireDataJsonConverter"));
    }

    public FireDataJSONConverter(FyreLogger logger) {
        this.logger = logger;
    }

    public String convertFireNodeToJson(FireNode node) {
        JSONObject jsonNode = new JSONObject();
        for (FireNode child : node.getChildren()) {
            if (child.getChildren().size() == 0) {
                jsonNode.put(child.getKey(), child.getValue());
            } else {
                jsonNode.put(child.getKey(), addChildren(new JSONObject(), child));
            }
        }

        JSONObject parent = new JSONObject();
        parent.put(node.getKey(), jsonNode);
        return parent.toString();
    }

    private JSONObject addChildren(JSONObject jsonParent, FireNode parent) {
        for (FireNode child : parent.getChildren()) {
            if (child.hasChildren()) {
                JSONObject jsonChild = new JSONObject();
                addChildren(jsonChild, child);
                jsonParent.put(child.getKey(), jsonChild);
            } else {
                jsonParent.put(child.getKey(), child.getValue());

            }
        }

        return jsonParent;
    }
}
