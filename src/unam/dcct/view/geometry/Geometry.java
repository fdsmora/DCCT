package unam.dcct.view.geometry;

import java.awt.Color;

public interface Geometry {
	int getVertexCount();
	double[][] getCoordinates();
	String[] getVertexLabels();
	Color[] getVertexColors();
	int[][] getFacesIndices();
}
