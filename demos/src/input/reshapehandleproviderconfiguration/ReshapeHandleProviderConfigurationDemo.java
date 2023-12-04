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
package input.reshapehandleproviderconfiguration;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.NodeReshapeHandleProvider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.Palette;
import toolkit.Themes;
import toolkit.WebViewUtils;


/**
 * Customize the resize behavior of nodes by implementing a custom {@link
 * IReshapeHandleProvider}.
 */
public class ReshapeHandleProviderConfigurationDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;
  private ApplicationState applicationState;

  /**
   * Registers a callback function as a decorator that provides a custom {@link IReshapeHandleProvider}
   * for each node. <p> This callback function is called whenever a node in the graph is queried for its
   * <code>IReshapeHandleProvider</code>. In this case, the 'node' parameter will be set to that node and the
   * 'delegateHandler' parameter will be set to the reshape handle provider that would have been returned without
   * setting this function as a decorator. </p>
   */
  private void registerReshapeHandleProvider(Rectangle boundaryRectangle) {
    NodeDecorator nodeDecorator = graphControl.getGraph().getDecorator().getNodeDecorator();

    // deactivate reshape handling for the red node
    nodeDecorator.getReshapeHandleProviderDecorator().hideImplementation(
        node -> Themes.PALETTE_RED.equals(node.getTag()));

    // return customized reshape handle provider for the orange, blue and green node
    nodeDecorator.getReshapeHandleProviderDecorator().setFactory(
        node -> Themes.PALETTE_ORANGE.equals(node.getTag())
            || Themes.PALETTE_BLUE.equals(node.getTag())
            || Themes.PALETTE_GREEN.equals(node.getTag())
            || Themes.PALETTE_PURPLE.equals(node.getTag())
            || Themes.PALETTE_LIGHTBLUE.equals(node.getTag())
            || Themes.PALETTE58.equals(node.getTag()),
        node -> {
          // Obtain the tag from the node
          Object nodeTag = node.getTag();
          RectD maximumBoundingArea = new RectD(boundaryRectangle.getX(), boundaryRectangle.getY(),
              boundaryRectangle.getWidth(), boundaryRectangle.getHeight());

          // Create a default reshape handle provider for nodes
          IReshapeHandler reshapeHandler = node.lookup(IReshapeHandler.class);
          NodeReshapeHandleProvider provider = new NodeReshapeHandleProvider(node, reshapeHandler,
              HandlePositions.BORDER);

          // Customize the handle provider depending on the node's color
          if (Themes.PALETTE_ORANGE.equals(nodeTag)) {
            // Restrict the node bounds to the boundaryRectangle
            provider.setMaximumBoundingArea(maximumBoundingArea);
          } else if (Themes.PALETTE_GREEN.equals(nodeTag)) {
            // Show only handles at the corners and always use aspect ratio resizing
            provider.setHandlePositions(HandlePositions.CORNERS);
            provider.setRatioReshapeRecognizer(IEventRecognizer.ALWAYS);
          } else if (Themes.PALETTE_BLUE.equals(nodeTag)) {
            // Restrict the node bounds to the boundaryRectangle and
            // show only handles at the corners and always use aspect ratio resizing
            provider.setMaximumBoundingArea(maximumBoundingArea);
            provider.setHandlePositions(HandlePositions.CORNERS);
            provider.setRatioReshapeRecognizer(IEventRecognizer.ALWAYS);
          } else if (Themes.PALETTE_PURPLE.equals(nodeTag)) {
            provider = new PurpleNodeReshapeHandleProvider(node, reshapeHandler);
          } else if (Themes.PALETTE58.equals(nodeTag)) {
            provider.setHandlePositions(HandlePositions.SOUTH_EAST);
            provider.setCenterReshapeRecognizer(IEventRecognizer.ALWAYS);
          } else if (Themes.PALETTE_LIGHTBLUE.equals(nodeTag)) {
            provider = new CyanNodeReshapeHandleProvider(node, reshapeHandler, applicationState);
          }
          return provider;
        });
  }

  /**
   * Initializes this demo by configuring the input mode and the model item lookup and creating an example graph
   * together with an enclosing rectangle some of the nodes may not stretch over.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    DemoStyles.initDemoStyles(graphControl.getGraph());

    // initialize the input mode
    initializeInputMode();

    // Create the rectangle that limits the movement of some nodes
    Rectangle boundaryRectangle = new Rectangle(20, 20, 480, 550);
    // and add it to the GraphControl using a black border and a transparent fill
    new Pen(Color.BLACK, 2).styleShape(boundaryRectangle);
    boundaryRectangle.setFill(Color.TRANSPARENT);
    graphControl.getRootGroup().addChild(boundaryRectangle, ICanvasObjectDescriptor.VISUAL);

    registerReshapeHandleProvider(boundaryRectangle);

    // initialize the graph
    createSampleGraph(graphControl.getGraph());

    // enable Undo/Redo for all edits after the initial graph has been constructed
    graphControl.getGraph().setUndoEngineEnabled(true);

    applicationState = new ApplicationState(graphControl, true);
  }

  private void initializeInputMode() {
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // do not allow for moving any graph items
    inputMode.setMovableItems(GraphItemTypes.NONE);
    // disable element creation and deletion
    inputMode.setCreateNodeAllowed(false);
    inputMode.setCreateEdgeAllowed(false);
    inputMode.setDeletableItems(GraphItemTypes.NONE);
    // disable label editing
    inputMode.setEditLabelAllowed(false);
    // or copy/paste
    inputMode.setClipboardOperationsAllowed(false);

    graphControl.setInputMode(inputMode);
  }

  /**
   * Centers the displayed content in the graph component.
   */
  public void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Creates the sample graph with four nodes. Each node has a different color that indicates which {@link
   * IReshapeHandleProvider} is used.
   */
  private void createSampleGraph(IGraph graph) {
    createNode(graph, 80, 100, 140, 30, Themes.PALETTE_RED, "Fixed Size");
    createNode(graph, 300, 100, 140, 30, Themes.PALETTE_GREEN, "Keep Aspect Ratio");
    createNode(graph, 80, 200, 140, 50, Themes.PALETTE58, "Keep Center");
    createNode(graph, 300, 200, 140, 50, Themes.PALETTE_PURPLE, "Keep Aspect Ratio\nat corners");
    createNode(graph, 80, 310, 140, 30, Themes.PALETTE_ORANGE, "Limited to Rectangle");
    createNode(graph, 300, 300, 140, 50, Themes.PALETTE_BLUE,
            "Limited to Rectangle\nand Keep Aspect Ratio");
    createNode(graph, 80, 400, 140, 50, Themes.PALETTE_LIGHTBLUE, "Keep Aspect Ratio\ndepending on State");
  }

  /**
   * Creates a sample node for this demo.
   */
  private static void createNode(IGraph graph, double x, double y, double w, double h, Palette palette, String labelText) {
    RectangleNodeStyle nodeStyle = DemoStyles.createDemoNodeStyle(palette);
    INode node = graph.createNode(new RectD(x, y, w, h), nodeStyle, palette);
    DefaultLabelStyle labelStyle = DemoStyles.createDemoNodeLabelStyle(palette);
    graph.addLabel(node, labelText, InteriorLabelModel.CENTER, labelStyle);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
