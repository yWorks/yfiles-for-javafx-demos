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
package input.edgereconnection;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.ITagOwner;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortCandidateValidity;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;


/**
 * Demo code that shows how to customize the reconnection behavior for existing edges in the graph by implementing a
 * custom {@link IEdgeReconnectionPortCandidateProvider}.
 */
public class EdgePortCandidateProviderDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    // set up our InputMode for this demo.
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // we don't want to create new nodes or edges because they wouldn't have any functionality.
    inputMode.setCreateNodeAllowed(false);
    inputMode.setCreateEdgeAllowed(false);
    // also disable deleting items
    inputMode.setDeletableItems(GraphItemTypes.NONE);
    // also restrict the interaction to edges and bends
    GraphItemTypes edgesOrBends = GraphItemTypes.EDGE.or(GraphItemTypes.BEND);
    inputMode.setClickSelectableItems(edgesOrBends);
    inputMode.setSelectableItems(edgesOrBends);
    inputMode.setFocusableItems(GraphItemTypes.NONE);
    // disable label editing
    inputMode.setEditLabelAllowed(false);
    graphControl.setInputMode(inputMode);

    IGraph graph = graphControl.getGraph();

    // switch automatic cleanup of unconnected ports off so we can move them around more freely.
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);
    // set a port style that makes the pre-defined ports visible.
    ShapeNodeStyle roundNodeStyle = new ShapeNodeStyle();
    roundNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    roundNodeStyle.setPaint(Color.BLACK);
    roundNodeStyle.setPen(null);
    NodeStylePortStyleAdapter simplePortStyle = new NodeStylePortStyleAdapter(roundNodeStyle);
    simplePortStyle.setRenderSize(new SizeD(3, 3));
    graph.getNodeDefaults().getPortDefaults().setStyle(simplePortStyle);

    // initialize the graph
    createSampleGraph(graph);

    // register custom provider implementations
    registerEdgeReconnectionPortCandidateProvider();
  }

  /**
   * Creates the sample graph. Each node has a different color that indicates which {@link
   * IEdgeReconnectionPortCandidateProvider} is used.
   */
  private void createSampleGraph(IGraph graph) {
    ShapeNodeStyle roundNodeStyle = new ShapeNodeStyle();
    roundNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    roundNodeStyle.setPaint(Color.BLACK);
    roundNodeStyle.setPen(null);
    NodeStylePortStyleAdapter blackPortStyle = new NodeStylePortStyleAdapter(roundNodeStyle);
    blackPortStyle.setRenderSize(new SizeD(3, 3));

    createSubgraph(graph, Color.FIREBRICK, 0);
    createSubgraph(graph, Color.DARKORANGE, 200);
    createSubgraph(graph, Color.FORESTGREEN, 600);

    // the blue nodes have some additional ports besides the ones used by the edge
    INode[] nodes = createSubgraph(graph, Color.ROYALBLUE, 400);
    graph.addPort(nodes[0], FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(1, 0.2), PointD.ORIGIN), blackPortStyle);
    graph.addPort(nodes[0], FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(1, 0.8), PointD.ORIGIN), blackPortStyle);

    // pre-define some port candidates for one of the blue nodes.
    AbstractPortCandidateProvider provider = IPortCandidateProvider.fromShapeGeometry(nodes[2], 0, 0.25, 0.5, 0.75);
    provider.setStyle(blackPortStyle);
    Iterable<IPortCandidate> candidates = provider.getSourcePortCandidates(graphControl.getInputModeContext());
    candidates.forEach(portCandidate -> {
      if (portCandidate.getValidity() != PortCandidateValidity.DYNAMIC) {
        portCandidate.createPort(graphControl.getInputModeContext());
      }
    });
  }

  /**
   * Creates a couple of nodes with the same color at different locations and connect two of them with an edge.
   * @param graph  the graph containing the node
   * @param color  the color to fill the node with
   * @param yOffset the offset by which the positions of the nodes should be shifted
   */
  private static INode[] createSubgraph(IGraph graph, Color color, double yOffset) {
    ShinyPlateNodeStyle style = new ShinyPlateNodeStyle();
    style.setPaint(color);
    INode n1 = graph.createNode(new RectD(100, 100 + yOffset, 60, 60), style, color);
    INode n2 = graph.createNode(new RectD(500, 100 + yOffset, 60, 60), style, color);
    INode n3 = graph.createNode(new RectD(300, 160 + yOffset, 60, 60), style, color);

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(color, 1.5));
    graph.createEdge(n1, n2, edgeStyle, color);
    return new INode[]{n1, n2, n3};
  }

  /**
   * Registers a callback function as decorator that provides a custom
   * {@link IEdgeReconnectionPortCandidateProvider} for each node.
   * This callback function is called whenever a node in the graph is queried
   * for its <code>IEdgePortCandidateProvider</code>. In this case, the 'node'
   * parameter will be set to that node.
   */
  private void registerEdgeReconnectionPortCandidateProvider() {
    graphControl.getGraph().getDecorator().getEdgeDecorator().getEdgeReconnectionPortCandidateProviderDecorator().setFactory(this::createEdgeReconnectionPortCandidateProvider);
  }

  /**
   * A factory method that can be used for the lookup chain for IEdges to provide custom IEdgePortCandidateProvider
   * implementations to the nodes depending on the color stored in their {@link ITagOwner#getTag()}.
   * @param edge the edge to create an IEdgePortCandidateProvider for
   */
  private IEdgeReconnectionPortCandidateProvider createEdgeReconnectionPortCandidateProvider(IEdge edge) {
    // obtain the tag from the edge
    Object edgeTag = edge.getTag();

    // see if it is a known tag
    if (edgeTag instanceof Color) {
      // and decide what implementation to provide
      if (Color.FIREBRICK.equals(edgeTag)) {
        return new RedEdgeReconnectionPortCandidateProvider(edge);
      } else if (Color.DARKORANGE.equals(edgeTag)) {
        return new OrangeReconnectionEdgePortCandidateProvider(edge);
      } else if (Color.ROYALBLUE.equals(edgeTag)) {
        return new BlueEdgeReconnectionPortCandidateProvider();
      } else if (Color.FORESTGREEN.equals(edgeTag)) {
        return new GreenEdgeReconnectionPortCandidateProvider();
      }
    }
    // otherwise revert to default behavior
    return null;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
