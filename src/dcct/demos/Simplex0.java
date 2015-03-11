package dcct.demos;

public class Simplex0 {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	private String name=null;
	private String view=null;
	private double[] position;

	public Simplex0(String name, String view, double[] position ){
		this.name=name;
		this.view=view;
		this.position=position;
	}
	
	public Simplex0(String name, String view){
		this(name,view,null);
	}
	
	public Simplex0(String name, double[] position){
		this(name,name,position);
	}
	
	public Simplex0(String name){
		this(name,name,null);
	}
}
