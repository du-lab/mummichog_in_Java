package Models;


import java.util.List;
import java.util.Set;
import pojo.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

/*
 * Metabolite-centric metabolic model Theoretical model, not containing user data
 */
public class MetabolicNetwork {

  private MetabolicModel metabolicModel;
  private Graph<String, DefaultEdge> network;
  // = new DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge.class);
  private Set<String> total_cpd_list;

  public MetabolicNetwork(MetabolicModel model) {
    /*
     * Initiation of metabolic network model. Building Compound index. Parsing input files. Matching
     * m/z - Compound.
     * 
     * MetabolicModel['Compounds'] are subset of cpds in network/pathways with mw. Not all in
     * total_cpd_list has mw.
     */

    this.metabolicModel = model;
    this.network = this.build_network(this.metabolicModel.getCpd_edges());
    this.total_cpd_list = this.network.vertexSet();

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

  public MetabolicModel getMetabolicModel() {
    return metabolicModel;
  }

  public void setMetabolicModel(MetabolicModel metabolicModel) {
    this.metabolicModel = metabolicModel;
  }

  public Graph<String, DefaultEdge> getNetwork() {
    return network;
  }

  public void setNetwork(Graph<String, DefaultEdge> network) {
    this.network = network;
  }

  public Set<String> getTotal_cpd_list() {
    return total_cpd_list;
  }

  public void setTotal_cpd_list(Set<String> total_cpd_list) {
    this.total_cpd_list = total_cpd_list;
  }

}

