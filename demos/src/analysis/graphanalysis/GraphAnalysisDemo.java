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
package analysis.graphanalysis;

import analysis.graphanalysis.algorithms.AlgorithmConfiguration;
import analysis.graphanalysis.algorithms.CentralityConfig;
import analysis.graphanalysis.algorithms.ConnectivityConfig;
import analysis.graphanalysis.algorithms.CyclesConfig;
import analysis.graphanalysis.algorithms.MinimumSpanningTreeConfig;
import analysis.graphanalysis.algorithms.PathsConfig;
import analysis.graphanalysis.algorithms.SubstructuresConfig;
import com.yworks.yfiles.graph.AdjacencyTypes;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.ComponentArrangementStyles;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayoutData;
import com.yworks.yfiles.layout.organic.Scope;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.MoveInputMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Pattern;

/**
 * This demo showcases a selection of algorithms to analyse the structure of a graph.
 */
public class GraphAnalysisDemo extends DemoApplication {
  private static final String validationPattern = "^(0*[1-9][0-9]*(\\.[0-9]+)?|0+\\.[0-9]*[1-9][0-9]*)$";

  @FXML private WebView help;
  @FXML private GraphControl graphControl;

  @FXML private ComboBox<NamedEntry> sampleComboBox;
  @FXML private ComboBox<NamedEntry> algorithmComboBox;
  @FXML private CheckBox edgeWeightsCheckBox;
  @FXML private CheckBox directionCheckBox;

  @FXML private Button prevSampleButton;
  @FXML private Button nextSampleButton;
  @FXML private Button generateEdgeLabelsButton;
  @FXML private Button deleteEdgeLabelsButton;
  @FXML private Button layoutButton;

  @FXML private Spinner<Integer> kCoreSpinner;
  @FXML private Label kCoreLabel;

  private Mapper<INode, Boolean> incrementalElements;
  private Mapper<INode, Boolean> incrementalNodesMapper;

  private boolean preventLayout;
  private boolean enabledUI = true;

  /**
   * Enables or disables UI elements while loading or layouting.
   */
  private void enableUI(boolean enable) {
    if (enabledUI != enable) {
      enabledUI = enable;

      AlgorithmConfiguration config = getAlgorithmConfig();

      sampleComboBox.setDisable(!enable);
      algorithmComboBox.setDisable(!enable);
      edgeWeightsCheckBox.setDisable(!(enable && config.supportsEdgeWeights()));
      directionCheckBox.setDisable(!(enable && config.supportsDirectedEdges()));
      prevSampleButton.setDisable(!(enable && isPrevSampleActionEnabled()));
      nextSampleButton.setDisable(!(enable && isNextSampleActionEnabled()));
      generateEdgeLabelsButton.setDisable(!(enable && config.supportsEdgeWeights()));
      deleteEdgeLabelsButton.setDisable(!(enable && config.supportsEdgeWeights()));
      layoutButton.setDisable(!enable);
    }
  }

  /**
   * Selects the previous sample graph.
   */
  @FXML private void onPrevSample() {
    sampleComboBox.getSelectionModel().selectPrevious();
  }

  /**
   * Returns whether there are previous samples available.
   * @return whether there are previous samples available.
   */
  private boolean isPrevSampleActionEnabled() {
    return sampleComboBox.getSelectionModel().getSelectedIndex() > 0;
  }

  /**
   * Selects the next sample graph.
   */
  @FXML private void onNextSample() {
    sampleComboBox.getSelectionModel().selectNext();
  }

  /**
   * Returns whether there are next samples available.
   * @return whether there are next samples available.
   */
  private boolean isNextSampleActionEnabled() {
    return sampleComboBox.getSelectionModel().getSelectedIndex() < sampleComboBox.getItems().size() - 1;
  }

  /**
   * Configures the {@link ComboBox} for choosing the current sample graph.
   */
  private void configureSampleComboBox() {
   sampleComboBox.getItems().addAll(
        new NamedEntry("Sample: Minimum Spanning Tree", "minimumspanningtree"),
        new NamedEntry("Sample: Connected Components", "connectivity"),
        new NamedEntry("Sample: Biconnected Components", "connectivity"),
        new NamedEntry("Sample: Strongly Connected Components", "connectivity"),
        new NamedEntry("Sample: Reachability", "connectivity"),
        new NamedEntry("Sample: k-Cores", "kcore"),
        new NamedEntry("Sample: Shortest Paths", "paths"),
        new NamedEntry("Sample: All Paths", "paths"),
        new NamedEntry("Sample: All Chains", "paths"),
        new NamedEntry("Sample: Single Source", "paths"),
        new NamedEntry("Sample: Cycles", "cycles"),
        new NamedEntry("Sample: Degree Centrality", "centrality"),
        new NamedEntry("Sample: Weight Centrality", "centrality"),
        new NamedEntry("Sample: Graph Centrality", "centrality"),
        new NamedEntry("Sample: Node Edge Betweeness Centrality", "centrality"),
        new NamedEntry("Sample: Closeness Centrality", "centrality"),
        new NamedEntry("Sample: Eigenvector Centrality", "centrality"),
        new NamedEntry("Sample: Page Rank", "centrality"),
        new NamedEntry("Sample: Chain Substructures", "substructures"),
        new NamedEntry("Sample: Cycle Substructures", "substructures"),
        new NamedEntry("Sample: Star Substructures", "substructures"),
        new NamedEntry("Sample: Tree Substructures", "substructures"),
        new NamedEntry("Sample: Clique Substructures", "cliques")
   );

    sampleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
      onSampleChanged()
    );
  }

  /**
   * Configures the {@link ComboBox} for choosing the current algorithm.
   */
  private void configureAlgorithmComboBox() {
    algorithmComboBox.getItems().addAll(
        new NamedEntry("Algorithm: Minimum Spanning Tree",
            new MinimumSpanningTreeConfig()),
        new NamedEntry("Algorithm: Connected Components",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.CONNECTED_COMPONENTS)),
        new NamedEntry("Algorithm: Biconnected Components",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.BICONNECTED_COMPONENTS)),
        new NamedEntry("Algorithm: Strongly Connected Components",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.STRONGLY_CONNECTED_COMPONENTS)),
        new NamedEntry("Algorithm: Reachability",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.REACHABILITY)),
        new NamedEntry("Algorithm: k-Core",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.KCORE)),
        new NamedEntry("Algorithm: Shortest Paths",
            new PathsConfig(PathsConfig.AlgorithmType.ALGORITHM_TYPE_SHORTEST_PATHS)),
        new NamedEntry("Algorithm: All Paths",
            new PathsConfig(PathsConfig.AlgorithmType.ALGORITHM_TYPE_ALL_PATHS)),
        new NamedEntry("Algorithm: All Chains",
            new PathsConfig(PathsConfig.AlgorithmType.ALGORITHM_TYPE_ALL_CHAINS)),
        new NamedEntry("Algorithm: Single Source",
            new PathsConfig(PathsConfig.AlgorithmType.ALGORITHM_TYPE_SINGLE_SOURCE)),
        new NamedEntry("Algorithm: Cycles",
            new CyclesConfig()),
        new NamedEntry("Algorithm: Degree Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.DEGREE_CENTRALITY)),
        new NamedEntry("Algorithm: Weight Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.WEIGHT_CENTRALITY)),
        new NamedEntry("Algorithm: Graph Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.GRAPH_CENTRALITY)),
        new NamedEntry("Algorithm: Node Edge Betweeness Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.NODE_EDGE_BETWEENESS_CENTRALITY)),
        new NamedEntry("Algorithm: Closeness Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.CLOSENESS_CENTRALITY)),
        new NamedEntry("Algorithm: Eigenvector Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.EIGENVECTOR_CENTRALITY)),
        new NamedEntry("Algorithm: Page Rank",
            new CentralityConfig(CentralityConfig.AlgorithmType.PAGERANK)),
        new NamedEntry("Algorithm: Chains",
            new SubstructuresConfig(SubstructuresConfig.AlgorithmType.CHAIN_SUBSTRUCTERS)),
        new NamedEntry("Algorithm: Cycles",
            new SubstructuresConfig(SubstructuresConfig.AlgorithmType.CYCLE_SUBSTRUCTERS)),
        new NamedEntry("Algorithm: Stars",
            new SubstructuresConfig(SubstructuresConfig.AlgorithmType.STAR_SUBSTRUCTERS)),
        new NamedEntry("Algorithm: Trees",
            new SubstructuresConfig(SubstructuresConfig.AlgorithmType.TREE_SUBSTRUCTERS)),
        new NamedEntry("Algorithm: Cliques",
            new SubstructuresConfig(SubstructuresConfig.AlgorithmType.CLIQUE_SUBSTRUCTERS))
    );

    algorithmComboBox.getSelectionModel().selectFirst();
    algorithmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
      onAlgorithmChanged()
    );
  }

  /**
   * Returns the currently chosen {@link AlgorithmConfiguration}.
   */
  private AlgorithmConfiguration getAlgorithmConfig() {
    NamedEntry entry = algorithmComboBox.getSelectionModel().getSelectedItem();
    AlgorithmConfiguration config = (AlgorithmConfiguration) entry.value;
    config.setDirected(useDirectedEdges());
    config.setUseUniformWeights(useUniformEdgeWeights());
    config.setkCore(kCoreSpinner.getValue());
    return config;
  }

  /**
   * Generates and adds random labels for the edges in the graph.
   * Existing labels will be deleted before new ones are added.
   */
  @FXML private void generateEdgeLabels() {
    IGraph graph = graphControl.getGraph();

    deleteCustomEdgeLabels();

    // remove labels from edges
    for (IEdge edge : graph.getEdges()) {
      // select a weight from 1 to 20
      double weight = useUniformEdgeWeights() ? 1 : Math.floor(Math.random() * 20 + 1);
      ILabel label = graph.addLabel(edge, Double.toString(weight), FreeEdgeLabelModel.INSTANCE.createDefaultParameter());
      label.setTag("weight");
    }

    runLayout(true, false, true);
  }

  /**
   * Deletes all edge labels with weight or centrality tags.
   */
  @FXML private void deleteCustomEdgeLabels() {
    IGraph graph = graphControl.getGraph();
    for (IEdge edge : graph.getEdges()) {
      for (ILabel label : edge.getLabels().toArray(ILabel.class)) {
        Object tag = label.getTag();
        if ("weight".equals(tag) || "centrality".equals(tag)) {
          graph.remove(label);
        }
      }
    }
  }

  /**
   * Resets the styles of the nodes and edges to the default style.
   */
  private void resetStyles() {
    getAlgorithmConfig().resetGraph(graphControl.getGraph());
  }

  /**
   * Returns whether or not to use uniform weights for all edges.
   */
  private boolean useUniformEdgeWeights() {
    return edgeWeightsCheckBox.isSelected();
  }

  /**
   * Handles changing whether to use directed edges or not.
   */
  @FXML private void onDirectionChanged() {
    runLayout(true, false, true);
  }

  /**
   * Returns whether or not to take edge direction into account.
   */
  private boolean useDirectedEdges() {
    return directionCheckBox.isSelected();
  }

  /**
   * Arranges the current graph.
   */
  @FXML private void runLayout() {
    runLayout(false, false, false);
  }

  /**
   * Arranges the current graph.
   * @param incremental if {@code true}, the layout should run in incremental mode.
   * @param clearUndo if {@code true}, the undo engine should be cleared.
   * @param runAlgorithm if {@code true}, the algorithm should be applied.
   */
  private void runLayout(boolean incremental, boolean clearUndo, boolean runAlgorithm) {
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setDeterministicModeEnabled(true);
    organicLayout.setNodeSizeConsiderationEnabled(true);
    ((ComponentLayout) organicLayout.getComponentLayout()).setStyle(
        ComponentArrangementStyles.NONE.or(ComponentArrangementStyles.MODIFIER_NO_OVERLAP));
    organicLayout.setScope(incremental ? Scope.MAINLY_SUBSET : Scope.ALL);
    organicLayout.setLabelingEnabled(false);

    OrganicLayoutData organicLayoutData = new OrganicLayoutData();
    organicLayoutData.setPreferredEdgeLengths(100d);
    organicLayoutData.setMinimumNodeDistances(10d);
    organicLayoutData.setAffectedNodes(incrementalNodesMapper);

    ILayoutAlgorithm layoutAlgorithm = organicLayout;
    LayoutData layoutData = organicLayoutData;
    AlgorithmConfiguration currentConfig = getAlgorithmConfig();
    if (currentConfig instanceof CentralityConfig) {
      CentralityConfig centrality = (CentralityConfig) currentConfig;
      layoutAlgorithm = centrality.configure(layoutAlgorithm);
      layoutData = centrality.configure(layoutData);
    }

    IGraph graph = graphControl.getGraph();

    enableUI(false);
    graphControl.morphLayout(layoutAlgorithm, Duration.ofMillis(500), layoutData, (source, args) -> {
      // apply graph algorithms after layout
      if (runAlgorithm) {
        applyAlgorithm();
      }
    });

    GenericLabeling genericLabeling = new GenericLabeling();
    genericLabeling.setEdgeLabelPlacementEnabled(true);
    genericLabeling.setNodeLabelPlacementEnabled(false);
    genericLabeling.setDeterministicModeEnabled(true);

    Mapper<ILabel, PreferredPlacementDescriptor> mapper = new Mapper<>();
    graph.getLabels().forEach(label -> {
      if (label.getOwner() instanceof IEdge) {
        PreferredPlacementDescriptor preferredPlacementDescriptor = new PreferredPlacementDescriptor();
        if ("centrality".equals(label.getTag())) {
          preferredPlacementDescriptor.setSideOfEdge(LabelPlacements.ON_EDGE);
        } else {
          preferredPlacementDescriptor.setSideOfEdge(LabelPlacements.RIGHT_OF_EDGE.or(LabelPlacements.LEFT_OF_EDGE));
          preferredPlacementDescriptor.setDistanceToEdge(5);
        }
        mapper.setValue(label, preferredPlacementDescriptor);
      }
    });
    graph.getMapperRegistry().addMapper(
        ILabel.class,
        PreferredPlacementDescriptor.class,
        LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY,
        mapper
    );

    graphControl.morphLayout(genericLabeling, Duration.ofMillis(200), (source, args) -> {
      if (clearUndo) {
        graph.getUndoEngine().clear();
      }

      // clean up data provider
      graph.getMapperRegistry().removeMapper(OrganicLayout.AFFECTED_NODES_DPKEY);
      graph.getMapperRegistry().removeMapper(LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY);
      incrementalNodesMapper.clear();

      enableUI(true);
    });
  }

  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    graphControl.setInputMode(createEditorMode());

    incrementalNodesMapper = new Mapper<>();
    incrementalNodesMapper.setDefaultValue(false);

    // initialize the graph and the defaults
    initializeGraph();

    // populate combo boxes
    configureSampleComboBox();
    configureAlgorithmComboBox();
    configureKCoreSpinner();
  }

  private void configureKCoreSpinner() {
    kCoreSpinner.valueProperty().addListener((observable, oldValue, newValue) -> runLayout(true, false, true));
  }

  @Override
  public void onLoaded() {
    sampleComboBox.getSelectionModel().selectFirst();
  }

  @Override
  public void start(final Stage primaryStage) throws IOException {
    primaryStage.setMaximized(true);
    super.start(primaryStage);
  }

  /**
   * Initializes the graph instance and sets default styles.
   */
  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();

    // enable undo support.
    graph.setUndoEngineEnabled(true);

    // use circular node visualizations by default
    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    nodeStyle.setPaint(Color.LIGHTGRAY);
    nodeStyle.setPen(Pen.getBlack());
    graph.getNodeDefaults().setStyle(nodeStyle);

    // change the default edge label model to an imlementation that works well
    // with generic labeling used in method runLayout below
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(FreeEdgeLabelModel.INSTANCE.createDefaultParameter());

    // change the default label text color to something less contrasting
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setTextPaint(Color.GRAY);
    graph.getEdgeDefaults().getLabelDefaults().setStyle(labelStyle);
  }

  /**
   * Creates the default input mode for the graph control, a
   * {@link GraphEditorInputMode} instance configured for snapping and
   * orthogonal edge editing.
   * @return a configured <code>GraphEditorInputMode</code> instance
   */
  private GraphEditorInputMode createEditorMode() {
    incrementalElements = new Mapper<>();
    incrementalElements.setDefaultValue(Boolean.FALSE);

    // configure interaction
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setShowHandleItems(GraphItemTypes.BEND.or(GraphItemTypes.EDGE).or(GraphItemTypes.LABEL).or(GraphItemTypes.PORT));

    // make it easier to select nodes
    // by default, edges have a higher hit test precedence than nodes
    // due to the fairly thick edges used by the demo, the default hit test
    // order would make selecting nodes with incident edges difficult  
    inputMode.setClickHitTestOrder(GraphItemTypes.NODE, GraphItemTypes.ALL);

    // when deleting nodes, mark neighbors for incremental layout
    // when deleting edges, mark source and target node for incremental layout
    inputMode.addDeletingSelectionListener((sender, eventArgs) -> {
      getAlgorithmConfig().setEdgeRemoved(true);
      ISelectionModel<IModelItem> selection = eventArgs.getSelection();
      for (IModelItem item : selection) {
        if (item instanceof INode) {
          INode node = (INode) item;
          graphControl.getGraph().edgesAt(node, AdjacencyTypes.ALL).forEach(edge -> {
            if (!selection.isSelected(edge.opposite(node))) {
              incrementalNodesMapper.setValue((INode) edge.opposite(node), true);
            }
          });
        } else if (item instanceof IEdge) {
          IEdge edge = (IEdge) item;
          if (!selection.isSelected(edge.getSourceNode())) {
            incrementalNodesMapper.setValue(edge.getSourceNode(), true);
            incrementalElements.setValue(edge.getSourceNode(), true);
          }
          if (!selection.isSelected(edge.getTargetNode())) {
            incrementalNodesMapper.setValue(edge.getTargetNode(), true);
            incrementalElements.setValue(edge.getTargetNode(), true);
          }
        }
      }

      getAlgorithmConfig().setIncrementalElements(incrementalElements);
    });

    // after elements are deleted, arrange the graph with the update incremental
    // elements information
    inputMode.addDeletedSelectionListener((sender, eventArgs) -> {
      runLayout(true, false, true);
    });

    // mark source and target nodes of new edges for incremental layout
    inputMode.getCreateEdgeInputMode().addEdgeCreatedListener((sender, args) -> {
      IEdge edge = args.getItem();
      incrementalNodesMapper.setValue(edge.getSourceNode(), true);
      incrementalNodesMapper.setValue(edge.getTargetNode(), true);

      incrementalElements.setValue(edge.getSourceNode(), true);
      incrementalElements.setValue(edge.getTargetNode(), true);

      getAlgorithmConfig().setIncrementalElements(incrementalElements);

      runLayout(true, false, true);
    });

    // mark new nodes for incremental layout
    inputMode.addNodeCreatedListener((sender, args) -> {
      incrementalElements.setValue(args.getItem(), true);

      getAlgorithmConfig().setIncrementalElements(incrementalElements);

      applyAlgorithm();
    });

    // arrange the graph anew, if only some of its nodes are moved
    // as a side effect, the currently chosen analysis algorithm is run as well
    // and might yield different results due to changed edge lenghts
    inputMode.getMoveInputMode().addDragFinishedListener((sender, eventArgs) -> {
      int count = 0;
      for (IModelItem item : ((MoveInputMode) sender).getAffectedItems()) {
        if (item instanceof INode) {
          count++;
        }
      }
      if (count < graphControl.getGraph().getNodes().size()) {
        runLayout(true, false, true);
      }
    });

    // run the currently chosen analysis algorithm whenever the text of labels
    // is changed to account for possibly changed edge weights
    inputMode.addLabelTextChangedListener((sender, eventArgs) -> {
      applyAlgorithm();
    });

    inputMode.addValidateLabelTextListener((sender, args) -> {
      // labels must contain only positive numbers
      args.setCanceling(!Pattern.matches(validationPattern, args.getNewText()));
    });

    // also we add a popup menu
    initializePopupMenu(inputMode);

    // bind "new" command to its default shortcut CTRL+N on Windows and Linux
    // and COMMAND+N on Mac OS
    inputMode.getKeyboardInputMode().addCommandBinding(ICommand.NEW,
        (command, parameter, source) -> {
          graphControl.getGraph().clear();
          ICommand.invalidateRequerySuggested();
          return true;
        },
        (command, parameter, source) ->
            graphControl.getGraph().getNodes().size() != 0 && enabledUI);

    return inputMode;
  }

  /**
   * Initializes the context menu.
   * @param inputMode The input mode.
   */
  private void initializePopupMenu(GraphEditorInputMode inputMode) {
    inputMode.setContextMenuItems(GraphItemTypes.NODE);
    inputMode.addPopulateItemContextMenuListener((source, args) -> {
      if (args.getItem() instanceof INode) {
        INode node = (INode) args.getItem();
        ContextMenu contextMenu = (ContextMenu) args.getMenu();

        AlgorithmConfiguration currentConfig = getAlgorithmConfig();
        if (currentConfig != null) {
          currentConfig.populateContextMenu(contextMenu, node, graphControl);
          if (!contextMenu.getItems().isEmpty()) {
            args.setShowingMenuRequested(true);
            args.setHandled(true);
          }
        }
      }
    });
  }

  /**
   * Handles a selection change in the sample combo box.
   */
  private void onSampleChanged() {
    int index = sampleComboBox.getSelectionModel().getSelectedIndex();
    loadSample((String) sampleComboBox.getSelectionModel().getSelectedItem().value);
    applyAlgorithm(index);
  }

  /**
   * Applies the algorithm to the selected file and runs the layout.
   */
  private void applyAlgorithm(int sampleSelectedIndex) {
    resetStyles();

    AlgorithmConfiguration currentConfig = getAlgorithmConfig();
    if (currentConfig != null &&
        currentConfig.getIncrementalElements() != null) {
      incrementalElements.clear();
      currentConfig.setIncrementalElements(incrementalElements);
      currentConfig.setEdgeRemoved(false);
    }

    // run the layout if the layout combo box is already correct
    int algorithmSelectedIndex = algorithmComboBox.getSelectionModel().getSelectedIndex();
    if (algorithmSelectedIndex != sampleSelectedIndex) {
      // otherwise, change the selection but prevent calculating a new layout
      // at this point
      preventLayout = true;
      algorithmComboBox.getSelectionModel().select(sampleSelectedIndex);
    }

    preventLayout = false;
    runLayout(false, true, true);
  }

  /**
   * Handles a selection change in the algorithm combo box.
   */
  private void onAlgorithmChanged() {
    AlgorithmConfiguration algorithmConfig = getAlgorithmConfig();
    directionCheckBox.setDisable(!algorithmConfig.supportsDirectedEdges());
    edgeWeightsCheckBox.setDisable(!algorithmConfig.supportsEdgeWeights());

    boolean kCoreUIEnabled = (algorithmConfig instanceof ConnectivityConfig)
        && ((ConnectivityConfig) algorithmConfig).algorithmType  == ConnectivityConfig.AlgorithmType.KCORE;

    kCoreSpinner.setDisable(!kCoreUIEnabled);
    kCoreLabel.setDisable(!kCoreUIEnabled);

    resetStyles();

    if (!preventLayout) {
      runLayout(false, false, true);
    }
  }

  /**
   * Runs the currently chosen analysis algorithm for the current graph.
   */
  private void applyAlgorithm() {
    // apply the algorithm
    getAlgorithmConfig().apply(graphControl);
  }

  /**
   * Handles a selection change in the sample combo box.
   */
  private void loadSample(String graphName) {
    graphControl.getGraph().clear();

    try {
      graphControl.importFromGraphML(getClass().getResource("resources/" + graphName + ".graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }



  /**
   * Name-value struct for combo box entries.
   */
  private static class NamedEntry {
    final String displayName;
    final Object value;

    NamedEntry(String displayName, Object value) {
      this.displayName = displayName;
      this.value = value;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }
}
