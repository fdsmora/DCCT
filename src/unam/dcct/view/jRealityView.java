package unam.dcct.view;

import java.util.ArrayList;
import java.util.List;
import unam.dcct.misc.Configuration;
import unam.dcct.misc.Constants;
import unam.dcct.model.Model;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.UI.InteractionControlPanel;
import unam.dcct.view.UI.SimplicialComplexPanel;
import unam.dcct.view.geometry.GeometricComplex;
import unam.dcct.view.geometry.Geometry;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.plugin.icon.ImageHook;
import de.jreality.plugin.menu.CameraMenu;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DoubleArrayArray;
import de.jreality.scene.data.StorageModel;
import de.jreality.scene.tool.InputSlot;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
//import de.jreality.scene.Appearance;
//import de.jreality.shader.DefaultGeometryShader;
//import de.jreality.shader.DefaultLineShader;
//import de.jreality.shader.DefaultPointShader;
//import de.jreality.shader.DefaultPolygonShader;
//import de.jreality.shader.DefaultTextShader;
//import de.jreality.shader.ShaderUtility;
//import java.awt.Font;
//import javax.swing.SwingConstants;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.DraggingTool;
import de.jreality.tools.FaceDragEvent;
import de.jreality.tools.FaceDragListener;
import de.jreality.tools.LineDragEvent;
import de.jreality.tools.LineDragListener;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
/***
 * This class contains methods and logic that produces geometric visualizations of
 * simplicial complexes and simplices using jReality library. 
 * @author Fausto Salazar
 * @see SimplicialComplexPanel
 * @see unam.dcct.view.geometry.Geometry
 * @see unam.dcct.view.geometry.Face
 * @see unam.dcct.view.geometry.GeometricComplex
 */
public class jRealityView implements View {

	private Model model;
	private Geometry geometricObject;
	private JRViewer viewer;
	private SceneGraphComponent sgc;
	private SceneGraphComponent sgcV;
	private IndexedFaceSetFactory faceFactory;
	private PointSetFactory psf;
	private static jRealityView instance = null;

	private ViewShrinkPanelPlugin simplicialComplexPanelPlugin;
	private InteractionControlPanel interactionControlPanel;
	
	private ContentAppearance contentAppearance;
	private boolean vertexDragEnabled = true;
	private boolean edgeDragEnabled = true;
	private boolean faceDragEnabled = true;

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
	}

	private void updateView() {
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
//			psf.setVertexColors(geometricObject.getVertexColors());
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
			
//			Color[] faceColors = new Color[faces.length];
//			for (int i = 0; i<faces.length; i++){
//				if (i%3==0)
//					faceColors[i]=Color.magenta;
//				else if (i%3==1)
//					faceColors[i]=Color.green;
//				else
//					faceColors[i]=Color.red;
//			}				
//			faceFactory.setFaceColors(toDoubleArray(faceColors));
//			faceFactory.setFaceColors(faceColors);
							
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
		
		addInteractiveTools(sgc);		
		configureCustomPlugins();
		
		viewer.addBasicUI();
		
		// We enable zoom tool by default. 
		viewer.getController().getPlugin(CameraMenu.class).setZoomEnabled(true);
		contentAppearance = new ContentAppearance();
		contentAppearance.setShowPanel(false);
		viewer.registerPlugin(contentAppearance);
		viewer.registerPlugin(new ContentLoader());
		viewer.registerPlugin(new ContentTools());
		viewer.registerPlugin(interactionControlPanel);
		viewer.registerPlugin(simplicialComplexPanelPlugin);
		interactionControlPanel.setShowPanel(false);
		//viewer.registerPlugin(new Inspector());
		viewer.registerPlugin(SCOutputConsole.getInstance());
		viewer.setShowPanelSlots(true, false, false, false);
		viewer.setContent(sgc);
	}
	
	/* Sets default values for properties such as vertex colors, labels size and fonts, etc. 
	 * Currently it is not used because it deactivates the functionality of the Content Appearance panel
	 * that lets users customize these properties in runtime. Maybe it is a jReality issue. We
	 * don't delete it as a solution may be found in the future .*/
//	private void setDefaultAppearance(){
//		sgc.setAppearance(new Appearance());
//		DefaultGeometryShader dgs = ShaderUtility.createDefaultGeometryShader(sgc.getAppearance(), false);
//		 
//		DefaultLineShader dls = (DefaultLineShader) dgs.createLineShader("default");
//		//dls.setDiffuseColor(de.jreality.shader.Color.YELLOW);
//		//dls.setTubeRadius(0.05);
//		
//		DefaultPolygonShader dps = (DefaultPolygonShader) dgs.createPolygonShader("default");
//		//dps.setDiffuseColor(de.jreality.shader.Color.GREEN);
//		
//		DefaultPointShader ps = (DefaultPointShader) dgs.getPointShader();
//		//ps.setPointRadius(0.2);
//		//ps.setDiffuseColor(de.jreality.shader.Color.red);
//		dgs.setShowPoints(true);
//		// Labels
//	    DefaultTextShader pts = (DefaultTextShader) ps.getTextShader();
//	    pts.setDiffuseColor(de.jreality.shader.Color.BLACK);
//	    // scale the label
//	    Double scale = new Double(0.01);
//	    pts.setScale(1.5*scale);
//	    // apply a translation to the position of the label in camera coordinates (-z away from camera)
//	    double[] offset = new double[]{-.3,0,0.3};
//	    pts.setOffset(offset);
//	    // the alignment specifies a direction in which the label will be shifted in the 2d-plane of the billboard
//	    pts.setAlignment(SwingConstants.TRAILING);
//	    // here you can specify any available Java font
//	    Font f = new Font("Arial Bold", Font.PLAIN, 16);
//	    pts.setFont(f);
//	}

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
		
		interactionControlPanel = new InteractionControlPanel();
	}

	/**
	 * Adds tools that let user interact with the visualizations. For more info about
	 * jReality's tools framework see 
	 * http://www3.math.tu-berlin.de/jreality/mediawiki/index.php/Developer_Tutorial#Tools,
	 * in particular check the DragEventTool01 and DragEventTool02 classes in the jReality tools tutorial (de.jreality.tutorial.tool) 
	 * @param sgc The SceneGraphComponent to which the tools will be attached so that they can interact with the visualizations it represents.
	 */
	private void addInteractiveTools(final SceneGraphComponent sgc) {
		/*		 
		 * Create DraggingTool to let user drag the whole geometric object around the visualization space.
		 Needed to tweak it a bit in order to enable it back, as this feature was removed 
		 in the latest versions of jReality (I brought it back because I consider it useful for this program). 
		 "PrimarySelection" is to activate dragging by pressing mouse's right button. 
		 "DragActivation" is the original behavior, which activates it with middle button (mouse's wheel)
		 but not every mouse has a middle button (e.g. Mac) */
		DraggingTool dragWholeObjectTool = new DraggingTool(InputSlot.getDevice("PrimarySelection"));
		dragWholeObjectTool.addCurrentSlot(InputSlot.getDevice("DragAlongViewDirection"));
		dragWholeObjectTool.addCurrentSlot(InputSlot.getDevice("PointerEvolution"));		
		sgc.addTool(dragWholeObjectTool);
		
		// Now this tool is for letting user drag individual vertices, edges and faces. 
		DragEventTool dragGeometryTool = new DragEventTool();
		// Add drag vertices capability
		dragGeometryTool.addPointDragListener(new PointDragListener() {
			public void pointDragEnd(PointDragEvent e) {
			}
			public void pointDragStart(PointDragEvent e) {
			}
			public void pointDragged(PointDragEvent e) {
				if (vertexDragEnabled)
					jRealityView.this.pointDragged(e);
			}
		});
		// Add drag edges capability
		dragGeometryTool.addLineDragListener(new LineDragListener() {
			
			private IndexedLineSet lineSet;
			private double[][] points;
			
			public void lineDragStart(LineDragEvent e) {
				if (edgeDragEnabled){
					lineSet = e.getIndexedLineSet();
					points=new double[lineSet.getNumPoints()][];
					lineSet.getVertexAttributes(Attribute.COORDINATES).toDoubleArrayArray(points);
				}
			}

			public void lineDragged(LineDragEvent e) {
				if (edgeDragEnabled){
					double[][] newPoints=(double[][])points.clone();
					Matrix trafo=new Matrix();
					MatrixBuilder.euclidean().translate(e.getTranslation()).assignTo(trafo);
					int[] lineIndices=e.getLineIndices();
					for(int i=0;i<lineIndices.length;i++){
						newPoints[lineIndices[i]]=trafo.multiplyVector(points[lineIndices[i]]);
						updateVertexCoordinates(lineIndices[i], newPoints[lineIndices[i]][0], newPoints[lineIndices[i]][1], newPoints[lineIndices[i]][2]);
					}
					// I think this is not necessary, but I leave it (commented) in case. 
					// lineSet.setVertexAttributes(Attribute.COORDINATES,StorageModel.DOUBLE_ARRAY.array(3).createReadOnly(newPoints));	
				}
			}

			public void lineDragEnd(LineDragEvent e) {
			}			
		});
		// Add drag faces capability
		dragGeometryTool.addFaceDragListener(new FaceDragListener() {
			
			private IndexedFaceSet faceSet;
			private double[][] points;
						
			public void faceDragStart(FaceDragEvent e) {
				if (faceDragEnabled){
					faceSet = e.getIndexedFaceSet();
					points=new double[faceSet.getNumPoints()][];
					points = faceSet.getVertexAttributes(Attribute.COORDINATES).toDoubleArrayArray(null);
				}
			}

			public void faceDragged(FaceDragEvent e) {
				if (faceDragEnabled){
					double[][] newPoints=(double[][])points.clone();
					Matrix trafo=new Matrix();
					MatrixBuilder.euclidean().translate(e.getTranslation()).assignTo(trafo);
					int[] faceIndices=e.getFaceIndices();
					for(int i=0;i<faceIndices.length;i++){
						newPoints[faceIndices[i]]=trafo.multiplyVector(points[faceIndices[i]]);
						updateVertexCoordinates(faceIndices[i], newPoints[faceIndices[i]][0], newPoints[faceIndices[i]][1], newPoints[faceIndices[i]][2]);
					}
					// I think this is not necessary, but I leave it (commented) in case. 
//					faceSet.setVertexAttributes(Attribute.COORDINATES,StorageModel.DOUBLE_ARRAY.array(3).createReadOnly(newPoints));	
					
					// Test code for developing 'faces click and color' feature. 
					//					faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE_ARRAY.array()..createReadOnly(toDoubleArray(new Color[]{Color.red})));
//					faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE_ARRAY.array().toDoubleArrayArray(new float[][]{Color.red.getColorComponents(null)}));
					
//					faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE3_INLINED.createReadOnly((toDoubleArray(new Color[]{Color.blue}))));
//					viewer.getViewer().getSceneRoot().getChildComponent(1).getAppearance().getAttribute(CommonAttributes.POLYGON_SHADER+"."+CommonAttributes.DIFFUSE_COLOR);
//					faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE3_INLINED.createWritableDataList(toDoubleArray(new Color[]{Color.blue, Color.gray, Color.cyan})));
//					faceSet.getFaceAttributes(Attribute.COLORS);
//					faceSet.setFaceAttributes(Attribute.COLORS, new DoubleArrayArray.Inlined( toDoubleArray(new Color[]{Color.blue, Color.gray, Color.cyan}), 1 ));
				}
			}

			public void faceDragEnd(FaceDragEvent e) {
			}			
		});
		
		sgc.addTool(dragGeometryTool);
	}
	
	/**
	 * Handles the event of dragging a vertex with the mouse, updating the vertex coordinates and redrawing the vertex with the new updated coordinates. 
	 * @param e The Event object that contains all the information about the dragging event such as the new vertex coordinates after the drag. 
	 */
	private void pointDragged(PointDragEvent e) {
		updateVertexCoordinates(e.getIndex(), e.getX(), e.getY(), e.getZ());
	}
	
	private void updateVertexCoordinates(int index, double x, double y, double z){
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

	/**
	 * When a simplicial complex is generated by the model, this is called 
	 * to draw the new complex into the screen. 
	 */
	public void displayComplex() {
		// Check if the generated complex is initial
		SimplicialComplex protocolComplex = model.getProtocolComplex();
		if (protocolComplex==null){
			geometricObject = new GeometricComplex(model.getInitialComplex(), null);
		}else
		{
			geometricObject = new GeometricComplex(protocolComplex, (GeometricComplex)geometricObject);
		}
		
		/* For testing purposes uncomment this line in order to see that also single faces can also be
		   drawn. */
		//geometricObject = ((GeometricComplex)geometricObject).getFaces().get(0);
		updateView();
		
		// Append geometric complex information to console.
		if (geometricObject instanceof GeometricComplex)
			SCOutputConsole.getInstance().setGeometricComplexInformation((GeometricComplex)geometricObject);
	
		// Show pertinent panels
		contentAppearance.setShowPanel(true);
		interactionControlPanel.setShowPanel(true);
		viewer.setShowPanelSlots(true, false, false, true);
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
			updateView(); 	
			
			// Append geometric complex information to console.
			SCOutputConsole.getInstance().setGeometricComplexInformation(gc);
		}
	}

	public boolean isVertexDragEnabled() {
		return vertexDragEnabled;
	}

	public void setVertexDragEnabled(boolean vertexDragEnabled) {
		this.vertexDragEnabled = vertexDragEnabled;
	}

	public boolean isEdgeDragEnabled() {
		return edgeDragEnabled;
	}

	public void setEdgeDragEnabled(boolean edgeDragEnabled) {
		this.edgeDragEnabled = edgeDragEnabled;
	}

	public boolean isFaceDragEnabled() {
		return faceDragEnabled;
	}

	public void setFaceDragEnabled(boolean faceDragEnabled) {
		this.faceDragEnabled = faceDragEnabled;
	}

	public SceneGraphComponent getSceneGraphComponent() {
		return sgc;
	}

}
