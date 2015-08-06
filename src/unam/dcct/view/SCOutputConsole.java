package unam.dcct.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.google.common.base.Strings;

import de.jreality.plugin.JRViewer;
import de.jreality.plugin.icon.ImageHook;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jtem.jrworkspace.plugin.sidecontainer.SideContainerPerspective;
import de.jtem.jrworkspace.plugin.sidecontainer.template.ShrinkPanelPlugin;
import unam.dcct.misc.Constants;
import unam.dcct.model.Model;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.geometry.GeometricComplex;

public class SCOutputConsole extends ShrinkPanelPlugin implements View{
	private JTextPane textPane = JRViewer.scriptingTextPane;
	private JScrollPane contentPanel = new JScrollPane();
	private StringBuilder complexInfo;
	private final String newLine = "\n";
	private final String complexInfoFormat = 
			Constants.SIMPLICIAL_COMPLEX+ " information:" + newLine
			+ Constants.OUTPUT_CONSOLE_DELIMITER
			+ "Type:%s\n" // Initial or protocol
			+ "%s" // field for distributed computing model information
			+ "%s\n" // field for chromaticity information
			+ "Number of simplices: %d\n" 
			+ "Dimension of complex: %d\n" // field for complex dimension
			+ Constants.SET_NOTATION_REPRESENTATION + ":" + newLine
			+ "%s\n";
	
	private static SCOutputConsole instance = null;

	public static SCOutputConsole getInstance(){
		if (instance == null){
			instance = new SCOutputConsole();
		}
		return instance;
	}
	
	private SCOutputConsole() {
        setInitialPosition(ShrinkPanelPlugin.SHRINKER_BOTTOM);
	}
	
	private void createLayout() {
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
		complexInfo = null;
        textPane.setText("");
	}
	
	public void setComplexInfo(SimplicialComplex complex, String type, String modelInformation){
		complexInfo = new StringBuilder(
				String.format(complexInfoFormat, type, 
						(!Strings.isNullOrEmpty(modelInformation)? 
								Constants.MODEL_INFORMATION + ":" + modelInformation : ""),
						(String.format("%s complex ", 
								(complex.isChromatic()? Constants.CHROMATIC : Constants.NON_CHROMATIC))),
						complex.getSimplices().size(),
						complex.dimension(),
						complex.toString()));
	}
	
	@Override
	public void install(Controller c) throws Exception {
		super.install(c);
		//this.controller = c;
		createLayout();
	}

	@Override
	public Class<? extends SideContainerPerspective> getPerspectivePluginClass() {
		return de.jreality.plugin.basic.View.class;
	}


	@Override
	public void start() {
		// TODO Auto-generated method stub
	}

	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = Constants.SIMPLICIAL_COMPLEX_CONSOLE;
		info.vendorName = "Fausto Salazar";
		info.icon = ImageHook.getIcon("select01.png");
		return info; 
	}
	
	@Override
	public void displayComplex(SimplicialComplex complex) {
		resetConsole();
		Model m = Model.getInstance();
		// Check if the generated complex is initial
		SimplicialComplex protocolComplex = m.getProtocolComplex();
		if (protocolComplex==null){
			setComplexInfo(m.getInitialComplex(), Constants.INITIAL_COMPLEX, null);
		}else
		{
			String modelInfo = m.getCommunicationMechanism().toString() + newLine + "Round : " + m.getRoundCount() + newLine ;
			setComplexInfo(m.getProtocolComplex(), Constants.PROTOCOL_COMPLEX, modelInfo);
		}
		textPane.setText(complexInfo.toString());
	}

	@Override
	public void updateChromaticity(boolean chromatic) {
		
	}

	@Override
	public void reset() {
		resetConsole();
	}

}
