package org.neuclear.signers.standalone;

import org.mortbay.http.HttpServer;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.id.tools.commandline.IdentityCreator;

import java.io.IOException;
import java.io.File;




/**
 * Starts a Jetty servlet Engine at port 11870, only listening on localhost.
 * with a SigningServlet at http://127.0.0.1:11870/Signer
 */
public class StandaloneSigner {
    public static void main(String args[]){
        if (args.length>0) {
            IdentityCreator.main(args);
            return;
        }
        File keystore=new File(CryptoTools.DEFAULT_KEYSTORE);
        if (!keystore.exists()) {
            System.out.println("First you need to create an Identity. Use this tool with the following options.\n For more help go to http://neuclear.org/signer/bdg.html");
            IdentityCreator.main(args);
        } else {
            try {
                Server server = new Server();
                server.addListener(new InetAddrPort("127.0.0.1",11870));
                HttpContext context = server.getContext("/");
                ServletHandler handler= new ServletHandler();
                handler.addServlet("/Signer","org.neuclear.signers.standalone.StandaloneSigningServlet");
                context.addHandler(handler);
                server.start();
                context.start();
                handler.start();
                handler.initializeServlets();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
    }
}
