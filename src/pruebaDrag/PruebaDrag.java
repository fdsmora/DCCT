package pruebaDrag;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import javax.swing.SwingConstants;

import test_polygon.DragPointSet;
import dcct.visualization.Vertex;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedLineSetFactory;
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
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jtem.jrworkspace.plugin.Plugin;

public class PruebaDrag {

	protected static SceneGraphComponent sgc = new SceneGraphComponent();
	protected static SceneGraphComponent sgcP = new SceneGraphComponent();

	protected static JRViewer viewer = new JRViewer();
	protected static DragEventTool dragTool = new DragEventTool();
	protected static IndexedLineSetFactory lsf = new IndexedLineSetFactory();
	protected static IndexedFaceSetFactory faceFactory = new IndexedFaceSetFactory();
	protected static PointSetFactory psf = new PointSetFactory();
	protected static int n = 4;
	protected static double[][] vertices = new double[n][3];
	protected static int[][] lines = new int[n][2];
	protected static int[][] faces = new int[2][3];
	
	public static void main(String[] args) {

		setPoints();
		//setLines();
		setFaces();
		sgc.setGeometry(faceFactory.getGeometry());
		
		dragTool.addPointDragListener(new PointDragListener() {
			public void pointDragEnd(PointDragEvent e) {
			}
			public void pointDragStart(PointDragEvent e) {
			}
			public void pointDragged(PointDragEvent e) {
				PruebaDrag.pointDragged(e);
			}
		});
		
		sgc.addTool(dragTool);
		
		setAppearance();
		configViewer();
		
		viewer.startup();
	}
	
	private static void configViewer() {
		viewer.setShowPanelSlots(true,true,true,true);
		viewer.addBasicUI();
		viewer.setShowPanelSlots(true, true, false, false);
		viewer.setContent(sgc);
		viewer.addContentUI();
		
	}

	public static void generateVertices(){
		
		Random rand = new Random();
		int range = 4;
		
		for (int i = 0; i<n; i++){
			int x = i%2==0 ? range:0;
			vertices[i][0]=rand.nextInt(range) - x;
			rand = new Random();
			vertices[i][1]=rand.nextInt(range) - x;
			rand = new Random();
			vertices[i][2]=rand.nextInt(range) - x;
		}
	}
	
	public static void setPoints(){
		generateVertices();
		psf.setVertexCount(n);
		psf.setVertexCoordinates(vertices);
		//setVertexLabels();
		psf.update();
		sgcP.setGeometry(psf.getGeometry());
		sgc.addChild(sgcP);
	}
	
	public static void setLines(){
		int i =0;
		for ( i=0; i<n-1;i++){
			lines[i][0]=i;
			lines[i][1]=i+1;
		}
		lines[i][0]=i;
		lines[i][1]=0;

		generateVertices();
		lsf.setVertexCount(n);
		lsf.setVertexCoordinates(vertices);
		lsf.setEdgeCount(lines.length);
		lsf.setEdgeIndices(lines);
		lsf.update();
	}
	
	public static void setFaces(){
		faceFactory.setVertexCount(n);
		faceFactory.setVertexCoordinates(vertices);
		
		faceFactory.setFaceCount(faces.length);
		
		faces[0][0]=0;
		faces[0][1]=1;
		faces[0][2]=2;
		faces[1][0]=0;
		faces[1][1]=1;
		faces[1][2]=3;
		
		faceFactory.setFaceIndices(faces);
		faceFactory.setGenerateFaceNormals(true);
		faceFactory.setGenerateEdgesFromFaces(true);
		faceFactory.update();
	}
	
	public static void pointDragged(PointDragEvent e){
		vertices[e.getIndex()][0]=e.getX();
		vertices[e.getIndex()][1]=e.getY();
		vertices[e.getIndex()][2]=e.getZ();
		psf.setVertexCoordinates(vertices);
		psf.update();
		faceFactory.setVertexCoordinates(vertices);
		faceFactory.update();
	}
	
	private static void setVertexLabels(){
		String[] labels = new String[n];
		for (int i=0; i<n; i++)
			labels[i++]="hola";
		
		psf.setVertexAttribute(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(labels));
	}
	
	protected static void setAppearance(){
		sgc.setAppearance(new Appearance());
		DefaultGeometryShader dps = ShaderUtility.createDefaultGeometryShader(sgc.getAppearance(), false);
		
		DefaultPointShader ps = (DefaultPointShader) dps.getPointShader();
		ps.setPointRadius(0.2);
		ps.setDiffuseColor(de.jreality.shader.Color.red);
		dps.setShowPoints(true);
		// Labels
	    DefaultTextShader pts = (DefaultTextShader) ps.getTextShader();
	    pts.setDiffuseColor(de.jreality.shader.Color.blue);
	    
	    DefaultLineShader ls = (DefaultLineShader) dps.getLineShader();
		
	    ls.setTubeRadius(0.02);
	    
	    DefaultPolygonShader gs = (DefaultPolygonShader) dps.getPolygonShader();
	    
	    ps.setDiffuseColor(de.jreality.shader.Color.green);
	  	    
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
