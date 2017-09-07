package xyz.vopen.cartier.provision;

public class Processor {

    static ThreadLocal<Processor.Handler> handlers = new ThreadLocal<Handler>() {

        @Override
        protected Handler initialValue () {
            return new Handler();
        }
    };

    public static class Handler {
        String name;
    }

    public static Handler get () {
        return handlers.get();
    }

    public static void remove () {
        handlers.remove();
    }
}