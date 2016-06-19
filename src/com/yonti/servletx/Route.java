package com.yonti.servletx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yonti on 18/06/2016.
 */
public class Route {
    private String mPrefix;
    private HashMap<String, HttpActionMiddlewares> mGetMap;
    private HashMap<String, HttpActionMiddlewares> mPostMap;
    private HashMap<String, HttpActionMiddlewares> mPutMap;
    private HashMap<String, HttpActionMiddlewares> mDeleteMap;
    private ArrayList<Middleware> mMiddlewares;

    private class HttpActionMiddlewares {

        private HttpAction mAction;
        private Middleware[] mMiddlewares;

        public HttpActionMiddlewares(HttpAction action, Middleware[] middlewares) {
            mAction = action;
            mMiddlewares = middlewares;
        }

        public HttpAction getAction() {
            return mAction;
        }

        public Middleware[] getMiddlewares() {
            return mMiddlewares;
        }
    }

    public Route(String prefix) {
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

    public void navigate(WebRequest req, WebResponse resp) {
        String method = req.getRequest().getMethod();
        String path = req.getRequest().getPathInfo();
        HashMap<String, HttpActionMiddlewares> map;

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

        HttpActionMiddlewares am = map.get(path);
        if (am != null) {
            HttpAction action = am.getAction();
            List<Middleware> middlewares = am.getMiddlewares() != null ? mergeMiddlewares(am.getMiddlewares()) : mMiddlewares;
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

    public void get(String route, HttpAction action) {
        mGetMap.put(route, new HttpActionMiddlewares(action, null));
    }

    public void get(String route, HttpAction action, Middleware... middlewares) {
        mGetMap.put(route, new HttpActionMiddlewares(action, middlewares));
    }

    public void post(String route, HttpAction action) {
        mPostMap.put(route, new HttpActionMiddlewares(action, null));
    }

    public void post(String route, HttpAction action, Middleware... middlewares) {
        mPostMap.put(route, new HttpActionMiddlewares(action, middlewares));
    }

    public void put(String route, HttpAction action) {
        mPutMap.put(route, new HttpActionMiddlewares(action, null));
    }

    public void put(String route, HttpAction action, Middleware... middlewares) {
        mPutMap.put(route, new HttpActionMiddlewares(action, middlewares));
    }

    public void delete(String route, HttpAction action) {
        mDeleteMap.put(route, new HttpActionMiddlewares(action, null));
    }

    public void delete(String route, HttpAction action, Middleware... middlewares) {
        mDeleteMap.put(route, new HttpActionMiddlewares(action, middlewares));
    }

    private List<Middleware> mergeMiddlewares(Middleware[] specificRequest) {
        List<Middleware> all = new ArrayList<>();
        all.addAll(mMiddlewares);
        all.addAll(Arrays.asList(specificRequest));
        return all;
    }
}
