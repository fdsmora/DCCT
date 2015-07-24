package view;

import dctopology.SimplicialComplex;

public class InitialComplexCommand extends Command{
	public InitialComplexCommand(View receiver, SimplicialComplex complex){
		super(receiver, complex);
	}
	public void execute(){
		((jRealityView)receiver).displayComplex(new GeometricComplex(complex));
	}
}
