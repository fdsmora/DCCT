package unam.dcct.model;

import java.util.HashSet;
import java.util.Set;

import unam.dcct.view.View;
import unam.dcct.view.commands.Command;

/**
 * Abstract Model for an MVC architecture suitable for this program. 
 * @author Fausto Salazar
 *
 */
public abstract class AbstractModel {
	
	protected Set<View> views = new HashSet<View>();

	/**
	 * Registers a {@link View} so that it is notified when some changes in the model occur. 
	 * @param v The object whose class implements the View interface.
	 */
	public void registerView(View v){
		views.add(v);
	}
	/**
	 * Unregisters a previously registered {@link View} so that it is not notified when some changes occur in the model.
	 * @param The object whose class implements the View interface and was previously registered. If that object was not previously
	 * registered nothing happens. 
	 */
	public void unregisterView(View v){
		views.remove(v);
	}
	/**
	 * Updates all registered Views. This method should be called when a change in the Model occurs.
	 * Each specific change is represented by a commandType. 
	 * @param commandType
	 * @see unam.dcct.view.commands.Command
	 */
	protected void updateViews(String commandType){
		for (View v: views){
			Command cmd = Command.createCommand(commandType, v);
			cmd.execute();
		}
	}
}
