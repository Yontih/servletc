package com.yonti.servletx;

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
        WebRequest yReq = new WebRequest(req);
        WebResponse yResp = new WebResponse(resp);
        String prefix = parsePrefix(req);
        Route route = findRoute(prefix);

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

    protected Route findRoute(String prefix) {
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
}
