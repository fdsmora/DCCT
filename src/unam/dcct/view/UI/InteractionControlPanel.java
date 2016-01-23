package unam.dcct.view.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.Transformation;
import de.jreality.util.DefaultMatrixSupport;
import de.jtem.jrworkspace.plugin.PluginInfo;
import unam.dcct.view.jRealityView;

/**
 * Provides UI controls to control the interaction with the visualization. 
 * <br>
 * Controls are for enabling/disabling vertex, edge and face dragging, reseting the original
 * camera perspective, etc. 
 * @author Fausto
 *
 */
public class InteractionControlPanel extends ViewShrinkPanelPlugin implements ItemListener {
	
	private JPanel panel;
	private JButton btnResetPerspective;
	private JCheckBox chkDragVertex;
	private JCheckBox chkDragEdge;
	private JCheckBox chkDragFace;
	private jRealityView jrview;

	public InteractionControlPanel(){
		// Define the position of the controls within jReality UI
		setInitialPosition(SHRINKER_LEFT);
		
		jrview = jRealityView.getInstance();
		
		panel = new JPanel();
		chkDragVertex = new JCheckBox("Drag vertices");
		chkDragEdge = new JCheckBox("Drag edges");
		chkDragFace = new JCheckBox("Drag faces");
		chkDragVertex.addItemListener(this);
		chkDragEdge.addItemListener(this);
		chkDragFace.addItemListener(this);
		chkDragVertex.setSelected(true);
		chkDragEdge.setSelected(true);
		chkDragFace.setSelected(true);
		btnResetPerspective = new JButton();
		
		/*
		 * This button is for allowing the user to reset the original camera perspective when she
		 * rotated or translated the visualized object. 
		 */
		btnResetPerspective.setText("Reset perspective");
		btnResetPerspective.setActionCommand("R");
		btnResetPerspective.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("R")){
					// Restore the original camera perspective
					Transformation t = jrview.getSceneGraphComponent().getTransformation();
					if (t!=null)
						DefaultMatrixSupport.getSharedInstance().restoreDefault(t, true);
				}
			}
		});
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.add(btnResetPerspective);
		panel.add(chkDragVertex);
		panel.add(chkDragEdge);
		panel.add(chkDragFace);
		// Embed this panel into jReality's Shrink Panel.
		getShrinkPanel().add(panel);
	}

	public void itemStateChanged(ItemEvent e) {
		Object src = e.getSource();
		if (src == chkDragVertex){
			jrview.setVertexDragEnabled(chkDragVertex.isSelected());
		}else if (src == chkDragEdge){
			jrview.setEdgeDragEnabled(chkDragEdge.isSelected());
		}else if (src == chkDragFace){
			jrview.setFaceDragEnabled(chkDragFace.isSelected());
		}
	}
				
	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = "Interaction control panel";
		info.vendorName = "UNAM";
		return info; 
	}
}
