package unam.dcct.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ViewMatrixGenerator {

	protected List<int[][]> matrices;
	protected Set<String> uniqueMatrices;
	
	public List<int[][]> generate(int dimension){
		int n = dimension + 1; // to get correct number of processes
		
    	// Initialize containers of matrices that will be generated
    	uniqueMatrices = new HashSet<String>();
    	matrices = new ArrayList<int[][]>();
    	
    	if (n<2)
    	{
        	int[][] M = new int[n][n];
    		M[0][0]=1;
    		matrices.add(M);
    		return matrices;
    	}
    	
    	generateMatrices(n);
    	
    	return matrices;
    	
	}
	
	protected abstract void generateMatrices(int n);

	protected void produce(int[][] M) {
		String str = Arrays.deepToString(M);
		if (!uniqueMatrices.contains(str)){
			uniqueMatrices.add(str);
//			System.out.println("r:"+ Arrays.deepToString(M.clone()));
			matrices.add(createCopy(M));
		}
	}
	
	public static void fillWithOnes(int[][] M){
    	for (int i = 0; i< M.length; i++)
    		for (int j = 0; j< M.length; j++)
    			M[i][j]=1;
    }

	public static int[][] createCopy(int[][] M) {
		int[][] copy = new int[M.length][];
		
		for (int i =0; i<M.length; i++){
			copy[i]=Arrays.copyOf(M[i], M[i].length);
		}
		return copy;
	}
}
