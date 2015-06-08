package view.scwizard;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import configuration.Configuration;
import configuration.Constants;

public class CommunicationModelStep extends Step {
	protected JComboBox<String> communicationModelOptions = new JComboBox<String>();
	protected JComboBox<String> communicationModelSubOptions = new JComboBox<String>();
	
	public CommunicationModelStep(SCPanel p){
		super(p);
		
		pContent.setBorder(BorderFactory.createTitledBorder("Protocol Complex"));
		
		communicationModelOptions.addActionListener(this);
		communicationModelOptions.setActionCommand("mo");
		communicationModelSubOptions.addActionListener(this);
		communicationModelSubOptions.setActionCommand("smo");
		
		pContent.add(communicationModelOptions);
		pContent.add(communicationModelSubOptions);
		
		displayModelOptions();
	}
	
	@Override
	public void visit(){
		super.visit();
		btnNext.setText(Constants.EXECUTE_ROUND);
		btnBack.setText(Constants.START_OVER);
	}
	
	protected void displayModelOptions() {			
		List<String> options = new ArrayList<String>(Configuration.availableCommunicationModels.keySet());
		String[] optionsArr = new String[options.size()];
		options.toArray(optionsArr);
		communicationModelOptions.setModel(new DefaultComboBoxModel<String>(optionsArr));
		
		displayModelSubOptions(options.get(0));
	}

	protected void displayModelSubOptions(String selectedModel) {			
		List<String> subOptions = Configuration.availableCommunicationModels.get(Configuration.SHARED_MEMORY);
		
		String[] subOptionsArr = new String[subOptions.size()];
		subOptions.toArray(subOptionsArr);
		
		communicationModelSubOptions.setModel(new DefaultComboBoxModel<String>(subOptionsArr));
	}
	
	@Override
	public void validateAndExecute(){
		String cModel = (String)communicationModelSubOptions.getSelectedItem();
		model.setCommunicationMechanism(cModel);
		model.setProtocolComplex(null);
		model.executeRound();		
		
		Step nextStep = scPanel.getSteps().get(Constants.NEXT_ROUND_STEP);
		scPanel.setCurrentStep(nextStep);
		nextStep.visit();	
	}
	
	@Override
	public void goBack(){
		scPanel.initialize();
		Step back = scPanel.getSteps().get(Constants.NUMBER_OF_PROCESSES_STEP);
		back.visit();
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "mo"){
			displayModelSubOptions((String)communicationModelOptions.getSelectedItem());
		}
	}
}
