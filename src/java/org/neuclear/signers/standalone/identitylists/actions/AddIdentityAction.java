package org.neuclear.signers.standalone.identitylists.actions;

import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.commons.crypto.passphraseagents.swing.actions.NeuClearAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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
 * Date: May 16, 2004
 * Time: 1:44:07 PM
 */
public class AddIdentityAction extends NeuClearAction {
    public AddIdentityAction(JFrame frame, JTree tree) {
        this(frame, tree, "addcontact", IconTools.loadIcon(AddIdentityAction.class, "org/neuclear/signers/standalone/icons/contact_new.png"));
    }

    public AddIdentityAction(JFrame frame, JTree tree, String name, Icon icon) {
        super(name, icon);
        this.tree = tree;
        this.frame = frame;
        putValue(SHORT_DESCRIPTION, caps.getString(name));
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
    }

    public void actionPerformed(ActionEvent event) {
        if (dia == null)
            dia = new AddIdentityDialog(frame, name);
        new Thread(new Runnable() {
            public void run() {
                try {
                    dia.addContact(tree, null);
                } catch (UserCancellationException e) {
                    ;
                }

            }
        }).start();
    }

    public void webAddContact(String webAdd) throws UserCancellationException {
        if (dia == null)
            dia = new AddIdentityDialog(frame, name);
        dia.addContact(tree, webAdd);
    }

    private final JTree tree;
    private AddIdentityDialog dia;
    private JFrame frame;
}
