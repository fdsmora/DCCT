package configuration;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Constants {
	public static final Color[] DEFAULT_COLORS = {Color.BLUE, Color.WHITE, Color.RED, Color.GREEN, Color.YELLOW};
	public static final int MAX_COLORS = 5;
	public static final String SHARED_MEMORY = "Shared memory";
	public static final String ATOMIC_IMMEDIATE_SNAPSHOT = "Atomic immediate snapshot";
	public static final String NEXT = "Next";
	public static final String BACK = "Back";
	public static final String GENERATE = "Generate";
	public static final String NUMBER_OF_PROCESSES_STEP = "NumberOfProcessesStep";
	public static final String NAME_COLOR_STEP = "NameColorStep";
	public static final String COMMUNICATION_MODEL_STEP = "CommunicationModelStep";
	public static final String NEXT_ROUND_STEP = "NextRoundStep";
	public static final String CHROMATIC_STEP = "ChromaticStep";
	public static final String EXECUTE_ROUND = "Execute round";
	public static final String START_OVER = "Start over";
	public static final String CHANGE_MODEL = "Change model";
	public static final String NEXT_ROUND = "Next round";
	public static final String MAX_ROUNDS_REACHED_MSG = "Maximum number of rounds allowed reached.";
	public static final String CHROMATIC = "Chromatic";
	public static final String NON_CHROMATIC = "Non-chromatic";
	
	public static final Map<String, List<String>> availableCommunicationModels;
	static {
		availableCommunicationModels = new LinkedHashMap<String, List<String>>();
		availableCommunicationModels.put(SHARED_MEMORY, Arrays.asList(ATOMIC_IMMEDIATE_SNAPSHOT));
	}
	
}
