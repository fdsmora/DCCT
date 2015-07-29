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
	
	public SimplicialComplex communicationRound(SimplicialComplex sc){
		List<Simplex> newSimplices = new ArrayList<Simplex>();
		
		for (Simplex s : sc.getSimplices()){
			scenarioGenerator = createScenarioGenerator(s);
			for (Scenario scenario : scenarioGenerator){
				List<Process> originalProcesses = s.getProcesses();
				List<Process> newProcesses = scenario.execute(originalProcesses);
				Simplex newSimplex = new Simplex(newProcesses);
				newSimplex.setParent(s);
				//generateNonChromaticSimplex(s, scenario, newSimplex, newProcesses);
				newSimplices.add(newSimplex);
			}
		}
		++rounds;
		return new SimplicialComplex(newSimplices);
	}
	
    private void generateNonChromaticSimplex(Simplex s, Scenario scenario,
			Simplex newSimplex, List<Process> newProcesses) {
    	
    	s.setChromatic(false);
		newSimplex.setChromatic(false);
		List<Process> originalNonChromaticProcesses = s.getProcesses();
		if (originalNonChromaticProcesses!=null)
			newProcesses = scenario.execute(originalNonChromaticProcesses);
		newSimplex.setNonChromaticSimplex(newProcesses);
		s.setChromatic(true);
		newSimplex.setChromatic(true);
	}
    
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
