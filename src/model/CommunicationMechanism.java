package model;

import model.AtomicImmediateSnapshotModel.AtomicImmediateSnapshot;
import configuration.Constants;
import dctopology.SimplicialComplex;

public abstract class CommunicationMechanism {
	protected int t=0;
	protected int rounds=0;

	public int get_t(){
		return t;
	};
	public void set_t(int t){
		this.t=t;
	}
	
	public abstract SimplicialComplex communicationRound(SimplicialComplex sc);
	
	public static CommunicationMechanism createCommunicationMechanism(String name){
		if (name.equals(Constants.ATOMIC_IMMEDIATE_SNAPSHOT))
			return new AtomicImmediateSnapshot();
		return null;
	}
	
	public int getRounds(){
		return rounds;
	}
	
	@Override
	public abstract String toString();
}
