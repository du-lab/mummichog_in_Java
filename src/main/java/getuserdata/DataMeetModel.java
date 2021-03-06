package getuserdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import Models.MetabolicNetwork;
import pojo.CPD2mzFeatures;
import pojo.MetabolicModel;
import pojo.RowEmpcpd;
import resources.Constants;

public class DataMeetModel {

  private MetabolicNetwork model;
  private InputUserData data;
  private double rtime_tolerance;
  private double max_retention_time;
  private Map<Integer, List<List<String>>> ionCpdTree;
  private Map<String, List<CPD2mzFeatures>> cpd2mzFeatures;
  private Map<String, MassFeature> rowDict;
  private List<EmpiricalCompound> listOfEmpiricalCompounds;
  private List<String> significant_features;
  private Map<String, List<EmpiricalCompound>> rowindex_to_EmpiricalCompounds;
  private Map<String, List<EmpiricalCompound>> compounds_to_EmpiricalCompounds;
  private List<String> mzrows;
  private List<RowEmpcpd> trioList;
  private List<RowEmpcpd> hitTriosList;

  private final static Logger LOGGER = Logger.getLogger(DataMeetModel.class.getName());

  public DataMeetModel(MetabolicNetwork theoreticalModel, InputUserData userData) {
    this.model = theoreticalModel;
    this.data = userData;

    // retention time window for grouping, based on fraction of max rtime
    this.rtime_tolerance =
        this.data.getMax_retention_time() * Constants.RETENTION_TIME_TOLERANCE_FRAC;

    // Data Structures
    this.ionCpdTree = this.build_cpdindex(this.data.getParadict().get("mode"));
    this.rowDict = this.build_rowindex(this.data.getListOfMassFeatures());
    this.listOfEmpiricalCompounds = this.get_ListOfEmpiricalCompounds();

    // Reference list
    this.mzrows = new ArrayList<String>();
    for (MassFeature mf : this.data.getListOfMassFeatures()) {
      this.mzrows.add(mf.getRow_number());
    }

    this.rowindex_to_EmpiricalCompounds = this.make_rowindex_to_EmpiricalCompounds();
    this.compounds_to_EmpiricalCompounds = this.index_Compounds_to_EmpiricalCompounds();

    // this is significant feature list
    this.significant_features = this.data.getInput_featurelist();
    this.trioList = this.batch_rowindex_EmpCpd_Cpd(this.significant_features);
  }

  public Map<Integer, List<List<String>>> build_cpdindex(String msmode) {

    /*
     * indexed Compound list, to speed up m/z matching. Limited to MASS_RANGE (default 50 ~ 2000
     * dalton).
     * 
     * 
     * changing from adduct_function to wanted_adduct_list dictionary
     * 
     * wanted_adduct_list['pos_default'] = ['M[1+]', 'M+H[1+]', 'M+2H[2+]', 'M(C13)+H[1+]',
     * 'M(C13)+2H[2+]', 'M+Na[1+]', 'M+H+Na[2+]', 'M+HCOONa[1+]'
     * 
     */

    List<String> wanted_ions = Constants.wanted_adduct_list.get(msmode);
    // Declaring IonCpdTree as a Map rather than an array of Lists as it will be a
    // waste of memory
    Map<Integer, List<List<String>>> IonCpdTree = new HashMap<Integer, List<List<String>>>();

    for (String key1 : this.model.getMetabolicModel().getCompounds().keySet()) {
      if (this.model.getMetabolicModel().getCompounds().containsKey(key1)) {
        for (String ion : this.model.getMetabolicModel().getCompounds().get(key1).getAdducts()
            .keySet()) {
          List<List<String>> cpdTreeList = null;
          if (wanted_ions.contains(ion)
              && Constants.MASS_RANGE.get(0) < this.model.getMetabolicModel().getCompounds()
                  .get(key1).getAdducts().get(ion)
              && Constants.MASS_RANGE.get(1) > this.model.getMetabolicModel().getCompounds()
                  .get(key1).getAdducts().get(ion)) {
            List<String> temp = new ArrayList<String>();
            temp.add(key1);
            temp.add(ion);
            temp.add(this.model.getMetabolicModel().getCompounds().get(key1).getAdducts().get(ion)
                .toString());
            if (IonCpdTree.containsKey(this.model.getMetabolicModel().getCompounds().get(key1)
                .getAdducts().get(ion).intValue())) {
              IonCpdTree.get(new Integer(this.model.getMetabolicModel().getCompounds().get(key1)
                  .getAdducts().get(ion).intValue())).add(temp);
            } else {
              cpdTreeList = new ArrayList<List<String>>();
              cpdTreeList.add(temp);
              IonCpdTree.put(new Integer(this.model.getMetabolicModel().getCompounds().get(key1)
                  .getAdducts().get(ion).intValue()), cpdTreeList);
            }
          }
        }
      }
    }
    return IonCpdTree;
  }

  public Map<String, MassFeature> build_rowindex(List<MassFeature> listOfMassFeatures) {
    // Index list of MassFeatures by row# in input data

    Map<String, MassFeature> rowDict = new HashMap<String, MassFeature>();

    for (MassFeature m : listOfMassFeatures) {
      rowDict.put(m.getRow_number(), m);
    }

    return rowDict;

  }

  public List<EmpiricalCompound> match_all_to_all() {
    /*
     * Here, we produce mapping dictionaries for mzFeatures to theoretical ions Compounds to
     * mzFeatures Then, EmpiricalCompounds are determined within Compound matched mzFeatures,
     * considering retention time.
     */
    this.match_to_mzFeatures();
    this.cpd2mzFeatures = this.index_Compounds_to_mzFeatures();
    return this.compound_to_EmpiricalCompounds();
  }

  public Map<String, List<CPD2mzFeatures>> index_Compounds_to_mzFeatures() {
    /*
     * compound ID - mzFeatures run after self.__match_to_mzFeatures__() L: (compoundID, ion, mass)
     * cpd2mzFeatures[compoundID] = [(ion, mass, mzFeature), ...]
     */

    Map<String, List<CPD2mzFeatures>> cpd2mzFeatures = new HashMap<String, List<CPD2mzFeatures>>();
    List<CPD2mzFeatures> listOfFeatures = null;

    for (MassFeature mf : this.data.getListOfMassFeatures()) {
      for (List<String> li : mf.getMatched_Ions()) {
        if (!cpd2mzFeatures.containsKey(li.get(0))) {
          listOfFeatures = new ArrayList<CPD2mzFeatures>();
          listOfFeatures.add(new CPD2mzFeatures(li.get(1), Double.parseDouble(li.get(2)), mf));
          cpd2mzFeatures.put(li.get(0), listOfFeatures);
        } else {
          cpd2mzFeatures.get(li.get(0))
              .add(new CPD2mzFeatures(li.get(1), Double.parseDouble(li.get(2)), mf));
        }
      }
    }
    return cpd2mzFeatures;

  }

  public List<EmpiricalCompound> compound_to_EmpiricalCompounds() {
    /*
     * EmpiricalCompounds are constructed in this function. First splitting features matching to
     * same compound by retention time; then merging those matched to same m/z features. run after
     * self.index_Compounds_to_mzFeatures()
     * 
     * 
     */
    List<EmpiricalCompound> listOfEmpiricalCompounds = new ArrayList<EmpiricalCompound>();
    for (String k : this.cpd2mzFeatures.keySet()) {
      listOfEmpiricalCompounds
          .addAll(this.split_Compound(k, this.cpd2mzFeatures.get(k), this.rtime_tolerance));
    }

    return this.merge_EmpiricalCompounds(listOfEmpiricalCompounds);
  }

  public List<EmpiricalCompound> split_Compound(String compoundID,
      List<CPD2mzFeatures> list_match_mzFeatures, Double rtime_tolerance) {
    /*
     * Determine EmpiricalCompounds among the ions matched to a Compound; return list of
     * EmpiricalCompounds (not final, but initiated here).
     * 
     * The retention time is grouped by tolerance value; This method should be updated in the
     * future.
     * 
     * input data format: cpd2mzFeatures[compoundID] = list_match_mzFeatures = [(ion, mass,
     * mzFeature), ...]
     * 
     * 
     */
    List<List<String>> all_mzFeatures = new ArrayList<List<String>>();
    List<EmpiricalCompound> eCompounds = new ArrayList<EmpiricalCompound>();
    List<List<String>> temp = new ArrayList<List<String>>();

    for (CPD2mzFeatures cf : list_match_mzFeatures) {
      List<String> tempList = new ArrayList<String>();
      tempList.add(cf.getMassFeature().getRetention_time().toString());
      tempList.add(cf.getMassFeature().getRow_number());
      tempList.add(cf.getIon());
      tempList.add(cf.getMass().toString());
      tempList.add(compoundID);
      tempList.add(cf.getMassFeature().getCompoundID_from_user());
      all_mzFeatures.add(tempList);
    }

    Collections.sort(all_mzFeatures,
        (a, b) -> Double.compare(Double.parseDouble(a.get(0)), Double.parseDouble(b.get(0))));

    temp.add(all_mzFeatures.get(0));
    // This should not work for only one element in all_mzFeatures. The loop should
    // run till size-2 .
    if (all_mzFeatures.size() > 1) {
      for (int i = 0; i < all_mzFeatures.size() - 1; i++) {
        if ((Double.parseDouble(all_mzFeatures.get(i + 1).get(0))
            - Double.parseDouble(all_mzFeatures.get(i).get(0))) < rtime_tolerance) {
          temp.add(all_mzFeatures.get(i + 1));
        } else {
          eCompounds.add(new EmpiricalCompound(temp));
          temp.clear();
          temp.add(all_mzFeatures.get(i + 1));
        }
      }
    }

    eCompounds.add(new EmpiricalCompound(temp));
    return eCompounds;

  }

  public List<EmpiricalCompound> merge_EmpiricalCompounds(
      List<EmpiricalCompound> listOfEmpiricalCompounds) {
    /*
     * If ion/mzFeatures are the same, merge EmpiricalCompounds EmpiricalCompounds.join() adds
     * Compounds
     * 
     * Because EmpiricalCompounds.str_row_ion uses mzFeatures sorted by row_number, this is
     */

    Map<String, EmpiricalCompound> resultMap = new HashMap<String, EmpiricalCompound>();

    for (EmpiricalCompound ec : listOfEmpiricalCompounds) {
      if (resultMap.containsKey(ec.getStr_row_ion())) {
        resultMap.get(ec.getStr_row_ion()).join(ec);
      } else {
        resultMap.put(ec.getStr_row_ion(), ec);
      }
    }

    return new ArrayList<EmpiricalCompound>(resultMap.values());
  }

  public void match_to_mzFeatures() {
    for (MassFeature m : this.data.getListOfMassFeatures()) {
      m.setMatched_Ions(this.__match_mz_ion__(m.getMz(), this.ionCpdTree));
    }
  }

  public List<List<String>> __match_mz_ion__(Double mz,
      Map<Integer, List<List<String>>> IonCpdTree) {

    /*
     * L: (compoundID, ion, mass) return ions matched to m/z
     */

    int floor = mz.intValue();
    double mztol = Constants.mz_tolerance(mz, this.data.getParadict().get("mode"));
    List<List<String>> matched = new ArrayList<List<String>>();

    for (int i = floor - 1; i <= floor + 1; i++) {
      if (IonCpdTree.containsKey(i)) {
        for (List<String> l : IonCpdTree.get(i)) {
          if (Math.abs(Double.parseDouble(l.get(2)) - mz) < mztol) {
            matched.add(l);
          }
        }
      }

    }
    return matched;
  }

  public List<EmpiricalCompound> get_ListOfEmpiricalCompounds() {

    int count = 1;
    List<EmpiricalCompound> result = new ArrayList<EmpiricalCompound>();

    for (EmpiricalCompound ec : this.match_all_to_all()) {
      ec.evaluate();
      ec.seteId("E" + count);
      ec.get_mzFeature_of_highest_statistic(this.rowDict);
      count++;
      if (this.data.getParadict().containsKey("force_primary_ion")
          && this.data.getParadict().get("force_primary_ion").equalsIgnoreCase("true")) {
        if (ec.getPrimary_ion_present())
          result.add(ec);
      } else {
        result.add(ec);
      }
    }
    return result;

  }

  public Map<String, List<EmpiricalCompound>> make_rowindex_to_EmpiricalCompounds() {
    Map<String, List<EmpiricalCompound>> result = new HashMap<String, List<EmpiricalCompound>>();
    List<EmpiricalCompound> tempList;
    for (EmpiricalCompound ec : this.listOfEmpiricalCompounds) {
      for (String row : ec.getMassfeature_rows()) {
        if (result.containsKey(row)) {
          result.get(row).add(ec);
        } else {
          tempList = new ArrayList<EmpiricalCompound>();
          tempList.add(ec);
          result.put(row, tempList);
        }
      }
    }
    return result;
  }

  public Map<String, List<EmpiricalCompound>> index_Compounds_to_EmpiricalCompounds() {
    Map<String, List<EmpiricalCompound>> result = new HashMap<String, List<EmpiricalCompound>>();
    List<EmpiricalCompound> tempList;
    for (EmpiricalCompound ec : this.listOfEmpiricalCompounds) {
      for (String cmp : ec.getCompounds()) {
        if (result.containsKey(cmp)) {
          result.get(cmp).add(ec);
        } else {
          tempList = new ArrayList<EmpiricalCompound>();
          tempList.add(ec);
          result.put(cmp, tempList);
        }
      }
    }
    return result;

  }

  public List<RowEmpcpd> batch_rowindex_EmpCpd_Cpd(List<String> significant_features) {
    /*
     * Batch matching from row feature to Ecpds; Use trio data structure, (M.row_number,
     * EmpiricalCompounds, Cpd). Will be used to map for both sig list and permutation lists.
     */
    List<RowEmpcpd> result = new ArrayList<RowEmpcpd>();
    // int count_match=0;
    for (String sf : significant_features) {
      if (this.rowindex_to_EmpiricalCompounds.containsKey(sf)) {
        // count_match++;
        for (EmpiricalCompound ec : this.rowindex_to_EmpiricalCompounds.get(sf)) {
          for (String cpd : ec.getCompounds()) {
            result.add(new RowEmpcpd(sf, ec, cpd));

          }
        }
      }
    }
    // System.out.println("Count match is " + count_match);
    return result;
  }

  public MetabolicNetwork getModel() {
    return model;
  }

  public void setModel(MetabolicNetwork model) {
    this.model = model;
  }

  public InputUserData getData() {
    return data;
  }

  public void setData(InputUserData data) {
    this.data = data;
  }

  public double getRtime_tolerance() {
    return rtime_tolerance;
  }

  public void setRtime_tolerance(double rtime_tolerance) {
    this.rtime_tolerance = rtime_tolerance;
  }

  public double getMax_retention_time() {
    return max_retention_time;
  }

  public void setMax_retention_time(double max_retention_time) {
    this.max_retention_time = max_retention_time;
  }

  public Map<Integer, List<List<String>>> getIonCpdTree() {
    return ionCpdTree;
  }

  public void setIonCpdTree(Map<Integer, List<List<String>>> ionCpdTree) {
    this.ionCpdTree = ionCpdTree;
  }

  public Map<String, List<CPD2mzFeatures>> getCpd2mzFeatures() {
    return cpd2mzFeatures;
  }

  public void setCpd2mzFeatures(Map<String, List<CPD2mzFeatures>> cpd2mzFeatures) {
    this.cpd2mzFeatures = cpd2mzFeatures;
  }

  public Map<String, MassFeature> getRowDict() {
    return rowDict;
  }

  public void setRowDict(Map<String, MassFeature> rowDict) {
    this.rowDict = rowDict;
  }

  public List<EmpiricalCompound> getListOfEmpiricalCompounds() {
    return listOfEmpiricalCompounds;
  }

  public void setListOfEmpiricalCompounds(List<EmpiricalCompound> listOfEmpiricalCompounds) {
    this.listOfEmpiricalCompounds = listOfEmpiricalCompounds;
  }

  public List<String> getSignificant_features() {
    return significant_features;
  }

  public void setSignificant_features(List<String> significant_features) {
    this.significant_features = significant_features;
  }

  public Map<String, List<EmpiricalCompound>> getRowindex_to_EmpiricalCompounds() {
    return rowindex_to_EmpiricalCompounds;
  }

  public void setRowindex_to_EmpiricalCompounds(
      Map<String, List<EmpiricalCompound>> rowindex_to_EmpiricalCompounds) {
    this.rowindex_to_EmpiricalCompounds = rowindex_to_EmpiricalCompounds;
  }

  public Map<String, List<EmpiricalCompound>> getCompounds_to_EmpiricalCompounds() {
    return compounds_to_EmpiricalCompounds;
  }

  public void setCompounds_to_EmpiricalCompounds(
      Map<String, List<EmpiricalCompound>> compounds_to_EmpiricalCompounds) {
    this.compounds_to_EmpiricalCompounds = compounds_to_EmpiricalCompounds;
  }

  public List<String> getMzrows() {
    return mzrows;
  }

  public void setMzrows(List<String> mzrows) {
    this.mzrows = mzrows;
  }

  public List<RowEmpcpd> getTrioList() {
    return trioList;
  }

  public void setTrioList(List<RowEmpcpd> trioList) {
    this.trioList = trioList;
  }

  public List<RowEmpcpd> getHitTriosList() {
    return hitTriosList;
  }

  public void setHitTriosList(List<RowEmpcpd> hitTriosList) {
    this.hitTriosList = hitTriosList;
  }

}
