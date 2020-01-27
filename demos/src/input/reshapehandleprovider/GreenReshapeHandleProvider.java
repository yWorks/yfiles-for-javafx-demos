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

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.ConstrainedHandle;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;
import javafx.scene.Cursor;

import java.util.Objects;

/**
 * An {@link IReshapeHandleProvider} that restricts the available
 * handles provided by the delegate provider to the ones in the four corners.
 * If the delegate provider doesn't provide all of these handles, this
 * handler doesn't do this as well. In addition, these handles have a
 * custom behavior: they maintain the current aspect ratio of the node.
 */
class GreenReshapeHandleProvider implements IReshapeHandleProvider {
  private final IReshapeHandleProvider delegateProvider;
  private final INode node;

  GreenReshapeHandleProvider(IReshapeHandleProvider delegateProvider, INode node) {
    this.delegateProvider = delegateProvider;
    this.node = node;
  }

  /**
   * Returns the available handles provided by the delegate provider
   * restricted to the ones in the four corners.
   */
   @Override
  public HandlePositions getAvailableHandles(IInputModeContext inputModeContext) {
    // return only corner handles
    return delegateProvider.getAvailableHandles(inputModeContext).and(
        HandlePositions.NORTH_EAST.or(HandlePositions.NORTH_WEST.or(HandlePositions.SOUTH_EAST.or(HandlePositions.SOUTH_WEST))));
  }

  /**
   * Returns a custom handle to maintains the aspect ratio of the node.
   */
   @Override
  public IHandle getHandle(IInputModeContext inputModeContext, HandlePositions position) {
    return new AspectRatioHandle(delegateProvider.getHandle(inputModeContext, position), position, node.getLayout());
  }

  /**
   * Note that for this use case it would have been dramatically simpler to subclass {@link ConstrainedHandle},
   * however the interface is completely implemented for demonstration purposes, here.
   */
  private static class AspectRatioHandle implements IHandle {
    private static final int MIN_SIZE = 5;
    private final IHandle handle;
    private final HandlePositions position;
    private final IRectangle layout;
    private PointD lastLocation;
    private double ratio;
    private SizeD originalSize;

    AspectRatioHandle(IHandle handle, HandlePositions position, IRectangle layout) {
      this.handle = handle;
      this.position = position;
      this.layout = layout;
    }

    @Override
    public HandleTypes getType() {
      return handle.getType();
    }

    @Override
    public Cursor getCursor() {
      return handle.getCursor();
    }

    @Override
    public IPoint getLocation() {
      return handle.getLocation();
    }

    /**
     * Stores the initial location and aspect ratio for reference, and calls the base method.
     */
    @Override
    public void initializeDrag(IInputModeContext inputModeContext) {
      handle.initializeDrag(inputModeContext);
      lastLocation = new PointD(handle.getLocation());
      originalSize = new SizeD(layout.toSizeD());
      if (Objects.equals(position, HandlePositions.NORTH_WEST) ||
          Objects.equals(position, HandlePositions.SOUTH_EAST)) {
        ratio = layout.getWidth() / layout.getHeight();
      } else if (Objects.equals(position, HandlePositions.NORTH_EAST) ||
          Objects.equals(position, HandlePositions.SOUTH_WEST)) {
        ratio = -layout.getWidth() / layout.getHeight();
      } else {
        throw new IllegalStateException("The HandlePositions " + position + " is not supported.");
      }
    }

    /**
     * Constrains the movement to maintain the aspect ratio. This is done
     * by calculating the constrained location for the given new location,
     * and invoking the original handler with the constrained location.
     */
    @Override
    public void handleMove(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      // For the given new location, the larger node side specifies the actual size change.
      PointD deltaDrag = PointD.subtract(newLocation, originalLocation);

      // manipulate the drag so that it respects the boundaries and enforces the ratio.
      if (Math.abs(ratio) > 1) {
        // the width is larger, so we take the north or south position as indicator for the dragY calculation
        // if the south handles are dragged, we have to add the value of the dragDeltaY to the original height
        // otherwise, we have to subtract the value.
        // calculate the dragX in respect to the dragY

        // the sign basically indicates from which side we are dragging and thus if
        // we have to add or subtract the drag delta y to the height
        double sign = (Objects.equals(position, HandlePositions.SOUTH_EAST) || Objects.equals(position,
            HandlePositions.SOUTH_WEST)) ? 1 : -1;
        double newHeight = originalSize.getHeight() + sign * (deltaDrag.getX() / ratio);

        if (newHeight > MIN_SIZE) {
          // if the new height is larger then the minimum size, set the deltaDragY to the deltaDragX with respect to the ratio.
          deltaDrag = new PointD(deltaDrag.getX(), deltaDrag.getX() / ratio);
        } else {
          // if the new height would fall below the minimum size, adjust the dragY so that the minimum size is satisfied and
          // then set the deltaDragX according to that value.
          double newDragY = Math.signum(deltaDrag.getX() / ratio) * (originalSize.getHeight() - MIN_SIZE);
          deltaDrag = new PointD(newDragY * ratio, newDragY);
        }
      } else {
        // the height is larger, so we take the west or east position as indicator for the dragX calculation
        // if the west handles are dragged, we have to add the value of the dragDeltaX to the original width
        // otherwise, we have to subtract the value.
        // calculate the dragY in respect to the dragX

        // the sign basically indicates from which side we are dragging and thus if
        // we have to add or subtract the drag delta x to the width
        double sign = (Objects.equals(position, HandlePositions.NORTH_EAST) || Objects.equals(position,
            HandlePositions.SOUTH_EAST)) ? 1 : -1;
        double newWidth = originalSize.getWidth() + sign * (deltaDrag.getY() * ratio);
        if (newWidth > MIN_SIZE) {
          // if the new width is larger then the minimum size, set the deltaDragX to the deltaDragY with respect to the ratio.
          deltaDrag = new PointD(deltaDrag.getY() * ratio, deltaDrag.getY());
        } else {
          // if the new width would fall below the minimum size, adjust the dragX so that the minimum size is satisfied and
          // then set the deltaDragY according to that value.
          double newDragX = Math.signum(deltaDrag.getY() * ratio) * (originalSize.getWidth() - MIN_SIZE);
          deltaDrag = new PointD(newDragX, newDragX / ratio);
        }
      }

      newLocation = PointD.add(originalLocation, deltaDrag);

      if (newLocation != lastLocation) {
        handle.handleMove(inputModeContext, originalLocation, newLocation);
        lastLocation = newLocation;
      }
    }

    @Override
    public void cancelDrag(IInputModeContext inputModeContext, PointD originalLocation) {
      handle.cancelDrag(inputModeContext, originalLocation);
    }

    @Override
    public void dragFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      handle.dragFinished(inputModeContext, originalLocation, newLocation);
    }
  }
}
