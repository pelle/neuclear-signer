package org.neuclear.signers.standalone;

import com.jgoodies.plaf.Options;
import org.dom4j.Document;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.util.InetAddrPort;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.crypto.passphraseagents.PassPhraseAgent;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.passphraseagents.swing.MessageLabel;
import org.neuclear.commons.crypto.signers.BrowsableSigner;
import org.neuclear.commons.crypto.signers.DefaultSigner;
import org.neuclear.xml.XMLException;
import org.neuclear.xml.XMLTools;
import org.neuclear.xml.xmlsec.EnvelopedSignature;
import org.neuclear.xml.xmlsec.XMLSignature;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;


/**
 * Starts a Jetty servlet Engine at port 11870, only listening on localhost.
 * with a SigningServlet at http://127.0.0.1:11870/Signer
 */
public class StandaloneSigner {
    public static void main(String args[]) {
//        Frame splash=SplashWindow.splash(Toolkit.getDefaultToolkit().createImage(StandaloneSigner.class.getClassLoader().getResource("neuclearsplash.png")));
        if (!LicenseScreen.accept())
            System.exit(0);
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
            ServletHolder[] holders = handler.getServlets();
            for (int i = 0; i < holders.length; i++) {
                ServletHolder holder = holders[i];
                if (holder.getServlet() instanceof StandaloneSigningServlet) {
//                    System.out.println("Found servlet: "+holder.getName());
                    if (!holder.isStarted())
                        holder.start();

                    JFrame frame = createFrame((DefaultSigner) ((StandaloneSigningServlet) holder.getServlet()).getSigner());
                }
            }

            Thread.sleep(2000);
//            splash.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static JFrame createFrame(final DefaultSigner signer) {
        try {
            UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
            UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }
        final JFrame frame = new JFrame("NeuClear Signing Agent");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        JButton quit = new JButton("Shut down");

        final ActionListener quitlistener = new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }

        };
//        quit.addActionListener(quitlistener);
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
        final MessageLabel message = new MessageLabel();
        message.info("Ready");
        content.add(message, BorderLayout.SOUTH);
//        content.add(quit, BorderLayout.SOUTH);
        content.setBackground(Color.BLUE);

        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menubar.add(menu);

        JMenuItem signItem = new JMenuItem("Sign file...",
                KeyEvent.VK_S);
        menu.add(signItem);
        menu.addSeparator();
        JMenuItem quitItem = new JMenuItem("Exit",
                KeyEvent.VK_X);
        menu.add(quitItem);
        quitItem.addActionListener(quitlistener);

        final JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File file) {
                String filename = file.getName();
                return file.isDirectory() || filename.endsWith(".xml") || filename.endsWith(".html") || filename.endsWith(".htm");
            }

            public String getDescription() {
                return "XML Files";
            }
        });
        signItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SignDocument sign = new SignDocument(signer, chooser, frame, message);
                new Thread(sign).start();
            }

        });
        frame.pack();
        frame.show();

        return frame;

    }

    public static class SignDocument implements Runnable {
        public SignDocument(BrowsableSigner signer, JFileChooser chooser, JFrame frame, MessageLabel message) {
            this.signer = signer;
            this.frame = frame;
            this.chooser = chooser;
            this.message = message;
        }

        public void run() {
            frame.setEnabled(false);
            chooser.setDialogTitle("Select XML file to sign...");
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.CANCEL_OPTION) {
                frame.setEnabled(true);
                message.info("Cancelled");
                return;
            }
            try {
                message.info("Parsing " + chooser.getSelectedFile().getName());
                Document doc = XMLTools.loadDocument(chooser.getSelectedFile());
                message.info("Signing " + chooser.getSelectedFile().getName());
                XMLSignature sig = new EnvelopedSignature(signer, doc.getRootElement());
                chooser.setDialogTitle("Save signed XML Document");
                result = chooser.showSaveDialog(frame);
                if (result == JFileChooser.CANCEL_OPTION) {
                    frame.setEnabled(true);
                    message.info("Cancelled");
                    return;
                }
                message.info("Saving " + chooser.getSelectedFile().getName());
                XMLTools.writeFile(chooser.getSelectedFile(), doc);
                message.info("Saved signed XML document " + chooser.getSelectedFile().getName());
                frame.setEnabled(true);

            } catch (XMLException e) {
                frame.setEnabled(true);
                message.error(e);
            } catch (UserCancellationException e) {
                frame.setEnabled(true);
                message.error(e);
            }

        }

        private final BrowsableSigner signer;
        private final JFileChooser chooser;
        private final JFrame frame;
        private final MessageLabel message;

    }
}
