package unam.dcct.topology;

import unam.dcct.model.Model;

/**
 * Represents a Process. 
 * <p>
 * In distributed computing through combinatorial topology a process is part of a simplex. 
 * In a geometric representation of a simplex, a process is represented as a vertex of the simplex. 
 * <p>
 * Basically a process \(p_i\) is a pair \((id, view\)), where the \(id\) is an integer that uniquely identifies
 * the process within the simplex and the \(view\) is what the process knows about the other processes 
 * that are also part of the distributed system; or, from a topology perspective, the other processes 
 * of the simplex. 
 * <p>
 * This class provides fields that map to this theoretical definition of a process, and
 * methods that facilitate its manipulation within a simplex. 
 * @author Fausto Salazar
 * @see Simplex
 * @see unam.dcct.model.CommunicationProtocol
 * @see Vertex
 */
public class Process implements Cloneable {

	private int id = -1;
	private String name = "";
	private View view = null;
	private boolean chromatic = true;
	
	/**
	 * Returns a string representation of the 'view' of this process. 
	 * Depending on the chromaticity of this process (see {@link #isChromatic()} and {@link #setChromatic(boolean)}),
	 * the representation will vary. 
	 * <p> 
	 * <h6> Chromatic process </h6>
	 * If this process, let's call it \(p_i\), is chromatic, this method will return the view in this format:
	 * <p>
	 * <i>openingBracket</i> \(v_0,...,v_{n-1}\) <i>closingBracket</i>
	 * <p>
	 * where: 
	 * <ul>
	 * <li><i>openingBracket</i> and <i>closingBracket</i> are the style of brackets selected by the user. 
	 * The options are {}, (), [], and &lt;&gt; . Default is {}. (See {@link Model#getSelectedBrackets()} and {@link Model#setSelectedBrackets(unam.dcct.misc.Constants.ProcessViewBrackets)}). 
	 * <li>\(v_j\) can be either the view of the other processes (let's call them \(p_j\)) if the view of \(p_j\) is known to this process;
	 * or the character "-" if it is unknown.
	 * </ul>
	 * Examples of views returned by this method, if the process is chromatic:
	 * <ul>
	 * <li><code>{0,-,-}</code>
	 * <li><code>{0,-,2}</code>
	 * <li><code>{{0,1,2},{0,1,2},-}</code> (for a two round execution of the protocol)
	 * <li><code>(0,-,2)</code> (if using () brackets)
	 * </ul>
	 * <h6>Non-chromatic process</h6>
	 * When the process is non-chromatic, the returned view is almost the same as in the chromatic process, but with
	 * the difference that no special character is displayed when the view of other processes is unknown. 
	 * In these case, the non-chromatic versions of the example views are these:
	 * <ul>
	 * <li><code>{0}</code>
	 * <li><code>{0,2}</code>
	 * <li><code>{{0,1,2}}</code> (for a two round execution of the protocol)
	 * <li><code>(0,2)</code> (if using () brackets)
	 * </ul>
	 * @return The string representation of the process's view in the format described above. 
	 */
	public String getView() {
		return view.getView();
	}
	
	/**
	 * Returns the internal string array that represents this process's view.
	 * @return The view as an array
	 */
	public String[] getViewArray(){
		return view.getViewArray();
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * Ideally a process's id should never change once it has been created, but in the
	 * {@link Simplex#makeProcessesNonChromatic} method, which creates non-chromatic copies of processes
	 * for non-chromatic simplices initilialization there is the need to give these copies new ids.  
	 * @param id
	 * @see Simplex#Simplex(boolean, java.util.List)
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public void setView(String[] view) {
		this.view.viewArray = view;
	}
	
	/**
	 * A process is initialized with the passed in id. 
	 * @param id The id to initialize the process. 
	 */
	public Process(int id){
		this.id = id;
		this.view = new View(this);
	}
	
	/**
	 * Returns a text representation of the process in the format: "(id, view)". 
	 * @return The string representation of the process. 
	 */
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
	
	/**
	 * The hash code of the process is built by adding the process id and the hashcode of its view.
	 */
	@Override 
	public int hashCode(){
		return this.id + this.getView().hashCode();
		//return this.getView().hashCode();
	}

	/**
	 * Returns the name of the process. The name of the process is used to label the vertex that represents the 
	 * process in the visualization of the simplicial complex. 
	 * <p>
	 * A process is uniquely identified within a simplex by it's id,
	 * but I decided to add a 'name' attribute which works as an alias for the process. 
	 * This is because users would sometimes refer to processes with names other than their integer ids, such
	 * as 'p','q','r','a','b','x','y', etc. Process names must also be unique within the simplex, but this is 
	 * enforced at the moment of data entry in user interface. 
	 * @return The name of the process. 
	 * @see Vertex
	 * @see NameColorStep
	 */
	public String getName() {
		return name;
	}

	/**
	 * See {@link Process#getName()} to learn what a process's name is. 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns how many processes's views this process saw during the last communication round. 
	 * In simpler terms, it returns how many not-null values are contained in the internal array 
	 * that represents this process's view. 
	 * @return
	 * @see #getViewArray()
	 * @see ChromaticFace#calculateCoordinates(Process)
	 * @see NonChromaticFace#calculateCoordinates(Process)
	 */
	public int getViewElementsCount(){
		return view.getViewElementsCount();
	}
	
	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
		this.chromatic = chromatic;
	}
	
	/**
	 * Represents a view for a process. 
	 * <p>
	 * During first development versions of the program a view was just simply an string array. 
	 * But as the program development progressed, more operations on process's views were needed,
	 * so I decided to have a dedicated class to represent and manage views. 
	 * @author Fausto Salazar
	 */
	private class View implements Cloneable{
		private String[] viewArray;
		private Process process;
		
		/** 
		 * A View can belong only to a process. 
		 * @param p The process that owns this view. 
		 */
		View(Process p){
			this.process = p;
		}
		
		/**
		 * Performs the construction of the string representation of a view. 
		 * @return
		 * @see Process#getView()
		 */
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

		/**
		 * @see Process#getViewArray()
		 * @return
		 */
		public String[] getViewArray(){
			return viewArray;
		}
		/**
		 * @see Process#getViewElementsCount
		 * @return
		 */
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