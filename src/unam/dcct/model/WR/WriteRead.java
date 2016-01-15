package unam.dcct.model.WR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import unam.dcct.misc.Configuration;
import unam.dcct.misc.Constants;
import unam.dcct.model.CommunicationProtocol;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;

public class WriteRead extends CommunicationProtocol {

	private List<List<int[][]>> allScenariosPerDimension;
	
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
				return new WriteReadIterator(allScenarios);
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
			allScenariosPerDimension.set(dimension, new WR_ScenarioGenerator().generate(dimension));
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
		return Constants.WRITE_READ;
	}
	
	private class WriteReadIterator implements Iterator<Scenario>{

		Iterator<int[][]> scenariosIterator;

		private WriteReadIterator(List<int[][]> allScenarios){
			this.scenariosIterator = allScenarios.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return scenariosIterator.hasNext();
		}

		@Override
		public Scenario next() {
			return new WriteReadScenario(scenariosIterator.next());
		}
	}
	
	/**
	 * Represents an scenario of execution of a round of this protocol. 
	 * @author Fausto
	 *
	 */
	private class WriteReadScenario implements Scenario{
		private int[][] viewsMatrix;
		
		/**
		 * Creates an scenario of execution. 
		 * @param strScenario A codification of an execution scenario. 
		 */
		WriteReadScenario(int[][] scenario){
			viewsMatrix = scenario;
		}
		
		@Override
		public Simplex execute(Simplex baseSimplex) {
			List<Process> originalProcesses = baseSimplex.getProcesses();
			int n = baseSimplex.getProcessCount();
			List<Process> newProcesses = new ArrayList<Process>(n);

			for (int pid = 0; pid <n ; pid++){
				Process newP = new Process(pid);
				String[] view = new String[n];
				for (int j = 0; j<n; j++){
					if (viewsMatrix[pid][j]>0)
						view[j]=originalProcesses.get(j).getView();
				}
				newP.setView(view);
				newProcesses.add(newP);
			}
			
			return new Simplex(baseSimplex.isChromatic(), newProcesses);
		}
	}
}
