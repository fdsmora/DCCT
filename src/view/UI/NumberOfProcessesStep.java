package view.UI;

import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

class NumberOfProcessesStep extends Step {
	private JRadioButton rbOneP = new JRadioButton("1");
	private JRadioButton rbTwoP = new JRadioButton("2");
	private JRadioButton rbThreeP = new JRadioButton("3");
	private boolean modified = false;
	private int n = 0;
	
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
	
	public void validateAndExecute(){
		Step next = Step.steps.get(NameColorStep.class.getName());
		scPanel.setCurrentStep(next);
		next.visit();
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
