package org.neuclear.signers.standalone.identitylists;

import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.id.Identity;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.signers.standalone.identitylists.actions.AddIdentityAction;
import org.neuclear.signers.standalone.identitylists.actions.RemoveIdentityAction;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

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
 * Date: May 15, 2004
 * Time: 5:26:23 PM
 */
public class AssetPanel extends IdentityPanel {
    public AssetPanel(JFrame frame) {
        super(frame, "Assets");
        IdentityTreeModel model = (IdentityTreeModel) tree.getModel();
//        model.addCategory("Money");
/*
        try {
            model.addIdentity("Money", Resolver.resolveIdentity("http://bux.neuclear.org/bux.html"));
        } catch (NameResolutionException e) {
            e.printStackTrace();
        } catch (InvalidNamedObjectException e) {
            e.printStackTrace();
        }
        expandTree();
*/

//        model.addCategory("Financial");
//        model.addCategory("Telecommunications");
//        model.addCategory("Entertainment");
//        model.addCategory("Misc");

    }

    protected AddIdentityAction createAddAction() {
        return new AddIdentityAction(frame, tree, "addasset", IconTools.loadIcon(this.getClass(), "org/neuclear/signers/standalone/icons/asset_new.png"));
    }

    protected RemoveIdentityAction createRemoveAction() {
        return new RemoveIdentityAction(frame, tree, "removeasset", IconTools.loadIcon(this.getClass(), "org/neuclear/signers/standalone/icons/asset_remove.png"));
    }

    protected void addDefaults() {
        IdentityTreeModel model = (IdentityTreeModel) tree.getModel();
        DefaultMutableTreeNode money = model.addCategory("Money");
        try {
            model.addIdentity(money, (Identity) Resolver.resolve("http://bux.neuclear.org/bux.html"));
            model.addIdentity(money, (Identity) Resolver.resolve("http://beta.veraxpay.com/rules.html"));
        } catch (NameResolutionException e) {
            e.printStackTrace();
        } catch (InvalidNamedObjectException e) {
            e.printStackTrace();
        }

        model.addCategory("Financial");
        model.addCategory("Telecom");
        model.addCategory("Entertainment");
        model.addCategory("Manufactured Goods");
        model.addCategory("Services");
        model.addCategory("Misc");

    }
}
