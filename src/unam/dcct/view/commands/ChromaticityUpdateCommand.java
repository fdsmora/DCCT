package unam.dcct.view.commands;

import unam.dcct.view.View;

public class ChromaticityUpdateCommand extends Command{
			
	public ChromaticityUpdateCommand(View receiver) {
		super(receiver);
	}

	@Override
	public void execute() {
		receiver.updateChromaticity();
	}
	
}