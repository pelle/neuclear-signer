package org.neuclear.signers.standalone.identitylists;

import javax.swing.*;

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
 * Date: May 19, 2004
 * Time: 12:26:42 PM
 */
public class IdentityComboBox extends JComboBox {
    public IdentityComboBox() {
        setRenderer(new IdentityListCellRenderer());
        setEditable(true);
/*
        addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==ItemEvent.SELECTED&&(e.getItem() instanceof DefaultMutableTreeNode) ){
                    DefaultMutableTreeNode node=(DefaultMutableTreeNode) e.getItem();
                    if (node.getChildCount()>0)
                        ((JComboBox)e.getItemSelectable()).setSelectedItem(node.getChildAt(1));
                    else
                        ((JComboBox)e.getItemSelectable()).setSelectedIndex(-1);
                }
            }
        });
*/
    }
}
