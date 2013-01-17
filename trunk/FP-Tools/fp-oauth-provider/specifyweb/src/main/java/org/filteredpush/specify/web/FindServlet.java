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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuthProblemException;
import net.oauth.example.provider.core.SampleOAuthProvider;

import org.filteredpush.mapper.driver.DataSourceException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This servlet expects two parameters, a type name, and a json representation of a hash map.
 * Together these things represent a domain object, where the values in the hash map may be
 * for fields directly on the corresponding persistable object, or may be values for joined
 * columns.  A possibly empty list of matching identifiers will be returned.
 * 
 * Results may be limited and paged by including values for parameters "start" and "max".  By
 * default, at most 100 results will be returned.
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
public class FindServlet extends HelloServlet {

    private static final int MAX_RESULTS = 100;
    
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		try {
			String userId = validate(request);
            
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
    		
    		String type = null;
    		String object = null;
    		String start = null;
    		String max = null;
    		
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
                else if (name.equalsIgnoreCase("start")) {
                    for (String value : entry.getValue()) {
                        start = value;
                        break;
                    }
                }
                else if (name.equalsIgnoreCase("max")) {
                    for (String value : entry.getValue()) {
                        max = value;
                        break;
                    }
                }
            }
    		
    		if (object != null && type != null) {
    		    int i = 1;
    		    int n = MAX_RESULTS;
    		    
    		    if (start != null) {
    		        try {
    		            i = Integer.parseInt(start);
    		            if (i < 1) i = 1;
    		        }
    		        catch (NumberFormatException e) {
    		            //
    		        }
    		    }
    		    if (max != null) {
    		        try {
                        n = Integer.parseInt(start);
                        if (n < 1) n = 1;
                        if (n > MAX_RESULTS) n = MAX_RESULTS;
                    }
                    catch (NumberFormatException e) {
                        //
                    }
    		    }
    			String result = find(type, object, userId, i, n);
    			out.println(result);
    		}
            out.close();
            
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
	
	private String find(String type, String object, String userId, int start, int max) throws DataSourceException {
		
		Gson gson = new Gson();
		
		Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();
		Map<String, String> map = gson.fromJson(object, mapType);
		
		List<Integer> ids = new ArrayList<Integer>();

		try {
			getDriver().login(userId, null); // TODO: put collection id/code here
			ids = getDriver().find(type, map, start, max);
		}
		catch (Exception e) {
			throw new DataSourceException(e);
		}
		finally {
			getDriver().logout(userId);
		}
		
		String result = gson.toJson(ids);
		
		return result;
	}
}
