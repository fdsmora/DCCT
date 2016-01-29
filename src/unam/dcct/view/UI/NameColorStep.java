package unam.dcct.view.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import de.jreality.util.ColorConverter;
import unam.dcct.misc.Constants;

/***
 * Represents the step in the {@link unam.dcct.view.UI.SimplicialComplexPanel} wizard
 * that lets the user introduce the text of the label and the color of the vertex that 
 * represents each process. The number of controls displayed depends on the choice 
 * of the previous step {@link unam.dcct.view.UI.NumberOfProcessesStep}. 
 * @author Fausto Salazar
 */
class NameColorStep extends Step {
	private JPanel pProcessNameColor = new JPanel();
	private List<JTextField> l_processNames;
	private List<ColorChooser> l_processColors;
	private ColorChooser ncChooser;

	private int n = 0;
	
	public NameColorStep() {
		super();
	}
	
	private JPanel createNonChromaticColorChooser(){
		JPanel pNonChromaticColor = new JPanel();
		pNonChromaticColor.setLayout(new BoxLayout(pNonChromaticColor,BoxLayout.PAGE_AXIS));
		pNonChromaticColor.setBorder(BorderFactory.createTitledBorder("Color for non-chromatic vertices"));
		pNonChromaticColor.setAlignmentX(Component.CENTER_ALIGNMENT);
		ncChooser = new ColorChooser(new Color(model.getNonChromaticColor().getRGB()));
//		ncChooser.setAlignmentX(Component.CENTER_ALIGNMENT);
		pNonChromaticColor.add(Box.createRigidArea(new Dimension(100,0)));
		pNonChromaticColor.add(ncChooser);
		return pNonChromaticColor;
	}
	
	@Override
	public void visit(){
		super.visit();
		
		int n = ((NumberOfProcessesStep)(Steps.NumberOfProcessesStep.getStep())).getSelectedNumberOfProcesses();
		
		pContent.removeAll();
		
		createFields(n);	
		
		pProcessNameColor.setLayout(new BoxLayout(pProcessNameColor,BoxLayout.PAGE_AXIS));
		pContent.setLayout(new BoxLayout(pContent,BoxLayout.PAGE_AXIS));
		pContent.add(pProcessNameColor);
		pContent.add(createNonChromaticColorChooser());
		
		String colorMsg = "";
		
		lbTitle.setText("Enter processes names" + colorMsg);
		
		btnNext.setEnabled(true);
		btnNext.setVisible(true);
		btnNext.setText(Constants.GENERATE);
		btnBack.setVisible(true);
	}
	
	private void createFields(int n){
		l_processNames = new ArrayList<JTextField>(n);
		l_processColors = new ArrayList<ColorChooser>(n);
		
		pProcessNameColor.removeAll();
		
		List<de.jreality.shader.Color> colors = model.getColors(); 
		List<String> pNames = model.getpNames();
		
		for (int i = 0; i<n ; i++){
			
			JPanel pBody = new JPanel();
			pBody.setLayout(new BoxLayout(pBody,BoxLayout.LINE_AXIS));
			
			JTextField txtN = new JTextField();
			// For limiting introduced text to one character. 
			txtN.setDocument(new JTextFieldLimit(1));
			txtN.setText(pNames.get(i));
			Dimension d = new Dimension(22,24);
			txtN.setPreferredSize(d);
			txtN.setMinimumSize(d);
			txtN.setMaximumSize(d);
			
			l_processNames.add(txtN);
			
			ColorChooser cChooser = new ColorChooser(ColorConverter.toAwt(colors.get(i)));
			l_processColors.add(cChooser);
			
			pBody.add(txtN);
			pBody.add(Box.createRigidArea(new Dimension(10,0)));
			pBody.add(cChooser);
			
			JLabel lbN = new JLabel("Process " + i + "'s name and color");
			lbN.setAlignmentX(Component.CENTER_ALIGNMENT);
			lbN.setLabelFor(pBody);
			pProcessNameColor.add(lbN);
			pProcessNameColor.add(pBody);
		}
	}
	
	/**
	 * Validates that the process names introduced are non-empty, alphanumeric, one-character and unique.
	 * If any of these conditions is not fulfilled, an error message dialog is displayed. 
	 * If validation succeeds, an initial complex is built using the data captured in this step and the previous. 
	 */
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
		
		List<de.jreality.shader.Color> lprocColors = new ArrayList<de.jreality.shader.Color>(n);
		for (ColorChooser ce : l_processColors){
			Color oldColor = ce.getSelectedColor();
			de.jreality.shader.Color newColor = ColorConverter.toJR(oldColor);
			lprocColors.add(newColor);
		}
		
		model.setColors(lprocColors);
		
		Color oldColor = ncChooser.getSelectedColor();
		model.setNonChromaticColor(ColorConverter.toJR(oldColor));
		model.createInitialComplex(lprocNames);
		
		//Step nextStep = Step.steps.get(CommunicationMechanismStep.class.getName());
		Step next = Steps.CommunicationMechanismStep.getStep();
		scPanel.setCurrentStep(next);
		next.visit();
	}
	
	@Override
	public void goBack(){
		//Step back = Step.steps.get(NumberOfProcessesStep.class.getName());
		Step back = Steps.NumberOfProcessesStep.getStep();
		scPanel.setCurrentStep(back);
		back.visit();		
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

		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		    if (str == null)
		      return;

		    if ((getLength() + str.length()) <= limit) {
		      super.insertString(offset, str, attr);
		    }
		  }
	}
}
