package fp.services;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.net.MalformedURLException;
import java.net.URL;


public class COLService extends SciNameServiceParent {

    public boolean nameSearchAgainstServices(String name)  {
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
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //no homonyms.synomyns handling for now
        if (Integer.valueOf(document.getRootElement().attribute(3).getText()) < 1) {
            comment = comment + " | Cannot find matches in Catalog of Life service";
            return false;
        }else{
            validatedScientificName = document.selectSingleNode("/results/result/name").getText();
            if (!document.selectSingleNode("/results/result/rank").getText().equals("Species")){
                comment = comment + " | The original scientificName is not at species level";
            }
            try{
                validatedAuthor =  document.selectSingleNode("/results/result/author").getText();
            }catch(Exception e){
                comment = comment + " | No author found in Catalog of Life service";
                return false;
            }
        }


        return true;

        //}
	}



	private final static String Url = "http://www.catalogueoflife.org/col/webservice";

}
