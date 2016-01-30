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
import unam.dcct.topology.Simplex;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.geometry.GeometricComplex;

/**
 * Represents the output console that displays simplicial complex textual information
 * such as its set notation representation. 
 * @author Fausto
 *
 */
public class SCOutputConsole extends ShrinkPanelPlugin implements View{
	private JTextPane textPane = JRViewer.scriptingTextPane;
	private JScrollPane contentPanel = new JScrollPane();
	private final String newLine = "\n";
	// Text that is displayed in the console. 
	private StringBuilder complexInfo;
	private final String complexInfoFormat = 
			Constants.SIMPLICIAL_COMPLEX+ " information:" + newLine
			+ Constants.OUTPUT_CONSOLE_DELIMITER
			+ "Type:%s\n" // Initial or protocol
			+ "%s" // field for distributed computing model information
			+ "%s\n" // field for chromaticity information
			+ Constants.NUMBER_OF_SIMPLICIES + ":%d\n" 
			+ "Dimension of complex: %d\n" // field for complex dimension
			+ Constants.SET_NOTATION_REPRESENTATION + ":" + newLine
			+ "%s\n";
	private final String geometricInfoFormat = 
			newLine + Constants.GEOMETRIC_INFORMATION + newLine
			+ Constants.OUTPUT_CONSOLE_DELIMITER
			+ "Number of vertices: %d\n"
			+ "Faces summary:\n%s\n";
	private String geometricInformation = "";
	
	private static SCOutputConsole instance = null;

	/**
	 * Global point of access that returns the singleton instance of this class. 
	 * @return
	 */
	public static SCOutputConsole getInstance(){
		if (instance == null){
			instance = new SCOutputConsole();
		}
		return instance;
	}
	
	private SCOutputConsole() {
		Model model = Model.getInstance();
		model.registerView(this);
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
	
	public void clearConsole(){
		complexInfo = null;
        textPane.setText("");
	}
	
	/**
	 * Builds the text that is displayed in the console. 
	 * @param complex Simplicial complex whose information is extracted in order to be displayed. 
	 * @param type type can 'initial complex' or 'protocol complex'. 
	 * @param modelInformation The current distributed computing model information. 
	 */
	private void setComplexInfo(SimplicialComplex complex, String type, String modelInformation){
		complexInfo = new StringBuilder(
				String.format(complexInfoFormat, type, 
						(!Strings.isNullOrEmpty(modelInformation)? 
								Constants.PROTOCOL_INFORMATION + ":" + modelInformation : ""),
						(String.format("%s complex ", 
								(complex.isChromatic()? Constants.CHROMATIC : Constants.NON_CHROMATIC))),
						complex.getSimplices().size(),
						complex.dimension(),
//						complex.toString()));
						buildSimplicesReport(complex)));
	}
	
	private String buildSimplicesReport(SimplicialComplex complex){
		StringBuilder sb = new StringBuilder();
		int capacity = complex.dimension() + 1;
		List<List<String>> simplicesPerDimension = new ArrayList<List<String>>(capacity);
		// Initialize
		for (int i = 0; i<capacity; i++)
			simplicesPerDimension.add(null);
		// Add simplices to corresponding dimension entry. 
		for (Simplex s : complex.getSimplices()){
			int dim = s.dimension();
			if (simplicesPerDimension.get(dim)==null)
				simplicesPerDimension.set(dim, new ArrayList<String>());
			simplicesPerDimension.get(dim).add(s.toString());
		}
		final String entryFormat = "  Number of %d-simplices: %d\n  %d-simplices:%s\n";
		int len = simplicesPerDimension.size();
		for (int i=0; i<len; i++){
			List<String> entry = simplicesPerDimension.get(i);
			if (entry!=null){
				sb.append(String.format(entryFormat, i, entry.size(), i, entry.toString()));
			}
		}

		return sb.toString();
	}
	
	/**
	 * This is for jReality use. 
	 */
	@Override
	public void install(Controller c) throws Exception {
		super.install(c);
		//this.controller = c;
		createLayout();
	}

	/**
	 * This is for jReality use. 
	 */
	@Override
	public Class<? extends SideContainerPerspective> getPerspectivePluginClass() {
		return de.jreality.plugin.basic.View.class;
	}

	/**
	 * This is for jReality use. 
	 */
	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = Constants.SIMPLICIAL_COMPLEX_CONSOLE;
		info.vendorName = "Fausto Salazar";
		info.icon = ImageHook.getIcon("select01.png");
		return info; 
	}
	
	/**
	 * When a simplicial complex is generated by the model, this is called 
	 * to write the new complex information into the console. 
	 */
	public void displayComplex() {
		clearConsole();
		Model m = Model.getInstance();
		SimplicialComplex protocolComplex = m.getProtocolComplex();
		// Check if the generated complex is initial
		if (protocolComplex==null){
			setComplexInfo(m.getInitialComplex(), Constants.INITIAL_COMPLEX, null);
		}else
		{
			String modelInfo = m.getCommunicationProtocol().toString() + newLine + "Round : " + m.getRoundCount() + newLine ;
			setComplexInfo(m.getProtocolComplex(), Constants.PROTOCOL_COMPLEX, modelInfo);
		}

		textPane.setText(complexInfo.toString() + geometricInformation.toString());
	}
	
	/**
	 * It lets display geometric information (number of vertices, faces, etc) in the console.
	 * @param geom The geometric complex object whose information will be extracted to be displayed. 
	 */
	public void setGeometricComplexInformation(GeometricComplex geom){
		if (geom!=null){

			geometricInformation = String.format(
					geometricInfoFormat, geom.getVertexCount(), 
					buildFacesReport(geom));//geom.getFacesIndices()[0].length-1,geom.getFacesIndices().length);
			
			// If displayComplex() has already been called, the text to display in the console has already been built,
			// so we just append the geometric information to it. 
			if (complexInfo!=null){
				textPane.setText(complexInfo.toString() + geometricInformation.toString());
			}
		}
	}
	
	private String buildFacesReport(GeometricComplex geom){
		StringBuilder sb = new StringBuilder();
		int[] facesDimensionCounts = new int[5];
		for (int[]face : geom.getFacesIndices()){
			facesDimensionCounts[face.length-1]++;
		}
		final String facesFormat = "  Number of %d-faces:%d\n";
		for (int i =0; i< facesDimensionCounts.length; i++){
			if (facesDimensionCounts[i]>0)
				sb.append(String.format(facesFormat, i, facesDimensionCounts[i]));
		}
		return sb.toString();
	}

	public void updateChromaticity() {
		displayComplex();
	}

	/**
	 * Clears the console
	 */
	public void reset() {
		clearConsole();
	}

	@Override
	public void creatingNewProtocolComplex() {
		// Nothing needs to be done in this step
		
	}

}
