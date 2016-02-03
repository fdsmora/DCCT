package unam.dcct.model.immediatesnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import unam.dcct.misc.Configuration;
import unam.dcct.misc.Constants;
import unam.dcct.model.CommunicationProtocol;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;

/**
 * Represents the shared memory communication protocol known as 'atomic immediate snapshot', in 
 * both the "iterated" and "non-iterated" versions. 
 * <p>
 * In this protocol n processes communicate by first writing to a shared memory, which is 
 * an array with n entries, each ith-entry, 0&lt;=i&lt;=n, is assigned to each process with id equals to i.
 * Each process writes the contents of it's view to its entry in the array and 
 * when the writing is complete it immediately reads the whole array atomically, that is,
 * it gets a snapshot of the whole content of the array, so it is an operation that takes time O(1).
 * <p>
 * In the iterated version processes write and read a blank new memory in each communication round.
 * <p>
 * In the non-iterated version processes write and read the same memory during all communication rounds. 
 * <p> 
 * This class provides functionality to first generate all possible scenarios of execution that could have taken place
 * place during a round of execution of this protocol and second, to simulate the communication of processes
 * using this protocol for each of the generated scenarios, thus creating new processes with new states (views)
 * that represent what the processes ended up knowing about the other processes after communicating in the order
 * specified by each particular scenario. 
 * <p> 
 * @author Fausto Salazar
 * @see CommunicationProtocol
 */
public class ImmediateSnapshot extends CommunicationProtocol {
	
	/**
	 * It stores all scenarios codification for each dimension of simplex, that is,
	 * for each number of processes in each simplex minus one.  
	 */
	private String[] allScenariosPerDimension = new String[Configuration.getInstance().SUPPORTED_NUMBER_OF_PROCESSES];


	/**
	 * It is the implementation of the abstract method {@link CommunicationProtocol#createScenarioGenerator(int)}.
	 * It creates all possible scenarios of execution of a communication round of this protocol. 
	 */
	@Override
	protected Iterable<Scenario> createScenarioGenerator(int dimension) {
		final String allScenarios = getAllScenarios(dimension);

		return new Iterable<Scenario>(){
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
		List<Process> newProcesses = new ArrayList<Process>(order.length);
		int i;
		for(i=0;i<order.length;i++){
			Process p = (Process)processes.get(order[i]).clone();
			write(p, memory);
			newProcesses.add(p);
		}
		for (Process p: newProcesses){
			snapshot(p, memory);
		}
		return newProcesses;
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
			return getName();
	}
	
	/**
	 * Returns the name of the current implementation of the @link{unam.dcct.model.CommunicationProtocol}
	 * This implementation is required as described in @link{unam.dcct.model.CommunicationProtocol#getName()}
	 * @return the name of the current implementation of the communication protocol.
	 */
	public static String getName(){
		return Constants.IMMEDIATE_SNAPSHOT_SHARED_MEMORY_ITERATED;
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

	/**
	 * Represents an scenario of execution of a round of this protocol. 
	 * @author Fausto
	 *
	 */
	private class ImmediateSnapshotScenario implements Scenario{
		private String[] blocks;
		
		/**
		 * Creates an scenario of execution. 
		 * @param strScenario A codification of an execution scenario. 
		 */
		ImmediateSnapshotScenario(String strScenario){
			blocks = strScenario.split("\\" + String.valueOf(PartitionGenerator.getDelimiter()));
		}
		
		@Override
		public Simplex execute(Simplex baseSimplex) {
			List<Process> originalProcesses = baseSimplex.getProcesses();
			String[] sharedMemory = getSharedMemory(baseSimplex);
			List<Process> newProcesses = new ArrayList<Process>(originalProcesses.size());
			for (String b : blocks) {
				int[] order = ImmediateSnapshot.toIndices(b);
				newProcesses.addAll(ImmediateSnapshot.simulateCommunication(originalProcesses, sharedMemory, order));
			}
			boolean chromatic = baseSimplex.isChromatic();
			Simplex newSimplex = new Simplex(chromatic, newProcesses);

			return newSimplex;
		}
		
		private String[] getSharedMemory(Simplex s){
			int n = s.getProcessCount();
			String[] sharedMemory = new String[n];
			
			return sharedMemory;
		}
		
	}
}