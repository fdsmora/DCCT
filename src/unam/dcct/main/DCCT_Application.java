package unam.dcct.main;

import unam.dcct.model.Model;
import unam.dcct.view.SCOutputConsole;
import unam.dcct.view.View;
import unam.dcct.view.jRealityView;

/***
 * Represents the main Distributed Computing through Combinatorial Topology Application. 
 * It just starts the application.
 * @author Fausto Salazar
 *
 */
public class DCCT_Application {
	public static void main(String[] args){
		jRealityView view = jRealityView.getInstance();
		view.start();
	}
}
