package view.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import configuration.Constants;
import dctopology.SimplicialComplex;
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
	protected StringBuilder protocolComplexInfo;
	
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
        initialComplexInfo = 
        		protocolComplexInfo = null;
	}
	
	public void setModelComplexInfo(String modelInfo, boolean chromatic){
		protocolComplexInfo = new StringBuilder("Model:"+ modelInfo + "\n" 
								+ Constants.CHROMATIC + (chromatic? "yes" : "no") + "\n" );
	}
	
	public void appendProtocolComplexInfo(SimplicialComplex complex, int round){
		if (protocolComplexInfo==null)
			protocolComplexInfo = new StringBuilder();
		protocolComplexInfo.append("Round:" + Integer.toString(round)+ "\n" 
									+ Constants.NUMBER_OF_SIMPLICIES + complex.getSimplices().size() + "\n"  
									+ Constants.PROTOCOL_COMPLEX + "\n" + Constants.OUTPUT_CONSOLE_DELIMITER + "\n" 
									
									+ complex.toString() + "\n\n");
	}
	
	public void setInitialComplexInfo(SimplicialComplex complex){
		initialComplexInfo = new StringBuilder(Constants.INITIAL_COMPLEX+ "\n"
									+ Constants.NUMBER_OF_SIMPLICIES + complex.getSimplices().size() + "\n"
									+ Constants.OUTPUT_CONSOLE_DELIMITER + "\n"); 
									 
		initialComplexInfo.append(complex.toString() + "\n\n");
	}
	
	public void print(){
		StringBuilder output = new StringBuilder(initialComplexInfo);
		if (protocolComplexInfo != null)
			output.append(protocolComplexInfo);
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

}
