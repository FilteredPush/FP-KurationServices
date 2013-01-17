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

class AnnotationGenerator {
	public $rdfhandler_endpoint;

	const IDENTIFICATION_ENDPOINT = 'identification';
	const GEOREFERENCE_ENDPOINT = 'georeference';
	
	function __construct($rdfhandler_endpoint) {
		if ($rdfhandler_endpoint[strlen($rdfhandler_endpoint)-1] != '/') {
			$rdfhandler_endpoint .= '/';
		}
		$this->rdfhandler_endpoint = $rdfhandler_endpoint;
	}

	function generateRdfXml($annotation) {
		$georeferenceKeys = array('decimalLatitude', 'decimalLongitude', 'coordinateUncertaintyInMeters',
				'geodeticDatum', 'verbatimCoordinates', 'georeferencedBy', 'georeferenceProtocol' ,
				'georeferenceSources', 'georeferenceVerificationStatus', 'georeferenceRemarks');
		$identificationKeys = array('scientificNameAuthorship', 'identifiedBy', 'scientificName', 'dateIdentified');
		$bodyKeys = array_keys($annotation['hasBody']);
		
		$handlerUrl = $this->rdfhandler_endpoint;
		if (sizeof(array_intersect($georeferenceKeys, $bodyKeys)) > 0) {
			$handlerUrl .= self::GEOREFERENCE_ENDPOINT;
		} else if (sizeof(array_intersect($identificationKeys, $bodyKeys)) > 0) {
			$handlerUrl .= self::IDENTIFICATION_ENDPOINT;
		}
		
		$json = "{\"annotation\":" . json_encode($annotation) . "}";
		$session = curl_init($handlerUrl);
		curl_setopt ($session, CURLOPT_POST, true);
		curl_setopt($session, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
		curl_setopt($session, CURLOPT_POSTFIELDS, $json);
		curl_setopt($session, CURLOPT_HEADER, false);
		curl_setopt($session, CURLOPT_RETURNTRANSFER, true);
		$response = curl_exec($session);
		curl_close($session);

		if (!$response) {
			throw new Exception("Error generating rdf/xml using annotation generation service.");
		}
		
		return $response;
	}
}

?>