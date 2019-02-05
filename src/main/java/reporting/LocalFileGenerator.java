package reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.opencsv.CSVWriter;

import Models.MModule;
import Models.MetabolicPathway;
import driver.Orchestration;
import functional_analysis.ActivityNetwork;
import functional_analysis.ModularAnalysis;
import functional_analysis.PathwayAnalysis;
import getuserdata.DataMeetModel;
import getuserdata.EmpiricalCompound;
import getuserdata.MassFeature;

public class LocalFileGenerator {

	private DataMeetModel mixedNetowrk;
	private PathwayAnalysis pathwayAnalysis;
	private ModularAnalysis modularAnalaysis;
	private ActivityNetwork activityNetwork;
	private final static Logger LOGGER = Logger.getLogger(LocalFileGenerator.class.getName());

	public LocalFileGenerator(DataMeetModel mixedNetowrk, PathwayAnalysis pathwayAnalysis,
			ModularAnalysis modularAnalaysis, ActivityNetwork activityNetwork, String outputDir) {
		super();
		this.mixedNetowrk = mixedNetowrk;
		this.pathwayAnalysis = pathwayAnalysis;
		this.modularAnalaysis = modularAnalaysis;
		this.activityNetwork = activityNetwork;
		String newoutputDir=outputDir+"/"+"MCGResults_"+System.currentTimeMillis();
		new File(newoutputDir).mkdirs();
		this.exportEmpiricalCompunds(newoutputDir+"/empirical_compounds.csv");
		this.exportPathwayAnalysisResult(newoutputDir+"/pathway_analysis.csv");
		this.exportModularAnalysisResult(newoutputDir + "/modular_analysis.csv");
	
	}

	public void exportModularAnalysisResult(String filePath) {
		File file = new File(filePath);
		CSVWriter writer = null;
		try {
			// create FileWriter object with file as parameter
			FileWriter outputfile = new FileWriter(file);

			// create CSVWriter object file writer object as parameter
			writer = new CSVWriter(outputfile);
			List<String[]> data = new ArrayList<String[]>();
			data.add(new String[] { "Module", "P-Value", "Size",
					"Members(Name)", "Memebers(ID)", "This Module overlaps with" });
			int counter=0;
			for (MModule mm:modularAnalaysis.getTopModules()) {
				counter++;
				List<String> nodes= new ArrayList<String>(mm.getGraph().vertexSet());
				data.add(new String[] {"module_"+counter,String.valueOf(mm.getpValue()),String.valueOf(nodes.size()),create_Names(nodes),create_Nodes(nodes),findTopPathways(nodes)});
			}
			writer.writeAll(data);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}

	}
	

	String findTopPathways(List<String> nodes) {
		StringBuffer result= new StringBuffer();
		List <String> pathways = new ArrayList<String>();
		for(String node: nodes) {
			try {
			pathways.addAll(this.mixedNetowrk.getModel().getMetabolicModel().getCpd2pathways().get(node));
			}catch(Exception e) {
				LOGGER.error(e.getMessage());
				continue;
			}
		}

		Map<String,Integer> countPathwayMap = new TreeMap<String, Integer>();
		
		for(String path: new HashSet<String>(pathways)) {
			countPathwayMap.put(path,Collections.frequency(pathways, path));
		}
		
		Map<String,Integer> sortedMap = sortByValues(countPathwayMap);
		
		int count=0;
		for(String i: sortedMap.keySet()) {
			count++;
			if(count>3)
				break;
			result.append(i).append(";");
		}
		
		return result.toString();
	}
	
	  public static <K, V extends Comparable<V>> Map<K, V> 
	    sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator = 
	             new Comparator<K>() {
	      public int compare(K k1, K k2) {
	        int compare = 
	              map.get(k2).compareTo(map.get(k1));
	        if (compare == 0) 
	          return 1;
	        else 
	          return compare;
	      }
	    };
	 
	    Map<K, V> sortedByValues = 
	      new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	  }
	
	String create_Names(List<String> nodes) {
		StringBuilder result = new StringBuilder();
		for(String node: nodes) {
			try {
			result.append(this.mixedNetowrk.getModel().getMetabolicModel().getDict_cpds_def().get(node)).append(";");
			}catch(Exception e) {
				result.append(";");
			}
		}
		
		if (result.length() > 0) {
			return result.substring(0, result.length() - 2);
		}
		return result.toString();
	}

	
	String create_Nodes(List<String> nodes) {
		StringBuilder result = new StringBuilder();
		for(String node: nodes) {
			result.append(node).append(";");
		}
		
		if (result.length() > 0) {
			return result.substring(0, result.length() - 1);
		}
		return result.toString();
	}
	public void exportPathwayAnalysisResult(String filePath) {
		File file = new File(filePath);
		CSVWriter writer = null;
		try {
			// create FileWriter object with file as parameter
			FileWriter outputfile = new FileWriter(file);

			// create CSVWriter object file writer object as parameter
			writer = new CSVWriter(outputfile);
			List<String[]> data = new ArrayList<String[]>();
			data.add(new String[] { "Pathway", "Overlap Size", "Pathway Size", "P Value",
					"OverLap Empirical Compunds(ID)", "OvaerLap Features(ID)", "Overlap Features(Name)" });
			for (MetabolicPathway mp : this.pathwayAnalysis.getResultListOfPathways()) {
				List<List<String>> compunds = create_Cmpds(mp.getOverlapEmpiricalCompunds());

				// Overlap Compunds are not being set when the overlap size is 5. Check the
				// issue
				data.add(new String[] { mp.getName(), String.valueOf(mp.getOverlapSize()),
						String.valueOf(mp.getEmpSize()), String.valueOf(mp.getAdjust_p()),
						create_OverLapEmpiricalCmpds(mp.getOverlapEmpiricalCompunds()), create_CmpdsString(compunds),
						create_CmpdsNames(compunds) });
			}
			writer.writeAll(data);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	String create_CmpdsNames(List<List<String>> compunds) {
		StringBuilder result = new StringBuilder();
		for (List<String> li : compunds) {
			for (String cmpd : li) {
				try {
					result.append(this.mixedNetowrk.getModel().getMetabolicModel().getDict_cpds_def().get(cmpd))
							.append(";");
				} catch (Exception e) {
					result.append("").append(";");
				}
			}
		}
		if (result.length() > 0) {
			return result.substring(0, result.length() - 2);
		}
		return result.toString();
	}

	List<List<String>> create_Cmpds(List<EmpiricalCompound> ecs) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (EmpiricalCompound s : ecs) {
			List<String> temp = new ArrayList<>();
			result.add(new ArrayList<>(s.getChosen_compounds()));
		}
		return result;
	}

	String create_CmpdsString(List<List<String>> compunds) {
		StringBuilder result = new StringBuilder();
		for (List<String> li : compunds) {
			for (String cmpd : li) {
				result.append(cmpd).append(";");
			}
		}
		if (result.length() > 0) {
			return result.substring(0, result.length() - 1);
		}
		return result.toString();
	}

	String create_OverLapEmpiricalCmpds(List<EmpiricalCompound> ecs) {
		StringBuilder result = new StringBuilder();
		for (EmpiricalCompound s : ecs) {
			result.append(s.geteId()).append(";");
		}
		if (result.length() > 0) {
			return result.substring(0, result.length() - 1);
		}
		return result.toString();
	}

	public void exportEmpiricalCompunds(String filePath) {
		File file = new File(filePath);
		CSVWriter writer = null;
		try {
			// create FileWriter object with file as parameter
			FileWriter outputfile = new FileWriter(file);

			// create CSVWriter object file writer object as parameter
			writer = new CSVWriter(outputfile);
			List<String[]> data = new ArrayList<String[]>();
			data.add(new String[] { "EID", "massfeature_rows", "rows_mz_retention_time", "str_row_ion", "compounds",
					"compound_names" });
			for (EmpiricalCompound ec : this.mixedNetowrk.getListOfEmpiricalCompounds()) {
				data.add(new String[] { ec.geteId(), createMassFeature_Rows(ec), createMz_Rows(ec), ec.getStr_row_ion(),
						createCompunds(ec), createCompundNames(ec) });
			}

			writer.writeAll(data);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	String createMassFeature_Rows(EmpiricalCompound ec) {
		StringBuilder result = new StringBuilder();
		for (String s : ec.getMassfeature_rows()) {
			result.append(s).append(";");
		}
		if (result.length() > 0) {
			return result.substring(0, result.length() - 2);
		}
		return result.toString();
	}

	String createCompunds(EmpiricalCompound ec) {
		StringBuilder result = new StringBuilder();
		for (String s : ec.getCompounds()) {
			result.append(s).append(";");
		}
		if (result.length() > 0) {		
			return result.substring(0, result.length() - 1);
		}
		return result.toString();
	}

	String createCompundNames(EmpiricalCompound ec) {
		StringBuilder result = new StringBuilder();
		for (String s : ec.getCompounds()) {
			try {
				result.append("\"").append(this.mixedNetowrk.getModel().getMetabolicModel().getDict_cpds_def().get(s))
						.append("\"").append(";");
			} catch (Exception e) {
				result.append("").append(";");
			}
		}
		if (result.length() > 0) {
			return result.substring(0, result.length() - 1);
		}
		return result.toString();
	}

	String createMz_Rows(EmpiricalCompound ec) {
		StringBuilder result = new StringBuilder();
		for(String row: ec.getMassfeature_rows()) {
		for(MassFeature mf:this.mixedNetowrk.getData().getListOfMassFeatures() ) {
			if(mf.getRow_number().equalsIgnoreCase(row)) {
				result.append("(").append(row).append(";").append(mf.getMz()).append(";").append(mf.getRetention_time()).append(")");
				break;
			}
		}}
		
//		for (List<String> s : ec.getListOfFeatures()) {
//			result.append("(").append(s.get(1)).append(";").append(s.get(3)).append(";").append(s.get(0)).append(")");
//		}
		return result.toString();
	}

}
