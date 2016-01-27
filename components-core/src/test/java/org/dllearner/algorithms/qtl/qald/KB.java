/**
 * Copyright (C) 2007 - 2016, Jens Lehmann
 *
 * This file is part of DL-Learner.
 *
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.dllearner.algorithms.qtl.qald;

import java.util.List;
import java.util.Map;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.kb.SparqlEndpointKS;

import com.hp.hpl.jena.shared.PrefixMapping;

class KB {
	
	static String id;
	SparqlEndpointKS ks;
	AbstractReasonerComponent reasoner;
	Map<String, String> prefixes;
	List<String> questionFiles;
	String baseIRI;
	PrefixMapping prefixMapping;
	
	public KB(){};

	public KB(SparqlEndpointKS ks, AbstractReasonerComponent reasoner, Map<String, String> prefixes,
			List<String> questionFiles) {
		this.ks = ks;
		this.reasoner = reasoner;
		this.prefixes = prefixes;
		this.questionFiles = questionFiles;
	}
}