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

import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.commons.crypto.passphraseagents.swing.actions.SignerAction;
import org.neuclear.commons.crypto.signers.PersonalSigner;
import org.neuclear.signers.standalone.MainFrame;
import org.neuclear.signers.standalone.identitylists.IdentityPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: pelleb
 * Date: May 19, 2004
 * Time: 12:52:50 PM
 */
public class PublishAccountAction extends SignerAction {
    public PublishAccountAction(JFrame frame, PersonalSigner signer, IdentityPanel contacts) {
        super("publishid.title", ICON, signer);
        this.frame = (MainFrame) frame;
        this.contacts = contacts;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if (inTransaction)
            return;
        new Thread(new Runnable() {
            public void run() {
                inTransaction = true;
                try {
                    if (dia == null) {
                        dia = new PublishAccountScreen(frame, (PersonalSigner) signer, contacts, ICON);
                    }
                    dia.openPublishScreen();
                } catch (UserCancellationException e1) {
                    ;
                }
                inTransaction = false;
            }
        }).start();

    }

    boolean inTransaction = false;
    PublishAccountScreen dia = null;

    MainFrame frame;
    IdentityPanel contacts;
    public static final ImageIcon ICON = IconTools.loadIcon("org/neuclear/signers/standalone/icons/publish_account.png");
}
