package servletx;

import servletx.http.HttpRequest;
import servletx.http.HttpResponse;
import servletx.middlewares.Action;
import servletx.middlewares.Middleware;
import servletx.routes.RegexRoute;
import servletx.routes.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yonti on 18/06/2016.
 */
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

        Route route = findRoute(map, path);
        if (route != null) {
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

    public void get(String route, Action action) {
        mGetMap.put(route, new RegexRoute(route, action, null));
    }

    public void get(String route, Action action, Middleware... middlewares) {
        mGetMap.put(route, new RegexRoute(route, action, middlewares));
    }

    public void post(String route, Action action) {
        mPostMap.put(route, new RegexRoute(route, action, null));
    }

    public void post(String route, Action action, Middleware... middlewares) {
        mPostMap.put(route, new RegexRoute(route, action, middlewares));
    }

    public void put(String route, Action action) {
        mPutMap.put(route, new RegexRoute(route, action, null));
    }

    public void put(String route, Action action, Middleware... middlewares) {
        mPutMap.put(route, new RegexRoute(route, action, middlewares));
    }

    public void delete(String route, Action action) {
        mDeleteMap.put(route, new RegexRoute(route, action, null));
    }

    public void delete(String route, Action action, Middleware... middlewares) {
        mDeleteMap.put(route, new RegexRoute(route, action, middlewares));
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
