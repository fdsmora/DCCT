package unam.dcct.model.tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestProtocolScenarioGenerator {

	// Three processes (dimension 2) scenarios
	private static int[][][] mat2 = new int[][][] {
		// Dummy scenarios
		{{1,0,0},{1,1,0},{1,0,1}},
		{{1,1,0},{0,1,0},{0,1,1}},
		{{1,0,1},{0,1,1},{0,0,1}},
		{{1,1,0},{1,1,0},{0,1,1}},
		{{1,0,1},{0,1,1},{1,0,1}},
		
		// Immediate Snapshot execution scenarios
		{{1,1,1},{0,1,0},{1,1,1}},
		{{1,1,1},{1,1,1},{0,0,1}},
		{{1,1,0},{1,1,0},{1,1,1}},
		{{1,0,1},{1,1,1},{1,0,1}},
		{{1,1,1},{0,1,1},{0,1,1}},
		{{1,0,0},{1,1,0},{1,1,1}},
		{{1,0,1},{1,1,1},{1,0,1}},
		{{1,1,0},{0,1,0},{1,1,1}},
		{{1,1,1},{0,1,0},{0,1,1}},
		{{1,1,1},{0,1,1},{0,0,1}},
		{{1,0,1},{1,1,1},{0,0,1}},
		{{1,0,0},{1,1,1},{1,0,1}},
		{{1,0,0},{1,1,1},{1,1,1}},
		{{1,1,1},{1,1,1},{1,1,1}}
	};
	
	// Two processes (dimension 1) scenarios
	private static int[][][] mat1 = new int[][][] {
		{{1,0},{1,1}},
		{{1,1},{1,1}},
		{{1,1},{0,1}}
	};
	
	// One process (dimension 0) scenario
	private static int[][][] mat0 = new int[][][] {
		{{1}}
	};

	private static List<List<int[][]>> allScenariosPerDimension;
	static{
		// Initalize list
		allScenariosPerDimension = new ArrayList<List<int[][]>>();
		allScenariosPerDimension.add(Arrays.asList(mat0));
		allScenariosPerDimension.add(Arrays.asList(mat1));
		allScenariosPerDimension.add(Arrays.asList(mat2));
	}
	
	public static List<int[][]> generate(int dimension){
		return allScenariosPerDimension.get(dimension);
	}
}
