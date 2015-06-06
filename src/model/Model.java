package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import configuration.Configuration;
import view.View;
import dctopology.Simplex;
import dctopology.SimplicialComplex;
import dctopology.Process;

public class Model {
	protected SimplicialComplex initialComplex;
	protected SimplicialComplex protocolComplex;
	protected CommunicationMechanism communicationMechanism;
	protected boolean chromatic = true;
	protected int n =0;
	protected View view;
	protected List<Color> pColors;

	
	public Model(){
	}
	
	public void reset(){
		initialComplex =protocolComplex= null;
		chromatic = true;
		pColors = null;
		n =0;
		if (view!=null)
			view.update("r");
	}
	
	public SimplicialComplex createInitialComplex(List<String> pNames){	
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

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n<1? 2:n;
	}

	public List<Color> getSimplicialComplexColors() {
		if (chromatic){
			if (pColors == null)
				return Arrays.asList(Configuration.DEFAULT_COLORS);
		} else return null;
		
		return pColors;
	}

	public void setSimplicialComplexColors(List<Color> pColors) {
		// Add additional colors to prevent cases when there are less colors than processes.
		if (pColors.size()<Configuration.MAX_COLORS){
			for (int i=Configuration.MAX_COLORS-pColors.size(); i>0; i--){
				pColors.add(Configuration.DEFAULT_COLORS[i]);
			}
		}
		this.pColors = pColors;
	}
	
	
}
