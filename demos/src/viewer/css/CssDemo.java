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
package viewer.css;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.LabelDefaults;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.SmartEdgeLabelModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GraphOverviewControl;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.OverviewGraphVisualCreator;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

/**
 * This demo shows how to customize the visualizations in yFiles for JavaFX with the help of CSS.
 */
public class CssDemo extends DemoApplication {
  private static final GridInfo GRID_INFO = new GridInfo(50);
  private static final String ENCODING = "UTF-8";


  private GridVisualCreator grid;
  private GraphSnapContext graphSnapContext;
  private LabelSnapContext labelSnapContext;

  public GraphControl graphControl;
  public GraphOverviewControl overviewControl;
  public WebView webView;
  public ComboBox<ThemeInfo> themesComboBox;
  public TextArea cssTextArea;
  public TitledPane cssTitledPane;
  public BorderPane root;

  private URL currentCss;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the demo's {@link GraphControl} instance is initialized.
   */
  public void initialize() {
    WebViewUtils.initHelp(webView, this);

    // initialize the graph overview component
    overviewControl.setGraphControl(graphControl);
    overviewControl.setHorizontalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    overviewControl.setVerticalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    // set the styles used by the OverviewControl which supports CSS
    OverviewGraphVisualCreator ogvc = overviewControl.getGraphVisualCreator();
    ogvc.setNodeStyle(new CssNodeStyle("overview-node-style"));
    ogvc.setGroupNodeStyle(new CssNodeStyle("overview-group-node-style"));
    ogvc.setEdgeStyle(new CssEdgeStyle("overview-edge-style"));

    // initialize the graph and the input mode
    initializeGraph();
    initializeGrid();
    initializeSnapContext();
    initializeInputMode();
    initializeGraphmlNamespaces();

    // initialize theme chooser
    configureThemeComboBox();

    // initialize the CSS text area
    root.setRight(null);
    cssTextArea.setStyle("-fx-font-family: monospace");
  }

  /**
   * Maps our styles to a custom namespace that is used when reading/writing
   * the demo's graph from/to GraphML.
   */
  private void initializeGraphmlNamespaces() {
    GraphMLIOHandler ioh = graphControl.getGraphMLIOHandler();
    ioh.addNamespace("http://www.yworks.com/yfiles-for-javafx/demos/Css/1.0", "demo");
    ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/Css/1.0", CssEdgeStyle.class);
    ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/Css/1.0", CssNodeStyle.class);
    ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/Css/1.0", CssGroupNodeStyle.class);
    ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/Css/1.0", CssLabelStyle.class);
    ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/Css/1.0", CssCollapsibleNodeStyleDecorator.class);
  }

  /**
   * Configure the themes.
   */
  private void configureThemeComboBox() {
    themesComboBox.getItems().addAll(
        new ThemeInfo("Dark", "theme_dark"),
        new ThemeInfo("Light", "theme_light")
    );
    themesComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadTheme(newValue.fileName));
  }

  /**
   * Loads and applies the theme from the file referenced by the given file name.
   */
  private void loadTheme(String fileName) {
    ObservableList<String> stylesheets = graphControl.getScene().getStylesheets();
    stylesheets.clear();

    URL themeCss = getClass().getResource("resources/" + fileName + ".css");
    if (themeCss != null) {
      stylesheets.add(themeCss.toExternalForm());

      try {
        cssTextArea.setText(read(themeCss));
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }

      currentCss = themeCss;
    }
  }

  /**
   * Toggles the visibility of the text area showing the current CSS file.
   */
  public void toggleCss(ActionEvent event) {
    if (event.getSource() instanceof ToggleButton) {
      boolean enable = ((ToggleButton) event.getSource()).isSelected();
      root.setRight(enable ? cssTitledPane : null);
    }
  }

  /**
   * Stores and applies the changes of the CSS shown in the text area.
   */
  public void applyCss() {
    if (currentCss != null) {
      try {
        write(currentCss, cssTextArea.getText());

        String themeCss = currentCss.toExternalForm();

        ObservableList<String> stylesheets = graphControl.getScene().getStylesheets();
        stylesheets.remove(themeCss);
        stylesheets.add(themeCss);
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }

  /**
   * Called after the scene graph has been built up and shown.
   */
  public void onLoaded() {
    themesComboBox.getSelectionModel().select(1);
  }

  /**
   * Overwritten to prevent the demo from using the default yFiles demo CSS.
   */
  @Override
  public String getCssFileName() {
    return null;
  }

  /**
   * Initializes the grid feature.
   */
  private void initializeGrid() {
    grid = new GridVisualCreator(GRID_INFO);
    graphControl.getBackgroundGroup().addChild(grid, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
    // disable the grid by default
    grid.setVisible(false);
  }

  /**
   * Initializes the snapping feature.
   */
  private void initializeSnapContext() {
    graphSnapContext = new GraphSnapContext();
    graphSnapContext.setEnabled(false);
    // disable grid snapping because grid is disabled by default
    graphSnapContext.setGridSnapType(GridSnapTypes.NONE);
    // add constraint provider for nodes, bends, and ports
    graphSnapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));
    graphSnapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));
    graphSnapContext.setPortGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));

    // initialize label snapping
    labelSnapContext = new LabelSnapContext();
    // set maximum distance between the current mouse coordinates and the coordinates to which the mouse will snap
    labelSnapContext.setSnapDistance(15);
    // set the amount by which snap lines that are induced by existing edge segments are being extended
    labelSnapContext.setSnapLineExtension(100);
  }

  /**
   * Creates the default input mode for this demo.
   * The mode is configured to allow snapping and orthogonal edge editing.
   */
  private void initializeInputMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    mode.setSnapContext(graphSnapContext);
    mode.setLabelSnapContext(labelSnapContext);
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    orthogonalEdgeEditingContext.setEnabled(false);
    mode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    // make bend creation more important than moving of selected edges
    // this has the effect that dragging a selected edge (not its bends)
    // will create a new bend instead of moving all bends
    // This is especially nicer in conjunction with orthogonal
    // edge editing because otherwise orthogonal edge editing creates additional
    // bends every time the edge is moved
    mode.getCreateBendInputMode().setPriority(mode.getMoveInputMode().getPriority() - 1);

    // enable grouping operations such as ...
    // ... grouping selected nodes and
    // ... moving nodes into group nodes
    mode.setGroupingOperationsAllowed(true);

    // add command binding for 'new'
    mode.getKeyboardInputMode().addCommandBinding(ICommand.NEW, this::executeNew, this::canExecuteNew);

    graphControl.setInputMode(mode);
  }

  /**
   * Initializes the default styles of the demo's graph instance and
   * enables support for folding.
   */
  public void initializeGraph() {
    //Enable folding
    IFoldingView view = new FoldingManager().createFoldingView();
    IGraph graph = view.getGraph();

    // Enable undo/redo support
    // Get the master graph instance and enable undo/redo support
    IGraph masterGraph = view.getManager().getMasterGraph();
    masterGraph.setUndoEngineEnabled(true);

    // configure the group node style.
    //PanelNodeStyle is a nice style especially suited for group nodes
    CssGroupNodeStyle decoratedStyle = new CssGroupNodeStyle("group-node-style");
    CollapsibleNodeStyleDecorator groupNodeStyle = new CssCollapsibleNodeStyleDecorator(decoratedStyle);
    groupNodeStyle.setButtonPlacement(new FreeNodeLabelModel().createParameter(
        new PointD(0, 0), new PointD(3, 3), new PointD(0, 0)));
    graph.getGroupNodeDefaults().setStyle(groupNodeStyle);

    // Set a different label style and parameter
    CssLabelStyle groupNodeLabelStyle = new CssLabelStyle("label-style", "group-node");
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(groupNodeLabelStyle);
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(
        new FreeNodeLabelModel().createParameter(new PointD(1, 0), new PointD(-3, 3), new PointD(1, 0))
    );

    // Configure Graph defaults
    // set the default edge style
    graph.getEdgeDefaults().setStyle(new CssEdgeStyle(new Arrow(ArrowType.NONE), new Arrow(ArrowType.DEFAULT), "edge-style"));
    graph.getEdgeDefaults().getLabelDefaults().setStyle(new CssLabelStyle("label-style", "edge"));
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(new SmartEdgeLabelModel().createDefaultParameter());
    // set the default node style
    graph.getNodeDefaults().setStyle(new CssNodeStyle("node-style"));
    CssLabelStyle nodeLabelStyle = new CssLabelStyle("label-style", "node");
    // use the same label defaults for folder nodes and group nodes but
    // different label defaults for normal nodes
    graph.getNodeDefaults().setLabelDefaults(new MultiplexingLabelDefaults(view));
    graph.getNodeDefaults().getLabelDefaults().setStyle(nodeLabelStyle);
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(new FreeNodeLabelModel().createDefaultParameter());

    graphControl.setGraph(graph);
  }

  /**
   * Stops the demo.
   */
  public void exit() {
    Platform.exit();
  }

  /**
   * Toggles the snapping behavior when placing nodes.
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
   * Toggles the orthogonal edge editing behavior.
   * When this is enabled, edges are created and updated with orthogonal edge
   * segments only.
   */
  public void toggleOrthogonalEditing(ActionEvent event) {
    if (event.getSource() instanceof ToggleButton) {
      ToggleButton orthogonalEditingButton = (ToggleButton) event.getSource();
      boolean selected = orthogonalEditingButton.isSelected();
      GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
      inputMode.getOrthogonalEdgeEditingContext().setEnabled(selected);
    }
  }

  /**
   * Toggles the visibility of the grid.
   */
  public void toggleGrid(ActionEvent event) {
    if (event.getSource() instanceof ToggleButton) {
      ToggleButton gridToggleButton = (ToggleButton) event.getSource();
      boolean selected = gridToggleButton.isSelected();
      if (selected){
        graphSnapContext.setGridSnapType(GridSnapTypes.ALL);
      } else {
        graphSnapContext.setGridSnapType(GridSnapTypes.NONE);
      }
      grid.setVisible(selected);
      // update GraphControl to force a repaint with the new grid state
      graphControl.invalidate();
    }
  }

  /**
   * Toggles the selection mode.
   */
  public void toggleSelectionMode(ActionEvent event) {
    if (event.getSource() instanceof ToggleButton) {
      ToggleButton selectionModeToggleButton = (ToggleButton) event.getSource();
      boolean selected = selectionModeToggleButton.isSelected();
      GraphEditorInputMode geim = (GraphEditorInputMode) graphControl.getInputMode();
      geim.getLassoSelectionInputMode().setEnabled(selected);
    }
  }

  /**
   * Determines whether or not {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNew(ICommand command, Object param, Object sender) {
    // don't allow layouts for empty graphs
    return graphControl.getGraph().getNodes().size() != 0;
  }

  /**
   * Clears the demo's graph when {@link ICommand#NEW} is executed.
   */
  private boolean executeNew(ICommand command, Object param, Object sender) {
    graphControl.getGraph().clear();

    // update the can-execute-states of the commands since this is not
    // triggered by clearing the graph programmatically
    ICommand.invalidateRequerySuggested();
    return true;
  }


  /**
   * Reads the text content from the file identified by the given URL.
   */
  private static String read( URL src ) throws IOException {
    final StringBuilder sb = new StringBuilder();

    try  (BufferedReader br = new BufferedReader(new InputStreamReader(
            src.openStream(), ENCODING))) {
      String newline = "";
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        sb.append(newline).append(line);
        newline = "\n";
      }
    }

    return sb.toString();
  }

  /**
   * Writes the given text content to the file identified by the given URL.
   */
  private static void write( URL tgt, String text ) throws IOException {
    try (OutputStreamWriter osw = new OutputStreamWriter(
            new FileOutputStream(tgt.getPath()), ENCODING)) {
      osw.write(text);
      osw.flush();
    }
  }


  /**
   * Entry of the {@link #themesComboBox} containing the file name and the display name of a theme.
   */
  private static class ThemeInfo {
    String fileName;
    String displayName;

    ThemeInfo(String displayName, String fileName) {
      this.fileName = fileName;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  /**
   * Differentiates between normal nodes and folder nodes.
   * For folder nodes, the label defaults are adopted from the group node label
   * defaults of the associated {@link IFoldingView}'s view graph.
   */
  private static class MultiplexingLabelDefaults extends LabelDefaults {
    final IFoldingView view;

    MultiplexingLabelDefaults(IFoldingView view) {
      this.view = view;
    }

    @Override
    public ILabelModelParameter getLayoutParameterInstance(ILabelOwner owner) {
      if (isFolderNode(owner)) {
        return getGroupLabelDefaults().getLayoutParameterInstance(owner);
      } else {
        return super.getLayoutParameterInstance(owner);
      }
    }

    @Override
    public ILabelStyle getStyleInstance(ILabelOwner owner) {
      if (isFolderNode(owner)) {
        return getGroupLabelDefaults().getStyleInstance(owner);
      } else {
        return super.getStyleInstance(owner);
      }
    }

    /**
     * Determines if the given label owner is a folder node.
     */
    private boolean isFolderNode(ILabelOwner owner) {
      if (owner instanceof INode) {
        INode node = (INode) owner;
        if (!view.getGraph().isGroupNode(node)) {
          INode masterNode = view.getMasterItem(node);
          if (view.getManager().getMasterGraph().isGroupNode(masterNode)) {
            return true;
          }
        }
      }
      return false;
    }

    /**
     * Returns the group node label defaults of the associated
     * {@link IFoldingView}'s view graph.
     */
    private ILabelDefaults getGroupLabelDefaults() {
      return view.getGraph().getGroupNodeDefaults().getLabelDefaults();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
