package unam.dcct.view.commands;

import unam.dcct.view.View;

public class ComplexUpdateCommand extends Command{
	public ComplexUpdateCommand(View receiver){
		super(receiver);
	}
	public void execute(){
		receiver.displayComplex();
	}
}
