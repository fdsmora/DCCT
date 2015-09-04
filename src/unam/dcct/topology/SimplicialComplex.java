package unam.dcct.topology;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a simplicial complex. 
 * A simplicial complex is made up of simplices. 
 * In distributed computing through combinatorial topology a simplicial complex can be represented
 * in two ways: chromatic or non-chromatic. This class supports both representations. 
 * This is determined by the state of the {@link chromatic} attribute. 
 * @author Fausto Salazar
 *
 */
public class SimplicialComplex {
	private List<Simplex> chromaticSimplices;
	private List<Simplex> nonChromaticSimplices;
	private int dimension;
	private boolean chromatic;
	private int totalProcessCount = 0;
	
	/**
	 * There are two versions of this constructor. This receives the simplices as an array.
	 * The other as a list. This so as to simplify client's code. 
	 * @param simplices An array of simplices
	 */
	public SimplicialComplex(Simplex... simplices){
		this(Arrays.asList(simplices));
	}
	
	/**
	 * The version of the constructor that receives a List of simplices. 
	 * @param simplices A List of simplices
	 */
	public SimplicialComplex(List<Simplex> simplices){
		this.chromatic=true;
		setChromaticSimplices(simplices);

		computeDimension();
		computeTotalProcessCount();	
	}
	
	/**
	 * Checks that the containing simplices are all chromatic or non-chromatic, according to the value of the chromatic parameter.
	 * @param chromatic
	 * @throws IllegalArgumentException If chromaticity is not valid
	 */
	private void verifyChromaticity(boolean chromatic){
		if (chromatic){
			for (Simplex s : chromaticSimplices)
				if (!s.isChromatic())
					throw new IllegalArgumentException("This complex is chromatic, so all simplices must be chromatic.");
		} else
			for (Simplex s : nonChromaticSimplices)
				if (s.isChromatic())
					throw new IllegalArgumentException("This complex is non-chromatic, so all simplices must be non-chromatic.");
	}
	
	/**
	 * Returns the list of this complex's containing simplices. If this complex is chromatic 
	 * all its simplices are also chromatic and the other way around. 
	 * @return
	 */
	public List<Simplex> getSimplices() {

		List<Simplex> simplices = null;
		try{
			if (chromatic){
				simplices = chromaticSimplices;
				if (simplices==null)
					throw new IllegalStateException("This complex is chromatic, but its chromatic simplices have not been supplied.");
			}
			else {
				simplices = nonChromaticSimplices;
				if (simplices==null)
					throw new IllegalStateException("This complex is non-chromatic, but its non-chromatic simplices have not been supplied.");
			}
		}
		catch(Exception e){}
		return simplices;
	}
	
	/**
	 * Returns the dimension of this complex. The dimension of a simplicial complex is the dimension
	 * of the greatest dimension simplex it contains. 
	 * @return
	 */
	public int dimension()
	{
		return dimension;
	}
	public int totalDistinctProcesses()
	{
		return dimension+1;
	}

	private void computeTotalProcessCount(){
		List<Simplex> simplices =  getSimplices();
		if (simplices!=null){
			for (Simplex s: simplices)
				totalProcessCount+= s.getProcessCount();
		}
	}
	
	private void computeDimension(){
		List<Simplex> simplices =  getSimplices();
		if (simplices!=null){
			// Get the maximum dimension of this complex's containing simplices. 
			this.dimension = Collections.max(simplices,
					new Comparator<Simplex>(){
						public int compare(Simplex s, Simplex t){
							return Integer.compare(s.dimension(),t.dimension());
						}}).dimension();
		}
	}
	
	/**
	 * Returns the set notation representation of this complex.
	 */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		sb.append("{");
		List<Simplex> simplices =  getSimplices();
		if (simplices!=null){
			for (Simplex s : simplices){
				sb.append(prefix);
				sb.append(s.toString());
				prefix = ",";
			}
		}
		sb.append("}");
		
		return sb.toString();
	}

	public boolean isChromatic() {
		return chromatic;
	}

	/** 
	 * Calling this method affects the value returned by the {@link SimplicialComplex#getSimplices()} method,
	 * to be more specific, the simplices returned are chromatic or non-chromatic depending on the value of
	 * the chromatic parameter. 
	 * 
	 * @param chromatic
	 */
	public void setChromatic(boolean chromatic) {

		this.chromatic = chromatic;

	}

	/**
	 * Returns the count of all of simplex processes contained in this complex.
	 * @return
	 */
	public int getTotalProcessCount() {
		return totalProcessCount;
	}
	
	public int getSimplexCount(){
		int size = 0;
		List<Simplex> simplices =  getSimplices();
		if (simplices!=null){
			size = simplices.size();
		}
		return size;
	}

	/** 
	 * The simplices supplied as parameters of this method will be the ones returned 
	 * by the {@link #getSimplices()} method when this complex is chromatic.
	 * @param chromaticSimplices
	 */
	public void setChromaticSimplices(Simplex... chromaticSimplices) {
		setChromaticSimplices(Arrays.asList(chromaticSimplices));
	}
	
	/** 
	 * The simplices supplied as parameters of this method will be the ones returned 
	 * by the {@link #getSimplices()} method when this complex is non-chromatic.
	 * @param nonChromaticSimplices
	 */
	public void setNonChromaticSimplices(Simplex... nonChromaticSimplices) {
		setNonChromaticSimplices(Arrays.asList(nonChromaticSimplices));
	}
	
	/** 
	 * The simplices supplied as parameters of this method will be the ones returned 
	 * by the {@link #getSimplices()} method when this complex is chromatic.
	 * @param chromaticSimplices
	 */
	public void setChromaticSimplices(List<Simplex> chromaticSimplices) {
		this.chromaticSimplices = chromaticSimplices;
		verifyChromaticity(true);
	}

	/** 
	 * The simplices supplied as parameters of this method will be the ones returned 
	 * by the {@link #getSimplices()} method when this complex is non-chromatic.
	 * @param nonChromaticSimplices
	 */
	public void setNonChromaticSimplices(List<Simplex> nonChromaticSimplices) {
		this.nonChromaticSimplices = nonChromaticSimplices;
		verifyChromaticity(false);
	}
	
}
