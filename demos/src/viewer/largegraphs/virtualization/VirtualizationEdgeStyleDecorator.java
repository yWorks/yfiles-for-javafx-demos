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
package viewer.largegraphs.virtualization;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.Tangent;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.input.IInputModeContext;
import javafx.scene.Node;


/**
 * Style decorator that wraps an edge style and turns off virtualization below a configurable zoom level.
 * <p>
 * This implementation delegates everything to the wrapped style except the visibility test, which always returns {@code
 * true} below the configured zoom level.
 * </p>
 */
public class VirtualizationEdgeStyleDecorator extends AbstractEdgeStyle {

  /**
   * Initializes a new instance of the {@link VirtualizationEdgeStyleDecorator} class with the given virtualization
   * threshold and wrapped style.
   * @param threshold The zoom level below which virtualization is turned off.
   * @param style The wrapped style.
   */
  public VirtualizationEdgeStyleDecorator(double threshold, IEdgeStyle style) {
    this.style = style;
    this.threshold = threshold;
  }

  private IEdgeStyle style;

  /**
   * Sets the wrapped style.
   */
  public void setStyle(IEdgeStyle style) {
    this.style = style;
  }

  /**
   * Gets the wrapped style.
   */
  public IEdgeStyle getStyle() {
    return style;
  }

  private double threshold;

  /**
   * Gets the zoom level below which virtualization should be turned off.
   */
  public double getThreshold() {
    return threshold;
  }

  /**
   * Sets the zoom level below which virtualization should be turned off.
   */
  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  /**
   * Determines whether the visualization for the specified edge is visible in the context.
   * <p>
   * This method is called in response to a {@link IVisibilityTestable#isVisible} call to the instance that has been
   * queried from the {@link #getRenderer()}.
   * </p>
   * <p>
   * This implementation returns {@code true} if the zoom level is below {@link #getThreshold()} and otherwise delegates
   * to the wrapped style.
   * </p>
   * @param context   The canvas context.
   * @param rectangle The clipping rectangle.
   * @param edge      The edge to which this style instance is assigned.
   * @return {@code true} if either the zoom level is below {@link #getThreshold()} or the edge is visible in the
   * clipping rectangle; {@code false} otherwise.
   */
  protected boolean isVisible(ICanvasContext context, RectD rectangle, IEdge edge) {
    if (context.getZoom() < threshold) {
      // Returning true here causes virtualization to be turned off. That is, all elements are always in the visual tree.
      return true;
    }
    return style.getRenderer().getVisibilityTestable(edge, style).isVisible(context, rectangle);
  }


  @Override
  protected Node createVisual(IRenderContext context, IEdge edge) {
    return style.getRenderer().getVisualCreator(edge, style).createVisual(context);
  }

  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, IEdge edge) {
    return style.getRenderer().getVisualCreator(edge, style).updateVisual(context, oldVisual);
  }

  @Override
  protected RectD getBounds(ICanvasContext context, IEdge edge) {
    return style.getRenderer().getBoundsProvider(edge, style).getBounds(context);
  }

  @Override
  protected boolean isHit(IInputModeContext context, PointD location, IEdge edge) {
    return style.getRenderer().getHitTestable(edge, style).isHit(context, location);
  }

  @Override
  protected boolean isInBox(IInputModeContext context, RectD rectangle, IEdge edge) {
    return style.getRenderer().getMarqueeTestable(edge, style).isInBox(context, rectangle);
  }

  @Override
  protected Tangent getTangent(IEdge edge, double ratio) {
    return style.getRenderer().getPathGeometry(edge, style).getTangent(ratio);
  }

  @Override
  protected Tangent getTangent(IEdge edge, int segmentIndex, double ratio) {
    return style.getRenderer().getPathGeometry(edge, style).getTangent(ratio);
  }

  @Override
  protected GeneralPath getPath(IEdge edge) {
    return style.getRenderer().getPathGeometry(edge, style).getPath();
  }

  @Override
  protected int getSegmentCount(IEdge edge) {
    return style.getRenderer().getPathGeometry(edge, style).getSegmentCount();
  }

  // endregion

}
