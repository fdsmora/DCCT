package dctopology;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SimplicialComplex {
	public List<Simplex> getSimplices() {
		return simplices;
	}

	protected List<Simplex> simplices;
	protected int dimension;
	protected boolean chromatic = true;
	
	public SimplicialComplex(Simplex... simplices){
		//this.simplices = new LinkedHashSet<Simplex>(Arrays.asList(simplices));
		this.simplices = Arrays.asList(simplices);
		computeDimension();
	}
	
	public SimplicialComplex(List<Simplex> simplices){
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

	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
		this.chromatic = chromatic;
		for (Simplex s : simplices){
			s.setChromatic(chromatic);
		}
	}
	
}
