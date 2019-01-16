package resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
	
	public static final String VERSION = "2.0.7-beta-20180423";
	public static final boolean RELEASE = false;
	public static final boolean USE_DEBUG = false;
	
	public static final int SEARCH_STEPS = 4;
	public static final int MODULE_SIZE_LIMIT = 100;
	public static final double SIGNIFICANCE_CUTOFF = 0.05;
	public static final double CUTOFF_AVE_CONN=0.5;
	
	//TBD
	//		MASS_RANGE = (50, 2000)
	public static List<Integer> MASS_RANGE= new ArrayList<Integer>(Arrays.asList(50,2000));

	public static final double RETENTION_TIME_TOLERANCE_FRAC = 0.01;    //fraction of total retention time
	public static final double PROTON = 1.00727646677;
	
	//currency metaboites of ubiquitous presence
	public static List<String> currList = new ArrayList<String>(Arrays.asList("C00001", "C00080", "C00007", "C00006", "C00005",
			"C0000", "C00004", "C00002", "C00013", "C00008", "C00009", "C00011", "G11113", "H2O", "H+", "Oxygen",
			"NADP+", "NADPH", "NAD+", "NADH", "ATP", "Pyrophosphate", "ADP", "Orthophosphate", "CO2"));
	
	
	public static List<String> shapesList = new ArrayList<String>(Arrays.asList("diamond", "octagon", "invtrapezium",
			"triangle", "box", "trapezium", "invtriangle", "parallelogram", "polygon", "egg"));
	
	public static List<String> zcolors = new ArrayList<String>(Arrays.asList("#99BBFF", "#99CCFF", "#99DDFF", "#99EEFF",
			"#99FFFF", "#FFFFFF", "#FFEE44", "#FFCC44", "#FFBB44", "#FFAA44", "#FF9944"));
	
	public static double sigmoid(double x, double theta) {
		if(theta==0.0) {
			theta=0.25;
		}
		
		return (1/(1 + Math.exp(-theta * x)));
	}
	
	public static double mz_tolerance (double mz, String mode) {
		try {
			int mod=Integer.parseInt(mode);
			return 0.000001 * mod * mz;
		}catch(Exception e) {
			if(mode.equalsIgnoreCase("FTMS")) {
				return Math.max(0.00001*mz, (3*(Math.pow(2,(1.98816* Math.log(mz) - 26.1945)))));
			}else if(mode.equalsIgnoreCase("")) {
				return 0.000010*mz;
				
			}else {
				return 0.000010*mz;
			}
		}
		
	}
	
	public static List<String> primary_ions= new ArrayList<String>(Arrays.asList("M+H[1+]", "M+Na[1+]", "M-H2O+H[1+]", "M-H[-]", "M-2H[2-]", "M-H2O-H[-]"));
	
	
	public static final Map<String, List<String>> wanted_adduct_list;
	static{
		wanted_adduct_list=new HashMap<String,List<String>>();
			List<String> pos_default = new ArrayList<String>(Arrays.asList("M[1+]", "M+H[1+]", "M+2H[2+]", "M(C13)+H[1+]", "M(C13)+2H[2+]", 
	                "M+Na[1+]", "M+H+Na[2+]", "M+HCOONa[1+]"));
			List<String> dpj_positive = new ArrayList<String>(Arrays.asList("M[1+]", "M+H[1+]", "M+2H[2+]", "M(C13)+H[1+]", "M(C13)+2H[2+]", 
	                "M+Na[1+]", "M+H+Na[2+]", "M+HCOONa[1+]",
	                "M(Cl37)+H[1+]", "M(S34)+H[1+]", "M+K[1+]", "M+HCOOK[1+]"));
			List <String> generic_positive = new ArrayList<String>(Arrays.asList("M[1+]","M+H[1+]","M+2H[2+]","M+3H[3+]","M(C13)+H[1+]","M(C13)+2H[2+]",
	                "M(C13)+3H[3+]","M(S34)+H[1+]","M(Cl37)+H[1+]","M+Na[1+]","M+H+Na[2+]","M+K[1+]",
	                "M+H2O+H[1+]","M-H2O+H[1+]","M-H4O2+H[1+]","M-NH3+H[1+]","M-CO+H[1+]",
	                "M-CO2+H[1+]","M-HCOOH+H[1+]","M+HCOONa[1+]","M-HCOONa+H[1+]","M+NaCl[1+]",
	                "M-C3H4O2+H[1+]","M+HCOOK[1+]","M-HCOOK+H[1+]"));
			List <String> negative= new ArrayList<String>(Arrays.asList("M-H[-]","M-2H[2-]","M(C13)-H[-]","M(S34)-H[-]","M(Cl37)-H[-]",
	                "M+Na-2H[-]","M+K-2H[-]","M-H2O-H[-]","M+Cl[-]","M+Cl37[-]",
	                "M+Br[-]","M+Br81[-]","M+ACN-H[-]","M+HCOO[-]","M+CH3COO[-]","M-H+O[-]"));
			
			wanted_adduct_list.put("pos_default", pos_default);
			wanted_adduct_list.put("dpj_positive", dpj_positive);
			wanted_adduct_list.put("generic_positive", generic_positive);
			wanted_adduct_list.put("negative", negative);
		
	}
	
	//This is left empty as this was not being used in python code.
	public static ArrayList<?> adduct_function(double mw, String mode){
		return null;
		
		
	}
	
	public static Map<String,Integer> dict_weight_adduct;
	
	static {
		dict_weight_adduct= new HashMap<String,Integer>();
		dict_weight_adduct.put("M[1+]", 5);
		dict_weight_adduct.put("M+H[1+]", 5);
		dict_weight_adduct.put("M+2H[2+]", 3);
		dict_weight_adduct.put("M+3H[3+]", 1);
		dict_weight_adduct.put("M+3H[3+]", 1);
		dict_weight_adduct.put("M(C13)+2H[2+]", 1);
		dict_weight_adduct.put("M(C13)+3H[3+]", 1);
		dict_weight_adduct.put("M(S34)+H[1+]", 1);
		dict_weight_adduct.put("M(Cl37)+H[1+]", 1);
		dict_weight_adduct.put("M+Na[1+]", 3);
		dict_weight_adduct.put("M+H+Na[2+]", 2);
		dict_weight_adduct.put("M+K[1+]", 2);
		dict_weight_adduct.put("M+H2O+H[1+]", 1);
		dict_weight_adduct.put("M-H2O+H[1+]", 1);
		dict_weight_adduct.put("M-H4O2+H[1+]", 1);
		dict_weight_adduct.put("M-NH3+H[1+]", 1);
		dict_weight_adduct.put("M-CO+H[1+]", 1);
		dict_weight_adduct.put("M-CO2+H[1+]", 1);
		dict_weight_adduct.put("M-HCOOH+H[1+]", 1);
		dict_weight_adduct.put("M+HCOONa[1+]", 1);
		dict_weight_adduct.put("M-HCOONa+H[1+]", 1);
		dict_weight_adduct.put("M+NaCl[1+]", 1);
		dict_weight_adduct.put("M-C3H4O2+H[1+]", 1);
		dict_weight_adduct.put("M+HCOOK[1+]", 1);
		dict_weight_adduct.put("M-HCOOK+H[1+]", 1);
		dict_weight_adduct.put("M-H[-]", 5);
		dict_weight_adduct.put("M-2H[2-]", 3);
		dict_weight_adduct.put("M(C13)-H[-]", 2);
		dict_weight_adduct.put("M(S34)-H[-]", 1);
		dict_weight_adduct.put("M(Cl37)-H[-]", 1);
		dict_weight_adduct.put("M+Na-2H[-]", 2);
		dict_weight_adduct.put("M+K-2H[-]", 1);
		dict_weight_adduct.put("M-H2O-H[-]", 1);
		dict_weight_adduct.put("M+Cl[-]", 1);
		dict_weight_adduct.put("M+Cl37[-]", 1);
		dict_weight_adduct.put("M+Br[-]", 1);
		dict_weight_adduct.put("M+Br81[-]", 1);
		dict_weight_adduct.put("M+ACN-H[-]", 1);
		dict_weight_adduct.put("M+HCOO[-]", 1);
		dict_weight_adduct.put("M+CH3COO[-]", 1);
		dict_weight_adduct.put("M-H+O[-]", 1);		
			
		
	}
	
	public static Map<String,String> optionsMap;
	
	static {
		long time_stamp = System.currentTimeMillis();
		optionsMap=new HashMap<String,String>();
		optionsMap.put("cutoff","0");
		optionsMap.put("network","human_mfn");
		optionsMap.put("modeling", "None");
		optionsMap.put("mode","pos_default");
		optionsMap.put("instrument","unspecified");
		optionsMap.put("force_primary_ion","True");
		optionsMap.put("workdir","");
		optionsMap.put("input","");
		optionsMap.put("reference","");
		optionsMap.put("infile","");
		optionsMap.put("output","");
		optionsMap.put("permutation","100");
		optionsMap.put("outdir","mcgresult" +time_stamp);	
	}
	
public static Map<String,Boolean> booleanMap;
	
	static {
		booleanMap=new HashMap<String,Boolean>();
		booleanMap.put("T",true);
		booleanMap.put("F", false);
		booleanMap.put("TRUE",true);
		booleanMap.put("FALSE",false);
		booleanMap.put("true",true);
		booleanMap.put("false",false);	
	}
	
	
public static Map<String,String> modesMap;
	
	static {
		modesMap=new HashMap<String,String>();
		modesMap.put("default","pos_default");
		modesMap.put("pos", "pos_default");
		modesMap.put("pos_default","pos_default");
		modesMap.put("dpj","dpj_positive");
		modesMap.put("positive","generic_positive");
		modesMap.put("Positive","generic_positive");
		modesMap.put("negative","negative");
		modesMap.put("Negative","negative");
	}
	
	
	


}
