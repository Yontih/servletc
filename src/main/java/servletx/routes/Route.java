package servletx.routes;

import servletx.middlewares.Action;
import servletx.middlewares.Middleware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {
    public final static String PARAM_REGEX = "/([^\\\\/]+?)";

    private Action mAction;
    private Middleware[] mMiddlewares;
    private String mRoute;

    private Pattern mRoutePattern;
    private Matcher mMatcher;
    private String[] mParams;


    public Route(String route, Action action, Middleware[] middlewares) {
        mRoute = route;
        mAction = action;
        mMiddlewares = middlewares;

        String routeAsRgx = routeToRegex(route);
        mRoutePattern = Pattern.compile(routeAsRgx);
    }

    public Map<String, Object> extractParams(String path) {
        HashMap<String, Object> params = new HashMap<>();
        int paramsCount = mMatcher.groupCount() + 1;

        if (paramsCount > 0) {
            for (int i = 1; i < paramsCount; i++) {
                String paramName = mParams[i];
                String paramValue = mMatcher.group(i);

                params.put(paramName, paramValue);
            }
        }

        return params;
    }

    public boolean hasParams() {
        return mMatcher != null && mMatcher.matches();
    }

    public boolean isMatch(String path) {
        mMatcher = mRoutePattern.matcher(path);
        return mMatcher.matches();
    }

    private String routeToRegex(String route) {
        StringBuffer result = new StringBuffer();
        List<String> params = new ArrayList<>();
        String[] routes = route.split("/");

        params.add("");

        for (String path : routes) {
            String s = null;
            if (path.isEmpty()) {
                continue;
            } else if (path.startsWith(":")) {
                String paramName = path.replace(":", "");
                params.add(paramName);
                s = PARAM_REGEX;
            } else {
                s = String.format("/%s", path);
            }

            result.append(s);
        }

        mParams = new String[params.size()];
        mParams = params.toArray(mParams);

        return result.toString();
    }

    public Action getAction() {
        return mAction;
    }

    public Middleware[] getMiddlewares() {
        return mMiddlewares;
    }

}