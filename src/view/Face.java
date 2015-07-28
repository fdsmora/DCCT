package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.Model;
import configuration.Constants;
import dctopology.LinearAlgebraHelper;
import dctopology.Process;
import dctopology.Simplex;

public class Face {
	private Simplex simplex;
	private double[][] coordinates;
	private Color[] colors;
	private String[] labels;
	private Face parent;
	private List<Vertex> vertices;
	private int[][] faceIndices;
	
	public Face(Simplex simplex){
		this(simplex,null);
	}
	
	public Face(Simplex simplex, Face parent){
		this.simplex = simplex;
		this.parent = parent;
		setVertices();
		setFaceIndices();
	}
	
	private void setVertices() {
		vertices = new ArrayList<Vertex>();
		for (Process p : simplex.getProcesses()){
			vertices.add(new Vertex(p));
		}
		setCoordinates();
		setLabels();
		setColors();
	}
	
	private void setColors() {
		colors = new Color[simplex.getProcessCount()];
		Queue<Color> qColors = new LinkedList<Color>(Model.getInstance().getColors());
		for (Vertex v:vertices){
			Process p = v.getProcess();
			Color c = qColors.remove();
			v.setColor(c);
			colors[p.getId()]=c;
		}		
	}

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
		boolean chromatic = simplex.isChromatic();
		coordinates = new double[simplex.getProcessCount()][]; 
		if (chromatic)
			for (Vertex v : vertices){
				Process p = v.getProcess();
				v.setCoordinates(calculateChromaticCoordinates(p));
				coordinates[p.getId()] = v.getCoordinates();
			}
	}
	
	private double[] calculateChromaticCoordinates(Process p) {
		String[] processView = p.getViewArray();
		int count = p.getViewElementsCount();
		int pid = p.getId();
		
		// If process only saw himself during communication round.
		if (count == 1)
			return parent.getCoordinates()[pid];
		
		final float EPSILON = Constants.EPSILON_DEFAULT;
		
		double smallFactor = (1-EPSILON)/count;
		double bigFactor = (1+(EPSILON/(count==3?2:1)))/count;
		double[] res = {0.0,0.0,0.0};
		
		for (int i = 0; i<simplex.getProcessCount(); i++){
			if (i==pid)
				res = LinearAlgebraHelper.vectorSum(
						LinearAlgebraHelper.scalarVectorMultiply(smallFactor,parent.getCoordinates()[pid])
						,res);
			else {
				double[] coords = (processView[i]==null? 
						new double[3] : parent.getCoordinates()[i]);
														
				res = LinearAlgebraHelper.vectorSum(
						LinearAlgebraHelper.scalarVectorMultiply(bigFactor, coords),res);
			}
		}
		return res;
	}
	
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
