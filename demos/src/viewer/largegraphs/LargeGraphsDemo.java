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
package viewer.largegraphs;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IEdgeDefaults;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.VoidLabelStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.graphml.SerializationProperties;
import com.yworks.yfiles.utils.IEnumerator;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GraphOverviewControl;
import com.yworks.yfiles.view.GraphSelection;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.LabelStyleDecorationInstaller;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.PathRenderPolicy;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.StyleDecorationZoomPolicy;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.WaitInputMode;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;
import viewer.largegraphs.animations.CircleNodeAnimation;
import viewer.largegraphs.animations.CirclePanAnimation;
import viewer.largegraphs.animations.ZoomInAndBackAnimation;
import viewer.largegraphs.styles.WrapperEdgeStyle;
import viewer.largegraphs.styles.WrapperLabelStyle;
import viewer.largegraphs.styles.WrapperNodeStyle;
import viewer.largegraphs.styles.fast.FastEdgeStyle;
import viewer.largegraphs.styles.fast.FastLabelStyle;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailLabelStyle;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailNodeStyle;
import viewer.largegraphs.styles.selection.FastEdgeSelectionStyle;
import viewer.largegraphs.styles.selection.FastLabelSelectionStyle;
import viewer.largegraphs.styles.selection.FastNodeSelectionStyle;
import viewer.largegraphs.virtualization.VirtualizationEdgeStyleDecorator;
import viewer.largegraphs.virtualization.VirtualizationNodeStyleDecorator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.GZIPInputStream;

/**
 * This demo illustrates improvements in rendering performance for very large graphs in yFiles for JavaFX.
 */
public class LargeGraphsDemo extends DemoApplication {

  // region Wrapper styles so that styles are easier to change.

  // Wrapper style for edge labels.
  private WrapperLabelStyle edgeLabelStyle = new WrapperLabelStyle(null);

  // Wrapper style for edges.
  private WrapperEdgeStyle edgeStyle = new WrapperEdgeStyle(null);

  // Wrapper style for node labels
  private WrapperLabelStyle nodeLabelStyle = new WrapperLabelStyle(null);

  // Wrapper style for nodes
  private WrapperNodeStyle nodeStyle = new WrapperNodeStyle(null);


  // region Fields for GUI items
  public WebView webView;

  public GraphControl graphControl;
  public GraphOverviewControl overview;

  public Button prevBtn;
  public Button nextBtn;
  public ComboBox<GraphEntry> graphChooserBox;
  public TitledPane overviewPane;
  public VBox rightVBox;
  public CheckBox disableOverviewCB;
  public CheckBox enablePathRenderOptimizationCB;
  public CheckBox enableFastStylesCB;
  public DoubleTextField hideEdgesTF;
  public DoubleTextField hideBendsTF;
  public DoubleTextField hideEdgeLabelsTF;
  public DoubleTextField sketchEdgeLabelsTF;
  public DoubleTextField nodeStyleTF;
  public DoubleTextField hideNodeLabelsTF;
  public DoubleTextField sketchNodeLabelsTF;
  public CheckBox disableVirtualizationsCB;
  public DoubleTextField virtualizeEdgesTF;
  public DoubleTextField virtualizeNodesTF;
  public CheckBox disableSelectionHandlesCB;
  public CheckBox customSelectionDecorationCB;
  public CheckBox fixLabelPositionsCB;
  public Label zoomLbl;
  public Label selectedItemsLbl;
  public Label fpsLbl;
  public Label frameCountLbl;
  private FPSMeter fpsMeter;

  // endregion

  // region Configure GUI Components

  // region Performance optimizations

  public void applyState(ActionEvent event) {
    CheckBox source = (CheckBox) event.getSource();
    boolean selected = source.isSelected();
    if (source == disableOverviewCB) {
      getPerformanceSettings().setOverviewDisabled(selected);
    } else if (source == enableFastStylesCB) {
      getPerformanceSettings().setFastStylesEnabled(selected);
    } else if (source == disableSelectionHandlesCB) {
      getPerformanceSettings().setSelectionHandlesDisabled(selected);
    } else if (source == customSelectionDecorationCB) {
      getPerformanceSettings().setCustomSelectionDecoratorEnabled(selected);
    } else if (source == fixLabelPositionsCB) {
      getPerformanceSettings().setFixedLabelPositionsEnabled(selected);
    } else if (source == enablePathRenderOptimizationCB) {
      getPerformanceSettings().setPathRenderOptimizationEnabled(selected);
    } else if (source == disableVirtualizationsCB) {
      getPerformanceSettings().setVirtualizationDisabled(selected);
    }
  }

  public void applyThreshold(ActionEvent event) {
    TextField source = (TextField) event.getSource();
    double value = Double.parseDouble(source.getText());
    if (source == hideEdgesTF) {
      getPerformanceSettings().setMinimumEdgeLength(value);
    } else if (source == hideBendsTF) {
      getPerformanceSettings().setEdgeBendThreshold(value);
    } else if (source == hideEdgeLabelsTF) {
      getPerformanceSettings().setEdgeLabelVisibilityThreshold(value);
    } else if (source == sketchEdgeLabelsTF) {
      getPerformanceSettings().setEdgeLabelTextThreshold(value);
    } else if (source == nodeStyleTF) {
      getPerformanceSettings().setComplexNodeStyleThreshold(value);
    } else if (source == hideNodeLabelsTF) {
      getPerformanceSettings().setNodeLabelVisibilityThreshold(value);
    } else if (source == sketchNodeLabelsTF) {
      getPerformanceSettings().setNodeLabelTextThreshold(value);
    } else if (source == virtualizeEdgesTF) {
      getPerformanceSettings().setEdgeVirtualizationThreshold(value);
    } else if (source == virtualizeNodesTF) {
      getPerformanceSettings().setNodeVirtualizationThreshold(value);
    }
  }

  // endregion

  // region Test controls

  /**
   * Initializes the listener to update the information on the Information pane.
   */
  private void initializeInformationPane() {
    graphControl.zoomProperty().addListener((observable, oldValue, newValue) ->
        zoomLbl.setText(((int) (graphControl.getZoom() * 10000)) / 100.0 + " %"));
    graphControl.getSelection().addItemSelectionChangedListener((source, args) -> {
      IGraphSelection s = graphControl.getSelection();
      int selectedItemCount = s.getSelectedBends().size() + s.getSelectedEdges().size() +
          s.getSelectedLabels().size() + s.getSelectedNodes().size() + s.getSelectedPorts().size();
      selectedItemsLbl.setText(Integer.toString(selectedItemCount));
    });

    fpsMeter = new FPSMeter(graphControl, () -> {
      fpsLbl.setText(fpsMeter.getFps());
      frameCountLbl.setText(Integer.toString(fpsMeter.getFrameCount()));
    });
  }

  // region Selection helpers

  /**
   * Clears the selection.
   */
  public void onSelectNothing(ActionEvent e) {
    graphControl.getSelection().clear();
  }

  /**
   * Selects all nodes, edges and labels.
   */
  public void onSelectAll(ActionEvent e) {
    onSelectAllNodes(e);
    onSelectAllEdges(e);
    onSelectAllLabels(e);
  }

  /**
   * Randomly selects 1000 nodes.
   */
  public void onSelect1000Nodes(ActionEvent e) {
    select1000(graphControl.getGraph().getNodes());
  }

  /**
   * Randomly selects 1000 edges.
   */
  public void onSelect1000Edges(ActionEvent e) {
    select1000(graphControl.getGraph().getEdges());
  }

  /**
   * Randomly selects 1000 labels.
   */
  public void onSelect1000Labels(ActionEvent e) {
    select1000(graphControl.getGraph().getLabels());
  }

  private <T extends IModelItem> void select1000(IListEnumerable<T> items) {
    IEnumerator<T> enumerator = items.enumerator();
    Object[] shuffled = shuffle(enumerator.toArray(enumerator, items.size()));
    for (int i = 0; i < 1000; i++) {
      graphControl.getSelection().setSelected((IModelItem) shuffled[i], true);
    }
  }

  /**
   * Fisher Yates Shuffle for arrays.
   *
   * @param array The Array to shuffle.
   * @return The shuffled array.
   */
  Object[] shuffle(Object[] array) {
    int m = array.length;
    Object t;
    int i;
    while (m > 0) {
      // pick a remaining element
      i = getRandomInt(m);
      m--;
      // swap with current element
      t = array[m];
      array[m] = array[i];
      array[i] = t;
    }
    return array;
  }

  int getRandomInt(int upper) {
    return (int) Math.floor(Math.random() * upper);
  }

  /**
   * Selects all nodes.
   */
  public void onSelectAllNodes(ActionEvent e) {
    IListEnumerable<INode> nodes = graphControl.getGraph().getNodes();
    for (INode node : nodes) {
      graphControl.getSelection().setSelected(node, true);
    }
  }

  /**
   * Selects all edges.
   */
  public void onSelectAllEdges(ActionEvent e) {
    IListEnumerable<IEdge> edges = graphControl.getGraph().getEdges();
    for (IEdge edge : edges) {
      graphControl.getSelection().setSelected(edge, true);
    }
  }

  /**
   * Selects all labels.
   */
  public void onSelectAllLabels(ActionEvent e) {
    IListEnumerable<ILabel> labels = graphControl.getGraph().getLabels();
    for (ILabel label : labels) {
      graphControl.getSelection().setSelected(label, true);
    }
  }

  // endregion

  // region Animations

  /**
   * Called when The 'Zoom animation' button was clicked.
   */
  public void onZoomAnimationClicked() {
    startAnimation();
    INode node = getRandomNode();
    graphControl.setCenter(node.getLayout().getCenter());

    IAnimation animation = new ZoomInAndBackAnimation(graphControl, 10, Duration.ofSeconds(5));
    Animator animator = new Animator(graphControl);
    animator.animate(animation).thenRun(this::endAnimation);
  }

  /**
   * Called when the 'Pan animation' button was clicked.
   */
  public void onPanAnimationClicked() {
    startAnimation();
    IAnimation animation = new CirclePanAnimation(graphControl, 5, Duration.ofSeconds(5));
    Animator animator = new Animator(graphControl);
    animator.animate(animation).thenRun(this::endAnimation);
  }

  /**
   * Called when the 'Spiral zoom animation' button was clicked.
   */
  public void onSpiralZoomAnimationClicked() {
    startAnimation();
    INode node = getRandomNode();
    graphControl.setCenter(
        PointD.add(node.getLayout().getCenter(), new PointD(graphControl.getViewport().getWidth() / 4, 0)));

    IAnimation zoom = new ZoomInAndBackAnimation(graphControl, 10, Duration.ofSeconds(10));
    IAnimation pan = new CirclePanAnimation(graphControl, 14, Duration.ofSeconds(10));
    IAnimation animation = IAnimation.createParallelAnimation(zoom, pan);
    Animator animator = new Animator(graphControl);
    animator.animate(animation).thenRun(this::endAnimation);
  }

  /**
   * Called when 'Move nodes' button was clicked.
   */
  public void onNodeAnimationClicked() {
    startAnimation();
    IGraphSelection selection = graphControl.getSelection();
    // If there is nothing selected, just use a random node
    if (selection.getSelectedNodes().size() == 0) {
      selection.setSelected(getRandomNode(), true);
    }

    ArrayList<INode> selectedNodes = new ArrayList<>(selection.getSelectedNodes().size());
    selection.getSelectedNodes().forEach(selectedNodes::add);

    IAnimation animation = new CircleNodeAnimation(graphControl.getGraph(), selectedNodes,
        graphControl.getViewport().getWidth() / 10, 10, Duration.ofSeconds(10));
    Animator animator = new Animator(graphControl);
    animator.animate(animation).thenRun(() -> {
      endAnimation();
      graphControl.invalidate();
    });
  }

  private void startAnimation() {
    fpsMeter.setRecording(true);
  }

  private void endAnimation() {
    fpsMeter.setRecording(false);
  }

  private Random rnd = new Random(42);

  /**
   * Returns a random node from the graph.
   * @return A random node from the graph.
   */
  private INode getRandomNode() {
    IListEnumerable<INode> nodes = graphControl.getGraph().getNodes();
    return nodes.getItem(rnd.nextInt(nodes.size()));
  }


  // endregion

  // endregion

  // region Menu

  /**
   * Exits the demo.
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

  // endregion

  // region ToolBar

  /**
   * Creates the JComboBox where the various graphs are selectable.
   */
  private void configureComboBox() {
    graphChooserBox.getItems().addAll(
        new GraphEntry("hierarchic_2000_2100.graphmlz", "Hierarchic: 2000 nodes, 2100 edges"),
        new GraphEntry("hierarchic_5000_5100.graphmlz", "Hierarchic: 5000 nodes, 5100 edges"),
        new GraphEntry("hierarchic_10000_11000.graphmlz", "Hierarchic: 10000 nodes, 11000 edges"),
        new GraphEntry("hierarchic_15000_16000.graphmlz", "Hierarchic: 15000 nodes, 16000 edges"),
        new GraphEntry("balloon_2000_1999.graphmlz", "Tree: 2000 nodes, 1999 edges"),
        new GraphEntry("balloon_5000_4999.graphmlz", "Tree: 5000 nodes, 4999 edges"),
        new GraphEntry("balloon_10000_9999.graphmlz", "Tree: 10000 nodes, 9999 edges"),
        new GraphEntry("balloon_15000_14999.graphmlz", "Tree: 15000 nodes, 14999 edges"));
    graphChooserBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> readSampleGraph());
  }

  /**
   * Reads the currently selected GraphML from the graphChooserBox
   */
  private void readSampleGraph() {
    GraphEntry graphEntry = this.graphChooserBox.getSelectionModel().getSelectedItem();
    loadGraphAsync(graphEntry);
  }

  /**
   * Loads a graph asynchronously and places it in the {@link com.yworks.yfiles.view.GraphControl}.
   *
   * @param graphEntry The graph information
   */
  private void loadGraphAsync(GraphEntry graphEntry) {
    graphChooserBox.setDisable(true);
    nextBtn.setDisable(true);
    prevBtn.setDisable(true);

    graphControl.lookup(WaitInputMode.class).setWaiting(true);

    updatePerformanceSettings(bestSettings[graphChooserBox.getSelectionModel().getSelectedIndex()]);
    final DefaultGraph g = new DefaultGraph();
    g.setUndoEngineEnabled(true);
    setDefaultStyles(g);
    updateStyles();
    setSelectionDecorators(g);
    updateSelectionHandlesSetting();
    updateOverviewDisabledSetting();

    // first derive the file name
    URL graphML = getClass().getResource("resources/" + graphEntry.fileName);

    new Thread(() -> {
      // then load the graph asynchronously
      try {
        GraphMLIOHandler handler = graphControl.getGraphMLIOHandler();
        FileInputStream inputStream = new FileInputStream(new File(graphML.toURI()));
        GZIPInputStream zipInputStream = new GZIPInputStream(inputStream);

        handler.getDeserializationPropertyOverrides().set(SerializationProperties.PARSE_LABEL_SIZE, Boolean.FALSE);
        handler.read(g, zipInputStream);
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }

      Platform.runLater(() -> {
        // update and set the graph in the JavaFX Application Thread
        updateFixedLabelPositionsSetting(g);

        graphChooserBox.setDisable(false);
        updateButtons();
        graphControl.lookup(WaitInputMode.class).setWaiting(false);
        graphControl.setGraph(g);
        graphControl.fitGraphBounds();
        // the commands CanExecute state might have changed - suggest a re-query. mainly to update the enabled status of the previous / next buttons.
        ICommand.invalidateRequerySuggested();
      });
    }).start();
  }

  /**
   * Disables the 'Previous/Next graph' buttons in the UI according to whether there is a previous/next graph to switch
   * to.
   */
  private void updateButtons() {
    nextBtn.setDisable(graphChooserBox.getSelectionModel().getSelectedIndex() >= graphChooserBox.getItems().size() - 1);
    prevBtn.setDisable(graphChooserBox.getSelectionModel().getSelectedIndex() <= 0);
  }

  public void showGraph(ActionEvent event) {
    if (event.getSource() == prevBtn) {
      graphChooserBox.getSelectionModel().select(graphChooserBox.getSelectionModel().getSelectedIndex() - 1);
    } else if (event.getSource() == nextBtn) {
      graphChooserBox.getSelectionModel().select(graphChooserBox.getSelectionModel().getSelectedIndex() + 1);
    }
  }

  /**
   * Entry of the {@link #graphChooserBox} containing the file name and the display name of a sample graph.
   */
  private static class GraphEntry {
    private String fileName;
    private String displayName;

    GraphEntry(String fileName, String displayName) {
      this.fileName = fileName;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  // endregion

  // endregion

  // region Initialization

  public void initialize() {
    configureComboBox();
    initializeInformationPane();
    initializeInputMode();
    initializePerformanceSettings();
    WebViewUtils.initHelp(webView, this);
  }

  @Override
  public void onLoaded() {
    graphChooserBox.getSelectionModel().select(0);
  }

  /**
   * Initializes the input mode for the {@link #graphControl}.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();

    // These two sub-input modes need to look at all elements in the graph to determine whether they are
    // responsible for a beginning drag. This slows down initial UI response to marquee selection or panning the
    // viewport in graphs with a large number of items.
    // Depending on the exact needs it might be better to enable those only when edges or bends actually need to
    // be created.
    // Of course, using a GraphViewerInputMode here sidesteps the problem completely, if no graph editing is
    // needed.
    geim.getCreateEdgeInputMode().setEnabled(false);
    geim.getCreateBendInputMode().setEnabled(false);

    graphControl.setInputMode(geim);
  }

  // endregion

  // region Performance setting helpers

  // Optimal settings for the sample graphs.
  private PerformanceSettings[] bestSettings;

  private PerformanceSettings performanceSettings;

  /**
   * Gets the performance settings for the current graph.
   */
  private PerformanceSettings getPerformanceSettings() {
    return performanceSettings;
  }

  /**
   * Sets the performance settings for the current graph.
   * <p>
   * For each sample graph, prepared settings are applied.
   * </p>
   */
  private void setPerformanceSettings(PerformanceSettings performanceSettings) {
    this.performanceSettings = performanceSettings;
    disableOverviewCB.setSelected(performanceSettings.isOverviewDisabled());
    enablePathRenderOptimizationCB.setSelected(performanceSettings.isPathRenderOptimizationEnabled());
    enableFastStylesCB.setSelected(performanceSettings.isFastStylesEnabled());
    hideEdgesTF.setValue(performanceSettings.getMinimumEdgeLength());
    hideBendsTF.setValue(performanceSettings.getEdgeBendThreshold());
    hideEdgeLabelsTF.setValue(performanceSettings.getEdgeLabelVisibilityThreshold());
    sketchEdgeLabelsTF.setValue(performanceSettings.getEdgeLabelTextThreshold());
    nodeStyleTF.setValue(performanceSettings.getComplexNodeStyleThreshold());
    hideNodeLabelsTF.setValue(performanceSettings.getNodeLabelVisibilityThreshold());
    sketchNodeLabelsTF.setValue(performanceSettings.getNodeLabelTextThreshold());

    disableVirtualizationsCB.setSelected(performanceSettings.isVirtualizationDisabled());
    virtualizeEdgesTF.setValue(performanceSettings.getEdgeVirtualizationThreshold());
    virtualizeNodesTF.setValue(performanceSettings.getNodeVirtualizationThreshold());

    disableSelectionHandlesCB.setSelected(performanceSettings.isSelectionHandlesDisabled());
    customSelectionDecorationCB.setSelected(performanceSettings.isCustomSelectionDecoratorEnabled());
    fixLabelPositionsCB.setSelected(performanceSettings.isFixedLabelPositionsEnabled());
  }

  /**
   * Initializes the list of optimal performance settings for the sample graphs.
   */
  private void initializePerformanceSettings() {

    PerformanceSettings ps1 = new PerformanceSettings();
    {
      ps1.setVirtualizationDisabled(false);
      ps1.setNodeVirtualizationThreshold(12);
      ps1.setEdgeVirtualizationThreshold(20);
      ps1.setMinimumEdgeLength(0);
      ps1.setEdgeBendThreshold(0);
      ps1.setEdgeLabelVisibilityThreshold(50);
      ps1.setNodeLabelVisibilityThreshold(20);
      ps1.setNodeLabelTextThreshold(40);
      ps1.setEdgeLabelTextThreshold(50);
      ps1.setComplexNodeStyleThreshold(60);
      ps1.setOverviewDisabled(true);
      ps1.setFastStylesEnabled(true);
      ps1.setSelectionHandlesDisabled(true);
      ps1.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps2 = new PerformanceSettings();
    {
      ps2.setVirtualizationDisabled(false);
      ps2.setNodeVirtualizationThreshold(10);
      ps2.setEdgeVirtualizationThreshold(15);
      ps2.setMinimumEdgeLength(10);
      ps2.setEdgeBendThreshold(50);
      ps2.setEdgeLabelVisibilityThreshold(80);
      ps2.setNodeLabelVisibilityThreshold(20);
      ps2.setNodeLabelTextThreshold(40);
      ps2.setEdgeLabelTextThreshold(80);
      ps2.setComplexNodeStyleThreshold(100);
      ps2.setOverviewDisabled(true);
      ps2.setFastStylesEnabled(true);
      ps2.setSelectionHandlesDisabled(true);
      ps2.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps3 = new PerformanceSettings();
    {
      ps3.setVirtualizationDisabled(true);
      ps3.setNodeVirtualizationThreshold(4);
      ps3.setEdgeVirtualizationThreshold(5);
      ps3.setFixedLabelPositionsEnabled(true);
      ps3.setMinimumEdgeLength(10);
      ps3.setEdgeBendThreshold(50);
      ps3.setEdgeLabelVisibilityThreshold(80);
      ps3.setNodeLabelVisibilityThreshold(20);
      ps3.setNodeLabelTextThreshold(40);
      ps3.setEdgeLabelTextThreshold(80);
      ps3.setComplexNodeStyleThreshold(100);
      ps3.setOverviewDisabled(true);
      ps3.setFastStylesEnabled(true);
      ps3.setSelectionHandlesDisabled(true);
      ps3.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps4 = new PerformanceSettings();
    {
      ps4.setVirtualizationDisabled(true);
      ps4.setNodeVirtualizationThreshold(3);
      ps4.setEdgeVirtualizationThreshold(4);
      ps4.setFixedLabelPositionsEnabled(true);
      ps4.setMinimumEdgeLength(10);
      ps4.setEdgeBendThreshold(50);
      ps4.setEdgeLabelVisibilityThreshold(80);
      ps4.setNodeLabelVisibilityThreshold(20);
      ps4.setNodeLabelTextThreshold(40);
      ps4.setEdgeLabelTextThreshold(80);
      ps4.setComplexNodeStyleThreshold(100);
      ps4.setOverviewDisabled(true);
      ps4.setFastStylesEnabled(true);
      ps4.setSelectionHandlesDisabled(true);
      ps4.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps5 = new PerformanceSettings();
    {
      ps5.setVirtualizationDisabled(false);
      ps5.setNodeVirtualizationThreshold(10);
      ps5.setEdgeVirtualizationThreshold(18);
      ps5.setMinimumEdgeLength(0);
      ps5.setEdgeBendThreshold(0);
      ps5.setEdgeLabelVisibilityThreshold(50);
      ps5.setNodeLabelVisibilityThreshold(20);
      ps5.setNodeLabelTextThreshold(40);
      ps5.setEdgeLabelTextThreshold(50);
      ps5.setComplexNodeStyleThreshold(60);
      ps5.setOverviewDisabled(true);
      ps5.setFastStylesEnabled(true);
      ps5.setSelectionHandlesDisabled(true);
      ps5.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps6 = new PerformanceSettings();
    {
      ps6.setVirtualizationDisabled(false);
      ps6.setNodeVirtualizationThreshold(2);
      ps6.setEdgeVirtualizationThreshold(4);
      ps6.setMinimumEdgeLength(10);
      ps6.setEdgeBendThreshold(0);
      ps6.setEdgeLabelVisibilityThreshold(50);
      ps6.setNodeLabelVisibilityThreshold(20);
      ps6.setNodeLabelTextThreshold(40);
      ps6.setEdgeLabelTextThreshold(50);
      ps6.setComplexNodeStyleThreshold(60);
      ps6.setOverviewDisabled(true);
      ps6.setFastStylesEnabled(true);
      ps6.setSelectionHandlesDisabled(true);
      ps6.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps7 = new PerformanceSettings();
    {
      ps7.setVirtualizationDisabled(true);
      ps7.setNodeVirtualizationThreshold(2);
      ps7.setEdgeVirtualizationThreshold(3);
      ps7.setFixedLabelPositionsEnabled(true);
      ps7.setMinimumEdgeLength(10);
      ps7.setEdgeBendThreshold(0);
      ps7.setEdgeLabelVisibilityThreshold(50);
      ps7.setNodeLabelVisibilityThreshold(20);
      ps7.setNodeLabelTextThreshold(40);
      ps7.setEdgeLabelTextThreshold(50);
      ps7.setComplexNodeStyleThreshold(60);
      ps7.setOverviewDisabled(true);
      ps7.setFastStylesEnabled(true);
      ps7.setSelectionHandlesDisabled(true);
      ps7.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps8 = new PerformanceSettings();
    {
      ps8.setVirtualizationDisabled(true);
      ps8.setNodeVirtualizationThreshold(1.2);
      ps8.setEdgeVirtualizationThreshold(1.8);
      ps8.setFixedLabelPositionsEnabled(true);
      ps8.setMinimumEdgeLength(10);
      ps8.setEdgeBendThreshold(0);
      ps8.setEdgeLabelVisibilityThreshold(80);
      ps8.setNodeLabelVisibilityThreshold(30);
      ps8.setNodeLabelTextThreshold(40);
      ps8.setEdgeLabelTextThreshold(80);
      ps8.setComplexNodeStyleThreshold(100);
      ps8.setOverviewDisabled(true);
      ps8.setFastStylesEnabled(true);
      ps8.setSelectionHandlesDisabled(true);
      ps8.setCustomSelectionDecoratorEnabled(true);
    }
    bestSettings = new PerformanceSettings[]{ps1, ps2, ps3, ps4, ps5, ps6, ps7, ps8};
  }

  /**
   * Sets a new {@link PerformanceSettings} instance for the sample graphs and updates the GUI.
   * <p>
   * Since the instance is mutable, this will assign a copy to {@link #setPerformanceSettings(PerformanceSettings)}.
   * </p>
   *
   * @param newSettings The new settings instance.
   */
  private void updatePerformanceSettings(PerformanceSettings newSettings) {
    if (getPerformanceSettings() != null) {
      getPerformanceSettings().setChangedCallback(null);
    }
    setPerformanceSettings(PerformanceSettings.getCopy(newSettings));
    getPerformanceSettings().setChangedCallback(this::onPerformanceSettingsChanged);
  }

  /**
   * Called when a property in {@link #getPerformanceSettings()} changes.
   * <p>
   * Styles and other settings are updated if needed, depending on the property that changed.
   * </p>
   * @param propertyName The name of the property that changed.
   */
  private void onPerformanceSettingsChanged(String propertyName) {
    // If the property name is the empty string or null, it indicates that every property changed.
    if (propertyName == null || propertyName.isEmpty()) {
      updateStyles();
      updateSelectionHandlesSetting();
      updateFixedLabelPositionsSetting(graphControl.getGraph());
      updateOverviewDisabledSetting();
      graphControl.invalidate();
      refreshSelection();
      return;
    }
    switch (propertyName) {
      case "FastStylesEnabled":
      case "MinimumEdgeLength":
      case "EdgeBendThreshold":
      case "EdgeLabelVisibilityThreshold":
      case "NodeLabelVisibilityThreshold":
      case "NodeLabelTextThreshold":
      case "EdgeLabelTextThreshold":
      case "ComplexNodeStyleThreshold":
      case "PathRenderOptimizationEnabled":
      case "VirtualizationDisabled":
      case "NodeVirtualizationThreshold":
      case "EdgeVirtualizationThreshold":
        updateStyles();
        break;
      case "SelectionHandlesDisabled":
        updateSelectionHandlesSetting();
        refreshSelection();
        break;
      case "CustomSelectionDecoratorEnabled":
        refreshSelection();
        break;
      case "FixedLabelPositionsEnabled":
        updateFixedLabelPositionsSetting(graphControl.getGraph());
        break;
      case "OverviewDisabled":
        updateOverviewDisabledSetting();
        break;
    }
  }

  private void updateOverviewDisabledSetting() {
    boolean b = getPerformanceSettings().isOverviewDisabled();
    overview.setGraphControl(b ? null : graphControl);
    if (b) {
      if (rightVBox.getChildren().size() > 1) {
        rightVBox.getChildren().remove(0);
      }
    } else {
      rightVBox.getChildren().add(0, overviewPane);
    }
  }

  private void setDefaultStyles(IGraph graph) {
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getEdgeDefaults().setStyle(edgeStyle);
    graph.getNodeDefaults().getLabelDefaults().setStyle(nodeLabelStyle);
    graph.getEdgeDefaults().getLabelDefaults().setStyle(edgeLabelStyle);
  }

  /**
   * Updates the styles according to the values in {@link #getPerformanceSettings()}.
   * <p>
   * See {@link PerformanceSettings#isFastStylesEnabled()} and {@link PerformanceSettings#isVirtualizationDisabled()}
   * for a detailed description of the optimizations involved.
   * </p>
   */
  private void updateStyles() {
    PerformanceSettings p = getPerformanceSettings();

    // A few colors we need more than once
    Color darkOrange = Color.DARKORANGE;
    Pen black = Pen.getBlack();
    Color white = Color.WHITE;

    // Default label styles (those are also used at high zoom levels)
    DefaultLabelStyle simpleEdgeLabelStyle = new DefaultLabelStyle();
    simpleEdgeLabelStyle.setBackgroundPaint(white);
    DefaultLabelStyle simpleNodeLabelStyle = new DefaultLabelStyle();

    if (p.isFastStylesEnabled()) {
      // Nodes
      LevelOfDetailNodeStyle lodns = new LevelOfDetailNodeStyle();
      ShapeNodeStyle sns1 = new ShapeNodeStyle();
      sns1.setShape(ShapeNodeShape.RECTANGLE);
      sns1.setPen(null);
      sns1.setPaint(darkOrange);
      lodns.getStyles().add(0, sns1);

      ShapeNodeStyle sns2 = new ShapeNodeStyle();
      sns2.setShape(ShapeNodeShape.ROUND_RECTANGLE);
      sns2.setPen(black);
      sns2.setPaint(darkOrange);
      lodns.getStyles().add(p.getComplexNodeStyleThreshold() / 100 / 2, sns2);

      RectangleNodeStyle rns = new RectangleNodeStyle();
      rns.setPen(black);
      rns.setPaint(darkOrange);
      lodns.getStyles().add(p.getComplexNodeStyleThreshold() / 100, rns);
      nodeStyle.setStyle(lodns);

      // Edges
      edgeStyle.setStyle(new FastEdgeStyle(p.getEdgeBendThreshold() / 100, p.getMinimumEdgeLength()));

      // Node labels
      LevelOfDetailLabelStyle lodls1 = new LevelOfDetailLabelStyle();
      lodls1.getStyles().add(0, VoidLabelStyle.INSTANCE);
      lodls1.getStyles().add(p.getNodeLabelVisibilityThreshold() / 100, new FastLabelStyle(true));
      lodls1.getStyles().add(p.getNodeLabelTextThreshold() / 100, simpleNodeLabelStyle);
      nodeLabelStyle.setStyle(lodls1);

      // Edge labels
      LevelOfDetailLabelStyle lodls2 = new LevelOfDetailLabelStyle();
      lodls2.getStyles().add(0, VoidLabelStyle.INSTANCE);
      lodls2.getStyles().add(p.getEdgeLabelVisibilityThreshold() / 100, new FastLabelStyle(true, white));
      lodls2.getStyles().add(p.getEdgeLabelTextThreshold() / 100, simpleEdgeLabelStyle);
      edgeLabelStyle.setStyle(lodls2);
    } else {
      ShapeNodeStyle sns = new ShapeNodeStyle();
      sns.setShape(ShapeNodeShape.RECTANGLE);
      sns.setPen(black);
      sns.setPaint(darkOrange);
      nodeStyle.setStyle(sns);
      PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
      if (p.isPathRenderOptimizationEnabled()) {
        edgeStyle.setPathRenderPolicy(PathRenderPolicy.LINES);
      }
      this.edgeStyle.setStyle(edgeStyle);
      edgeLabelStyle.setStyle(simpleEdgeLabelStyle);
      nodeLabelStyle.setStyle(simpleNodeLabelStyle);
    }

    // If we disable virtualization we just wrap the style we had so far into a Virtualization*StyleDecorator
    if (p.isVirtualizationDisabled()) {
      nodeStyle.setStyle(new VirtualizationNodeStyleDecorator(p.getNodeVirtualizationThreshold() / 100,
          nodeStyle.getStyle()));
      edgeStyle.setStyle(new VirtualizationEdgeStyleDecorator(p.getEdgeVirtualizationThreshold() / 100,
          edgeStyle.getStyle()));
    }

    // Repaint the graph control to update the visuals according to the changed style
    graphControl.invalidate();
  }

  /**
   * Sets the selection decorators on the given {@link IGraph} instance.
   * <p>
   * This actually sets the selection decorator implementation by using a custom predicate which simply queries the
   * current {@link #getPerformanceSettings()}. Thus the decoration is always up-to-date; the only thing that's needed
   * when the setting changes is to re-select all selected items to re-create the respective selection decoration
   * visuals.
   * </p>
   * @param g The graph.
   */
  private void setSelectionDecorators(IGraph g) {
    NodeStyleDecorationInstaller nodeStyleDecorationInstaller = new NodeStyleDecorationInstaller();
    nodeStyleDecorationInstaller.setNodeStyle(new FastNodeSelectionStyle(null, new Pen(Color.DARKRED, 4)));
    nodeStyleDecorationInstaller.setZoomPolicy(StyleDecorationZoomPolicy.WORLD_COORDINATES);
    nodeStyleDecorationInstaller.setMargins(InsetsD.EMPTY);

    EdgeStyleDecorationInstaller edgeStyleDecorationInstaller = new EdgeStyleDecorationInstaller();
    edgeStyleDecorationInstaller.setEdgeStyle(new FastEdgeSelectionStyle(new Pen(Color.DARKRED, 3)));
    edgeStyleDecorationInstaller.setZoomPolicy(StyleDecorationZoomPolicy.WORLD_COORDINATES);

    LabelStyleDecorationInstaller labelStyleDecorationInstaller = new LabelStyleDecorationInstaller();
    labelStyleDecorationInstaller.setLabelStyle(new FastLabelSelectionStyle(null, new Pen(Color.LIGHTGRAY, 4)));
    labelStyleDecorationInstaller.setZoomPolicy(StyleDecorationZoomPolicy.WORLD_COORDINATES);
    labelStyleDecorationInstaller.setMargins(InsetsD.EMPTY);

    GraphDecorator decorator = g.getDecorator();
    decorator.getNodeDecorator().getSelectionDecorator().setImplementation(
        a -> getPerformanceSettings().isCustomSelectionDecoratorEnabled(), nodeStyleDecorationInstaller);
    decorator.getEdgeDecorator().getSelectionDecorator().setImplementation(
        a -> getPerformanceSettings().isCustomSelectionDecoratorEnabled(), edgeStyleDecorationInstaller);
    decorator.getLabelDecorator().getSelectionDecorator().setImplementation(
        a -> getPerformanceSettings().isCustomSelectionDecoratorEnabled(), labelStyleDecorationInstaller);
  }

  /**
   * Updates the input mode to reflect the current value of the {@link PerformanceSettings#isSelectionHandlesDisabled()}
   * setting.
   * <p>
   * See {@link PerformanceSettings#isSelectionHandlesDisabled()} for a rationale for this optimization.
   * </p>
   */
  private void updateSelectionHandlesSetting() {
    PerformanceSettings p = getPerformanceSettings();
    IInputMode inputMode = graphControl.getInputMode();

    if (inputMode instanceof GraphEditorInputMode) {
      ((GraphEditorInputMode) inputMode).setShowHandleItems(
          p.isSelectionHandlesDisabled() ? GraphItemTypes.NONE : GraphItemTypes.ALL);
    }
  }

  /**
   * Updates all labels in the graph according to the current value of the
   * {@link PerformanceSettings#isFixedLabelPositionsEnabled()} setting.
   * <p>
   * See the {@link PerformanceSettings#isFixedLabelPositionsEnabled()} for a rationale for this optimization.
   * </p>
   * <p>
   * When activating this setting, all labels get a {@link FreeLabelModel}. The model's
   * {@link FreeLabelModel#createAbsolute(PointD, double) absolute parameter} is used to ensure the  labels do not
   * change their positions.
   * When disabling this setting, all labels are assigned the appropriate default label layout parameter for their
   * type (i.e. node label, node port label, edge label, or edge port label).
   * </p>
   * <p>
   * Labels using the {@link FreeLabelModel} are positioned absolutely in the canvas. Thus they won't move when their
   * owners move. If there is no need of getting the last bit of performance out of yFiles, {@link FreeNodeLabelModel}
   * and {@link FreeEdgeLabelModel} can be used instead. They are a bit slower than {@link FreeLabelModel}, but have the
   * benefit that they are anchored relative to their owner, creating a less jarring experience than labels that just
   * stay where they were when their owner moves.
   * </p>
   * <p>
   * Another option (not shown in this demo) would be to convert between the label models on affected labels prior to
   * and after an edit operation, such as moving nodes, adding or moving bends to edges, etc. so that the editing
   * experience uses the expensive label models, but all non-affected labels (and after finishing the edit, all labels)
   * use a {@link FreeLabelModel}.
   * </p>
   *
   * @param graph The graph whose labels shall be updated.
   */
  private void updateFixedLabelPositionsSetting(IGraph graph) {
    if (getPerformanceSettings().isFixedLabelPositionsEnabled()) {
      // fix the label position at the label's current absolute location
      FreeLabelModel model = new FreeLabelModel();
      for (ILabel l : graph.getLabels()) {
        IOrientedRectangle labelLayout = l.getLayout();
        graph.setLabelLayoutParameter(l, model.createAbsolute(labelLayout.getAnchorLocation(), getAngle(labelLayout)));
      }
    } else {
      // use default parameters for all labels
      // with these default parameters, label positions will be relative to their owner geometry
      INodeDefaults nodeDefaults = graph.getNodeDefaults();
      ILabelModelParameter nodeLabelParameter = nodeDefaults.getLabelDefaults().getLayoutParameter();
      ILabelModelParameter nodePortLabelParameter = nodeDefaults.getPortDefaults().getLabelDefaults().getLayoutParameter();
      IEdgeDefaults edgeDefaults = graph.getEdgeDefaults();
      ILabelModelParameter edgeLabelParameter = edgeDefaults.getLabelDefaults().getLayoutParameter();
      ILabelModelParameter edgePortLabelParameter = edgeDefaults.getPortDefaults().getLabelDefaults().getLayoutParameter();

      for (ILabel l : graph.getLabels()) {
        ILabelOwner owner = l.getOwner();
        if (owner instanceof INode) {
          graph.setLabelLayoutParameter(l, nodeLabelParameter);
        } else if (owner instanceof IEdge) {
          graph.setLabelLayoutParameter(l, edgeLabelParameter);
        } else if (owner instanceof IPort) {
          IPortOwner portOwner = ((IPort) owner).getOwner();
          if (portOwner instanceof INode) {
            graph.setLabelLayoutParameter(l, nodePortLabelParameter);
          } else if (portOwner instanceof IEdge) {
            graph.setLabelLayoutParameter(l, edgePortLabelParameter);
          }
        }
      }
    }
  }

  /**
   * Calculates the rotation angle of the given oriented rectangle.
   */
  private static double getAngle( IOrientedRectangle r ) {
    return Math.atan2(r.getUpX(), -r.getUpY());
  }

  /**
   * De-selects all elements and re-selects them again.
   * <p>
   * This is needed to update the visuals for the handles or selection decoration.
   * </p>
   */
  private void refreshSelection() {
    IGraphSelection oldSelection = graphControl.getSelection();
    graphControl.setSelection(new GraphSelection(graphControl.getGraph()));
    graphControl.setSelection(oldSelection);
  }

  // endregion

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) throws IOException {
    // we want the application to be maximized from the start
    primaryStage.setMaximized(true);
    super.start(primaryStage);
  }
}
