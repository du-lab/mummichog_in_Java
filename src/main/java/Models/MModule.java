package Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class MModule {

	private Graph<String, DefaultEdge> network;
	private int num_ref_edges;
	private int num_ref_nodes;
	private Graph<String, DefaultEdge> graph;
	private double activityScore;
	private double QValue;
	private int n_seeds;
	private String nodeStr;
	private double pValue;

	public MModule(Graph<String, DefaultEdge> network, Graph<String, DefaultEdge> subgraph, List<String> trioList) {
		// TrioList is the third element lost of the original triolist.
		this.network = network;
		this.graph = subgraph;
		this.shave(trioList);
		this.num_ref_nodes = this.network.vertexSet().size();
		this.num_ref_edges = this.network.edgeSet().size();
		this.n_seeds = trioList.size();
		// TODO implement shave yet
		this.activityScore = activity_score(trioList, get_num_EmpCpd(trioList));
		this.setNodeStr(makeNodeStr());
	}
	
	
	String makeNodeStr() {
		StringBuilder result = new StringBuilder();
		List<String>nodeSet= new ArrayList<String>();
		nodeSet.addAll(new ArrayList<String>(this.graph.vertexSet()));
		Collections.sort(nodeSet);
		for(String node: nodeSet) {
			result.append(node);
		}
		return result.toString();	
	}

	int get_num_EmpCpd(List<String> trioList) {

		int count = 0;

		for (String node : this.graph.vertexSet()) {
			if (trioList.contains(node)) {
				count++;
			}
		}

		return count;
	}

	double activity_score(List<String> seed_cpds, int num_EmpCpd) {

		Float nm = (float) this.graph.vertexSet().size();

		if (nm > 0) {
			this.compute_modularity();
			return Math.sqrt((this.n_seeds / nm)) * this.QValue * (num_EmpCpd / nm) * 100;

		} else {
			return 0;
		}
	}

	void compute_modularity() {

		int m = this.num_ref_edges;
		double expected = 0;
		for (String n1 : this.graph.vertexSet()) {
			for (String n2 : this.graph.vertexSet()) {
				if (!n1.equalsIgnoreCase(n2)) {
					expected += this.network.degreeOf(n1) * this.network.degreeOf(n2);
				}
			}
		}
		expected = (expected) / (4.0 * m);
		this.QValue = (this.graph.edgeSet().size() - expected) / m;

	}

	void test_compute_modularity() {

		int m = this.graph.edgeSet().size();
		double expected = 0;
		for (String n1 : this.graph.vertexSet()) {
			expected += this.network.degreeOf(n1);
		}
		this.QValue = 2 * m * (Math.sqrt(this.graph.vertexSet().size())) / expected;
	}

	// Run this function to confirm the behavior.
	void shave(List<String> seed_cpds) {

		List<String> nonSeed = new ArrayList<String>();
		List<String> excessive = new ArrayList<String>();
		for (String node : this.graph.vertexSet()) {
			if (!seed_cpds.contains(node)) {
				nonSeed.add(node);
			}
		}

		for (String node : nonSeed) {
			if (this.graph.degreeOf(node) == 1) {
				excessive.add(node);
			}
		}

		while (excessive.size() > 0) {

			for (String n1 : excessive) {
				this.graph.removeVertex(n1);
			}
			excessive.clear();
			nonSeed.clear();
			for (String node : this.graph.vertexSet()) {
				if (!seed_cpds.contains(node)) {
					nonSeed.add(node);
				}
			}

			for (String node : nonSeed) {
				if (this.graph.degreeOf(node) == 1) {
					excessive.add(node);
				}
			}
		}
	}

	// Yet to complete this function. Need to understand what's happening.
//		void export_network_txt( met_model, filename) {
//	        
//	        String s = "SOURCE\tTARGET\tENZYMES\n";
//	        for (DefaultEdge e : this.graph.edgeset()) {
//	            s += e[0] + '\t' + e[1] + '\t' + met_model.edge2enzyme.get(','.join(sorted(e)), '') + '\n';
//	        }
//	        out = open(filename, 'w')
//	        out.write(s)
//	        out.close()
//		}

	public Graph<String, DefaultEdge> getNetwork() {
		return network;
	}

	public void setNetwork(Graph<String, DefaultEdge> network) {
		this.network = network;
	}

	public int getNum_ref_edges() {
		return num_ref_edges;
	}

	public void setNum_ref_edges(int num_ref_edges) {
		this.num_ref_edges = num_ref_edges;
	}

	public int getNum_ref_nodes() {
		return num_ref_nodes;
	}

	public void setNum_ref_nodes(int num_ref_nodes) {
		this.num_ref_nodes = num_ref_nodes;
	}

	public Graph<String, DefaultEdge> getGraph() {
		return graph;
	}

	public void setGraph(Graph<String, DefaultEdge> graph) {
		this.graph = graph;
	}

	public double getActivityScore() {
		return activityScore;
	}

	public void setActivityScore(double activityScore) {
		this.activityScore = activityScore;
	}

	public double getQValue() {
		return QValue;
	}

	public void setQValue(double qValue) {
		QValue = qValue;
	}

	public int getN_seeds() {
		return n_seeds;
	}

	public void setN_seeds(int n_seeds) {
		this.n_seeds = n_seeds;
	}


	public String getNodeStr() {
		return nodeStr;
	}


	public void setNodeStr(String nodeStr) {
		this.nodeStr = nodeStr;
	}


	public double getpValue() {
		return pValue;
	}


	public void setpValue(double pValue) {
		this.pValue = pValue;
	}

}
