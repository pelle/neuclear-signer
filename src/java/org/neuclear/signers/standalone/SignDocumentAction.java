package org.neuclear.signers.standalone;

import org.dom4j.Document;
import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.commons.crypto.passphraseagents.swing.actions.SignerAction;
import org.neuclear.commons.crypto.signers.BrowsableSigner;
import org.neuclear.xml.XMLTools;
import org.neuclear.xml.xmlsec.EnvelopedSignature;
import org.neuclear.xml.xmlsec.HTMLSignature;
import org.neuclear.xml.xmlsec.XMLSignature;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.prefs.Preferences;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * User: pelleb
 * Date: May 13, 2004
 * Time: 9:39:17 AM
 */
public class SignDocumentAction extends SignerAction implements Runnable {

    public SignDocumentAction(BrowsableSigner signer) {
        super("signfile", IconTools.getSign(), signer);
        putValue(SHORT_DESCRIPTION, "Sign Document");
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));

    }

    public void actionPerformed(ActionEvent event) {
        parent = findParent(event);
        if (chooser == null) {
            chooser = new JFileChooser();
            chooser.setFileFilter(new FileFilter() {
                public boolean accept(File file) {
                    String filename = file.getName();
                    return file.isDirectory() || filename.endsWith(".xml") || filename.endsWith(".html") || filename.endsWith(".htm");
                }

                public String getDescription() {
                    return "XML Files";
                }
            });
        }
        new Thread(this).start();
    }

    private static Component findParent(ActionEvent event) {
        if (!(event.getSource() instanceof Component))
            return null;
        return ((Component) event.getSource());
    }

    public void run() {
        parent.setEnabled(false);
        chooser.setDialogTitle("Select XML file to sign...");
        chooser.setCurrentDirectory(new File(prefs.get("LASTDIR", System.getProperty("user.home"))));
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.CANCEL_OPTION) {
            parent.setEnabled(true);
//            message.info("Cancelled");
            return;
        }
        try {
            prefs.put("LASTDIR", chooser.getSelectedFile().getParent());
//            message.info("Parsing " + chooser.getSelectedFile().getName());
            final File file = chooser.getSelectedFile();
            Document doc;
            if (file.getName().endsWith(".html") || file.getName().endsWith(".html")) {
                InputStream is = new BufferedInputStream(new FileInputStream(file));
//                message.info("Signing " + chooser.getSelectedFile().getName());
                XMLSignature sig = new HTMLSignature(signer, is);
                doc = sig.getElement().getDocument();
            } else {
                doc = XMLTools.loadDocument(file);
//                message.info("Signing " + chooser.getSelectedFile().getName());
                new EnvelopedSignature(signer, doc.getRootElement());
            }
            final String path = chooser.getSelectedFile().getAbsolutePath();
            final int i = path.lastIndexOf(".");
            String suggested = path.substring(0, i) + "_sig" + path.substring(i);
            chooser.setSelectedFile(new File(suggested));
            chooser.setDialogTitle("Save signed XML Document");
            result = chooser.showSaveDialog(parent);
            if (result == JFileChooser.CANCEL_OPTION) {
                parent.setEnabled(true);
//                message.info("Cancelled");
                return;
            }
            prefs.put("LASTDIR", chooser.getSelectedFile().getParent());
//            message.info("Saving " + chooser.getSelectedFile().getName());
            XMLTools.writeFile(chooser.getSelectedFile(), doc);
//            message.info("Saved signed XML document " + chooser.getSelectedFile().getName());
            parent.setEnabled(true);
            prefs.flush();
        } catch (Exception e) {
            parent.setEnabled(true);
//            message.error(e);
        }

    }

    private JFileChooser chooser;
    private Component parent;
    private final Preferences prefs = Preferences.userNodeForPackage(this.getClass());
}