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
package complete.aggregategraphwrapper;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;
import toolkit.aggregation.AggregateGraphWrapper;
import toolkit.aggregation.EdgeReplacementPolicy;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Using the <code>AggregateGraphWrapper</code> class allows for aggregating graph items by hiding items and adding new
 * items to a wrapped graph.
 */
public class AggregateGraphWrapperDemo extends DemoApplication {

  public GraphControl graphControl;

  public WebView helpView;


  // selectors for shape and/or color
  private static final Function<INode, ShapeNodeShape> SHAPE_SELECTOR = n -> ((ShapeNodeStyle) n.getStyle()).getShape();
  private static final Function<INode, Paint> COLOR_SELECTOR = n -> ((ShapeNodeStyle) n.getStyle()).getPaint();

  private final Function<INode, ShapeAndPaint> ShapeAndBrushSelector =
      n -> new ShapeAndPaint(SHAPE_SELECTOR.apply(n), COLOR_SELECTOR.apply(n));

  private static final Pen GRAY_BORDER = new Pen(Color.DIMGRAY, 2);

  // style factories for aggregation nodes
  private static final Function<ShapeNodeShape, INodeStyle> SHAPE_STYLE = shape -> {
    ShapeNodeStyle shapeNodeStyle = new ShapeNodeStyle();
    shapeNodeStyle.setShape(shape);
    shapeNodeStyle.setPaint(Color.FLORALWHITE);
    shapeNodeStyle.setPen(GRAY_BORDER);
    return shapeNodeStyle;
  };

  private static final Function<Paint, INodeStyle> PAINT_STYLE = brush -> {
    ShapeNodeStyle shapeNodeStyle = new ShapeNodeStyle();
    shapeNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    shapeNodeStyle.setPaint(brush);
    shapeNodeStyle.setPen(GRAY_BORDER);
    return shapeNodeStyle;
  };

  private static final Function<ShapeAndPaint, INodeStyle> SHAPE_AND_PAINT_STYLE = shapeAndBrush -> {
    ShapeNodeStyle shapeNodeStyle = new ShapeNodeStyle();
    shapeNodeStyle.setShape(shapeAndBrush.shape);
    shapeNodeStyle.setPaint(shapeAndBrush.paint);
    shapeNodeStyle.setPen(GRAY_BORDER);
    return shapeNodeStyle;
  };


  /**
   * Initializes the graph and the input mode.
   */
  public void initialize() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);

    // create and configure a new AggregateGraphWrapper
    AggregateGraphWrapper aggregateGraph = new AggregateGraphWrapper(graphControl.getGraph());

    // set default label text sizes for aggregation labels
    DefaultLabelStyle nodeLabelStyle = new DefaultLabelStyle();
    nodeLabelStyle.setFont(new Font("Dialog", 28));
    aggregateGraph.getAggregationNodeDefaults().getLabelDefaults().setStyle(nodeLabelStyle);
    DefaultLabelStyle edgeLabelStyle = new DefaultLabelStyle();
    edgeLabelStyle.setFont(new Font("Dialog", 18));
    aggregateGraph.getAggregationEdgeDefaults().getLabelDefaults().setStyle(edgeLabelStyle);

    // disable edge cropping, so thick aggregation edges run smoothly into nodes
    aggregateGraph.getDecorator().getPortDecorator().getEdgePathCropperDecorator().hideImplementation();

    // don't create edges in both directions when replacing edges by aggregation edges
    aggregateGraph.setEdgeReplacementPolicy(EdgeReplacementPolicy.UNDIRECTED);

    // assign it to the graphControl
    graphControl.setGraph(aggregateGraph);

    // initialize the GraphViewerInputMode
    initializeInputModes();

    // listen to node and edge creation to add labels to aggregation items
    addItemCreationListener();

    // load a sample graph
    loadSampleGraph();
  }

  @Override
  public void onLoaded() {
    graphControl.fitGraphBounds();
  }

  private void initializeInputModes() {
    GraphViewerInputMode graphViewerInputMode = new GraphViewerInputMode();
    graphViewerInputMode.setContextMenuItems(GraphItemTypes.NODE);
    graphViewerInputMode.addPopulateItemContextMenuListener(this::onPopulateItemPopupMenu);
    graphControl.setInputMode(graphViewerInputMode);
  }

  /*
   * Fills the popup menu with menu items based on the selected node.
   */
  private void onPopulateItemPopupMenu(Object source, PopulateItemContextMenuEventArgs<IModelItem> e) {
    // first update the selection:
    // if the cursor is over a node select it, else clear selection
    updateSelection((INode) e.getItem());

    final ObservableList<MenuItem> menu = ((ContextMenu) e.getMenu()).getItems();

    // Create the popup menu items
    ISelectionModel<INode> selectedNodes = graphControl.getSelection().getSelectedNodes();
    if (selectedNodes.size() > 0) {
      // only allow aggregation operations on nodes that are not aggregation nodes already
      boolean aggregateAllowed = selectedNodes.stream().anyMatch(n -> !getAggregateGraph().isAggregationItem(n));
      menu.add(createMenuItem("Aggregate Nodes with Same Shape",
          event -> aggregateSame(selectedNodes.toList(), SHAPE_SELECTOR, SHAPE_STYLE), !aggregateAllowed));
      menu.add(createMenuItem("Aggregate Nodes with Same Color",
          event -> aggregateSame(selectedNodes.toList(), COLOR_SELECTOR, PAINT_STYLE), !aggregateAllowed));
      menu.add(createMenuItem("Aggregate Nodes with Same Shape & Color",
          event -> aggregateSame(selectedNodes.toList(), ShapeAndBrushSelector, SHAPE_AND_PAINT_STYLE),
          !aggregateAllowed));
      boolean separateAllowed = selectedNodes.stream().allMatch(n -> getAggregateGraph().isAggregationItem(n));
      menu.add(createMenuItem("Separate", event -> separate(selectedNodes.toList()), !separateAllowed));
    } else {

      menu.add(
          createMenuItem("Aggregate All Nodes by Shape", event -> aggregateAll(SHAPE_SELECTOR, SHAPE_STYLE), false));
      menu.add(
          createMenuItem("Aggregate All Nodes by Color", event -> aggregateAll(COLOR_SELECTOR, PAINT_STYLE), false));
      menu.add(createMenuItem("Aggregate All Nodes by Shape & Color",
          event -> aggregateAll(ShapeAndBrushSelector, SHAPE_AND_PAINT_STYLE), false));

      boolean separateAllowed = selectedNodes.stream().allMatch(n -> getAggregateGraph().isAggregationItem(n));
      menu.add(createMenuItem("Separate All", event -> {
        getAggregateGraph().separateAll();
        runLayout();
      }, !separateAllowed));
    }
    // make the menu show
    e.setShowingMenuRequested(true);
    e.setHandled(true);
  }

  /**
   * Creates a MenuItem with the given text and event handler.
   */
  private MenuItem createMenuItem(String text, EventHandler<ActionEvent> handler, boolean disabled) {
    MenuItem menuItem = new MenuItem(text);
    menuItem.setOnAction(handler);
    menuItem.setDisable(disabled);
    return menuItem;
  }

  /*
   * Updates the node selection state when the popup menu is opened on <code>node</code>.
   * If <code>node</code> is <code>null</code>, the selection is cleared.
   * If <code>node</code> is already selected, the selection keeps unchanged, otherwise the selection
   * is cleared and <code>node</code> is selected.
   *
   * @param node The node to select or <code>null</code>.
   */
  private void updateSelection(INode node) {
    // see if no node was hit
    if (node == null) {
      // clear the whole selection
      graphControl.getSelection().clear();
    } else {
      // see if the node was selected, already and keep the selection in this case
      final IGraphSelection selection = graphControl.getSelection();
      if (!selection.getSelectedNodes().isSelected(node)) {
        // no - clear the remaining selection
        selection.clear();
        // select the node
        selection.getSelectedNodes().setSelected(node, true);
        // also update the current item
        graphControl.setCurrentItem(node);
      }
    }
  }


  private void addItemCreationListener() {

    getGraph().addNodeCreatedListener((sender, args) -> {
      if (getAggregateGraph().isAggregationItem(args.getItem())) {
        // add a label with the number of aggregated items to the new aggregation node
        String text = String.valueOf(getAggregateGraph().getAggregatedItems(args.getItem()).size());
        getGraph().addLabel(args.getItem(), text);
      }
    });

    getGraph().addEdgeCreatedListener((sender, args) -> {
      IEdge edge = args.getItem();
      if (!getAggregateGraph().isAggregationItem(edge)) {
        return;
      }

      // add a label with the number of all original aggregated edges represented by the new aggregation edge
      int aggregatedEdgesCount = getAggregateGraph().getAllAggregatedOriginalItems(edge).size();
      if (aggregatedEdgesCount > 1) {
        getGraph().addLabel(edge, String.valueOf(aggregatedEdgesCount));
      }

      // set the thickness to the number of aggregated edges
      PolylineEdgeStyle style = new PolylineEdgeStyle();
      style.setPen(new Pen(Color.GRAY, 1 + aggregatedEdgesCount));

      getGraph().setStyle(edge, style);
    });
  }

  /**
   * For all passed nodes, aggregates all nodes that match the given node by the selector.
   *
   * After the aggregation a layout calculation is run.
   *
   * @param nodes        A list of nodes that should be matched.
   * @param selector     The selector used to match the nodes.
   * @param styleFactory A factory to provide a style for each node type.
   * @param <TKey>       The type by which the nodes shall be selected.
   */
  private <TKey> void aggregateSame(List<INode> nodes, Function<INode, TKey> selector,
                                    Function<TKey, INodeStyle> styleFactory) {
    // get one representative of each kind of node (determined by the selector) ignoring aggregation nodes
    Collection<List<INode>> nodeKindsLists = nodes.stream()
        .filter(n -> !getAggregateGraph().isAggregationItem(n))
        .collect(Collectors.groupingBy(selector)).values();
    List<INode> distinctNodes = nodeKindsLists.stream().map(list -> list.get(0)).collect(Collectors.toList());
    for (INode node : distinctNodes) {
      // aggregate all nodes of the same kind as the representing node
      List<INode> nodesOfSameKind = collectNodesOfSameKind(node, selector);
      aggregate(nodesOfSameKind, selector.apply(node), styleFactory);
    }
    runLayout();
  }

  /**
   * Collects all un-aggregated nodes that match the kind of <code>node</code> by the selector.
   *
   * @param node     The node to provide the kind to match.
   * @param selector The selector for the kind.
   * @param <TKey>   The type of the kind to match.
   *
   * @return A list of all un-aggregated nodes that match the kind of <code>node</code> by the selector.
   */
  private <TKey> List<INode> collectNodesOfSameKind(INode node, Function<INode, TKey> selector) {
    TKey nodeKind = selector.apply(node);
    return getGraph().getNodes().stream()
        .filter(n -> !getAggregateGraph().isAggregationItem(n) && selector.apply(n).equals(nodeKind))
        .collect(Collectors.toList());
  }

  /**
   * Aggregates all nodes of the original graph by the selector and runs the layout.
   *
   * Before aggregating the nodes, all existing aggregations are {@link AggregateGraphWrapper#separateAll() separated}.
   *
   * @param selector     The selector for the kind.
   * @param styleFactory A factory to provide a style for each node type.
   * @param <TKey>       The type of the kind to match.
   */
  private <TKey> void aggregateAll(Function<INode, TKey> selector, Function<TKey, INodeStyle> styleFactory) {
    getAggregateGraph().separateAll();

    Map<TKey, List<INode>> typeToNodes = getGraph().getNodes().stream().collect(Collectors.groupingBy(selector));
    for (Map.Entry<TKey, List<INode>> grouping : typeToNodes.entrySet()) {
      aggregate(grouping.getValue(), grouping.getKey(), styleFactory);
    }

    runLayout();
  }

  /**
   * Aggregates the nodes to a new aggregation node.
   *
   * Adds a label with the number of aggregated nodes and adds labels to all created aggregation edges with the
   * number of replaced original edges.
   *
   * @param nodes        The nodes to aggregate.
   * @param key          The key to aggregate the nodes by.
   * @param styleFactory A factory to provide a style for each node type.
   * @param <TKey>       The type by which the nodes shall be selected.
   */
  private <TKey> void aggregate(Collection<INode> nodes, TKey key, Function<TKey, INodeStyle> styleFactory) {
    SizeD defaultSize = getGraph().getNodeDefaults().getSize();
    double factor = 1 + nodes.size() * 0.2;
    SizeD size = new SizeD(defaultSize.getWidth() * factor, defaultSize.getHeight() * factor);
    RectD layout = RectD.fromCenter(PointD.ORIGIN, size);
    getAggregateGraph().aggregate(IListEnumerable.create(nodes), layout, styleFactory.apply(key));
  }

  /**
   * Separates all <code>nodes</code> and runs the layout afterwards.
   *
   * @param nodes The nodes to separate.
   */
  private void separate(Collection<INode> nodes) {
    AggregateGraphWrapper aggregateGraph = getAggregateGraph();
    for (INode child : nodes) {
      if (aggregateGraph.isAggregationItem(child)) {
        aggregateGraph.separate(child);
      }
    }
    runLayout();
  }

  /**
   * Helper class for aggregation by shape and color.
   */
  private static class ShapeAndPaint {
    public final ShapeNodeShape shape;
    public final Paint paint;

    public ShapeAndPaint(ShapeNodeShape shape, Paint paint) {
      this.shape = shape;
      this.paint = paint;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      ShapeAndPaint that = (ShapeAndPaint) o;

      if (shape != that.shape) {
        return false;
      }
      return paint.equals(that.paint);
    }

    @Override
    public int hashCode() {
      int result = shape.hashCode();
      result = 31 * result + paint.hashCode();
      return result;
    }
  }


  /**
   * Runs an organic layout with edge labeling.
   */
  private void runLayout() {
    GenericLabeling genericLabeling = new GenericLabeling();
    genericLabeling.setEdgeLabelPlacementEnabled(true);
    genericLabeling.setNodeLabelPlacementEnabled(false);
    genericLabeling.setAmbiguityReductionEnabled(true);

    OrganicLayout layout = new OrganicLayout();
    layout.setMinimumNodeDistance(60);
    layout.setAvoidingNodeEdgeOverlapsEnabled(true);
    layout.setLabelingEnabled(true);
    layout.setLabeling(genericLabeling);
    graphControl.morphLayout(layout, Duration.ofSeconds(1));
  }



  public IGraph getGraph() {
    return graphControl.getGraph();
  }

  public AggregateGraphWrapper getAggregateGraph() {
    return (AggregateGraphWrapper) graphControl.getGraph();
  }

  /**
   * Loads a sample graph from GraphML for this demo.
   */
  private void loadSampleGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
