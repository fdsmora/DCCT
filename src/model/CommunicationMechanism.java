package model;

import dctopology.SimplicialComplex;
import dctopology.Process;

public interface CommunicationMechanism {
	int t=0;
	int get_t();
	void set_t(int t);
	SimplicialComplex communicationRound(SimplicialComplex sc);
	Process createProcess(int id);
}
