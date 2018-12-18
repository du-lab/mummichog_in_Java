package functional_analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;

import getuserdata.DataMeetModel;
import getuserdata.EmpiricalCompound;
import net.maizegenetics.analysis.imputation.EmissionProbability;
import pojo.RowEmpcpd;
import resources.Constants;

public class ActivityNetwork {

//	Tally cpds responsible for significant pathways and modules,
//  and build an acitvity network to represent top story in the data.
//  Remove singletons in network. Try no more than 3 steps. AN can get too big or too small.

	private DataMeetModel mixedNetwork;
	private Graph<String, DefaultEdge> network;
	
	private final static Logger LOGGER = Logger.getLogger(ActivityNetwork.class.getName());
	
	public ActivityNetwork(List<RowEmpcpd> hitTriosList,DataMeetModel mixedNetwork) {
		mixedNetwork.setHitTriosList(hitTriosList);
		this.mixedNetwork=mixedNetwork;
		this.network=mixedNetwork.getModel().getNetwork();
		List<String> nodes = new ArrayList <String>();
		for(RowEmpcpd row : hitTriosList) {
			nodes.add(row.getCompound());
		}
		
	}
	
	public Graph<String,DefaultEdge> build_Activity_Network(List<String> nodes) {
		
//		 Get a network with good connections in no more than 3 steps.
//	     No modularity requirement for 1 step connected nodes.
		
		int expected_size=10;
		double cutoff_ave_conn =0.5;
		
		Graph <String,DefaultEdge> an = new AsSubgraph<String, DefaultEdge>(this.mixedNetwork.getModel().getNetwork(),new HashSet<String>(nodes));
		
//		if(nodes != null && nodes.size()>0)
//		{
//			Graph subG1=  getLargestSubgraph(an);
//			if (subG1.vertexSet().size()> expected_size) {
//				LOGGER.info("Activity network was connected in 1 step.");
//				return subG1;
//			}
//			else {
//				an.edgeSet 
//			}
//			
//			
//			
//			
//			
//			
//		}
		return an;

	}
	
	public Graph<String,DefaultEdge> getLargestSubgraph(Graph<String,DefaultEdge> an) {
	
     //   connected_component_subgraphs likely to return sorted subgraphs. Just to be sure here.
		BiconnectivityInspector<String, DefaultEdge> scAlg = new BiconnectivityInspector<>(an);
		Set<Graph<String, DefaultEdge>> stronglyConnectedSubgraphs = scAlg.getConnectedComponents();
		List <Graph<String,DefaultEdge>> connectedComponents = new ArrayList<Graph<String,DefaultEdge>>(stronglyConnectedSubgraphs);
		Collections.sort(connectedComponents,(a, b) -> Integer.compare(a.vertexSet().size(), b.vertexSet().size()));
		//Instead of reverse sorting, returning the last element of the list
		
		return connectedComponents.get(connectedComponents.size()-1);
		
	}
	
	
	
	
}
