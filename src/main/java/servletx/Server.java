package servletx;

import org.json.JSONException;
import org.json.JSONObject;
import servletx.http.HttpRequest;
import servletx.http.HttpResponse;
import servletx.json.JSONBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Yonti on 18/06/2016.
 */
public class Server extends HttpServlet {
    private ArrayList<Route> mRoutes;
    private ArrayList<Middleware> mMiddlewares;

    public Server() {
        mRoutes = new ArrayList<>();
        mMiddlewares = new ArrayList<>();
    }

    public void useController(Controller c) {
        mRoutes.add(c.getRoute());
    }

    public void useControllers(Controller... controllers) {
        for (Controller c : controllers) {
            useController(c);
        }
    }

    public void useMiddleware(Middleware middleware) {
        mMiddlewares.add(middleware);
    }

    public void useMiddleware(Middleware... middlewares) {
        for (Middleware middleware : middlewares) {
            useMiddleware(middleware);
        }
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
        navigate(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        navigate(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        navigate(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        navigate(req, resp);
    }

    protected void navigate(HttpServletRequest req, HttpServletResponse resp) {
        HttpRequest yReq = new HttpRequest(req);
        HttpResponse yResp = new HttpResponse(resp);
        Route route = findRoute(req);

        if (route != null) {
            // middlewares logic: return true if next middleware or http action should invoke.
            boolean next = true;
            for (Middleware middleware : mMiddlewares) {
                next = middleware.invoke(yReq, yResp);
                if (!next) {
                    break;
                }
            }

            if (next) {
                route.navigate(yReq, yResp);
            }
        } else {
            try {
                //TODO: if no external route found for the giver uri, search for the route in the server.
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

    protected Route findRouteByPrefix(String prefix) {
        Route route = null;

        for (Route currentRoute : mRoutes) {
            String routePrefix = currentRoute.getPrefix();
            if (routePrefix.equals(prefix) || routePrefix.equals("/" + prefix)) {
                route = currentRoute;
                break;
            }
        }

        return route;
    }

    protected Route findRoute(HttpServletRequest req) {
        String prefix = parsePrefix(req);
        Route route = findRouteByPrefix(prefix);

        return route != null ? route : findRouteByPrefix("/");
    }
}