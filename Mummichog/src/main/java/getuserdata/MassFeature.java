package getuserdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MassFeature {
	
	private String row_number;
	private Double mz;
	private Double retention_time;
	private Double p_value;
	private Double statistic;
	private String compoundID_from_user;
	private List<List<String>> matched_Ions;
	private List<String> matched_Compounds;
	private List<String> matched_EmpiricalCompounds;
	private Boolean is_significant;
	
	//For future use
	private int peak_quality;
	private List<String> database_match;
	
	private final static Logger LOGGER = Logger.getLogger(MassFeature.class.getName());
	
	
	MassFeature(String row_number, Double mz,Double retention_time, Double p_value, Double statistic,String compoundID_from_user){
		
		this.row_number=row_number;
		this.mz=mz;
		this.retention_time=retention_time;
		this.p_value=p_value;
		this.statistic=statistic;
		// No need to provide default value as empty to string. 
		this.compoundID_from_user=compoundID_from_user;
		this.is_significant=false;
		this.peak_quality=0;
		
		this.matched_Ions= new ArrayList<List<String>>();
		this.matched_Compounds=new ArrayList<String>();
		this.matched_EmpiricalCompounds=new ArrayList<String>();
		this.database_match=new ArrayList<String>();
	}
	
	String make_str_output() {
		
		return "\t" + this.row_number + this.mz + this.retention_time + this.p_value + this.statistic + this.compoundID_from_user;
		
	}

	public String getRow_number() {
		return row_number;
	}

	public void setRow_number(String row_number) {
		this.row_number = row_number;
	}

	public Double getMz() {
		return mz;
	}

	public void setMz(Double mz) {
		this.mz = mz;
	}

	public Double getRetention_time() {
		return retention_time;
	}

	public void setRetention_time(Double reteretention_time) {
		this.retention_time = reteretention_time;
	}

	public Double getP_value() {
		return p_value;
	}

	public void setP_value(Double p_value) {
		this.p_value = p_value;
	}

	public Double getStatistic() {
		return statistic;
	}

	public void setStatistic(Double statistic) {
		this.statistic = statistic;
	}

	public String getCompoundID_from_user() {
		return compoundID_from_user;
	}

	public void setCompoundID_from_user(String compoundID_from_user) {
		this.compoundID_from_user = compoundID_from_user;
	}

	public List<List<String>> getMatched_Ions() {
		return matched_Ions;
	}

	public void setMatched_Ions(List<List<String>> matched_Ions) {
		this.matched_Ions = matched_Ions;
	}

	public List<String> getMatched_Compounds() {
		return matched_Compounds;
	}

	public void setMatched_Compounds(List<String> matched_Compounds) {
		this.matched_Compounds = matched_Compounds;
	}

	public List<String> getMatched_EmpiricalCompounds() {
		return matched_EmpiricalCompounds;
	}

	public void setMatched_EmpiricalCompounds(List<String> matched_EmpiricalCompounds) {
		this.matched_EmpiricalCompounds = matched_EmpiricalCompounds;
	}

	public Boolean getIs_significant() {
		return is_significant;
	}

	public void setIs_significant(Boolean is_significant) {
		this.is_significant = is_significant;
	}

	public int getPeak_quality() {
		return peak_quality;
	}

	public void setPeak_quality(int peak_quality) {
		this.peak_quality = peak_quality;
	}

	public List<String> getDatabase_match() {
		return database_match;
	}

	public void setDatabase_match(List<String> database_match) {
		this.database_match = database_match;
	}
	
	
	

}
