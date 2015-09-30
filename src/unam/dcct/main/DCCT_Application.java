package unam.dcct.main;

import java.io.IOException;

import unam.dcct.misc.Configuration;
import unam.dcct.misc.Configuration.InvalidConfigFormatException;
import unam.dcct.view.jRealityView;

/***
 * Represents the Distributed Computing through Combinatorial Topology Application. 
 * It just starts the application.
 * @author Fausto Salazar
 *
 */
public class DCCT_Application {
	public static void main(String[] args) {
		// Calling the Configuration constructor reads and loads configuration file. 
//		Configuration conf = Configuration.getInstance();
		jRealityView view = jRealityView.getInstance();
		view.start();
	}
	
}
