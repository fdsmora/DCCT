package unam.dcct.view.UI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import unam.dcct.misc.Constants;
import unam.dcct.misc.Constants.CommunicationMechanism;
import unam.dcct.misc.Constants.ProcessViewBrackets;
import unam.dcct.model.Model;

/**
 * Represents the step in the {@link unam.dcct.view.UI.SimplicialComplexPanel} wizard
 * that lets the user specify the distributed computing model on which the protocol 
 * complex will be generated. 
 * @author Fausto
 *
 */
class CommunicationMechanismStep extends Step {
	private JComboBox<CommunicationMechanism> communicationModelOptions;
	private JComboBox<String> communicationModelSubOptions;
	
	public CommunicationMechanismStep(){
		super();
		
		pContent.setBorder(BorderFactory.createTitledBorder(Constants.PROTOCOL_COMPLEX));
		
		JPanel pCustomizations = createCustomizationsPanel();
		displayModelOptions();
		
		pContent.add(communicationModelOptions);
		pContent.add(communicationModelSubOptions);
		pContent.add(pCustomizations);

	}
	
	@Override
	public void visit(){
		super.visit();
		btnNext.setText(Constants.EXECUTE_ROUND);
		btnBack.setText(Constants.START_OVER);
	}
	
	private void displayModelOptions() {			

		communicationModelOptions = new JComboBox<CommunicationMechanism>(CommunicationMechanism.values());
		communicationModelOptions.addActionListener(this);
		communicationModelOptions.setActionCommand("mo");
		
		displayModelSubOptions(CommunicationMechanism.values()[0]);
	}

	private void displayModelSubOptions(CommunicationMechanism selectedModel) {	
		communicationModelSubOptions = new JComboBox<String>();
		communicationModelSubOptions.addActionListener(this);
		communicationModelSubOptions.setActionCommand("smo");
		
		List<String> subOptions = selectedModel.subModels();
		String[] subOptionsArr = new String[subOptions.size()];
		subOptions.toArray(subOptionsArr);
		
		communicationModelSubOptions.setModel(new DefaultComboBoxModel<String>(subOptionsArr));
	}
	
	/**
	 * Adds a subpanel where the user can customize some attributes of the 
	 * visualization, such as the style of brackets that enclose the process view labels.
	 * @return Returns the customizations subpanel. 
	 */
	private JPanel createCustomizationsPanel(){
		new JPanel();
		JPanel pCustomizations = new JPanel();
		pCustomizations.setLayout(new BoxLayout(pCustomizations,BoxLayout.LINE_AXIS));
		pCustomizations.setBorder(BorderFactory.createTitledBorder("Personalize"));
		
		JLabel lbSelectBrackets = new JLabel("Select brackets for process views");
		JComboBox<ProcessViewBrackets> cbBrackets = new JComboBox<ProcessViewBrackets>(ProcessViewBrackets.values());
		
		cbBrackets.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Model m = Model.getInstance();
				m.setSelectedBrackets((ProcessViewBrackets)cbBrackets.getSelectedItem());
			}
			
		});
		cbBrackets.setActionCommand("br");
		
		pCustomizations.add(lbSelectBrackets);
		pCustomizations.add(Box.createRigidArea(new Dimension(10,0)));
		pCustomizations.add(cbBrackets);
		
		return pCustomizations;
	}
	
	/**
	 * Generates the protocol complex for the first communication round 
	 * using the distributed computing model specified in this step.  
	 */
	public void validateAndExecute(){
		String cModel = (String)communicationModelSubOptions.getSelectedItem();
		model.setCommunicationMechanism(cModel);
		model.setProtocolComplex(null);
		model.executeRound();		
		
		//Step nextStep = Step.steps.get(NextRoundStep.class.getName());
		Step next = Steps.NextRoundStep.getStep();
		scPanel.setCurrentStep(next);
		next.visit();	
	}
	
	/**
	 * Takes the user to the first step {@link unam.dcct.view.UI.NumberOfProcessesStep},
	 * resetting the state of all wizard's controls.
	 */
	@Override
	public void goBack(){
		//Step.resetAllSteps(scPanel);
		Steps.resetAllSteps();
		//Step back = Step.steps.get(NumberOfProcessesStep.class.getName());
		Step back = Steps.NumberOfProcessesStep.getStep();
		scPanel.setCurrentStep(back);
		back.visit();
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "mo"){
			displayModelSubOptions((CommunicationMechanism)communicationModelOptions.getSelectedItem());
		}
	}

}
