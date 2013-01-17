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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.example.provider.core.SampleOAuthProvider;

/**
 * 
 * @author mkelly
 * 
 * $Id:$
 */
@SuppressWarnings("serial")
public class RemoveServlet extends HelloServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		try {
			validate(request);
            
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
    		
    		String type = null;
    		String object = null;
    		
    		for (Object e : request.getParameterMap().entrySet()) {
    		    
                @SuppressWarnings("unchecked")
                Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;
                String name = entry.getKey();
                if (name.equalsIgnoreCase("object")) {
                	for (String value : entry.getValue()) {
                		object = value;
                		break;
                	}
                }
                else if (name.equalsIgnoreCase("type")) {
                	for (String value : entry.getValue()) {
                		type = value;
                		break;
                	}
                }
            }
    		
    		if (object != null && type != null) {
    			out.print("true");  // return "true" on success
    		}
    		else {
    			out.print("false"); // return "false" on failure
    		}
            out.close();
            
        }
		catch (Exception e) {
            SampleOAuthProvider.handleException(e, request, response, false);
		}
	}
	
}
