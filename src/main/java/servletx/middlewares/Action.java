package servletx.middlewares;

import servletx.http.HttpRequest;
import servletx.http.HttpResponse;

/**
 * Created by Yonti on 18/06/2016.
 */
public interface Action {
    public void invoke(HttpRequest req, HttpResponse resp);
}
