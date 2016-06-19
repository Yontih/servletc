package com.yonti.servletx;

/**
 * Created by Yonti on 18/06/2016.
 */
public interface HttpAction {
    public void invoke(WebRequest req, WebResponse resp);
}
