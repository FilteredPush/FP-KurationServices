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
import java.util.HashMap;
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
 * Together these things represent a persistable domain object, where the key-value pairs in
 * the hash map are the fields of the object.  The fields may be primitive values or foreign
 * keys.
 * 
 * If the parameter object contains a value for its primary key field, then the corresponding
 * persisted object will be updated with the values present in the hash map.  If no value is
 * present for the primary key field, a new row will be inserted for the object.
 * 
 * Since this servlet implements the provider end of OAuth, the incoming request is expected
 * to have been set up with the necessary authorization information.  The request will be
 * validated, and if it passes muster, the appropriate userId will be available for recording
 * audit info for the new or updated row.
 * 
 * @author mkelly
 * 
 * $Id$
 */
@SuppressWarnings("serial")
public class AddServlet extends HelloServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		try {
			String userId = validate(request);
            
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
    			String result = save(type, object, userId);
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

	private String save(String type, String object, String userId) throws DataSourceException {

		Gson gson = new Gson();

		Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();
		Map<String, String> map = gson.fromJson(object, mapType);

		Integer id = null;

		try {
			getDriver().login(userId, null);// TODO: put collection id/code here
			id = getDriver().save(type, map);
		}
		catch (Exception e) {
			throw new DataSourceException(e);
		}
		finally {
		    getDriver().logout(userId);
		}

		String result = id != null ? id.toString() : ""; // TODO: return values?

		return result;
	}
}
