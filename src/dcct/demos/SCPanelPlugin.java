package dcct.demos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.PropertiesMenu;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.SceneGraphComponent;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginInfo;

public class SCPanelPlugin extends ViewShrinkPanelPlugin implements ActionListener {
	
	private JButton btnSubDiv = new JButton("Subdivide");
	private final SceneGraphComponent sgc;
	private SimplicialComplex sc;
	
	public SCPanelPlugin(SceneGraphComponent sgc, SimplicialComplex sc){
		this.sgc = sgc;
		this.sc = sc;
		setInitialPosition(SHRINKER_LEFT);
		getShrinkPanel().add(btnSubDiv);
		
		btnSubDiv.addActionListener(this);
		
	}

	public PluginInfo getPluginInfo() {
		return new PluginInfo("Primitive Chooser");
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
		sc.subdivide();
		MainDisplay.draw();
	}
	
}
