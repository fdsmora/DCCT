package dctopology;

import model.Model;
import configuration.Constants;

public class Process implements Cloneable {

	private int id = -1;
	private String name = "";
	private View view = null;
	private boolean chromatic = true;
	
	public String getView() {
		return view.getView();
		//String result = "";
//		if (this.view == null)
//			//result = name;
//			return name;
//		else {
//			String prefix = "";
//			StringBuilder sb = new StringBuilder();
//			for(String v : this.view){
//				sb.append(prefix);
//				sb.append(v!=null? v: "-");
//				prefix = ",";
//			}
//			//result = sb.toString();
//			return "(" + sb.toString() + ")";
//		}
		//return "(" + result + ")";
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
			if (this.view != null)
				p.view = (View) this.view.clone();
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
			if (process.chromatic)
				return getChromaticView();
			return getNonChromaticView();
		}
		
		private String getNonChromaticView() {
			String prefix = "";
			StringBuilder sb = new StringBuilder();
			for(String v : this.viewArray){
				sb.append(prefix);
				sb.append(v!=null? v: "-");
				prefix = ",";
			}
			return null;
		}

		private String getChromaticView() {
				String prefix = "";
				StringBuilder sb = new StringBuilder();
				for(String v : this.viewArray){
					if (process.chromatic)
						sb.append(prefix);
					sb.append(v!=null? v: 
								process.chromatic? "-" : "");
					prefix = ",";
				}
				//result = sb.toString();

				return String.format(Model.getInstance().getSelectedBrackets(), sb.toString());
				//return "(" + sb.toString() + ")";
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
			//return this.getView().hashCode();
		}
	}
}