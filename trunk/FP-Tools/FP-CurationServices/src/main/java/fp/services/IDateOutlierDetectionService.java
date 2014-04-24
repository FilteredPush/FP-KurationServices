package fp.services;

import fp.util.SpecimenRecord;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public interface IDateOutlierDetectionService extends ICurationWithFileService {
	
	//The key is the collector
	//The TreeSet is the events belonging to this collection and ordered by the collecting date
	public void identifyOutlier(LinkedHashMap<String, TreeSet<SpecimenRecord>> inputDataMap, boolean doRemoteComparison);
	
	public LinkedList<SpecimenRecord> getNoneOutlier();
	
	//The key is the outlier
	//the linkedlist is the comparator data from the local dataset
	public LinkedHashMap<SpecimenRecord,LinkedList<SpecimenRecord>> getOutlierLocalComparatorMap();

}