package org.neuclear.signers.standalone.identitylists;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.id.Identity;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

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
 * Time: 4:45:21 PM
 */
public class IdentityTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (leaf && node.getUserObject() instanceof Asset) {
            setIcon(assetIcon);
            Asset asset = (Asset) node.getUserObject();
            setToolTipText(asset.getNickname());
            setText(asset.getNickname() + " (" + asset.getURL() + ")");
        } else if (leaf && node.getUserObject() instanceof Identity) {
            setIcon(contactIcon);
            Identity id = (Identity) node.getUserObject();
            setToolTipText(id.getNickname());
            setText(id.getNickname() + " (" + id.getURL() + ")");
        } else {
            setToolTipText(null); //no tool tip
        }

        return this;
    }

    private Icon folderIcon = IconTools.loadIcon(this.getClass(), "org/neuclear/signers/standalone/icons/bookmark_folder.png");
    private Icon contactIcon = IconTools.loadIcon(this.getClass(), "org/neuclear/signers/standalone/icons/contact.png");
    private Icon assetIcon = IconTools.loadIcon(this.getClass(), "org/neuclear/signers/standalone/icons/asset.png");

}
