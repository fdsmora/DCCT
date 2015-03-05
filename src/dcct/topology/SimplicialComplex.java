package dcct.topology;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimplicialComplex {
	public Set<Simplex> getSimplices() {
		return simplices;
	}

	protected Set<Simplex> simplices;
	
	public SimplicialComplex(Simplex... simplices){
		this.simplices = new LinkedHashSet<Simplex>(Arrays.asList(simplices));
	}
	
	public SimplicialComplex(Set<Simplex> simplices){
		this.simplices = simplices;
	}
	
	
}
