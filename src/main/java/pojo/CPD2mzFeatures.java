package pojo;

import getuserdata.MassFeature;

public class CPD2mzFeatures {

  private String ion;
  private Double mass;
  MassFeature massFeature;

  public String getIon() {
    return ion;
  }

  public void setIon(String ion_1) {
    this.ion = ion_1;
  }

  public Double getMass() {
    return this.mass;
  }

  public void setIon_2(Double mass) {
    this.mass = mass;
  }

  public MassFeature getMassFeature() {
    return massFeature;
  }

  public void setMassFeature(MassFeature massFeature) {
    this.massFeature = massFeature;
  }

  public CPD2mzFeatures(String ion_1, Double mass, MassFeature massFeature) {
    super();
    this.ion = ion_1;
    this.mass = mass;
    this.massFeature = massFeature;
  }


}
