package unam.dcct.view;

import java.util.ArrayList;
import java.util.List;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.plugin.icon.ImageHook;
import de.jreality.plugin.menu.CameraMenu;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;
import de.jreality.shader.Color;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import unam.dcct.misc.Configuration;
import unam.dcct.misc.Constants;
import unam.dcct.model.AbstractModel;
import unam.dcct.model.Model;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.UI.InteractiveToolsPanel;
import unam.dcct.view.UI.SimplicialComplexPanel;
import unam.dcct.view.commands.Command;
import unam.dcct.view.geometry.GeometricComplex;
import unam.dcct.view.geometry.Geometry;
/***
 * This class contains methods and logic that produces geometric visualizations of
 * simplicial complexes and simplices using jReality library. 
 * <b>
 * This class implements the {@link unam.dcct.view.View} interface because
 * some panels may need to be notified about changes in its state.  
 * @author Fausto Salazar
 * @see SimplicialComplexPanel
 * @see unam.dcct.view.geometry.Geometry
 * @see unam.dcct.view.geometry.Face
 * @see unam.dcct.view.geometry.GeometricComplex
 */
public class jRealityView extends AbstractModel implements View {

	private Model model;
	private Geometry geometricObject;
	private JRViewer viewer;
	private SceneGraphComponent sgc;
	private SceneGraphComponent sgcV;
	private IndexedFaceSetFactory faceFactory;
	private PointSetFactory psf;
	private static jRealityView instance = null;

	private ViewShrinkPanelPlugin simplicialComplexPanelPlugin;
	private InteractiveToolsPanel interactionControlPanel;
	
	private ContentAppearance contentAppearance;
	private boolean disconnectedFaces;
	
	/**
	 * Global point of access that returns the singleton instance of this class. 
	 * @return
	 */
	public static jRealityView getInstance(){
		if (instance == null)
			instance = new jRealityView();
		return instance;
	}
	
	private jRealityView(){		
		model = Model.getInstance();
		model.registerView(this);
	}
	
	/**
	 * Clears the screen.
	 */
	public void reset(){
		psf = null; 
		faceFactory = null;
		sgcV.setGeometry(null);
		sgcV.removeAllChildren();
		sgcV = new SceneGraphComponent();
		sgc.setGeometry(null);
		sgc.removeAllChildren();
		sgc.addChild(sgcV);
		
		// Hide unnecessary panels
		contentAppearance.setShowPanel(false);
		interactionControlPanel.setShowPanel(false);
		viewer.setShowPanelSlots(true, false, false, false);
		
		resetDisconnectedFaces();
		
		updateViews(Command.RESET_VIEW);
	}

	private void updateGeometry() {
		setVertices();
		setFaces();
	}

	/**
	 * Displays the jReality main window.
	 */
	public void start(){
		configViewer();
		viewer.startup();
	}
	
	private void setVertices() {
		if (geometricObject!=null){
			psf = new PointSetFactory();
			psf.setVertexCount(geometricObject.getVertexCount());
			psf.setVertexCoordinates(geometricObject.getCoordinates());
			// Need to convert colors to a double array, otherwise doesn't work. 
			psf.setVertexColors(toDoubleArray(geometricObject.getVertexColors()));
			psf.setVertexAttribute(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(geometricObject.getVertexLabels()));
			psf.update();
			sgcV.setGeometry(psf.getPointSet());
		}
	}
	
	private void setFaces() {
		if (geometricObject!=null){
			faceFactory = new IndexedFaceSetFactory();
			faceFactory.setVertexCount(geometricObject.getVertexCount());
			faceFactory.setVertexCoordinates(geometricObject.getCoordinates());
			int[][] faces = geometricObject.getFacesIndices();
			faceFactory.setFaceCount(faces.length);							
			faceFactory.setFaceIndices(faces);
			faceFactory.setGenerateFaceNormals(true);
			faceFactory.setGenerateEdgesFromFaces(true);

			faceFactory.update();
			sgc.setGeometry(faceFactory.getPointSet());
		}
	}

	/**
	 * Converts the color to a double array representation. 
	 * This is a 'patch' to fix a bug in jReality. This method is originally
	 * copy and pasted from de.jreality.geometry.AbstractPointSetFactory#toDoubleArray.
	 * @param color
	 * @return
	 */
	private static double [] toDoubleArray( Color [] color ) {
//		float [] c = new float[5]; Original bug from jReality
		float [] c = new float[4]; // This is the fix.
		double [] array = new double[color.length * 4 ];
		for( int i=0, j=0; i<array.length; i+=4, j++ ) {
			color[j].getComponents(c);
			array[i+0] = c[0];
			array[i+1] = c[1];
			array[i+2] = c[2];
			array[i+3] = c[3];
		}		
		return array;
	}
	
	private void configViewer(){
		viewer = new JRViewer();
		sgc = new SceneGraphComponent();
		sgcV = new SceneGraphComponent();
		sgc.addChild(sgcV);
		
		configureCustomPlugins();
		
		viewer.addBasicUI();
		
		// We enable zoom tool by default. 
		viewer.getController().getPlugin(CameraMenu.class).setZoomEnabled(true);
		viewer.registerPlugin(contentAppearance);
		viewer.registerPlugin(new ContentLoader());
		viewer.registerPlugin(interactionControlPanel);
		viewer.registerPlugin(simplicialComplexPanelPlugin);
		viewer.registerPlugin(new ContentTools());		
		//viewer.registerPlugin(new Inspector());
		viewer.registerPlugin(SCOutputConsole.getInstance());
		viewer.setShowPanelSlots(true, false, false, false);
		viewer.setContent(sgc);
	}

	/**
	 * Some custom jReality plugins need to be wrapped into ViewShrinkPanelPlugins 
	 * so they can be integrated into jReality's UI. This methods does that, besides
	 * it configures these plugins properly. 
	 * See http://www3.math.tu-berlin.de/jreality/mediawiki/index.php/Developer_Tutorial#Graphical_user_interface.
	 */
	private void configureCustomPlugins() {

		simplicialComplexPanelPlugin =  new ViewShrinkPanelPlugin(){
			
			private SimplicialComplexPanel scPanel;
			private final String  COLOR = "color";
			private final String BRACKETS = "brackets";
			private final int NUMBER_OF_PROCESSES = 5;
			private final String NC_COLOR = "nc_color"; 
			private final String P_NAME = "p_name"; 
			
			// Instance initializer (as the class is anonymous we can't specify a constructor)
			{
				// Define the position of the controls within jReality UI
				setInitialPosition(SHRINKER_LEFT);
				scPanel = SimplicialComplexPanel.getInstance();
				// Embed this panel into jReality's Shrink Panel.
				getShrinkPanel().add(scPanel);
			}
			
			
			@Override
			public PluginInfo getPluginInfo() {
				PluginInfo info = new PluginInfo();
				info.name = Constants.SIMPLICIAL_COMPLEX_PANEL;
				info.vendorName = "UNAM";
				info.icon = ImageHook.getIcon("select01.png");
				return info; 
			}
			
			@Override
			public String getHelpDocument() {
				return "";
			}
			
			@Override
			public String getHelpPath() {
				return "/de/jreality/plugin/help/";
			}
			
			@Override
			public Class<?> getHelpHandle() {
				return getClass();
			}
			/**
			 * It restores property values from configuration file at program's startup. 
			 * In this case property values are user preferences, such as vertex, edges and face's 
			 * colors, sizes, transparency, etc. 
			 */
			@Override
			public void restoreStates(Controller c) throws Exception {
				super.restoreStates(c);
				Model m = Model.getInstance();
				// Restore brackets (third parameter is default in case property value is not found in preferences file)
				m.setSelectedBrackets(
						c.getProperty(getClass(), "brackets", Constants.ProcessViewBrackets.DEFAULT.getBracketsWithFormat()));

				// Restore process colors
				List<Color> processColorsChosen = new ArrayList<Color>();
				int n = NUMBER_OF_PROCESSES;
				int i =0;
				for (; i<n; i++){
					String propName = COLOR + i;
					Color restoredColor = c.getProperty(getClass(), propName, Configuration.getInstance().DEFAULT_COLORS.get(i));
					processColorsChosen.add(restoredColor);
				}
				m.setColors(processColorsChosen);
				
				// restore non chromatic color
				Color ncColor = c.getProperty(getClass(), NC_COLOR, Color.GRAY);
				m.setNonChromaticColor(ncColor);
				
				// restore process names
				List<String> pNames = new ArrayList<String>();
				for (i=0; i<n; i++){
					String propName = P_NAME + i;
					String pName = c.getProperty(getClass(), propName, Integer.toString(i));
					pNames.add(pName);
				}
				m.setpNames(pNames);
				
				// After restoring state the panel has to be started so that it can load UI controls
				// with correct restored data.
				scPanel.start();
			}
			/**
			 * It stores (persists) property values to configuration file at program's exit. 
			 * In this case property values are user preferences, such as vertex, edges and face's 
			 * colors, sizes, transparency, etc. 
			 */
			@Override
			public void storeStates(Controller c) throws Exception {
				super.storeStates(c);
				Model m = Model.getInstance();

				// Save process view brackets chosen by user so that next time the app starts 
				// it loads this choice. 
				c.storeProperty(getClass(), BRACKETS, m.getSelectedBrackets());
				
				// Save processes colors chosen by user
				List<Color> processColorsChosen = m.getColors();
				int i =0;
				for (; i<processColorsChosen.size(); i++){
					String propName = COLOR + i;
					c.storeProperty(getClass(), propName, processColorsChosen.get(i));
				}
				
				// Save non-chromatic color chosen by user
				Color ncColor = m.getNonChromaticColor();
				c.storeProperty(getClass(), NC_COLOR, ncColor);
				
				int n = NUMBER_OF_PROCESSES;
				// Save processes names chosen by user
				List<String> pNames = m.getpNames();
				for (i=0; i<n; i++){
					String propName = P_NAME + i;
					c.storeProperty(getClass(), propName, pNames.get(i));
				}
			}
		};	
		
		contentAppearance = new ContentAppearance();
		contentAppearance.setShowPanel(false);
		interactionControlPanel = new InteractiveToolsPanel();
		interactionControlPanel.setShowPanel(false);
	}

	/**
	 * When a simplicial complex is generated by the model, this is called 
	 * to draw the new complex into the screen. 
	 */
	public void displayComplex() {
						
		// Check if the generated complex is initial
		SimplicialComplex protocolComplex = model.getProtocolComplex();
		if (protocolComplex==null){
			geometricObject = new GeometricComplex(model.getInitialComplex(), null, disconnectedFaces);
		}else
		{
			geometricObject = new GeometricComplex(protocolComplex, (GeometricComplex)geometricObject, disconnectedFaces);
		}
		
		/* For testing purposes uncomment this line in order to see that also single faces can also be
		   drawn. */
		//geometricObject = ((GeometricComplex)geometricObject).getFaces().get(0);
		updateGeometry();
				
		// Append geometric complex information to console.
		if (geometricObject instanceof GeometricComplex)
			SCOutputConsole.getInstance().setGeometricComplexInformation((GeometricComplex)geometricObject);
	
		updateViews(Command.COMPLEX_UPDATE);
		
		// Show pertinent panels
		contentAppearance.setShowPanel(true);
		interactionControlPanel.setShowPanel(true);
		// If geometric object has more than one face then offer the possibility of disconnecting faces
		if (disconnectedFaces==false)
			if (geometricObject.getFacesIndices().length>1)
				interactionControlPanel.getBtnDisconnectFaces().setVisible(true);
		
		viewer.setShowPanelSlots(true, false, false, true);
	}
	
	/**
	 * Updates the coordinates of vertices of the geometric object displayed.
	 * @param index
	 * @param x 
	 * @param y
	 * @param z
	 */
	public void updateVertexCoordinates(int index, double x, double y, double z){		
		double[][] coordinates = geometricObject.getCoordinates(); 
		// Update coordinates values
		coordinates[index][0]=x;
		coordinates[index][1]=y;
		coordinates[index][2]=z;
		
		// Update point set
		psf.setVertexCoordinates(coordinates);
		psf.update();
		
		// Update face set
		faceFactory.setVertexCoordinates(coordinates);
		faceFactory.update();
	}
	
	public void updateFacesColors(Color[] colors){
		if (colors==null){
			if (faceFactory==null) return;
			faceFactory.getIndexedFaceSet().setFaceAttributes(Attribute.COLORS, null);
		}else 
			faceFactory.setFaceColors(toDoubleArray(colors));
		psf.update();
		faceFactory.update();
	}

	/**
	 * This redraws the simplicial complex currently displayed into the screen so that
	 * it reflects the chromaticity update. Currently it is only supported for {@link GeometricComplex}
	 */
	@Override
	public void updateChromaticity() {
		// Currently it is only supported for GeometricComplex
		if (geometricObject instanceof GeometricComplex){
			GeometricComplex gc = (GeometricComplex)geometricObject;
			gc.setChromatic(model.isChromatic());
			updateGeometry(); 	
			
			updateViews(Command.CHROMATICITY_UPDATE);
			
			// Append geometric complex information to console.
			SCOutputConsole.getInstance().setGeometricComplexInformation(gc);
		}
	}

	public SceneGraphComponent getSceneGraphComponent() {
		return sgc;
	}

	public Geometry getGeometricObject() {
		return geometricObject;
	}

	/**
	 * Returns the core JRealityViewer.
	 * @return
	 */
	public JRViewer getJRealityViewer() {
		return viewer;
	}

	@Override
	public void creatingNewProtocolComplex() {
		resetDisconnectedFaces();
		updateViews(Command.NEW_PROTOCOL_COMPLEX);
	}

	public void enableDisconnectedFaces() {
		disconnectedFaces  = true;
		displayComplex();
	}
	
	private void resetDisconnectedFaces(){
		disconnectedFaces = false;
		interactionControlPanel.getBtnDisconnectFaces().setVisible(false);
	}
}
