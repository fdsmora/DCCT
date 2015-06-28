package dctopology;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import dctopology.Process;

public class Simplex {
	
	private static long idCounter = -1;
	protected long id=0;
	protected long parentId=-1;
	
	public Set<Process> getProcesses() {
		return processes;
	}
	
	protected Set<Process> processes;
	
	public Simplex(Process... processes){
		this(new LinkedHashSet<Process>(Arrays.asList(processes)));
	}
	
	public Simplex(Set<Process> processes){
		this.processes = processes;
		this.id = ++idCounter;
	}
	
	public int dimension(){
		return processes.size()-1;
	}
	
	public int getProcessCount(){
		return processes.size();
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
		for (Process p : this.processes)
			hashC+=p.hashCode();
		return hashC;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		sb.append("{");
		for (Process p : this. processes){
			sb.append(prefix);
			sb.append(p.toString());
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
	
}
