package servletc.http;

import servletc.utils.Convert;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private HttpServletRequest req;
    private Map<String, Object> mExtraData;
    private Map<String, Object> mParams;
    private Map<String, String> mQueryParams;

    public HttpRequest(HttpServletRequest req) {
        this.req = req;
        mExtraData = new HashMap<String, Object>();
        mQueryParams = extractQueryParams();
    }

    public HttpServletRequest getRequest() {
        return req;
    }


    public HttpRequest setParams(Map<String, Object> params) {
        mParams = params;
        return this;
    }

    public String getHeader(String key) {
        return this.req.getHeader(key);
    }

    public <T> T getParam(String name) {
        return (T) mParams.get(name);
    }

    public <T> void put(String key, T value) {
        mExtraData.put(key, value);
    }

    public <T> T get(String key) {
        return (T) mExtraData.get(key);
    }

    /**
     * Query params
     */

    public String getQuery(String key) {
        return mQueryParams.get(key);
    }

    public int getQueryInt(String key) {
        return Integer.parseInt(mQueryParams.get(key));
    }

    /**
     * @param def: default value
     */
    public int getQueryInt(String key, int def) {
        return Convert.toInt(mQueryParams.get(key), def);
    }

    public float getQueryFloat(String key) {
        return Float.parseFloat(mQueryParams.get(key));
    }

    /**
     * @param def: default value
     */
    public float getQueryFloat(String key, float def) {
        return Convert.toFloat(mQueryParams.get(key), def);
    }


    /**
     * PRIVATE
     */

    private Map<String, String> extractQueryParams() {
        Map<String, String> map = new HashMap<String, String>();
        String queryString = req.getQueryString();
        if (queryString != null) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
        }

        return map;
    }
}
