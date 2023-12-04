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
package analysis.networkflows;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * A {@link IVisualCreator} to visualize the minimum cut line of a max flow / min cut diagram.
 */
class MinCutLineVisualCreator implements IVisualCreator {
  private RectD bounds;
  private boolean visible;

  MinCutLineVisualCreator() {
    this.bounds = RectD.EMPTY;
    this.visible = false;
  }

  /**
   * Creates a visual that displays the min-cut line.
   */
  @Override
  public Node createVisual(IRenderContext context) {
    MinCutLineVisual visual = new MinCutLineVisual();
    visual.update(bounds, visible);
    return visual;
  }


  @Override
  public Node updateVisual(IRenderContext context, Node oldVisual) {
    if (oldVisual instanceof MinCutLineVisual) {
      MinCutLineVisual visual = (MinCutLineVisual) oldVisual;
      visual.update(bounds, visible);
      return visual;
    } else {
      return createVisual(context);
    }
  }

  /**
   * Gets the bounds of the min-cut line.
   */
  RectD getBounds() {
    return bounds;
  }

  /**
   * Sets the bounds of the min-cut line.
   */
  void setBounds(RectD bounds) {
    this.bounds = bounds;
  }

  /**
   * Gets whether the min-cut line is visible.
   */
  boolean isVisible() {
    return visible;
  }

  /**
   * Sets whether the min-cut line is visible.
   */
  void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Custom {@link IVisual} implementation used to display the MinCutLine
   */
  private static class MinCutLineVisual extends VisualGroup {
    private static final String TEXT = "MIN CUT";

    private final Rectangle line;
    private final Text text;
    private boolean visible;

    MinCutLineVisual() {
      line = new Rectangle();
      line.setFill(Color.GOLD);
      this.add(line);
      text = new Text(TEXT);
      text.setFill(Color.DARKORANGE);
      text.setRotate(90);
      this.add(text);
    }

    void update(final RectD bounds, final boolean visible) {
      if (bounds == null) {
        throw new IllegalArgumentException("Bounds may not be null.");
      }

      if (bounds.getWidth() > 0 && bounds.getHeight() > 0) {
        this.visible = visible;
        double r = Math.min(bounds.getWidth(), bounds.getHeight());
        this.line.setX(bounds.getX());
        this.line.setY(bounds.getY());
        this.line.setWidth(bounds.getWidth());
        this.line.setHeight(bounds.getHeight());
        this.line.setArcWidth(r);
        this.line.setArcHeight(r);

        this.text.relocate(bounds.getCenterX() - this.text.getLayoutBounds().getWidth()/2 + 1, bounds.getCenterY());
        this.setVisible(visible);
      } else {
        this.setVisible(false);
      }
    }
  }
}
