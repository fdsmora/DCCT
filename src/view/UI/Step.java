package view.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import model.Model;
import configuration.Constants;

public abstract class Step implements ActionListener {
	
	protected JLabel lbDesc = new JLabel();
	protected JPanel pContent = new JPanel();
	protected SCPanel scPanel = null;
	protected JButton btnNext =null;
	protected JButton btnBack =null;
	protected Model model = null;
		
	public Step(SCPanel p){
		this.scPanel = p;
		
		pContent.add(lbDesc);
		pContent.setLayout(new BoxLayout(pContent,BoxLayout.Y_AXIS));
		pContent.setBorder(BorderFactory.createTitledBorder(Constants.INITIAL_COMPLEX));
		//Border b = BorderFactory.createEmptyBorder(10,10,10,10);
		//pContent.setBorder(BorderFactory.createTitledBorder(b, Constants.INITIAL_COMPLEX));
		
		btnNext = scPanel.getBtnNext();
		btnBack = scPanel.getBtnBack();
		
		model = p.getModel();
	}
	
	public void visit(){
		btnNext.setText(Constants.NEXT);
		btnNext.setEnabled(true);
		btnNext.setVisible(true);
		btnBack.setText(Constants.BACK);
		btnBack.setEnabled(true);
		btnBack.setVisible(true);
		scPanel.getpMain().remove(0);
		scPanel.getpMain().add(pContent,0);
	}
	
	public static Step createStep(String kind, SCPanel p){
		if (kind.equals(Constants.NUMBER_OF_PROCESSES_STEP)){
			return new NumberOfProcessesStep(p);
		}
		else if (kind.equals(Constants.NAME_COLOR_STEP)){
			return new NameColorStep(p);
		}
		else if (kind.equals(Constants.COMMUNICATION_MODEL_STEP)){
			return new CommunicationModelStep(p);
		}
		else if (kind.equals(Constants.NEXT_ROUND_STEP)){
			return new NextRoundStep(p);
		}
		return null;
	}

	public void validateAndExecute(){	
	}
	
	public void goBack(){
	}

	public void actionPerformed(ActionEvent e) {
		
	}
}
