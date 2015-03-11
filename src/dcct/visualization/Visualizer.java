package dcct.visualization;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import dcct.topology.DCModel;
import dcct.topology.SimplicialComplex;
import dcct.topology.Simplex;
import dcct.process.Process;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;

public class Visualizer {

	protected Map<String, Vertex> vertices = new LinkedHashMap<String, Vertex>();
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
		jRealityV.addPlugin(new SCPanelPlugin(jRealityV.getSgc(),this));
		jRealityV.addPlugin(new SCGeneratePanelPlugin(jRealityV.getSgc(),this));
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
							v.coordinates = randomCoordGenerator();
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
	
	protected double[] randomCoordGenerator() {
		double[] coords = new double[3];
		Random rand = new Random();
		coords[0]=rand.nextInt(60);
		coords[1]=rand.nextInt(60);
		coords[2]=0.0;
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
		System.out.println("-----------Faces-------------");
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
