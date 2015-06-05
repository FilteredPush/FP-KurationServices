/** 
 * CollectingEventOutlierIdentificationService.java 
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
package org.filteredpush.kuration.services;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.filteredpush.kuration.interfaces.ICollectingEventIdentificationService;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.filteredpush.kuration.util.GEOUtil;
import org.filteredpush.kuration.util.SpecimenRecord;
import org.filteredpush.kuration.util.SpecimenRecordTypeConf;

public class CollectingEventOutlierIdentificationService extends BaseCurationService implements ICollectingEventIdentificationService {

	private String CollectorLabel;
    private String yearCollectedLabel;
    private String monthCollectedLabel;
    private String dayCollectedLabel;
    private String latitudeLabel;
    private String longitudeLabel;  
	
    private LinkedList<SpecimenRecord> noneOutlier = new LinkedList<SpecimenRecord>();
    private LinkedHashMap<SpecimenRecord,LinkedList<SpecimenRecord>> outlierLocalComparatorMap = new LinkedHashMap<SpecimenRecord,LinkedList<SpecimenRecord>>();
    private LinkedHashMap<SpecimenRecord,LinkedList<SpecimenRecord>> outlierRemoteComparatorMap = new LinkedHashMap<SpecimenRecord,LinkedList<SpecimenRecord>>();
    
    private final int temporalDistanceThreshold = 7; //in day
    private final int travelDistanceThreshold = 1000; //in km/day
    private final int outlierDenyRecordNumberFromFP = 10; 
    private final int day = 86400000;    
	
	private final String serviceName = "Collecting Event Outlier Identification Service";
	
	public void identifyOutlier(LinkedHashMap<String, TreeSet<SpecimenRecord>> inputDataMap, boolean doRemoteComparison) {
		initBase();
		noneOutlier.clear();
		outlierLocalComparatorMap.clear();
		outlierRemoteComparatorMap.clear();
		
		try{
			initializeLabel();

			Iterator<String> inputDataMapIter = inputDataMap.keySet().iterator();
			while(inputDataMapIter.hasNext()){
				String collector = inputDataMapIter.next();
				TreeSet<SpecimenRecord> specimenRecordSet = inputDataMap.get(collector);
				
				LinkedList<SpecimenRecord> timeBasedCluster = new LinkedList<SpecimenRecord>();
				long previousTime = 0;
				Iterator<SpecimenRecord> specimenRecordSetIter = specimenRecordSet.iterator();
				while(specimenRecordSetIter.hasNext()){
                    SpecimenRecord specimenRecordDataToken = specimenRecordSetIter.next();
                    SpecimenRecord currentSpecimenRecord = specimenRecordDataToken;
					long currentTimestamp = getTimeStamp(currentSpecimenRecord);					
					
					if(previousTime!=0 && isTooLongInterval(previousTime,currentTimestamp)){
						//end of one time-based cluster
						findOutlier(timeBasedCluster, doRemoteComparison);
						timeBasedCluster.clear();
					}
					
					timeBasedCluster.add(specimenRecordDataToken);
					previousTime = currentTimestamp;
				}
				
				if(timeBasedCluster.size()>0){
					findOutlier(timeBasedCluster, doRemoteComparison);
				}			
			}
			
			if(outlierLocalComparatorMap.size()>0 || outlierRemoteComparatorMap.size()>0){
				setCurationStatus(CurationComment.UNABLE_CURATED);
				if(doRemoteComparison){
					addToComment("Record is an outlier of collecting events of a collector by comparing to the data in the dataset.");
				}else{
					addToComment("Record is an outlier of collecting events of a collector by comparing to both data in this dataset and data queried from Filtered-Push network.");
				}
			}else{
				setCurationStatus(CurationComment.CORRECT);
				addToComment("Collecting event does not appear to be an outlier.");
			}
			
		}catch(CurationException ex){
			addToComment(ex.getMessage());
			setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
			return;
		}		
	}	
	
	public LinkedList<SpecimenRecord> getNoneOutlier() {
		return noneOutlier;
	}

	public LinkedHashMap<SpecimenRecord, LinkedList<SpecimenRecord>> getOutlierLocalComparatorMap() {
		return outlierLocalComparatorMap;
	}

	public LinkedHashMap<SpecimenRecord, LinkedList<SpecimenRecord>> getOutlierRemoteComparatorMap() {
		return outlierRemoteComparatorMap;
	}	
	
	public void setCacheFile(String file) throws CurationException {
	}	
	
	public void flushCacheFile() throws CurationException {
	}

    @Override
    public List<List> getLog() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUseCache(boolean use) {
    }

    public String getServiceName(){
		return serviceName;
	}
	
	private void initializeLabel() throws CurationException {
		SpecimenRecordTypeConf speicmenRecordTypeConf = SpecimenRecordTypeConf.getInstance();
				
		CollectorLabel = speicmenRecordTypeConf.getLabel("RecordedBy");
		if(CollectorLabel == null){
			throw new CurationException(getClass().getName()+" failed since the RecordedBy label of the SpecimenRecordType is not set.");
		}		
		
		yearCollectedLabel = speicmenRecordTypeConf.getLabel("YearCollected");
		if(yearCollectedLabel == null){
			throw new CurationException(getClass().getName()+" failed since the YearCollected label of the SpecimenRecordType is not set.");
		}
		
		monthCollectedLabel = speicmenRecordTypeConf.getLabel("MonthCollected");
		if(monthCollectedLabel == null){
			throw new CurationException(getClass().getName()+" failed since the MonthCollected label of the SpecimenRecordType is not set.");
		}
		
		dayCollectedLabel = speicmenRecordTypeConf.getLabel("DayCollected");
		if(dayCollectedLabel == null){
			throw new CurationException(getClass().getName()+" failed since the DayCollected label of the SpecimenRecordType is not set.");
		}	
		
		latitudeLabel = speicmenRecordTypeConf.getLabel("DecimalLatitude");
		if(latitudeLabel == null){
			throw new CurationException(getClass().getName()+" failed since the DecimalLatitude label of the SpecimenRecordType is not set.");
		}
		
		longitudeLabel = speicmenRecordTypeConf.getLabel("DecimalLongitude");
		if(longitudeLabel == null){
			throw new CurationException(getClass().getName()+" failed since the DecimalLongitude label of the SpecimenRecordType is not set.");
		}		
	}
	
	private long getTimeStamp(Map<String,String> record){
		int year = Integer.parseInt(record.get(yearCollectedLabel));
		int month = Integer.parseInt(record.get(monthCollectedLabel));
		int day = Integer.parseInt(record.get(dayCollectedLabel));
		return getTimestamp(getFormatedDate(year,month,day));
	}
	
	private long getTimestamp(String dateStr){	
		//date is in format of mm-dd-yyyy
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		Date date;
		try {
			date = format.parse(dateStr);
			return date.getTime();
		} catch (ParseException e) {
			// shouldn't happen
			e.printStackTrace();
		}
		return 0;
	}
	
	private String getFormatedDate(int year, int month, int day){
		//assume year is four digit
		String yearStr = String.valueOf(year);

		String monthStr = String.valueOf(month);		
		if(month<10){
			monthStr = "0"+monthStr;
		}

		String dayStr = String.valueOf(day);		
		if(day<10){
			dayStr = "0"+dayStr;
		}
		
		return monthStr+"-"+dayStr+"-"+yearStr;
	}
	
	private boolean isTooLongInterval(long t1, long t2){
		long distance = t2 - t1;
		if(distance>=0 && (distance/day)>temporalDistanceThreshold){
			return true;
		}else{
			return false;
		}
	}	
	
	private void findOutlier(LinkedList<SpecimenRecord> timeBasedCluster, boolean doRemoteComparison) throws CurationException {
		HashMap<SpecimenRecord, LinkedList<SpecimenRecord>> outlierMap = new HashMap<SpecimenRecord, LinkedList<SpecimenRecord>>();
		LinkedList<SpecimenRecord> localComparator = new LinkedList<SpecimenRecord>();
		for(int i=0;i<timeBasedCluster.size();i++){
            SpecimenRecord currentSpecimenDataToken = timeBasedCluster.get(i);
            SpecimenRecord currentSpecimenRecord = currentSpecimenDataToken;
			
			//test outlier by comparing backward
			boolean isOutlierBackward = false;
			if(i!=0){
				//find the nearest none outlier
				int j=i-1;
                SpecimenRecord previousDataToken = timeBasedCluster.get(j);
				while(outlierMap.containsKey(previousDataToken) && j>0){
					j = j-1;
					previousDataToken = timeBasedCluster.get(j);
				}
				
				if(!outlierMap.containsKey(previousDataToken)){
					isOutlierBackward = isTooFar(previousDataToken, currentSpecimenRecord);
				}
			}
			
			//test outlier by comparing forward
			boolean isOutlierForward = false;
			if(i<timeBasedCluster.size()-1){
                SpecimenRecord successiveRecord = timeBasedCluster.get(i+1);
				
				if(isTooFar(currentSpecimenRecord,successiveRecord)){
					isOutlierForward = true;
				}
			}			
			
			boolean isOutlier = false;
			if(isOutlierBackward && isOutlierForward){				
				//It's a potential outlier				
				if(doRemoteComparison){
					//do further confirmation by using records quried from FP
					//once it's confirmed once, then we think it's an outlier no matter whether the other records come to the same result
					//since at least it's a highly suspicious record.
					//But if all the records got from FP agree that it's not an outlier and the number of the records exceed a threshold, we think it's not outlier so far.
					//this part should be refined in the future.
					boolean confirmedOutlier = false;
					LinkedList<SpecimenRecord> specimenRecordsFromFP = getAdjacentRecordsFromFP(currentSpecimenRecord);
					for(int k=0;k<specimenRecordsFromFP.size();k++){
						if(isTooFar(currentSpecimenRecord,specimenRecordsFromFP.get(k))){
							confirmedOutlier = true;
							break;
						}
					}
					
					if(confirmedOutlier||
						!confirmedOutlier && specimenRecordsFromFP.size()<outlierDenyRecordNumberFromFP){
						isOutlier = true;		
						outlierMap.put(currentSpecimenDataToken, specimenRecordsFromFP);
					}	
				}else{
					isOutlier = true;
					outlierMap.put(currentSpecimenDataToken, null);
				}				
			}
			
			if(!isOutlier){
				noneOutlier.add(currentSpecimenDataToken);
				localComparator.add(currentSpecimenDataToken);
			}
		}
		
		if(outlierMap.size()>0){
			Iterator<SpecimenRecord> iter = outlierMap.keySet().iterator();
			while(iter.hasNext()){
                SpecimenRecord outlier = iter.next();
				outlierLocalComparatorMap.put(outlier,localComparator);
				if(doRemoteComparison){
					outlierRemoteComparatorMap.put(outlier,outlierMap.get(outlier));
				}				
			}
		}
	}	
	
	private LinkedList<SpecimenRecord> getAdjacentRecordsFromFP(SpecimenRecord specimenRecord) throws CurationException {
		//add search conditions
		String collector = specimenRecord.get(CollectorLabel);
		int year = Integer.parseInt(specimenRecord.get(yearCollectedLabel));
		int month = Integer.parseInt(specimenRecord.get(monthCollectedLabel));
		int day = Integer.parseInt(specimenRecord.get(dayCollectedLabel));
		
		int previousYear = year;
		int previousMonth = month;
		int previousDay = day - 1;
		
		int successiveYear = year;
		int successiveMonth = month;
		int successiveDay = day + 1;		
		
		if(month==1 && day ==1){
			previousYear = year -1;
			previousMonth = 12;
			previousDay = 31;
		}else if(month==12 && day ==31){
			successiveYear = year + 1;
			successiveMonth = 1;
			successiveDay = 1;			
		}else if(day ==1){
			previousMonth = previousMonth -1;
			if(isBigMonth(previousMonth)){
				previousDay = 31;
			}else{
				previousDay = 30;
			}				
		}else if(isBigMonth(month) && day ==31 || !isBigMonth(month) && day ==30){
			successiveMonth = successiveMonth +1;
			successiveDay = 1;		
		}

        LinkedList<SpecimenRecord> result = new LinkedList<SpecimenRecord>();
        /*
        TODO!
		FPQuery fpQuery = new FPQuery();
		
		fpQuery.clearConditions();
		fpQuery.addCondition("collector", "Binary", 0.0, collector);
		fpQuery.addCondition("year", "Binary", 0.0, String.valueOf(previousYear));
		fpQuery.addCondition("month", "Binary", 0.0, String.valueOf(previousMonth));
		fpQuery.addCondition("day", "Binary", 0.0, String.valueOf(previousDay));
		LinkedList<SpecimenRecord> result = fpQuery.search();
		
		fpQuery.clearConditions();
		fpQuery.addCondition("collector", "Binary", 0.0, collector);
		fpQuery.addCondition("year", "Binary", 0.0, String.valueOf(year));
		fpQuery.addCondition("month", "Binary", 0.0, String.valueOf(month));
		fpQuery.addCondition("day", "Binary", 0.0, String.valueOf(day));		
		result.addAll(fpQuery.search()); 
				
		fpQuery.clearConditions();
		fpQuery.addCondition("collector", "Binary", 0.0, collector);
		fpQuery.addCondition("year", "Binary", 0.0, String.valueOf(successiveYear));
		fpQuery.addCondition("month", "Binary", 0.0, String.valueOf(successiveMonth));
		fpQuery.addCondition("day", "Binary", 0.0, String.valueOf(successiveDay));	
		result.addAll(fpQuery.search());
        */

		return result;
	}
	
	private boolean isBigMonth(int month){
		if(month==1 || month ==3 || month ==5 || month ==7 || month ==8 || month ==10 || month ==12){
			return true;
		}else{
			return false;
		}
	}	
	
	private boolean isTooFar(SpecimenRecord r1, SpecimenRecord r2){
		long timestamp1 = getTimeStamp(r1);
		double latitude1 = Double.valueOf(r1.get(latitudeLabel));
		double longitutde1 = Double.valueOf(r1.get(longitudeLabel));
		
		long timestamp2 = getTimeStamp(r2);
		double latitude2 = Double.valueOf(r2.get(latitudeLabel));
		double longitutde2 = Double.valueOf(r2.get(longitudeLabel));
		
		double travelDistancePerDay = GEOUtil.getDistanceKm(latitude1, longitutde1, latitude2, longitutde2)/(Math.abs(timestamp2 - timestamp1)/day);
		if(travelDistancePerDay>=travelDistanceThreshold){ 
			return true;
		}else{
			return false;
		}		
	}	
	

	
}
