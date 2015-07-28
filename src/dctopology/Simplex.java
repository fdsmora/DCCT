package dctopology;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import view.Face;
import dctopology.Process;

public class Simplex {
	
	private boolean chromatic = true;
	private Simplex parent = null;
	private Face associatedFace = null;
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

	public Simplex getParent() {
		return parent;
	}

	public void setParent(Simplex parent) {
		this.parent = parent;
	}

	public Face getFace() {
		return associatedFace;
	}

	public void setFace(Face face) {
		this.associatedFace = face;
	}
	
}


