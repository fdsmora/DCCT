package dcct.visualization;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import dcct.topology.DCModel;
import dcct.topology.SimplicialComplex;
import dcct.topology.Simplex;
import dcct.process.Process;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.plugin.menu.CameraMenu;
import de.jreality.scene.tool.Tool;
import de.jreality.tools.ClickWheelCameraZoomTool;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jreality.tools.RotateTool;

public class Visualizer {

	protected Map<String, Vertex> vertices; 
	protected List<int[]> faces;
	protected jRealityVisualization jRealityV;
	
	public DCModel getModel() {
		return model;
	}

	public void setModel(DCModel model) {
		this.model = model;
	}

	protected DCModel model;
	
	public Visualizer(DCModel model){
		this.model = model;
		jRealityV = new jRealityVisualization();
		// Para permitir rotación y zoom
		jRealityV.addPlugin(new ContentLoader());
		jRealityV.addPlugin(new ContentTools());
		jRealityV.addPlugin(new SCGeneratePanelPlugin(jRealityV.getSgc(),this));
		
//		for (Tool t: jRealityV.getSgc().getTools())
//			if (t.getClass().equals(DragEventTool.class))
//			{
//				((DragEventTool)t).addPointDragListener(new PointDragListener() {
//					public void pointDragEnd(PointDragEvent e) {
//					}
//					public void pointDragStart(PointDragEvent e) {
//					}
//					public void pointDragged(PointDragEvent e) {
//						jRealityV.pointDragged(e);
//					}
//				});
//				break;
//			}
		jRealityV.configViewer();
	}
	
	public void startVisualization(){
		jRealityV.startVisualization();
	}
	
	public void draw(SimplicialComplex sc){
		if (sc!=null) {
			try {
				List<Color> colors = DCModel.getColors(sc.totalDistinctProcesses());
				if (colors == null || sc.totalDistinctProcesses() > colors.size())
					throw new Exception(
							"Not enough colors to assign to all processes.");
				Color[] processColors = new Color[sc.totalDistinctProcesses()];
				int indexCount = 0;
				Queue<Color> qColors = new LinkedList<Color>(colors);
				vertices = new LinkedHashMap<String, Vertex>();
				faces = new ArrayList<int[]>(sc.getSimplices().size());
				for (Simplex s : sc.getSimplices()) {
					int[] face = new int[s.getProcessCount()];
					int i=0;
					for (Process p : s.getProcesses()) {
						Vertex v;
						if (vertices.containsKey(p.toString())) {
							v = vertices.get(p.toString());
						}else {
							v = new Vertex(p);
							v.index = indexCount++;
							v.coordinates = randomCoordGenerator(indexCount);
							setColor(v, p, qColors, processColors);
							vertices.put(p.toString(),v);
						}
						face[i++]=v.index;
					}
					faces.add(face);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//TEST
		testDraw();
		
		jRealityV.setVertices(vertices);
		jRealityV.setFaces(faces);
		jRealityV.startVisualization();
	}
	
	public void subdivision(){
		draw(model.subdivide());	
	}
	
	protected double[] randomCoordGenerator(int i) {
		double[] coords = new double[3];
		Random rand = new Random();
		int range = 4; 
		int x = i%2==0 ? range:0;
		coords[0]=rand.nextInt(range) - x;
		rand = new Random();
		coords[1]=rand.nextInt(range) - x;
		rand = new Random();
		coords[2]=rand.nextInt(range) - x;
		return coords;
	}

	protected void setColor(Vertex v, Process p, Queue<Color> qColors, Color[] pColors){
		if (pColors[p.getId()]==null)
			pColors[p.getId()]=qColors.remove();
		v.color=pColors[p.getId()];	
	}
	
	public void testDraw(){
		System.out.println("-----------Vertices-------------");
		for (Vertex v : vertices.values())
			System.out.println(v);
		System.out.println("-----------Faces=" + faces.size() +"-------------");
		System.out.print("[");
		for (int[] f : faces){
			String prefix = "";
			System.out.print("[");
			for(int i=0; i< f.length; i++){
				System.out.print(prefix + f[i]);
				prefix = ",";
			}
			System.out.print("]");
		}
		System.out.println("]");
	}
}
