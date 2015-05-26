package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import view.View;
import dctopology.Simplex;
import dctopology.SimplicialComplex;
import dctopology.Process;

public class Model {
	protected SimplicialComplex initialComplex;
	protected SimplicialComplex protocolComplex;
	protected CommunicationMechanism communicationMechanism;
	protected Map<String, List<String>> availableCommunicationModels = new LinkedHashMap<String, List<String>>();
	protected boolean chromatic = false;
	protected View view;
	
	public Model(){
		List<String> smOptions = new ArrayList<String>();
		smOptions.add("Atomic immediate snapshot");
		availableCommunicationModels.put("Shared memory", smOptions);
	}
	
	public SimplicialComplex createInitialComplex(int n, List<String> pNames){
		Process[] processes = new Process[n];
		for (int i = 0; i<n; i++){
			processes[i]= communicationMechanism.createProcess(i);
			processes[i].setName(pNames.get(i));
		}
		
		initialComplex = new SimplicialComplex(new Simplex(processes));
		protocolComplex = null;
		if (view!=null)
			view.update("i");
		
		return initialComplex;
	}
	
	public void setInitialComplex(SimplicialComplex sc){
		initialComplex=sc;
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
		//TEST
		toString();
	}

	public CommunicationMechanism getCommunicationMechanism() {
		return communicationMechanism;
	}

	public void setCommunicationMechanism(
			CommunicationMechanism communicationMechanism) {
		this.communicationMechanism = communicationMechanism;
	}

	public Map<String, List<String>> getAvailableCommunicationModels() {
		return availableCommunicationModels;
	}

	public String toString(){
		System.out.println("InitialComplex\n===================");
		if (initialComplex!=null)
			System.out.println(initialComplex.toString());
		else 
			System.out.println("null");
		System.out.println("ProtocolComplex\n===================");
		if (protocolComplex!=null)
			System.out.println(protocolComplex.toString());
		else 
			System.out.println("null");
		return null;
	}
	
	
}
