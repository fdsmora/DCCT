package unam.dcct.view.geometry;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import unam.dcct.misc.Configuration;
import unam.dcct.misc.Constants;
import unam.dcct.misc.LinearAlgebraHelper;
import unam.dcct.model.Model;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;
/**
 * Represents a face that is part of a geometric complex.
 * <p>
 * A Face can have a chromatic representation or a non-chromatic
 * representation. This determines how the coordinates of its
 * containing vertices are computed and how their colors are set.
 * <p>
 * 
 * @author Fausto Salazar
 * @see Geometry
 * @see GeometricComplex
 * @see Vertex
 */
public class Face implements Geometry {
	private Simplex simplex;
	private double[][] coordinates;
	private Color[] colors;
	private String[] labels;
	private Face parent;
	private List<Vertex> vertices; // Vertices contained in this face
	private int[][] faceIndices;
	private boolean chromatic;
	private Configuration config = Configuration.getInstance();
	/** 
	 * This property represents an object that is responsible for
	 * calculating the coordinates of the vertices contained in the
	 * face and setting their colors. This is based on the 'Strategy'
	 * design pattern. 
	 */
	private ChromaticityBehaviour chromaticityBehaviour;
	
	private Map<String, Vertex> verticesMap; 
		
	public Face(Simplex simplex, Simplex parent, boolean chromatic){
		this.simplex = simplex;
		if (parent !=null) 
			this.parent = parent.getFace();
		this.chromatic = chromatic;
		setVertices();
		
//		this.chromaticityBehaviour = chromatic? new ChromaticBehaviour(this) :
//									new NonChromaticBehaviour(this); 
		this.chromaticityBehaviour = chromatic? new ChromaticBehaviour() :
			new NonChromaticBehaviour(); 
		setAttributes();
	}
	
	/**
	 * Builds a list of vertices that correspond to the list of processes to the associated simplex. 
	 */
	private void setVertices(){
		vertices = new ArrayList<Vertex>(simplex.getProcessCount());
//		for (Process p : simplex.getProcesses()){
//			Vertex v = new Vertex(p);
//			vertices.add(v);
//		}
		verticesMap = new LinkedHashMap<String, Vertex>(simplex.getProcessCount());
		for (Process p : simplex.getProcesses()){
			Vertex v = new Vertex(p);
			String key = chromatic ? v.getProcess().toString() : v.getProcess().getView();
			verticesMap.put(key, v);
			vertices.add(v);
		}
	}
	
	private void setAttributes() {

		colors = new Color[simplex.getProcessCount()];
		faceIndices = new int[1][simplex.getProcessCount()];
		labels = new String[simplex.getProcessCount()];
		coordinates = new double[simplex.getProcessCount()][]; 
		
		for (Vertex v: getVertices()){
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
			v.setCoordinates(config.DEFAULT_SIMPLEX_VERTEX_COORDINATES[simplex.dimension()][p.getId()]);
		}else {
			v.setCoordinates(chromaticityBehaviour.calculateCoordinatesPerProcess(p));
		}
	}

	private Color getColor(){
		return chromaticityBehaviour.getColor();
	}
	@Override
	public String[] getVertexLabels(){
		return labels;
	}
	
	@Override
	public int[][] getFacesIndices() {
		return faceIndices;
	}

	@Override
	public Color[] getVertexColors(){
		return colors;
	}
	@Override
	public int getVertexCount(){
		return getVertices().size();
	}
	@Override
	public double[][] getCoordinates(){
		return coordinates;
	}

	public boolean isChromatic() {
		return chromatic;
	}

	public Simplex getSimplex() {
		return simplex;
	}

	public List<Vertex> getVertices() {
//		return new ArrayList<Vertex>(verticesMap.values());
		return vertices;
	}

	public Face getParent() {
		return parent;
	}
	
	private double[] locateAncestorCoordinates(String key, Integer levels){
		Face ancestor = this.parent;
		Vertex x = null;
		int count = 0;
		while(ancestor!=null && 
				(x = ancestor.verticesMap.get(key))==null){
			if (levels!=null){
				if (count<levels)
					count++;
				else break;
			}
			ancestor = ancestor.parent; // Now search in the next parent.
		}
		if (x!=null) // vertex found
		{
			System.out.printf("%s found!\n", key);
			return x.getCoordinates();
		}
		System.out.printf("%s NOT found!\n", key);
		
		return null;
	}

	/**
	 * Interface to an object that is responsible for 
	 * calculating the face's vertices coordinates and
	 * setting their colors. 
	 * @author Fausto Salazar
	 *
	 */
	private interface ChromaticityBehaviour {
		double[] calculateCoordinatesPerProcess(Process p);
		Color getColor();
	}

	/**
	 * Represents an object that is responsible for calculating
	 * the coordinates of a chromatic face's vertices and also
	 * setting their colors. To understand the formulas that calculate the 
	 * vertices coordinates, refer to section 5.5.3 of my thesis. 
	 * @author Fausto Salazar
	
	 */
	private class ChromaticBehaviour implements ChromaticityBehaviour {

		private Queue<Color> qColors;
//		private Face face;
//		
//		private ChromaticBehaviour(Face face){
//			this.face = face;
//		}

		@Override
		public double[] calculateCoordinatesPerProcess(Process p) {
			String[] processView = p.getViewArray();
			int count = p.getViewElementsCount();
			int pid = p.getId();
			
			// If process only saw himself during communication round 
			// use the coordinates of the vertex that represented it 
			// in the complex of the previous round. 
			if (count == 1){
				String key = String.format(Process.STR_FORMAT, pid, processView[pid]);
				return locateAncestorCoordinates(key, null);
//				return parent.getCoordinates()[pid];
			}
			
			final float EPSILON = Configuration.getInstance().EPSILON_VALUE  ;
			double smallFactor = (1-EPSILON)/count;
			double bigFactor = (1+(EPSILON/(count-1)))/count;
			double[] res = {0.0,0.0,0.0};
			
			for (int i = 0; i<processView.length; i++){
				// temp
				if (processView[i]!=null){
					double[] pCoords = null;// = {0.0,0.0,0.0};
					String key = String.format(Process.STR_FORMAT, i, processView[i]);
					pCoords = locateAncestorCoordinates(key, null);
					if (pCoords!=null) {
						if (i==pid)
							res = LinearAlgebraHelper.vectorSum(
									LinearAlgebraHelper.scalarVectorMultiply(smallFactor,pCoords)
									,res);
						else
							res = LinearAlgebraHelper.vectorSum(
									LinearAlgebraHelper.scalarVectorMultiply(bigFactor, pCoords)
									,res);
					}
				}
				
//				if (i==pid)
//					res = LinearAlgebraHelper.vectorSum(
//							LinearAlgebraHelper.scalarVectorMultiply(smallFactor,parent.getCoordinates()[pid])
//							,res);
//				else if (processView[i]!=null){
//					double[] coords =  parent.getCoordinates()[i];
//															
//					res = LinearAlgebraHelper.vectorSum(
//							LinearAlgebraHelper.scalarVectorMultiply(bigFactor, coords),res);
//				}
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
	/**
	 * Represents an object that is responsible for calculating
	 * the coordinates of a non-chromatic face's vertices and also
	 * setting their colors. To understand the formulas that calculate the 
	 * vertices coordinates, refer to section 5.5.3 of my thesis. 
	 * @author Fausto Salazar
	 *
	 */
	private class NonChromaticBehaviour implements ChromaticityBehaviour {

//		private Map<String, Vertex> coordinatesMap; 
//		protected Face face;
//		
//		private NonChromaticBehaviour(Face face){
//			this.face = face;
//			setCoordinatesMap();
//		}
		
		/**
		 * In the non-chromatic representation of a face
		 * vertices are referenced by process's views, not by process's id's
		 * like in the chromatic representation, so we need to build
		 * a map to support this kind of referencing schema. 
		 */
//		private void setCoordinatesMap() {
//			List<Vertex> vertices = face.vertices;
//			coordinatesMap = new HashMap<String, Vertex>(vertices.size());
//			for (Vertex v : vertices){
//				coordinatesMap.put(v.getLabel(), v);
//			}
//		}
		
//		private double[] getCoordinates(String processView){
//			Vertex v = coordinatesMap.get(processView);
//			if (v!=null)
//				return v.getCoordinates();
//			return null;
//		}
		

		@Override
		public double[] calculateCoordinatesPerProcess(Process p) throws ClassCastException{
			String[] processView = p.getViewArray();
			int count = p.getViewElementsCount();
			
//			NonChromaticBehaviour parentNcBehaviour = null;
//			try {
//				parentNcBehaviour = (NonChromaticBehaviour)parent.chromaticityBehaviour;
//			} catch (ClassCastException e) {
//				throw new ClassCastException("The parent's face chromaticity behaviour must be non-chromatic ");
//			}
			
			double factor = 1.0/count;
			double[] res = {0.0,0.0,0.0};

			int len = processView.length;
			for (int i = 0; i<len; i++){
				double[] pCoords = null;
				if (processView[i]!=null){
//					pCoords = parentNcBehaviour.getCoordinates(processView[i]);
					// Temp
//					Vertex x = verticesMap.get(processView[i]);
					
					pCoords = locateAncestorCoordinates(processView[i], 1);
					if (pCoords !=null)
						res = LinearAlgebraHelper.vectorSum(
								LinearAlgebraHelper.scalarVectorMultiply(factor, pCoords),res);
					// The case when pCoords is null happens when:
					// 1) the protocol is non-iterated immediate snapshot 
					// 2) non-chromatic complex
					// 3) three processes
					// 4) 2nd round of execution
					// 5) When the simplex is dimension 1 (edge)
//					if (pCoords==null) continue;
				}

			}
			return res;
		}

		@Override
		public Color getColor(){
			return Model.getInstance().getNonChromaticColor();
		}

	}
}
