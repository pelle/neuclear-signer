package org.neuclear.signers.standalone;

import org.neuclear.signers.servlet.SigningServlet;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.commons.crypto.signers.DefaultSigner;
import org.neuclear.commons.crypto.passphraseagents.GuiDialogAgent;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.servlets.ServletTools;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
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
    protected Signer createSigner(ServletConfig config) throws GeneralSecurityException, NeuClearException, IOException {
        return new DefaultSigner(new GuiDialogAgent());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer=response.getWriter();
        ServletTools.printHeader(writer, request,"NeuClear Signing Servlet");

    }
}
