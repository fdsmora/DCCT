package dcct.topology;

import java.util.Set;

public interface CommunicationModel {
	Set<Simplex> communicationRound(Set<Simplex> simplices);
	
}
