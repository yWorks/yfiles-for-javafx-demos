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
package tutorial02_CustomStyles.step23_CustomPorts;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.styles.AbstractPortStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/////////////// This class is new in this sample ///////////////

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.IPortStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractPortStyle} as its base class. The port is rendered as a circle.
 */
public class MySimplePortStyle extends AbstractPortStyle {
  // pen to paint the border of the port with
  private static final Pen DEFAULT_PEN = new Pen(Color.rgb(255, 255, 255, 0.3));

  // the size of the port shape
  private static final double WIDTH = 4.0;
  private static final double HEIGHT = 4.0;

  /**
   * Creates the visual for a port.
   */
  @Override
  protected Node createVisual(IRenderContext context, IPort port) {
    PortVisual visual = new PortVisual();
    visual.update(port.getLocation());
    return visual;
  }

  /**
   * Re-renders the port using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, IPort)} is called instead.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, IPort port) {
    PortVisual visual = (PortVisual) oldVisual;
    visual.update(port.getLocation());
    return visual;
  }

  /**
   * Calculates the bounds of this port.
   * These are also used for arranging the visual, hit testing, visibility testing, and marquee box tests.
   */
  @Override
  protected RectD getBounds(ICanvasContext context, IPort port) {
    return RectD.fromCenter(port.getLocation(), new SizeD(WIDTH, HEIGHT));
  }

  /**
   * A {@link VisualGroup} that paints a port as a circle.
   */
  private static class PortVisual extends VisualGroup {
    // the shape of the port
    private Ellipse shape;

    PortVisual() {
      shape = new Ellipse(WIDTH * 0.5, HEIGHT * 0.5);
      shape.setFill(Color.TRANSPARENT);
      DEFAULT_PEN.styleShape(shape);
      this.add(shape);
    }

    /**
     * Updates the location of the shape used to paint the port.
     * @param location the location of the port
     */
    public void update(IPoint location) {
      shape.setCenterX(location.getX());
      shape.setCenterY(location.getY());
    }
  }
}
