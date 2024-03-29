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
package layout.partiallayout;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.GroupNodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.partial.ComponentAssignmentStrategy;
import com.yworks.yfiles.layout.partial.EdgeRoutingStrategy;
import com.yworks.yfiles.layout.partial.LayoutOrientation;
import com.yworks.yfiles.layout.partial.PartialLayout;
import com.yworks.yfiles.layout.partial.PartialLayoutData;
import com.yworks.yfiles.layout.partial.SubgraphPlacement;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.Themes;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;

/**
 * Shows how to apply the partial layout algorithm to a graph.
 */
public class PartialLayoutDemo extends DemoApplication {
  public WebView helpView;
  public GraphControl graphControl;

  /**
   * The core layout algorithm used for for subgraph placement (not-fixed or partial nodes).
   */
  private static final Algorithm SUBGRAPH_LAYOUT = Algorithm.Orthogonal;
  /**
   * Determines the placement strategy for subgraph components.
   */
  private static final SubgraphPlacement SUBGRAPH_PLACEMENT = SubgraphPlacement.BARYCENTER;
  /**
   * Determines how partial layout partitions nodes and edges into subgraph
   * components.
   */
  private static final ComponentAssignmentStrategy COMPONENT_ASSIGNMENT_STRATEGY = ComponentAssignmentStrategy.CONNECTED;
  /**
   * The edge routing strategy for edges routed by partial layout.
   * This includes partial edges and edges connecting different subgraph
   * components.
   */
  private static final EdgeRoutingStrategy EDGE_ROUTING_STRATEGY = EdgeRoutingStrategy.ORTHOGONAL;
  /**
   * The layout orientation for subgraph components.
   * {@link LayoutOrientation#AUTO_DETECT} specifies that the orientation should
   * be determined automatically.
   */
  private static final LayoutOrientation PARTIAL_LAYOUT_ORIENTATION = LayoutOrientation.TOP_TO_BOTTOM;
  /**
   * The minimum distance between two adjacent nodes.
   */
  private static final int MIN_NODE_DISTANCE = 5;
  /**
   * Determines if subgraph components may be mirrored to improve the layout quality.
   */
  private static final boolean MIRRORING = false;
  /**
   * Determines if the algorithm tries to align nodes with each other.
   */
  private static final boolean SNAPPING = false;


  /**
   * Specifies if a node is a partial node.
   * Partial layout may freely place nodes marked as partial.
   * All other nodes remain fixed.
   */
  private Mapper<INode, Boolean> partialNodesMapper;
  /**
   * Specifies if an edges is a partial edge or not.
   * Partial layout may freely route edges marked as partial.
   * All other edges remain fixed.
   * Note:
   * Edges connected to partial nodes are always considered to be partial edges.
   */
  private Mapper<IEdge, Boolean> partialEdgesMapper;

  /** The node style for fixed group nodes. */
  private GroupNodeStyle fixedGroupNodeStyle;
  /** The node style for partial group nodes. */
  private GroupNodeStyle partialGroupNodeStyle;
  /** The node style for fixed normal nodes. */
  private RectangleNodeStyle fixedNodeStyle;
  /** The node style for partial normal nodes. */
  private RectangleNodeStyle partialNodeStyle;
  /** The node style for fixed edges. */
  private PolylineEdgeStyle fixedEdgeStyle ;
  /** The node style for partial edges. */
  private PolylineEdgeStyle partialEdgeStyle;

  /**
   * Initializes the demo.
   */
  public void initialize() {
    // configure user interaction
    initializeInputMode();

    // creates mappers for storing partial/fixed state of graph elements
    initializeMappers();

    // configure default styles.
    initializeGraphDefaults();

    // load a sample GraphML file suitable for the current layout algorithm. 
    loadGraph();

    WebViewUtils.initHelp(helpView, this);
  }

  /**
   * Centers the graph in the graph control.
   */
  @Override
  protected void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Configures user interaction.
   * Adds listeners to initialize partial state of new graph elements and to
   * toggle partial state of graph elements on double-clicks.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setGroupingOperationsAllowed(true);
    geim.setEditLabelAllowed(false);

    // toggle the partial/fixed state of graph elements on double-click
    geim.addItemDoubleClickedListener(( sender, args ) -> {
      if (args.getItem() instanceof INode) {
        INode node = (INode) args.getItem();
        togglePartialNodeState(node);
        args.setHandled(true);
      }
      if (args.getItem() instanceof IEdge) {
        IEdge edge = (IEdge) args.getItem();
        togglePartialEdgeState(edge);
        args.setHandled(true);
      }
    });

    // mark new nodes as partial and add a default label
    geim.addNodeCreatedListener(( sender, args ) -> {
      INode node = args.getItem();
      IGraph graph = graphControl.getGraph();
      if (graph.isGroupNode(node)) {
        graph.addLabel(node, "Group");
      } else {
        graph.addLabel(node, String.valueOf(graph.getNodes().size()));
      }
      partialNodesMapper.setValue(node, true);
    });

    // mark new edges as partial
    geim.getCreateEdgeInputMode().addEdgeCreatedListener(( sender, args ) -> {
      partialEdgesMapper.setValue(args.getItem(), true);
    });

    graphControl.setInputMode(geim);
  }

  /**
   * Toggles the partial state for the given edge and updates its style,
   * and therefore the appearance in the graph.
   */
  private void togglePartialEdgeState( IEdge edge ) {
    boolean partial = !partialEdgesMapper.getValue(edge);
    partialEdgesMapper.setValue(edge, partial);
    updateEdgeStyle(edge);
  }

  /**
   * Toggles the partial state for the given node and updates its style,
   * and therefore the appearance in the graph.
   */
  private void togglePartialNodeState( INode node ) {
    boolean partial = !partialNodesMapper.getValue(node);
    partialNodesMapper.setValue(node, partial);
    updateNodeStyle(node);
  }

  /**
   * Creates and initializes the mappers used for storing the partial/fixed
   * state of nodes and edges.
   */
  private void initializeMappers() {
    IGraph graph = graphControl.getGraph();

    // initialize the data providers that determine if a graph element
    // is a partial element or a fixed element
    partialNodesMapper = graph.getMapperRegistry().createMapper(PartialLayout.AFFECTED_NODES_DPKEY);
    partialNodesMapper.setDefaultValue(false);
    partialEdgesMapper = graph.getMapperRegistry().createMapper(PartialLayout.AFFECTED_EDGES_DPKEY);
    partialEdgesMapper.setDefaultValue(false);
  }

  /**
   * Configures style defaults for partial/fixed nodes, edges and group nodes. Initializes the
   * data providers {@link #partialEdgesMapper} and {@link #partialNodesMapper}.
   */
  private void initializeGraphDefaults() {
    // configure default styles for normal nodes ...
    partialNodeStyle = DemoStyles.createDemoNodeStyle(Themes.PALETTE_ORANGE);

    fixedNodeStyle = DemoStyles.createDemoNodeStyle(Themes.PALETTE58);

    // ... and group nodes
    partialGroupNodeStyle = DemoStyles.createDemoGroupStyle(Themes.PALETTE12, false);

    fixedGroupNodeStyle = DemoStyles.createDemoGroupStyle(Themes.PALETTE12, false);

    // configure the default style for edges
    partialEdgeStyle = DemoStyles.createDemoEdgeStyle(new toolkit.Palette("#ff6c00", "#ff6c00", "#ff6c00", "#ff6c00", "#ff6c00"));

    fixedEdgeStyle = DemoStyles.createDemoEdgeStyle(Themes.PALETTE58);

    // configure the default style for labels
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setTextPaint(Color.WHITE);


    IGraph graph = graphControl.getGraph();
    graph.getNodeDefaults().setSize(new SizeD(60, 30));
    graph.getNodeDefaults().setStyle(partialNodeStyle);
    graph.getGroupNodeDefaults().setStyle(partialGroupNodeStyle);
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);
    graph.getEdgeDefaults().setStyle(partialEdgeStyle);
  }

  /**
   * Loads a sample graph suitable for the current layout algorithm.
   */
  private void loadGraph() {
    String res;
    switch (SUBGRAPH_LAYOUT) {
      case Orthogonal:
        res = "resources/orthogonal.graphml";
        break;
      case Organic:
        res = "resources/organic.graphml";
        break;
      case Hierarchic:
        res = "resources/hierarchic.graphml";
        break;
      default:
        res = "resources/circular.graphml";
    }

    //Read the file and add the data provider for the partial/fixed state
    GraphMLIOHandler reader = graphControl.getGraphMLIOHandler();
    reader.addInputMapper(INode.class, Boolean.class, "AffectedNodesDpKey", partialNodesMapper);
    reader.addInputMapper(IEdge.class, Boolean.class, "AffectedEdgesDpKey", partialEdgesMapper);
    try {
      graphControl.importFromGraphML(getClass().getResource(res));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // set styles for nodes and edges depending on their "partial" state
    IGraph graph = graphControl.getGraph();
    graph.getNodes().stream().forEach(this::updateNodeStyle);
    graph.getEdges().stream().forEach(this::updateEdgeStyle);
  }

  /**
   * Arranges the displayed graph with {@link PartialLayout}.
   */
  public void runLayout() {
    PartialLayout partialLayout = new PartialLayout();
    partialLayout.setCoreLayout(getSubgraphLayout(SUBGRAPH_LAYOUT));
    partialLayout.setComponentAssignmentStrategy(COMPONENT_ASSIGNMENT_STRATEGY);
    partialLayout.setSubgraphPlacement(SUBGRAPH_PLACEMENT);
    partialLayout.setEdgeRoutingStrategy(EDGE_ROUTING_STRATEGY);
    partialLayout.setLayoutOrientation(PARTIAL_LAYOUT_ORIENTATION);
    partialLayout.setMinimumNodeDistance(MIN_NODE_DISTANCE);
    partialLayout.setMirroringAllowed(MIRRORING);
    partialLayout.setNodeAlignmentConsiderationEnabled(SNAPPING);

    PartialLayoutData partialLayoutData = new PartialLayoutData();
    partialLayoutData.setAffectedEdges(partialEdgesMapper);
    partialLayoutData.setAffectedNodes(partialNodesMapper);
    graphControl.morphLayout(partialLayout, Duration.ofMillis(500), partialLayoutData);
  }

  /**
   * Updates the style of the given edge depending on its
   * {@link #partialEdgesMapper partial} state.
   */
  private void updateEdgeStyle( IEdge edge ) {
    boolean partial = partialEdgesMapper.getValue(edge).booleanValue();
    graphControl.getGraph().setStyle(edge, partial ? partialEdgeStyle : fixedEdgeStyle);
  }

  /**
   * Updates the style of the given node depending on its
   * {@link #partialNodesMapper} state.
   */
  private void updateNodeStyle( INode node ) {
    boolean partial = partialNodesMapper.getValue(node).booleanValue();
    IGraph graph = graphControl.getGraph();
    if (graph.isGroupNode(node)) {
      graph.setStyle(node, partial ? partialGroupNodeStyle : fixedGroupNodeStyle);
    } else {
      graph.setStyle(node, partial ? partialNodeStyle : fixedNodeStyle);
    }
  }


  /**
   * Creates a pre-configured layout algorithm instance of the given type.
   */
  private ILayoutAlgorithm getSubgraphLayout( Algorithm algorithm ) {
    switch (algorithm) {
      case Hierarchic:
        HierarchicLayout hierarchicLayout = new HierarchicLayout();
        hierarchicLayout.setMinimumLayerDistance(MIN_NODE_DISTANCE);
        hierarchicLayout.setNodeToNodeDistance(MIN_NODE_DISTANCE);
        return hierarchicLayout;
      case Orthogonal:
        OrthogonalLayout orthogonalLayout = new OrthogonalLayout();
        orthogonalLayout.setGridSpacing(MIN_NODE_DISTANCE);
        return orthogonalLayout;
      case Organic:
        OrganicLayout organicLayout = new OrganicLayout();
        organicLayout.setMinimumNodeDistance(MIN_NODE_DISTANCE);
        return organicLayout;
      default: // Circular:
        CircularLayout circularLayout = new CircularLayout();
        circularLayout.getSingleCycleLayout().setMinimumNodeDistance(MIN_NODE_DISTANCE);
        circularLayout.getBalloonLayout().setMinimumNodeDistance(MIN_NODE_DISTANCE);
        return circularLayout;
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Layout algorithms for arranging partial layout's subgraph components.
   */
  private enum Algorithm {
    Hierarchic,
    Orthogonal,
    Organic,
    Circular
  }
}
