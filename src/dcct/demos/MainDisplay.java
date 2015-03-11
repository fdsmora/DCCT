package dcct.demos;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import de.jreality.scene.data.StorageModel;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.ShaderUtility;
import de.jtem.jrworkspace.plugin.sidecontainer.template.ShrinkPanelPlugin;

public class MainDisplay {
	
	public static JRViewer viewer = new JRViewer();
	public static SceneGraphComponent sgc = new SceneGraphComponent();
	public static SimplicialComplex sc;
	
	public MainDisplay(){}
	
	public static void main(String[] args) throws InterruptedException {
		sc = new SimplicialComplex();
		Simplex0 p = new Simplex0("p", new double[]{0.0,12.0,0.0});
		Simplex0 q = new Simplex0("q", new double[]{-20.0,-20.0,0.0});
		Simplex0 r = new Simplex0("r", new double[]{20.0,-20.0,0.0});
		
		Simplex2 initial = new Simplex2(p,q,r);
		
		sc.addTriangle(initial);
		
		configViewer();
		
		draw();
		
//		Thread.sleep(2000);
//		sc.subdivide();
//		draw();
//		
//		Thread.sleep(2000);
//		sc.subdivide();
//		draw();
//
//		Thread.sleep(2000);
//		sc.subdivide();
//		draw();
		
	}
	
	public static void configViewer(){
		viewer.setShowPanelSlots(true,true,true,true);
		viewer.addBasicUI();
//		viewer.getController().registerPlugin(new Avatar());
//		viewer.getController().registerPlugin(new Terrain());
//		viewer.getController().registerPlugin(new Sky());
		viewer.registerPlugin(new SCPanelPlugin(sgc,sc));
		viewer.setShowPanelSlots(true, false, false, false);
		viewer.setContent(sgc);
//		viewer.addContentUI();
		// Para permitir rotación y zoom
		viewer.registerPlugin(new ContentLoader());
		ContentTools contentTools = new ContentTools();
		viewer.registerPlugin(contentTools);
	}
	
	public static void draw(){
		setAppearance();
		drawVertices();
		drawTriangles();
		viewer.startup();
	}
	
	public static void drawTriangles(){
		IndexedFaceSetFactory faceFactory = new IndexedFaceSetFactory();
		
		int t = sc.getTriangles().size();
	
		faceFactory.setVertexCount(sc.getVertices().size());
		faceFactory.setVertexCoordinates(sc.getVertexCoordinates());

		Color[] faceColors = new Color[t];
		
		for (int i=0 ; i<t; i++){
			faceColors[i]=Color.GREEN;
		}
		
		faceFactory.setFaceCount( t);
		faceFactory.setFaceIndices( sc.getFacesIndexSet() ); 
		faceFactory.setFaceColors(toDoubleArray(faceColors));    
		faceFactory.setGenerateFaceNormals( true );
		faceFactory.setGenerateEdgesFromFaces(true);

		faceFactory.update();

		sgc.setGeometry(faceFactory.getPointSet());	
	}

	public static void drawVertices(){
		SceneGraphComponent sgcV = new SceneGraphComponent();
		
		PointSetFactory psf = new PointSetFactory();
		psf.setVertexCount(sc.getVertices().size());
		psf.setVertexCoordinates(sc.getVertexCoordinates());
		setVertexLabels(psf, sc);
		psf.update();
		
		sgcV.setGeometry(psf.getPointSet());
		
		sgc.addChild(sgcV);
	}
	
	private static void setVertexLabels(PointSetFactory psf, SimplicialComplex sc){
		String[] labels = new String[sc.getVertices().size()];
		int i=0;
		for (Simplex0 v : sc.getVertices())
			labels[i++]=v.getName();
		
		psf.setVertexAttribute(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(labels));
	}
	
	private static void setAppearance(){
		sgc.setAppearance(new Appearance());
		DefaultGeometryShader dps = ShaderUtility.createDefaultGeometryShader(sgc.getAppearance(), false);
		dps.setShowPoints(true);
		
		DefaultPointShader ps = (DefaultPointShader) dps.getPointShader();
		ps.setPointRadius(0.5);
		//ps.setDiffuseColor(Color.red);
		
		// Labels
	    DefaultTextShader pts = (DefaultTextShader) ps.getTextShader();
	    //pts.setDiffuseColor(Color.blue);
	    // scale the label
	    Double scale = new Double(0.01);
	    pts.setScale(1.5*scale);
	    // apply a translation to the position of the label in camera coordinates (-z away from camera)
	    double[] offset = new double[]{-.1,0,0.3};
	    pts.setOffset(offset);
	    // the alignment specifies a direction in which the label will be shifted in the 2d-plane of the billboard
	    pts.setAlignment(SwingConstants.NORTH_WEST);
	    // here you can specify any available Java font
	    Font f = new Font("Arial Bold", Font.ITALIC, 48);
	    pts.setFont(f);
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
}
