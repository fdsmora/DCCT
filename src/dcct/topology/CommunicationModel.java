package dcct.topology;

import java.util.Set;

public interface CommunicationModel {
	Set<Simplex> generateNewSimplices(Set<Simplex> simplices);
	
}
