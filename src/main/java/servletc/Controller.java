package servletc;

import servletc.http.HttpRequest;
import servletc.http.HttpResponse;

public class Controller {

    private Router mRouter;

    public Controller(String prefix) {
        mRouter = new Router(prefix);
    }

    public void processRequest(HttpRequest req, HttpResponse resp) {
        String path = req.get("pathInfo");
        path = path.equals("/") ? path : path.replace("//", "/");
        mRouter.navigate(req, resp, path);
    }

    public Router getRouter() {
        return mRouter;
    }
    public String getPrefix() {
        return mRouter.getPrefix();
    }

}

