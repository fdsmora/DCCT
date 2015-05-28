package view.UI;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import configuration.Configuration;


public class CommunicationModelStep extends Step {
	
	JComboBox<String> communicationModelOptions = new JComboBox<String>();
	JComboBox<String> communicationModelSubOptions = new JComboBox<String>();
	static final String EXECUTE_ROUND = "Execute round!";
	static final String START_OVER = "Start over";
	
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
	
	public void visit(){
		super.visit();
		
		btnNext.setText(EXECUTE_ROUND);
		btnBack.setText(START_OVER);
		
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
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "mo"){
			displayModelSubOptions((String)communicationModelOptions.getSelectedItem());
		}
	}
	
	@Override
	public boolean execute(){
		String cModel = (String)communicationModelSubOptions.getSelectedItem();
		m.setCommunicationMechanism(cModel);
		m.setProtocolComplex(null);
		m.executeRound();
		return true;
	}
}