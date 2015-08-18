package unam.dcct.view.commands;

import unam.dcct.view.View;

public class ResetViewCommand extends Command{
	
	public ResetViewCommand(View receiver){
		super(receiver);
	}
	public void execute() {
		receiver.reset();
	}
}
