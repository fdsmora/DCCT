package view;

import dctopology.SimplicialComplex;

public class ProtocolComplexCommand extends Command {	
	public ProtocolComplexCommand(View receiver, SimplicialComplex complex){
		super(receiver,complex);
	}
	public void execute() {
		
		//TEST
		//complex.setChromatic(false);
		((jRealityView)receiver).displayComplex(new GeometricComplex(complex));
	}
}
