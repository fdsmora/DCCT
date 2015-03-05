package dcct.combinatorics;
//For now only works for 0>n<10
public class ScenarioGenerator {
	protected static StringBuilder allCombinations = new StringBuilder();
	protected static String delimiter ="|";
	
	// TEST
	public static void main(String[] args){
		generate(4);
		System.out.println(allCombinations.toString());
	}
	
	public static String generate(int n){
		++n; // to get correct number of processes
		String base = buildBaseCombination(n);
		putCombination(base);
		generate(base,-1);
		return allCombinations.toString().replaceAll(" ","");
	}
	
	protected static String buildBaseCombination(int n){
		int newSize = 2*n-1;
		StringBuilder combination = new StringBuilder(newSize);
		for (int i=1, offset=1;i<=newSize;i++){
	        if (i%2==0)
	        	combination.append(delimiter);
	        else
	        	combination.append(i-offset++);
		}
		return combination.toString();
	}
	
	protected static void generate(String original, int s){
		int n=original.length();
		for (int i=s; i<n-3; i+=2){
			StringBuilder P = new StringBuilder(original);
			int j = i+2;
			P.setCharAt(j, ' ');
			putCombination(P.toString());
			for (j=j+2;j<n-1;j+=2){
	            if (j<n-3)
	            	generate(P.toString(), j);
	            P.setCharAt(j, ' ');
	            putCombination(P.toString());
			}
		}
	}
	
	protected static void putCombination(String P)
	{
		allCombinations.append(P);
		allCombinations.append("\n");
	}
	
	public static String getDelimiter(){ return delimiter;}
}
