package unam.dcct.view.UI;

import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * Represents the step in the {@link unam.dcct.view.UI.SimplicialComplexPanel} wizard
 * that lets the user select the number of processes that the initial complex will contain. 
 * @author Fausto
 *
 */
class NumberOfProcessesStep extends Step {
	private JRadioButton rbOneP = new JRadioButton("1");
	private JRadioButton rbTwoP = new JRadioButton("2");
	private JRadioButton rbThreeP = new JRadioButton("3");
	private int selected_n = 0;
	
	public NumberOfProcessesStep(){
		super();
		// Reset the state of the model.
		model.reset();
					
		ButtonGroup nprocGroup = new ButtonGroup();
		nprocGroup.add(rbOneP);
		nprocGroup.add(rbTwoP);
		nprocGroup.add(rbThreeP);
		
		pContent.add(lbTitle);
		pContent.add(rbOneP);
		pContent.add(rbTwoP);
		pContent.add(rbThreeP);
		
		rbOneP.setActionCommand(rbOneP.getText());
		rbOneP.addActionListener(this);
		rbTwoP.setActionCommand(rbTwoP.getText());
		rbTwoP.addActionListener(this);
		rbThreeP.setActionCommand(rbThreeP.getText());
		rbThreeP.addActionListener(this);
		
		rbThreeP.setSelected(true);
		selected_n = 3;
	}
	
	@Override
	public void visit(){
		super.visit();
		
		lbTitle.setText("Select number of processes");

		btnBack.setVisible(false);
	}
	
	/**
	 * Just takes the user to the next step {@link unam.dcct.view.NameColorStep}
	 */
	public void validateAndExecute(){
		//Step next = Step.steps.get(NameColorStep.class.getName());
		Step next = Steps.NameColorStep.getStep();
		scPanel.setCurrentStep(next);
		next.visit();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		selected_n = Integer.parseInt(command);
	}	
	

	public int getSelectedNumberOfProcesses(){
		return selected_n;
	}

}
