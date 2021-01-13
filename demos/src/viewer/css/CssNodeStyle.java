/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * A node style for non-group nodes that can be styled with CSS.
 * <p>
 * You can change the visualization of the node shape by defining properties for the style class {@code node-syle}.
 * As the node visualization is essentially a JavaFX
 * <a href="http://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#rectangle">Rectangle</a>,
 * the {@code node-syle} style class supports rectangle properties.
 * </p>
 */
public class CssNodeStyle extends AbstractNodeStyle {
  /**
   * The radius of the round corners in world coordinates.
   */
  public static final double CORNER_RADIUS = 2;

  private String[] styleClasses;

  /**
   * Initializes a new {@code CssNodeStyle} instance.
   */
  public CssNodeStyle() {
  }

  /**
   * Initializes a new {@code CssNodeStyle} instance using the given style classes.
   */
  public CssNodeStyle(String... styleClasses) {
    this.styleClasses = styleClasses;
  }

  /**
   * Returns the style classes. using the given style classes.
   */
  public String[] getStyleClasses() {
    return styleClasses;
  }

  /**
   * Sets the style classes.
   */
  public void setStyleClasses(String... styleClasses) {
    this.styleClasses = styleClasses;
  }

  /**
   * Creates the JavaFX node used for visualizing normal nodes.
   */
  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    RectD layout = node.getLayout().toRectD();
    Rectangle rect = new Rectangle();
    updateGeometry(rect, layout);
    if (styleClasses != null) {
      rect.getStyleClass().addAll(styleClasses);
    }
    rect.setUserData(layout);
    return rect;
  }

  /**
   * Updates the JavaFX node used for visualizing normal nodes.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, INode node) {
    if (oldVisual instanceof Rectangle) {
      Rectangle rect = (Rectangle) oldVisual;
      RectD layout = node.getLayout().toRectD();
      if (!rect.getUserData().equals(layout)) {
        updateGeometry(rect, layout);
        rect.setUserData(layout);
      }
      return oldVisual;
    } else {
      // create a new visual
      return createVisual(context, node);
    }
  }

  /**
   * Updates the geometry of the given JavaFX {@link Rectangle} to reflect the
   * geometry of the given node bounds.
   */
  private static void updateGeometry(Rectangle r, IRectangle layout) {
    double arc = Math.min(CORNER_RADIUS, Math.min(layout.getWidth(), layout.getHeight()) * 0.5);
    r.setX(layout.getX());
    r.setY(layout.getY());
    r.setWidth(layout.getWidth());
    r.setHeight(layout.getHeight());
    r.setArcWidth(arc);
    r.setArcHeight(arc);
  }
}
