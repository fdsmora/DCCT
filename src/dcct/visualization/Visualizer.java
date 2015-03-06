package dcct.visualization;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import dcct.topology.SimplicialComplex;
import dcct.topology.Simplex;
import dcct.process.Process;

public class Visualizer {
	
	public Set<Vertex> getVertices() {
		return vertices;
	}

	public void setVertices(Set<Vertex> vertices) {
		this.vertices = vertices;
	}

	protected Set<Vertex> vertices = new LinkedHashSet<Vertex>();
	
	public static void main(String[] args) {
	}
	
	public Visualizer(){
	}
	
	public void draw(SimplicialComplex sc, List<String> colors){
		if (sc!=null) {
			try {
				if (colors == null || sc.totalDistinctProcesses() > colors.size())
					throw new Exception(
							"Not enough colors to assign to all processes.");
				String[] processColors = new String[sc.totalDistinctProcesses()];
				int indexCount = 0;
				Queue<String> qColors = new LinkedList<String>(colors);
				for (Simplex s : sc.getSimplices()) {
					for (Process p : s.getProcesses()) {
						Vertex v = new Vertex(p);
						if (vertices.add(v)) {
							v.index = indexCount++;
							// v.coordinates = nextCoordinate();
							setColor(v, p, qColors, processColors);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//TEST
		testDraw();
	}
	
	protected void setColor(Vertex v, Process p, Queue<String> qColors, String[] pColors){
		if (pColors[p.getId()]==null)
			pColors[p.getId()]=qColors.remove();
		v.color=pColors[p.getId()];	
	}
	
	public void testDraw(){
		System.out.println("------------------------");
		for (Vertex v : vertices)
			System.out.println(v);
	}
}
