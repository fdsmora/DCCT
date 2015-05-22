package view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingConstants;

import model.Model;
import controller.Controller;
import view.SCGeneratePanelPlugin;
import dctopology.SimplicialComplex;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;

public class jRealityView implements View {

	protected Model model;
	protected Controller controller;
	protected SimplicialComplex complex;
	protected Geometry g;
	
	protected JRViewer viewer = new JRViewer();
	protected SceneGraphComponent sgc = new SceneGraphComponent();
	protected SceneGraphComponent sgcV = new SceneGraphComponent();
	protected DragEventTool dragTool = new DragEventTool();
	protected IndexedFaceSetFactory faceFactory = new IndexedFaceSetFactory();
	protected PointSetFactory psf = new PointSetFactory();
	
	protected double[][] coordinates;
	
	public jRealityView(Model m, Controller c){
		model = m;
		model.registerView(this);
		controller = c;
	}
	
	public void update(String action) {
		complex = action=="i"? model.getInitialComplex() : model.getProtocolComplex();
		g = new Geometry(complex);
		updateView();
	}

	private void updateView() {
		configVertices();
		configFaces();
	}

	public void start(){
		sgc.removeAllChildren();
		updateView();
		setAppearance();
		configViewer();
		viewer.startup();
	}
	
	protected void configVertices() {
		if (g!=null){			
			psf.setVertexCount(g.getVertices().size());
			psf.setVertexCoordinates(getVertexCoordinates());
			psf.setVertexColors(toDoubleArray(getVertexColors()));
			setVertexLabels(psf);
			psf.update();

			sgcV.setGeometry(psf.getPointSet());			
			sgc.addChild(sgcV);
		}
	}
	
	protected void configFaces() {		
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
	
	protected int[][] getFaceIndices(){
		int[][] faceIndices = new int[g.getFaces().size()][];
		int i =0;
		for (int[] l : g.getFaces()){
			faceIndices[i++]=l;
		}
		return faceIndices;
	}

	protected double[][] getVertexCoordinates() {
	
		//if (coordinates ==null){
			coordinates = new double[g.getVertices().size()][3];
			int i=0;
			for (Geometry.Vertex v:g.getVertices().values())
				coordinates[i++]=v.getCoordinates();
		//}
		return coordinates;
	}
	
	protected Color[] getVertexColors() {
		Color[] colors = new Color[g.getVertices().size()];
		int i=0;
		for (Geometry.Vertex v:g.getVertices().values())
			colors[i++]=v.getColor();
		return colors;
	}
	
	protected void setVertexLabels(PointSetFactory psf){
		String[] labels = new String[g.getVertices().size()];
		int i=0;
		for (Geometry.Vertex v : g.getVertices().values())
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
		sgc.addTool(dragTool);
		
		dragTool.addPointDragListener(new PointDragListener() {
			public void pointDragEnd(PointDragEvent e) {
			}
			public void pointDragStart(PointDragEvent e) {
			}
			public void pointDragged(PointDragEvent e) {
				jRealityView.this.pointDragged(e);
			}
		});
		
		viewer.setShowPanelSlots(true,true,true,true);
		viewer.addBasicUI();
		viewer.registerPlugin(new ContentLoader());
		viewer.registerPlugin(new ContentTools());
		viewer.registerPlugin(new SCGeneratePanelPlugin(model));
		viewer.setShowPanelSlots(true, true, false, false);
		viewer.setContent(sgc);
		viewer.addContentUI();

	}
	
	protected void setAppearance(){
		sgc.setAppearance(new Appearance());
		DefaultGeometryShader dps = ShaderUtility.createDefaultGeometryShader(sgc.getAppearance(), false);
		
		DefaultPointShader ps = (DefaultPointShader) dps.getPointShader();
		ps.setPointRadius(0.2);
		//ps.setDiffuseColor(de.jreality.shader.Color.red);
		dps.setShowPoints(true);
		// Labels
	    DefaultTextShader pts = (DefaultTextShader) ps.getTextShader();
	    pts.setDiffuseColor(de.jreality.shader.Color.blue);
	    // scale the label
	    Double scale = new Double(0.01);
	    pts.setScale(1.5*scale);
	    // apply a translation to the position of the label in camera coordinates (-z away from camera)
	    double[] offset = new double[]{-.3,0,0.3};
	    pts.setOffset(offset);
	    // the alignment specifies a direction in which the label will be shifted in the 2d-plane of the billboard
	    pts.setAlignment(SwingConstants.TRAILING);
	    // here you can specify any available Java font
	    Font f = new Font("Arial Bold", Font.ITALIC, 20);
	    pts.setFont(f);
	}
	

}