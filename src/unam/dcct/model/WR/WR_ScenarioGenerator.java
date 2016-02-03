package unam.dcct.model.WR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import unam.dcct.model.ViewMatrixGenerator;

public class WR_ScenarioGenerator extends ViewMatrixGenerator {
	
	public static void main(String[] args){
		// Test
		int n = 2;
		WR_ScenarioGenerator gen = new WR_ScenarioGenerator();
		List<int[][]> mats = gen.generate(n);
		for (int[][] m : mats){
			System.out.println(Arrays.deepToString(m));
		}
	}
	
	private static List<List<Integer>> generateCombinations(int n, int k){
		// Create array containing integers from 0 to n-1
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
    	}
    	return combinations;
	}
	
	private void setNextValue(int[][] M, int current, int j, List<Integer> combination) {
		int n = M.length;
		int i = combination.get(current);
    	while(true){
			if (j>i || 
					(j<i && M[j][i]==1))
				M[i][j]=0;
			while (true){
				if (j+1 < n)
					setNextValue(M, current, j+1, combination);
				else {
					if (current+1==combination.size())
						produce(M);
					else
						setNextValue(M, current+1, 0, combination);
				}
				if (M[i][j]<1)
					++M[i][j];
				else return;
			}
		}
	}

	@Override
	protected void generateMatrices(int n) {
		
    	int[][] M = new int[n][n];

		List<List<Integer>> combinations = generateCombinations(n, n-1);
		
    	for (List<Integer> combination : combinations ){
    		fillWithOnes(M);
    		setNextValue(M, 0, 0, combination);
    	}
		
	}
	
}
