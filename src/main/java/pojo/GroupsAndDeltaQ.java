package pojo;

import java.util.List;

public class GroupsAndDeltaQ {
  private List<List<String>> groups;
  private double deltaQ;

  public List<List<String>> getGroups() {
    return groups;
  }

  public void setGroups(List<List<String>> groups) {
    this.groups = groups;
  }

  public double getDeltaQ() {
    return deltaQ;
  }

  public void setDeltaQ(double deltaQ) {
    this.deltaQ = deltaQ;
  }

  public GroupsAndDeltaQ(List<List<String>> groups, double deltaQ) {
    super();
    this.groups = groups;
    this.deltaQ = deltaQ;
  }



}
