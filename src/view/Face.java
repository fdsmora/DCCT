package view;

import java.util.HashMap;
import java.util.Map;
import configuration.Constants;

public class Face {
	protected Map<Integer, Vertex> vertices =  new HashMap<Integer, Vertex>();
	protected int[] faceArray =null;
	protected long id = 0;
	protected Face parentFace = null;
	protected boolean chromatic = true;
	private int n = 0;
	
	public Face(long id){
		this.id = id;
	}
	public long getId(){
		return id;
	}
	public void add(Vertex v){
		vertices.put(v.getId(), v);
	}
	public int[] getFaceArray(){
		if (faceArray==null ||
				n<vertices.size()){ // In case add method was called after the last call of this method, we need to update facesArray.
			n = vertices.size();
			faceArray = new int[n];
			int i = 0;
			for (Vertex v : vertices.values())
				faceArray[i++]=v.getIndex();
		}
		return faceArray;
	}
	public void setParentFace(Face parent){
		parentFace = parent;
	}
	public boolean isChromatic() {
		return chromatic;
	}
	public void setChromatic(boolean chromatic) {
		this.chromatic = chromatic;
	}
	public void calculateCoordinates(){
		if (parentFace==null){
			calculateInitialComplexCoordinates();
			return;
		}
		for (Vertex v : vertices.values()){
			v.calculateCoordinates(parentFace, chromatic);
		}
	}
	private void calculateInitialComplexCoordinates(){
		switch(vertices.size()){
			case 1: 
				vertices.get(0).setCoordinates(Constants.DEFAULT_0_SIMPLEX_VERTEX_COORDINATES);
				break;
			case 2: 
				vertices.get(0).setCoordinates(Constants.DEFAULT_1_SIMPLEX_VERTEX_COORDINATES[0]);
				vertices.get(1).setCoordinates(Constants.DEFAULT_1_SIMPLEX_VERTEX_COORDINATES[1]);
				break;
			case 3:
				vertices.get(0).setCoordinates(Constants.DEFAULT_2_SIMPLEX_VERTEX_COORDINATES[0]);
				vertices.get(1).setCoordinates(Constants.DEFAULT_2_SIMPLEX_VERTEX_COORDINATES[1]);
				vertices.get(2).setCoordinates(Constants.DEFAULT_2_SIMPLEX_VERTEX_COORDINATES[2]);
				break;
		}
	}
	public Map<Integer,Vertex> getVertices() {
		return vertices;
	}
	
}
