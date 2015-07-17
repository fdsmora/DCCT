package dctopology;

import model.Model;
import view.View;
import view.jRealityView;

public class DCCT_Application {
	public static void main(String[] args){
		View view = jRealityView.getInstance();
		view.start();
	}
}
