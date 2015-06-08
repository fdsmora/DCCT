package view.scwizard;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

import configuration.Constants;

public class NextRoundStep extends Step {
	protected JButton btnChangeModel = new JButton(Constants.CHANGE_MODEL);
	protected static final int MAX_ALLOWED_ROUNDS = 3;
	protected JLabel lbMaxReached = new JLabel(Constants.MAX_ROUNDS_REACHED_MSG);
	protected int roundCount = 1;
	
	public NextRoundStep(SCPanel p){
		super(p);	
		
		lbMaxReached.setForeground(Color.RED);
		pContent.add(lbMaxReached);
		
		btnChangeModel.setActionCommand("c");
		btnChangeModel.addActionListener(this);
		
	}
	
	@Override
	public void visit(){
		super.visit();
		roundCount = 1;
		lbMaxReached.setVisible(false);		
		btnBack.setText(Constants.START_OVER);
		btnNext.setText(Constants.NEXT_ROUND);
		scPanel.getpButtons().add(btnChangeModel,2);
	}
	
	@Override
	public void goBack(){
		scPanel.getpButtons().remove(2);
		scPanel.initialize();
		Step back = scPanel.getSteps().get(Constants.NUMBER_OF_PROCESSES_STEP);
		back.visit();
	}
	
	@Override
	public void validateAndExecute(){
		model.executeRound();
		++roundCount;
		if (roundCount>=MAX_ALLOWED_ROUNDS){
			btnNext.setEnabled(false);
			lbMaxReached.setVisible(true);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("c")){
			scPanel.getpButtons().remove(2);
			Step next = scPanel.getSteps().get(Constants.COMMUNICATION_MODEL_STEP);
			scPanel.currentStep=next;
			next.visit();
		}
	}	
		
}
