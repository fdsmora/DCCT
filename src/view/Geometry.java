package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
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
	//protected List<int[]> faces;
	protected static final Color DEFAULT_COLOR = Color.BLUE;
	protected List<Face> oldFaces = null;
	protected List<Face> faces = null;
	protected static Geometry geometry = null;
	
	public static Geometry createGeometry(SimplicialComplex sc, List<Color> colors){
		if (geometry!=null){
			List<Face> oldFaces = geometry.getFaces();
			geometry = new Geometry(sc, colors);
			geometry.setOldFaces(oldFaces);
		}
		else 
			geometry = new Geometry(sc, colors);
		return geometry;
	}
	
	public static void reset(){
		geometry = null;
	}
	
	private Geometry(SimplicialComplex sc, List<Color> colors){
		if (sc!=null) {
			Color[] processColors = new Color[sc.totalDistinctProcesses()];
			int indexCount = 0;
			Queue<Color> qColors = null;
			if (colors!=null)
				qColors = new LinkedList<Color>(colors) ;
			
			vertices = new LinkedHashMap<String, Vertex>(); //The only purpose of this is to control the uniqueness of vertices. 
			faces = new ArrayList<Face>(sc.getSimplices().size());
			
			for (Simplex s : sc.getSimplices()) {
				//int[] face = new int[s.getProcessCount()];
				Face face = new Face(s.getId());
				int i=0;
				for (Process p : s.getProcesses()) {
					Vertex v;
					// If complex is chromatic, distinguish processes by pair (id, view), otherwise only by view. 
					String pKey = colors != null? p.toString() : p.getView();
					if (vertices.containsKey(pKey)) {
						v = vertices.get(pKey);
					} else {
						v = new Vertex(p);
						v.index = indexCount++;
						v.coordinates = randomCoordGenerator(indexCount);
						setColor(v, p, qColors, processColors);
						vertices.put(pKey,v);
					}
					face.add(v);
					//face[i++]=v.index;
				}
				faces.add(face);
			}
		}
	}

	public Map<String, Vertex> getVertices() {
		return vertices;
	}

	public List<Face> getFaces() {
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
			v.color=pColors[p.getId()];	
		}else // Non-Chromatic case
			v.color=DEFAULT_COLOR;
	}
	
	public void test(){
		System.out.println("-----------Vertices-------------");
		for (Vertex v : vertices.values())
			System.out.println(v);
		System.out.println("-----------Faces=" + faces.size() +"-------------");
		System.out.print("[");
		for (Face f : faces){
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
	
	public class Face {
		protected List<Vertex> vertices =  new ArrayList<Vertex>();
		protected int[] faceArray =null;
		protected long id = 0;
		protected long parentId = 0;
		protected int n = 0;
		
		public Face(long id){
			this.id = id;
		}
		public long getId(){
			return id;
		}
		public void add(Vertex v){
			vertices.add(v);
		}
		public int[] getFaceArray(){
			if (faceArray==null ||
					n<vertices.size()){ // In case add method was called after the last call of this method, we need to update facesArray.
				n = vertices.size();
				faceArray = new int[n];
				int i = 0;
				for (Vertex v : vertices)
					faceArray[i++]=v.index;
			}
			return faceArray;
		}
	}

	
	protected class Vertex {
		
		double[] getCoordinates() {
			return coordinates;
		}
		void setCoordinates(double[] coordinates) {
			this.coordinates = coordinates;
		}
		int getIndex() {
			return index;
		}
		void setIndex(int index) {
			this.index = index;
		}
		Color getColor() {
			return color;
		}
		void setColor(Color color) {
			this.color = color;
		}
		String getLabel() {
			return label;
		}
		void setLabel(String label) {
			this.label = label;
		}
		
		String getId() {
			return id;
		}
		protected double[] coordinates;
		protected int index;
		protected Color color;
		protected String label;
		protected String id;
		
		Vertex(Process p){
			if (p!=null){
				this.label = p.toString();//p.getView();  // Temporal, to highlight id of process
				this.id = p.toString();
			}
		}
		
		@Override
		public String toString(){
			return String.format("Vertex.index=%1$s\nVertex.label=%2$s\nVertex.coordinates=(%3$.2f,%3$.2f,%3$.2f)\nVertex.color=%4$s\n",
					this.index,this.label,this.coordinates[0],this.coordinates[1],this.coordinates[2],this.color);
		}
		
		@Override
		public boolean equals(Object o){
			if (!(o instanceof Vertex)) 
			    return false;
			return true;
		}
		
		@Override
		public int hashCode(){
			return this.id.hashCode();
		}
	}


	public List<Face> getOldFaces() {
		return oldFaces;
	}

	public void setOldFaces(List<Face> oldFaces) {
		this.oldFaces = oldFaces;
	}
}
