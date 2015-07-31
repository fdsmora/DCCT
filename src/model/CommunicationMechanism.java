package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import model.ImmediateSnapshotModel.ImmediateSnapshot;
import configuration.Constants;
import dctopology.Process;
import dctopology.Simplex;
import dctopology.SimplicialComplex;

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
	
//    private void generateNonChromaticSimplex(Simplex s, Scenario scenario,
//			Simplex newSimplex, List<Process> newProcesses) {
//    	
//    	s.setChromatic(false);
//		newSimplex.setChromatic(false);
//		List<Process> originalNonChromaticProcesses = s.getProcesses();
//		if (originalNonChromaticProcesses!=null)
//			newProcesses = scenario.execute(originalNonChromaticProcesses);
//		newSimplex.setNonChromaticSimplex(newProcesses);
//		s.setChromatic(true);
//		newSimplex.setChromatic(true);
//	}
	
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
		
//		List<Simplex> newSimplices = new ArrayList<Simplex>();
//
//		// Make sure it is chromatic.
//		//parent.setChromatic(true);
//		
//		scenarioGenerator = createScenarioGenerator(parent);
//		for (Scenario scenario : scenarioGenerator){
//			List<Process> originalProcesses = parent.getProcesses();
//			List<Process> newProcesses = scenario.execute(originalProcesses);
//			Simplex newSimplex = new Simplex(newProcesses);
//			newSimplex.setParent(parent);
//			//generateNonChromaticSimplex(s, scenario, newSimplex, newProcesses);
//			newSimplices.add(newSimplex);
//		}
//		
//		return newSimplices;
	}

	
//	private void generateNonChromaticSimplices(List<Simplex> newSimplices, Simplex parent){
//		parent.setChromatic(false);
//		
//		List<Process> originalNonChromaticProcesses = parent.getProcesses();
//		List<Process> newProcesses=null;
//		
//		// If originalNonChromaticProcesses is null, then parent is an initial complex simplex,
//		// so the new non-chromatic processes must be built from the new simplices's processes 
//		// because their views are already generated. 
//		// Otherwise, the new non-chromatic processes must be built from the non-chromatic processes
//		// of the parent, because doing it like in the above case produces incorrect processes views. 
//		scenarioGenerator=null;
//		Iterator<Scenario> scnIterator = null;
//		if (originalNonChromaticProcesses!=null){
//			scenarioGenerator = createScenarioGenerator(parent);
//			scnIterator =  scenarioGenerator.iterator();
//		}
//		for (Simplex ns : newSimplices){
//			if (scnIterator!=null){
//				Scenario scn = scnIterator.next();
//				newProcesses = scn.execute(originalNonChromaticProcesses);
//			}
//			else {
//				newProcesses = new ArrayList<Process>(ns.getProcessCount());
//				for(Process p :ns.getProcesses()){
//					Process copy = (Process)p.clone();
//					newProcesses.add(copy);
//				}
//			}
//			
//			ns.setNonChromaticSimplex(newProcesses);
//		}
//		// Reset it.
//		parent.setChromatic(true);
//		
//	}
    
	protected abstract ScenarioGenerator createScenarioGenerator(Simplex s);
    
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
