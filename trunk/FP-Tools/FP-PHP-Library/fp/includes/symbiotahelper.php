<?php
/* Copyright © 2012 President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Author: David B. Lowery
 */
function checkValidArgs($requiredKeys, $providedKeys) {
	// TODO: The validation below really should be done by the annotation generation web service
	// Either collectioncode or institutioncode is required for all annotation types
	$collectionOrInstitutionCode = in_array('collectioncode', $providedKeys) ||
		in_array('institutioncode', $providedKeys);

	return (array_intersect($requiredKeys, $providedKeys) == $requiredKeys) && $collectionOrInstitutionCode;
}

function fpNewDetArr($detArr) {
	
	$requiredKeys = array('catalognumber', 'annotatorname', 'scientificnameauthorship',
			'identifiedby', 'sciname', 'dateidentified');
	$providedKeys = array_keys($detArr);
	
	if (!checkValidArgs($requiredKeys, $providedKeys)) {
		$message = '$detArr is missing required keys:';
		foreach (array_diff($requiredKeys, $providedKeys) as $key) {
			$message .= ' ' . $key ;
		}
		throw new Exception($message);
	}

	$annotator = array();
	$annotator['name'] = $detArr['annotatorname'];
	if (array_key_exists('annotatoremail', $detArr)) {
		$annotator['mbox'] = $detArr['annotatoremail'];
		$annotator['mbox_sha1sum'] = sha1($detArr['annotatoremail']);
	}

	$selector = array();
	$selector['catalogNumber'] = $detArr['catalognumber'];
	
	if (array_key_exists('collectioncode', $detArr))
		$selector['collectionCode'] = $detArr['collectioncode'];
	if (array_key_exists('institutioncode', $detArr))
		$selector['institutionCode'] = $detArr['institutioncode'];

	$target = array();
	$target['hasSelector'] = $selector;
	
	$body = array();
	$body['dateIdentified'] = $detArr['dateidentified'];
	$body['identifiedBy'] = $detArr['identifiedby'];
	$body['scientificName'] = $detArr['sciname'];
	$body['scientificNameAuthorship'] = $detArr['scientificnameauthorship'];

	$evidence = array();
	if (!empty($detArr['identificationreferences']))
		$evidence['chars'] = $detArr['identificationreferences'];

	// TODO: fix these and make rdfhandler include them as defaults
	$generator = array(' ');
	$expectation = array();
	$expectation['type'] = "oad:Expectation_Insert";

	$annotation = Array('annotator' => $annotator, 'generator' => $generator,
			'hasExpectation' => $expectation, 'hasTarget' => $target, 'hasBody' => $body, 'hasEvidence' => $evidence);
	
	return $annotation;
}

function fpNewGeorefArr($occArr) {
	$requiredKeys = array('decimallatitude', 'decimallongitude', 'geodeticdatum', 'coordinateuncertaintyinmeters', 
			'georeferencedby', 'georeferenceprotocol');
	$providedKeys = array_keys($occArr);

	if (!checkValidArgs($requiredKeys, $providedKeys)) {
		$message = '$occArr is missing required keys:';
		foreach (array_diff($requiredKeys, $providedKeys) as $key) {
			$message .= ' ' . $key ;
		}
		throw new Exception($message);
	}
	
	$annotator = array();
	$annotator['name'] = $occArr['annotatorname'];
	if (array_key_exists('annotatoremail', $occArr)) {
		$annotator['mbox'] = $occArr['annotatoremail'];
		$annotator['mbox_sha1sum'] = sha1($occArr['annotatoremail']);
	}
	
	$selector = array();
	$selector['catalogNumber'] = $occArr['catalognumber'];
	
	if (array_key_exists('collectioncode', $occArr))
		$selector['collectionCode'] = $occArr['collectioncode'];
	if (array_key_exists('institutioncode', $occArr))
		$selector['institutionCode'] = $occArr['institutioncode'];
	
	$target = array();
	$target['hasSelector'] = $selector;
	
	$body = array();
	$body['decimalLatitude'] = $occArr['decimallatitude'];
	$body['decimalLongitude'] = $occArr['decimallongitude'];
	$body['coordinateUncertaintyInMeters'] = $occArr['coordinateuncertaintyinmeters'];
	$body['geodeticDatum'] = $occArr['geodeticdatum'];
	$body['verbatimCoordinates'] = $occArr['verbatimcoordinates'];
	$body['georeferencedBy'] = array('name' => $occArr['georeferencedby']);
	$body['georeferenceProtocol'] = $occArr['georeferenceprotocol'];
	$body['georeferenceSources'] = $occArr['georeferencesources'];
	$body['georeferenceVerificationStatus'] = $occArr['georeferenceverificationstatus'];
	$body['georeferenceRemarks'] = $occArr['georeferenceremarks'];
	
	$evidence = array();
	if (!empty($detArr['identificationreferences']))
		$evidence['chars'] = $detArr['identificationreferences'];
	
	// TODO: fix these and make rdfhandler include them as defaults
	$generator = array(' ');
	$expectation = array();
	$expectation['type'] = "oad:Expectation_Insert";
	
	$annotation = Array('annotator' => $annotator, 'generator' => $generator,
			'hasExpectation' => $expectation, 'hasTarget' => $target, 'hasBody' => $body, 'hasEvidence' => $evidence);
	
	return $annotation;
}