package org.neuclear.signers.standalone.identitylists;

import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.Options;
import org.neuclear.id.Identity;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.signers.standalone.identitylists.actions.AddIdentityAction;
import org.neuclear.signers.standalone.identitylists.actions.RemoveIdentityAction;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
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
 * Date: May 15, 2004
 * Time: 4:40:32 PM
 */
public class IdentityPanel extends JPanel {
    public IdentityPanel(JFrame frame, String title) {
        this.frame = frame;
        this.setLayout(new BorderLayout());
        tree = new IdentityTree(title);
        JToolBar toolbar = new JToolBar();
        toolbar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
        toolbar.setFloatable(true);
        toolbar.setRollover(true);

        if (tree.getModel().getChildCount(tree.getModel().getRoot()) == 0)
            addDefaults();
        expandTree();
        addContact = createAddAction();
        final JButton addButton = new JButton(addContact);
//        addButton.setText(null);
        addButton.putClientProperty(Options.IS_NARROW_KEY, Boolean.TRUE);

        toolbar.add(addButton);
        removeContact = createRemoveAction();
        final JButton removeButton = new JButton(removeContact);
//        removeButton.setText(null);
        removeButton.putClientProperty(Options.IS_NARROW_KEY, Boolean.TRUE);

        toolbar.add(removeButton);
        add(toolbar, BorderLayout.NORTH);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(split, BorderLayout.CENTER);
        final JScrollPane treeScroll = new JScrollPane(tree);
        final Dimension min = new Dimension(100, 100);
        treeScroll.setMinimumSize(min);
        treeScroll.setPreferredSize(new Dimension(100, 200));
        split.setTopComponent(treeScroll);

        preview = new JEditorPane("text/html", "");
        preview.setEditable(false);
        final JScrollPane scroll = new JScrollPane(preview);
        scroll.setAutoscrolls(true);
        scroll.setMinimumSize(min);
        split.setBottomComponent(scroll);
        preview.setVisible(true);
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
                final Object selected = e.getPath().getLastPathComponent();
                if (selected != null && selected instanceof IdentityNode) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                final String encoded = ((IdentityNode) selected).getIdentity().getEncoded();
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        preview.setText(encoded);
                                        preview.setVisible(true);
                                        scroll.scrollRectToVisible(new Rectangle(0, 0, scroll.getWidth(), scroll.getHeight()));
                                    }
                                });
                            } catch (NameResolutionException e1) {
                                e1.printStackTrace();
                            } catch (InvalidNamedObjectException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }).start();
                } else {
                    preview.setText("<b>Select an item above</b>");
                }

            }

        });
    }

    protected void addDefaults() {
        IdentityTreeModel model = (IdentityTreeModel) tree.getModel();
        final DefaultMutableTreeNode friends = model.addCategory("Friends");
        try {
            model.addIdentity(friends, (Identity) Resolver.resolve("http://talk.org/pelletest.html"));
        } catch (NameResolutionException e) {
            e.printStackTrace();
        } catch (InvalidNamedObjectException e) {
            e.printStackTrace();
        }
        model.addCategory("Family");
        model.addCategory("Business");
        model.addCategory("Projects");
        model.addCategory("Misc");

    }

    protected RemoveIdentityAction createRemoveAction() {
        return new RemoveIdentityAction(frame, tree);
    }

    protected AddIdentityAction createAddAction() {
        return new AddIdentityAction(frame, tree);
    }

    public IdentityPanel(JFrame frame) {
        this(frame, "Identities");
    }

    public Action[] getActions() {
        return new Action[]{addContact};
    }

    public void expandTree() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public ComboBoxModel getComboBoxModel() {
        final MutableComboBoxModel model = ((IdentityTreeModel) tree.getModel()).getComboBoxModel();
        if (tree.getSelectionPath() != null) {
            TreePath path = tree.getSelectionPath();
            if (path.getLastPathComponent() instanceof IdentityNode)
                model.setSelectedItem(path.getLastPathComponent());
        }
        return model;
    }

    protected final IdentityTree tree;
    private AddIdentityAction addContact;
    private RemoveIdentityAction removeContact;
    private JEditorPane preview;
    protected JFrame frame;
}
