package unam.dcct.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import unam.dcct.topology.Process;
import unam.dcct.view.geometry.Face;

/**
 * Represents a distributed computing simplex. 
 * A simplex can be chromatic or non-chromatic. This is determined by the chromatic field.
 * The chromatic field is read-only, so there is no setter method. This value is set during
 * construction. See the {@link #Simplex(boolean, List)} and {@link #Simplex(boolean, Process...)}
 * constructors. 
 * @author Fausto Salazar
 * @see SimplicialComplex
 * @see Process
 * @see unam.dcct.model.CommunicationProtocol
 * @see unam.dcct.view.geometry.Face
 */
public class Simplex {
	/**
	 * This field is read-only in the sense that no setter method is provided for it. It's 
	 * default value is true.
	 */
	private boolean chromatic = true; 
	/**
	 *  Each simplex has a 'parent' simplex associated with it. The parent simplex 
	 *  is the simplex from where this simplex was produced during the communication round. 
	 *  See {@link unam.dcct.model.CommunicationProtocol.generateAllNewSimplices(List<Simplex> , boolean)}
	 */
	private Simplex parent = null;
	/**
	 * It is the face that is part of the unam.dcct.view.geometry.GeometricComplex instance
	 * that represents the geometric representation of the simplicial complex of which this
	 * simplex is part of. A simplex maps to a Face instance (associatedFace) and a SimplicialComplex
	 * maps to a GeometricComplex. See the GeometricComplex class constructor to see how this field is
	 * used. Ideally a Simplex instance should not be aware of its associated face
	 * in order to keep things weakly decoupled, but I didn't have time to device a better solution,
	 * something better may be figured out. 
	 */
	private Face associatedFace = null;
	/**
	 * The number n of processes contained in this simplex.
	 */
	private int n = 0;
	private List<Process> processes;

	/**
	 * Overload of the constructor which receives the processes as an array and lets the client create the simplex as chromatic or non-chromatic.  
	 * @param chromatic 
	 * @param processes The array of processes that will make up this simplex. 
	 */
	public Simplex(boolean chromatic, Process... processes){
		this(chromatic, Arrays.asList(processes));
	}
	
	/**
	 * Constructor of a Simplex that lets the client creates the simplex as chromatic or non-chromatic.  
	 * A simplex can't change its chromatic representation once it is instantiated. So at 
	 * construction time the value of this field must be provided. 
	 * <p>
	 * If the chromatic value passed in is set to false all the processes passed in will 
	 * automatically be converted to non-chromatic. This is required, as in the non-chromatic
	 * representation of processes if two or more processes have the same view (see {@link Process#getView()})
	 * they will be "merged", that is, only one of these processes will be chosen to be part of this 
	 * simplex and the others will be discarded. 
	 * <p>
	 * The processes passed in will be sorted in non-decreasing order according to their id's. 
	 * 
	 * @param chromatic Denotes if the simplex will be chromatic or non-chromatic
	 * @param processes The array of processes that will make up this simplex. 
	 */
	public Simplex(boolean chromatic, List<Process> processes){
		
		sort(processes);

		this.chromatic = chromatic;
		if (!chromatic)
			this.processes = makeProcessesNonChromatic(processes);
		else 
			this.processes = processes;
		
		this.n = this.processes.size();
	}
	
	/**
	 * Converts the list of processes passed in to a list of non-chromatic processes 
	 * apt to be part of a non-chromatic simplex. That is, if two or more processes have the same view (see {@link Process#getView()})
	 * they will be "merged", that is, only one of these processes will be chosen to be part of this 
	 * simplex and the others will be discarded. The original processes passed in won't be affected
	 * by this method, as the list of processes returned by this are copies of the original processes. 
	 * @param processes Processes whose copies will be converted and merged so they are non-chromatic. 
	 * @return A List of processes that are non-chromatic and each process is distinct in terms of their views. 
	 */
	private List<Process> makeProcessesNonChromatic(List<Process> processes) {
		Map<String, Process> uniqueProcesses = new LinkedHashMap<String, Process>(processes.size());
		
//		if (!chromatic){
//			System.out.println("Printing processes, chromatic:" + chromatic);
//			System.out.println(processes);
//		}
		
		int idCounter = 0;
		for (Process p : processes){
			String key = p.getView();
			if (!uniqueProcesses.containsKey(key)){
				Process _p = (Process)p.clone();
				uniqueProcesses.put(key, _p);
				_p.setChromatic(false);
				// Re-set process ids
				_p.setId(idCounter++);
			}
		}
//		if (!chromatic){
//			System.out.println("New processes:");
//			System.out.println(uniqueProcesses.toString());
//		}
		return  new ArrayList<Process>(uniqueProcesses.values());
	}

	/**
	 * Overload of the constructor that just receives the array of processes.
	 * By default the simplex is chromatic if using this constructor. 
	 * @param processes
	 */
	public Simplex(Process... processes){
		this(true, Arrays.asList(processes));
	}
	
	/**
	 * Overload of the constructor that just receives the List of processes.
	 * By default the simplex is chromatic if using this constructor. 
	 * @param processes
	 */
	public Simplex(List<Process> processes){
		this(true, processes);
	}
	
	/**
	 * Sort processes in non-decreasing order by their id's. 
	 * @param processes
	 */
	private void sort(List<Process> processes){
		// Processes must be sorted in increasing order by id.
		Collections.sort(processes, new Comparator<Process>(){
			@Override
			public int compare(Process p1, Process p2) {
				return Integer.compare(p1.getId(), p2.getId());
			}
		});
	}
	
	public List<Process> getProcesses() {
		return processes;
	}
	
	/**
	 * Returns the dimension of this simplex. The dimension of a simplex is the number n of 
	 * processes it contains - 1. 
	 * @return The dimension of this simplex. 
	 */
	public int dimension(){
		return n -1 ;
	}
	
	/**
	 * Returns the number of processes this simplex contains. 
	 * @return The number of processes this simplex contains. 
	 */
	public int getProcessCount(){
		return n;
	}
	@Override 
	public boolean equals(Object o){
		if (!(o instanceof Simplex)) 
		    return false;
		return true;
	}
	/**
	 * A simplex hashcode is built by adding the hashcodes of its containing processes. 
	 */
	@Override 
	public int hashCode(){
		int hashC =0;
		for (Process p : getProcesses())
			hashC+=p.hashCode();
		return hashC;
	}
	
	/**
	 * Returns the set notation representation of this simplex. 
	 */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		sb.append("{");
		for (Process p : getProcesses()){
			sb.append(prefix);
			sb.append(p.toString());
			prefix = ",";
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 
	 * @return A boolean that indicates if this simplex is chromatic or non-chromatic. 
	 */
	public boolean isChromatic() {
		return chromatic;
	}

	/**
	 * See {@link #parent}
	 * @return parent The Simplex instance that is parent of this simplex. 
	 */
	public Simplex getParent() {
		return parent;
	}

	/**
	 * See {@link #parent}
	 * @param parent The Simplex instance that is parent of this simplex. 
	 */
	public void setParent(Simplex parent) {
		this.parent = parent;
	}

	/**
	 * Returns the {@link Simplex#associatedFace} of this simplex. 
	 * @return The {@link unam.dcct.view.geometry.Face} instance associated to this simplex. 
	 */
	public Face getFace() {
		return associatedFace;
	}

	/**
	 * Sets the {@link Simplex#associatedFace} of this simplex. 
	 */
	public void setFace(Face face) {
		this.associatedFace = face;
	}
	
}


