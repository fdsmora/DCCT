package unam.dcct.view.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

/**
 * Custom color chooser.
 * @author Fausto
 *
 */
public class ColorChooser extends JButton implements ActionListener  {

	private static final long serialVersionUID = 1L;
	private Color selectedColor;
	private JDialog dialog;
	private JColorChooser colorChooser = new JColorChooser();
	private static final String EDIT = "edit";
	private Dimension defaultSize = new Dimension(20,24);
	
	public ColorChooser(Color defaultColor){
		selectedColor = defaultColor;
		addActionListener(this);
		setActionCommand(EDIT);
		setBorderPainted(false);
		setBackground(selectedColor);
		setContentAreaFilled(false);
		setOpaque(true);
		dialog = JColorChooser.createDialog(this,"Pick a Color",
                true,  
                colorChooser,
                this,  
                null); 
		setSize();
	}

	public void actionPerformed(ActionEvent e) {
		if (EDIT.equals(e.getActionCommand())){
            colorChooser.setColor(selectedColor);
            dialog.setVisible(true);
		}
		else{
			selectedColor = colorChooser.getColor();
			setBackground(selectedColor);
			setForeground(selectedColor);
		}
			
	}
	
	private void setSize(){
		setPreferredSize(defaultSize);
		setMinimumSize(defaultSize);
		setMaximumSize(defaultSize);
	}

	/**
	 * Returns the selected {@link java.awt.Color} 
	 * @return The selected color
	 */
	public Color getSelectedColor() {
		return selectedColor;
	}
	
	public Dimension getSize() {
		return defaultSize;
	}

	public void setSize(Dimension size) {
		this.defaultSize = size;
	}
	
	public void setSize(int width, int height) {
		this.defaultSize = new Dimension(width, height);
	}

}