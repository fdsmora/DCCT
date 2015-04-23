package dcct.visualization;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;

public class SCGeneratePanelPlugin extends ViewShrinkPanelPlugin implements
		ActionListener {
	
	private JButton btnGenerate = new JButton("Generate");
	private JButton btnSubDiv = new JButton("Subdivide");
	private JTextField txtN = new JTextField();
	private JPanel panel = new JPanel();
	private final SceneGraphComponent sgc;
	private Visualizer visualizer;
	
	public SCGeneratePanelPlugin(SceneGraphComponent sgc, Visualizer visualizer){
		this.sgc = sgc;
		this.visualizer = visualizer;
		setInitialPosition(SHRINKER_LEFT);
		txtN.setText("");
		txtN.setSize(50, 50);
		panel.add(btnGenerate);
		panel.add(txtN);
		panel.add(btnSubDiv);
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		getShrinkPanel().add(panel);		
		btnGenerate.addActionListener(this);
		btnSubDiv.addActionListener(this);
	}

	public PluginInfo getPluginInfo() {
		return new PluginInfo("Simplicial Complex Panel");
	}
	
	public void install(Controller c) throws Exception{
		super.install(c);
//		c.getPlugin(Scene.class)
//			.getContentComponent()
//			.addChild(sgc);
	}
	
	public void uninstall(Controller c) throws Exception {
//		c.getPlugin(Scene.class)
//			.getContentComponent()
//		    .removeChild(sgc);
		super.uninstall(c);
	}
	
	public void actionPerformed(ActionEvent e){
		if (((JButton)e.getSource()).getText()=="Generate")
		{
			int n =0;
		     try{  
		    	 n = Integer.parseInt(txtN.getText());   
		     } catch(NumberFormatException nfe){  
		    	 n = 2;
		     } 
		    n = n>0? n: 1;
			visualizer.draw(visualizer.getModel().createInicialComplex(n));
		}
		else 
		{
			visualizer.subdivision();
		}
	}
}
