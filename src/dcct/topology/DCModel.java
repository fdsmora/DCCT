package dcct.topology;

public class DCModel {
	protected CommunicationModel communicationModel;
	
	public DCModel(CommunicationModel communicationModel){
		this.communicationModel = communicationModel;
	}
	
	public SimplicialComplex subdivide(SimplicialComplex sc){
		return new SimplicialComplex(communicationModel.communicationRound(sc.getSimplices()));
	}
}
