package unam.dcct.view.tools;

import de.jreality.scene.Appearance;
import de.jreality.scene.event.AppearanceEvent;
import de.jreality.scene.event.AppearanceListener;
import de.jreality.scene.pick.PickResult;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
import unam.dcct.view.jRealityView;
import unam.dcct.view.UI.InteractiveToolsPanel;

/**
 * A tool that lets the user color individual faces of the displayed geometric object
 * by clicking on them. 
 * @author Fausto Salazar
 *
 */
public class ColorFacesTool extends AbstractTool {
	
	private boolean enabled = false;
	private Color[] colors;
	private Appearance sceneContentAppearance;
	private Color selectedColor;
	private InteractiveToolsPanel toolsPanel;
	
	private jRealityView jrView;
	
	private int numberOfFaces;
	
	/**
	 * Creates a ColorFacesTool instance that references the {@link unam.dcct.ui.InteractiveToolsPanel}
	 * that controls it. 
	 * @param toolsPanel The panel that has a chkActiveColorFacesTool check-box that controls this tool's
	 * activation/inactivation.
	 */
	public ColorFacesTool(InteractiveToolsPanel toolsPanel) {
		super(InputSlot.LEFT_BUTTON);
		
		jrView = jRealityView.getInstance();
		this.toolsPanel = toolsPanel;
	}
	
	@Override
	public void activate(ToolContext tc) {
		if (enabled)
		{
			PickResult pick = tc.getCurrentPick();
			if (pick.getPickType()==PickResult.PICK_TYPE_FACE){
				int indexOfClickedFace = pick.getIndex();
				if (colors[indexOfClickedFace] != selectedColor){
					colors[indexOfClickedFace] = selectedColor;
					jrView.updateFacesColors(colors);
				}
			}
		}
	}
	
	private int getNumberOfFaces(){
		return jrView.getGeometricObject().getFacesIndices().length;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if (enabled && 
				sceneContentAppearance==null){
			// The first time this tool is enabled we 
			// need to set up everything that is required
			// to make this tool's functionality available.
			sceneContentAppearance = jrView.getJRealityViewer()
					.getViewer().getSceneRoot()
					.getChildComponent(1) // 1 corresponds to the index of the main content node in the scene graph. See http://www3.math.tu-berlin.de/jreality/68-0-the-jreality-scene-graph.html
					.getAppearance(); 
			sceneContentAppearance.addAppearanceListener(new AppearanceListener(){

				@Override
				public void appearanceChanged(AppearanceEvent ev) {
					
					ColorFacesTool.this.enabled = false;
					ColorFacesTool.this.toolsPanel.setEnabledColorFacesToolUserControls(false);
					
					colorFacesFromAppearance();
			}});
			
			colorFacesFromAppearance();
		}
	}
	
	/**
	 * Colors the faces of the geometric object being displayed using the current's 
	 * scene's main content appearance. 
	 * <p>
	 * This color is the one that is set using the ContentAppearance panel. 
	 */
	private void colorFacesFromAppearance(){
		
		numberOfFaces = getNumberOfFaces();
				
		colors = new Color[numberOfFaces];
		Color baseColor = (Color)  sceneContentAppearance.getAttribute(CommonAttributes.POLYGON_SHADER + "." + CommonAttributes.DIFFUSE_COLOR);
		for (int i = 0; i< numberOfFaces; i++){
			colors[i] = baseColor;
		}	
	
		jrView.updateFacesColors(colors);
	}
}
