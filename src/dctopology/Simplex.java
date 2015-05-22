package dctopology;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import dctopology.Process;

public class Simplex {
	
	public Set<Process> getProcesses() {
		return processes;
	}
	
	protected Set<Process> processes;
	
	public Simplex(Process... processes){
		this.processes = new LinkedHashSet<Process>(Arrays.asList(processes));
	}
	
	public Simplex(Set<Process> processes){
		this.processes = processes;
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
	
}
