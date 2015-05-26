package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

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
		pMain.setBorder(BorderFactory.createTitledBorder("xxxxxxxxxxx"));
		pButtons.setLayout(new BoxLayout(pButtons,BoxLayout.X_AXIS));
		pButtons.setBorder(BorderFactory.createEtchedBorder());
		pButtons.add(btnBack);
		pButtons.add(btnNext);
		
		pMain.add(pContent,0);
		pMain.add(pButtons);
		
		//TEST
//		JPanel pTest0 = new JPanel();
//		JTextField txt0= new JTextField("0");
//		pTest0.add(txt0);
//		pContent=pTest0;
//		
//		pMain.add(txt0);
//		pMain.add(pButtons);
//		
//		txt0.setText("1");
//		
//		txt0 = new JTextField("2");
//
//		pMain.remove(0);
//		pMain.add(txt0,0);
////		pMain.revalidate();
//		pMain.repaint();
//		
//		pTest0.validate();
//		pTest0.repaint();
//		
//		pContent.validate();
//		pContent.repaint();
//		
//		pMain.revalidate();
//		pMain.repaint();
//		pMain.update(pMain.getGraphics());

//		
//		pContent=null;
//		
//		JPanel pTest = new JPanel();
//		JTextField txt1 = new JTextField("1");
//		pTest.add(txt1);
//		pContent=pTest;
//		
//		pContent.repaint();
//		
//		pMain.revalidate();
//		pMain.repaint();
//		pMain.add(pContent);
		
		setInitialPosition(SHRINKER_LEFT);
		
		initializeWizard();
		
		current.visit();
		getShrinkPanel().add(pMain);
	}

	private void initializeWizard() {
		current = new ChromaticStep(this);
		Step base = current;
		current.setNext(new NumberOfProcessesStep(this));
		current.getNext().setBack(current);
		current=current.getNext();
//		current.setNext(new NameColorStep());
//		current.getNext().setBack(current);
// 		current=current.getNext();
		current=base;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command=="next"){
			current = current.getNext();
			current.visit();
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
	
	
	
	
	
	
	
	
	public interface Step extends ActionListener{
		Step getNext();
		Step getBack();
		void setNext(Step next);
		void setBack(Step back);
		void visit();
	}
	
	public class ChromaticStep implements Step{
		
		JLabel lbDesc = new JLabel("Choose kind of complex");
		JRadioButton rbChromatic = new JRadioButton("Chromatic");
		JRadioButton rbNonChromatic = new JRadioButton("Non chromatic");
		JPanel pContent = new JPanel();
		SCPanel scPanel = null;
		JButton btnNext =null;
		JButton btnBack =null;
		Step next = null;
		Step back = null;
		boolean modified = false;

		public ChromaticStep(SCPanel p){
			this.scPanel = p;
			
			pContent.setLayout(new BoxLayout(pContent,BoxLayout.Y_AXIS));
			pContent.setBorder(BorderFactory.createTitledBorder("Initial Complex"));
			
			ButtonGroup chromGroup = new ButtonGroup();
			chromGroup.add(rbChromatic);
			chromGroup.add(rbNonChromatic);

			pContent.add(lbDesc);
			pContent.add(rbChromatic);
			pContent.add(rbNonChromatic);
			
			rbChromatic.setActionCommand("c");
			rbNonChromatic.setActionCommand("nc");
			
//			pContent.add(scPanel.getBtnNext());
//			pContent.add(scPanel.getBtnBack());
			
			rbChromatic.addActionListener(this);
			rbNonChromatic.addActionListener(this);
		}
		
		public void visit() {		
			if (!modified){
				btnNext = scPanel.getBtnNext();
				btnNext.setEnabled(false);
				btnNext.setVisible(true);
			}
			scPanel.getBtnBack().setVisible(false);
			scPanel.getpMain().remove(0);
			scPanel.getpMain().add(pContent,0);
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

		public void actionPerformed(ActionEvent e) {
			modified = true;
			btnNext.setEnabled(true);
			btnNext.setVisible(true);
		}		
	}

	public class NumberOfProcessesStep implements Step{

		JLabel lbDesc = new JLabel("Choose number of processes");
		JRadioButton rbOneP = new JRadioButton("1");
		JRadioButton rbTwoP = new JRadioButton("2");
		JRadioButton rbThreeP = new JRadioButton("3");
		JPanel pContent = new JPanel();
		SCPanel scPanel = null;
		JButton btnNext =null;
		JButton btnBack =null;
		Step next = null;
		Step back = null;
		boolean modified = false;
		
		public NumberOfProcessesStep(SCPanel p){
			this.scPanel = p;
			pContent.setLayout(new BoxLayout(pContent,BoxLayout.Y_AXIS));
			
			ButtonGroup nprocGroup = new ButtonGroup();
			nprocGroup.add(rbOneP);
			nprocGroup.add(rbTwoP);
			nprocGroup.add(rbThreeP);
			
			pContent.add(lbDesc);
			pContent.add(rbOneP);
			pContent.add(rbTwoP);
			pContent.add(rbThreeP);
			
			rbOneP.setActionCommand("1");
			rbOneP.addActionListener(this);
			rbTwoP.setActionCommand("2");
			rbTwoP.addActionListener(this);
			rbThreeP.setActionCommand("3");
			rbThreeP.addActionListener(this);
			
			btnNext = scPanel.getBtnNext();
			btnNext.setEnabled(false);
			btnNext.setVisible(true);
		}
		
		public Step getNext() {
			return next;
		}

		public Step getBack() {
			return back;
		}

		public void setNext(Step next) {
			this.next=next;
		}

		public void setBack(Step back) {
			this.back=back;
		}

		public void visit() {
			if (!modified){
				btnNext = scPanel.getBtnNext();
				btnNext.setEnabled(false);
				btnNext.setVisible(true);
			}
			scPanel.getBtnBack().setVisible(true);
			scPanel.getpMain().remove(0);
			scPanel.getpMain().add(pContent,0);
		}

		public void actionPerformed(ActionEvent e) {
			modified = true;
			String command = e.getActionCommand();
			if (command=="1"){
			}else if (command=="2"){
			}else {
			}
			btnNext.setVisible(true);
			btnNext.setEnabled(true);
		}
		
	}

	public JPanel getpMain() {
		return pMain;
	}


}
