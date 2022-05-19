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
package layout.cleararea;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualTemplate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeHitTester;
import com.yworks.yfiles.view.input.MarqueeSelectionEventArgs;
import com.yworks.yfiles.view.input.MarqueeSelectionInputMode;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;

public class MarqueeClearAreaLayoutDemo extends DemoApplication {
  public WebView helpView;
  public GraphControl graphControl;

  /**
   * Performs layout and animation while dragging the marquee.
   */
  private ClearAreaLayoutHelper layoutHelper;

  /**
   * The marquee rectangle used to mark the area to clear
   */
  private RectangleVisual marqueeRectangle;

  private boolean layoutRunning;

  public void initialize() {
    WebViewUtils.initHelp(helpView, this);
    marqueeRectangle = new RectangleVisual();

    initializeInputModes();

    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ORANGE);
    graphControl.getGraph().getNodeDefaults().setStyle(nodeStyle);

    loadGraph();

  }

  @Override
  protected void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Registers the {@link GraphEditorInputMode} as the {@link GraphControl}s input mode
   * and initializes the marquee input mode that clears the area of the marquee rectangle.
   */
  private void initializeInputModes() {
    // enable undo/redo support
    graphControl.getGraph().setUndoEngineEnabled(true);

    // create an input mode to edit graphs
    GraphEditorInputMode editMode = new GraphEditorInputMode();

    // create an input mode to clear the area of a marquee rectangle
    // using the right mouse button
    MarqueeSelectionInputMode marqueeClearInputMode = new MarqueeSelectionInputMode();

    marqueeClearInputMode.setPressedRecognizer(IEventRecognizer.MOUSE_RIGHT_PRESSED);
    marqueeClearInputMode.setDraggedRecognizer(IEventRecognizer.MOUSE_RIGHT_DRAGGED);
    marqueeClearInputMode.setReleasedRecognizer(IEventRecognizer.MOUSE_RIGHT_RELEASED);
    marqueeClearInputMode.setCancelRecognizer(IEventRecognizer.ESCAPE_PRESSED.or(IEventRecognizer.MOUSE_LOST_CAPTURE));
    marqueeClearInputMode.setTemplate(marqueeRectangle);

    // handle dragging the marquee
    marqueeClearInputMode.addDragStartingListener(this::onDragStarting);
    marqueeClearInputMode.addDraggedListener(this::onDragged);
    marqueeClearInputMode.addDragCanceledListener(this::onDragCanceled);
    marqueeClearInputMode.addDragFinishedListener(this::onDragFinished);

    // add this mode to the edit mode
    editMode.add(marqueeClearInputMode);

    // and install the edit mode into the canvas
    graphControl.setInputMode(editMode);
  }

  /**
   * Dragging the the marquee rectangle is starting
   */
  private void onDragStarting(Object sender, MarqueeSelectionEventArgs e) {
    INode hitGroupNode = getHitGroupNode(e.getContext(), e.getContext().getCanvasControl().getLastEventLocation());

    layoutHelper = new ClearAreaLayoutHelper(graphControl);
    layoutHelper.setClearRectangle(e.getRectangle());
    layoutHelper.setGroupNode(hitGroupNode);

    layoutHelper.initializeLayout();
  }

  /**
   * The marquee rectangle is currently dragged.
   * For each drag a new layout is calculated and applied if the previous one is completed.
   */
  private void onDragged(Object sender, MarqueeSelectionEventArgs e) {
    layoutHelper.setClearRectangle(e.getRectangle());
    layoutHelper.runLayout();
  }

  /**
   * Dragging the marquee rectangle has been canceled so
   * the state before the gesture must be restored.
   */
  private void onDragCanceled(Object sender, MarqueeSelectionEventArgs e) {
    layoutHelper.setClearRectangle(e.getRectangle());
    layoutHelper.cancelLayout();
  }

  /**
   * Dragging the marquee rectangle has been finished so
   * we execute the layout with the final rectangle.
   */
  private void onDragFinished(Object sender, MarqueeSelectionEventArgs e) {
    layoutHelper.setClearRectangle(e.getRectangle());
    layoutHelper.stopLayout();
  }

  /**
   * Returns the group node at the given location.
   * If there is no group node, <code>null</code> is returned.
   */
  private INode getHitGroupNode(IInputModeContext context, PointD location) {
    INodeHitTester hitTester = context.lookup(INodeHitTester.class);
    if (hitTester != null) {
      return hitTester
              .enumerateHits(context, location)
              .stream()
              .filter(iNode -> graphControl.getGraph().isGroupNode(iNode))
              .findFirst().orElse(null);
    }
    return null;
  }


  /**
   * Loads the graph from the sample graphml file
   */
  private void loadGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/grouping.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Applies the {@link HierarchicLayout} to the graph
   */
  public void layoutHierarchical() {
    if (layoutRunning) {
      return;
    }
    layoutRunning = true;

    HierarchicLayout layout = new HierarchicLayout();
    layout.setOrthogonalRoutingEnabled(true);

    LayoutExecutor executor = new LayoutExecutor(graphControl, layout);
    executor.setDuration(Duration.ofMillis(500));
    executor.setContentRectUpdatingEnabled(true);

    executor.addLayoutFinishedListener((source, args) -> {
      layoutRunning = false;
    });

    executor.start();
  }

  public static void main(String[] args) {
    launch(args);
  }

  private static class RectangleVisual implements IVisualTemplate {

    private final Color color = new Color(1.0f, 0.27058825f, 0.0f, 0.5);
    private final Rectangle rectangle;

    public RectangleVisual() {
      this.rectangle = new Rectangle();
    }

    @Override
    public Node createVisual(IRenderContext context, RectD bounds, Object dataObject) {
      rectangle.setFill(color);
      rectangle.setX(bounds.getX());
      rectangle.setY(bounds.getY());
      rectangle.setWidth(bounds.getWidth());
      rectangle.setHeight(bounds.getHeight());
      return rectangle;
    }

    @Override
    public Node updateVisual(IRenderContext context, Node oldVisual, RectD bounds, Object dataObject) {
      return createVisual(context, bounds, dataObject);
    }
  }
}
