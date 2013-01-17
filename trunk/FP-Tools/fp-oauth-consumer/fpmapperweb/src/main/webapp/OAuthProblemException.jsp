<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="net.oauth.OAuthProblemException"%>
<%@page import="net.oauth.server.OAuthServlet"%>
<HTML>
<body>
<%
    OAuthProblemException p = (OAuthProblemException) request.getAttribute("OAuthProblemException");
    for (Iterator i = p.getParameters().entrySet().iterator(); i.hasNext(); ) {
        Map.Entry parameter = (Map.Entry) i.next();
        Object v = parameter.getValue();
        Object k = parameter.getKey();

        if (k != null) {
            String key = k.toString();

            if (OAuthProblemException.OAUTH_PROBLEM.equals(key)) {
            	String value = v != null ? v.toString() : "";
                %><%=OAuthServlet.htmlEncode(value)%>
                <%
            }
            %>
    <%
        }
    }
%>
</body>
</HTML>
