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
package input.orthogonaledges;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.EdgeDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IOrthogonalEdgeHelper;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeHelper;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;


/**
 * This Demo shows how orthogonal edge editing can be customized by implementing the
 * {@link IOrthogonalEdgeHelper} interface and modifying the Lookup of the edges in the graph
 * via the GraphDecorator mechanism.
 */
public class OrthogonalEdgesDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph. At the time this
   * method is called, all nodes in the scene graph are available. Most importantly, the GraphControl instance is
   * initialized.
   */
  public void initialize() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);

    // setup initial graph
    IGraph graph = graphControl.getGraph();
    initializeGraphDefaults(graph);
    createSampleGraph(graph);

    // and enable the undo feature.
    graph.setUndoEngineEnabled(true);

    // setup user interaction
    initializeInputModes();
  }

  private void initializeGraphDefaults(IGraph graph) {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);
  }

  /**
   * Creates and configures the {@link GraphEditorInputMode} to enable orthogonal edge editing.
   * Also configures the snapping behavior for this demo and finally registers the
   * {@link IOrthogonalEdgeHelper} that are the core of this demo.
   */
  private void initializeInputModes() {
    // create a default editor input mode
    GraphEditorInputMode editMode = new GraphEditorInputMode();

    // enable orthogonal edge editing
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    editMode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    // disable the interactive creation of nodes or edges to focus on the sample graph
    editMode.setCreateEdgeAllowed(false);
    editMode.setCreateNodeAllowed(false);
    // also restrict the interaction to edges and bends
    GraphItemTypes edgesOrBends = GraphItemTypes.EDGE.or(GraphItemTypes.BEND);
    editMode.setClickSelectableItems(edgesOrBends);
    editMode.setSelectableItems(edgesOrBends);
    // disable the moving of arbitrary elements, we handle the movement manually
    editMode.setMovableItems(GraphItemTypes.NONE);
    // disable focusing and deleting items
    editMode.setFocusableItems(GraphItemTypes.NONE);
    editMode.setDeletableItems(GraphItemTypes.NONE);
    // disable label editing
    editMode.setEditLabelAllowed(false);

    // enable snapping for edges only...
    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setCollectingNodeSnapLinesEnabled(false);
    snapContext.setCollectingNodePairCenterSnapLinesEnabled(false);
    snapContext.setCollectingNodePairSnapLinesEnabled(false);
    snapContext.setCollectingNodePairSegmentSnapLinesEnabled(false);
    snapContext.setCollectingNodeSizesEnabled(false);
    snapContext.setSnappingNodesToSnapLinesEnabled(false);
    snapContext.setSnappingOrthogonalMovementEnabled(false);
    editMode.setSnapContext(snapContext);

    registerOrthogonalEdgeHelperDecorators();

    // and finally register our input mode with the component.
    graphControl.setInputMode(editMode);
  }

  /**
   * Creates custom {@link IOrthogonalEdgeHelper}s and
   * registers them with the {@link EdgeDecorator} of the graph.
   * Additionally, it sets some other decorators that complete the desired behavior.
   */
  private void registerOrthogonalEdgeHelperDecorators() {
    EdgeDecorator edgeDecorator = graphControl.getGraph().getDecorator().getEdgeDecorator();

    // Add different OrthogonalEdgeHelpers to demonstrate various custom behaviours
    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Color.FIREBRICK, new RedOrthogonalEdgeHelper());

    // Green edges have the regular orthogonal editing behavior and therefore we don't need a custom implementation
    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Color.FORESTGREEN, new OrthogonalEdgeHelper());

    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Color.PURPLE, new PurpleOrthogonalEdgeHelper());
    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Color.DARKORANGE, new OrangeOrthogonalEdgeHelper());
    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Color.ROYALBLUE, new BlueOrthogonalEdgeHelper());

    // Disable moving of the complete edge for orthogonal edges since this would create way too many bends
    edgeDecorator.getPositionHandlerDecorator().hideImplementation(
        edge -> (edge.getTag() == Color.DARKORANGE) ||
            (edge.getTag() == Color.FORESTGREEN) ||
            (edge.getTag() == Color.PURPLE)
    );

    // Add a custom BendCreator for blue edges that ensures orthogonality
    // if a bend is added to the first or last (non-orthogonal) segment
    edgeDecorator.getBendCreatorDecorator().setImplementation(edge -> edge.getTag() == Color.ROYALBLUE, new BlueBendCreator());

    // Add a custom EdgePortHandleProvider to make the handles of purple and
    // orange edge move within the bounds of the node
    edgeDecorator.getEdgePortHandleProviderDecorator().setImplementationWrapper(edge -> edge.getTag() == Color.PURPLE,
        (edge, provider) -> new ConstrainedEdgePortHandleProvider(provider));
    edgeDecorator.getEdgePortHandleProviderDecorator().setImplementation(edge -> edge.getTag() == Color.DARKORANGE, new ConstrainedEdgePortHandleProvider());

    // Allow the relocating of an edge to another node
    edgeDecorator.getEdgeReconnectionPortCandidateProviderDecorator().setImplementation(
        IEdgeReconnectionPortCandidateProvider.ALL_NODE_CANDIDATES);
  }

  /**
   * Creates a graph with five orthogonal or partly orthogonal edges. The edges have different colors that are also
   * used as tag. That way, they can be treated differently during interactive change depending on their color.
   */
  private void createSampleGraph(IGraph graph) {
    createSubgraph(graph, Color.FIREBRICK, 0, false);
    createSubgraph(graph, Color.FORESTGREEN, 110, false);
    createSubgraph(graph, Color.PURPLE, 220, true);
    createSubgraph(graph, Color.DARKORANGE, 330, false);

    // the blue edge has more bends then the other edges
    IEdge blueEdge = createSubgraph(graph, Color.ROYALBLUE, 440, false);
    graph.clearBends(blueEdge);
    double sourcePortY = blueEdge.getSourcePort().getLocation().getY();
    graph.addBend(blueEdge, new PointD(220, sourcePortY - 30));
    graph.addBend(blueEdge, new PointD(300, sourcePortY - 30));
    double targetPortY = blueEdge.getTargetPort().getLocation().getY();
    graph.addBend(blueEdge, new PointD(300, targetPortY + 30));
    graph.addBend(blueEdge, new PointD(380, targetPortY + 30));

    graphControl.updateContentRect();
  }

  /**
   * Creates the sample graph of the given color with two nodes and a single edge.
   */
  private IEdge createSubgraph(IGraph graph, Color color, double yOffset, boolean createPorts) {
    // Create two nodes
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(color);
    INode n1 = graph.createNode(new RectD(110, 100 + yOffset, 40, 40), nodeStyle, color);
    INode n2 = graph.createNode(new RectD(450, 130 + yOffset, 40, 40), nodeStyle, color);

    // Create an edge, either between the two nodes or between the nodes' ports
    // For the edge style, use a pen based on the color that is a tiny bit thicker than the normal pen
    IEdge edge;
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(color, 1.5));
    if (!createPorts) {
      edge = graph.createEdge(n1, n2, edgeStyle, color);
    } else {
      IPort[] p1 = createSamplePorts(graph, n1, true);
      IPort[] p2 = createSamplePorts(graph, n2, false);
      edge = graph.createEdge(p1[1], p2[2], edgeStyle, color);
    }

    // Add bends that create a vertical segment in the middle of the edge
    PointD sourceLocation = edge.getSourcePort().getLocation();
    PointD targetLocation = edge.getTargetPort().getLocation();
    double x = (sourceLocation.getX() + targetLocation.getX()) / 2;
    graph.addBend(edge, new PointD(x, sourceLocation.getY()));
    graph.addBend(edge, new PointD(x, targetLocation.getY()));
    return edge;
  }

  /**
   * Adds some ports to the given node.
   */
  private static IPort[] createSamplePorts(IGraph graph, INode node, boolean toEastSide) {
    FreeNodePortLocationModel model = FreeNodePortLocationModel.INSTANCE;
    double x = toEastSide ? 0.9 : 0.1;
    IPort[] ports = new IPort[4];
    ports[0] = graph.addPort(node, model.createParameter(new PointD(x, 0.05), PointD.ORIGIN));
    ports[1] = graph.addPort(node, model.createParameter(new PointD(x, 0.35), PointD.ORIGIN));
    ports[2] = graph.addPort(node, model.createParameter(new PointD(x, 0.65), PointD.ORIGIN));
    ports[3] = graph.addPort(node, model.createParameter(new PointD(x, 0.95), PointD.ORIGIN));
    return ports;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
