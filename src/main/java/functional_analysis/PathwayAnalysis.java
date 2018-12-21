package functional_analysis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Sets;

import org.apache.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import pojo.CPD2mzFeatures;
import pojo.MetabolicPathwayPOJO;
import Models.MetabolicPathway;
import getuserdata.DataMeetModel;
import getuserdata.EmpiricalCompound;
import net.maizegenetics.stats.statistics.FisherExact;
import pojo.RowEmpcpd;
import resources.Constants;
import smile.data.AttributeDataset.Row;
import smile.stat.distribution.GammaDistribution;
import utils.ReservoirSampling;

public class PathwayAnalysis {

	private DataMeetModel mixedNetwork;
	private Graph<String, DefaultEdge> network;
	private Map<String, String> paradict;
	private List<MetabolicPathway> pathways;
	private List<RowEmpcpd> trioList;
	private Set<EmpiricalCompound> significantEmpiricalCompounds;
	private List<EmpiricalCompound> ListOfEmpiricalCompounds;
	private int totalNnumberEmpiricalCompounds;
	private List<Double> permutationRecord;
	private List<MetabolicPathway> resultListOfPathways;

	private final static Logger LOGGER = Logger.getLogger(PathwayAnalysis.class.getName());

	public PathwayAnalysis(DataMeetModel mixedNetowrk, List<MetabolicPathwayPOJO> pathways) {
		this.mixedNetwork = mixedNetowrk;
		this.network = this.mixedNetwork.getModel().getNetwork();
		this.paradict = this.mixedNetwork.getData().getParadict();
		this.pathways = this.getPathways(pathways);
		this.significantEmpiricalCompounds = new HashSet<EmpiricalCompound>();
		this.permutationRecord = new ArrayList<Double>();
		this.resultListOfPathways = new ArrayList<MetabolicPathway>();
		// need to figure out the data type resultListOfPathways = []

		this.trioList = mixedNetowrk.getTrioList();
		for (RowEmpcpd rw : this.trioList) {
			this.significantEmpiricalCompounds.add(rw.getEmpiricalCompound());
		}
		this.ListOfEmpiricalCompounds = mixedNetowrk.get_ListOfEmpiricalCompounds();
		this.totalNnumberEmpiricalCompounds = this.ListOfEmpiricalCompounds.size();
		LOGGER.info("Starting Pathway Analysis");

	}

	public List<MetabolicPathway> getPathways(List<MetabolicPathwayPOJO> pathways) {

		List<MetabolicPathway> result = new ArrayList<MetabolicPathway>();
		MetabolicPathway metabolicPathway = null;
		for (MetabolicPathwayPOJO li : pathways) {
			metabolicPathway = new MetabolicPathway();
			metabolicPathway.setId(li.getId());
			metabolicPathway.setName(li.getName());
			metabolicPathway.setRxns(li.getRxns());
			metabolicPathway.setEcs(li.getEcs());
			metabolicPathway.setEcnum(li.getEcs().size());
			metabolicPathway.setCpds(li.getCpds());
			metabolicPathway.setCpd_num(li.getCpds().size());
			// P.adjusted_p = '' Not sure what this is in the original code
			metabolicPathway.setAdjust_p(0.0);
			metabolicPathway.setEmpiricalCompounds(this.get_empiricalCompounds_by_cpds(metabolicPathway.getCpds()));
			result.add(metabolicPathway);
		}

		return result;

	}

	public Set<EmpiricalCompound> get_empiricalCompounds_by_cpds(List<String> cpds) {
		Set<EmpiricalCompound> result = new HashSet<EmpiricalCompound>();
		List<EmpiricalCompound> empiricalCompoundlist = null;
		for (String cpd : cpds) {
			empiricalCompoundlist = mixedNetwork.getCompounds_to_EmpiricalCompounds().get(cpd);
			if (empiricalCompoundlist != null) {
				result.addAll(empiricalCompoundlist);
			}
		}

		return result;
	}

	public void cpd_enrich_test() {
		List<MetabolicPathway> fet_tested_pathways = new ArrayList<MetabolicPathway>();
		Set<EmpiricalCompound> qset = new HashSet<EmpiricalCompound>();
		qset.addAll(this.significantEmpiricalCompounds);
		int query_set_size = qset.size();
		int total_feature_num = this.totalNnumberEmpiricalCompounds;

		for (MetabolicPathway mp : this.pathways) {
			mp.setOverlapEmpiricalCompunds(Sets.intersection(qset, mp.getEmpiricalCompounds()));
			mp.setOverlapFeatures(Sets.intersection(qset, mp.getEmpiricalCompounds()));
			mp.setOverlapSize(mp.getOverlapEmpiricalCompunds().size());
			int overlapSize = mp.getOverlapSize();
			mp.setEmpSize(mp.getEmpiricalCompounds().size());
			int epcdNum = mp.getEmpSize();
			if (overlapSize > 0) {
				int nonneg = total_feature_num + overlapSize - epcdNum - query_set_size;

				// Perform Fisher Exact Test
				// p_FET = stats.fisher_exact([[overlap_size, query_set_size - overlap_size],
				// [ecpd_num - overlap_size, negneg]], 'greater')[1]
				mp.setpFet(FisherExact.getInstance(20000).getRightTailedP(overlapSize, (query_set_size - overlapSize),
						(epcdNum - overlapSize), nonneg));

				mp.setpEase(mp.getpFet());

			} else {
				mp.setpEase(1);
				mp.setpFet(1);
			}

			fet_tested_pathways.add(mp);
		}

		this.resultListOfPathways = get_adjust_p_by_permutations(fet_tested_pathways);
		// For Debugging
		// printMinMax();

		Collections.sort(this.resultListOfPathways, (a, b) -> Double.compare(a.getAdjust_p(), b.getAdjust_p()));
		System.out.println("Pathway Analysis Completed");

	}

	// This function is for debugging only
	void printMinMax() {
		double min = +1000.0;
		double max = -1000.0;

		for (MetabolicPathway mp : this.resultListOfPathways) {
			if (mp.getAdjust_p() > max) {
				max = mp.getAdjust_p();
			}

			if (mp.getAdjust_p() < min) {
				min = mp.getAdjust_p();
			}
		}

		System.out.println("Max value is " + max);
		System.out.println("Min values is " + min);

	}

	double[] giveDoubleArray(List<Double> input) {
		double[] result = new double[input.size()];

		for (int i = 0; i < input.size(); i++) {
			result[i] = input.get(i);
		}
		return result;
	}

	public List<MetabolicPathway> get_adjust_p_by_permutations(List<MetabolicPathway> pathways) {
		this.do_permutations(pathways, Integer.parseInt(this.paradict.get("permutation")));

		if (this.paradict.get("modeling").equalsIgnoreCase("gamma")) {
			// if(true) {
			// TODO need to correct vector fit
			List<Double> vectorToFit = new ArrayList<Double>();
			for (Double d : this.permutationRecord) {
				vectorToFit.add(-1 * Math.log10(d));
			}

			GammaDistribution gammaDistribution = new GammaDistribution(giveDoubleArray(vectorToFit));
			for (MetabolicPathway mp : pathways) {
				mp.setAdjust_p((1 - gammaDistribution.cdf(-1 * Math.log10(mp.getpEase()))));
			}

		} else {
			for (MetabolicPathway mp : pathways) {
				mp.setAdjust_p(this.calculatePValue(mp.getpEase(), this.permutationRecord));
			}
		}
		return pathways;
	}

	double calculatePValue(double x, List<Double> record) {

		List<Double> total_scores = new ArrayList<Double>();
		total_scores.add(x);
		total_scores.addAll(record);
		// Collections.sort(total_scores, Collections.reverseOrder());
		Collections.sort(total_scores);
		double d = total_scores.size() + 1.0;
		return (total_scores.indexOf(x) + 1) / d;

	}

	public void do_permutations(List<MetabolicPathway> pathways, int numOfPerm) {

		LOGGER.info("Resampling " + numOfPerm + "permutations to estimate background ...");
		List<RowEmpcpd> randomTrioList;
		List<EmpiricalCompound> queryEmpriricalCompunds = new ArrayList<EmpiricalCompound>();

		int n = this.mixedNetwork.getSignificant_features().size();
		System.out.println();
		for (int i = 0; i < numOfPerm; i++) {
			// Why this
			// sys.stdout.write( ' ' + str(ii + 1))
			// sys.stdout.flush()
			System.out.print(" " + (i + 1));
			queryEmpriricalCompunds.clear();
			randomTrioList = this.mixedNetwork
					.batch_rowindex_EmpCpd_Cpd(ReservoirSampling.selectKItems(this.mixedNetwork.getMzrows(), n));
			for (RowEmpcpd row : randomTrioList) {
				queryEmpriricalCompunds.add(row.getEmpiricalCompound());
			}
			//
			this.permutationRecord.addAll(this.calculate_permutation_value(queryEmpriricalCompunds, pathways));
		}
		System.out.println();
		LOGGER.info("Pathway background is estimated on " + this.permutationRecord.size() + " random pathway values");

	}

	public List<Double> calculate_permutation_value(List<EmpiricalCompound> queryEmpriricalCompunds,
			List<MetabolicPathway> pathways) {
		List<Double> result = new ArrayList<Double>();
		int querySetSize = queryEmpriricalCompunds.size();
		int totalFeatures = this.totalNnumberEmpiricalCompounds;
		Set overlapCompunds;

		for (MetabolicPathway mp : pathways) {

			overlapCompunds = Sets.intersection(new HashSet<EmpiricalCompound>(queryEmpriricalCompunds),
					mp.getEmpiricalCompounds());
			int overlapSize = overlapCompunds.size();
			int epcdNum = mp.getEmpiricalCompounds().size();
			if (overlapSize > 0) {
				int nonneg = totalFeatures + overlapSize - epcdNum - querySetSize;

				//  Perform Fisher Exact Test
				// p_FET = stats.fisher_exact([[overlap_size, query_set_size - overlap_size],
				// [ecpd_num - overlap_size, negneg]], 'greater')[1]
				// double pval=0;
				result.add(FisherExact.getInstance(20000).getRightTailedP(overlapSize, (querySetSize - overlapSize),
						(epcdNum - overlapSize), nonneg));
			} else {
				result.add(1.0);
			}

		}

		return result;
	}

	public List<RowEmpcpd> collectHitTrios() {
		List<RowEmpcpd> result = new ArrayList<RowEmpcpd>();
		Set<EmpiricalCompound> overlapEmpiricalCompounds = new HashSet<EmpiricalCompound>();

		for (MetabolicPathway mp : this.resultListOfPathways) {
			if (mp.getAdjust_p() < Constants.SIGNIFICANCE_CUTOFF) {
				overlapEmpiricalCompounds = Sets.union(overlapEmpiricalCompounds, mp.getOverlapEmpiricalCompunds());
			}
		}

		for (RowEmpcpd row : this.trioList) {
			if (overlapEmpiricalCompounds.contains(row.getEmpiricalCompound())
					&& this.mixedNetwork.getSignificant_features().contains(row.getRow())) {
				row.getEmpiricalCompound().update_chosen_cpds(row.getCompound());
				row.getEmpiricalCompound().designate_face_cpd();
				result.add(row);
			}
		}
		return result;

	}

}
