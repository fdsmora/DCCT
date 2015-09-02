package unam.dcct.view.UI;

/**
 * Provides a global point of access to all steps of the wizard  {@link unam.dcct.view.UI.SimplicialComplexPanel}. 
 * Every reference to a particular step should be obtain from here. 
 * The purpose of this is to maintain state of all steps controls across navigation. 
 * For example, if a control in a step A is given some value and then the user navigates to
 * another step B and then goes back to A, the control must show the last introduced value. 
 * This is similar to a Singleton, but not equal because it provides the ability to 
 * reset all step instances. 
 * 
 * @author Fausto
 *
 */
public enum Steps {
	NumberOfProcessesStep,
	NameColorStep,
	CommunicationMechanismStep,
	NextRoundStep;
	
	private Step step;

	public Step getStep(){
		if (step == null)
			resetAllSteps();
		return step;
	}
	
	/**
	 * Re-creates instances of all steps, thus, reseting all controls and state for each step. 
	 */
	public static void resetAllSteps(){
		Step[] default_steps = new Step[]{new NumberOfProcessesStep(), 
				new NameColorStep(), 
				new CommunicationProtocol(), 
				new NextRoundStep()};
		int i = 0;
		for (Steps s : Steps.values()){
			s.step = default_steps[i++];
		}
	}
}
