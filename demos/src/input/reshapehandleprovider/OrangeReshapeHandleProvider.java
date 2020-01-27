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
package input.reshapehandleprovider;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.input.ConstrainedHandle;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;
import javafx.scene.shape.Rectangle;

/**
 * Implementation of {@link IReshapeHandleProvider} that constrains the resize to be within an enclosing rectangle and
 * delegates to another (the original) handler.
 */
class OrangeReshapeHandleProvider implements IReshapeHandleProvider {
  private final Rectangle boundaryRectangle;
  private final IReshapeHandleProvider delegateProvider;

  OrangeReshapeHandleProvider(Rectangle boundaryRectangle, IReshapeHandleProvider delegateProvider) {
    this.boundaryRectangle = boundaryRectangle;
    this.delegateProvider = delegateProvider;
  }

  /**
   * Returns the available handles of the delegate provider.
   */
  @Override
  public HandlePositions getAvailableHandles(IInputModeContext inputModeContext) {
    return delegateProvider.getAvailableHandles(inputModeContext);
  }

  /**
   * Returns a handle for the given original position that is limited to
   * the bounds of the boundary rectangle of this class.
   */
  @Override
  public IHandle getHandle(IInputModeContext inputModeContext, HandlePositions position) {
    // return handle that is constrained by a box
    IHandle handle = delegateProvider.getHandle(inputModeContext, position);
    return new BoxConstrainedHandle(handle, boundaryRectangle);
  }

  /**
   * A {@link ConstrainedHandle} that is limited to the interior of a given rectangle.
   */
  private static class BoxConstrainedHandle extends ConstrainedHandle {
    private final Rectangle boundaryRectangle;
    private RectD constraintRect;

    BoxConstrainedHandle(IHandle handle, Rectangle boundaryRectangle) {
      super(handle);
      this.boundaryRectangle = boundaryRectangle;
    }

    /**
     * Makes sure that the constraintRect is set to the current boundary rectangle
     * and delegates to the base implementation.
     */
    @Override
    protected void onInitialized(IInputModeContext inputModeContext, PointD originalLocation) {
      super.onInitialized(inputModeContext, originalLocation);
      constraintRect = new RectD(boundaryRectangle.getX(), boundaryRectangle.getY(), boundaryRectangle.getWidth(), boundaryRectangle.getHeight());
    }

    /**
     * Returns for the given new location the constrained location that is inside the boundary rectangle.
     */
    @Override
    protected PointD constrainNewLocation(IInputModeContext context, PointD originalLocation, PointD newLocation) {
      // return location constrained by rectangle
      return newLocation.getConstrained(constraintRect);
    }
  }
}
