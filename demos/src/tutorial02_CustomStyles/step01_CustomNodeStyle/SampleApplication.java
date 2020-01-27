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
package tutorial02_CustomStyles.step01_CustomNodeStyle;

import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.Pen;
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
 * Create a custom node style.
 */
public class SampleApplication extends Application {

  public GraphControl graphControl;
  public WebView help;

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
  }

  /**
   * Called right after stage is loaded.
   * In JavaFX, nodes don't have a width or height until the stage is displayed and the scene graph is calculated.
   * As {@link #initialize()} is called right after a node is created, but before displayed, we have to update
   * the view port later.
   */
  public void onLoaded() {
    // show the whole graph in the view
    graphControl.fitGraphBounds();
  }

  /**
   * Creates the default input mode for the GraphComponent, a {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
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
    // use the DefaultLabelStyle as default label style
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setBackgroundPen(Pen.getBlack());
    labelStyle.setBackgroundPaint(Color.WHITE);
    graph.getNodeDefaults().getLabelDefaults().setStyle(labelStyle);
    graph.getEdgeDefaults().getLabelDefaults().setStyle(labelStyle);
    // node labels should be placed above the node by default
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(ExteriorLabelModel.NORTH);

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
    ILabel label0 = graph.addLabel(edge0, "Edge Label");
    ILabel label1 = graph.addLabel(node1, "Node Label");
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
    stage.setTitle("Step 1 - Custom Node Style");
    stage.setScene(scene);
    stage.show();
  }
}
