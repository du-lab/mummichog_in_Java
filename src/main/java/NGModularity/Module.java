package NGModularity;

import java.util.ArrayList;
import java.util.List;

import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

import pojo.GroupsAndDeltaQ;

public class Module {
	private List<String> nodes;
	private int numOfNodes;
	private double[][] modularityMatrix;
	private Double deltaQ;
	private List<Integer> s;
	private boolean toBeDivide;
	
	Module(List<String> nodes){
		this.nodes=nodes;
		this.numOfNodes=this.nodes.size();
		this.deltaQ=0.0;
		this.toBeDivide=true;
		this.s = new ArrayList<Integer>();
		for(int i=0;i<this.numOfNodes;i++) {
			this.s.add(0);
		}
	}
	public GroupsAndDeltaQ quickDivide(Network networkobj) {
		List<List<String>> groups =this.eigenDivide(networkobj);
		this.toBeDivide=false;
		return new GroupsAndDeltaQ(groups, this.deltaQ);
		
	}
	
	List<List<String>> eigenDivide(Network networkobj) {
		List<List<String>> result = new ArrayList<List<String>>();
		this.fetchModularityMatrix(networkobj);
		List<String> group1= new ArrayList<String>();
		List <String> group2= new ArrayList<String>();
		DoubleMatrix dm= new DoubleMatrix(this.modularityMatrix);
		DoubleMatrix eigens[] = Eigen.symmetricEigenvectors(dm);
		
		if(eigens[1].diag().max()>0) {
			DoubleMatrix lead_vector=eigens[0].getColumn(eigens[1].diag().argmax());
			for(int i=0;i<this.numOfNodes;i++) {
				if(lead_vector.get(i)<0.0) {
					group1.add(this.nodes.get(i));
				}else {
					group2.add(this.nodes.get(i));
				}
			}
			this.calculateS(group1, group2);
			this.deltaQ=this.computeDeltaQ(networkobj);
		}
		
		result.add(group1);
		result.add(group2);
		return result;
	}
	
	Double computeDeltaQ(Network networkobj) {
		double dQ=0.0;
		for(int i=0;i<this.numOfNodes;i++) {
			for(int j=0;j<this.numOfNodes;j++) {
				dQ+= ((this.s.get(i)*this.s.get(j) - 1)*this.modularityMatrix[i][j]);
			}
		}
		return dQ/(4*networkobj.getNumOfEdges());
		
	}
	
	void calculateS(List<String> g1, List<String> g2) {
		for(int i=0;i<this.nodes.size();i++) {
			if(g1.contains(this.nodes.get(i))) {
				this.s.add(i, 1);
			}else {
				this.s.add(i, -1);
			}
		}
		
	}
	
	void fetchModularityMatrix(Network networkobj) {
		double[][] am= new double[this.numOfNodes][this.numOfNodes];
		for(int i=0;i<this.numOfNodes;i++) {
			for(int j=0;j<this.numOfNodes;j++) {
				am[i][j]=0.0;
			}
		}
		
		for(int k=0;k<this.numOfNodes;k++) {
			for(int l=0;l<this.numOfNodes;l++) {
				am[k][l]= networkobj.getModularityMatrix()[networkobj.getCrd().get(this.nodes.get(k))][networkobj.getCrd().get(this.nodes.get(l))];
			}
		}
		this.modularityMatrix=am;
	}

	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}

	public int getNumOfNodes() {
		return numOfNodes;
	}

	public void setNumOfNodes(int numOfNodes) {
		this.numOfNodes = numOfNodes;
	}

	public double[][] getModularityMatrix() {
		return modularityMatrix;
	}

	public void setModularityMatrix(double[][] modularityMatrix) {
		this.modularityMatrix = modularityMatrix;
	}

	public Double getDeltaQ() {
		return deltaQ;
	}

	public void setDeltaQ(Double deltaQ) {
		this.deltaQ = deltaQ;
	}

	public List<Integer> getS() {
		return s;
	}

	public void setS(List<Integer> s) {
		this.s = s;
	}

	public boolean isToBeDivide() {
		return toBeDivide;
	}

	public void setToBeDivide(boolean toBeDivide) {
		this.toBeDivide = toBeDivide;
	}
	
	
	
}
