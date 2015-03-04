package dcct.topology;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimplicialComplex {
	protected Set<Simplex> simplices;
	
	public SimplicialComplex(Simplex... simplices){
		this.simplices = new LinkedHashSet<Simplex>(Arrays.asList(simplices));
	}
	
	public void subdivide(){
		
	}
}
