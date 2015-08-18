package unam.dcct.view.commands;

import unam.dcct.misc.Constants;
import unam.dcct.view.View;

public abstract class Command {
	
	protected View receiver;
	
	public Command(View receiver){
		this.receiver = receiver;
	}
	
	public abstract void execute();

	public static Command createCommand(String type, View receiver){
		if (type.equals(Constants.RESET_VIEW_COMMAND))
			return new ResetViewCommand(receiver);
		if (type.equals(Constants.COMPLEX_UPDATE_COMMAND))
			return new ComplexUpdateCommand(receiver);
		if (type.equals(Constants.CHROMATICITY_UPDATE_COMMAND))
			return new ChromaticityUpdateCommand(receiver);
		return null;
	}
}
