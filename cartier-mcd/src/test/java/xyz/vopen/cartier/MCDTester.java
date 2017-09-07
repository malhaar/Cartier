package xyz.vopen.cartier;

import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.folsom.ConnectFuture;
import com.spotify.folsom.EmbeddedServer;
import com.spotify.folsom.MemcacheClient;
import com.spotify.folsom.MemcacheClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * xyz.vopen.cartier
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 23/08/2017.
 */
public class MCDTester {


    EmbeddedServer embeddedServer;
    int port;

    @Before
    public void startup () throws Exception {
        embeddedServer = new EmbeddedServer(false);
        port = embeddedServer.getPort();
    }

    @Test
    public void runMCD () throws Exception {

        MemcacheClient<String> memcacheClient = MemcacheClientBuilder.newStringClient()
                .withAddress(HostAndPort.fromParts("localhost", port))
                .connectAscii();

        ConnectFuture.connectFuture(memcacheClient).get();

        memcacheClient.set("k1", "v1", 100);

        ListenableFuture future = memcacheClient.get("k1");
        System.out.println(future.get());

    }

    @After
    public void down () throws Exception {
        embeddedServer.stop();
    }


}
