package org.neuclear.signers.standalone.identitylists.actions;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.neuclear.commons.crypto.passphraseagents.AgentMessages;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.swing.LongChildProcess;
import org.neuclear.commons.swing.ProcessDialog;
import org.neuclear.id.Identity;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.signers.standalone.identitylists.IdentityNode;
import org.neuclear.signers.standalone.identitylists.IdentityTreeModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
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
 * Date: May 18, 2004
 * Time: 1:28:39 PM
 */
public class AddIdentityDialog extends ProcessDialog {
    public AddIdentityDialog(Frame frame, String id) {
        super(frame, id);
        url.addKeyListener(keyValidator);
        url.addActionListener(okAction);
//        categories.addActionListener(okAction);
    }

    protected boolean validateForm() {
        return (url.getText().length() > 0) && (categories.getSelectedItem() != null);
    }

    protected void clear() {
        url.setText("");
//        categories.setSelectedItem(null);
    }

    protected Component buildPanel() {
        FormLayout layout = new FormLayout("right:pref, 3dlu, pref:grow ",
                "pref,3dlu,pref,5dlu:grow");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.setDefaultDialogBorder();
        url = new JTextField();

        builder.addLabel(AgentMessages.getTitle(id + ".url"), cc.xy(1, 1)).setLabelFor(url);
        builder.add(url, cc.xy(3, 1));
        categories = new JComboBox();
        categories.setEditable(true);
        builder.addLabel(AgentMessages.getTitle(id + ".categories"), cc.xy(1, 3)).setLabelFor(categories);
        builder.add(categories, cc.xy(3, 3));

        return builder.getPanel();
    }

    protected void initializeForm() {
        url.requestFocus();
    }

    public void addContact(JTree tree, String defurl) throws UserCancellationException {
        DefaultMutableTreeNode category = null;
        final IdentityTreeModel model = (IdentityTreeModel) tree.getModel();
        categories.setModel(model.getCategoriesModel());
        if (tree.getSelectionPath() != null) {
            MutableTreeNode node = (MutableTreeNode) tree.getSelectionPath().getLastPathComponent();
            if (node instanceof IdentityNode) {
                category = (DefaultMutableTreeNode) node.getParent();
            } else if ((node instanceof DefaultMutableTreeNode) && !node.equals(tree.getModel().getRoot())) {
                category = (DefaultMutableTreeNode) node;
            }
        }
        if (category != null)
            categories.setSelectedItem(category);
        if (defurl != null) {
            url.setText(defurl);
            ok.setEnabled(true);
        }
        Object tmp = categories.getSelectedItem();
        Identity id = (Identity) openAndWait(new LongChildProcess() {
            public void run() {
                try {
                    Identity id = Resolver.resolveIdentity(url.getText());
                    setResult(id);
                } catch (NameResolutionException e) {
                    parent.error("Unknown Page: " + url.getText());
                } catch (InvalidNamedObjectException e) {
                    parent.error("The page at " + url.getText() + " was not signed");
                }
            }
        });
        Object selected = categories.getSelectedItem();
        if (selected instanceof DefaultMutableTreeNode)
            category = (DefaultMutableTreeNode) selected;
        else
            category = model.getCategory(selected.toString());
        TreeNode idnode = model.addIdentity(category, id);
        final TreePath path = new TreePath(new Object[]{model.getRoot(), category, idnode});
        tree.setSelectionPath(path);
        tree.expandPath(path);
        tree.scrollPathToVisible(path);
    }

    private JTextField url;
    private JComboBox categories;

}
