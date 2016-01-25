package unam.dcct.view.geometry;

import de.jreality.shader.Color;
import unam.dcct.topology.Process; 

public class Vertex {
	private Process process;
	private int index=0;
	private double[] coordinates;
	private Color color;
	
	public Vertex(Process p){
		this.process = p;
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getLabel(){
		return process.getView();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}

	public Process getProcess() {
		return process;
	}
	
	@Override 
	public int hashCode(){
		return process.hashCode();
	}
}
