package unam.dcct.view.UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import unam.dcct.misc.Constants;

/***
 * Represents the wizard that guides the user through the process of building an initial 
 * simplicial complex and generating protocol complexes using different distributed computing models.
 * This wizards consists of a main content panel that is populated with the current step's controls; and 
 * a bottom panel that contains two buttons: Next and Back, that let user navigate across the wizard
 * steps (optionally each step can add or remove buttons from this panel).
 * @author Fausto Salazar
 *
 */
public class SimplicialComplexPanel extends JPanel implements ActionListener {
	private JPanel pContent = new JPanel();
	private JPanel pButtons = new JPanel();
	private JButton btnNext = new JButton(Constants.NEXT);
	private JButton btnBack = new JButton(Constants.BACK);
	private Step currentStep = null;
	
	// Implementing Singleton design pattern
	private static SimplicialComplexPanel instance = null;
	public static SimplicialComplexPanel getInstance(){
		if (instance == null){
			instance = new SimplicialComplexPanel();
		}
		return instance;
	}
	private SimplicialComplexPanel(){
		// Configuring basic UI controls
		btnNext.addActionListener(this);
		btnNext.setActionCommand(Constants.NEXT);
		btnBack.addActionListener(this);
		btnBack.setActionCommand(Constants.BACK);
		setLayout(new BorderLayout());
		GridLayout gLayout = new GridLayout(0,2);
		pButtons.setLayout(gLayout);
		pButtons.setBorder(BorderFactory.createEtchedBorder());
		pButtons.add(btnBack);
		pButtons.add(btnNext);
		add(pContent, BorderLayout.CENTER);
		add(pButtons, BorderLayout.PAGE_END);


	}
	
	/**
	 * Populates the wizard with the first step's controls
	 */
	public void start(){
		Steps.resetAllSteps();
		currentStep = Steps.NumberOfProcessesStep.getStep();
		currentStep.visit();
	}
	
	/**
	 * Handles the navigation buttons (Next and Back) pressing events. 
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(Constants.NEXT))
			currentStep.validateAndExecute();
		else 
			currentStep.goBack();
	}

	/**
	 * Returns the main content panel that is populated with the current step's controls.
	 * @return The main content panel. 
	 */
	public JPanel getContentPanel() {
		return pContent;
	}

	public void setContentPanel(JPanel contentPanel) {
		this.pContent = contentPanel;
	}

	public JButton getBtnNext() {
		return btnNext;
	}

	public void setBtnNext(JButton btnNext) {
		this.btnNext = btnNext;
	}

	public JButton getBtnBack() {
		return btnBack;
	}

	public void setBtnBack(JButton btnBack) {
		this.btnBack = btnBack;
	}

	public Step getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(Step currentStep) {
		this.currentStep = currentStep;
	}

	public JPanel getButtonsPanel() {
		return pButtons;
	}

	public void setButtonsPanel(JPanel buttonsPanel) {
		this.pButtons = buttonsPanel;
	}
	
}
