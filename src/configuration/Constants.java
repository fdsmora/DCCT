package configuration;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Constants {
	public static final Color[] DEFAULT_COLORS = {Color.BLUE, Color.WHITE, Color.GREEN, Color.YELLOW, Color.RED};
	public static final int MAX_COLORS = 5;
	public static final String SHARED_MEMORY = "Shared memory";
	public static final String IMMEDIATE_SNAPSHOT = "Immediate snapshot";
	public static final String NEXT = "Next";
	public static final String BACK = "Back";
	public static final String GENERATE = "Generate";
//	public static final String NUMBER_OF_PROCESSES_STEP = "NumberOfProcessesStep";
//	public static final String NAME_COLOR_STEP = "NameColorStep";
//	public static final String COMMUNICATION_MODEL_STEP = "CommunicationModelStep";
//	public static final String NEXT_ROUND_STEP = "NextRoundStep";
//	public static final String CHROMATIC_STEP = "ChromaticStep";
	public static final String COMMUNICATION_MODEL = "Communication model";
	public static final String EXECUTE_ROUND = "Execute round";
	public static final String START_OVER = "Start over";
	public static final String CHANGE_MODEL = "Change model";
	public static final String NEXT_ROUND = "Next round";
	public static final String MAX_ROUNDS_REACHED_MSG = "Maximum number of rounds allowed reached.";
	public static final String CHROMATIC = "Chromatic";
	public static final String NON_CHROMATIC = "Non-chromatic";
	public static final String PROTOCOL_COMPLEX = "Protocol Complex";
	public static final String INITIAL_COMPLEX = "Initial Complex";
	public static final String SIMPLICIAL_COMPLEX_CONSOLE = "Simplicial Complex Console";
	public static final String SIMPLICIAL_COMPLEX_PANEL = "Simplicial Complex Panel";
	public static final String OUTPUT_CONSOLE_DELIMITER = "===================\n";
	public static final String NUMBER_OF_SIMPLICIES = "Number of simplicies";
	public static final String SET_NOTATION_REPRESENTATION = "Set notation representation";
	public static final String UNKNOWN = "Unknown";

	public static final double[][] DEFAULT_0_SIMPLEX_VERTEX_COORDINATES = {{0,0,0}};
	public static final double[][] DEFAULT_1_SIMPLEX_VERTEX_COORDINATES = {{-4.5,0,0},{4.5,0,0}};
	public static final double[][] DEFAULT_2_SIMPLEX_VERTEX_COORDINATES = {{0,3.8,0}, {-4.5,-4,0},{4.5,-4,0}};
	public static final double[][][] DEFAULT_SIMPLEX_VERTEX_COORDINATES = { DEFAULT_0_SIMPLEX_VERTEX_COORDINATES,
																			DEFAULT_1_SIMPLEX_VERTEX_COORDINATES,
																			DEFAULT_2_SIMPLEX_VERTEX_COORDINATES};
											
	public enum ProcessViewBrackets{
		ROUND("(%s)"),
		SQUARE("[%s]"),
		CURLY("{%s}"),
		ANGLE("<%s>");
		
		private String formatString;
		ProcessViewBrackets(String format){
			this.formatString = format;
		}
		public String getBrackets(){
			return formatString;
		}
		public static final ProcessViewBrackets DEFAULT = CURLY;
	}
	
//	public static final Map<String, String> VIEW_BRACKETS = new HashMap<String, String>(4);
//	static {
//		VIEW_BRACKETS.put("()", "(%)");
//		VIEW_BRACKETS.put("[]", "[%]");
//		VIEW_BRACKETS.put("{}", "{%}");
//		VIEW_BRACKETS.put("<>", "<%>");
//	}
//	public static final String DEFAULT_BRACKET = "()";
	
	public static final float EPSILON_DEFAULT = 0.4f;
	
	public static final Map<String, List<String>> availableCommunicationModels;
	static {
		availableCommunicationModels = new LinkedHashMap<String, List<String>>();
		availableCommunicationModels.put(SHARED_MEMORY, Arrays.asList(IMMEDIATE_SNAPSHOT));
	}
	
}
