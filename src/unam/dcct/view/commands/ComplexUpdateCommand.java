package unam.dcct.view.commands;

import unam.dcct.topology.SimplicialComplex;
import unam.dcct.view.View;
import unam.dcct.view.geometry.GeometricComplex;

public class ComplexUpdateCommand extends Command{
	public ComplexUpdateCommand(View receiver, SimplicialComplex complex){
		super(receiver, complex);
	}
	public void execute(){
		receiver.displayComplex(new GeometricComplex(complex));
	}
}
