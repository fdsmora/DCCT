package view.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import model.Model;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

public class SCPanel extends ViewShrinkPanelPlugin implements ActionListener {

	protected Model model = null;
	protected JPanel pContent = new JPanel();
	protected JPanel pMain = new JPanel();
	protected JPanel pButtons = new JPanel();
	protected JButton btnNext = new JButton("Next");
	protected JButton btnBack = new JButton("Back");
	protected Step current;
	
	public SCPanel(Model m){
		this.model = m;
		btnNext.setActionCommand("next");
		btnNext.addActionListener(this);
		btnBack.setActionCommand("back");
		btnBack.addActionListener(this);
		pMain.setLayout(new BoxLayout(pMain,BoxLayout.Y_AXIS));
		pButtons.setLayout(new BoxLayout(pButtons,BoxLayout.X_AXIS));
		pButtons.setBorder(BorderFactory.createEtchedBorder());
		pButtons.add(btnBack);
		pButtons.add(btnNext);
		
		pMain.add(pContent,0);
		pMain.add(pButtons);
		
		setInitialPosition(SHRINKER_LEFT);
		
		initializeWizard();
		
		current.visit();
		getShrinkPanel().add(pMain);
	}

	public void initializeWizard() {
		current = new ChromaticStep(this);
		Step base = current;
		current.setNext(new NumberOfProcessesStep(this));
		current.getNext().setBack(current);
		current=current.getNext();
		current.setNext(new NameColorStep(this));
		current.getNext().setBack(current);
 		current=current.getNext();
 		current.setNext(new CommunicationModelStep(this));
		current.getNext().setBack(current);
 		current=current.getNext();
 		current.setNext(new NextRoundStep(this));
		current.getNext().setBack(current);
		current=base;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command=="next"){
			if (current.execute()){
				current = current.getNext();
				current.visit();
			}
		}else if (command=="back"){
			current = current.getBack();
			current.visit();
		}
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
	
	public PluginInfo getPluginInfo() {
		return new PluginInfo("Simplicial Complex Panel");
	}
	
	public void install(Controller c) throws Exception{
		super.install(c);
	}
	
	public void uninstall(Controller c) throws Exception {
		super.uninstall(c);
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
}
