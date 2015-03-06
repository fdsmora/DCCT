package dcct.visualization;

//import java.awt.Color;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
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
import de.jtem.jrworkspace.plugin.sidecontainer.template.ShrinkPanelPlugin;

public class jRealityVisualization {
	
	protected Set<Vertex> vertices;
	protected JRViewer viewer = new JRViewer();
	protected static SceneGraphComponent sgc = new SceneGraphComponent();
	
	public jRealityVisualization(Set<Vertex> vertices){
		this.vertices = vertices;
	}
	
	public void createVisualization(){
		configVertices();
		//configFaces()
		configViewer(viewer);
		viewer.startup();
	}
	
	protected void configVertices() {
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

	protected double[][] getVertexCoordinates() {
		double[][] coordinates = new double[vertices.size()][3];
		int i=0;
		for (Vertex v:vertices)
			coordinates[i++]=v.getCoordinates();
		return coordinates;
	}
	
	protected Color[] getVertexColors() {
		Color[] colors = new Color[vertices.size()];
		int i=0;
		for (Vertex v:vertices)
			colors[i++]=v.getColor();
		return colors;
	}
	
	private void setVertexLabels(PointSetFactory psf){
		String[] labels = new String[vertices.size()];
		int i=0;
		for (Vertex v : vertices)
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

//	public static void main(String[] args){
//		JRViewer viewer = new JRViewer();
//		configViewer(viewer);
//		viewer.startup();
//	}
	
	public void configViewer(JRViewer viewer){
		viewer.setShowPanelSlots(true,true,true,true);
		viewer.addBasicUI();
//		viewer.getController().registerPlugin(new Avatar());
//		viewer.getController().registerPlugin(new Terrain());
//		viewer.getController().registerPlugin(new Sky());
		//viewer.registerPlugin(new SCPanelPlugin(sgc,sc));
		viewer.setShowPanelSlots(true, false, false, false);
		viewer.setContent(sgc);
//		viewer.addContentUI();
		// Para permitir rotación y zoom
		viewer.registerPlugin(new ContentLoader());
		ContentTools contentTools = new ContentTools();
		viewer.registerPlugin(contentTools);
	}
	
	protected void setAppearance(){
		sgc.setAppearance(new Appearance());
		DefaultGeometryShader dps = ShaderUtility.createDefaultGeometryShader(sgc.getAppearance(), false);
		dps.setShowPoints(true);
		
		DefaultPointShader ps = (DefaultPointShader) dps.getPointShader();
		ps.setPointRadius(3.0);
		//ps.setDiffuseColor(de.jreality.shader.Color.red);
		
		// Labels
	    DefaultTextShader pts = (DefaultTextShader) ps.getTextShader();
	    //pts.setDiffuseColor(de.jreality.shader.Color.blue);
	    // scale the label
	    Double scale = new Double(0.01);
	    pts.setScale(1.0*scale);
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
