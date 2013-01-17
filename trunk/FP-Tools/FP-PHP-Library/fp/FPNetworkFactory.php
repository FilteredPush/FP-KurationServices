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
require_once(dirname(__FILE__) . '/FPConfig.php');
require_once(dirname(__FILE__) . '/facades/' . NETWORK_FACADE . '.php');
require_once(dirname(__FILE__) . '/common/AnnotationGenerator.php');
require_once(dirname(__FILE__) . '/common/SparqlEndpoint.php');

class FPNetworkFactory {
    static function getNetworkFacade() {
		$facade = NETWORK_FACADE;
		return new $facade();
	}
	
	static function getAnnotationGenerator() {
		return new AnnotationGenerator(RDFHANDLER_ENDPOINT);
	}
	
	static function getSparqlEndpoint() {
		return new SparqlEndpoint(SPARQL_ENDPOINT, DS, RESULT_XSLT);
	}
}