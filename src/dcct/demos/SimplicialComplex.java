package dcct.demos;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SimplicialComplex {

	public Set<Simplex0> getVertices() {
		return Vertices;
	}
	
	public double[][] getVertexCoordinates(){
		int n = Vertices.size();
		double[][] coords = new double[n][3];
		int i = 0;
		for (Simplex0 v : Vertices)
			coords[i++]=v.getPosition();
		
		return coords;
	}

	public void addVertex(Simplex0 vertex) {
		Vertices.add(vertex);
	}

	public Set<Simplex1> getEdges() {
		return Edges;
	}

	public void addEdge(Simplex1 edge) {
		Edges.add(edge);
		Vertices.addAll(edge.getFaces());
	}

	public Set<Simplex2> getTriangles() {
		return Triangles;
	}

	public void addTriangle(Simplex2 triangle) {
		Triangles.add(triangle);
		Vertices.addAll(triangle.getFaces());
	}
	
	public void subdivide(){
//		Por ahora solo subdividimos triangulos
		Set<Simplex2> newTriangles = new LinkedHashSet<Simplex2>();
		
		for(Simplex2 t : Triangles){
			Simplex0 p,q,r,v0,v1,v2,v3,v4,v5,v6,v7,v8;
			double posX;
			double posY;
			double posZ;
			p = t.getVertex0();
			q = t.getVertex1();
			r = t.getVertex2();
			
			// subdivision de arista (p,q)
			posX = (2*p.getPosition()[0]+q.getPosition()[0])/3;
			posY = (2*p.getPosition()[1]+q.getPosition()[1])/3;
			posZ = (2*p.getPosition()[2]+q.getPosition()[2])/3;
			v0 = new Simplex0("", new double[]{posX,posY,posZ});
			
			posX = (p.getPosition()[0]+2*q.getPosition()[0])/3;
			posY = (p.getPosition()[1]+2*q.getPosition()[1])/3;
			posZ = (p.getPosition()[2]+2*q.getPosition()[2])/3;
			v1 = new Simplex0("", new double[]{posX,posY,posZ});

			// subdivision de arista (q,r)
			posX = (2*q.getPosition()[0]+r.getPosition()[0])/3;
			posY = (2*q.getPosition()[1]+r.getPosition()[1])/3;
			posZ = (2*q.getPosition()[2]+r.getPosition()[2])/3;
			v2 = new Simplex0("", new double[]{posX,posY,posZ});
			
			posX = (q.getPosition()[0]+2*r.getPosition()[0])/3;
			posY = (q.getPosition()[1]+2*r.getPosition()[1])/3;
			posZ = (q.getPosition()[2]+2*r.getPosition()[2])/3;
			v3 = new Simplex0("", new double[]{posX,posY,posZ});

			// subdivision de arista (p,r)
			posX = (2*p.getPosition()[0]+r.getPosition()[0])/3;
			posY = (2*p.getPosition()[1]+r.getPosition()[1])/3;
			posZ = (2*p.getPosition()[2]+r.getPosition()[2])/3;
			v4 = new Simplex0("", new double[]{posX,posY,posZ});
			
			posX = (p.getPosition()[0]+2*r.getPosition()[0])/3;
			posY = (p.getPosition()[1]+2*r.getPosition()[1])/3;
			posZ = (p.getPosition()[2]+2*r.getPosition()[2])/3;
			v5 = new Simplex0("", new double[]{posX,posY,posZ});
			
			// Subdivision interior
			posX = (p.getPosition()[0]+2*q.getPosition()[0]+2*r.getPosition()[0])/5;
			posY = (p.getPosition()[1]+2*q.getPosition()[1]+2*r.getPosition()[1])/5;
			posZ = (p.getPosition()[2]+2*q.getPosition()[2]+2*r.getPosition()[2])/5;
			v6 = new Simplex0("", new double[]{posX,posY,posZ});
		
			posX = (2*p.getPosition()[0]+q.getPosition()[0]+2*r.getPosition()[0])/5;
			posY = (2*p.getPosition()[1]+q.getPosition()[1]+2*r.getPosition()[1])/5;
			posZ = (2*p.getPosition()[2]+q.getPosition()[2]+2*r.getPosition()[2])/5;
			v7 = new Simplex0("", new double[]{posX,posY,posZ});
		
			posX = (2*p.getPosition()[0]+2*q.getPosition()[0]+r.getPosition()[0])/5;
			posY = (2*p.getPosition()[1]+2*q.getPosition()[1]+r.getPosition()[1])/5;
			posZ = (2*p.getPosition()[2]+2*q.getPosition()[2]+r.getPosition()[2])/5;
			v8 = new Simplex0("", new double[]{posX,posY,posZ});
		
			Vertices.add(v0);
			Vertices.add(v1);
			Vertices.add(v2);
			Vertices.add(v3);
			Vertices.add(v4);
			Vertices.add(v5);
			Vertices.add(v6);
			Vertices.add(v7);
			Vertices.add(v8);
			
			newTriangles.add(new Simplex2(v8,q,v6));
			newTriangles.add(new Simplex2(v6,v2,v3));
			newTriangles.add(new Simplex2(v7,v6,r));
			
			newTriangles.add(new Simplex2(q,v8,v1));
			newTriangles.add(new Simplex2(q,v2,v6));
			
			newTriangles.add(new Simplex2(r,v6,v3));
			newTriangles.add(new Simplex2(r,v5,v7));
			
			newTriangles.add(new Simplex2(v6,v7,v8));
			
			newTriangles.add(new Simplex2(v0,v1,v8));
			newTriangles.add(new Simplex2(v4,v7,v5));
			
			newTriangles.add(new Simplex2(p,v8,v7));
			
			newTriangles.add(new Simplex2(p,v0,v8));
			newTriangles.add(new Simplex2(p,v7,v4));
		}
		Triangles = newTriangles;
	}
	
	public int[][] getFacesIndexSet(){
		int[][] indexSet = new int[Triangles.size()][3];
		
		List<Simplex0> lVert = new ArrayList<Simplex0>(Vertices);
		
		int i = 0;
		for (Simplex2 t: Triangles){
			Simplex0 v0 = t.getVertex0();
			Simplex0 v1 = t.getVertex1();
			Simplex0 v2 = t.getVertex2();
			indexSet[i++]=new int[]
					{lVert.indexOf(v0), lVert.indexOf(v1),
					lVert.indexOf(v2)};
		}
		
		return indexSet;
	}
	
	// Usamos LinkedHashSet para asi mantener el orden en que los 
	// elementos fueron insertados. 
	private Set<Simplex0> Vertices = new LinkedHashSet<Simplex0>();
	private Set<Simplex1> Edges = new LinkedHashSet<Simplex1>();
	private Set<Simplex2> Triangles = new LinkedHashSet<Simplex2>();
	
	public SimplicialComplex(){}
}
