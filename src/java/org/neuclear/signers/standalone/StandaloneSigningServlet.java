package org.neuclear.signers.standalone;

import org.neuclear.commons.crypto.passphraseagents.InteractiveAgent;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.passphraseagents.swing.SwingAgent;
import org.neuclear.commons.crypto.signers.BrowsableSigner;
import org.neuclear.commons.crypto.signers.DefaultSigner;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.id.signers.SigningServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Dec 15, 2003
 * Time: 9:20:32 AM
 * To change this template use Options | File Templates.
 */
public class StandaloneSigningServlet extends SigningServlet {
    public StandaloneSigningServlet() {
        this.agent = new SwingAgent();
    }

    protected BrowsableSigner createSigner(ServletConfig config) throws UserCancellationException {
        return new DefaultSigner(agent);
    }

    protected String getTitle() {
        return "NeuClear Personal Signing Service";
    }
/*
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if(!request.getRemoteAddr().equals("127.0.0.2"))
            ((HttpServletResponse)response).sendError(500,"No external access allowed");
        super.service(request, response);
    }
*/
    public BrowsableSigner getSigner() {
        return super.getSigner();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        ServletTools.printHeader(writer, request, "NeuClear Signing Servlet");

    }


    private final InteractiveAgent agent;

}
