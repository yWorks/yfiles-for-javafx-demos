/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package input.positionhandler;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.ConstrainedPositionHandler;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;
import javafx.scene.shape.Rectangle;

/**
 * A {@link ConstrainedPositionHandler} that limits the movement of a
 * node to be within an rectangle and delegates for other aspects to another (the original) handler.
 */
class OrangePositionHandler extends ConstrainedPositionHandler {
  private final Rectangle boundaryRectangle;
  private final INode node;
  private RectD boundaryPositionRectangle;

  OrangePositionHandler(Rectangle boundaryRectangle, INode node, IPositionHandler delegateHandler) {
    super(delegateHandler);
    this.boundaryRectangle = boundaryRectangle;
    this.node = node;
  }

  /**
   * Prepares the rectangle that is actually used to limit the node
   * position, besides the base functionality. Since a position handler
   * works on points, the actual rectangle must be a limit for the upper
   * left corner of the node and not for the node's bounding box.
   */
  protected void onInitialized(IInputModeContext inputModeContext, PointD originalLocation) {
    super.onInitialized(inputModeContext, originalLocation);
    // Shrink the rectangle by the node size to get the limits for the upper left node corner
    boundaryPositionRectangle = new RectD(
        boundaryRectangle.getX(),
        boundaryRectangle.getY(),
        boundaryRectangle.getWidth() - node.getLayout().getWidth(),
        boundaryRectangle.getHeight() - node.getLayout().getHeight());
  }

  /**
   * Returns the position that is constrained by the rectangle.
   */
  protected PointD constrainNewLocation(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    return newLocation.getConstrained(boundaryPositionRectangle);
  }
}
