package view.UI;

import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class ChromaticStep extends Step {
	
	JRadioButton rbChromatic = new JRadioButton("Chromatic");
	JRadioButton rbNonChromatic = new JRadioButton("Non chromatic");
	boolean modified = false;

	public ChromaticStep(SCPanel p){
		super(p);
					
		ButtonGroup chromGroup = new ButtonGroup();
		chromGroup.add(rbChromatic);
		chromGroup.add(rbNonChromatic);

		pContent.add(lbDesc);
		pContent.add(rbChromatic);
		pContent.add(rbNonChromatic);
		
		rbChromatic.setActionCommand("c");
		rbNonChromatic.setActionCommand("nc");
		
		rbChromatic.addActionListener(this);
		rbNonChromatic.addActionListener(this);
	}
	
	public void visit(){
		super.visit();
		
		if (!modified){
			btnNext.setEnabled(false);
			btnNext.setVisible(true);
		}
		
		btnBack.setVisible(false);
		lbDesc.setText("Select simplicial complex's color");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		modified = true;
		btnNext.setEnabled(true);			
		m.setChromatic(e.getActionCommand()=="c");	
	}	

}