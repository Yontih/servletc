package servletc.util;

import servletc.middlewares.Middleware;

import java.util.*;

/**
 * Created by Yonti on 09/07/2016.
 */
public class Merger {

    public static List<Middleware> mergeMiddlewares(List<Middleware> list1, List<Middleware> list2) {
        List<Middleware> all = new ArrayList<>();
        all.addAll(list1);
        all.addAll(list2);
        return all;
    }

}
