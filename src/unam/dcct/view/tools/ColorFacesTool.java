package unam.dcct.view.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

/**
 * A tool that lets the user color individual faces of the displayed geometric object
 * by clicking on them. 
 * @author Fausto Salazar
 *
 */
public class ColorFacesTool extends AbstractTool implements View {
	
	private boolean enabled, 
			anyFacesPainted; // This is true when at least one face has been painted.
	private Appearance sceneContentAppearance;
	private Color selectedColor = ColorConverter.toJR(Constants.FACE_COLOR_CHOOSER_DEFAULT_COLOR);
	private InteractiveToolsPanel toolsPanel;
	private jRealityView jrView;
	private ColorFacesGeometry colorFacesGeometry;
	private boolean mapChromaticity;
	
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
				if (colorFacesGeometry==null){
					// Call this method in order to create the colorFacesGeometry
					createColorFacesGeometry();
				}
				colorFacesGeometry.updateFaceColors(pick.getIndex(), selectedColor);
				setAnyFacesPainted(true);
			}
		}
	}
	
	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}
	
	public void setEnabled(boolean enabled) {
		// If mapChromaticity is enabled, then it need to behave
		// as if it was always enabled, no matter if the 'activate' checkbox 
		// is checked or unchecked. 
		if (isMapChromaticityEnabled())
			enabled = true;
		this.enabled = enabled;
		
		activateMapChromaticityButton();
		
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
					if (ev.getKey().equals("polygonShader.diffuseColor")){						
						resetEverything();
					}
			}});
			
			// In order to work, I need to be notified when changes in jRealityView occur. 
			jrView.registerView(this);
			createColorFacesGeometry();
		}
		else if (!enabled){
			if (anyFacesPainted== false)
				resetEverything();
			else 
				toolsPanel.setEnabledColorFacesToolUserControls(false);
		}
	}
	
	private void resetEverything(){
		toolsPanel.setEnabledColorFacesToolUserControls(false);
		toolsPanel.getBtnEraseColoredFaces().setVisible(false);
		enabled = anyFacesPainted = mapChromaticity = false;
		toolsPanel.resetMapChromaticity();
		sceneContentAppearance = null;
		colorFacesGeometry = null;
		jrView.updateFacesColors(null);
		jrView.unregisterView(this);
	}

	@Override
	public void displayComplex() {
		if (isValid()){
			createColorFacesGeometry();
			
			activateMapChromaticityButton();
		}
	}
	
	private void createColorFacesGeometry(){
		GeometricComplex baseGeometry = (GeometricComplex)jrView.getGeometricObject();
		colorFacesGeometry = new ColorFacesGeometry(baseGeometry, colorFacesGeometry);
	}

	/**
	 * When called it cleans the already painted faces in the geometric object displayed.
	 */
	@Override
	public void updateChromaticity() {
		if (isValid()){
			colorFacesGeometry.setChromatic(Model.getInstance().isChromatic());
			colorFacesGeometry.updateFaceColors();
		}	
	}

	@Override
	public void reset() {
		resetEverything();
	}
	
	@Override
	public void creatingNewProtocolComplex() {
		if (isValid()){
			resetEverything();
		}
	}

	public boolean isAnyFacesPainted() {
		return anyFacesPainted;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setAnyFacesPainted(boolean anyFacesPainted) {
		this.anyFacesPainted = anyFacesPainted;
		if (anyFacesPainted == false){
			if (!enabled){
				resetEverything();
			}
			else{
				colorFacesGeometry.colorAllFacesFromAppearance();
			}
		}
		else{
			toolsPanel.getBtnEraseColoredFaces().setVisible(true);
		}
	}
	private boolean isValid(){
		return (enabled || anyFacesPainted);
	}
	
	public void setMapChromaticity(boolean b) {
		mapChromaticity = b;
		if (mapChromaticity){
			// Need to create it again but this time the 
			// data needed to support map chromaticity will be created.
			createColorFacesGeometry();
			colorFacesGeometry.colorFacesFromCommonAncestors(b);
		}
	}
	
	public boolean isMapChromaticityEnabled(){
		return mapChromaticity;
	}
	
	/**
	 * Checks if this moment is appropiate to enable that button. If so, activate it.  
	 */
	private void activateMapChromaticityButton(){
		// This button should only be displayed in the first round of execution.
		boolean isFirstRound = Model.getInstance().getRoundCount()==1;
		toolsPanel.getBtnMapChromaticity().setVisible(isFirstRound);
	}
	
	/**
	 * A wrapper class that enhances the {@link unam.dcct.view.geometry.GeometricComplex} class
	 * with the ability of having individually  {@link unam.dcct.view.geometry.Face}s.
	 * @author Fausto
	 *
	 */
	private class ColorFacesGeometry{
		private GeometricComplex innerGeometry;
		private ColorFacesGeometry parentGeometry;
		private Color[] chromaticColors,
						nonChromaticColors;
		private boolean chromatic;	
		private FacesCommonAncestorHelper facesCommonAncestorHelper;
		
		/**
		 * Creates an instance of a "face-colorable" geometric simplicial complex.
		 * @param baseGeometry The {@link unam.dcct.view.geometry.GeometricComplex} whose faces will be colorable. 
		 * @param parentGeometry The geometric complex of the previous round. This is to support the functionality that 
		 * if in the previous round the geometric complex had some faces colored, in the new complex the faces that 
		 * are a subdivision of those faces need to appear colored with the same color. 
		 */
		ColorFacesGeometry(GeometricComplex baseGeometry, ColorFacesGeometry parentGeometry){
			innerGeometry = baseGeometry;
			this.parentGeometry = parentGeometry;
			chromatic = baseGeometry.isChromatic();
			setFaceCommonAncestors();
			
			if (parentGeometry==null){
				colorAllFacesFromAppearance();
				return;
			}
			else if (baseGeometry.getComplex()!=parentGeometry.innerGeometry.getComplex()){
				colorFacesFromParentFaces();
			}
			else {
				// This happens when the disconnected faces mode is activated while this tool is active.
				chromaticColors = parentGeometry.chromaticColors;
				nonChromaticColors = parentGeometry.nonChromaticColors;
				updateFaceColors();
			}
		}
		
		private void setFaceCommonAncestors(){
			if (mapChromaticity)
				facesCommonAncestorHelper = new FacesCommonAncestorHelper(this);
		}
		
		private void colorFacesFromParentFaces(){
			getOrCreateColors();	
			ColorFacesCommand colorFacesFromParentCmd = new ColorFacesFromParentCommand();
			if (mapChromaticity){
				colorFacesFromParentCmd.execute(this);
				colorFacesFromCommonAncestors(chromatic);
			}else 
				executeCommandBothChromaticities(colorFacesFromParentCmd);
			
			updateFaceColors();
		}
		
		/**
		 * Colors the faces from the other mode's complex that correspond 
		 * to the current mode's complex faces. 
		 * @param chromatic The current mode of the complex
		 */
		private void colorFacesFromCommonAncestors(boolean chromatic) {
			facesCommonAncestorHelper.colorFacesFromCommonAncestors(chromatic);
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
			getOrCreateColors();
			executeCommandBothChromaticities(new ColorFacesFromAppearanceCommand());
			updateFaceColors();
		}
		
		/**
		 * It executes commands (from the "Command design pattern")
		 * to update both chromatic and non-chromatic complexes. 
		 */
		private void executeCommandBothChromaticities(ColorFacesCommand cmd){
			// Save original chromaticity state.
			boolean originalChromaticity = chromatic;
			setChromatic(true);
			cmd.execute(this);
			setChromatic(false);
			cmd.execute(this);
			// Restore the state as it was originally.
			setChromatic(originalChromaticity);
		}
		/**
		 * Updates the color of the face indexed by index with
		 * selectedColor and commands the visualizer (jReality) to reflect
		 * the updated list of colors in the geometric object currently displayed. 
		 * @param index The index of the face 
		 * @param selectedColor The new color of the face indexed by index
		 */
		void updateFaceColors(int index, Color selectedColor) {
			Color[] colors = getColors();
			if (colors[index]!=selectedColor){
				colors[index]=selectedColor;
				if (mapChromaticity)
					colorFacesFromCommonAncestors(chromatic);
				updateFaceColors();
			}
		}
		
		/** 
		 * Just commands the visualizer (jReality) to reflect
		 * the updated list of colors in the geometric object currently displayed. 
		 */
		void updateFaceColors(){
			jrView.updateFacesColors(getColors());
		}

		/**
		 * Returns an array containing the colors of this complex's faces. It such an array doesn't exists it creates an empty one. 
		 * @return An existing array containing the colors of this complex's faces or a new empty array if such colors didn't existed before calling this method.
		 */
		Color[] getOrCreateColors(){
			if (getColors()==null)
				executeCommandBothChromaticities(new CreateColorFacesCommand());
			return getColors();
		}
		
		List<Face> getFaces(){
			return innerGeometry.getFaces();
		}
		
		Color[] getColors(){
			return getColors(this.chromatic);
		}
		
		Color[] getColors(boolean chromatic){
			return chromatic ? chromaticColors : nonChromaticColors;
		}
		
		boolean isChromatic() {
			return chromatic;
		}

		void setChromatic(boolean chromatic) {
			this.chromatic = chromatic;
			innerGeometry.setChromatic(chromatic);
		}
		
		ColorFacesGeometry getParent(){
			return parentGeometry;
		}
		
		Color[] getParentColors(){
			return getParentColors(chromatic);
		}
		
		List<Face> getParentFaces(){
			return getParentFaces(chromatic);
		}
		
		Color[] getParentColors(boolean chromatic){
			return (chromatic? parentGeometry.chromaticColors : parentGeometry.nonChromaticColors);
		}
		
		List<Face> getParentFaces(boolean chromatic){
			return chromatic? parentGeometry.innerGeometry.getChromaticFaces() :
				parentGeometry.innerGeometry.getNonChromaticFaces();
		}
		
		int getNumberOfFaces(){
			return innerGeometry.getFacesIndices().length;
		}

		public void setColors(Color[] colors) {
			if (chromatic)
				this.chromaticColors = colors;
			else
				this.nonChromaticColors = colors;
		}
	}
	
	/** Helper class that supports the automatic coloring of non-chromatic (nc) faces
	 * when a corresponding chromatic (c) face is colored or viceversa.
	 * <br>
	 * It contains methods that help decide whether a nc face corresponds to
	 * c face or viceversa, so that when one of them if colored, the other is too. 
	 * <br>
	 * This relation between faces is as follows: A nc face corresponds to a c face (or viceversa)
	 * if both have the same common ancestor id.
	 * <br>
	 * The common ancestor id is the index of an ancestor face of both faces (remember that each face has a parent face)
	 * with respect to the ordering in the list of faces returned by the {@link unam.dcct.view.geometry.GeometricComplex#getFaces()}
	 * method. 
	 * <br>
	 * Such an ancestor id was created when in an ancestor geometric complex both the nc and c representations had the
	 * same number of faces (or simplices). Such rule was chosen because this represents a situation where it was 
	 * easy to relate both kinds of faces (as there was a 1:1 relation between both ), but in protocol complexes for other
	 * rounds the number of nc faces may be smaller than the number of c faces, so there is not a 1:1 relation anymore. 
	 * @author Fausto
	 *
	 */
	private class FacesCommonAncestorHelper{
		Map<Face, Integer> chromaticfacesCommonAncestorIds,
							nonChromaticfacesCommonAncestorIds;
		List<Face> chromaticFaces;
		List<Face> nonChromaticFaces;
		ColorFacesGeometry geometry;
		ColorFacesGeometry parentGeometry;
		GeometricComplex innerGeometry; 
		
		FacesCommonAncestorHelper parentColorFacesCommonAncestorHelper;
		
		FacesCommonAncestorHelper(ColorFacesGeometry geom){
			this.geometry = geom;
			innerGeometry = geometry.innerGeometry;
			parentGeometry = geometry.parentGeometry;
			if (parentGeometry!=null)
				parentColorFacesCommonAncestorHelper = geometry.parentGeometry.facesCommonAncestorHelper;
			setFaceCommonAncestors();
		}
		
		private void setFaceCommonAncestors() {
			chromaticfacesCommonAncestorIds = new HashMap<Face, Integer>();
			nonChromaticfacesCommonAncestorIds = new HashMap<Face, Integer>();
			
			chromaticFaces = innerGeometry.getChromaticFaces();
			nonChromaticFaces = innerGeometry.getNonChromaticFaces();
			int chromaticFacesCount = chromaticFaces.size();
			int nonChromaticFacesCount = nonChromaticFaces.size();
			// If sizes are equal then build a new ancestors map
			if (chromaticFacesCount == nonChromaticFacesCount){
				Iterator<Face> cIter = chromaticFaces.iterator();
				Iterator<Face> ncIter = nonChromaticFaces.iterator();
				int index = 0;
				while (cIter.hasNext()){
					chromaticfacesCommonAncestorIds.put(cIter.next(), index);
					nonChromaticfacesCommonAncestorIds.put(ncIter.next(), index);
					++index;
				}
			}else {
				inheritAncestorIds(true);
				inheritAncestorIds(false);
			}
			// Don't need it anymore
			if (parentGeometry!=null)
				parentGeometry.facesCommonAncestorHelper =null;
		}
		
		/**
		 * Inherits the ancestor ids from the immediate parent.
		 * @param chromatic
		 */
		private void inheritAncestorIds(boolean chromatic){
			if (parentGeometry==null) return;
			Map<Face, Integer> commonAncestorIds = getCommonAncestorIds( chromatic);
			Map<Face, Integer> parentCommonAncestorIds = getParentCommonAncestorIds(chromatic);
			// Inherit ancestor ids
			for (Face f :getFaces(chromatic) ){
				Integer foundParentId = parentCommonAncestorIds.get(f.getParent());
				if (foundParentId!=null)
					commonAncestorIds.put(f, foundParentId);
			}
		}
		
		List<Face> getFaces(boolean chromatic){
			return chromatic? innerGeometry.getChromaticFaces() : innerGeometry.getNonChromaticFaces();
		}
		
		Map<Face, Integer> getCommonAncestorIds(boolean chromatic){
			return chromatic? chromaticfacesCommonAncestorIds : 
				nonChromaticfacesCommonAncestorIds;
		}
		
		Map<Face, Integer> getParentCommonAncestorIds(boolean chromatic){
			return chromatic? parentColorFacesCommonAncestorHelper.chromaticfacesCommonAncestorIds : 
				parentColorFacesCommonAncestorHelper.nonChromaticfacesCommonAncestorIds;
		}
		
		/**
		 * Colors the faces from the other mode's complex that correspond 
		 * to the current mode's complex faces. 
		 * @param chromatic The current mode of the complex
		 */
		void colorFacesFromCommonAncestors(boolean chromatic) {
			Color[] colors = geometry.getColors(chromatic);
			Color[] otherColors = geometry.getColors(!chromatic);
			Map<Face, Integer> commonAncestorIds = getCommonAncestorIds(chromatic);
			Map<Face, Integer> otherCommonAncestorIds = getCommonAncestorIds(!chromatic);
			
			List<Face> faces = getFaces(chromatic);
			List<Face> otherFaces = getFaces(!chromatic);
			// This is not efficient, can be improved. 
			for (Face f : faces){
				for (Face of : otherFaces){
					if (sameAncestorId(f, of, commonAncestorIds,
							otherCommonAncestorIds )){
						int index = faces.indexOf(f);
						if (index >= 0 && index < colors.length)
							otherColors[otherFaces.indexOf(of)] = colors[index];
					}
				}
			}
		}
		/**
		 * Decides if both faces have the same ancestor id. 
		 * @param f one face (chromatic or non-chromatic)
		 * @param of the other face (chromatic or non-chromatic)
		 * @param commonAncestorIds The set of ancestor ids where f may be registered.
		 * @param otherCommonAncestorIds The set of ancestor ids where of may be registered.
		 * @return
		 */
		private boolean sameAncestorId(Face f, Face of, 
				Map<Face, Integer> commonAncestorIds, 
				Map<Face, Integer> otherCommonAncestorIds) {
			Integer id = commonAncestorIds.get(f);
			Integer oid = otherCommonAncestorIds.get(of);
			if (id!=null && oid!=null)
				return id.equals(oid);
			return false;
		}
	}
	
	private interface ColorFacesCommand{
		void execute(ColorFacesGeometry geometry);
	}
	
	private class ColorFacesFromAppearanceCommand implements ColorFacesCommand{
		@Override
		public void execute(ColorFacesGeometry geometry) {
			Color[] colors = geometry.getColors();			
			Color baseColor = (Color)  sceneContentAppearance.getAttribute(CommonAttributes.POLYGON_SHADER + "." + CommonAttributes.DIFFUSE_COLOR);
			for (int i = 0; i< geometry.getNumberOfFaces(); i++){
				colors[i] = baseColor;
			}	
		}
	}
	/**
	 * Colors the new faces with the colors of their parent faces's colors.
	 * @author Fausto
	 */
	private class ColorFacesFromParentCommand implements ColorFacesCommand{
		@Override
		public void execute(ColorFacesGeometry geometry) {
			Color[] colors = geometry.getColors();
			Color[] parentColors = geometry.getParentColors();
			int i = 0;
			List<Face> parentFaces = geometry.getParentFaces();
			for (Face f : geometry.getFaces()){
				Face parentFace = f.getParent();
				colors[i++] = parentColors[parentFaces.indexOf(parentFace)];
			}		
		}
		
	}
	
	private class CreateColorFacesCommand implements ColorFacesCommand {
		@Override
		public void execute(ColorFacesGeometry geometry) {
			Color[] colors = geometry.getColors();
			if (colors == null)
				geometry.setColors(new Color[geometry.getNumberOfFaces()]);
		}
	}
}
