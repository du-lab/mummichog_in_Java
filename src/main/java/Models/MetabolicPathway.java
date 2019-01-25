package Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.json.JsonObject;

import getuserdata.EmpiricalCompound;

public class MetabolicPathway {
	
	private String id;
	private String name;
	private List<String> rxns;
	private List<String> ecs;
	private int ec_num;
	private List<String> cpds;
	private int cpd_num;
	private List<String> slelected_features;
	private int combined_pvalue;
	private double adjust_p;
	private Set<EmpiricalCompound> EmpiricalCompounds;
	private List<EmpiricalCompound> overlapEmpiricalCompunds;
	private List<EmpiricalCompound> overlapFeatures;
	private int overlapSize;
	private int empSize;
	private double pEase;
	private double pFet;
	
	
	public int getEc_num() {
		return ec_num;
	}


	public void setEc_num(int ec_num) {
		this.ec_num = ec_num;
	}





	public int getOverlapSize() {
		return overlapSize;
	}


	public void setOverlapSize(int overlapSize) {
		this.overlapSize = overlapSize;
	}


	public int getEmpSize() {
		return empSize;
	}


	public void setEmpSize(int empSize) {
		this.empSize = empSize;
	}


	public MetabolicPathway(){
		
		this.rxns = new ArrayList<String>();
		this.ecs=new ArrayList<String>();
		this.ec_num=0;
		this.cpds=new ArrayList<String>();
		this.cpd_num=0;
		this.slelected_features=new ArrayList<String>();
		this.combined_pvalue=0;
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

	public int getEcnum() {
		return ec_num;
	}


	public void setEcnum(int ecnum) {
		this.ec_num = ecnum;
	}


	public List<String> getCpds() {
		return cpds;
	}

	public void setCpds(List<String> cpds) {
		this.cpds = cpds;
	}


	public int getCpd_num() {
		return cpd_num;
	}


	public void setCpd_num(int cpd_num) {
		this.cpd_num = cpd_num;
	}

	public List<String> getSlelected_features() {
		return slelected_features;
	}


	public void setSlelected_features(List<String> slelected_features) {
		this.slelected_features = slelected_features;
	}



	public int getCombined_pvalue() {
		return combined_pvalue;
	}


	public void setCombined_pvalue(int combined_pvalue) {
		this.combined_pvalue = combined_pvalue;
	}


	public void str_import(String s) {

		String[] a=removeTrailingSpaces(s).split("\t");
		this.id = a[0];
		this.name = a[1];
		this.rxns = Arrays.asList(a[2].split(";"));
		this.ecs = Arrays.asList(a[3].split(";"));
		this.ec_num = Integer.parseInt(a[4]);
		this.cpds = Arrays.asList(a[5].split(";"));
		//Not Sure what corresponding line in python code means
		//this.cpds = [x for x in cpds if x not in currency]
		this.cpd_num = this.cpds.size();
		
		
	}
	
	public void json_import(JsonObject j) {
		this.id=j.getString("id");
		this.name=j.getString("name");
		this.rxns=Arrays.asList(j.getString("rxns").split(";"));
		this.ecs=Arrays.asList(j.getString("ecs").split(";"));
		this.cpds=Arrays.asList(j.getString("cpds").split(";"));
		this.ec_num=this.ecs.size();
		this.cpd_num=this.cpds.size();
		
		
	}
	
	
	
	
	public static String removeTrailingSpaces(String param)
    {
        if (param == null)
            return null;
        int len = param.length();
        for (; len > 0; len--) {
            if (!Character.isWhitespace(param.charAt(len - 1)))
                break;
        }
        return param.substring(0, len);
    }


	public double getAdjust_p() {
		return adjust_p;
	}


	public void setAdjust_p(double adjust_p) {
		this.adjust_p = adjust_p;
	}


	public Set<EmpiricalCompound> getEmpiricalCompounds() {
		return EmpiricalCompounds;
	}


	public void setEmpiricalCompounds(Set<EmpiricalCompound> empiricalCompounds) {
		EmpiricalCompounds = empiricalCompounds;
	}


	public double getpFet() {
		return pFet;
	}


	public void setpFet(double pFet) {
		this.pFet = pFet;
	}


	public double getpEase() {
		return pEase;
	}


	public void setpEase(double pEase) {
		this.pEase = pEase;
	}


	public List<EmpiricalCompound> getOverlapEmpiricalCompunds() {
		return overlapEmpiricalCompunds;
	}


	public void setOverlapEmpiricalCompunds(List<EmpiricalCompound> overlapEmpiricalCompunds) {
		this.overlapEmpiricalCompunds = overlapEmpiricalCompunds;
	}


	public List<EmpiricalCompound> getOverlapFeatures() {
		return overlapFeatures;
	}


	public void setOverlapFeatures(List<EmpiricalCompound> overlapFeatures) {
		this.overlapFeatures = overlapFeatures;
	}


	
	


}
