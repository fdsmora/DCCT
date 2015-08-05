package unam.dcct.view.commands;

import unam.dcct.misc.Constants;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.View;

public abstract class Command {
	
	protected View receiver;
	protected SimplicialComplex complex;
	
	public Command(View receiver, SimplicialComplex complex){
		this.receiver = receiver;
		this.complex = complex;
	}
	
	public abstract void execute();

	public static Command createCommand(String type, View receiver, SimplicialComplex complex){
		if (type.equals(Constants.RESET_VIEW_COMMAND))
			return new ResetViewCommand(receiver, complex);
		if (type.equals(Constants.COMPLEX_UPDATE_COMMAND))
			return new ComplexUpdateCommand(receiver, complex);
		if (type.equals(Constants.CHROMATICITY_UPDATE_COMMAND))
			return new ChromaticityUpdateCommand(receiver, complex);
		return null;
	}
}
