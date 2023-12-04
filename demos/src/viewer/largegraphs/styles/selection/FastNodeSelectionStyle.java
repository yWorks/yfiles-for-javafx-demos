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
package viewer.largegraphs.styles.selection;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Node style that is used as a zoom-invariant selection decorator.
 * <p>
 * This style essentially displays a rectangle and scales its stroke thickness and brush by 1&nbsp;/&nbsp;zoom level.
 * This means that positioning considerations can still be done in world coordinates and the path doesn't require a
 * series of transformations to end up where it should be. The brush is scaled because the default selection
 * decoration uses a pixel checkerboard pattern which would otherwise be scaled with the zoom level.
 * </p>
 * <p>
 * This style caches the scaled pen to avoid creating a new one for every invocation of
 * {@link #updateVisual(IRenderContext, Node, INode)}. Thus, this style cannot be shared over multiple
 * {@link com.yworks.yfiles.view.GraphControl} instances because the zoom level might differ. If the
 * pen's paint is simply a solid color the scaling step can be omitted.
 * </p>
 */
public class FastNodeSelectionStyle extends AbstractNodeStyle {

  // region Properties

  /**
   * The cached instance of the scaled pen.
   */
  private Pen scaledPen;

  /**
   * The scale at which the cached pen was scaled.
   */
  private double scaledPenScale;

  private Color fill;

  /**
   * Gets the color used to fill the selection rectangle.
   */
  public Color getFill() {
    return fill;
  }

  /**
   * Sets the color used to fill the selection rectangle.
   */
  public void setFill(Color fill) {
    this.fill = fill;
  }

  private Pen pen;

  /**
   * Gets the pen used to draw the rectangle outline.
   */
  public Pen getPen() {
    return pen;
  }

  /**
   * Sets the pen used to draw the rectangle outline.
   */
  public void setPen(Pen pen) {
    this.pen = pen;
  }

  // endregion

  /**
   * Initializes a new instance with the given fill and pen.
   * @param fill The color used to fill the selection rectangle.
   * @param pen  The pen used to draw the rectangle outline.
   */
  public FastNodeSelectionStyle(Color fill, Pen pen) {
    this.fill = fill;
    this.pen = pen;
  }

  // region Style

  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    double scale = 1 / context.getZoom();
    RectD layout = getSelectionBounds(node, scale);
    Rectangle rect = createRectangle(layout);
    rect.setUserData(layout);
    rect.setFill(getFill());
    updatePen(rect, scale);
    return rect;
  }

  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, INode node) {
    if (!(oldVisual instanceof Rectangle)) {
      return createVisual(context, node);
    }
    Rectangle rect = (Rectangle) oldVisual;

    double scale = 1 / context.getZoom();
    RectD layout = getSelectionBounds(node, scale);
    RectD oldLayout = (RectD) rect.getUserData();

    if (!layout.equals(oldLayout)) {
      rect = updateRectangle(rect, layout);
      rect.setUserData(layout);
    }
    rect.setFill(getFill());
    updatePen(rect, scale);
    return rect;
  }

  /**
   * Creates the rectangle with the necessary properties.
   * @param layout The intended layout for the rectangle.
   */
  private Rectangle createRectangle(RectD layout) {
    return updateRectangle(new Rectangle(), layout);
  }

  /**
   * Updates the rectangle with the necessary properties.
   * @param rect   The rectangle to update.
   * @param layout The intended layout for the rectangle.
   */
  private Rectangle updateRectangle(Rectangle rect, RectD layout) {
    rect.setX(layout.x);
    rect.setY(layout.y);
    rect.setWidth(layout.width);
    rect.setHeight(layout.height);
    return rect;
  }

  /**
   * Re-creates the scaled pen if necessary and sets it on the rectangle.
   * @param shape The shape whose stroke brush will be updated.
   * @param scale The scale. This is 1&nbsp;/&nbsp;zoom level.
   */
  private void updatePen(Shape shape, double scale) {
    if (scale != scaledPenScale || scaledPen == null) {
      // If the cached brush no longer matches the scale, re-create it.
      scaledPen = pen.cloneCurrentValue();
      scaledPen.setThickness(pen.getThickness() * scale);
      scaledPen.freeze();
      scaledPenScale = scale;
    }
    scaledPen.styleShape(shape);
  }

  /**
   * Returns the size and position of the selection rectangle around a node.
   * @param node The node.
   * @param scale The scale. This is 1&nbsp;/&nbsp;zoom level.
   * @return The selection rectangle layout, enlarged by the scaled stroke thickness.
   */
  private RectD getSelectionBounds(INode node, double scale) {
    RectD layout = node.getLayout().toRectD().getEnlarged(getPen().getThickness() * scale);
    return layout;
  }

  // endregion
}
