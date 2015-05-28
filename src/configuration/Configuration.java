package configuration;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
	public static final Color[] DEFAULT_COLORS = {Color.BLUE, Color.WHITE, Color.RED, Color.GREEN, Color.YELLOW};
	public static final int MAX_COLORS = 5;
	public static final String SHARED_MEMORY = "Shared memory";
	public static final String ATOMIC_IMMEDIATE_SNAPSHOT = "Atomic immediate snapshot";
	public static final Map<String, List<String>> availableCommunicationModels;
	static {
		availableCommunicationModels = new LinkedHashMap<String, List<String>>();
		availableCommunicationModels.put(SHARED_MEMORY, Arrays.asList(ATOMIC_IMMEDIATE_SNAPSHOT));
	}
}
