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
package complete.bpmn.editor;

import complete.bpmn.layout.BpmnLayout;
import complete.bpmn.layout.LayoutOrientation;
import complete.bpmn.view.ActivityNodeStyle;
import complete.bpmn.view.AnnotationLabelStyle;
import complete.bpmn.view.AnnotationNodeStyle;
import complete.bpmn.view.BpmnConstants;
import complete.bpmn.view.BpmnEdgeStyle;
import complete.bpmn.view.BpmnLayoutData;
import complete.bpmn.view.BpmnNodeStyle;
import complete.bpmn.view.BpmnPortCandidateProvider;
import complete.bpmn.view.ChoreographyLabelModel;
import complete.bpmn.view.ChoreographyNodeStyle;
import complete.bpmn.view.ConversationNodeStyle;
import complete.bpmn.view.ConversationType;
import complete.bpmn.view.DataObjectNodeStyle;
import complete.bpmn.view.DataStoreNodeStyle;
import complete.bpmn.view.EdgeType;
import complete.bpmn.view.EventNodeStyle;
import complete.bpmn.view.EventPortStyle;
import complete.bpmn.view.GatewayNodeStyle;
import complete.bpmn.view.GroupNodeStyle;
import complete.bpmn.view.MessageLabelStyle;
import complete.bpmn.view.Participant;
import complete.bpmn.view.PoolHeaderLabelModel;
import complete.bpmn.view.PoolNodeStyle;
import complete.bpmn.view.config.ActivityNodeStyleConfiguration;
import complete.bpmn.view.config.AnnotationNodeStyleConfiguration;
import complete.bpmn.view.config.BpmnEdgeStyleConfiguration;
import complete.bpmn.view.config.ChoreographyNodeStyleConfiguration;
import complete.bpmn.view.config.ConversationNodeStyleConfiguration;
import complete.bpmn.view.config.DataObjectNodeStyleConfiguration;
import complete.bpmn.view.config.EdgeStyleConfiguration;
import complete.bpmn.view.config.EventNodeStyleConfiguration;
import complete.bpmn.view.config.GatewayNodeStyleConfiguration;
import complete.bpmn.view.config.NodeStyleConfiguration;
import complete.bpmn.view.config.PoolNodeStyleConfiguration;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IEdgeDefaults;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IRow;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.ITable;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.StripeTypes;
import com.yworks.yfiles.graph.Table;
import com.yworks.yfiles.graph.labelmodels.CompositeLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.VoidStripeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.MoveViewportInputMode;
import com.yworks.yfiles.view.input.NodeAlignmentPolicy;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import com.yworks.yfiles.view.input.PortCandidateValidity;
import com.yworks.yfiles.view.input.ReparentNodeHandler;
import com.yworks.yfiles.view.input.ReparentStripeHandler;
import com.yworks.yfiles.view.input.SelectionEventArgs;
import com.yworks.yfiles.view.input.StripeSubregion;
import com.yworks.yfiles.view.input.StripeSubregionTypes;
import com.yworks.yfiles.view.input.TableEditorInputMode;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;
import toolkit.optionhandler.OptionEditor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This demo shows how an editor for business process diagrams can be created using yFiles for JavaFX.
 *
 * <p>The visualization and business logic is based on the BPMN 1.1 specification but isn't meant to
 * implement all aspects of the specification but to demonstrate what techniques offered by
 * yFiles for Java can be used to create such an editor:
 * </p>
 * <p>
 * <ul>
 * <li>
 * Custom NodeStyles
 * </li>
 * <li>
 * Custom EdgeStyle with custom Arrows
 * </li>
 * <li>
 * Usage of group node insets: Group nodes make use of the {@link INodeInsetsProvider} interface to define
 * what insets they want to have. These insets are used e.g. during the layout.
 * </li>
 * <li>
 * Node creation via Drag'n'Drop: Like in the DragNDrop demo it is shown how a drag'n'drop mechanism can be used by
 * the user to generate nodes with different default styles.
 * </li>
 * <li>
 * Usage of a PortCandidateProvider: The BPMN specification regulates what type of relations are allowed between what
 * type of diagram elements. How the creation of an edge as well as the relation of one of its ports can be restricted
 * to follow this specification is demonstrated using PortCandidateProvider.
 * </li>
 * <li>
 * Usage of Tables: This demo showcases how table nodes can be used for visualization and interaction. It is
 * demonstrated how the layout can be made aware of the table nodes.
 * </li>
 * </ul>
 * </p>
 */
public class BpmnEditorDemo extends DemoApplication {

  public ComboBox<String> graphChooserBox;
  private boolean inLoadSample;
  public VBox palette;
  public GraphControl graphControl;
  public WebView webView;
  private TableEditorInputMode tableEditorInputMode;

  // input mode that handles node dragging
  private NodeDropInputMode nodeDropInputMode;

  public Pane editorPane;
  private OptionEditor builder = new OptionEditor();
  public Label styleOptionLabel;

  private static final HashMap<Class, NodeStyleConfiguration> styleConfigurationHashMap;
  private static final BpmnEdgeStyleConfiguration bpmnEdgeStyleConfiguration = new BpmnEdgeStyleConfiguration();

  static {
    // initialize the map with the style configurations for the different BPMN node styles
    styleConfigurationHashMap = new HashMap<>();
    styleConfigurationHashMap.put(ActivityNodeStyle.class, new ActivityNodeStyleConfiguration());
    styleConfigurationHashMap.put(AnnotationNodeStyle.class, new AnnotationNodeStyleConfiguration());
    styleConfigurationHashMap.put(ChoreographyNodeStyle.class, new ChoreographyNodeStyleConfiguration());
    styleConfigurationHashMap.put(ConversationNodeStyle.class, new ConversationNodeStyleConfiguration());
    styleConfigurationHashMap.put(DataObjectNodeStyle.class, new DataObjectNodeStyleConfiguration());
    styleConfigurationHashMap.put(EventNodeStyle.class, new EventNodeStyleConfiguration());
    styleConfigurationHashMap.put(GatewayNodeStyle.class, new GatewayNodeStyleConfiguration());
    styleConfigurationHashMap.put(PoolNodeStyle.class, new PoolNodeStyleConfiguration());
  }

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph is available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // add the names of the sample diagrams to the combo box
    graphChooserBox.getItems().addAll(
        "Business",
        "Collaboration",
        "Different Exception Flows",
        "Expanded Subprocess",
        "Lanes Segment",
        "Lanes with Information Systems",
        "Matrix Lanes",
        "Process Normal Flow",
        "Project Application",
        "Simple BPMN Model",
        "Vertical Swimlanes");

    // load the help pane
    WebViewUtils.initHelp(webView, this);

    // initialize the graph
    initializeGraph();

    // initialize the input mode
    initializeInputModes();

    // setup the palette to drag nodes from
    configureNodePalette();

    // fit graph bounds when the window is resized
    graphControl.layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
        Platform.runLater(graphControl::fitGraphBounds));
  }

  /**
   * Initializes the graph instance and sets default styles.
   */
  public void initializeGraph() {
    GraphMLIOHandler ioh = new GraphMLIOHandler();

    // we set the IO handler on the GraphControl, so the GraphControl's IO methods
    // will pick up our handler for use during serialization and deserialization.
    graphControl.setGraphMLIOHandler(ioh);

    // map the classes in the bpmn.view package to a separate bpmn namespace
    ioh.addXamlNamespaceMapping(BpmnConstants.YFILES_BPMN_NS, BpmnNodeStyle.class);
    ioh.addNamespace(BpmnConstants.YFILES_BPMN_NS, BpmnConstants.YFILES_BPMN_PREFIX);

    // after loading a sample diagram, the item proerty pane should be cleared
    ioh.addParsedListener((source, args) -> clearOptionPane("No items selected"));

    FoldingManager manager = new FoldingManager();
    IFoldingView foldingView = manager.createFoldingView();
    IGraph graph = foldingView.getGraph();
    graphControl.setGraph(graph);
    // update the option pane if a node is collapsed or expanded because it may have different properties in those states.
    foldingView.addGroupCollapsedListener((source, args) -> updateOptionPane(graphControl.getSelection()));
    foldingView.addGroupExpandedListener((source, args) -> updateOptionPane(graphControl.getSelection()));

    // Ports should not be removed when an attached edge is deleted
    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.getPortDefaults().setAutoCleanUpEnabled(false);

    // Set default styles and label model parameter
    IEdgeDefaults edgeDefaults = graph.getEdgeDefaults();
    BpmnEdgeStyle bpmnEdgeStyle = new BpmnEdgeStyle();
    bpmnEdgeStyle.setType(EdgeType.SEQUENCE_FLOW);
    edgeDefaults.setStyle(bpmnEdgeStyle);
    edgeDefaults.setStyleInstanceSharingEnabled(false);
    edgeDefaults.getLabelDefaults().setLayoutParameter(
        new EdgeSegmentLabelModel(10, 0, 0, true, EdgeSides.ABOVE_EDGE).createDefaultParameter());
    // For nodes we use a CompositeLabelModel that combines label placements inside and outside of the node
    CompositeLabelModel compositeLabelModel = new CompositeLabelModel();
    compositeLabelModel.getLabelModels().add(new InteriorLabelModel());
    ExteriorLabelModel exteriorLabelModel = new ExteriorLabelModel();
    exteriorLabelModel.setInsets(new InsetsD(10));
    compositeLabelModel.getLabelModels().add(exteriorLabelModel);
    nodeDefaults.getLabelDefaults().setLayoutParameter(compositeLabelModel.createDefaultParameter());

    graph.getGroupNodeDefaults().setStyle(new GroupNodeStyle());

    // use a specialized port candidate provider
    GraphDecorator decorator = manager.getMasterGraph().getDecorator();
    decorator.getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
        node -> (node.getStyle() instanceof BpmnNodeStyle || node.getStyle() instanceof GroupNodeStyle),
        BpmnPortCandidateProvider::new);
    // Pools only have a dynamic PortCandidate
    decorator.getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
        node -> (node.getStyle() instanceof PoolNodeStyle),
        node -> {
          DefaultPortCandidate candidate = new DefaultPortCandidate(node);
          candidate.setValidity(PortCandidateValidity.DYNAMIC);
          return IPortCandidateProvider.fromCandidates(candidate);
        });

    // allow reconnecting of edges
    decorator.getEdgeDecorator().getEdgeReconnectionPortCandidateProviderDecorator().setImplementation(
        IEdgeReconnectionPortCandidateProvider.ALL_NODE_CANDIDATES);

    // enable undo operations
    manager.getMasterGraph().setUndoEngineEnabled(true);
    // use the undo support from the graph also for all future table instances
    Table.installStaticUndoSupport(manager.getMasterGraph());
  }
  
  /**
   * Calls {@link #createEditorMode()}  and registers
   * the result as the {@link com.yworks.yfiles.view.CanvasControl#getInputMode()}.
   */
  public void initializeInputModes() {
    graphControl.setFileIOEnabled(true);
    graphControl.setInputMode(createEditorMode());
  }
  
  /**
   * Creates the default input mode for the graphControl,
   * @see GraphEditorInputMode
   * @return a new GraphEditorInputMode instance and configures snapping and orthogonal edge editing
   */
  public IInputMode createEditorMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    // enable grouping operations
    geim.setGroupingOperationsAllowed(true);
    // We want orthogonal edge creation and editing
    geim.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());

    // don't allow node creation (except for context menu and drag'n'drop)
    geim.setCreateNodeAllowed(false);
    // Alter the ClickHitTestOrder so ports are tested before nodes
    geim.setClickHitTestOrder(
            GraphItemTypes.BEND, GraphItemTypes.EDGE_LABEL, GraphItemTypes.EDGE,
            GraphItemTypes.PORT, GraphItemTypes.NODE, GraphItemTypes.NODE_LABEL
    );

    // Enable snapping
    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setEdgeToEdgeDistance(10);
    snapContext.setNodeToEdgeDistance(15);
    snapContext.setNodeToNodeDistance(20);
    snapContext.setSnappingBendsToSnapLinesEnabled(true);
    geim.setSnapContext(snapContext);
    // tables shall not become child nodes but reparenting for other nodes is always enabled
    ReparentNodeHandler reparentNodeHandler = new NoTableReparentNodeHandler();
    reparentNodeHandler.setReparentRecognizer(IEventRecognizer.ALWAYS);
    geim.setReparentNodeHandler(reparentNodeHandler);
    // we use a default MoveViewportInputMode that allows us to drag the viewport without pressing 'SHIFT'
    MoveViewportInputMode moveViewportInputMode = new MoveViewportInputMode();
    geim.setMoveViewportInputMode(moveViewportInputMode);
    // increase the priority value of the MoveViewportInputMode so other input modes are still preferred.
    moveViewportInputMode.setPriority(110);
    // disable marquee selection so the MoveViewportInputMode can work without modifiers
    geim.getMarqueeSelectionInputMode().setEnabled(false);

    // ensure that collapsing/expanding nodes does not move the nodes
    geim.getNavigationInputMode().setAutoGroupNodeAlignmentPolicy(NodeAlignmentPolicy.BOTTOM_CENTER);

    // Create a new TEIM instance which also allows drag and drop
    tableEditorInputMode = new TableEditorInputMode();
    // Enable drag & drop of stripes
    tableEditorInputMode.getStripeDropInputMode().setEnabled(true);
    // Maximal level for both reparent and drag and drop is 2
    ReparentStripeHandler reparentStripeHandler = new ReparentStripeHandler();
    reparentStripeHandler.setMaxColumnLevel(2);
    reparentStripeHandler.setMaxRowLevel(2);
    tableEditorInputMode.setReparentStripeHandler(reparentStripeHandler);
    // Add to GEIM - we set the priority higher than for the handle input mode so that handles win if both gestures are possible
    tableEditorInputMode.setPriority(geim.getHandleInputMode().getPriority() + 1);
    geim.add(tableEditorInputMode);

    // Palette drag and drop: configure node drop operations
    configureNodeDropping(geim);

    // setup the context menu
    initializeContextMenu(geim);

    // bind selection changes to property pane
    geim.addMultiSelectionFinishedListener(this::onSelectionChanged);
    tableEditorInputMode.getStripeSelection().addItemSelectionChangedListener((source, args) -> clearOptionPane("No Properties to show"));

    // initialize additional input bindings
    initializeInputBindings(geim.getKeyboardInputMode());
    return geim;
  }

  /**
   * Enables support for dropping nodes on the given {@link GraphEditorInputMode}.
   * @param editorInputMode The GraphEditorInputMode for this application.
   */
  private void configureNodeDropping(GraphEditorInputMode editorInputMode) {
    // dropping nodes from the palette
    nodeDropInputMode = new MyNodeDropInputMode();
    nodeDropInputMode.setEnabled(true);
    // We identify the group nodes during a drag by either a custom tag or if they have a table associated.
    nodeDropInputMode.setIsGroupNodePredicate(
        node -> node.lookup(ITable.class) != null || node.getTag() == "GroupNode");

    editorInputMode.setNodeDropInputMode(nodeDropInputMode);
  }

  private void onSelectionChanged(Object source, SelectionEventArgs<IModelItem> args) {
    updateOptionPane(args.getSelection());
  }

  private void updateOptionPane(ISelectionModel<IModelItem> selection) {
    // only show the properties if a single node or edge is selected
    if (selection.size() > 1) {
      clearOptionPane("Multiple Items selected");
      return;
    } else if (selection.size() == 0) {
      clearOptionPane("No Item selected");
      return;
    } else {
      IModelItem item = selection.stream().findFirst().orElse(null);
      if (item instanceof INode) {
        INode node = (INode) item;
        INodeStyle style = node.getStyle();
        if (styleConfigurationHashMap.containsKey(style.getClass())) {
          // lookup the style configuration matching the node's style and initialize it with the current style properties
          NodeStyleConfiguration configuration = styleConfigurationHashMap.get(style.getClass());
          configuration.initializeFromExistingStyle(style);
          // update the Item Properties pane using an OptionEditor
          builder.setConfiguration(configuration);
          editorPane.getChildren().clear();
          editorPane.getChildren().add(builder.buildEditor());
          setLabel(configuration.getClass());
          return;
        }
      } else if (item instanceof IEdge) {
        IEdge edge = (IEdge) item;
        IEdgeStyle style = edge.getStyle();
        if (style instanceof BpmnEdgeStyle) {
          // initialize the edge style configuration with the current edge style properties
          bpmnEdgeStyleConfiguration.initializeFromExistingStyle((BpmnEdgeStyle) style);
          // update the Item Properties pane using an OptionEditor
          builder.setConfiguration(bpmnEdgeStyleConfiguration);
          editorPane.getChildren().clear();
          editorPane.getChildren().add(builder.buildEditor());
          setLabel(bpmnEdgeStyleConfiguration.getClass());
          return;
        }
      }
    }
    clearOptionPane("No Properties to show");
  }

  private void clearOptionPane(String text) {
    builder.setConfiguration(null);
    editorPane.getChildren().clear();
    styleOptionLabel.setText(text);
  }

  private void setLabel(Class classobject) {
    // update the label in the Item Properties pane to show the type of the node or edge whose properties are displayed
    Annotation labelAnnotation = classobject.getDeclaredAnnotation(toolkit.optionhandler.Label.class);
    String label;
    if (labelAnnotation != null) {
      toolkit.optionhandler.Label attr = (toolkit.optionhandler.Label) labelAnnotation;
      label = attr.value();
    } else {
      label = classobject.getName();
    }
    styleOptionLabel.setText(label);
  }

  /**
   * Initializes additional input bindings for running the layout, creating an empty graph component and
   * applying/resetting the style properties in the Item Properties pane.
   */
  private void initializeInputBindings(KeyboardInputMode kim) {
    kim.addCommandBinding(RUN_LAYOUT, this::executeLayout, this::canExecuteLayout);
    kim.addCommandBinding(ICommand.NEW, this::executeNewCommand, this::canExecuteNewCommand);

    kim.addCommandBinding(APPLY_STYLE, this::executeApplyStyleCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(RESET_STYLE, this::executeResetStyleCommand, this::canExecuteChangeCommand);
  }

  private void initializeContextMenu(GraphEditorInputMode geim) {
    // open context menu when node or edge is clicked
    geim.setContextMenuItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    geim.addPopulateItemContextMenuListener(this::onPopulateItemContextMenu);
  }
  
  /**
   * Builds different popup menus depending on the node or edge type
   */
  private void onPopulateItemContextMenu(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
    if (args.isHandled()) {
      return;
    }
    IModelItem item = args.getItem();
    ContextMenu menu = (ContextMenu) args.getMenu();
    IGraph graph = graphControl.getGraph();

    if (item instanceof INode) {
      INode node = (INode) item;

      // Add an annotation label to the node and start editing its text
      addMenuItem(menu, "Add annotation label", (e) -> {
        ILabelModelParameter modelParameter = FreeNodeLabelModel.INSTANCE.createParameter(new PointD(0.75, 0),
            new PointD(0, -50), PointD.ORIGIN, PointD.ORIGIN, 0);
        ILabel newLabel = graph.addLabel(node, "", modelParameter, new AnnotationLabelStyle());
        ((GraphEditorInputMode) graphControl.getInputMode()).editLabel(newLabel);
      });

      // If it is an Choreography node...
      INodeStyle style = node.getStyle();
      if (style instanceof ChoreographyNodeStyle) {
        ChoreographyNodeStyle choreographyNodeStyle = (ChoreographyNodeStyle) style;
        menu.getItems().add(new SeparatorMenuItem());

        // ... check if a participant was right-clicked
        Participant participant = choreographyNodeStyle.getParticipant(node, args.getQueryLocation());
        if (participant != null) {
          // and if so, offer to remove it
          addMenuItem(menu, "Remove participant", (e) -> {
            if (!choreographyNodeStyle.getTopParticipants().remove(participant)) {
              choreographyNodeStyle.getBottomParticipants().remove(participant);
            }
            graphControl.invalidate();
          });
          // or toggle its Multi-Instance flag
          addMenuItem(menu, "Toggle Participant Multi-Instance", (e) -> {
            participant.setMultiInstance(!participant.isMultiInstance());
            graphControl.invalidate();
          });
        } else {
          // if no participant was clicked, a new one can be added to the top or bottom participants
          addMenuItem(menu, "Add Participant at Top", (e) -> {
            choreographyNodeStyle.getTopParticipants().add(new Participant());
            graphControl.invalidate();
          });
          addMenuItem(menu, "Add Participant at Bottom", (e) -> {
            choreographyNodeStyle.getBottomParticipants().add(new Participant());
            graphControl.invalidate();
          });
        }
      }

      // If it is an Activity node...
      if (style instanceof ActivityNodeStyle) {
        menu.getItems().add(new SeparatorMenuItem());
        // allow to add a Boundary Event as port that uses an EventPortStyle
        addMenuItem(menu, "Add Boundary Event", (e) ->
            graph.addPort(node, FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED, new EventPortStyle()));
      }

      // If a row of a pool node has been hit...
      StripeSubregion stripeDescriptor = tableEditorInputMode.findStripe(args.getQueryLocation(), StripeTypes.ALL,
          StripeSubregionTypes.HEADER);

      if (stripeDescriptor != null) {
        IStripe stripe = stripeDescriptor.getStripe();
        // add the insert before menu item
        addMenuItem(menu, "Insert new lane before " + stripe, (e) -> {
          IStripe parent = stripe.getParentStripe();
          int index = stripe.getIndex();
          tableEditorInputMode.insertChild(parent, index);
        });
        // add the insert after menu item
        addMenuItem(menu, "Insert new lane after " + stripe, (e) -> {
          IStripe parent = stripe.getParentStripe();
          int index = stripe.getIndex();
          tableEditorInputMode.insertChild(parent, index + 1);
        });
        // add the delete menu item
        addMenuItem(menu, "Delete lane", (e) -> tableEditorInputMode.deleteStripe(stripe));

        if (stripe instanceof IRow) {
          // ... allow to increase or decrease the row header size
          InsetsD insets = stripe.getInsets();
          InsetsD defaultInsets = stripe.getTable().getRowDefaults().getInsets();

          menu.getItems().add(new SeparatorMenuItem());

          InsetsD insetsBefore = stripe.getTable().getAccumulatedInsets();
          if (insets.getLeft() > defaultInsets.getLeft()) {
            addMenuItem(menu, "Reduce header size", (e) -> {
              // by reducing the header size of one of the rows, the size of the table insets might change
              stripe.getTable().setStripeInsets(stripe,
                  InsetsD.fromLTRB(insets.getLeft() - defaultInsets.getLeft(), insets.getTop(), insets.getRight(),
                      insets.getBottom()));
              InsetsD insetsAfter = stripe.getTable().getAccumulatedInsets();
              // if the table insets have changed, the bounds of the pool node have to be adjusted as well
              double diff = insetsBefore.getLeft() - insetsAfter.getLeft();
              RectD layout = node.getLayout().toRectD();
              graph.setNodeLayout(node,
                  new RectD(layout.getX() + diff, layout.getY(), layout.getWidth() - diff, layout.getHeight()));
              graphControl.invalidate();
            });
          }
          addMenuItem(menu, "Increase header size", (e) -> {
            stripe.getTable().setStripeInsets(stripe,
                InsetsD.fromLTRB(insets.getLeft() + defaultInsets.getLeft(), insets.getTop(), insets.getRight(),
                    insets.getBottom()));
            InsetsD insetsAfter = stripe.getTable().getAccumulatedInsets();
            double diff = insetsBefore.getLeft() - insetsAfter.getLeft();
            RectD layout = node.getLayout().toRectD();
            graph.setNodeLayout(node,
                new RectD(layout.getX() + diff, layout.getY(), layout.getWidth() - diff, layout.getHeight()));
            graphControl.invalidate();
          });
        }
      }
      // we don't want to be queried again if there are more items at this location
      args.setHandled(true);
    } else if (item instanceof IEdge) {
      IEdge edge = (IEdge) item;
      // For edges a label with a Message Icon may be added
      addMenuItem(menu, "Add Message Icon Label", (evt) -> {
        ILabelModelParameter modelParameter = new EdgeSegmentLabelModel(0, 0, 0,
            false, EdgeSides.ON_EDGE).createDefaultParameter();
        graph.addLabel(edge, "", modelParameter, new MessageLabelStyle(), new SizeD(20, 14));
      });

      // we don't want to be queried again if there are more items at this location
      args.setHandled(true);
    }
  }

  private void addMenuItem(ContextMenu menu, String name, Consumer<ActionEvent> consumer) {
    MenuItem item = new MenuItem(name);
    item.setOnAction(consumer::accept);
    menu.getItems().add(item);
  }

  /**
   * Populates the palette with a pool node, a row and nodes using the different BPMN types.
   * The elements in the palette can be dragged over and dropped into the demo's
   * <code>GraphControl</code> to create new elements of the corresponding type
   * in the displayed diagram.
   */
  private void configureNodePalette() {
    // create a new graph in which the palette nodes live
    DefaultGraph nodeContainer = new DefaultGraph();

    // Create the sample node for the pool
    PoolNodeStyle poolNodeStyle = new PoolNodeStyle();
    INode poolNode = nodeContainer.createNode(PointD.ORIGIN, poolNodeStyle);
    ITable poolTable = getTable(poolNodeStyle);
    poolTable.getColumnDefaults().setInsets(new InsetsD());
    poolTable.createGrid(1, 1);
    //Use twice the default width for this sample column (looks nicer in the preview)
    poolTable.getRootColumn().getChildColumns().stream().findFirst().ifPresent(column -> {
      poolTable.setSize(column, column.getActualSize() * 2);
      nodeContainer.setNodeLayout(poolNode, poolTable.getLayout().toRectD());
      nodeContainer.addLabel(poolNode, "Pool", PoolHeaderLabelModel.WEST);
    });

    PoolNodeStyle rowPoolNodeStyle = new PoolNodeStyle();
    INode rowNode = nodeContainer.createNode(PointD.ORIGIN, rowPoolNodeStyle);
    ITable rowTable = getTable(rowPoolNodeStyle);

    IStripe rowSampleRow = rowTable.createRow(100d);
    IStripe rowSampleColumn = rowTable.createColumn(200d);
    rowTable.setStyle(rowSampleColumn, VoidStripeStyle.INSTANCE);
    rowTable.setStripeInsets(rowSampleColumn, new InsetsD());
    rowTable.setInsets(new InsetsD());
    rowTable.addLabel(rowSampleRow, "Row");
    nodeContainer.setNodeLayout(rowNode, rowTable.getLayout().toRectD());
    // Set the first row as tag so the NodeDragControl knows that a row and not a complete pool node shall be dragged
    rowTable.getRootRow().getChildRows().stream().findFirst().ifPresent(rowNode::setTag);

    // Add BPMN sample nodes - mark ActivityNodes and GroupNodes as 'GroupNode' so they may contain other nodes
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(80, 50)), new ActivityNodeStyle(), "GroupNode");
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(50, 50)), new GatewayNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(50, 50)), new EventNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(80, 20)), new AnnotationNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(40, 60)), new DataObjectNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(50, 50)), new DataStoreNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(80, 60)), new GroupNodeStyle(), "GroupNode");

    // Add a Choreography node with 2 participants
    ChoreographyNodeStyle choreographyNodeStyle = new ChoreographyNodeStyle();
    choreographyNodeStyle.getTopParticipants().add(new Participant());
    choreographyNodeStyle.getBottomParticipants().add(new Participant());
    INode choreographyNode = nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(80, 90)),
        choreographyNodeStyle,
        "GroupNode");
    nodeContainer.addLabel(choreographyNode, "Participant 1",
        new ChoreographyLabelModel().createParticipantParameter(true, 0));
    nodeContainer.addLabel(choreographyNode, "Participant 2",
        new ChoreographyLabelModel().createParticipantParameter(false, 0));

    ConversationNodeStyle conversationNodeStyle = new ConversationNodeStyle();
    conversationNodeStyle.setType(ConversationType.CONVERSATION);
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(50, 50)), conversationNodeStyle);

    initializePaletteModel(nodeContainer);
  }

  private static ITable getTable(PoolNodeStyle poolNodeStyle) {
    return poolNodeStyle.getTableNodeStyle().getTable();
  }

  private void initializePaletteModel(DefaultGraph nodeContainer) {
    // fill the palette with sample nodes
    nodeContainer.getNodes().forEach(this::addToPalette);
  }

  /**
   * Adds the given INode visualization to the palette and sets up the drag and drop functionality for it
   */
  private void addToPalette(final INode item) {
    Group container = new Group();

    // create the visual representation using the node's style renderer
    INodeStyle style = item.getStyle();
    IVisualCreator nodeVisualCreator = style.getRenderer().getVisualCreator(item, style);
    container.getChildren().add(nodeVisualCreator.createVisual(graphControl.createRenderContext()));

    // add labels if there are any
    item.getLabels().stream()
        .map(label -> label.getStyle().getRenderer().getVisualCreator(label, label.getStyle()).createVisual(null))
        .forEach(container.getChildren()::add);

    // When the user starts dragging a node in the palette, setup the information about the dragging.
    container.setOnDragDetected(event -> {
      // start the drag
      Dragboard db = container.startDragAndDrop(TransferMode.ANY);
      Map<DataFormat, Object> contentMap = new HashMap<>();

      // we use the nodes string representation as key. that is a good enough estimate for this use case.
      // On the Stripe- respectively NodeDropInputMode we map this drop id to the IStripe/INode instance
      String key = item.toString();
      if (item.getTag() instanceof IStripe){

        // If the dummy node has a stripe as its tag, we use the stripe directly
        // This allows StripeDropInputMode to take over
        tableEditorInputMode.getStripeDropInputMode().getDropDataMap().put(key, item.getTag());

      } else {

        // Otherwise, we use a copy of the node and let (hopefully) NodeDropInputMode take over
        SimpleNode value = new SimpleNode();
        value.setLayout(item.getLayout());
        value.setStyle((INodeStyle) item.getStyle().clone());
        value.setTag(item.getTag());
        nodeDropInputMode.getDropDataMap().put(key, value);

      }
      // store the key with the data format of the NodeDropInputMode in the content map that is set on the dragboard.
      contentMap.put(NodeDropInputMode.DATA_FORMAT_DROP_ID, key);

      db.setContent(contentMap);
      // to prevent a semi-transparent paper appears above the dragged node on MacOSX,
      // we set the drag view to a blank image
      db.setDragView(EMPTY_IMAGE);

      // we handled the event, so prevent it from bubbling further.
      event.consume();
    });

    container.setOnDragDone(Event::consume);

    palette.getChildren().add(container);
  }

  private final WritableImage EMPTY_IMAGE = new WritableImage(1, 1);

  @Override
  public void start(final Stage primaryStage) throws IOException {
    // we want the application to be maximized from the start
    primaryStage.setMaximized(true);
    super.start(primaryStage);
  }

  protected void onLoaded() {
    graphChooserBox.getSelectionModel().select(0);
    // loads the 'Business' example diagram
    if (!Objects.equals(graphChooserBox.getSelectionModel().getSelectedItem(), "Business")) {
      graphChooserBox.getSelectionModel().select("Business");
    } else {
      onSampleGraphChanged(null);
    }
  }

  public void onSampleGraphChanged(ActionEvent event) {
    if (inLoadSample) {
      return;
    }
    String key = (String) graphChooserBox.getSelectionModel().getSelectedItem();
    if (key == null || "None".equals(key)) {
      // no specific item - just clear the graph
      graphControl.getGraph().clear();
      // and fit the contents
      ICommand.FIT_GRAPH_BOUNDS.execute(null, graphControl);
      return;
    }
    inLoadSample = true;
    setUIEnabled(false);
    // derive the file name from the key
    String fileName = "resources/" + key.toLowerCase();
    fileName = fileName.replace("-", "");
    fileName = fileName.replace(" ", "_") + ".graphml";

    try {
      // load the sample graph
      graphControl.importFromGraphML(getClass().getResource(fileName));
    } catch (IOException exc) {
      exc.printStackTrace();
    } finally {
      inLoadSample = false;
      setUIEnabled(true);
      clearOptionPane("No Properties to show");
    }
  }
  private void setUIEnabled(boolean enabled) {
    graphChooserBox.setDisable(!enabled);
    graphControl.setFileIOEnabled(enabled);

    // Note:
    // Changing the enabled state triggers the CommandManager method
    // invalidateRequerySuggested() which in turn also ensures
    // that the enabled state of file IO actions are updated
    ((GraphEditorInputMode) graphControl.getInputMode()).setEnabled(enabled);
  }

  // ======== ICommand Actions =========
  
  /**
   * Helper that determines whether the {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNewCommand(ICommand command, Object parameter, Object sender) {
    IGraph graph = graphControl.getGraph();
    // if the graph has nodes in it, it can be cleared.
    return graph != null && graph.getNodes().size() > 0;
  }


  /**
   * Handler for the {@link ICommand#NEW}
   */
  private boolean executeNewCommand(ICommand command, Object parameter, Object sender) {
    IGraph graph = graphControl.getGraph();
    graph.clear();
    // Clearing the graph programmatically like this won't necessarily trigger an updating of the can-execute-states of the commands.
    // So we do this manually here
    ICommand.invalidateRequerySuggested();

    return true;
  }


  /**
   * A {@link ICommand} that is used to layout the given graph.
   */
  public static final ICommand RUN_LAYOUT = ICommand.createCommand("RunLayout");

  /**
   * Helper that determines whether the {@link #RUN_LAYOUT} can be executed.
   */
  private boolean canExecuteLayout(ICommand command, Object parameter, Object sender) {
    IGraph graph = graphControl.getGraph();
    return graph != null && !(graph.getNodes().size() == 0);
  }

  /**
   * Handler for the {@link #RUN_LAYOUT} command.
   */
  private boolean executeLayout(ICommand command, Object parameter, Object sender) {
    setUIEnabled(false);

    // Create a new BpmnLayout using a Left-To-Right layout orientation
    BpmnLayout bpmnLayout = new BpmnLayout();
    bpmnLayout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);

    //We use Layout executor convenience method that already sets up the whole layout pipeline correctly
    LayoutExecutor layoutExecutor = new LayoutExecutor(graphControl, bpmnLayout);
    layoutExecutor.setDuration(Duration.ofMillis(500));
    layoutExecutor.setViewportAnimationEnabled(true);
    layoutExecutor.getTableLayoutConfigurator().setHorizontalLayoutEnabled(true);
    layoutExecutor.getTableLayoutConfigurator().setFromSketchEnabled(true);
    // The BpmnLayoutData provides information about the BPMN node and edge types to the layout algorithm.
    layoutExecutor.setLayoutData(new BpmnLayoutData());
    layoutExecutor.addLayoutFinishedListener((source, args) -> setUIEnabled(true));

    layoutExecutor.start();
    
    return true;
  }

  /**
   * A {@link ICommand} that is used to apply the style properties.
   */
  public static final ICommand APPLY_STYLE = ICommand.createCommand("Apply");

  /**
   * A {@link ICommand} that is used to reset the style properties.
   */
  public static final ICommand RESET_STYLE = ICommand.createCommand("Reset");

  private boolean executeApplyStyleCommand(ICommand command, Object parameter, Object sender) {
    applyStyle();
    return true;
  }

  private boolean executeResetStyleCommand(ICommand command, Object parameter, Object sender) {
    builder.resetEditor((Parent) editorPane.getChildren().get(0));
    return true;
  }

  private boolean canExecuteChangeCommand(ICommand command, Object parameter, Object sender) {
    return builder.getConfiguration() != null;
  }

  /**
   * Actually applies the style properties.
   */
  private void applyStyle() {
    Object config = builder.getConfiguration();
    if (config instanceof NodeStyleConfiguration) {
      graphControl.getSelection().getSelectedNodes().stream().findFirst().ifPresent(node ->
      ((NodeStyleConfiguration) config).apply(graphControl, node));
    } else if (config instanceof EdgeStyleConfiguration) {
      graphControl.getSelection().getSelectedEdges().stream().findFirst().ifPresent(edge ->
      ((EdgeStyleConfiguration) config).apply(graphControl, edge));
    }
  }

  // ======== Button Actions =========

  /**
   * Stops the demo.
   */
  public void exit() {
    Platform.exit();
  }

  /**
   * Resets the zoom value of the graph control to 1.
   */
  public void resetZoom(){
    graphControl.setZoom(1);
  }

  @Override
  public String getTitle() {
    return "Business Process Model Demo - yFiles for JavaFX";
  }

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Custom {@link NodeDropInputMode} that disallows creating a table node inside of a group node
   * (especially inside of another table node)
   */
  private static class MyNodeDropInputMode extends NodeDropInputMode {
    @Override
    protected IModelItem getDropTarget(PointD dragLocation) {
      //Ok, this node has a table associated -> disallow dragging it into a group node.
      if (getDraggedItem().lookup(ITable.class) != null) {
        return null;
      }
      return super.getDropTarget(dragLocation);
    }
  }

  /**
   * Custom {@link ReparentNodeHandler} that disallows reparenting a table node.
   */
  private static class NoTableReparentNodeHandler extends ReparentNodeHandler {
    @Override
    public boolean isValidParent(IInputModeContext context, INode node, INode newParent) {
      // table nodes shall not become child nodes
      return node.lookup(ITable.class) == null && super.isValidParent(context, node, newParent);
    }
  }
}
