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

//Localhost config
define('RDFHANDLER_ENDPOINT', 'http://localhost:8081/FP-AnnotationGenerator/rest/generate/');
define('FPNODE_ENDPOINT', 'http://localhost:8081/FPNetworkAccessPointWebServiceService/FPNetworkAccessPointWebService?wsdl');

define('SPARQLPUSH_SERVER', 'http://localhost/node1/server');
define('SPARQLPUSH_CLIENT', 'http://localhost/node1/client');
define('DS','AnnotationStore');
define('SPARQL_ENDPOINT', 'http://localhost:3030/');
define('RESULT_XSLT', 'annotations.xsl');

define('X509_CERTIFICATE', '/etc/filteredpush/auth/symbiota-cert.pem');
define('PRIVATE_KEY', '/etc/filteredpush/auth/symbiota-privkey.pem');

define('NETWORK_FACADE', 'FPLiteFacade');

?>