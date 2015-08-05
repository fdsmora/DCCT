package unam.dcct.view.commands;

import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.View;


public class ChromaticityUpdateCommand extends Command{
		
	//private boolean chromatic;
	
	public ChromaticityUpdateCommand(View receiver, SimplicialComplex complex) {
		super(receiver, complex);
		//this.chromatic = complex.isChromatic();
	}

	@Override
	public void execute() {
		receiver.updateChromaticity(complex.isChromatic());
	}
	
}