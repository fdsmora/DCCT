package unam.dcct.model.immediatesnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates all possible partitions of an \(n\) cardinality set \({0,1,...,n-1}\). 
 * The partitions are produced in a string format where each block in the partition
 * is delimited by the "|" character. 
 * @author Fausto Salazar
 *
 */
public class PartitionGenerator {
	private static int n=0;
	private static String delimiter = "|";
	
	public static void main(String[] args){
		// Test
		String all = generate(3);
		System.out.println(all);
	}
	
	public static String generate(int dimension){
		n = dimension + 1; // to get correct number of processes
		List<int[]> allRGF = generateAllRGF();
		
		String allPartitions = generateOrderedPartitions(allRGF);
		//System.out.print( all);
		return allPartitions;
	}
	
	private static int getMax(int[] array){
		int max = array[0];
		for (int j=1; j<n; j++){
			if (max < array[j])
				max = array[j];
		}
		return max;
	}
	
	private static void initPartition(int k, List<StringBuilder> p){
		for (int i=0; i<k; i++){
			p.add(new StringBuilder());
		}
	}
	
	/**
	 * 	 This algorithm generates all partitions of the set {0,1,..,n-1} 
	 *	 coded in restricted growth functions. This method is an implementation
	 *	 of the algorithm described in D. Stanton and D. White's Constructive Combinatorics, 1986. 
	 * @return
	 */

	private static List<int[]> generateAllRGF(){
		List<int[]> allRGF = new ArrayList<int[]>();
		int[] M = new int[n];
		int[] V = new int[n];
		for (int i=0; i<n; i++){
			V[i]=1;
			M[i]=2;
		}
		int j = 0;
		while (true){
			allRGF.add(V.clone());
			j=n-1;
			while (V[j]==M[j]) j--;
			if (j>0){
				V[j]++;
				for (int i=j+1; i<n; i++){
					V[i]=1;
					if (V[j]==M[j])
						M[i]=M[j]+1;
					else 
						M[i]=M[j];
				}
			}else
				return allRGF;
		}
	}
	
	/**
	 * This algorithm is an implementation of the algorithm described in  D. Stanton and D. White's Constructive Combinatorics, 1986. 
	 * @param allRGF
	 * @return
	 */
	private static String generateOrderedPartitions(List<int[]> allRGF){
		StringBuilder allPartitions = new StringBuilder();
		
		for (int[] rgf : allRGF){
			System.out.println(Arrays.toString(rgf));
			int k = getMax(rgf);
			List<StringBuilder> partition = new ArrayList<StringBuilder>(k);
			initPartition(k, partition);
			for (int i=0; i< n; i++){
				partition.get(rgf[i]-1).append(Integer.toString(i));
			}

			generatePartitionPermutations(partition, allPartitions, k);
		}
		
		return allPartitions.toString();
	}
	
	private static void generatePartitionPermutations(List<StringBuilder> partition, StringBuilder block, int k){
		PermutationGenerator permutations = new PermutationGenerator(k);
		
		for (List<Integer> p : permutations){
			List<StringBuilder> orderedPartition=new ArrayList<StringBuilder>(k);
			for (int i : p){
				orderedPartition.add(partition.get(i-1));
			}
			add(orderedPartition, block);
		}
	}
	
	private static void add(List<StringBuilder> lista, StringBuilder sb){
		if (sb==null)
			throw new IllegalArgumentException("No string builder initialized");
		String delim = "";
		for(StringBuilder s : lista){
			sb.append(delim);
			sb.append(s);
			delim = delimiter;
		}
		sb.append("\n");
	}
	
	public static String getDelimiter(){
		return delimiter;
	}
}
