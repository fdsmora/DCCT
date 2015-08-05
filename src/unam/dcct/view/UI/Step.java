package unam.dcct.view.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import unam.dcct.misc.Constants;
import unam.dcct.model.Model;

abstract class Step implements ActionListener {
	
	protected JLabel lbTitle = new JLabel();
	protected JPanel pContent = new JPanel();
	protected SimplicialComplexPanel scPanel = null;
	protected JButton btnNext =null;
	protected JButton btnBack =null;
	protected Model model = null;
	protected static Map<String, Step> steps ; 	
	
	public Step(SimplicialComplexPanel p){
		this.scPanel = p;
		
		pContent.add(lbTitle);
		pContent.setLayout(new BoxLayout(pContent,BoxLayout.Y_AXIS));
		pContent.setBorder(BorderFactory.createTitledBorder(Constants.INITIAL_COMPLEX));
		
		btnNext = scPanel.getBtnNext();
		btnBack = scPanel.getBtnBack();
		
		model = Model.getInstance();
	}
			
	public void visit(){
		btnNext.setText(Constants.NEXT);
		btnNext.setEnabled(true);
		btnNext.setVisible(true);
		btnBack.setText(Constants.BACK);
		btnBack.setEnabled(true);
		btnBack.setVisible(true);
		
		scPanel.remove(0);
		scPanel.add(pContent,0);
	}
	
	public static void resetAllSteps(SimplicialComplexPanel scPanel){
				
		steps = new HashMap<String, Step>();
		steps.put(NumberOfProcessesStep.class.getName(), new NumberOfProcessesStep(scPanel));
		steps.put(NameColorStep.class.getName(), new NameColorStep(scPanel));
		steps.put(CommunicationModelStep.class.getName(), new CommunicationModelStep(scPanel));
		steps.put(NextRoundStep.class.getName(), new NextRoundStep(scPanel));

	}
	
	public abstract void validateAndExecute();
	
	public void goBack(){
	}

	public void actionPerformed(ActionEvent e) {
	}

}
