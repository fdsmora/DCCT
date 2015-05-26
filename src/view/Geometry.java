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
	protected List<int[]> faces;
	protected static List<Color> colors = Arrays.asList(Color.WHITE,Color.BLACK,Color.GREEN,Color.RED,
			Color.BLUE,Color.YELLOW, Color.PINK, Color.CYAN, Color.MAGENTA);
	
	public Geometry(SimplicialComplex sc){
		if (sc!=null) {
			if (colors == null || sc.totalDistinctProcesses() > colors.size())
				throw new IllegalArgumentException(
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
					} else {
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
		}
	}

	public List<Color> getColors() {
		return colors;
	}

	public Map<String, Vertex> getVertices() {
		return vertices;
	}

	public List<int[]> getFaces() {
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
		if (pColors[p.getId()]==null)
			pColors[p.getId()]=qColors.remove();
		v.color=pColors[p.getId()];	
	}
	
	public void test(){
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
				this.label = p.getView();
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
}
