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
package input.portcandidateprovider;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortCandidateValidity;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.Palette;
import toolkit.Themes;
import toolkit.WebViewUtils;

/**
 * Demo code that shows how to customize the port relocation feature
 * by implementing a custom {@link IPortCandidateProvider}.
 */
public class PortCandidateProviderDemo extends DemoApplication {

  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);

    // Initialize the input mode
    initializeInputMode();

    // Disable automatic cleanup of unconnected ports since some nodes have a predefined set of ports
    IGraph graph = graphControl.getGraph();
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);
    // set a port style that makes the pre-defined ports visible
    ShapeNodeStyle roundNodeStyle = new ShapeNodeStyle();
    roundNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    roundNodeStyle.setPaint(Color.BLACK);
    roundNodeStyle.setPen(null);
    NodeStylePortStyleAdapter simplePortStyle = new NodeStylePortStyleAdapter(roundNodeStyle);
    simplePortStyle.setRenderSize(new SizeD(3, 3));
    graph.getNodeDefaults().getPortDefaults().setStyle(simplePortStyle);
    // enable the undo feature
    graph.setUndoEngineEnabled(true);

    // register custom provider implementations
    registerPortCandidateProvider();

    // initialize the graph
    createSampleGraph();
  }

  private void initializeInputMode() {
    // set up our InputMode for this demo.
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // disable the interactive creation of nodes to focus on the sample graph
    inputMode.setCreateNodeAllowed(false);
    // also restrict the interaction to edges and bends
    GraphItemTypes edgesOrBends = GraphItemTypes.EDGE.or(GraphItemTypes.BEND);
    inputMode.setClickSelectableItems(edgesOrBends);
    inputMode.setSelectableItems(edgesOrBends);
    inputMode.setDeletableItems(edgesOrBends);
    // disable focusing of items
    inputMode.setFocusableItems(GraphItemTypes.NONE);
    // disable label editing
    inputMode.setEditLabelAllowed(false);
    // Finally, set the input mode to the graph control.
    graphControl.setInputMode(inputMode);
  }

  /**
   * Constructs a sample graph that contains nodes that demonstrates each of the custom {@link
   * IPortCandidateProvider} above.
   */
  private void createSampleGraph() {
    IGraph graph = graphControl.getGraph();
    DemoStyles.initDemoStyles(graph);

    createNode(graph, 100, 100, 80, 30, Themes.PALETTE_RED, "No Edge");
    createNode(graph, 350, 100, 80, 30, Themes.PALETTE_GREEN, "Green Only");
    createNode(graph, 100, 200, 80, 30, Themes.PALETTE_GREEN, "Green Only");
    createNode(graph, 350, 200, 80, 30, Themes.PALETTE_RED, "No Edge");

    // The blue nodes have predefined ports
    IPortStyle portStyle = new ColorPortStyle();

    INode blue1 = createNode(graph, 100, 300, 80, 30, Themes.PALETTE_LIGHTBLUE, "One   Port");
    graph.addPort(blue1, blue1.getLayout().getCenter(), portStyle).setTag(Color.BLACK);

    INode blue2 = createNode(graph, 350, 275, 100, 100, Themes.PALETTE_LIGHTBLUE, "Many Ports");
    // pre-define a bunch of ports at the outer border of one of the blue nodes
    AbstractPortCandidateProvider portCandidateProvider = IPortCandidateProvider.fromShapeGeometry(blue2, 0, 0.25, 0.5, 0.75);
    portCandidateProvider.setStyle(portStyle);
    portCandidateProvider.setTag(Color.BLACK);
    Iterable<IPortCandidate> candidates = portCandidateProvider.getSourcePortCandidates(
            graphControl.getInputModeContext());
    candidates.forEach(portCandidate -> {
      if (portCandidate.getValidity() != PortCandidateValidity.DYNAMIC) {
        portCandidate.createPort(graphControl.getInputModeContext());
      }
    });

    // The orange node
    createNode(graph, 100, 400, 100, 100, Themes.PALETTE_ORANGE, "Dynamic Ports");

    INode n = createNode(graph, 100, 540, 100, 100, Themes.PALETTE_PURPLE, "Individual\nPort\nConstraints");
    addIndividualPorts(graph, n);

    INode n2 = createNode(graph, 350, 540, 100, 100, Themes.PALETTE_PURPLE, "Individual\nPort\nConstraints");
    addIndividualPorts(graph, n2);

    // The olive node
    createNode(graph, 350, 410, 100, 80, Themes.PALETTE43, "No\nParallel\nEdges");
  }

  /**
   * Convenience method to create a node with a label.
   */
  private INode createNode(IGraph graph, double x, double y, double w, double h, Palette palette, String labelText) {
    RectangleNodeStyle nodeStyle = DemoStyles.createDemoNodeStyle(palette);
    INode node = graph.createNode(new RectD(x, y, w, h), nodeStyle, palette);
    graph.addLabel(node, labelText, InteriorLabelModel.CENTER, DemoStyles.createDemoNodeLabelStyle(palette));
    return node;
  }

  /**
   * Adds ports with different colors to the node.
   */
  private void addIndividualPorts(IGraph graph, INode node) {
    IPortStyle portStyle = new ColorPortStyle();
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.25, 0), PointD.ORIGIN), portStyle, Themes.PALETTE_RED);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.75, 0), PointD.ORIGIN), portStyle, Themes.PALETTE_GREEN);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0, 0.25), PointD.ORIGIN), portStyle, Color.BLACK);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0, 0.75), PointD.ORIGIN), portStyle, Color.BLACK);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(1, 0.25), PointD.ORIGIN), portStyle, Themes.PALETTE_LIGHTBLUE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(1, 0.75), PointD.ORIGIN), portStyle, Themes.PALETTE_ORANGE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.25, 1), PointD.ORIGIN), portStyle, Themes.PALETTE_PURPLE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.75, 1), PointD.ORIGIN), portStyle, Themes.PALETTE_PURPLE);
  }

  /**
   * Registers a callback function as decorator that provides a custom
   * {@link IPortCandidateProvider} for each node.
   * This callback function is called whenever a node in the graph is queried
   * for its <code>IPortCandidateProvider</code>. In this case, the 'node'
   * parameter will be assigned that node.
   */
  private void registerPortCandidateProvider() {
    NodeDecorator nodeDecorator = graphControl.getGraph().getDecorator().getNodeDecorator();
    nodeDecorator.getPortCandidateProviderDecorator().setFactory(
            node -> {
              // obtain the tag from the node
              Object nodeTag = node.getTag();

              // see if it is a known tag
              if (nodeTag instanceof Palette) {
                // and decide what implementation to provide
                if (Themes.PALETTE_RED.equals(nodeTag)) {
                  return new RedPortCandidateProvider(node);
                } else if (Themes.PALETTE_LIGHTBLUE.equals(nodeTag)) {
                  return new BluePortCandidateProvider(node);
                } else if (Themes.PALETTE_GREEN.equals(nodeTag)) {
                  return new GreenPortCandidateProvider(node);
                } else if (Themes.PALETTE_ORANGE.equals(nodeTag)) {
                  return new OrangePortCandidateProvider(node);
                } else if (Themes.PALETTE_PURPLE.equals(nodeTag)) {
                  return new PurplePortCandidateProvider(node);
                } else if (Themes.PALETTE43.equals(nodeTag)) {
                  return new OlivePortCandidateProvider(node);
                }
              }
              // otherwise, revert to default behavior
              return null;
            });
  }

  public static void main(String[] args) {
    launch(args);
  }
}