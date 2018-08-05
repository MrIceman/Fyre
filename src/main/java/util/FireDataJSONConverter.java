package util;

import model.FireNode;
import org.json.JSONObject;

public class FireDataJSONConverter {

    public String convertFireNodeToJson(FireNode node) {
        JSONObject jsonNode = new JSONObject();
        for (FireNode child : node.getChildren()) {
            if (child.getChildren().size() == 0)
                jsonNode.put(child.getKey(), child.getValue());
            else {
                addChildren(jsonNode, child);
            }
        }
        return jsonNode.toString();
    }

    private JSONObject addChildren(JSONObject jsonParent, FireNode parent) {
        for (FireNode child : parent.getChildren()) {
            if (child.getChildren().size() > 0) {
                JSONObject jsonChild = new JSONObject();
                addChildren(jsonChild, child);
                jsonParent.put(parent.getKey(), jsonChild);
            } else
                jsonParent.put(parent.getKey(), new JSONObject().put(child.getKey(), child.getValue()));
        }

        return jsonParent;
    }
}
