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
    	String name = toCheck.getOriginalScientificName();
    	String author = toCheck.getOriginalAuthorship();
    	
        String key = getKey(name, author);
        if(useCache && sciNameCache.containsKey(key)){
            SciNameCacheValue hitValue = (SciNameCacheValue) sciNameCache.get(key);
            addToComment(hitValue.getComment());
            curationStatus = hitValue.getStatus();
            addToServiceName(hitValue.getSource());
            this.validatedNameUsage.setAuthorship(hitValue.getAuthor());
            this.validatedNameUsage.setScientificName(hitValue.getTaxon());
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
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //no homonyms.synomyns handling for now
        int matches = Integer.valueOf(document.getRootElement().attribute(3).getText());
        if (matches < 1) {
        	addToComment("Cannot find matches in Catalog of Life service");
            addToCache(false);
            return false;
        } else if (matches > 1) {
        	addToComment("More than one match in Catalog of Life service, may be homonym or hemihomonym.");
            addToCache(false);
            return false;
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
        				authorQuery = "/results/result/author";
        			} else if(document.selectSingleNode("/results/result/name_status").getText().equals("synonym")){
        				// synonym
        				validatedNameUsage.setScientificName(document.selectSingleNode("/results/result/name").getText());
        				validatedNameUsage.setAcceptedName(document.selectSingleNode("/results/result/accepted_name/name").getText());
        				validatedNameUsage.setAcceptedAuthorship(document.selectSingleNode("/results/accepted_name/author").getText());
        				authorQuery = "/results/result/author";
        				addToComment("Found and resolved synonym");
        			} else if(document.selectSingleNode("/results/result/name_status").getText().equals("ambiguous synonym")){
        				// TODO: Authorship may be able to provide guidance on name to return for scientificName, won't be able
        				// to return acceptedName.
        				addToComment("Found but could not resolve synonym ");
        				addToCache(false);
        				return false;
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
        		}catch(Exception e){
        			addToComment("No author found in Catalog of Life service");
        			addToCache(false);
        			return false;
        		}
        	} else {
        		addToComment("The original scientificName is a " + rank +", not at species level");
        	}
        }

        addToCache(true);
        return true;
	}


}
