/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.css;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;

import java.util.Objects;

/**
 * An edge style that can be styled with CSS.
 * <p>
 * You can change the visualization of the edge path by defining properties for the style class {@code edge-style}.
 * As the edge path is essentially a JavaFX
 * <a href="http://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#path">Path</a>,
 * the {@code edge-style} style class supports path properties.
 * </p>
 * <p>
 * Changing the stroke or stroke width of the edge path will also be applied to the arrows of the edge.
 * </p>
 */
public class CssEdgeStyle extends AbstractEdgeStyle {
  // identity transformation
  private static final Matrix2D IDENTITY = new Matrix2D();

  private String[] styleClasses;

  private Arrow sourceArrow;
  private Arrow targetArrow;

  /**
   * Initializes a new {@code CssEdgeStyle} instance using no arrow at both ends of the edge.
   */
  public CssEdgeStyle() {
  }

  /**
   * Initializes a new {@code CssEdgeStyle} instance using the given style classes.
   */
  public CssEdgeStyle(String... styleClasses) {
    this(new Arrow(ArrowType.NONE), new Arrow(ArrowType.NONE), styleClasses);
  }

  /**
   * Initializes a new {@code CssEdgeStyle} instance using the given arrows and the given style classes.
   */
  public CssEdgeStyle(Arrow sourceArrow, Arrow targetArrow, String... styleClasses) {
    setSourceArrow(sourceArrow);
    setTargetArrow(targetArrow);
    this.styleClasses = styleClasses;
  }

  /**
   * Returns the style classes.
   */
  public String[] getStyleClasses() {
    return styleClasses;
  }

  /**
   * Sets the style classes.
   */
  public void setStyleClasses(String[] styleClasses) {
    this.styleClasses = styleClasses;
  }

  /**
   * Returns the arrow at the beginning of the edge.
   */
  public Arrow getSourceArrow() {
    return sourceArrow;
  }

  /**
   * Specifies the arrow at the beginning of the edge.
   */
  public void setSourceArrow(Arrow arrow) {
    this.sourceArrow = arrow;
  }

  /**
   * Returns the arrow at the end of the edge.
   */
  public Arrow getTargetArrow() {
    return targetArrow;
  }

  /**
   * Specifies the arrow at the end of the edge.
   */
  public void setTargetArrow(Arrow arrow) {
    this.targetArrow = arrow;
  }

  /**
   * Creates the visual for an edge.
   */
  @Override
  protected Node createVisual(IRenderContext context, IEdge edge) {
    // create a group that holds all visuals needed to paint the edge
    CssEdgeVisual group = new CssEdgeVisual();

    Path path = new Path();
    if (styleClasses != null) {
      path.getStyleClass().addAll(styleClasses);
    }
    group.add(path);

    // update the arrow if stroke or width has been changed
    path.strokeProperty().addListener((observable, oldPaint, newPaint) ->
        updateArrows(context, newPaint, path.getStrokeWidth()));
    path.strokeWidthProperty().addListener((observable, oldWidth, newWidth) ->
        updateArrows(context, path.getStroke(), newWidth));

    GeneralPath generalPath = getPath(edge);
    update(group, generalPath);

    // add visuals that paint the arrows
    addArrows(context, group, edge, generalPath, getSourceArrow(), getTargetArrow());

    return group;
  }

  /**
   * Re-renders the edge using the old visual instead of creating a new one for each call.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, IEdge edge) {
    if (!(oldVisual instanceof CssEdgeVisual)) {
      return createVisual(context, edge);
    }
    CssEdgeVisual group = (CssEdgeVisual) oldVisual;

    GeneralPath generalPath = getPath(edge);
    if (!Objects.equals(generalPath, group.generalPath)) {
      update(group, generalPath);
    }

    // since the edge's ends might have changed their positions or orientations, we also have to update the visuals
    // painting the arrows
    updateArrows(context, group, edge, generalPath, getSourceArrow(), getTargetArrow());

    return group;
  }

  /**
   * Updates the given visual's {@link Path} instance to reflect the geometry
   * of the given {@link GeneralPath} instance.
   */
  private void update(CssEdgeVisual group, GeneralPath generalPath) {
    Path path = (Path) group.getChildren().get(0);
    generalPath.updatePath(path, IDENTITY);
    group.generalPath = generalPath;
  }

  /**
   * Updates the arrows if the color or the width of edge has been changed.
   */
  private void updateArrows(IRenderContext context, Paint stroke, Number width) {
    if (sourceArrow != null) {
      sourceArrow.setPaint(stroke);
      sourceArrow.setPen(new Pen(stroke, width.doubleValue()));
    }
    if (targetArrow != null) {
      targetArrow.setPaint(stroke);
      targetArrow.setPen(new Pen(stroke, width.doubleValue()));
    }
    context.getCanvasControl().invalidate();
  }

  /**
   * Creates a {@link com.yworks.yfiles.geometry.GeneralPath} from the segments of the given edge.
   */
  @Override
  protected GeneralPath getPath(IEdge edge) {
    // create a general path from the source port over the bends to the target port of the edge
    GeneralPath path = new GeneralPath();
    path.moveTo(edge.getSourcePort().getLocation());
    for (IBend bend : edge.getBends()) {
      path.lineTo(bend.getLocation());
    }
    path.lineTo(edge.getTargetPort().getLocation());

    // crop the edge's path at its nodes

    // take the arrows into account when cropping the path
    return cropPath(edge, getSourceArrow(), getTargetArrow(), path);
  }

  /**
   * Stores the geometry of the visualization to speed-up visualization updates in
   * {@link CssEdgeStyle#updateVisual(IRenderContext, Node, IEdge)} if the edge geometry has not changed.
   */
  private static class CssEdgeVisual extends VisualGroup {
    GeneralPath generalPath;
  }
}
