package dcct.visualization;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;

public class SCGeneratePanelPlugin extends ViewShrinkPanelPlugin implements
		ActionListener {
	
	private JButton btnGenerate = new JButton("Generate");
	private JTextField txtN = new JTextField();
	private final SceneGraphComponent sgc;
	private Visualizer visualizer;
	
	public SCGeneratePanelPlugin(SceneGraphComponent sgc, Visualizer visualizer){
		this.sgc = sgc;
		this.visualizer = visualizer;
		setInitialPosition(SHRINKER_LEFT);
		getShrinkPanel().add(btnGenerate);
		getShrinkPanel().add(txtN);
		txtN.setText("Type number of processes for initial complex");
		txtN.setSize(50, 50);
		btnGenerate.addActionListener(this);
	}

	public PluginInfo getPluginInfo() {
		return new PluginInfo("Simplicial Complex Panel");
	}
	
	public void install(Controller c) throws Exception{
		super.install(c);
		c.getPlugin(Scene.class)
			.getContentComponent()
			.addChild(sgc);
	}
	
	public void uninstall(Controller c) throws Exception {
		c.getPlugin(Scene.class)
			.getContentComponent()
		    .removeChild(sgc);
		super.uninstall(c);
	}
	
	public void actionPerformed(ActionEvent e){
		//visualizer.getModel().createInicialComplex(Integer.parseInt(txtN.getText()));
		visualizer.draw(visualizer.getModel().createInicialComplex(3));

	}


}
