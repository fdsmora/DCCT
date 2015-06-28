package model.AtomicImmediateSnapshotModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import configuration.Constants;
import model.CommunicationMechanism;
import dctopology.SimplicialComplex;
import dctopology.Simplex;
import dctopology.Process;

public class AtomicImmediateSnapshot extends CommunicationMechanism {

	/**
	 * Simulates all possible execution scenarios of processes communicating through shared memory.
	 * @param simplices The set containing the simplices which in turn contain the processes of the simplicial complex from which the simplices of this
	 * protocol complex will be obtained.  
	 */
	public SimplicialComplex communicationRound(SimplicialComplex sc) {
		Set<Simplex> simplices = sc.getSimplices();
		
		Set<Simplex> newSimplices = new LinkedHashSet<Simplex>();
		
		for(Simplex s: simplices){
			String strAllScenarios = PartitionGenerator.generate(s.dimension());
			String[] scenarios = strAllScenarios.split("\n");
			for (String scn : scenarios) {
				String[] groups = scn.split("\\" + String.valueOf(PartitionGenerator.getDelimiter()));
				List<Process> processes = new ArrayList<Process>(s.getProcesses());
				String[] sharedMemory = new String[processes.size()];
				Set<Process> newProcesses = new LinkedHashSet<Process>(processes.size());
				for (String g : groups) {
					int[] order = toIndices(g);
					newProcesses.addAll(simulateCommunication(processes, sharedMemory, order));
				}
				Simplex newSimplex = new Simplex(newProcesses);
				newSimplex.setParentId(s.getId());
				newSimplices.add(newSimplex);
			}
		}
		rounds++;
		return new SimplicialComplex(newSimplices);
	}
	
	/**
	 * Makes copies of processes write and take snapshots of shared memory in the given order, thus producing a list of these 
	 * processes with new views. 
	 * @param processes The processes whose copies will write and take snapshots of shared memory. 
	 * @param memory An array that represents the shared memory. 
	 * @param order The permutation of process indices that determine the order n in which processes communicate. 
	 * @return A list containing a copy of the processes with new views. 
	 */
	protected List<Process> simulateCommunication(List<Process> processes, String[] memory, int[] order) {
		List<Process> tempProcesses = new ArrayList<Process>(order.length);
		int i;
		for(i=0;i<order.length;i++){
			Process p = (Process)processes.get(order[i]).clone();
			write(p, memory);
			tempProcesses.add(p);
		}
		for (Process p: tempProcesses){
			snapshot(p, memory);
		}
		return tempProcesses;
	}
	
	protected static void write(Process p, String[] memory){
		memory[p.getId()] = p.getView();
	}
	
	protected static void snapshot(Process p, String[] memory){
		p.setView(memory.clone());
	}
	
	/**
	 * Transforms the permutation of process indices in group into an array of integers. 
	 * @param group The string representation of the process indices permutation. 
	 * @return The permutation of process indices in the form of an array of integers. 
	 */
	protected int[] toIndices(String group){
		int n = group.length();
		int[] indices = new int[n];
		for(int i=0; i<n; i++)
			indices[i]=Integer.parseInt(String.valueOf(group.charAt(i)));
		return indices;
	}
	
	@Override 
	public String toString(){
		return Constants.ATOMIC_IMMEDIATE_SNAPSHOT + " " + Constants.SHARED_MEMORY ;
	}


}