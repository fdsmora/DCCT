package unam.dcct.view.commands;

import unam.dcct.model.Model;
import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.View;
import unam.dcct.view.geometry.GeometricComplex;

public class ComplexUpdateCommand extends Command{
	public ComplexUpdateCommand(View receiver){
		super(receiver);
	}
	public void execute(){
		receiver.displayComplex();
	}
}
