/* Class representing SpeciemnRecord.
 *
 * Copyright (c) 2008 The Regents of the University of California.
 * All rights reserved.
 *
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, provided that the
 * above copyright notice and the following two paragraphs appear in
 * all copies of this software.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 * FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 * ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN
 * IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 * PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */
 
package fp.util;

import java.util.Map;

public class CurationCommentType {

    String status;
    String details;
    String source;

	public CurationCommentType(Map<String, String> fieldMap) {
        this.status = fieldMap.get("status");
        this.details = fieldMap.get("details");
        this.source = fieldMap.get("source");
	}

    public CurationCommentType(String status, String details, String source) {
        this.status = status;
        this.details = details;
        this.source = source;
    }

    public String getStatus() {
		return this.status;
	}
	
	public String getDetails() {
		return this.details;
	}
	
	public String getSource() {
		return this.source;
	}	


	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(status);
        sb.append(",");
        sb.append(details);
        sb.append(",");
        sb.append(source);
        sb.append("]");
	    return sb.toString();
	}

	private static final long serialVersionUID = 1L;	
}
