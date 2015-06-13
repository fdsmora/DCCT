package view.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import configuration.Constants;

/***
 * 
 * @author Fausto
 *
 */
public class NameColorStep extends Step {
	protected JPanel pProcessNames = new JPanel();
	protected JPanel pProcessColors = new JPanel();
	protected List<JTextField> l_processNames;
	protected List<ColorEditor> l_processColors;
	protected int n = 0;

	public NameColorStep(SCPanel p) {
		super(p);
	}
	
	@Override
	public void visit(){
		super.visit();
		
		int n = ((NumberOfProcessesStep)(scPanel.getSteps()
						.get(Constants.NUMBER_OF_PROCESSES_STEP))).getN();
		
		pContent.removeAll();
		
		createFields(n);	
		
		pProcessNames.setLayout(new BoxLayout(pProcessNames,BoxLayout.PAGE_AXIS));
		pContent.setLayout(new BoxLayout(pContent,BoxLayout.LINE_AXIS));
		pContent.add(pProcessNames);
		
		String colorMsg = "";
		
//		pProcessColors.setLayout(new BoxLayout(pProcessColors,BoxLayout.Y_AXIS));
//		createColors(n);
//		pContent.add(pProcessColors);
//		colorMsg = " and colors";
		
		lbDesc.setText("Enter processes names" + colorMsg);
		
		btnNext.setEnabled(true);
		btnNext.setVisible(true);
		btnNext.setText(Constants.GENERATE);
		btnBack.setVisible(true);
	}
	
	protected void createFields(int n){
		l_processNames = new ArrayList<JTextField>(n);
		l_processColors = new ArrayList<ColorEditor>(n);
		
		pProcessNames.removeAll();
		
		for (int i = 0; i<n ; i++){			
			JPanel pBody = new JPanel();
			pBody.setLayout(new BoxLayout(pBody,BoxLayout.LINE_AXIS));
			
			JTextField txtN = new JTextField();
			// For limiting introduced text to one character. 
			txtN.setDocument(new JTextFieldLimit(1));
			txtN.setText(Integer.toString(i));
			Dimension d = new Dimension(20,24);
			txtN.setPreferredSize(d);
			txtN.setMinimumSize(d);
			txtN.setMaximumSize(d);
			l_processNames.add(txtN);
			
			ColorEditor cEditor = new ColorEditor(Constants.DEFAULT_COLORS[i]);
			l_processColors.add(cEditor);
			
			pBody.add(txtN);
			pBody.add(Box.createRigidArea(new Dimension(10,0)));
			pBody.add(cEditor.getButton());
			
			JLabel lbN = new JLabel("Process " + i + "'s name and color");
			lbN.setAlignmentX(Component.CENTER_ALIGNMENT);
			pProcessNames.add(lbN);
			pProcessNames.add(pBody);
		}
	}
	
//	protected void createColors(int n){
//		l_processColors = new ArrayList<ColorEditor>(n);
//		pProcessColors.removeAll();
//		
//		for (int i = 0; i<n ; i++){
//			ColorEditor cEditor = new ColorEditor(Constants.DEFAULT_COLORS[i]);
//			l_processColors.add(cEditor);
//			pProcessColors.add(cEditor.getButton());	
//		}
//	}
	
	@Override
	public void validateAndExecute(){
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
				return;
			}
		}
		
		List<String> lprocNames = new ArrayList<String>(n);
		lprocNames.addAll(procNames);
		
		List<Color> lprocColors = new ArrayList<Color>(n);
		for (ColorEditor ce : l_processColors)
			lprocColors.add(ce.getCurrentColor());
		
		model.setSimplicialComplexColors(lprocColors);
		model.createInitialComplex(lprocNames);
		
		Step nextStep = scPanel.getSteps().get(Constants.COMMUNICATION_MODEL_STEP);
		scPanel.setCurrentStep(nextStep);
		nextStep.visit();
	}
	
	@Override
	public void goBack(){
		Step back = scPanel.getSteps().get(Constants.NUMBER_OF_PROCESSES_STEP);
		scPanel.setCurrentStep(back);
		back.visit();		
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
			//button.setForeground(currentColor);
			button.setContentAreaFilled(false);
			button.setOpaque(true);
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

	/***
	 * Auxiliary class for limiting the number of characters introduced into a JTextField
	 * Taken from : http://stackoverflow.com/questions/10136794/limiting-the-number-of-characters-in-a-jtextfield
	 */
	private class JTextFieldLimit extends PlainDocument {
		  private int limit;
		  JTextFieldLimit(int limit) {
		    super();
		    this.limit = limit;		
		  }

		  JTextFieldLimit(int limit, boolean upper) {
		    super();
		    this.limit = limit;
		  }

		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		    if (str == null)
		      return;

		    if ((getLength() + str.length()) <= limit) {
		      super.insertString(offset, str, attr);
		    }
		  }
		}
}
