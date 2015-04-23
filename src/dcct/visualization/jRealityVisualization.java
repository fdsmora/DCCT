package dcct.visualization;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.JRViewer.ContentType;
import de.jreality.plugin.basic.Content;
import de.jreality.plugin.basic.Inspector;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.plugin.scene.Avatar;
import de.jreality.plugin.scene.Sky;
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
import de.jtem.jrworkspace.plugin.Plugin;

public class jRealityVisualization  {
	
	protected double[][] coordinates;
	protected Map<String, Vertex> vertices;
	protected List<int[]> faces;
	protected JRViewer viewer = new JRViewer();
	protected SceneGraphComponent sgc = new SceneGraphComponent();
	protected SceneGraphComponent sgcV = new SceneGraphComponent();
	protected DragEventTool dragTool = new DragEventTool();
	protected IndexedFaceSetFactory faceFactory = new IndexedFaceSetFactory();
	protected PointSetFactory psf = new PointSetFactory();
	protected List<Plugin> plugins = new ArrayList<Plugin>();

	public jRealityVisualization(){

	}
	
	public void startVisualization(){
		sgc.removeAllChildren();
		configVertices();
		configFaces();
		setAppearance();
		viewer.startup();
	}

	protected void configVertices() {
		if (vertices!=null){			
			psf.setVertexCount(vertices.size());
			psf.setVertexCoordinates(getVertexCoordinates());
			psf.setVertexColors(toDoubleArray(getVertexColors()));
			setVertexLabels(psf);
			psf.update();

			sgcV.setGeometry(psf.getPointSet());			
			sgc.addChild(sgcV);
		}
	}
	
	protected void configFaces() {		
		if (faces!=null) {
			
			int f = faces.size();
			
//			Color[] faceColors = new Color[f];
//			for (int i=0 ; i<f; i++){
//				faceColors[i]=Color.ORANGE;
//			}
//			faceFactory.setFaceColors(toDoubleArray(faceColors));
			
			faceFactory.setVertexCount(vertices.size());
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
		int[][] faceIndices = new int[faces.size()][];
		int i =0;
		for (int[] l : faces){
			faceIndices[i++]=l;
		}
		return faceIndices;
	}

	protected double[][] getVertexCoordinates() {
	
		//if (coordinates ==null){
			coordinates = new double[vertices.size()][3];
			int i=0;
			for (Vertex v:vertices.values())
				coordinates[i++]=v.getCoordinates();
		//}
		return coordinates;
	}
	
	protected Color[] getVertexColors() {
		Color[] colors = new Color[vertices.size()];
		int i=0;
		for (Vertex v:vertices.values())
			colors[i++]=v.getColor();
		return colors;
	}
	
	private void setVertexLabels(PointSetFactory psf){
		String[] labels = new String[vertices.size()];
		int i=0;
		for (Vertex v : vertices.values())
			labels[i++]=v.getLabel();
		
		psf.setVertexAttribute(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(labels));
	}
	
	static double [] toDoubleArray( Color [] color ) {
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
	
	public void configViewer(){
		sgc.addTool(dragTool);
		
		dragTool.addPointDragListener(new PointDragListener() {
			public void pointDragEnd(PointDragEvent e) {
			}
			public void pointDragStart(PointDragEvent e) {
			}
			public void pointDragged(PointDragEvent e) {
				jRealityVisualization.this.pointDragged(e);
			}
		});
		
		viewer.setShowPanelSlots(true,true,true,true);
		viewer.addBasicUI();
		for (Plugin p : plugins){
			viewer.registerPlugin(p);
		}
//		viewer.registerPlugin(new SCPanelPlugin(sgc,sc));
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
	
	public void addPlugin(Plugin plugin){
		plugins.add(plugin);
	}
	
	public SceneGraphComponent getSgc() {
		return sgc;
	}

	public Map<String, Vertex> getVertices() {
		return vertices;
	}

	public void setVertices(Map<String, Vertex> vertices) {
		this.vertices = vertices;
	}

	public List<int[]> getFaces() {
		return faces;
	}

	public void setFaces(List<int[]> faces) {
		this.faces = faces;
	}

}
