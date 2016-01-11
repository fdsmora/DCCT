package unam.dcct.model.WR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class WR_ScenarioGenerator {
	private static int n=0;
	private static List<int[][]> matrices;
	private static Set<String> uniqueMatrices;
	
	public static void main(String[] args){
		// Test
		List<int[][]> mats = generate(2);
		for (int[][] m : mats){
			System.out.println(Arrays.deepToString(m));
		}
	}

	public static List<int[][]> generate(int dimension){
		n = dimension + 1; // to get correct number of processes
		
    	// Initialize containers of matrices that will be generated
    	uniqueMatrices = new HashSet<String>();
    	matrices = new ArrayList<int[][]>();
    	int[][] M = new int[n][n];
    	
    	if (n<2)
    	{
    		M[0][0]=1;
    		matrices.add(M);
    		return matrices;
    	}

		List<List<Integer>> combinations = generateCombinations(n, n-1);
		

    	for (List<Integer> combination : combinations ){
    		fillWithOnes(M);
    		visitNext(M, 0, 0, combination);
    	}
    	
    	return matrices;
	}
	
	private static List<List<Integer>> generateCombinations(int n, int k){
		Integer[] ids = new Integer[n] ; 
		for (int i = 0; i<n; i++){
			ids[i]=i;
		}
		
    	// Create the initial vector
    	ICombinatoricsVector<Integer> initialVector = Factory.createVector(ids );

    	// Create a simple combination generator to generate k-combinations of the initial vector
    	Generator<Integer> gen = Factory.createSimpleCombinationGenerator(initialVector, k);

    	List<List<Integer>> combinations = new ArrayList<List<Integer>>();
    	// Convert all possible combinations to lists.
    	for (ICombinatoricsVector<Integer> c : gen) {
    		combinations.add(c.getVector());
//    		System.out.println(c.getVector());
    	}
    	return combinations;
	}
	
	private static void fillWithOnes(int[][] M){
    	for (int i = 0; i< M.length; i++)
    		for (int j = 0; j< M.length; j++)
    			M[i][j]=1;
    }
	
	private static void visitNext(int[][] M, int c, int j, List<Integer> combination) {
		int n = M.length;
		int i = combination.get(c);
    	while(true){
			if (j>i || 
					(j<i && M[j][i]==1))
				M[i][j]=0;
			while (true){
				if (j+1 < n)
					visitNext(M, c, j+1, combination);
				else {
					if (c+1==combination.size())
						produce(M);
					else
						visitNext(M, c+1, 0, combination);
				}
				if (M[i][j]<1)
					++M[i][j];
				else return;
			}
		}
	}
	
	private static void produce(int[][] M) {
		String str = Arrays.deepToString(M);
		if (!uniqueMatrices.contains(str)){
			uniqueMatrices.add(str);
//			System.out.println("r:"+ Arrays.deepToString(M.clone()));
			matrices.add(createCopy(M));
		}
//		System.out.println("Hasta ahora:");
//		for (int[][] m : matrices){
//			System.out.println(Arrays.deepToString(m));
//		}
	}

	private static int[][] createCopy(int[][] M) {
		int[][] copy = new int[M.length][];
		
		for (int i =0; i<M.length; i++){
			copy[i]=Arrays.copyOf(M[i], M[i].length);
		}
		return copy;
	}
	
//	private static String toString(int[][] M) {
//		StringBuilder sb = new  StringBuilder();
////		System.out.println();
//		for (int i=0; i< M.length; i++){
////    		System.out.println(Arrays.toString(M[i]));
//    		sb.append(Arrays.toString(M[i]));
//		}
//		return sb.toString();
//	}
}
