package org.neuclear.signers.standalone;

import org.neuclear.signers.servlet.SigningServlet;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.commons.crypto.signers.DefaultSigner;
import org.neuclear.commons.crypto.signers.InvalidPassphraseException;
import org.neuclear.commons.crypto.passphraseagents.GuiDialogAgent;
import org.neuclear.commons.crypto.passphraseagents.InteractiveAgent;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.CryptoException;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.servlets.ServletTools;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
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
        this.agent = new GuiDialogAgent();
    }

    protected Signer createSigner(ServletConfig config) throws CryptoException {
        try {
            return new DefaultSigner(agent);
        } catch (InvalidPassphraseException e) {
            return createSigner(config);
        }
    }

    protected String getTitle(){
        return "NeuClear Personal Signing Service";
    }
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if(!request.getRemoteAddr().equals("127.0.0.2"))
            ((HttpServletResponse)response).sendError(500,"No external access allowed");
        super.service(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer=response.getWriter();
        ServletTools.printHeader(writer, request,"NeuClear Signing Servlet");

    }
    private final InteractiveAgent agent;

}
