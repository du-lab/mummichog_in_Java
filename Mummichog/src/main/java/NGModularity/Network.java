package NGModularity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jblas.DoubleMatrix;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import getuserdata.InputUserData;
import pojo.GroupsAndDeltaQ;

public class Network {

	private int numOfNodes;
	private int numOfEdges;
	//Coordinates for Nodes
	private Map<String,Integer> crd;
	private List<String> nodes;
	private List<List<String>> edges;
	private Integer[][] adjacencyMatrix;
	private List<Integer> degrees;
	private Double[][] modularityMatrix;
	private final static Logger LOGGER = Logger.getLogger(InputUserData.class.getName());
	private List<Module> modules;
	private double qVal;
	
	public Network() {
		this.modules= new ArrayList<Module>();
		this.qVal=0;
		this.numOfEdges=0;
		this.numOfNodes=0;
		//TODO Check if any more initializations need to be done
		this.edges=new ArrayList<List<String>>();
	}
	
	
	
	public List<List<String>> specsplit() throws Exception {
		List <List<String>> result = new ArrayList <List<String>>();
		this.prime_network();
		Module m = new Module(this.nodes);
		this.modules.add(m);
		List<Module> divisible = new ArrayList<Module>();
		for(Module module : this.modules) {
			if(module.isToBeDivide()) {
				divisible.add(module);
			}
		}
		while(divisible.size()>0) {
			for(Module mod: divisible) {
				GroupsAndDeltaQ gpq= mod.quickDivide(this);
				
				//Check if division was successful
				if(gpq.getDeltaQ()>0 && gpq.getGroups().size()>0) {
					this.qVal+=gpq.getDeltaQ();
					this.modules.remove(mod);
					for(List<String> group : gpq.getGroups()) {
						this.modules.add(new Module(group));
					}
				}
			}
			divisible.clear();
			for(Module mod2 : this.modules) {
				if(mod2.isToBeDivide()) {
					divisible.add(mod2);
				}
			}
			
		}
		
		for(Module mod3: this.modules) {
			result.add(mod3.getNodes());
		}
	return result;	
	}
	

	
	public void prime_network() throws Exception {
		if(this.nodes !=null & this.edges !=null) {
			this.numOfNodes=this.nodes.size();
			this.numOfEdges=this.edges.size();
			this.make_node_index();
			this.makeAdjacencyMatrix();
			this.computeDegrees();
			this.makeModularityMatrix();
		}
		else {
			LOGGER.error("Network nodes or edges not properly defined!");
			throw new Exception("network not correct");
		}
	}
	
	public void make_node_index() {
		
		for(int i=0;i<this.numOfNodes; i ++) {
			this.crd.put(nodes.get(i), i);
		}
		}
	
	public void makeAdjacencyMatrix() {
		Integer[][] am= new Integer[this.numOfNodes][this.numOfNodes];
		for(int i=0;i<this.numOfNodes;i++) {
			for(int j=0;j<this.numOfNodes;j++) {
				am[i][j]=0;
			}
		}
		for(List<String> li: this.edges) {
			am[this.nodes.indexOf(li.get(0))][this.nodes.indexOf(li.get(1))] +=1;
			am[this.nodes.indexOf(li.get(1))][this.nodes.indexOf(li.get(0))] +=1;
		}
		this.adjacencyMatrix=am;		
	}
	
	 void computeDegrees() {
		 if(this.degrees==null) {
			 int sum;
			 for(int i=0;i<this.numOfNodes;i++) {
				 sum=0;	
				 for(int j=0;j<this.numOfNodes;j++) {
						sum+=this.adjacencyMatrix[i][j];
					}
				 this.degrees.add(sum);
		 }}else {
			 System.out.println("Degrees Already Exist");
		 }
	 }
	 
	 void makeModularityMatrix() {
		 Double[][] am= new Double[this.numOfNodes][this.numOfNodes];
			for(int i=0;i<this.numOfNodes;i++) {
				for(int j=0;j<this.numOfNodes;j++) {
					am[i][j]=0.0;
				}
			}
			
			for(int k=0;k<this.numOfNodes;k++) {
				for(int l=0;l<this.numOfNodes;l++) {
					am[k][l]= (this.adjacencyMatrix[k][l])-((this.degrees.get(k) * this.degrees.get(k))/(2.0/this.numOfEdges));
				}
			}
			this.modularityMatrix=am;
			}



	public int getNumOfNodes() {
		return numOfNodes;
	}



	public void setNumOfNodes(int numOfNodes) {
		this.numOfNodes = numOfNodes;
	}



	public int getNumOfEdges() {
		return numOfEdges;
	}



	public void setNumOfEdges(int numOfEdges) {
		this.numOfEdges = numOfEdges;
	}



	public Map<String, Integer> getCrd() {
		return crd;
	}



	public void setCrd(Map<String, Integer> crd) {
		this.crd = crd;
	}



	public List<String> getNodes() {
		return nodes;
	}



	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}



	public List<List<String>> getEdges() {
		return edges;
	}



	public void setEdges(List<List<String>> edges) {
		this.edges = edges;
	}



	public Integer[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}



	public void setAdjacencyMatrix(Integer[][] adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}



	public List<Integer> getDegrees() {
		return degrees;
	}



	public void setDegrees(List<Integer> degrees) {
		this.degrees = degrees;
	}



	public Double[][] getModularityMatrix() {
		return modularityMatrix;
	}



	public void setModularityMatrix(Double[][] modularityMatrix) {
		this.modularityMatrix = modularityMatrix;
	}



	public List<Module> getModules() {
		return modules;
	}



	public void setModules(List<Module> modules) {
		this.modules = modules;
	}



	public static Logger getLogger() {
		return LOGGER;
	}
	
	public void copyFromGraph(Graph<String,DefaultEdge> g) {
		this.nodes=new ArrayList<String>(g.vertexSet());
		for(DefaultEdge ed:g.edgeSet()) {
			this.edges.add(new ArrayList<String>(Arrays.asList(g.getEdgeSource(ed),g.getEdgeTarget(ed))));
		}
	}
	
	
}
