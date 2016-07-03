package servletc.middlewares;

import servletc.http.HttpRequest;
import servletc.http.HttpResponse;

/**
 * Created by Yonti on 18/06/2016.
 */
public interface Middleware {
    public boolean invoke(HttpRequest req, HttpResponse resp);
}
