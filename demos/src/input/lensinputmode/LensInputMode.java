/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package input.lensinputmode;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Mouse2DEvent;
import com.yworks.yfiles.view.MouseWheelBehaviors;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.AbstractInputMode;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * A specialized {@link IInputMode} that displays the currently hovered-over part of the graph in
 * some kind of magnifying glass.
 */
public class LensInputMode extends AbstractInputMode {

  /**
   * The {@link ICanvasObjectGroup} containing the lens graph control.
   */
  private ICanvasObjectGroup lensGroup;

  /**
   * Indicates whether the mouse is inside the {@link com.yworks.yfiles.view.CanvasComponent}.
   * If not, the magnifying {@link GraphComponent} is not painted in the {@link LensVisual}.
   */
  private boolean mouseInside;

  private final EventHandler<Mouse2DEvent> updateLensLocation = args -> repaint();

  private final EventHandler<Mouse2DEvent> updateLensVisibility =
      args -> {
        mouseInside = args.getEventType() == Mouse2DEvent.ENTERED;
        repaint();
      };

  /**
   * Stores the size of the magifying glass.
   */
  private final DoubleProperty diameter = new SimpleDoubleProperty(250);

  /**
   * Gets the size of the "magnifying glass".
   */
  public double getDiameter() {
    return diameter.get();
  }

  /**
   * Sets the size of the "magnifying glass".
   */
  public void setDiameter(double value) {
    diameter.set(value);
  }

  /**
   * The size of the "magnifying glass".
   */
  public DoubleProperty diameterProperty() {
    return diameter;
  }

  /**
   * Stores the zoom factor of the magnifying glass.
   */
  private final DoubleProperty zoomFactor = new SimpleDoubleProperty(3);

  /**
   * Gets the zoom factor used for magnifying the graph.
   */
  public double getZoomFactor() {
    return zoomFactor.get();
  }

  /**
   * Sets the zoom factor used for magnifying the graph.
   */
  public void setZoomFactor(double value) {
    zoomFactor.set(value);
  }

  /**
   * The zoom factor used for magnifying the graph.
   */
  public DoubleProperty zoomFactorProperty() {
    return zoomFactor;
  }


  /**
   * Initializes a new instance of {@code LensInputMode}/
   */
  public LensInputMode() {
    zoomFactorProperty().addListener((observable, oldValue, newValue) -> repaint());
    diameterProperty().addListener((observable, oldValue, newValue) -> repaint());
  }

  /**
   * Repaints the graph control to which this mode has been added.
   */
  private void repaint() {
    IInputModeContext context = getInputModeContext();
    if (context != null && context.getCanvasControl() != null) {
      context.getCanvasControl().invalidate();
    }
  }

  /**
   * Installs the {@link LensInputMode} by adding the {@link LensVisual}
   * to the {@link lensGroup} and registering the necessary mouse event listeners.
   */
  public void install(IInputModeContext context, ConcurrencyController controller) {
    super.install(context, controller);

    GraphControl canvasControl = (GraphControl) context.getCanvasControl();

    lensGroup = canvasControl.getRootGroup().addGroup();
    lensGroup.above(canvasControl.getInputModeGroup());
    lensGroup.addChild(new LensVisualCreator());

    canvasControl.addEventHandler(Mouse2DEvent.MOVED, updateLensLocation);
    canvasControl.addEventHandler(Mouse2DEvent.DRAGGED, updateLensLocation);

    canvasControl.addEventHandler(Mouse2DEvent.ENTERED, updateLensVisibility);
    canvasControl.addEventHandler(Mouse2DEvent.EXITED, updateLensVisibility);
  }

  /**
   * Uninstalls the {@link LensInputMode} by removing the {@link lensGroup}
   * and unregistering the various mouse event listeners.
   */
  public void uninstall(IInputModeContext context) {
    CanvasControl canvasComponent = context.getCanvasControl();

    if (lensGroup != null) {
      lensGroup.remove();
      lensGroup = null;
    }

    canvasComponent.removeEventHandler(Mouse2DEvent.MOVED, updateLensLocation);
    canvasComponent.removeEventHandler(Mouse2DEvent.DRAGGED, updateLensLocation);

    canvasComponent.removeEventHandler(Mouse2DEvent.ENTERED, updateLensVisibility);
    canvasComponent.removeEventHandler(Mouse2DEvent.EXITED, updateLensVisibility);

    super.uninstall(context);
  }

  /**
   * The {@link IVisualCreator} that displays the magnifying {@link GraphControl}.
   */
  private class LensVisualCreator implements IVisualCreator {

    @Override
    public Node createVisual(IRenderContext context) {
      GraphControl graphControl = getGraphControl(context);

      // Instantiate the GraphControl displaying the zoomed graph.
      GraphControl lensGraphControl = new GraphControl();
      // Re-use the same graph, selection, and projection
      lensGraphControl.setGraph(graphControl.getGraph());
      lensGraphControl.setSelection(graphControl.getSelection());
      lensGraphControl.setProjection(graphControl.getProjection());

      // Disable interaction and scrollbars
      lensGraphControl.setMouseWheelBehavior(MouseWheelBehaviors.NONE);
      lensGraphControl.setAutoDragEnabled(false);
      lensGraphControl.setMouseTransparent(true);
      lensGraphControl.setHorizontalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      lensGraphControl.setVerticalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      // This is only necessary to show handles in the zoomed graph. Remove if not needed
      lensGraphControl.setInputMode(new GraphEditorInputMode());

      // Clip the overlay to circular
      lensGraphControl.setClip(new Ellipse());
      lensGraphControl.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

      // Create an outline so that it looks like a lens
      Ellipse outline = new Ellipse();
      outline.setStroke(Color.GRAY);
      outline.setStrokeWidth(2);
      outline.setFill(null);

      // Arrange both the GraphControl and an outline
      VisualGroup visualGroup = new VisualGroup();
      visualGroup.getChildren().addAll(lensGraphControl, outline);

      // Update all properties to their current values and ensure that the
      // overlay is rendered where it should
      return updateVisual(context, visualGroup);
    }

    @Override
    public Node updateVisual(IRenderContext context, Node oldVisual) {
      GraphControl graphControl = getGraphControl(context);

      VisualGroup visualGroup = (VisualGroup) oldVisual;
      GraphControl lensGraphControl = (GraphControl) visualGroup.getChildren().get(0);
      Ellipse outline = (Ellipse) visualGroup.getChildren().get(1);

      // Set where the overlay should appear
      PointD location = context.toViewCoordinates(graphControl.getLastEventLocation());
      double diameter = getDiameter();
      double radius = diameter * 0.5;
      lensGraphControl.setPrefSize(diameter, diameter);
      lensGraphControl.setTranslateX(location.x);
      lensGraphControl.setTranslateY(location.y);
      setCenterAndRadius(outline, location.x + radius, location.y + radius, radius);

      // Update center, zoom, and projection
      lensGraphControl.setCenter(graphControl.getLastEventLocation());
      lensGraphControl.setZoom(getZoomFactor() * context.getZoom());
      lensGraphControl.setProjection(context.getProjection());

      // Ensure that the overlay is displayed in view coordinates
      visualGroup.getTransforms().clear();
      visualGroup.getTransforms().add(context.getViewTransform());

      // Update the clipping path
      Ellipse clip = (Ellipse) lensGraphControl.getClip();
      setCenterAndRadius(clip, radius, radius, radius);

      visualGroup.setVisible(mouseInside && isEnabled());

      return oldVisual;
    }

    /**
     * Assigns a new center and radius to the given circle.
     */
    private void setCenterAndRadius(Ellipse circle, double x, double y, double radius) {
      circle.setCenterX(x);
      circle.setCenterY(y);
      circle.setRadiusX(radius);
      circle.setRadiusY(radius);
    }

    /**
     * Gets the graph control from the given context.
     */
    private GraphControl getGraphControl(ICanvasContext context) {
      return (GraphControl) context.getCanvasControl();
    }
  }
}
