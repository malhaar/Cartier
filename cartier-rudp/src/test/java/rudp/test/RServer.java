package rudp.test;

import java.io.IOException;
import xyz.vopen.cartier.net.rudp.ReliableServerSocket;

/**
 * rudp.test
 *
 * @author Elve.Xu [iskp.me<at>gmail.com]
 * @version v1.0 - 2018/4/27.
 */
public class RServer {

    public static void main(String[] args) throws IOException {

        ReliableServerSocket serverSocket = new ReliableServerSocket(9991);

    }
}
