package dcct.test;

import dcct.topology.AtomicImmediateSnapshot;
import dcct.topology.DCModel;
import dcct.topology.Simplex;
import dcct.topology.SimplicialComplex;
import dcct.process.Process;

public class Main {
	public static void main(String[] args){
		DCModel model = new DCModel(new AtomicImmediateSnapshot());
		
		Simplex s0 = new Simplex(new Process(0), new Process(1));
		
		System.out.println("Simplex s0 has dimension "+s0.dimension());
		for (Process p : s0.getProcesses()){
			System.out.println("Process "+ p.getId() + "'s view:" + p.getView());
		}
		
		SimplicialComplex initialComplex = new SimplicialComplex(s0);
		SimplicialComplex protocolComplex = model.subdivide(initialComplex);
	
		for (Simplex s: protocolComplex.getSimplices()){
			System.out.println("Simplex s has dimension "+s0.dimension());
			for (Process p : s.getProcesses()){
				System.out.println("Process "+ p.getId() + "'s view:" + p.getView());
			}
		}
		
		System.out.println("2nd round");
		protocolComplex = model.subdivide(protocolComplex);
		
		for (Simplex s: protocolComplex.getSimplices()){
			System.out.println("Simplex s has dimension "+s0.dimension());
			for (Process p : s.getProcesses()){
				System.out.println("Process "+ p.getId() + "'s view:" + p.getView());
			}
		}
	}
}
