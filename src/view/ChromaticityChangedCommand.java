package view;

import dctopology.SimplicialComplex;


public class ChromaticityChangedCommand extends Command{
		
	//private boolean chromatic;
	
	public ChromaticityChangedCommand(View receiver, SimplicialComplex complex) {
		super(receiver, complex);
		//this.chromatic = chromatic;
	}

	@Override
	public void execute() {
		((jRealityView)receiver).updateChromacity(complex.isChromatic());
	}
	
}