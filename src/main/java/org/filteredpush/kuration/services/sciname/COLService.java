/** 
 * CollectingEventOutlierIdentificationService.java 
 * 
 * Copyright 2015 President and Fellows of Harvard College
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

import edu.harvard.mcz.nametools.ICZNAuthorNameComparator;
import edu.harvard.mcz.nametools.NameUsage;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.filteredpush.kuration.util.SciNameCacheValue;

import java.net.MalformedURLException;
import java.net.URL;


public class COLService extends SciNameServiceParent {
	
	private final static String Url = "http://www.catalogueoflife.org/col/webservice";

	public COLService() { 
		init();
	}
	
	protected void init() {
		validatedNameUsage = new NameUsage("Catalog Of Life",new ICZNAuthorNameComparator(.75d, .5d));
	}
	
	@Override
	protected String getServiceImplementationName() {
		return "Catalog Of Life";
	}	
	
    @Override
    public boolean nameSearchAgainstServices(NameUsage toCheck)  {
    	boolean result = false;
    	String name = toCheck.getOriginalScientificName();
    	String author = toCheck.getOriginalAuthorship();
    	
        String key = getKey(name, author);
        if(useCache && sciNameCache.containsKey(key)){
            SciNameCacheValue hitValue = (SciNameCacheValue) sciNameCache.get(key);
            addToComment(hitValue.getComment());
            curationStatus = hitValue.getStatus();
            addToServiceName(hitValue.getSource());
            validatedNameUsage.setAuthorship(hitValue.getAuthor());
            validatedNameUsage.setScientificName(hitValue.getTaxon());
            //System.out.println("count  = " + count++);
            //System.out.println(key);
            return hitValue.getHasResult();
        }

        addToServiceName("Catalog of Life");
        Document document = null;
        URL url;

        /*
         * Some illustrative example calls: 
         * http://www.catalogueoflife.org/col/webservice?name=Spurilla%20alba&response=full
         * http://www.catalogueoflife.org/col/webservice?name=Hieracium%20albanicum%20subsp.%20albanicum&response=full
         * http://www.catalogueoflife.org/col/webservice?name=Bembidion%20cruciatum%20albanicum&response=full
         * http://www.catalogueoflife.org/col/webservice?name=Oenanthe&response=full
         * 
         */
        
        SAXReader reader = new SAXReader();
        try {
            url = new URL(Url + "?name=" + name.replace(" ", "%20") + "&format=xml&response=full");
            //System.out.println("url = " + url.toString());
            document = reader.read(url);
        } catch (DocumentException e) {
        	addToComment("Failed to get information by parsing the response from Catalog of Life service for: "+e.getMessage());
            addToCache(false);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (document!=null) { 
        //no homonyms.synomyns handling for now
        int matches = Integer.valueOf(document.getRootElement().attribute(3).getText());
        if (matches < 1) {
        	addToComment("Cannot find matches in Catalog of Life service");
        } else if (matches > 1) {
        	addToComment("More than one match in Catalog of Life service, may be homonym or hemihomonym.");
        } else {
        	String rank = document.selectSingleNode("/results/result/rank").getText();
        	if (rank.equals("Species") || rank.equals("Infraspecies")) { 
        		String authorQuery = "";
        		try {
        			if (
        				  document.selectSingleNode("/results/result/name_status").getText().contains("accepted name") ||
        				  document.selectSingleNode("/results/result/name_status").getText().contains("provisionally accepted name")
        			   )
        			{
        				// accepted name
        				validatedNameUsage.setScientificName(document.selectSingleNode("/results/result/name").getText());
        				validatedNameUsage.setAcceptedName(document.selectSingleNode("/results/result/name").getText());
        				validatedNameUsage.setAcceptedAuthorship(document.selectSingleNode("/results/result/author").getText());
        				validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
        				validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
        				authorQuery = "/results/result/author";
        				result = true;
        			} else if(document.selectSingleNode("/results/result/name_status").getText().equals("synonym")){
        				// synonym
        				validatedNameUsage.setScientificName(document.selectSingleNode("/results/result/name").getText());
        				validatedNameUsage.setAcceptedName(document.selectSingleNode("/results/result/accepted_name/name").getText());
        				validatedNameUsage.setAcceptedAuthorship(document.selectSingleNode("/results/result/accepted_name/author").getText());
        				validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
        				validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
        				authorQuery = "/results/result/author";
        				result = true;
        				addToComment("Found and resolved synonym");
        			} else if(document.selectSingleNode("/results/result/name_status").getText().equals("ambiguous synonym")){
        				// TODO: Authorship may be able to provide guidance on name to return for scientificName, won't be able
        				// to return acceptedName.
        				addToComment("Found but could not resolve synonym ");
        			} else {
        				System.out.println("others document = " + document.toString());
        			}
        		} catch (Exception e) {
        			System.out.println("---");
        			e.printStackTrace();
        			System.out.println("document = " + document.toString());
        			System.out.println("name = " + name);
        			System.out.println("===");
        		}
        		try{
        			validatedNameUsage.setAuthorship(document.selectSingleNode(authorQuery).getText());
        			//TODO: if kingdom is plantae or fungi and rank is infraspecies and species=infraspecies, then 
        			//the authorship should be blank, as this is a botanical autonym.
        			result = true;
        		}catch(Exception e){
        			addToComment("No author found in Catalog of Life service");
        			result = false;
        		}
        	} else {
        		addToComment("The original scientificName is a " + rank +", not at species level");
        	}
        }

        }
        addToCache(result);
		if (!result) { 
			addToComment("No match found in Catalog of Life.");
		}
        return result;
	}


}
