package org.neuclear.signers.standalone;

import org.neuclear.commons.crypto.signers.NonExistingSignerException;
import org.neuclear.commons.crypto.signers.PersonalSigner;
import org.neuclear.xml.XMLException;
import org.neuclear.xml.xmlsec.KeyInfo;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.security.PublicKey;

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
 * Time: 11:13:24 AM
 */
public class PublicKeyTransferHandler extends TransferHandler implements ClipboardOwner {
    public PublicKeyTransferHandler(PersonalSigner signer) {
        this.signer = signer;
    }

    private PersonalSigner signer;

    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return false;
    }

    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        System.out.println("Copy operation");
        if (action == COPY && comp instanceof JList) {
            String selected = (String) ((JList) comp).getSelectedValue();
            if (selected != null && signer.canSignFor(selected)) {
                try {
                    PublicKey pub = signer.getPublicKey(selected);
                    String ki = new KeyInfo(pub).asXML();
                    clip.setContents(new StringSelection(ki), this);
                    System.out.println("Set Clipboard");
                } catch (NonExistingSignerException e) {
                    e.printStackTrace();
                } catch (XMLException e) {
                    e.printStackTrace();
                }

            }

        }
    }


    protected Transferable createTransferable(JComponent c) {
        System.out.println("Drag");
        if (c instanceof JList) {
            String selected = (String) ((JList) c).getSelectedValue();
            if (selected != null && signer.canSignFor(selected)) {
                try {
                    PublicKey pub = signer.getPublicKey(selected);
                    String ki = new KeyInfo(pub).asXML();
                    System.out.println("Created Transferable");
                    return new StringSelection(ki);
                } catch (NonExistingSignerException e) {
                    e.printStackTrace();
                } catch (XMLException e) {
                    e.printStackTrace();
                }

            }

        }
        return null;
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

}
