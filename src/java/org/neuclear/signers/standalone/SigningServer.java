package org.neuclear.signers.standalone;

import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.util.InetAddrPort;
import org.neuclear.commons.crypto.passphraseagents.swing.MessageLabel;
import org.neuclear.commons.crypto.signers.BrowsableSigner;
import org.neuclear.signers.standalone.identitylists.actions.AddIdentityAction;

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
 * Date: May 15, 2004
 * Time: 11:43:45 AM
 */
public class SigningServer extends Thread {
    public SigningServer(BrowsableSigner signer, MessageLabel messages, AddIdentityAction addId, AddIdentityAction addAsset) {
        this.signer = signer;
        this.messages = messages;
        this.addId = addId;
        this.addAsset = addAsset;
        try {
            server = new Server();
            server.addListener(new InetAddrPort("127.0.0.1", 11870));
            context = server.getContext("/");
            handler = new ServletHandler();
            handler.addServlet("/Signer", "org.neuclear.signers.standalone.StandaloneSigningServlet");
            handler.addServlet("addcontact", "/AddContact", "org.neuclear.signers.standalone.AddContactServlet");
            handler.addServlet("addasset", "/AddAsset", "org.neuclear.signers.standalone.AddContactServlet");
            context.addHandler(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        synchronized (this) {
            try {
                server.start();
                context.start();
                handler.start();
                handler.initializeServlets();
                ServletHolder[] holders = handler.getServlets();
                for (int i = 0; i < holders.length; i++) {
                    ServletHolder holder = holders[i];
                    if (holder.getServlet() instanceof StandaloneSigningServlet) {
                        //                    System.out.println("Found servlet: "+holder.getName());
                        if (!holder.isStarted())
                            holder.start();
                        ((StandaloneSigningServlet) holder.getServlet()).setSigner(signer);
                        ((StandaloneSigningServlet) holder.getServlet()).setMessage(messages);
                    } else if (holder.getName().equals("addcontact")) {
                        if (!holder.isStarted())
                            holder.start();
                        ((AddContactServlet) holder.getServlet()).setAddIdentityAction(addId);
                    } else if (holder.getName().equals("addasset")) {
                        if (!holder.isStarted())
                            holder.start();
                        ((AddContactServlet) holder.getServlet()).setAddIdentityAction(addAsset);


                    }

                }
                isStarted = true;
                messages.info("Signing Servlet Started");
                this.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(messages, "The Personal Trader is already running. We will now exit.", "Already Running", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    public boolean waitForStart() {

        synchronized (this) {
            return isStarted;
        }
    }

    private boolean isStarted = false;
    private final MessageLabel messages;
    private final BrowsableSigner signer;
    private HttpContext context;
    private ServletHandler handler;
    private Server server;
    private AddIdentityAction addId;
    private AddIdentityAction addAsset;
}
