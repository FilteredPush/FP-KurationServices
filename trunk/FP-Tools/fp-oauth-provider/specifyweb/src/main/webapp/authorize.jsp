<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%
    String appDesc = (String)request.getAttribute("CONS_DESC");
    String token = (String)request.getAttribute("TOKEN");
    String callback = (String)request.getAttribute("CALLBACK");
    if(callback == null)
        callback = "";
    
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Specify HUH</title>
    </head>
    <body>
        <jsp:include page="banner.jsp"/>
        
    <h3>"<%=appDesc%>" is asking for authority to import data into Specify.</h3>
    
    Enter your Specify login credentials:
    <form name="authZForm" action="authorize" method="POST">
        <input type="text" name="userId" value="" size="20" /><br>
        <input type="password" name="password" value="" size="20" /><br>
        <input type="hidden" name="oauth_token" value="<%= token %>"/>
        <input type="hidden" name="oauth_callback" value="<%= callback %>"/>
        <input type="submit" name="Authorize" value="Authorize"/>
    </form>
    
    </body>
</html>
