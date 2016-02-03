package unam.dcct.misc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import de.jreality.shader.Color;

public class Configuration {
	
	private int _MAX_ALLOWED_ROUNDS;
	private float _EPSILON_VALUE;
	private Constants.ProcessViewBrackets _DEFAULT_BRACKETS;
	private Color _NON_CHROMATIC_COLOR;
	private List<Color> _DEFAULT_COLORS;
	public final int MAX_ALLOWED_ROUNDS ;
	public final int SUPPORTED_NUMBER_OF_PROCESSES = 3;
	public final float EPSILON_VALUE;
	public final Constants.ProcessViewBrackets DEFAULT_BRACKETS;
	public final Color NON_CHROMATIC_COLOR;
	public final double[][] DEFAULT_0_SIMPLEX_VERTEX_COORDINATES = {{0,0,0}};
	public final double[][] DEFAULT_1_SIMPLEX_VERTEX_COORDINATES = {{0,0,0},{0,0,0}};
	public final double[][] DEFAULT_2_SIMPLEX_VERTEX_COORDINATES = {{0,0,0}, {0,0,0},{0,0,0}};
	public final double[][][] DEFAULT_SIMPLEX_VERTEX_COORDINATES = { DEFAULT_0_SIMPLEX_VERTEX_COORDINATES,
																			DEFAULT_1_SIMPLEX_VERTEX_COORDINATES,
																			DEFAULT_2_SIMPLEX_VERTEX_COORDINATES};
	public final List<Color> DEFAULT_COLORS;
	
	private static final String CONFIG_NOT_FOUND = "Configuration file not found";
	private static final String PROPERTIES_MISSING = "Some basic properties are missing";
	private static final String PROPERTIES_VALUES_MISSING = "Some required properties values are missing";
	private static final String INVALID_COLOR = "The color specified in the config file is invalid";
	private static final String INVALID_COORDINATES = "The coordinates format specified in the config file is invalid";
	private static final String INVALID_NUMBER_FORMAT = "The number format specified in the config file is invalid";
	private static final String INVALID_NUMBER_OF_COLORS = "There must be at least 3 colors";
	
	private static Configuration instance = null;
	
	//TEST
	public static void main(String[] args) throws IOException, InvalidConfigFormatException{
		Configuration c = Configuration.getInstance();
	}
	
	public static Configuration getInstance() {
		if (instance == null){
			instance = new Configuration();
		}
		return instance;
	}
	
	private Configuration() {
		MAX_ALLOWED_ROUNDS = 5;
		EPSILON_VALUE = 0.35f;
		DEFAULT_BRACKETS = Constants.ProcessViewBrackets.DEFAULT;
		NON_CHROMATIC_COLOR = Color.GRAY;
		Color[] aColors = {Color.BLUE,Color.WHITE,Color.GREEN,Color.YELLOW,Color.RED};
		DEFAULT_COLORS = Arrays.asList(aColors);
		double[][] coords0 = {{0,3.8,0}};
		double[][] coords1 = {{-4.5,0,0},{4.5,0,0}};
		double[][] coords2 = {{0,3.8,0},{-4.5,-4,0},{4.5,-4,0}};
		DEFAULT_SIMPLEX_VERTEX_COORDINATES[0] = coords0;
		DEFAULT_SIMPLEX_VERTEX_COORDINATES[1] = coords1;
		DEFAULT_SIMPLEX_VERTEX_COORDINATES[2] = coords2;
//		BufferedReader br = null;
//		boolean error = false;
//		try{
//			br = new BufferedReader(new FileReader(Constants.CONFIG_FILE_NAME));
//		}
//        catch (IOException ex)
//        {
//        	System.err.println(CONFIG_NOT_FOUND);
//        } 	
//		try{
//			String line;
//			while((line = br.readLine())!=null){
//				if (line.trim().isEmpty() || line.startsWith("#")) // Ignore comments and blank lines
//					continue;
//				//System.out.println(line);
//				setProperties(line);
//			}
//		}
//		catch(Exception e){
//			error = true;
//		}
//		finally{
//			try {
//				if (br!=null)
//					br.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			if (error){
//				// Set default values
//				System.err.println("Some errors happened while reading configuration file, setting default properties values");
//				MAX_ALLOWED_ROUNDS = 3;
//				EPSILON_VALUE = 0.35f;
//				DEFAULT_BRACKETS = Constants.ProcessViewBrackets.DEFAULT;
//				NON_CHROMATIC_COLOR = Color.GRAY;
//				Color[] aColors = {Color.BLUE,Color.WHITE,Color.GREEN,Color.YELLOW,Color.RED};
//				DEFAULT_COLORS = Arrays.asList(aColors);
//				double[][] coords0 = {{0,3.8,0}};
//				double[][] coords1 = {{-4.5,0,0},{4.5,0,0}};
//				double[][] coords2 = {{0,3.8,0},{-4.5,-4,0},{4.5,-4,0}};
//				DEFAULT_SIMPLEX_VERTEX_COORDINATES[0] = coords0;
//				DEFAULT_SIMPLEX_VERTEX_COORDINATES[1] = coords1;
//				DEFAULT_SIMPLEX_VERTEX_COORDINATES[2] = coords2;
//
//			}else {
//				MAX_ALLOWED_ROUNDS = _MAX_ALLOWED_ROUNDS;
//				EPSILON_VALUE = _EPSILON_VALUE;
//				DEFAULT_BRACKETS = _DEFAULT_BRACKETS;
//				NON_CHROMATIC_COLOR = _NON_CHROMATIC_COLOR;
//				DEFAULT_COLORS = _DEFAULT_COLORS;
//				
//			}
//		}

	}
	
	private void setProperties(String line) throws InvalidConfigFormatException{
		String[] pair = line.split("=");
		String prop = pair[0];
		
		// For these properties the value is optional. 
		if (prop.equals(Constants.SELECTED_DEFAULT_COLORS )){
			if (pair.length > 1)
				setDefaultColors(pair[1]);
		} else if (prop.equals(Constants.SELECTED_DEFAULT_NON_CHROMATIC_COLOR)){
			if (pair.length > 1)
				setNonChromaticColor(pair[1]);
		} else if(prop.equals(Constants.SELECTED_DEFAULT_BRACKETS) ){
			if (pair.length>1)
				setDefaultBrackets(pair[1]);
		} // For these properties the value is required. 
		else if (pair.length > 1 && !pair[1].isEmpty()){
			if (prop.equals(Constants.DEFAULT_COLORS)){
				setDefaultColors(pair[1]);
			}else if (prop.equals(Constants.DEFAULT_NON_CHROMATIC_COLOR)){
				setNonChromaticColor(pair[1]);
			}else if (prop.equals(Constants.EPSILON_VALUE)){
				setEpsilonValue(pair[1]);	
			}else if (prop.equals(Constants.MAX_ALLOWED_ROUNDS)){
				setMaxAllowedRounds(pair[1]);
			}else if (Pattern.matches(Constants.DEFAULT_COORDINATES_REGEX, prop)){
				int dim = Character.getNumericValue(prop.charAt(8));
				setDefaultCoordinates(pair[1], dim);
			}else if (prop.equals(Constants.DEFAULT_BRACKETS)){
				setDefaultBrackets(pair[1]);
			}
			else throw new InvalidConfigFormatException(PROPERTIES_MISSING);
		}
		else throw new InvalidConfigFormatException(PROPERTIES_VALUES_MISSING);
	}

	private void setDefaultBrackets(String str) {
		if (str.equals(Constants.ProcessViewBrackets.CURLY.toString())){
			_DEFAULT_BRACKETS = Constants.ProcessViewBrackets.CURLY;
		}else if (str.equals(Constants.ProcessViewBrackets.SQUARE.toString())){
			_DEFAULT_BRACKETS = Constants.ProcessViewBrackets.SQUARE;
		}else if (str.equals(Constants.ProcessViewBrackets.ANGLE.toString())){
			_DEFAULT_BRACKETS = Constants.ProcessViewBrackets.ANGLE;
		}else if (str.equals(Constants.ProcessViewBrackets.ROUND.toString())){
			_DEFAULT_BRACKETS = Constants.ProcessViewBrackets.ROUND;
		}else _DEFAULT_BRACKETS = Constants.ProcessViewBrackets.DEFAULT;
	}

	private void setDefaultColors(String values) {
		if (_DEFAULT_COLORS== null){
			String[] strColors = values.split("[,\\s]+");
			if(strColors.length<3)
				throw new IllegalArgumentException(INVALID_NUMBER_OF_COLORS);
			List<Color> colors = new ArrayList<Color>();
			for (String colorName : strColors){
				Color color;
				// Convert color name to Color instance.
				try{
					Field field = Class.forName("java.awt.Color").getField(colorName);
					color = (Color)field.get(null);
				}catch (Exception e) {
				    throw new IllegalArgumentException();
				}
				colors.add(color);
			}
			_DEFAULT_COLORS = colors;
		}
	}
	
	private void setNonChromaticColor(String colorName) {
		if (_NON_CHROMATIC_COLOR ==null){
			// Convert color name to Color instance.
			try{
				Field field = Class.forName("java.awt.Color").getField(colorName);
				_NON_CHROMATIC_COLOR = (Color)field.get(null);
			}catch (Exception e) {
			    throw new IllegalArgumentException(INVALID_COLOR);
			}
		}	
	}

	private void setDefaultCoordinates(String str, int dim) {
		String[] strCoords = str.split("\\)\\s*,\\s*\\(");

		try{
			for (int i = 0; i<dim+1; i++){
				double[] coords = new double[3];
				String[] values = strCoords[i].replaceAll("[\\(\\)]", "").split("\\s*,\\s*");
				
				for (int j = 0; j<values.length; j++){
					String v = values[j];
					coords[j]=Double.parseDouble(v);
				}
				DEFAULT_SIMPLEX_VERTEX_COORDINATES[dim][i]=coords;
			}
		}catch(Exception e){
			throw new IllegalArgumentException(INVALID_COORDINATES);
		}
			
	}

	private void setMaxAllowedRounds(String numStr) {
		try{
			_MAX_ALLOWED_ROUNDS = Integer.parseInt(numStr);
		}catch (NumberFormatException e) {
		    throw new IllegalArgumentException(INVALID_NUMBER_FORMAT);
		}
	}

	private void setEpsilonValue(String numStr) {
		try{
			_EPSILON_VALUE = Float.parseFloat(numStr);
		}catch (NumberFormatException e) {
		    throw new IllegalArgumentException(INVALID_NUMBER_FORMAT);
		}
	}
	public class InvalidConfigFormatException extends Exception{
		public InvalidConfigFormatException(){}
		public InvalidConfigFormatException(String message){
			super(message);
		}
	}
}
