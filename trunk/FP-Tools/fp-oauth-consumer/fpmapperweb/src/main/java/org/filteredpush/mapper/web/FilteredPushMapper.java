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
package org.filteredpush.mapper.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.ParameterStyle;
import net.oauth.example.consumer.webapp.CookieConsumer;
import net.oauth.server.HttpRequestMessage;

/**
 * This class was modeled after net.oauth.example.consumer.webapp.SampleProviderConsumer.
 * 
 * @author mkelly
 * 
 * $Id:$
 * 
 * Consumer for Sample OAuth Provider
 * 
 * @author Praveen Alavilli
 */
public class FilteredPushMapper extends HttpServlet {

	private static final String NAME = "filteredpush";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		OAuthConsumer consumer = null;
		
		try {
			consumer = CookieConsumer.getConsumer(NAME, getServletContext());
			
			OAuthAccessor accessor =
					CookieConsumer.getAccessor(request, response, consumer);
			
			Collection<OAuth.Parameter> parameters =
					HttpRequestMessage.getParameters(request);

			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
						
			out.println(invoke(accessor, parameters));
		}
		catch (Exception e) {
			CookieConsumer.handleException(e, request, response, consumer);
		}
	}

	private String invoke(OAuthAccessor accessor, Collection<? extends Map.Entry> parameters)
			throws OAuthException, IOException, URISyntaxException {

		String action = "hello";
		String fragment = "";

		Map<String, String> map = OAuth.newMap(parameters);
		
		if (map.containsKey("get")) {
			String type = "";
			String id = "";
			if (map.containsKey("type")) {
				type = map.get("type");
			}
			if (map.containsKey("id")) {
				id = map.get("id");
			}
			action = "get";
			fragment = "type=" + type + "&id=" + id; // TODO: re-do this the right way
		}
		else if (map.containsKey("find")) {
			String type = "";
			String object = "";
			if (map.containsKey("type")) {
				type = map.get("type");
			}
			if (map.containsKey("object")) {
				object = map.get("object");
			}
			action = "find";
			fragment = "type=" + type + "&object=" + object;
		}
		else if (map.containsKey("add")) {
			String type = "";
			String object = "";
			if (map.containsKey("type")) {
				type = map.get("type");
			}
			if (map.containsKey("object")) {
				object = map.get("object");
			}
			action = "add";
			fragment = "type=" + type + "&object=" + object;
		}
		else if (map.containsKey("update")) {
			String type = "";
			String object = "";
			if (map.containsKey("type")) {
				type = map.get("type");
			}
			if (map.containsKey("object")) {
				object = map.get("object");
			}
			action = "update";
			fragment = "type=" + type + "&object=" + object;
		}
		else if (map.containsKey("remove")) {
			action = "remove";
			fragment = "type=collectionobject&id=234481";
		}

		URI importURL = new URI("http", "localhost:8080", "/specifyweb/" + action, fragment, null);
		
		OAuthMessage request = accessor.newRequestMessage("GET", importURL.toASCIIString(), parameters);
		
		OAuthMessage response = CookieConsumer.CLIENT.invoke(request,
				ParameterStyle.AUTHORIZATION_HEADER);
		
		String responseBody = response.readBodyAsString();
		
		return responseBody;
	}

	private static final long serialVersionUID = 1L;

}
