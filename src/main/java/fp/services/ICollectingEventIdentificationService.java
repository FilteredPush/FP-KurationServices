package fp.services;

import fp.util.SpecimenRecord;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public interface ICollectingEventIdentificationService extends ICurationWithFileService {
	
	//The key is the collector
	//The TreeSet is the events belonging to this collection and ordered by the collecting date
	public void identifyOutlier(LinkedHashMap<String, TreeSet<SpecimenRecord>> inputDataMap, boolean doRemoteComparison);
	
	public LinkedList<SpecimenRecord> getNoneOutlier();
	
	//The key is the outlier
	//the linkedlist is the comparator data from the local dataset
	public LinkedHashMap<SpecimenRecord,LinkedList<SpecimenRecord>> getOutlierLocalComparatorMap();
	
	//The key is the outlier
	//the LinkedList is the comparator data queried remotely from the Filtered-push network
	public LinkedHashMap<SpecimenRecord,LinkedList<SpecimenRecord>> getOutlierRemoteComparatorMap();
}