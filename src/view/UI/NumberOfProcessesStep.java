package view.UI;

import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class NumberOfProcessesStep extends Step {

	JRadioButton rbOneP = new JRadioButton("1");
	JRadioButton rbTwoP = new JRadioButton("2");
	JRadioButton rbThreeP = new JRadioButton("3");
	boolean modified = false;
	
	public NumberOfProcessesStep(SCPanel p){
		super(p);
					
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
		
		btnNext.setEnabled(false);

	}
	
	public void visit(){
		super.visit();
		
		if (!modified){
			btnNext.setEnabled(false);
			btnNext.setVisible(true);
		}
		
		lbDesc.setText("Select number of processes");
		
		btnNext.setVisible(true);
		btnBack.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		modified = true;			
		btnNext.setVisible(true);
		btnNext.setEnabled(true);
		
		String command = e.getActionCommand();
		m.setN(Integer.parseInt(command));
	}	
}
