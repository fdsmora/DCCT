package dcct.process;

public class Process implements Cloneable {

	private int id = -1;
	private String[] view = null;
	
	public String getView() {
		if (this.view == null)
			return Integer.toString(this.id);
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
	
	public void write(String[] memory){
		memory[this.id]=this.getView();
	}
	
	public void snapshot(String[] memory){
		this.setView(memory.clone());
	}
	
	@Override 
	public String toString(){
		return String.format("(%d,%s)", this.id,getView());
	}
	
	@Override
	public Object clone() {
		Process p;
		try {
			p = (Process) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			p = new Process(this.id);
			if (this.view != null)
				p.view = this.view.clone();
		}
		return p;
	}
}