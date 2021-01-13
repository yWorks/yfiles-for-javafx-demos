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
package complete.collapse;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.TemplateNodeStyle;
import com.yworks.yfiles.layout.FixNodeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Demonstrates the wrapping and decorating of {@link com.yworks.yfiles.graph.IGraph} instances.
 * <p>
 *   This demo shows a collapsible tree structure. Subtrees can be collapsed or expanded by clicking on
 *   their root nodes.
 * </p>
 * @see com.yworks.yfiles.graph.FilteredGraphWrapper
 */
public class CollapsibleTreeDemo extends DemoApplication {
  public ComboBox<String> layoutComboBox;
  public GraphControl graphControl;
  public WebView webView;

  // graph containing all nodes
  private DefaultGraph fullGraph;
  // graph that contains visible nodes
  private FilteredGraphWrapper filteredGraph;
  // list that stores collapsed nodes
  private final List<INode> collapsedNodes = new ArrayList<>();
  // currently selected layout algorithm
  private ILayoutAlgorithm currentLayout;
  // mapper for mapping layout algorithms to their string representation in the combobox
  private IMapper<String, ILayoutAlgorithm> layoutMapper = new Mapper<>();
  // the node that has just been toggled and should stay fixed.
  private INode toggledNode;
  // style for leaf nodes
  private INodeStyle leafNodeStyle;

  /**
   * The command that can be used by the node buttons to toggle the visibility of the child nodes.
   */
  static final ICommand TOGGLE_CHILDREN = ICommand.createCommand("Toggle");

  private final Random random = new Random(666);

  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(webView, this);

    // initialize demo
    initializeInputModes();
    initializeGraph();
  }

  /**
   * Creates a mode and registers it as the {@link CanvasControl#setInputMode(IInputMode)}
   */
  private void initializeInputModes() {
    // create a simple mode that reacts to mouse clicks on nodes.
    GraphViewerInputMode inputMode = new GraphViewerInputMode();

    // register command bindings: The command that can be used by the node buttons to toggle the visibility of the child nodes.
    inputMode.getKeyboardInputMode().addCommandBinding(TOGGLE_CHILDREN, this::toggleChildrenExecuted);
    inputMode.getKeyboardInputMode().addKeyBinding(new KeyCodeCombination(KeyCode.ENTER), TOGGLE_CHILDREN);

    graphControl.setInputMode(inputMode);
  }

  /**
   * Initializes the graph instance, setting default styles and creating a small sample graph.
   */
  protected void initializeGraph() {
    Class resolver = getClass();

    leafNodeStyle = new TemplateNodeStyle(resolver.getResource("LeafNodeStyle.fxml"));

    // create the graph instance that will hold the complete graph.
    fullGraph = new DefaultGraph();

    // Create a nice default style for the nodes...
    fullGraph.getNodeDefaults().setStyle(new TemplateNodeStyle(resolver.getResource("InnerNodeStyle.fxml")));
    fullGraph.getNodeDefaults().setSize(new SizeD(60, 30));
    fullGraph.getNodeDefaults().setStyleInstanceSharingEnabled(false);

    // ...and a style for the labels
    fullGraph.getNodeDefaults().getLabelDefaults().setStyle(new DefaultLabelStyle());

    // now build a simple sample tree
    buildTree(fullGraph, 3, 3, 3);

    // create a view of the graph that contains only non-collapsed subtrees.
    // use a predicate method to decide what nodes should be part of the graph.
    filteredGraph = new FilteredGraphWrapper(fullGraph, this::getNodePredicate, this::getEdgePredicate);

    // display the filtered new graph in our control.
    graphControl.setGraph(filteredGraph);

    // create layout algoritms
    setupLayouts();
  }

  /**
   * Called when the stage is shown and the {@link GraphControl} is already resized to its preferred size.
   * The graph is moved to the center of the <code>GraphControl</code> and the initial layout is calculated.
   */
  protected void onLoaded() {
    // center the graph to prevent the initial layout fading in from the top left corner
    graphControl.fitGraphBounds();

    // calculate and run the initial layout.
    runLayout(true, null);
  }

  /**
   * Gets the predicate for the filtered graph wrapper that indicates whether an edge should be visible.
   * As edges are only in the filtered graph when both end nodes are in the graph, they should always be visible.
   *
   * @param edge The edge the visible state is returned for.
   * @return <code>true</code> if the edge should be visible, <code>false</code>otherwise.
   */
  private boolean getEdgePredicate(IEdge edge) {
    // return true for any edge
    return true;
  }

  /**
   * Gets the predicate for the filtered graph wrapper that indicates whether a node should be visible.
   *
   * @param node The node the visible state is returned for.
   * @return <code>true</code> if the node should be visible, <code>false</code>otherwise.
   */
  private boolean getNodePredicate(INode node) {
    // return true if none of the parent nodes is collapsed
    for (IEdge edge : fullGraph.inEdgesAt(node)) {
      final INode parent = (INode) edge.getSourcePort().getOwner();
      return !collapsedNodes.contains(parent) && getNodePredicate(parent);
    }
    return true;
  }

  /**
   * Builds a sample graph.
   */
  private void buildTree(IGraph graph, int children, int levels, int collapseLevel) {
    final INode root = graph.createNode(new PointD(20, 20));
    setCollapsedTag(root, false);
    addChildren(levels, graph, root, children, collapseLevel);
  }

  /**
   * Recursively add children to the tree.
   */
  private void addChildren(int level, IGraph graph, INode root, int childCount, int collapseLevel) {
    int actualChildCount = random.nextInt(childCount) + 1;
    for (int i = 0; i < actualChildCount; i++) {
      INode child = graph.createNode(new PointD(20, 20));
      graph.createEdge(root, child);
      if (level < collapseLevel) {
        collapsedNodes.add(child);
        setCollapsedTag(child, true);
      } else {
        setCollapsedTag(child, false);
      }
      if (level > 0) {
        addChildren(level - 1, graph, child, 4, 2);
      } else {
        graph.setStyle(child, leafNodeStyle);
      }
    }
  }

  /**
   * Called when the ToggleChildren command has been executed.
   * <p>
   *   Note: Toggles the visibility of the node's children.
   * </p>
   */
  private boolean toggleChildrenExecuted(ICommand command, Object parameter, Object source) {
    INode node = parameter instanceof INode ? (INode) parameter : graphControl.getCurrentItem() instanceof INode ? (INode) graphControl.getCurrentItem() : null;
    if (node != null) {
      boolean canExpand = filteredGraph.outDegree(node) != filteredGraph.getWrappedGraph().outDegree(node);
      if (canExpand) {
        expand(node);
      } else {
        collapse(node);
      }
      return true;
    }
    return false;
  }

  /**
   * Show the children of a collapsed node.
   *
   * @param node The node that should be expanded.
   */
  private void expand(INode node) {
    if (collapsedNodes.contains(node)) {
      toggledNode = node;
      setCollapsedTag(node, false);
      alignChildren(node);
      collapsedNodes.remove(node);
      filteredGraph.nodePredicateChanged();
      runLayout(false, node);
    }
  }

  /**
   * Hide the children of a expanded node.
   *
   * @param node The node that should be collapsed.
   */
  private void collapse(INode node) {
    if (!collapsedNodes.contains(node)) {
      toggledNode = node;
      setCollapsedTag(node, true);
      collapsedNodes.add(node);
      filteredGraph.nodePredicateChanged();
      runLayout(false, node);
    }
  }

  /**
   * Positions all children of the given node on the same location as the node so they appear to move out of their
   * parent node.
   *
   * @param node The parent node.
   */
  private void alignChildren(INode node) {
    // This method is used to set the initial positions of the children
    // of a node which gets expanded to the position of the expanded node.
    // This looks nicer in the following animated layout. Try commenting
    // out the method body to see the difference.
    final IRectangle nodeLayout = node.getLayout();
    final PointD center =
        new PointD(nodeLayout.getX() + nodeLayout.getWidth() * 0.5, nodeLayout.getY() + nodeLayout.getHeight() * 0.5);
    fullGraph.outEdgesAt(node).stream()
        .filter(edge -> edge.getSourcePort().getOwner() == node)
        .forEach(edge -> {
          fullGraph.clearBends(edge);
          INode child = (INode) edge.getTargetPort().getOwner();
          fullGraph.setNodeCenter(child, center);
          alignChildren(child);
        });
  }

  /**
   * Stores collapsed state for a node in its style.
   *
   * @param node      The node whose state is set.
   * @param collapsed The new collapsed state.
   */
  private void setCollapsedTag(INode node, boolean collapsed) {
    final TemplateNodeStyle style = (TemplateNodeStyle) node.getStyle();
    if (style != null) {
      final StyleTag styleTag = (StyleTag) style.getStyleTag();
      if (styleTag == null) {
        style.setStyleTag(new StyleTag(collapsed));
      } else {
        styleTag.setCollapsed(collapsed);
      }
    }
  }

  /**
   * Configures different layout algorithms for trees.
   */
  private void setupLayouts() {
    TreeLayout treeLayout = new TreeLayout();
    treeLayout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    treeLayout.prependStage(new FixNodeLayoutStage());
    layoutMapper.setValue("Tree", treeLayout);

    BalloonLayout balloonLayout = new BalloonLayout();
    balloonLayout.setFromSketchModeEnabled(true);
    balloonLayout.setCompactnessFactor(1.0);
    balloonLayout.setOverlapsAllowed(true);
    balloonLayout.prependStage(new FixNodeLayoutStage());
    layoutMapper.setValue("Balloon", balloonLayout);

    HierarchicLayout hierarchicLayout = new HierarchicLayout();
    hierarchicLayout.prependStage(new FixNodeLayoutStage());
    layoutMapper.setValue("Hierarchic", hierarchicLayout);

    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setMinimumNodeDistance(40);
    organicLayout.prependStage(new FixNodeLayoutStage());
    organicLayout.setDeterministicModeEnabled(true);
    layoutMapper.setValue("Organic", organicLayout);

    OrthogonalLayout orthogonalLayout = new OrthogonalLayout();
    orthogonalLayout.prependStage(new FixNodeLayoutStage());
    layoutMapper.setValue("Orthogonal", orthogonalLayout);

    // set initial layout algorithm
    currentLayout = treeLayout;
  }

  /**
   * Runs layout calculation using the currently selected layout algorithm.
   */
  private void runLayout(boolean animateViewport, INode toggledNode) {
    if (currentLayout != null) {
      // provide additional data to configure the FixNodeLayoutStage
      FixNodeLayoutData fixNodeLayoutData = new FixNodeLayoutData();
      // specify the node whose position is to be fixed during layout
      fixNodeLayoutData.setFixedNodes(toggledNode);
      // run the layout and animate the result
      LayoutExecutor layoutExecutor = new LayoutExecutor(graphControl, currentLayout);
      layoutExecutor.setContentRectUpdatingEnabled(true);
      layoutExecutor.setViewportAnimationEnabled(animateViewport);
      layoutExecutor.setDuration(Duration.ofMillis(300));
      layoutExecutor.setLayoutData(fixNodeLayoutData);
      layoutExecutor.start();
    }
  }

  /**
   * Called when the selection of the layout algorithm combobox changes.
   * A layout is run with the newly selected layout.
   */
  public void selectedLayoutChanged() {
    currentLayout = layoutMapper.getValue(layoutComboBox.getSelectionModel().getSelectedItem());
    runLayout(true, null);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
