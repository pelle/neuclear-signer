package org.neuclear.signers.standalone.identitylists;

import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.Options;
import org.neuclear.signers.standalone.identitylists.actions.AddIdentityAction;
import org.neuclear.signers.standalone.identitylists.actions.RemoveIdentityAction;

import javax.swing.*;
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
 * Time: 4:40:32 PM
 */
public class IdentityPanel extends JPanel {
    public IdentityPanel(String title) {
        this.setLayout(new BorderLayout());
        tree = new IdentityTree(title);
        JToolBar toolbar = new JToolBar();
        toolbar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
        toolbar.setFloatable(true);
        toolbar.setRollover(true);

        addContact = createAddAction();
        final JButton addButton = new JButton(addContact);
        addButton.setText(null);
        addButton.putClientProperty(Options.IS_NARROW_KEY, Boolean.TRUE);

        toolbar.add(addButton);
        removeContact = createRemoveAction();
        final JButton removeButton = new JButton(removeContact);
        removeButton.setText(null);
        removeButton.putClientProperty(Options.IS_NARROW_KEY, Boolean.TRUE);

        toolbar.add(removeButton);
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
        add(new JComboBox((ComboBoxModel) tree.getModel()), BorderLayout.SOUTH);
    }

    protected RemoveIdentityAction createRemoveAction() {
        return new RemoveIdentityAction(tree);
    }

    protected AddIdentityAction createAddAction() {
        return new AddIdentityAction((IdentityListModel) tree.getModel());
    }

    public IdentityPanel() {
        this("Identities");
        IdentityListModel model = (IdentityListModel) tree.getModel();
//        model.addCategory("Friends");
//        try {
//            model.addIdentity("NeuClear Developers", Resolver.resolveIdentity("http://talk.org/pelletest.html"));
//            model.addIdentity("Friends", Resolver.resolveIdentity("http://ao.com.au/iangreen_sig.html"));
        expandTree();

//        } catch (NameResolutionException e) {
//            e.printStackTrace();
//        } catch (InvalidNamedObjectException e) {
//            e.printStackTrace();
//        }
//        model.addCategory("Business");
//        model.addCategory("Family");
//        model.addCategory("Misc");

    }

    public Action[] getActions() {
        return new Action[]{addContact};
    }

    public void expandTree() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    protected final IdentityTree tree;
    private AddIdentityAction addContact;
    private RemoveIdentityAction removeContact;
}
