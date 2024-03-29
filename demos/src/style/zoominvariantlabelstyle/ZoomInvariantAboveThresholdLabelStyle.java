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
package style.zoominvariantlabelstyle;

import com.yworks.yfiles.graph.ILabel;

/**
 * Stops the label from getting larger in view coordinates if the zoom is
 * greater than the style's maximum zoom value.
 */
public class ZoomInvariantAboveThresholdLabelStyle extends AbstractZoomInvariantLabelStyle {
  private double maxZoom;

  /**
   * Initializes a new {@code ZoomInvariantAboveThresholdLabelStyle} instance
   * with {@code maxZoom} set to {@code 1.0}.
   */
  public ZoomInvariantAboveThresholdLabelStyle() {
    maxZoom = 1;
  }

  /**
   * Stops the label from getting larger in view coordinates if the zoom is
   * greater than the style's maximum zoom value.
   * @param label the current label which will be styled
   * @param zoom the current zoom factor
   */
  @Override
  protected double getScaleForZoom( ILabel label, double zoom ) {
    double max = getMaxZoom();
    if (zoom < max) {
      return 1;
    }
    return max / zoom;
  }

  /**
   * Gets the maximum zoom value for this style.
   * This style will scale labels only up to this zoom value.
   */
  public double getMaxZoom() {
    return maxZoom;
  }

  /**
   * Sets the maximum zoom value for this style.
   * This style will scale labels only up to this zoom value.
   */
  public void setMaxZoom( double value ) {
    this.maxZoom = value;
  }
}
