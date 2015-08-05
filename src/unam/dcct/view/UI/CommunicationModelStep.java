package unam.dcct.view.UI;

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

class CommunicationModelStep extends Step {
	protected JComboBox<String> communicationModelOptions = new JComboBox<String>();
	protected JComboBox<String> communicationModelSubOptions = new JComboBox<String>();
	
	public CommunicationModelStep(SimplicialComplexPanel p){
		super(p);
		
		pContent.setBorder(BorderFactory.createTitledBorder(Constants.PROTOCOL_COMPLEX));
		
		communicationModelOptions.addActionListener(this);
		communicationModelOptions.setActionCommand("mo");
		communicationModelSubOptions.addActionListener(this);
		communicationModelSubOptions.setActionCommand("smo");
		
		JPanel pCustomizations = createCustomizationsPanel();
		
		pContent.add(communicationModelOptions);
		pContent.add(communicationModelSubOptions);
		pContent.add(pCustomizations);

		displayModelOptions();
	}
	
	@Override
	public void visit(){
		super.visit();
		btnNext.setText(Constants.EXECUTE_ROUND);
		btnBack.setText(Constants.START_OVER);
	}
	
	private void displayModelOptions() {			
		List<String> options = new ArrayList<String>(Constants.availableCommunicationModels.keySet());
		String[] optionsArr = new String[options.size()];
		options.toArray(optionsArr);
		communicationModelOptions.setModel(new DefaultComboBoxModel<String>(optionsArr));
		
		displayModelSubOptions(options.get(0));
	}

	private void displayModelSubOptions(String selectedModel) {			
		List<String> subOptions = Constants.availableCommunicationModels.get(Constants.SHARED_MEMORY);
		
		String[] subOptionsArr = new String[subOptions.size()];
		subOptions.toArray(subOptionsArr);
		
		communicationModelSubOptions.setModel(new DefaultComboBoxModel<String>(subOptionsArr));
	}
	
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
	
	@Override
	public void validateAndExecute(){
		String cModel = (String)communicationModelSubOptions.getSelectedItem();
		model.setCommunicationMechanism(cModel);
		model.setProtocolComplex(null);
		model.executeRound();		
		
		Step nextStep = Step.steps.get(NextRoundStep.class.getName());
		scPanel.setCurrentStep(nextStep);
		nextStep.visit();	
	}
	
	@Override
	public void goBack(){
		Step.resetAllSteps(scPanel);
		Step back = Step.steps.get(NumberOfProcessesStep.class.getName());
		scPanel.setCurrentStep(back);
		back.visit();
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "mo"){
			displayModelSubOptions((String)communicationModelOptions.getSelectedItem());
		}
	}
}
