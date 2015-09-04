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
	}
	
	public int dimension(){
		return n -1 ;
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

//	public void setChromatic(boolean chromatic) {
//
//		this.chromatic = chromatic;
//	}

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


