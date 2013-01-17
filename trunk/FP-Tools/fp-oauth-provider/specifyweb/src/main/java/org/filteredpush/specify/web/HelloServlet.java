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
package org.filteredpush.specify.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.filteredpush.specify.SpecifyDriver;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.example.provider.core.SampleOAuthProvider;
import net.oauth.server.OAuthServlet;

/**
 * 
 * @author mkelly
 * 
 * $Id$
 */
@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {

    private SpecifyDriver specifyDriver; // TODO: make this type an interface again, DataSourceDriver
    
    @Override
    public void init() throws ServletException {
        this.specifyDriver = new SpecifyDriver(); // TODO
    }
    
	@SuppressWarnings("unchecked")
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		try {
			validate(request);
            
			response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println("Hi, I'm the Specify \"Hello Servlet.\"  I was called like this:\n");
    		out.println("Parameters:\n");

    		for (Object e : request.getParameterMap().entrySet()) {
                Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;
                String name = entry.getKey();
                for (String value : entry.getValue()) {
                	out.println(name + ": " + value);
                }
            }
    		out.println("Authorization Headers:\n");
    		for (Enumeration<String> headers = request.getHeaders("Authorization"); headers != null
                    && headers.hasMoreElements();) {
                String header = headers.nextElement();
                out.println(header);
    		}
            out.close();
            
        }
		catch (Exception e) {
            SampleOAuthProvider.handleException(e, request, response, false);
		}
	}
	
	/**
	 * Do OAuth validation; return the Specify username.
	 */
	protected String validate(HttpServletRequest request) throws IOException, OAuthException, URISyntaxException {
		OAuthMessage requestMessage = OAuthServlet.getMessage(request, null);
        OAuthAccessor accessor = SampleOAuthProvider.getAccessor(requestMessage);
        SampleOAuthProvider.VALIDATOR.validateMessage(requestMessage, accessor);
        
		String userId = (String) accessor.getProperty("user");
        
        return userId;
	}

	protected SpecifyDriver getDriver() {
	    return this.specifyDriver;
	}
}
