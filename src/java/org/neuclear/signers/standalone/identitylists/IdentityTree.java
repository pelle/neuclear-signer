package org.neuclear.signers.standalone.identitylists;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

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
 * Time: 3:20:21 PM
 */
public class IdentityTree extends JTree {
    public IdentityTree(String title) {
        this(IdentityListModel.getModel(title));
        setShowsRootHandles(false);
//        setRootVisible(false);
//        expandPath(new TreePath((TreeNode) getModel().getRoot()));
        setCellRenderer(new IdentityTreeCellRenderer());
        getModel().addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {

            }

            public void treeNodesInserted(TreeModelEvent e) {
                scrollPathToVisible(e.getTreePath());
            }

            public void treeNodesRemoved(TreeModelEvent e) {

            }

            public void treeStructureChanged(TreeModelEvent e) {

            }

        });
    }


    public IdentityTree(IdentityListModel model) {
        super(model);
    }

}
