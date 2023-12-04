/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for JavaFX functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for JavaFX version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for JavaFX powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for JavaFX
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package tutorial02_CustomStyles.step27_CustomGroupBounds;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Path;
import javafx.scene.transform.Affine;

/**
 * An implementation of {@link com.yworks.yfiles.graph.styles.IArrow} that creates and updates the visual representation of a
 * custom arrow. To reduce the amount of memory we apply the flyweight pattern here: since all the arrows with the
 * same thickness look the same, we can share one shape and fill among all ArrowVisuals painting an arrow with the same
 * thickness. The only thing that differs and must be stored separately in each ArrowVisual, is the transformation that
 * moves and rotates the arrow to the current location and direction of its edge's endpoint.
 * <p>
 * Note that the flyweight pattern used in this class only performs well if a MySimpleArrow instance is only shared by
 * edges having the same thickness. Otherwise the arrow shape and fill that are stored in the arrow instance are
 * updated several times in each render step.
 * </p>
 */
public class MySimpleArrow implements IArrow, IVisualCreator, IBoundsProvider {
  // these variables hold objects shared by all ArrowVisuals painting an arrow with the same thickness
  // the paint to fill the arrow with
  private static final Paint DEFAULT_FILL = new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT,
      new Stop(0, Color.rgb(180, 180, 180, 1)),
      new Stop(0.5, Color.rgb(50, 50, 50, 1)),
      new Stop(1, Color.rgb(150, 150, 150, 1)));

  // the shape of the arrow at a normal node
  private GeneralPath nodeArrowShape;

  //////////////// New in this sample ////////////////
  // the shape of the arrow at a group node
  private GeneralPath groupArrowShape;
  ////////////////////////////////////////////////////

  // these variables hold the state for the transformation and differ from ArrowVisual to ArrowVisual; they are
  // populated in getVisualCreator and getBoundsProvider and assigned to the ArrowVisual in createVisual() and
  // updateVisual()
  // the location of the arrow
  private PointD anchor;
  // the direction of the arrow
  private PointD direction;

  //////////////// New in this sample ////////////////
  // the node at the arrow
  private INode node;
  ////////////////////////////////////////////////////

  // the thickness of the arrow used to calculate arrowShape and arrowFill
  private double thickness;

  // backing field for below getter/setter
  private double fallbackThickness;

  /**
   * Initializes a new <code>MySimpleArrow</code> instance and sets the fallback thickness to 2.0.
   */
  public MySimpleArrow() {
    setFallbackThickness(2.0);
    nodeArrowShape = new GeneralPath();
    groupArrowShape = new GeneralPath();
  }

  /**
   * Gets the fallback thickness of the arrow that is used if an edge doesn't use a MySimpleEdgeStyle.
   */
  public double getFallbackThickness() {
    return fallbackThickness;
  }

  /**
   * Sets the fallback thickness of the arrow that is used if an edge doesn't use a MySimpleEdgeStyle.
   */
  public void setFallbackThickness(double thickness) {
    this.fallbackThickness = thickness;
  }

  /**
   * Calculates the thickness for the given edge and updates the shape and fill if necessary.
   */
  private void updateThickness(IEdge edge) {
    double newThickness = calculateThickness(edge);
    if (this.thickness != newThickness) {
      this.thickness = newThickness;
      // since the thickness has been changed, we must update the shape and fill of the arrow
      updateShape();
    }
  }

  /**
   * Calculates the thickness to use for the next visual creation.
   * @param edge the edge to read the thickness from
   */
  private double calculateThickness(IEdge edge) {
    // get the edge's thickness
    if (edge.getStyle() instanceof MySimpleEdgeStyle) {
      MySimpleEdgeStyle style = (MySimpleEdgeStyle) edge.getStyle();
      return style.getPathThickness();
    } else {
      return getFallbackThickness();
    }
  }

  /**
   * Updates the shape of the arrow with the current thickness.
   */
  private void updateShape() {
    nodeArrowShape.clear();
    nodeArrowShape.moveTo(-7, -thickness * 0.5);
    nodeArrowShape.lineTo(-7, thickness * 0.5);
    nodeArrowShape.cubicTo(-5, thickness * 0.5, -1.5, thickness * 0.5, 1, thickness * 1.666);
    nodeArrowShape.cubicTo(0, thickness * 0.833, 0, -thickness * 0.833, 1, -thickness * 1.666);
    nodeArrowShape.cubicTo(-1.5, -thickness * 0.5, -5, -thickness * 0.5, -7, -thickness * 0.5);
    nodeArrowShape.close();

    //////////////// New in this sample ////////////////
    groupArrowShape.clear();
    groupArrowShape.moveTo(-7, -thickness * 0.5);
    groupArrowShape.lineTo(-7, thickness * 0.5);
    groupArrowShape.lineTo(-thickness * 0.5, thickness * 0.5);
    groupArrowShape.quadTo(0, thickness * 0.5, 0, 0);
    groupArrowShape.quadTo(0, -thickness * 0.5, -thickness * 0.5, -thickness * 0.5);
    groupArrowShape.close();
    ////////////////////////////////////////////////////
  }

  /**
   * Returns the length of the arrow, i.e. the distance from the arrow's tip to the position where the visual
   * representation of the edge's path should begin. Always returns 5.
   */
  @Override
  public double getLength() {
    return 5;
  }

  /**
   * Gets the cropping length associated with this instance. Always returns 1. This value is used by {@link
   * com.yworks.yfiles.graph.styles.IEdgeStyle}s to let the edge appear to end shortly before its actual target.
   */
  @Override
  public double getCropLength() {
    return 1;
  }

  /**
   * Gets an {@link com.yworks.yfiles.view.IVisualCreator} implementation that will create the visual
   * for this arrow at the given location using the given direction for the given edge.
   * @param edge      the edge this arrow belongs to
   * @param atSource  whether this will be the source arrow
   * @param anchor    the anchor point for the tip of the arrow
   * @param direction the direction the arrow is pointing in
   * @return itself as a flyweight
   */
  @Override
  public IVisualCreator getVisualCreator(IEdge edge, boolean atSource, PointD anchor, PointD direction) {
    updateThickness(edge);

    this.anchor = anchor;
    this.direction = direction;

    //////////////// New in this sample ////////////////
    this.node = atSource ? edge.getSourceNode() : edge.getTargetNode();
    ////////////////////////////////////////////////////

    return this;
  }

  /**
   * Gets an {@link com.yworks.yfiles.view.IBoundsProvider} implementation that can yield this arrow's bounds if
   * painted at the given location using the given direction for the given edge.
   * @param edge      the edge this arrow belongs to
   * @param atSource  whether this will be the source arrow
   * @param anchor    the anchor point for the tip of the arrow
   * @param direction the direction the arrow is pointing in
   * @return an implementation of the {@link com.yworks.yfiles.view.IBoundsProvider} interface that can subsequently
   * be used to query the bounds. Clients will always call this method before using the implementation and may not cache
   * the instance returned. This allows for applying the flyweight design pattern to implementations.
   */
  @Override
  public IBoundsProvider getBoundsProvider(IEdge edge, boolean atSource, PointD anchor, PointD direction) {
    updateThickness(edge);

    this.anchor = anchor;
    this.direction = direction;

    //////////////// New in this sample ////////////////
    this.node = atSource ? edge.getSourceNode() : edge.getTargetNode();
    ////////////////////////////////////////////////////

    return this;
  }

  /**
   * This method is called by the framework to create a {@link Node} that will
   * be included into the {@link com.yworks.yfiles.view.IRenderContext}.
   * @param context the context that describes where the visual will be used
   * @return the arrow visual to include in the canvas object visual tree
   */
  @Override
  public Node createVisual(IRenderContext context) {
    ArrowVisual visual = new ArrowVisual();

    //////////////// New in this sample ////////////////
    GeneralPath arrowShape = isGroupNode(node, (GraphControl) context.getCanvasControl()) ? groupArrowShape : nodeArrowShape;
    ////////////////////////////////////////////////////

    visual.update(arrowShape, anchor, direction);
    return visual;
  }

  /**
   * The {@link CanvasControl} uses this method to give implementations a chance to update an
   * existing visual that has previously been created by the same instance during a call to {@link
   * #createVisual(com.yworks.yfiles.view.IRenderContext)}.
   */
  @Override
  public Node updateVisual(IRenderContext context, Node oldVisual) {
    ArrowVisual visual = (ArrowVisual) oldVisual;

    //////////////// New in this sample ////////////////
    GeneralPath arrowShape = isGroupNode(node, (GraphControl) context.getCanvasControl()) ? groupArrowShape : nodeArrowShape;
    ////////////////////////////////////////////////////

    visual.update(arrowShape, anchor, direction);
    return visual;
  }

  //////////////// New in this sample ////////////////
  /**
   * Checks whether or not a given node is a group node.
   */
  private static boolean isGroupNode(INode node, GraphControl graphControl) {
    if (!graphControl.getGraph().contains(node)) {
      // node is a dummy for edge-like connectors from a node to its labels
      return false;
    }
    IFoldingView foldingView = graphControl.getGraph().getFoldingView();
    return foldingView != null && foldingView.getManager().getMasterGraph().isGroupNode(foldingView.getMasterItem(node));
  }
  ////////////////////////////////////////////////////

  /**
   * Returns the bounds of the arrow for the current flyweight configuration.
   */
  @Override
  public RectD getBounds(ICanvasContext context) {
    return new RectD(anchor.getX() - 8 - thickness, anchor.getY() - 8 - thickness, 16 + thickness * 2, 16 + thickness * 2);
  }

  /**
   * A {@link VisualGroup} that paints the arrow as {@link Path} that forms a suction cup.
   * Note that we paint the arrow at the origin and move and rotate the group to the current
   * location and direction of its edge's endpoint.
   */
  private static class ArrowVisual extends VisualGroup {
    // identity transformation
    private static final Matrix2D IDENTITY = new Matrix2D();

    // the path that paints the arrow
    private Path path;
    // transform that rotates and moves the arrow to correct position
    private Affine transform;

    ArrowVisual() {
      transform = new Affine();
      path = new Path();
      path.setFill(DEFAULT_FILL);
      Pen.getTransparent().styleShape(path);
      path.getTransforms().add(transform);
      this.add(path);
    }

    /**
     * Updates the location and direction of the arrow.
     */
    void update(GeneralPath arrowShape, PointD anchor, PointD direction) {
      arrowShape.updatePath(path, IDENTITY);
      transform.setToTransform(direction.getX(), -direction.getY(), anchor.getX(), direction.getY(), direction.getX(), anchor.getY());
    }
  }
}
