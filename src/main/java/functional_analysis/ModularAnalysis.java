package functional_analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;


import Models.MModule;
import NGModularity.Network;
import getuserdata.DataMeetModel;
import pojo.RowEmpcpd;
import resources.Constants;
import smile.stat.distribution.GammaDistribution;
import utils.ReservoirSampling;

public class ModularAnalysis {
	private DataMeetModel mixedNetwork;
	private Graph<String, DefaultEdge> network;
	Map<String,String> paradict;
	private List <String> ref_featurelist;
	private List <String> significant_features;
	private List <RowEmpcpd> significant_Trios;
	//private List<MModule> modules;
	private final static Logger LOGGER = Logger.getLogger(ModularAnalysis.class.getName());
	private List<Double> permutationScores;
	private List<MModule> modulesFromSignificantFeaures;
	private List<MModule> topModules;
	

	public ModularAnalysis(DataMeetModel mixedNetwork) {
		this.mixedNetwork=mixedNetwork;
		this.network=mixedNetwork.getModel().getNetwork();
		this.paradict=mixedNetwork.getData().getParadict();
	//	this.modules= new ArrayList<MModule>();
		
		this.ref_featurelist=this.mixedNetwork.getMzrows();
		this.significant_features=this.mixedNetwork.getSignificant_features();
		this.significant_Trios= this.mixedNetwork.getTrioList();			
	}

	public void dispatch() {
		
		String s = "\nModular Analysis, using %d permutations ... " + this.paradict.get("permutation");
		LOGGER.info(s);
		
		this.modulesFromSignificantFeaures= this.runAnalysisReal();
		this.permutationScores=this.doPermutations(Integer.parseInt(this.paradict.get("permutation")));
		System.out.println("Modular Analysis is done");
		this.rankSignificance();
			
		
	}
	
	List<Double> doPermutations(int numOfPermutations) {
		List<Double>permutationScores = new ArrayList<Double>();
		int numSignificantFeatures=this.significant_features.size();
		List<MModule> randomModules;
		for(int i=0;i<numOfPermutations;i++) {
			System.out.print(" " + (i+1));
			
			List<RowEmpcpd> randomTrios=this.mixedNetwork.batch_rowindex_EmpCpd_Cpd(ReservoirSampling.selectKItems(this.ref_featurelist, numSignificantFeatures));
			
			// Did not get the 0 logic in the python code
			
			randomModules=this.findModules(randomTrios);
			for(MModule module: randomModules) {
				permutationScores.add(module.getActivityScore());
			}

		}
		return permutationScores;	
	}
	
	List<MModule> runAnalysisReal(){
		return this.findModules(this.significant_Trios);
	}
	
	@SuppressWarnings("unchecked")
	public List<MModule> findModules(List <RowEmpcpd> significant_Trios) {

		List<String> seeds = new ArrayList<String>();
		for (RowEmpcpd row : significant_Trios) {
			seeds.add(row.getCompound());
		}

		List<MModule> modules = new ArrayList<MModule>();
		List<MModule> modules2 = new ArrayList<MModule>();
		List<String> module_nodes_list = new ArrayList<String>();
		List<List<String>> edges;
		List<List<String>> temp_edges = new ArrayList<List<String>>();
		Graph<String, DefaultEdge> newNetwork = null;
		
//		//Getting list of compds from the triolist to create Mmodule
//		List<String> cpds = new ArrayList<String>();
//		
//		for (RowEmpcpd rec :significant_Trios) {
//			cpds.add(rec.getCompound());
//		}
		
		try {
		
		for (int i = 0; i < Constants.SEARCH_STEPS; i++) {
			// Problem the number of edges coming in python code are 400 more
			edges = getgraphedges(seeds);;
			if (i == 0) {
				// Step 0 to count the edges connected to the seeds
				for (List<String> li : edges) {
					if (seeds.contains(li.get(0)) && seeds.contains(li.get(1))) {
						temp_edges.add(li);
					}}
					newNetwork = build_network(temp_edges);
		//			System.out.println(temp_edges);
					temp_edges.clear();			
			} else {
				newNetwork = build_network(edges);
				seeds = new ArrayList<String>(newNetwork.vertexSet());

			}
			
			// Code to generate Connected Components of a graph
//			 ConnectivityInspector<String, DefaultEdge> scAlg =new ConnectivityInspector<>(newNetwork);
//			 List<Set<String>> stronglyConnectedSubgraphs =scAlg.connectedSets();
//			        for (int i = 0; i < stronglyConnectedSubgraphs.size(); i++) {
//			            if(3<stronglyConnectedSubgraphs.get(i).size() && stronglyConnectedSubgraphs.get(i).size()<Constants.MODULE_SIZE_LIMIT) {
//			            	
//			            }
//			        }

			// Code to generate Connected Components of a graph
			BiconnectivityInspector<String, DefaultEdge> scAlg = new BiconnectivityInspector<>(newNetwork);
			Set<Graph<String, DefaultEdge>> stronglyConnectedSubgraphs = scAlg.getConnectedComponents();
			for (Graph<String,DefaultEdge> g1 : stronglyConnectedSubgraphs) {
				if(3 < g1.vertexSet().size() && g1.vertexSet().size() < Constants.MODULE_SIZE_LIMIT) {
	            	MModule modul= new MModule(this.network,g1,significant_Trios);
	            	modules.add(modul);
	            }
			}
		}
		}catch(Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		
		
		for(MModule sub:modules) {
			if(sub.getGraph().vertexSet().size()>5) {
				for(Graph<String,DefaultEdge> graph: this.splitModules(sub.getGraph())) {
					modules2.add(new MModule(this.network, graph, significant_Trios));
				}
			}
		}
		
		List<MModule> newList = new ArrayList<MModule>();
		modules2.addAll(modules);
		for(MModule m: modules2) {
			if(m.getGraph().vertexSet().size()>3 && !module_nodes_list.contains(m.getNodeStr())) {
				newList.add(m);
				module_nodes_list.add(m.getNodeStr());
			}
		}
		
		return newList;

	}
	
	
	List<List<String>> getgraphedges(List<String> vertices){
		List<List<String>> result = new ArrayList <List<String>>();
		// Maybe the edges should be counted even if they are connected to just one vertex
		
		//approach 2 
		for(DefaultEdge ed: this.network.edgeSet()) {
			if(vertices.contains(this.network.getEdgeSource(ed)) || vertices.contains(this.network.getEdgeTarget(ed))){
				result.add(new ArrayList<String>(Arrays.asList(this.network.getEdgeSource(ed),this.network.getEdgeTarget(ed))));
			}
		}
		
		
//		//approach 3
//		Set<DefaultEdge> edgst= new HashSet<DefaultEdge>();
//		for(String v: vertices) {
//			edgst.addAll(this.network.edgesOf(v));
//		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	Graph<String, DefaultEdge> build_network(List<List<String>> edges) {

		Graph<String, DefaultEdge> result = new DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge.class);

		for (List<String> list : edges) {
			for (String node : list) {
				result.addVertex(node);
			}
			result.addEdge(list.get(0), list.get(1));
		}
		return result;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	List<Graph> splitModules(Graph<String, DefaultEdge> g) {
		/*
		 * return nx.graph instance after splitting the input graph by Newman's spectral
		 * split method Only modules more than 3 nodes are considered as good small
		 * modules should have been generated in 1st connecting step.
		 */
		
		List<Graph> result = new ArrayList<Graph>();
		Network  network = new Network();
		network.copyFromGraph(g);
		try {
			List<List<String>> specSplitRes=network.specsplit();
			for(List<String> nodes: specSplitRes) {
				if(nodes.size()>3) {
					result.add(new AsSubgraph<String, DefaultEdge>(g,new HashSet(nodes)));
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}
	
	void rankSignificance() {
		LOGGER.info("\nNull distribution is estimated on " + this.permutationScores.size() +" random modules");
		LOGGER.info("User data yield " + this.modulesFromSignificantFeaures.size() + " network modules");
		
		if(this.paradict.get("modeling").equalsIgnoreCase("gamma")) {
			// Need a gamma function here
			GammaDistribution gammaDistribution= new GammaDistribution(this.giveDoubleArray(this.permutationScores));
			for (MModule mod: this.modulesFromSignificantFeaures) {
				mod.setpValue((1 - gammaDistribution.cdf(mod.getActivityScore())));
			}
		}else {
			for(MModule mod: this.modulesFromSignificantFeaures) {
				mod.setpValue(calculatePValue(mod.getActivityScore(),this.permutationScores));
			}
			
		}
		
		 this.topModules= new ArrayList<MModule>();
		for(MModule module: this.modulesFromSignificantFeaures) {
			if(module.getpValue() < Constants.SIGNIFICANCE_CUTOFF) {
				this.topModules.add(module);
			}
		}
		
		// TODO Sorting has been done on top modules in the python code . Check why 
		System.out.println("total number of top modules: "+ this.topModules.size());
		
	}
	
	
	double[] giveDoubleArray(List<Double> input) {
		double[] result = new double [input.size()];
		
		for(int i=0;i<input.size(); i++) {
			result[i]=input.get(i);
		}
		return result;
	}
	
	double calculatePValue(double x, List<Double> record) {
		
		List<Double> total_scores= new ArrayList<Double>();
		total_scores.add(x);
		total_scores.addAll(record);
		Collections.sort(total_scores, Collections.reverseOrder());
		double d = total_scores.size();
		return (total_scores.indexOf(x) +1/d);

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



}
