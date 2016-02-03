package unam.dcct.view.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import unam.dcct.misc.Constants;
import unam.dcct.model.Model;

/**
 * Abstract class that represents a  {@link unam.dcct.view.UI.SimplicialComplexPanel} wizard step.
 * An step is a set of UI controls and the logic for handling the data
 * that flows through these controls and handling events. Each steps
 * implements the responsibilities of both the View and Controller components
 * of an MVC design. 
 * 
 * This class has all the controls common to all steps. 
 * 
 * Each particular step is represented by a class that inherits from this class. 
 * 
 * When each step is visited, its controls are loaded into the SimplicialContentPanel's 
 * main content panel and displayed. 
 * @author Fausto Salazar
 *
 */
abstract class Step implements ActionListener {
	
	protected JLabel lbTitle = new JLabel();
	protected JPanel pContent = new JPanel();
	protected SimplicialComplexPanel scPanel = null;
	protected JButton btnNext =null;
	protected JButton btnBack =null;
	protected Model model = null;
	
	/**
	 * Sets up the controls common to all steps. 
	 */
	public Step(){
		this.scPanel = SimplicialComplexPanel.getInstance();
		
		pContent.add(lbTitle);
		pContent.setLayout(new BoxLayout(pContent,BoxLayout.PAGE_AXIS));
		pContent.setBorder(BorderFactory.createTitledBorder(Constants.INITIAL_COMPLEX));
		
		btnNext = scPanel.getBtnNext();
		btnBack = scPanel.getBtnBack();
		
		model = Model.getInstance();
	}
			
	/**
	 * Loads the current step controls into the main content panel of 'SimplicialComplexPanel' wizard. 
	 */
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
	
	/**
	 * Subclasses should provide logic that validates input data and perform some processing. 
	 */
	public abstract void validateAndExecute();
	
	/**
	 * Takes the user to the previous step. Subclasses that override this method should define what is the previous step. 
	 */
	public void goBack(){
	}

	public void actionPerformed(ActionEvent e) {
	}
	

}
