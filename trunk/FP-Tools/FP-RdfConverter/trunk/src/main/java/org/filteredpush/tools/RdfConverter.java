/* Copyright (C) 2010-2012 President and Fellows of Harvard College
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
 */
package org.filteredpush.tools;


import java.io.File;
import java.io.FileNotFoundException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/** 
 * Simple utility for converting rdf/xml to n3 using the Jena library facilities
 * 
 * @author dlowery
 *
 * $Id$
 */
public class RdfConverter {
	
	/** 
	 * Invoke this utility from the shell with the args listed in usage
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 1) {
			System.out.println("Usage: java RdfConverter insert_identification.rdfxml");
		} else {
			Model model = ModelFactory.createDefaultModel();
			File file = new File(args[0]);
			model.read("file://" + file.getAbsolutePath(), "N3");
			model.removeNsPrefix("xml");
			model.write(System.out, "RDF/XML");
		}
	}
}
