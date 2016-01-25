package unam.dcct.view.UI;

import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;

public class SelectFaceTool extends AbstractTool {
	private Color selectionColor;
	private boolean selected;
	
	public SelectFaceTool() {
		super(InputSlot.POINTER_HIT);
	}
	@Override
	public void activate(ToolContext tc) {
		selected = !selected;
		SceneGraphComponent cmp = tc.getRootToLocal().getLastComponent();

		if (selected){
			cmp.getAppearance().setAttribute(CommonAttributes.POLYGON_SHADER+"."+CommonAttributes.DIFFUSE_COLOR, selectionColor);
		}
		else {
			cmp.getAppearance().setAttribute(CommonAttributes.POLYGON_SHADER+"."+CommonAttributes.DIFFUSE_COLOR, Appearance.INHERITED);
		}
	}
	@Override
	public void deactivate(ToolContext tc) {	}
	public Color getSelectionColor() {
		return selectionColor;
	}
	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}
}
