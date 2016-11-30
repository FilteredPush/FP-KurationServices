/** 
 * GNISupportingService.java 
 * 
 * Copyright 2012 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.filteredpush.kuration.services.sciname;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.filteredpush.kuration.util.CurationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Services provided by GNI, the global names index, an index of text strings of 
 * names of organisms.
 * 
 * Is not a child of SciNameServiceParent as GNI isn't a name validation
 * service.
 * 
 * @see SciNameServiceParent
 * 
 * @author Lei Dou
 * @author Paul J. Morris
 *
 */
public class GNISupportingService {
	
	private static final Log logger = LogFactory.getLog(GNISupportingService.class);

	
	   /**
	    * Parse name into scientific name and authorship using result returned from 
	    * GNI's name parsing web service.
	    * 
	    * @param name Scientific Name string including authorship.
	    * @return String vector containing the canonical scientificName as the first element and the
	    * scientificNameAuthorship as the second element.
	    * @throws CurationException on encountering problems with the web service call.
	    */
	   public static Vector<String> parseName(String name) throws CurationException {
	       Vector<String> result = new Vector<String>();
           org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
           httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
           httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

           List<NameValuePair> parameters = new ArrayList<NameValuePair>();
           parameters.add(new BasicNameValuePair("names", name));

	       try {
               HttpPost httpPost = new HttpPost(GNINameParsingURL);
               HttpGet httpGet = new HttpGet(GNINameParsingURL + "?names=" + URLEncoder.encode(name, "UTF-8"));
               httpPost.setEntity(new UrlEncodedFormEntity(parameters));
               HttpResponse resp = httpclient.execute(httpGet);
	           long statusCode = resp.getStatusLine().getStatusCode();
	           if(statusCode!=200){
	        	   logger.error(statusCode);
	        	   logger.error(resp.getStatusLine().getReasonPhrase());
	               throw new CurationException("Failed to parse name of "+ name+" by accessing GNI name parser service at: " +GNINameParsingURL);
	           }           
               InputStream reponseStream = resp.getEntity().getContent();
	           
	           //parse the output
	           SAXReader reader = new SAXReader();
	           Document document = reader.read(reponseStream);
	           Node scientificNameNode = document.selectSingleNode("//node_key[fn:normalize-space(.)='canonical:']/following-sibling::node_value");
	           Node scientificNameAuthorshipNode = document.selectSingleNode("//node_key[.='details']/following-sibling::node//node_key[fn:normalize-space(.)='authorship:']/following-sibling::node_value");
	           
	           if(scientificNameNode == null || scientificNameAuthorshipNode==null){
	               // can't find the scientific name and the authorship by parsing the name
	        	   // leave result as an empty vector. 
	           } else { 
	               result.add(scientificNameNode.getText());
	               result.add(scientificNameAuthorshipNode.getText());
	           } 
	       } catch (IOException e) {
	           throw new CurationException("Failed to get the IPNI source ID by accessing GNI service at: "+GNIResourceURL+" for: "+e.getMessage());
	       } catch (DocumentException e) {
	           throw new CurationException("Failed to get the IPNI source ID by parsing the response from GNI service at: "+GNIResourceURL+" for: "+e.getMessage());
	       }       
	       return result;
	   }
	   
	   /**
	    * Given a scientific name and the ID of a data source to GNI, return a name string that is in
	    * the same lexical group and is also provided to GNI by the data source.  This method allows the
	    * lexical groups of GNI to be used as a fuzzy match against names in authoritative data sources. 
	    * 
	    * @param scientificName the name for which to find a lexical group.
	    * @param sourceID the GNI ID of the data source which must provide the returned name (or a pipe
	    *    delimited list of GNI IDs for data sources).  
	    * @return the scientific name string in the same lexical group as the scientificName provided
	    * which is also a name provided to GNI by the sourceID.
	    * @throws CurationException
	    */
	   public static String searchLexicalGroupInGNI(String scientificName, String sourceID) throws CurationException{
           org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
           httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
           httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

           List<NameValuePair> parameters = new ArrayList<NameValuePair>();
           parameters.add(new BasicNameValuePair("names", scientificName));
           parameters.add(new BasicNameValuePair("best_match_only", "true"));
           parameters.add(new BasicNameValuePair("data_source_ids", sourceID));
	       
	       logger.debug("Searching GNI for " + scientificName + " with limit of best_match_only and data_source_ids = " + sourceID);
	       
	       try {
               HttpPost httpPost = new HttpPost(GNINameResolverURL);
               httpPost.setEntity(new UrlEncodedFormEntity(parameters));
               logger.debug(httpPost.getURI());
               logger.debug(scientificName);
               logger.debug(sourceID);
               HttpResponse resp = httpclient.execute(httpPost);
	           long statusCode = resp.getStatusLine().getStatusCode();
	           if(statusCode!=200){
	        	   logger.error(statusCode);
	        	   logger.error(resp.getStatusLine().getReasonPhrase());
	               throw new CurationException("Failed to search for the ScientificName in the same lexical group as "+ scientificName+" and from source "+sourceID+ " by accessing GNI service at: " +GNINameResolverURL);
	           }           
               InputStream reponseStream = resp.getEntity().getContent();
	           
	           //parse the output
	           SAXReader reader = new SAXReader();
	           Document document = reader.read(reponseStream);
	           // logger.debug(document.asXML());
	           // Node nameStringNode = document.selectSingleNode("/records/record/data_source_id[.="+sourceID+"]/following-sibling::name_string");
	           // Node nameStringNode = document.selectSingleNode("/hash/data/datum/results/result/data_source_id[.="+sourceID+"]/following-sibling::name-string");
	           Node nameStringNode = document.selectSingleNode("/hash/data/datum/results/result/name-string");
	           if(nameStringNode == null){
	        	   logger.debug("xml node not found");
	               //can't find any name string which is in the same lexical group as the queried one and comes from IPNI
	               return null;
	           }           
	           System.out.println("GNI match: " + nameStringNode.getText());
	           return nameStringNode.getText();            
	       } catch (IOException e) {
	           throw new CurationException("Failed to get the IPNI source ID by accessing GNI service at: "+GNINameResolverURL+" for: "+e.getMessage());
	       } catch (DocumentException e) {
	           throw new CurationException("Failed to get the IPNI source ID by parsing the response from GNI service at: "+GNINameResolverURL+" for: "+e.getMessage());
	       }           
	   }		   
	   
	   
	   /**
	    * Given a scientific name return a name string that is the best match found in the global
	    * names resolver.  This method allows name matches found in GNI to be used as a fuzzy match 
	    * against names in authoritative data sources. 
	    * 
	    * @param scientificName the name for which to find a lexical group.
	    * @param sourceID the GNI ID of the data source which must provide the returned name.  
	    * @return the scientific name string in the same lexical group as the scientificName provided
	    * which is also a name provided to GNI by the sourceID.
	    * @throws CurationException
	    */
	   public static String searchAnyInGNI(String scientificName) throws CurationException{
           org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
           httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
           httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

           List<NameValuePair> parameters = new ArrayList<NameValuePair>();
           parameters.add(new BasicNameValuePair("names", scientificName));
           parameters.add(new BasicNameValuePair("best_match_only", "true"));
	       
	       //System.out.println("Searching GNI for " + scientificName + " with limit of dataSource = " + sourceID);
	       
	       try {
               HttpPost httpPost = new HttpPost(GNINameResolverURL);
               httpPost.setEntity(new UrlEncodedFormEntity(parameters));
               logger.debug(httpPost.getURI());
               logger.debug(scientificName);
               HttpResponse resp = httpclient.execute(httpPost);
	           long statusCode = resp.getStatusLine().getStatusCode();
	           if(statusCode!=200){
	        	   logger.error(statusCode);
	        	   logger.error(resp.getStatusLine().getReasonPhrase());
	               throw new CurationException("Failed to search for the ScientificName in the same lexical group as "+ scientificName+" and from any source by accessing GNI service at: " +GNINameResolverURL);
	           }           
               InputStream reponseStream = resp.getEntity().getContent();
	           
	           //parse the output
	           SAXReader reader = new SAXReader();
	           Document document = reader.read(reponseStream);
	           logger.debug(document.asXML());
	           // Node nameStringNode = document.selectSingleNode("/records/record/data_source_id[.="+sourceID+"]/following-sibling::name_string");
	           // Node nameStringNode = document.selectSingleNode("/hash/data/datum/results/result/data_source_id[.="+sourceID+"]/following-sibling::name-string");
	           Node nameStringNode = document.selectSingleNode("/hash/data/datum/results/result/name-string");
	           if(nameStringNode == null){
	        	   logger.debug("xml node not found");
	               //can't find any name string which is in the same lexical group as the queried one and comes from IPNI
	               return null;
	           }           
	           System.out.println("GNI match: " + nameStringNode.getText());
	           return nameStringNode.getText();            
	       } catch (IOException e) {
	           throw new CurationException("Failed to get the IPNI source ID by accessing GNI service at: "+GNINameResolverURL+" for: "+e.getMessage());
	       } catch (DocumentException e) {
	           throw new CurationException("Failed to get the IPNI source ID by parsing the response from GNI service at: "+GNINameResolverURL+" for: "+e.getMessage());
	       }           
	   }	   
	   
	   /**
	    * Fallback to GNI to look for matching name strings.  Finds the best matching name from GNI in 
	    * any data source. 
	    * 
	    * @param scientificName the name to check.
	    * @return a string vector containing the scientificName and the scientificNameAuthorship.
	    * @throws CurationException
	    */
	   public static Vector<String> resolveDataSourcesNameInLexicalGroupFromGNIAny(String scientificName) throws CurationException{
		   

		   Vector<String> result = null;

			//search name in GNI
			String nameFromIPNIInLexicalGroup = searchAnyInGNI(scientificName);
			if(nameFromIPNIInLexicalGroup != null){
				//parse name into scientific name and author by using the name parsing service in GNI
				result = parseName(nameFromIPNIInLexicalGroup);
			} 
	
		   
		   return result;
	   }	   
	   
	   /**
	    * Fallback to GNI to look for matching name strings.  Finds the lexical group matching the provided 
	    * scientificName, then in that lexical group returns a name which has a data source in the
	    * provided list.  Data sources are checked in sequence, and the first match of a data source is returned.  
	    * 
	    * @param scientificName the name to check.
	    * @return a string vector containing the scientificName and the scientificNameAuthorship.
	    * @throws CurationException
	    */
	   public static Vector<String> resolveDataSourcesNameInLexicalGroupFromGNI(String scientificName) throws CurationException{
		   
		   ArrayList<String> sourcesToCheck = new ArrayList<String>();
		   // List of targets, returns first matching target.
		   sourcesToCheck.add("IPNI");
		   sourcesToCheck.add("Index Fungorum");
		   sourcesToCheck.add("WoRMS");
		   sourcesToCheck.add("AmphibiaWeb");
		   sourcesToCheck.add("AntWeb");
		   sourcesToCheck.add("ZooKeys");
		   sourcesToCheck.add("ZooBank");
		   sourcesToCheck.add("Diatoms");
		   sourcesToCheck.add("Catalog of Fishes");
		   sourcesToCheck.add("Mantodea Species File");
		   sourcesToCheck.add("Orthoptera Species File");
		   sourcesToCheck.add("The Mammal Species of The World");
		   sourcesToCheck.add("The International Plant Names Index");
		   sourcesToCheck.add("World Register of Marine Species");
		   sourcesToCheck.add("The International Plant Names Index");
		   sourcesToCheck.add("The Paleobiology Database");

		   Vector<String> result = null;
		   
		   StringBuilder dataSourceIds = new StringBuilder(); 
		   Iterator<String> i = sourcesToCheck.iterator();
		   while (i.hasNext()) { 
			   String source = i.next();
			   String dataSourceId = null;
			   dataSourceId = GNISupportingService.getGNIDataSourceID(source);

			   //If GNI doesn't support the data source of IPNI, then do nothing.
			   if(dataSourceId != null){
				   if (dataSourceIds.length()>0) { dataSourceIds.append("|"); } 
				   dataSourceIds.append(dataSourceId);
			   }
		   }
		   
		   //search name in GNI
		   String nameFromIPNIInLexicalGroup = searchLexicalGroupInGNI(scientificName,dataSourceIds.toString());
		   if(nameFromIPNIInLexicalGroup != null){
			   //parse name into scientific name and author by using the name parsing service in GNI
				result = parseName(nameFromIPNIInLexicalGroup);
		   } 
		   
		   return result;
	   }   
	   
	   /**
	    * Find the ID of a GNI data source from the data source title (e.g. IPNI, WoRMS, Index Fungorum, 
	    * AmphibiaWeb, AntWeb, ZooKeys, Diatoms, Catalog of Fishes
	    * 
	    * @param title
	    * @return
	    */
	   public static String getGNIDataSourceID(String title)  throws CurationException  {
           org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
           httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
           httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

	       try {
               HttpGet method = new HttpGet(GNIResourceURL);
               HttpResponse resp = httpclient.execute(method);
               long statusCode = resp.getStatusLine().getStatusCode();
	           if(statusCode!=200){
	        	   System.out.println(GNIResourceURL + "is returning status " + Long.toString(statusCode));
	               throw new CurationException("Failed to get the " + title + " source ID by accessing GNI service at: "+GNIResourceURL);
	           }           
               InputStream reponseStream = resp.getEntity().getContent();
	           
	           //parse the output
	           SAXReader reader = new SAXReader();
	           Document document = reader.read(reponseStream);
	           //  System.out.println(document.asXML());
	           Node idNode = document.selectSingleNode("/data_sources/data_source/title[.='"+title+"']/preceding-sibling::id");
	           if(idNode == null){
	               //can't find data source by title
	               return null;
	           }           
	           return idNode.getText();            
	       } catch (IOException e) {
	    	   System.out.println("GNIsupportingService [e1]: " + e.getMessage());
	           throw new CurationException("Failed to get the "+ title +" source ID by accessing GNI service at: "+GNIResourceURL+" for: "+e.getMessage());
	       } catch (DocumentException e) {
	    	   System.out.println("GNIsupportingService [e2]: " + e.getMessage());
	           throw new CurationException("Failed to get the "+ title +" source ID by parsing the response from GNI service at: "+GNIResourceURL+" for: "+e.getMessage());
	       } 	   
	   }
	   
	   /**
	    * Wrapper for getGNIDataSourceID("IPNI"). 
	    * @return GNI dataSourceID for data source with the title IPNI.
	    * @throws CurationException
	    */
	   public static String getIPNISourceId() throws CurationException {
		   return getGNIDataSourceID("IPNI");         
	   }	   
	   
	   private final static String GNIResourceURL = "http://gni.globalnames.org/data_sources.xml";
	   //private final static String GNINameResolverURL = "http://gni.globalnames.org/name_resolver.xml";
	   private final static String GNINameResolverURL = "http://resolver.globalnames.org/name_resolvers.xml";
	   private final static String GNINameParsingURL = "http://gni.globalnames.org/parsers.xml";	   

	   
	   
}
