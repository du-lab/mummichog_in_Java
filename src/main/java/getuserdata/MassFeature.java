package getuserdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/*
 *  Data model, to store info per input feature
    row_number is used as unique ID. A string like "row23" is used instead of integer for two reasons:
    to enforce unique IDs in string not in number, and for clarity throughout the code;
    to have human friendly numbering, starting from 1 not 0
 */

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compoundID_from_user == null) ? 0 : compoundID_from_user.hashCode());
		result = prime * result + ((database_match == null) ? 0 : database_match.hashCode());
		result = prime * result + ((is_significant == null) ? 0 : is_significant.hashCode());
		result = prime * result + ((matched_Compounds == null) ? 0 : matched_Compounds.hashCode());
		result = prime * result + ((matched_EmpiricalCompounds == null) ? 0 : matched_EmpiricalCompounds.hashCode());
		result = prime * result + ((matched_Ions == null) ? 0 : matched_Ions.hashCode());
		result = prime * result + ((mz == null) ? 0 : mz.hashCode());
		result = prime * result + ((p_value == null) ? 0 : p_value.hashCode());
		result = prime * result + peak_quality;
		result = prime * result + ((retention_time == null) ? 0 : retention_time.hashCode());
		result = prime * result + ((row_number == null) ? 0 : row_number.hashCode());
		result = prime * result + ((statistic == null) ? 0 : statistic.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MassFeature other = (MassFeature) obj;
		if (compoundID_from_user == null) {
			if (other.compoundID_from_user != null)
				return false;
		} else if (!compoundID_from_user.equals(other.compoundID_from_user))
			return false;
		if (database_match == null) {
			if (other.database_match != null)
				return false;
		} else if (!database_match.equals(other.database_match))
			return false;
		if (is_significant == null) {
			if (other.is_significant != null)
				return false;
		} else if (!is_significant.equals(other.is_significant))
			return false;
		if (matched_Compounds == null) {
			if (other.matched_Compounds != null)
				return false;
		} else if (!matched_Compounds.equals(other.matched_Compounds))
			return false;
		if (matched_EmpiricalCompounds == null) {
			if (other.matched_EmpiricalCompounds != null)
				return false;
		} else if (!matched_EmpiricalCompounds.equals(other.matched_EmpiricalCompounds))
			return false;
		if (matched_Ions == null) {
			if (other.matched_Ions != null)
				return false;
		} else if (!matched_Ions.equals(other.matched_Ions))
			return false;
		if (mz == null) {
			if (other.mz != null)
				return false;
		} else if (!mz.equals(other.mz))
			return false;
		if (p_value == null) {
			if (other.p_value != null)
				return false;
		} else if (!p_value.equals(other.p_value))
			return false;
		if (peak_quality != other.peak_quality)
			return false;
		if (retention_time == null) {
			if (other.retention_time != null)
				return false;
		} else if (!retention_time.equals(other.retention_time))
			return false;
		if (row_number == null) {
			if (other.row_number != null)
				return false;
		} else if (!row_number.equals(other.row_number))
			return false;
		if (statistic == null) {
			if (other.statistic != null)
				return false;
		} else if (!statistic.equals(other.statistic))
			return false;
		return true;
	}
	
	
	

}
