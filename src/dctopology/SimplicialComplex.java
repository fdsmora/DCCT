package dctopology;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class SimplicialComplex {
	public List<Simplex> getSimplices() {
		return simplices;
	}

	private List<Simplex> simplices;
	private int dimension;
	private boolean chromatic = true;
	private int totalProcessCount = 0;
	
	public SimplicialComplex(Simplex... simplices){
		//this.simplices = new LinkedHashSet<Simplex>(Arrays.asList(simplices));
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
		// Geometric data
		sb.append("\nGeometry data:\n");
		sb.append("Total process count:"+totalProcessCount +"\n");
		double[][] coords = getCoordinates();
		sb.append("All coordinates:"+Arrays.deepToString(coords) + "\n");
		String[] labels = getProcessLabels();
		sb.append("All labels:"+Arrays.deepToString(labels)+"\n");
		Color[] colors  = getProcessColors();
		sb.append("All colors:"+Arrays.deepToString(colors)+"\n");
		int[][] faces = getFaces();
		sb.append("All faces:"+Arrays.deepToString(faces)+"\n");
		
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
	
	public double[][] getCoordinates(){
		double[][] coordinates = new double[0][]; 
		for (Simplex s : simplices){
			double[][] temp = ArrayUtils.addAll(coordinates,s.getCoordinates());
			coordinates = temp;
		}
			
		//Test
		//System.out.println("All coordinates:\n"+Arrays.toString(coordinates));
		
		return coordinates;
	}
	
	public String[] getProcessLabels(){
		String[] labels = new String[0];
		for (Simplex s : simplices){
			String[] temp = ArrayUtils.addAll(labels,s.getProcessLabels());
			labels = temp;
		}
		//Test
		//System.out.println("All labels:\n"+Arrays.toString(labels));
		
		return labels;
	}
	
	public Color[] getProcessColors(){
		Color[] colors = new Color[0];
		for (Simplex s : simplices){
			Color[] temp = ArrayUtils.addAll(colors,s.getProcessColors());
			colors = temp;
		}
		//Test
		//System.out.println("All colors:\n"+Arrays.toString(colors));
		return colors;
	}
	
	public int[][] getFaces(){
		int[][] faces = new int[simplices.size()][];
		int index = 0, j=0;		
		for (Simplex s : simplices){
			int processCount = s.getProcessCount();
			int[] face = new int[processCount];
			for (int i =0; i<processCount; i++){
				face[i]=index++;
			}
			faces[j++] = face;
		}
		//Test
		//System.out.println("All faces:\n"+Arrays.toString(faces));
		return faces;
	}

	public int getTotalProcessCount() {
		return totalProcessCount;
	}
	
}
