package dcct.combinatorics;

//For now only works for 0>n<10
public class ScenarioGenerator {
	protected static StringBuilder allCombinations;
	protected static char delimiter ='|';
	protected static StringBuilder sb;
    protected static int n=0;
    protected static int S=0;
	
	// TEST
	public static void main(String[] args){
		System.out.println(generate(3));
	}
	
	public static void newBin(int S, int i, int k){
		for (int len = 1 ; i+len<=n; len++){
			for (int j=0; j<n;j++){
				if (SetHelper.inSet(j,S)){
					sb.setCharAt(k, (char)('0'+j));
					S = SetHelper.removeFromSet(j, S);
					if (i+1==n){
						allCombinations.append(sb.toString());
						allCombinations.append("\n");
						return;
					}
					if (len==1){
						sb.setCharAt(k+1, '|');
						newBin(S, i+1, k+2);
					}
					else{
						sb.setCharAt(k+1, ' ');
						int C = SetHelper.B(j,S);
						if (C>0)
							sameBin(S, C, i+1, k+2, len, 1);
					}
					S = SetHelper.addToSet(j, S);
				}
			}
		}
	}
	
	public static void sameBin(int S, int C, int i, int k, int len, int h){
		for (int j=0; j<n; j++){
			if (SetHelper.inSet(j,C)){
				sb.setCharAt(k, (char)('0'+j));
				S = SetHelper.removeFromSet(j, S);
				if (i+1==n){
					allCombinations.append(sb.toString());
					allCombinations.append("\n");
					return;
				}
				if (h+1==len){
					sb.setCharAt(k+1, '|');
					newBin(S, i+1, k+2);
				}
				else{
					sb.setCharAt(k+1, ' ');
					C = SetHelper.B(j,S);
					if (C>0)
						sameBin(S, C, i+1, k+2, len, h+1);
				}
				S = SetHelper.addToSet(j, S);
			}
		}
	}
	
	public static String generate(int m){
		n=m+1; // to get correct number of processes

		int newSize = 2*n-1;
		
		sb = new StringBuilder(newSize);
		for (int i=0;i<newSize;i++)
			sb.append(" ");

		allCombinations=new StringBuilder();
		
		S = SetHelper.createSet(n);
		
		newBin(S, 0, 0);
		
		return allCombinations.toString().replaceAll(" ","");

	}
	
	public static String getDelimiter(){
		return String.valueOf(delimiter);
	}
	
	protected static class SetHelper{
		public static int createSet(int n){
			return (int) (Math.pow(2,n)-1);
		}
		
		public static int B(int e, int S){
			int aux = (1<<(e+1))-1;
			return (S|aux)-aux;
		}
		
		public static int addToSet(int e, int S){
			return S | (1<<e);
		}
		
		public static int removeFromSet(int e, int S){
			return S - (1<<e);
		}
		
		public static boolean inSet(int e, int S){
			return (1 & (S>>e))>0;
		}
	}	

}
