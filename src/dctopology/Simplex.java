package dctopology;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import dctopology.Process;

public class Simplex {
	
	private static long idCounter = -1;
	protected long id=0;
	protected long parentId=-1;
	protected boolean chromatic = true;
	protected Set<Process> chromaticProcesses;
	protected Set<Process> nonChromaticProcesses;
	
	public Set<Process> getProcesses() {
		if (chromatic)
			return chromaticProcesses;
		if (nonChromaticProcesses==null)
			buildNonChromaticProcesses();
		return nonChromaticProcesses;

	}
	
	private void buildNonChromaticProcesses() {
		Map<String, Process> map = new LinkedHashMap<String, Process>();
		for (Process p : chromaticProcesses){
			String view = p.getView();
			if (!map.containsKey(view))
				map.put(view, p);
		}
		nonChromaticProcesses = new LinkedHashSet<Process>(map.size());
		for (String view : map.keySet()){
			nonChromaticProcesses.add(map.get(view));
		}
	}

	public Simplex(Process... processes){
		this(new LinkedHashSet<Process>(Arrays.asList(processes)));
	}
	
	public Simplex(Set<Process> processes){
		// When created, it is by default chromatic
		this.chromaticProcesses = processes;
		this.id = ++idCounter;
	}
	
	public int dimension(){
		return getProcesses().size()-1;
	}
	
	public int getProcessCount(){
		return getProcesses().size();
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
		for (Process p : chromaticProcesses)
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
	
}


