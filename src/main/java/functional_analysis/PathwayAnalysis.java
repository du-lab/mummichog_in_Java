package functional_analysis;

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
import pojo.MetabolicPathwayPOJO;
import Models.MetabolicPathway;
import driver.ExecuteMummiChog;
import getuserdata.DataMeetModel;
import getuserdata.EmpiricalCompound;
import net.maizegenetics.stats.statistics.FisherExact;
import pojo.RowEmpcpd;
import resources.Constants;
import smile.stat.distribution.GammaDistribution;
import utils.ReservoirSampling;

/*
 * From matched features to pathway enrichment analysis. Using mfn human pathways for now. p-value
 * is from Fisher exact test, adjusted by resampling method in GF Berriz, OD King, B Bryant, C
 * Sander & FP Roth. Characterizing gene sets with FuncAssociate. Bioinformatics 19(18):2502-2504
 * (2003)
 * 
 * "Adjusted_p" is not an accurate term. It's rather an empirical p-value.
 */

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
  private ExecuteMummiChog emc;


  public List<MetabolicPathway> getResultListOfPathways() {
    return resultListOfPathways;
  }

  public void setResultListOfPathways(List<MetabolicPathway> resultListOfPathways) {
    this.resultListOfPathways = resultListOfPathways;
  }

  private final static Logger LOGGER = Logger.getLogger(PathwayAnalysis.class.getName());

  public PathwayAnalysis(DataMeetModel mixedNetowrk, List<MetabolicPathwayPOJO> pathways,
      ExecuteMummiChog executeMummiChog) {
    /*
     * mixedNetwork contains both user input data, metabolic model, and mapping btw (mzFeature,
     * EmpiricalCompound, cpd)
     */
    this.mixedNetwork = mixedNetowrk;
    this.emc = executeMummiChog;
    this.setNetwork(this.mixedNetwork.getModel().getNetwork());
    this.paradict = this.mixedNetwork.getData().getParadict();
    this.pathways = this.getPathways(pathways);
    this.significantEmpiricalCompounds = new HashSet<EmpiricalCompound>();
    this.permutationRecord = new ArrayList<Double>();
    this.resultListOfPathways = new ArrayList<MetabolicPathway>();

    this.trioList = mixedNetowrk.getTrioList();
    for (RowEmpcpd rw : this.trioList) {
      this.significantEmpiricalCompounds.add(rw.getEmpiricalCompound());
    }
    this.ListOfEmpiricalCompounds = mixedNetowrk.get_ListOfEmpiricalCompounds();
    this.totalNnumberEmpiricalCompounds = this.ListOfEmpiricalCompounds.size();
    LOGGER.info("Starting Pathway Analysis");

  }

  public List<MetabolicPathway> getPathways(List<MetabolicPathwayPOJO> pathways) {

    /*
     * convert pathways in JSON formats (import from .py) to list of Pathway class. Adding list of
     * EmpiricalCompounds per pathway, which reflects the measured pathway coverage.
     */

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
      metabolicPathway.setAdjust_p(0.0);
      metabolicPathway
          .setEmpiricalCompounds(this.get_empiricalCompounds_by_cpds(metabolicPathway.getCpds()));
      result.add(metabolicPathway);
    }

    return result;

  }

  public Set<EmpiricalCompound> get_empiricalCompounds_by_cpds(List<String> cpds) {
    /*
     * Mapping cpds to empirical_cpds. Also used for counting EmpCpds for each Pathway.
     */
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
    /*
     * Fisher Exact Test in cpd space, after correction of detected cpds. Fisher exact test is using
     * scipy.stats.fisher_exact for right-tail p-value: >>> stats.fisher_exact([[12, 5], [29, 2]],
     * 'greater')[1] 0.99452520602188932
     * 
     * query size is now counted by EmpiricalCompounds. adjusted_p should be model p-value, not fdr.
     * This returns a list of Pathway instances, with p-values.
     * 
     */
    List<MetabolicPathway> fet_tested_pathways = new ArrayList<MetabolicPathway>();
    Set<EmpiricalCompound> qset = new HashSet<EmpiricalCompound>();
    qset.addAll(this.significantEmpiricalCompounds);
    int query_set_size = qset.size();
    int total_feature_num = this.totalNnumberEmpiricalCompounds;

    for (MetabolicPathway mp : this.pathways) {
      mp.setOverlapEmpiricalCompunds(
          new ArrayList<EmpiricalCompound>(Sets.intersection(qset, mp.getEmpiricalCompounds())));
      mp.setOverlapFeatures(
          new ArrayList<EmpiricalCompound>(Sets.intersection(qset, mp.getEmpiricalCompounds())));
      mp.setOverlapSize(mp.getOverlapEmpiricalCompunds().size());
      int overlapSize = mp.getOverlapSize();
      mp.setEmpSize(mp.getEmpiricalCompounds().size());
      int epcdNum = mp.getEmpSize();
      if (overlapSize > 0) {
        int nonneg = total_feature_num + overlapSize - epcdNum - query_set_size;
        mp.setpFet(FisherExact.getInstance(20000).getRightTailedP(overlapSize,
            (query_set_size - overlapSize), (epcdNum - overlapSize), nonneg));

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

    Collections.sort(this.resultListOfPathways,
        (a, b) -> Double.compare(a.getAdjust_p(), b.getAdjust_p()));

    // System.out.println("Sno "+"Name "+"PFet "+"PAdjusted");
    // int i=0;
    // for(MetabolicPathway path: this.resultListOfPathways) {
    // i++;
    // System.out.print(i+" "+path.getName()+" "+path.getpFet()+" "+path.getAdjust_p());
    // System.out.println();
    // }
    //
    LOGGER.info("Pathway Analysis Completed");
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
    /*
     * EASE score is used as a basis for adjusted p-values, as mummichog encourages bias towards
     * more hits/pathway. pathways were already updated by first round of Fisher exact test, to
     * avoid redundant calculations
     */

    this.do_permutations(pathways, Integer.parseInt(this.paradict.get("permutation")));

    if (this.paradict.get("modeling").equalsIgnoreCase("gamma")) {
      List<Double> vectorToFit = new ArrayList<Double>();
      for (Double d : this.permutationRecord) {
        double rescal = -1 * Math.log10(d);
        if (rescal <= 0.0) {
          rescal = 0.00000000000000000000000000000000000000000000001;
        }
        vectorToFit.add(rescal);
      }

      GammaDistribution gammaDistribution = new GammaDistribution(giveDoubleArray(vectorToFit));
      System.out.println("Scale of distribution " + gammaDistribution.getScale());
      System.out.println("Entropy  " + gammaDistribution.entropy());
      System.out.println("Shape" + gammaDistribution.getShape());
      System.out.println("Mean of Distribution" + gammaDistribution.mean());
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
    // calculate p-value based on the rank in record of permutation p-values

    List<Double> total_scores = new ArrayList<Double>();
    total_scores.add(x);
    total_scores.addAll(record);
    Collections.sort(total_scores);
    double d = record.size() + 1.0;
    return (total_scores.indexOf(x) + 1) / d;

  }

  public void do_permutations(List<MetabolicPathway> pathways, int numOfPerm) {
    /*
     * Modified from Berriz et al 2003 method. After collecting p-values from resampling, do a Gamma
     * fit.
     * 
     */

    LOGGER.info("Resampling " + numOfPerm + "permutations to estimate background ...");
    List<RowEmpcpd> randomTrioList;
    Set<EmpiricalCompound> queryEmpriricalCompunds = new HashSet<EmpiricalCompound>();

    int n = this.mixedNetwork.getSignificant_features().size();
    System.out.println();
    for (int i = 0; i < numOfPerm; i++) {
      if (this.emc != null) {
        emc.setProgress(emc.getProgress() + 28.0 / numOfPerm);
        System.out.println("The value of pathway progress is " + emc.getProgress());
      }
      System.out.print(" " + (i + 1));
      queryEmpriricalCompunds.clear();
      randomTrioList = this.mixedNetwork.batch_rowindex_EmpCpd_Cpd(
          ReservoirSampling.selectKItems(this.mixedNetwork.getMzrows(), n));
      for (RowEmpcpd row : randomTrioList) {
        queryEmpriricalCompunds.add(row.getEmpiricalCompound());
      }
      //
      this.permutationRecord.addAll(this.calculate_permutation_value(
          new ArrayList<EmpiricalCompound>(queryEmpriricalCompunds), pathways));
    }
    System.out.println();
    LOGGER.info("Pathway background is estimated on " + this.permutationRecord.size()
        + " random pathway values");

  }

  public List<Double> calculate_permutation_value(List<EmpiricalCompound> queryEmpriricalCompunds,
      List<MetabolicPathway> pathways) {
    /*
     * calculate the FET p-value for all pathways. But not save anything to Pathway instances.
     */
    List<Double> result = new ArrayList<Double>();
    int querySetSize = queryEmpriricalCompunds.size();
    int totalFeatures = this.totalNnumberEmpiricalCompounds;
    @SuppressWarnings("rawtypes")
    Set overlapCompunds;

    for (MetabolicPathway mp : pathways) {

      overlapCompunds = Sets.intersection(new HashSet<EmpiricalCompound>(queryEmpriricalCompunds),
          mp.getEmpiricalCompounds());
      int overlapSize = overlapCompunds.size();
      int epcdNum = mp.getEmpiricalCompounds().size();
      if (overlapSize > 0) {
        int nonneg = totalFeatures + overlapSize - epcdNum - querySetSize;

        result.add(FisherExact.getInstance(20000).getRightTailedP(overlapSize,
            (querySetSize - overlapSize), (epcdNum - overlapSize), nonneg));
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
        overlapEmpiricalCompounds =
            Sets.union(new HashSet<EmpiricalCompound>(overlapEmpiricalCompounds),
                new HashSet<EmpiricalCompound>(mp.getOverlapEmpiricalCompunds()));
      }
    }
    List<EmpiricalCompound> checkList = new ArrayList<EmpiricalCompound>(overlapEmpiricalCompounds);

    for (RowEmpcpd row : this.trioList) {

      if (checkList.contains(row.getEmpiricalCompound())
          && this.mixedNetwork.getSignificant_features().contains(row.getRow())) {
        row.getEmpiricalCompound().update_chosen_cpds(row.getCompound());
        row.getEmpiricalCompound().designate_face_cpd();
        result.add(row);
      }
    }
    return result;

  }

  public Graph<String, DefaultEdge> getNetwork() {
    return network;
  }

  public void setNetwork(Graph<String, DefaultEdge> network) {
    this.network = network;
  }

}
