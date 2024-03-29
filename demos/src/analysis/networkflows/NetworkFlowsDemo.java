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
package analysis.networkflows;

import com.yworks.yfiles.analysis.MaximumFlow;
import com.yworks.yfiles.analysis.MinimumCostFlow;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.PartitionGrid;
import com.yworks.yfiles.layout.PartitionGridData;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.LayerConstraintData;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;

/**
 * Three network flow graph analysis algorithms are presented in this application that are applied on a network of
 * water pipes.
 */
public class NetworkFlowsDemo extends DemoApplication {

  private static final String MAX_FLOW = "maxflow";
  private static final String MIN_COST_FLOW = "mincost";
  private static final String MAX_FLOW_MIN_CUT = "maxflowmincut";

  @FXML
  private WebView help;
  @FXML
  private WebView algorithmDescription;
  @FXML
  private GraphControl graphControl;
  @FXML
  private ComboBox<NamedEntry> algorithmComboBox;
  @FXML
  private Button prevAlgorithmButton;
  @FXML
  private Button nextAlgorithmButton;
  @FXML
  private Label resultLabel;

  private boolean inLayout;
  private MinCutLineVisualCreator minCutLine;

  /**
   * Selects the previous algorithm.
   */
  @FXML
  private void onPrevAlgorithm() {
    if (algorithmComboBox.getSelectionModel().getSelectedIndex() > 0) {
      algorithmComboBox.getSelectionModel().selectPrevious();
    } else {
      algorithmComboBox.getSelectionModel().selectLast();
    }
  }

  /**
   * Selects the next algorithm.
   */
  @FXML
  private void onNextAlgorithm() {
    if (algorithmComboBox.getSelectionModel().getSelectedIndex() + 1 == algorithmComboBox.getItems().size()) {
      algorithmComboBox.getSelectionModel().selectFirst();
    } else {
      algorithmComboBox.getSelectionModel().selectNext();
    }
  }

  /**
   * Configures the {@link ComboBox} for choosing the current sample graph.
   */
  private void configureAlgorithmComboBox() {
    algorithmComboBox.getItems().addAll(
        new NamedEntry("Maximum Flow", MAX_FLOW),
        new NamedEntry("Minimum Cost Flow", MIN_COST_FLOW),
        new NamedEntry("Maximum Flow / Minimum Cut", MAX_FLOW_MIN_CUT));

    algorithmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        onAlgorithmChanged()
    );
  }


  /**
   * Callback that is triggered when a new flow algorithm is set.
   */
  private void onAlgorithmChanged() {
    // update description of the new flow algorithm
    String selectedAlgorithm = getSelectedAlgorithm();
    this.updateDescriptionText(selectedAlgorithm);

    graphControl.getSelection().clear();

    // only show the minimum cut line when the according algorithm was chosen
    minCutLine.setVisible(selectedAlgorithm == MAX_FLOW_MIN_CUT);

    // execute the flow algorithm and apply the results
    runFlowAlgorithm();

    // apply a new graph layout
    runLayout();
  }

  private String getSelectedAlgorithm() {
    return algorithmComboBox.getSelectionModel().getSelectedItem().value;
  }

  /**
   * Runs the selected flow algorithm and show the result in the resultLabel.
   */
  private void runFlowAlgorithm() {
    IGraph graph = graphControl.getGraph();

    if (inLayout || graph.getNodes().size() == 0) {
      return;
    }

    // determine the algorithm to run
    String resultName;
    int flowValue;
    switch (getSelectedAlgorithm()) {
      case MAX_FLOW_MIN_CUT:
        resultName = "Maximum Flow/Minimum Cut";
        flowValue = calculateMaxFlowMinCut(true);
        break;
      case MIN_COST_FLOW:
        resultName = "Minimum Cost";
        flowValue = calculateMinCostFlow();
        break;
      default:
      case MAX_FLOW:
        resultName = "Maximum Flow";
        flowValue = calculateMaxFlowMinCut(false);
        break;
    }

    // update flow information
    resultLabel.setText("  " + resultName + ": " + flowValue);
  }

  /**
   * Calculates a {@link MinimumCostFlow} algorithm considering the capacities, costs, and supplies of the edges
   * and updates the node and edge visualization to show the results.
   *
   * @return The total cost of the minimum cost flow calculation.
   */
  private int calculateMinCostFlow() {
    IGraph graph = graphControl.getGraph();
    // make sure that there is flow
    graph.getNodes().forEach(node -> {
      NodeData nodeData = (NodeData) node.getTag();
      if (graph.inDegree(node) == 0) {
        nodeData.setSupply(0.5);
      } else if (graph.outDegree(node) == 0) {
        nodeData.setSupply(-0.5);
      } else {
        nodeData.setSupply(0);
      }
    });

    MinimumCostFlow.Result minCostFlowResult = null;
    try {
      MinimumCostFlow minCostFlow = new MinimumCostFlow();
      minCostFlow.setMaximumCapacities(edge -> ((EdgeData) edge.getTag()).getCapacity());
      minCostFlow.setCosts(edge -> edge.getTag() != null ? ((EdgeData) edge.getTag()).getCost() : 0);
      // the supply or demand of a node was calculated in calculateMaxFlow and set as node tag
      minCostFlow.setSupply(node -> node.getTag() != null ? ((int) (((NodeData) node.getTag()).getSupply() * node.getLayout().getHeight())) : 0);
      minCostFlowResult = minCostFlow.run(graph);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      MinimumCostFlow.Result finalMinCostFlowResult = minCostFlowResult;
      // update edge flow and edge cost labels
      graph.getEdges().forEach(edge -> {
        EdgeData tag = (EdgeData) edge.getTag();
        tag.setFlow(finalMinCostFlowResult.getFlow().getValue(edge));

        String costText = tag.getCost() + " \u20AC";
        if (edge.getLabels().size() > 1) {
          // add label for cost
          graph.setLabelText(edge.getLabels().getItem(1), costText);
        } else {
          graph.addLabel(edge,
              costText,
              null,
              graph.getEdgeDefaults().getLabelDefaults().getStyle(),
              null,
              "cost");
        }
      });
    }

    updateEdgeFlowLabels();

    // update flow through the nodes flow
    for (INode node : graph.getNodes()) {
      NodeData nodeData = (NodeData) node.getTag();
      double flow = 0;
      if (graph.inDegree(node) > 0 && graph.outDegree(node) > 0) {
        for (IEdge edge : graph.inEdgesAt(node)) {
          flow += ((EdgeData) edge.getTag()).getFlow();
        }
      } else {
        // display supply or demand of source/sink node
        flow = Math.abs(((NodeData) node.getTag()).getSupply() * node.getLayout().getHeight());
      }
      nodeData.setFlow(flow);

      // mark source and sink nodes
      if (nodeData.getSupply() < 0) {
        nodeData.setSink(true);
      } else if (nodeData.getSupply() > 0) {
        nodeData.setSource(true);
      }
    }

    return minCostFlowResult != null ? minCostFlowResult.getTotalCost() : 0;
  }

  /**
   * Calculates a {@link MaximumFlow} algorithm considering the capacities of the edges
   * and updates the node and edge visualization to show the results.
   *
   * @return The total cost of the minimum cost flow calculation.
   */
  private int calculateMaxFlowMinCut(boolean minCut) {
    IGraph graph = graphControl.getGraph();

    // remove edge cost labels if there are any
    for (IEdge edge : graph.getEdges()) {
      IListEnumerable<ILabel> labels = edge.getLabels();
      if (labels.size() > 1) {
        graph.remove(labels.getItem(1));
      }
    }

    // calculate the maximum flow using the edge capacities stored in edge tags
    MaximumFlow maxFlowMinCut = new MaximumFlow();
    maxFlowMinCut.setSources(this::isSource);
    maxFlowMinCut.setSinks(this::isSink);
    maxFlowMinCut.setCapacities(edge -> ((EdgeData) edge.getTag()).getCapacity());

    MaximumFlow.Result maxFlowMinCutResults = maxFlowMinCut.run(graph);

    maxFlowMinCutResults.getFlow().forEach((edge, flow) -> ((EdgeData) edge.getTag()).setFlow(flow));

    for (INode node : graph.getNodes()) {
      int flow = 0;
      IListEnumerable<IEdge> flowEdges = graph.inDegree(node) > 0 ? graph.inEdgesAt(node) : graph.outEdgesAt(node);
      for (IEdge flowEdge : flowEdges) {
        flow += ((EdgeData) flowEdge.getTag()).getFlow();
      }
      NodeData tag = (NodeData) node.getTag();
      tag.setFlow(flow);
      tag.setSupply(0);
      tag.setSource(this.isSource(node));
      tag.setSink(this.isSink(node));
    }

    if (minCut) {
      maxFlowMinCutResults.getSourcePartition().forEach(node -> ((NodeData) node.getTag()).setCut(true));
      maxFlowMinCutResults.getSinkPartition().forEach(node -> ((NodeData) node.getTag()).setCut(false));
    }

    updateEdgeFlowLabels();
    return maxFlowMinCutResults.getMaximumFlow();
  }

  /**
   * Decorates the nodes and the edges of the graph based on the result of the algorithm.
   */
  private void updateEdgeFlowLabels() {
    IGraph graph = graphControl.getGraph();

    for (IEdge edge : graph.getEdges()) {
      EdgeData flowData = (EdgeData) edge.getTag();

      DefaultLabelStyle labelStyle = new DefaultLabelStyle();
      labelStyle.setTextPaint(flowData.getCapacity() == 0 || flowData.getFlow() == 0 ? Color.BLACK : Color.CYAN);

      ILabel label = edge.getLabels().first();
      graph.setStyle(label, labelStyle);
      graph.setLabelText(label, (int) Math.floor(flowData.getFlow()) + " / " + flowData.getCapacity());
    }
    graphControl.invalidate();
  }


  /**
   * Run a hierarchic layout.
   */
  private void runLayout() {
    IGraph graph = graphControl.getGraph();

    if (inLayout || graph.getNodes().size() == 0) {
      return;
    }

    inLayout = true;

    HierarchicLayout layout = new HierarchicLayout();
    layout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    layout.setIntegratedEdgeLabelingEnabled(true);
    layout.setBackLoopRoutingEnabled(true);

    HierarchicLayoutData layoutData = new HierarchicLayoutData();
    layoutData.setEdgeThickness(edge -> (double) ((EdgeData) edge.getTag()).getCapacity());

    // sources will be in the first layer, sinks in the last layer
    LayerConstraintData layerConstraints = layoutData.getLayerConstraints();
    for (INode node : graph.getNodes()) {
      if (isSource(node)) {
        layerConstraints.placeAtTop(node);
      } else if (isSink(node)) {
        layerConstraints.placeAtBottom(node);
      }
    }

    boolean isMaxFlowMinCut = MAX_FLOW_MIN_CUT.compareTo(getSelectedAlgorithm()) == 0;

    if (isMaxFlowMinCut) {
      // use a partition grid to place all nodes at the source side of the cut left of those at the sink side
      PartitionGridData partitionGridData = new PartitionGridData();
      partitionGridData.setGrid(new PartitionGrid(1, 2, 0, 150, 0, 0));
      partitionGridData.setCellIds((node, grid) -> grid.createCellId(0, ((NodeData) node.getTag()).isCut() ? 0 : 1));
      layoutData.setPartitionGridData(partitionGridData);
    }

    graphControl.morphLayout(layout, Duration.ofSeconds(1), layoutData).thenRun(() -> {
      inLayout = false;

      if (isMaxFlowMinCut) {
        updateMinCutLine();
      }
    });
  }

  /**
   * Update visibility and bounds of the minimum cut line.
   */
  private void updateMinCutLine() {
    IGraph graph = graphControl.getGraph();

    if (graph.getEdges().size() > 0) {
      // find the center between the last "source-cut"-layer and the first "target-cut" layer
      minCutLine.setVisible(true);
      double maxX = Double.NEGATIVE_INFINITY;
      double minX = Double.POSITIVE_INFINITY;
      for (INode node : graph.getNodes()) {
        if (((NodeData) node.getTag()).isCut()) {
          maxX = Math.max(maxX, node.getLayout().getMaxX());
        } else {
          minX = Math.min(minX, node.getLayout().getX());
        }
      }

      if (Double.isFinite(maxX) && Double.isFinite(minX)) {
        RectD graphBounds = graphControl.getContentRect();
        minCutLine.setBounds(new RectD(
            (maxX + minX) * 0.5 - 5, graphBounds.getY() - 30, 10, graphBounds.getHeight() + 60));
      } else {
        minCutLine.setBounds(RectD.EMPTY);
      }
    } else {
      minCutLine.setVisible(false);
    }

    graphControl.invalidate();
  }

  /**
   * Returns whether the node has no incoming but outgoing edges and is therefore a source node.
   *
   * @param node The node to check.
   * @return Whether the node is a source node.
   */
  private boolean isSource(INode node) {
    IGraph graph = graphControl.getGraph();
    return graph.inDegree(node) == 0 && graph.outDegree(node) != 0;
  }

  /**
   * Returns whether the node has no outgoing but incoming edges and is therefore a sink node.
   *
   * @param node The node to check.
   * @return Whether the node is a sink node.
   */
  private boolean isSink(INode node) {
    IGraph graph = graphControl.getGraph();
    return graph.inDegree(node) != 0 && graph.outDegree(node) == 0;
  }




  public void initialize() {
    // set up the help text on the right side.
    WebViewUtils.initHelp(help, this);

    // populate combo boxes
    configureAlgorithmComboBox();

    initializeGraphDefaults();
    initializeMinCutVisualization();
    initializeInputMode();
    createSampleGraph();

  }

  /**
   * Updates the description text based on the selected algorithm.
   */
  private void updateDescriptionText(String algorithmName) {
    WebEngine engine = algorithmDescription.getEngine();
    URL descriptionFile = this.getClass().getResource("resources/" + algorithmName + ".html");
    if (descriptionFile != null) {
      engine.load(descriptionFile.toExternalForm());
    } else {
      engine.loadContent("Could not resolve algorithm description text. Please ensure that your build process or IDE adds the " +
          "HTML files to the class path.", "text/plain");
    }
  }


  /**
   * Creates and prepares the input graph.
   */
  private void createSampleGraph() {
    IGraph graph = graphControl.getGraph();
    graph.clear();

    INode node1 = graph.createNode();
    INode node2 = graph.createNode();
    INode node3 = graph.createNode();
    INode node4 = graph.createNode();
    INode node5 = graph.createNode();
    INode node6 = graph.createNode();
    INode node7 = graph.createNode();
    INode node8 = graph.createNode();

    createEdge(graph, node1, node2, 19, 14);
    createEdge(graph, node2, node3, 15, 16);
    createEdge(graph, node1, node3, 16, 15);
    createEdge(graph, node1, node4, 25, 13);
    createEdge(graph, node2, node6, 10, 11);
    createEdge(graph, node4, node3, 15, 13);
    createEdge(graph, node3, node5, 23, 10);
    createEdge(graph, node4, node7, 16, 10);
    createEdge(graph, node5, node6, 10, 15);
    createEdge(graph, node5, node7, 10, 16);
    createEdge(graph, node5, node8, 16, 15);
    createEdge(graph, node6, node8, 13, 15);
    createEdge(graph, node7, node8, 15, 16);

    for (INode node : graph.getNodes()) {
      double supply = 0;
      if (graph.inDegree(node) == 0) {
        // source
        supply = 0.5;
      } else if (graph.outDegree(node) == 0) {
        // sink
        supply = -0.5;
      }
      // just use the flow for all nodes initially. It is changed in the first algorithm run anyway.
      node.setTag(new NodeData(supply, 15));
      adjustNodeSizeToCapacity(node);
    }

    algorithmComboBox.getSelectionModel().selectFirst();
  }

  /**
   * Creates an edge and adds a label with the edge thickness.
   * The thickness is the percentage of flow that passes through an edge in comparison to the overall thickness.
   *
   * @param graph    The graph to create the edge in.
   * @param source   The source node of the edge.
   * @param target   The target node of the edge.
   * @param capacity The maximum capacity of the edge.
   * @param cost     The cost for sending flow through the edge.
   */
  private void createEdge(IGraph graph, INode source, INode target, int capacity, int cost) {
    EdgeData edgeData = new EdgeData(capacity, cost);
    IEdge edge = graph.createEdge(source, target, null, edgeData);
    // add label for capacity
    String text = "0 / " + capacity;
    graph.addLabel(edge, text, new FreeEdgeLabelModel().createDefaultParameter(), new DefaultLabelStyle());
  }

  /**
   * Calculates and sets the node size based on the thickness of incoming and outgoing edges.
   *
   * @param node The given node
   */
  private void adjustNodeSizeToCapacity(INode node) {
    IGraph graph = graphControl.getGraph();

    int incomingCapacity = totalCapacity(graph.inEdgesAt(node));
    int outgoingCapacity = totalCapacity(graph.outEdgesAt(node));

    int height = Math.max(incomingCapacity, outgoingCapacity);
    IRectangle layout = node.getLayout();
    RectD newBounds = new RectD(layout.getX(), layout.getY(), layout.getWidth(), Math.max(height, 30));
    graph.setNodeLayout(node, newBounds);
  }

  private int totalCapacity(IListEnumerable<IEdge> edges) {
    int capacity = 0;
    for (IEdge edge : edges) {
      capacity += ((EdgeData) edge.getTag()).getCapacity();
    }
    return capacity;
  }

  /**
   * Set graph defaults and hide edge and label highlighting.
   */
  private void initializeGraphDefaults() {
    IGraph graph = graphControl.getGraph();

    graph.getNodeDefaults().setStyle(new NetworkFlowNodeStyle());
    graph.getNodeDefaults().setSize(new SizeD(60, 30));
    graph.getEdgeDefaults().setStyle(new NetworkFlowEdgeStyle());

    // add a mapper that indicates the layout where to place the edge labels
    graph.getMapperRegistry().createFunctionMapper(
        ILabel.class,
        PreferredPlacementDescriptor.class,
        LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY,
        key -> {
          PreferredPlacementDescriptor preferredPlacementDescriptor = new PreferredPlacementDescriptor();
          if ("cost".equals(key.getTag())) {
            preferredPlacementDescriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE);
            preferredPlacementDescriptor.setDistanceToEdge(5);
          } else {
            preferredPlacementDescriptor.setSideOfEdge(LabelPlacements.ON_EDGE);
          }
          preferredPlacementDescriptor.freeze();
          return preferredPlacementDescriptor;
        });

    graph.getEdgeDefaults().getLabelDefaults()
        .setLayoutParameter(FreeEdgeLabelModel.INSTANCE.createDefaultParameter());

    // label style used for the 'cost' labels
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setFont(new Font("Dialog", 11));
    defaultLabelStyle.setBackgroundPen(new Pen(Color.SKYBLUE));
    defaultLabelStyle.setBackgroundPaint(Color.ALICEBLUE);
    defaultLabelStyle.setInsets(new InsetsD(3, 5, 3, 5));
    graph.getEdgeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);

    GraphDecorator decorator = graph.getDecorator();
    decorator.getEdgeDecorator().getHighlightDecorator().hideImplementation();
    decorator.getEdgeDecorator().getSelectionDecorator().hideImplementation();

    decorator.getLabelDecorator().getSelectionDecorator().hideImplementation();
    decorator.getLabelDecorator().getFocusIndicatorDecorator().hideImplementation();
  }

  /**
   * Add a {@link MinCutLineVisualCreator} to visualize the minimum cut after a max flow / min cut algorithm run.
   */
  private void initializeMinCutVisualization() {
    minCutLine = new MinCutLineVisualCreator();
    graphControl.getHighlightGroup().addChild(minCutLine, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  /**
   * Set a {@link GraphViewerInputMode} as we don't support edit gestures.
   */
  private void initializeInputMode() {
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setFocusableItems(GraphItemTypes.NONE);
    gvim.setSelectableItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    graphControl.setInputMode(gvim);
  }

  @Override
  public void onLoaded() {
    algorithmComboBox.getSelectionModel().selectFirst();
  }

  @Override
  public void start(final Stage primaryStage) throws IOException {
    primaryStage.setMaximized(true);
    super.start(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }

  // endregion

  /**
   * Name-value struct for combo box entries.
   */
  private static class NamedEntry {
    final String displayName;
    final String value;

    NamedEntry(String displayName, String value) {
      this.displayName = displayName;
      this.value = value;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }
}
