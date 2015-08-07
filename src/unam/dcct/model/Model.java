package unam.dcct.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unam.dcct.misc.Constants;
import unam.dcct.misc.Constants.ProcessViewBrackets;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.View;
import unam.dcct.view.commands.*;

public class Model {
	private SimplicialComplex initialComplex;
	private SimplicialComplex protocolComplex;
	private CommunicationMechanism communicationMechanism;
	private Set<View> views = new HashSet<View>(5);
	private List<Color> pColors;
	private ProcessViewBrackets selectedBrackets = ProcessViewBrackets.DEFAULT;
	private int roundCount = 0;
	
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
		pColors = null;
		communicationMechanism = null;
		selectedBrackets = ProcessViewBrackets.DEFAULT;
		roundCount = 0;
		updateViews(Constants.RESET_VIEW_COMMAND);
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
		
		initialComplex = new SimplicialComplex(new Simplex(processes));
		initialComplex.setNonChromaticSimplices(new Simplex(false,processes));
		protocolComplex = null;
		updateViews(Constants.COMPLEX_UPDATE_COMMAND);
//		if (view!=null)
//			view.update(new InitialComplexCommand(view, initialComplex));
		
		//TEST
		//toString();
		
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
		return protocolComplex.isChromatic();
	}

	public void setChromatic(boolean chromatic) {
		protocolComplex.setChromatic(chromatic);
		updateViews(Constants.COMPLEX_UPDATE_COMMAND);
	}
	
	public void registerView(View v){
		views.add(v);
	}
	
	public void updateViews(String cmdType){
		for (View v: views){
			Command cmd = Command.createCommand(cmdType, v);
			cmd.execute();
		}
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
		++roundCount;
		
		updateViews(Constants.COMPLEX_UPDATE_COMMAND);

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
		return selectedBrackets.getBracketsWithFormat();
	}
	public void setSelectedBrackets(ProcessViewBrackets selectedBrackets) {
		this.selectedBrackets = selectedBrackets;
	}
	public int getRoundCount() {
		return roundCount;
	}
	
	
}
