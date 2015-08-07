package unam.dcct.view;

import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.geometry.GeometricComplex;

/*** 
 * Represents the View component of the MVC architecture of the application. 
 * @author Fausto
 *
 */
public interface View {

	/***
	 * Classes that implement the View interface need to provide logic in this method to set up and 
	 * display the main screen of the application. Usually one class will be responsible for starting up
	 * the application, so only one class implements this method. 
	 */
	void start();
	/***
	 * 
	 * @param complex
	 */
	void displayComplex();
	void updateChromaticity();
	void reset();
}
