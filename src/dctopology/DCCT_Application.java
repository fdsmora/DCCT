package dctopology;

import model.Model;
import view.View;
import view.jRealityView;

public class DCCT_Application {
	public static void main(String[] args){
		Model model = new Model();
		View view = new jRealityView(model);
		view.start();
	}
}
