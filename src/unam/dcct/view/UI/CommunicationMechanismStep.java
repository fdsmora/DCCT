package unam.dcct.view.UI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import unam.dcct.misc.Constants;
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
	private JComboBox<String> communicationModelOptions;
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
		JLabel lblCommunicationModel = new JLabel("Select communication mechanism:");
		lblCommunicationModel.setLabelFor(communicationModelOptions);
		// In order to properly align all controls to the left, this label, the combo boxes, and ALL top level controls contained 
		// inside pContent must have this property set to this value. If any of these doesn't have its property set to this value, 
		// layout will be ugly. For more information read https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html#alignment 
		// I recommend to read the complete article about BoxLayout (https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html)
		// and in general, the most you can about Swing Layout managers. 
		lblCommunicationModel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		List<String> commMechanisms = new ArrayList<String>(Constants.availableCommunicationMechanisms.keySet());
		String[] commMechanismArray = new String[commMechanisms.size()];
		commMechanisms.toArray(commMechanismArray);
		communicationModelOptions = new JComboBox<String>(new DefaultComboBoxModel<String>(commMechanismArray));
		communicationModelOptions.addActionListener(this);
		communicationModelOptions.setActionCommand("mo");
		// Set visual properties
		communicationModelOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
		communicationModelOptions.setMaximumSize(new Dimension(150,15));

		pContent.add(lblCommunicationModel);
		pContent.add(communicationModelOptions);
		pContent.add(Box.createRigidArea(new Dimension(0,5)));

		displayModelSubOptions(commMechanismArray[0]);
	}

	private void displayModelSubOptions(String selectedMechanism) {	
		JLabel lblCommunicationSubModel = new JLabel("Select communication mechanism's suboptions:");
		lblCommunicationSubModel.setLabelFor(communicationModelSubOptions);
		lblCommunicationSubModel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		communicationModelSubOptions = new JComboBox<String>();
		communicationModelSubOptions.addActionListener(this);
		communicationModelSubOptions.setActionCommand("smo");
		communicationModelSubOptions.setMaximumSize(new Dimension(150,15));
		communicationModelSubOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		populateSubOptions(selectedMechanism);
		
		pContent.add(lblCommunicationSubModel);
		pContent.add(communicationModelSubOptions);
	}
	
	private void populateSubOptions(String selectedMechanism){		
		List<String> subOptions = Constants.availableCommunicationMechanisms.get(selectedMechanism);
		String[] subOptionsArr = new String[subOptions.size()];
		subOptions.toArray(subOptionsArr);
		
		communicationModelSubOptions.setModel(new DefaultComboBoxModel<String>(subOptionsArr));
	}
	
	/**
	 * Adds a subpanel where the user can customize some attributes of the 
	 * visualization, such as the style of brackets that enclose the process view labels.
	 */
	private void createCustomizationsPanel(){
		JPanel pCustomizations = new JPanel();
		pCustomizations.setLayout(new BoxLayout(pCustomizations,BoxLayout.LINE_AXIS));
		pCustomizations.setBorder(BorderFactory.createTitledBorder("Personalize"));
		pCustomizations.setAlignmentX(Component.LEFT_ALIGNMENT);
		
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
		
		pContent.add(Box.createRigidArea(new Dimension(0,10)));
		pContent.add(pCustomizations);
	}
	
	/**
	 * Generates the protocol complex for the first communication round 
	 * using the distributed computing model specified in this step.  
	 */
	public void validateAndExecute(){
		String selectedMech = (String)communicationModelSubOptions.getSelectedItem();
		model.setCommunicationMechanism(selectedMech);
		// As this is the complex of the first round, we dismiss any previous generated protocol complexes. 
		model.setProtocolComplex(null);
		model.executeRound();		
		
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
		Steps.resetAllSteps();
		Step back = Steps.NumberOfProcessesStep.getStep();
		scPanel.setCurrentStep(back);
		back.visit();
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "mo"){
			populateSubOptions((String)communicationModelOptions.getSelectedItem());
		}
	}

}
