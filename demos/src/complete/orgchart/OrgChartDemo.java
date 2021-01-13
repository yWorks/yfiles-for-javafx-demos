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
package complete.orgchart;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.TemplateNodeStyle;
import com.yworks.yfiles.graph.styles.TemplateNodeStyleRenderer;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.CompositeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutStage;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.tree.AssistantNodePlacer;
import com.yworks.yfiles.layout.tree.ChildPlacement;
import com.yworks.yfiles.layout.tree.DefaultNodePlacer;
import com.yworks.yfiles.layout.tree.INodePlacer;
import com.yworks.yfiles.layout.tree.LeftRightNodePlacer;
import com.yworks.yfiles.layout.tree.RootAlignment;
import com.yworks.yfiles.layout.tree.RoutingStyle;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeLayoutData;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GraphOverviewControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * An organizational chart application.
 * <p>
 * The employees are visualized using a custom node style that renders different levels of detail based on the zoom
 * level. A <code>FilteredGraphWrapper</code> is used to display a subgraph of the model graph.
 * </p>
 * <p>
 * This is the class with the main logic. It is responsible for the connection of the model and the view. It holds all
 * actions and builds and populates the main components.
 * </p>
 */
public class OrgChartDemo extends DemoApplication {

  /**
   * The command that can be used by the buttons to show the parent node.
   * <p>
   *   This command requires the corresponding {@link INode} as the parameter.
   * </p>
   */
  public static final ICommand SHOW_PARENT = ICommand.createCommand("ShowParent");

  /**
   * The command that can be used by the buttons to hide the parent node.
   * <p>
   * This command requires the corresponding {@link INode} as the parameter.
   * </p>
   */

  public static final ICommand HIDE_PARENT = ICommand.createCommand("HideParent");

  /**
   * The command that can be used by the buttons to show the child nodes.
   * <p>
   *  This command requires the corresponding {@link INode} as the parameter.
   * </p>
   */
  public static final ICommand SHOW_CHILDREN = ICommand.createCommand("ShowChildren");

  /**
   * The command that can be used by the buttons to hide the child nodes.
   * <p>
   *  This command requires the corresponding {@link INode} as the parameter.
   * </p>
   */
  public static final ICommand HIDE_CHILDREN = ICommand.createCommand("HideChildren");

  /**
   * The command that can be used by the buttons to expand all collapsed nodes.
   */
  public static final ICommand SHOW_ALL = ICommand.createCommand("ShowAll");

  // view elements.
  public BorderPane root;
  public GraphOverviewControl overviewControl;
  public GraphControl graphControl;
  public WebView webView;
  public TreeView<Employee> treeView;

  /**
   * Used by the predicate function to determine which nodes should not be shown.
   */
  private  HashSet<INode> hiddenNodesSet;

  /**
   * The filtered graph instance that hides nodes from the graph to create smaller graphs for easier navigation.
   */
  private FilteredGraphWrapper filteredGraphWrapper;

  /**
   * The predicate used for the FilteredGraphWrapper
   */
  private Predicate<INode> shouldShowNode = node -> !hiddenNodesSet.contains(node);

  private Map<Employee, INode> correspondingNodes = new HashMap<>();

  // a listener for the TreeView that zooms the GraphControl to the selected item
  private ChangeListener<TreeItem<Employee>> treeViewSelectionListener = (observable, oldValue, newValue)
      -> zoomToTreeItem();

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    hiddenNodesSet = new HashSet<>();

    // setup the overview.
    overviewControl.setGraphControl(graphControl);
    overviewControl.setHorizontalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    overviewControl.setVerticalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    // setup the help text on the right side.
    WebViewUtils.initHelp(webView, this);

    // Initialize the GraphControl and other views.
    initializeGraph();
    initializeInputMode();
    initializeTreeView();

    // disable selection, focus and highlight painting
    graphControl.getSelectionIndicatorManager().setEnabled(false);
    graphControl.getFocusIndicatorManager().setEnabled(false);
    graphControl.getHighlightIndicatorManager().setEnabled(false);

     // Instead of selections, we use the current item of the GraphControl to
     // manage highlights. The current item changes with the focused property.
    graphControl.currentItemProperty().addListener((observableValue, lastItem, currentItem) -> {
      // when the current (focused) item changes, update the TreeView and properties view.
      if (currentItem != null) {
        selectCurrentItemInTreeView();
        // transform the status that is specified in the Employee class into a javafx color and deposit it in a property of the root
        // in FXML, the property is accessible via root.properties.currentEmployeeStatusColor
        EmployeeWrapper tag = (EmployeeWrapper) currentItem.getTag();
        root.getProperties().put("currentEmployeeStatusColor", statusToColor(tag.getEmployee().getStatus()));
      }
    });

    // we wrap the graph instance by a filtered graph wrapper
    filteredGraphWrapper = new FilteredGraphWrapper(graphControl.getGraph(), shouldShowNode, edge -> true);
    graphControl.setGraph(filteredGraphWrapper);

    // now calculate the initial layout
    doLayout();
    fitGraphBounds();
    fitOverviewBounds();

    // configure view port limiter to use the current content rect
    graphControl.getViewportLimiter().setHonoringBothDimensionsEnabled(false);
    graphControl.getViewportLimiter().setBounds(graphControl.getContentRect().getEnlarged(20));

    // hide the status indicator in the properties view initially
    root.getProperties().put("currentEmployeeStatusColor", Color.TRANSPARENT);
  }

  /**
   * Called when the stage is shown and the {@link GraphControl} is already resized to its preferred size.
   */
  public void onLoaded() {
    fitGraphBounds();
    fitOverviewBounds();
  }

  private void initializeInputMode() {
    GraphViewerInputMode inputMode = new GraphViewerInputMode();
    // Only nodes are allowed to be focused.
    inputMode.setSelectableItems(GraphItemTypes.NONE);
    inputMode.setFocusableItems(GraphItemTypes.NODE);
    // zoom to the double clicked item
    inputMode.addItemDoubleClickedListener((source, args) -> zoomToCurrentItem());

    KeyboardInputMode kim = inputMode.getKeyboardInputMode();
    // register custom command bindings defined here
    kim.addCommandBinding(HIDE_CHILDREN, this::executeHideChildren, this::canExecuteHideChildren);
    kim.addCommandBinding(SHOW_CHILDREN, this::executeShowChildren, this::canExecuteShowChildren);
    kim.addCommandBinding(HIDE_PARENT, this::executeHideParent, this::canExecuteHideParent);
    kim.addCommandBinding(SHOW_PARENT, this::executeShowParent, this::canExecuteShowParent);
    kim.addCommandBinding(SHOW_ALL, this::executeShowAll, this::canExecuteShowAll);

    kim.addKeyBinding(new KeyCodeCombination(KeyCode.PAGE_UP), SHOW_PARENT);
    kim.addKeyBinding(new KeyCodeCombination(KeyCode.PAGE_DOWN), HIDE_PARENT);
    kim.addKeyBinding(new KeyCodeCombination(KeyCode.ADD), SHOW_CHILDREN);
    kim.addKeyBinding(new KeyCodeCombination(KeyCode.SUBTRACT), HIDE_CHILDREN);
    kim.addKeyBinding(new KeyCodeCombination(KeyCode.MULTIPLY), SHOW_ALL);
    
    graphControl.setInputMode(inputMode);
  }

  private void initializeGraph() {
    // Create new node style that delegates to other styles for different zoom ranges.
    // The visibility of the nodes is set to ALWAYS, so that the nodes are loaded only once.
    TemplateNodeStyle nodeStyle = new TemplateNodeStyle(
        new TemplateNodeStyleRenderer() {
          @Override
          public IVisibilityTestable getVisibilityTestable(INode node, INodeStyle style) {
            return IVisibilityTestable.ALWAYS;
          }
        });
    nodeStyle.setStyleResource(getClass().getResource("Employee.fxml"));

    graphControl.getGraph().getNodeDefaults().setStyle(nodeStyle);
    graphControl.getGraph().getNodeDefaults().setSize(new SizeD(250, 100));

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setSmoothingLength(10);
    graphControl.getGraph().getEdgeDefaults().setStyle(edgeStyle);

    buildSampleGraph();

    adjustNodeSizes();
  }

  /**
   * Resizes all nodes so that all information on the node is completely visible in the detail level.
   */
  private void adjustNodeSizes() {
    IGraph graph = graphControl.getGraph();

    // set zoom for detail level
    double oldZoom = graphControl.getZoom();
    graphControl.setZoom(1);

    // determine the max size for all nodes...
    double w = 0;
    double h = 0;
    IRenderContext renderContext = graphControl.createRenderContext();
    for (INode node : graph.getNodes()) {
      if (node.getStyle() instanceof TemplateNodeStyle) {
        TemplateNodeStyle style = (TemplateNodeStyle) node.getStyle();
        SizeD size = style.getPreferredSize(renderContext, node);
        w = Math.max(w, size.width);
        h = Math.max(h, size.height);
      }
    }

    // .. and set this size to all nodes
    SizeD newSize = new SizeD(w, h);
    for (INode node : graph.getNodes()) {
      PointD center = node.getLayout().getCenter();
      graph.setNodeLayout(node, RectD.fromCenter(center, newSize));
    }

    graphControl.setZoom(oldZoom);
  }

  /**
   * Loads the sample graph from disk.
   */
  private void buildSampleGraph() {
    try {
      URL url = OrgChartDemo.class.getResource("resources/samplegraph.graphml");

      // create an IOHandler that will be used for all IO operations
      final GraphMLIOHandler ioh = new GraphMLIOHandler();

      // we set the IO handler on the GraphControl, so the GraphControl's IO methods
      // will pick up our handler for use during serialization and deserialization.
      graphControl.setGraphMLIOHandler(ioh);

      ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/OrgChartEditor/1.0", Employee.class);

      graphControl.importFromGraphML(url);
    } catch (IOException e) {
      e.printStackTrace(System.err);
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Searches the root of the graph, i.e. the node with an indegree of zero.
   */
  private INode getRoot(IGraph graph) {
    return graph.getNodes().stream()
        .filter(node -> graph.inDegree(node) == 0)
        .findFirst()
        .orElse(null);
  }

// ===== Tree Layout Configuration and initial execution =====

  /**
   * Applies a tree layout of the Graph provided by the {@link TreeLayout}.
   * The layout and assistant attributes from the business data of the employees are used to
   * guide the the layout.
   */
  public void doLayout() {
    IGraph tree = graphControl.getGraph();

    // use the TreeLayout to calculate a layout for the org-chart
    TreeLayout treeLayout = new TreeLayout();

    // provide additional data to configure the TreeLayout
    TreeLayoutData treeLayoutData = new TreeLayoutData();
    // specify a node placer for each node
    treeLayoutData.setNodePlacers(this::getNodePlacer);
    // specify for each node whether it represents an assistant or not
    treeLayoutData.setAssistantNodes(this::isAssistant);

    // run the layout algorithm (without animation)
    tree.applyLayout(treeLayout, treeLayoutData);
  }

  /**
   * Returns a node placer for the given node.
   */
  private INodePlacer getNodePlacer(INode node) {
    IGraph tree = graphControl.getGraph();
    EmployeeWrapper employee = (EmployeeWrapper) node.getTag();
    if (tree.outDegree(node) == 0 || employee == null) {
      return null;
    }

    INodePlacer childNodePlacer;
    switch (employee.getLayout()) {
      case RightHanging:
        childNodePlacer = RIGHT_HANGING_NODE_PLACER;
        break;
      case LeftHanging:
        childNodePlacer = LEFT_HANGING_NODE_PLACER;
        break;
      case BothHanging:
        childNodePlacer = BOTH_HANGING_NODE_PLACER;
        break;
      default:
        childNodePlacer = DEFAULT_NODE_PLACER;
        break;
    }
    AssistantNodePlacer assistantNodePlacer = new AssistantNodePlacer();
    assistantNodePlacer.setChildNodePlacer(childNodePlacer);
    return assistantNodePlacer;
  }

  /**
   * Returns whether or not the given node is an assistant node.
   */
  private boolean isAssistant(INode node){
    EmployeeWrapper employee = (EmployeeWrapper) node.getTag();
    return employee != null && employee.isAssistant();
  }

  private static final INodePlacer RIGHT_HANGING_NODE_PLACER = new DefaultNodePlacer(ChildPlacement.VERTICAL_TO_RIGHT,
      RootAlignment.LEADING_ON_BUS, RoutingStyle.FORK_AT_ROOT, 30, 30);
  private static final INodePlacer LEFT_HANGING_NODE_PLACER = new DefaultNodePlacer(ChildPlacement.VERTICAL_TO_LEFT,
      RootAlignment.LEADING_ON_BUS, RoutingStyle.FORK_AT_ROOT, 30, 30);
  private static final INodePlacer BOTH_HANGING_NODE_PLACER = new LeftRightNodePlacer();
  private static final INodePlacer DEFAULT_NODE_PLACER = new DefaultNodePlacer(ChildPlacement.HORIZONTAL_DOWNWARD,
      RootAlignment.MEDIAN, 30, 30);


  /**
   * Determines whether the {@link #SHOW_CHILDREN} can be executed.
   */
  private boolean canExecuteShowChildren(ICommand command, Object parameter, Object sender) {
    INode node;
    if (parameter == null && graphControl.getCurrentItem() instanceof INode){
      node = (INode) graphControl.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout && filteredGraphWrapper != null) {
      return filteredGraphWrapper.outDegree(node) != filteredGraphWrapper.getWrappedGraph().outDegree(node);
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #SHOW_CHILDREN}
   */
  private boolean executeShowChildren(ICommand command, Object parameter, Object sender) {
    INode node;
    if (parameter == null && graphControl.getCurrentItem() instanceof INode){
      node = (INode) graphControl.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout) {
      int count = hiddenNodesSet.size();
      IGraph fullGraph = filteredGraphWrapper.getWrappedGraph();
      for (IEdge childEdge : fullGraph.outEdgesAt(node)) {
        INode child = childEdge.getTargetNode();
        if (hiddenNodesSet.remove(child)) {
          fullGraph.setNodeCenter(child, node.getLayout().getCenter());
          fullGraph.clearBends(childEdge);
        }
      }
      refreshLayout(count, node);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #SHOW_PARENT} can be executed.
   */
  private boolean canExecuteShowParent(ICommand command, Object parameter, Object sender) {
    INode node;
    if (parameter == null && graphControl.getCurrentItem() instanceof INode){
      node = (INode) graphControl.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout && filteredGraphWrapper != null) {
      return filteredGraphWrapper.inDegree(node) == 0 && filteredGraphWrapper.getWrappedGraph().inDegree(node) > 0;
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #SHOW_PARENT}
   */
  private boolean executeShowParent(ICommand command, Object parameter, Object sender) {
    INode node;
    if (parameter == null && graphControl.getCurrentItem() instanceof INode){
      node = (INode) graphControl.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout) {
      int count = hiddenNodesSet.size();
      IGraph fullGraph = filteredGraphWrapper.getWrappedGraph();
      for (IEdge parentEdge : fullGraph.inEdgesAt(node)){
        INode parent = parentEdge.getSourceNode();
        if (hiddenNodesSet.remove(parent)) {
          fullGraph.setNodeCenter(parent, node.getLayout().getCenter());
          fullGraph.clearBends(parentEdge);
        }
      }
      refreshLayout(count, node);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #HIDE_PARENT} can be executed.
   */
  private boolean canExecuteHideParent(ICommand command, Object parameter, Object sender) {
    INode node;
    if (parameter == null && graphControl.getCurrentItem() instanceof INode){
      node = (INode) graphControl.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout && filteredGraphWrapper != null && filteredGraphWrapper.contains(node)) {
      return filteredGraphWrapper.inDegree(node) > 0;
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #HIDE_PARENT}
   */
  private boolean executeHideParent(ICommand command, Object parameter, Object sender) {
    final INode node;
    if (parameter == null && graphControl.getCurrentItem() instanceof INode){
      node = (INode) graphControl.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout) {
      int count = hiddenNodesSet.size();

      // this is a root node - remove it and all children
      filteredGraphWrapper.getWrappedGraph().getNodes().stream()
          .filter(testNode -> testNode != node && filteredGraphWrapper.contains(testNode) && filteredGraphWrapper.inDegree(testNode) == 0)
          .forEach(testNode -> hideAllExcept(testNode, node));

      refreshLayout(count, node);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #HIDE_CHILDREN} can be executed.
   */
  private boolean canExecuteHideChildren(ICommand command, Object parameter, Object sender) {
    INode node;
    if (parameter == null && graphControl.getCurrentItem() instanceof INode){
      node = (INode) graphControl.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout && filteredGraphWrapper != null) {
      return filteredGraphWrapper.outDegree(node) > 0;
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #HIDE_CHILDREN}
   */
  private boolean executeHideChildren(ICommand command, Object parameter, Object sender) {
    INode node;
    if (parameter == null && graphControl.getCurrentItem() instanceof INode){
      node = (INode) graphControl.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout) {
      int count = hiddenNodesSet.size();
      for (INode child : filteredGraphWrapper.successors(INode.class, node)) {
        hideAllExcept(child, node);
      }
      refreshLayout(count, node);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #SHOW_PARENT} can be executed.
   */
  private boolean canExecuteShowAll(ICommand command, Object parameter, Object sender) {
    return filteredGraphWrapper != null && !hiddenNodesSet.isEmpty() && !doingLayout;
  }

  /**
   * Handler for the {@link #SHOW_ALL}
   */
  private boolean executeShowAll(ICommand command, Object parameter, Object sender) {
    if (!doingLayout) {
      hiddenNodesSet.clear();
      INode node;
      if (parameter == null && graphControl.getCurrentItem() instanceof INode){
        node = (INode) graphControl.getCurrentItem();
      } else if (parameter instanceof INode) {
        node = (INode) parameter;
      } else {
        return false;
      }
      refreshLayout(-1, node);
      return true;
    }
    return false;
  }

  /**
   * Helper that hides all nodes and its descendants except for a given node
   */
  private void hideAllExcept(INode nodeToHide, INode exceptNode) {
    hiddenNodesSet.add(nodeToHide);
    Iterable<INode> successors = filteredGraphWrapper.getWrappedGraph().successors(INode.class, nodeToHide);
    for (INode child : successors) {
      if (exceptNode != child) {
        hideAllExcept(child, exceptNode);
      }
    }
  }

  // indicates whether a layout is calculated at the moment. Prevents two layouts to be executed at the same time.
  private boolean doingLayout;

  /**
   * Refreshes the layout after children or parent nodes have been added or removed.
   */
  private void refreshLayout(int count, INode fixedNode) {
    if (doingLayout) {
      return;
    }

    if (count != hiddenNodesSet.size()) {
      // tell our filter to refresh the graph
      filteredGraphWrapper.nodePredicateChanged();
      // the commands CanExecute state might have changed - suggest a re-query.
      ICommand.invalidateRequerySuggested();

      // use the TreeLayout to calculate a layout for the org-chart
      TreeLayout treeLayout = new TreeLayout();

      // provide additional data to configure the TreeLayout
      TreeLayoutData treeLayoutData = new TreeLayoutData();
      // specify a node placer for each node
      treeLayoutData.setNodePlacers(this::getNodePlacer);
      // specify for each node whether it represents an assistant or not
      treeLayoutData.setAssistantNodes(this::isAssistant);

      // use the FixNodeLayoutStage to fix the position of the upper left
      // corner of the fixedNode
      FixNodeLayoutStage layoutStage = new FixNodeLayoutStage(treeLayout);
      treeLayout.appendStage(layoutStage);

      // provide additional data to configure the FixNodeLayoutStage
      FixNodeLayoutData fixNodeLayoutData = new FixNodeLayoutData();
      // specify the fixedNode whose upper left corner position should be fixed during layout
      fixNodeLayoutData.setFixedNodes(fixedNode);

      // run the layout algorithm and animate the result
      LayoutExecutor executor = new LayoutExecutor(graphControl, layoutStage);
      executor.setViewportAnimationEnabled(fixedNode == null);
      executor.setEasedAnimationEnabled(true);
      executor.setRunningInThread(true);
      executor.setContentRectUpdatingEnabled(true);
      executor.setDuration(Duration.ofMillis(500));
      executor.setLayoutData(new CompositeLayoutData(treeLayoutData, fixNodeLayoutData));
      // add hook for cleanup
      executor.addLayoutFinishedListener((source, args) -> {
        doingLayout = false;

        // update viewport limiter to use the new content rect
        graphControl.getViewportLimiter().setBounds(graphControl.getContentRect().getEnlarged(20));
      });

      doingLayout = true;
      executor.start();
    }
  }

  /**
   * Moves the ViewPort of the GraphControl to its current item.
   */
  private void zoomToCurrentItem() {
    if (graphControl.getCurrentItem() instanceof INode) {
      INode currentItem = (INode) graphControl.getCurrentItem();
      // visible current item
      if (graphControl.getGraph().contains(currentItem)) {
        ICommand.ZOOM_TO_CURRENT_ITEM.execute(null, graphControl);
      } else {
        // see if it can be made visible
        IGraph fullGraph = filteredGraphWrapper.getWrappedGraph();
        if (fullGraph.contains(currentItem)) {
          // hide all nodes except the node to be displayed and all its descendants
          hiddenNodesSet.clear();
          fullGraph.getNodes().stream()
              .filter(testNode -> testNode != currentItem && fullGraph.inDegree(testNode) == 0)
              .forEach(testNode -> hideAllExcept(testNode, currentItem));

          // reset the layout to make the animation nicer
          filteredGraphWrapper.getNodes().forEach(node -> filteredGraphWrapper.setNodeCenter(node, PointD.ORIGIN));
          filteredGraphWrapper.getEdges().forEach(filteredGraphWrapper::clearBends);
          refreshLayout(-1, null);
        }
      }
    }
  }

  private void initializeTreeView(){
    // first, set up the listeners of the TreeView and its interaction with the rest of the control.

    // add a listener to the selection property of the TreeView that updates the state of the controller when a TreeItem gets selected
    treeView.getSelectionModel().selectedItemProperty().addListener(treeViewSelectionListener);

    // when the user clicks once, zoom to the clicked item. If the user performs a
    // double click, zoom to the clicked item and center it on the screen.
    treeView.setOnMouseClicked(mouseEvent -> {
      if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
        if (mouseEvent.getClickCount() == 2) {
          zoomToTreeItemAndCenter();
        }
      }
    });

    // if the user presses enter, then the selected TreeItem should be centered as well.
    treeView.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        zoomToTreeItemAndCenter();
      }
    });

    // fill the TreeView with the graph model of the GraphControl.
    populateTreeView();
  }

  /**
   * Builds TreeItems that correspond to the graph in the GraphControl.
   */
  private void populateTreeView() {
    IGraph graph = graphControl.getGraph();
    INode root = getRoot(graph);

    if (root.getTag() instanceof EmployeeWrapper) {
      EmployeeWrapper wrapper = (EmployeeWrapper) root.getTag();
      TreeItem<Employee> treeRoot = createEmployeeTreeItem(wrapper.getEmployee(), root);
      treeView.setRoot(treeRoot);
      graph.successors(INode.class, root).forEach(node -> addNodesToTreeView(node, treeRoot));
    }
  }

  /**
   * Recursively adds this node and all its children to the TreeView
   */
  private void addNodesToTreeView(INode node, TreeItem<Employee> treeRoot) {
    if (node.getTag() instanceof EmployeeWrapper) {
      EmployeeWrapper wrapper = (EmployeeWrapper) node.getTag();
      TreeItem<Employee> treeItem = createEmployeeTreeItem(wrapper.getEmployee(), node);
      treeRoot.getChildren().add(treeItem);
      graphControl.getGraph().successors(INode.class, node).forEach(child -> addNodesToTreeView(child, treeItem));
    }
  }

  /**
   * Moves the ViewPort of the GraphControl in such a way that the node
   * of the given TreeItem is centered on the screen.
   */
  private void zoomToTreeItemAndCenter() {
    TreeItem<Employee> treeItem = treeView.getSelectionModel().getSelectedItem();
    if (treeItem != null) {
      INode selectedNode = correspondingNodes.get(treeItem.getValue());
      graphControl.setCurrentItem(selectedNode);
      zoomToCurrentItem();
    }
  }

  /**
   * Moves the ViewPort of the GraphControl in such a way that the current selected tree item
   * is visible, if needed. This won't center the node on the screen.
   */
  private void zoomToTreeItem() {
    TreeItem<Employee> treeItem = treeView.getSelectionModel().getSelectedItem();
    if (treeItem != null) {
      // get the correspondent node in the graph
      INode selectedNode = correspondingNodes.get(treeItem.getValue());
      if (selectedNode != null && graphControl.getGraph().contains(selectedNode)) {
        // select the node in the GraphControl
        graphControl.setCurrentItem(selectedNode);
        IRectangle layout = selectedNode.getLayout();
        graphControl.makeVisible(layout.toRectD());
      }
    }
  }

  /**
   * Selects the EmployeeTreeItem in the TreeView that holds the current item of the GraphControl.
   */
  private void selectCurrentItemInTreeView(){
    // remove the listener to the selection property because we are going to select an item manually and don't want the event to be caught.
    treeView.getSelectionModel().selectedItemProperty().removeListener(treeViewSelectionListener);
    INode node = (INode) graphControl.getCurrentItem();

    TreeItem<Employee> correspondingItem = findCorrespondingTreeItem(treeView.getRoot(), node);

    // set the selection of the TreeView to the corresponding item of the node
    treeView.getSelectionModel().clearSelection();
    treeView.getSelectionModel().select(correspondingItem);

    // re-insert the selection listener.
    treeView.getSelectionModel().selectedItemProperty().addListener(treeViewSelectionListener);
  }

  /**
   * Looks in the TreeView for a TreeItem that has the given INode as the corresponding node.
   */
  private TreeItem<Employee> findCorrespondingTreeItem(TreeItem<Employee> root, INode node) {
    if (correspondingNodes.get(root.getValue()) == node){
      // Its the current item.
      return root;
    } else if (root.getChildren().size() == 0){
      // if its not the current item and its a leaf, the node was not found in this branch.
      return null;
    } else {
      // for every branch of children for the current item, look recursively if the node is in it and return it.
      return root.getChildren().stream()
          .map(employeeTreeItem -> findCorrespondingTreeItem(employeeTreeItem, node))
          .filter(Objects::nonNull)
          .findFirst().orElse(null);
    }
  }


  /**
   * Converts a {@link EmployeeStatus} into a color to display on the screen.
   * @return
   * <ul>
   *   <li>{@link javafx.scene.paint.Color#GREEN} if the employee is present</li>
   *   <li>{@link javafx.scene.paint.Color#RED} if the employee is unavailable</li>
   *   <li>{@link javafx.scene.paint.Color#PURPLE} if the employee is travelling</li>
   * </ul>
   */
  static Color statusToColor(EmployeeStatus status){
    if (status.equals(EmployeeStatus.Present)){
      return Color.GREEN;
    } else if (status.equals(EmployeeStatus.Unavailable)){
      return Color.RED;
    } else if (status.equals(EmployeeStatus.Travel)){
      return Color.PURPLE;
    }
    return Color.WHITE;
  }

  /**
   * Returns a {@link javafx.scene.control.TreeItem} that can display an employee.
   */
  private TreeItem<Employee> createEmployeeTreeItem(Employee employee, INode correspondingNode) {
    HBox graphic = new HBox(2);
    graphic.setAlignment(Pos.CENTER_LEFT);
    Ellipse statusIndicator = new Ellipse(4,4);
    statusIndicator.setFill(statusToColor(employee.getStatus()));
    Label firstName = new Label("");
    Label lastName = new Label("");
    graphic.getChildren().addAll(statusIndicator, firstName, lastName);
    firstName.textProperty().bind(employee.firstNameProperty());
    lastName.textProperty().bind(employee.nameProperty());
    correspondingNodes.put(employee, correspondingNode);
    return new TreeItem<>(employee, graphic);
  }

  /**
   * Updates the overview to the current status of the GraphControl.
   */
  private void fitOverviewBounds(){
    overviewControl.updateContentRect(new InsetsD(1), graphControl.getContentGroup());
    overviewControl.fitContent();
  }

  /**
   * Delegates to {@link GraphControl#fitGraphBounds}.
   */
  private void fitGraphBounds(){
    graphControl.fitGraphBounds();
  }

  private static final Tooltip tooltipInstance = new Tooltip("");

  /**
   * The tooltip instance used for the Employees.
   */
  static Tooltip getTooltipInstance() {
    return tooltipInstance;
  }

  /**
   * Updates the {@link #getTooltipInstance() Tooltip} instance for the current ContentControl.
   * @param control The control that shall be used for the tooltip.
   */
  static void updateTooltip(final ContentControl control) {
    if (control == null) {
      return;
    }

    final DetailTooltip detailTooltip = DetailTooltip.getInstance();
    if (detailTooltip.getEmployee() == null || !detailTooltip.getEmployee().equals(control.getEmployee())) {
      detailTooltip.setEmployee(control.getEmployee());
      detailTooltip.setFullDisplayName(control.getFullDisplayName());
      detailTooltip.setStatusColor(control.getStatusColor());
    }

    final RoughTooltip roughTooltip = RoughTooltip.getInstance();
    if (roughTooltip.getEmployee() == null || !roughTooltip.getEmployee().equals(control.getEmployee())) {
      roughTooltip.setEmployee(control.getEmployee());
      roughTooltip.setShortDisplayName(control.getShortDisplayName());
    }
  }

  @Override
  public String getTitle() {
    return "Organizational Chart Demo - yFiles for JavaFX";
  }

  public static void main(String[] args) {
    launch(args);
  }
}
