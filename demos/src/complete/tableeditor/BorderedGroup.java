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
package complete.tableeditor;

import com.yworks.yfiles.geometry.InsetsD;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * A {@link Group} that can have arbitrary borders on each of their sides. The borders are aligned like
 * in a {@link javafx.scene.layout.BorderPane}.
 * <p>
 *   The BorderedGroup consists of a background {@link Rectangle} that is as wide and tall as the overall width and height
 *   and filled by the {@link #getBackgroundPaint()}
 * </p>
 * The thickness of the borders are determined by {@link InsetsD}
 * which can be set by {@link #setThickness(com.yworks.yfiles.geometry.InsetsD)}. The width of the whole {@link Group}
 * always includes the thickness of the borders.
 */
public class BorderedGroup extends Group {

  /**
   * Property for the thickness of the borders.
   */
  private ObjectProperty<InsetsD> thickness = new SimpleObjectProperty<InsetsD>(this, "thickness", InsetsD.EMPTY);

  /**
   * Width property of the BorderedGroup.
   */
  private DoubleProperty width = new SimpleDoubleProperty(this, "width", 0);

  /**
   * Height property of the BorderedGroup.
   */
  private DoubleProperty height = new SimpleDoubleProperty(this, "height", 0);

  private Paint backgroundPaint;
  private Paint borderPaint;

  private Rectangle background;
  private Rectangle top;
  private Rectangle right;
  private Rectangle bottom;
  private Rectangle left;

  public BorderedGroup(double width, double height) {
    // Install listeners to the properties that call update() when something changes
    this.thickness.addListener((observableValue, oldThickness, newThickness) -> update());
    this.width.addListener((observableValue, oldWidth, newWidth) -> update());
    this.height.addListener((observableValue, oldHeight, newHeight) -> update());

    background = new Rectangle(0, 0, 0, 0);
    background.setFill(backgroundPaint);

    top = new Rectangle(0, 0, 0, 0);
    top.setFill(borderPaint);
    right = new Rectangle(0, 0, 0, 0);
    right.setFill(borderPaint);
    bottom = new Rectangle(0, 0, 0, 0);
    bottom.setFill(borderPaint);
    left = new Rectangle(0, 0, 0, 0);
    left.setFill(borderPaint);

    this.getChildren().addAll(background, top, right, bottom, left);

    setWidth(width);
    setHeight(height);
  }

  /**
   * Gets the {@link Paint} that is used for the fill color of the background rectangle.
   */
  public Paint getBackgroundPaint() {
    return backgroundPaint;
  }

  /**
   * Sets the {@link Paint} that is used for the fill color of the background rectangle.
   */
  public void setBackgroundPaint(Paint backgroundPaint) {
    this.backgroundPaint = backgroundPaint;
    background.setFill(backgroundPaint);
  }

  /**
   * Gets the {@link Paint} that is used for the fill color of the border rectangles.
   */
  public Paint getBorderPaint() {
    return borderPaint;
  }

  /**
   * Sets the {@link Paint} that is used for the fill color of the border rectangles.
   */
  public void setBorderPaint(Paint borderPaint) {
    this.borderPaint = borderPaint;
    top.setFill(borderPaint);
    bottom.setFill(borderPaint);
    right.setFill(borderPaint);
    left.setFill(borderPaint);
  }

  /**
   * Gets the {@link InsetsD}-thickness of the borders.
   */
  public InsetsD getThickness() {
    return thickness.get();
  }

  public ObjectProperty<InsetsD> thicknessProperty() {
    return thickness;
  }

  /**
   * Sets the {@link InsetsD}-thickness of the borders.
   */
  public void setThickness(InsetsD thickness) {
    this.thickness.set(thickness);
  }

  /**
   * Gets the total width of the BorderedGroup.
   */
  public double getWidth() {
    return width.get();
  }

  public DoubleProperty widthProperty() {
    return width;
  }

  /**
   * Sets the total width of the BorderedGroup.
   */
  public void setWidth(double width) {
    this.width.set(width);
  }

  /**
   * Gets the total height of the BorderedGroup.
   */
  public double getHeight() {
    return height.get();
  }

  public DoubleProperty heightProperty() {
    return height;
  }

  /**
   * Sets the total height of the BorderedGroup.
   */
  public void setHeight(double height) {
    this.height.set(height);
  }

  /**
   * Calculates the position and size of the background and border rectangles.
   */
  private void update() {
    double width = getWidth();
    double height = getHeight();
    InsetsD thickness = getThickness();

    background.setWidth(width);
    background.setHeight(height);
    double x = background.getX();
    double y = background.getY();

    top.setLayoutX(x);
    top.setLayoutY(y);
    top.setWidth(width);
    top.setHeight(thickness.getTop());

    bottom.setLayoutX(x);
    bottom.setLayoutY(y + height - thickness.getBottom());
    bottom.setWidth(width);
    bottom.setHeight(thickness.getBottom());

    left.setLayoutX(x);
    left.setLayoutY(y + thickness.getTop());
    left.setWidth(thickness.getLeft());
    left.setHeight(height - thickness.getTop() - thickness.getBottom());

    right.setLayoutX(x + width - thickness.getRight());
    right.setLayoutY(y + thickness.getTop());
    right.setWidth(thickness.getRight());
    right.setHeight(height - thickness.getTop() - thickness.getBottom());
  }
}
