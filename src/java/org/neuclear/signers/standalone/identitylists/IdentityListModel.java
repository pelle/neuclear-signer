package org.neuclear.signers.standalone.identitylists;

import org.neuclear.id.Identity;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.HashMap;

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
public class IdentityListModel extends DefaultTreeModel implements ListModel {
    public IdentityListModel(String title) {
        super(new DefaultMutableTreeNode(title));
        this.title = title;
        list = new ArrayList();
        listeners = new EventListenerList();
        categories = new HashMap();
    }

    public DefaultMutableTreeNode addCategory(String title) {
        DefaultMutableTreeNode category = new DefaultMutableTreeNode(title, true);

        ((DefaultMutableTreeNode) getRoot()).add(category);
        categories.put(title, category);
        return category;
    }

    public DefaultMutableTreeNode getCategory(String title) {
        if (!categories.containsKey(title))
            return addCategory(title);
        return (DefaultMutableTreeNode) categories.get(title);
    }

    public void addIdentity(String category, Identity id) {
        list.add(id);
        getCategory(category).add(new DefaultMutableTreeNode(id, false));
        fireListUpdated();
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
        listeners.add(ListDataListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be removed
     */
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(ListDataListener.class, l);
    }

    protected void fireListUpdated() {
        // Guaranteed to return a non-null array
        Object[] listenerArray = listeners.getListenerList();
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

    private HashMap categories;
    private ArrayList list;
    private EventListenerList listeners;
    private String title;
    private DefaultMutableTreeNode root;

}
