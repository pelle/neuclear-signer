package org.neuclear.signers.standalone;

import com.jgoodies.plaf.Options;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import com.l2fprod.common.swing.StatusBar;
import com.l2fprod.common.util.OS;
import org.masukomi.aspirin.core.MailQue;
import org.masukomi.aspirin.core.MailWatcher;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.crypto.passphraseagents.AgentMessages;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.commons.crypto.passphraseagents.swing.KeyStorePanel;
import org.neuclear.commons.crypto.passphraseagents.swing.MessageLabel;
import org.neuclear.commons.crypto.signers.PersonalSigner;
import org.neuclear.signers.standalone.identitylists.AssetPanel;
import org.neuclear.signers.standalone.identitylists.IdentityPanel;
import org.neuclear.signers.standalone.identitylists.actions.AddIdentityAction;
import org.neuclear.signers.standalone.signingscreens.TransferAction;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Collection;

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
    public MainFrame(Frame splash) {
        try {

            if (OS.isMacOSX()) {
                System.setProperty("com.apple.macos.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "NeuClear Personal Trader");
            } else if (OS.isWindows2003() || OS.isWindowsXP()) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
                UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
            }
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }
        setTitle("NeuClear Personal Trader");
        AssetGlobals.registerReaders();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(IconTools.getLogo().getImage());
        signer = new PersonalSigner(this);
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        JTaskPane taskpane = new JTaskPane();

        JTaskPaneGroup personalityTasks = new JTaskPaneGroup();
        personalityTasks.setIcon(IconTools.getPersonalities());
        personalityTasks.setText("Account related tasks");
        personalityTasks.setEnabled(true);
        taskpane.add(personalityTasks);

        JTaskPaneGroup assetTasks = new JTaskPaneGroup();
        assetTasks.setIcon(ICON_ASSETS);
        assetTasks.setText("Tasks related to your Assets");
        assetTasks.setEnabled(true);
        taskpane.add(assetTasks);
        JTaskPaneGroup contactTasks = new JTaskPaneGroup();
        contactTasks.setIcon(ICON_CONTACTS);
        contactTasks.setText("Tasks related to your contacts");
        contactTasks.setEnabled(true);
        taskpane.add(contactTasks);

        JTabbedPane tabbed = new JTabbedPane();
        content.add(tabbed, BorderLayout.CENTER);
        tabbed.addTab("Start here", IconTools.loadIcon(MainFrame.class, "org/neuclear/signers/standalone/icons/starthere.png"), taskpane);
        final KeyStorePanel ksPane = new KeyStorePanel(signer);
        Action actions[] = ksPane.getActions();
        for (int i = 0; i < actions.length; i++) {
            Action action = actions[i];
            personalityTasks.add(action);
        }
        tabbed.addTab(AgentMessages.getText("identities"), IconTools.getPersonalities(), ksPane);
        final JPanel contacts = new JPanel();
        contacts.setLayout(new BorderLayout());
        final IdentityPanel contactsPanel = new IdentityPanel(this);
        contacts.add(contactsPanel, BorderLayout.CENTER);

        actions = contactsPanel.getActions();
        AddIdentityAction addId = null;
        for (int i = 0; i < actions.length; i++) {
            Action action = actions[i];
            contactTasks.add(action);
            if (action instanceof AddIdentityAction)
                addId = (AddIdentityAction) action;
        }

        tabbed.addTab(AgentMessages.getText("contacts"), ICON_CONTACTS, contacts);
        final JPanel assets = new JPanel();
        assets.setLayout(new BorderLayout());
        final AssetPanel assetPanel = new AssetPanel(this);
        assets.add(assetPanel, BorderLayout.CENTER);
        tabbed.addTab(AgentMessages.getText("assets"), ICON_ASSETS, assets);
        actions = assetPanel.getActions();
        AddIdentityAction addAsset = null;
        for (int i = 0; i < actions.length; i++) {
            Action action = actions[i];
            assetTasks.add(action);
            if (action instanceof AddIdentityAction)
                addAsset = (AddIdentityAction) action;
        }

        JTaskPaneGroup signingTasks = new JTaskPaneGroup();
        signingTasks.setIcon(IconTools.getSign());
        signingTasks.setText("Signing tasks");
        signingTasks.setEnabled(true);
        taskpane.add(signingTasks);

        StatusBar status = new StatusBar();

        message = new MessageLabel();
        status.addZone("messages", message, "90%");
        content.add(status, BorderLayout.SOUTH);

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
        TransferAction maketransfer = new TransferAction(this, signer, assetPanel, contactsPanel);
        assetTasks.add(maketransfer);
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
        JMenu directory = new JMenu("Directory");
        directory.setMnemonic(KeyEvent.VK_D);
        directory.add(createWebMenuItem("Search Yellow Pages", "http://pkyp.org", message));
        directory.add(createWebMenuItem("Submit Page to Yellow Pages", "http://pkyp.org/submit.jsp", message));

        menubar.add(directory);

        helpmenu = new JMenu("Help");
        helpmenu.setMnemonic(KeyEvent.VK_H);
        menubar.add(helpmenu);

        createWebHelpMenuItem("Personal Trader Help", "http://neuclear.org/display/neu/Personal+Trader", message);
        createWebHelpMenuItem("NeuClear Site", "http://neuclear.org", message);
        createWebHelpMenuItem("The Two-Minute NeuClear Tour", "http://neuclear.org/display/neu/The+Two-Minute+NeuClear+Tour", message);
        helpmenu.addSeparator();
//        createWebHelpMenuItem("Road Map", "http://jira.neuclear.org/secure/BrowseProject.jspa?id=10030&report=roadmap", message);
        createWebHelpMenuItem("Report Bug or Suggest Feature", "http://jira.neuclear.org/secure/CreateIssue!default.jspa", message);
        helpmenu.addSeparator();
        createWebHelpMenuItem("Developers Blog", "http://talk.org", message);
        helpmenu.addSeparator();
        JMenuItem about = new JMenuItem("About");
        about.setMnemonic(KeyEvent.VK_A);
        helpmenu.add(about);
        final MainFrame frame = this;

        MailQue.addListener(new MailWatcher() {
            public void deliverySuccess(MimeMessage message, Collection recepients) {
                try {
                    frame.message.info("Message: " + message.getSubject() + " sent successfully");
                } catch (MessagingException e) {
                    frame.message.error(e);
                }
            }

            public void deliveryFailure(MimeMessage message, Collection recepients) {
                try {
                    frame.message.error("Message: " + message.getSubject() + " could not be sent");
                } catch (MessagingException e) {
                    frame.message.error(e);
                }

            }
        });
        about.addActionListener(new ActionListener() {

            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                new SplashWindow(frame, Toolkit.getDefaultToolkit().createImage(QuickStart.class.getClassLoader().getResource("neuclearsplash.jpg"))).show();
            }

        });

        placeWindow();
        show();
        setEnabled(false);
        CryptoTools.ensureProvider();
        server = new SigningServer(signer, message, addId, addAsset);
        server.start();
        if (splash != null)
            splash.dispose();
        toFront();
        while (!signer.isOpen()) {
            try {
                signer.open();
            } catch (UserCancellationException e) {
                int choice = JOptionPane.showOptionDialog(this, "You need to open or create an accounts file to start NeuClear Trader.",
                        "Really Quit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, QUIT_OPTIONS, QUIT_OPTIONS[1]);
                if (choice == JOptionPane.NO_OPTION) // Yeah I know it doesnt make sense
                    System.exit(0);

            }
        }
        setEnabled(true);

    }

    private void placeWindow() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(400, (screenDim.getHeight() < 500 ? (int) screenDim.getHeight() : 500));
        setLocation((screenDim.width - getWidth() - 10),
                (screenDim.height - getHeight()) - 25);
    }

    private void createWebHelpMenuItem(final String title, final String url, final MessageLabel message) {
        JMenuItem item = createWebMenuItem(title, url, message);
        helpmenu.add(item);
    }

    private JMenuItem createWebMenuItem(final String title, final String url, final MessageLabel message) {
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
        return item;
    }

    public static void main(String args[]) {
        final MainFrame main = new MainFrame(null);
    }

    private final SigningServer server;
    private SignDocumentAction signdoc;
    private final Icon webicon = IconTools.loadIcon(StandaloneSigner.class, "org/neuclear/signers/standalone/icons/browser.png");
    private JMenu helpmenu;
    private PersonalSigner signer;
    public static final Icon ICON_CONTACTS = IconTools.loadIcon(MainFrame.class, "org/neuclear/signers/standalone/icons/contacts.png");
    public static final Icon ICON_ASSETS = IconTools.loadIcon(MainFrame.class, "org/neuclear/signers/standalone/icons/assets.png");
    public static final String[] QUIT_OPTIONS = new String[]{"Try again", "Quit"};
    private MessageLabel message;
}
