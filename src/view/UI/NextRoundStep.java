package view.UI;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

public class NextRoundStep extends Step {
	private static final String NEXT_ROUND = "Next round";
	private static final String START_OVER = "Start over";
	private static final String CHANGE_MODEL = "Change model";
	private JButton btnNextRound = new JButton(NEXT_ROUND);
	private JButton btnStartOver = new JButton(START_OVER);
	//private JButton btnChangeModel = new JButton(CHANGE_MODEL);
	private static final int MAX_ALLOWED_ROUNDS = 4;
	private int roundCount = 1;
	
	public NextRoundStep(SCPanel p){
		super(p);	
		btnNextRound.setActionCommand("n");
		btnNextRound.addActionListener(this);
		btnStartOver.setActionCommand("s");
		btnStartOver.addActionListener(this);
		//btnChangeModel.setActionCommand("c");
		//btnChangeModel.addActionListener(this);
		
		pContent.add(btnNextRound);
		//pContent.add(btnChangeModel);
		pContent.add(btnStartOver);
	}
	
	@Override
	public void visit(){
		roundCount = 1;
		btnNextRound.setToolTipText("");

		btnNext.setVisible(false);
		
		btnBack.setText(CHANGE_MODEL);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "n"){
			m.executeRound();
			++roundCount;
			if (roundCount >= MAX_ALLOWED_ROUNDS){
				btnNextRound.setEnabled(false);
				btnNextRound.setToolTipText("It is not possible to execute more rounds.");
			}
		}
		else if (command == "s"){
			scPanel.initializeWizard();
		}
	}
	
}
