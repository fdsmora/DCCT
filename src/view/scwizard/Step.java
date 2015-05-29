package view.scwizard;

import configuration.Constants;

public abstract class Step {
		
	public static Step createStep(String kind, SCPanel p){
		if (kind.equals(Constants.NUMBER_OF_PROCESSES_STEP)){
			return new NumberOfProcessesStep(p);
		}
	}
}
