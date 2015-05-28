package view.UI;

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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import configuration.Configuration;

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
	
	@Override
	public void visit(){
		super.visit();
		
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
		
		btnNext.setEnabled(true);
		btnNext.setVisible(true);
		btnNext.setText(GENERATE);
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
		pProcessColors.removeAll();
		
		for (int i = 0; i<n ; i++){
			ColorEditor cEditor = new ColorEditor(Configuration.DEFAULT_COLORS[i]);
			l_processColors.add(cEditor);
			pProcessColors.add(cEditor.getButton());	
		}
	}

	@Override
	public boolean execute(){
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
				return false;
			}
		}
		
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

		return true;
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