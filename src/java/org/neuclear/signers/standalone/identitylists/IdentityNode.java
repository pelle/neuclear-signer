package org.neuclear.signers.standalone.identitylists;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.commons.Utility;
import org.neuclear.id.Identity;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.resolver.Resolver;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.Enumeration;

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
 * Date: May 17, 2004
 * Time: 9:24:47 AM
 */
public class IdentityNode implements MutableTreeNode, Serializable {
    public IdentityNode(Identity id) {
        this.digest = id.getDigest();
        this.title = Utility.denullString(id.getNickname(), id.getName().substring(0, 4) + '-'
                + id.getName().substring(4, 8)) + (Utility.isEmpty(id.getURL()) ? "" : ("(" + id.getURL() + ")"));
        this.type = (id instanceof Asset) ? ASSET_TYPE : ID_TYPE;
        this.url = id.getURL();
    }

    public String getUrl() {
        return url;
    }

    public String getDigest() {
        return digest;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public Identity getIdentity() throws NameResolutionException, InvalidNamedObjectException {
        try {
            return Resolver.resolveIdentity(digest);
        } catch (NameResolutionException e) {
            return Resolver.resolveIdentity(url);
        }
    }

    public int getChildCount() {
        return 0;
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public Enumeration children() {
        return new Enumeration() {
            public boolean hasMoreElements() {
                return false;
            }

            public Object nextElement() {
                return null;
            }

        };
    }

    public TreeNode getParent() {
        return parent;
    }

    public TreeNode getChildAt(int i) {
        return null;
    }

    public int getIndex(TreeNode node) {
        return -1;
    }

    public String toString() {
        return title;
    }

    public void removeFromParent() {
        parent.remove(this);
    }

    public void remove(int i) {

    }

    public void setUserObject(Object o) {

    }

    public void remove(MutableTreeNode node) {

    }

    public void setParent(MutableTreeNode node) {
        parent = node;
    }

    public void insert(MutableTreeNode node, int i) {

    }


    private MutableTreeNode parent;
    private String digest;
    private String title;
    private int type;
    public static final int ASSET_TYPE = 1;
    public static final int ID_TYPE = 0;
    private String url;

}
