package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import configuration.Constants;
import view.View;
import dctopology.Simplex;
import dctopology.SimplicialComplex;
import dctopology.Process;

public class Model {
	protected SimplicialComplex initialComplex;
	protected SimplicialComplex protocolComplex;
	protected CommunicationMechanism communicationMechanism;
	protected boolean chromatic = true;
	protected View view;
	protected List<Color> pColors;

	public Model(){
	}
	
	public void reset(){
		initialComplex =protocolComplex= null;
		chromatic = true;
		pColors = null;
		communicationMechanism = null;
		if (view!=null)
			view.update("r"); // reset
	}
	
	public SimplicialComplex createInitialComplex(List<String> pNames){
		int n = pNames.size();
		Process[] processes = new Process[n];
		for (int i = 0; i<n; i++){
			processes[i]= new Process(i);
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
		if (view!=null)
			view.update("c"); // Chromatic changed
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

	public void setCommunicationMechanism(String c) {
		this.communicationMechanism = CommunicationMechanism.createCommunicationMechanism(c);
		if (view!=null)
			view.update("u"); // Update communication model 
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

	public List<Color> getSimplicialComplexColors() {
		if (chromatic){
			if (pColors == null)
				return Arrays.asList(Constants.DEFAULT_COLORS);
		} else return null;
		
		return pColors;
	}

	public void setSimplicialComplexColors(List<Color> pColors) {
		// Add additional colors to prevent cases when there are less colors than processes.
		if (pColors.size()<Constants.MAX_COLORS){
			for (int i=Constants.MAX_COLORS-pColors.size(); i>0; i--){
				pColors.add(Constants.DEFAULT_COLORS[i]);
			}
		}
		this.pColors = pColors;
	}
	
	
}
