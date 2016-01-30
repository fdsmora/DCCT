package unam.dcct.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;


/***
 * The purpose of this class is to provide a global access to the most commonly used constant values used throughout the application. 
 * @author Fausto
 */
public final class Constants {
	public static final String BACK = "Back";
	public static final String GENERATE = "Generate";
	public static final String COMMUNICATION_PROTOCOL = "Communication protocol";
	public static final String NEXT = "Next";
	public static final String EXECUTE_ROUND = "Execute round";
	public static final String START_OVER = "Start over";
	public static final String CHANGE_MODEL = "Change protocol";
	public static final String NEXT_ROUND = "Next round";
	public static final String MAX_ROUNDS_REACHED_MSG = "Maximum number of rounds allowed reached.";
	public static final String CHROMATIC = "Chromatic";
	public static final String NON_CHROMATIC = "Non-chromatic";
	public static final String PROTOCOL_COMPLEX = "Protocol Complex";
	public static final String INITIAL_COMPLEX = "Initial Complex";
	public static final String SIMPLICIAL_COMPLEX_CONSOLE = "Simplicial Complex Output Console";
	public static final String SIMPLICIAL_COMPLEX_PANEL = "Simplicial Complex Panel";
	public static final String OUTPUT_CONSOLE_DELIMITER = "===================\n";
	public static final String NUMBER_OF_SIMPLICIES = "Total number of simplicies";
	public static final String SET_NOTATION_REPRESENTATION = "Simplices summary and set notation representation";
	public static final String UNKNOWN = "Unknown";
	public static final String SIMPLICIAL_COMPLEX = "Simplicial complex";
	public static final String SHARED_MEMORY = "Shared memory";
	public static final String IMMEDIATE_SNAPSHOT = "Immediate Snapshot";
	public static final String SELECTED_DEFAULT_COLORS = "SELECTED_DEFAULT_COLORS";
	public static final String DEFAULT_COORDINATES_REGEX = "DEFAULT_\\d_SIMPLEX_VERTEX_COORDINATES";
	public static final String SELECTED_DEFAULT_NON_CHROMATIC_COLOR = "SELECTED_DEFAULT_NON_CHROMATIC_COLOR";
	public static final String SELECTED_DEFAULT_BRACKETS = "SELECTED_DEFAULT_BRACKETS";
	public static final String EPSILON_VALUE = "EPSILON_VALUE";
	public static final String DEFAULT_BRACKETS = "DEFAULT_BRACKETS";
	public static final String DEFAULT_NON_CHROMATIC_COLOR = "DEFAULT_NON_CHROMATIC_COLOR";
	public static final String MAX_ALLOWED_ROUNDS = "MAX_ALLOWED_ROUNDS";
	public static final String DEFAULT_COLORS = "DEFAULT_COLORS";
	public static final String CONFIG_FILE_NAME = "dcct.config";
	public static final String PROTOCOL_INFORMATION = "Protocol information";
	public static final String GEOMETRIC_INFORMATION = "Geometric information";
	public static final String IMMEDIATE_SNAPSHOT_SHARED_MEMORY_ITERATED = "Iterated " + Constants.IMMEDIATE_SNAPSHOT + " " + Constants.SHARED_MEMORY;
	public static final String IMMEDIATE_SNAPSHOT_SHARED_MEMORY_NON_ITERATED = "Non-Iterated " + Constants.IMMEDIATE_SNAPSHOT + " " + Constants.SHARED_MEMORY ;
	public static final String NON_IMMEDIATE_SNAPSHOT_SHARED_MEMORY = "Non-" + Constants.IMMEDIATE_SNAPSHOT + " " + Constants.SHARED_MEMORY ;
	public static final String WRITE_READ = "Iterated Write/Read (WR) " + Constants.SHARED_MEMORY ;;
	
	
	public enum ProcessViewBrackets{
		CURLY("{%s}"),
		ROUND("(%s)"),
		SQUARE("[%s]"),
		ANGLE("<%s>");
		
		private String formatString;
		ProcessViewBrackets(String format){
			this.formatString = format;
		}
		public String getBracketsWithFormat(){
			return formatString;
		}
		@Override
		public String toString(){
			return String.format(formatString, "");
		}
		
		/**
		 * Find by formatString 
		 * @param formatString This should correspond to one of the defined ProcessViewBrackets in this enum (see the constructor calls to see which formatString corresponds to each ProcessViewBrackets)
		 * @return The ProcessViewBrackets what corresponds to the passed formatString. If no formatString corresponds to any ProcessViewBrackets, returns null. 
		 */
		public static ProcessViewBrackets find(String formatString){
			for (ProcessViewBrackets b : ProcessViewBrackets.values())
				if (b.getBracketsWithFormat().equals(formatString))
					return b;
			return null;
		}
		public static final ProcessViewBrackets DEFAULT = CURLY;
	}
	
	/**
	 * List of the names of the implemented communication protocols in the program.
	 * The names are obtained by scanning the program's classpath at startup (using Reflections library)
	 * in order to find all classes that extend the class @link{unam.dcct.model.CommunicationProtocol}. 
	 * Once the classes are found the static methods getName() implemented in each of these classes
	 * are called using Reflections in order to populate this list. 
	 * @see unam.dcct.model.Model#setCommunicationProtocol(String)
	 * @see unam.dcct.model.Model#getCommunicationProtocol()
	 * @see unam.dcct.model.immediatesnapshot.ImmediateSnapshot#getName()
	 */
	public static final List<String> availableCommunicationProtocolsNames = new ArrayList<String>();
	public static final Color FACE_COLOR_CHOOSER_DEFAULT_COLOR = Color.red;

	static {
		List<String> protocolNames = getCommunicationProtocols();
		for (String name : protocolNames){
			availableCommunicationProtocolsNames.add(name);
		}
	}
	
	public static Set<Class<? extends unam.dcct.model.CommunicationProtocol>> availableCommunicationProtocolsClasses;
	/**
	 * Scans the classpath (using Reflections library) in order to find all
	 * classes that extend the class @link{unam.dcct.model.CommunicationProtocol}. This classes 
	 * represent the available communication protocols supported in the program. 
	 * @return The list of names of the available communication protocols supported in the program.
	 * @see unam.dcct.view.UI.CommunicationProtocolStep
	 * @see unam.dcct.model.Model#setCommunicationProtocol(String)
	 * @see unam.dcct.model.Model#getCommunicationProtocol()
	 */
	private static List<String> getCommunicationProtocols(){
		List<String> info = new ArrayList<String>();
		Reflections reflections = new Reflections("unam.dcct.model");
		availableCommunicationProtocolsClasses = reflections
				.getSubTypesOf(unam.dcct.model.CommunicationProtocol.class);
		for (Class<? extends unam.dcct.model.CommunicationProtocol> c : availableCommunicationProtocolsClasses) {
			try {
				String name = (String) c.getMethod("getName", null).invoke(null, null);
				info.add(name);
			} catch (Exception e) {}
		}
		// Sort the names so that they appear nice in the drop down list. 
		Collections.sort(info);
		return info;
	}
	

}
