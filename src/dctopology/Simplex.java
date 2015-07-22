package dctopology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import view.Vertex;
import configuration.Constants;
import dctopology.Process;

public class Simplex {
	
	private static long idCounter = -1;
	private long id=0;
	private long parentId=-1;
	private boolean chromatic = true;
	private Simplex parent = null;
	private int n = 0;
	private List<Process> processes;
	
	public List<Process> getProcesses() {
//		if (chromatic)
//			return processes;
//		if (nonChromaticProcesses==null)
//			buildNonChromaticProcesses();
//		return nonChromaticProcesses;
		return processes;

	}
	
//	//TEMPORAL, PLEASE REPLACE FOR SOMETHING BETTER
//	private int countProcessViewElements(String[] processView){
//		int c = 0;
//		for (int i =0; i<processView.length; i++){
//			if (processView[i]!=null)
//				c++;
//		}
//		return c;
//	}
	
//	private void buildNonChromaticProcesses() {
//		Map<String, Process> map = new LinkedHashMap<String, Process>();
//		nonChromaticProcesses = new LinkedHashSet<Process>(map.size());
//		for (Process p : chromaticProcesses){
//			String view = p.getView();
//			if (!map.containsKey(view)){
//				Process ncp = (Process) p.clone(); // non-Chromatic process
//				String[] processView = p.getViewArray();
//				int id = countProcessViewElements(processView)-1;
//				ncp.setView(processView);
//				ncp.setId(id);
//				nonChromaticProcesses.add(ncp);
//				map.put(view, p);
//			}
//
//		}
////		nonChromaticProcesses = new LinkedHashSet<Process>(map.size());
////		for (String view : map.keySet()){
////			nonChromaticProcesses.add(map.get(view));
////		}
//	}

	public Simplex(Process... processes){
		//this(new LinkedHashSet<Process>(Arrays.asList(processes)));
		this(Arrays.asList(processes));
	}
	
	public Simplex(List<Process> processes){
		// When created, it is by default chromatic
		this.processes = processes;
		this.id = ++idCounter;
		this.n = getProcesses().size();
	}
	
	public int dimension(){
		return n-1;
	}
	
	public int getProcessCount(){
		return n;
	}
	@Override 
	public boolean equals(Object o){
		if (!(o instanceof Simplex)) 
		    return false;
		return true;
	}
	@Override 
	public int hashCode(){
		int hashC =0;
		for (Process p : processes)
			hashC+=p.hashCode();
		return hashC;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		sb.append("{");
		for (Process p : getProcesses()){
			sb.append(prefix);
			sb.append(chromatic? 
					p.toString() : p.getView());
			prefix = ",";
		}
		sb.append("}");
		return sb.toString();
	}

	public long getId() {
		return id;
	}

	public long getParentId() {
		return parentId;
	}
	
	public void setParentId(long parentId) {
		this.parentId=parentId;
	}

	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
		this.chromatic = chromatic;
	}
	
//	public int[] getVertexIndices(){
//		int n = getProcesses().size();
//		int[] vertexIndices = new int[n];
//		for (int i = 0; i<n; i++ ){
//			vertexIndices[i]=i;
//		}
//		return vertexIndices;
//	}
	
//	public double[][] getCoordinates(){
//		if (parent==null){
//			return Constants.DEFAULT_SIMPLEX_VERTEX_COORDINATES[dimension()];
//		}
//		double[][] coordinates = new double[n][]; 
//		if (chromatic)
//			for (Process p : processes)
//				coordinates[p.getId()] = calculateVertexChromaticCoordinates(p);
//			
//	}

//	private double[] calculateChromaticCoordinates(Process p) {
//		int count = countProcessViewElements(p.getViewArray());
//		
//		if (count == 1)
//			return parent.processes.//.get(id).coordinates;
//		
//		final float EPSILON =Constants.EPSILON_DEFAULT;
//		
//		//int divisor = parentVertices.size();
//		double smallFactor = (1-EPSILON)/count;
//		double bigFactor = (1+(EPSILON/(count==3?2:1)))/count;
//		double[] res = {0.0,0.0,0.0};
//		
//		for (int i = 0; i<processView.length; i++){
//			if (i==id)
//				res = vectorSum(
//						scalarVectorMultiply(smallFactor,parentVertices.get(id).getCoordinates())
//						,res);
//			else {
//				double[] coords = (processView[i]==null? new double[3] : 
//										parentVertices.get(i).getCoordinates());
//				res = vectorSum(
//						scalarVectorMultiply(bigFactor, coords)
//						,res);
//			}
//		}
//		return res;
//	}
	
	private int countProcessViewElements(String[] processView){
		int c = 0;
		for (int i =0; i<processView.length; i++){
			if (processView[i]!=null)
				c++;
		}
		return c;
	}

	public Simplex getParent() {
		return parent;
	}

	public void setParent(Simplex parent) {
		this.parent = parent;
	}
	
}


