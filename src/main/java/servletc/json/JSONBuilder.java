package servletc.json;

import org.json.JSONObject;

/**
 * Created by Yonti on 19/06/2016.
 */
public class JSONBuilder {

    private JSONObject json;

    public JSONBuilder() {
        this.json = new JSONObject();
    }

    public JSONBuilder(JSONObject json) {
        this.json = json;
    }

    public <T> JSONBuilder put(String key, T value) {
        this.json.put(key, value);
        return this;
    }

    public JSONBuilder clean() {
        this.json = new JSONObject();
        return this;
    }

    public JSONObject build() {
        return this.json;
    }

    public static JSONBuilder instance() {
        return new JSONBuilder();
    }
}
