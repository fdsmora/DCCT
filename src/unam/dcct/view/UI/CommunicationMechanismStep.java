package unam.dcct.view.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.LayoutManager2;
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
import javax.swing.SwingConstants;
import javax.swing.border.Border;

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
		
		displayModelOptions();
		
		createCustomizationsPanel();

	}
	
	@Override
	public void visit(){
		super.visit();
		btnNext.setText(Constants.EXECUTE_ROUND);
		btnBack.setText(Constants.START_OVER);
	}
	
	private void displayModelOptions() {
		JLabel lblCommunicationModel = new JLabel("Select communication mechanism dsfsa:");
		lblCommunicationModel.setLabelFor(communicationModelOptions);
		// In order to properly align all controls to the left, this label, the combo boxes, and ALL top level controls contained 
		// inside pContent must have this property set to this value. If any of these doesn't have its property set to this value, 
		// layout will be ugly. For more information read https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html#alignment 
		// I recommend to read the complete article about BoxLayout (https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html)
		// and in general, the most you can about Swing Layout managers. 
		lblCommunicationModel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		communicationModelOptions = new JComboBox<CommunicationMechanism>(CommunicationMechanism.values());
		communicationModelOptions.addActionListener(this);
		communicationModelOptions.setActionCommand("mo");
		communicationModelOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
	
		communicationModelOptions.setMaximumSize(new Dimension(150,15));

		pContent.add(lblCommunicationModel);
		pContent.add(communicationModelOptions);
		pContent.add(Box.createRigidArea(new Dimension(0,10)));

		displayModelSubOptions(CommunicationMechanism.values()[0]);
	}

	private void displayModelSubOptions(CommunicationMechanism selectedModel) {	
		JLabel lblCommunicationSubModel = new JLabel("Select communication mechanism's suboptions:");
		lblCommunicationSubModel.setLabelFor(communicationModelSubOptions);
		lblCommunicationSubModel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		communicationModelSubOptions = new JComboBox<String>();
		communicationModelSubOptions.addActionListener(this);
		communicationModelSubOptions.setActionCommand("smo");
		communicationModelSubOptions.setMaximumSize(new Dimension(150,15));
		communicationModelSubOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		List<String> subOptions = selectedModel.subModels();
		String[] subOptionsArr = new String[subOptions.size()];
		subOptions.toArray(subOptionsArr);
		
		communicationModelSubOptions.setModel(new DefaultComboBoxModel<String>(subOptionsArr));
		
		pContent.add(lblCommunicationSubModel);
		pContent.add(communicationModelSubOptions);
	}
	
	/**
	 * Adds a subpanel where the user can customize some attributes of the 
	 * visualization, such as the style of brackets that enclose the process view labels.
	 * @return Returns the customizations subpanel. 
	 */
	private void createCustomizationsPanel(){
		JPanel pCustomizations = new JPanel();
		pCustomizations.setLayout(new BoxLayout(pCustomizations,BoxLayout.LINE_AXIS));
		pCustomizations.setBorder(BorderFactory.createTitledBorder("Personalize"));
		pCustomizations.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel lbSelectBrackets = new JLabel("Select brackets for process views");
		//lbSelectBrackets.setAlignmentX(Component.LEFT_ALIGNMENT);
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
		
		pContent.add(pCustomizations);
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
