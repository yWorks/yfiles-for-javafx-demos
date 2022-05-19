/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package integration.neo4j;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.builder.EdgesSource;
import com.yworks.yfiles.graph.builder.GraphBuilder;
import com.yworks.yfiles.graph.builder.NodesSource;
import com.yworks.yfiles.graph.labelmodels.EdgePathLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.organic.ChainSubstructureStyle;
import com.yworks.yfiles.layout.organic.CycleSubstructureStyle;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.ParallelSubstructureStyle;
import com.yworks.yfiles.layout.organic.StarSubstructureStyle;
import com.yworks.yfiles.layout.radial.CenterNodesPolicy;
import com.yworks.yfiles.layout.radial.RadialLayout;
import com.yworks.yfiles.layout.radial.RadialLayoutData;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.StyleDecorationZoomPolicy;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.neo4j.driver.AccessMode;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.internal.value.StringValue;
import org.neo4j.driver.types.Entity;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import toolkit.WebViewUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Loads data from a Neo4j database and displays it in a {@link GraphControl}.
 */
public class Neo4JDemo extends Application {
  public GraphControl graphControl;
  public WebView help;

  private Session session;

  /**
   * Initializes the application after its user interface has been built up.
   */
  public void initialize() {
    initializeGraphDefaults();
    initializeHighlighting();
    initializeInputMode();
  }

  /**
   * Initializes the database, loads a graph from it and performs a layouts.
   */
  public void onLoaded() {
    while(true) {
      try {
        initializeDataBase();
        loadGraph();
        break;
      } catch (Exception e) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
        errorAlert.setHeaderText("Could not load initial graph");
        errorAlert.showAndWait();
      }
    }
    doLayout();
  }

  /**
   * Initializes the graph defaults.
   */
  private void initializeGraphDefaults() {
    IGraph graph = graphControl.getGraph();

    // set the default style for nodes
    ShapeNodeStyle defaultNodeStyle = new ShapeNodeStyle();
    defaultNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    defaultNodeStyle.setPaint(Color.LIGHTBLUE);
    graph.getNodeDefaults().setStyle(defaultNodeStyle);

    // and the default size
    graph.getNodeDefaults().setSize(new SizeD(30, 30));

    // and configure node labels to be truncated if they exceed a certain size
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setMaximumSize(new SizeD(116, 36));
    defaultLabelStyle.setTextWrapping(TextWrapping.WRAP);
    graph.getNodeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);

    ExteriorLabelModel newExteriorLabelModel = new ExteriorLabelModel();
    newExteriorLabelModel.setInsets(new InsetsD(5));
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(
        newExteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH));

    // and finally specify the placement policy for edge labels.
    ILabelModelParameter labelModelParameter =
        new EdgePathLabelModel(3, 0, 0, false, EdgeSides.ABOVE_EDGE).createDefaultParameter();
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(labelModelParameter);
  }

  /**
   * Configures highlight styling. See the GraphViewer demo for more details.
   */
  private void initializeHighlighting() {
    Pen orangePen = new Pen(Color.ORANGERED, 3);

    GraphDecorator decorator = graphControl.getGraph().getDecorator();

    ShapeNodeStyle highlightShape = new ShapeNodeStyle();
    highlightShape.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    highlightShape.setPen(orangePen);
    highlightShape.setPaint(null);

    NodeStyleDecorationInstaller nodeStyleHighlight = new NodeStyleDecorationInstaller();
    nodeStyleHighlight.setNodeStyle(highlightShape);
    nodeStyleHighlight.setMargins(new InsetsD(5));
    nodeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
    decorator.getNodeDecorator().getHighlightDecorator().setImplementation(nodeStyleHighlight);

    Arrow dummyCroppingArrow = new Arrow(ArrowType.NONE,null, null, 5, 1);

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(orangePen);
    edgeStyle.setTargetArrow(dummyCroppingArrow);
    edgeStyle.setSourceArrow(dummyCroppingArrow);

    EdgeStyleDecorationInstaller edgeStyleHighlight = new EdgeStyleDecorationInstaller();
    edgeStyleHighlight.setEdgeStyle(edgeStyle);
    edgeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
    decorator.getEdgeDecorator().getHighlightDecorator().setImplementation(edgeStyleHighlight);
  }

  /**
   * Initializes and configures support for (limited) user interaction.
   * Aside from panning and zooming, double-clicking on a node will arrange
   * the graph using the radial layout algorithm.
   */
  private void initializeInputMode() {
    GraphViewerInputMode inputMode = new GraphViewerInputMode();
    inputMode.setClickableItems(GraphItemTypes.NODE);
    inputMode.setFocusableItems(GraphItemTypes.NODE);
    inputMode.setSelectableItems(GraphItemTypes.NONE);
    inputMode.getMarqueeSelectionInputMode().setEnabled(false);

    inputMode.getItemHoverInputMode().setEnabled(true);
    // we are only interested in hover events for nodes and edges
    inputMode.getItemHoverInputMode().setHoverItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    // hovering over other elements should not result in hover events
    inputMode.getItemHoverInputMode().setInvalidItemsDiscardingEnabled(false);

    // when the user hovers over a node, we want to highlight all nodes that are reachable from this node
    inputMode.getItemHoverInputMode().addHoveredItemChangedListener((sender, args) -> {
      // we use the highlight manager of the GraphControl to highlight related items
      HighlightIndicatorManager<IModelItem> manager = graphControl.getHighlightIndicatorManager();
      // first remove previous highlights
      manager.clearHighlights();
      // then see where we are hovering over, now
      if (args.getItem() != null) {
        // we highlight the item itself
        manager.addHighlight(args.getItem());
        // and if it is a node, we highlight all connected edges, too
        if (args.getItem() instanceof INode) {
          graphControl.getGraph().edgesAt((INode) args.getItem()).forEach(manager::addHighlight);
        } else if (args.getItem() instanceof IEdge) {
          // if it is an edge - we highlight the connected nodes
          manager.addHighlight(((IEdge) args.getItem()).getSourceNode());
          manager.addHighlight(((IEdge) args.getItem()).getTargetNode());
        }
      }
    });

    // display a tooltip when the mouse hovers over an item
    inputMode.addQueryItemToolTipListener((sender, args) -> {
      // the neo4j data is stored in the "tag" property of the item
      // if it contains "properties", show them in a simple list
      IModelItem item = args.getItem();
      Entity entity = item != null && item.getTag() instanceof Entity ? ((Entity) item.getTag()) : null;
      Map<String, Object> properties = entity != null  ? entity.asMap() : null;
      if (properties != null && !properties.isEmpty()) {
        StringBuilder tooltipText = new StringBuilder();
        properties.forEach((key, value) -> tooltipText.append(key).append(" : ").append(value).append("\n"));
        args.setToolTip(new Tooltip(tooltipText.toString()));
      }
    });

    // when the user double-clicks on a node, we want to focus that node in a radial layout
    inputMode.addItemDoubleClickedListener((sender, args) -> {
      // clicks could also be on a label, edge, port, etc.
      if (args.getItem() instanceof INode) {
        // tell the engine that we don't want the default action for double-clicks to happen
        args.setHandled(true);
        // we configure the layout data
        RadialLayoutData layoutData = new RadialLayoutData();
        // and tell it to put the item into the center
        layoutData.setCenterNodes((INode) args.getItem());
        // we build the layout algorithm
        RadialLayout layout = new RadialLayout();
        layout.setCenterNodesPolicy(CenterNodesPolicy.CUSTOM);
        // now we calculate the layout and morph the results
        graphControl.morphLayout(layout, Duration.ofSeconds(1), layoutData);
      }
    });

    graphControl.setInputMode(inputMode);
  }

  /**
   * Prompt user for credentials and establish a connection with a neo4j instance
   */
  private void initializeDataBase() {
    Neo4jConfigurationDialog dialog = new Neo4jConfigurationDialog();
   	Optional<DBInformation> result = dialog.showAndWait();
   	result.ifPresent(db -> {

    AuthToken authToken = AuthTokens.basic(db.username, db.password);
    Driver driver = GraphDatabase.driver(db.url, authToken);
    SessionConfig.Builder builder = db.name.isEmpty()
        ? SessionConfig.builder().withDefaultAccessMode(AccessMode.READ)
        : SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).withDatabase(db.name);
    session = driver.session(builder.build());
    });
  }

  /**
   * Loads the graph from the database.
   */
  private void loadGraph() {
    // first we query a limited number of arbitrary nodes
    // modify the query to suit your requirement!
    Result nodeResult = session.run("MATCH (node) RETURN node LIMIT 25");
    // we put the resulting records in a separate list
    List<Node> nodes = nodeResult.stream()
        .map(record -> record.get("node").asNode())
        .collect(Collectors.toList());

    // with the node ids we can query the edges between the nodes
    Long[] nodeIds = nodes.stream().map(Entity::id).toArray(Long[]::new);
    Result edgeResult = session.run(
      "MATCH (n)-[edge]-(m) " +
          "WHERE id(n) IN $nodes " +
          "AND id(m) IN $nodes " +
          "RETURN DISTINCT edge LIMIT 100",
        Values.parameters("nodes", nodeIds)
    );
    // and store the edges in a list
    List<Relationship> edges = edgeResult.stream()
        .map(record -> record.get("edge").asRelationship())
        .collect(Collectors.toList());

    // this triggers the initial construction of the graph
    GraphBuilder graphBuilder = createGraphBuilder(graphControl.getGraph(), nodes, edges);
    graphBuilder.buildGraph();
  }

  /**
   * Returns a GraphBuilder that is configured to work well with Neo4J query results.
   */
  private GraphBuilder createGraphBuilder(IGraph graph, List<Node> nodes, List<Relationship> edges) {
    // now we create the helper class that will help us build the graph from the data
    GraphBuilder graphBuilder = new GraphBuilder(graph);

    // now we pass it the collection of nodes and tell it how to identify the nodes
    NodesSource<Node> nodesSource = graphBuilder.createNodesSource(nodes, Entity::id);
    Map<String, ShapeNodeStyle> nodeToStyle = createNodeStyleMapping(nodes);

    // whenever a node is created...
    nodesSource.getNodeCreator().setStyleProvider(n4jNode -> {
      // look for a mapping for any of the nodes labels and use the mapped style
      for (String labelName : nodeToStyle.keySet()) {
        if (n4jNode.hasLabel(labelName)) {
          return nodeToStyle.get(labelName);
        }
      }
      return nodesSource.getNodeCreator().getDefaults().getStyleInstance();
    });

    // as well as what text to use as the first label for each node
    nodesSource.getNodeCreator().createLabelBinding(node -> {
      // try to find a suitable node label
      String[] candidates = {"name", "title", "firstName", "lastName", "email", "content"};
      for (String candidate : candidates) {
        if (node.containsKey(candidate)) {
          // trim the label
          Value labelValue = node.get(candidate);
          String label = labelValue instanceof StringValue ? labelValue.asString() : labelValue.toString();
          return label.length() > 30 ? label.substring(0, 30) : label;
        }
      }
      return String.join(" - ", node.labels());
    });

    // specify how to identify the source and target nodes - this matches the nodeIdBinding above
    EdgesSource<Relationship> edgesSource = graphBuilder.createEdgesSource(edges, Relationship::startNodeId,
        Relationship::endNodeId);
    // and we display the label, too, using the type of the relationship
    edgesSource.getEdgeCreator().createLabelBinding(Relationship::type);

    return graphBuilder;
  }

  /**
   * Creates a mapping between node labels and node styles.
   */
  private Map<String, ShapeNodeStyle> createNodeStyleMapping(List<Node> nodes) {
    Map<String, Integer> labelCount = new HashMap<>();
    List<String> labels = new ArrayList<>();

    for (Node n4jNode : nodes) {
      for (String label : n4jNode.labels()) {
        if (!(labelCount.containsKey(label))) {
          labelCount.put(label, 0);
          labels.add(label);
        }
        labelCount.put(label, labelCount.get(label) + 1);
      }
    }

    // sort unique labels by their frequency
    labels.sort(Comparator.comparingInt(labelCount::get));
    // define some distinct looking styles
    ShapeNodeStyle[] styles = {
        newShapeNodeStyle(ShapeNodeShape.TRIANGLE, Color.DARKORANGE),
        newShapeNodeStyle(ShapeNodeShape.DIAMOND, Color.LIMEGREEN),
        newShapeNodeStyle(ShapeNodeShape.RECTANGLE, Color.BLUE),
        newShapeNodeStyle(ShapeNodeShape.HEXAGON, Color.DARKVIOLET),
        newShapeNodeStyle(ShapeNodeShape.ELLIPSE, Color.AZURE),
    };
    // map label names to styles
    Map<String, ShapeNodeStyle> labelToStyle = new HashMap<>();
    for (int i = 0; i < labels.size(); i++) {
      labelToStyle.put(labels.get(i), styles[i % styles.length]);
    }
    return labelToStyle;
  }

  private static ShapeNodeStyle newShapeNodeStyle(ShapeNodeShape shape, Color fill) {
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setShape(shape);
    style.setPaint(fill);
    return style;
  }

  /**
   * Applies an organic layout to the current graph. Tries to highlight substructures in the process.
   */
  private void doLayout() {
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setChainSubstructureStyle(ChainSubstructureStyle.STRAIGHT_LINE);
    organicLayout.setCycleSubstructureStyle(CycleSubstructureStyle.CIRCULAR);
    organicLayout.setParallelSubstructureStyle(ParallelSubstructureStyle.STRAIGHT_LINE);
    organicLayout.setStarSubstructureStyle(StarSubstructureStyle.SEPARATED_RADIAL);
    organicLayout.setMinimumNodeDistance(60);
    organicLayout.setNodeLabelConsiderationEnabled(true);
    organicLayout.setNodeSizeConsiderationEnabled(true);
    organicLayout.setDeterministicModeEnabled(true);
    organicLayout.setParallelEdgeRouterEnabled(false);

    LayoutExecutor executor = new LayoutExecutor(graphControl, organicLayout);
    executor.setDuration(Duration.ofSeconds(1));
    executor.setViewportAnimationEnabled(true);
    executor.start();
  }

  private void closeSession() {
    if (session == null) {
      return;
    }

    try {
      session.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  ///////////////////////////////////////////////////////
  //////////// GUI STUFF ////////////////////////////////
  ///////////////////////////////////////////////////////

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Neo4j.fxml"));
    fxmlLoader.setController(this);
    Parent root = fxmlLoader.load();
    WebViewUtils.initHelp(help, this);

    Scene scene = new Scene(root, 1365, 768);

    scene.getStylesheets().add(getClass().getResource("/toolkit/resources/DemoApplication.css").toExternalForm());

    // JavaFX nodes do not have a width or height until the stage is shown and the scene graph is calculated.
    // onLoaded does some initialization that needs the correct bounds of the nodes.
    stage.setOnShown(windowEvent -> onLoaded());
    stage.setOnHidden(windowEvent -> closeSession());

    stage.setTitle("Neo4j Demo - yFiles for JavaFX");
    stage.getIcons().addAll(
        new Image("resources/logo_16.png"),
        new Image("resources/logo_24.png"),
        new Image("resources/logo_32.png"),
        new Image("resources/logo_48.png"),
        new Image("resources/logo_64.png"),
        new Image("resources/logo_128.png"));
    stage.setScene(scene);
    stage.show();
  }
}
