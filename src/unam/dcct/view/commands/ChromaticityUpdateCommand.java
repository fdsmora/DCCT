package unam.dcct.view.commands;

import unam.dcct.view.View;

public class ChromaticityUpdateCommand extends Command{
		
	//private boolean chromatic;
	
	public ChromaticityUpdateCommand(View receiver) {
		super(receiver);
		//this.chromatic = complex.isChromatic();
	}

	@Override
	public void execute() {
		receiver.updateChromaticity();
	}
	
}