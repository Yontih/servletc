package servletx.http;

import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Yonti on 18/06/2016.
 */
public class HttpResponse {
    public final static String C_JSON_CONTENT_TYPE = "\"text/json; charset=UTF-8\"";

    private HttpServletResponse mResp;

    public HttpResponse(HttpServletResponse resp) {
        mResp = resp;
        mResp.setStatus(202);
    }

    public HttpServletResponse getResponse() {
        return mResp;
    }

    public void sendJson(JSONObject json) {
        sendJson(json.toString());
    }

    public HttpResponse setStatus(int status) {
        mResp.setStatus(status);
        return this;
    }

    public void sendJson(String json) {
        mResp.setContentType("text/json; charset=UTF-8");
        send(json);
    }

    public void sendHtml(String html) {
        mResp.setContentType("text/html");
        send(html);
    }

    public void send(String value) {
        try {
            mResp.getWriter().println(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendError(int code) {
        try {
            mResp.sendError(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
