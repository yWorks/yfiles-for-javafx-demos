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
package layout.nodetypes;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.ComponentLayoutData;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.PortAdjustmentPolicy;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.circular.CircularLayoutData;
import com.yworks.yfiles.layout.circular.NodeTypeAwareSequencer;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.organic.CycleSubstructureStyle;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayoutData;
import com.yworks.yfiles.layout.organic.ParallelSubstructureStyle;
import com.yworks.yfiles.layout.organic.StarSubstructureStyle;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.tree.CompactNodePlacer;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeLayoutData;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;

/**
 * A demo that shows how different layout algorithms handle nodes with types.
 */
public class NodeTypesDemo extends DemoApplication {
  @FXML
  private WebView helpView;
  @FXML
  private GraphControl graphControl;
  @FXML
  private ComboBox<Sample> sampleComboBox;
  @FXML
  private CheckBox considerTypesCheckBox;
  @FXML
  private Button prevSampleButton;
  @FXML
  private Button nextSampleButton;
  @FXML
  private Button layoutButton;

  // region Style definitions

  // node visualizations for each node type
  private static final ShapeNodeStyle[] NODE_STYLES = {
      createNodeStyle(Color.web("#17bebb"), Color.web("#407271")),
      createNodeStyle(Color.web("#ffc914"), Color.web("#998953")),
      createNodeStyle(Color.web("#ff6c00"), Color.web("#662b00")),
  };

  // edge visualizations for directed and undirected edges
  private static final IEdgeStyle DIRECTED_EDGE_STYLE = createEdgeStyle(true);
  private static final IEdgeStyle UNDIRECTED_EDGE_STYLE = createEdgeStyle(false);

  private static ShapeNodeStyle createNodeStyle(Color fill, Color stroke) {
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    style.setPaint(fill);
    style.setPen(new Pen(stroke, 1.5));
    return style;
  }

  private static IEdgeStyle createEdgeStyle(boolean directed) {
    PolylineEdgeStyle style = new PolylineEdgeStyle();
    style.setPen(new Pen(Color.BLACK, 1.5));
    style.setTargetArrow(directed ? IArrow.DEFAULT : IArrow.NONE);
    return style;
  }

  // endregion

  // region User interface event handling

  /**
   * Loads the previous sample graph.
   */
  @FXML
  private void onPrevSampleButtonClicked() {
    int index = sampleComboBox.getSelectionModel().getSelectedIndex();
    int sampleCount = sampleComboBox.getItems().size();
    int newIndex = index > 0 ? index - 1 : sampleCount - 1;
    sampleComboBox.getSelectionModel().select(newIndex);
  }

  /**
   * Loads the next sample graph.
   */
  @FXML
  private void onNextSampleButtonClicked() {
    int index = sampleComboBox.getSelectionModel().getSelectedIndex();
    int sampleCount = sampleComboBox.getItems().size();
    int newIndex = index < sampleCount - 1 ? index + 1 : 0;
    sampleComboBox.getSelectionModel().select(newIndex);
  }

  /**
   * Initializes the sample combobox for choosing a sample graph.
   */
  private void initializeSampleComboBox() {
    sampleComboBox.getItems().addAll(
        createHierarchicSample(),
        createOrganicSample(),
        createTreeSample(),
        createCircularSample(),
        createComponentSample()
    );
    sampleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> loadSample());
  }

  /**
   * Invokes the layout calculation.
   */
  @FXML
  private void onLayoutButtonClicked() {
    applyLayout(true);
  }

  /**
   * Toggles type consideration.
   */
  @FXML
  private void onConsiderTypesCheckBoxClicked() {
    applyLayout(true);
  }

  /**
   * Activates or deactivates the UI elements for changing the sample.
   */
  private void enableUi(boolean enabled) {
    layoutButton.setDisable(!enabled);
    sampleComboBox.setDisable(!enabled);
    prevSampleButton.setDisable(!enabled);
    nextSampleButton.setDisable(!enabled);
    considerTypesCheckBox.setDisable(!enabled);
  }

  // endregion

  // region Initialization

  /**
   * Initializes the demo.
   */
  public void initialize() {
    WebViewUtils.initHelp(helpView, this);
    initializeInputModes();
    initializeGraph(graphControl.getGraph());
    initializeSampleComboBox();
  }

  /**
   * Loads the first sample graph.
   */
  @Override
  protected void onLoaded() {
    super.onLoaded();
    sampleComboBox.getSelectionModel().select(0);
  }


  /**
   * Initializes the edit mode and the context menu.
   */
  private void initializeInputModes() {
    GraphEditorInputMode editMode = new GraphEditorInputMode();
    editMode.setSelectableItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    editMode.setContextMenuItems(GraphItemTypes.NODE);
    editMode.setNodeCreator((context, graph, location, parent) -> {
      INode node = graph.createNode(location);
      node.setTag(0);
      graph.setStyle(node, getNodeStyle(node));
      return node;
    });

    editMode.addPopulateItemContextMenuListener(this::onPopulateItemPopupMenu);

    graphControl.setInputMode(editMode);
  }

  /**
   * Opens a context menu to change the node type.
   */
  private void onPopulateItemPopupMenu(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
    IModelItem item = args.getItem();
    if (item instanceof INode) {
      // Select node if not already selected
      if (!graphControl.getSelection().isSelected(item)) {
        graphControl.getSelection().clear();
        graphControl.getSelection().setSelected(item, true);
      }

      ContextMenu menu = (ContextMenu) args.getMenu();

      for (int i = 0; i < NODE_STYLES.length; i++) {
        int type = i;
        ShapeNodeStyle newNodeStyle = NODE_STYLES[type];
        MenuItem menuItem = new MenuItem();
        Color color = (Color) newNodeStyle.getPaint();
        menuItem.setGraphic(new Rectangle(50, 30, color));
        menuItem.setOnAction(e -> {
          // Change color for all selected nodes
          for (INode node : graphControl.getSelection().getSelectedNodes()) {
            node.setTag(type);
            graphControl.getGraph().setStyle(node, newNodeStyle);
          }
          applyLayout(true);
        });
        menu.getItems().add(menuItem);
      }
    }
  }

  /**
   * Configures the defaults for the graph.
   */
  private void initializeGraph(IGraph graph) {
    graph.getNodeDefaults().setStyleInstanceSharingEnabled(false);
    graph.getNodeDefaults().setSize(new SizeD(40, 40));

    graph.setUndoEngineEnabled(true);
  }

  /**
   * Loads a sample graph and adapts the visualization of the nodes to their types and of the edges to their
   * directions.
   */
  private void loadSample() {
    Sample selectedSample = sampleComboBox.getSelectionModel().getSelectedItem();
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/" + selectedSample.file + ".graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    IGraph graph = graphControl.getGraph();
    graph.getNodes().forEach(node -> graph.setStyle(node, getNodeStyle(node)));
    graph.getEdges().forEach(edge -> graph.setStyle(edge, getEdgeStyle(selectedSample.isDirected)));

    applyLayout(false);
    graphControl.fitGraphBounds();
  }

  // endregion

  // region Node type handling

  /**
   * Gets the type of the given node from its tag.
   */
  private int getNodeType(INode node) {
    // The implementation of this demo assumes that on the INode.Tag a type property
    // (a number) exists. Note though that for the layout's node type feature arbitrary objects
    // from arbitrary sources may be used.
    return node.getTag() instanceof Integer ? (int) node.getTag() : 0;
  }

  /**
   * Determines the visualization of a node based on its type.
   */
  private ShapeNodeStyle getNodeStyle(INode node) {
    int type = getNodeType(node);
    return NODE_STYLES[type];
  }

  /**
   * Determines the visualization of an edge based on its direction.
   */
  private IEdgeStyle getEdgeStyle(boolean directed) {
    return directed ? DIRECTED_EDGE_STYLE : UNDIRECTED_EDGE_STYLE;
  }

  // endregion

  // region Sample creation

  /**
   * Creates and configures the {@link HierarchicLayout} and the {@link HierarchicLayoutData} such that node types are
   * considered.
   */
  private Sample createHierarchicSample() {
    // create hierarchic layout - no further settings on the algorithm necessary to support types
    HierarchicLayout layout = new HierarchicLayout();

    // the node types are specified as delegate on the nodeTypes property of the layout data
    HierarchicLayoutData layoutData = new HierarchicLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Hierarchic", "hierarchic", layout, layoutData, true);
  }

  /**
   * Creates and configures the {@link OrganicLayout} and the {@link OrganicLayoutData} such that node types are
   * considered.
   */
  private Sample createOrganicSample() {
    // to consider node types, substructures handling (stars, parallel structures and cycles)
    // on the organic layout is enabled - otherwise types have no influence
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setDeterministicModeEnabled(true);
    organicLayout.setNodeSizeConsiderationEnabled(true);
    organicLayout.setMinimumNodeDistance(30);
    organicLayout.setStarSubstructureStyle(StarSubstructureStyle.CIRCULAR);
    organicLayout.setStarSubstructureTypeSeparationEnabled(false);
    organicLayout.setParallelSubstructureStyle(ParallelSubstructureStyle.RECTANGULAR);
    organicLayout.setParallelSubstructureTypeSeparationEnabled(false);
    organicLayout.setCycleSubstructureStyle(CycleSubstructureStyle.CIRCULAR);

    // create an organic layout wrapped by an organic edge router
    OrganicEdgeRouter layout = new OrganicEdgeRouter(organicLayout);

    // the node types are specified as delegate on the nodeTypes property of the layout data
    OrganicLayoutData layoutData = new OrganicLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Organic", "organic", layout, layoutData, false);
  }

  /**
   * Creates and configures the {@link TreeLayout} and the {@link TreeLayoutData} such that node types are considered.
   */
  private Sample createTreeSample() {
    // create a tree layout including a reduction stage to support non-tree graphs too
    TreeLayout layout = new TreeLayout();
    layout.setDefaultNodePlacer(new CompactNodePlacer());

    EdgeRouter edgeRouter = new EdgeRouter();
    edgeRouter.setScope(Scope.ROUTE_AFFECTED_EDGES);

    TreeReductionStage reductionStage = new TreeReductionStage();
    reductionStage.setNonTreeEdgeRouter(edgeRouter);
    reductionStage.setNonTreeEdgeSelectionKey(edgeRouter.getAffectedEdgesDpKey());

    layout.prependStage(reductionStage);

    // the node types are specified as delegate on the nodeTypes property of the layout data
    TreeLayoutData layoutData = new TreeLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Tree", "tree", layout, layoutData, true);
  }

  /// <summary>
  /// Creates and configures the <see cref="CircularLayout"/> and the <see cref="CircularLayoutData"/>
  /// such that node types are considered.
  /// </summary>

  /**
   * Creates and configures the {@link CircularLayout} and the {@link CircularLayoutData} such that node types are
   * considered.
   */
  private Sample createCircularSample() {
    // create a circular layout and specify the NodeTypeAwareSequencer as sequencer responsible
    // for the ordering on the circle - this is necessary to support node types
    CircularLayout layout = new CircularLayout();
    layout.getSingleCycleLayout().setNodeSequencer(new NodeTypeAwareSequencer());

    // the node types are specified as delegate on the nodeTypes property of the layout data
    CircularLayoutData layoutData = new CircularLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Circular", "circular", layout, layoutData, false);
  }

  /**
   * Creates and configures the {@link ComponentLayout} and the {@link ComponentLayoutData} such that node types are
   * considered.
   */
  private Sample createComponentSample() {
    // create a component layout with default settings
    ComponentLayout layout = new ComponentLayout();

    // note that with the default component arrangement style the types of nodes have an influence
    // already - however, if in a row only components with nodes of the same type should be
    // allowed, this can be achieved by specifying the style as follows:
    // layout.Style = ComponentArrangementStyles.MultiRowsTypeSeparated

    // the node types are specified as delegate on the nodeTypes property of the layout data
    ComponentLayoutData layoutData = new ComponentLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Component", "component", layout, layoutData, false);
  }

  // endregion

  // region Layout calculation

  /**
   * Calculates a layout taking the node types into account.
   */
  private void applyLayout(boolean animate) {
    Sample sample = sampleComboBox.getSelectionModel().getSelectedItem();
    boolean considerTypes = considerTypesCheckBox.isSelected();

    ILayoutAlgorithm layout = sample.layout;
    LayoutData layoutData = considerTypes ? sample.layoutData : null;

    LayoutExecutor layoutExecutor = new LayoutExecutor(graphControl, layout);
    layoutExecutor.setLayoutData(layoutData);
    layoutExecutor.setPortAdjustmentPolicy(PortAdjustmentPolicy.ALWAYS);
    layoutExecutor.setRunningInThread(false);
    layoutExecutor.setViewportAnimationEnabled(true);
    if (animate) {
      layoutExecutor.setDuration(Duration.ofMillis(700));
    }

    enableUi(false);
    layoutExecutor.start().thenRun(() -> enableUi(true));
  }

  // endregion

  /**
   * Contains all information about a sample.
   */
  static class Sample {
    public final String name;
    public final String file;
    public final ILayoutAlgorithm layout;
    public final LayoutData layoutData;
    public final boolean isDirected;

    public Sample(String name, String file, ILayoutAlgorithm layout, LayoutData layoutData, boolean isDirected) {
      this.name = name;
      this.file = file;
      this.layout = layout;
      this.layoutData = layoutData;
      this.isDirected = isDirected;
    }

    public String toString() {
      return name;
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
