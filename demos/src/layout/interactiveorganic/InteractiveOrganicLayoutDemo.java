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
package layout.interactiveorganic;

import com.yworks.yfiles.algorithms.GraphConnectivity;
import com.yworks.yfiles.algorithms.INodeMap;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.CopiedLayoutGraph;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.organic.InteractiveOrganicLayout;
import com.yworks.yfiles.utils.FlagsEnum;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.view.IAnimationCallback;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.Themes;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Sample application that demonstrates the usage of {@link com.yworks.yfiles.layout.organic.InteractiveOrganicLayout}.
 */
public class InteractiveOrganicLayoutDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;

  private InteractiveOrganicLayout layout;
  private CopiedLayoutGraph copiedLayoutIGraph;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    // initialize the input mode
    initializeInputModes();
  }

  /**
   * Calls {@link #createEditorMode()} and registers the result with
   * {@link CanvasControl#setInputMode(IInputMode)}.
   */
  private void initializeInputModes() {
    graphControl.setInputMode(createEditorMode());
  }

  /**
   * Creates the default input mode for the GraphControl, a {@link GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    mode.getCreateBendInputMode().setEnabled(false);
    mode.setSelectableItems(FlagsEnum.or(GraphItemTypes.NODE, GraphItemTypes.EDGE));
    mode.setMarqueeSelectableItems(GraphItemTypes.NODE);
    mode.setClickSelectableItems(FlagsEnum.or(GraphItemTypes.NODE, GraphItemTypes.EDGE));
    mode.setClickableItems(GraphItemTypes.NODE);
    mode.setShowHandleItems(GraphItemTypes.NONE);
    mode.getCreateEdgeInputMode().setCreateBendAllowed(false);

    // wrap the position handler used for the MoveInputMode to update the layout
    // when graph elements have been moved interactively
    mode.getMoveInputMode().setPositionHandler(new MyPositionHandler(mode.getMoveInputMode().getPositionHandler()));
    return mode;
  }

  /**
   * Called when the stage is shown and the {@link GraphControl} is already resized to its preferred size.
   */
  public void onLoaded() {
    // initialize the graph
    initializeGraph();
  }

  /**
   * Initializes the graph instance setting default styles and loads a sample graph.
   */
  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();

    DemoStyles.initDemoStyles(graph);
    graph.getEdgeDefaults().setStyle(DemoStyles.createDemoEdgeStyle(Themes.PALETTE_ORANGE, false));

    // load a sample graph
    try {
      new GraphMLIOHandler().read(graph, getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // set some defaults
    graph.getNodes().stream().findFirst().ifPresent(node -> {
      graph.getNodeDefaults().setStyle(node.getStyle());
      graph.getNodeDefaults().setStyleInstanceSharingEnabled(true);
    });

    // center the initial graph
    graphControl.fitGraphBounds();

    // create a copy of the graph for the layout algorithm
    copiedLayoutIGraph = new LayoutGraphAdapter(graph).createCopiedLayoutGraph();

    // create and start the layout algorithm. It runs in a thread and
    // can update the current layout with its wake-up method.
    layout = startLayout();
    wakeUp();

    // register a listeners so that structure updates between the
    // graph layout and the graph are handled automatically
    graph.addNodeCreatedListener((source, evt) -> {
      if (layout != null) {
        PointD center = evt.getItem().getLayout().getCenter();
        layout.syncStructure(true);
        // we nail down all newly created nodes
        Node copiedNode = copiedLayoutIGraph.getCopiedNode(evt.getItem());
        layout.setCenter(copiedNode, center.getX(), center.getY());
        layout.setInertia(copiedNode, 1);
        layout.setStress(copiedNode, 0);
      }
    });
    graph.addNodeRemovedListener(this::synchronize);
    graph.addEdgeCreatedListener(this::synchronize);
    graph.addEdgeRemovedListener(this::synchronize);
  }

  /**
   * Creates a new layout algorithm instance and starts it in a new thread.
   */
  private InteractiveOrganicLayout startLayout() {
    // create the layout algorithm
    InteractiveOrganicLayout organicLayout = new InteractiveOrganicLayout();
    organicLayout.setMaximumDuration(2000);

    // use an animator that animates an infinite animation. This means that all
    // changes to the layout are animated, as long as the demo is running.
    Animator animator = new Animator(graphControl);
    animator.setAutoUpdateEnabled(false);
    animator.setUserInteractionAllowed(true);
    animator.animate(new IAnimationCallback() {
      private boolean hasLayoutStarted;

      @Override
      public void animate(double time) {
        // wait until the layout algorithm has been started
        if (!hasLayoutStarted){
          if (organicLayout.isRunning()){
            hasLayoutStarted = true;
          }
          return;
        }

        // now the layout algorithm has been started and we check if it is still running.
        // If not we destroy the animator otherwise we apply the layout to the graph
        if (!organicLayout.isRunning()) {
          animator.stop();
          return;
        }
        if (organicLayout.commitPositionsSmoothly(50, 0.05) > 0) {
          graphControl.invalidate();
        }
      }
    }, Duration.ofDays(100));

    // run the layout algorithm in a separate thread. If we want a recalculation of the layout,
    // we need to call the wake-up method of the InteractiveOrganicLayout
    Thread thread = new Thread(() -> {
      organicLayout.applyLayout(copiedLayoutIGraph);
      // stop the animator when the layout returns (does not normally happen at all)
      Platform.runLater(animator::stop);
    });
    thread.setDaemon(true);
    thread.start();

    return organicLayout;
  }

  /**
   * Wakes the layout algorithm up to calculate an initial layout.
   */
  private void wakeUp() {
    if (layout != null) {
      // we make all nodes freely movable
      copiedLayoutIGraph.getNodes().forEach(node -> layout.setInertia(node, 0));
      // then wake up the layout algorithm
      layout.wakeUp();

      // and after two second we freeze the nodes again...
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          copiedLayoutIGraph.getNodes().forEach(node -> layout.setInertia(node, 1));
          timer.cancel();
        }
      }, 2000);
    }
  }

  /**
   * EventHandler method that synchronizes the structure with the layout
   * algorithm if the graph structure has been changed interactively.
   */
  private void synchronize(Object sender, IEventArgs args) {
    if (layout != null) {
      layout.syncStructure(true);
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Custom position handler that automatically triggers layout updates when graph elements have been moved
   * interactively.
   */
  private class MyPositionHandler implements IPositionHandler {
    private IPositionHandler originalHandler;

    MyPositionHandler(IPositionHandler originalHandler) {
      this.originalHandler = originalHandler;
    }

    @Override
    public IPoint getLocation() {
      return originalHandler.getLocation();
    }

    @Override
    public void initializeDrag(IInputModeContext inputModeContext) {
      InteractiveOrganicLayout layout = InteractiveOrganicLayoutDemo.this.layout;
      if (layout != null) {
        CopiedLayoutGraph copy = copiedLayoutIGraph;
        INodeMap componentNumber = copy.createNodeMap();
        GraphConnectivity.connectedComponents(copy, componentNumber);

        Set<Integer> movedComponents = new HashSet<>();
        Set<Node> selectedNodes = new HashSet<>();

        for (INode node : graphControl.getSelection().getSelectedNodes()) {
          Node copiedNode = copy.getCopiedNode(node);
          if (copiedNode != null) {
            // remember that we nailed down this node
            selectedNodes.add(copiedNode);
            // remember that we are moving this component
            movedComponents.add(componentNumber.getInt(copiedNode));
            // update the position of the node in the CopiedLayoutGraph to match the one in the IGraph
            layout.setCenter(copiedNode, node.getLayout().getCenter().getX(), node.getLayout().getCenter().getY());
            // actually, the node itself is fixed at the start of a drag gesture
            layout.setInertia(copiedNode, 1.0);
            // increasing the heat has the effect that the layout algorithm will consider this node as not completely placed...
            // In this case, the node itself is fixed, but its neighbors will wake up
            increaseHeat(copiedNode, layout, 0.5);
          }
        }

        // there are components that won't be moved - nail the nodes down so that they don't spread apart infinitely
        for (Node copiedNode : copy.getNodes()) {
          if (!movedComponents.contains(componentNumber.getInt(copiedNode))) {
            layout.setInertia(copiedNode, 1);
          } else {
            if (!selectedNodes.contains(copiedNode)) {
              // make it float freely
              layout.setInertia(copiedNode, 0);
            }
          }
        }

        // dispose the map
        copy.disposeNodeMap(componentNumber);

        // notify the layout algorithm that there is new work to do...
        layout.wakeUp();
      }
      originalHandler.initializeDrag(inputModeContext);
    }

    @Override
    public void handleMove(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      originalHandler.handleMove(inputModeContext, originalLocation, newLocation);
      InteractiveOrganicLayout layout = InteractiveOrganicLayoutDemo.this.layout;
      if (layout != null) {
        CopiedLayoutGraph copy = copiedLayoutIGraph;
        for (INode node : graphControl.getSelection().getSelectedNodes()) {
          Node copiedNode = copy.getCopiedNode(node);
          if (copiedNode != null) {
            // update the position of the node in the CopiedLayoutGraph to match the one in the IGraph
            layout.setCenter(copiedNode, node.getLayout().getCenter().getX(), node.getLayout().getCenter().getY());
            // increasing the heat has the effect that the layout algorithm will consider these nodes as not completely placed...
            increaseHeat(copiedNode, layout, 0.05);
          }
        }
        // notify the layout algorithm that there is new work to do...
        layout.wakeUp();
      }
    }

    private void increaseHeat(Node copiedNode, InteractiveOrganicLayout layout, double delta) {
      // increase heat of neighbors
      for (Node neighbor : copiedNode.getNeighbors()) {
        double oldStress = layout.getStress(neighbor);
        layout.setStress(neighbor, Math.min(1, oldStress + delta));
      }
    }

    @Override
    public void cancelDrag(IInputModeContext inputModeContext, PointD originalLocation) {
      originalHandler.cancelDrag(inputModeContext, originalLocation);
      InteractiveOrganicLayout layout = InteractiveOrganicLayoutDemo.this.layout;
      if (layout != null) {
        CopiedLayoutGraph copy = copiedLayoutIGraph;
        for (INode node : graphControl.getSelection().getSelectedNodes()) {
          Node copiedNode = copy.getCopiedNode(node);
          if (copiedNode != null) {
            // update the position of the node in the CLG to match the one in the IGraph
            layout.setCenter(copiedNode, node.getLayout().getCenter().getX(), node.getLayout().getCenter().getY());
            layout.setStress(copiedNode, 0);
          }
        }
        for (Node copiedNode : copy.getNodes()) {
          // reset the node's inertia to be fixed
          layout.setInertia(copiedNode, 1.0);
          layout.setStress(copiedNode, 0);
        }
        // we don't want to restart the layout (since we canceled the drag anyway...)
      }
    }

    @Override
    public void dragFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      originalHandler.dragFinished(inputModeContext, originalLocation, newLocation);
      InteractiveOrganicLayout layout = InteractiveOrganicLayoutDemo.this.layout;
      if (layout != null) {
        CopiedLayoutGraph copy = copiedLayoutIGraph;
        for (INode node : graphControl.getSelection().getSelectedNodes()) {
          Node copiedNode = copy.getCopiedNode(node);
          if (copiedNode != null) {
            // update the position of the node in the CLG to match the one in the IGraph
            layout.setCenter(copiedNode, node.getLayout().getCenter().getX(), node.getLayout().getCenter().getY());
            layout.setStress(copiedNode, 0);
          }
        }
        for (Node copiedNode : copy.getNodes()) {
          // reset the node's inertia to be fixed
          layout.setInertia(copiedNode, 1.0);
          layout.setStress(copiedNode, 0);
        }
      }
    }
  }
}
