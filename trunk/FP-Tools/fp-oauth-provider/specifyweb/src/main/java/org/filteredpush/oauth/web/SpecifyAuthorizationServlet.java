/* Copyright © 2011 President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.filteredpush.oauth.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.filteredpush.specify.auth.SpecifyAuthenticator;


import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.example.provider.core.SampleOAuthProvider;
import net.oauth.server.OAuthServlet;

/**
 * This class was adapted from net.oauth.example.provider.servlets.AuthorizationServlet.
 * It authenticates a Specify userid/password pair with a Specify database.  I have
 * retained the original author's comments.
 * 
 * @author mkelly
 * 
 * $Id$
 * 
 * Authorization request handler.
 *
 * @author Praveen Alavilli
 */
public class SpecifyAuthorizationServlet extends HttpServlet {
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try{
        	SpecifyAuthenticator.loadDbProperties();
        }
        catch(IOException e) {
            throw new ServletException(e.getMessage());
        }
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        try{
            OAuthMessage requestMessage = OAuthServlet.getMessage(request, null);
            
            OAuthAccessor accessor = SampleOAuthProvider.getAccessor(requestMessage);
           
            if (Boolean.TRUE.equals(accessor.getProperty("authorized"))) {
                // already authorized send the user back
                returnToConsumer(request, response, accessor);
            } else {
                sendToAuthorizePage(request, response, accessor);
            }
        
        } catch (Exception e){
            SampleOAuthProvider.handleException(e, request, response, true);
        }
        
        
        
    }
    
    @Override 
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException{
        
        try{
            OAuthMessage requestMessage = OAuthServlet.getMessage(request, null);
            
            OAuthAccessor accessor = SampleOAuthProvider.getAccessor(requestMessage);
            
            String userId = request.getParameter("userId");
            if(userId == null){
                sendToAuthorizePage(request, response, accessor);
            }
            String password = request.getParameter("password");
            authenticate(userId, password);

            // set userId in accessor and mark it as authorized
            SampleOAuthProvider.markAsAuthorized(accessor, userId);
            
            returnToConsumer(request, response, accessor);
            
        } catch (Exception e){
            SampleOAuthProvider.handleException(e, request, response, true);
        }
    }
    
    /**
     * Authenticate the user to Specify, or throw an OAuthProblemException
     * @throws OAuthProblemException 
     */
    private void authenticate(String userId, String password) throws OAuthProblemException {

    	boolean authenticated = false;
    	try {
			authenticated = SpecifyAuthenticator.authenticate(userId, password);
		}
    	catch (LoginException e) {
			OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.OAUTH_PROBLEM_ADVICE); // TODO: what should this be?
			throw problem;
    	}

    	if (! authenticated) {
    		OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.USER_REFUSED);
    		throw problem;
    	}
    }

    private void sendToAuthorizePage(HttpServletRequest request, 
            HttpServletResponse response, OAuthAccessor accessor)
    throws IOException, ServletException{
        String callback = request.getParameter("oauth_callback");
        if(callback == null || callback.length() <=0) {
            callback = "none";
        }
        String consumer_description = (String)accessor.consumer.getProperty("description");
        request.setAttribute("CONS_DESC", consumer_description);
        request.setAttribute("CALLBACK", callback);
        request.setAttribute("TOKEN", accessor.requestToken);
        request.getRequestDispatcher //
                    ("/authorize.jsp").forward(request,
                        response);
        
    }
    
    private void returnToConsumer(HttpServletRequest request, 
            HttpServletResponse response, OAuthAccessor accessor)
    throws IOException, ServletException{
        // send the user back to site's callBackUrl
        String callback = request.getParameter("oauth_callback");
        if("none".equals(callback) 
            && accessor.consumer.callbackURL != null 
                && accessor.consumer.callbackURL.length() > 0){
            // first check if we have something in our properties file
            callback = accessor.consumer.callbackURL;
        }
        
        if( "none".equals(callback) ) {
            // no call back it must be a client
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println("You have successfully authorized '" 
                    + accessor.consumer.getProperty("description") 
                    + "'. Please close this browser window and click continue"
                    + " in the client.");
            out.close();
        } else {
            // if callback is not passed in, use the callback from config
            if(callback == null || callback.length() <=0 )
                callback = accessor.consumer.callbackURL;
            String token = accessor.requestToken;
            if (token != null) {
                callback = OAuth.addParameters(callback, "oauth_token", token);
            }

            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", callback);
        }
    }

    private static final long serialVersionUID = 1L;

}
