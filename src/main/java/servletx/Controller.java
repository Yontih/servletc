package servletx;

/**
 * Created by Yonti on 18/06/2016.
 */
public class Controller {

    protected Route mRoute;

    public Controller(String route) {
        mRoute = new Route(route);
    }

    public Route getRoute() {
        return mRoute;
    }
}

