package com.yonti.servletx;

import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Yonti on 18/06/2016.
 */
public class WebResponse {
    public final static String C_JSON_CONTENT_TYPE = "\"text/json; charset=UTF-8\"";

    private HttpServletResponse mResp;

    public WebResponse(HttpServletResponse resp) {
        mResp = resp;
        mResp.setStatus(202);
    }

    public HttpServletResponse getResponse() {
        return mResp;
    }

    public void sendJson(JSONObject json) {
        sendJson(json.toString());
    }

    public WebResponse setStatus(int status) {
        mResp.setStatus(status);
        return this;
    }

    public void sendJson(String json) {
        try {
            mResp.setContentType("text/json; charset=UTF-8");
            mResp.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
