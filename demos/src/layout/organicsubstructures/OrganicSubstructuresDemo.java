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
package layout.organicsubstructures;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.organic.ChainSubstructureStyle;
import com.yworks.yfiles.layout.organic.CycleSubstructureStyle;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayoutData;
import com.yworks.yfiles.layout.organic.ParallelSubstructureStyle;
import com.yworks.yfiles.layout.organic.StarSubstructureStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Shows how {@link OrganicLayout} handles substructures and node types.
 */
public class OrganicSubstructuresDemo extends DemoApplication {
  @FXML private WebView helpView;
  @FXML private GraphControl graphControl;

  @FXML private ComboBox<String> samples;
  @FXML private ComboBox<CycleSubstructureStyle> cycleStyles;
  @FXML private ComboBox<ChainSubstructureStyle> chainStyles;
  @FXML private ComboBox<StarSubstructureStyle> starStyles;
  @FXML private ComboBox<ParallelSubstructureStyle> parallelStyles;
  @FXML private CheckBox useEdgeGrouping;
  @FXML private CheckBox considerNodeTypes;
  @FXML private CheckBox separateParallel;
  @FXML private CheckBox separateStar;
  @FXML private Button applyLayout;

  private boolean layoutPending;

  public static void main( String[] args ) {
    launch();
  }

  /**
   * Initializes user interaction and loads an initial sample graph.
   */
  public void initialize() {
    WebViewUtils.initHelp(helpView, this);

    configureSampleChooser(samples);
    configureStyleChooser(cycleStyles);
    configureStyleChooser(chainStyles);
    configureStyleChooser(starStyles);
    configureStyleChooser(parallelStyles);

    applyLayout.setOnAction(event -> runLayout(true));

    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setSelectableItems(GraphItemTypes.EDGE.or(GraphItemTypes.NODE));
    inputMode.setAddLabelAllowed(false);
    inputMode.setEditLabelAllowed(false);
    inputMode.addPopulateItemContextMenuListener(this::onPopulateItemPopupMenu);
    graphControl.setInputMode(inputMode);

    loadSample("mixed_large");
  }

  /**
   * Calculates a new graph layout and optionally applies the new layout in an
   * animated fashion. This method creates and configures a new organic layout
   * algorithm for this purpose.
   */
  private void runLayout( boolean animate ) {
    if (layoutPending) {
      return;
    }
    layoutPending = true;

    // configure the organic layout algorithm
    OrganicLayout algorithm = new OrganicLayout();

    //configure some basic settings
    algorithm.setDeterministicModeEnabled(true);
    algorithm.setMinimumNodeDistance(20);
    algorithm.setPreferredEdgeLength(60);

    // configure substructure styles (cycles, chains, parallel structures, star)
    algorithm.setCycleSubstructureStyle(getSelectedStyle(cycleStyles));
    algorithm.setChainSubstructureStyle(getSelectedStyle(chainStyles));
    algorithm.setParallelSubstructureStyle(getSelectedStyle(parallelStyles));
    algorithm.setStarSubstructureStyle(getSelectedStyle(starStyles));

    //configure type separation for parallel and star substructures
    algorithm.setParallelSubstructureTypeSeparationEnabled(separateParallel.isSelected());
    algorithm.setStarSubstructureTypeSeparationEnabled(separateStar.isSelected());

    // configure data-driven features for the organic layout algorithm by using OrganicLayoutData
    OrganicLayoutData layoutData = new OrganicLayoutData();

    if (useEdgeGrouping.isSelected()) {
      // if desired, define edge grouping on the organic layout data
      layoutData.setSourceGroupIds("groupAll");
      layoutData.setTargetGroupIds("groupAll");
    }

    if (considerNodeTypes.isSelected()) {
      // if types should be considered define a delegate on the respective layout data property
      // that queries the type from the node's tag
      layoutData.setNodeTypes(this::getNodeType);
    }

    // runs the layout algorithm and applies the result
    Duration duration = animate ? Duration.ofMillis(500) : Duration.ZERO;
    graphControl.morphLayout(algorithm, duration, layoutData, (source, args) -> layoutPending = false);
  }

  /**
   * Determines the type of the given node.
   * This demo uses integers stored in the node's tag as node types for
   * simplicity's sake. However, {@link OrganicLayout} and
   * {@link OrganicLayoutData} do not impose any
   * restrictions on the type of objects used as node types.
   */
  private Integer getNodeType( INode node ) {
    return NodeTypeSupport.getNodeType(node);
  }

  /**
   * Configures default visualizations for the given graph.
   * @param graph The demo's graph.
   */
  private void configureGraph( IGraph graph ) {
    graph.getNodeDefaults().setSize(new SizeD(40, 40));
    graph.getNodeDefaults().setStyle(NodeTypeSupport.newNodeStyle(0));

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(Color.rgb(102, 43, 0)));
    graph.getEdgeDefaults().setStyle(edgeStyle);
  }

  /**
   * Loads a sample graph for testing the substructure and node types support
   * of the organic layout algorithm.
   */
  private void loadSample( String sample ) {
    try {
      // load sample data
      IGraph newGraph = new DefaultGraph();
      updateGraph(newGraph, "resources/" + sample + ".graphml");

      // update the settings UI to match the sample's default layout settings
      Properties settings =
        loadSampleSettings("resources/" + sample + ".properties");
      updateLayoutSettings(settings);

      // update input mode setting depending on whether we are allowed to
      // change the graph structure
      boolean alterTypesAndStructure =
        booleanValue(settings, "alterTypesAndStructure");
      GraphEditorInputMode inputMode =
        (GraphEditorInputMode) graphControl.getInputMode();
      inputMode.setCreateEdgeAllowed(alterTypesAndStructure);
      inputMode.setCreateNodeAllowed(alterTypesAndStructure);
      inputMode.setDuplicateAllowed(alterTypesAndStructure);
      inputMode.setDeletableItems(
        alterTypesAndStructure ? GraphItemTypes.ALL : GraphItemTypes.NONE);

      // center new sample graph in current view
      graphControl.setGraph(newGraph);

      // configures default styles for newly created graph elements
      configureGraph(newGraph);

      runLayout(false);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Loads sample setting from the file identified by the given sample path.
   * @param settingsPath the path to the sample settings file.
   */
  private Properties loadSampleSettings( String settingsPath ) {
    try {
      Properties settings = new Properties();
      try (InputStream is = getClass().getResource(settingsPath).openStream()) {
        settings.load(is);
      }
      return settings;
    } catch (Exception ex) {
      return new Properties();
    }
  }

  /**
   * Rebuilds the demo's graph from the given sample data.
   * @param graph The demo's graph.
   * @param samplePath The path to the sample data representing the desired
   * graph structure.
   */
  private void updateGraph( IGraph graph, String samplePath ) throws IOException {
    new GraphMLIOHandler().read(graph, getClass().getResource(samplePath));
  }

  /**
   * Updates the demo's settings controls for the given sample settings.
   * @param settings The sample settings to be used.
   */
  private void updateLayoutSettings( Properties settings ) {
    boolean oldValue = layoutPending;
    layoutPending = true;
    try {
      updateLayoutSettingsCore(settings);
    } finally {
      layoutPending = oldValue;
    }
  }

  /**
   * Rebuilds the demo's graph from the given sample data.
   * @param graph The demo's graph.
   * @param samplePath The path to the sample data representing the desired graph structure.
   */
  private void updateLayoutSettingsCore( Properties settings ) {
    if (settings.isEmpty()) {
      setSelectedIndex(cycleStyles, 0);
      setSelectedIndex(chainStyles, 0);
      setSelectedIndex(starStyles, 0);
      setSelectedIndex(parallelStyles, 0);
      useEdgeGrouping.setSelected(false);
      considerNodeTypes.setSelected(true);
      separateParallel.setSelected(false);
      separateStar.setSelected(false);
    } else {
      cycleStyles.setValue(CycleSubstructureStyle.valueOf(settings.getProperty("cycleSubstructureStyle")));
      chainStyles.setValue(ChainSubstructureStyle.valueOf(settings.getProperty("chainSubstructureStyle")));
      starStyles.setValue(StarSubstructureStyle.valueOf(settings.getProperty("starSubstructureStyle")));
      parallelStyles.setValue(ParallelSubstructureStyle.valueOf(settings.getProperty("parallelSubstructureStyle")));

      useEdgeGrouping.setSelected(booleanValue(settings, "useEdgeGrouping"));
      considerNodeTypes.setSelected(booleanValue(settings, "considerNodeTypes"));
      separateParallel.setSelected(booleanValue(settings, "parallelSubstructureTypeSeparation"));
      separateStar.setSelected(booleanValue(settings, "starSubstructureTypeSeparation"));
    }
  }

  /**
   * Returns a boolean value corresponding to the value of the given setting.
   */
  private static boolean booleanValue( Properties settings, String setting ) {
    return Boolean.parseBoolean(settings.getProperty(setting));
  }

  /**
   * Adds controls for changing a node's type to the context menu for nodes.
   */
  private void onPopulateItemPopupMenu(
    Object source, PopulateItemContextMenuEventArgs<IModelItem> args
  ) {
    IModelItem item = args.getItem();
    if (item instanceof INode) {
      GraphControl graphControl = ((GraphEditorInputMode) source).getGraphControl();

      // ensure the clicked node is selected
      IGraphSelection selection = graphControl.getSelection();
      if (!selection.isSelected(item)) {
        selection.setSelected(item, true);
      }

      ContextMenu menu = (ContextMenu) args.getMenu();
      int typeCount = 8;
      for (int i = 0; i < typeCount; ++i) {
        Integer newType = Integer.valueOf(i);

        MenuItem menuItem = new MenuItem();
        menuItem.setGraphic(new Rectangle(50, 30, NodeTypeSupport.getFillColor(i)));
        menuItem.setOnAction(e -> {
          for (INode node : selection.getSelectedNodes()) {
            Integer oldType = NodeTypeSupport.getNodeType(node);
            if (!oldType.equals(newType)) {
              NodeTypeSupport.setNodeType(node, newType);
              graphControl.getGraph().setStyle(node, NodeTypeSupport.newNodeStyle(newType));
            }
          }

          Platform.runLater(() -> runLayout(true));
        });
        menu.getItems().add(menuItem);
      }
    }
  }

  /**
   * Centers the initial sample graph in the visible area.
   */
  @Override
  public void onLoaded() {
    graphControl.fitGraphBounds();
  }



  /*
   * #####################################################################
   *
   * JavaFX UI boilerplate methods
   * The following methods are not related to yFiles functionality, but
   * are required to build the demo's JavaFX UI.
   *
   * #####################################################################
   */

  /**
   * Returns the selected item of the given combo box.
   */
  private static <T> T getSelectedStyle( ComboBox<T> cb ) {
    return (T) cb.getValue();
  }

  /**
   * Sets the selected index of the given combo box.
   */
  private static <T> void setSelectedIndex( ComboBox<T> cb, int index ) {
    cb.getSelectionModel().select(index);
  }

  /**
   * Configures the given combo box for choosing sample graphs.
   */
  private void configureSampleChooser( ComboBox<String> cb ) {
    HashMap<String, String> nameToId = new HashMap<>();
    nameToId.put("Simple Mixed, Large", "mixed_large");
    nameToId.put("Simple Mixed, Small", "mixed_small");
    nameToId.put("Simple Parallel", "parallel");
    nameToId.put("Simple Star", "star");
    nameToId.put("Computer Network", "computer_network");
    HashMap<String, String> idToName = new HashMap<>();
    for (Map.Entry<String, String> entry : nameToId.entrySet()) {
      idToName.put(entry.getValue(), entry.getKey());
    }

    cb.setConverter(new StringConverter<String>() {
      @Override
      public String toString( String id ) {
        return idToName.get(id);
      }

      @Override
      public String fromString( String name ) {
        return nameToId.get(name);
      }
    });
    cb.getSelectionModel().selectedItemProperty().addListener(
      ( observable, oldValue, newValue ) -> loadSample(newValue));
  }

  /**
   * Configures the given combo box for choosing substructure styles.
   */
  private static <T> void configureStyleChooser( ComboBox<T> cb ) {
    cb.setConverter(new StyleConverter<T>());
  }

  /**
   * Converts substructure styles to human-readable names.
   */
  private static class StyleConverter<T> extends StringConverter<T> {
    final Map<T, String> names;
    final Map<String, T> inverse;

    StyleConverter() {
      names = new HashMap<>();
      inverse = new HashMap<>();
    }

    @Override
    public String toString( T style ) {
      return getHumanReadableName(style);
    }

    @Override
    public T fromString( String name ) {
      return inverse.get(name);
    }

    /**
     * Determines the human-readabble name of the given style value.
     */
    private String getHumanReadableName( T value ) {
      String name = names.get(value);
      if (name == null) {
        name = toHumanReadableName(value.toString());
        names.put(value, name);
        inverse.put(name, value);
      }
      return name;
    }

    /**
     * Converts the given style constant name into a human-readable style name.
     */
    private static String toHumanReadableName( String s ) {
      String del = "";
      StringBuilder sb = new StringBuilder();
      for (StringTokenizer st = new StringTokenizer(s, "_"); st.hasMoreTokens();) {
        String token = st.nextToken();
        sb.append(del)
          .append(Character.toUpperCase(token.charAt(0)))
          .append(token.substring(1).toLowerCase(Locale.ENGLISH));
        del = " ";
      }
      return sb.toString();
    }
  }
}
