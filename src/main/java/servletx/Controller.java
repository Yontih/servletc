package servletx;

/**
 * Created by Yonti on 18/06/2016.
 */
public class Controller {

    protected Router mRouter;

    public Controller(String route) {
        mRouter = new Router(route);
    }

    public Router getRoute() {
        return mRouter;
    }
}

