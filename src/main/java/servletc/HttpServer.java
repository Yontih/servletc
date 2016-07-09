package servletc;

import servletc.exceptions.DuplicatePathException;
import servletc.http.HttpRequest;
import servletc.http.HttpResponse;
import servletc.middlewares.Middleware;
import servletc.util.Merger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer extends HttpServlet {

    private class CM {
        private List<Controller> mControllers;
        private List<Middleware> mMiddlewares;
        private String mPath;

        public CM(String path) {
            mControllers = new ArrayList<>();
            mMiddlewares = new ArrayList<>();
            mPath = path;
        }

        public List<Controller> getControllers() {
            return mControllers;
        }

        public List<Middleware> getMiddlewares() {
            return mMiddlewares;
        }

        public String getPath() {
            return mPath;
        }
    }

    private Map<String, CM> mPathObjects;
    protected Controller mDefaultCtrl;

    public HttpServer() {
        mPathObjects = new HashMap<>();
    }

    /**
     * @param c: Will use as the default controller, will invoke if no other controllers where found (best practice will be controller without prefix").
     */
    public HttpServer setDefaultCtrl(Controller c) {
        mDefaultCtrl = c;
        this.addController("", c);
        return this;
    }

    public HttpServer useController(String path, Controller c) {
        this.addController(path, c);
        return this;
    }

    public HttpServer useControllers(String path, Controller... controllers) {
        for (Controller c : controllers) {
            this.addController(path, c);
        }

        return this;

    }

    public HttpServer useController(Controller c) {
        this.addController("", c);
        return this;
    }

    public HttpServer useControllers(Controller... controllers) {
        for (Controller c : controllers) {
            useController(c);
        }

        return this;
    }

    public HttpServer useMiddleware(String path, Middleware m) {
        addMiddleware(path, m);
        return this;
    }

    public HttpServer useMiddleware(String path, Middleware... middlewares) {
        for (Middleware m : middlewares) {
            addMiddleware(path, m);
        }

        return this;
    }

    public HttpServer useMiddleware(Middleware middleware) {
        addMiddleware("", middleware);
        return this;
    }

    public HttpServer useMiddleware(Middleware... middlewares) {
        for (Middleware middleware : middlewares) {
            useMiddleware(middleware);
        }

        return this;
    }

    private void addController(String path, Controller c) {
        CM cm = mPathObjects.get(path);

        if (cm == null) {
            cm = new CM(path);
            mPathObjects.put(path, cm);
        }

        cm.getControllers().add(c);
    }

    private void addMiddleware(String path, Middleware m) {
        CM cm = mPathObjects.get(path);

        if (cm == null) {
            cm = new CM(path);
            mPathObjects.put(path, cm);
        }

        cm.getMiddlewares().add(m);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*String debug = new JSONBuilder()
                .put("url", req.getRequestURL().toString())
                .put("path", req.getPathInfo())
                .put("queryString", req.getQueryString())
                .put("server", req.getServerName())
                .put("port", req.getServerPort())
                .put("protocol", req.getScheme())
                .put("prefix", parsePrefix(req))
                .put("method", req.getMethod())
                .build().toString();*/
/*
        JSONObject json = new JSONObject();
        try {
            json.put("url", req.getRequestURL().toString());
            json.put("path", req.getPathInfo());
            json.put("queryString", req.getQueryString());
            json.put("server", req.getServerName());
            json.put("port", req.getServerPort());
            json.put("protocol", req.getScheme());
            json.put("prefix", parsePrefix(req));
            json.put("method", req.getMethod());
            System.out.println(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/

//        System.out.println(req.getRequestURL().toString());
//        System.out.println(req.getPathInfo());
        invokeController(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invokeController(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invokeController(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invokeController(req, resp);
    }

    protected void invokeController(HttpServletRequest req, HttpServletResponse resp) {
        HttpRequest request = new HttpRequest(req);
        HttpResponse response = new HttpResponse(resp, req);
        String pathInfo = req.getPathInfo();
        CM cm = findPathObject(pathInfo);

        if (cm != null) {
            pathInfo = pathInfo.replace(cm.getPath(), "");
            Controller controller = findController(cm.getControllers(), pathInfo);
            if (controller != null) {
                request.put("pathInfo", pathInfo);
                List<Middleware> middlewares;

                if (cm.getPath() == "") {
                    middlewares = cm.getMiddlewares();
                } else {
                    // merge default middlewares with the path middlewares
                    List<Middleware> defMiddlewares = mPathObjects.get("").getMiddlewares();
                    middlewares = Merger.mergeMiddlewares(defMiddlewares, cm.getMiddlewares());
                }

                // middlewares logic: return true if next middleware or http action should invoke.
                boolean next = true;
                for (Middleware middleware : middlewares) {
                    next = middleware.invoke(request, response);
                    if (!next) {
                        break;
                    }
                }

                if (next) {
                    controller.processRequest(request, response);
                }
            } else {
                response.sendError(404);
            }
        } else {
            response.sendError(404);
        }

    }

    protected String parsePrefix(String path) {
        String[] allPaths = path.split("/");

        return allPaths.length > 0 ? allPaths[1] : path;
    }

    private CM findPathObject(String path) {
        CM cm = mPathObjects.get(path);

        while (cm == null && !path.isEmpty()) {
            int index = path.lastIndexOf("/");
            path = path.substring(0, index);
            cm = mPathObjects.get(path);
        }

        if (cm == null) {
            // default
            System.out.println("Default cm selected");
            cm = mPathObjects.get("");
        }

        return cm;
    }

    private Controller findController(List<Controller> controllers, String path) {
        String prefix = parsePrefix(path);
        Controller controller = null;

        for (Controller c : controllers) {
            String routePrefix = c.getPrefix();
            if (routePrefix.equals(prefix) || routePrefix.equals("/" + prefix)) {
                controller = c;
                break;
            }
        }

        if (controller == null) {
            System.out.println("Default controller selected");
            controller = mDefaultCtrl;
        }

        return controller;
    }
}
