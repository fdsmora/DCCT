package view.UI;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import configuration.Constants;

class NextRoundStep extends Step {
	private JButton btnChangeModel = new JButton(Constants.CHANGE_MODEL);
	private static final int MAX_ALLOWED_ROUNDS = 3;
	private JLabel lbMaxReached = new JLabel(Constants.MAX_ROUNDS_REACHED_MSG);
	private int roundCount = 1;
	private JRadioButton rbChromatic = new JRadioButton(Constants.CHROMATIC);
	private JRadioButton rbNonChromatic = new JRadioButton(Constants.NON_CHROMATIC);

	public NextRoundStep(SimplicialComplexPanel p){
		super(p);	
		
		lbMaxReached.setForeground(Color.RED);
		pContent.add(lbMaxReached);
		
		pContent.setBorder(BorderFactory.createTitledBorder(Constants.PROTOCOL_COMPLEX));
		
		btnChangeModel.setActionCommand("h");
		btnChangeModel.addActionListener(this);
		
		ButtonGroup chromGroup = new ButtonGroup();
		chromGroup.add(rbChromatic);
		chromGroup.add(rbNonChromatic);

		pContent.add(lbTitle);
		pContent.add(rbChromatic);
		pContent.add(rbNonChromatic);
		
		rbChromatic.setActionCommand("c");
		rbNonChromatic.setActionCommand("nc");
		
		rbChromatic.addActionListener(this);
		rbNonChromatic.addActionListener(this);
		
		lbTitle.setText("Select simplicial complex's color");
	}
	
	@Override
	public void visit(){
		super.visit();
		roundCount = 1;
		lbMaxReached.setVisible(false);		
		btnBack.setText(Constants.START_OVER);
		btnNext.setText(Constants.NEXT_ROUND);
		scPanel.getpButtons().add(btnChangeModel,2);
		rbChromatic.setSelected(true);
	}
	
	@Override
	public void goBack(){
		scPanel.getpButtons().remove(2);
		Step.resetAllSteps(scPanel);
		Step back = Step.steps.get(NumberOfProcessesStep.class.getName());
		scPanel.setCurrentStep(back);
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
		if (cmd.equals("h")){
			scPanel.getpButtons().remove(2);
			Step next = Step.steps.get(CommunicationModelStep.class.getName());
			scPanel.setCurrentStep(next);
			next.visit();
		}else{
		//	model.setChromatic(cmd=="c");
		}
	}	
		
}
