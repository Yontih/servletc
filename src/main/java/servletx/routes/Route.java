package servletx.routes;

import servletx.middlewares.Action;
import servletx.middlewares.Middleware;

import java.util.Map;

/**
 * Created by Yonti on 02/07/2016.
 */
public abstract class Route {
    private Action mAction;
    private Middleware[] mMiddlewares;
    private String mRoute;


    public Route(String route, Action action, Middleware[] middlewares) {
        mRoute = route;
        mAction = action;
        mMiddlewares = middlewares;

    }

    public abstract Map<String, Object> extractParams(String path);
    public abstract boolean isMatch(String path);

    public Action getAction() {
        return mAction;
    }

    public Middleware[] getMiddlewares() {
        return mMiddlewares;
    }

}