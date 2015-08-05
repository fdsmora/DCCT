package unam.dcct.view.commands;

import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.View;

public class ResetViewCommand extends Command{
	
	public ResetViewCommand(View receiver, SimplicialComplex complex){
		super(receiver, complex);
	}
	public void execute() {
		receiver.reset();
	}
}
