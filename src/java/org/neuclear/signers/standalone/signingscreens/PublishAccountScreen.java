package org.neuclear.signers.standalone.signingscreens;

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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.signers.PersonalSigner;
import org.neuclear.commons.swing.LongChildProcess;
import org.neuclear.commons.swing.ProcessDialog;
import org.neuclear.id.Identity;
import org.neuclear.id.builders.IdentityBuilder;
import org.neuclear.id.senders.Sender;
import org.neuclear.signers.standalone.identitylists.IdentityPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: pelleb
 * Date: May 19, 2004
 * Time: 11:22:52 AM
 */
public class PublishAccountScreen extends ProcessDialog {
    public PublishAccountScreen(Frame frame, PersonalSigner signer, IdentityPanel contacts, Icon icon) {
        super(frame, "publishid", icon);
        this.signer = signer;
        this.contacts = contacts;
        this.setModal(false);
        this.setResizable(true);
        nickname.addKeyListener(keyValidator);
        nickname.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {
                url.setText("http://pkyp.org/" + nickname.getText() + "/");
            }

            public void keyTyped(KeyEvent e) {

            }

        });
    }

    protected boolean validateForm() {
        message.clear();
        return true;
    }

    protected Component buildPanel() {
        FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow",
                "pref,3dlu,pref,3dlu,pref:grow,3dlu,pref,3dlu,pref,3dlu,pref,3dlu,pref,3dlu,pref:grow,3dlu,pref:grow,5dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        nickname = new JTextField();
        builder.addLabel("nickname", cc.xy(1, 1)).setLabelFor(nickname);
        builder.add(nickname, cc.xy(3, 1));
        url = new JTextField();
        builder.addLabel("Page Address", cc.xy(1, 3)).setLabelFor(url);
        builder.add(url, cc.xy(3, 3));
        url.setEditable(false);
        whoami = new JTextField();

        builder.addLabel("Who am I?", cc.xy(1, 5)).setLabelFor(whoami);
        builder.add(whoami, cc.xy(3, 5));
        blog = new JTextField();
        builder.addLabel("My blog", cc.xy(1, 7)).setLabelFor(blog);
        builder.add(blog, cc.xy(3, 7));
        imageurl = new JTextField();
        builder.addLabel("My Picture", cc.xy(1, 9)).setLabelFor(imageurl);
        builder.add(imageurl, cc.xy(3, 9));
        email = new JTextField();
        builder.addLabel("email", cc.xy(1, 11)).setLabelFor(email);
        builder.add(email, cc.xy(3, 11));
        website = new JTextField();
        builder.addLabel("My web site", cc.xy(1, 13)).setLabelFor(website);
        builder.add(website, cc.xy(3, 13));
        where = new JTextField();
        builder.addLabel("Where do I live?", cc.xy(1, 15)).setLabelFor(where);
        builder.add(where, cc.xy(3, 15));
        more = new JTextField();
        builder.addLabel("More about myself", cc.xy(1, 17)).setLabelFor(more);
        builder.add(more, cc.xy(3, 17));
        builder.setDefaultDialogBorder();
        return builder.getPanel();
    }

    public void openPublishScreen() throws UserCancellationException {
        validateForm();
        openAndWait(new LongChildProcess() {
            public void run() {
                try {

                    builder = new IdentityBuilder(nickname.getText(), url.getText(), "http://portfolio.neuclear.org/Receive", whoami.getText());
                    builder.addImage("image", imageurl.getText());
                    builder.addWebLink("blog", "My Blog", blog.getText());
                    builder.addEmail("email", "Email", email.getText());
                    builder.addWebLink("website", "My Blog", website.getText());
                    builder.addBlock("where", "Where am I?", where.getText());
                    builder.addBlock("more", "More about me", more.getText());
                    builder.addAddContactLink();
                    Identity id = (Identity) builder.convert(signer);
                    System.out.println("Sending");
                    Sender.quickSend("http://pkyp.org/Post", id);
                    setResult(id);
                } catch (NeuClearException e) {
                    e.printStackTrace();
                    parent.error("Server Error");
                }
            }

        });
    }


    private PersonalSigner signer;
//    private JComboBox accounts;
    private JTextField nickname;
    private JTextField url;
    private JTextField whoami;
    private JTextField blog;
    private JTextField imageurl;
    private JTextField more;
    private JTextField email;
    private JTextField website;
    private JTextField where;

    private IdentityBuilder builder;
    private JComboBox server;
    private IdentityPanel contacts;
}
