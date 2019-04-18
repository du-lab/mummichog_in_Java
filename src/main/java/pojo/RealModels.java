package pojo;

public class RealModels {
  private MetabolicModel human_model_mfn;
  private MetabolicModel worm_model_icel1273;

  public MetabolicModel getHuman_model_mfn() {
    return human_model_mfn;
  }

  public void setHuman_model_mfn(MetabolicModel human_model_mfn) {
    this.human_model_mfn = human_model_mfn;
  }

  public MetabolicModel getWorm_model_icel1273() {
    return worm_model_icel1273;
  }

  public void setWorm_model_icel1273(MetabolicModel worm_model_icel1273) {
    this.worm_model_icel1273 = worm_model_icel1273;
  }

  @Override
  public String toString() {
    return "RealModels [human_model_mfn=" + human_model_mfn + ", worm_model_icel1273="
        + worm_model_icel1273 + "]";
  }



}
