package org.neuclear.signers.standalone;

import com.jgoodies.plaf.Options;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.InetAddrPort;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.crypto.passphraseagents.PassPhraseAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


/**
 * Starts a Jetty servlet Engine at port 11870, only listening on localhost.
 * with a SigningServlet at http://127.0.0.1:11870/Signer
 */
public class StandaloneSigner {
    public static void main(String args[]) {
//        Frame splash=SplashWindow.splash(Toolkit.getDefaultToolkit().createImage(StandaloneSigner.class.getClassLoader().getResource("neuclearsplash.png")));

        try {
            CryptoTools.ensureProvider();
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

            JFrame frame = createFrame();

            Thread.sleep(2000);
//            splash.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JFrame createFrame() {
        try {
            UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
            UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }
        JFrame frame = new JFrame("NeuClear Signing Agent");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton quit = new JButton("Shut down");

        quit.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }

        });
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        final URL imageurl = PassPhraseAgent.class.getClassLoader().getResource("org/neuclear/commons/crypto/passphraseagents/neuclear.png");
        JLabel label = new JLabel("NeuClear Personal Signer");
        ;
        if (imageurl != null) {
            final ImageIcon icon = new ImageIcon(imageurl);
            frame.setIconImage(icon.getImage());
            label.setIcon(icon);
        }

        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        content.add(label, BorderLayout.NORTH);
        content.add(quit, BorderLayout.SOUTH);
        content.setBackground(Color.BLUE);
        frame.pack();
        frame.show();

        return frame;

    }
}
