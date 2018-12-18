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


}
