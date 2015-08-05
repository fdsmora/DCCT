package unam.dcct.view;

import unam.dcct.view.geometry.GeometricComplex;

/*** 
 * Represents the View component of the MVC architecture of the application. 
 * @author Fausto
 *
 */
public interface View {
	/***
	 * The purpose of this method is to be called by the Model when it changes its state so that 
	 * associated views can be updated to reflect these changes. 
	 * @param action A command object that represents the particular code that is executed in response to 
	 * a particular change in the model. For example, if a new initial complex has been created, then 
	 * an InitialComplexCommand object has to be supplied as this argument. 
	 */
//	void update(Command action);
	/***
	 * Classes that implement the View interface need to provide logic in this method to set up and 
	 * display the main screen of the application. 
	 */
	void start();
	void displayComplex(GeometricComplex complex);
	void updateChromaticity(boolean chromatic);
	void reset();
}
