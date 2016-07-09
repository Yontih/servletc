package servletc.http;

import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Yonti on 18/06/2016.
 */
public class HttpResponse {
    public final static String C_JSON_CONTENT_TYPE = "\"text/json; charset=UTF-8\"";

    private HttpServletResponse resp;
    private HttpServletRequest req;

    public HttpResponse(HttpServletResponse resp, HttpServletRequest req) {
        this.resp = resp;
        this.req = req;
        this.resp.setStatus(202);
    }

    public HttpServletResponse getResponse() {
        return resp;
    }

    public HttpResponse setStatus(int status) {
        resp.setStatus(status);
        return this;
    }

    public <T> HttpResponse setHeader(String key, T value) {
        resp.setHeader(key, value.toString());
        return this;
    }

    public void sendJson(String json) {
        resp.setContentType("text/json; charset=UTF-8");
        this.send(json);
    }

    public void sendJson(JSONObject json) {
        sendJson(json.toString());
    }

    public void sendHtml(String html) {
        resp.setContentType("text/html");
        send(html);
    }

    public void sendHtmlFile(String path) {
        resp.setContentType("text/html");
        RequestDispatcher view = req.getRequestDispatcher(path);
        try {
            view.forward(req, resp);
        } catch (IOException e) {

        } catch (ServletException e) {

        }
    }

    public void end() {
        send("");
    }

    public void send(String value) {
        try {
            resp.getWriter().println(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendError(int code) {
        try {
            resp.sendError(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
