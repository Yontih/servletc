package servletx.http;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yonti on 18/06/2016.
 */
public class HttpRequest {
    private HttpServletRequest mReq;
    private Map<String, Object> mExtraData;
    private Map<String, Object> mParams;

    public HttpRequest(HttpServletRequest req) {
        mReq = req;
        mExtraData = new HashMap<String, Object>();
    }

    public HttpServletRequest getRequest() {
        return mReq;
    }

    public Map<String, String> getQueryParams() {
        Map<String, String> map = new HashMap<String, String>();
        String queryString = mReq.getQueryString();
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

    public void setParams(Map<String, Object> params) {
        mParams = params;
    }

    public <T> void put(String key, T value) {
        mExtraData.put(key, value);
    }

    public <T> T get(String key) {
        return (T) mExtraData.get(key);
    }
}
