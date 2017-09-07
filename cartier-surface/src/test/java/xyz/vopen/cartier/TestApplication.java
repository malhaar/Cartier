package xyz.vopen.cartier;

import xyz.vopen.cartier.surface.CartierApplication;
import xyz.vopen.cartier.surface.CartierApplicationContext;
import xyz.vopen.cartier.surface.exception.CartierOptionException;

import java.util.Map;

public class TestApplication extends CartierApplication {

    /**
     * New init method for sub-application
     *
     * @param context
     *         application context
     */
    @Override
    public void init (CartierApplicationContext context) throws Exception {

    }

    /**
     * Start Run
     *
     * @param context
     *         application context
     *
     * @throws Exception
     *         exception
     */
    @Override
    public void start (CartierApplicationContext context) throws Exception {

    }

    /**
     * Shutdown
     *
     * @param context
     *         application context
     *
     * @throws Exception
     *         exception
     */
    @Override
    public void shutdown (CartierApplicationContext context) throws Exception {

    }

    /**
     * check application option parameter ,<br/>
     * if config error ,just throw <code>{@link CartierOptionException}</code>
     *
     * @param options
     *         option map
     *
     * @throws CartierOptionException
     *         option exception
     * @deprecated use set properties on [appliation.properties] instead of
     */
    @Override
    public void checkOptions (Map<String, String> options) throws CartierOptionException {

    }
}
