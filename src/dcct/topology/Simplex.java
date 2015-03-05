package dcct.topology;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import dcct.process.Process;

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
	@Override 
	public boolean equals(Object o){
		if (!(o instanceof Simplex)) 
		    return false;
//		Simplex other = (Simplex) o;
//				
//		Process[] myP = new Process[this.processes.size()];
//		Process[] otherP = new Process[other.processes.size()];
//		this.processes.toArray(myP);
//		this.processes.toArray(otherP);
//		for(int i=0; i<this.processes.size(); i++){
//			if (!myP[i].equals(otherP[i]))
//				return false;
//		}
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
