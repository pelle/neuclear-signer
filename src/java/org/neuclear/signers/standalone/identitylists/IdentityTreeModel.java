package org.neuclear.signers.standalone.identitylists;

import org.neuclear.id.Identity;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.*;

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
 * Time: 2:27:25 PM
 */
public class IdentityTreeModel extends DefaultTreeModel {
    public IdentityTreeModel(String title, File file) {
        super(new DefaultMutableTreeNode(title));
//        this.listenerList = new XSSafeEventListenerList(); //XStream doesnt initialize the ListenerList correctly
        this.title = title;
        this.file = file;
        addAutoSave();
    }

    private void addAutoSave() {
        addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent event) {
                save();
            }

            public void treeNodesInserted(TreeModelEvent event) {
                save();
            }

            public void treeNodesRemoved(TreeModelEvent event) {
                save();
            }

            public void treeStructureChanged(TreeModelEvent event) {
                save();

            }

        });
    }

    public synchronized DefaultMutableTreeNode addCategory(String title) {
        DefaultMutableTreeNode category = new DefaultMutableTreeNode(title, true);

        insertNodeInto(category, (MutableTreeNode) root, root.getChildCount());
        return category;
    }

    public DefaultMutableTreeNode getCategory(String title) {
        DefaultMutableTreeNode top = (DefaultMutableTreeNode) root;
        for (int i = 0; i < top.getChildCount(); i++) {
            if (top.getChildAt(i).toString().equals(title))
                return (DefaultMutableTreeNode) top.getChildAt(i);
        }
        return addCategory(title);
    }

    public synchronized TreeNode addIdentity(String category, Identity id) {
        return addIdentity(getCategory(category), id);
    }

    public synchronized TreeNode addIdentity(DefaultMutableTreeNode parent, Identity id) {
        final IdentityNode idnode = new IdentityNode(id);
        insertNodeInto(idnode, parent, parent.getChildCount());
        return idnode;
    }


    public void addIdentity(Identity id) {
        addIdentity("Misc", id);
    }

    private synchronized void save() {
        try {
            System.out.println("Saving");
            saveSerialized();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSerialized() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        oos.writeObject(this);
        oos.close();
    }

    private void setFile(File file) {
        this.file = file;
    }

    public static IdentityTreeModel getModel(String title) {
        File file = new File(System.getProperty("user.home") + "/.neuclear/ui/" + title + ".ser");
        if (file.exists() && file.length() > 0) {
            try {
                return loadSerialized(file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        file.getParentFile().mkdirs();
        return new IdentityTreeModel(title, file);
    }


    private static IdentityTreeModel loadSerialized(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        IdentityTreeModel model = (IdentityTreeModel) ois.readObject();
        ois.close();
        model.setFile(file);
        model.addAutoSave();
        return model;
    }

    public final MutableComboBoxModel getCategoriesModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 0; i < ((DefaultMutableTreeNode) getRoot()).getChildCount(); i++)
            model.addElement(((DefaultMutableTreeNode) getRoot()).getChildAt(i));
        return model;
    }

    public final MutableComboBoxModel getComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        getNodes(model, (TreeNode) getRoot());
        return model;
    }

    private void getNodes(DefaultComboBoxModel model, final TreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            final TreeNode child = node.getChildAt(i);
            if (child instanceof IdentityNode)
                model.addElement(child);
            else
                getNodes(model, child);
        }
    }

    private transient File file;
    private String title;
}
