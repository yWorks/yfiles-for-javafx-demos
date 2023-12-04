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
package layout.edgebundling;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.CurveFittingLayoutStage;
import com.yworks.yfiles.layout.EdgeBundleDescriptor;
import com.yworks.yfiles.layout.EdgeBundling;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.MultiStageLayout;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.circular.CircularLayoutData;
import com.yworks.yfiles.layout.circular.LayoutStyle;
import com.yworks.yfiles.layout.radial.RadialLayout;
import com.yworks.yfiles.layout.radial.RadialLayoutData;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.DefaultNodePlacer;
import com.yworks.yfiles.layout.tree.RoutingStyle;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.layout.tree.TreeReductionStageData;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import java.net.URL;
import java.time.Duration;


/**
 * Demonstrates edge bundling for different layout styles.
 */
public class EdgeBundlingDemo extends DemoApplication {

  /**
   * Determines the layout style used for demonstrating edge bundling.
   */
  private static final LayoutAlgorithm selectedAlgorithm = LayoutAlgorithm.SINGLE_CYCLE;

  /** Determines the bundling strength. */
  private static final double bundlingStrength = 0.98;

  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the demo.
   */
  public void initialize() {
    // reads and shows "resources/help.html"
    WebViewUtils.initHelp(helpView, this);

    // initialize user interaction support
    createInputMode();

    // initialize the graph
    initializeGraph();
  }

  /**
   * Loads and arranges a sample graph.
   */
  @Override
  protected void onLoaded() {
    // load the sample graph and run the layout
    loadSample();
  }

  /**
   * Creates the input mode.
   */
  private void createInputMode() {
    GraphViewerInputMode mode = new GraphViewerInputMode();
    mode.setFocusableItems(GraphItemTypes.NONE);

    mode.getItemHoverInputMode().setHoverItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    mode.getItemHoverInputMode().setInvalidItemsDiscardingEnabled(false);
    mode.getItemHoverInputMode().addHoveredItemChangedListener(( sender, args ) -> {
      IModelItem item = args.getItem();
      HighlightIndicatorManager<IModelItem> highlightIndicatorManager = graphControl.getHighlightIndicatorManager();
      highlightIndicatorManager.clearHighlights();
      if (item != null) {
        highlightIndicatorManager.addHighlight(item);
        if (item instanceof INode) {
          for (IEdge edge : graphControl.getGraph().edgesAt((INode) item)) {
            highlightIndicatorManager.addHighlight(edge);
          }
        } else if (item instanceof IEdge) {
          IEdge edge = (IEdge) item;
          highlightIndicatorManager.addHighlight(edge.getSourceNode());
          highlightIndicatorManager.addHighlight(edge.getTargetNode());
        }
      }
    });

    graphControl.setInputMode(mode);
  }

  /**
   * Sets the default styles for the graph elements and initializes the highlight.
   */
  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();

    //set the node and edge default styles
    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    nodeStyle.setPaint(Color.DARKORANGE);
    nodeStyle.setPen(null);
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getEdgeDefaults().setStyle(new DemoEdgeStyle());

    // hide the selection decorator
    graph.getDecorator().getNodeDecorator().getSelectionDecorator().hideImplementation();
    graph.getDecorator().getEdgeDecorator().getSelectionDecorator().hideImplementation();

    // initialize the edge highlight manager
    DemoHighlightManager highlightManager = new DemoHighlightManager();
    graphControl.setHighlightIndicatorManager(highlightManager);

    // when a node is selected, select also the adjacent edges
    graphControl.getSelection().addItemSelectionChangedListener(( sender, args ) -> {
      IModelItem item = args.getItem();
      IGraphSelection selection = graphControl.getSelection();
      if (item instanceof INode && args.isItemSelected()) {
        selection.setSelected(item, true);
        graph.edgesAt((INode) item).forEach(edge -> selection.setSelected(edge, true));
      }
    });
  }

  /**
   * Loads a sample graph structure and arranges it using a suitable layout
   * style.
   */
  private void loadSample() {
    String resource;
    switch (selectedAlgorithm) {
      default:
      case SINGLE_CYCLE:
        resource = "resources/circular.graphml";
        break;
      case CIRCULAR:
        resource = "resources/bccCircular.graphml";
        break;
      case RADIAL:
        resource = "resources/radial.graphml";
        break;
      case BALLOON:
        resource = "resources/balloon.graphml";
        break;
      case TREE:
        resource = "resources/tree.graphml";
        break;
    }
    loadGraph(graphControl.getGraph(), resource);
    graphControl.fitGraphBounds();

    runLayout();
  }

  /**
   * Parses the GraphML and creates the graph elements.
   */
  private void loadGraph( IGraph graph, String resource ) {
    graph.clear();

    URL url = getClass().getResource(resource);
    if (url != null) {
      try {
        new GraphMLIOHandler().read(graph, url);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Runs the layout.
   */
  private void runLayout() {
    MultiStageLayout layoutAlgorithm;
    LayoutData layoutData;
    switch (selectedAlgorithm) {
      default:
      case SINGLE_CYCLE:
        layoutAlgorithm = createCircularLayout(true);
        layoutData = new CircularLayoutData();
        ((CircularLayoutData) layoutData).setCircleIds(new Mapper<>());
        break;
      case CIRCULAR:
        layoutAlgorithm = createCircularLayout(false);
        layoutData = new CircularLayoutData();
        ((CircularLayoutData) layoutData).setCircleIds(new Mapper<>());
        break;
      case RADIAL:
        layoutAlgorithm = createRadialLayout();
        layoutData = new RadialLayoutData();
        break;
      case BALLOON:
        layoutAlgorithm = createBalloonLayout();
        layoutData = new TreeReductionStageData();
        break;
      case TREE:
        layoutAlgorithm = createTreeLayout();
        layoutData = new TreeReductionStageData();
        break;
    }

    // to apply bezier fitting, append the CurveFittingLayoutStage to the layout algorithm
    // we could also enable the bezier fitting from the edge bundling descriptor but, we would like for this demo to
    // have small error
    CurveFittingLayoutStage curveFittingStage = new CurveFittingLayoutStage();
    curveFittingStage.setMaximumError(1);
    layoutAlgorithm.prependStage(curveFittingStage);

    graphControl.morphLayout(layoutAlgorithm, Duration.ZERO, layoutData);
  }


  /**
   * Creates and configures the circular layout algorithm.
   */
  private static CircularLayout createCircularLayout( boolean singleCycle ) {
    CircularLayout circularLayout = new CircularLayout();
    if (singleCycle) {
      circularLayout.setLayoutStyle(LayoutStyle.SINGLE_CYCLE);
      circularLayout.getSingleCycleLayout().setMinimumNodeDistance(0);
    }
    circularLayout.setLabelingEnabled(true);
    configureEdgeBundling(circularLayout.getEdgeBundling());
    return circularLayout;
  }

  /**
   * Creates and configures the radial layout algorithm.
   */
  private static RadialLayout createRadialLayout() {
    RadialLayout radialLayout = new RadialLayout();
    radialLayout.setLabelingEnabled(true);
    configureEdgeBundling(radialLayout.getEdgeBundling());
    return radialLayout;
  }

  /**
   * Creates and configures the balloon layout algorithm.
   */
  private static BalloonLayout createBalloonLayout() {
    BalloonLayout balloonLayout = new BalloonLayout();
    balloonLayout.setIntegratedEdgeLabelingEnabled(true);
    balloonLayout.setIntegratedNodeLabelingEnabled(true);
    TreeReductionStage treeReductionStage = createTreeReductionStage();
    configureEdgeBundling(treeReductionStage.getEdgeBundling());
    balloonLayout.prependStage(treeReductionStage);
    return balloonLayout;
  }

  /**
   * Creates and configures the tree layout algorithm.
   */
  private static TreeLayout createTreeLayout() {
    DefaultNodePlacer nodePlacer = new DefaultNodePlacer();
    nodePlacer.setRoutingStyle(RoutingStyle.STRAIGHT);
    TreeLayout treeLayout = new TreeLayout();
    treeLayout.setDefaultNodePlacer(nodePlacer);
    treeLayout.setNodeLabelConsiderationEnabled(true);
    treeLayout.setIntegratedEdgeLabelingEnabled(true);
    TreeReductionStage treeReductionStage = createTreeReductionStage();
    configureEdgeBundling(treeReductionStage.getEdgeBundling());
    treeLayout.prependStage(treeReductionStage);
    return treeLayout;
  }

  /**
   * Creates and configures the tree reduction stage.
   */
  private static TreeReductionStage createTreeReductionStage() {
    TreeReductionStage treeReductionStage = new TreeReductionStage();
    treeReductionStage.setNonTreeEdgeRouter(new OrganicEdgeRouter());
    treeReductionStage.setNonTreeEdgeSelectionKey(OrganicEdgeRouter.AFFECTED_EDGES_DPKEY);
    return treeReductionStage;
  }

  /**
   * Configures the edge bundling descriptor.
   */
  private static void configureEdgeBundling( EdgeBundling bundling ) {
    EdgeBundleDescriptor bundlingDescriptor = new EdgeBundleDescriptor();
    bundlingDescriptor.setBundled(true);
    // we could either enable here the bezier fitting or append the CurveFittingLayoutStage to our layout algorithm
    // if we would like to adjust the approximation error
    // bundlingDescriptor.setBezierFittingEnabled(true);
    bundling.setBundlingStrength(bundlingStrength);
    bundling.setDefaultBundleDescriptor(bundlingDescriptor);
  }


  public static void main( String[] args ) {
    launch(args);
  }


  /**
   * Layout styles that support edge bundling.
   */
  private enum LayoutAlgorithm {
    SINGLE_CYCLE,
    CIRCULAR,
    RADIAL,
    BALLOON,
    TREE;
  }
}
