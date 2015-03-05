package dcct.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dcct.process.Process;

public class Simplex {
	
	public List<Process> getProcesses() {
		return processes;
	}
	
	protected List<Process> processes;
	
	public Simplex(Process... processes){
		this.processes = Arrays.asList(processes);
	}
	
	public Simplex(List<Process> processes){
		this.processes = processes;
	}
	
	public int dimension(){
		return processes.size()-1;
	}
	
}
