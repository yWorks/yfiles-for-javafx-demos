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
package toolkit;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;

import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A simple node style for non-group nodes used by some of the demos.
 */
public class DemoNodeStyle extends AbstractNodeStyle {
  /**
   * The radius of the round corners in world coordinates.
   */
  public static final double CORNER_RADIUS = 2;

  /**
   * The default background color of the node.
   */
  public static final Color BACKGROUND = Color.ORANGE;

  /**
   * The default border pen of the node.
   */
  public static final Pen PEN = new Pen(Color.WHITE, 1);

  // region Property Pen

  private Pen pen;

  /**
   * Returns the border pen of the node.
   */
  public Pen getPen() {
    return pen;
  }

  /**
   * Sets the border pen of the node.
   */
  public void setPen(Pen pen) {
    this.pen = pen;
  }

  // endregion

  // region Property Background

  private Color background;

  /**
   * Returns the background color of the node.
   */
  public Color getBackground() {
    return background;
  }

  /**
   * Sets the background color of the node.
   */
  public void setBackground(Color background) {
    this.background = background;
  }

  // endregion

  // region Constructors

  /**
   * Creates a new instance using the default {@link #BACKGROUND background} and {@link #PEN border pen}.
   */
  public DemoNodeStyle() {
    this(BACKGROUND, PEN);
  }

  /**
   * Creates a new instance using the specified <code>background</code> and the default {@link #PEN border pen}.
   */
  public DemoNodeStyle(Color background) {
    this(background, PEN);
  }

  /**
   * Creates a new instance using the specified <code>background</code> and border <code>pen</code>.
   */
  public DemoNodeStyle(Color background, Pen pen) {
    this.pen = pen;
    this.background = background;
  }

  // endregion

  @Override
  protected Node createVisual( final IRenderContext context, final INode node ) {
    return new DemoVisualGroup(node.getLayout(), pen, background);
  }

  @Override
  protected Node updateVisual( IRenderContext context, Node oldVisual, INode node ) {
    if (oldVisual instanceof DemoVisualGroup) {
      // reuse and update old visual
      ((DemoVisualGroup) oldVisual).update(node.getLayout(), pen, background);
      return oldVisual;
    } else {
      // create a new visual
      return createVisual(context, node);
    }
  }

  /**
   * Stores the geometry of the visualization (in addition to child nodes)
   * to speed-up updates of the visualization in
   * {@link DemoNodeStyle#updateVisual(IRenderContext, Node, INode)}
   * if the node geometry has not changed.
   */
  private static final class DemoVisualGroup extends VisualGroup {
    RectD layout;

    DemoVisualGroup( IRectangle layout, Pen pen, Color background ) {
      this.layout = RectD.EMPTY;

      Rectangle shape = new Rectangle(0, 0, 1, 1);
      add(shape);

      update(layout, pen, background);
    }

    void update( IRectangle newLayout, Pen pen, Color background ) {
      Rectangle shape = (Rectangle) getNullableChildren().get(0);
      pen.styleShape(shape);
      shape.setFill(background);

      final double newX = newLayout.getX();
      final double newY = newLayout.getY();
      double newW = newLayout.getWidth();
      double newH = newLayout.getHeight();

      boolean newPos = layout.x != newX || layout.y != newY;
      boolean newSize = layout.width != newW || layout.height != newH;
      if (newPos || newSize) {
        if (newSize) {
          setGeometry((Rectangle) getChildren().get(0), newW, newH);
        }

        if (newPos) {
          setLayoutX(newX);
          setLayoutY(newY);
        }

        layout = new RectD(newX, newY, newW, newH);
      }
    }

    private static void setGeometry( Rectangle r, double width, double height ) {
      double arc = Math.min(CORNER_RADIUS, Math.min(width, height) * 0.5);
      r.setWidth(width);
      r.setHeight(height);
      r.setArcWidth(arc);
      r.setArcHeight(arc);
    }
  }
}