package functional_analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import getuserdata.DataMeetModel;
import pojo.RowEmpcpd;
import resources.Constants;

/*
 * Tally cpds responsible for significant pathways and modules, and build an acitvity network to
 * represent top story in the data. Remove singletons in network. Try no more than 3 steps. AN can
 * get too big or too small.
 */
public class ActivityNetwork {

  private DataMeetModel mixedNetwork;
  private Graph<String, DefaultEdge> network;
  private Graph<String, DefaultEdge> activityNetwork;

  private final static Logger LOGGER = Logger.getLogger(ActivityNetwork.class.getName());

  public ActivityNetwork(List<RowEmpcpd> hitTriosList, DataMeetModel mixedNetwork) {
    /*
     * Build a consensus network for hit_Trios, [(mzFeature, EmpiricalCompound, cpd),...] for
     * top_modules and sig pathways. hit_Trios = set(PA.collect_hit_Trios() +
     * MA.collect_hit_Trios())
     */
    mixedNetwork.setHitTriosList(hitTriosList);
    this.mixedNetwork = mixedNetwork;
    this.network = mixedNetwork.getModel().getNetwork();
    List<String> nodes = new ArrayList<String>();
    for (RowEmpcpd row : hitTriosList) {
      nodes.add(row.getCompound());
    }
    this.activityNetwork = this.build_Activity_Network(nodes);

  }

  public Graph<String, DefaultEdge> build_Activity_Network(List<String> nodes) {

    /*
     * Get a network with good connections in no more than 3 steps. No modularity requirement for 1
     * step connected nodes.
     */

    int expected_size = 10;

    Graph<String, DefaultEdge> an = new AsSubgraph<String, DefaultEdge>(
        this.mixedNetwork.getModel().getNetwork(), new HashSet<String>(nodes));

    if (nodes != null && nodes.size() > 0) {
      Graph<String, DefaultEdge> subG1 = getLargestSubgraph(an);
      if (subG1.vertexSet().size() > expected_size) {
        LOGGER.info("Activity network was connected in 1 step.");
        return subG1;
      } else {
        List<List<String>> edges = this.getgraphedges(nodes);
        Graph<String, DefaultEdge> subG2 = getLargestSubgraph(build_network(edges));
        double conn = this.getAveConnections(subG2);
        if (an.vertexSet().size() > Constants.MODULE_SIZE_LIMIT
            || conn > Constants.CUTOFF_AVE_CONN) {
          Log.info("Activity Network was connected in 2 steps. ");
          return subG2;
        } else {
          edges = this.getgraphedges(new ArrayList<String>(subG2.vertexSet()));
          Graph<String, DefaultEdge> subG3 = getLargestSubgraph(build_network(edges));
          conn = this.getAveConnections(subG3);
          if (conn > Constants.CUTOFF_AVE_CONN) {
            LOGGER.info("Activity Network was connected in 3 steps");
            return subG3;
          } else {
            return an;
          }
        }
      }
    }
    return an;

  }

  public Graph<String, DefaultEdge> getLargestSubgraph(Graph<String, DefaultEdge> an) {

    BiconnectivityInspector<String, DefaultEdge> scAlg = new BiconnectivityInspector<>(an);
    Set<Graph<String, DefaultEdge>> stronglyConnectedSubgraphs = scAlg.getConnectedComponents();
    List<Graph<String, DefaultEdge>> connectedComponents =
        new ArrayList<Graph<String, DefaultEdge>>(stronglyConnectedSubgraphs);
    Collections.sort(connectedComponents,
        (a, b) -> Integer.compare(a.vertexSet().size(), b.vertexSet().size()));
    // Instead of reverse sorting, returning the last element of the list

    return connectedComponents.get(connectedComponents.size() - 1);

  }

  List<List<String>> getgraphedges(List<String> vertices) {
    List<List<String>> result = new ArrayList<List<String>>();

    for (DefaultEdge ed : this.network.edgeSet()) {
      if (vertices.contains(this.network.getEdgeSource(ed))
          || vertices.contains(this.network.getEdgeTarget(ed))) {
        result.add(new ArrayList<String>(
            Arrays.asList(this.network.getEdgeSource(ed), this.network.getEdgeTarget(ed))));
      }
    }

    return result;
  }

  @SuppressWarnings("rawtypes")
  Graph<String, DefaultEdge> build_network(List<List<String>> edges) {

    Graph<String, DefaultEdge> result =
        new DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge.class);

    for (List<String> list : edges) {
      for (String node : list) {
        result.addVertex(node);
      }
      result.addEdge(list.get(0), list.get(1));
    }
    return result;
  }

  double getAveConnections(Graph<String, DefaultEdge> net) {
    try {
      return (double) net.edgeSet().size() / (net.vertexSet().size());
    } catch (Exception e) {
      return 0.0;
    }

  }

  public DataMeetModel getMixedNetwork() {
    return mixedNetwork;
  }

  public void setMixedNetwork(DataMeetModel mixedNetwork) {
    this.mixedNetwork = mixedNetwork;
  }

  public Graph<String, DefaultEdge> getNetwork() {
    return network;
  }

  public void setNetwork(Graph<String, DefaultEdge> network) {
    this.network = network;
  }

  public Graph<String, DefaultEdge> getActivityNetwork() {
    return activityNetwork;
  }

  public void setActivityNetwork(Graph<String, DefaultEdge> activityNetwork) {
    this.activityNetwork = activityNetwork;
  }

}
