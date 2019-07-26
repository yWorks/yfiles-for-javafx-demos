/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package tutorial02_CustomStyles.step26_CustomGroupStyle;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.NinePositionsEdgeLabelModel;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.WebViewUtils;

/**
 * Implement a custom port style. In this case the port is a transparent white circle.
 */
public class SampleApplication extends Application {

  public GraphControl graphControl;
  public WebView help;

  /**
   * Creates a sample graph, specifies the default styles and sets the default input mode.
   */
  public void initialize() {

    //////////////// New in this sample ////////////////
    // configures the default style for group nodes
    configureGroupNodeStyles();

    // from now on, everything can be done on the actual managed view instance
    enableFolding();
    ////////////////////////////////////////////////////

    // initialize the input mode
    graphControl.setInputMode(createEditorMode());

    // initialize the default styles for newly created graph items
    initializeDefaultStyles();

    // create some graph elements with the above defined styles
    createSampleGraph();
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

  //////////////// New in this sample ////////////////
  /**
   * Configures the default style for group nodes.
   */
  private void configureGroupNodeStyles() {
    // use the custom group style
    graphControl.getGraph().getGroupNodeDefaults().setStyle(new MyGroupNodeStyle());
  }

  /**
   * Enables folding - changes the GraphComponent's graph to a managed view that provides the actual collapse/expand state.
   */
  private void enableFolding() {
    // creates the folding manager and sets its master graph to
    // the single graph that has served for all purposes up to this point
    FoldingManager manager = new FoldingManager(graphControl.getGraph());
    // creates a managed view from the master graph and
    // replaces the existing graph view with a managed view
    graphControl.setGraph(manager.createFoldingView().getGraph());
    wrapGroupNodeStyles();
  }

  /**
   * Changes the default style for group nodes. We use {@link com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator}
   * to wrap the group style, since we want to have nice -/+ buttons for collapsing/expanding. The {@link
   * com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecoratorRenderer renderer} is customized to change the
   * button visualization.
   */
  private void wrapGroupNodeStyles() {
    IFoldingView foldingView = graphControl.getGraph().getFoldingView();
    if (foldingView != null) {
      // wrap the group node style with a custom CollapsibleNodeStyleDecorator to change the button visualization
      INodeStyle groupNodeStyle = foldingView.getGraph().getGroupNodeDefaults().getStyle();
      CollapsibleNodeStyleDecorator nodeStyleDecorator = new MyCollapsibleNodeStyleDecorator(groupNodeStyle, new SizeD(14, 14));
      // use a different label model for button placement which fixes it at the upper left corner of the node layout
      InteriorLabelModel labelModel = new InteriorLabelModel();
      labelModel.setInsets(new InsetsD(2));
      nodeStyleDecorator.setButtonPlacement(labelModel.createParameter(InteriorLabelModel.Position.NORTH_WEST));
      // set the decorator as the new default style for group nodes
      foldingView.getGraph().getGroupNodeDefaults().setStyle(nodeStyleDecorator);
    }
  }
  ////////////////////////////////////////////////////

  /**
   * Creates the default input mode for the GraphComponent, a {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    //////////////// New in this sample ////////////////
    // enable grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    mode.setGroupingOperationsAllowed(true);
    /////////////////////////////////////////////////////

    // enable label editing
    mode.setEditLabelAllowed(true);
    return mode;
  }

  /**
   * Initializes the default styles that are used as templates for newly created graph items.
   */
  private void initializeDefaultStyles() {
    IGraph graph = graphControl.getGraph();

    // create a new style and use it as default port style
    graph.getNodeDefaults().getPortDefaults().setStyle(new MySimplePortStyle());

    // create a new style and use it as default node style
    graph.getNodeDefaults().setStyle(new MySimpleNodeStyle());

    // create a new style and use it as default edge style
    graph.getEdgeDefaults().setStyle(new MySimpleEdgeStyle());

    // create a new style and use it as default label style
    graph.getNodeDefaults().getLabelDefaults().setStyle(new MyDefaultLabelStyle());
    graph.getEdgeDefaults().getLabelDefaults().setStyle(new MyDefaultLabelStyle());
    // node labels should be placed below and left of the node by default, so we can see the connector to its node
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(ExteriorLabelModel.SOUTH_WEST);

    // use 50x50 as default node size
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Creates the initial sample graph.
   */
  private void createSampleGraph() {
    IGraph graph = graphControl.getGraph();
    INode node0 = graph.createNode(new RectD(180, 40, 30, 30));
    INode node1 = graph.createNode(new RectD(260, 50, 30, 30));
    INode node2 = graph.createNode(new RectD(284, 200, 30, 30));
    INode node3 = graph.createNode(new RectD(350, 40, 30, 30));
    IEdge edge0 = graph.createEdge(node1, node2);
    // add some bends
    graph.addBend(edge0, new PointD(350, 130));
    graph.addBend(edge0, new PointD(230, 170));
    graph.createEdge(node1, node0);
    graph.createEdge(node1, node3);
    ILabel label0 = graph.addLabel(edge0, "Edge Label", NinePositionsEdgeLabelModel.CENTER_CENTERED);

    //////////////// New in this sample ////////////////
    // create group nodes containing some of the above node
    INode group1 = graph.groupNodes(node0, node1);
    INode group2 = graph.groupNodes(node2);
    group1.setTag(Color.GOLD);
    group2.setTag(Color.LIMEGREEN);
    ////////////////////////////////////////////////////

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
    stage.setTitle("Step 26 - Custom Group Style");
    stage.setScene(scene);
    stage.show();
  }
}
