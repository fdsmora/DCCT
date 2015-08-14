package unam.dcct.view;

import java.awt.Color;
//import java.awt.Font;
//import javax.swing.SwingConstants;
import unam.dcct.misc.Constants;
import unam.dcct.model.Model;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.UI.SimplicialComplexPanel;
import unam.dcct.view.geometry.GeometricComplex;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.plugin.icon.ImageHook;
import de.jreality.plugin.menu.CameraMenu;
//import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;
import de.jreality.scene.tool.InputSlot;
//import de.jreality.shader.DefaultGeometryShader;
//import de.jreality.shader.DefaultLineShader;
//import de.jreality.shader.DefaultPointShader;
//import de.jreality.shader.DefaultPolygonShader;
//import de.jreality.shader.DefaultTextShader;
//import de.jreality.shader.ShaderUtility;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.DraggingTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jtem.jrworkspace.plugin.PluginInfo;
/***
 * This class contains all the methods and logic that renders simplicial complexes 
 * into the screen using the jReality API. 
 * @author Fausto
 *
 */
public class jRealityView implements View {

	private Model model;
	private GeometricComplex gComplex;
	private JRViewer viewer;
	private SceneGraphComponent sgc;
	private SceneGraphComponent sgcV;
	private IndexedFaceSetFactory faceFactory;
	private PointSetFactory psf;
	private static jRealityView instance = null;
	
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
	}

	public void updateView() {
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
		if (gComplex!=null){
			psf = new PointSetFactory();
			psf.setVertexCount(gComplex.getVertexCount());
			psf.setVertexCoordinates(gComplex.getCoordinates());
			// Need to convert colors to a double array, otherwise doesn't work. 
			psf.setVertexColors(toDoubleArray(gComplex.getVertexColors()));
			psf.setVertexAttribute(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(gComplex.getVertexLabels()));
			psf.update();
			sgcV.setGeometry(psf.getPointSet());
		}
	}
	
	private void setFaces() {
		if (gComplex!=null){
			faceFactory = new IndexedFaceSetFactory();
			faceFactory.setVertexCount(gComplex.getVertexCount());
			faceFactory.setVertexCoordinates(gComplex.getCoordinates());
			int[][] faces = gComplex.getFacesIndices();
			faceFactory.setFaceCount(faces.length);
			
//			Color[] faceColors = new Color[faces.length];
//			for (int i = 0; i<faces.length; i++){
//				faceColors[i]=Constants.DEFAULT_FACE_COLOR;
//			}
						
//			faceFactory.setFaceColors(toDoubleArray(faceColors));
			
			faceFactory.setFaceIndices(faces);
			faceFactory.setGenerateFaceNormals(true);
			faceFactory.setGenerateEdgesFromFaces(true);

			faceFactory.update();
			sgc.setGeometry(faceFactory.getPointSet());
		}
	}
	
	/**
	 * Handles the event of dragging a vertex with the mouse, updating the vertex coordinates and redrawing the vertex with the new updated coordinates. 
	 * @param e
	 */
	public void pointDragged(PointDragEvent e) {
		double[][] coordinates = gComplex.getCoordinates(); 
		coordinates[e.getIndex()][0]=e.getX();
		coordinates[e.getIndex()][1]=e.getY();
		coordinates[e.getIndex()][2]=e.getZ();

		psf.setVertexCoordinates(coordinates);
		psf.update();
		
		faceFactory.setVertexCoordinates(coordinates);
		faceFactory.update();
	}

	private static double [] toDoubleArray( Color [] color ) {
		float [] c = new float[5];
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
		/*		 Create DraggingTool to let user drag the geometric object in the visualization space.
		 Needed to tweak it a bit in order to enable it back, as this feature was removed 
		 in the latest versions of jReality. 
		 "PrimarySelection" is to activate dragging by pressing mouse's right button. 
		 "DragActivation" is the original behaviour, which activates it with middle button (mouse's wheel)
		 but not every mouse has a middle button (e.g. Mac) */
		DraggingTool dragObjectTool = new DraggingTool(InputSlot.getDevice("PrimarySelection"));
		dragObjectTool.addCurrentSlot(InputSlot.getDevice("DragAlongViewDirection"));
		dragObjectTool.addCurrentSlot(InputSlot.getDevice("PointerEvolution"));		
		sgc.addTool(dragObjectTool);
		
		DragEventTool dragVertexTool = new DragEventTool();
		dragVertexTool.addPointDragListener(new PointDragListener() {
			public void pointDragEnd(PointDragEvent e) {
			}
			public void pointDragStart(PointDragEvent e) {
			}
			public void pointDragged(PointDragEvent e) {
				jRealityView.this.pointDragged(e);
			}
		});
		sgc.addTool(dragVertexTool);
				
		ViewShrinkPanelPlugin simplicialComplexPanelPlugin =  new ViewShrinkPanelPlugin(){
			
			// Instance initializer
			{
				// Define the position of the controls within jReality UI
				setInitialPosition(SHRINKER_LEFT);
				SimplicialComplexPanel scPanel = SimplicialComplexPanel.getInstance();
				// Embed this panel into jReality's Shrink Panel.
				getShrinkPanel().add(scPanel);
				scPanel.start();
			}
			
			
			@Override
			public PluginInfo getPluginInfo() {
				PluginInfo info = new PluginInfo();
				info.name = Constants.SIMPLICIAL_COMPLEX_PANEL;
				info.vendorName = "Fausto Salazar";
				info.icon = ImageHook.getIcon("select01.png");
				return info; 
			}
			
			@Override
			public String getHelpDocument() {
				return "";
			}
			
			@Override
			public String getHelpPath() {
				//return "C:\\Users\\Fausto\\Documents\\MCC UNAM\\tesis\\impl\\DCCT\\help\\";
				return "/de/jreality/plugin/help/";
			}
			
			@Override
			public Class<?> getHelpHandle() {
				return getClass();
			}
			
		};
		
		viewer.addBasicUI();
		
		// We enable zoom tool by default. 
		viewer.getController().getPlugin(CameraMenu.class).setZoomEnabled(true);
		viewer.registerPlugin(new ContentAppearance());
		viewer.registerPlugin(new ContentLoader());
		viewer.registerPlugin(new ContentTools());
		viewer.registerPlugin(simplicialComplexPanelPlugin);
		//viewer.registerPlugin(new Inspector());
		viewer.registerPlugin(SCOutputConsole.getInstance());
		viewer.setShowPanelSlots(true, false, false, true);
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
	 * When a simplicial complex is generated by the model, this is called 
	 * to draw the new complex into the screen. 
	 */
	public void displayComplex() {
		Model m = Model.getInstance();
		// Check if the generated complex is initial
		SimplicialComplex protocolComplex = m.getProtocolComplex();
		if (protocolComplex==null){
			gComplex = new GeometricComplex(m.getInitialComplex());
		}else
		{
			gComplex = new GeometricComplex(protocolComplex);
		}
		updateView();
		SCOutputConsole.getInstance().addGeometricInformation(gComplex);
	}

	/**
	 * This redraws the simplicial complex currently displayed into the screen so that
	 * it reflects the chromaticity update. 
	 */
	@Override
	public void updateChromaticity() {
		gComplex.setChromatic(model.isChromatic());
		updateView();
	}	

}
