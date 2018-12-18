package driver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import Models.MetabolicNetwork;
import functional_analysis.ModularAnalysis;
import functional_analysis.PathwayAnalysis;
import getuserdata.DataMeetModel;
import getuserdata.InputUserData;
import getuserdata.UserDataClass;
import pojo.MetabolicModel;
import pojo.RealModels;

public class Orchestration {
	
	private final static Logger LOGGER = Logger.getLogger(Orchestration.class.getName());

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Hello");
	//	UserDataClass.dispatcher(args);
	//	System.out.println("Ends here");
		
		System.out.println("Mummichog Code Run Begins");
		
		Map<String,String> optDict = UserDataClass.dispatcher(args);
		
		InputUserData userdata = new InputUserData(optDict);
		RealModels rm=null;
		
		//Testing reading Json File
		ObjectMapper mapper = new ObjectMapper();
		try {
			rm= mapper.readValue(new File("./JSON_metabolicModels.py"), RealModels.class);
			System.out.println("File read");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> human= new ArrayList<String>(Arrays.asList("human","hsa", "Human", "human_mfn", "hsa_mfn"));
		List<String> worm= new ArrayList<String>(Arrays.asList("worm", "C. elegans", "icel1273", "Caenorhabditis elegans"));
		
		MetabolicNetwork theoreticalModel=null;
		
		
		// check the issue in parameter picking
//		if(human.contains(userdata.getParadict().get("network"))){
			
			theoreticalModel = new MetabolicNetwork(rm.getHuman_model_mfn());
			
//		}else if (worm.contains(userdata.getParadict().get("network"))) {
//			theoreticalModel = new MetabolicNetwork(rm.getWorm_model_icel1273());
//		}
//		
		DataMeetModel mixedNetwork = new DataMeetModel(theoreticalModel, userdata);
		
	//	PathwayAnalysis pathwayAnalysis = new PathwayAnalysis(mixedNetwork, mixedNetwork.getModel().getMetabolicModel().getMetabolic_pathways());
//		pathwayAnalysis.cpd_enrich_test();
		
		ModularAnalysis modularAnalysis = new ModularAnalysis(mixedNetwork);
		modularAnalysis.dispatch();

	}

}
