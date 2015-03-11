package dcct.visualization;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.SceneGraphComponent;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

public class SCPanelPlugin extends ViewShrinkPanelPlugin implements ActionListener {
	
	private JButton btnSubDiv = new JButton("Subdivide");
	private final SceneGraphComponent sgc;
	private Visualizer visualizer;
	
	public SCPanelPlugin(SceneGraphComponent sgc, Visualizer visualizer){
		this.sgc = sgc;
		this.visualizer = visualizer;
		setInitialPosition(SHRINKER_LEFT);
		getShrinkPanel().add(btnSubDiv);
		
		btnSubDiv.addActionListener(this);
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
		visualizer.subdivision();
	}
	
}
