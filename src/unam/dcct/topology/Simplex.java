package unam.dcct.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import unam.dcct.topology.Process;
import unam.dcct.view.geometry.Face;

public class Simplex {
	
	private boolean chromatic = true;
	private Simplex parent = null;
	private Face associatedFace = null;
	private int n = 0;
	private List<Process> processes;
//	private List<Process> nonChromaticProcesses;


		
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
	
	
//	public void setNonChromaticSimplex(Process... nonChromaticProcesses){
//		setNonChromaticSimplex(Arrays.asList(nonChromaticProcesses));
//	}
	
//	public void setNonChromaticSimplex(List<Process> nonChromaticProcesses){
//		
//		Map<String, Process> uniqueProcesses = new HashMap<String, Process>(nonChromaticProcesses.size());
//		
//		int idCounter = 0;
//		for (Process p : nonChromaticProcesses){
//			if (!uniqueProcesses.containsKey(p.getView())){
//				uniqueProcesses.put(p.getView(), p);
//				p.setChromatic(false);
//				// Re-set process ids
//				p.setId(idCounter++);
//			}
//		}
//		nonChromaticProcesses = new ArrayList<Process>(uniqueProcesses.values());
//		sortProcesses(nonChromaticProcesses);
//		
//		this.nonChromaticProcesses = nonChromaticProcesses;
//	}

	public Simplex(boolean chromatic, Process... processes){
		this(chromatic, Arrays.asList(processes));
	}
	
	public Simplex(boolean chromatic, List<Process> processes){
		this.chromatic = chromatic;
		if (!chromatic)
			this.processes = makeProcessesNonChromatic(processes);
		else 
			this.processes = processes;
		
		sortProcesses(this.processes);

		//this.chromaticProcesses = processes;
		this.n = this.processes.size();
	}
	
	private List<Process> makeProcessesNonChromatic(List<Process> processes) {
		Map<String, Process> uniqueProcesses = new HashMap<String, Process>(processes.size());
		
		int idCounter = 0;
		for (Process p : processes){
			if (!uniqueProcesses.containsKey(p.getView())){
				Process _p = (Process)p.clone();
				uniqueProcesses.put(p.getView(), _p);
				_p.setChromatic(false);
				// Re-set process ids
				_p.setId(idCounter++);
			}
		}
		return new ArrayList<Process>(uniqueProcesses.values());
	}

	public Simplex(Process... processes){
		this(true, Arrays.asList(processes));
	}
	
	public Simplex(List<Process> processes){
		this(true, processes);
	}
	
	private void sortProcesses(List<Process> processes){
		// Processes must be sorted in increasing order by id.
		Collections.sort(processes, new Comparator<Process>(){
			@Override
			public int compare(Process p1, Process p2) {
				return Integer.compare(p1.getId(), p2.getId());
			}
		});
	}
	
	public List<Process> getProcesses() {
		return processes;
//		if (chromatic)
//			return chromaticProcesses;
//		return nonChromaticProcesses;
	}
	
	public int dimension(){
		return n -1 ;
		//return getProcesses().size() -1;
	}
	
	public int getProcessCount(){
		return n;
		//return getProcesses().size();
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
		for (Process p : getProcesses())
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
//			sb.append(chromatic? 
//					p.toString() : p.getView());
			sb.append(p.toString());
			prefix = ",";
		}
		sb.append("}");
		return sb.toString();
	}

	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
//		if (!chromatic && nonChromaticProcesses == null)
//			try {
//				throw new Exception("The method 'setNonChromaticSimplex(List<Process> nonChromaticProcesses)' must be called before calling this method with a 'false' argument. ");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
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


