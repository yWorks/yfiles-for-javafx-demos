/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 * Label style that is used as a zoom-invariant selection decorator.
 * <p>
 * This style essentially displays a rotated rectangle and scales its stroke thickness and brush by 1 / zoom level.
 * This means that positioning considerations can still be done in world coordinates and the path doesn't require a
 * series of transformations to end up where it should be.
 * </p>
 * <p>
 * Since the default label selection decorator uses a solid line instead of a pattern, this style doesn't scale or cache
 * its stroke pen, unlike {@link FastEdgeSelectionStyle} or {@link FastNodeSelectionStyle}.
 * </p>
 */
public class FastLabelSelectionStyle extends AbstractLabelStyle {

  // region Properties

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
   * @param pen The pen used to draw the rectangle outline.
   */
  public FastLabelSelectionStyle(Color fill, Pen pen) {
    this.fill = fill;
    this.pen = pen;
  }

  // region Style

  @Override
  protected Node createVisual(IRenderContext context, ILabel label) {
    double scale = 1 / context.getZoom();
    IOrientedRectangle layout = getSelectionBounds(label, scale);

    Rectangle path = createGeometry(layout);
    path.setFill(fill);
    pen.styleShape(path);
    path.setStrokeWidth(pen.getThickness() * scale);

    path.setUserData(layout);
    return path;
  }

  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, ILabel label) {
    if (!(oldVisual instanceof Rectangle)) {
      return createVisual(context, label);
    }
    Rectangle path = (Rectangle) oldVisual;

    double scale = 1 / context.getZoom();
    IOrientedRectangle layout = getSelectionBounds(label, scale);
    IOrientedRectangle oldLayout = (IOrientedRectangle) path.getUserData();

    if (!layout.equals(oldLayout)) {
      path = updateGeometry(path, layout);
      path.setUserData(layout);
    }
    path.setFill(fill);
    path.setStrokeWidth(pen.getThickness() * scale);
    return path;
  }

  /**
   * Creates the geometry for the selection rectangle.
   * @param layout The label's layout.
   * @return The selection rectangle geometry.
   */
  private Rectangle createGeometry(IOrientedRectangle layout) {
    return updateGeometry(new Rectangle(), layout);
  }

  /**
   * Updates the geometry for the selection rectangle.
   * @param path   The path to update.
   * @param layout The label's layout.
   * @return The selection rectangle geometry.
   */
  private Rectangle updateGeometry(Rectangle path, IOrientedRectangle layout) {
    double upY = layout.getUpY();
    double upX = layout.getUpX();
    double width = layout.getWidth();
    double height = layout.getHeight();
    double layoutX = layout.getAnchorX();
    double layoutY = layout.getAnchorY();

    Transform transform = upY > 0
        ? Affine.affine(upY, -upX, upX, upY, layoutX - upY * width, layoutY + upX * width)
        : Affine.affine(-upY, upX, -upX, -upY, layoutX + upX * height, layoutY + upY * height);

    path.setWidth(layout.getWidth());
    path.setHeight(layout.getHeight());
    path.getTransforms().clear();
    path.getTransforms().add(transform);
    return path;
  }

  @Override
  protected SizeD getPreferredSize(ILabel label) {
    return label.getLayout().toSizeD();
  }

  /**
   * Returns an {@link IOrientedRectangle} representing the selection rectangle around the label.
   * @param label The label.
   * @param scale The scale. This is 1 / zoom level.
   * @return The selection rectangle, enlarged by the scaled pen thickness.
   */
  private IOrientedRectangle getSelectionBounds(ILabel label, double scale) {
    IOrientedRectangle layout = label.getLayout();
    // Normally I'd say scale / 2 would be correct here, but scale / 4 reproduces the default label selection exactly.
    double amount = getPen().getThickness() * scale / 4;
    PointD up = layout.getUp();
    PointD anchor = layout.getAnchorLocation();
    PointD right = new PointD(-up.getY(), up.getX());
    double width = layout.getWidth();
    double height = layout.getHeight();

    double newAnchorX = anchor.getX() - up.getX() * amount - right.getX() * amount;
    double newAnchorY = anchor.getY() - up.getY() * amount - right.getY() * amount;
    double newWidth = width + amount * 2;
    double newHeight = height + amount * 2;

    return new OrientedRectangle(newAnchorX, newAnchorY, newWidth, newHeight, up.getX(), up.getY());
  }

  // endregion
}