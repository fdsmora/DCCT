package view.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import configuration.Constants;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.View;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.sidecontainer.SideContainerPerspective;
import de.jtem.jrworkspace.plugin.sidecontainer.template.ShrinkPanelPlugin;

public class SCOutputConsole extends ShrinkPanelPlugin {
	protected JTextPane textPane = JRViewer.scriptingTextPane;
	protected Controller controller = null;
	protected JScrollPane contentPanel = new JScrollPane();
	protected String consoleContent = "";
	protected StringBuilder initialComplexInfo;
	protected List<String> protocolComplexInfo = new ArrayList<String>();
	protected List<Integer> numberOfSimplicesChromatic =new ArrayList<Integer>();
	protected List<Integer> numberOfSimplicesNonChromatic =new ArrayList<Integer>();
	protected String communicationModel = "";
	protected int round = 0;
	
	public SCOutputConsole() {
        setInitialPosition(ShrinkPanelPlugin.SHRINKER_BOTTOM);
	}
	
	protected void createLayout() {
		Dimension d = new Dimension(400, 250);
        contentPanel.setPreferredSize(d);
        contentPanel.setMinimumSize(d);
        shrinkPanel.setLayout(new GridLayout());
        shrinkPanel.add(contentPanel);
        shrinkPanel.setShrinked(false);
        contentPanel.setViewportView(textPane);
        textPane.setText(Constants.SIMPLICIAL_COMPLEX_CONSOLE);
        textPane.setEditable(false);
        textPane.setCaretColor(Color.BLACK);
	}
	
	public void resetConsole(){
        textPane.setText(Constants.SIMPLICIAL_COMPLEX_CONSOLE);
        initialComplexInfo = null;
        protocolComplexInfo = new ArrayList<String>();
        numberOfSimplicesChromatic =new ArrayList<Integer>();
        numberOfSimplicesNonChromatic =new ArrayList<Integer>();
        communicationModel = "";
        round = 0;
	}
	
	public void resetProtocolComplexInfo(){
		protocolComplexInfo = new ArrayList<String>();
		round = 0;
		numberOfSimplicesChromatic = new ArrayList<Integer>();
		numberOfSimplicesNonChromatic= new ArrayList<Integer>();
	}
	
//	public void addNumberOfChromaticSimplices( int n){
//		numberOfSimplicesChromatic.set(numberOfSimplicesChromatic.size()-1, n);
//	}
//	public void addNumberOfNonChromaticSimplices( int n){
//		numberOfSimplicesNonChromatic.set(numberOfSimplicesNonChromatic.size()-1, n);
//
//	}
	
	public void addProtocolComplexInfo(String info, int numChr, int numNonChr){
		protocolComplexInfo.add(info);
		numberOfSimplicesChromatic.add(numChr);
		numberOfSimplicesNonChromatic.add(numNonChr);
		round++;
	}
	
	public void addNonChromaticInfo(int numNonChr){
		numberOfSimplicesNonChromatic.set(round-1, numNonChr);
	}
	
	public void setInitialComplexInfo(String info){
		initialComplexInfo = new StringBuilder("\n" +Constants.INITIAL_COMPLEX+ " information:\n"
									+ Constants.OUTPUT_CONSOLE_DELIMITER
									+ Constants.SET_NOTATION_REPRESENTATION + ":\n");
									 
		initialComplexInfo.append(info + "\n\n");
	}
	
	public void print(){
		StringBuilder output = new StringBuilder(initialComplexInfo);
		
		String format = "\n" + Constants.PROTOCOL_COMPLEX + " information:\n" + Constants.OUTPUT_CONSOLE_DELIMITER
				+ Constants.COMMUNICATION_MODEL + ":%s\n" 
				+ "Round:%d\n" 
				+ Constants.NUMBER_OF_SIMPLICIES + " (" + Constants.CHROMATIC + "):%d\n"
				+ "%s" // special slot for number of simplices of non-chromatic complex.
				+ Constants.SET_NOTATION_REPRESENTATION + ":\n%s\n";
		
		String formatNonChromatic = Constants.NUMBER_OF_SIMPLICIES + " (" + Constants.NON_CHROMATIC + "):%d\n";
		StringBuilder protInfo = new StringBuilder();
		if (round>0){
			for (int i=1; i<=round; i++){
				int numChr = numberOfSimplicesChromatic.get(i-1);
				
				int numNonChr = numberOfSimplicesNonChromatic.get(i-1);
						
				protInfo.append(String.format(format, 
						communicationModel,
						round, 
						numChr,
						// When Number of simplices of non-chromatic complex is unknown, omit it.
						(numNonChr>0? String.format(formatNonChromatic, numNonChr) : "") ,
						protocolComplexInfo.get(i-1)));
			}
			output.append(protInfo);
		}
		textPane.setText(output.toString());
	}
	
	@Override
	public void install(Controller c) throws Exception {
		super.install(c);
		this.controller = c;
		createLayout();
	}

	@Override
	public Class<? extends SideContainerPerspective> getPerspectivePluginClass() {
		return View.class;
	}

	public String getConsoleContent() {
		return consoleContent;
	}

	public void setConsoleContent(String consoleContent) {
		this.consoleContent = consoleContent;
	}

	public void setCommunicationModel(String communicationModel) {
		this.communicationModel = communicationModel;
	}

}
