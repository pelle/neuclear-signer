package org.neuclear.signers.standalone.identitylists;

import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.signers.standalone.identitylists.actions.AddIdentityAction;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.IOException;

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
 * Date: May 31, 2004
 * Time: 11:43:08 AM
 */
public class ContactTransferHandler extends TransferHandler implements ClipboardOwner {
    public ContactTransferHandler(AddIdentityAction addcontact) {
        this.addcontact = addcontact;
    }

    private AddIdentityAction addcontact;

    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        if (comp instanceof IdentityTree) {
            IdentityTree tree = (IdentityTree) comp;
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof IdentityNode) {
                IdentityNode node = (IdentityNode) tree.getSelectionPath().getLastPathComponent();
                clip.setContents(new StringSelection(node.getUrl()), this);
                if (action == MOVE) {
                    node.removeFromParent();
                }
            }
        }

    }

    public boolean importData(JComponent comp, Transferable t) {
        if (comp instanceof IdentityTree && (t.isDataFlavorSupported(DataFlavor.stringFlavor))) {
            try {
                final String contacturl = (String) t.getTransferData(DataFlavor.stringFlavor);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            addcontact.webAddContact(contacturl);
                        } catch (UserCancellationException e) {
                            e.printStackTrace();
                        }

                    }

                }).start();
                return true;
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if (!(comp instanceof IdentityTree))
            return false;
        for (int i = 0; i < transferFlavors.length; i++) {
            if (transferFlavors[i].isFlavorTextType())
                return true;
        }
        return false;
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
