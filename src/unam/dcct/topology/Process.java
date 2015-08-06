package unam.dcct.topology;

import unam.dcct.misc.Constants;
import unam.dcct.model.Model;

public class Process implements Cloneable {

	private int id = -1;
	private String name = "";
	private View view = null;
	private boolean chromatic = true;
	
	public String getView() {
		return view.getView();
	}
	
	public String[] getViewArray(){
		return view.getViewArray();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setView(String[] view) {
		this.view.viewArray = view;
	}
	
	public Process(int id){
		this.id = id;
		this.view = new View(this);
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
			if (this.view != null){
				p.view = (View) this.view.clone();
				p.view.process = p;
			}
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
		//return this.getView().hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getViewElementsCount(){
		return view.getViewElementsCount();
	}
	
	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
		this.chromatic = chromatic;
	}
	
	private class View implements Cloneable{
		private String[] viewArray;
		private Process process;
		public View(Process p){
			this.process = p;
		}
		
		public String getView(){
			if (this.viewArray == null)
				return process.name;
			String prefix = "";
			String value = "";
			StringBuilder sb = new StringBuilder();
			for(String v : this.viewArray){
				value = v!=null? v : process.isChromatic()? "-" : "";
				if (!value.equals("")){
					sb.append(prefix);
					sb.append(value);
					prefix =",";
				}
			}
			return String.format(Model.getInstance().getSelectedBrackets(), sb.toString());
		}

		public String[] getViewArray(){
			return viewArray;
		}
		public int getViewElementsCount(){
			int c = 0;
			if (viewArray != null){
				for (int i =0; i<viewArray.length; i++){
					if (viewArray[i]!=null)
						c++;
				}
			}
			return c;
		}
		@Override
		public Object clone() {
			View v = null;
			try {
				v = (View) super.clone();
				if (this.viewArray != null)
					v.viewArray = this.viewArray.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return v;
		}
		@Override 
		public boolean equals(Object o){
			if (!(o instanceof View)) 
			    return false;
			return true;
		}
		@Override 
		public int hashCode(){
			return this.getView().hashCode();
		}
	}
}