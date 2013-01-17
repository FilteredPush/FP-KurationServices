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
require_once(dirname(dirname(__FILE__)) . '/FPConfig.php');

class acceptMessage {
	public $type; // string
	public $messageUUID; // string
	public $date; // dateTime
	public $content; // string
	public $scheme; // string
	public $originatorUUID; // string
	public $origin; // clientIdentity
}

class clientIdentity {
}

class acceptMessageResponse {
	public $return; // string
}

class checkForMessages {
	public $topic; // string
	public $requestor; // clientIdentity
}

class checkForMessagesResponse {
	public $return; // fpMessage
}

class fpMessage {
	public $clientMessageID; // string
	public $content; // string
	public $inResponseTo; // string
	public $messageId; // string
	public $scheme; // string
	public $sender; // clientIdentity
	public $time; // dateTime
	public $type; // messageType
}

class messageType {
	public $type; // string
}


/**
 * FPNetworkAccessPointWebServiceService class
 *
 *
 *
 * @author    {author}
 * @copyright {copyright}
 * @package   {package}
 */
class FPNetworkAccessPointWebServiceService extends SoapClient {

	private static $classmap = array(
			'acceptMessage' => 'acceptMessage',
			'clientIdentity' => 'clientIdentity',
			'acceptMessageResponse' => 'acceptMessageResponse',
			'checkForMessages' => 'checkForMessages',
			'checkForMessagesResponse' => 'checkForMessagesResponse',
			'fpMessage' => 'fpMessage',
			'messageType' => 'messageType',
	);

	public function FPNetworkAccessPointWebServiceService($options = array()) {
		foreach(self::$classmap as $key => $value) {
			if(!isset($options['classmap'][$key])) {
				$options['classmap'][$key] = $value;
			}
		}
		parent::__construct(FPNODE_ENDPOINT, $options);
	}

	/**
	 *
	 *
	 * @param acceptMessage $parameters
	 * @return acceptMessageResponse
	 */
	public function acceptMessage(acceptMessage $parameters) {
		try {
			return $this->__soapCall('acceptMessage', array($parameters),       array(
					'uri' => 'http://triage.fp2.mcz.harvard.edu/',
					'soapaction' => '')
			);
		} catch (Exception $e) {
			throw $e;
		}
	}

	/**
	 *
	 *
	 * @param checkForMessages $parameters
	 * @return checkForMessagesResponse
	 */
	public function checkForMessages(checkForMessages $parameters) {
		try {
			return $this->__soapCall('checkForMessages', array($parameters),       array(
					'uri' => 'http://triage.fp2.mcz.harvard.edu/',
					'soapaction' => '')
			);
		} catch (Exception $e) {
			throw $e;
		}
	}

}

?>
