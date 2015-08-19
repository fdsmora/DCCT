package unam.dcct.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import unam.dcct.misc.Constants;
import unam.dcct.model.ImmediateSnapshotModel.ImmediateSnapshot;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;
import unam.dcct.topology.SimplicialComplex;

public abstract class CommunicationMechanism{
	protected int t=0;
	protected int rounds=0;
	protected ScenarioGenerator scenarioGenerator;
	private SimplicialComplex newComplex;

	public SimplicialComplex communicationRound(SimplicialComplex sc){
		sc.setChromatic(true);
		newComplex = null;
		generateOrCompleteNewComplex(sc);
//		// Now lets generate all non-chromatic simplices
		sc.setChromatic(false);
		generateOrCompleteNewComplex(sc);
		
		// Default is chromatic
		sc.setChromatic(true);

		++rounds;
		return newComplex;
	}
	
	private void generateOrCompleteNewComplex(SimplicialComplex originalComplex){
		List<Simplex> originalSimplices=originalComplex.getSimplices();

		if (originalSimplices!=null){
			boolean chromatic = originalComplex.isChromatic();
			List<Simplex> allNewSimplices = generateAllNewSimplices(originalSimplices, chromatic);
			if (newComplex!=null){
				if (chromatic)
					newComplex.setChromaticSimplices(allNewSimplices);
				else
					newComplex.setNonChromaticSimplices(allNewSimplices);
			}
			else{
				newComplex = new SimplicialComplex(allNewSimplices);
			}
		}
		
	}
	
	private List<Simplex> generateAllNewSimplices(List<Simplex> allSimplices, boolean chromatic){
		List<Simplex> allNewSimplices = new ArrayList<Simplex>();

		for (Simplex s : allSimplices){
			List<Simplex> newSimplices= new ArrayList<Simplex>();
			scenarioGenerator = createScenarioGenerator(s);
			for (Scenario scenario : scenarioGenerator){
				List<Process> originalProcesses = s.getProcesses();
				List<Process> newProcesses = scenario.execute(originalProcesses);
				Simplex newSimplex = chromatic ? new Simplex(newProcesses) : new Simplex(false, newProcesses);
				newSimplex.setParent(s);
				//generateNonChromaticSimplex(s, scenario, newSimplex, newProcesses);
				newSimplices.add(newSimplex);
			}
			//generateNonChromaticSimplices(newSimplices, s);
			allNewSimplices.addAll(newSimplices);
		}
		
		return allNewSimplices;
	}

	protected abstract ScenarioGenerator createScenarioGenerator(Simplex s);
    
	public static String name(){
		return "Please implement this in subclass with appropiate name";
	}
	
	public static String basicMechanismName(){
		return "Please implement this in subclass with appropiate name";
	}
	
	public static CommunicationMechanism createCommunicationMechanism(String name){
		if (name.equals(Constants.IMMEDIATE_SNAPSHOT))
			return new ImmediateSnapshot();
		return null;
	}
	
	public int getRounds(){
		return rounds;
	}
	
	public int get_t(){
		return t;
	};
	public void set_t(int t){
		this.t=t;
	}
		
	protected interface ScenarioGenerator extends Iterable<Scenario>{
		Iterator<Scenario> iterator();
	}
	
	protected interface Scenario{
		List<Process> execute(List<Process> originalProcesses);
	}
}
