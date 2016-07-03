package servletx;

public class Controller {

    private Router mRouter;

    public Controller(String route) {
        mRouter = new Router(route);
    }

    public Router getRouter() {
        return mRouter;
    }

}

