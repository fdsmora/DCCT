package unam.dcct.model.nonimmediatesnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import unam.dcct.misc.Configuration;
import unam.dcct.misc.Constants;
import unam.dcct.model.CommunicationProtocol;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;

public class NonImmediateSnapshot extends CommunicationProtocol {
	
	private Map<Simplex, String[]> simplexMemoryMap;
	private List<List<int[][]>> allScenariosPerDimension;

//	private int[][][] mat2 = new int[][][] {
//		{{1,0,0},{1,1,0},{1,1,1}},
//		{{1,0,0},{1,1,1},{1,1,1}},
//		{{1,0,1},{1,1,1},{1,1,1}},
//		{{1,1,0},{1,1,0},{1,1,1}},
//		{{1,1,0},{1,1,1},{1,1,1}},
//		
//		{{1,1,0},{0,1,0},{1,1,1}},
//		{{1,1,1},{0,1,0},{1,1,1}},
//		{{1,1,1},{0,1,1},{1,1,1}},
//		{{1,1,1},{1,1,0},{1,1,1}},
//		
//		{{1,0,0},{1,1,1},{1,0,1}},
//		{{1,0,0},{1,1,1},{1,1,1}},
//		{{1,0,1},{1,1,1},{1,0,1}},
//		{{1,0,1},{1,1,1},{1,1,1}},
//		{{1,1,0},{1,1,1},{1,1,1}},
//		
//		{{1,0,1},{1,1,1},{0,0,1}},
//		{{1,1,1},{1,1,1},{0,0,1}},
//		{{1,1,1},{1,1,1},{0,1,1}},
//		{{1,1,1},{1,1,1},{1,0,1}},
//		
//		{{1,1,1},{0,1,0},{0,1,1}},
//		{{1,1,1},{0,1,0},{1,1,1}},
//		{{1,1,1},{0,1,1},{0,1,1}},
//		{{1,1,1},{0,1,1},{1,1,1}},
//		{{1,1,1},{1,1,0},{1,1,1}},
//		
//		{{1,1,1},{0,1,1},{0,0,1}},
//		{{1,1,1},{1,1,1},{0,0,1}},
//		{{1,1,1},{1,1,1},{0,1,1}},
//		{{1,1,1},{1,1,1},{1,0,1}},
//		
//		{{1,1,1},{1,1,1},{1,1,1}}
//	};
//	
//	private int[][][] mat1 = new int[][][] {
//		{{1,0},{1,1}},
//		{{1,1},{1,1}},
//		{{1,1},{0,1}}
//	};
//	
//	private int[][][] mat0 = new int[][][] {
//		{{1}}
//	};
//	
//	private int[][][][] allScenariosPerDimension = new int[][][][]{
//		mat0,mat1,mat2
//	};

			
	/**
	 * It is the implementation of the abstract method {@link CommunicationProtocol#createScenarioGenerator(int)}.
	 * It creates all possible scenarios of execution of a communication round of this protocol. 
	 */
	@Override
	protected Iterable<Scenario> createScenarioGenerator(int dimension) {
		final List<int[][]> allScenarios = getAllScenarios(dimension);

		return new Iterable<Scenario>(){
			@Override
			public Iterator<Scenario> iterator() {
				return new NonImmediateSnapshotIterator(allScenarios);
			}
		};
	}
	
	private List<int[][]> getAllScenarios(int dimension){
		if (allScenariosPerDimension==null){
			int maxDimensions = Configuration.getInstance().SUPPORTED_NUMBER_OF_PROCESSES;
			allScenariosPerDimension = new ArrayList<List<int[][]>>(maxDimensions);
			for (int i =0; i<maxDimensions; i++)
				allScenariosPerDimension.add(null);
		}
		if (allScenariosPerDimension.get(dimension)==null)
			allScenariosPerDimension.set(dimension, new NIS_ScenarioGenerator().generate(dimension));
		return allScenariosPerDimension.get(dimension);
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
		return Constants.NON_IMMEDIATE_SNAPSHOT_SHARED_MEMORY;
	}
	
	private String[] getSharedMemory(Simplex s){
		int n = s.getProcessCount();
		String[] sharedMemory;

			// In the non-iterated immediate snapshot memory model processes read and write the same memory during the whole execution of the protocol.
			// so here we get the previous memory associated with the simplex in the previous round. 
			
			// First communication round, add the "base case" memory.
			if (simplexMemoryMap== null) {
				simplexMemoryMap = new HashMap<Simplex, String[]>();
				simplexMemoryMap.put(s, new String[n]);
			} 
			
			// For each round a copy of the memory of the last round is needed. 
			
			sharedMemory = simplexMemoryMap.get(s).clone();				
		
		return sharedMemory;
	}
	
	private class NonImmediateSnapshotIterator implements Iterator<Scenario>{

		Iterator<int[][]> scenariosIterator;
		private NonImmediateSnapshotIterator(List<int[][]> allScenarios){
			this.scenariosIterator = allScenarios.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return scenariosIterator.hasNext();
		}

		@Override
		public Scenario next() {
			return new NonImmediateSnapshotScenario(scenariosIterator.next());
		}
	}
	
	/**
	 * Represents an scenario of execution of a round of this protocol. 
	 * @author Fausto
	 *
	 */
	private class NonImmediateSnapshotScenario implements Scenario{
		private int[][] viewsMatrix;
		
		/**
		 * Creates an scenario of execution. 
		 * @param strScenario A codification of an execution scenario. 
		 */
		NonImmediateSnapshotScenario(int[][] scenario){
			viewsMatrix = scenario;
		}
		
		@Override
		public Simplex execute(Simplex baseSimplex) {
			List<Process> originalProcesses = baseSimplex.getProcesses();
			int n = baseSimplex.getProcessCount();
			List<Process> newProcesses = new ArrayList<Process>(n);

//			String[] lastView = new String[n]; 
			for (int pid = 0; pid <n ; pid++){
				Process newP = new Process(pid);
				String[] view = new String[n];
				
				// To support non-iterated shared memory model
//				String[] baseView = getSharedMemory(baseSimplex);
//				if (baseView!=null)
//					copy(view, baseView);
				
				int count = 0;
				for (int j = 0; j<n; j++){
					if (viewsMatrix[pid][j]>0){
						count++;
						view[j]=originalProcesses.get(j).getView();
					}
				}
//				if (count==n){
//					lastView = view;
//				}
				newP.setView(view);
				newProcesses.add(newP);
			}
			Simplex newSimplex = new Simplex(baseSimplex.isChromatic(), newProcesses);
			
//			simplexMemoryMap.put(newSimplex,  lastView);

			return newSimplex;
		}

		private void copy(String[] copy, String[] original) {
			for (int i = 0; i<original.length; i++){
				copy[i]=original[i];
			}
		}
	}
}
