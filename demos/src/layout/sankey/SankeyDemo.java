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
package layout.sankey;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.styles.BezierEdgeStyle;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.layout.AbstractLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LayoutEventArgs;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.input.ConstrainedPositionHandler;
import com.yworks.yfiles.view.input.GraphEditorInputMode;

import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;


/**
 * Uses {@link com.yworks.yfiles.layout.hierarchic.HierarchicLayout} to arrange Sankey diagrams.
 */
public class SankeyDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;
  public Button layoutButton;

  /** Whether or not the layout algorithm should use the existing drawing as sketch. */
  private boolean fromSketchEnabled = true;

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // reads and shows a help text
    WebViewUtils.initHelp(helpView, this);

    // initialize user interaction support
    createInputMode();

    // only allow vertical moving of nodes and no resizing
    configureNodeMovementAndResize();

    // initialize the graph
    initializeGraph();
  }

  /**
   * Loads an example graph.
   */
  private void initializeGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/voter-migration.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates the input mode.
   */
  private void createInputMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    mode.setEditLabelAllowed(false);
    mode.setCreateNodeAllowed(false);
    mode.setCreateEdgeAllowed(false);
    mode.setSelectableItems(GraphItemTypes.NODE);
    mode.setMarqueeSelectableItems(GraphItemTypes.NONE);

    mode.getMoveInputMode().addDragFinishedListener((dragSource, dragArgs) -> {
      layoutButton.setDisable(true);
      applyLayout((eventSource, eventArgs) -> layoutButton.setDisable(false));
    });

    graphControl.setInputMode(mode);
  }

  /**
   * Prevent resizing of nodes and only allow movement along the y-axis.
   */
  private void configureNodeMovementAndResize() {
    NodeDecorator nodeDecorator = graphControl.getGraph().getDecorator().getNodeDecorator();
    nodeDecorator.getReshapeHandleProviderDecorator().setImplementationWrapper((node, delegateProvider) -> new NoReshapeHandleProvider());
    nodeDecorator.getPositionHandlerDecorator().setImplementationWrapper((node, delegateHandler) -> new VerticalPositionHandler(delegateHandler));
  }

  /**
   * Adjusts the view on the first start of the demo.
   */
  @Override
  protected void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Toggles between whether or not the layout algorithm should use the
   * existing drawing as sketch.
   */
  public void toggleFromSketchMode(ActionEvent event ) {
    fromSketchEnabled = !fromSketchEnabled;
  }

  /**
   * Runs a layout calculation.
   */
  public void applyLayout( ActionEvent event ) {
    Button btn = (Button) event.getSource();

    // deactivate the control for running the layout algorithm to prevent
    // concurrent layout calculations
    btn.setDisable(true);
    applyLayout(( source, args ) -> {
      // activate the control for running the layout algortihm once again
      // when the previous calculation has finished
      btn.setDisable(false);

      graphControl.fitGraphBounds();
    });
  }

  /**
   * Calculates and applies a layout in an animated fashion.
   */
  private void applyLayout( IEventHandler<LayoutEventArgs> onLayoutFinished ) {
    IGraph graph = graphControl.getGraph();

    // edge thickness is determined by the pen thickness stored in the edge styles
    IMapper<IEdge, Double> thicknessMapper = new Mapper<>();
    for (IEdge edge : graph.getEdges()) {
      if (edge.getStyle() instanceof BezierEdgeStyle) {
        double thickness = ((BezierEdgeStyle) edge.getStyle()).getPen().getThickness();
        thicknessMapper.setValue(edge, thickness);
      }
    }

    // configure the layout algorithm
    HierarchicLayout layout = new HierarchicLayout();
    LayoutOrientation orientation = LayoutOrientation.LEFT_TO_RIGHT;
    layout.setLayoutOrientation(orientation);
    layout.setLayoutMode(fromSketchEnabled ? LayoutMode.INCREMENTAL : LayoutMode.FROM_SCRATCH);
    layout.setNodeToNodeDistance(50);
    layout.getEdgeLayoutDescriptor().setMinimumFirstSegmentLength(100);
    layout.getEdgeLayoutDescriptor().setMinimumLastSegmentLength(100);

    // create the layout data
    HierarchicLayoutData layoutData = new HierarchicLayoutData();
    layoutData.setEdgeThickness(thicknessMapper);

    // a port border gap ratio of zero means that ports can be placed directly on the corners of the nodes
    double portBorderRatio = 0;
    layout.getNodeLayoutDescriptor().setPortBorderGapRatios(portBorderRatio);

    // configures the generic labeling algorithm which produces more compact results, here
    GenericLabeling labelLayout = (GenericLabeling) layout.getLabeling();
    labelLayout.setNodeLabelPlacementEnabled(false);
    labelLayout.setNodeOverlapsRemovalEnabled(true);
    layout.setLabelingEnabled(true);

    // all edge labels should be placed close to the edges' source
    PreferredPlacementDescriptor placementDescriptor = new PreferredPlacementDescriptor();
    placementDescriptor.setPlaceAlongEdge(LabelPlacements.AT_SOURCE);
    graph.getMapperRegistry().createConstantMapper(
            ILabel.class, PreferredPlacementDescriptor.class,
            LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY,
            placementDescriptor);

    // for Sankey diagrams, the nodes should be adjusted to the incoming/outgoing flow (enlarged if necessary)
    // -> use NodeResizingStage for that purpose
    NodeResizingStage nodeResizingStage = new NodeResizingStage(layout);
    nodeResizingStage.setLayoutOrientation(orientation);
    nodeResizingStage.setPortBorderGapRatio(portBorderRatio);

    // run the layout algorithm and animate the result
    LayoutExecutor executor = new LayoutExecutor(graphControl, nodeResizingStage);
    executor.addLayoutFinishedListener(( source, args ) ->
            graph.getMapperRegistry().removeMapper(
                    LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY));
    executor.addLayoutFinishedListener(onLayoutFinished);
    executor.setViewportAnimationEnabled(false);
    executor.setEasedAnimationEnabled(true);
    executor.setRunningInThread(true);
    executor.setContentRectUpdatingEnabled(true);
    executor.setDuration(Duration.ofMillis(500));
    executor.setLayoutData(layoutData);
    executor.start();
  }

  /**
   * Layout stage that ensures that the size of the nodes is large enough for
   * all edges to be placed without overlaps.
   */
  private static class NodeResizingStage extends AbstractLayoutStage {
    private double minimumPortDistance;
    private double portBorderGapRatio;
    private LayoutOrientation layoutOrientation;

    NodeResizingStage( ILayoutAlgorithm coreLayout ) {
      super(coreLayout);
    }

    /**
     * Returns the main orientation of the layout. Should be the same value as for the associated core layout
     * algorithm.
     */
    public LayoutOrientation getLayoutOrientation() {
      return layoutOrientation;
    }

    /**
     * Specifies the main orientation of the layout. Should be the same value as for the associated core layout
     * algorithm.
     * @param layoutOrientation one of the default layout orientations
     */
    public void setLayoutOrientation( LayoutOrientation layoutOrientation ) {
      this.layoutOrientation = layoutOrientation;
    }

    /**
     * Returns the port border gap ratio for the port distribution at the sides of the nodes.
     * Should be the same value as for the associated core layout algorithm.
     */
    public double getPortBorderGapRatio() {
      return portBorderGapRatio;
    }

    /**
     * Specifies the port border gap ratio for the port distribution at the sides of the nodes. Should be the same value
     * as for the associated core layout algorithm.
     * @param portBorderGapRatio the given ratio
     */
    public void setPortBorderGapRatio( double portBorderGapRatio ) {
      this.portBorderGapRatio = portBorderGapRatio;
    }

    /**
     * Returns the minimum distance between two ports on the same node side.
     */
    public double getMinimumPortDistance() {
      return minimumPortDistance;
    }

    /**
     * Specifies the minimum distance between two ports on the same node side.
     * @param minimumPortDistance the minimum distance
     */
    public void setMinimumPortDistance( double minimumPortDistance ) {
      this.minimumPortDistance = minimumPortDistance;
    }

    @Override
    public void applyLayout( LayoutGraph graph ) {
      for (Node node : graph.getNodes()) {
        //adjust the node size
        adjustNodeSizes(node, graph);
      }

      applyLayoutCore(graph);
    }

    private void adjustNodeSizes( Node node, LayoutGraph graph ) {
      double width = graph.getWidth(node);
      double height = graph.getHeight(node);

      double inEdgeSpace = calcRequiredSpace(node.getInEdges(), graph);
      double outEdgeSpace = calcRequiredSpace(node.getOutEdges(), graph);
      if (layoutOrientation == LayoutOrientation.TOP_TO_BOTTOM ||
          layoutOrientation == LayoutOrientation.BOTTOM_TO_TOP) {
        //we have to enlarge the width such that the in-/out-edges can be placed side by side without overlaps
        width = Math.max(width, inEdgeSpace);
        width = Math.max(width, outEdgeSpace);
      } else {
        //we have to enlarge the height such that the in-/out-edges can be placed side by side without overlaps
        height = Math.max(height, inEdgeSpace);
        height = Math.max(height, outEdgeSpace);
      }

      // adjust size for edges with strong port constraints
      IDataProvider edgeThicknessDP = graph.getDataProvider(HierarchicLayout.EDGE_THICKNESS_DPKEY);
      if (edgeThicknessDP != null) {
        for (Edge edge : node.getEdges()) {
          double thickness = edgeThicknessDP.getDouble(edge);

          PortConstraint spc = PortConstraint.getSPC(graph, edge);
          if (edge.source() == node && spc != null && spc.isStrong()) {
            YPoint sourcePoint = graph.getSourcePointRel(edge);
            width = Math.max(width, Math.abs(sourcePoint.getX()) * 2 + thickness);
            height = Math.max(height, Math.abs(sourcePoint.getY()) * 2 + thickness);
          }

          PortConstraint tpc = PortConstraint.getTPC(graph, edge);
          if (edge.target() == node && tpc != null && tpc.isStrong()) {
            YPoint targetPoint = graph.getTargetPointRel(edge);
            width = Math.max(width, Math.abs(targetPoint.getX()) * 2 + thickness);
            height = Math.max(height, Math.abs(targetPoint.getY()) * 2 + thickness);
          }
        }
      }

      graph.setSize(node, width, height);
    }

    /**
     * Calculates the space required when placing the given edge side by side without overlaps and considering
     * the specified minimum port distance and edge thickness.
     */
    private double calcRequiredSpace( IEnumerable<Edge> edgesOnSideCur, LayoutGraph graph ) {
      double requiredSpace = 0;
      IDataProvider edgeThicknessDP = graph.getDataProvider(HierarchicLayout.EDGE_THICKNESS_DPKEY);
      int count = 0;
      for (Edge edge : edgesOnSideCur) {
        double thickness = (edgeThicknessDP == null) ? 0 : edgeThicknessDP.getDouble(edge);
        requiredSpace += Math.max(thickness, 1);
        count++;
      }
      requiredSpace += (count - 1) * getMinimumPortDistance();
      requiredSpace += 2 * getPortBorderGapRatio() * getMinimumPortDistance();
      return requiredSpace;
    }
  }

  public static void main( String[] args ) {
    launch(args);
  }


  /**
   * A {@link IPositionHandler}, that restricts node movement to the y-axis.
   */
  private static class VerticalPositionHandler extends ConstrainedPositionHandler {
    /**
     * Initializes a new instance of the {@link VerticalPositionHandler} class that delegates to the
     * {@code wrappedHandler}.
     * @param wrappedHandler The handler to wrap.
     */
    VerticalPositionHandler( IPositionHandler wrappedHandler ) {
      super(wrappedHandler);
    }

    @Override
    protected PointD constrainNewLocation( IInputModeContext context, PointD originalLocation, PointD newLocation ) {
      return new PointD(originalLocation.getX(), newLocation.getY());
    }
  }

  /**
   * An {@link IReshapeHandleProvider} that doesn't provide any handles, thus preventing
   * node resizing.
   */
  private static class NoReshapeHandleProvider implements IReshapeHandleProvider {
    /**
     * Returns the indicator for no valid position.
     */
    public HandlePositions getAvailableHandles( IInputModeContext context ) {
      return HandlePositions.NONE;
    }

    public IHandle getHandle( IInputModeContext inputModeContext, HandlePositions position ) {
      // Never called since getAvailableHandles returns no valid position.
      return null;
    }
  }
}
