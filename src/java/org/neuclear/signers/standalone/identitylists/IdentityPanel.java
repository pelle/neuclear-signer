package org.neuclear.signers.standalone.identitylists;

import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.resolver.Resolver;

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
        toolbar.add(new JButton("add"));
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    public IdentityPanel() {
        this("Identities");
        IdentityListModel model = (IdentityListModel) tree.getModel();
//        model.addCategory("Friends");
        try {
            model.addIdentity("NeuClear Developers", Resolver.resolveIdentity("http://talk.org/pelletest.html"));
            model.addIdentity("Friends", Resolver.resolveIdentity("http://ao.com.au/iangreen_sig.html"));
        } catch (NameResolutionException e) {
            e.printStackTrace();
        } catch (InvalidNamedObjectException e) {
            e.printStackTrace();
        }
//        model.addCategory("Business");
//        model.addCategory("Family");
//        model.addCategory("Misc");

    }

    protected final IdentityTree tree;
}
