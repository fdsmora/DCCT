package dcct.visualization;

import dcct.process.Process;

public class Vertex {
	
	public double[] getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	protected double[] coordinates;
	protected int index;
	protected String color;
	protected String label;
	
	public Vertex(Process p){
		if (p!=null){
			this.label = p.toString();
		}
	}
	
	@Override
	public String toString(){
		return String.format("Vertex.index=%1$s\nVertex.label=%2$s\nVertex.coordinates=%3$.2f\nVertex.color=%4$s\n",
				this.index,this.label,this.coordinates,this.color);
	}
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Vertex)) 
		    return false;
		return true;
	}
	
	@Override
	public int hashCode(){
		return this.label.hashCode();
	}
}
