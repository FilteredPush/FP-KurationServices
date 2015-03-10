package fp.services;

import fp.util.SciNameCacheValue;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.net.MalformedURLException;
import java.net.URL;


public class COLService extends SciNameServiceParent {

    @Override
    public boolean nameSearchAgainstServices(String name, String author)  {


        String key = getKey(name, author);
        if(useCache && sciNameCache.containsKey(key)){
            SciNameCacheValue hitValue = (SciNameCacheValue) sciNameCache.get(key);
            comment += hitValue.getComment();
            curationStatus = hitValue.getStatus();
            serviceName = hitValue.getSource();
            validatedAuthor = hitValue.getAuthor();
            validatedScientificName = hitValue.getTaxon();
            //System.out.println("count  = " + count++);
            //System.out.println(key);
            return true;
        }

        serviceName = serviceName + " | Catalog of Life";
        Document document = null;
        URL url;

        /*
        Reader stream = null;
        try {
            url = new URL(Url + "?name=" + name.replace(" ", "%20") + "&format=xml&response=full");
            //System.out.println("url.toString() = " + url.toString());
            URLConnection connection = url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                sb.append(br.readLine());
            }
            stream = new StringReader(sb.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            comment = comment + " | Fail to access Catalog of Life service, trying failover to Checklistbank Back bone";
        }
        */

        SAXReader reader = new SAXReader();
        try {
            url = new URL(Url + "?name=" + name.replace(" ", "%20") + "&format=xml&response=full");
            //System.out.println("url = " + url.toString());
            document = reader.read(url);
        } catch (DocumentException e) {
            comment = comment + " | Failed to get information by parsing the response from Catalog of Life service for: "+e.getMessage();
            addToCache();
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //no homonyms.synomyns handling for now
        if (Integer.valueOf(document.getRootElement().attribute(3).getText()) < 1) {
            comment = comment + " | Cannot find matches in Catalog of Life service";
            addToCache();
            return false;
        }else{

            String authorQuery = "";
            try {
                if (document.selectSingleNode("/results/result/name_status").getText().contains("accepted name")){

                    validatedScientificName = document.selectSingleNode("/results/result/name").getText();
                    authorQuery = "/results/result/author";
                }else if(document.selectSingleNode("/results/result/name_status").getText().equals("synonym")){
                    validatedScientificName = document.selectSingleNode("/results/result/name").getText();
                    authorQuery = "/results/result/accepted_name/author";
                    comment = comment + " | found and solved synonym";
                }else if(document.selectSingleNode("/results/result/name_status").getText().equals("ambiguous synonym")){
                    comment = comment + " | found but could not solve synonym";
                    addToCache();
                    return false;
                }else {
                    System.out.println("others document = " + document.toString());
                }

                   // comment = comment + " | The original scientificName is not at species level";

            } catch (Exception e) {
                System.out.println("---");
                e.printStackTrace();
                System.out.println("document = " + document.toString());
                System.out.println("name = " + name);
                System.out.println("===");
            }
            try{
                validatedAuthor =  document.selectSingleNode(authorQuery).getText();
            }catch(Exception e){
                comment = comment + " | No author found in Catalog of Life service";
                addToCache();
                return false;
            }
        }

        addToCache();
        return true;

        //}
	}



	private final static String Url = "http://www.catalogueoflife.org/col/webservice";

}
