package view.UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import configuration.Constants;

/***
 * 
 * @author Fausto Salazar
 *
 */
public class SimplicialComplexPanel extends JPanel implements ActionListener {
//	private Model model = null;
	private JPanel pContent = new JPanel();
	private JPanel pButtons = new JPanel();
	private JButton btnNext = new JButton(Constants.NEXT);
	private JButton btnBack = new JButton(Constants.BACK);
	//private Step startStep = null;
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
		// Configuring UI controls
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
		
		Step.resetAllSteps(this);
		currentStep = Step.steps.get(NumberOfProcessesStep.class.getName());
		currentStep.visit();
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(Constants.NEXT))
			currentStep.validateAndExecute();
		else 
			currentStep.goBack();
	}
	

	public JPanel getpContent() {
		return pContent;
	}

	public void setpContent(JPanel pContent) {
		this.pContent = pContent;
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

	public JPanel getpButtons() {
		return pButtons;
	}

	public void setpButtons(JPanel pButtons) {
		this.pButtons = pButtons;
	}
	
}
