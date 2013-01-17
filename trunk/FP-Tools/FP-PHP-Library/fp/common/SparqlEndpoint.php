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

require_once('FPQuery.php');

class SparqlEndpoint {
	function __construct($query_endpoint, $datasource, $result_xslt) {
		$this->query_endpoint = $query_endpoint;
		$this->datasource = $datasource;
		$this->result_xslt = $result_xslt;
	}
	
	function query($query) {
		if (empty($query)) {
			echo 'Invalid SPARQL query! Please provide a valid query parameter.';
			exit;
		}
	
		$url = $this->datasource_url() . $this->query_params($query);
	
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, $url);
	
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_TIMEOUT, '3');
		$response = curl_exec($ch);

		$code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
		curl_close($ch);
	
		$error = 'Query execution failed or the SPARQL endpoint is inaccessible!';
		if (($code > 399 && $code < 500) || empty($response)) {
			if ($response) {
				error_log($response);
			} else {
				error_log("SPARQL endpoint returned http code $code and empty response was recieved. url=$url");
				$error = 'No results';
			}
			echo $error;
			exit;
		}
	
		$xml = new DOMDocument;
	
		if (!$xml->loadXml($response)) {
			echo 'Error parsing the query result returned by the SPARQL endpoint!';
			exit;
		}
	
		$xpath = new DOMXpath($xml);
		$nodes = $xpath->evaluate('/processing-instruction("xml-stylesheet")');
	
		$xsl = new DOMDocument();
	
		if(!empty($nodes)) {
			$pi = $nodes->item(0);
			if(ereg('type *= *"text/xsl" +href *= *"([^"]+)"', $pi->data, $mem)) {
				if (!$xsl->load($this->endpoint_url() . $mem[1])) {
					echo 'Error parsing the stylesheet "' . $mem[1] . '" returned by the SPARQL endpoint!';
					exit;
				}
			}
		} else {
			// TODO: no stylesheet declaration found in processing instructions
			// obtain one from GET params or use a default
		}
	
		$xslt = new XSLTProcessor();
		$xslt->importStylesheet($xsl);
	
		$html = $xslt->transformToDoc($xml);
	
		return $html->saveHTML();
	}
	
	function getAnnotations($params) {
		$filter = 'FILTER (';
		if (array_key_exists('collectioncode', $params)) {
			$filter .= '?collectionCode = "'.$params['collectioncode'].'" && ';
		}
		if (array_key_exists('institutioncode', $params)) {
			$filter .= '?institutionCode = "'.$params['institutioncode'].'" && ';
		}
		
		$filter .= '?catalogNumber = "'.$params['catalognumber'].'" )';
		
		$query = new FPQuery();
		$sparql = $query->annotationQuery($filter);
		
		return $this->query($sparql);
	}
	
	function query_params($query) {
		return 'query?query=' . urlencode($query) . '&output=xml&stylesheet=' . urlencode($this->result_xslt);
	}
	
	function endpoint_url() {
		$url = $this->query_endpoint;
		if ($url[strlen($url)-1] != '/') {
			$url .= '/';
		}
		return $url;
	}
	
	function datasource_url() {
		return $this->endpoint_url() . $this->datasource . '/';
	}
}

?>