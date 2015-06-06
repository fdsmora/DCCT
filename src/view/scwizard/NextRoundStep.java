package view.scwizard;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

import configuration.Constants;

public class NextRoundStep extends Step {
	protected JButton btnNextRound = new JButton(Constants.NEXT_ROUND);
	protected JButton btnChangeModel = new JButton(Constants.CHANGE_MODEL);
	protected static final int MAX_ALLOWED_ROUNDS = 3;
	protected JLabel lbMaxReached = new JLabel(Constants.MAX_ROUNDS_REACHED_MSG);
	protected int roundCount = 1;
	
	public NextRoundStep(SCPanel p){
		super(p);	
		
		lbMaxReached.setForeground(Color.RED);
		pContent.add(lbMaxReached);
		
		btnNextRound.setActionCommand("n");
		btnNextRound.addActionListener(this);
		btnChangeModel.setActionCommand("c");
		btnChangeModel.addActionListener(this);
		//btnChangeModel.setActionCommand("c");
		//btnChangeModel.addActionListener(this);
		
		pContent.add(btnNextRound);
		pContent.add(btnChangeModel);
//		pContent.add(btnStartOver);
	}
	
	@Override
	public void visit(){
		super.visit();
		roundCount = 1;
		btnNext.setVisible(false);
		lbMaxReached.setVisible(false);		
		btnBack.setText(Constants.START_OVER);
	}
	
	@Override
	public Step getBack(){
		scPanel.initialize();
		if (back == null)
			back = scPanel.getSteps().get(Constants.NUMBER_OF_PROCESSES_STEP);
		return back;
	}
	
	@Override
	public Step getNext(){

		if (next == null)
			next = scPanel.getSteps().get(Constants.NEXT_ROUND_STEP);
		return back;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("n")){
			model.executeRound();
			++roundCount;
			if (roundCount>=MAX_ALLOWED_ROUNDS){
				btnNextRound.setEnabled(false);
				lbMaxReached.setVisible(true);
			}
		}else
			scPanel.getSteps().get(Constants.COMMUNICATION_MODEL_STEP).visit();
	}	
		
}
