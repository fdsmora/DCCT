package view;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import configuration.Constants;
import dctopology.Process;

public class Vertex {
	
	private double[] coordinates;
	private int index;
	private Color color;
	private String label;
	private int id;
	private String[] processView;
	
	public Vertex(Process p){
		if (p!=null){
			this.label = p.getView();
			this.id = p.getId();
			this.processView = p.getViewArray();
		}
	}
	
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
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public int getId() {
		return id;
	}

	public void calculateCoordinates(Face parentFace, boolean chromatic){
		List<Vertex> parentVertices = parentFace.getVertices();
		if (chromatic){
			coordinates = calculateChromaticCoordinates(parentVertices);
		}
	}
	
	private double[] calculateChromaticCoordinates(List<Vertex> parentVertices) {
		int count = countProcessViewElements();
		
		if (count == 1)
			return parentVertices.get(id).coordinates;
		
		//int divisor = parentVertices.size();
		double smallFactor = (1-Constants.EPSILON)/count;
		double bigFactor = (1+(Constants.EPSILON/(count==3?2:1)))/count;
		double[] res = {0.0,0.0,0.0};
		
		for (int i = 0; i<processView.length; i++){
			if (i==id)
				res = vectorSum(
						scalarVectorMultiply(smallFactor,parentVertices.get(id).getCoordinates())
						,res);
			else {
				double[] coords = (processView[i]==null? new double[3] : 
										parentVertices.get(i).getCoordinates());
				res = vectorSum(
						scalarVectorMultiply(bigFactor, coords)
						,res);
			}
		}
		return res;
	}
	
	public static double[] scalarVectorMultiply(double scalar, double[] vector){
		double[] res = vector.clone();
		for (int i = 0; i<vector.length; i++){
			res[i]*=scalar;
		}
		return res;
	}
	
	public static double[] vectorSum(double[] vector1, double[] vector2){
		double[] res = new double[vector1.length];
		for (int i = 0; i<vector1.length; i++){
			res[i]=vector1[i]+vector2[i];
		}
		return res;
	}
	
	private int countProcessViewElements(){
		int c = 0;
		for (int i =0; i<processView.length; i++){
			if (processView[i]!=null)
				c++;
		}
		return c;
	}
	@Override
	public String toString(){
		return String.format("Vertex.index=%1$s\nVertex.label=%2$s\nVertex.coordinates=(%3$.2f,%4$.2f,%5$.2f)\nVertex.color=%6$s\n",
				this.index,this.label,this.coordinates[0],this.coordinates[1],this.coordinates[2],this.color);
	}
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Vertex)) 
		    return false;
		return true;
	}
	
	@Override
	public int hashCode(){
		//return this.id.hashCode();
		
		// This hash code is computed the same way as the Process class. 
		return this.id + this.label.hashCode();

	}
}