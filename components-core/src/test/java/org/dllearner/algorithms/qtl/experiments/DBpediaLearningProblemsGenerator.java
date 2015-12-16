/**
 * 
 */
package org.dllearner.algorithms.qtl.experiments;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.Var;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.jena.riot.Lang;
import org.dllearner.kb.SparqlEndpointKS;
import org.dllearner.kb.sparql.ConciseBoundedDescriptionGenerator;
import org.dllearner.kb.sparql.ConciseBoundedDescriptionGeneratorImpl;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.reasoning.SPARQLReasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

/**
 * Generate learning problems based on the DBpedia knowledge base.
 * @author Lorenz Buehmann
 *
 */
public class DBpediaLearningProblemsGenerator {
	
	SparqlEndpoint endpoint;
	SparqlEndpointKS ks;
	SPARQLReasoner reasoner;
	ConciseBoundedDescriptionGenerator cbdGen;
	
	File dataDir;
	private Model schema;
	private File benchmarkDirectory;
	private int threadCount;

	private static String DBPEDIA_ONTOLOGY_URL = "http://downloads.dbpedia.org/2014/dbpedia_2014.owl.bz2";
	
	public DBpediaLearningProblemsGenerator(SparqlEndpoint endpoint, File benchmarkDirectory, int threadCount) throws Exception {
		this.endpoint = endpoint;
		this.benchmarkDirectory = benchmarkDirectory;
		this.threadCount = threadCount;
		
		ks = new SparqlEndpointKS(endpoint);
		ks.setCacheDir(new File(benchmarkDirectory, "cache").getPath() + ";mv_store=false");
		ks.setPageSize(50000);
		ks.setUseCache(true);
		ks.setQueryDelay(100);
		ks.init();
		
		reasoner = new SPARQLReasoner(ks);
		reasoner.init();
		
		cbdGen = new ConciseBoundedDescriptionGeneratorImpl(ks.getQueryExecutionFactory());
		
		dataDir = new File(benchmarkDirectory, "data/dbpedia/");
		dataDir.mkdirs();
		
		schema = ModelFactory.createDefaultModel();
		try(InputStream is = new BZip2CompressorInputStream(new URL(DBPEDIA_ONTOLOGY_URL).openStream())){
			schema.read(is, null, Lang.RDFXML.getName());
		}

//		schema.read(new FileInputStream(new File(benchmarkDirectory, "dbpedia_2014.owl")), null, "RDF/XML");
	}
	
	private Set<OWLClass> getClasses() {
		return reasoner.getOWLClasses("http://dbpedia.org/ontology");
//		return reasoner.getMostSpecificClasses();
	}
	
	
	public void generateBenchmark(int nrOfSPARQLQueries, final int minDepth, final int maxDepth, int minNrOfExamples) {
		Collection<OWLClass> classes = getClasses();
		ArrayList<OWLClass> classesList = new ArrayList<>(classes);
		Collections.shuffle(classesList, new Random(123));
		classes = classesList;
		classes = Sets.<OWLClass>newHashSet(new OWLClassImpl(IRI.create("http://dbpedia.org/ontology/Artist")));
		

		
//		ExecutorService tp = Executors.newFixedThreadPool(threadCount);
		List<Future<Path>> futures = new ArrayList<Future<Path>>();
		List<Path> allPaths = new ArrayList<Path>();
		
		ThreadPoolExecutor tp = new CustomFutureReturningExecutor(
				threadCount, threadCount,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(classes.size(), true));
				
		JDKRandomGenerator rndGen = new JDKRandomGenerator();
		rndGen.setSeed(123);
		
		int nrOfQueriesPerDepth = nrOfSPARQLQueries / (maxDepth - minDepth + 1);
		
		// for each depth <= maxDepth
		for(int depth = minDepth; depth <= maxDepth; depth++) {
			System.out.println("Generating " + nrOfQueriesPerDepth + " queries for depth " + depth);

			Iterator<OWLClass> iterator = classes.iterator();

			List<Path> pathsForDepth = new ArrayList<Path>();
			// generate paths of depths <= maxDepth
			while(pathsForDepth.size() < nrOfQueriesPerDepth && iterator.hasNext()) {
				
				// pick next class
				OWLClass cls = iterator.next();
				
//				int depth = rndGen.nextInt(maxDepth) + 1;
				
				Future<Path> future = tp.submit(new PathDetectionTask(dataDir, ks, schema, cls, depth, minNrOfExamples));
//				futures.add(future);
				try {
					 Path path = future.get();
			    	  if(path != null) {
			    		  paths.add(path);
			    	  }
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			allPaths.addAll(paths);
		}
		
		
//		for (Future<Path> future : futures) {
//		      try {
//		    	  Path path = future.get();
//		    	  if(path != null) {
//		    		  paths.add(path);
//		    	  }
//		    	  if(paths.size() == nrOfSPARQLQueries) {
//			    	  System.err.println("Benchmark generation finished. Stopping all running threads.");
//			    	  tp.shutdownNow();
//			      }
//			} catch (InterruptedException | ExecutionException e) {
//				e.printStackTrace();
//			}
//		      if(paths.size() == nrOfSPARQLQueries) {
//		    	  System.err.println("Benchmark generation finished. Stopping all running threads.");
//		    	  tp.shutdownNow();
//		      }
//		}
		
		tp.shutdownNow();
		try {
			tp.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			tp.awaitTermination(1, TimeUnit.DAYS);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		// write queries to disk
		String queries = "";
		int i = 1;
		for (Path path : paths) {
			System.out.println(i++ + ":" + path);
			queries += path.asSPARQLQuery(Var.alloc("s")) + "\n";
		}
		File file = new File(benchmarkDirectory, "queries_" + nrOfSPARQLQueries + "_" + minDepth + "-" + maxDepth + "_" + minNrOfExamples+ ".txt");
		try {
			Files.write(queries, file, Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	class CustomFutureReturningExecutor extends ThreadPoolExecutor {

	    public CustomFutureReturningExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue) {
	        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	    }

	    @Override
	    protected RunnableFuture newTaskFor(Callable callable) {
	        if (callable instanceof PathDetectionTask) {
	            return ((PathDetectionTask) callable).newTask();
	        } else {
	            return super.newTaskFor(callable); // A regular Callable, delegate to parent
	        }
	    }
	}

	
	public static void main(String[] args) throws Exception {
		File benchmarkBaseDirectory = new File(args[0]);
		int threadCount = Integer.parseInt(args[1]);
		int nrOfSPARQLQueries = Integer.parseInt(args[2]);
		int minDepth = Integer.parseInt(args[3]);
		int maxDepth = Integer.parseInt(args[4]);
		int minNrOfExamples = Integer.parseInt(args[5]);

//		SparqlEndpoint endpoint = SparqlEndpoint.create("http://dbpedia.org/sparql", "http://dbpedia.org");
		SparqlEndpoint endpoint = SparqlEndpoint.create("http://sake.informatik.uni-leipzig.de:8890/sparql", "http://dbpedia.org");

		DBpediaLearningProblemsGenerator generator = new DBpediaLearningProblemsGenerator(endpoint, benchmarkBaseDirectory, threadCount);
		generator.generateBenchmark(nrOfSPARQLQueries, minDepth, maxDepth, minNrOfExamples);
	}
	

}
