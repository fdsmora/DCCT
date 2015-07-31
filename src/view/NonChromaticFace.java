package view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import configuration.Constants;
import dctopology.LinearAlgebraHelper;
import dctopology.Process;
import dctopology.Simplex;

public class NonChromaticFace extends Face {
	
	private Map<String, double[]> coordinatesMap; 
	
	public NonChromaticFace(Simplex simplex, Simplex pSimplex){
		super(simplex, pSimplex);
		setCoordinatesMap();
	}
	
	private void setCoordinatesMap() {
		coordinatesMap = new HashMap<String, double[]>(vertices.size());
		for (Vertex v : vertices){
			coordinatesMap.put(v.getLabel(), v.getCoordinates());
		}
	}
	
	protected double[] calculateCoordinates(Process p) {
		String[] processView = p.getViewArray();
		int count = p.getViewElementsCount();
		NonChromaticFace parentFace = (NonChromaticFace) parent;
				
		double factor = 1.0/count;
		double[] res = {0.0,0.0,0.0};
		double[] pCoords;

		for (int i = 0; i<processView.length; i++){
			if (processView[i]!=null){
				pCoords = parentFace.coordinatesMap.get(processView[i]);
//			}else
//				pCoords = new double[3];		
				res = LinearAlgebraHelper.vectorSum(
						LinearAlgebraHelper.scalarVectorMultiply(factor, pCoords),res);
			}
		}
		return res;
	}
	
	protected Color getColor(){
		return Constants.DEFAULT_NON_CHROMATIC_COLOR;
	}
	
}
