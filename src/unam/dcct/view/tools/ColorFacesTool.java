package unam.dcct.view.tools;

import java.util.List;

import de.jreality.scene.Appearance;
import de.jreality.scene.event.AppearanceEvent;
import de.jreality.scene.event.AppearanceListener;
import de.jreality.scene.pick.PickResult;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
import de.jreality.util.ColorConverter;
import unam.dcct.misc.Constants;
import unam.dcct.model.Model;
import unam.dcct.view.View;
import unam.dcct.view.jRealityView;
import unam.dcct.view.UI.InteractiveToolsPanel;
import unam.dcct.view.geometry.Face;
import unam.dcct.view.geometry.GeometricComplex;
import unam.dcct.view.geometry.Geometry;

/**
 * A tool that lets the user color individual faces of the displayed geometric object
 * by clicking on them. 
 * @author Fausto Salazar
 *
 */
public class ColorFacesTool extends AbstractTool implements View {
	
	private boolean enabled = false;
	private Appearance sceneContentAppearance;
	private Color selectedColor = ColorConverter.toJR(Constants.FACE_COLOR_CHOOSER_DEFAULT_COLOR);
	private InteractiveToolsPanel toolsPanel;
	
	private jRealityView jrView;
	
	private int numberOfFaces;
	private ColorFacesGeometry colorFacesGeometry;
	
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
			if (pick!=null && pick.getPickType()==PickResult.PICK_TYPE_FACE){
				colorFacesGeometry.updateFaceColors(pick.getIndex(), selectedColor);
			}
		}
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
					
					deactivate();

			}});
			
			// In order to work, I need to be notified when changes in the Model occur. 
			Model.getInstance().registerView(this);
			displayComplex();
		}
		else if (!enabled){

			deactivate();
		}
	}
	
	private void deactivate(){
		reset();
		Model.getInstance().unregisterView(this);
	}

	@Override
	public void displayComplex() {
		if (enabled){
			GeometricComplex baseGeometry = (GeometricComplex)jrView.getGeometricObject();
			colorFacesGeometry = new ColorFacesGeometry(baseGeometry, colorFacesGeometry);
		}
	}

	@Override
	public void updateChromaticity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		sceneContentAppearance = null;
		colorFacesGeometry = null;
		jrView.updateFacesColors(null);
	}
	
	private class ColorFacesGeometry{
		private GeometricComplex innerGeometry;
		private Color[] colors;
		
		ColorFacesGeometry(GeometricComplex baseGeometry, ColorFacesGeometry parentGeometry){
			innerGeometry = baseGeometry;
			if (parentGeometry==null)
				colorAllFacesFromAppearance();
			else{
				// Color the new faces with the colors of their parent faces's colors.
				Color[] newColors = new Color[baseGeometry.getFacesIndices().length];
				int i = 0;
				List<Face> parentFaces = parentGeometry.getFaces();
				System.out.println("parentFaces:"+parentFaces);
				for (Face f : baseGeometry.getFaces()){
					Face parentFace = f.getParent();
					System.out.println("parentFace:"+parentFace);
					newColors[i++] = parentGeometry.colors[parentFaces.indexOf(parentFace)];
				}
				colors = newColors;
				jrView.updateFacesColors(colors);
			}
		}
		
		void updateFaceColors(int index, Color selectedColor) {
			if (colors[index]!=selectedColor){
				colors[index]=selectedColor;
				jrView.updateFacesColors(colors);
			}
		}

		Color[] getFacesColors(){
			return colors;
		}
		
		List<Face> getFaces(){
			return innerGeometry.getFaces();
		}
		
		/**
		 * Colors the faces of the geometric object being displayed using the current's 
		 * scene's main content appearance. 
		 * <p>
		 * This color is the one that is set using the ContentAppearance panel. 
		 * <p>
		 * Can be thought as a kind of "reset" behavior that swaps all colored faces by
		 * this tool, coloring all of them with the color set by the ContentAppearance panel.
		 */
		void colorAllFacesFromAppearance(){
			
			numberOfFaces = innerGeometry.getFacesIndices().length;
					
			colors = new Color[numberOfFaces];
			Color baseColor = (Color)  sceneContentAppearance.getAttribute(CommonAttributes.POLYGON_SHADER + "." + CommonAttributes.DIFFUSE_COLOR);
			for (int i = 0; i< numberOfFaces; i++){
				colors[i] = baseColor;
			}	
		
			jrView.updateFacesColors(colors);
		}
	}
}