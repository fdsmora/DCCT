package view.UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import model.Model;
import configuration.Constants;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
//import de.jtem.jrworkspace.plugin.flavor.HelpFlavor;

/***
 * 
 * @author Fausto
 *
 */
public class SCPanel extends ViewShrinkPanelPlugin implements ActionListener {
	protected Model model = null;
	protected JPanel pContent = new JPanel();
	protected JPanel pMain = new JPanel();
	protected JPanel pButtons = new JPanel();
	protected JButton btnNext = new JButton(Constants.NEXT);
	protected JButton btnBack = new JButton(Constants.BACK);
	protected Step startStep = null;
	protected Step currentStep = null;
	protected Map<String, Step> steps;
	
	public SCPanel(Model m){
		this.model = m;
		btnNext.addActionListener(this);
		btnNext.setActionCommand(Constants.NEXT);
		btnBack.addActionListener(this);
		btnBack.setActionCommand(Constants.BACK);
		
		pMain.setLayout(new BorderLayout());
		
		GridLayout gLayout = new GridLayout(0,2);
		pButtons.setLayout(gLayout);
		
		pButtons.setBorder(BorderFactory.createEtchedBorder());
		
		pButtons.add(btnBack);
		pButtons.add(btnNext);
		
		pMain.add(pContent, BorderLayout.CENTER);
		pMain.add(pButtons, BorderLayout.PAGE_END);
		
		setInitialPosition(SHRINKER_LEFT);
		
		initialize();
		currentStep.visit();
		
		getShrinkPanel().add(pMain);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(Constants.NEXT))
			currentStep.validateAndExecute();
		else 
			currentStep.goBack();
//		currentStep = cmd.equals(Constants.NEXT) ?
//							currentStep.getNext() : currentStep.getBack();
//		if (currentStep!=null)
//			currentStep.visit();
		
//		String help = this.getHelpDocument();
//		String x = this.getHelpPath();
//		String y = this.getHelpStyleSheet();
//		String z =  this.getHelpTitle();
//		
//		this.setHelpListener(new HelpListener(){
//
//			@Override
//			public void showHelpPage(HelpFlavor arg0) {
//				//this.showHelpPage(arg0);
//				SCPanel.this.helpCalled();
//				
//			}});
	}
	
	public void initialize(){
		steps = new HashMap<String, Step>();
		steps.put(Constants.NUMBER_OF_PROCESSES_STEP, Step.createStep(Constants.NUMBER_OF_PROCESSES_STEP, this));
		steps.put(Constants.NAME_COLOR_STEP, Step.createStep(Constants.NAME_COLOR_STEP, this));
		steps.put(Constants.COMMUNICATION_MODEL_STEP, Step.createStep(Constants.COMMUNICATION_MODEL_STEP, this));
		steps.put(Constants.CHROMATIC_STEP, Step.createStep(Constants.CHROMATIC_STEP, this));
		steps.put(Constants.NEXT_ROUND_STEP, Step.createStep(Constants.NEXT_ROUND_STEP, this));
		startStep = steps.get(Constants.NUMBER_OF_PROCESSES_STEP);
		currentStep = startStep;
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
	
	public JPanel getpMain() {
		return pMain;
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	public PluginInfo getPluginInfo() {
		return new PluginInfo("Simplicial Complex Panel");
	}
	
	public void install(Controller c) throws Exception{
		super.install(c);
	}
	
	public void uninstall(Controller c) throws Exception {
		super.uninstall(c);
	}

	public Map<String, Step> getSteps() {
		return steps;
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
