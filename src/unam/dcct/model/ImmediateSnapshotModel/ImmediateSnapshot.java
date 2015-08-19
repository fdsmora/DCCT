package unam.dcct.model.ImmediateSnapshotModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import unam.dcct.misc.Constants;
import unam.dcct.model.CommunicationMechanism;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;
import unam.dcct.topology.SimplicialComplex;

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
	
	public static String name(){
		return Constants.IMMEDIATE_SNAPSHOT;
	}
	
	public static String basicMechanismName(){
		return Constants.SHARED_MEMORY;
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