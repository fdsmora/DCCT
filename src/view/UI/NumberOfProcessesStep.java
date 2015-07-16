package view.UI;

import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import configuration.Constants;

public class NumberOfProcessesStep extends Step {
	protected JRadioButton rbOneP = new JRadioButton("1");
	protected JRadioButton rbTwoP = new JRadioButton("2");
	protected JRadioButton rbThreeP = new JRadioButton("3");
	protected boolean modified = false;
	protected int n = 0;
	
	public NumberOfProcessesStep(SimplicialComplexPanel p){
		super(p);
		model.reset();
					
		ButtonGroup nprocGroup = new ButtonGroup();
		nprocGroup.add(rbOneP);
		nprocGroup.add(rbTwoP);
		nprocGroup.add(rbThreeP);
		
		pContent.add(lbDesc);
		pContent.add(rbOneP);
		pContent.add(rbTwoP);
		pContent.add(rbThreeP);
		
		rbOneP.setActionCommand(rbOneP.getText());
		rbOneP.addActionListener(this);
		rbTwoP.setActionCommand(rbTwoP.getText());
		rbTwoP.addActionListener(this);
		rbThreeP.setActionCommand(rbThreeP.getText());
		rbThreeP.addActionListener(this);
	}
	
	@Override
	public void visit(){
		super.visit();
		
		if (!modified){
			btnNext.setEnabled(false);
			btnNext.setVisible(true);
		}
		
		lbDesc.setText("Select number of processes");

		btnBack.setVisible(false);
	}
	
	@Override
	public void validateAndExecute(){
		Step next = scPanel.getSteps().get(Constants.NAME_COLOR_STEP);
		scPanel.setCurrentStep(next);
		next.visit();
	}
	@Override
	public void goBack(){
		Step back = scPanel.getSteps().get(Constants.NUMBER_OF_PROCESSES_STEP);
		scPanel.setCurrentStep(back);
		back.visit();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		modified = true;			
		btnNext.setVisible(true);
		btnNext.setEnabled(true);
		
		String command = e.getActionCommand();
		n = Integer.parseInt(command);
	}	
	
	public int getN(){
		return n;
	}
}
