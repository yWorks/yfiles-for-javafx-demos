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
package input.portcandidateprovider;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.styles.AbstractPortStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * A custom port style that is rendered as a circle and filled with the color stored in the port's tag.
 */
class ColorPortStyle extends AbstractPortStyle {

  private final int renderSize;
  private final double renderSizeHalf;

  ColorPortStyle() {
    this(6);
  }

  private ColorPortStyle(int renderSize) {
    this.renderSize = renderSize;
    this.renderSizeHalf = renderSize * 0.5;
  }

  @Override
  protected Node createVisual(IRenderContext context, IPort port) {
    Color color = port.getTag() instanceof Color ? (Color) port.getTag() : Color.WHITE;
    IPoint location = port.getLocation();
    Ellipse ellipse = new Ellipse(location.getX(), location.getY(), renderSizeHalf, renderSizeHalf);
    ellipse.setFill(color);
    Pen.getGray().styleShape(ellipse);
    return ellipse;
  }

  @Override
  protected Node updateVisual(IRenderContext renderContext, Node oldVisual, IPort port) {
    if (oldVisual instanceof Ellipse) {
      Ellipse ellipse = (Ellipse) oldVisual;
      IPoint location = port.getLocation();
      ellipse.setCenterX(location.getX());
      ellipse.setCenterY(location.getY());
      ellipse.setRadiusX(renderSizeHalf);
      ellipse.setRadiusY(renderSizeHalf);
      return ellipse;
    }
    return createVisual(renderContext, port);
  }

  @Override
  protected RectD getBounds(ICanvasContext canvasContext, IPort port) {
    IPoint location = port.getLocation();
    return new RectD(location.getX() - renderSizeHalf, location.getY() - renderSizeHalf, renderSize, renderSize);
  }

}
