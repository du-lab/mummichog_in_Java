package driver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import Models.MetabolicNetwork;
import functional_analysis.ActivityNetwork;
import functional_analysis.ModularAnalysis;
import functional_analysis.PathwayAnalysis;
import getuserdata.DataMeetModel;
import getuserdata.InputUserData;
import getuserdata.UserDataClass;
import pojo.RealModels;
import pojo.RowEmpcpd;
import reporting.LocalFileGenerator;

public class Orchestration {

  private final static Logger LOGGER = Logger.getLogger(Orchestration.class.getName());


  public static void main(String[] args) {
    LOGGER.info("Mummichog Code Run Begins");

    Map<String, String> optDict = UserDataClass.dispatcher(args);

    InputUserData userdata = new InputUserData(optDict, false, "");
    RealModels rm = null;

    ObjectMapper mapper = new ObjectMapper();
    try {
      // Read the JSON file for Human and Worm models
      rm = mapper.readValue(Orchestration.class.getResource("JSON_metabolicModels.py"),
          RealModels.class);
      LOGGER.info("JSON File Read");

    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    }

    List<String> human =
        new ArrayList<String>(Arrays.asList("human", "hsa", "Human", "human_mfn", "hsa_mfn"));
    List<String> worm = new ArrayList<String>(
        Arrays.asList("worm", "C. elegans", "icel1273", "Caenorhabditis elegans"));

    MetabolicNetwork theoreticalModel = null;

    // // Choosing appropriate Data Model based on Input parameter "Network"
    if (human.contains(userdata.getParadict().get("network"))) {

      theoreticalModel = new MetabolicNetwork(rm.getHuman_model_mfn());

    } else if (worm.contains(userdata.getParadict().get("network"))) {
      theoreticalModel = new MetabolicNetwork(rm.getWorm_model_icel1273());
    }

    DataMeetModel mixedNetwork = new DataMeetModel(theoreticalModel, userdata);

    // getting a list of Pathway instances, with p-values, in PA.resultListOfPathways
    PathwayAnalysis pathwayAnalysis = new PathwayAnalysis(mixedNetwork,
        mixedNetwork.getModel().getMetabolicModel().getMetabolic_pathways(), null);
    pathwayAnalysis.cpd_enrich_test();

    // Module analysis, getting a list of Mmodule instances
    ModularAnalysis modularAnalysis = new ModularAnalysis(mixedNetwork, null);
    modularAnalysis.dispatch();


    Set<RowEmpcpd> combined_TrioList = new HashSet<RowEmpcpd>();
    combined_TrioList.addAll(pathwayAnalysis.collectHitTrios());
    combined_TrioList.addAll(modularAnalysis.collectHitTrios());
    //Generating Activity Network from Modular and Pathway Analysis
    ActivityNetwork activityNetwork =
        new ActivityNetwork(new ArrayList<RowEmpcpd>(combined_TrioList), mixedNetwork);
    //Generating Output CSV Files
    @SuppressWarnings("unused")
    LocalFileGenerator lfg = new LocalFileGenerator(mixedNetwork, pathwayAnalysis, modularAnalysis,
        activityNetwork, userdata.getParadict().get("outdir"));
  }

}
