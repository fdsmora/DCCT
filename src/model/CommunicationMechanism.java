package model;

import model.AtomicImmediateSnapshotModel.AtomicImmediateSnapshot;
import configuration.Configuration;
import dctopology.SimplicialComplex;

public abstract class CommunicationMechanism {
	protected int t=0;

	public int get_t(){
		return t;
	};
	public void set_t(int t){
		this.t=t;
	}
	
	public abstract SimplicialComplex communicationRound(SimplicialComplex sc);
	
	public static CommunicationMechanism createCommunicationMechanism(String name){
		if (name == Configuration.ATOMIC_IMMEDIATE_SNAPSHOT)
			return new AtomicImmediateSnapshot();
		return null;
	}
}
