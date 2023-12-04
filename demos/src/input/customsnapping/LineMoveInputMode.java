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
package input.customsnapping;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;
import com.yworks.yfiles.view.input.InputModeEventArgs;
import com.yworks.yfiles.view.input.MoveInputMode;
import javafx.scene.shape.Line;

import java.util.List;

/**
 * This input mode allows moving {@link javafx.scene.shape.Line}s using a drag gesture.
 */
class LineMoveInputMode extends MoveInputMode {
  private final List<Line> lines;
  private IPositionHandler handler;

  /**
   * Creates a new instance.
   *
   * @param lines A list of the {@link javafx.scene.shape.Line}s that shall be moved by this input mode.
   */
  LineMoveInputMode(final List<Line> lines) {
    this.lines = lines;
  }

  /**
   * Returns the list of {@link javafx.scene.shape.Line}s this input mode works with.
   */
  public List<Line> getLines() {
    return lines;
  }

  /**
   * Clears the {@link MoveInputMode#getPositionHandler()} property and sets the {@link
   * MoveInputMode#getHitTestable()} property to check for hit {@link Line}s.
   */
  @Override
  public void install( IInputModeContext context, ConcurrencyController concurrencyController ) {
    super.install(context, concurrencyController);
    setPositionHandler(null);
    setHitTestable(this::isValidHit);
  }

  /**
   * Returns true if an line can be found in a close surrounding of the given location.
   */
  private boolean isValidHit(ICanvasContext context, PointD location) {
    Line line = tryGetLineAt(location);
    if (line != null) {
      handler = new LinePositionHandler(line, location);
      return true;
    }
    handler = null;
    return false;
  }

  /**
   * Returns the first line found in a close surrounding of the given location or null if none can be found.
   */
  private Line tryGetLineAt(PointD location) {
    RectD surrounding = new RectD(location.getX() - 3, location.getY() - 3, 6, 6);

    return getLines().stream()
        // filter all lines that intersect with surrounding
        .filter(line -> intersects(surrounding, line))
        // return the first one or null if no snap line is in the surrounding
        .findFirst().orElse(null);
  }

  /**
   * Determines whether the given rectangle and the given line intersect.
   */
  private static boolean intersects(RectD rect, Line line) {
    return rect.intersectsLine(
        new PointD(line.getStartX(), line.getStartY()),
        new PointD(line.getEndX(), line.getEndY()));
  }

  /**
   * Sets the {@link MoveInputMode#getPositionHandler()} property.
   */
  @Override
  protected void onDragStarting(InputModeEventArgs inputModeEventArgs) {
    setPositionHandler(handler);
    super.onDragStarting(inputModeEventArgs);
  }

  /**
   * Clears the {@link MoveInputMode#getPositionHandler()} property.
   */
  @Override
  protected void onDragCanceled(InputModeEventArgs inputModeEventArgs) {
    super.onDragCanceled(inputModeEventArgs);
    setPositionHandler(null);
  }

  /**
   * Clears the {@link MoveInputMode#getPositionHandler()} property.
   */
  @Override
  protected void onDragFinished(InputModeEventArgs inputModeEventArgs) {
    super.onDragFinished(inputModeEventArgs);
    setPositionHandler(null);
  }

  /**
   * An {@link IPositionHandler} used to move {@link javafx.scene.shape.Line}s instances.
   */
  private static class LinePositionHandler implements IPositionHandler {
    private final Line line;
    private final PointD mouseDeltaFromStart;
    private PointD startFrom;

    /**
     * Creates a new handler for the given <code>line</code>.
     *
     * @param line          the line to move
     * @param mouseLocation the mouse location at the beginning of a move gesture
     */
    LinePositionHandler(Line line, PointD mouseLocation) {
      this.line = line;
      mouseDeltaFromStart = PointD.subtract(mouseLocation, new PointD(line.getStartX(), line.getStartY()));
    }

    /**
     * Called by clients to set the position to the given coordinates. The given position are interpreted to be the new
     * position of the start point of the moved {@link javafx.scene.shape.Line}.
     *
     * @param location the new location for the start point of {@link javafx.scene.shape.Line}
     */
    public void setPosition(PointD location) {
      double deltaX = location.getX() - line.getStartX();
      double deltaY = location.getY() - line.getStartY();

      line.setStartX(location.getX());
      line.setStartY(location.getY());

      line.setEndX(line.getEndX() + deltaX);
      line.setEndY(line.getEndY() + deltaY);
    }

    /**
     * Returns a view of the location of the item. The point describes the current world coordinate of the start point
     * of the moved {@link javafx.scene.shape.Line}.
     */
    @Override
    public IPoint getLocation() {
      return new PointD(line.getStartX(), line.getStartY());
    }

    /**
     * Called by clients to indicate that the element is going to be dragged. This call will be followed by one or more
     * calls to {@link IPositionHandler#handleMove(IInputModeContext, PointD, PointD)},
     * and a final {@link IPositionHandler#dragFinished(IInputModeContext, PointD, PointD)}
     * or {@link IPositionHandler#cancelDrag(IInputModeContext, PointD)}.
     *
     * @param inputModeContext the context to retrieve information about the drag from
     */
    @Override
    public void initializeDrag(IInputModeContext inputModeContext) {
      startFrom = new PointD(line.getStartX(), line.getStartY());
    }

    /**
     * Called by clients to indicate that the element has been dragged and its position should be updated. This method
     * may be called more than once after an initial {@link IPositionHandler#initializeDrag(IInputModeContext)}
     * and the final call will be followed by either one {@link IPositionHandler#dragFinished(IInputModeContext, PointD, PointD)}
     * or one {@link IPositionHandler#cancelDrag(IInputModeContext, PointD)}
     * call.
     *
     * @param inputModeContext The context to retrieve information about the drag from.
     * @param originalLocation The value of the {@link IPositionHandler#getLocation() Location}
     *                         property at the time of {@link IPositionHandler#initializeDrag(IInputModeContext)}.
     * @param newLocation      The coordinates in the world coordinate system that the client wants the handle to be at.
     *                         Depending on the implementation the {@link IPositionHandler#getLocation()
     *                         Location} may or may not be modified to reflect the new value.
     */
    @Override
    public void handleMove(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      setPosition(PointD.subtract(newLocation, mouseDeltaFromStart));
    }

    /**
     * Called by clients to indicate that the dragging has been canceled by the user. This method may be called after
     * the initial {@link #initializeDrag(IInputModeContext)} and zero or more invocations of
     * {@link #handleMove(IInputModeContext, PointD, PointD)}.
     * Alternatively to this method the {@link #dragFinished(IInputModeContext, PointD, PointD)}
     * method might be called.
     *
     * @param inputModeContext The context to retrieve information about the drag from.
     * @param originalLocation The value of the coordinate of the {@link IPositionHandler#getLocation()
     *                         Location} property at the time of {@link IPositionHandler#initializeDrag(IInputModeContext)}.
     */
    @Override
    public void cancelDrag(IInputModeContext inputModeContext, PointD originalLocation) {
      setPosition(startFrom);
    }

    /**
     * Called by clients to indicate that the repositioning has just been finished. This method may be called after the
     * initial {@link #initializeDrag(IInputModeContext)} and zero or more invocations of {@link
     * IPositionHandler#handleMove(IInputModeContext, PointD, PointD)}.
     * Alternatively to this method the {@link IPositionHandler#cancelDrag(IInputModeContext, PointD)}
     * method might be called.
     *
     * @param inputModeContext The context to retrieve information about the drag from.
     * @param originalLocation The value of the {@link IPositionHandler#getLocation() Location}
     *                         property at the time of {@link IPositionHandler#initializeDrag(IInputModeContext)}.
     * @param newLocation      The coordinates in the world coordinate system that the client wants the handle to be at.
     *                         Depending on the implementation the {@link IPositionHandler#getLocation()
     *                         Location} may or may not be modified to reflect the new value. This is the same value as
     *                         delivered in the last invocation of {@link IPositionHandler#handleMove(IInputModeContext,
     *                         PointD, PointD)}
     */
    @Override
    public void dragFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      setPosition(PointD.subtract(newLocation, mouseDeltaFromStart));
    }
  }
}
