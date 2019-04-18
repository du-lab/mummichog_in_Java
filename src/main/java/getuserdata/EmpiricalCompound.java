package getuserdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import resources.Constants;

/*
 * EmpiricalCompound is a computational unit to include multiple ions that belong to the same
 * metabolite, and isobaric/isomeric metabolites when not distinguished by the mass spec data.
 * Thought to be a tentative metabolite. Due to false matches, one Compound could have more
 * EmpiricalCompounds
 * 
 * In mummichog, this replaces the Mnode class in version 1; and is the compound presentation for
 * Activity network and HTML report.
 * 
 * This class serves as in between user-input MassFetaure and theoretical model Compound.
 * 
 */

public class EmpiricalCompound {

  private List<List<String>> listOfFeatures;
  private String str_row_ion;
  private String eId;
  private String face_compound;
  private List<String> chosen_compounds;
  private int evidence_score;
  private Boolean primary_ion_present;
  private int statistic;
  private List<String> compounds;
  private List<String> massfeature_rows;
  private Map<String, String> ions;
  private Map<String, String> row_to_ion;
  private MassFeature mzFeature_of_highest_statistic;

  private final static Logger LOGGER = Logger.getLogger(EmpiricalCompound.class.getName());

  public EmpiricalCompound(List<List<String>> listOfFeatures) {
    /*
     * Initiation using listOfFeatures = [[retention_time, row_number, ion, mass, compoundID], ...]
     * This will be merged and split later to get final set of EmpCpds.
     */

    this.listOfFeatures = listOfFeatures;
    this.massfeature_rows = new ArrayList<String>();
    this.chosen_compounds = new ArrayList<String>();
    this.ions = new HashMap<String, String>();
    this.row_to_ion = new HashMap<String, String>();
    this.compounds = new ArrayList<String>();
    // Sorting the list of features
    Collections.sort(this.listOfFeatures, (a, b) -> a.get(1).compareTo(b.get(1)));
    this.str_row_ion = this.make_str_row_ion();
    this.unpack_listOfFeatures();
    this.eId = "";
    this.face_compound = "";
    this.evidence_score = 0;
    this.primary_ion_present = false;
    this.statistic = 0;

  }

  public void join(EmpiricalCompound empiricalCompound) {
    /*
     * Join another instance with identical ions. str_row_ion must be the same to be joined.
     */

    for (String c : empiricalCompound.compounds) {
      if (!this.compounds.contains(c)) {
        this.compounds.add(c);
      }
    }

  }

  public String make_str_row_ion() {
    // feature order is fixed now after sorting by row_number
    StringBuilder sb = new StringBuilder();
    for (List<String> list : listOfFeatures) {
      sb.append(list.get(1)).append("_").append(list.get(2)).append(";");
    }

    return sb.toString();
  }

  public void unpack_listOfFeatures() {
    Set<String> cmpndsSet = new HashSet<String>();
    for (List<String> list : listOfFeatures) {
      cmpndsSet.add(list.get(4));
      massfeature_rows.add(list.get(1));
      ions.put(list.get(2), list.get(3));
      row_to_ion.put(list.get(1), list.get(2));
    }
    this.compounds.addAll(cmpndsSet);
  }

  public void evaluate() {

    HashSet<String> S1 = new HashSet<String>(this.ions.keySet());
    S1.retainAll(Constants.primary_ions);

    if (S1.size() > 0)
      this.primary_ion_present = true;

    for (String ion : this.ions.keySet()) {
      if (Constants.dict_weight_adduct.containsKey(ion))
        this.evidence_score = this.evidence_score + Constants.dict_weight_adduct.get(ion);
    }

  }

  public void update_chosen_cpds(String cpd) {
    if (!this.chosen_compounds.contains(cpd)) {
      this.chosen_compounds.add(cpd);
    }
  }

  public void designate_face_cpd() {
    /*
     * When there are more than one compounds suggested by pathway and module analysis, one is
     * arbitrarily designated as "face compound".
     */
    this.face_compound = this.chosen_compounds.get(this.chosen_compounds.size() - 1);
  }

  public void get_mzFeature_of_highest_statistic(Map<String, MassFeature> dict_mzFeature) {
    /*
     * Take highest abs(statistic) among all matched ions, which will give statistic value for
     * downstream output.
     */

    List<MassFeature> all = new ArrayList<MassFeature>();

    for (String row : this.massfeature_rows) {
      all.add(dict_mzFeature.get(row));
    }

    // Sorting for the highest statistic
    Collections.sort(all, new MassFeatureComparator());

    this.mzFeature_of_highest_statistic = all.get(all.size() - 1);

  }

  public List<List<String>> getListOfFeatures() {
    return listOfFeatures;
  }

  public void setListOfFeatures(List<List<String>> listOfFeatures) {
    this.listOfFeatures = listOfFeatures;
  }

  public String getStr_row_ion() {
    return str_row_ion;
  }

  public void setStr_row_ion(String str_row_ion) {
    this.str_row_ion = str_row_ion;
  }

  public String geteId() {
    return eId;
  }

  public void seteId(String eId) {
    this.eId = eId;
  }

  public String getFace_compound() {
    return face_compound;
  }

  public void setFace_compound(String face_compound) {
    this.face_compound = face_compound;
  }

  public List<String> getChosen_compounds() {
    return chosen_compounds;
  }

  public void setChosen_compounds(List<String> chosen_compounds) {
    this.chosen_compounds = chosen_compounds;
  }

  public int getEvidence_score() {
    return evidence_score;
  }

  public void setEvidence_score(int evidence_score) {
    this.evidence_score = evidence_score;
  }

  public Boolean getPrimary_ion_present() {
    return primary_ion_present;
  }

  public void setPrimary_ion_present(Boolean primary_ion_present) {
    this.primary_ion_present = primary_ion_present;
  }

  public int getStatistc() {
    return statistic;
  }

  public void setStatistc(int statistc) {
    this.statistic = statistc;
  }

  public List<String> getCompounds() {
    return compounds;
  }

  public void setCompounds(List<String> compounds) {
    this.compounds = compounds;
  }

  public List<String> getMassfeature_rows() {
    return massfeature_rows;
  }

  public void setMassfeature_rows(List<String> massfeature_rows) {
    this.massfeature_rows = massfeature_rows;
  }

  public Map<String, String> getIons() {
    return ions;
  }

  public void setIons(Map<String, String> ions) {
    this.ions = ions;
  }

  public Map<String, String> getRow_to_ion() {
    return row_to_ion;
  }

  public void setRow_to_ion(Map<String, String> row_to_ion) {
    this.row_to_ion = row_to_ion;
  }

  public MassFeature getMzFeature_of_highest_statistic() {
    return mzFeature_of_highest_statistic;
  }

  public void setMzFeature_of_highest_statistic(MassFeature mzFeature_of_highest_statistic) {
    this.mzFeature_of_highest_statistic = mzFeature_of_highest_statistic;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((chosen_compounds == null) ? 0 : chosen_compounds.hashCode());
    result = prime * result + ((compounds == null) ? 0 : compounds.hashCode());
    result = prime * result + ((eId == null) ? 0 : eId.hashCode());
    result = prime * result + evidence_score;
    result = prime * result + ((face_compound == null) ? 0 : face_compound.hashCode());
    result = prime * result + ((ions == null) ? 0 : ions.hashCode());
    result = prime * result + ((listOfFeatures == null) ? 0 : listOfFeatures.hashCode());
    result = prime * result + ((massfeature_rows == null) ? 0 : massfeature_rows.hashCode());
    result = prime * result + ((mzFeature_of_highest_statistic == null) ? 0
        : mzFeature_of_highest_statistic.hashCode());
    result = prime * result + ((primary_ion_present == null) ? 0 : primary_ion_present.hashCode());
    result = prime * result + ((row_to_ion == null) ? 0 : row_to_ion.hashCode());
    result = prime * result + statistic;
    result = prime * result + ((str_row_ion == null) ? 0 : str_row_ion.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EmpiricalCompound other = (EmpiricalCompound) obj;
    if (chosen_compounds == null) {
      if (other.chosen_compounds != null)
        return false;
    } else if (!chosen_compounds.equals(other.chosen_compounds))
      return false;
    if (compounds == null) {
      if (other.compounds != null)
        return false;
    } else if (!compounds.equals(other.compounds))
      return false;
    if (eId == null) {
      if (other.eId != null)
        return false;
    } else if (!eId.equals(other.eId))
      return false;
    if (evidence_score != other.evidence_score)
      return false;
    if (face_compound == null) {
      if (other.face_compound != null)
        return false;
    } else if (!face_compound.equals(other.face_compound))
      return false;
    if (ions == null) {
      if (other.ions != null)
        return false;
    } else if (!ions.equals(other.ions))
      return false;
    if (listOfFeatures == null) {
      if (other.listOfFeatures != null)
        return false;
    } else if (!listOfFeatures.equals(other.listOfFeatures))
      return false;
    if (massfeature_rows == null) {
      if (other.massfeature_rows != null)
        return false;
    } else if (!massfeature_rows.equals(other.massfeature_rows))
      return false;
    if (mzFeature_of_highest_statistic == null) {
      if (other.mzFeature_of_highest_statistic != null)
        return false;
    } else if (!mzFeature_of_highest_statistic.equals(other.mzFeature_of_highest_statistic))
      return false;
    if (primary_ion_present == null) {
      if (other.primary_ion_present != null)
        return false;
    } else if (!primary_ion_present.equals(other.primary_ion_present))
      return false;
    if (row_to_ion == null) {
      if (other.row_to_ion != null)
        return false;
    } else if (!row_to_ion.equals(other.row_to_ion))
      return false;
    if (statistic != other.statistic)
      return false;
    if (str_row_ion == null) {
      if (other.str_row_ion != null)
        return false;
    } else if (!str_row_ion.equals(other.str_row_ion))
      return false;
    return true;
  }

}
