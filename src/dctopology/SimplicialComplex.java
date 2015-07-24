package dctopology;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimplicialComplex {
	public List<Simplex> getSimplices() {
		return simplices;
	}

	private List<Simplex> simplices;
	private int dimension;
	private boolean chromatic = true;
	private int totalProcessCount = 0;
	
	public SimplicialComplex(Simplex... simplices){
		this(Arrays.asList(simplices));
	}
	
	public SimplicialComplex(List<Simplex> simplices){
		this.simplices = simplices;
		computeDimension();
		computeTotalProcessCount();
	}

	public int dimension()
	{
		return dimension;
	}
	public int totalDistinctProcesses()
	{
		return dimension+1;
	}
	private void computeTotalProcessCount(){
		for (Simplex s: simplices)
			totalProcessCount+= s.getProcessCount();
	}
	
	private void computeDimension(){
		// Get the maximum dimension of this complex's containing simplices. 
		this.dimension = Collections.max(this.simplices,
				new Comparator<Simplex>(){
					public int compare(Simplex s, Simplex t){
						return Integer.compare(s.dimension(),t.dimension());
					}}).dimension();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		sb.append("{");
		for (Simplex s : this.simplices){
			sb.append(prefix);
			sb.append(s.toString());
			prefix = ",";
		}
		sb.append("}");
		
		return sb.toString();
	}

	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
		this.chromatic = chromatic;
		for (Simplex s : simplices){
			s.setChromatic(chromatic);
		}
	}

	public int getTotalProcessCount() {
		return totalProcessCount;
	}
	
	public int getSimplexCount(){
		return simplices.size();
	}
	
}
