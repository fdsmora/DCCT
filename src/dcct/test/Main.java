package dcct.test;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import dcct.topology.AtomicImmediateSnapshot;
import dcct.topology.DCModel;
import dcct.topology.Simplex;
import dcct.topology.SimplicialComplex;
import dcct.visualization.Visualizer;
import dcct.process.Process;

public class Main {
	
	protected static DCModel model;
	protected static List<Color> colors = Arrays.asList(Color.WHITE,Color.BLACK,Color.GREEN,Color.RED,
			Color.BLUE,Color.YELLOW, Color.PINK, Color.CYAN, Color.MAGENTA);
	
	public static void main(String[] args){
		
		model = new DCModel(new AtomicImmediateSnapshot());
		
		Simplex s0 = new Simplex(new Process(0), new Process(1), new Process(2));
		
		System.out.println("Simplex s0 has dimension "+s0.dimension());
		for (Process p : s0.getProcesses()){
			System.out.println("Process "+ p.getId() + "'s view:" + p.getView());
		}
		
		SimplicialComplex initialComplex = new SimplicialComplex(s0);
		
		int rounds = 0;
		
		SimplicialComplex protocolComplex = testSubdivison(initialComplex, rounds);
		
		Visualizer visualizer = new Visualizer();
		visualizer.draw(protocolComplex,
				buildColors(protocolComplex.totalDistinctProcesses()));
	}
	
	public static SimplicialComplex testSubdivison(SimplicialComplex sc, int rounds){
		SimplicialComplex tComplex = sc;
		for (int i=0;i<rounds;i++){
			System.out.println("-------------------\nround: " + i);
			System.out.println("-------------------");
			SimplicialComplex protocolComplex = model.subdivide(tComplex);
		
			for (Simplex s: protocolComplex.getSimplices()){
				System.out.println("Simplex s has dimension "+s.dimension());
				for (Process p : s.getProcesses()){
					System.out.println("Process "+ p.getId() + "'s view:" + p.getView());
				}
			}
			System.out.println("Complex has "+protocolComplex.getSimplices().size()+" simplices.");
			tComplex = protocolComplex;
		}
		return tComplex;
	}
	
	public static List<Color> buildColors(int k){
		return colors.subList(0, k);
	}
}