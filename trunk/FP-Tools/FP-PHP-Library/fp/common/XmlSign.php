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
require_once 'xmlseclibs.php';

class XmlSign {
	function sign($xml, $cert, $pk) {
		$doc = new DOMDocument();
		$doc->loadXML($xml);
		
		$objDSig = new XMLSecurityDSig();
		$objDSig->setCanonicalMethod(XMLSecurityDSig::EXC_C14N);
		$objDSig->addReference($doc, XMLSecurityDSig::SHA1, array('http://www.w3.org/2000/09/xmldsig#enveloped-signature'));
		$objKey = new XMLSecurityKey(XMLSecurityKey::RSA_SHA1, array('type'=>'private'));
		/* load private key */
		$objKey->loadKey($pk, TRUE);
		
		/* if key has Passphrase, set it using $objKey->passphrase = <passphrase> " */
		$objDSig->sign($objKey);
		
		/* Add associated public key */
		$objDSig->add509Cert(file_get_contents($cert));
		$objDSig->appendSignature($doc->documentElement);
		
		return $doc->saveXML();
	}
}