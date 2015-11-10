package unam.dcct.view.geometry;

import java.awt.Color;
/**
 * Represents an geometric object that can be drawn, 
 * such as a {@link unam.dcct.view.geometry.GeometricComplex} or a {@link unam.dcct.topology.Simplex}.
 * @author Fausto Salazar
 */
public interface Geometry {
	/**
	 * Returns the number of vertices contained in the Geometry.
	 * @return the number of vertices.
	 */
	int getVertexCount();
	/**
	 * Returns an array that contains the 3D coordinates corresponding
	 * to each vertex in the Geometry. 
	 * @return An array of double arrays.
	 */
	double[][] getCoordinates();
	/**
	 * Returns an array containing the labels of the vertices in the Geometry. 
	 * @return An array of String objects, where each object is a vertex's label. 
	 */
	String[] getVertexLabels();
	/**
	 * Returns an array containing the colors of the vertices in the Geometry. 
	 * @return An array of Color objects, where each object is a vertex's color. 
	 */
	Color[] getVertexColors();
	/**
	 * Returns an array containing arrays that represent face indices.
	 * <p>
	 * To understand what is a 'face indices' suppose that we enumerate 
	 * the vertices contained in a face. Each number assigned to a vertex
	 * is an 'index' of the vertex, so the set of indices corresponding 
	 * to the vertices of a face is a 'face indices'.  
	 * <p>
	 * For example, consider a tetrahedron represented by an instance of this class. It has four vertices and four
	 * faces. A call to this method should return the array [[0,1,2],[1,2,3],[0,1,3],[0,2,3]]. 
	 * Such an array is required for geometric drawing by some math visualization libraries such as jReality. 
	 * @return The array described above. 
	 */
	int[][] getFacesIndices();
//	void setChromatic(boolean chromatic);
//	boolean isChromatic();
}
