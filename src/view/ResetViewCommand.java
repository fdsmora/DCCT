package view;

import dctopology.SimplicialComplex;

public class ResetViewCommand extends Command{
	
	public ResetViewCommand(View receiver, SimplicialComplex complex){
		super(receiver, complex);
	}
	public void execute() {
		((jRealityView)receiver).reset();
	}
}
