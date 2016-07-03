package servletc;

import servletc.http.HttpRequest;
import servletc.http.HttpResponse;
import servletc.middlewares.Action;
import servletc.middlewares.Middleware;
import servletc.routes.Route;

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
        String method = req.getRequest().getMethod();
        String path = req.getRequest().getPathInfo();
        HashMap<String, Route> map;

        System.out.println("Path: " + path);
        path = path.equals("/") ? path : path.replace(mPrefix, "").replace("//", "/");

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
                System.out.println("Unknown http method: " + method);
                resp.sendError(404);
                return;
        }

        System.out.println("Path: " + path);
        Route route = findRoute(map, path);
        if (route != null) {
            if (route.hasParams()) {
                Map<String, Object> reqParams = route.extractParams(path);
                req.setParams(reqParams);
            }

            Action action = route.getAction();
            List<Middleware> middlewares = route.getMiddlewares() != null ? mergeMiddlewares(route.getMiddlewares()) : mMiddlewares;
            // middlewares logic: return true if next middleware or http action should invoke.
            boolean next = true;
            if (middlewares != null && middlewares.size() > 0) {
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

    public void useMiddleware(Middleware middleware) {
        mMiddlewares.add(middleware);
    }

    public void useMiddlewares(Middleware... middlewares) {
        for (Middleware middleware : middlewares) {
            useMiddleware(middleware);
        }
    }

    public Router get(String route, Action action) {
        return this.get(route, action, null);
    }

    public Router get(String route, Action action, Middleware... middlewares) {
        mGetMap.put(route, new Route(route, action, middlewares));
        return this;
    }

    public Router post(String route, Action action) {
        return this.post(route, action, null);
    }

    public Router post(String route, Action action, Middleware... middlewares) {
        mPostMap.put(route, new Route(route, action, middlewares));
        return this;
    }

    public Router put(String route, Action action) {
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
        mDeleteMap.put(route, new Route(route, action, middlewares));
        return this;
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

    private List<Middleware> mergeMiddlewares(Middleware[] specificRequest) {
        List<Middleware> all = new ArrayList<>();
        all.addAll(mMiddlewares);
        all.addAll(Arrays.asList(specificRequest));
        return all;
    }
}
