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
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.passphraseagents.AgentMessages;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.signers.PersonalSigner;
import org.neuclear.commons.swing.LongChildProcess;
import org.neuclear.commons.swing.ProcessDialog;
import org.neuclear.id.Identity;
import org.neuclear.signers.standalone.identitylists.IdentityPanel;

import javax.swing.*;
import java.awt.*;

/**
 * User: pelleb
 * Date: May 19, 2004
 * Time: 11:22:52 AM
 */
public class PublishAccountScreen extends ProcessDialog {
    public PublishAccountScreen(Frame frame, PersonalSigner signer, IdentityPanel contacts, Icon icon) {
        super(frame, "transfer", icon);
        this.signer = signer;
        this.contacts = contacts;
        this.setModal(false);
        TransferGlobals.registerReaders();
    }

    protected boolean validateForm() {
        message.clear();
        return true;
    }

    protected Component buildPanel() {
        FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow,1dlu,pref",
                "pref,3dlu,pref,3dlu,pref,3dlu,pref,3dlu,pref:grow,5dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.setDefaultDialogBorder();
        builder.addLabel(AgentMessages.getText("asset"), cc.xy(1, 1)).setLabelFor(assets);
        builder.add(assets, cc.xyw(3, 1, 3));
        builder.addLabel(AgentMessages.getText("payee"), cc.xy(1, 3)).setLabelFor(contacts);
        builder.add(contacts, cc.xyw(3, 3, 3));
        builder.addLabel(AgentMessages.getText("amount"), cc.xy(1, 5)).setLabelFor(amount);
        builder.add(amount, cc.xy(3, 5));
        units = builder.addLabel("units", cc.xy(5, 5));
        builder.addLabel(AgentMessages.getText("comment"), cc.xyw(1, 7, 5)).setLabelFor(comment);
        builder.add(comment, cc.xyw(1, 9, 5));
        return builder.getPanel();
    }

    public void openPublishScreen() throws UserCancellationException {
        accounts.setModel(signer);
        validateForm();
        openAndWait(new LongChildProcess() {
            public void run() {
                try {
                    Identity id = (Identity) builder.convert(signer);
                    System.out.println("Sending");
                    TransferReceipt receipt = (TransferReceipt) asset.service(order);
                    setResult(receipt);
                } catch (NeuClearException e) {
                    e.printStackTrace();
                    parent.error("Server Error");
                }
            }

        });
    }


    private PersonalSigner signer;
    private JComboBox accounts;
    private JTextField nickname;
    private JTextField url;
    private JTextField whoami;
    private JTextField blog;
    private JTextField imageurl;
    private JTextField more;
    private JTextField email;
    private JTextField website;
    private JTextField where;

    private InteractiveIdentityBuilder builder;
    private JComboBox server;
    private IdentityPanel contacts;
}
