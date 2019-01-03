package pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetabolicRxns {
	
	private List <String> reactants;
	private String id;
	private String source;
	private List<String> ecs;
	private List<String> products;
	private List<String> cpds;
	private String pathway;
	public List<String> getReactants() {
		return reactants;
	}
	public void setReactants(List<String> reactants) {
		this.reactants = reactants;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public List<String> getEcs() {
		return ecs;
	}
	public void setEcs(List<String> ecs) {
		this.ecs = ecs;
	}
	public List<String> getProducts() {
		return products;
	}
	public void setProducts(List<String> products) {
		this.products = products;
	}
	public List<String> getCpds() {
		return cpds;
	}
	public void setCpds(List<String> cpds) {
		this.cpds = cpds;
	}
	public String getPathway() {
		return pathway;
	}
	public void setPathway(String pathway) {
		this.pathway = pathway;
	}
	public MetabolicRxns(List<String> reactants, String id, String source, List<String> ecs, List<String> products,
			List<String> cpds, String pathway) {
		super();
		this.reactants = reactants;
		this.id = id;
		this.source = source;
		this.ecs = ecs;
		this.products = products;
		this.cpds = cpds;
		this.pathway = pathway;
	}
	@Override
	public String toString() {
		return "MetabolicRxns [reactants=" + reactants + ", id=" + id + ", source=" + source + ", ecs=" + ecs
				+ ", products=" + products + ", cpds=" + cpds + ", pathway=" + pathway + "]";
	}
	
	
public MetabolicRxns() {
		
	}
	
	
	

}
