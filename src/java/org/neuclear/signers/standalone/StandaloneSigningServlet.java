package org.neuclear.signers.standalone;

import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.passphraseagents.swing.MessageLabel;
import org.neuclear.commons.crypto.signers.BrowsableSigner;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.id.signers.SigningServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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

    protected BrowsableSigner createSigner(ServletConfig config) throws UserCancellationException {
        return getSigner();
    }

    protected String getTitle() {
        return "NeuClear Personal Signing Service";
    }

    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (!request.getRemoteAddr().equals("127.0.0.1"))
            ((HttpServletResponse) response).sendError(500, "No external access allowed");
        super.service(request, response);
    }

    public BrowsableSigner getSigner() {
        return super.getSigner();
    }

    public void setSigner(BrowsableSigner signer) {
        super.setSigner(signer);
    }

    public void setMessage(MessageLabel message) {
        this.message = message;
    }

    /**
     * Return True when ready to sign.
     * Multirequest signers, need to verify that the correct request parameters are available.
     *
     * @param request
     * @return
     */
    protected boolean isReadyToSign(HttpServletRequest request) {
        message.info("Incoming Signature Request");
        return super.isReadyToSign(request);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        ServletTools.printHeader(writer, request, "NeuClear Signing Servlet");

    }


    private MessageLabel message;

}
