package org.dllearner.algorithms.qtl.experiments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ComparisonChain;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDAbstractDateTimeType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_LogicalOr;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

public class VirtuosoUtils {
	
	/**
	 * Returns a rewritten query which tried to workaround issues with xsd:date
	 * literals and applies matching on the string value instead, i.e. a triple pattern
	 * <code>?s :p "2002-09-24"^xsd:string</code> will be converted to 
	 * <code>?s :p ?date1 . FILTER(STR(?date1) = "2002-09-24")</code>.
	 * @param query the query to rewrite
	 * @return the rewritten query
	 */
	public static Query rewriteForVirtuosoDateLiteralBug(Query query){
		final Query copy = QueryFactory.create(query);
		final Element queryPattern = copy.getQueryPattern();
		final List<ElementFilter> filters = new ArrayList<>();
		ElementWalker.walk(queryPattern, new ElementVisitorBase() {
			
			int cnt = 0;
			
			@Override
			public void visit(ElementTriplesBlock el) {
				Set<Triple> newTriplePatterns = new TreeSet<>(new Comparator<Triple>() {
					@Override
					public int compare(Triple o1, Triple o2) {
						return ComparisonChain.start().compare(o1.getSubject().toString(), o2.getSubject().toString())
								.compare(o1.getPredicate().toString(), o2.getPredicate().toString())
								.compare(o1.getObject().toString(), o2.getObject().toString()).result();
					}
				});

				Iterator<Triple> iterator = el.patternElts();
				while (iterator.hasNext()) {
					Triple tp = iterator.next();

					if (tp.getObject().isLiteral()) {
						RDFDatatype dt = tp.getObject().getLiteralDatatype();
						if (dt != null && dt instanceof XSDAbstractDateTimeType) {
							iterator.remove();
							// new triple pattern <s p ?var> 
							Node objectVar = NodeFactory.createVariable("date" + cnt++);
							newTriplePatterns.add(Triple.create(tp.getSubject(), tp.getPredicate(), objectVar));

							String lit = tp.getObject().getLiteralLexicalForm();
							Object literalValue = tp.getObject().getLiteralValue();
							Expr filterExpr = new E_Equals(new E_Str(new ExprVar(objectVar)), NodeValue.makeString(lit));
							if (literalValue instanceof XSDDateTime) {
								Calendar calendar = ((XSDDateTime) literalValue).asCalendar();
								Date date = new Date(calendar.getTimeInMillis() + TimeUnit.HOURS.toMillis(2));
								SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
								String inActiveDate = format1.format(date);
								filterExpr = new E_LogicalOr(filterExpr, new E_Equals(
										new E_Str(new ExprVar(objectVar)), NodeValue.makeString(inActiveDate)));
							}
							ElementFilter filter = new ElementFilter(filterExpr);
							filters.add(filter);
						}
					}
				}
				
				for (Triple tp : newTriplePatterns) {
					el.addTriple(tp);
				}
				
				for (ElementFilter filter : filters) {
					((ElementGroup)queryPattern).addElementFilter(filter);
				}
			}
			
			@Override
			public void visit(ElementPathBlock el) {
				Set<Triple> newTriplePatterns = new TreeSet<>(new Comparator<Triple>() {
					@Override
					public int compare(Triple o1, Triple o2) {
						return ComparisonChain.start().compare(o1.getSubject().toString(), o2.getSubject().toString())
								.compare(o1.getPredicate().toString(), o2.getPredicate().toString())
								.compare(o1.getObject().toString(), o2.getObject().toString()).result();
					}
				});

				Iterator<TriplePath> iterator = el.patternElts();
				while (iterator.hasNext()) {
					Triple tp = iterator.next().asTriple();

					if (tp.getObject().isLiteral()) {
						RDFDatatype dt = tp.getObject().getLiteralDatatype();
						if (dt != null && dt instanceof XSDAbstractDateTimeType) {
							iterator.remove();
							// new triple pattern <s p ?var> 
							Node objectVar = NodeFactory.createVariable("date" + cnt++);
							newTriplePatterns.add(Triple.create(tp.getSubject(), tp.getPredicate(), objectVar));

							String lit = tp.getObject().getLiteralLexicalForm();
							Object literalValue = tp.getObject().getLiteralValue();
							Expr filterExpr = new E_Equals(new E_Str(new ExprVar(objectVar)), NodeValue.makeString(lit));
							if (literalValue instanceof XSDDateTime) {
								Calendar calendar = ((XSDDateTime) literalValue).asCalendar();
								Date date = new Date(calendar.getTimeInMillis() + TimeUnit.HOURS.toMillis(2));
								SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
								String inActiveDate = format1.format(date);
								filterExpr = new E_LogicalOr(filterExpr, new E_Equals(
										new E_Str(new ExprVar(objectVar)), NodeValue.makeString(inActiveDate)));
							}
							ElementFilter filter = new ElementFilter(filterExpr);
							filters.add(filter);
						}

					}
				}
				
				for (Triple tp : newTriplePatterns) {
					el.addTriple(tp);
				}
				
			}
		});
		for (ElementFilter filter : filters) {
			((ElementGroup)queryPattern).addElementFilter(filter);
		}
		return copy;
	}

}
