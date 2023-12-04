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
package tutorial02_CustomStyles.step28_BridgeSupport;

import com.yworks.yfiles.view.BridgeManager;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphObstacleProvider;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.WebViewUtils;

/**
 * Enable bridges for a custom edge style.
 */
public class SampleApplication extends Application {

  public GraphControl graphControl;
  public WebView help;

  //////////////// New in this sample ////////////////
  /**
   * Adds and configures the {@link com.yworks.yfiles.view.BridgeManager}.
   */
  private void configureBridges() {
    // The graph item styles are responsible for both providing obstacles and drawing bridges.
    // the bridge manager collects the obstacles and updates given edge paths to add bridges.
    // for an example implementation, have a look at the create/get path methods in the
    // MySimpleEdgeStyle class of this demo.
    BridgeManager bridgeManager = new BridgeManager();

    // convenience class that just queries all model items
    GraphObstacleProvider provider = new GraphObstacleProvider();

    // register the obstacle provider to the BridgeManager. It will query all registered
    // obstacle providers to determine if a bridge must be created.
    bridgeManager.addObstacleProvider(provider);
    // bind the bridge manager to the GraphControl...
    bridgeManager.setCanvasControl(graphControl);
  }
  ////////////////////////////////////////////////////

  /**
   * Creates a sample graph, specifies the default styles and sets the default input mode.
   */
  public void initialize() {
    // initialize the input mode
    graphControl.setInputMode(createEditorMode());

    // initialize the default styles for newly created graph items
    initializeDefaultStyles();

    // create some graph elements with the above defined styles
    createSampleGraph();

    //////////////// New in this sample ////////////////
    // add bridge support by configuring a BridgeManager and binding it to the graph control
    configureBridges();
    ////////////////////////////////////////////////////
  }

  /**
   * Called right after stage is loaded.
   * In JavaFX, nodes don't have a width or height until the stage is displayed and the scene graph is calculated.
   * As {@link #initialize()} is called right after a node is created, but before displayed, we have to update
   * the view port later.
   */
  public void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Creates the default input mode for the GraphControl, a {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    // enable label editing
    mode.setEditLabelAllowed(true);
    return mode;
  }

  /**
   * Initializes the default styles that are used as templates for newly created graph items.
   */
  private void initializeDefaultStyles() {
    IGraph graph = graphControl.getGraph();

    // create a new style and use it as default node style
    graph.getNodeDefaults().setStyle(new MySimpleNodeStyle());
    // create a new style and use it as default edge style
    graph.getEdgeDefaults().setStyle(new MySimpleEdgeStyle());
    // use 50x50 as default node size
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Creates the initial sample graph.
   */
  private void createSampleGraph() {
    IGraph graph = graphControl.getGraph();
    INode node0 = graph.createNode(new RectD(-100, -50, 30, 30));
    INode node1 = graph.createNode(new RectD(100, -50, 30, 30));
    IEdge edge0 = graph.createEdge(node0, node1);
    INode node2 = graph.createNode(new RectD(-100, 50, 30, 30));
    INode node3 = graph.createNode(new RectD(100, 50, 30, 30));
    IEdge edge2 = graph.createEdge(node2, node3);
    INode node4 = graph.createNode(new RectD(-50, -100, 30, 30));
    INode node5 = graph.createNode(new RectD(-50, 100, 30, 30));
    IEdge edge3 = graph.createEdge(node4, node5);
    INode node6 = graph.createNode(new RectD(50, -100, 30, 30));
    INode node7 = graph.createNode(new RectD(50, 100, 30, 30));
    IEdge edge4 = graph.createEdge(node6, node7);
  }

  /**
   * Action handler for zoom in button action.
   */
  public void handleZoomInAction() {
    graphControl.setZoom(graphControl.getZoom() * 1.25);
  }

  /**
   * Action handler for zoom out button action.
   */
  public void handleZoomOutAction() {
    graphControl.setZoom(graphControl.getZoom() * 0.8);
  }

  /**
   * Action handler for reset zoom button action.
   */
  public void handleResetZoomAction() {
    graphControl.setZoom(1);
  }

  /**
   * Action handler for fit to content button action.
   */
  public void handleFitToContentAction() {
    graphControl.fitGraphBounds();
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SampleApplication.fxml"));
    fxmlLoader.setController(this);
    BorderPane root = fxmlLoader.load();

    WebViewUtils.initHelp(help, this);
    Scene scene = new Scene(root, 1365, 768);

    // In JavaFX, nodes don't have a width or height until the stage is shown and the scene graph is calculated.
    // onLoaded does some initialization that need the correct bounds of the nodes.
    stage.setOnShown(windowEvent -> onLoaded());
    stage.setTitle("Step 28 - Bridge Support");
    stage.setScene(scene);
    stage.show();
  }
}
