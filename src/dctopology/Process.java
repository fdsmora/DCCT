package dctopology;

public abstract class Process implements Cloneable {

	protected int id = -1;
	protected String name = "";
	protected String[] view = null;
	
	public String getView() {
		if (this.view == null)
			return name;
			//return Integer.toString(this.id);
		String prefix = "";
		StringBuilder sb = new StringBuilder();
		for(String v : this.view){
			sb.append(prefix);
			sb.append(v!=null? v: "-");
			prefix = ",";
		}
		return "(" + sb.toString() + ")";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setView(String[] view) {
		this.view = view;
	}
	
	public Process(int id){
		this.id = id;
	}
	
	@Override 
	public String toString(){
		return String.format("(%d, %s)", this.id, getView());
	}
	
	@Override
	public Object clone() {
		Process p = null;
		try {
			p = (Process) super.clone();
			if (this.view != null)
				p.view = this.view.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	@Override 
	public boolean equals(Object o){
		if (!(o instanceof Process)) 
		    return false;
		return true;
	}
	@Override 
	public int hashCode(){
		return this.id + this.getView().hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}