package unam.dcct.misc;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.regex.Pattern;

import unam.dcct.misc.Constants.ProcessViewBrackets;

public class Configuration {
	public final List<Color> DEFAULT_COLORS;// = {Color.BLUE, Color.WHITE, Color.GREEN, Color.YELLOW, Color.RED};
	//public static int MAX_COLORS;
	public final float EPSILON_VALUE;
	
	private static final Configuration instance = null;
	
	public static Configuration getInstance(){
		if (instance == null){
			instance = new Configuration();
		}
		return instance;
	}
	
	private Configuration(){
		loadConfiguration();
	}
	
	public static Color DEFAULT_NON_CHROMATIC_COLOR;
	public static int MAX_ALLOWED_ROUNDS;
	public static ProcessViewBrackets DEFAULT_BRACKET;

	public static double[][] DEFAULT_0_SIMPLEX_VERTEX_COORDINATES = {{0,0,0}};
	public static double[][] DEFAULT_1_SIMPLEX_VERTEX_COORDINATES = {{0,0,0},{0,0,0}};
	public static double[][] DEFAULT_2_SIMPLEX_VERTEX_COORDINATES = {{0,0,0}, {0,0,0},{0,0,0}};
	public static double[][][] DEFAULT_SIMPLEX_VERTEX_COORDINATES = { DEFAULT_0_SIMPLEX_VERTEX_COORDINATES,
																			DEFAULT_1_SIMPLEX_VERTEX_COORDINATES,
																			DEFAULT_2_SIMPLEX_VERTEX_COORDINATES};
	
	prvaite void loadConfiguration() throws IOException{
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader("exampleconfigfile.txt"));
		}
        catch (IOException ex)
        {
            throw new IOException("Configuration file not found");
        } 
		
		try{
			String line = br.readLine();
			while(line!=null){
				System.out.println(line);
				line = br.readLine();
				setProperties(line);
			}
		}
		finally{
			br.close();
		}
	}
	
	private static void setProperties(String line){
		String[] pair = line.split("=");
		String prop = pair[0];
		if (prop.equals(Constants.SELECTED_DEFAULT_COLORS ) && (pair.length>1)){
			setDefaultColors(pair[1]);
		} else if (prop.equals(Constants.SELECTED_DEFAULT_NON_CHROMATIC_COLOR) && (pair.length>1)){
			setChromaticColor(pair[1]);
		} else if(prop.equals(Constants.SELECTED_DEFAULT_BRACKETS) && (pair.length>1)){
			setSelectedBrackets(pair[1]);
		} else 
		if (pair.length > 1 && !pair[1].isEmpty()){
			if (prop.equals(Constants.DEFAULT_NON_CHROMATIC_COLOR)){
				setChromaticColor(pair[1]);
			}else if (prop.equals(Constants.EPSILON_VALUE)){
				setEpsilonValue(pair[1]);	
			}else if (prop.equals(Constants.MAX_ALLOWED_ROUNDS)){
				setMaxAllowedRounds(pair[1]);
			}else if (Pattern.matches(Constants.DEFAULT_COORDINATES_REGEX, prop)){
				int dim = Character.getNumericValue(prop.charAt(8));
				setDefaultCoordinates(pair[1], dim);
			}else if (prop.equals(Constants.DEFAULT_BRACKETS)){
				setSelectedBrackets(pair[1]);
			}
			else throw new InvalidConfigFormatException("Some basic properties are missing.");
		}
		else throw new InvalidConfigFormatException("The configuration file is malformed.");
	}

	private static void setDefaultBrackets(String str) {
		if (str.equals(Constants.ProcessViewBrackets.CURLY)){
			DEFAULT_BRACKETS = Constants.ProcessViewBrackets.CURLY;
		}else if (str.equals(Constants.ProcessViewBrackets.SQUARE)){
			DEFAULT_BRACKETS = Constants.ProcessViewBrackets.SQUARE;
		}else if (str.equals(Constants.ProcessViewBrackets.ANGLE)){
			DEFAULT_BRACKETS = Constants.ProcessViewBrackets.ANGLE;
		}else if (str.equals(Constants.ProcessViewBrackets.ROUND)){
			DEFAULT_BRACKETS = Constants.ProcessViewBrackets.ROUND;
		}else DEFAULT_BRACKET = Constants.ProcessViewBrackets.DEFAULT;
	}

	private static void setDefaultCoordinates(String str, int dim) {
		String[] strCoords = str.split(",\\s");

		try{
			for (int i = 0; i<dim+1; i++){
				double[] coords = new double[3];
				String[] values = strCoords[i].split(",[]");
				for (String v : values){
					coords[i++]=Double.parseDouble(v);
				}
				DEFAULT_SIMPLEX_VERTEX_COORDINATES[dim][i]=coords;
			}
		}catch(Exception e){
			throw new IllegalArgumentException("The coordinates format specified in the config file is invalid");
		}
			
	}

	private static void setMaxAllowedRounds(String numStr) {
		try{
			MAX_ALLOWED_ROUNDS = Integer.parseInt(numStr);
		}catch (NumberFormatException e) {
		    throw new IllegalArgumentException("The number format specified in the config file is invalid");
		}
	}

	private static void setEpsilonValue(String numStr) {
		try{
			EPSILON_VALUE = Float.parseFloat(numStr);
		}catch (NumberFormatException e) {
		    throw new IllegalArgumentException("The number format specified in the config file is invalid");
		}
	}

	private static void setChromaticColor(String colorName) {
		if (NON_CHROMATIC_COLOR ==null){
			// Convert color name to Color instance.
			try{
				Field field = Class.forName("java.awt.Color").getField(colorName);
				NON_CHROMATIC_COLOR = (Color)field.get(null);
			}catch (Exception e) {
			    throw new IllegalArgumentException("The color specified in the config file is invalid");
			}
		}
		
	}

	private static void setDefaultColors(String values) {
		// Enter only if SELECTED_DEFAULT_COLORS failed due to bad format or value not specified. 
		if (DEFAULT_COLORS== null){
			String[] strColors = values.split(",\\s");
			List<Color> colors = new ArrayList<Color>();
			for (String colorName : strColors){
				Color color;
				// Convert color name to Color instance.
				try{
					Field field = Class.forName("java.awt.Color").getField(colorName);
					color = (Color)field.get(null);
				}catch (Exception e) {
				    throw new IllegalArgumentException("The color specified in the config file is invalid");
				}
				colors.add(color);
			}
			DEFAULT_COLORS = colors;
		}
	}
	
	class InvalidConfigFormatException extends Exception{
		public InvalidConfigFormatException(){}
		public InvalidConfigFormatException(String message){
			super(message);
		}
	}
}
