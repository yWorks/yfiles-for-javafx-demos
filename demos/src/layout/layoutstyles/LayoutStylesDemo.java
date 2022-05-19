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
package layout.layoutstyles;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.WaitInputMode;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import layout.layoutstyles.configurations.BalloonLayoutConfig;
import layout.layoutstyles.configurations.BusEdgeRouterConfig;
import layout.layoutstyles.configurations.ChannelEdgeRouterConfig;
import layout.layoutstyles.configurations.CircularLayoutConfig;
import layout.layoutstyles.configurations.ClassicTreeLayoutConfig;
import layout.layoutstyles.configurations.ComponentLayoutConfig;
import layout.layoutstyles.configurations.GraphTransformerConfig;
import layout.layoutstyles.configurations.HierarchicLayoutConfig;
import layout.layoutstyles.configurations.LabelingConfig;
import layout.layoutstyles.configurations.LayoutConfiguration;
import layout.layoutstyles.configurations.OrganicEdgeRouterConfig;
import layout.layoutstyles.configurations.OrganicLayoutConfig;
import layout.layoutstyles.configurations.OrthogonalLayoutConfig;
import layout.layoutstyles.configurations.ParallelEdgeRouterConfig;
import layout.layoutstyles.configurations.PartialLayoutConfig;
import layout.layoutstyles.configurations.PolylineEdgeRouterConfig;
import layout.layoutstyles.configurations.RadialLayoutConfig;
import layout.layoutstyles.configurations.SeriesParallelLayoutConfig;
import layout.layoutstyles.configurations.TabularLayoutConfig;
import layout.layoutstyles.configurations.TreeLayoutConfig;
import toolkit.DemoApplication;
import toolkit.DemoGroupNodeStyle;
import toolkit.DemoNodeStyle;
import toolkit.WebViewUtils;
import toolkit.optionhandler.OptionEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Provides controls for playing around with the major layout algorithms of
 * yFiles including hierarchic, organic, orthogonal, tree, circular and balloon.
 */
public class LayoutStylesDemo extends DemoApplication {
  private static final String HIERARCHIC_LAYOUT_STYLE = "Hierarchic";

  public GraphControl graphControl;
  public ComboBox<String> layoutComboBox;
  public ComboBox<String> graphChooserBox;
  public VBox editorPane;
  public WebView webView;

  /**
   * Stores all available layout algorithms and maps each name to the corresponding configuration.
   */
  private Map<String, LayoutConfiguration> availableLayouts;

  private OptionEditor builder;

  private boolean inLayout;
  private boolean inLoadSample;

  private WaitInputMode waitInputMode;

  // region Initialization methods

  /**
   * Initializes the controller.
   * This method is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available.
   * Most importantly, the {@link #graphControl} instance is initialized.
   */
  public void initialize() {
    WebViewUtils.initHelp(webView, this);
  }

  /**
   * Initializes the graph and the input mode.
   */
  public void onLoaded() {
    // initialize the option editor builder that is responsible for creating
    // the editor controls for interactive configuration of layout algorithms
    builder = new OptionEditor();
    builder.setConfiguration(new HierarchicLayoutConfig());

    ObservableList<Node> editorControls = editorPane.getChildren();
    editorControls.clear();
    editorControls.add(builder.buildEditor());

    // initialize the graph
    initializeGraph();

    // initialize the input mode
    initializeInputModes();

    // initializes the available layout algorithm configurations.
    initializeLayoutAlgorithms();

    // load hierarchic sample graph and apply the hierarchic layout
    if (!HIERARCHIC_LAYOUT_STYLE.equals(graphChooserBox.getValue())) {
      graphChooserBox.setValue(HIERARCHIC_LAYOUT_STYLE);
    } else {
      onSampleGraphChanged();
    }
  }

  /**
   * Creates a {@link GraphEditorInputMode} and registers it as
   * the {@link com.yworks.yfiles.view.CanvasControl#setInputMode(IInputMode)}.
   */
  private void initializeInputModes() {
    // allow file operations such as open/save
    graphControl.setFileIOEnabled(true);

    GraphEditorInputMode mode = new GraphEditorInputMode();
    waitInputMode = mode.getWaitInputMode();

    // initialize snapping
    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setEnabled(false);
    mode.setSnapContext(snapContext);
    mode.setLabelSnapContext(new LabelSnapContext());

    // initialize orthogonal edge editing
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    orthogonalEdgeEditingContext.setEnabled(false);
    mode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    // enable grouping operations such as grouping selected or nodes moving nodes into group nodes
    mode.setGroupingOperationsAllowed(true);
    
    KeyboardInputMode kim = mode.getKeyboardInputMode();
    kim.addCommandBinding(ICommand.NEW, this::executeNewCommand, this::canExecuteNewCommand);
    kim.addCommandBinding(APPLY_SETTINGS, this::executeApplySettingsCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(RESET_SETTINGS, this::executeResetSettingsCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(PREVIOUS_GRAPH, this::executeShowGraphCommand, this::canExecuteShowGraphCommand);
    kim.addCommandBinding(NEXT_GRAPH, this::executeShowGraphCommand, this::canExecuteShowGraphCommand);
    kim.addCommandBinding(GENERATE_NODE_LABELS, this::executeGenerateLabelsCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(GENERATE_EDGE_LABELS, this::executeGenerateLabelsCommand, this::canExecuteChangeCommand);

    graphControl.setInputMode(mode);
  }

  /**
   * Initializes the available layout algorithm configurations.
   */
  private void initializeLayoutAlgorithms() {
    availableLayouts = new HashMap<>();

    HierarchicLayoutConfig hierarchicLayoutConfig = new HierarchicLayoutConfig();
    hierarchicLayoutConfig.enableSubComponents();
    availableLayouts.put(HIERARCHIC_LAYOUT_STYLE, hierarchicLayoutConfig);
    OrganicLayoutConfig organicLayoutConfig = new OrganicLayoutConfig();
    organicLayoutConfig.enableSubstructures();
    availableLayouts.put("Organic", organicLayoutConfig);
    OrthogonalLayoutConfig orthogonalLayoutConfig = new OrthogonalLayoutConfig();
    orthogonalLayoutConfig.enableSubstructures();
    availableLayouts.put("Orthogonal", orthogonalLayoutConfig);
    availableLayouts.put("Circular", new CircularLayoutConfig());
    availableLayouts.put("Tree", new TreeLayoutConfig());
    availableLayouts.put("Classic Tree", new ClassicTreeLayoutConfig());
    availableLayouts.put("Balloon", new BalloonLayoutConfig());
    availableLayouts.put("Radial", new RadialLayoutConfig());
    availableLayouts.put("Series-Parallel", new SeriesParallelLayoutConfig());
    availableLayouts.put("Tabular", new TabularLayoutConfig());
    availableLayouts.put("Edge Router", new PolylineEdgeRouterConfig());
    availableLayouts.put("Channel Router", new ChannelEdgeRouterConfig());
    availableLayouts.put("Bus Router", new BusEdgeRouterConfig());
    availableLayouts.put("Organic Router", new OrganicEdgeRouterConfig());
    availableLayouts.put("Parallel Router", new ParallelEdgeRouterConfig());
    availableLayouts.put("Labeling", new LabelingConfig());
    availableLayouts.put("Components", new ComponentLayoutConfig());
    availableLayouts.put("Partial", new PartialLayoutConfig());
    availableLayouts.put("Graph Transform", new GraphTransformerConfig());
  }

  /**
   * Initializes the graph instance and sets default styles.
   */
  private void initializeGraph() {
    // enable undo support
    graphControl.getGraph().setUndoEngineEnabled(true);

    // set the default style for normal nodes
    graphControl.getGraph().getNodeDefaults().setStyle(new DemoNodeStyle());

    // set the default style for group nodes
    INodeDefaults groupNodeDefaults = graphControl.getGraph().getGroupNodeDefaults();
    groupNodeDefaults.setStyle(new DemoGroupNodeStyle());

    // use a custom style and layout parameter for group node labels
    InteriorLabelModel interiorLabelModel = new InteriorLabelModel();
    interiorLabelModel.setInsets(new InsetsD(2));
    groupNodeDefaults.getLabelDefaults().setLayoutParameter(
            interiorLabelModel.createParameter(InteriorLabelModel.Position.NORTH_WEST));

    DefaultLabelStyle groupNodeLabelStyle = new DefaultLabelStyle();
    groupNodeLabelStyle.setFont(Font.font("System", FontWeight.BOLD, 12));
    groupNodeLabelStyle.setTextPaint(Color.WHITE);
    groupNodeDefaults.getLabelDefaults().setStyle(groupNodeLabelStyle);

    // set the default style for edges
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    graphControl.getGraph().getEdgeDefaults().setStyle(edgeStyle);
  }

  // endregion

  // region Callback methods for the layout calculation

  /**
   * Arranges the displayed graph using the layout algorithm corresponding to
   * the given key.
   */
  private void applyLayoutForKey(String sampleKey) {
    // center the initial position of the animation
    ICommand.FIT_GRAPH_BOUNDS.execute(null, graphControl);

    // get the actual key, as there are samples sharing the layout config (e.g. Organic with Substructures and Organic)
    String actualKey = getLayoutKey(sampleKey);

    // get the layout algorithm and use "Hierarchic" if the key is unknown (shouldn't happen in this demo)
    actualKey = availableLayouts != null && availableLayouts.containsKey(actualKey) ? actualKey : HIERARCHIC_LAYOUT_STYLE;

    if (actualKey.equals(layoutComboBox.getValue())) {
      // run the layout if the layout combo box is already correct
      onLayoutChanged();
    } else {
      // otherwise, change the selection and indirectly trigger the layout
      layoutComboBox.setValue(actualKey);
    }
    applyLayout(true);
  }
  
  private String getLayoutKey(String sampleKey) {
    //for some special samples, we need to use the correct layout key, because the layout configurations are shared
    if (sampleKey.startsWith("Organic")) {
      return "Organic";
    } else if (sampleKey.startsWith("Hierarchic")) {
      return HIERARCHIC_LAYOUT_STYLE;
    } else if (sampleKey.startsWith("Orthogonal")) {
      return "Orthogonal";
    } else if (sampleKey.startsWith("Edge Router")) {
      return "Edge Router";
    }
    //... for other samples the layout key corresponds to the sample graph key
    return sampleKey;
  }
  

  /**
   * Arranges the displayed graph using the currently selected layout algorithm.
   *
   * @param clearUndo Specifies whether or not the undo queue should be cleared
   * after the layout calculation.
   */
  private void applyLayout(boolean clearUndo) {
    LayoutConfiguration config = (LayoutConfiguration) builder.getConfiguration();

    if (config == null || inLayout) {
      return;
    }

    // prevent starting another layout calculation
    inLayout = true;
    setUIEnabled(false);

    // calculate the layout and animate the result in one second
    config.apply(graphControl, () ->
    {
      releaseLocks();
      setUIEnabled(true);
      if (clearUndo) {
        graphControl.getGraph().getUndoEngine().clear();
      }
      // the commands CanExecute state might have changed - suggest a re-query. mainly to update the enabled status of the previous / next buttons.
      ICommand.invalidateRequerySuggested();
    });
  }

  /**
   * Creates new editor controls for interactive configuration of layout
   * algorithms whenever the user changes the desired layout algorithm.
   */
  public void onLayoutChanged() {
    String key = layoutComboBox.getValue();
    if (key != null && availableLayouts.containsKey(key)) {
      LayoutConfiguration newLayoutConfig = availableLayouts.get(key);
      if (builder != null && editorPane != null) {
        builder.setConfiguration(newLayoutConfig);
        final ObservableList<Node> editorControls = editorPane.getChildren();
        editorControls.clear();
        Parent editor = builder.buildEditor();
        editorControls.add(editor);
        // open 'General' or 'Layout' pane of the selected layout
        Node pane = editor.lookup("#General");
        if (!(pane instanceof Accordion)) {
          pane = editor.lookup("#Layout");
        }
        if (pane instanceof Accordion) {
          Accordion accordion = (Accordion) pane;
          accordion.setExpandedPane(accordion.getPanes().get(0));
        }
      }
    }
  }

  // endregion

  // region Callback methods for the snapping and orthogonal edge editing buttons

  /**
   * Enables or disables snapping support for placing nodes and labels.
   */
  public void toggleSnapping(ActionEvent event) {
    if (event.getSource() instanceof ToggleButton) {
      ToggleButton snappingButton = (ToggleButton) event.getSource();
      GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
      inputMode.getSnapContext().setEnabled(snappingButton.isSelected());
      inputMode.getLabelSnapContext().setEnabled(snappingButton.isSelected());
    }
  }

  /**
   * Enables or disables orthogonal edge editing support.
   * With orthogonal editing enabled, new edges are created with orthogonal
   * segments and existing orthogonal segments will remain orthogonal when
   * modified interactively.
   */
  public void toggleOrthogonalEditing(ActionEvent event) {
    if (event.getSource() instanceof ToggleButton) {
      ToggleButton orthogonalEditingButton = (ToggleButton) event.getSource();
      boolean selected = orthogonalEditingButton.isSelected();
      GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
      inputMode.getOrthogonalEdgeEditingContext().setEnabled(selected);
    }
  }

  // endregion

  private void setUIEnabled(boolean enabled) {
    layoutComboBox.setDisable(!enabled);
    graphChooserBox.setDisable(!enabled);
    graphControl.setFileIOEnabled(enabled);
  }

  // region ICommand configurations

  /**
   * Helper that determines whether the {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNewCommand(ICommand command, Object parameter, Object sender) {
    // don't allow a new graph if we already have an empty graph
    IGraph graph = graphControl.getGraph();
    return !inLoadSample && !inLayout && !waitInputMode.isWaiting() && graph != null && graph.getNodes().size() != 0;
  }

  /**
   * Handler for the {@link ICommand#NEW}
   */
  private boolean executeNewCommand(ICommand command, Object parameter, Object sender) {
    graphControl.getGraph().clear();

    // update the can-execute-states of the commands since this is not
    // triggered by clearing the graph programmatically
    ICommand.invalidateRequerySuggested();

    return true;
  }

  /**
   * A {@link ICommand} that is usable from FXML and changes the current graph
   * to the previous sample graph.
   */
  public static final ICommand PREVIOUS_GRAPH = ICommand.createCommand("PreviousGraph");
  /**
   * A {@link ICommand} that is usable from FXML and changes the current graph
   * to the next sample graph.
   */
  public static final ICommand NEXT_GRAPH = ICommand.createCommand("NextGraph");

  /**
   * Changes the current graph to the previous or next graph.
   */
  private boolean executeShowGraphCommand(ICommand command, Object parameter, Object sender) {
    if (parameter instanceof String) {
      switch ((String) parameter) {
        case "Previous":
          graphChooserBox.getSelectionModel().selectPrevious();
          return true;
        case "Next":
          graphChooserBox.getSelectionModel().selectNext();
          return true;
      }
    }
    return false;
  }

  /**
   * Determines if the command for changing the current graph may be executed.
   */
  private boolean canExecuteShowGraphCommand(ICommand command, Object parameter, Object sender) {
    if (parameter instanceof String) {
      ComboBox<String> cb = this.graphChooserBox;
      SingleSelectionModel<String> cbSm = cb.getSelectionModel();
      switch ((String) parameter) {
        case "Previous":
          return !cb.isDisabled() && !cbSm.isSelected(0);
        case "Next":
          return !cb.isDisabled() && !cbSm.isSelected(cb.getItems().size() - 1);
      }
    }
    return false;
  }

  /**
   * A {@link ICommand} that is usable from FXML and creates labels for some edges.
   */
  public static final ICommand GENERATE_EDGE_LABELS = ICommand.createCommand("GenerateEdgeLabels");
  /**
   * A {@link ICommand} that is usable from FXML and creates labels for some nodes.
   */
  public static final ICommand GENERATE_NODE_LABELS = ICommand.createCommand("GenerateNodeLabels");

  /**
   * Generates node or edge labels when the appropriate commands are triggered.
   */
  private boolean executeGenerateLabelsCommand(ICommand command, Object parameter, Object sender) {
    if (parameter instanceof String) {
      switch ((String) parameter) {
        case "Edge":
          onGenerateItemLabels(graphControl.getGraph().getEdges());
          return true;
        case "Node":
          onGenerateItemLabels(graphControl.getGraph().getNodes());
          return true;
      }
    }
    return false;
  }

  /**
   * A {@link ICommand} that is usable from FXML and arranges the given graph
   * with the current layout settings.
   */
  public static final ICommand APPLY_SETTINGS = ICommand.createCommand("Apply");

  private boolean executeApplySettingsCommand( ICommand command, Object parameter, Object sender) {
    applyLayout(false);
    return true;
  }

  /**
   * A {@link ICommand} that is usable from FXML and reverts the current layout
   * settings to their original values.
   */
  public static final ICommand RESET_SETTINGS = ICommand.createCommand("Reset");

  private boolean executeResetSettingsCommand( ICommand command, Object parameter, Object sender) {
    builder.resetEditor((Parent) editorPane.getChildren().get(0));
    return true;
  }

  /**
   * Determines if commands that change the displayed graph may be executed.
   * These commands include generating node or edge labels as well as
   * starting a layout calculation.
   */
  private boolean canExecuteChangeCommand(ICommand command, Object parameter, Object sender) {
    return !inLoadSample && !inLayout && (waitInputMode == null || !waitInputMode.isWaiting());
  }

  // endregion

  /**
   * Reads the GraphML file corresponding to the current selection in the
   * {@link #graphChooserBox}.
   */
  public void onSampleGraphChanged() {
    if (inLayout || inLoadSample) {
      return;
    }
    String key = graphChooserBox.getValue();
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
    fileName = fileName.replace(" ", "") + ".graphml";

    try {
      if ("Hierarchic with Buses".equals(key)) {
        //for this specific hierarchic layout sample we make sure to enable the bus structure feature
        final HierarchicLayoutConfig hlc = (HierarchicLayoutConfig) availableLayouts.get("Hierarchic");
        hlc.enableAutomaticBusRouting();
      } else if ("Edge Router with Buses".equals(key)) {
        final PolylineEdgeRouterConfig edgeRouterConfig = (PolylineEdgeRouterConfig) availableLayouts.get("Edge Router");
        edgeRouterConfig.setBusRoutingItem(PolylineEdgeRouterConfig.EnumBusRouting.BY_COLOR);
      } else if ("Edge Router".equals(key)) {
        final PolylineEdgeRouterConfig edgeRouterConfig = (PolylineEdgeRouterConfig) availableLayouts.get("Edge Router");
        edgeRouterConfig.setBusRoutingItem(PolylineEdgeRouterConfig.EnumBusRouting.NONE);
      }
      
      // load the sample graph and start the layout algorithm
      graphControl.importFromGraphML(getClass().getResource(fileName));
      applyLayoutForKey(key);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void releaseLocks() {
    inLoadSample = false;
    inLayout = false;
  }

  // region Label generation

  /**
   * Generates and adds labels for a random subset of the given graph elements.
   * <p>
   * Existing labels will be deleted before adding the new labels.
   * </p>
   * @param items The collection of items the labels are generated for.
   */
  private <T extends ILabelOwner> void onGenerateItemLabels(IListEnumerable<T> items)  {
    int wordCountMin = 1;
    int wordCountMax = 3;
    double labelPercMin = 0.2;
    double labelPercMax = 0.7;
    Random rnd = new Random();
    int labelCount = (int) Math.floor(items.size() * (rnd.nextDouble() * (labelPercMax - labelPercMin) + labelPercMin));
    ArrayList<ILabelOwner> labelOwners = new ArrayList<>(items.size());
    for (T item : items) {
      labelOwners.add(item);
    }

    IGraph graph = graphControl.getGraph();

    //remove all existing item labels
    items.stream()
         // gather all labels of the given items
         .flatMap(item -> item.getLabels().stream())
         // copy them into a list to avoid concurrent modification
         .collect(Collectors.toList())
         // remove them from the graph
         .forEach(graph::remove);

    //add random item labels
    String[] loremList = getLoremIpsum();
    for (int i = 0; i < labelCount; i++) {
      String label = "";
      int wordCount = rnd.nextInt(wordCountMax - wordCountMin + 1) + wordCountMin;
      for(int j = 0; j < wordCount; j++) {
        int k = rnd.nextInt(loremList.length);
        label += (j == 0) ? "" : " ";
        label = label + loremList[k];
      }
      int itemIdx = rnd.nextInt(labelOwners.size());
      ILabelOwner item = labelOwners.remove(itemIdx);
      graph.addLabel(item, label);
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  private static String[] getLoremIpsum() {
    return new String[] {
            "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "donec", "felis", "erat"
            , "malesuada", "quis", "ipsum", "et", "condimentum",
            "ultrices", "orci", "nullam", "interdum", "vestibulum", "eros", "sed", "porta", "donec", "ac",
            "eleifend", "dolor", "at", "dictum", "ipsum", "pellentesque", "vel", "suscipit", "mi", "nullam",
            "aliquam", "turpis", "et", "dolor", "porttitor", "varius", "nullam", "vel", "arcu", "rutrum", "iaculis"
            , "est", "sit", "amet", "rhoncus", "turpis", "vestibulum", "lacinia", "sollicitudin",
            "urna", "nec", "vestibulum", "nulla", "id", "lacinia", "metus", "etiam", "ac", "felis", "rutrum",
            "sollicitudin", "erat", "vitae", "egestas", "tortor", "curabitur", "quis", "libero", "aliquet",
            "mattis", "mauris", "nec", "tempus", "nibh", "in", "at", "lectus", "luctus", "mattis", "urna",
            "pretium", "eleifend", "lacus", "sed", "interdum", "sapien", "nec", "justo", "vestibulum", "non",
            "scelerisque",
            "nibh", "sollicitudin", "interdum", "et", "malesuada", "fames", "ac", "ante", "ipsum", "primis", "in",
            "faucibus", "vivamus", "congue", "tristique", "magna", "quis", "elementum", "phasellus", "sit", "amet",
            "tristique", "massa", "vestibulum", "eu", "leo", "vitae", "quam", "dictum", "venenatis", "eu", "id",
            "nibh", "donec", "eget", "eleifend", "felis", "nulla", "ac", "suscipit", "ante", "et", "sollicitudin",
            "dui", "mauris",
            "in", "pulvinar", "tortor", "vestibulum", "pulvinar", "arcu", "vel", "tellus", "maximus", "blandit",
            "morbi", "sed", "sem", "vehicula", "fermentum", "nisi", "eu", "fringilla", "metus", "duis", "ut", "quam",
            "eget",
            "odio", "hendrerit", "finibus", "ut", "a", "lectus", "cras", "ullamcorper", "turpis", "in", "purus",
            "facilisis", "vestibulum", "donec", "maximus", "ac", "tortor", "tempus", "egestas", "aenean", "est", "diam",
            "dictum", "et", "sodales", "vel", "efficitur", "ac", "libero", "vivamus", "vehicula", "ligula", "eu",
            "diam", "auctor", "at", "dapibus", "nulla", "pellentesque", "morbi", "et", "dapibus", "dolor", "quis",
            "auctor",
            "turpis", "nunc", "sed", "pretium", "diam", "quisque", "non", "massa", "consectetur", "tempor", "augue"
            , "vel", "volutpat", "ex", "vivamus", "vestibulum", "dolor", "risus", "quis", "mollis", "urna", "fermentum",
            "sed",
            "sed", "porttitor", "venenatis", "volutpat", "nulla", "facilisi", "donec", "aliquam", "mi", "vitae",
            "ligula", "dictum", "ornare", "suspendisse", "finibus", "ligula", "vitae", "congue", "iaculis", "donec",
            "vestibulum", "erat", "vel", "tortor", "iaculis", "tempor", "vivamus", "et", "purus", "eu", "ipsum",
            "rhoncus", "pretium", "sit", "amet", "nec", "nisl", "nunc", "molestie", "consectetur", "rhoncus", "duis",
            "ex",
            "nunc", "interdum", "at", "molestie", "quis", "blandit", "quis", "diam", "nunc", "imperdiet", "lorem",
            "vel", "scelerisque", "facilisis", "eros", "massa", "auctor", "nisl", "vitae", "efficitur", "leo", "diam",
            "vel",
            "felis", "aliquam", "tincidunt", "dapibus", "arcu", "in", "pulvinar", "metus", "tincidunt", "et",
            "etiam", "turpis", "ligula", "sodales", "a", "eros", "vel", "fermentum", "imperdiet", "purus", "fusce",
            "mollis",
            "enim", "sed", "volutpat", "blandit", "arcu", "orci", "iaculis", "est", "non", "iaculis", "lorem",
            "sapien", "sit", "amet", "est", "morbi", "ut", "porttitor", "elit", "aenean", "ac", "sodales", "lectus",
            "morbi", "ut",
            "bibendum", "arcu", "maecenas", "tincidunt", "erat", "vel", "maximus", "pellentesque", "ut", "placerat",
            "quam", "sem", "a", "auctor", "ligula", "imperdiet", "quis", "pellentesque", "gravida", "consectetur",
            "urna", "suspendisse", "vitae", "nisl", "et", "ante", "ornare", "vulputate", "sed", "a", "est", "lorem",
            "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "sed", "eu", "facilisis", "lectus",
            "nullam",
            "iaculis", "dignissim", "eros", "eget", "tincidunt", "metus", "viverra", "at", "donec", "nec", "justo",
            "vitae", "risus", "eleifend", "imperdiet", "eget", "ut", "ante", "ut", "arcu", "ex", "convallis", "in",
            "lobortis",
            "at", "mattis", "sed", "velit", "ut", "viverra", "ultricies", "lacus", "suscipit", "feugiat", "eros",
            "luctus", "et", "vestibulum", "et", "aliquet", "mauris", "quisque", "convallis", "purus", "posuere",
            "aliquam",
            "nulla", "sit", "amet", "posuere", "orci", "nullam", "sed", "iaculis", "mauris", "ut", "volutpat",
            "est", "suspendisse", "in", "vestibulum", "felis", "nullam", "gravida", "nulla", "at", "varius",
            "fringilla", "ipsum",
            "ipsum", "finibus", "lectus", "nec", "vestibulum", "lorem", "arcu", "ut", "magna", "aliquam", "aliquam"
            , "erat", "erat", "ac", "euismod", "orci", "iaculis", "blandit", "morbi", "tincidunt", "posuere", "mi",
            "non",
            "eleifend", "vivamus", "accumsan", "dolor", "magna", "in", "cursus", "eros", "malesuada", "eu", "sed",
            "auctor", "consectetur", "tempus", "maecenas", "luctus", "turpis", "a"
    };
  }

  // endregion
}
