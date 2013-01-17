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
require_once(dirname(dirname(__FILE__)) . '/common/FPNetworkAccessPointWebServiceService.php');

class FPMediumFacade {
	function injectIntoFP($rdf) {
		$accesspoint = new FPNetworkAccessPointWebServiceService();
		$message = new acceptMessage();
		$message->content = $rdf;
		$message->date = new DateTime();
		$message->messageUUID = $this->generateUUID();
		$message->origin = new clientIdentity();
		$message->originatorUUID = $this->generateUUID();
		$message->scheme = "RDF_XML";
		$message->type = "ANNOTATION";
		
		try {
			$accesspoint->acceptMessage($message);
			return true;
		} catch (Exception $e) {
			error_log($e->getMessage());
			return false;
		}
	}
	
	function registerInterest($sparql) {
		// not yet implemented
	}
	
	function generateUUID() {
		uuid_create(&$context);
		uuid_make($context, UUID_MAKE_V4);
		uuid_export($context, UUID_FMT_STR, &$uuid);
		return trim($uuid);
	}	
}