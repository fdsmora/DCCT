package unam.dcct.view.tools;

import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.data.Attribute;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.FaceDragEvent;
import de.jreality.tools.FaceDragListener;
import de.jreality.tools.LineDragEvent;
import de.jreality.tools.LineDragListener;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import unam.dcct.view.jRealityView;

/**
 * A custom tool for dragging vertices, edges and faces.
 * @author Fausto Salazar
 *
 */
public class DragGeometryTool extends DragEventTool {
	
	private boolean vertexDragEnabled = true;
	private boolean edgeDragEnabled = true;
	private boolean faceDragEnabled = true;
	
	private jRealityView jrView;	

	public DragGeometryTool(){
		jrView = jRealityView.getInstance();
		
		addDragListeners();
	}
	
	/**
	 * Adds the listeners that handles the events of
	 * vertex, edge and face dragging. 
	 */
	private void addDragListeners() {
		// Add drag vertices capability
		addPointDragListener(new PointDragListener() {
			public void pointDragEnd(PointDragEvent e) {
			}
			public void pointDragStart(PointDragEvent e) {
			}
			public void pointDragged(PointDragEvent e) {
				if (vertexDragEnabled)
					DragGeometryTool.this.pointDragged(e);
			}
		});
		
		// Add drag edges capability
		addLineDragListener(new LineDragListener() {
			
			private IndexedLineSet lineSet;
			private double[][] points;
			
			public void lineDragStart(LineDragEvent e) {
				if (edgeDragEnabled){
					lineSet = e.getIndexedLineSet();
					points=new double[lineSet.getNumPoints()][];
					lineSet.getVertexAttributes(Attribute.COORDINATES).toDoubleArrayArray(points);
				}
			}

			public void lineDragged(LineDragEvent e) {
				if (edgeDragEnabled){
					double[][] newPoints=(double[][])points.clone();
					Matrix trafo=new Matrix();
					MatrixBuilder.euclidean().translate(e.getTranslation()).assignTo(trafo);
					int[] lineIndices=e.getLineIndices();
					for(int i=0;i<lineIndices.length;i++){
						newPoints[lineIndices[i]]=trafo.multiplyVector(points[lineIndices[i]]);
						jrView.updateVertexCoordinates(lineIndices[i], newPoints[lineIndices[i]][0], newPoints[lineIndices[i]][1], newPoints[lineIndices[i]][2]);
					}
					// I think this is not necessary, but I leave it (commented) in case. 
					// lineSet.setVertexAttributes(Attribute.COORDINATES,StorageModel.DOUBLE_ARRAY.array(3).createReadOnly(newPoints));	
				}
			}

			public void lineDragEnd(LineDragEvent e) {
			}			
		});
		
		// Add drag faces capability
		addFaceDragListener(new FaceDragListener() {
			
			private IndexedFaceSet faceSet;
			private double[][] points;
						
			public void faceDragStart(FaceDragEvent e) {
				if (faceDragEnabled){
					faceSet = e.getIndexedFaceSet();
					points=new double[faceSet.getNumPoints()][];
					points = faceSet.getVertexAttributes(Attribute.COORDINATES).toDoubleArrayArray(null);
				}
			}

			public void faceDragged(FaceDragEvent e) {
				if (faceDragEnabled){
					double[][] newPoints=(double[][])points.clone();
					Matrix trafo=new Matrix();
					MatrixBuilder.euclidean().translate(e.getTranslation()).assignTo(trafo);
					int[] faceIndices=e.getFaceIndices();
					for(int i=0;i<faceIndices.length;i++){
						newPoints[faceIndices[i]]=trafo.multiplyVector(points[faceIndices[i]]);
						jrView.updateVertexCoordinates(faceIndices[i], newPoints[faceIndices[i]][0], newPoints[faceIndices[i]][1], newPoints[faceIndices[i]][2]);
					}
					// I think this is not necessary, but I leave it (commented) in case. 
//							faceSet.setVertexAttributes(Attribute.COORDINATES,StorageModel.DOUBLE_ARRAY.array(3).createReadOnly(newPoints));	
					
					// Test code for developing 'faces click and color' feature. 
					//					faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE_ARRAY.array()..createReadOnly(toDoubleArray(new Color[]{Color.red})));
//							faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE_ARRAY.array().toDoubleArrayArray(new float[][]{Color.red.getColorComponents(null)}));
					
//							faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE3_INLINED.createReadOnly((toDoubleArray(new Color[]{Color.blue}))));
//							viewer.getViewer().getSceneRoot().getChildComponent(1).getAppearance().getAttribute(CommonAttributes.POLYGON_SHADER+"."+CommonAttributes.DIFFUSE_COLOR);
//							faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE3_INLINED.createWritableDataList(toDoubleArray(new Color[]{Color.blue, Color.gray, Color.cyan})));
//							faceSet.getFaceAttributes(Attribute.COLORS);
//							faceSet.setFaceAttributes(Attribute.COLORS, new DoubleArrayArray.Inlined( toDoubleArray(new Color[]{Color.blue, Color.gray, Color.cyan}), 1 ));

				}
			}

			public void faceDragEnd(FaceDragEvent e) {
			}			
		});
	}

	/**
	 * Handles the event of dragging a vertex with the mouse, updating the vertex coordinates and redrawing the vertex with the new updated coordinates. 
	 * @param e The Event object that contains all the information about the dragging event such as the new vertex coordinates after the drag. 
	 */
	private void pointDragged(PointDragEvent e) {
		jrView.updateVertexCoordinates(e.getIndex(), e.getX(), e.getY(), e.getZ());
	}
	
	public void setVertexDragEnabled(boolean vertexDragEnabled) {
		this.vertexDragEnabled = vertexDragEnabled;
	}

	public void setEdgeDragEnabled(boolean edgeDragEnabled) {
		this.edgeDragEnabled = edgeDragEnabled;
	}

	public void setFaceDragEnabled(boolean faceDragEnabled) {
		this.faceDragEnabled = faceDragEnabled;
	}

}
