package view;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import configuration.Constants;
import model.Model;
import dctopology.LinearAlgebraHelper;
import dctopology.Process;
import dctopology.Simplex;

public class ChromaticFace extends Face {

	private Queue<Color> qColors;
	
	public ChromaticFace(Simplex simplex, Simplex pSimplex){
		super(simplex, pSimplex);
	}
	
	protected double[] calculateCoordinates(Process p) {
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
	
	protected Color getColor(){
		if (qColors== null)
			qColors = new LinkedList<Color>(Model.getInstance().getColors());
		return qColors.remove();
	}

}
