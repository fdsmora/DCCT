package dcct.demos;

import java.util.ArrayList;
import java.util.List;

public class Simplex1 {
	
	public Simplex0 getVertex0() {
		return vertex0;
	}

	public void setVertex0(Simplex0 vertex0) {
		this.vertex0 = vertex0;
	}

	public Simplex0 getVertex1() {
		return vertex1;
	}

	public void setVertex1(Simplex0 vertex1) {
		this.vertex1 = vertex1;
	}

	public List<Simplex0> getFaces() {
		return faces;
	}

	public void setFaces(List<Simplex0> faces) {
		this.faces = faces;
	}

	private Simplex0 vertex0 = null;
	private Simplex0 vertex1 = null;
	
	private List<Simplex0> faces = new ArrayList<Simplex0>();	

	public Simplex1(Simplex0 s0, Simplex0 s1){
		this.vertex0 = s0;
		this.vertex1 = s1;
		this.faces.add(s0);
		this.faces.add(s1);
	}
	
	
}
