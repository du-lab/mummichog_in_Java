package reporting;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import getuserdata.DataMeetModel;
import getuserdata.EmpiricalCompound;
import getuserdata.MassFeature;
import pojo.Compound;

/*
 * This class is used as a data transfer object to export Mummichog's output to other programs.
 */
public class MzMineOutput {
  private DataMeetModel mixedNetowrk;
  private final static Logger LOGGER = Logger.getLogger(MzMineOutput.class.getName());

  public DataMeetModel getMixedNetowrk() {
    return mixedNetowrk;
  }

  public void setMixedNetowrk(DataMeetModel mixedNetowrk) {
    this.mixedNetowrk = mixedNetowrk;
  }

  public MzMineOutput(DataMeetModel mixedNetowrk) {
    super();
    this.mixedNetowrk = mixedNetowrk;
  }

  // Generates Output in format required for MzMine
  public Map<String, List<Compound>> generateMzMineOutput() {
    Map<String, List<Compound>> result = new HashMap<String, List<Compound>>();

    for (EmpiricalCompound ec : this.mixedNetowrk.getListOfEmpiricalCompounds()) {

      List<Compound> temp_cpds = new ArrayList<Compound>();

      for (String cmpd : ec.getCompounds()) {
        try {
          temp_cpds.add(this.mixedNetowrk.getModel().getMetabolicModel().getCompounds().get(cmpd));
        } catch (Exception e) {
          LOGGER.error(e.getMessage());
        }
      }
      for (List<Double> mzrValue : createMz_Rows(ec)) {
        result.put(mzrValue.get(0).toString() + ";" + mzrValue.get(1).toString(), temp_cpds);
      }
    }

    return result;
  }

  List<List<Double>> createMz_Rows(EmpiricalCompound ec) {
    List<List<Double>> result = new ArrayList<List<Double>>();
    for (String row : ec.getMassfeature_rows()) {
      for (MassFeature mf : this.mixedNetowrk.getData().getListOfMassFeatures()) {
        if (mf.getRow_number().equalsIgnoreCase(row)) {
          result.add(new ArrayList<Double>(Arrays.asList(mf.getMz(), mf.getRetention_time())));
          break;
        }
      }
    }
    return result;
  }

}
