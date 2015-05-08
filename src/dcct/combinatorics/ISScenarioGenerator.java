package dcct.combinatorics;
import dcct.combinatorics.CSet;

/**
 * (Immediate Snapshot Scenario Generator) Generates all encodings of scenarios of execution (histories) for n &lt; 10 processes writing/reading atomic immediate 
 * snapshot shared memory. Each scenario is composed of groups of indices of processes that will 
 * communicate concurrently. Each group is delimited by the delimiter character. All generated scenarios are all permutations
 * of groups of all sizes and all combinations of different process indices. Each distinct group contains distinct combinations
 * of process indices.
 * @author Fausto
 *
 */
public class ISScenarioGenerator {
	protected StringBuilder allScenarios;
	protected char delimiter ='|';
	protected StringBuilder scenario;
    protected int n=0;

	// TEST
	public static void main(String[] args){
		ISScenarioGenerator sg = new ISScenarioGenerator();
		System.out.println(sg.generate(3));
	}
	
	/**
	 * Generates all scenarios. 
	 * @param S The binary representation of the set that contains the indices of the n processes. The first process index 
	 * to be added to a new group is taken from here. Subsequent process indices to be added to the same group will be taken 
	 * from C. 
	 * @param C An auxiliary set that holds all process indices allowed to be added into the current group so that it holds only valid 
	 * process index combinations. 
	 * @param first The first position of the first process's index in the group. 
	 * @param k An auxiliary index into the current position of the scenario StringBuilder instance where the 
	 * scenario encoding is written. 
	 * @param originalLen Keeps track of the length of the current group being built in the current call to this method. 
	 * @param h Keeps track of the position of the next process index that will be added into the current group being built. 
	 */
	public void generateAllScenarios(CSet S, CSet C,int first, int k, int originalLen, int h){
		CSet R = S;
		if (h>0)
			R = C;
		h++;
		for (int len = originalLen==0? 1: originalLen ; first+len<=n; len++){
			for (int j : R){
				scenario.setCharAt(k, (char)('0'+j));
				S.remove(j);
				//if (first+h==n){
				if (S.cardinality()==0){
					allScenarios.append(scenario.toString());
					allScenarios.append("\n");
					return;
				}
				if (h==len){
					scenario.setCharAt(k+1, this.delimiter);
					generateAllScenarios(new CSet(S), C, first+h, k+2, 0, 0);
				}
				else{
					scenario.setCharAt(k+1, ' ');
					C = R.B(j);
					if (C.cardinality()>0)
						generateAllScenarios(new CSet(S), C, first, k+2, len, h);
				}
				S.add(j);
			}
			if (h>1) return;
		}
	}
	/**
	 * Initializes all variables required to compute all execution scenarios and calls generateAllScenarios.
	 * @param dimension of the simplicial complex. dimension + 1 is the max number of processes per simplex in the complex. 
	 * @return A string containing all encoded execution scenarios. 
	 */
	public String generate(int dimension){
		n=dimension+1; // to get correct number of processes

		int newSize = 2*n-1;
		
		scenario = new StringBuilder(newSize);
		for (int i=0;i<newSize;i++)
			scenario.append(" ");

		allScenarios=new StringBuilder();
		
		CSet S = new CSet(n);
		
		generateAllScenarios(S, null, 0, 0, 0, 0);
		
		return allScenarios.toString().replaceAll(" ","");

	}
	
	public char getDelimiter(){
		return delimiter;
	}
	public void setDelimiter(char d){
		delimiter= d;
	}

}
