package unam.dcct.view.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.jreality.shader.Color;
import unam.dcct.topology.Process;
import unam.dcct.topology.Simplex;
import unam.dcct.topology.SimplicialComplex;

/**
 * Wrapper class that transforms a {@link SimplicialComplex}
 * into its geometric representation. To build the geometric
 * representation of a simplicial complex, it properly builds vertices
 * from the complex's processes and faces from the complex's simplices. 
 * @author Fausto Salazar
 *
 */
public class GeometricComplex implements Geometry{
	private boolean chromatic = true;
	private GeometricComplex parent;

	// Geometric attribute fields
	private int[][] faceIndices; 	
	private double[][] coordinates;
	private Color[] colors;
	private String[] labels;
	private boolean disconnectedFaces;
	/*
	 * Vertices and faces computations are dependent on whether the geometric complex
	 * is chromatic or non-chromatic, so these are managed by these special classes. 
	 */
	private GeometricComplexBehaviour innerGeometricComplexBehaviour;
	private ChromaticGeometricComplexBehaviour chromaticGeometricComplexBehaviour;
	private NonChromaticGeometricComplexBehaviour nonChromaticGeometricComplexBehaviour;
	
	/**
	 * Creates an instance of a geometric complex based on the simplicial complex parameter and
	 * also sets its parent. 
	 * <p>
	 * The parent of a geometric complex is the equivalent parent of the parent
	 * of a simplicial complex. This parameter is only useful for non-chromatic geometric complexes. 
	 * <br>
	 * The parent of a nth-round protocol complex is the (n-1)th-round protocol complex,
	 * or, if round is first, the parent would be the initial complex. 
	 * <br>
	 * We need this notion of parent geometric complex in order to properly create the 
	 * vertices of the geometric complex. If parent == null, then this geometric complex
	 * is considered to be the initial complex.  
	 * @see GeometricComplex#setVertices()
	 * @see GeometricComplex#getAncestorNonChromaticVertexLabels(String)
	 * @see GeometricComplex#nonChromaticSortedVertexLabels
	 * @see GeometricComplex#populateNonChromaticVertexLabels()
	 */
	public GeometricComplex(SimplicialComplex complex, GeometricComplex parent, boolean disconnectedFaces){
		this.parent = parent;
		this.disconnectedFaces = disconnectedFaces;
		chromaticGeometricComplexBehaviour = new ChromaticGeometricComplexBehaviour(complex, disconnectedFaces);
		nonChromaticGeometricComplexBehaviour = new NonChromaticGeometricComplexBehaviour(complex, parent, disconnectedFaces);

		setChromatic(complex.isChromatic());
	}
		
	/**
	 *  Sets all its fundamental properties: vertices, coordinates,
	 *  labels, colors and face indices. 
	 */
	private void setAttributes(){
		setCoordinates();
		setLabels();
		setColors();
		setFaceIndices();
	}
	
	private void setCoordinates() {
		coordinates = new double[getVertexCount()][];
		
		int j = 0;
		for (Vertex v : getVertices()){
			coordinates[j++] = v.getCoordinates(); 
		}		
	}
	
	private void setLabels() {
		labels = new String[getVertexCount()];
		int j = 0;
		for (Vertex v : getVertices()){
			labels[j++]=v.getLabel();
		}		
	}
	
	private void setColors() {
		colors = new Color[getVertexCount()];
		int j = 0;
		for (Vertex v : getVertices()){
			colors[j++]= v.getColor();
		}		
	}
	
	private void setFaceIndices(){
		faceIndices = new int[getFaces().size()][];
		int i=0;
		int k = 0; // used only when disconnectedFaces == true.
		for (Face f : getFaces()){
			int[] face_idx = new int[f.getVertexCount()];
			int j = 0;
			for (Vertex v : f.getVertices()){
				face_idx[j++]= disconnectedFaces ? k++ : v.getIndex();
			}
			faceIndices[i++]=face_idx;
		}
	}

	@Override
	public int getVertexCount() {
		return innerGeometricComplexBehaviour.getVertexCount();
	}
	
	@Override
	public double[][] getCoordinates(){
		return coordinates;
	}
	@Override
	public String[] getVertexLabels(){
		return labels;
	}
	@Override
	public Color[] getVertexColors(){
		return colors;
	}
	@Override
	public int[][] getFacesIndices(){		
		return faceIndices;
	}
	
	public List<Face> getFaces(){
		return innerGeometricComplexBehaviour.getFaces();
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
	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
		innerGeometricComplexBehaviour = chromatic ? 
				chromaticGeometricComplexBehaviour : nonChromaticGeometricComplexBehaviour;
		this.chromatic = chromatic;
		// Need to update geometric attributes as these are computed
		// on the fly
		setAttributes();
	}

	public List<Vertex> getVertices() {
		return innerGeometricComplexBehaviour.getVertices();
	}

	public GeometricComplex getParent() {
		return parent;
	}
	
	/**
	 * Contains common methods for computing vertices and faces.
	 * @author Fausto
	 *
	 */
	private abstract class GeometricComplexBehaviour {
		private List<Face> faces;
//		protected Map<String, Vertex> vertices;
		private List<Vertex> vertices;
		protected SimplicialComplex complex;
		private boolean chromatic;
		private boolean disconnectedFaces;
		
		GeometricComplexBehaviour(SimplicialComplex complex, boolean chromatic, boolean disconnectedFaces){
			this.chromatic = chromatic;
			this.complex = complex;
			this.disconnectedFaces = disconnectedFaces;
		}
	
		protected void setVerticesAndFaces(){
			buildFaces();
			buildVertices();
		}
		
		protected void buildFaces() {
			
			boolean originalChromaticity = complex.isChromatic();
			
			complex.setChromatic(chromatic);
						
			List<Simplex> simplices =  complex.getSimplices();
			if (simplices!=null){
				// Set a Face for each simplex
				faces = new ArrayList<Face>(complex.getSimplexCount());
				for (Simplex s : simplices){
					Simplex parent = s.getParent();
					Face f = new Face(s, parent, chromatic);			
					faces.add(f);
					s.setFace(f);
				}
			}
			
			// Leave it as it was
			complex.setChromatic(originalChromaticity);
		}
		
		/**
		 * It builds the set of vertices by 'gluing' together the 
		 * geometric complex's faces. 
		 */
		protected void buildVertices() {
			
			// Save the original chromaticity state
			boolean originalChromaticity = complex.isChromatic();
			complex.setChromatic(chromatic);
			
			if (disconnectedFaces){
				vertices = new ArrayList<Vertex>();
				for (Face f : getFaces()){
					for (Vertex v : f.getVertices()){
						vertices.add(v);
					}
				}
			}
			else {	
				Map<String, Vertex> verticesMap = new LinkedHashMap<String, Vertex>();
				int indexCount = 0;
				for (Face f : getFaces()){
					for (Vertex v : f.getVertices()){
						String key = getKey(v);
						if(verticesMap.containsKey(key)){
							Vertex dup = verticesMap.get(key);
							v.setIndex(dup.getIndex());
						}
						else {
	//						if (!chromatic) {
	//							System.out.println("Key: " + key + " vertex: " + v.getLabel() );
	//						}
							verticesMap.put(key,v);
							v.setIndex(indexCount++);
						}
					}			
				}
				vertices = new ArrayList<Vertex>(verticesMap.values());
			}
			
			// Leave it as it was
			complex.setChromatic(originalChromaticity);
		}
		
		List<Vertex> getVertices() {
			return vertices;
		}

		List<Face> getFaces() {
			return faces;
		}

		int getVertexCount() {
			return getVertices().size();
		}

		protected String getKey(Vertex v){
			return v.getProcess().toString();
		}
	}
	
	/**
	 * Specialized class that computes vertices and faces for a chromatic geometric complex 
	 * @author Fausto
	 */
	private class ChromaticGeometricComplexBehaviour extends GeometricComplexBehaviour {

		public ChromaticGeometricComplexBehaviour(SimplicialComplex complex, boolean disconnectedFaces) {		
			super(complex, true, disconnectedFaces);
			setVerticesAndFaces();
		}

	}
	
	/**
	 * Specialized class that computes vertices and faces for a non-chromatic geometric complex 
	 * @author Fausto
	 */
	private class NonChromaticGeometricComplexBehaviour extends GeometricComplexBehaviour {

		// See the documentation for the constructor of GeometricComplex to understand this field. 
		private NonChromaticGeometricComplexBehaviour parent;
		
		public NonChromaticGeometricComplexBehaviour(SimplicialComplex complex, GeometricComplex parent, boolean disconnectedFaces) {		
			super(complex, false,disconnectedFaces);
			this.parent = parent!=null ? parent.nonChromaticGeometricComplexBehaviour : null;
			setVerticesAndFaces();
		}
		// See the documentation for the normalizeView() method to understand this field. 
		// The key is the string that represents the original view as it is and the value is the string that represents
		// the sorted view. 
		private Map<String, String> nonChromaticSortedVertexLabels = new HashMap<String, String>();
		
		@Override 
		protected void buildFaces(){
			super.buildFaces();
			if (parent==null)
				populateNonChromaticVertexLabels();
		}
		
		@Override
		protected String getKey(Vertex v) {
			// For initial complexes the normalizeView method should not be called.
			if (parent == null)
				return super.getKey(v);
			return normalizeView(v);
		}
		
		/**
		 * This method should only be called for protocol complexes.
		 * <p>
		 * To 'normalize' a non-chromatic process's view is to sort the views of the other processes 
		 * contained in it's view. This is needed in order to properly create the vertices for non-chromatic complexes. 
		 * <p>
		 * The problem that this method solves is that sometimes the views two or more non-chromatic processes
		 * are semantically the same but are different strings because the views of the last round appear
		 * unsorted, so this caused duplicate vertices in the @link{GeometricComplex#setVertices()} method.
		 * For example, we could have two processes with views: <<0,1>,<0,1,2>> and <<0,1,2>,<0,1>>, which 
		 * really represent the same view, but because the strings are different this generated duplicated vertices.
		 * <p>
		 * In order to solve this I came up with the solution of sorting the contained views inside 
		 * a process view, so sorting the views in previous example we ended up with: <<0,1,2>,<0,1>> for both
		 * views, which is the same string and solves the duplication of vertices problem. 
		 * <p>
		 * There is another problem with this. In another round of execution the views of the new processes
		 * would not persist the already ordered views, so now it would be harder and not efficient to sort the 
		 * inner views again, as there are now more of them and now they are nested in more pairs
		 * of brackets. 
		 * <p>
		 * The solution to this is to save in this map the sorted views for the current complex so that they can
		 * be reused for the complex of the next round (via the {@link GeometricComplex#getParent()} method)
		 * without the need to sort them again. 
		 * @param
		 * @return 
		 * @see GeometricComplex#populateNonChromaticVertexLabels()
		 */
		private String normalizeView(Vertex v){
			Process p = v.getProcess();
			String[] viewArray = p.getViewArray();
			String[] toSortViewArray = new String[viewArray.length]; 
			for (int i =0; i<viewArray.length; i++){
				String sortedView = "";
				if (viewArray[i]!=null) {
					if (parent!=null && parent.nonChromaticSortedVertexLabels!=null)
						sortedView = getAncestorNonChromaticVertexLabels(viewArray[i]);
					else sortedView = viewArray[i];
				}
				toSortViewArray[i] = sortedView;
			}
			Arrays.sort(toSortViewArray);
//			System.out.println("Sorted view: " + Arrays.toString(toSortViewArray));
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (String sortedView : toSortViewArray){
				sb.append(sortedView);
			}
			sb.append(")");
			String strView = sb.toString();
			nonChromaticSortedVertexLabels.put(p.getView(), strView);
			
			return strView;
			
		}
		
		/**
		 * In some communication protocols such as the non-iterated immediate snapshot
		 * we need to reference the information of other upper-level parents (ancestors),
		 * so this method searches for the key in these ancestors. 
		 * @param key The string that represents the label of a vertex in an ancestor
		 * complex. 
		 * @return The key that was found or empty string if not found. 
		 */
		private String getAncestorNonChromaticVertexLabels(String key) {
			NonChromaticGeometricComplexBehaviour ancestor = this.parent;
			
			while (ancestor!=null){
//				System.out.println("Key: " +key);
				String foundKey = ancestor.nonChromaticSortedVertexLabels.get(key);
				if (foundKey!=null)
					return foundKey;
				ancestor=ancestor.parent;
			}
			return "";
		}
		
		/**
		 * This populates the {@link GeometricComplex#nonChromaticSortedVertexLabels} map.
		 * This method is intended to use only for initial complex, where parent==null. 
		 */
		private void populateNonChromaticVertexLabels() {
			List<Vertex> verts = new ArrayList<Vertex>(); 
			for (Face f: getFaces()){
				verts.addAll(f.getVertices());
			}
			for (Vertex v : verts){
				String view = v.getProcess().getView();
				nonChromaticSortedVertexLabels.put(view,view);
			}
		}		
	}
}
