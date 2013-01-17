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
class FPQuery {
	function annotationQuery($filter) {
		$query = 'PREFIX oa: <http://www.w3.org/ns/openannotation/core/>
				  PREFIX foaf: <http://xmlns.com/foaf/0.1/>
				  PREFIX dwcFP: <http://filteredpush.org/dwcFP/>
		          PREFIX bom: <http://www.ifi.uzh.ch/ddis/evoont/2001/11/bom#>
		          PREFIX marl: <http://purl.org/marl/ns/>
		          PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
		          
		          SELECT ?uri ?date ?createdBy ?collectionCode ?institutionCode ?catalogNumber
		                 ?identifiedBy ?dateIdentified ?scientificName ?scientificNameAuthorship
		                 ?decimalLatitude ?decimalLongitude ?geodeticDatum ?coordinateUncertaintyInMeters
		                 ?georeferencedBy ?georeferenceProtocol ?describesObject ?polarity ?opinionText
		          WHERE {
		            ?uri oa:hasTarget ?target .
		            ?uri oa:annotated ?date .
		            ?uri oa:hasBody ?body .
		            ?uri oa:annotator ?annotator .
		            ?annotator foaf:name ?createdBy .
		            {
		              ?target a bom:Issue .
		              ?body marl:hasPolarity ?thePolarity .
		              ?body marl:describesObject ?describesObject .
		              ?body marl:opinionText ?opinionText .
		              ?thePolarity rdf:type ?polarity
	                } UNION {
	                  ?target oa:hasSelector ?selector .
		              { ?selector dwcFP:collectionCode ?collectionCode } UNION
		              { ?selector dwcFP:institutionCode ?institutionCode } .
		              ?selector dwcFP:catalogNumber ?catalogNumber .
		              {
						?body a dwcFP:Identification .
			            ?body dwcFP:identifiedBy ?identifiedBy .
			            ?body dwcFP:dateIdentified ?dateIdentified .
			            ?body dwcFP:scientificName ?scientificName .
			            ?body dwcFP:scientificNameAuthorship ?scientificNameAuthorship
	                  } UNION {
	                    ?body a dwcFP:Location .
			            ?body dwcFP:decimalLatitude ?decimalLatitude .
			            ?body dwcFP:decimalLongitude ?decimalLongitude .
			            ?body dwcFP:geodeticDatum ?geodeticDatum .
			            ?body dwcFP:coordinateUncertaintyInMeters ?coordinateUncertaintyInMeters .
			            ?body dwcFP:georeferencedBy ?theGeoreferencedBy .
			            ?theGeoreferencedBy foaf:name ?georeferencedBy .
			            ?body dwcFP:georeferenceProtocol ?georeferenceProtocol
			          }
			        }'.$filter.
		         '}';
		
		return $query;
	}
}