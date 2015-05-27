package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
		//pMain.setBorder(BorderFactory.createTitledBorder("xxxxxxxxxxx"));
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

	private void initializeWizard() {
		current = new ChromaticStep(this);
		Step base = current;
		current.setNext(new NumberOfProcessesStep(this));
		current.getNext().setBack(current);
		current=current.getNext();
		current.setNext(new NameColorStep(this));
		current.getNext().setBack(current);
 		current=current.getNext();
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
	
	public JPanel getpMain() {
		return pMain;
	}
	
	
	
	
	
	public abstract class Step implements ActionListener{
		protected JLabel lbDesc = new JLabel();
		protected JPanel pContent = new JPanel();
		protected SCPanel scPanel = null;
		protected JButton btnNext =null;
		protected JButton btnBack =null;
		protected Step next = null;
		protected Step back = null;
		protected boolean modified = false;
		
		public Step(SCPanel p){
			this.scPanel = p;
			
			pContent.add(lbDesc);
			pContent.setLayout(new BoxLayout(pContent,BoxLayout.Y_AXIS));
			pContent.setBorder(BorderFactory.createTitledBorder("Initial Complex"));
		}
		
		public void visit(){
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
		
		public void actionPerformed(ActionEvent arg0) {	
		}
	}
	
//	public interface Step extends ActionListener{
//		Step getNext();
//		Step getBack();
//		void setNext(Step next);
//		void setBack(Step back);
//		void visit();
//	}
	
	public class ChromaticStep extends Step {
		
		JRadioButton rbChromatic = new JRadioButton("Chromatic");
		JRadioButton rbNonChromatic = new JRadioButton("Non chromatic");

		public ChromaticStep(SCPanel p){
			super(p);
						
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
		
		public void visit(){
			super.visit();
			lbDesc.setText("Select simplicial complex's color");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			modified = true;
			btnNext.setEnabled(true);
			btnNext.setVisible(true);

			scPanel.getModel().setChromatic(e.getActionCommand()=="c");
				
		}		
	}

	public class NumberOfProcessesStep extends Step {

		JRadioButton rbOneP = new JRadioButton("1");
		JRadioButton rbTwoP = new JRadioButton("2");
		JRadioButton rbThreeP = new JRadioButton("3");
		
		public NumberOfProcessesStep(SCPanel p){
			super(p);
						
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

		}
		
		public void visit(){
			super.visit();
			lbDesc.setText("Select number of processes");
			
			btnNext.setVisible(true);
			btnBack = scPanel.getBtnBack();
			btnBack.setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			modified = true;			
			btnNext.setVisible(true);
			btnNext.setEnabled(true);
			
			String command = e.getActionCommand();
			Model m = scPanel.getModel();
			m.setN(Integer.parseInt(command));
		}	
	}
	
	public class NameColorStep extends Step {

		JPanel pProcessNames = new JPanel();
		JPanel pProcessColors = new JPanel();
		List<JTextField> l_processNames;
		List<ColorEditor> l_processColors;
		static final String GENERATE = "Generate!";
		static final String NEXT = "Next";
		
		public NameColorStep(SCPanel p) {
			super(p);		
		}
		
		public void visit(){
			super.visit();
			
			Model m = scPanel.getModel();
			int n = m.getN();
			
			pContent.removeAll();
			
			createFields(n);	
			pProcessNames.setLayout(new BoxLayout(pProcessNames,BoxLayout.PAGE_AXIS));
			pContent.setLayout(new BoxLayout(pContent,BoxLayout.Y_AXIS));
			pContent.add(pProcessNames);
			
			String colorMsg = "";
			if (m.isChromatic()){
				pProcessColors.setLayout(new BoxLayout(pProcessColors,BoxLayout.Y_AXIS));
				createColors(n);
				pContent.add(pProcessColors);
				colorMsg = " and colors";
			}
			
			lbDesc.setText("Enter processes names" + colorMsg);
			
			btnNext = scPanel.getBtnNext();
			btnNext.setEnabled(true);
			btnNext.setVisible(true);
			btnNext.setText(GENERATE);
			
			btnBack = scPanel.getBtnBack();
			btnBack.setVisible(true);
		}
		
		protected void createFields(int n){
			l_processNames = new ArrayList<JTextField>(n);
			pProcessNames.removeAll();
			
			for (int i = 0; i<n ; i++){
				JLabel lbN = new JLabel("Process " + i + "'s name");
				JTextField txtN = new JTextField(Integer.toString(i));
				txtN.setSize(new Dimension(15,15));
				l_processNames.add(txtN);
				pProcessNames.add(lbN);
				pProcessNames.add(txtN);
			}
		}	
		
		protected void createColors(int n){
			l_processColors = new ArrayList<ColorEditor>(n);
			
//			l_processColors.add(Color.BLUE);
//			l_processColors.add(Color.WHITE);
//			l_processColors.add(Color.RED);
//			l_processColors.add(Color.GREEN);
//			l_processColors.add(Color.YELLOW);
			
			pProcessColors.removeAll();
			
			for (int i = 0; i<n ; i++){
				ColorEditor cEditor = new ColorEditor(model.DEFAULT_COLORS[i]);
				l_processColors.add(cEditor);
				pProcessColors.add(cEditor.getButton());
				
//				JDialog dialog = JColorChooser.createDialog(btnColor, "Pick a color", true, colorChooser, 
//						new ActionListener(){
//
//							public void actionPerformed(ActionEvent arg0) {
//								NameColorStep.this.l_processColors.set(i, colorChooser.getColor());
//								
//							}
//					
//				}, 
//						null);
			
			}
		}
	
		@Override
		public Step getNext(){
			Set<String> procNames = new LinkedHashSet<String>();
			
			Pattern p = Pattern.compile("^[a-zA-Z0-9]*$");
			
			for (JTextField txtPName : l_processNames){
				String text = txtPName.getText();
				Matcher m = p.matcher(text);
				if (text.length() == 1 && 
						m.matches() && 
						!procNames.contains(text)){
					procNames.add(text);
				}
				else{
					JOptionPane.showMessageDialog(null, "Process names must be non-empty, alphanumeric, one-character and unique.");
					return this;
				}
			}
			
			Model m = scPanel.getModel();
			List<String> lprocNames = new ArrayList<String>(m.getN());
			lprocNames.addAll(procNames);
			
			if (m.isChromatic()){
				List<Color> lprocColors = new ArrayList<Color>(m.getN());
				for (ColorEditor ce : l_processColors)
					lprocColors.add(ce.getCurrentColor());
				m.setSimplicialComplexColors(lprocColors);
			}
		
			m.createInitialComplex(lprocNames);
			
			btnNext.setText(NEXT);

			return super.getNext();
		}
		
		private class ColorEditor implements ActionListener {
			JButton button = new JButton(" ");
			Color currentColor;
			JDialog dialog;
			JColorChooser colorChooser = new JColorChooser();
			static final String EDIT = "edit";
			
			public ColorEditor(Color defaultColor){
				this.currentColor = defaultColor;
				button.addActionListener(this);
				button.setActionCommand(EDIT);
				button.setBorderPainted(false);
				button.setBackground(currentColor);
				button.setForeground(currentColor);
				dialog = JColorChooser.createDialog(this.button,"Pick a Color",
                        true,  
                        colorChooser,
                        this,  
                        null); 
			}

			public void actionPerformed(ActionEvent e) {
				if (EDIT.equals(e.getActionCommand())){
		            colorChooser.setColor(currentColor);
		            dialog.setVisible(true);
				}
				else{
					currentColor = colorChooser.getColor();
					button.setBackground(currentColor);
					button.setForeground(currentColor);
				}
					
			}

			public JButton getButton() {
				return button;
			}

			public Color getCurrentColor() {
				return currentColor;
			}

		}
		
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}


}
