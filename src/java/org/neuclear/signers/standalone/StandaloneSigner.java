package org.neuclear.signers.standalone;

import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.InetAddrPort;


/**
 * Starts a Jetty servlet Engine at port 11870, only listening on localhost.
 * with a SigningServlet at http://127.0.0.1:11870/Signer
 */
public class StandaloneSigner {
    public static void main(String args[]) {
        try {
            Server server = new Server();
            server.addListener(new InetAddrPort("127.0.0.1", 11870));
            HttpContext context = server.getContext("/");
            ServletHandler handler = new ServletHandler();
            handler.addServlet("/Signer", "org.neuclear.signers.standalone.StandaloneSigningServlet");
            context.addHandler(handler);
            server.start();
            context.start();
            handler.start();
            handler.initializeServlets();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
