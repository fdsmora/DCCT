package dcct.topology;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dcct.process.*;
import dcct.process.Process;
import dcct.combinatorics.*;

import org.apache.commons.collections4.iterators.PermutationIterator;
public class AtomicImmediateSnapshot implements CommunicationModel{

	public AtomicImmediateSnapshot(){}
	
	public Set<Simplex> communicationRound(Set<Simplex> simplices){
		Set<Simplex> newSimplices = new LinkedHashSet<Simplex>();
		
		for(Simplex s: simplices){
			String strAllScenarios = ScenarioGenerator.generate(s.dimension());
			String[] scenarios = strAllScenarios.split("\n");
			for (String scn : scenarios) {
				String[] groups = scn.split("\\" + ScenarioGenerator.getDelimiter());
				// Change PermutationIterator to something more efficient
				PermutationIterator<Process> iter = new PermutationIterator<Process>(s.processes);
				while (iter.hasNext()) {
					List<Process> permut = iter.next();
					String[] memory = new String[permut.size()];
					List<Process> newProcesses = new ArrayList<Process>(permut.size());
					for (String g : groups) {
						int[] indices = toIndices(g);
						performExecution(newProcesses, permut, memory, indices);
					}
					newSimplices.add(new Simplex(newProcesses));
				}
			}
		}
		return newSimplices;
	}
	
	protected void performExecution(List<Process> newProcesses, List<Process> permut, String[] memory, int[] indices) {
		List<Process> tempProcesses = new ArrayList<Process>(indices.length);
		int i;
		for(i=0;i<indices.length;i++){
			Process p = (Process)permut.get(indices[i]).clone();
			p.write(memory);
			tempProcesses.add(p);
		}
		for (Process p: tempProcesses){
			p.snapshot(memory);
		}
		newProcesses.addAll(tempProcesses);
	}
	
	protected int[] toIndices(String group){
		int n = group.length();
		int[] indices = new int[n];
		for(int i=0; i<n; i++)
			indices[i]=Integer.parseInt(String.valueOf(group.charAt(i)));
		return indices;
	}
}


