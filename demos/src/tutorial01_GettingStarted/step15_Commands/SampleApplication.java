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
package tutorial01_GettingStarted.step15_Commands;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.IMapperRegistry;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.GroupNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InsideOutsidePortLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.GroupNodeStyle;
import com.yworks.yfiles.graph.styles.GroupNodeStyleIconBackgroundShape;
import com.yworks.yfiles.graph.styles.GroupNodeStyleIconType;
import com.yworks.yfiles.graph.styles.GroupNodeStyleTabPosition;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.graphml.KeyType;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.KeyboardInputMode.CanExecuteCommandHandler;
import com.yworks.yfiles.view.input.KeyboardInputMode.ExecuteCommandHandler;
import com.yworks.yfiles.view.input.NavigationInputMode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * <h1>Step 15: Commands and Command Binding.</h1>
 * Wire up an action to an {@link ICommand} and define it as a command binding
 * that can be executed using a shortcut.
 * <p>
 * Please see the file help.html for more details.
 * </p>
 * @see com.yworks.yfiles.view.input.KeyboardInputMode#addCommandBinding(ICommand, KeyboardInputMode.ExecuteCommandHandler, KeyboardInputMode.CanExecuteCommandHandler) addCommandBinding
 * @see KeyboardInputMode#addKeyBinding(KeyCodeCombination, ICommand) addKeyBinding
 */
public class SampleApplication extends Application {

  public GraphControl graphControl;
  public WebView help;
  public Button layoutButton;

  private FoldingManager manager;

  /**
   * Symbolic name for the mapper that allows transparent access to the correct implementation even across
   * wrapped graphs.
   */
  private static final String DATE_MAPPER_KEY = "DateMapperKey";
  // Format used to store the date as custom information associated with graph elements.
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

  /**
   * Initializes the application after its user interface has been built up.
   */
  public void initialize() {
    // Specifies the default style for group nodes.
    configureGroupNodeStyles();

    // Customizes the provided ports that an edge can connect to when a user interacts with edges and their endpoints.
    // Note that this has to be done on the backing graph since we have to change some settings that are structurally
    // important at this stage (before folding is enabled, this works like in the previous demos) if folding was
    // enabled, here already, the below function would have to retrieve the IFoldingView first and obtain the master
    // backing graph from it to perform the customization on it.
    customizePortHandling();

    // Enables all kinds of interaction with a graph and its graph elements. In particular, this includes editing the
    // graph, i.e., creation and deletion of graph elements.
    // Note that this step has been moved before enableDataBinding, because one approach for setting up the custom data
    // depends on the input mode being already initialized. Creating the input mode does not depend on one of the other
    // setup steps.
    configureInteraction();

    // Set up data binding to associate custom information with graph elements and listen to the node creation event to
    // update the data. This must be done before enabling folding, since we want to subscribe to events on the original
    // graph.
    enableDataBinding();

    // Enables the ability to collapse and expand group nodes. Collapsing a group node hides all of its children, while
    // expanding the group node makes them visible again.
    enableFolding();

    // Enables file operations on the graph control to be able to interactively save or load a graph.
    enableGraphMLIO();

    // Displays tooltips to show the custom information associated with graph elements.
    setupTooltips();

     // Adds a context menu to update the custom information associated with graph elements.
    setupContextMenu();

    // Enables reading and writing the custom information associated with graph elements.
    enableDataPersistence();

    // Specifies the default label model parameters for node and edge label. Label model parameters control the actual
    // label placement, as well as the available placement candidates when moving the label interactively.
    setDefaultLabelParameters();

    // Specifies a default style for each type of graph element. These styles are applied to new graph elements if no
    // style is explicitly specified during element creation.
    setDefaultStyles();

    ////////
    // Read the initial graph from a resource file instead of creating it programmatically
    //populateGraph();

    // Read a sample graph from an GraphML file
    readSampleGraph();

    //////// New in this sample /////////////////////////
    // Initializes an additional input binding for running the layout algorithm
    initializeLayoutCommand();
    /////////////////////////////////////////////////////

    // Undo and redo are provided by the graph out-of-the-box, but have to be enabled before they can be used.
    // This needs to be done on the backing graph, too - so we do have to change this method.
    enableUndo();
  }

  /**
   * Called right after stage is loaded.
   * In JavaFX, nodes don't have a width or height until the stage is displayed and the scene graph is calculated.
   * As {@link #initialize()} is called right after a node is created, but before displayed, we have to update
   * the view port later.
   */
  public void onLoaded() {
    // Updates the content rectangle that encloses the graph and adjust the zoom level to show the whole graph in the
    // view.
    updateViewPort();
  }

  /**
   * Reads a sample graph from an embedded resource using {@link GraphMLIOHandler}.
   */
  private void readSampleGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("layout-sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

   //////// New in this sample ///////////////////////////

  /**
   * A {@link ICommand} that is used to layout the given graph and can be executed by pressing Shortcut+L.
   */
  private static final ICommand LAYOUT_GRAPH = ICommand.createCommand("LayoutGraph");

  /**
   * Initializes an additional command for running the layout algorithm.
   */
  private void initializeLayoutCommand() {
    GraphEditorInputMode geim = (GraphEditorInputMode) graphControl.getInputMode();
    // the event listener for the event that the executability of the command may have changed
    // (due to certain events or manual invocation) and needs to be re-evaluated.
    CanExecuteCommandHandler canExecuteCommandHandler = (command, param, source) -> {
      // if a layout is currently running, no other layout shall be executable for two reasons:
      // - the result of the current layout run shall be presented before executing a new layout
      // - the layout algorithms are not thread safe, so executing a layout algorithm that is currently running
      //   may result in errors
      boolean layoutIsRunning = geim.getWaitInputMode().isWaiting();
      return graphControl.getGraph().getNodes().size() != 0 && !layoutIsRunning;
    };

    // the event handler that is called in the event that the command is being exeuted.
    ExecuteCommandHandler commandExecutedHandler = (command, param, source) -> {
      // Uses the convenience class LayoutUtilities to perform the layout in a background thread,
      // animate it, manage undo and adjust the content rectangle in one simple step.
      graphControl.morphLayout(new HierarchicLayout(), Duration.ofSeconds(1));
      return true;
    };

    // create a command binding in the list of command bindings of the GraphControl and bind the
    // command with the handler for the execution and the handler that the executability may have changed.
    geim.getKeyboardInputMode().addCommandBinding(LAYOUT_GRAPH, commandExecutedHandler, canExecuteCommandHandler);
    // add the input gestures to which the command should be executed on
    geim.getKeyboardInputMode().addKeyBinding(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN), LAYOUT_GRAPH);

    // connect the layout button with the layout command
    // execute the command if the button is pressed
    layoutButton.setOnAction(event -> LAYOUT_GRAPH.execute(null, graphControl));
    // disable the button if the command cannot be executed at the moment
    LAYOUT_GRAPH.addCanExecuteChangedListener((source, args) -> layoutButton.setDisable(!LAYOUT_GRAPH.canExecute(null, graphControl)));
  }

  ///////////////////////////////////////////////////////

  /**
   * Registers input and output handlers that read/store the data into/from the mapper as GraphMLAttributes.
   */
  private void enableDataPersistence() {
    // create an IOHandler that will be used for all IO operations
    GraphMLIOHandler ioh = new GraphMLIOHandler();

    // we set the IO handler on the GraphControl, so the GraphControl's IO methods
    // will pick up our handler for use during serialization and deserialization.
    graphControl.setGraphMLIOHandler(ioh);

    IMapperRegistry registry = manager.getMasterGraph().getMapperRegistry();
    IMapper<INode, LocalDateTime> dateMapper = registry.getMapper(INode.class, LocalDateTime.class, DATE_MAPPER_KEY);
    if (dateMapper != null) {
      // The OutputHandler just stores the string value of the attribute
      // We need to provide the symbolic name of the attribute in the GraphML file, the data source as an IMapper and the
      // GraphML type of the attribute
      ioh.addOutputMapper(INode.class, LocalDateTime.class, DATE_MAPPER_KEY, "", dateMapper, (source, args) -> {
        args.getWriter().writeString(((LocalDateTime) args.getItem()).format(DATE_FORMAT));
        args.setHandled(true);
      }, KeyType.STRING);

      // To read back a Date value from a string GraphML attribute, we have to provide an additional (very simple...) callback method.
      ioh.addInputMapper(INode.class, LocalDateTime.class,
          element -> GraphMLIOHandler.matchesName(element, DATE_MAPPER_KEY) && GraphMLIOHandler.matchesType(element, KeyType.STRING),
          dateMapper, (source, args) -> args.setResult(LocalDateTime.parse(args.getXmlNode().getTextContent(), DATE_FORMAT)));
    }
  }

  /**
   * Sets up simple data binding. Creates an {@link IMapper},
   * registers it and subscribes to the node creation event on the backing graph.
   */
  private void enableDataBinding() {
    IGraph graph = getGraph();
    // Create a specialized instance - we use WeakDictionaryMapper here so that when
    // a node is removed from the graph, we don't have to care about the reference in the
    // IMapper
    // Create the mapper
    IMapper<INode, LocalDateTime> dateMapper = new Mapper<>(new WeakHashMap<>());
    // Register the mapper under a symbolic name
    graph.getMapperRegistry().addMapper(INode.class, LocalDateTime.class, DATE_MAPPER_KEY, dateMapper);
    //Subscribe to the node creation event to record the node creation time.
    //Note that since this event is triggered after undo/redo, the time will
    //be updated during redos of node creations and undos of node deletions.
    //If this is unwanted behavior, you should customize the node creation itself
    //to associate the data with the element at the time of the initial creation,
    //e.g. by listening to the NodeCreated event of GraphEditorInputMode, see below
    graph.addNodeCreatedListener((source, evt) -> dateMapper.setValue(evt.getItem(), LocalDateTime.now()));

    // Alternatively (or in addition to that) - we could use the event for
    // interactive node creation as follows (provided that the input mode
    // for the graph control is already set).
//    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
//    inputMode.addNodeCreatedListener((source, args) -> {
//      IFoldingView foldingView = graph.getFoldingView();
//      if (foldingView != null) {
//        // Store the data at the master graph as in the original approach.
//        dateMapper.setValue(args.getItem(), LocalDateTime.now());
//      }
//    });
  }

  /**
   * Sets up tooltips that return the value that is stored in the mapper.
   * <p>Dynamic tooltips are implemented by adding a tooltip provider as an event handler for
   * the {@link com.yworks.yfiles.view.input.MouseHoverInputMode#queryToolTipEvent} event of the GraphEditorInputMode using the
   * {@link  com.yworks.yfiles.view.input.ToolTipQueryEventArgs} parameter.</p>
   * <p>The {@link com.yworks.yfiles.view.input.ToolTipQueryEventArgs} parameter provides three relevant properties:
   * Handled, QueryLocation, and ToolTip. The Handled property is a flag which indicates
   * if the tooltip was already set by one of possibly several tooltip providers. The
   * QueryLocation property contains the mouse position for the query in world coordinates.
   * The tooltip is set by setting the ToolTip property.</p>
   */
  private void setupTooltips() {
    GraphEditorInputMode graphInputMode = (GraphEditorInputMode) graphControl.getInputMode();
    if (graphInputMode != null) {
      // Specify nodes to be the items that can have tool tips.
      graphInputMode.setToolTipItems(GraphItemTypes.NODE);
      // Add a listener to the input mode for the event that a tool tip is about to be shown for a specific item (a node, as previously specified)
      graphInputMode.addQueryItemToolTipListener((source, args) -> {
        if (args.isHandled()) {
          // A tooltip has already been assigned -> nothing to do.
          return;
        }
        //Find out if a node is under the current location.
        INode hitNode = (INode) args.getItem();
        if (hitNode == null) {
          return;
        }
        //Since the returned instance is not the same as in the backing graph (where our data is bound to)
        //we retrieve the mapper indirectly through its symbolic name - the folding framework automatically returns
        //an IMapper instance that translates to the original elements:
        //if we did not use the folding feature, this step would be unnecessary and we
        //could use the mapper instance directly (on the original nodes).
        IMapperRegistry registry = getGraph().getMapperRegistry();
        IMapper<INode, LocalDateTime> dateMapper = registry.getMapper(INode.class, LocalDateTime.class, DATE_MAPPER_KEY);
        if (dateMapper != null) {
          //Found a suitable mapper.

          // Set the tooltip if a date is mapped.
          LocalDateTime date = dateMapper.getValue(hitNode);
          if (date != null) {
            args.setToolTip(new Tooltip("Node: " + hitNode + "\n" + "Creation Time: " + date.format(DATE_FORMAT)));

            // Indicate that the tooltip has been set.
            args.setHandled(true);
          }
        }
      });

      // Add a little offset to the tooltip such that it is not obscured by the mouse pointer.
      graphInputMode.getMouseHoverInputMode().setToolTipLocationOffset(new PointD(20, 20));
    }
  }

  /**
   * Adds a context menu for nodes.
   */
  private void setupContextMenu() {
    GraphEditorInputMode mode = (GraphEditorInputMode) graphControl.getInputMode();
    if (mode != null) {
      mode.setContextMenuItems(GraphItemTypes.NODE);
      mode.addPopulateItemContextMenuListener((source, args) -> {
        INode node = (INode) args.getItem();
        if (node != null) {
          // add a context menu entry
          MenuItem menuItem = new MenuItem("Set to now");
          menuItem.setOnAction(event -> setToNow(node));
          ((ContextMenu) args.getMenu()).getItems().add(menuItem);
          args.setHandled(true);
        }
      });
    }
  }

  /**
   * Updates the local date associated to the given node to "now".
   * @param node node to change its tooltip
   */
  private void setToNow(INode node) {
    IMapper<INode, LocalDateTime> mapper = getGraph().getMapperRegistry().getMapper(INode.class, LocalDateTime.class,
        DATE_MAPPER_KEY);
    if (mapper != null) {
      mapper.setValue(node, LocalDateTime.now());
    }
  }

  /**
   * Enables folding. Changes the GraphControl's graph to a folding view
   * that provides the actual collapse/expand state.
   */
  private void enableFolding() {
    // create the folding manager
    manager = new FoldingManager(getGraph());
    // replace the displayed graph with a folding view
    graphControl.setGraph(manager.createFoldingView().getGraph());
  }

  /**
   * Configures the default style for group nodes.
   */
  private void configureGroupNodeStyles() {
    // GroupNodeStyle is a style especially suited to group nodes
    INodeDefaults groupNodeDefaults = graphControl.getGraph().getGroupNodeDefaults();

    GroupNodeStyle groupNodeStyle = new GroupNodeStyle();
    groupNodeStyle.setGroupIcon(GroupNodeStyleIconType.CHEVRON_DOWN);
    groupNodeStyle.setFolderIcon(GroupNodeStyleIconType.CHEVRON_UP);
    groupNodeStyle.setIconSize(14);
    groupNodeStyle.setIconBackgroundShape(GroupNodeStyleIconBackgroundShape.CIRCLE);
    groupNodeStyle.setIconForegroundPaint(Color.WHITE);
    groupNodeStyle.setTabPaint(Color.rgb(0x24, 0x22, 0x65));
    groupNodeStyle.setTabPosition(GroupNodeStyleTabPosition.TOP_TRAILING);
    groupNodeStyle.setPen(new Pen(Color.rgb(0x24, 0x22, 0x65), 2));
    groupNodeStyle.setCornerRadius(8);
    groupNodeStyle.setTabWidth(70);
    groupNodeStyle.setContentAreaInsets(new InsetsD(8));
    groupNodeStyle.setContentAreaHitTransparent(true);
    groupNodeDefaults.setStyle(groupNodeStyle);

    // Sets a label style with right-aligned text
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setTextAlignment(TextAlignment.RIGHT);
    defaultLabelStyle.setTextPaint(Color.WHITE);
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    defaultLabelStyle.setFont(Font.font(11));
    groupNodeDefaults.getLabelDefaults().setStyle(defaultLabelStyle);

    // Places the label inside of the tab.
    groupNodeDefaults.getLabelDefaults().setLayoutParameter(
        new GroupNodeLabelModel().createDefaultParameter()
    );
  }

  /**
   * Creates a group node programmatically.
   * Creates a couple of nodes and puts them into a group node.
   */
  private INode createGroupNode(INode... childNodes) {
    IGraph graph = getGraph();

    // Creates a group node that encloses the given child nodes
    INode groupNode = graph.groupNodes(childNodes);

    // Creates a label for the group node
    graph.addLabel(groupNode, "Group 1");

    // Adjusts the bounds of the group nodes
    graph.adjustGroupNodeLayout(groupNode);
    return groupNode;
  }

  /**
   * Configures custom port handling with the help of {@link com.yworks.yfiles.graph.ILookup}.
   * <p>
   * When a user interacts with edges and their endpoints,
   * <code>node.lookup(IPortCandidateProvider.class)</code>
   * is called for the nodes in that graph,
   * and the framework returns the implementation of IPortCandidateProvider which
   * supplies the list of available ports.
   *
   * Instead of the default, we'll register a custom lookup for type IPortCandidateProvider.
   *
   * Note: we'll update this method in a future tutorial step to work with folding.
   * </p>
   */
  private void customizePortHandling() {
    IGraph graph = getGraph();
    // We don't want to remove "empty ports", since we want that our port candidate provider
    // can optionally return them too, even if they are unoccupied.
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);

    // Register a custom implementation that overrides
    // the default one present in the lookup for nodes
    // for some types (in this case, for type IPortCandidateProvider)

    // The net effect is that instead of the default port candidates
    // present for each node, a different set of port candidates will be returned
    // and used, e.g. during interactive edge creation.

    // To modify the existing lookup for a graph element, typically it
    // is decorated with the help of the getDecorator() method on IGraph,
    // which allows to dynamically insert custom implementations for the specified types.

    // Doing this can be seen as dynamically subclassing
    // the class in question (the INode implementation in this case), but only
    // for the node instances that live in the graph in question and then
    // overriding just their lookup(type) method. The only difference to traditional
    // subclassing is that you get the "this" passed in as a parameter.
    // Doing this more than once is like subclassing more and more, so the order matters.

    // Once node.lookup(IPortCandidateProvider.class) is called for the nodes in that graph,
    // the framework will delegate to the factory method below and finally yield the result.
    graph.getDecorator().getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
        node -> IPortCandidateProvider.combine(
            IPortCandidateProvider.fromExistingPorts(node),   // provides already existing port candidates of the node
            IPortCandidateProvider.fromNodeCenter(node),      // provides a port candidate at the center of the node
            IPortCandidateProvider.fromShapeGeometry(node)    // provides a port candidate at the center of each straight line segment
        ));
  }

  /**
   * Enables GraphML I/O command bindings.
   */
  private void enableGraphMLIO() {
    graphControl.setFileIOEnabled(true);
  }

  /**
   * Enables undo functionality.
   * <p>Undo functionality is disabled by default.</p>
   */
  private void enableUndo() {
    manager.getMasterGraph().setUndoEngineEnabled(true);
  }

  /**
   * Configures basic interaction.
   * <p>
   * Interaction is handled by so called input modes. {@link com.yworks.yfiles.view.input.GraphEditorInputMode} is the main
   * input mode that already provides a large number of graph interaction possibilities, such as moving, deleting,
   * creating, resizing graph elements. Note that to create or edit a label, just press F2. Also, try to move a label
   * around and see what happens.
   * </p>
   */
  private void configureInteraction() {
    // Creates a new GraphEditorInputMode instance and registers it as the main
    // input mode for the graphControl
    GraphEditorInputMode geim = new GraphEditorInputMode();
    // Enable grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    geim.setGroupingOperationsAllowed(true);

    // Add a label for interactive created group nodes
    geim.addNodeCreatedListener((source, args) -> {
      INode node = args.getItem();
      if (getGraph().isGroupNode(node)) {
        getGraph().addLabel(node, "Group Node");
      }
    });

    graphControl.setInputMode(geim);
  }

  /**
   * Sets up default label model parameters for graph elements.
   * Label model parameters control the actual label placement, as well as the available
   * placement candidates when moving the label interactively.
   */
  private void setDefaultLabelParameters() {
    IGraph graph = getGraph();
    // For node labels, the default is a label position at the node center
    // Let's keep the default.  Here is how to set it manually
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorLabelModel.CENTER);

    // For edge labels, the default is a label that is rotated to match the associated edge segment
    // We'll start by creating a model that is similar to the default:
    EdgeSegmentLabelModel edgeSegmentLabelModel = new EdgeSegmentLabelModel();
    // However, by default, the rotated label is centered on the edge path.
    // Let's move the label off of the path:
    edgeSegmentLabelModel.setDistance(10);
    // Finally, we can set this label model as the default for edge labels using a location at the center of the first segment
    ILabelModelParameter labelModelParameter = edgeSegmentLabelModel.createParameterFromSource(0, 0.5, EdgeSides.RIGHT_OF_EDGE);
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(labelModelParameter);

    // For port labels, the default is a label that is placed outside the owner node
    graph.getNodeDefaults().getPortDefaults().getLabelDefaults().setLayoutParameter(new InsideOutsidePortLabelModel().createOutsideParameter());
  }

  /**
   * Sets up default styles for graph elements.
   * <p>
   * Default styles apply only to elements created after the default style has been set,
   * so typically, you'd set these as early as possible in your application.
   * </p>
   */
  private void setDefaultStyles() {
    IGraph graph = getGraph();
    // Sets the default style for nodes
    // Creates a nice ShapeNodeStyle instance, using an orange color.
    // Sets this style as the default for all nodes that don't have another
    // style assigned explicitly
    ShapeNodeStyle defaultNodeStyle = new ShapeNodeStyle();
    defaultNodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    defaultNodeStyle.setPaint(Color.rgb(255, 108, 0));
    defaultNodeStyle.setPen(new Pen(Color.rgb(102, 43, 0), 1.5));
    graph.getNodeDefaults().setStyle(defaultNodeStyle);

    // Sets the default style for edges:
    // Creates a PolylineEdgeStyle which will be used as default for all edges
    // that don't have another style assigned explicitly
    PolylineEdgeStyle defaultEdgeStyle = new PolylineEdgeStyle();
    defaultEdgeStyle.setPen(new Pen(Color.rgb(102, 43, 0), 1.5));
    defaultEdgeStyle.setTargetArrow(new Arrow(ArrowType.TRIANGLE, Color.rgb(102, 43, 0)));

    // Sets the defined edge style as the default for all edges that don't have
    // another style assigned explicitly:
    graph.getEdgeDefaults().setStyle(defaultEdgeStyle);

    // Sets the default style for labels
    // Creates a label style with the label text color set to dark red
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setFont(Font.font(12));
    defaultLabelStyle.setTextPaint(Color.BLACK);

    // Sets the defined style as the default for both edge and node labels:
    graph.getEdgeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);
    graph.getNodeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);

    // Sets the default node size explicitly to 40x40
    graph.getNodeDefaults().setSize(new SizeD(40, 40));
  }

  /**
   * Creates a sample graph and introduces all important graph elements present in yFiles. Additionally, this method
   * specifies the label placement for some specific labels.
   */
  private void populateGraph() {
    IGraph graph = getGraph();

    // Creates two nodes with the default node size
    // The location is specified for the center
    INode node1 = graph.createNode(new PointD(50, 50));
    INode node2 = graph.createNode(new PointD(150, 50));
    // Creates a third node with a different size of 80x40
    // In this case, the location of (360,380) describes the upper left
    // corner of the node bounds
    INode node3 = graph.createNode(new RectD(360, 380, 80, 40));

    // Creates some edges between the nodes
    IEdge edge1 = graph.createEdge(node1, node2);
    IEdge edge2 = graph.createEdge(node2, node3);

    // Creates the first bend for edge2 at (400, 50)
    IBend bend1 = graph.addBend(edge2, new PointD(400, 50));

    // Actually, edges connect "ports", not nodes directly.
    // If necessary, you can manually create ports at nodes
    // and let the edges connect to these.
    // Creates a port in the center of the node layout
    IPort port1AtNode1 = graph.addPort(node1, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);

    // Creates a port at the middle of the left border
    // Note to use absolute locations in world coordinates when placing ports using PointD.
    // The method obtains a model parameter that best matches the given port location.
    IPort port1AtNode3 = graph.addPort(node3, new PointD(node3.getLayout().getX(), node3.getLayout().getCenter().getY()));

    // Creates an edge that connects these specific ports
    IEdge edgeAtPorts = graph.createEdge(port1AtNode1, port1AtNode3);

    // Adds labels to several graph elements
    graph.addLabel(node1, "Node 1");
    graph.addLabel(node2, "Node 2");
    graph.addLabel(node3, "Node 3");
    graph.addLabel(edgeAtPorts, "Edge at Ports");

    // Add some more elements to have a larger graph to edit
    INode n4 = graph.createNode(new PointD(50, -50));
    graph.addLabel(n4, "Node 4");
    INode n5 = graph.createNode(new PointD(50, -150));
    graph.addLabel(n5, "Node 5");
    INode n6 = graph.createNode(new PointD(-50, -50));
    graph.addLabel(n6, "Node 6");
    INode n7 = graph.createNode(new PointD(-50, -150));
    graph.addLabel(n7, "Node 7");
    INode n8 = graph.createNode(new PointD(150, -50));
    graph.addLabel(n8, "Node 8");

    graph.createEdge(n4, node1);
    graph.createEdge(n5, n4);
    graph.createEdge(n7, n6);
    IEdge e6_1 = graph.createEdge(n6, node1);
    graph.addBend(e6_1, new PointD(-50, 50), 0);

    // Creates a group node programmatically which groups the child nodes n4, n5, and n8
    INode groupNode = createGroupNode(n4, n5, n8);
    // creates an edge between the group node and node 2
    IEdge eg_2 = graph.createEdge(groupNode, node2);
    graph.addBend(eg_2, new PointD(100, 0), 0);
    graph.addBend(eg_2, new PointD(150, 0), 1);
  }

  /**
   * Updates the content rectangle to encompass all existing graph elements.
   * <p>
   * If you create your graph elements programmatically, the content rectangle
   * (i.e. the rectangle in <b>world coordinates</b> that encloses the graph)
   * is <b>not</b> updated automatically to enclose these elements. Typically,
   * this manifests in wrong/missing scrollbars, incorrect
   * {@link com.yworks.yfiles.view.GraphOverviewControl} behavior and the like.
   * </p>
   * <p>
   * This method demonstrates several ways to update the content rectangle, with
   * or without adjusting the zoom level to show the whole graph in the view.
   * </p>
   * <p>
   * Note that updating the content rectangle only does not change the current
   * view port (i.e. the world coordinate rectangle that corresponds to the
   * currently visible area in view coordinates).
   * </p>
   * <p>
   * Try to uncomment the example code in this method and observe the different
   * effects.
   * </p>
   * <p>
   * The following steps in this tutorial assume you just called
   * <code>graphControl.fitGraphBounds();</code> in this method.
   * </p>
   */
  void updateViewPort() {
    // Uncomment the following line to update the content rectangle
    // to include all graph elements
    // This should result in correct scrolling behaviour:

    // graphControl.updateContentRect();

    // Additionally, we can also set the zoom level so that the
    // content rectangle fits exactly into the view port area:
    // Uncomment this line in addition to UpdateContentRect:
    // Note that this changes the zoom level (i.e. the graph elements will look smaller)

    // graphControl.fitContent();

    // The sequence above is equivalent to just calling:
    graphControl.fitGraphBounds();
  }

  /**
   * Action handler for zoom in button action.
   */
  public void handleZoomInAction() {
    graphControl.setZoom(graphControl.getZoom() * 1.25);
  }

  /**
   * Action handler for zoom out button action.
   */
  public void handleZoomOutAction() {
    graphControl.setZoom(graphControl.getZoom() * 0.8);
  }

  /**
   * Action handler for reset zoom button action.
   */
  public void handleResetZoomAction() {
    graphControl.setZoom(1);
  }

  /**
   * Action handler for fit to content button action.
   */
  public void handleFitToContentAction() {
    graphControl.fitGraphBounds();
  }


  /**
   * Action handler for copy current selected items.
   */
  public void handleCopyAction() {

    ICommand.COPY.execute(null, graphControl);
  }

  /**
   * Action handler for Paste current selected items.
   */
  public void handlePasteAction() {
    ICommand.PASTE.execute(null, graphControl);
  }

  /**
   * Action handler for delete current selected items.
   */
  public void handleDeleteAction() {
    final GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    inputMode.deleteSelection();
  }

  /**
   * Action handler for undo last action.
   */
  public void handleUndoAction() {
    ICommand.UNDO.execute(null, graphControl);
  }

  /**
   * Action handler for redo last undo.
   */
  public void handleRedoAction() {
    ICommand.REDO.execute(null, graphControl);
  }

  /**
   * Action handler to close demo.
   */
  public void handleExitAction() {
    Platform.exit();
  }

  /**
   * Action handler for cut current selected items.
   */
  public void handleCutAction() {
    ICommand.CUT.execute(null, graphControl);
  }

  /**
   * Action handler for open file action.
   */
  public void handleOpenAction() {
    ICommand.OPEN.execute(null, graphControl);
  }

  /**
   * Action handler for save file action.
   */
  public void handleSaveAction() {
    ICommand.SAVE.execute(null, graphControl);
  }

  /**
   * Action handler for save as file action.
   */
  public void handleSaveAsAction() {
    ICommand.SAVE_AS.execute(null, graphControl);
  }

  /**
   * Action handler for group selection action.
   */
  public void handleGroupAction() {
    final GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    inputMode.groupSelection();
  }

  /**
   * Action handler for ungroup selection action.
   */
  public void handleUngroupAction() {
    final GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    inputMode.ungroupSelection();
  }

  /**
   * Action handler for expanding group action.
   */
  public void handleExpandGroupAction() {
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    final NavigationInputMode navigationInputMode = inputMode.getNavigationInputMode();
    final ISelectionModel<INode> selectedNodes = graphControl.getSelection().getSelectedNodes();
    List<INode> nodesToExpand = selectedNodes.stream().collect(Collectors.toList());
    nodesToExpand.forEach(navigationInputMode::expandGroup);
    }

  /**
   * Action handler for collapsing group action.
   */
  public void handleCollapseGroupAction() {
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    final NavigationInputMode navigationInputMode = inputMode.getNavigationInputMode();
    final ISelectionModel<INode> selectedNodes = graphControl.getSelection().getSelectedNodes();
    List<INode> nodesToCollapse = selectedNodes.stream().collect(Collectors.toList());
    nodesToCollapse.forEach(navigationInputMode::collapseGroup);
    }

  /**
   * Action handler for entering group action.
   */
  public void handleEnterGroupAction() {
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    final NavigationInputMode navigationInputMode = inputMode.getNavigationInputMode();
    final ISelectionModel<INode> selectedNodes = graphControl.getSelection().getSelectedNodes();
    List<INode> nodesToEnter = selectedNodes.stream().collect(Collectors.toList());
    nodesToEnter.forEach(navigationInputMode::enterGroup);
   }

  /**
   * Action handler for exit group action.
   */
  public void handleExitGroupAction() {
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    inputMode.getNavigationInputMode().exitGroup();
  }

  /**
   * Convenience method to retrieve the graph.
   */
  public IGraph getGraph() {
    return graphControl.getGraph();
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SampleApplication.fxml"));
    fxmlLoader.setController(this);
    Parent root = fxmlLoader.load();
    WebViewUtils.initHelp(help, this);

    Scene scene = new Scene(root, 1365, 768);

    // In JavaFX, nodes don't have a width or height until the stage is shown and the scene graph is calculated.
    // onLoaded does some initialization that need the correct bounds of the nodes.
    stage.setOnShown(windowEvent -> onLoaded());

    stage.setTitle("Step 15 - Commands and Command Bindings");
    stage.setScene(scene);
    stage.show();
  }
}
