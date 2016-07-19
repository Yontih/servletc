package servletc;

import servletc.http.HttpRequest;
import servletc.http.HttpResponse;
import servletc.middlewares.Action;
import servletc.middlewares.Middleware;
import servletc.routes.Route;
import servletc.utils.Merger;

import java.util.*;

public class Router {
    private String mPrefix;
    private HashMap<String, Route> mGetMap;
    private HashMap<String, Route> mPostMap;
    private HashMap<String, Route> mPutMap;
    private HashMap<String, Route> mDeleteMap;
    private ArrayList<Middleware> mMiddlewares;

    public Router(String prefix) {
        mPrefix = prefix;
        mGetMap = new HashMap<>();
        mPostMap = new HashMap<>();
        mPutMap = new HashMap<>();
        mDeleteMap = new HashMap<>();
        mMiddlewares = new ArrayList<>();
    }

    public String getPrefix() {
        return mPrefix;
    }


    public void navigate(HttpRequest req, HttpResponse resp) {
        String path = req.getRequest().getPathInfo();
        path = path.equals("/") ? path : path.replace(mPrefix, "").replace("//", "/");

        navigate(req, resp, path);
    }

    public void navigate(HttpRequest req, HttpResponse resp, String path) {
        String method = req.getRequest().getMethod();
        HashMap<String, Route> map;

        switch (method) {
            case "GET":
                map = mGetMap;
                break;
            case "POST":
                map = mPostMap;
                break;
            case "PUT":
                map = mPutMap;
                break;
            case "DELETE":
                map = mDeleteMap;
                break;
            default:
                System.out.println("Unknown http method : " + method);
                resp.sendError(404);
                return;
        }

        Route route = findRoute(map, path);
        if (route != null) {
            if (route.hasParams()) {
                Map<String, Object> reqParams = route.extractParams(path);
                req.setParams(reqParams);
            }

            Action action = route.getAction();
            List<Middleware> middlewares = route.getMiddlewares() != null ? Merger.mergeMiddlewares(mMiddlewares, Arrays.asList(route.getMiddlewares())) : mMiddlewares;
            // middlewares logic: return true if next middleware or http action should invoke.
            boolean next = true;
            if (middlewares != null && !middlewares.isEmpty()) {
                for (Middleware middleware : middlewares) {
                    next = middleware.invoke(req, resp);
                    if (!next) {
                        break;
                    }
                }
            }

            if (next) {
                action.invoke(req, resp);
            }
        } else {
            resp.sendError(404);
        }
    }

    public Router useMiddleware(Middleware middleware) {
        mMiddlewares.add(middleware);
        return this;
    }

    public Router useMiddlewares(Middleware... middlewares) {
        for (Middleware middleware : middlewares) {
            useMiddleware(middleware);
        }

        return this;
    }

    public Router get(String route, Action action) {
        return this.get(route, action, null);
    }

    public Router get(String route, Action action, Middleware... middlewares) {
        route = resolveRouteStr(route);
        mGetMap.put(route, new Route(route, action, middlewares));
        return this;
    }

    public Router post(String route, Action action) {
        return this.post(route, action, null);
    }

    public Router post(String route, Action action, Middleware... middlewares) {
        route = resolveRouteStr(route);
        mPostMap.put(route, new Route(route, action, middlewares));
        return this;
    }

    public Router put(String route, Action action) {
        route = resolveRouteStr(route);
        return this.put(route, action, null);
    }

    public Router put(String route, Action action, Middleware... middlewares) {
        mPutMap.put(route, new Route(route, action, middlewares));
        return this;
    }

    public Router delete(String route, Action action) {
        return this.delete(route, action, null);
    }

    public Router delete(String route, Action action, Middleware... middlewares) {
        route = resolveRouteStr(route);
        mDeleteMap.put(route, new Route(route, action, middlewares));
        return this;
    }

    private String resolveRouteStr(String route) {
        if (mPrefix.isEmpty() || mPrefix.equals("/")) {
            return route;
        } else if (route.equals("/")) {
            return String.format("/%s", mPrefix);
        } else {
            return String.format("/%s%s", mPrefix, route);
        }
    }

    private Route findRoute(HashMap<String, Route> routesMap, String path) {
        Route route = routesMap.get(path);

        if (route == null) {
            for (String p : routesMap.keySet()) {
                Route r = routesMap.get(p);
                if (r.isMatch(path)) {
                    route = r;
                    break;
                }
            }
        }

        return route;
    }
}
