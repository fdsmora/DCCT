package model;

import view.View;
import dctopology.Simplex;
import dctopology.SimplicialComplex;
import dctopology.Process;

public class Model {
	protected SimplicialComplex initialComplex;
	protected SimplicialComplex protocolComplex;
	protected CommunicationMechanism communicationMechanism;
	protected boolean chromatic = false;
	protected View view;
	
	
	public SimplicialComplex createInitialComplex(int n){
		Process[] processes = new Process[n];
		for (int i = 0; i<n; i++)
			processes[i]= communicationMechanism.createProcess(i);
		
		initialComplex = new SimplicialComplex(new Simplex(processes));
		protocolComplex = null;
		if (view!=null)
			view.update("i");
		return initialComplex;
	}
	
	public SimplicialComplex getInitialComplex(){
		return initialComplex;
	}

	public SimplicialComplex getProtocolComplex() {
		return protocolComplex;
	}

	public void setProtocolComplex(SimplicialComplex pc) {
		this.protocolComplex = pc;
	}

	public boolean isChromatic() {
		return chromatic;
	}

	public void setChromatic(boolean chromatic) {
		this.chromatic = chromatic;
	}
	
	public void registerView(View v){
		view = v;
	}
	
	public void executeRound(){
		if (communicationMechanism == null)
			throw new NullPointerException("No communicationMechanism specified");
		
		protocolComplex = communicationMechanism
				.communicationRound(protocolComplex!=null? protocolComplex : initialComplex);
		if (view!=null)
			view.update("p");
	}

	public CommunicationMechanism getCommunicationMechanism() {
		return communicationMechanism;
	}

	public void setCommunicationMechanism(
			CommunicationMechanism communicationMechanism) {
		this.communicationMechanism = communicationMechanism;
	}

	
	
}
