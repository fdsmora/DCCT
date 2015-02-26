package dcct.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dcct.process.Process;

public class Simplex {
	protected List<Process> processes;
	
	public List<Process> getProcesses() {
		return processes;
	}
	
	public Simplex(Process... processes){
		this.processes = Arrays.asList(processes);
	}
	
	public int Dimension(){
		return processes.size();
	}
	
}
