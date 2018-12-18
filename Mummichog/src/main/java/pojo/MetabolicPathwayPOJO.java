package pojo;

import java.util.List;

public class MetabolicPathwayPOJO {
	
	private List<String> cpds;
	private List<String> rxns;
	private List<String> ecs;
	private String id;
	private String name;
	
	public List<String> getCpds() {
		return cpds;
	}
	public void setCpds(List<String> cpds) {
		this.cpds = cpds;
	}
	public List<String> getRxns() {
		return rxns;
	}
	public void setRxns(List<String> rxns) {
		this.rxns = rxns;
	}
	public List<String> getEcs() {
		return ecs;
	}
	public void setEcs(List<String> ecs) {
		this.ecs = ecs;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public MetabolicPathway(List<String> cpds, List<String> rxns, List<String> ecs, String id, String name) {
//		super();
//		this.cpds = cpds;
//		this.rxns = rxns;
//		this.ecs = ecs;
//		this.id = id;
//		this.name = name;
//	}
//	@Override
//	public String toString() {
//		return "MetabolicPathway [cpds=" + cpds + ", rxns=" + rxns + ", ecs=" + ecs + ", id=" + id + ", name=" + name
//				+ "]";
//	}
//	
//public MetabolicPathway() {
//		
//	}
	
}
