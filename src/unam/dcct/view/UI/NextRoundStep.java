package unam.dcct.view.UI;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import unam.dcct.misc.Constants;

class NextRoundStep extends Step {
	private JButton btnChangeModel = new JButton(Constants.CHANGE_MODEL);
	private JLabel lbMaxReached = new JLabel(Constants.MAX_ROUNDS_REACHED_MSG);
	private int roundCount = 1;
	private JRadioButton rbChromatic = new JRadioButton(Constants.CHROMATIC);
	private JRadioButton rbNonChromatic = new JRadioButton(Constants.NON_CHROMATIC);
	
	public NextRoundStep(){
		super();	
		
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
		scPanel.getButtonsPanel().add(btnChangeModel,2);
		rbChromatic.setSelected(true);
	}
	
	@Override
	public void goBack(){
		scPanel.getButtonsPanel().remove(2);
		Steps.resetAllSteps();
		Step back = Steps.NumberOfProcessesStep.getStep();
		scPanel.setCurrentStep(back);
		back.visit();
	}
	
	@Override
	public void validateAndExecute(){
		int capture = 0;
		if (roundCount==2) 
			capture = JOptionPane.showConfirmDialog(null,"Performance of the application may decrease after executing this round. Do you want to proceed?");
		if (capture==0) {
			model.executeRound();
			++roundCount;
		}
		if (roundCount>=Constants.MAX_ALLOWED_ROUNDS){
			btnNext.setEnabled(false);
			lbMaxReached.setVisible(true);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("h")){
			scPanel.getButtonsPanel().remove(2);
			Step next = Steps.CommunicationMechanismStep.getStep();
			scPanel.setCurrentStep(next);
			next.visit();
		}else{
			model.setChromatic(cmd=="c");
		}
	}	

}
