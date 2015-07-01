package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import dctopology.Process;
import dctopology.Simplex;
import dctopology.SimplicialComplex;

public class Geometry {
	protected Map<String, Vertex> vertices; 
	protected static final Color DEFAULT_COLOR = Color.BLUE;
	private Map<Long, Face> faces = null;
	protected static Geometry geometry = null;
		
	public Geometry(SimplicialComplex sc, Geometry parentGeometry, List<Color> colors){
		if (sc!=null) {
			Color[] processColors = new Color[sc.totalDistinctProcesses()];
			int indexCount = 0;
			Queue<Color> qColors = null;
			if (sc.isChromatic())
				qColors = new LinkedList<Color>(colors) ;
			
			vertices = new LinkedHashMap<String, Vertex>(); //The only purpose of this is to control the uniqueness of vertices. 
			faces = new HashMap<Long, Face>(sc.getSimplices().size());
			
			for (Simplex s : sc.getSimplices()) {
				boolean chromatic = s.isChromatic();
				
				Face face = new Face(s.getId());
				if (parentGeometry != null)
					face.setParentFace(parentGeometry.faces.get(s.getParentId()));
				face.setChromatic(chromatic);
				
				int i=0;
				for (Process p : s.getProcesses()) {
					Vertex v;
					// If complex is chromatic, distinguish processes by pair (id, view), otherwise only by view. 
					String pKey = chromatic? p.toString() : p.getView();
					if (vertices.containsKey(pKey)) {
						v = vertices.get(pKey);
					} else {
						v = new Vertex(p);
						v.setIndex(indexCount++);
						//v.setCoordinates(randomCoordGenerator(indexCount));
						setColor(v, p, qColors, processColors);
						vertices.put(pKey,v);
					}
					face.add(v);
					face.calculateCoordinates();
				}
				faces.put(face.id,face);
			}
		}
	}

	public Map<String, Vertex> getVertices() {
		return vertices;
	}

	public Map<Long, Face> getFaces() {
		return faces;
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
		coords[2]=0;//rand.nextInt(range) - x;
		return coords;
	}

	protected void setColor(Vertex v, Process p, Queue<Color> qColors, Color[] pColors){
		if (qColors != null){ // Chromatic case
			if (pColors[p.getId()]==null)
				pColors[p.getId()]= qColors.remove();
			v.setColor(pColors[p.getId()]);	
		}else // Non-Chromatic case
			v.setColor(DEFAULT_COLOR);
	}
	
	public void test(){
		System.out.println("-----------Vertices-------------");
		for (Vertex v : vertices.values()){
			System.out.println(v);
			//System.out.println("Coordinates: " + Arrays.toString(v.getCoordinates()));
		}
		System.out.println("-----------COORDINATES-------------");
		String comma = "";
		for (Vertex v : vertices.values()){
			double[] coords = v.getCoordinates();
			System.out.print(comma + String.format("(%1$.2f,%2$.2f)", coords[0], coords[1] ));
			comma = ",";
		}
		System.out.println("\n-----------Faces=" + faces.size() +"-------------");
		System.out.print("[");
		for (Face f : faces.values()){
			String prefix = "";
			System.out.print("[");
			for (int i:f.getFaceArray()){
			//for(int i=0; i< f.length; i++){
			//	System.out.print(prefix + f[i]);
				System.out.print(prefix + i);
				prefix = ",";
			}
			System.out.print("]");
		}
		System.out.println("]");
	}
}



