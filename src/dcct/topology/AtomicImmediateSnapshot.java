package dcct.topology;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import dcct.process.Process;
import dcct.combinatorics.*;

public class AtomicImmediateSnapshot implements CommunicationModel{

	public AtomicImmediateSnapshot(){}
	
	public Set<Simplex> communicationRound(Set<Simplex> simplices){
		Set<Simplex> newSimplices = new LinkedHashSet<Simplex>();
		
		for(Simplex s: simplices){
			String strAllScenarios = ScenarioGenerator.generate(s.dimension());
			String[] scenarios = strAllScenarios.split("\n");
			for (String scn : scenarios) {
				String[] groups = scn.split("\\" + ScenarioGenerator.getDelimiter());
				List<Process> processes = new ArrayList<Process>(s.getProcesses());
				String[] memory = new String[processes.size()];
				Set<Process> newProcesses = new LinkedHashSet<Process>(processes.size());
				for (String g : groups) {
					int[] indices = toIndices(g);
					newProcesses.addAll(performExecution(processes, memory, indices));
				}
				newSimplices.add(new Simplex(newProcesses));
			}
		}
		return newSimplices;
	}
	
	protected List<Process> performExecution(List<Process> processes, String[] memory, int[] indices) {
		List<Process> tempProcesses = new ArrayList<Process>(indices.length);
		int i;
		for(i=0;i<indices.length;i++){
			Process p = (Process)processes.get(indices[i]).clone();
			p.write(memory);
			tempProcesses.add(p);
		}
		for (Process p: tempProcesses){
			p.snapshot(memory);
		}
		return tempProcesses;
	}
	
	protected int[] toIndices(String group){
		int n = group.length();
		int[] indices = new int[n];
		for(int i=0; i<n; i++)
			indices[i]=Integer.parseInt(String.valueOf(group.charAt(i)));
		return indices;
	}
	
}


