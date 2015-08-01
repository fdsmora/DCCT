package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import configuration.Constants;
import configuration.Constants.ProcessViewBrackets;
import view.ChromaticityChangedCommand;
import view.InitialComplexCommand;
import view.ProtocolComplexCommand;
import view.ResetViewCommand;
import view.View;
import dctopology.Simplex;
import dctopology.SimplicialComplex;
import dctopology.Process;

public class Model {
	private SimplicialComplex initialComplex;
	private SimplicialComplex protocolComplex;
	private CommunicationMechanism communicationMechanism;
	//protected boolean chromatic = true;
	private View view;
	private List<Color> pColors;
	
	// Singleton design pattern
	private static Model instance = null;
	public static Model getInstance(){
		if (instance == null)
			instance = new Model();
		return instance;
	}
	private Model(){
	}
	
	public void reset(){
		initialComplex =protocolComplex= null;
//		chromatic = true;
		pColors = null;
		communicationMechanism = null;
		if (view!=null)
			view.update(new ResetViewCommand(view,null)); // reset
	}
	
	public SimplicialComplex createInitialComplex(List<String> pNames){
		int n = pNames.size();
		List<Process> processes = new ArrayList<Process>(n);
		int idCounter = 0;
		for (String pName : pNames){
			Process p = new Process(idCounter++);
			p.setName(pName);
			processes.add(p);
		}
//		Process[] processes = new Process[n];
//		for (int i = 0; i<n; i++){
//			processes[i]= new Process(i);
//			processes[i].setName(pNames.get(i));
//		}
		
		initialComplex = new SimplicialComplex(new Simplex(processes));
		initialComplex.setNonChromaticSimplices(new Simplex(false,processes));
		protocolComplex = null;
		if (view!=null)
			view.update(new InitialComplexCommand(view, initialComplex));
		
		//Test
		toString();
		
		return initialComplex;
	}
	
	public void setInitialComplex(SimplicialComplex sc){
		initialComplex=sc;
	}
	
	public SimplicialComplex getInitialComplex(){
		return initialComplex;
	}

	public SimplicialComplex getProtocolComplex() {
		return protocolComplex;
	}

	public void setProtocolComplex(SimplicialComplex pc) {
		this.protocolComplex = pc;
	}

	public boolean isChromatic() {
		//return chromatic;
		return protocolComplex.isChromatic();
	}

	public void setChromatic(boolean chromatic) {
		//this.chromatic = chromatic;
		protocolComplex.setChromatic(chromatic);
		// Chromatic changed
		if (view!=null)
			view.update(new ChromaticityChangedCommand(view, protocolComplex)); 
	}
	
	public void registerView(View v){
		view = v;
	}
	
	public void executeRound(){
		if (communicationMechanism == null)
			throw new NullPointerException("No communicationMechanism specified");
		
		boolean previousColoring = true;
		if (protocolComplex!=null)
			previousColoring = protocolComplex.isChromatic();
		
		protocolComplex = communicationMechanism
					.communicationRound(protocolComplex!=null? 
							protocolComplex : initialComplex);
		protocolComplex.setChromatic(previousColoring);
	
		if (view!=null)
			view.update(new ProtocolComplexCommand(view, protocolComplex));
		//TEST
		toString();
	}

	public CommunicationMechanism getCommunicationMechanism() {
		return communicationMechanism;
	}

	public void setCommunicationMechanism(String c) {
		this.communicationMechanism = CommunicationMechanism.createCommunicationMechanism(c);
//		if (view!=null)
//			view.update("u"); // Update communication model 
	}

	public String toString(){
		System.out.println("\nInitialComplex\n===================");
		if (initialComplex!=null)
			System.out.println(initialComplex.toString());
		else 
			System.out.println("null");
		System.out.println("ProtocolComplex\n===================");
		if (protocolComplex!=null){
			boolean originalChromacity = protocolComplex.isChromatic();
			protocolComplex.setChromatic(true);
			System.out.println(protocolComplex.toString());
			System.out.println("<<<<Non-chromatic version>>>>");
			protocolComplex.setChromatic(false);
			System.out.println(protocolComplex.toString());
			protocolComplex.setChromatic(originalChromacity);
		}
		else 
			System.out.println("null");
		return null;
	}

	public List<Color> getColors() {
		if (pColors == null)
			return Arrays.asList(Constants.DEFAULT_COLORS);
		return pColors;
	}

	public void setColors(List<Color> pColors) {
		// Add additional colors to prevent cases when there are less colors than processes.
		if (pColors.size()<Constants.MAX_COLORS){
			for (int i=Constants.MAX_COLORS-pColors.size(); i>0; i--){
				pColors.add(Constants.DEFAULT_COLORS[i]);
			}
		}
		this.pColors = pColors;
	}
	public String getSelectedBrackets() {
		return ProcessViewBrackets.DEFAULT.getBrackets();
	}
	
	
}
