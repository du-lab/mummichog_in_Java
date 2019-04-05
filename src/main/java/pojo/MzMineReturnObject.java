package pojo;

public class MzMineReturnObject {

	private double mz;
	//retention time
	private double rt;
	private String compundName;
	private String formula;
	private String adduct;
	public MzMineReturnObject(double mz, double rt, String compundName, String formula, String adduct) {
		super();
		this.mz = mz;
		this.rt = rt;
		this.compundName = compundName;
		this.formula = formula;
		this.adduct = adduct;
	}
	public double getMz() {
		return mz;
	}
	public void setMz(double mz) {
		this.mz = mz;
	}
	public double getRt() {
		return rt;
	}
	public void setRt(double rt) {
		this.rt = rt;
	}
	public String getCompundName() {
		return compundName;
	}
	public void setCompundName(String compundName) {
		this.compundName = compundName;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getAdduct() {
		return adduct;
	}
	public void setAdduct(String adduct) {
		this.adduct = adduct;
	}
	
	
	
}
