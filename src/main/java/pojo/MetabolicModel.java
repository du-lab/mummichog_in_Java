package pojo;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetabolicModel {


  private Map<String, String> edge2rxn;
  private String version;
  private List<MetabolicRxns> metabolic_rxns;
  private List<List<String>> cpd_edges;
  private List<MetabolicPathwayPOJO> metabolic_pathways;

  private Map<String, Compound> compounds;
  private Map<String, String> dict_cpds_def;
  private Map<String, List<String>> cpd2pathways;
  private Map<String, String> edge2enzyme;
  private Map<String, Float> dict_cpds_mass;


  public Map<String, String> getEdge2rxn() {
    return edge2rxn;
  }

  public void setEdge2rxn(Map<String, String> edge2rxn) {
    this.edge2rxn = edge2rxn;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<MetabolicRxns> getMetabolic_rxns() {
    return metabolic_rxns;
  }

  public void setMetabolic_rxns(List<MetabolicRxns> metabolic_rxns) {
    this.metabolic_rxns = metabolic_rxns;
  }

  public List<List<String>> getCpd_edges() {
    return cpd_edges;
  }

  public void setCpd_edges(List<List<String>> cpd_edges) {
    this.cpd_edges = cpd_edges;
  }

  public List<MetabolicPathwayPOJO> getMetabolic_pathways() {
    return metabolic_pathways;
  }

  public void setMetabolic_pathways(List<MetabolicPathwayPOJO> metabolic_pathways) {
    this.metabolic_pathways = metabolic_pathways;
  }

  public Map<String, String> getDict_cpds_def() {
    return dict_cpds_def;
  }

  public void setDict_cpds_def(Map<String, String> dict_cpds_def) {
    this.dict_cpds_def = dict_cpds_def;
  }

  public Map<String, List<String>> getCpd2pathways() {
    return cpd2pathways;
  }

  public void setCpd2pathways(Map<String, List<String>> cpd2pathways) {
    this.cpd2pathways = cpd2pathways;
  }

  public Map<String, String> getEdge2enzyme() {
    return edge2enzyme;
  }

  public void setEdge2enzyme(Map<String, String> edge2enzyme) {
    this.edge2enzyme = edge2enzyme;
  }

  public MetabolicModel() {

  }

  public Map<String, Float> getDict_cpds_mass() {
    return dict_cpds_mass;
  }

  public void setDict_cpds_mass(Map<String, Float> dict_cpds_mass) {
    this.dict_cpds_mass = dict_cpds_mass;
  }

  public Map<String, Compound> getCompounds() {
    return compounds;
  }

  public void setCompounds(Map<String, Compound> compounds) {
    this.compounds = compounds;
  }



}


