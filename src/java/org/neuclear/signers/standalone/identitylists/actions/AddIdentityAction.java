package org.neuclear.signers.standalone.identitylists.actions;

import org.neuclear.commons.Utility;
import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.commons.crypto.passphraseagents.swing.actions.NeuClearAction;
import org.neuclear.id.Identity;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.signers.standalone.identitylists.IdentityListModel;

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
public class AddIdentityAction extends NeuClearAction implements Runnable {
    public AddIdentityAction(IdentityListModel model) {
        this(model, "addcontact", IconTools.loadIcon(AddIdentityAction.class, "org/neuclear/signers/standalone/icons/contact_new.png"));
    }

    public AddIdentityAction(IdentityListModel model, String name, Icon icon) {
        super(name, icon);
        this.model = model;
        putValue(SHORT_DESCRIPTION, caps.getString(name));
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
    }

    public void actionPerformed(ActionEvent event) {
        url = JOptionPane.showInputDialog("Enter the url:");
        if (!Utility.isEmpty(url))
            new Thread(this).start();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        try {
            Identity id = Resolver.resolveIdentity(url);
            model.addIdentity(id);

        } catch (NameResolutionException e) {
            e.printStackTrace();
        } catch (InvalidNamedObjectException e) {
            e.printStackTrace();
        }

    }

    private final IdentityListModel model;
    private String url;
}