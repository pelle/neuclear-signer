package org.neuclear.signers.standalone.identitylists;

import com.thoughtworks.xstream.XStream;
import org.neuclear.id.Identity;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Date: May 15, 2004
 * Time: 2:27:25 PM
 */
public class IdentityListModel extends DefaultTreeModel implements ComboBoxModel {
    public IdentityListModel(String title, File file) {
        super(new DefaultMutableTreeNode(title));
//        this.listenerList = new XSSafeEventListenerList(); //XStream doesnt initialize the ListenerList correctly
        this.title = title;
        list = new ArrayList();
        categories = new HashMap();
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
//        ((DefaultMutableTreeNode) getRoot());.add(category);
        categories.put(title, category);
//        fireTreeNodesInserted(getRoot(),new Object[]{getRoot()},new int[]{((MutableTreeNode)getRoot()).getIndex(category)},new Object[]{category});
        return category;
    }

    public DefaultMutableTreeNode getCategory(String title) {
        if (!categories.containsKey(title))
            return addCategory(title);
        return (DefaultMutableTreeNode) categories.get(title);
    }

    public synchronized TreeNode addIdentity(String category, Identity id) {
        return addIdentity(getCategory(category), id);
    }

    public synchronized TreeNode addIdentity(DefaultMutableTreeNode parent, Identity id) {
        final IdentityNode idnode = new IdentityNode(id);
        insertNodeInto(idnode, parent, parent.getChildCount());
        list.add(idnode);
        fireListUpdated();
//        save();
        return idnode;
    }

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     *
     * @param l the listener to add
     * @see #removeTreeModelListener
     */
    public void addTreeModelListener(TreeModelListener l) {
        super.addTreeModelListener(l);
        save();
    }

    public void addIdentity(Identity id) {
        addIdentity("Misc", id);
    }

    /**
     * Returns the length of the list.
     *
     * @return the length of the list
     */
    public int getSize() {
        return list.size();
    }

    /**
     * Returns the value at the specified index.
     *
     * @param index the requested index
     * @return the value at <code>index</code>
     */
    public Object getElementAt(int index) {
        return list.get(index);
    }

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be added
     */
    public void addListDataListener(ListDataListener l) {
        listenerList.add(ListDataListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be removed
     */
    public void removeListDataListener(ListDataListener l) {
        listenerList.remove(ListDataListener.class, l);
    }

    protected void fireListUpdated() {
        // Guaranteed to return a non-null array
        Object[] listenerArray = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        ListDataEvent event = null;
        for (int i = listenerArray.length - 1; i >= 0; i--) {
            if (listenerArray[i] instanceof ListDataListener) {
                // Lazily create the event:
                if (event == null)
                    event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize());
                ((ListDataListener) listenerArray[i]).contentsChanged(event);
            }
        }
    }

    public Object getSelectedItem() {
        return selected;
    }

    public void setSelectedItem(Object o) {
        selected = o;
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

    private void saveXStream() throws IOException {
        XStream xs = new XStream();
        xs.toXML(this, new BufferedWriter(new FileWriter(file)));
    }

    private void setFile(File file) {
        this.file = file;
    }

    public static IdentityListModel getModel(String title) {
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
        return new IdentityListModel(title, file);
    }

    private static IdentityListModel loadXStream(File file) throws FileNotFoundException {
        XStream xs = new XStream();
        IdentityListModel model = (IdentityListModel) xs.fromXML(new BufferedReader(new FileReader(file)));
        model.setFile(file);
        return model;
    }

    private static IdentityListModel loadSerialized(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        IdentityListModel model = (IdentityListModel) ois.readObject();
        ois.close();
        model.setFile(file);
        model.addAutoSave();
        return model;
    }

    public final MutableComboBoxModel getCategoriesModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Iterator iter = categories.keySet().iterator();
        while (iter.hasNext())
            model.addElement(iter.next());
        return model;
    }

    private transient File file;
    private Object selected;
    private HashMap categories;
    private ArrayList list;
    private String title;
}
