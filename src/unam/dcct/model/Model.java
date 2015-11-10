package unam.dcct.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import javax.security.auth.login.Configuration;
import unam.dcct.misc.Constants;
import unam.dcct.misc.Configuration;
import unam.dcct.misc.Constants.ProcessViewBrackets;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.View;
import unam.dcct.view.commands.*;

/**
 * It is the model component of the application's MVC architecture.
 * Contains methods for manipulating the application's state.
 * In particular generating and manipulating the simplicial complexes
 * that are displayed and other information related to them.
 * It implements the Singleton design pattern as there can be only one
 * instance of this class during the application execution.  
 * @author Fausto Salazar
 *
 */
public class Model {
	private SimplicialComplex initialComplex;
	private SimplicialComplex protocolComplex;
	private CommunicationProtocol communicationProtocol;
	private Set<View> views = new HashSet<View>(5);
	private List<Color> pColors;
	private Color nonChromaticColor= null;
	private ProcessViewBrackets selectedBrackets = ProcessViewBrackets.DEFAULT;
	private int roundCount = 0;
	private Configuration config = Configuration.getInstance();
	
	// Singleton design pattern
	private static Model instance = null;
	/**
	 * Global point of access that returns the singleton instance of this class. 
	 * @return
	 */
	public static Model getInstance(){
		if (instance == null)
			instance = new Model();
		return instance;
	}
	private Model(){
	}
	
	/**
	 * Resets all generated complexes and input data. Also causes all views to reset, that is,
	 * clear all displayed information. 
	 */
	public void reset(){
		initialComplex =protocolComplex= null;
		pColors = null;
		communicationProtocol = null;
		selectedBrackets = ProcessViewBrackets.DEFAULT;
		roundCount = 0;
		updateViews(Constants.RESET_VIEW_COMMAND);
	}
	
	/**
	 * Creates an initial simplicial complex. 
	 * @param pNames A list of names that will be assigned to each process of the new initial complex. 
	 * @return The initial complex that has been created. 
	 */
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

		//TEST
		//toString();
		
		return initialComplex;
	}

	/**
	 * Returns the initial complex that has been created.
	 * @return The initial complex. 
	 */
	public SimplicialComplex getInitialComplex(){
		return initialComplex;
	}
	/**
	 * Returns the protocol complex that was generated in the last communication round.
	 * @return The protocol complex
	 */
	public SimplicialComplex getProtocolComplex() {
		return protocolComplex;
	}

	/**
	 * Clears the last generated protocol complex. 
	 */
	public void clearProtocolComplex() {
		this.protocolComplex = null;
	}

	/**
	 * Returns whether the last generated protocol complex is chromatic or not.
	 * @return
	 */
	public boolean isChromatic() {
		return protocolComplex.isChromatic();
	}

	/**
	 * Toogles the chromaticity of the last generated protocol complex. Calling this method causes all views to be updated. 
	 * @param chromatic
	 */
	public void setChromatic(boolean chromatic) {
		protocolComplex.setChromatic(chromatic);
		updateViews(Constants.CHROMATICITY_UPDATE_COMMAND);
	}
	
	/**
	 * Registers a {@link View} so that it is notified when some changes in the model occur. 
	 * @param v The object whose class implements the View interface.
	 */
	public void registerView(View v){
		views.add(v);
	}
	/**
	 * Updates all registered Views. This method should be called when a change in the Model occurs.
	 * Each specific change is represented by a commandType. 
	 * @param commandType
	 * @see unam.dcct.view.commands.Command
	 */
	private void updateViews(String commandType){
		for (View v: views){
			Command cmd = Command.createCommand(commandType, v);
			cmd.execute();
		}
	}
	/**
	 * Generates a protocol complex. This is generated by executing a simulation of a 
	 * communication round in the distributed system represented by the protocol. Once the protocol complex for this round 
	 * is generated, all registered views are notified and updated. 
	 */
	public void executeRound(){
		if (communicationProtocol == null)
			throw new NullPointerException("No communicationMechanism specified");
		
		boolean previousColoring = true;
		if (protocolComplex!=null)
			previousColoring = protocolComplex.isChromatic();
		
		SimplicialComplex baseComplex = (protocolComplex!=null? protocolComplex : initialComplex);
		protocolComplex = communicationProtocol
					.executeRound(baseComplex);
		protocolComplex.setChromatic(previousColoring);
		++roundCount;
		
		updateViews(Constants.COMPLEX_UPDATE_COMMAND);

	}
	/** 
	 * Returns the communication mechanism chosen by the user. 
	 * @return An object representing the communication protocol. 
	 */
	public CommunicationProtocol getCommunicationProtocol() {
		return communicationProtocol;
	}

	/**
	 * Sets the communication protocol that the Model will use for simulating communication rounds and generating protocol complexes. 
	 * @param cm A string that has the name of the communicationProtocol
	 */
	public void setCommunicationProtocol(String cm) {
		this.communicationProtocol = CommunicationProtocol.createCommunicationProtocol(cm);

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
			return config.DEFAULT_COLORS;
		return pColors;
	}
	
	public Color getNonChromaticColor(){
		if (nonChromaticColor == null)
			nonChromaticColor = config.NON_CHROMATIC_COLOR;
		return nonChromaticColor;
	}
	
	public void setNonChromaticColor(Color color){
		nonChromaticColor = color;
	}

	public void setColors(List<Color> pColors) {
		
//		// Add additional colors to prevent cases when there are less colors than processes.
//		if (pColors.size()<Constants.MAX_COLORS){
//			for (int i=Constants.MAX_COLORS-pColors.size(); i>0; i--){
//				pColors.add(Constants.DEFAULT_COLORS[i]);
//			}
//		}
//		this.pColors = config.DEFAULT_COLORS;
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
