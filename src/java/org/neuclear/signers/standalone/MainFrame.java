package org.neuclear.signers.standalone;

import com.jgoodies.plaf.Options;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import com.l2fprod.common.swing.UIUtilities;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.commons.crypto.passphraseagents.swing.KeyStorePanel;
import org.neuclear.commons.crypto.passphraseagents.swing.MessageLabel;
import org.neuclear.commons.crypto.signers.PersonalSigner;
import org.neuclear.signers.standalone.identitylists.AssetPanel;
import org.neuclear.signers.standalone.identitylists.IdentityPanel;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

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
 * Time: 9:23:30 AM
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "NeuClear Trader");
        try {

            if (!UIManager.getSystemLookAndFeelClassName().equals("apple.laf.AquaLookAndFeel")) {
                UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
                UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
            }
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }
        setTitle("NeuClear Trader");
        AssetGlobals.registerReaders();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(IconTools.getLogo().getImage());
        signer = new PersonalSigner(this);
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        JTaskPane taskpane = new JTaskPane();

        JTaskPaneGroup personalityTasks = new JTaskPaneGroup();
        personalityTasks.setIcon(IconTools.getPersonalities());
        personalityTasks.setText("Personality related tasks");
        personalityTasks.setEnabled(true);
        taskpane.add(personalityTasks);

        JTaskPaneGroup assetTasks = new JTaskPaneGroup();
        assetTasks.setIcon(ICON_ASSETS);
        assetTasks.setText("Tasks related top your Assets");
        assetTasks.setEnabled(true);
        taskpane.add(assetTasks);
        JTaskPaneGroup contactTasks = new JTaskPaneGroup();
        contactTasks.setIcon(ICON_CONTACTS);
        contactTasks.setText("Tasks related to your contacts");
        contactTasks.setEnabled(true);
        taskpane.add(contactTasks);

        JTabbedPane tabbed = new JTabbedPane();
        content.add(tabbed, BorderLayout.CENTER);
        tabbed.addTab("Tasks", IconTools.loadIcon(MainFrame.class, "org/neuclear/signers/standalone/icons/starthere.png"), taskpane);
        final KeyStorePanel ksPane = new KeyStorePanel(signer);
        Action actions[] = ksPane.getActions();
        for (int i = 0; i < actions.length; i++) {
            Action action = actions[i];
            personalityTasks.add(action);
        }
        tabbed.addTab("Personalities", IconTools.getPersonalities(), ksPane);
        final JPanel contacts = new JPanel();
        contacts.setLayout(new BorderLayout());
        final IdentityPanel contactsPanel = new IdentityPanel();
        contacts.add(contactsPanel, BorderLayout.CENTER);

        actions = contactsPanel.getActions();
        for (int i = 0; i < actions.length; i++) {
            Action action = actions[i];
            contactTasks.add(action);
        }

        tabbed.addTab("Contacts", ICON_CONTACTS, contacts);
        final JPanel assets = new JPanel();
        assets.setLayout(new BorderLayout());
        final AssetPanel assetPanel = new AssetPanel();
        assets.add(assetPanel, BorderLayout.CENTER);
        tabbed.addTab("Assets", ICON_ASSETS, assets);
        actions = assetPanel.getActions();
        for (int i = 0; i < actions.length; i++) {
            Action action = actions[i];
            assetTasks.add(action);
        }

        JTaskPaneGroup signingTasks = new JTaskPaneGroup();
        signingTasks.setIcon(IconTools.getSign());
        signingTasks.setText("Signing tasks");
        signingTasks.setEnabled(true);
        taskpane.add(signingTasks);

        MessageLabel message = new MessageLabel();
        content.add(message, BorderLayout.SOUTH);

        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menubar.add(menu);
        Action[] fileactions = ksPane.getFileActions();
        for (int i = 0; i < fileactions.length; i++) {
            menu.add(fileactions[i]);

        }
        menu.addSeparator();
        signdoc = new SignDocumentAction(signer);
        signingTasks.add(signdoc);
        JMenuItem signItem = new JMenuItem(signdoc);
        signItem.setIcon(IconTools.getSign());
        menu.add(signItem);


        menu.addSeparator();
        JMenuItem quitItem = new JMenuItem("Exit",
                KeyEvent.VK_X);
        menu.add(quitItem);


        quitItem.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }

        });
        helpmenu = new JMenu("Help");
        helpmenu.setMnemonic(KeyEvent.VK_H);
        menubar.add(helpmenu);

        createWebMenuItem("NeuClear Site", "http://neuclear.org", message);
        createWebMenuItem("The Two-Minute NeuClear Tour", "http://neuclear.org/display/neu/The+Two-Minute+NeuClear+Tour", message);
        helpmenu.addSeparator();
        createWebMenuItem("Road Map", "http://jira.neuclear.org/secure/BrowseProject.jspa?id=10030&report=roadmap", message);
        createWebMenuItem("Report Bug or Suggest Feature", "http://jira.neuclear.org/secure/CreateIssue!default.jspa", message);
        helpmenu.addSeparator();
        createWebMenuItem("Developers Blog", "http://talk.org", message);
        helpmenu.addSeparator();
        JMenuItem about = new JMenuItem("About");
        about.setMnemonic(KeyEvent.VK_A);
        helpmenu.add(about);
        final Frame frame = this;
        about.addActionListener(new ActionListener() {

            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                new SplashWindow(frame, Toolkit.getDefaultToolkit().createImage(QuickStart.class.getClassLoader().getResource("neuclearsplash.jpg"))).show();
            }

        });

        setSize(300, 500);
        UIUtilities.centerOnScreen(this);
        show();
        setEnabled(false);
        CryptoTools.ensureProvider();
        server = new SigningServer(signer, message);
        server.start();
        while (!signer.isOpen()) {
            try {
                signer.open();
            } catch (UserCancellationException e) {
                int choice = JOptionPane.showOptionDialog(this, "You need to open a Personalities File to start NeuClear Trader.",
                        "Really Quit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, QUIT_OPTIONS, QUIT_OPTIONS[1]);
                if (choice == JOptionPane.NO_OPTION) // Yeah I know it doesnt make sense
                    System.exit(0);

            }
        }
        setEnabled(true);

    }

    private void createWebMenuItem(final String title, final String url, final MessageLabel message) {
        final JMenuItem item = new JMenuItem(title);
        item.setIcon(webicon);

        item.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
                    // Invoke the showDocument method
                    bs.showDocument(new URL(url));
                } catch (Exception e1) {
                    message.error(e1);
                }
            }

        });
        helpmenu.add(item);
    }

    public static void main(String args[]) {
        final MainFrame main = new MainFrame();
    }

    private final SigningServer server;
    private SignDocumentAction signdoc;
    private final Icon webicon = IconTools.loadIcon(StandaloneSigner.class, "org/neuclear/signers/standalone/icons/browser.png");
    private JMenu helpmenu;
    private PersonalSigner signer;
    public static final Icon ICON_CONTACTS = IconTools.loadIcon(MainFrame.class, "org/neuclear/signers/standalone/icons/contacts.png");
    public static final Icon ICON_ASSETS = IconTools.loadIcon(MainFrame.class, "org/neuclear/signers/standalone/icons/assets.png");
    public static final String[] QUIT_OPTIONS = new String[]{"Try again", "Quit"};
}
