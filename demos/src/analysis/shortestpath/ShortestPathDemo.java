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
package analysis.shortestpath;

import com.yworks.yfiles.analysis.AllPairsShortestPaths;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.LabelTextValidatingEventArgs;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import com.yworks.yfiles.view.input.QueryItemToolTipEventArgs;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.IconProvider;
import toolkit.Themes;
import toolkit.WebViewUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Demonstrates usage of a yFiles algorithm on an {@link com.yworks.yfiles.graph.IGraph}.
 * <p>
 *   This demo dynamically calculates the set of edges that lie on shortest paths
 *   between two sets of nodes (sources and targets).
 *   Geometric edge lengths are used for the weight of the edges, unless there are numeric
 *   labels attached to them in which case the value of the label text is used.
 *   Note that negative edge weights will be interpreted as 0.
 * </p>
 */
public class ShortestPathDemo extends DemoApplication {
  public GraphControl graphControl;
  public ComboBox<String> layoutComboBox;
  public ComboBox directedComboBox;
  public WebView helpView;
  public Button applyLayoutButton;
  public Button editLabelsButton;
  public Button newButton;

  // holds all available layout algorithms by name for the combo box
  private final IMapper<String, ILayoutAlgorithm> layouts = new Mapper<>();
  // holds the currently chosen layout algorithm
  private ILayoutAlgorithm currentLayout;

  // the styles to use for source nodes, target nodes, and ordinary nodes
  private INodeStyle defaultNodeStyle, targetNodeStyle, sourceNodeStyle, sourceAndTargetNodeStyle;
  // the style to use for ordinary edges and edges that lie on a shortest path
  private PolylineEdgeStyle defaultEdgeStyle, pathEdgeStyle;
  // the current source nodes
  private List<INode> sourceNodes;
  // the current target nodes
  private List<INode> targetNodes;

  // whether to use directed path calculation
  private boolean directed;

  // for creating sample graphs
  private RandomGraphGenerator randomGraphGenerator;

  // the set of the edges that are currently part of the path
  private Set<IEdge> pathEdges = new HashSet<>();

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    sourceNodes = new ArrayList<>();
    targetNodes = new ArrayList<>();

    initializeInputModes();
    initializeStyles();
    initializeLayouts();

    // initialize random graph generator
    randomGraphGenerator = new RandomGraphGenerator();
    randomGraphGenerator.setCycleCreationAllowed(true);
    randomGraphGenerator.setParallelEdgeCreationAllowed(true);
    randomGraphGenerator.setSelfLoopCreationAllowed(true);
    randomGraphGenerator.setEdgeCount(40);
    randomGraphGenerator.setNodeCount(30);
    WebViewUtils.initHelp(helpView, this);
  }

  private void initializeInputModes() {
    // build an input mode and register for the various events
    // that could change the shortest path calculation
    GraphEditorInputMode editMode = new GraphEditorInputMode();

    // deletion
    editMode.addDeletedSelectionListener(this::calculateShortestPath);
    // edge creation
    editMode.getCreateEdgeInputMode().addEdgeCreatedListener(this::calculateShortestPath);
    // edge reversal
    graphControl.getGraph().addEdgePortsChangedListener(this::calculateShortestPath);
    // movement of items
    editMode.getMoveInputMode().addDragFinishedListener(this::calculateShortestPath);
    // resizing of items as well as creating and moving bends
    editMode.getHandleInputMode().addDragFinishedListener(this::calculateShortestPath);
    // adding or changing labels
    editMode.addLabelAddedListener(this::calculateShortestPath);
    editMode.addLabelTextChangedListener(this::calculateShortestPath);

    // allow only numeric label texts
    editMode.addValidateLabelTextListener(this::validateLabelText);

    // also prepare a context menu
    editMode.addPopulateItemContextMenuListener(this::populateNodeContextMenu);

    // show weight tooltips
    editMode.setToolTipItems(GraphItemTypes.EDGE);
    editMode.addQueryItemToolTipListener(this::queryItemToolTip);
    editMode.getMouseHoverInputMode().setToolTipLocationOffset(new PointD(0, -20));

    // when the WaitInputMode kicks in, disable buttons which might cause
    // inconsistencies when performed while a layout calculation is running
    editMode.getWaitInputMode().addWaitingStartedListener((source, args) -> {
      newButton.setDisable(true);
      layoutComboBox.setDisable(true);
      applyLayoutButton.setDisable(true);
    });
    editMode.getWaitInputMode().addWaitingEndedListener((source, args) -> {
      newButton.setDisable(false);
      layoutComboBox.setDisable(false);
      applyLayoutButton.setDisable(false);
    });

    graphControl.setInputMode(editMode);
  }

  /**
   * Allows only empty labels (for deletion) and labels containing a floating point value.
   */
  private void validateLabelText(Object sender, LabelTextValidatingEventArgs args) {
    args.setNewText(args.getNewText().trim());
    if (args.getNewText().isEmpty()) {
      return;
    }
    try {
      if (Double.parseDouble(args.getNewText()) < 0) {
        args.setCanceling(true);
      }
    } catch (NumberFormatException e) {
      args.setCanceling(true);
    }
  }

  /**
   *  Populates the popup menu for nodes.
   */
  private void populateNodeContextMenu(Object sender, PopulateItemContextMenuEventArgs<IModelItem> args) {
    ISelectionModel<INode> selection = graphControl.getSelection().getSelectedNodes();
    if (args.getItem() instanceof INode) {
      INode node = (INode) args.getItem();
      if (!selection.isSelected(node)) {
        selection.clear();
        selection.setSelected(node, true);
        graphControl.setCurrentItem(node);
      }
    }
    if (selection.size() > 0) {
      final MenuItem markAsSourceItem = new MenuItem("Mark as Source");
      markAsSourceItem.setOnAction(actionEvent -> {
        List<INode> selectedNodes = new ArrayList<>();
        for (INode node : selection) {
          selectedNodes.add(node);
        }
        mark(selectedNodes, true);
      });
      ContextMenu contextMenu = (ContextMenu) args.getMenu();
      contextMenu.getItems().add(markAsSourceItem);
      final MenuItem markAsTargetItem = new MenuItem("Mark as Target");
      markAsTargetItem.setOnAction(actionEvent -> {
        List<INode> selectedNodes = new ArrayList<>();
        selection.forEach(selectedNodes::add);
        mark(selectedNodes, false);
      });
      contextMenu.getItems().add(markAsTargetItem);

      // check if one or more of the selected nodes are already marked as source or target
      boolean marked = false;
      for (INode n : selection) {
        if (sourceNodes.contains(n) || targetNodes.contains(n)) {
          marked = true;
          break;
        }
      }
      if (marked) {
        // add the 'Remove Mark' item
        MenuItem removeMarkItem = new MenuItem("Remove Mark");
        removeMarkItem.setOnAction(actionEvent -> {
          List<INode> sn = new ArrayList<>(sourceNodes);
          sourceNodes.stream().filter(selection::isSelected).forEach(sn::remove);
          mark(sn, true);
          List<INode> tn = new ArrayList<>(targetNodes);
          targetNodes.stream().filter(selection::isSelected).forEach(tn::remove);
          mark(tn, false);
        });
        contextMenu.getItems().add(removeMarkItem);
      }
    }
    args.setHandled(true);
  }

  /**
   * Shows the weight of the edge as a tooltip.
   */
  private void queryItemToolTip(Object sender, QueryItemToolTipEventArgs<IModelItem> args) {
    IEdge edge = (IEdge) args.getItem();
    if (edge != null) {
      args.setToolTip(new Tooltip("Weight = " + getEdgeWeight(edge)));
    }
  }

  /**
   * Initializes the styles to use for the graph.
   */
  private void initializeStyles() {
    defaultNodeStyle = DemoStyles.createDemoNodeStyle(Themes.PALETTE_ORANGE);
    sourceNodeStyle = DemoStyles.createDemoNodeStyle(Themes.PALETTE_GREEN);
    targetNodeStyle = DemoStyles.createDemoNodeStyle(Themes.PALETTE_RED);
    sourceAndTargetNodeStyle = DemoStyles.createDemoNodeStyle(Themes.PALETTE_BLUE);
    defaultEdgeStyle = DemoStyles.createDemoEdgeStyle();

    pathEdgeStyle = new PolylineEdgeStyle();
    pathEdgeStyle.setPen(new Pen(Color.RED, 4.0));
    pathEdgeStyle.setTargetArrow(directed ? new Arrow(ArrowType.DEFAULT, new Pen(Color.RED), Color.RED)  : IArrow.NONE);

    graphControl.getGraph().getNodeDefaults().setStyle(defaultNodeStyle);
    graphControl.getGraph().getNodeDefaults().setSize(new SizeD(30, 30));
    graphControl.getGraph().getEdgeDefaults().setStyle(defaultEdgeStyle);

    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(Font.font(10));
    labelStyle.setTextPaint(Color.BLACK);
    labelStyle.setBackgroundPaint(Color.WHITE);
    graphControl.getGraph().getEdgeDefaults().getLabelDefaults().setStyle(labelStyle);
  }

  /** Initializes all layout algorihtms. */
  private void initializeLayouts() {
    layouts.setValue("Hierarchic Layout", new HierarchicLayout());
    final OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setMinimumNodeDistance(40);
    layouts.setValue("Organic Layout", organicLayout);
    layouts.setValue("Orthogonal Layout", new OrthogonalLayout());
  }

  /** Initializes parts of the demo that depends on the size of the scene. */
  protected void onLoaded() {
    generateGraph();

    directedComboBox.getSelectionModel().select(1);
    layoutComboBox.getSelectionModel().select(1);
    currentLayout = layouts.getValue(layoutComboBox.getSelectionModel().getSelectedItem());

    updateLayoutButtonGraphic();
  }

  /** Marks all selected nodes as source nodes for shortest paths when the according button is activated. */
  public void markSourceNodesButtonClick() {
    List<INode> selectedNodes = graphControl.getSelection().getSelectedNodes().stream().collect(Collectors.toList());
    mark(selectedNodes, true);
  }

  /** Marks all selected nodes as target nodes for shortest paths when the according button is activated. */
  public void markTargetNodesButtonClick() {
    List<INode> selectedNodes = graphControl.getSelection().getSelectedNodes().stream().collect(Collectors.toList());
    mark(selectedNodes, false);
  }

  /** Make the graph directed or undirected, respectively, when the selection of <code>directedComboBox</code> changed. */
  public void directedComboBoxSelectedIndexChanged() {
    directed = directedComboBox.getSelectionModel().getSelectedIndex() == 0;
    defaultEdgeStyle.setTargetArrow(directed ? DemoStyles.createDemoEdgeStyle(Themes.PALETTE_ORANGE, true).getTargetArrow() : IArrow.NONE);
    pathEdgeStyle.setTargetArrow(directed ? new Arrow(ArrowType.DEFAULT, new Pen(Color.RED), Color.RED)  : IArrow.NONE);
    calculateShortestPath(graphControl, IEventArgs.EMPTY);
  }

  public void newGraphButtonClick() {
    generateGraph();
  }

  /**
   * Marks the list of nodes as source respectively target nodes.
   */
  private void mark(List<INode> nodes, boolean asSource) {
    // Reset style of old target nodes
    (asSource ? sourceNodes : targetNodes).stream()
        .filter(graphControl.getGraph()::contains)
        .forEach(node -> graphControl.getGraph().setStyle(node, defaultNodeStyle));

    if (asSource) {
      sourceNodes = nodes;
    } else {
      targetNodes = nodes;
    }

    setStyles();
    calculateShortestPath(graphControl, IEventArgs.EMPTY);
  }

  /**
   * Sets the node styles for source and target nodes.
   */
  private void setStyles() {
    // set target node styles
    for (INode targetNode : targetNodes) {
      graphControl.getGraph().setStyle(targetNode, targetNodeStyle);
    }

    // set source node styles
    for (INode sourceNode : sourceNodes) {
      // check for nodes which are both - source and target
      if (targetNodes.contains(sourceNode)) {
        graphControl.getGraph().setStyle(sourceNode, sourceAndTargetNodeStyle);
      } else {
        graphControl.getGraph().setStyle(sourceNode, sourceNodeStyle);
      }
    }
  }

  /** Applies the layout and recalculates the shortest paths. */
  private void applyLayout() {
    if (currentLayout != null) {
      graphControl.morphLayout(currentLayout, Duration.ofSeconds(1), this::calculateShortestPath);
    }
  }

  /** Handles the Click event of the applyLayoutButton control. */
  public void applyLayoutButtonClick() {
    applyLayout();
  }

  /** Handles the SelectedIndexChanged event of the layoutComboBox control. */
  public void layoutComboBoxSelectedIndexChanged() {
    currentLayout = layouts.getValue(layoutComboBox.getSelectionModel().getSelectedItem());
    updateLayoutButtonGraphic();
    applyLayout();
  }

  /** Generates a new graph, applies the layout and recalculates the shortest paths. */
  private void generateGraph() {
    graphControl.getGraph().clear();
    randomGraphGenerator.generate(graphControl.getGraph());

    // center the graph to prevent the initial layout fading in from the top left corner
    graphControl.fitGraphBounds();
    applyLayout();
  }

  /**
   * Calculates the shortest paths from a set of source nodes to a set of target nodes and marks it. <p> This is the
   * implementation for a list of source and target nodes. </p>
   */
  private void calculateShortestPath(final Object source, final IEventArgs args) {
    // reset old path edges
    pathEdges.stream()
        .filter(graphControl.getGraph()::contains)
        .forEach(edge -> graphControl.getGraph().setStyle(edge, defaultEdgeStyle));
    pathEdges.clear();

    // remove deleted nodes
    ArrayList<INode> sn = new ArrayList<>(sourceNodes);
    sn.stream()
        .filter(sourceNode -> !graphControl.getGraph().contains(sourceNode))
        .forEach(sourceNodes::remove);
    ArrayList<INode> tn = new ArrayList<>(targetNodes);
    tn.stream()
        .filter(targetNode -> !graphControl.getGraph().contains(targetNode))
        .forEach(targetNodes::remove);

    // show wait cursor while running the shortest path algorithm asynchronously
    GraphEditorInputMode geim = (GraphEditorInputMode) graphControl.getInputMode();
    geim.setWaiting(true);

    AllPairsShortestPaths allPaths = new AllPairsShortestPaths();
    allPaths.setCosts(this::getEdgeWeight);
    allPaths.setDirected(directed);
    allPaths.setSources(sourceNodes);
    allPaths.setSinks(targetNodes);

    allPaths.runAsync(graphControl.getGraph()).thenAccept(result -> {
      // collect all edges of all paths
      pathEdges = result.getPaths().stream()
          .flatMap(path -> path.getEdges().stream())
          .collect(Collectors.toSet());

      // mark path with path style
      pathEdges.stream()
          .filter(graphControl.getGraph()::contains)
          .forEach(edge -> graphControl.getGraph().setStyle(edge, pathEdgeStyle));

      geim.setWaiting(false);

      graphControl.invalidate();
    });
  }

  /**
   * Returns the edge weight for a given edge.
   */
  private double getEdgeWeight(IEdge edge) {
    // if edge has at least one label...
    if (edge.getLabels().size() > 0) {
      // ..try to return its value
      try {
        double weight = Double.parseDouble(edge.getLabels().getItem(0).getText());
        return Math.max(weight, 0);
      } catch (NumberFormatException e) {
        // do nothing
      }
    }

    // calculate geometric edge length
    PointD[] edgePoints = new PointD[edge.getBends().size() + 2];

    edgePoints[0] = edge.getSourcePort().getLocation();
    edgePoints[edge.getBends().size() + 1] = edge.getTargetPort().getLocation();

    for (int i = 0; i < edge.getBends().size(); i++) {
      edgePoints[i + 1] = edge.getBends().getItem(i).getLocation().toPointD();
    }

    double totalEdgeLength = 0;
    for (int i = 0; i < edgePoints.length - 1; i++) {
      totalEdgeLength += edgePoints[i].distanceTo(edgePoints[i + 1]);
    }
    return totalEdgeLength;
  }

  /**
   * Handles the click event of the setLabelValueButton control.
   * <p>
   *   Shows a dialog that can be used to specify a numeric label for all edges.
   * </p>
   */
  public void editLabelsButtonClick() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.initOwner(editLabelsButton.getScene().getWindow());
    dialog.initModality(Modality.WINDOW_MODAL);
    dialog.setTitle("Uniform Edge Weights");
    dialog.setHeaderText("Enter Uniform Edge Weight");
    Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.addEventFilter(ActionEvent.ACTION, event -> {
      if (!isValidLabelText(dialog.getEditor().getText())) {
        dialog.setHeaderText("Only non-negative numbers allowed!");
        event.consume(); //not valid
      }
    });

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {
      String text = result.get();
      for (IEdge edge : graphControl.getGraph().getEdges()) {
        if (edge.getLabels().size() > 0) {
          graphControl.getGraph().setLabelText(edge.getLabels().getItem(0), text);
        } else {
          graphControl.getGraph().addLabel(edge, text);
        }
      }

      calculateShortestPath(graphControl, IEventArgs.EMPTY);
    }
  }


  /**
   * Validates the given text. Only non-negative numbers are allowed.
   */
  private boolean isValidLabelText(String text) {
    try {
      return text.isEmpty() || Double.parseDouble(text) >= 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /** Handles the click event of the deleteLabelsButton control. */
  public void removeLabelsButtonClick() {
    for (IEdge edge : graphControl.getGraph().getEdges()) {
      List<ILabel> labels = new ArrayList<>();
      edge.getLabels().forEach(labels::add);
      labels.forEach(graphControl.getGraph()::remove);
    }
    calculateShortestPath(graphControl, IEventArgs.EMPTY);
  }

  /** Updates the graphic on the apply layout button depending on the current layout algorithm. */
  private void updateLayoutButtonGraphic() {
    switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
      case "Hierarchic Layout":
        applyLayoutButton.setGraphic(IconProvider.valueOf("LAYOUT_HIERARCHIC"));
        break;
      case "Organic Layout":
        applyLayoutButton.setGraphic(IconProvider.valueOf("LAYOUT_ORGANIC"));
        break;
      case "Orthogonal Layout":
        applyLayoutButton.setGraphic(IconProvider.valueOf("LAYOUT_ORTHOGONAL"));
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
