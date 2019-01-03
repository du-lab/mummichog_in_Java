package pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import getuserdata.EmpiricalCompound;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RowEmpcpd {
	private String row;
	private EmpiricalCompound empiricalCompound;
	private String compound;

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public EmpiricalCompound getEmpiricalCompound() {
		return empiricalCompound;
	}

	public void setEmpiricalCompound(EmpiricalCompound empiricalCompound) {
		this.empiricalCompound = empiricalCompound;
	}

	public String getCompound() {
		return compound;
	}

	public void setCompound(String compound) {
		this.compound = compound;
	}

	public RowEmpcpd(String row, EmpiricalCompound empiricalCompound, String compound) {
		super();
		this.row = row;
		this.empiricalCompound = empiricalCompound;
		this.compound = compound;
	}

	@Override
	public String toString() {
		return "RowEmpcpd [row=" + row + ", empiricalCompound=" + empiricalCompound + ", compound=" + compound + "]";
	}

	public RowEmpcpd() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compound == null) ? 0 : compound.hashCode());
		result = prime * result + ((empiricalCompound == null) ? 0 : empiricalCompound.hashCode());
		result = prime * result + ((row == null) ? 0 : row.hashCode());
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
		RowEmpcpd other = (RowEmpcpd) obj;
		if (compound == null) {
			if (other.compound != null)
				return false;
		} else if (!compound.equals(other.compound))
			return false;
		if (empiricalCompound == null) {
			if (other.empiricalCompound != null)
				return false;
		} else if (!empiricalCompound.equals(other.empiricalCompound))
			return false;
		if (row == null) {
			if (other.row != null)
				return false;
		} else if (!row.equals(other.row))
			return false;
		return true;
	}

}
