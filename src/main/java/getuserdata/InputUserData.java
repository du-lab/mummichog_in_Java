package getuserdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import resources.Constants;

public class InputUserData {

	private Map<String, String> paradict;
	private List<String> header_fields;
	private List<MassFeature> listOfMassFeatures;
	private List<String> input_featurelist;
	private Double max_retention_time;
	private Double max_mz;

	private final static Logger LOGGER = Logger.getLogger(InputUserData.class.getName());

	public InputUserData(Map<String, String> paradict,boolean integrationRunFlag,String inputData) {
		this.paradict = paradict;
		this.header_fields = new ArrayList<String>();
		this.listOfMassFeatures = new ArrayList<MassFeature>();
		this.input_featurelist = new ArrayList<String>();
		
		if(!integrationRunFlag) {
			this.read();
		}else {
			List<String>inputDataList= new ArrayList<String>();
			for(String line: inputData.split("\n")) {
				inputDataList.add(line);
			}
			text_to_ListOfMassFeatures(inputDataList, "\t");
		}
		
		this.determine_significant_list();

		this.max_retention_time = 0.0;
		this.max_mz = 0.0;

		for (MassFeature mf : this.listOfMassFeatures) {
			if (mf.getRetention_time() > this.max_retention_time) {
				this.max_retention_time = mf.getRetention_time();
			}
			if (mf.getMz() > this.max_mz) {
				this.max_mz = mf.getMz();
			}
		}

	}

	public void read() {
		/*
		 * Read input feature lists to ListOfMassFeatures. Row_numbers (rowii+1) are
		 * used as primary ID.
		 */
		List<String> linesOfFile = new ArrayList<String>();
		BufferedReader buf = null;
		try {
			buf = new BufferedReader(new FileReader(this.paradict.get("input")));
			String lineJustFetched = null;
			while (true) {
				lineJustFetched = buf.readLine();
				if (lineJustFetched == null) {
					break;
				} else {
					linesOfFile.add(lineJustFetched);
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				buf.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
		text_to_ListOfMassFeatures(linesOfFile, "\t");
	}

	public void text_to_ListOfMassFeatures(List<String> textLines, String delimiter) {
		// Assigning header list values and deleting it from the data.
		this.header_fields = Arrays.asList(textLines.get(0).split(delimiter));
		textLines.remove(0);
		check_redundant(textLines);
		String[] lineArray;
		int i = 1;
		List<String[]> excluded_list = new ArrayList<String[]>();
		for (String l : textLines) {
			i++;
			if (l.contains("nan")) {
				continue;
			}
			lineArray = l.split(delimiter);
			String CompoundID_from_user = "";
			if (lineArray.length > 4) {
				CompoundID_from_user = lineArray[4];
			}
			if (Constants.MASS_RANGE.get(0) < Double.parseDouble(lineArray[0])
					&& Constants.MASS_RANGE.get(1) > Double.parseDouble(lineArray[0])) {
				// System.out.println(l);

				this.listOfMassFeatures.add(new MassFeature("row" + i, Double.parseDouble(lineArray[0]),
						Double.parseDouble(lineArray[1]), Double.parseDouble(lineArray[2]),
						Double.parseDouble(lineArray[3]), CompoundID_from_user));
			} else {
				excluded_list.add(lineArray = l.split(delimiter));
			}
		}

		if (excluded_list.size() > 0) {
			LOGGER.info("Some elements were excluded");
		}

	}

	public void check_redundant(List<String> textLines) {
		Set<String> setofLines = new HashSet<String>(textLines);
		if (setofLines.size() != textLines.size()) {
			LOGGER.info("There are redundant lines in the file");
		}

	}

	public void determine_significant_list() {
		/*
		 * For single input file format in ver 2. The significant list, input_mzlist,
		 * should be a subset of ref_mzlist, determined either by user specificed
		 * --cutoff, or by automated cutoff close to a p-value hotspot, in which case,
		 * paradict['cutoff'] is updated accordingly. 
		 */
		if (!this.paradict.containsKey("cutoff")
				|| (this.paradict.containsKey("cutoff") && Double.parseDouble(this.paradict.get("cutoff")) == 0.0)) {

			List<MassFeature> newList = new ArrayList<MassFeature>(this.listOfMassFeatures);
			// Lamda function to sort list of mass features on p values
			Collections.sort(newList, (a, b) -> a.getP_value().compareTo(b.getP_value()));

			Double[] p_hotspots = { 0.2, 0.1, 0.05, 0.01, 0.005, 0.001, 0.0001 };
			List<Integer> N_hotspots = new ArrayList<Integer>();

			int count = 0;
			for (Double pval : p_hotspots) {
				count = 0;
				for (MassFeature mf : this.listOfMassFeatures) {
					if (mf.getP_value() < pval) {
						count++;
					}
				}
				N_hotspots.add(count);
			}
			Integer N_quantile = (newList.size() / 4);
			int N_optimum = 300;
			int N_minimum = 30;
			int chosen = 999;

			for (int i = 0; i < N_hotspots.size(); i++) {
				if (N_optimum < N_hotspots.get(i) && N_hotspots.get(i) < N_quantile) {
					chosen = i;
				}
			}

			if (chosen > 100) {
				for (int j = 0; j < N_hotspots.size(); j++) {
					if (N_minimum < N_hotspots.get(j) && N_hotspots.get(j) < N_quantile) {
						chosen = j;
					}
				}
			}

			if (chosen > 100) {
				int N_chosen = N_quantile;
				this.paradict.put("cutoff", newList.get(N_chosen + 1).getP_value().toString());
			} else {
				this.paradict.put("cutoff", p_hotspots[chosen].toString());
			}

		}
		for (MassFeature mf : this.listOfMassFeatures) {
			if (mf.getP_value() < Double.parseDouble(this.paradict.get("cutoff"))) {
				mf.setIs_significant(true);
				if (this.input_featurelist == null) {
					this.input_featurelist = new ArrayList<String>();
				}
				input_featurelist.add(mf.getRow_number());
			}
		}

	}

	public Map<String, String> getParadict() {
		return paradict;
	}

	public void setParadict(Map<String, String> paradict) {
		this.paradict = paradict;
	}

	public List<String> getHeader_fields() {
		return header_fields;
	}

	public void setHeader_fields(List<String> header_fields) {
		this.header_fields = header_fields;
	}

	public List<MassFeature> getListOfMassFeatures() {
		return listOfMassFeatures;
	}

	public void setListOfMassFeatures(List<MassFeature> listOfMassFeatures) {
		this.listOfMassFeatures = listOfMassFeatures;
	}

	public List<String> getInput_featurelist() {
		return input_featurelist;
	}

	public void setInput_featurelist(List<String> input_featurelist) {
		this.input_featurelist = input_featurelist;
	}

	public Double getMax_retention_time() {
		return max_retention_time;
	}

	public void setMax_retention_time(Double max_retention_time) {
		this.max_retention_time = max_retention_time;
	}

	public Double getMax_mz() {
		return max_mz;
	}

	public void setMax_mz(Double max_mz) {
		this.max_mz = max_mz;
	}

}
