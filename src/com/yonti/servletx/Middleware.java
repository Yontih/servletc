package com.yonti.servletx;

/**
 * Created by Yonti on 18/06/2016.
 */
public interface Middleware {
    public boolean invoke(WebRequest req, WebResponse resp);
}
