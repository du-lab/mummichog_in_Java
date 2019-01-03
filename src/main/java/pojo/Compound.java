package pojo;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Compound {
	
	private String formula;
	private Double mw;
	private String name;
	private Map <String,Double> adducts;
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Double> getAdducts() {
		return adducts;
	}
	public void setAdducts(Map<String, Double> adducts) {
		this.adducts = adducts;
	}
//	public Compound(String formula, Double mw, String name, Map<String, Double> adducts) {
//		super();
//		this.formula = formula;
//		this.mw = mw;
//		this.name = name;
//		this.adducts = adducts;
//	}
//	@Override
//	public String toString() {
//		return "Compound [formula=" + formula + ", mw=" + mw + ", name=" + name + ", adducts=" + adducts + "]";
//	}
	public Double getMw() {
		return mw;
	}
	public void setMw(Double mw) {
		this.mw = mw;
	}

}
