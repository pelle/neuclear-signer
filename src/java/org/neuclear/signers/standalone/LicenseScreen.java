package org.neuclear.signers.standalone;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.plaf.Options;
import org.neuclear.commons.crypto.passphraseagents.PassPhraseAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.prefs.Preferences;

/*
$Id: LicenseScreen.java,v 1.2 2004/04/21 23:04:36 pelle Exp $
$Log: LicenseScreen.java,v $
Revision 1.2  2004/04/21 23:04:36  pelle
Fixed mac look and feel

Revision 1.1  2004/04/15 20:03:26  pelle
Added license screen to Personal Signer.
Added Sign document menu to  Personal Signer.

*/

/**
 * User: pelleb
 * Date: Apr 15, 2004
 * Time: 1:13:16 PM
 */
public class LicenseScreen {

    public static boolean accept() {
        final Preferences prefs = Preferences.userNodeForPackage(StandaloneSigner.class);
        if (prefs.getBoolean(ACCEPTED, false)) {// User accepted the license
            return true;
        }

        try {
            if (UIManager.getSystemLookAndFeelClassName().equals("apple.laf.AquaLookAndFeel"))
                System.setProperty("com.apple.laf.useScreenMenuBar", "true");
            else {
                UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
                UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
            }
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }

        final JFrame frame = new JFrame("License");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        final URL imageurl = PassPhraseAgent.class.getClassLoader().getResource("org/neuclear/commons/crypto/passphraseagents/neuclear.png");
        JLabel label = new JLabel("NeuClear Personal Signer License Agreement");
        ;
        if (imageurl != null) {
            final ImageIcon icon = new ImageIcon(imageurl);
            frame.setIconImage(icon.getImage());
            label.setIcon(icon);
        }

        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        content.add(label, BorderLayout.NORTH);
//        final MessageLabel message=new MessageLabel();
//        message.info("Ready");
//        content.add(message, BorderLayout.SOUTH);
//        content.add(quit, BorderLayout.SOUTH);
        content.setBackground(Color.BLUE);
        JTextArea license = new JTextArea(loadLicense());

        license.setEditable(false);
        content.add(new JScrollPane(license), BorderLayout.CENTER);
        ButtonBarBuilder bb = new ButtonBarBuilder();
        final JCheckBox check = new JCheckBox("I accept the above license", false);
        bb.addGridded(check);
        bb.addGlue();
        bb.addRelatedGap();
        final JButton ok = new JButton("Accept License");
        ok.setEnabled(false);
        JButton cancel = new JButton("Cancel");
        bb.addGridded(ok);
        bb.addGridded(cancel);
        content.add(bb.getPanel(), BorderLayout.SOUTH);

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }

        });

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                synchronized (ok) {
                    ok.notifyAll();
                }
            }

        });
        check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                ok.setEnabled(check.isSelected());
            }

        });

        frame.pack();
        frame.show();
        frame.toFront();
        try {
            synchronized (ok) {
                while (true) {
                    ok.wait();
                    if (check.isSelected()) {
                        frame.dispose();
                        prefs.putBoolean(ACCEPTED, true);
                        return true;
                    }
                }
            }
        } catch (InterruptedException e) {
            return false;
        }

    }

    private static String loadLicense() {
        InputStream is = LicenseScreen.class.getClassLoader().getResourceAsStream("LICENSE.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer buf = new StringBuffer();
        try {
            String line = reader.readLine();
            while (line != null) {
                buf.append(line);
                buf.append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    private static final String ACCEPTED = "LICENSE_ACCEPTED";
}
