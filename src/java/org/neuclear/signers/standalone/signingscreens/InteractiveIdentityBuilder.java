package org.neuclear.signers.standalone.signingscreens;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.neuclear.commons.Utility;
import org.neuclear.id.builders.IdentityBuilder;

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
 * Date: May 27, 2004
 * Time: 12:25:28 PM
 */
public class InteractiveIdentityBuilder extends IdentityBuilder {
    public InteractiveIdentityBuilder() {
        super("http://pkyp.org");
        setNickname("Your Nickname");
        image = addBlock("image");
        whoami = addBlock("whoami");
        email = addBlock("email");
        blog = addBlock("blog");
        website = addBlock("website");
        where = addBlock("where");
        more = addBlock("more");

    }

    private Element addBlock(String id) {
        final Element element = body.addElement("div");
        element.addAttribute("id", id);
        return element;

    }

    public void set(Element elem, String text) {
        if (elem.getName().equals("h3") && Utility.isEmpty(text))
            elem.setName("div");
        else if (elem.getName().equals("div") && !Utility.isEmpty(text))
            elem.setName("h3");
        elem.setText(text);
    }

    public void setWho(String name) {
        set(whoami, name);
    }

    private Element whoami;
    private Element blog;
    private Element image;
    private Attribute imageurl;
    private Element more;
    private Element email;
    private Element website;
    private Element where;

}
