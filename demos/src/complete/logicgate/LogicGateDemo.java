/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.logicgate;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InsideOutsidePortLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortSide;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.polyline.PolylineEdgeRouterData;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.PixelImageExporter;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.DropInputMode;
import com.yworks.yfiles.view.input.EdgeDirectionPolicy;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.IDropCreationCallback;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.ShowPortCandidates;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.IconProvider;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This demo shows the usage of ports by taking the example of a digital system consisted of logic gates.
 */
public class LogicGateDemo extends DemoApplication {

  // the XML namespace that corresponds to the demo package.
  private static final String YFILES_DEMO_NS = "http://www.yworks.com/xml/yfiles-logicgate/java/1.0";

  // the default namespace prefix for {@link #YFILES_DEMO_NS}
  private static final String YFILES_DEMO_PREFIX = "demo";

  // the default style
  private static final LogicGateNodeStyle DEFAULT_STYLE = new LogicGateNodeStyle(LogicGateType.AND);

  private static final WritableImage EMPTY_IMAGE = new WritableImage(1, 1);

  @FXML private GraphControl graphControl;
  @FXML private WebView helpView;
  @FXML private ListView<INode> palette;
  @FXML private ComboBox<NamedEntry> edgeDirectionPolicyComboBox;
  @FXML private Button hlButton;
  @FXML private Button erButton;
  @FXML private ToolBar toolBar;


  public void initialize() {
    WebViewUtils.initHelp(helpView, this);
    configureToolBar();
  }

  /**
   * Called when the application frame is displayed.
   * This method initializes the graph and the input mode.
   */
  @Override
  public void onLoaded() {
    initializeGraphControl();
    initializeGraph();
    initializeInputModes();
    configureEdgeDirectionPolicyComboBox();
    populateNodesList();
  }

  /**
   * Enables file IO and specifies a namespace for saving types of this demo.
   */
  private void initializeGraphControl() {
    // enable opening and saving files
    graphControl.setFileIOEnabled(true);

    // map the classes in the complete.logicgate package to a separate demo namespace
    GraphMLIOHandler ioHandler = graphControl.getGraphMLIOHandler();
    ioHandler.addXamlNamespaceMapping(YFILES_DEMO_NS, LogicGateNodeStyle.class);
    ioHandler.addNamespace(YFILES_DEMO_NS, YFILES_DEMO_PREFIX);
  }

  /**
   * Initializes the graph instance by setting default styles and loading a small sample graph.
   */
  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();

    // set the default style for all new nodes
    graph.getNodeDefaults().setStyle(DEFAULT_STYLE);
    graph.getNodeDefaults().setSize(new SizeD(50, 30));

    // set the default style for all new node labels
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(Font.font(10));
    graph.getNodeDefaults().getLabelDefaults().setStyle(labelStyle);

    // set the default style for all new edge labels
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setSourceArrow(new Arrow(ArrowType.NONE));
    edgeStyle.setTargetArrow(new Arrow(ArrowType.NONE));
    Pen pen = new Pen(Color.BLACK, 3);
    pen.setLineCap(StrokeLineCap.SQUARE);
    edgeStyle.setPen(pen);
    graph.getEdgeDefaults().setStyle(edgeStyle);

    // disable edge cropping
    graph.getDecorator().getPortDecorator().getEdgePathCropperDecorator().hideImplementation();

    // don't delete ports a removed edge was connected to
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);

    // set a custom port candidate provider
    graph.getDecorator().getNodeDecorator().getPortCandidateProviderDecorator().setImplementation(
        new DescriptorDependentPortCandidateProvider());

    // read initial graph from embedded resource
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml").toExternalForm());
    } catch (IOException e) {
      e.printStackTrace();
    }

    // apply a new hierarchic layout
    applyHierarchicLayout();
  }

  /**
   * Calls {@link #createEditorMode()} and registers the result as the {@link CanvasControl#setInputMode(IInputMode)}.
   */
  private void initializeInputModes() {
    graphControl.setInputMode(createEditorMode());
  }

  /**
   * Creates the default input mode for the {@link GraphControl}, a {@link GraphEditorInputMode}.
   * The control uses a custom node creation callback that creates business objects for newly created nodes.
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    // don't allow nodes to be created using a mouse click
    mode.setCreateNodeAllowed(false);
    // don't allow bends to be created using a mouse drag on an edge
    mode.setCreateBendAllowed(false);
    // disable node resizing
    mode.setShowHandleItems(GraphItemTypes.BEND.or(GraphItemTypes.EDGE));
    // enable orthogonal edge creation and editing
    mode.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());
    // enable drag and drop
    mode.getNodeDropInputMode().setEnabled(true);
    // disable moving labels
    mode.getMoveLabelInputMode().setEnabled(false);
    // enable snapping for easier orthogonal edge editing
    mode.setSnapContext(new GraphSnapContext());

    // wrap the original node creator so it copies the ports and labels from the dragged node
    IDropCreationCallback<INode> originalNodeCreator = mode.getNodeDropInputMode().getItemCreator();
    mode.getNodeDropInputMode().setItemCreator((context, graph, draggedNode, dropTarget, layout) -> {
      if (draggedNode != null) {
        SimpleNode simpleNode = new SimpleNode();
        simpleNode.setStyle(draggedNode.getStyle());
        simpleNode.setLayout(draggedNode.getLayout());
        INode newNode = originalNodeCreator.createItem(context, graph, simpleNode, dropTarget, layout);
        // copy the ports
        for (IPort port : draggedNode.getPorts()) {
          PortDescriptor descriptor = (PortDescriptor) port.getTag();

          // specify port style using a node style
          ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
          nodeStyle.setPaint(descriptor.getEdgeDirection() == PortDescriptor.EdgeDirection.IN ?
              Color.GREEN : Color.DODGERBLUE);
          nodeStyle.setPen(null);
          nodeStyle.setShape(ShapeNodeShape.RECTANGLE);

          NodeStylePortStyleAdapter portStyle = new NodeStylePortStyleAdapter(nodeStyle);
          portStyle.setRenderSize(new SizeD(5, 5));

          IPort newPort = graph.addPort(newNode, port.getLocationParameter(), portStyle, port.getTag());

          // create the port labels
          ILabelModelParameter parameter = new InsideOutsidePortLabelModel().createOutsideParameter();
          ILabel label = graph.addLabel(newPort, descriptor.getLabelText(), parameter);
          label.setTag(descriptor);
        }
        // copy the labels
        for (ILabel label : draggedNode.getLabels()) {
          ILabel newLabel = graph.addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle());
          newLabel.setTag(label.getTag());
        }
        return newNode;
      }
      // fallback
      return originalNodeCreator.createItem(context, graph, draggedNode, dropTarget, layout);
    });

    mode.getCreateEdgeInputMode().addEdgeCreatedListener((source, args) -> {
      if (args.getSourcePort().getLabels().size() > 0) {
        ILabel sourcePortLabel = args.getSourcePort().getLabels().first();
        replaceLabelModel(args.getSourcePort(), sourcePortLabel);
      }
      if (args.getTargetPort().getLabels().size() > 0) {
        ILabel targetPortLabel = args.getTargetPort().getLabels().first();
        replaceLabelModel(args.getTargetPort(), targetPortLabel);
      }
    });

    // only allow starting an edge creation over a valid port candidate
    mode.getCreateEdgeInputMode().setStartingOverCandidateOnlyEnabled(true);

    // show all port candidates when hovering over a node
    mode.getCreateEdgeInputMode().setShowPortCandidates(ShowPortCandidates.ALL);

    graphControl.getGraph().addEdgeRemovedListener((source, args) -> {
      if (args.getSourcePort().getLabels().size() > 0 &&
          graphControl.getGraph().edgesAt(args.getSourcePort()).size() == 0) {
        ILabel sourcePortLabel = args.getSourcePort().getLabels().first();
        graphControl.getGraph().setLabelLayoutParameter(sourcePortLabel,
            new InsideOutsidePortLabelModel().createOutsideParameter());
      }
      if (args.getTargetPort().getLabels().size() > 0 &&
          graphControl.getGraph().edgesAt(args.getTargetPort()).size() == 0) {
        ILabel targetPortLabel = args.getTargetPort().getLabels().first();
        graphControl.getGraph().setLabelLayoutParameter(targetPortLabel,
            new InsideOutsidePortLabelModel().createOutsideParameter());
      }
    });

    return mode;
  }

  private void replaceLabelModel(IPort port, ILabel label) {
    PortDescriptor descriptor = (PortDescriptor) port.getTag();
    graphControl.getGraph().setLabelLayoutParameter(label, descriptor.getLabelPlacementWithEdge());
  }

  /**
   * Configures the {@link ComboBox} for choosing the {@link EdgeDirectionPolicy}.
   */
  private void configureEdgeDirectionPolicyComboBox() {
    edgeDirectionPolicyComboBox.getItems().addAll(
        new NamedEntry("Start at source", EdgeDirectionPolicy.START_AT_SOURCE),
        new NamedEntry("Start at target", EdgeDirectionPolicy.START_AT_TARGET),
        new NamedEntry("Keep direction", EdgeDirectionPolicy.KEEP_DIRECTION),
        new NamedEntry("Determine from port candidates", EdgeDirectionPolicy.DETERMINE_FROM_PORT_CANDIDATES)
    );

    edgeDirectionPolicyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      CreateEdgeInputMode createEdgeInputMode = ((GraphEditorInputMode) graphControl.getInputMode()).getCreateEdgeInputMode();
      createEdgeInputMode.setEdgeDirectionPolicy(newValue.value);
    });

    edgeDirectionPolicyComboBox.getSelectionModel().select(0);
  }



  /**
   * Fill the node list that acts as a source for nodes.
   */
  private void populateNodesList() {
    // Create a new Graph in which the palette nodes live and copy all relevant settings
    IGraph nodeContainer = new DefaultGraph();
    nodeContainer.getNodeDefaults().setStyle(DEFAULT_STYLE);
    nodeContainer.getNodeDefaults().setSize(new SizeD(50, 30));

    // Create some nodes
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.AND, "AND", null);
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.NAND, "NAND", null);
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.OR, "OR", null);
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.NOR, "NOR", null);
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.NOT, "NOT", null);
    // Create an IC
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.TIMER, "555", new SizeD(70, 120));
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.AD_CONVERTER, "2-bit A/D\nConverter", new SizeD(70, 120));

    // maps transfer data (text that serves as IDs) to node templates from which
    // to create nodes on drop
    GraphEditorInputMode geim = (GraphEditorInputMode) graphControl.getInputMode();
    Map nodeDropData = geim.getNodeDropInputMode().getDropDataMap();

    // fill the model of the palette with sample nodes
    for (INode node : nodeContainer.getNodes()) {
      palette.getItems().add(node);
      nodeDropData.put(node.getLabels().first().getText(), node);
    }

    // set a cell factory for NodeTemplates to use in the palette
    palette.setCellFactory(palette -> {
      NodeListCell cell = new NodeListCell();
      // when the user starts dragging a node in the palette, setup the DnD information
      cell.setOnDragDetected(event -> {
        // start the drag
        Dragboard db = palette.startDragAndDrop(TransferMode.ANY);
        Map<DataFormat, Object> contentMap = new HashMap<>();

        // use the name of the selected template as content.
        contentMap.put(DropInputMode.DATA_FORMAT_DROP_ID, palette.getSelectionModel().getSelectedItem().getLabels().first().getText());
        db.setContent(contentMap);
        // to prevent a semi-transparent paper appears above the dragged node on MacOSX,
        // we set the drag view to a blank image
        db.setDragView(EMPTY_IMAGE);
        event.consume();
      });

      cell.setOnDragDone(Event::consume);
      return cell;
    });
  }

  /**
   * Creates a node of the specified type.
   * The method will specify the ports that the node should have based on its type.
   */
  private void createNode(IGraph graph, PointD location, LogicGateType type, String label, SizeD size) {
    RectD newBounds = RectD.fromCenter(location, graph.getNodeDefaults().getSize());
    INode node;
    if (type == LogicGateType.TIMER || type == LogicGateType.AD_CONVERTER) {
      ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
      nodeStyle.setPen(new Pen(Color.BLACK, 2));
      node = graph.createNode(RectD.fromCenter(location, size), nodeStyle);
    } else {
      node = graph.createNode(newBounds, new LogicGateNodeStyle(type));
    }

    graph.addLabel(node, label, InteriorLabelModel.CENTER);

    PortDescriptor[] portDescriptors = PortDescriptor.createPortDescriptors(type);

    // use relative port locations
    FreeNodePortLocationModel model = new FreeNodePortLocationModel();

    // add ports for all descriptors using the descriptor as the tag of the port
    for (PortDescriptor descriptor : portDescriptors) {
      // use the descriptor's location as offset
      IPortLocationModelParameter portLocationModelParameter =
          model.createParameter(PointD.ORIGIN, new PointD(descriptor.getX(), descriptor.getY()));
      IPort port = graph.addPort(node, portLocationModelParameter);
      port.setTag(descriptor);
    }
  }



  @FXML private void resetZoom(){
    graphControl.setZoom(1);
  }

  @FXML private void exit() {
    Platform.exit();
  }



  @FXML private void configureToolBar() {
    toolBar.getItems().add(new Separator());

    hlButton = new Button("Hierarchic Layout", new ImageView(IconProvider.LAYOUT_HIERARCHIC));
    hlButton.setOnAction(event -> applyHierarchicLayout());
    toolBar.getItems().add(hlButton);

    erButton = new Button("Route Edges", new ImageView(IconProvider.LAYOUT_TREE));
    erButton.setOnAction(event -> applyEdgeRouting());
    toolBar.getItems().add(erButton);
  }

  @FXML private void applyHierarchicLayout() {
    HierarchicLayout hl = new HierarchicLayout();
    hl.setOrthogonalRoutingEnabled(true);
    hl.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    hl.setNodeLabelConsiderationEnabled(true);

    HierarchicLayoutData hlData = new HierarchicLayoutData();
    configurePortConstraints(hlData);

    applyLayout(hl, hlData, true);
  }

  @FXML private void applyEdgeRouting() {
    EdgeRouter er = new EdgeRouter();
    er.setNodeLabelConsiderationEnabled(true);

    PolylineEdgeRouterData erData = new PolylineEdgeRouterData();
    configurePortConstraints(erData);

    applyLayout(er, erData, false);
  }

  private void configurePortConstraints( LayoutData layoutData ) {
    // outgoing edges must be routed to the right of the node
    // we use the same value for all edges, which is a strong port constraint that forces
    // the edge to leave at the east (right) side
    PortConstraint east = PortConstraint.create(PortSide.EAST, true);
    // incoming edges must be routed to the left of the node
    // we use the same value for all edges, which is a strong port constraint that forces
    // the edge to enter at the west (left) side
    PortConstraint west = PortConstraint.create(PortSide.WEST, true);

    Function<IEdge, PortConstraint> sourceFunction =
            edge -> ((PortDescriptor) edge.getSourcePort().getTag()).getX() == 0 ? west : east;
    Function<IEdge, PortConstraint> targetFunction =
            edge -> ((PortDescriptor) edge.getTargetPort().getTag()).getX() == 0 ? west : east;

    if (layoutData instanceof HierarchicLayoutData) {
      HierarchicLayoutData hlData = (HierarchicLayoutData) layoutData;
      hlData.setSourcePortConstraints(sourceFunction);
      hlData.setTargetPortConstraints(targetFunction);
    } else if (layoutData instanceof PolylineEdgeRouterData) {
      PolylineEdgeRouterData erData = (PolylineEdgeRouterData) layoutData;
      erData.setSourcePortConstraints(sourceFunction);
      erData.setTargetPortConstraints(targetFunction);
    }
  }

  /**
   * Perform the layout operation.
   */
  private void applyLayout(ILayoutAlgorithm layout, LayoutData layoutData, boolean animateViewport) {
    // layout starting, disable button
    hlButton.setDisable(true);
    erButton.setDisable(true);
    // do the layout
    LayoutExecutor executor = new LayoutExecutor(graphControl, layout);
    executor.setLayoutData(layoutData);
    executor.setDuration(Duration.ofSeconds(1));
    executor.setViewportAnimationEnabled(animateViewport);
    executor.addLayoutFinishedListener( (source, args) -> {
      // layout finished, enable layout button again
      hlButton.setDisable(false);
      erButton.setDisable(false);
    });
    executor.start();
  }


  /**
   * A {@link ListCell} that shows images for a given {@link INode} in a {@link ListView}.
   */
  private static final class NodeListCell extends ListCell<INode> {
    private static final int MAX_SIZE = 120;

    @Override
    public void updateItem( INode node, boolean empty) {
      super.updateItem(node, empty);

      if (empty) {
        // what to do if the cell has no template
        setGraphic(null);
        setText(null);
      } else {
        setGraphic(createImage(node));
        setText(null);
      }
    }

    /**
     * Creates an image showing a node applying the template.
     */
    private static Node createImage(INode node) {
      // create a GraphControl instance and add a copy of the given node with its labels
      GraphControl graphControl = new GraphControl();
      IGraph graph = graphControl.getGraph();
      RectD newLayout = new RectD(0, 0, Math.min(MAX_SIZE, node.getLayout().getWidth()), Math.min(MAX_SIZE, node.getLayout().getHeight()));
      INode newNode = graph.createNode(newLayout, node.getStyle(), node.getTag());
      node.getLabels().forEach(label ->
          graph.addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle(), label.getPreferredSize(), label.getTag()));
      node.getPorts().forEach(port ->
          graph.addPort(newNode, port.getLocationParameter(), port.getStyle()));

      // render the graph control in an image
      graphControl.updateContentRect();
      PixelImageExporter pixelImageExporter = new PixelImageExporter(graphControl.getContentRect().getEnlarged(2));
      pixelImageExporter.setBackgroundFill(Color.TRANSPARENT);
      WritableImage image = pixelImageExporter.exportToBitmap(graphControl);

      // put the image into a pane to center it
      StackPane pane = new StackPane();
      pane.getChildren().add(new ImageView(image));
      return pane;
    }
  }

  /**
   * Name-value struct for combo box entries.
   */
  private static class NamedEntry {
    final String displayName;
    final EdgeDirectionPolicy value;

    NamedEntry(String displayName, EdgeDirectionPolicy value) {
      this.displayName = displayName;
      this.value = value;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }
}
