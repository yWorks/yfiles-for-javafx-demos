/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * A node style for group nodes that can be styled with CSS.
 * <p>
 * You can change the visualization of the group node shape by defining properties for the style classes
 * {@code group-node-style.inner} and {@code group-node-style.outer}.
 * As the inner and outer rectangles are JavaFX
 * <a href="http://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#rectangle">Rectangles</a>,
 * the {@code group-node-style.inner} and {@code group-node-style.outer} style classes support rectangle properties.
 * </p>
 */
public class CssGroupNodeStyle extends AbstractNodeStyle {

  private static final int BORDER_THICKNESS = 4;
  private static final int HEADER_THICKNESS = 22;
  private static final int INSET = 4;
  private static final InsetsD INSETS = new InsetsD(
      HEADER_THICKNESS + INSET,
      BORDER_THICKNESS + INSET,
      BORDER_THICKNESS + INSET,
      BORDER_THICKNESS + INSET);

  private String[] styleClasses;

  /**
   * Initializes a new {@code CssGroupNodeStyle} instance.
   */
  public CssGroupNodeStyle() {
    styleClasses = new String[0];
  }

  /**
   * Initializes a new {@code CssGroupNodeStyle} instance using the given style classes.
   */
  public CssGroupNodeStyle(String... styleClasses) {
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
  public void setStyleClasses(String... styleClasses) {
    this.styleClasses = styleClasses;
  }

  /**
   * Creates the JavaFX node used for visualizing group nodes.
   */
  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    IRectangle layout = node.getLayout();
    VisualGroup group = new VisualGroup();
    group.setLayoutX(layout.getX());
    group.setLayoutY(layout.getY());

    // outerWidth
    double ow = layout.getWidth();
    // outerHeight
    double oh = layout.getHeight();
    Rectangle outerRect = new Rectangle(0, 0, ow, oh);
    outerRect.getStyleClass().add("outer");
    if (styleClasses != null) {
      outerRect.getStyleClass().addAll(styleClasses);
    }
    group.add(outerRect);

    // innerWidth
    double iw = ow - 2 * BORDER_THICKNESS;
    // innerHeight
    double ih = oh - HEADER_THICKNESS - BORDER_THICKNESS;
    if (iw > 0 && ih > 0) {
      Rectangle innerRect = new Rectangle(BORDER_THICKNESS, HEADER_THICKNESS, iw, ih);
      innerRect.getStyleClass().add("inner");
      if (styleClasses != null) {
        innerRect.getStyleClass().addAll(styleClasses);
      }
      group.add(innerRect);
    }

    group.setUserData(layout.toSizeD());
    return group;
  }

  /**
   * Updates the JavaFX node used for visualizing group nodes.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, INode node) {
    if (!(oldVisual instanceof VisualGroup)) {
      // the oldVisual is not an instance created by this style
      // create a new, appropriate visual
      return createVisual(context, node);
    }

    VisualGroup group = (VisualGroup) oldVisual;
    SizeD oldSize = (SizeD) group.getUserData();
    IRectangle layout = node.getLayout();
    if (!SizeD.equals(layout.toSizeD(), oldSize)) {
      // the node size has changed
      // create a new, appropriate visual
      return createVisual(context, node);
    }

    // the node size has not changed
    // keep the old visual but move it to the node's current position
    group.setLayoutX(layout.getX());
    group.setLayoutY(layout.getY());
    return group;
  }

  /**
   * Returns the implementation of the given type that is associated to the
   * given node or {@code null} if no implementation of the given type is
   * associated to the given node.
   */
  @Override
  protected Object lookup(INode node, Class type) {
    if (type == INodeInsetsProvider.class) {
      return (INodeInsetsProvider) (node2) -> INSETS;
    }
    return super.lookup(node, type);
  }
}
