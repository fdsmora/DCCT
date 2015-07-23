package dctopology;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import model.Model;
import configuration.Constants;
import dctopology.Process;

public class Simplex {
	
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
		this(Arrays.asList(processes));
	}
	
	public Simplex(List<Process> processes){
		
		// Processes must be sorted in increasing order by id.
		Collections.sort(processes, new Comparator<Process>(){
			@Override
			public int compare(Process p1, Process p2) {
				return Integer.compare(p1.getId(), p2.getId());
			}
			
		});
		
		this.processes = processes;
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

	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
		this.chromatic = chromatic;
	}
	
	public double[][] getCoordinates(){
		if (parent==null){
			return Constants.DEFAULT_SIMPLEX_VERTEX_COORDINATES[dimension()];
		}
		double[][] coordinates = new double[n][]; 
		if (chromatic)
			for (Process p : processes)
				coordinates[p.getId()] = calculateChromaticCoordinates(p);
			
		return coordinates;
	}
	
	public String[] getProcessLabels(){
		String[] labels = new String[n];
		for (Process p: processes)
			labels[p.getId()]=p.getView();
		return labels;
	}
	
	public int[][] getFaces(){
		int[][] face = new int[1][n];
		for (Process p : processes){
			face[0][p.getId()] = p.getId();
		}
		return face;
	}
	
	public Color[] getProcessColors(){
		Color[] colors = new Color[n];
		Queue<Color> qColors = new LinkedList<Color>(Model.getInstance().getColors());
		for (Process p:processes){
			colors[p.getId()]=qColors.remove();
		}
		return colors;
	}

	private double[] calculateChromaticCoordinates(Process p) {
		String[] processView = p.getViewArray();
		int count = Process.countViewElements(processView);
		int pid = p.getId();
		
		// If process only saw himself during communication round.
		if (count == 1)
			return parent.getCoordinates()[pid];
		
		final float EPSILON = Constants.EPSILON_DEFAULT;
		
		//int divisor = parentVertices.size();
		double smallFactor = (1-EPSILON)/count;
		double bigFactor = (1+(EPSILON/(count==3?2:1)))/count;
		double[] res = {0.0,0.0,0.0};
		
		for (int i = 0; i<n; i++){
			if (i==pid)
				res = LinearAlgebraHelper.vectorSum(
						LinearAlgebraHelper.scalarVectorMultiply(smallFactor,parent.getCoordinates()[pid])
						,res);
			else {
				double[] coords = (processView[i]==null? 
						new double[3] : parent.getCoordinates()[i]);
														
				res = LinearAlgebraHelper.vectorSum(
						LinearAlgebraHelper.scalarVectorMultiply(bigFactor, coords),res);
			}
		}
		return res;
	}

	public Simplex getParent() {
		return parent;
	}

	public void setParent(Simplex parent) {
		this.parent = parent;
	}
	
}


