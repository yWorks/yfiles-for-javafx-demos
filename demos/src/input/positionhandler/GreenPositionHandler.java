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
package input.positionhandler;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.input.ConstrainedPositionHandler;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;

/**
 * A position handler that constrains the movement of a node to one axis (for each gesture) and delegates
 * for other aspects to another (the original) handler.
 * <p>
 * Note that the simpler solution for this use case is subclassing {@link ConstrainedPositionHandler},
 * however the interface is completely implemented for illustration, here.
 * </p>
 */
class GreenPositionHandler implements IPositionHandler {
  private final IPositionHandler delegateHandler;
  private PointD lastLocation;

  GreenPositionHandler(IPositionHandler delegateHandler) {
    this.delegateHandler = delegateHandler;
  }

  @Override
  public IPoint getLocation() {
    return delegateHandler.getLocation();
  }

  /**
   * Stores the initial location of the movement for reference, and calls the base method.
   */
  @Override
  public void initializeDrag(IInputModeContext inputModeContext) {
    delegateHandler.initializeDrag(inputModeContext);
    lastLocation = new PointD(delegateHandler.getLocation());
  }

  /**
   * Constrains the movement to one axis. This is done by calculating the
   * constrained location for the given new location, and invoking the
   * original handler with the constrained location.
   */
  @Override
  public void handleMove(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
    // The larger difference in coordinates specifies whether this is
    // a horizontal or vertical movement.
    PointD delta = PointD.subtract(newLocation, originalLocation);

    if (Math.abs(delta.getX()) > Math.abs(delta.getY())) {
      newLocation = new PointD(newLocation.getX(), originalLocation.getY());
    } else {
      newLocation = new PointD(originalLocation.getX(), newLocation.getY());
    }
    if (!newLocation.equals(lastLocation)) {
      delegateHandler.handleMove(inputModeContext, originalLocation, newLocation);
      lastLocation = newLocation;
    }
  }

  @Override
  public void cancelDrag(IInputModeContext inputModeContext, PointD originalLocation) {
    delegateHandler.cancelDrag(inputModeContext, originalLocation);
  }

  @Override
  public void dragFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
    delegateHandler.dragFinished(inputModeContext, originalLocation, newLocation);
  }
}
