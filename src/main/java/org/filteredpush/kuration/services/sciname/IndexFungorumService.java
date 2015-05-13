/** 
 * IndexFungorumService.java 
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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;

import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationStatus;
import org.indexfungorum.cabi.fungusserver.FungusSoapProxy;
import org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult;

import edu.harvard.mcz.nametools.AuthorNameComparator;
import edu.harvard.mcz.nametools.ICNafpAuthorNameComparator;
import edu.harvard.mcz.nametools.NameComparison;
import edu.harvard.mcz.nametools.NameUsage;

/**
 * @author mole
 *
 */
public class IndexFungorumService extends SciNameServiceParent  {
	
	private static final Log logger = LogFactory.getLog(IndexFungorumService.class);

	private FungusSoapProxy ifService;
	protected AuthorNameComparator authorNameComparator;
	
	public IndexFungorumService() throws IOException { 
		init();
		test();
	}
	
	protected void init(){ 
		authorNameComparator = new ICNafpAuthorNameComparator(.70d,.5d);
		validatedNameUsage = new NameUsage("IndexFungorum",authorNameComparator);
	}

	protected void test() throws IOException { 
		ifService = new FungusSoapProxy();
		logger.debug(ifService.getEndpoint());
		URL test = new URL(ifService.getEndpoint());
		URLConnection conn = test.openConnection();
		conn.connect();
	}
	
	@Override
	protected String getServiceImplementationName() {
		return "IndexFungorum";
	}	
	
	@Override
	protected boolean nameSearchAgainstServices(NameUsage toCheck) {
		boolean result = false;
		addToServiceName("IndexFungorum");
		String taxonName = toCheck.getOriginalScientificName();
		try {
			//TODO: Autonyms should not have authors.  Parse name, check if specific and lowest epithet are the same.
			//Handle cases of autonym with author provided (might be author of species name) and not provided (correct).
			NameSearchResponseNameSearchResult searchResult = ifService.nameSearch(taxonName, false, 2);
			if (searchResult!=null) { 
				List<MessageElement> mes = Arrays.asList(searchResult.get_any());
				logger.debug(mes.size());
				Iterator<MessageElement> i = mes.iterator();
				while (i.hasNext()) { 
					MessageElement me = i.next();
					logger.debug(me);
					Iterator<MessageElement> it = me.getChildElements();
					while (it.hasNext()) { 
						MessageElement mei = it.next();
						logger.debug(mei.getChildElement(new QName("NAME_x0020_OF_x0020_FUNGUS")).getValue());
						String name = mei.getChildElement(new QName("NAME_x0020_OF_x0020_FUNGUS")).getValue();
						if (name.equals(taxonName)) {
							String authorship = mei.getChildElement(new QName("AUTHORS")).getValue();
							if (authorship!=null && authorship.equals(toCheck.getOriginalAuthorship())) { 
								String uuid = mei.getChildElement(new QName("UUID")).getValue();
								String recnum = mei.getChildElement(new QName("RECORD_x0020_NUMBER")).getValue();
								String lsid = "urn:lsid:indexfungorum.org:names:" + recnum;
								logger.debug("\"" + taxonName + "\", \"urn:uuid:" + uuid + "\",\"" + lsid +  "\"");
								validatedNameUsage = new NameUsage("IndexFungorum",authorNameComparator, toCheck.getOriginalScientificName(), toCheck.getOriginalAuthorship());
								validatedNameUsage.setScientificName(taxonName);
								validatedNameUsage.setKey(Integer.parseInt(recnum));
								validatedNameUsage.setGuid(lsid);
								validatedNameUsage.setSourceID("urn:uuid:"+ uuid);
								validatedNameUsage.setAuthorship(authorship);
								validatedNameUsage.setMatchDescription(NameComparison.MATCH_EXACT);
								validatedNameUsage.setAuthorshipStringEditDistance(1d);
								validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
								result = true;
								addToComment("Found exact match in IndexFungorum.");
								curationStatus = CurationComment.CORRECT;
							} else { 
								double similarity = AuthorNameComparator.calulateSimilarityOfAuthor(toCheck.getAuthorship(), authorship);
								logger.debug(similarity);
								if (similarity>.75d) { 
									String uuid = mei.getChildElement(new QName("UUID")).getValue();
									String recnum = mei.getChildElement(new QName("RECORD_x0020_NUMBER")).getValue();
								    String lsid = "urn:lsid:indexfungorum.org:names:" + recnum;
									logger.debug("\"" + taxonName + "\", \"urn:uuid:" + uuid + "\",\"" + lsid +  "\"");
									validatedNameUsage = new NameUsage("IndexFungorum",authorNameComparator, toCheck.getOriginalScientificName(), toCheck.getOriginalAuthorship());
									validatedNameUsage.setScientificName(taxonName);
									validatedNameUsage.setKey(Integer.parseInt(recnum));
									validatedNameUsage.setGuid(lsid);
									validatedNameUsage.setSourceID("urn:uuid:"+ uuid);
									validatedNameUsage.setAuthorship(authorship);
									validatedNameUsage.setMatchDescription(NameComparison.MATCH_AUTHSIMILAR);
									validatedNameUsage.setAuthorshipStringEditDistance(similarity);
								    validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
								    validatedNameUsage.setOriginalScientificName(toCheck.getScientificName());
								    validatedNameUsage.setOriginalAuthorship(toCheck.getAuthorship());
								    result = true;
								    addToComment("Found plausible match in IndexFungorum.");
								} 
							}
						}
					}
					
				}
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}


}
