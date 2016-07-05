package servletc;

import servletc.http.HttpRequest;
import servletc.http.HttpResponse;
import servletc.middlewares.Middleware;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class HttpServer extends HttpServlet {
    private ArrayList<Controller> mControllers;
    private ArrayList<Middleware> mMiddlewares;
    protected Controller mDefaultCtrl;

    public HttpServer() {
        mControllers = new ArrayList<>();
        mMiddlewares = new ArrayList<>();
    }

    /**
     * @param c: Will use as the default controller, will invoke if no other controllers where found (best practice will be controller without prefix").
     */
    public HttpServer setDefaultCtrl(Controller c) {
        mDefaultCtrl = c;
        return this;
    }

    public HttpServer useController(Controller c) {
        mControllers.add(c);
        return this;
    }

    public HttpServer useControllers(Controller... controllers) {
        for (Controller c : controllers) {
            useController(c);
        }

        return this;
    }

    public HttpServer useMiddleware(Middleware middleware) {
        mMiddlewares.add(middleware);
        return this;
    }

    public HttpServer useMiddleware(Middleware... middlewares) {
        for (Middleware middleware : middlewares) {
            useMiddleware(middleware);
        }

        return this;
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

        System.out.println(req.getRequestURL().toString());
        System.out.println(req.getPathInfo());
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
        HttpResponse response = new HttpResponse(resp);
        Controller controller = findController(req);

        if (controller != null) {
            // middlewares logic: return true if next middleware or http action should invoke.
            boolean next = true;
            for (Middleware middleware : mMiddlewares) {
                next = middleware.invoke(request, response);
                if (!next) {
                    break;
                }
            }

            if (next) {
                controller.processRequest(request, response);
            }
        } else {
            try {
                //TODO: if no external route found for the given uri, search for the route in the server.
                resp.sendError(404);
            } catch (Exception e) {
                String msg = String.format("Error: %s", e.getMessage());
                System.out.println(msg);
            }
        }
    }

    protected String parsePrefix(HttpServletRequest req) {
        String path = req.getPathInfo();
        String[] allPaths = path.split("/");

        return allPaths.length > 0 ? allPaths[1] : path;
    }

    protected Controller findController(HttpServletRequest req) {
        String prefix = parsePrefix(req);
        Controller controller = null;

        for (Controller c: mControllers) {
            String routePrefix = c.getPrefix();
            if (routePrefix.equals(prefix) || routePrefix.equals("/" + prefix)) {
                controller = c;
                break;
            }
        }

        if (controller == null) {
            controller = mDefaultCtrl;
        }

        return controller;
    }
}
