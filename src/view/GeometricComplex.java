package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import dctopology.Simplex;
import dctopology.SimplicialComplex;

public class GeometricComplex {
	private SimplicialComplex complex;
	private boolean chromatic = true;
	private List<Face> chromaticFaces;
	private List<Face> nonChromaticFaces;
	private int[][] faceIndices; 	
	private double[][] coordinates;
	private Color[] colors;
	private String[] labels;
	private Map<String, Vertex> vertices = new LinkedHashMap<String, Vertex>();

	public GeometricComplex(SimplicialComplex complex){
		this.complex = complex;
		
		boolean originalChromaticState = complex.isChromatic();

		complex.setChromatic(true);
		chromaticFaces = buildFaces(complex);
		// Think in a better solution.
		complex.setChromatic(false);
		nonChromaticFaces = buildFaces(complex);
		
		this.chromatic = originalChromaticState;
		
		// Leave it as it was
		complex.setChromatic(originalChromaticState);
		
		setVertices();
		setFaceIndices();
		
	}
	
	private List<Face> buildFaces(SimplicialComplex complex) {
		List<Face> faces = null;
		// Set a Face for each simplex
		List<Simplex> simplices = complex.getSimplices();
		if (simplices !=null){
			faces = new ArrayList<Face>(complex.getSimplexCount());
			for (Simplex s : simplices){
				Face f;
				Simplex parent = s.getParent();
				f = parent != null? new Face(s, parent.getFace()) : new Face(s); 
				faces.add(f);
				s.setFace(f);
			}
		}
		return faces;
	}

	private void setVertices() {
		
		int indexCount = 0;
		for (Face f : getFaces()){
			for (Vertex v : f.getVertices()){
				String key = v.getProcess().toString();
				if(vertices.containsKey(key)){
					Vertex dup = vertices.get(key);
					v.setIndex(dup.getIndex());
				}
				else {
					vertices.put(key,v);
					v.setIndex(indexCount++);
				}
			}			
		}	
		setCoordinates();
		setLabels();
		setColors();
	}

	private void setColors() {
		colors = new Color[getVertexCount()];
		int j = 0;
		for (Vertex v : vertices.values()){
			colors[j++]= v.getColor();
		}		
	}

	private void setLabels() {
		labels = new String[getVertexCount()];
		int j = 0;
		for (Vertex v : vertices.values()){
			labels[j++]=v.getLabel();
		}		
	}

	private void setCoordinates() {
		coordinates = new double[getVertexCount()][];
		
		int j = 0;
		for (Vertex v : vertices.values()){
			coordinates[j++] = v.getCoordinates(); 
		}		
	}

	public int getVertexCount() {
		return vertices.size();
	}
	
	public double[][] getCoordinates(){
		return coordinates;
	}
	
	public String[] getVertexLabels(){
		return labels;
	}
	
	public Color[] getVertexColors(){
		return colors;
	}
	
	public int[][] getFacesIndices(){		
		return faceIndices;
	}
	
	private List<Face> getFaces(){
		if (chromatic)
			return chromaticFaces;
		return nonChromaticFaces;
	}
	
	private void setFaceIndices(){
		faceIndices = new int[getFaces().size()][];
		int i=0;
		for (Face f : getFaces()){
			int[] face_idx = new int[f.getVertexCount()];
			int j = 0;
			for (Vertex v : f.getVertices()){
				face_idx[j++]=v.getIndex();
			}
			faceIndices[i++]=face_idx;
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\nGeometry data:\n");
		sb.append("Total vertex count:"+getVertexCount() +"\n");
		sb.append("All coordinates:"+Arrays.deepToString(coordinates) + "\n");
		sb.append("All labels:"+Arrays.deepToString(labels)+"\n");
		sb.append("All colors:"+Arrays.deepToString(colors)+"\n");
		sb.append("All faces:"+Arrays.deepToString(faceIndices)+"\n");
		return sb.toString();
	}
		
}
