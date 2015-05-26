package dctopology;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimplicialComplex {
	public Set<Simplex> getSimplices() {
		return simplices;
	}

	protected Set<Simplex> simplices;
	protected int dimension;
	
	public SimplicialComplex(Simplex... simplices){
		this.simplices = new LinkedHashSet<Simplex>(Arrays.asList(simplices));						
		computeDimension();
	}
	
	public SimplicialComplex(Set<Simplex> simplices){
		this.simplices = simplices;
		computeDimension();
	}
	
	public int dimension()
	{
		return dimension;
	}
	public int totalDistinctProcesses()
	{
		return dimension+1;
	}
	
	protected void computeDimension(){
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
	
}
