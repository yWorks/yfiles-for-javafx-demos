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
package viewer.backgroundimage;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Creates JavaFX nodes that visualize the content rectangle of a
 * {@link com.yworks.yfiles.view.CanvasControl}.
 */
public class RectangleVisualCreator implements IVisualCreator {
  /**
   * Creates the JavaFX node for the background.
   * @param context The context that describes where the JavaFX node will be used.
   */
  @Override
  public Node createVisual( IRenderContext context ) {
    return newRectangle(getBackgroundRectangle(context));
  }

  /**
   * Updates the JavaFX node for the background.
   * @param context The context that describes where the JavaFX node will be used.
   * @param oldNode The old JavaFX node.
   */
  @Override
  public Node updateVisual( IRenderContext context, Node oldNode ) {
    RectD area = getBackgroundRectangle(context);

    if (oldNode instanceof Rectangle) {
      Rectangle r = (Rectangle) oldNode;
      r.setX(area.getX());
      r.setY(area.getY());
      r.setWidth(area.getWidth());
      r.setHeight(area.getHeight());

      return oldNode;
    } else {
      return newRectangle(area);
    }
  }

  /**
   * Creates a JavaFX node for the given rectangular area.
   */
  private static Rectangle newRectangle( RectD area ) {
    Rectangle rect = new Rectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
    rect.setFill(Color.rgb(102, 153, 204));
    return rect;
  }

  /**
   * Returns the area which contains all elements in the graph.
   */
  private static RectD getBackgroundRectangle( IRenderContext context ) {
    return context.getCanvasControl().getContentRect().getEnlarged(new InsetsD(20));
  }
}
