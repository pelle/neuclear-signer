package org.neuclear.signers.standalone.identitylists;

import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.Options;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.signers.standalone.identitylists.actions.AddIdentityAction;
import org.neuclear.signers.standalone.identitylists.actions.RemoveIdentityAction;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        content.add(new JScrollPane(tree), BorderLayout.NORTH);

        preview = new JEditorPane("text/html", "");
        preview.setEditable(false);
        final JScrollPane scroll = new JScrollPane(preview);
        scroll.setSize(300, 0);
        content.add(scroll, BorderLayout.SOUTH);
        preview.setVisible(false);
        preview.addHyperlinkListener(new HyperlinkListener() {
            /**
             * Called when a hypertext link is updated.
             *
             * @param e the event responsible for the update
             */
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
                        // Invoke the showDocument method
                        bs.showDocument(e.getURL());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });


        tree.addTreeSelectionListener(new TreeSelectionListener() {
            /**
             * Called whenever the value of the selection changes.
             *
             * @param e the event that characterizes the change.
             */
            public void valueChanged(TreeSelectionEvent e) {
                Object selected = e.getPath().getLastPathComponent();
                if (selected != null && selected instanceof IdentityNode) {
                    try {
                        preview.setText(((IdentityNode) selected).getIdentity().getEncoded());
                        scroll.setSize(300, 200);

                        preview.setVisible(true);
                    } catch (NameResolutionException e1) {
                        e1.printStackTrace();
                    } catch (InvalidNamedObjectException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    preview.setVisible(false);
                }

            }

        });
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
    private JEditorPane preview;
}
