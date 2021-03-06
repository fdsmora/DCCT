package unam.dcct.view.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.InputSlot;
import de.jreality.tools.DraggingTool;
import de.jreality.util.ColorConverter;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import unam.dcct.misc.Constants;
import unam.dcct.view.jRealityView;
import unam.dcct.view.tools.ColorFacesTool;
import unam.dcct.view.tools.DragGeometryTool;

/**
 * Adds tools to let user interact with the visualization. Also provides a UI to use these
 * tools. 
 * <br>
 * Controls are for enabling/disabling vertex, edge and face dragging, reseting the original
 * camera perspective (i.e. putting the object in the center of the camera), dragging the whole geometric object, etc. 
 * @author Fausto Salazar
 *
 */
public class InteractiveToolsPanel extends ViewShrinkPanelPlugin implements ItemListener, ActionListener {
	
	private JPanel pContent;
	private JCheckBox chkDragVertex,
	chkDragEdge,
	chkDragFace,
	chkActiveColorFacesTool;
	private jRealityView jrView;
	
	private DragGeometryTool dragGeometryTool;
	private DraggingTool dragWholeObjectTool;
	private ColorFacesTool colorFacesTool;
	private ColorChooser faceColorChooser;
	private JButton btnDisconnectFaces,
					btnEraseColoredFaces,
					btnMapChromaticity;
	private static final String mapChromaticityToolTipText = "<html>This helps to distinguish which face in a non-chromatic complex corresponds to a face in a chromatic complex or viceversa."
			+ "								<br> When a face is colored, the corresponding face in the other complex will be automatically colored as well."
			+ "								<br> This function can only be activated now.</html>";
	private final JLabel lbMapChromaticity = new JLabel("Map chromaticity is activated");
	private final JLabel lbDisconnectFaces = new JLabel("Faces are disconnected");
	{
		lbDisconnectFaces.setForeground(Color.BLUE);
		lbDisconnectFaces.setVisible(false);
		lbDisconnectFaces.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		lbMapChromaticity.setForeground(Color.BLUE);
		lbMapChromaticity.setVisible(false);
		lbMapChromaticity.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbMapChromaticity.setToolTipText(mapChromaticityToolTipText);
	}

	public InteractiveToolsPanel(){
		// Define the position of the controls within jReality UI
		setInitialPosition(SHRINKER_LEFT);
		
		jrView = jRealityView.getInstance();
		
		dragGeometryTool = new DragGeometryTool();
		colorFacesTool = new ColorFacesTool(this);
		
		pContent = new JPanel();
		pContent.setBorder(BorderFactory.createTitledBorder("Interaction tools"));
		pContent.setLayout(new BoxLayout(pContent,BoxLayout.PAGE_AXIS));

		JPanel dragPanel = new JPanel();
		dragPanel.setLayout(new BoxLayout(dragPanel,BoxLayout.PAGE_AXIS));
		dragPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		chkDragVertex = new JCheckBox("Drag vertices");
		chkDragEdge = new JCheckBox("Drag edges");
		chkDragFace = new JCheckBox("Drag faces");
		chkDragVertex.addItemListener(this);
		chkDragEdge.addItemListener(this);
		chkDragFace.addItemListener(this);
		chkDragVertex.setSelected(true);
		chkDragEdge.setSelected(true);
		chkDragFace.setSelected(true);
		
		JLabel lbResetCamera = new JLabel("To center object press 'e'");
		lbResetCamera.setForeground(Color.BLUE);
		
		dragPanel.add(lbResetCamera);
		
		dragPanel.add(chkDragVertex);
		dragPanel.add(chkDragEdge);
		dragPanel.add(chkDragFace);
		
		btnDisconnectFaces = new JButton("Disconnect faces");
		btnDisconnectFaces.addActionListener(this);
		btnDisconnectFaces.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		pContent.add(dragPanel);
		
		makeColorFacesControl();
		pContent.add(btnDisconnectFaces);
		pContent.add(lbDisconnectFaces);

		// Embed this panel into jReality's Shrink Panel.
		getShrinkPanel().add(pContent);
	}

	private void makeColorFacesControl(){
		JPanel sfPanel = new JPanel();
		sfPanel.setLayout(new BoxLayout(sfPanel,BoxLayout.PAGE_AXIS));
		sfPanel.setBorder(BorderFactory.createTitledBorder("Color faces"));
		sfPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		chkActiveColorFacesTool = new JCheckBox("Active");
		chkActiveColorFacesTool.addItemListener(this);
		// Proper alignment properties
		chkActiveColorFacesTool.setAlignmentX(Component.CENTER_ALIGNMENT);
		sfPanel.add(chkActiveColorFacesTool);
		
		JPanel pColor = new JPanel();
		pColor.setLayout(new BoxLayout(pColor,BoxLayout.LINE_AXIS));
		
		faceColorChooser = new ColorChooser(Constants.FACE_COLOR_CHOOSER_DEFAULT_COLOR);
		faceColorChooser.setAlignmentX(Component.CENTER_ALIGNMENT);
		faceColorChooser.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				colorFacesTool.setSelectedColor(ColorConverter.toJR(faceColorChooser.getSelectedColor()));				
			}
			
		});
		
		btnEraseColoredFaces = new JButton("Erase");
		btnEraseColoredFaces.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnEraseColoredFaces.setVisible(false);
		btnEraseColoredFaces.addActionListener(this);
		
		setEnabledColorFacesToolUserControls(false);
		
		sfPanel.add(Box.createRigidArea(new Dimension(180,0)));
		pColor.add(faceColorChooser);
		pColor.add(Box.createRigidArea(new Dimension(10,0)));
		pColor.add(btnEraseColoredFaces);
		pColor.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
			
		sfPanel.add(pColor);
		btnMapChromaticity = new JButton("Map chromaticity");
		btnMapChromaticity.setVisible(false);
		btnMapChromaticity.addActionListener(this);
		btnMapChromaticity.setToolTipText(mapChromaticityToolTipText);
		btnMapChromaticity.setAlignmentX(Component.CENTER_ALIGNMENT);
		sfPanel.add(btnMapChromaticity);
		sfPanel.add(lbMapChromaticity);
		
		pContent.add(sfPanel);
	}
	
	/**
	 * Depending on the value of the parameter, checks or unchecks the color faces tool's activation's checkbox
	 * and hides or displays its color chooser button.
	 * @param enabled 
	 */
	public void setEnabledColorFacesToolUserControls(boolean enabled){
		chkActiveColorFacesTool.setSelected(enabled);
		faceColorChooser.setVisible(enabled);
	}
	
	public void itemStateChanged(ItemEvent e) {
		Object src = e.getSource();
		if (src == chkDragVertex){
			dragGeometryTool.setVertexDragEnabled(chkDragVertex.isSelected());
		}else if (src == chkDragEdge){
			dragGeometryTool.setEdgeDragEnabled(chkDragEdge.isSelected());
		}else if (src == chkDragFace){
			dragGeometryTool.setFaceDragEnabled(chkDragFace.isSelected());
		}else if (src == chkActiveColorFacesTool ){
			boolean selected = chkActiveColorFacesTool.isSelected();
			setEnabledColorFacesToolUserControls(selected);
			colorFacesTool.setEnabled(selected);
		}
	}
		

	@Override
	public void install(Controller c) throws Exception {
		super.install(c);
		SceneGraphComponent sgc = jrView.getSceneGraphComponent();
		sgc.addTool(dragGeometryTool);
		sgc.addTool(colorFacesTool);
		
		/*		 
		 * Create DraggingTool to let user drag the whole geometric object around the visualization space.
		 Needed to tweak it a bit in order to enable it back, as this feature was removed 
		 in the latest versions of jReality (I brought it back because I consider it useful for this program). 
		 "PrimarySelection" is to activate dragging by pressing mouse's right button. 
		 "DragActivation" is the original behavior, which activates it with middle button (mouse's wheel)
		 but not every mouse has a middle button (e.g. Mac) */
		dragWholeObjectTool = new DraggingTool(InputSlot.getDevice("PrimarySelection"));
		dragWholeObjectTool.addCurrentSlot(InputSlot.getDevice("DragAlongViewDirection"));
		dragWholeObjectTool.addCurrentSlot(InputSlot.getDevice("PointerEvolution"));		
		sgc.addTool(dragWholeObjectTool);
	}
	
	@Override
	public void uninstall(Controller c) throws Exception {
		SceneGraphComponent sgc = jrView.getSceneGraphComponent();
		sgc.removeTool(dragGeometryTool);
		sgc.removeTool(dragWholeObjectTool);
		sgc.removeTool(colorFacesTool);
		super.uninstall(c);
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = "Interaction control panel";
		info.vendorName = "UNAM";
		return info; 
	}

	public JCheckBox getChkActiveColorFacesTool() {
		return chkActiveColorFacesTool;
	}

	public JButton getBtnDisconnectFaces() {
		return btnDisconnectFaces;
	}

	public JButton getBtnEraseColoredFaces() {
		return btnEraseColoredFaces;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object s = arg0.getSource();
		if(s == btnDisconnectFaces){
			jrView.enableDisconnectedFaces();
			btnDisconnectFaces.setVisible(false);
			lbDisconnectFaces.setVisible(true);
		}else if (s == btnEraseColoredFaces){
			if (colorFacesTool.isAnyFacesPainted()){
				colorFacesTool.setAnyFacesPainted(false); // Erase color in colored faces
				btnEraseColoredFaces.setVisible(false);
			}
		}else if (s == btnMapChromaticity){
			colorFacesTool.setMapChromaticity(true);
			btnMapChromaticity.setVisible(false);
			lbMapChromaticity.setVisible(true);
		}
		
	}

	public void resetDisconnectFaces() {
		btnDisconnectFaces.setVisible(false);
		lbDisconnectFaces.setVisible(false);
	}
	
	public void resetMapChromaticity() {
		btnMapChromaticity.setVisible(false);
		lbMapChromaticity.setVisible(false);
	}

	public JButton getBtnMapChromaticity() {
		return btnMapChromaticity;
	}
}
