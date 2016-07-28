package servletc;

import org.json.JSONObject;
import servletc.http.HttpRequest;
import servletc.http.HttpResponse;
import servletc.utils.Convert;

import javax.servlet.http.HttpServlet;
import java.io.InputStream;

public class Controller {

    private Router router;
    private HttpServlet servlet;

    public Controller(String prefix) {
        router = new Router(prefix);
    }

    public void processRequest(HttpRequest req, HttpResponse res) {
        String path = req.get("pathInfo");
        path = path.equals("/") ? path : path.replace("//", "/");
        this.router.navigate(req, res, path);
    }

    public Router getRouter() {
        return this.router;
    }

    public String getPrefix() {
        return this.router.getPrefix();
    }

    public void prepare(HttpServlet servlet) {
        this.servlet = servlet;
    }

    protected JSONObject readResourceAsJson(String path) {
        return new JSONObject(
                this.readResource(path)
        );
    }

    protected String readResource(String path) {
        String content = null;
        try {
            InputStream is = this.servlet.getServletContext().getResourceAsStream(path);
            content = Convert.streamToString(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

}

