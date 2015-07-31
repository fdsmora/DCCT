package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import configuration.Constants;
import dctopology.Process;
import dctopology.Simplex;

public abstract class Face {
	protected Simplex simplex;
	protected double[][] coordinates;
	protected Color[] colors;
	protected String[] labels;
	protected Face parent;
	protected List<Vertex> vertices;
	protected int[][] faceIndices;
		
	Face(Simplex simplex, Simplex pSimplex){
		this.simplex = simplex;
		if (pSimplex !=null) 
			this.parent = pSimplex.getFace();
		setVertices();
		setLabels();
		setColors();
		setCoordinates();
		setFaceIndices();
	}
	
	private void setVertices() {
		vertices = new ArrayList<Vertex>();
		for (Process p : simplex.getProcesses()){
			vertices.add(new Vertex(p));
		}
	}
	
	private void setColors(){
		colors = new Color[simplex.getProcessCount()];
		for (Vertex v:vertices){
			Color c = getColor();
			v.setColor(c);
			colors[v.getProcess().getId()]= c;
		}	
	}
	
	protected abstract Color getColor();

	private void setLabels() {
		labels = new String[simplex.getProcessCount()];
		for (Process p: simplex.getProcesses())
			labels[p.getId()]=p.getView();		
	}
	
	private void setCoordinates() {
		// If this is a Face of an Initial Complex, it's parent is null.
		if (parent==null){
			int i = 0;
			int dimension = simplex.dimension();
			
			for (Vertex v : vertices){
				v.setCoordinates(Constants.DEFAULT_SIMPLEX_VERTEX_COORDINATES[dimension][i++]);
			}
				
			coordinates= Constants.DEFAULT_SIMPLEX_VERTEX_COORDINATES[dimension];
			return;
		}
		
		coordinates = new double[simplex.getProcessCount()][]; 
		for (Vertex v : vertices){
			Process p = v.getProcess();
			double[] vCoords = calculateCoordinates(p);
			v.setCoordinates(vCoords);
			coordinates[p.getId()] = vCoords;
		}
	}
	
	protected abstract double[] calculateCoordinates(Process p);
	
	private void setFaceIndices() {
		faceIndices = new int[1][simplex.getProcessCount()];
		for (Process p : simplex.getProcesses()){
			faceIndices[0][p.getId()] = p.getId();
		}		
	}

	public String[] getVertexLabels(){
		return labels;
	}
	
	public int[][] getFaceIndices(){
		return faceIndices;
	}
	
	public Color[] getVertexColors(){
		return colors;
	}
	
	public int getVertexCount(){
		return vertices.size();
	}

	public Simplex getSimplex() {
		return simplex;
	}

	public List<Vertex> getVertices() {
		return vertices;
	}
	
	public double[][] getCoordinates(){
		return coordinates;
	}

}
