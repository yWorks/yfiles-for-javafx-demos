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
package layout.hierarchiclayout;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.ConstrainedPositionHandler;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;
import com.yworks.yfiles.view.input.MoveInputMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Helper class that moves a node and uses the location of the mouse to determine the layer where the nodes should be
 * moved to.
 */
class LayerPositionHandler extends ConstrainedPositionHandler {
  // visualizes the layers calculated by the HierarchicLayout
  private LayerVisualCreator layerVisualCreator;
  // the node to move to another layer
  private INode node;
  // the mapping that assigns each node to a layer
  private IMapper<INode, Integer> newLayerMapper;
  // the highlighting of the target layer
  private ICanvasObject targetLayerHighlightCanvasObject;

  /**
   * Initializes a new <code>LayerPositionHandler</code> instance.
   * @param layerVisual     visualizes the layers calculated by the HierarchicLayout
   * @param node            the node that is dragged to move it to another layer
   * @param positionHandler handles the positioning of the dragged node
   * @param newLayerMapper  the mapping that assigns each node to a layer
   */
  LayerPositionHandler(LayerVisualCreator layerVisual, INode node, IPositionHandler positionHandler,
                       IMapper<INode, Integer> newLayerMapper) {
    super(positionHandler);
    this.layerVisualCreator = layerVisual;
    this.node = node;
    this.newLayerMapper = newLayerMapper;
  }

  @Override
  protected void onInitialized(IInputModeContext inputModeContext, PointD originalLocation) {
    super.onInitialized(inputModeContext, originalLocation);
    if (inputModeContext.getParentInputMode() instanceof MoveInputMode) {
      CanvasControl canvasComponent = inputModeContext.getCanvasControl();

      // add a visual indicator rectangle for this layer
      addTargetLayerHighlighting(canvasComponent);
      // updates the bounds of the visual indicator to the bounds of the currently hit layer
      updateTargetLayerHighlighting(canvasComponent.getLastEventLocation());
    }
  }

  /**
   * Adds a visual that highlights the layer a moved node will be assigned to.
   * @param canvasComponent the canvas to which the indicator shall be added
   */
  private void addTargetLayerHighlighting(CanvasControl canvasComponent) {
    if(targetLayerHighlightCanvasObject != null) {
      targetLayerHighlightCanvasObject.remove();
    }

    // create a visual that highlights the target layer
    Rectangle highlightRect = new Rectangle();
    highlightRect.setFill(Color.rgb(128, 128, 128, 0.2));
    Pen.getLightGray().styleShape(highlightRect);


    // add the visual to the background group of the scene graph
    targetLayerHighlightCanvasObject = canvasComponent.getBackgroundGroup().addChild(highlightRect, ICanvasObjectDescriptor.VISUAL);
  }

  /**
   * Removes the visual that highlights the target layer.
   */
  private void removeTargetLayerHighlighting() {
    if (targetLayerHighlightCanvasObject != null) {
      targetLayerHighlightCanvasObject.remove();
      targetLayerHighlightCanvasObject = null;
    }
  }

  @Override
  protected void onCanceled(IInputModeContext inputModeContext, PointD originalLocation) {
    super.onCanceled(inputModeContext, originalLocation);
    if (targetLayerHighlightCanvasObject != null) {
      // clean up
      removeTargetLayerHighlighting();
    }
  }

  @Override
  protected void onFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
    super.onFinished(inputModeContext, originalLocation, newLocation);
    if (targetLayerHighlightCanvasObject != null) {
      CanvasControl canvasComponent = inputModeContext.getCanvasControl();
      // assign the node to the layer at the current mouse location
      int targetLayer = getTargetLayer(canvasComponent.getLastEventLocation());
      newLayerMapper.setValue(node, targetLayer);

      // clean up
      removeTargetLayerHighlighting();
    }
  }

  @Override
  protected void onMoved(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
    super.onMoved(inputModeContext, originalLocation, newLocation);
    if (targetLayerHighlightCanvasObject != null) {
      CanvasControl canvasComponent = inputModeContext.getCanvasControl();
      // updates the bounds of the visual indicator
      updateTargetLayerHighlighting(canvasComponent.getLastEventLocation());
    }
  }

  /**
   * Updates the bounds of the highlight visual to the bounds of the layer at the given position.
   * @param location the location to get the layer from
   */
  private void updateTargetLayerHighlighting(PointD location) {
    // determine the bounds of the target layer, i.e. the layer at the given location
    int targetLayer = getTargetLayer(location);
    RectD layerBounds = layerVisualCreator.getLayerBounds(targetLayer);
    if(!layerBounds.isFinite()) {
      layerBounds = RectD.EMPTY;
    }

    // updates the bounds of the visual indicator
    if (targetLayerHighlightCanvasObject != null && targetLayerHighlightCanvasObject.getUserObject() instanceof Rectangle) {
      Rectangle highlightRect = (Rectangle) targetLayerHighlightCanvasObject.getUserObject();
      highlightRect.setX(layerBounds.getX());
      highlightRect.setY(layerBounds.getY());
      highlightRect.setWidth(layerBounds.getWidth());
      highlightRect.setHeight(layerBounds.getHeight());
    }
  }

  /**
   * Returns the layer at the given location.
   */
  private int getTargetLayer(PointD location) {
    return layerVisualCreator.getLayer(location);
  }

  @Override
  protected PointD constrainNewLocation(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    // do not constrain...
    return newLocation;
  }
}
