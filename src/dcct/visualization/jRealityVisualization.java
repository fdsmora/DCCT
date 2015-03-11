package dcct.visualization;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import de.jreality.plugin.scene.Terrain;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DataList;
import de.jreality.scene.data.StorageModel;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.ShaderUtility;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.sidecontainer.template.ShrinkPanelPlugin;

public class jRealityVisualization  {
	
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
	
	public void addPlugin(Plugin plugin){
		plugins.add(plugin);
	}

	protected Map<String, Vertex> vertices;
	protected List<int[]> faces;
	protected JRViewer viewer = new JRViewer();
	public SceneGraphComponent getSgc() {
		return sgc;
	}

	public void setSgc(SceneGraphComponent sgc) {
		jRealityVisualization.sgc = sgc;
	}

	protected static SceneGraphComponent sgc = new SceneGraphComponent();
	protected List<Plugin> plugins = new ArrayList<Plugin>();
	
	public jRealityVisualization(){
	}
	
	public void startVisualization(){
		configVertices();
		configFaces();
		configViewer(viewer);
		viewer.startup();
	}
	
	protected void configFaces() {		
		if (faces!=null) {
			IndexedFaceSetFactory faceFactory = new IndexedFaceSetFactory();
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

	protected void configVertices() {
		if (vertices!=null){
			SceneGraphComponent sgcV = new SceneGraphComponent();
			
			PointSetFactory psf = new PointSetFactory();
			psf.setVertexCount(vertices.size());
			psf.setVertexCoordinates(getVertexCoordinates());
			psf.setVertexColors(toDoubleArray(getVertexColors()));
			setVertexLabels(psf);
			psf.update();
			
			sgcV.setGeometry(psf.getPointSet());
			
			sgc.addChild(sgcV);
		}
		
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
		double[][] coordinates = new double[vertices.size()][3];
		int i=0;
		for (Vertex v:vertices.values())
			coordinates[i++]=v.getCoordinates();
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
	
	public void configViewer(JRViewer viewer){
		viewer.setShowPanelSlots(true,true,true,true);
		viewer.addBasicUI();
		for (Plugin p : plugins){
			viewer.registerPlugin(p);
		}
//		viewer.getController().registerPlugin(new Avatar());
//		viewer.getController().registerPlugin(new Terrain());
//		viewer.getController().registerPlugin(new Sky());
//		viewer.registerPlugin(new SCPanelPlugin(sgc,sc));
		viewer.setShowPanelSlots(true, true, true, true);
		viewer.setContent(sgc);
		viewer.addContentUI();

	}
	
	protected void setAppearance(){
		sgc.setAppearance(new Appearance());
		DefaultGeometryShader dps = ShaderUtility.createDefaultGeometryShader(sgc.getAppearance(), false);
		
		DefaultPointShader ps = (DefaultPointShader) dps.getPointShader();
		ps.setPointRadius(0.8);
		//ps.setDiffuseColor(de.jreality.shader.Color.red);
		dps.setShowPoints(true);
		// Labels
	    DefaultTextShader pts = (DefaultTextShader) ps.getTextShader();
	    pts.setDiffuseColor(de.jreality.shader.Color.blue);
	    // scale the label
	    Double scale = new Double(0.01);
	    pts.setScale(1.5*scale);
	    // apply a translation to the position of the label in camera coordinates (-z away from camera)
	    double[] offset = new double[]{-.1,0,0.3};
	    pts.setOffset(offset);
	    // the alignment specifies a direction in which the label will be shifted in the 2d-plane of the billboard
	    pts.setAlignment(SwingConstants.NORTH_WEST);
	    // here you can specify any available Java font
	    Font f = new Font("Arial Bold", Font.ITALIC, 24);
	    pts.setFont(f);
	}
}
