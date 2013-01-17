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

import net.oauth.OAuth;
import net.oauth.OAuthProblemException;
import net.oauth.example.provider.core.SampleOAuthProvider;

import org.filteredpush.mapper.driver.DataSourceException;

import com.google.gson.Gson;

/**
 * This servlet expects two parameters, a type name, and an identifier.  A lookup is done
 * to retrieve the object with the given type and identifier, and is returned as a json
 * hash map, where the key-value pairs may represent fields directly on the object, or may
 * represent joined columns.
 * 
 * Since this servlet implements the provider end of OAuth, the incoming request is expected
 * to have been set up with the necessary authorization information.  The request will be
 * validated, and if it passes muster, the appropriate userId will be available for checking
 * read permissions.
 * 
 * @author mkelly
 * 
 * $Id$
 */
@SuppressWarnings("serial")
public class GetServlet extends HelloServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		try {
			String userId = validate(request);
    		
    		String type = null;
    		String id = null;
    		
    		for (Object e : request.getParameterMap().entrySet()) {

    			@SuppressWarnings("unchecked")
				Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;
                
				String name = entry.getKey();
                if (name.equalsIgnoreCase("id")) {
                	for (String value : entry.getValue()) {
                		id = value;
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
    		
    		if (id != null && type != null) {
    			String result = get(id, type, userId);
    			if (result == null) {
    				String complaint = "Record not found";
    				OAuthProblemException problem = new OAuthProblemException(complaint);
    				problem.setParameter(OAuthProblemException.OAUTH_PROBLEM, complaint);
    				problem.setParameter(OAuthProblemException.HTTP_STATUS_CODE, 500);
    				throw problem;
    			}
    			
    			response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
    			out.print(result);
    			out.close();
    		}
            
        }
		catch (OAuthProblemException e) {
			SampleOAuthProvider.handleException(e, request, response, false);
		}
		catch (Exception e) {

			String complaint = e.getMessage();
			OAuthProblemException problem = new OAuthProblemException(complaint);
			problem.setParameter(OAuthProblemException.OAUTH_PROBLEM, complaint);
			problem.setParameter(OAuthProblemException.HTTP_STATUS_CODE, 500);
			
            SampleOAuthProvider.handleException(problem, request, response, false);
		}
	}
	
	private String get(String id, String type, String userId) throws DataSourceException {

		Map<String, String> dwcKeyValuePairs = null;
		try {
			getDriver().login(userId, null);// TODO: put collection id/code here
			dwcKeyValuePairs = getDriver().get(type, id);
		}
		catch (Exception e) {
			throw new DataSourceException(e);
		}
		finally {
			getDriver().logout(userId);
		}
		
		if (dwcKeyValuePairs == null) return null;

		// serialize as json
		Gson gson = new Gson();
		String json = gson.toJson(dwcKeyValuePairs);
		
		return json;
	}
}
