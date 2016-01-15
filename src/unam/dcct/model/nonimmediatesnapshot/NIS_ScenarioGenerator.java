package unam.dcct.model.nonimmediatesnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import unam.dcct.model.ViewMatrixGenerator;

public class NIS_ScenarioGenerator extends ViewMatrixGenerator{

	public static void main(String[] args){
		// Test
		NIS_ScenarioGenerator gen = new NIS_ScenarioGenerator();

		int n = 2;
		List<int[][]> mats = gen.generate(n);
		for (int[][] m : mats){
			System.out.println(Arrays.deepToString(m));
		}
		
		System.out.println("Total matrices:"+ mats.size());
	}
	
	@Override
	protected void generateMatrices(int n) {
		
    	int[][] M = new int[n][n];

		List<List<Integer>> subsetPermutations = generateSubsetPermutations(n, n-1);
		
    	for (List<Integer> permutation : subsetPermutations ){
    		ViewMatrixGenerator.fillWithOnes(M);
    		setNextValue(M, 0, 0, permutation);
    	}
	}
	
	private static List<List<Integer>> generateSubsetPermutations(int n, int k) {
		// Create array containing integers from 0 to n-1
		Integer[] ids = new Integer[n] ; 
		for (int i = 0; i<n; i++){
			ids[i]=i;
		}
		
		// Create the initial vector of n integers
		ICombinatoricsVector<Integer> initialVector = Factory.createVector(ids);

	    // Create the permutation generator by calling the appropriate method in the Factory class
	    Generator<Integer> gen = Factory.createPermutationGenerator(initialVector);

		List<List<Integer>> subsetPermutations = new ArrayList<List<Integer>>();

	    for (ICombinatoricsVector<Integer> perm : gen){
			subsetPermutations.add(perm.getVector().subList(0, k));
		}
		return subsetPermutations;
	}
	
	private void setNextValue(int[][] M, int current, int j, List<Integer> permutation) {
		int n = M.length;
		int i = permutation.get(current);
		int previous = current > 0 ? permutation.get(current-1) : 0;
    	while(true){
    		if (i!=j){
    			M[i][j]=0;
    			if (current > 0 && M[previous][j]==1)
    				M[i][j]=1;
    		}
			while (true){
				if (j+1 < n)
					setNextValue(M, current, j+1, permutation);
				else {
					if (current+1==permutation.size())
						produce(M);
					else
						setNextValue(M, current+1, 0, permutation);
				}
				if (M[i][j]<1){
					++M[i][j];
				}
				else return;
			}
		}
	}
}
