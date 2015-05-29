package view.scwizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import configuration.Constants;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

/***
 * 
 * @author Fausto
 *
 */
public class SCPanel extends ViewShrinkPanelPlugin implements ActionListener {

	Step startStep = null;
	Step currentStep = null;
	Map<String, Step> steps;
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void initializeWizard(){
		steps = new HashMap<String, Step>();
		steps.put(Constants.NUMBER_OF_PROCESSES_STEP, Step.createStep(Constants.NUMBER_OF_PROCESSES_STEP, this));
		// .. los demás
	}
	
	
	public PluginInfo getPluginInfo() {
		return new PluginInfo("Simplicial Complex Panel");
	}
	
	public void install(Controller c) throws Exception{
		super.install(c);
	}
	
	public void uninstall(Controller c) throws Exception {
		super.uninstall(c);
	}
	
}
