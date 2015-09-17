package unam.dcct.view.geometry;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import unam.dcct.misc.Constants;
import unam.dcct.misc.LinearAlgebraHelper;
import unam.dcct.model.Model;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;

public class Face {
	private Simplex simplex;
	private double[][] coordinates;
	private Color[] colors;
	private String[] labels;
	private Face parent;
	private List<Vertex> vertices;
	private int[][] faceIndices;
	private ChromaticityBehaviour chromaticityBehaviour;
		
	public Face(Simplex simplex, Simplex pSimplex, boolean chromatic){
		this.simplex = simplex;
		if (pSimplex !=null) 
			this.parent = pSimplex.getFace();
		setVertices();
		this.chromaticityBehaviour = chromatic? new ChromaticBehaviour(this) :
									new NonChromaticBehaviour(this); 
		setVerticesAttributes();
	}
	
	public void setVertices(){
		vertices = new ArrayList<Vertex>(simplex.getProcessCount());
		for (Process p : simplex.getProcesses()){
			Vertex v = new Vertex(p);
			vertices.add(v);
		}
	}
	
	private void setVerticesAttributes() {

		colors = new Color[simplex.getProcessCount()];
		faceIndices = new int[1][simplex.getProcessCount()];
		labels = new String[simplex.getProcessCount()];
		coordinates = new double[simplex.getProcessCount()][]; 
		
		for (Vertex v: vertices){
			int pid = v.getProcess().getId();
			
			// Set colors
			Color c = getColor();
			v.setColor(c);
			colors[pid]= c;
			
			// Set labels 
			labels[pid]=v.getLabel();	
			
			// Set face indices
			faceIndices[0][pid] = pid;
			
			// Set coordinates
			calculateCoordinatesPerVertex(v);
			coordinates[pid] = v.getCoordinates();			
		}
	}
	
	private void calculateCoordinatesPerVertex(Vertex v){
		Process p = v.getProcess();
		if (parent == null){
			v.setCoordinates(Constants.DEFAULT_SIMPLEX_VERTEX_COORDINATES[simplex.dimension()][p.getId()]);
		}else {
			v.setCoordinates(chromaticityBehaviour.calculateCoordinatesPerProcess(p));
		}
	}

	private Color getColor(){
		return chromaticityBehaviour.getColor();
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

	public Face getParent() {
		return parent;
	}
	
	private interface ChromaticityBehaviour {
		double[] calculateCoordinatesPerProcess(Process p);
		Color getColor();
	}

	private class ChromaticBehaviour implements ChromaticityBehaviour {

		private Queue<Color> qColors;
		private Face face;
		
		private ChromaticBehaviour(Face face){
			this.face = face;
		}

		@Override
		public double[] calculateCoordinatesPerProcess(Process p) {
			String[] processView = p.getViewArray();
			int count = p.getViewElementsCount();
			int pid = p.getId();
			
			// If process only saw himself during communication round.
			if (count == 1)
				return parent.getCoordinates()[pid];
			
			final float EPSILON = Constants.EPSILON_DEFAULT  ;
			double smallFactor = (1-EPSILON)/count;
			double bigFactor = (1+(EPSILON/(count==3?2:1)))/count;
			double[] res = {0.0,0.0,0.0};
			Face parent = face.getParent();
			
			for (int i = 0; i<processView.length; i++){
				if (i==pid)
					res = LinearAlgebraHelper.vectorSum(
							LinearAlgebraHelper.scalarVectorMultiply(smallFactor,parent.getCoordinates()[pid])
							,res);
				else if (processView[i]!=null){
					double[] coords =  parent.getCoordinates()[i];
															
					res = LinearAlgebraHelper.vectorSum(
							LinearAlgebraHelper.scalarVectorMultiply(bigFactor, coords),res);
				}
			}
			return res;
		}

		@Override
		public Color getColor(){
			if (qColors== null)
				qColors = new LinkedList<Color>(Model.getInstance().getColors());
			return qColors.remove();
		}

	}
	
	private class NonChromaticBehaviour implements ChromaticityBehaviour {

		private Map<String, Vertex> coordinatesMap; 
		protected Face face;
		
		private NonChromaticBehaviour(Face face){
			this.face = face;
			setCoordinatesMap();
		}
		
		private void setCoordinatesMap() {
			List<Vertex> vertices = face.vertices;
			coordinatesMap = new HashMap<String, Vertex>(vertices.size());
			for (Vertex v : vertices){
				coordinatesMap.put(v.getLabel(), v);
			}
		}
		
		@Override
		public double[] calculateCoordinatesPerProcess(Process p) throws ClassCastException{
			String[] processView = p.getViewArray();
			int count = p.getViewElementsCount();
			
			NonChromaticBehaviour parentNcBehaviour = null;
			try {
				parentNcBehaviour = (NonChromaticBehaviour)parent.chromaticityBehaviour;
			} catch (ClassCastException e) {
				throw new ClassCastException("The parent's face chromaticity behaviour must be non-chromatic ");
			}
			
			double factor = 1.0/count;
			double[] res = {0.0,0.0,0.0};
			double[] pCoords;

			for (int i = 0; i<processView.length; i++){
				if (processView[i]!=null){
					pCoords = parentNcBehaviour.coordinatesMap.get(processView[i]).getCoordinates();
//			
					res = LinearAlgebraHelper.vectorSum(
							LinearAlgebraHelper.scalarVectorMultiply(factor, pCoords),res);
				}
			}
			return res;
		}

		@Override
		public Color getColor(){
			return Constants.DEFAULT_NON_CHROMATIC_COLOR;
		}

	}

}
