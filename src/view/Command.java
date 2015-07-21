package view;

import dctopology.SimplicialComplex;

public abstract class Command {
	
	protected View receiver;
	protected SimplicialComplex complex;
	
	public Command(View receiver, SimplicialComplex complex){
		this.receiver = receiver;
		this.complex = complex;
	}
	
	public abstract void execute();

}
