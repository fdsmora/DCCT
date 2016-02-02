package unam.dcct.view.geometry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.jreality.shader.Color;
import unam.dcct.misc.Configuration;
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

		this.chromaticityBehaviour = chromatic? new ChromaticBehaviour() :
			new NonChromaticBehaviour(); 
		setAttributes();
	}
	
	/**
	 * Builds a list of vertices that correspond to the list of processes to the associated simplex. 
	 */
	private void setVertices(){
		vertices = new ArrayList<Vertex>(simplex.getProcessCount());
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
		return vertices;
	}

	public Face getParent() {
		return parent;
	}
	/**
	 * In some communication protocols such as the non-iterated immediate snapshot
	 * we need to reference the information of other upper-level parents (ancestors),
	 * so this method searches for the key in these ancestors. 
	 * @param key The string that represents the label of a vertex in an ancestor
	 * complex. 
	 * @param levels The number of levels of depth in the hierarchy of ancestors to search into.  
	 * @return The coordinates of the vertex whose label was found or empty null if not found. 
	 */
	private double[] locateAncestorCoordinates(String key, Integer levels){
		Face ancestor = this.parent;
		Vertex x = null;
		int count = 0;
		
		while(ancestor!=null){ 
			if (levels!=null){
				if (count<levels)
					count++;
				else break;
			}
			x = ancestor.verticesMap.get(key);
			if (x!=null)
				return x.getCoordinates(); 

			ancestor = ancestor.parent; // Now search in the next parent.
		}		
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
				return locateAncestorCoordinates(key, null)
						.clone(); // Need to clone in order to fix a bug that happens when disconnected faces mode is on.
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
		

		@Override
		public double[] calculateCoordinatesPerProcess(Process p) throws ClassCastException{
			String[] processView = p.getViewArray();
			int count = p.getViewElementsCount();
			
			double factor = 1.0/count;
			double[] res = {0.0,0.0,0.0};

			int len = processView.length;
			for (int i = 0; i<len; i++){
				double[] pCoords = null;
				if (processView[i]!=null){
					
					pCoords = locateAncestorCoordinates(processView[i], 1);
					if (pCoords !=null)
						res = LinearAlgebraHelper.vectorSum(
								LinearAlgebraHelper.scalarVectorMultiply(factor, pCoords),res);

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
