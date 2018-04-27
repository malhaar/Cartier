package rudp.test;

import java.io.IOException;
import xyz.vopen.cartier.net.rudp.ReliableSocket;

/**
 * rudp.test
 *
 * @author Elve.Xu [iskp.me<at>gmail.com]
 * @version v1.0 - 2018/4/27.
 */
public class RClient {

    public static void main(String[] args) throws IOException {

        ReliableSocket client = new ReliableSocket("127.0.0.1", 9991);

    }
}
