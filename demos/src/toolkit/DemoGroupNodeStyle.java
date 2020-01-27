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
package toolkit;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A simple node style for group nodes used by some of the demos.
 */
public class DemoGroupNodeStyle extends AbstractNodeStyle {
  private static final int BORDER_THICKNESS = 4;
  private static final int HEADER_THICKNESS = 22;
  private static final int INSET = 4;

  private static final Color BORDER_COLOR = Color.rgb(104, 176, 227);
  private static final Pen OUTER_BORDER_PEN = new Pen(Color.WHITE, 1);

  private static final InsetsD INSETS = new InsetsD(
          HEADER_THICKNESS + INSET,
          BORDER_THICKNESS + INSET,
          BORDER_THICKNESS + INSET,
          BORDER_THICKNESS + INSET);

  // region Property BackgroundColor

  private Color backgroundColor;

  /**
   * Returns the background color of the node.
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Sets the background color of the node.
   */
  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  // endregion

  // region Property BorderColor

  private Color borderColor;

  /**
   * Returns the border color of the node.
   */
  public Color getBorderColor() {
    return borderColor;
  }

  /**
   * Sets the border color of the node.
   */
  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  // endregion

  // region Property OuterBorderPen

  private Pen outerBorderPen;

  /**
   * Returns the pen used for the outer border of the node.
   */
  public Pen getOuterBorderPen() {
    return outerBorderPen;
  }

  /**
   * Sets the pen used for the outer border of the node.
   */
  public void setOuterBorderPen(Pen outerBorderPen) {
    this.outerBorderPen = outerBorderPen;
  }

  // endregion

  public DemoGroupNodeStyle() {
    this.borderColor = BORDER_COLOR;
    this.backgroundColor = Color.WHITE;
    this.outerBorderPen = OUTER_BORDER_PEN;
  }

  @Override
  protected Node createVisual( IRenderContext context, INode node ) {
    IRectangle layout = node.getLayout();
    DemoGroupNodeStyleVisual group = new DemoGroupNodeStyleVisual(layout.toSizeD());
    group.setLayoutX(layout.getX());
    group.setLayoutY(layout.getY());

    // outerWidth
    double ow = layout.getWidth();
    // outerHeight
    double oh = layout.getHeight();
    Rectangle outerRect = new Rectangle(0, 0, ow, oh);
    outerBorderPen.styleShape(outerRect);
    outerRect.setFill(borderColor);
    group.add(outerRect);

    // innerWidth
    double iw = ow - 2 * BORDER_THICKNESS;
    // innerHeight
    double ih = oh - HEADER_THICKNESS - BORDER_THICKNESS;
    if (iw > 0 && ih > 0) {
      Rectangle innerRect = new Rectangle(BORDER_THICKNESS, HEADER_THICKNESS, iw, ih);
      innerRect.setFill(backgroundColor);
      group.add(innerRect);
    }

    return group;
  }

  @Override
  protected Node updateVisual( IRenderContext context, Node oldVisual, INode node ) {
    if (!(oldVisual instanceof DemoGroupNodeStyleVisual)) {
      // the oldVisual is not an instance created by this style
      // create a new, appropriate visual
      return createVisual(context, node);
    }

    DemoGroupNodeStyleVisual group = (DemoGroupNodeStyleVisual) oldVisual;
    IRectangle layout = node.getLayout();
    if (!SizeD.equals(layout.toSizeD(), group.getSize())) {
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

  @Override
  protected Object lookup( final INode node, final Class type ) {
    if (type == INodeInsetsProvider.class) {
      return (INodeInsetsProvider) (node2) -> INSETS;
    }
    return super.lookup(node, type);
  }


  /**
   * Stores the size of the visualization (in addition to child nodes) 
   * to speed-up updates of the visualization in
   * {@link DemoGroupNodeStyle#updateVisual(IRenderContext, Node, INode)}
   * if the node size has not changed.
   */
  private static final class DemoGroupNodeStyleVisual extends VisualGroup {
    final SizeD size;

    DemoGroupNodeStyleVisual( SizeD size ) {
      this.size = size;
    }

    SizeD getSize() {
      return size;
    }
  }
}
