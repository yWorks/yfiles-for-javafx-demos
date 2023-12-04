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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ViewportLimiter;
import com.yworks.yfiles.view.ViewportLimitingPolicy;

/**
 * Prevents scrolling above or below the vertical bounds of the task lanes.
 */
public class RestrictedViewportLimiter extends ViewportLimiter {
  private final GraphControl taskControl;

  /**
   * Initializes a new {@code RestrictedViewportLimiter} instances.
   */
  public RestrictedViewportLimiter( GraphControl taskControl ) {
    this.taskControl = taskControl;
  }

  /**
   * Limits the viewport to the area which contains task nodes.
   * @param canvas The canvas control on which the viewport should be applied.
   * @param suggestedViewport The suggested viewport.
   * @param forceStrictLimits Whether to force {@link ViewportLimitingPolicy#STRICT} limiting. Passing {@code true} overrides the
   * {@link #getLimitingPolicy() LimitingPolicy}. This is used for example by {@link CanvasComponent#fitContent(boolean)}
   * or ZoomToCurrentItemCommand where larger viewport changes are expected.
   */
  @Override
  public RectD limitViewport( CanvasControl canvas, RectD suggestedViewport, boolean forceStrictLimits ) {
    double topY = taskControl.getContentRect().getY();
    double bottomY = taskControl.getContentRect().getMaxY();

    double oldY = suggestedViewport.getY();
    double newY = Math.max(topY, Math.min(bottomY - suggestedViewport.getHeight(), oldY));

    return new RectD(suggestedViewport.getX(), newY, suggestedViewport.getWidth(), suggestedViewport.getHeight());
  }
}
