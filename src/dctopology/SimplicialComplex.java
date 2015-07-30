package dctopology;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimplicialComplex {
	private List<Simplex> chromaticSimplices;
	private List<Simplex> nonChromaticSimplices;
	private int dimension;
	private boolean chromatic = true;
	private int totalProcessCount = 0;
	
	public SimplicialComplex(Simplex... simplices){
		this(true, Arrays.asList(simplices));
	}
	
	public SimplicialComplex(List<Simplex> simplices){
		this(true, simplices);
	}
	
	public SimplicialComplex( boolean chromatic, Simplex... simplices){
		this(Arrays.asList(simplices));
	}
	
	public SimplicialComplex(boolean chromatic, List<Simplex> simplices){
		this.chromatic=chromatic;
		if (chromatic){
			setChromaticSimplices(simplices);
		}
		else 
			setNonChromaticSimplices(simplices);

		computeDimension();
		computeTotalProcessCount();
	}

	private void verifyChromacity(boolean chromatic){
		if (chromatic){
			for (Simplex s : chromaticSimplices)
				if (!s.isChromatic())
					throw new IllegalArgumentException("This complex is chromatic, so all simplices must be chromatic.");
		}else
			for (Simplex s : nonChromaticSimplices)
				if (s.isChromatic())
					throw new IllegalArgumentException("This complex is non-chromatic, so all simplices must be non-chromatic.");
	}
	
	public List<Simplex> getSimplices() {
		if (chromatic)
			return chromaticSimplices;
		 
		return nonChromaticSimplices;
//		List<Simplex> simplices = null;
//		try{
//			if (chromatic){
//				simplices = chromaticSimplices;
//				if (simplices==null)
//					throw new IllegalStateException("This complex is chromatic, but its chromatic simplices have not been supplied.");
//				return chromaticSimplices;
//			}
//			else {
//				simplices = nonChromaticSimplices;
//				if (simplices==null)
//					throw new IllegalStateException("This complex is non-chromatic, but its non-chromatic simplices have not been supplied.");
//				return nonChromaticSimplices;
//			}
//		}
//		catch(Exception e){}
//		return simplices;
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
		for (Simplex s: getSimplices())
			totalProcessCount+= s.getProcessCount();
	}
	
	private void computeDimension(){
		// Get the maximum dimension of this complex's containing simplices. 
		this.dimension = Collections.max(this.getSimplices(),
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
		for (Simplex s : getSimplices()){
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
		if (chromatic && chromaticSimplices==null)
			throw new IllegalStateException("Chromatic simplices must be supplied before setting this complex chromatic.");
		else if (!chromatic && nonChromaticSimplices == null)
			throw new IllegalStateException("Non-chromatic simplices must be supplied before setting this complex non-chromatic.");

		this.chromatic = chromatic;

	}

	public int getTotalProcessCount() {
		return totalProcessCount;
	}
	
	public int getSimplexCount(){
		return getSimplices().size();
	}

	public void setChromaticSimplices(Simplex... chromaticSimplices) {
		setChromaticSimplices(Arrays.asList(chromaticSimplices));
	}
	
	public void setNonChromaticSimplices(Simplex... nonChromaticSimplices) {
		setNonChromaticSimplices(Arrays.asList(nonChromaticSimplices));
	}
	
	public void setChromaticSimplices(List<Simplex> chromaticSimplices) {
		this.chromaticSimplices = chromaticSimplices;
		verifyChromacity(true);
	}

	public void setNonChromaticSimplices(List<Simplex> nonChromaticSimplices) {
		this.nonChromaticSimplices = nonChromaticSimplices;
		verifyChromacity(false);
	}
	
}
