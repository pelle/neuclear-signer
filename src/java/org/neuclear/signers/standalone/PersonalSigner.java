package org.neuclear.signers.standalone;

import org.neuclear.commons.crypto.CryptoException;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.signers.*;

import javax.swing.*;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.util.Iterator;

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
 * Time: 9:12:42 AM
 */
public class PersonalSigner implements BrowsableSigner {
    public PersonalSigner(JFrame frame) throws UserCancellationException {
//        signer=null;
        this.frame = frame;
        open();
    }

    public final byte[] sign(final String name, final byte[] data) throws NonExistingSignerException, UserCancellationException {
        openIfNecessary();
        return signer.sign(name, data);
    }

    private void openIfNecessary() throws UserCancellationException {
        if (signer == null)
            open();
    }

    private void open() throws UserCancellationException {
        try {
            signer = new TestCaseSigner();
        } catch (InvalidPassphraseException e) {
            e.printStackTrace();
        }

    }


    public final boolean canSignFor(final String name) {
        return signer.canSignFor(name);
    }

    public final int getKeyType(final String name) {
        return signer.getKeyType(name);
    }

    public final PublicKey generateKey(final String alias) throws UserCancellationException {
        openIfNecessary();
        return signer.generateKey(alias);
    }

    public final PublicKey getPublicKey(final String name) throws NonExistingSignerException {
        return signer.getPublicKey(name);
    }

    public byte[] sign(byte data[], SetPublicKeyCallBack callback) throws UserCancellationException {
        openIfNecessary();
        return signer.sign(data, callback);
    }

    public byte[] sign(String name, char pass[], byte data[], SetPublicKeyCallBack callback) throws InvalidPassphraseException {
        return signer.sign(name, pass, data, callback);
    }

    public void createKeyPair(String alias, char passphrase[]) throws CryptoException {
        signer.createKeyPair(alias, passphrase);
    }

    public void save() throws UserCancellationException {
        if (signer != null)
            signer.save();
    }

    public Iterator iterator() throws KeyStoreException {
        if (signer == null)
            return new Iterator() {
                public void remove() {

                }

                public boolean hasNext() {
                    return false;
                }

                public Object next() {
                    return null;
                }

            };
        return signer.iterator();
    }

    private BrowsableSigner signer;
    private JFrame frame;
}
