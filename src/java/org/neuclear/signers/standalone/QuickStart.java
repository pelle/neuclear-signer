package org.neuclear.signers.standalone;

import java.awt.*;


/**
 * Starts a Jetty servlet Engine at port 11870, only listening on localhost.
 * with a SigningServlet at http://127.0.0.1:11870/Signer
 */
public class QuickStart {
    public static void main(String args[]) {
        Frame splash = SplashWindow.splash(Toolkit.getDefaultToolkit().createImage(QuickStart.class.getClassLoader().getResource("neuclearsplash.png")));
        try {
            Class.forName("org.neuclear.signers.standalone.StandaloneSigner")
                    .getMethod("main", new Class[]{String[].class})


                    .invoke(null, new Object[]{args});
        } catch (Throwable e) {
            e.printStackTrace();


            System.err.flush();
            System.exit(10);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        splash.dispose();
    }
}
