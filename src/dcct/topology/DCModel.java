package dcct.topology;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import dcct.process.Process;

public class DCModel {
	protected CommunicationModel communicationModel;
	protected SimplicialComplex initialComplex;
	protected SimplicialComplex protocolComplex;
	
	public static List<Color> getColors() {
		return colors;
	}
	
	public static List<Color> getColors(int k){
		return colors.subList(0, k);
	}

	public static void setColors(List<Color> colors) {
		DCModel.colors = colors;
	}

	protected static List<Color> colors = Arrays.asList(Color.WHITE,Color.BLACK,Color.GREEN,Color.RED,
			Color.BLUE,Color.YELLOW, Color.PINK, Color.CYAN, Color.MAGENTA);
	
	public DCModel(CommunicationModel communicationModel){
		this.communicationModel = communicationModel;
	}
	
	public SimplicialComplex subdivide(SimplicialComplex sc){
		return new SimplicialComplex(communicationModel.communicationRound(sc.getSimplices()));
	}
	
	public SimplicialComplex subdivide(){
		if (protocolComplex!=null)
			protocolComplex = subdivide(protocolComplex);
		else if (initialComplex!=null)
			protocolComplex = subdivide(initialComplex);;
		return protocolComplex;
	}
	
	public SimplicialComplex createInicialComplex(int n){
		initialComplex = new SimplicialComplex(createSimplex(n));
		protocolComplex = null;
		return initialComplex;
	}
	
	public static Simplex createSimplex(int n){
		Process[] aP = new Process[n];
		for (int i=0;i<n;i++){
			aP[i]=new Process(i);
		}
		return new Simplex(aP);
	}
	
}
