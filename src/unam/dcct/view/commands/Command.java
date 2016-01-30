package unam.dcct.view.commands;

import unam.dcct.view.View;

public abstract class Command {
	
	protected View receiver;
	
	public static final String RESET_VIEW = "r";
	public static final String COMPLEX_UPDATE = "cxu";
	public static final String CHROMATICITY_UPDATE = "chu";
	
	public Command(View receiver){
		this.receiver = receiver;
	}
	
	public abstract void execute();

	public static Command createCommand(String type, View receiver){
		if (type.equals(Command.RESET_VIEW))
			return new ResetViewCommand(receiver);
		if (type.equals(Command.COMPLEX_UPDATE))
			return new ComplexUpdateCommand(receiver);
		if (type.equals(Command.CHROMATICITY_UPDATE))
			return new ChromaticityUpdateCommand(receiver);
		return null;
	}
}
