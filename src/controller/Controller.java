package controller;
import view.View;
import view.jRealityView;
import model.Model;

public class Controller {

	public static void main(String[] args){
		Model model = new Model();
//		model.setCommunicationMechanism(new AtomicImmediateSnapshot());
		Controller controller = new Controller();
		View view = new jRealityView(model, controller);
		view.start();
	}
}
