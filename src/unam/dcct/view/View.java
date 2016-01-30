package unam.dcct.view;

/*** 
 * Represents the View component of the MVC architecture of the application. 
 * @author Fausto
 *
 */
public interface View {

	/***
	 * Classes that implement the View interface need to provide logic in order to display the simplicial complex
	 * that the model generates. 
	 */
	void displayComplex();
	/***
	 * When a simplicial complex representation is currently displayed on screen and the chromaticity is changed, 
	 * this method most provide logic in order to update the representation to reflect the chromaticity update. 
	 */
	void updateChromaticity();
	/***
	 * Implementing classes must provide logic in this method that cleans the display and resets all controls.
	 */
	void reset();
	/**
	 * Notifies views that a new protocol complex is about to be created in the user interface
	 */
	void creatingNewProtocolComplex();
}
