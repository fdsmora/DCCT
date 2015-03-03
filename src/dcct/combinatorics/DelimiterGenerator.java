package dcct.combinatorics;

public class DelimiterGenerator {
	
	private static StringBuilder allCombinations = new StringBuilder();
	
	public static void main(String[] args){
		generate(6);
		System.out.println(allCombinations.toString());
	}
	
	public static void generate(int n){
		String base = buildBaseCombination(n);//"|a|b|c|d|e|f|g|";
		putCombination(base);
		generate(base,0);
	}
	
	protected static String buildBaseCombination(int n){
		int newSize = 2*n+1;
		StringBuilder combination = new StringBuilder(newSize);
		for (int i=0, offset=0;i<newSize;i++){
	        if (i%2==0)
	        	combination.append('|');
	        else
	        	combination.append(i-offset++);
		}
		return combination.toString();
	}
	
	protected static void generate(String original, int s){
		int n=original.length();
		for (int i=s; i<n-4; i+=2){
			StringBuilder P = new StringBuilder(original);
			int j = i+2;
			P.setCharAt(j, ' ');
			putCombination(P.toString());
			for (j=j+2;j<n-1;j+=2){
	            if (j<n-4)
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
	
}


