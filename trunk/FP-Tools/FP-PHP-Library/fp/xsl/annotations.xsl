<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
				  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				  xmlns:res="http://www.w3.org/2005/sparql-results#">
				  
<xsl:template match="/">
        <ol>
            <xsl:for-each select="/res:sparql/res:results/res:result">
            <xsl:sort select="res:binding[@name='date']" order="descending" />
            
            <xsl:variable name="describesObject" select="res:binding[@name='describesObject']" />
            <xsl:variable name="scientificName" select="res:binding[@name='scientificName']" />
            <xsl:variable name="georeferencedBy" select="res:binding[@name='georeferencedBy']" />
            
            <xsl:if test="not($describesObject)">
            <li>
                <xsl:if test="$scientificName">
                <h1>Identification (<xsl:value-of select="res:binding[@name='scientificName']" /> - <xsl:value-of select="res:binding[@name='scientificNameAuthorship']/res:literal" />)</h1>
                <table>
                    <tr>
                        <td><b>Created By:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='createdBy']/res:literal" /></td>
                        <td><b>Created On:</b></td><td><xsl:value-of select="res:binding[@name='date']/res:literal" /></td>
                    </tr>
                    <tr>
                        <td><b>Collection Code:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='collectionCode']/res:literal" /></td>
                        <td><b>Catalog Number:</b></td><td><xsl:value-of select="res:binding[@name='catalogNumber']/res:literal" /></td>
                    </tr>
                    <tr>
                    	<td><b>Institution Code:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='institutionCode']/res:literal" /></td>
                    	<td></td><td></td>
                    </tr>
                    <tr>
                        <td><b>Date Identified:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='dateIdentified']/res:literal" /></td>
                        <td><b>Identified By:</b></td><td><xsl:value-of select="res:binding[@name='identifiedBy']/res:literal" /></td>
                    </tr>
                </table>
                </xsl:if>
                
                <xsl:if test="$georeferencedBy">
                <h1>Georeference (<xsl:value-of select="res:binding[@name='decimalLatitude']" />, <xsl:value-of select="res:binding[@name='decimalLongitude']/res:literal" />)</h1>
                <table>
                    <tr>
                        <td><b>Created By:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='createdBy']/res:literal" /></td>
                        <td><b>Created On:</b></td><td><xsl:value-of select="res:binding[@name='date']/res:literal" /></td>
                    </tr>
                    <tr>
                        <td><b>Collection Code:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='collectionCode']/res:literal" /></td>
                        <td><b>Catalog Number:</b></td><td><xsl:value-of select="res:binding[@name='catalogNumber']/res:literal" /></td>
                    </tr>
                    <tr>
                    	<td><b>Institution Code:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='institutionCode']/res:literal" /></td>
                    	<td><b>Georeferenced By:</b></td><td><xsl:value-of select="res:binding[@name='georeferencedBy']/res:literal" /></td>
                    </tr>
                    <tr>
                        <td><b>Latitude:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='decimalLatitude']/res:literal" /></td>
                        <td><b>Longitude:</b></td><td><xsl:value-of select="res:binding[@name='decimalLongitude']/res:literal" /></td>
                    </tr>
                    <tr>
                        <td><b>Uncertainty (meters):</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='coordinateUncertaintyInMeters']/res:literal" /></td>
                        <td><b>Datum:</b></td><td><xsl:value-of select="res:binding[@name='geodeticDatum']/res:literal" /></td>
                    </tr>
                    <tr>
                        <td><b>Georeference Protocol:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='georeferenceProtocol']/res:literal" /></td>
                        <td></td><td></td>
                    </tr>
                </table>
                </xsl:if>
                
                <p><b>Evidence for Assertion:</b> <xsl:value-of select="res:binding[@name='evidence']/res:literal" /></p>
                <p><b>Motivation for Assertion:</b> <xsl:value-of select="res:binding[@name='motivation']/res:literal" /></p>
                
                <xsl:variable name="annotationUri" select="res:binding[@name='uri']/res:uri" />
                <xsl:variable name="response" select="/res:sparql/res:results/res:result/res:binding[res:uri=$annotationUri and @name='describesObject']/parent::*" />
                <xsl:if test="$response">
                
                <h2>Response Annotations:</h2>
                <xsl:for-each select="$response">
                <xsl:sort select="res:binding[@name='date']" order="descending" />
                <ul>
                    <li>
                        <table>
                            <tr>
                                <td><b>Created By:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='createdBy']/res:literal" /></td>
                                <td><b>Created On:</b></td><td><xsl:value-of select="res:binding[@name='date']/res:literal" /></td>
                            </tr>
                            <tr>
                                <td><b>Opinion:</b></td><td style="padding-right:35px;"><xsl:value-of select="res:binding[@name='opinionText']/res:literal" /></td>
                                <td><b></b></td><td></td>
                            </tr>
                        </table>
                        
                        <p><b>Evidence for Assertion:</b> <xsl:value-of select="res:binding[@name='evidence']/res:literal" /></p>
                        <p><b>Motivation for Assertion:</b> <xsl:value-of select="res:binding[@name='motivation']/res:literal" /></p>
                    </li>
                </ul>
                </xsl:for-each>
                </xsl:if>
            </li>
            </xsl:if>
            </xsl:for-each>
        </ol>
	</xsl:template>
</xsl:stylesheet>