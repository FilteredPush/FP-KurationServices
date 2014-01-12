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

include_once('../../../config/symbini.php');
include_once('fp/FPNetworkFactory.php');

// Check for required query params
if (array_key_exists('catalogNumber', $_GET) &&
		(array_key_exists('collectionCode', $_GET) || (array_key_exists('institutionCode', $_GET)))) {
	$endpoint = FPNetworkFactory::getSparqlEndpoint();
	
	// returns query result formatted as html
	echo $endpoint->getAnnotations($_GET);
} else {
	throw new Exception("catalogNumber and either collectionCode or institutionCode required for \"Annotations\" tab view.");
}

?>