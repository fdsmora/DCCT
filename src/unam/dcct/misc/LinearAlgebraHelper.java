package unam.dcct.misc;

public class LinearAlgebraHelper {
	public static double[] scalarVectorMultiply(double scalar, double[] vector){
		double[] res =vector.clone();
		for (int i = 0; i<vector.length; i++){
			res[i]*=scalar;
		}
		return res;
	}
	
	public static double[] vectorSum(double[] vector1, double[] vector2){
		double[] res = new double[vector1.length];
		for (int i = 0; i<vector1.length; i++){
			res[i]=vector1[i]+vector2[i];
		}
		return res;
	}
}
