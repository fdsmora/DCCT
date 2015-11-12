package unam.dcct.main;

import unam.dcct.view.jRealityView;

/***
 * Represents the Distributed Computing through Combinatorial Topology Application. 
 * It just starts the application.
 * @author Fausto Salazar
 *
 */
public class DCCT_Application {
	public static void main(String[] args) {

		jRealityView view = jRealityView.getInstance();
		view.start();
	}
	
}
