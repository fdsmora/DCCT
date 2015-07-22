package view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingConstants;
import configuration.Constants;
import model.Model;
import view.UI.SimplicialComplexPanel;
import dctopology.SimplicialComplex;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.plugin.menu.CameraMenu;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;
import de.jreality.scene.tool.InputSlot;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.DraggingTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jtem.jrworkspace.plugin.PluginInfo;

public class jRealityView implements View {

	private Model model;
	private SimplicialComplex complex;
	private Geometry initialComplexGeometry;
	private Geometry protocolComplexGeometry;
	private JRViewer viewer = new JRViewer();
	private SceneGraphComponent sgc = new SceneGraphComponent();
	private SceneGraphComponent sgcV = new SceneGraphComponent();
	private IndexedFaceSetFactory faceFactory = new IndexedFaceSetFactory();
	private PointSetFactory psf = new PointSetFactory();
	private SCOutputConsole console = new SCOutputConsole();
	
	protected double[][] coordinates;
	
	private static jRealityView instance = null;
	public static jRealityView getInstance(){
		if (instance == null)
			instance = new jRealityView();
		return instance;
	}
	
	private jRealityView(){
		model = Model.getInstance();
		model.registerView(this);
	}
	
	public void update(Command action) {
		action.execute();
//		if (action.equals("r")){
//			resetView();
//			resetGeometry();
//			console.resetConsole();
//			return;
//		}
//		
//		if (action.equals("u")){
//			console.resetProtocolComplexInfo();
//			return;
//		}
//		
//		if(action.equals("i")){
//			complex = model.getInitialComplex() ;
//			initialComplexGeometry = new Geometry (complex, 
//					null, model.getSimplicialComplexColors());
//		}
//		else if (action.equals("p") || 
//					action.equals("c")){
//			complex = model.getProtocolComplex();
//			protocolComplexGeometry = new Geometry(complex, 
//					protocolComplexGeometry!=null? protocolComplexGeometry : initialComplexGeometry, 
//					model.getSimplicialComplexColors());
//			//Test
//			//protocolComplexGeometry.test();
//		}
//		
////		g = new Geometry(complex, 
////				model.isChromatic()? model.getSimplicialComplexColors() : null);
//
//		if (action.equals("i")){
//			console.setInitialComplexInfo(complex.toString());
//		}
//		else if (action.equals("p")) {
//			CommunicationMechanism cm = model.getCommunicationMechanism();
//			console.setCommunicationModel(cm.toString());
//			
//			console.addProtocolComplexInfo(complex.toString(),
//					complex.getSimplices().size(),
//					model.isChromatic()? 0 : protocolComplexGeometry.getFaces().size());
//		}else if (action.equals("c")){
//			console.addNonChromaticInfo(protocolComplexGeometry.getFaces().size());
//		}
//							
//		console.print();
//		
//		updateView();
	}
	
	public void reset(){
		initialComplexGeometry = protocolComplexGeometry = null;
		sgcV.setGeometry(null);
		sgcV.removeAllChildren();
		sgc.setGeometry(null);
		sgc.removeAllChildren();
		sgc.addChild(sgcV);
	}

	public void updateView() {
		configVertices();
		configFaces();
	}

	public void start(){
		//sgc.removeAllChildren();
		//updateView();
		//setAppearance();
		configViewer();
		viewer.startup();
	}
	
	protected void configVertices() {
		Geometry g = getGeometry();
		if (g!=null){			
			psf.setVertexCount(g.getVertices().size());
			psf.setVertexCoordinates(getVertexCoordinates());
			psf.setVertexColors(toDoubleArray(getVertexColors()));
			setVertexLabels(psf);
			psf.update();
			sgcV.setGeometry(psf.getPointSet());			
		}
	}
	
	protected void configFaces() {
		Geometry g = getGeometry();
		if (g!=null) {
			int f = g.getFaces().size();
			
//			Color[] faceColors = new Color[f];
//			for (int i=0 ; i<f; i++){
//				faceColors[i]=Color.ORANGE;
//			}
//			faceFactory.setFaceColors(toDoubleArray(faceColors));
			
			faceFactory.setVertexCount(g.getVertices().size());
			faceFactory.setVertexCoordinates(getVertexCoordinates());
			faceFactory.setFaceCount(f);
			faceFactory.setFaceIndices(getFaceIndices());
			faceFactory.setGenerateFaceNormals(true);
			faceFactory.setGenerateEdgesFromFaces(true);
			faceFactory.update();
			
			sgc.setGeometry(faceFactory.getPointSet());
		}
	}
	
	public void pointDragged(PointDragEvent e) {
		coordinates[e.getIndex()][0]=e.getX();
		coordinates[e.getIndex()][1]=e.getY();
		coordinates[e.getIndex()][2]=e.getZ();
		//psf.setVertexCoordinates(getVertexCoordinates());
		psf.setVertexCoordinates(coordinates);
		psf.update();
		faceFactory.setVertexCoordinates(coordinates);
		faceFactory.update();
	}
	
	private Geometry getGeometry(){
		if (protocolComplexGeometry == null)
			return initialComplexGeometry;
		else return protocolComplexGeometry;
	}
	
	
//	protected int[][] getFaceIndices(){
//		int[][] faceIndices = new int[g.getFaces().size()][];
//		int i =0;
//		for (int[] l : g.getFaces()){
//			faceIndices[i++]=l;
//		}
//		return faceIndices;
//	}
	protected int[][] getFaceIndices(){
		Geometry g = getGeometry();
		int[][] faceIndices = new int[g.getFaces().size()][];
		int i =0;
		for (Face f : g.getFaces().values()){
			faceIndices[i++]=f.getFaceArray();
		}
		return faceIndices;
	}

	protected double[][] getVertexCoordinates() {
		Geometry g = getGeometry();
		//if (coordinates ==null){
			coordinates = new double[g.getVertices().size()][3];
			int i=0;
			for (Vertex v:g.getVertices().values())
				coordinates[i++]=v.getCoordinates();
		//}
		return coordinates;
	}
	
	protected Color[] getVertexColors() {
		Geometry g = getGeometry();
		Color[] colors = new Color[g.getVertices().size()];
		int i=0;
		for (Vertex v:g.getVertices().values())
			colors[i++]=v.getColor();
		return colors;
	}
	
	protected void setVertexLabels(PointSetFactory psf){
		Geometry g = getGeometry();
		String[] labels = new String[g.getVertices().size()];
		int i=0;
		for (Vertex v : g.getVertices().values())
			labels[i++]=v.getLabel();
		
		psf.setVertexAttribute(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(labels));
	}
	
	protected static double [] toDoubleArray( Color [] color ) {
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
	
	protected void configViewer(){
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
			}
			
			public PluginInfo getPluginInfo() {
				return new PluginInfo(Constants.SIMPLICIAL_COMPLEX_PANEL);
			}
			
		};
		
		viewer.addBasicUI();
		
		// We enable zoom tool by default. 
		viewer.getController().getPlugin(CameraMenu.class).setZoomEnabled(true);
		viewer.registerPlugin(new ContentAppearance());
		viewer.registerPlugin(new ContentLoader());
		viewer.registerPlugin(new ContentTools());
		viewer.registerPlugin(simplicialComplexPanelPlugin);
		//viewer.registerPlugin(new SCOutputConsole());
		viewer.setShowPanelSlots(true, false, false, true);
		viewer.setContent(sgc);
	}
	
	protected void setAppearance(){
		sgc.setAppearance(new Appearance());
		DefaultGeometryShader dgs = ShaderUtility.createDefaultGeometryShader(sgc.getAppearance(), false);
		 
		DefaultLineShader dls = (DefaultLineShader) dgs.createLineShader("default");
		//dls.setDiffuseColor(de.jreality.shader.Color.YELLOW);
		//dls.setTubeRadius(0.05);
		
		DefaultPolygonShader dps = (DefaultPolygonShader) dgs.createPolygonShader("default");
		//dps.setDiffuseColor(de.jreality.shader.Color.GREEN);
		
		DefaultPointShader ps = (DefaultPointShader) dgs.getPointShader();
		//ps.setPointRadius(0.2);
		//ps.setDiffuseColor(de.jreality.shader.Color.red);
		dgs.setShowPoints(true);
		// Labels
	    DefaultTextShader pts = (DefaultTextShader) ps.getTextShader();
	    pts.setDiffuseColor(de.jreality.shader.Color.BLACK);
	    // scale the label
	    Double scale = new Double(0.01);
	    pts.setScale(1.5*scale);
	    // apply a translation to the position of the label in camera coordinates (-z away from camera)
	    double[] offset = new double[]{-.3,0,0.3};
	    pts.setOffset(offset);
	    // the alignment specifies a direction in which the label will be shifted in the 2d-plane of the billboard
	    pts.setAlignment(SwingConstants.TRAILING);
	    // here you can specify any available Java font
	    Font f = new Font("Arial Bold", Font.PLAIN, 16);
	    pts.setFont(f);
	}

	public void createInitialComplexGeometry(SimplicialComplex complex) {
		this.initialComplexGeometry = new Geometry (complex, 
				null, Model.getInstance().getSimplicialComplexColors());
		updateView();
	}
	
	public void createProtocolComplexGeometry(SimplicialComplex complex) {
		protocolComplexGeometry =new Geometry(complex, 
				protocolComplexGeometry!=null? protocolComplexGeometry : initialComplexGeometry, 
						Model.getInstance().getSimplicialComplexColors());
		updateView();		
	}
}
