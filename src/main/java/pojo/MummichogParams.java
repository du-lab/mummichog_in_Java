package pojo;

public class MummichogParams {

  private double cutOff;
  private String network;
  private boolean force_primary_ion;
  private String modeling;
  private String output;



  public MummichogParams(double cutOff, String network, boolean force_primary_ion, String modeling,
      String output) {
    super();
    this.cutOff = cutOff;
    this.network = network;
    this.force_primary_ion = force_primary_ion;
    this.modeling = modeling;
    this.output = output;
  }


  public String[] generateArgumentString() {
    String[] arguments = new String[10];

    arguments[0] = "--cutoff";
    arguments[1] = String.valueOf(this.cutOff);
    arguments[2] = "--network";
    arguments[3] = this.network;
    arguments[4] = "--force_primary_ion";
    arguments[5] = String.valueOf(this.force_primary_ion);
    arguments[6] = "--modeling";
    arguments[7] = this.modeling;
    arguments[8] = "--output";
    arguments[9] = this.output;

    return arguments;
  }

  public double getCutOff() {
    return cutOff;
  }

  public void setCutOff(double cutOff) {
    this.cutOff = cutOff;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public boolean isForce_primary_ion() {
    return force_primary_ion;
  }

  public void setForce_primary_ion(boolean force_primary_ion) {
    this.force_primary_ion = force_primary_ion;
  }

  public String getModeling() {
    return modeling;
  }

  public void setModeling(String modeling) {
    this.modeling = modeling;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }



}
