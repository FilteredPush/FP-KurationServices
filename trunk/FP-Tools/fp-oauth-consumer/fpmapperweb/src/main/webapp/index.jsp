<HTML>
<body>
	<jsp:include page="/banner.jsp" />
	<a href="Specify">Say Hello to Specify</a>
	<br />
	<br />
    <table><tr>
	<td><form action="Specify">
		<select name="type">
			<option value="dwc_Event">DwC Event</option>
			<option value="dwc_Occurrence" selected>DwC Occurrence</option>
			<option value="dwc_Identification">DwC Identification</option>
			<option value="dcterms_Location">DwC Location</option>
			<option value="dwc_Taxon">DwC Taxon</option>
            <option value="gci_Botanist">GCI Botanist</option>
		</select> <input type="text" name="id" value="234481" /> <input type="hidden"
			name="get" /> <input type="submit" value="Get" />
	</form></td>
    <td>For GCI Botanist, try 29004</td>
    </tr></table>
	<table>
		<tr>
			<td><form action="Specify">
					<select name="type">
						<option value="dwc_Event">DwC Event</option>
						<option value="dwc_Occurrence">DwC Occurrence</option>
						<option value="dwc_Identification" selected>DwC Identification</option>
						<option value="dcterms_Location">DwC Location</option>
						<option value="dwc_Taxon">DwC Taxon</option>
                        <option value="gci_Botanist">GCI Botanist</option>
					</select> <input type="text" name="object" size="40"
						value="{'dwc_occurrenceID'='202','dwc_taxonID'='59641','gci_botanistID'='1','dwc_identificationRemarks'='FP Mapper','dwc_dateIdentified'='2012-05-24'}" /> <input type="hidden"
						name="add" /> <input type="submit" value="Add" />
				</form></td>
			<td>
            For GCI Botanist, try {gci_labelName="J. A. Macklin"}	
			</td>
		</tr>
	</table>
    <table>
        <tr>
            <td><form action="Specify">
                    <select name="type">
                        <option value="dwc_Event">DwC Event</option>
                        <option value="dwc_Occurrence">DwC Occurrence</option>
                        <option value="dwc_Identification" selected>DwC Identification</option>
                        <option value="dcterms_Location">DwC Location</option>
                        <option value="dwc_Taxon">DwC Taxon</option>
                        <option value="gci_Botanist">GCI Botanist</option>
                    </select> <input type="text" name="object" size="40"
                        value="{'dwc_occurrenceID'='202','dwc_taxonID'='59641','gci_botanistID'='1','dwc_identificationRemarks'='FP Mapper','dwc_dateIdentified'='2012-05-24'}" /> <input type="hidden"
                        name="update" /> <input type="submit" value="Update" />
                </form></td>
            <td>
            For GCI Botanist, try {gci_botanistID="94812", gci_datesType="birth/death", gci_startDate="1960-02-1"}             
            </td>
        </tr>
    </table>
	<table>
		<tr>
			<td><form action="Specify">
					<select name="type">
						<option value="dwc_Event">DwC Event</option>
						<option value="dwc_Occurrence" selected>DwC Occurrence</option>
						<option value="dwc_Identification">DwC Identification</option>
						<option value="dcterms_Location">DwC Location</option>
						<option value="dwc_Taxon">DwC Taxon</option>
                        <option value="gci_Botanist">GCI Botanist</option>
					</select> <input type="text" name="object" size="40"
						value="{dwc_catalogNumber=00000673}" /> <input type="hidden"
						name="find" /> <input type="submit" value="Find" />
				</form></td>
			<td>
				<table>
					<tr>
						<td>For DwC Event, try: {dwc_locality=Barretts}</td>
					</tr>
					<tr>
						<td>For DwC Occurrence, try:
							{dwc_collectionCode=A,dwc_catalogNumber=00000673}</td>
					</tr>
					<!-- <tr>
						<td>For DwC Identification, try : {dwc_identifiedBy="W. J.
							Crins"}</td>
					</tr> -->
					<tr>
						<td>For DwC Locality, try: {dwc_higherGeography=Middlesex}</td>
					</tr>
					<tr>
						<td>For DwC Taxon, try:
							{dwc_taxonRank=Genus,dwc_scientificName=Crataegus}</td>
					</tr>
                    <tr>
                        <td>For GCI Botanist, try:
                            {gci_labelName=Khairuddin}</td>
                    </tr>
				</table>
			</td>
		</tr>
	</table>
	<br />
		<i>ALL TERMS MATCHED EXACTLY.</i>
	<br />
	<br />
	<table>
		<tr>
			<th>DwC Type</th>
			<th>DwC Fields Available for "Find"</th>
		</tr>
		<tr>
			<td>DwC Event</td>
			<td><ul>
					<li>dwc_eventID</li>
					<li>dwc_eventDate <i>(in "YYYY-MM-DD" format; note that inexact
						dates are signified by using "01" for month and/or day)</i></li>
					<li>dwc_higherGeographyID <i>(the smallest geographical unit
						containing the locality, by id)</i></li>
					<li>dwc_higherGeography <i>(the smallest geographical unit
						containing the locality, by name)</i></li>
					<li>dwc_locationID</li>
					<li>dwc_locality</li>
					<li>dwc_habitat <i>(try "Swamp" or "Forest")</i></li>
				</ul></td>
		</tr>
		<tr>
			<td>DwC Occurrence</td>
			<td><ul>
					<li>dwc_occurrenceID</li>
					<li>dwc_institutionCode</li>
					<li>dwc_collectionCode</li>
					<li>dwc_catalogNumber</li>
					<li>dwc_reproductiveCondition</li>
					<li>dwc_sex</li>
					<li>dwc_habitat <i>(try "Swamp" or "Forest")</i></li>
					<li>dwc_locality</li>
					<li>dwc_eventID</li>
				</ul></td>
		</tr>
		<tr>
			<td>DwC Identification</td>
			<td><ul>
					<li>dwc_identificationID</li>
					<li>dwc_occurrenceID</li>
					<li>dwc_taxonID</li>
					<li>dwc_taxonRank <i>(e.g. Species, Genus, Subspecies, Variety)</i></li>
					<li>dwc_identifiedBy</li>
					<li>dwc_identificationQualifier</li>
					<li>dwc_identificationRemarks</li>
					<li>dwc_dateIdentified</li>
					<li>dwc_typeStatus</li>
					<li>dwc_scientificName</li>
					<li>dwc_scientificNameAuthorship</li>
				</ul></td>
		</tr>
		<tr>
			<td>DwC Location</td>
			<td><ul>
					<li>dwc_higherGeographyID <i>(the smallest geographical unit
						containing the locality, by id)</i></li>
					<li>dwc_higherGeography <i>(the smallest geographical unit
						containing the locality, by name)</i></li>
					<li>dwc_locationID</li>
					<li>dwc_locality</li>
				</ul></td>
		</tr>
		<tr>
			<td>DwC Taxon</td>
			<td><ul>
					<li>dwc_taxonID</li>
					<li>dwc_higherTaxonID <i>(the immediate parent taxon, by id)</i></li>
					<li>dwc_higherTaxon <i>(the immediate parent taxon, by name)</i></li>
					<li>dwc_taxonRank <i>(e.g. Species, Genus, Subspecies, Variety)</i></li>
					<li>dwc_scientificName</li>
					<li>dwc_scientificNameAuthorship</li>
				</ul></td>
		</tr>
	</table>
	<a href="Specify?remove">Remove</a>
	<br />
	<br />
	<a href="Reset">Reset</a>
	<br />
</body>
</HTML>
