package org.neuclear.signers.standalone.identitylists.actions;

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

import org.neuclear.commons.crypto.passphraseagents.icons.IconTools;
import org.neuclear.commons.crypto.passphraseagents.swing.actions.NeuClearAction;
import org.neuclear.commons.swing.Messages;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * User: pelleb
 * Date: May 16, 2004
 * Time: 1:44:07 PM
 */
public class RemoveIdentityAction extends NeuClearAction implements TreeSelectionListener {
    public RemoveIdentityAction(JFrame frame, JTree tree) {
        this(frame, tree, "removecontact", IconTools.loadIcon(RemoveIdentityAction.class, "org/neuclear/signers/standalone/icons/contact_remove.png"));
    }

    public RemoveIdentityAction(JFrame frame, JTree tree, String name, Icon icon) {
        super(name, icon);
        this.tree = tree;
        this.frame = frame;
        putValue(SHORT_DESCRIPTION, Messages.getText(name));
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
        this.setEnabled(!tree.isSelectionEmpty());
        tree.addTreeSelectionListener(this);
    }

    public void actionPerformed(ActionEvent event) {
        MutableTreeNode node = (MutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        int response = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove " + node, "Remove item", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
        }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent e) {
        this.setEnabled(!tree.isSelectionEmpty());
        selection = e.getPath();
    }

    private TreePath selection;
    private final JTree tree;
    private final JFrame frame;
}
