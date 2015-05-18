/** 
 * ICollectingEventIdentificationService.java 
 * 
 * Copyright 2013 President and Fellows of Harvard College
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
package org.filteredpush.kuration.interfaces;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import org.filteredpush.kuration.util.SpecimenRecord;

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