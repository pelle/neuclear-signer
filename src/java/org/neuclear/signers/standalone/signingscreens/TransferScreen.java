package org.neuclear.signers.standalone.signingscreens;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.signers.BrowsableSigner;
import org.neuclear.commons.swing.LongChildProcess;
import org.neuclear.commons.swing.Messages;
import org.neuclear.commons.swing.ProcessDialog;
import org.neuclear.id.Identity;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.builders.Builder;
import org.neuclear.signers.standalone.identitylists.IdentityComboBox;
import org.neuclear.signers.standalone.identitylists.IdentityNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
 * Date: May 19, 2004
 * Time: 11:22:52 AM
 */
public class TransferScreen extends ProcessDialog {
    public TransferScreen(Frame frame, BrowsableSigner signer, Icon icon) {
        super(frame, "transfer", icon);
        this.signer = signer;
        amount.addKeyListener(keyValidator);
        amount.addActionListener(okAction);
        comment.addActionListener(okAction);
        this.setModal(false);
        TransferGlobals.registerReaders();
    }

    protected boolean validateForm() {
        if (assets.getSelectedItem() == null) {
            message.invalid("You must select an Asset");
            return false;
        }
        if (contacts.getSelectedItem() == null) {
            message.invalid("You must select a Payee");
            return false;
        }
        if (amount.getText().length() == 0) {
            message.invalid("You must enter an amount");
            return false;
        }
        try {
            double am = Double.parseDouble(amount.getText());
            if (am < 0) {
                message.invalid("Please enter a positive number in the amount field");
                return false;
            }
        } catch (NumberFormatException e) {
            message.invalid("Please enter a valid amount");
            return false;
        }
        message.clear();
        return true;
    }

    protected Component buildPanel() {
        FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow,1dlu,pref",
                "pref,3dlu,pref,3dlu,pref,3dlu,pref,3dlu,pref:grow,5dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        amount = new JTextField();
        comment = new JTextField();
        assets = new IdentityComboBox();

        assets.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    updateUnits();

            }
        });
        contacts = new IdentityComboBox();

        builder.setDefaultDialogBorder();
        builder.addLabel(Messages.getText("asset"), cc.xy(1, 1)).setLabelFor(assets);
        builder.add(assets, cc.xyw(3, 1, 3));
        builder.addLabel(Messages.getText("payee"), cc.xy(1, 3)).setLabelFor(contacts);
        builder.add(contacts, cc.xyw(3, 3, 3));
        builder.addLabel(Messages.getText("amount"), cc.xy(1, 5)).setLabelFor(amount);
        builder.add(amount, cc.xy(3, 5));
        units = builder.addLabel("units", cc.xy(5, 5));
        builder.addLabel(Messages.getText("comment"), cc.xyw(1, 7, 5)).setLabelFor(comment);
        builder.add(comment, cc.xyw(1, 9, 5));
        return builder.getPanel();
    }

    public void openTransferScreen(ComboBoxModel assetModel, ComboBoxModel contactsModel) throws UserCancellationException {
        assets.setModel(assetModel);
        contacts.setModel(contactsModel);
        amount.setText("0.0");
        updateUnits();
        validateForm();
        openAndWait(new LongChildProcess() {
            public void run() {
                try {

                    final Asset asset = (Asset) ((IdentityNode) assets.getSelectedItem()).getIdentity();
                    processInfo("Creating Transfer Order");
                    final Identity recipient = ((IdentityNode) contacts.getSelectedItem()).getIdentity();
                    Builder builder = new TransferOrderBuilder(asset,
                            recipient.getSignatory(),
                            recipient.getURL(),
                            new Amount(Double.parseDouble(amount.getText())),
                            comment.getText());
                    processInfo("Signing Transfer Order");
                    TransferOrder order = (TransferOrder) builder.convert(signer);
                    System.out.println("Sending");
                    processInfo("Sending Transfer Order to " + asset.getServiceUrl());
                    TransferReceipt receipt = (TransferReceipt) asset.service(order);
                    processInfo("Transfer was succesful receipt: " + receipt.getDigest());
                    parent.info("Transfer was succesful receipt: " + receipt.getDigest());
                    setResult(receipt);
                } catch (InvalidTransferException e) {
                    e.printStackTrace();
                    parent.error(e);
                } catch (NeuClearException e) {
                    e.printStackTrace();
                    parent.error("Server Error");
                }
            }

        });
    }

    private void updateUnits() {
        if (assets.getSelectedIndex() >= 0 && (assets.getSelectedItem() instanceof IdentityNode)) {
            try {
                asset = (Asset) ((IdentityNode) assets.getSelectedItem()).getIdentity();
                units.setText(asset.getUnits());
            } catch (NameResolutionException e) {
                e.printStackTrace();
            } catch (InvalidNamedObjectException e) {
                e.printStackTrace();
            }
        }
    }

    private BrowsableSigner signer;
    private JComboBox assets;
    private JComboBox contacts;
    private JLabel units;
    private JTextField amount;
    private JTextField comment;
    private Asset asset;
}
