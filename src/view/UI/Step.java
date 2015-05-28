package view.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Model;

public abstract class Step implements ActionListener{
	protected JLabel lbDesc = new JLabel();
	protected JPanel pContent = new JPanel();
	protected SCPanel scPanel = null;
	protected JButton btnNext =null;
	protected JButton btnBack =null;
	protected Step next = null;
	protected Step back = null;
	protected Model m = null;
	protected static final String NEXT = "Next";
	protected static final String BACK = "Back";
	
	public Step(SCPanel p){
		this.scPanel = p;
		
		pContent.add(lbDesc);
		pContent.setLayout(new BoxLayout(pContent,BoxLayout.Y_AXIS));
		pContent.setBorder(BorderFactory.createTitledBorder("Initial Complex"));
		
		btnNext = scPanel.getBtnNext();
		btnBack = scPanel.getBtnBack();
		
		m = p.getModel();
	}
	
	public void visit(){
		btnNext.setText(NEXT);
		btnNext.setEnabled(true);
		btnNext.setVisible(true);
		btnBack.setText(BACK);
		btnBack.setEnabled(true);
		btnBack.setVisible(true);
		scPanel.getpMain().remove(0);
		scPanel.getpMain().add(pContent,0);
	}
	
	public boolean execute(){ return true;}
	
	public void actionPerformed(ActionEvent arg0) {	
	}
	
	public Step getNext() {
		return next;
	}

	public void setNext(Step next) {
		this.next = next;
	}

	public Step getBack() {
		return back;
	}

	public void setBack(Step back) {
		this.back = back;
	}
}