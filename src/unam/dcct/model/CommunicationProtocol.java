package unam.dcct.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import unam.dcct.misc.Constants;
import unam.dcct.model.ImmediateSnapshot.ImmediateSnapshot;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;
import unam.dcct.topology.SimplicialComplex;

/**
 * Base class that represents a distributed computing communication protocol. 
 * It's most important method is {@link #executeRound}.
 * @author Fausto Salazar
 * @see ImmediateSnapshot
 */
public abstract class CommunicationProtocol{
	protected int t=0;
	protected int rounds=0;
	protected ScenarioGenerator scenarioGenerator;
	private SimplicialComplex newComplex;

	/**
	 * Simulates a communication round of the distributed system's protocol represented
	 * by this instance. 
	 * <p>
	 * The communication round is executed two times, one for the chromatic processes of the 
	 * chromatic representation of the base complex and one for the non-chromatic processes of the
	 * non-chromatic representation of the base complex. This is done so as to produce the protocol complex
	 * in both representations: chromatic and non-chromatic. 
	 * @param baseComplex The initial complex (if this is the first round) or the protocol complex (if this is a round other than the first)
	 * whose containing processes will communicate during this round. 
	 * @return A protocol complex that results of the execution of the communication round. This complex contains simplices which in turn
	 * contain processes with views that resulted from executing all possible communication scenarios that could have taken place during the 
	 * communication round's execution. 
	 */
	public SimplicialComplex executeRound(SimplicialComplex baseComplex){
		
		boolean originalChromaticity = baseComplex.isChromatic();
		baseComplex.setChromatic(true);
		newComplex = null;
		generateOrCompleteNewComplex(baseComplex);
		// Now lets generate all non-chromatic simplices
		baseComplex.setChromatic(false);
		generateOrCompleteNewComplex(baseComplex);
		
		// Leave it as it was.
		baseComplex.setChromatic(originalChromaticity);

		++rounds;
		return newComplex;
	}
	
	/**
	 * I think this method is a little confusing so I give a little long explanation.
	 * The method executeRound(SimplicialComplex) first calls this method to generate the chromatic 
	 * protocol complex of this round of execution, that is, it sets the newComplex field. 
	 * Then, to generate the non-chromatic version of the protocol complex, we don't need to create 
	 * a new SimplicialComplex instance. We have to generate the list of new non-chromatic simplices with a call to  
	 * generateAllNewSimplices(originalSimplices, false), and assign this list  to
	 * the already created newComplex with a call to setNonChromaticSimplices(allNewSimplices);
	 * @param baseComplex
	 */
	private void generateOrCompleteNewComplex(SimplicialComplex baseComplex){
		List<Simplex> originalSimplices=baseComplex.getSimplices();

		if (originalSimplices!=null){
			boolean chromatic = baseComplex.isChromatic();
			List<Simplex> allNewSimplices = generateAllNewSimplices(originalSimplices, chromatic);
			if (newComplex!=null){
				// This corresponds to the 'complete new complex' part of this method's name.
				// This will be executed when this method is called for more than the first 
				// time, like it is done in executeRound(SimplicialComplex)
				if (chromatic)
					newComplex.setChromaticSimplices(allNewSimplices);
				else
					newComplex.setNonChromaticSimplices(allNewSimplices);
			}
			else{
				// This corresponds to the 'generate new complex' part of this method's name.
				newComplex = new SimplicialComplex(allNewSimplices);
			}
		}
		
	}
	
	/**
	 * Generates the new simplices that will be used to build the new protocol complex of this round. 
	 * @param baseSimplices The simplices that contain the processes that will communicate, in other words, the 
	 * simplices that will give rise to the simplicices of the new protocol complex. 
	 * @param chromatic Determines if the simplices that this method generates will be chromatic or non-chromatic. 
	 * @return The list of new simplices that can be used to build the new protocol complex. These simplices 
	 * will be chromatic or non chromatic, depending on the chromatic parameter. 
	 */
	private List<Simplex> generateAllNewSimplices(List<Simplex> baseSimplices, boolean chromatic){
		List<Simplex> allNewSimplices = new ArrayList<Simplex>();

		for (Simplex s : baseSimplices){
			List<Simplex> newSimplices= new ArrayList<Simplex>();
			scenarioGenerator = createScenarioGenerator(s.dimension());
			for (Scenario scenario : scenarioGenerator){
				List<Process> originalProcesses = s.getProcesses();
				List<Process> newProcesses = scenario.execute(originalProcesses);
				Simplex newSimplex = chromatic ? new Simplex(newProcesses) : new Simplex(false, newProcesses);
				newSimplex.setParent(s);
				newSimplices.add(newSimplex);
			}
			allNewSimplices.addAll(newSimplices);
		}
		
		return allNewSimplices;
	}

	/**
	 * Specific communication protocols implement this method in order to create their 
	 * particular set of all possible scenarios of execution. 
	 * @param dimension The dimension of the simplex. This is used to generate all possible
	 * scenarios of execution in which dimension+1 processes (which is the total number of 
	 * processes in the simplex) communicate during the round.
	 * <p>
	 * This is a "Factory method" because subclasess "manufacture" 
	 * the specific implementation of the {@link ScenarioGenerator}.
	 * @return An {@link ScenarioGenerator} instance which lets clients iterate over all generated scenarios of execution of this protocol.
	 */
	protected abstract ScenarioGenerator createScenarioGenerator(int dimension);
    
	/**
	 * This is intended for specific communication protocols to provide their names in order to
	 * use it, for example, in UI combo boxes. 
	 * @return The name of this communication protocol.
	 * @see Constants#availableCommunicationProtocols
	 */
	public static String getName(){
		return "Please implement this in subclass with appropiate name";
	}
	/**
	 * This is intended for specific communication protocols to provide their names in order to
	 * use it, for example, in UI combo boxes. 
	 * @return The name of this communication protocol.
	 * @see Constants#availableCommunicationProtocols
	 */
	public static String getBasicProtocolName(){
		return "Please implement this in subclass with appropiate name";
	}
	
	/**
	 * Simple factory method that creates specific communication protocol instances.
	 * @param name The name of the communication protocol whose instance will be created. 
	 * @return the communication protocol instance created. 
	 */
	public static CommunicationProtocol createCommunicationProtocol(String name){
		if (name.equals(Constants.IMMEDIATE_SNAPSHOT))
			return new ImmediateSnapshot();
		return null;
	}
	
	//
	public int getRounds(){
		return rounds;
	}
	
	public int get_t(){
		return t;
	};
	public void set_t(int t){
		this.t=t;
	}
		
	/**
	 * This interface provides a way to iterate over all generated scenarios of execution 
	 * of a communication round for a particular protocol. 
	 * <p>
	 * It is an implementation of the iterator pattern. 
	 * @author Fausto Salazar
	 *
	 */
	protected interface ScenarioGenerator extends Iterable<Scenario>{
		//Iterator<Scenario> iterator();
	}
	
	/**
	 * Represents an scenario of execution of a communication round for a particular protocol.
	 * @author Fausto
	 *
	 */
	protected interface Scenario{
		/**
		 * Simulates how the process communicate executing a round of the protocol but
		 * in the order and circumstances specified in this scenario.
		 * @param originalProcesses The process that will participate in the simulation. 
		 * @return A list that contains copies of originalProcesses that have views that represent the knowledge this processes 
		 * acquired after communicating in the way specified by this scenario. 
		 */
		List<Process> execute(List<Process> originalProcesses);
	}
}
