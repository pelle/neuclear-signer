package org.neuclear.signers.standalone;

import org.neuclear.commons.Utility;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.signers.standalone.identitylists.actions.AddIdentityAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
 * Date: May 26, 2004
 * Time: 2:37:38 PM
 */
public class AddContactServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!request.getRemoteAddr().equals("127.0.0.1"))
            ((HttpServletResponse) response).sendError(500, "No external access allowed");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html");
        final PrintWriter out = response.getWriter();
        out.println("<html><head><title>Add Contact");
        out.println("</title><style>\n\t#banner {\n" +
                "\t\tfont-family:Arial Rounded MT Bold, \"Bitstream Vera Sans\",\"Trebuchet MS\", verdana, lucida, arial, helvetica, sans-serif;\n" +
                "\t\ttext-align: left;\n" +
                "\t\tcolor:#FFF;\n" +
                "\t\tfont-size:24px;\n" +
                "\t\tfont-weight:bolder;\n" +
                "\t\tborder-top:10px solid #FFFFFF;\n" +
                "\t\tborder-bottom:2px solid #009;\n" +
                "  \t\tbackground:#0000FF;\n" +
                "  \t\tpadding-left:15px;\n" +
                "\t\tpadding-top:3px;\n" +
                "\t\tpadding-bottom:3px;\n" +
                "\n" +
                "\t\tletter-spacing: .2em;\n" +
                "\t\t}\nbody\t{\n" +
                "\t\tbackground: #FFF ;\n" +
                "\t\tcolor: black;\n" +
                "        margin:0px 0px 10px 0px;\n" +
                "\t\tborder-top: 5px solid #white;\n" +
                "\t\ttext-align: left;\n" +
                "\t\tfont-family: \"Trebuchet MS\", \"Bitstream Vera Sans\", verdana, lucida, arial, helvetica, sans-serif;\n" +
                "\t\tfont-size: small;\n" +
                "\t\tpadding-bottom: 25px;\n" +
                "\t}\n#content {margin-left:20;margin-right:20;background:#F0F0FF;}\n" +
                "\t#log {background: #008;font-size:14px;font-weight:bolder;color:white;}" +
                "</style></head><body><div id=\"banner\">NeuClear Personal Trader - Add Contact</div>");
        String contacturl = request.getQueryString();
        if (Utility.isEmpty(contacturl)) {
            out.println("No Account Page");
            return;
        }
        final String referrer = request.getHeader("Referer");

        out.print("Attempting to do Add <a href=\"");
        out.print(contacturl);
        out.print("\">");
        out.print(contacturl);
        out.println("</a> to your Contacts. If you can't see your window have a look around to see if it is behind your browser.");
        out.flush();
        try {
            action.webAddContact(contacturl);
        } catch (UserCancellationException e) {
            out.print("Cancelled<br/><a href=\"");
            out.print(referrer);
            out.println("\">Click to go back</a>");
            return;
        }
        out.println("Added<br/><a href=\"");
        out.print(referrer);
        out.println("\">Click to go back</a>");
        return;
    }

    void setAddIdentityAction(AddIdentityAction action) {
        this.action = action;
        System.out.println("Added AddIdentity Action");
    }

    private AddIdentityAction action;
}
