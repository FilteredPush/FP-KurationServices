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
require_once(dirname(dirname(__FILE__)) . '/common/XmlSign.php');

class FPLiteFacade {
	function injectIntoFP($rdf) {
		$signer = new XmlSign();

		$data = "";

		try {
			$data = $signer->sign($rdf, X509_CERTIFICATE, PRIVATE_KEY);
		} catch (Exception $e) {
			error_log('Error signing the annotation rdf/xml: ' . $e->getMessage());
			return false;
		}

		$post = 'data=' . urlencode($data);
		$response = $this->curl_post($post, SPARQLPUSH_SERVER . "/?post=1");
		
		if (!$response) {
			throw new Exception("Error during network submission to sparqlpush.");
		}
		
		return $response;
	}

	function registerInterest($sparql) {
		$get = 'query=' . urlencode($sparql);
		$feed_url = $this->curl_get(SPARQLPUSH_SERVER . "/?" . $get);
		
		$get = 'action=r&' . $get . '&endpoint=' . SPARQLPUSH_SERVER;
		$response = $this->curl_get($post, SPARQLPUSH_CLIENT . "/register.php?" . $get);
		
		if ($response)
			return $feed_url; // returns a feed url upon success
	}

	function curl_post($post, $request) {
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_URL, $request);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $post);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		$response = curl_exec($ch);
			
		$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE) == 200;
		curl_close($ch);
			
		return $http_code;
	}
	
	function curl_get($request) {
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_GET, 1);
		curl_setopt($ch, CURLOPT_URL, $request);
		$response = curl_exec($ch);
			
		$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE) == 200;
		curl_close($ch);
			
		return $response;
	}
}