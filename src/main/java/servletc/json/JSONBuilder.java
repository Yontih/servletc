package servletc.json;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;

/**
 * Created by Yonti on 19/06/2016.
 */
public class JSONBuilder {

    private JSONObject json;
    private final static DateTimeFormatter DEFAULT_FORMAT = ISODateTimeFormat.dateTime();

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

    public JSONBuilder putDate(String key, DateTime dt) {
        return putDate(key, dt, DEFAULT_FORMAT);
    }

    public JSONBuilder putDate(String key, DateTime dt, DateTimeFormatter formatter) {
        this.json.put(key, dt.toString(formatter));
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
