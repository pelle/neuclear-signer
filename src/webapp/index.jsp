<%@ page import="org.neuclear.commons.Utility,
                 org.neuclear.id.SignedNamedObject,
                 org.neuclear.auth.AuthenticationTicket,
                 org.neuclear.id.NSTools,
                 org.neuclear.commons.servlets.ServletTools        ,
                 org.neuclear.id.Identity,
                 org.neuclear.id.resolver.NSResolver"%>
 <%
    response.setHeader("Pragma","no-cache");
    response.setDateHeader("Expires",0);
    Identity userns=(Identity) request.getUserPrincipal();
    boolean loggedin=userns!=null;

 %>
<html>
<head><title>
NeuClear Sample Web App
</title></head>
<body>
<h1>NeuClear Sample Web App</h1>
<%
    if(!loggedin){
%>
<form action="Authorize" method="POST">
<table bgcolor="#FFFFE0"><tr><td valign="top">
    <input name="identity" value="<%=(userns!=null)?userns.getName():""%>" type="text" size="30">
    </td><td valign="top">
    <input type="submit" name="submit" value="Login">
    </td>
</tr>
<tr><td colspan="2" bgcolor="#F0F0FF">
Try logging in using the example neu's below:<br>
<pre>
neu://bob@test
neu://alice@test
</pre>
Use the passphrase: "<tt>neuclear</tt>" when asked.
</td></tr></table>
</form>
<%
    } else {
        %>
        <%=userns.getName()%> is Logged In<br><hr>
        <a href="<%=ServletTools.getAbsoluteURL(request,"/")%>?logout=1">Log Out</a>
        <%
    }
%>


</body>
</html>