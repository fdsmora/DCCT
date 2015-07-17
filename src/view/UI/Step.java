package view.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Model;
import configuration.Constants;

abstract class Step implements ActionListener {
	
	protected JLabel lbDesc = new JLabel();
	protected JPanel pContent = new JPanel();
	protected SimplicialComplexPanel scPanel = null;
	protected JButton btnNext =null;
	protected JButton btnBack =null;
	protected Model model = null;
	protected static Map<String, Step> steps; 
	
	protected static Step instance = null;
	public static Step getInstance(SimplicialComplexPanel p){
		return instance;
	}	
	
	public Step(SimplicialComplexPanel p){
		this.scPanel = p;
		
		pContent.add(lbDesc);
		pContent.setLayout(new BoxLayout(pContent,BoxLayout.Y_AXIS));
		pContent.setBorder(BorderFactory.createTitledBorder(Constants.INITIAL_COMPLEX));
		
		btnNext = scPanel.getBtnNext();
		btnBack = scPanel.getBtnBack();
		
		model = Model.getInstance();
	}
		
	public void visit(){
		btnNext.setText(Constants.NEXT);
		btnNext.setEnabled(true);
		btnNext.setVisible(true);
		btnBack.setText(Constants.BACK);
		btnBack.setEnabled(true);
		btnBack.setVisible(true);
		
		scPanel.remove(0);
		scPanel.add(pContent,0);
	}
	
//	public static Step createStep(String kind, SimplicialComplexPanel p){
//		if (kind.equals(Constants.NUMBER_OF_PROCESSES_STEP)){
//			return new NumberOfProcessesStep(p);
//		}
//		else if (kind.equals(Constants.NAME_COLOR_STEP)){
//			return new NameColorStep(p);
//		}
//		else if (kind.equals(Constants.COMMUNICATION_MODEL_STEP)){
//			return new CommunicationModelStep(p);
//		}
//		else if (kind.equals(Constants.NEXT_ROUND_STEP)){
//			return new NextRoundStep(p);
//		}
//		return null;
//	}
	
	public static void initializeAllSteps(SimplicialComplexPanel scPanel){
						
//		steps = new HashMap<String, Step>();
//		steps.put(Constants.NUMBER_OF_PROCESSES_STEP, Step.createStep(Constants.NUMBER_OF_PROCESSES_STEP, this));
//		steps.put(Constants.NAME_COLOR_STEP, Step.createStep(Constants.NAME_COLOR_STEP, this));
//		steps.put(Constants.COMMUNICATION_MODEL_STEP, Step.createStep(Constants.COMMUNICATION_MODEL_STEP, this));
//		steps.put(Constants.CHROMATIC_STEP, Step.createStep(Constants.CHROMATIC_STEP, this));
//		steps.put(Constants.NEXT_ROUND_STEP, Step.createStep(Constants.NEXT_ROUND_STEP, this));
//		startStep = steps.get(Constants.NUMBER_OF_PROCESSES_STEP);
//		currentStep = startStep;
		
		steps = new HashMap<String, Step>();
		steps.put(NumberOfProcessesStep.class.getName(), new NumberOfProcessesStep(scPanel));
		steps.put(NameColorStep.class.getName(), new NameColorStep(scPanel));
		steps.put(CommunicationModelStep.class.getName(), new CommunicationModelStep(scPanel));
		steps.put(NextRoundStep.class.getName(), new NextRoundStep(scPanel));

	}
	
//	public static Map<String, Step> getSteps(){
//		if (steps==null)
//			initializeAllSteps();
//		return steps;
//	}
	

	public void validateAndExecute(){	
	}
	
	public void goBack(){
	}

	public void actionPerformed(ActionEvent e) {
		
	}

}
