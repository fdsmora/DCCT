package model.ImmediateSnapshotModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import configuration.Constants;
import model.CommunicationMechanism;
import dctopology.SimplicialComplex;
import dctopology.Simplex;
import dctopology.Process;

public class ImmediateSnapshot extends CommunicationMechanism {
	
	private String[] allScenariosPerDimension = new String[3];
	
	
	public ImmediateSnapshot(){}

	@Override
	protected ScenarioGenerator createScenarioGenerator(Simplex s) {
		String allScenarios = getAllScenarios(s.dimension());
		return new ScenarioGenerator(){

			@Override
			public Iterator<Scenario> iterator() {
				return new ImmediateSnapshotIterator(allScenarios);
			}
			
		};
	}
	
	/**
	 * Simulates all possible execution scenarios of processes communicating through shared memory.
	 * @param simplices The set containing the simplices which in turn contain the processes of the simplicial complex from which the simplices of this
	 * protocol complex will be obtained.  
	 */
//	public SimplicialComplex communicationRound(SimplicialComplex sc) {
//		// The communication round must be performed on chromatic simplices.
//		//sc.setChromatic(true);
//		
////		Set<Simplex> simplices = sc.getSimplices();
////		Set<Simplex> newSimplices = new LinkedHashSet<Simplex>();
//		List<Simplex> simplices = sc.getSimplices();
//		List<Simplex> newSimplices = new ArrayList<Simplex>();
//		
//		for(Simplex s: simplices){
//			String strAllScenarios = PartitionGenerator.generate(s.dimension());
//			//Test
//			//System.out.println("\n" + strAllScenarios);
//			String[] scenarios = strAllScenarios.split("\n");
//			for (String scn : scenarios) {
//				String[] groups = scn.split("\\" + String.valueOf(PartitionGenerator.getDelimiter()));
//				List<Process> processes = new ArrayList<Process>(s.getProcesses());
//				String[] sharedMemory = new String[processes.size()];
////				Set<Process> newProcesses = new LinkedHashSet<Process>(processes.size());
//				List<Process> newProcesses = new ArrayList<Process>(processes.size());
//				for (String g : groups) {
//					int[] order = toIndices(g);
//					newProcesses.addAll(simulateCommunication(processes, sharedMemory, order));
//				}
//				Simplex newSimplex = new Simplex(newProcesses);
//				newSimplex.setParent(s);
//				//newSimplex.setParentId(s.getid);
//				newSimplices.add(newSimplex);
//			}
//		}
//		rounds++;
//		return new SimplicialComplex(newSimplices);
//	}
	
	/**
	 * Makes copies of processes write and take snapshots of shared memory in the given order, thus producing a list of these 
	 * processes with new views. 
	 * @param processes The processes whose copies will write and take snapshots of shared memory. 
	 * @param memory An array that represents the shared memory. 
	 * @param order The permutation of process indices that determine the order n in which processes communicate. 
	 * @return A list containing a copy of the processes with new views. 
	 */
	private static List<Process> simulateCommunication(List<Process> processes, String[] memory, int[] order) {
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
	
	private static void write(Process p, String[] memory){
		memory[p.getId()] = p.getView();
	}
	
	private static void snapshot(Process p, String[] memory){
		p.setView(memory.clone());
	}
	
	/**
	 * Transforms the permutation of process indices in group into an array of integers. 
	 * @param group The string representation of the process indices permutation. 
	 * @return The permutation of process indices in the form of an array of integers. 
	 */
	private static int[] toIndices(String group){
		int n = group.length();
		int[] indices = new int[n];
		for(int i=0; i<n; i++)
			indices[i]=Integer.parseInt(String.valueOf(group.charAt(i)));
		return indices;
	}
	
	@Override 
	public String toString(){
		return Constants.IMMEDIATE_SNAPSHOT + " " + Constants.SHARED_MEMORY ;
	}
	
	private String getAllScenarios(int dimension){
		if (allScenariosPerDimension[dimension]==null)
			allScenariosPerDimension[dimension] = PartitionGenerator.generate(dimension);
		return allScenariosPerDimension[dimension];
	}
	
	private class ImmediateSnapshotIterator implements Iterator<Scenario>{

		Iterator<String> scenariosIterator;

		private ImmediateSnapshotIterator(String allScenarios){
			List<String> scenarios = Arrays.asList(allScenarios.split("\n"));
			this.scenariosIterator = scenarios.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return scenariosIterator.hasNext();
		}

		@Override
		public Scenario next() {
			return new ImmediateSnapshotScenario(scenariosIterator.next());
		}
	}

	private class ImmediateSnapshotScenario implements Scenario{
		private String[] groups;
		
		ImmediateSnapshotScenario(String strScenario){
			groups = strScenario.split("\\" + String.valueOf(PartitionGenerator.getDelimiter()));
		}
		
		@Override
		public List<Process> execute(List<Process> originalProcesses) {
			String[] sharedMemory = new String[originalProcesses.size()];
			List<Process> newProcesses = new ArrayList<Process>(originalProcesses.size());
			for (String g : groups) {
				int[] order = ImmediateSnapshot.toIndices(g);
				newProcesses.addAll(ImmediateSnapshot.simulateCommunication(originalProcesses, sharedMemory, order));
			}
			return newProcesses;
		}
		
	}

}