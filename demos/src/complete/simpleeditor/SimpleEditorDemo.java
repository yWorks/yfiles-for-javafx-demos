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
package complete.simpleeditor;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.LabelDefaults;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.labelmodels.SmartEdgeLabelModel;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.CanvasPrinter;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GraphOverviewControl;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.WaitInputMode;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import toolkit.BitmapExportHelper;
import toolkit.CommandMenuItem;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

/**
 * Simple demo that hosts a {@link GraphControl}
 * which enables graph editing via the default {@link GraphEditorInputMode}
 * input mode for editing graphs.
 * <p>This demo also supports grouped graphs, i.e., selected nodes can be grouped
 * in so-called group nodes using Ctrl/Command-G, and again be un-grouped using Ctrl/Command-U.
 * To move sets of nodes into and out of group nodes using the mouse, hold down
 * the SHIFT key while dragging.</p>
 * <p>
 * Apart from graph editing, the demo demonstrates various basic features that are already
 * present on GraphControl (either as predefined commands or as simple method calls), e.g.
 * load/save/export.
 * </p>
 * <p>
 * In addition to the GraphControl itself, the demo also shows how to use the GraphOverviewControl.
 </p>
 */
public class SimpleEditorDemo extends DemoApplication {
  private static String[] IMAGE_FILE_EXTENSIONS = {"jpg", "jpeg", "jpe", "png", "bmp"};

  private GridVisualCreator grid;
  private GraphSnapContext graphSnapContext;
  private LabelSnapContext labelSnapContext;
  private static final int GRID_SIZE = 50;
  private static final GridInfo GRID_INFO = new GridInfo(GRID_SIZE);

  public GraphControl graphControl;
  private WaitInputMode waitInputMode;
  public GraphOverviewControl overviewControl;
  public WebView webView;
  public MenuBar menubar;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph is available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    overviewControl.setGraphControl(graphControl);
    overviewControl.setHorizontalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    overviewControl.setVerticalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    WebViewUtils.initHelp(webView, this);
  }

  /**
   * Initializes the graph and the input mode.
   */
  public void onLoaded() {
    // initialize the graph
    initializeGraph();

    // initialize the grid for grid snapping
    initializeGrid();

    // initialize the snap context
    initializeSnapContext();

    // initialize the input mode
    initializeInputModes();

    // initializes the menu that contains the available layout algorithms for this demo
    initializeLayoutMenu();

    // loads the example graph
    loadSampleGraph();

  }

  /**
   * Adds a menu to the menu bar that contains CommandMenuItems. These
   * menu items invoke a specific layouting algorithm.
   */
  private void initializeLayoutMenu() {
    ObservableList<Menu> menus = menubar.getMenus();
    Menu layoutMenu = new Menu("Layout");

    addCommandMenuItem(layoutMenu, "Hierarchic", new HierarchicLayout());
    addCommandMenuItem(layoutMenu, "Organic", new OrganicLayout());
    addCommandMenuItem(layoutMenu, "Orthogonal", new OrthogonalLayout());
    addCommandMenuItem(layoutMenu, "Circular", new CircularLayout());
    addCommandMenuItem(layoutMenu, "Tree", new TreeLayout());
    addCommandMenuItem(layoutMenu, "Balloon", new BalloonLayout());
    layoutMenu.getItems().add(new SeparatorMenuItem());
    addCommandMenuItem(layoutMenu, "Orthogonal Router", new EdgeRouter());
    addCommandMenuItem(layoutMenu, "Organic Router", new OrganicEdgeRouter());

    menus.add(layoutMenu);
  }

  /**
   * Creates and initializes a CommandMenuItem using the given command name and parameter and adds it to the given menu.
   * @param menu The menu to add the CommandMenuItem to.
   * @param commandName The name for the command (will be displayed as text).
   * @param commandParameter The parameter for the command.
   */
  private void addCommandMenuItem(Menu menu, String commandName, Object commandParameter) {
    CommandMenuItem item = new CommandMenuItem();
    item.setText(commandName);
    item.setCommand(RUN_LAYOUT);
    item.setCommandParameter(commandParameter);
    item.setCommandTarget(graphControl);
    menu.getItems().add(item);
  }

  /**
   * Loads a sample graph from GraphML for this demo.
   */
  private void loadSampleGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/example.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
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
   * Calls {@link #createEditorMode()}  and registers
   * the result as the {@link CanvasControl#getInputMode()}.
   */
  private void initializeInputModes() {
    IInputMode geim = createEditorMode();
    graphControl.setInputMode(geim);
  }

  /**
   * Creates the default input mode for the GraphControl,
   * @see GraphEditorInputMode
   * @return a new GraphEditorInputMode instance and configures snapping and orthogonal edge editing
   */
  public IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    mode.setSnapContext(graphSnapContext);
    mode.setLabelSnapContext(labelSnapContext);
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    orthogonalEdgeEditingContext.setEnabled(false);
    mode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    waitInputMode = mode.getWaitInputMode();

    // make bend creation more important than moving of selected edges
    // this has the effect that dragging a selected edge (not its bends)
    // will create a new bend instead of moving all bends
    // This is especially nicer in conjunction with orthogonal
    // edge editing because this creates additional bends every time
    // the edge is moved otherwise
    mode.getCreateBendInputMode().setPriority(mode.getMoveInputMode().getPriority() - 1);

    // enable grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    mode.setGroupingOperationsAllowed(true);


    // add command bindings for 'run layout'
    mode.getKeyboardInputMode().addCommandBinding(RUN_LAYOUT, this::executeLayout, this::canExecuteLayout);
    mode.getKeyboardInputMode().addKeyBinding(new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN), RUN_LAYOUT);

    // add command binding for 'new' and 'print'
    mode.getKeyboardInputMode().addCommandBinding(ICommand.NEW, this::executeNew, this::canExecuteNew);
    mode.getKeyboardInputMode().addCommandBinding(ICommand.PRINT, this::executePrintCommand, this::canExecutePrintCommand);

    // The following line triggers a call to the can-execute-method of each registered action/binding. This is normally
    // done automatically by yFiles via input modes and on specific structural changes. But we want to have the above
    // added actions to be initially in the correct can-execute-state, so we trigger this method manually.
    ICommand.invalidateRequerySuggested();
    return mode;
  }

  /**
   * Initializes the graph instance setting default styles
   */
  public void initializeGraph() {
    //Enable folding
    IFoldingView view = new FoldingManager().createFoldingView();
    IGraph graph = view.getGraph();

    // Enable undoability
    // Get the master graph instance and enable undoability support.
    IGraph masterGraph = view.getManager().getMasterGraph();
    masterGraph.setUndoEngineEnabled(true);

    // configure the group node style.
    //PanelNodeStyle is a nice style especially suited for group nodes
    Color groupNodeColor = Color.web("#CFE2F8FF");
    PanelNodeStyle decoratedStyle = new PanelNodeStyle();
    decoratedStyle.setColor(groupNodeColor);
    decoratedStyle.setLabelInsetsColor(groupNodeColor);
    CollapsibleNodeStyleDecorator groupNodeStyle = new CollapsibleNodeStyleDecorator(decoratedStyle);
    graph.getGroupNodeDefaults().setStyle(groupNodeStyle);

    // Set a different label style and parameter
    DefaultLabelStyle groupNodeLabelStyle = new DefaultLabelStyle();
    groupNodeLabelStyle.setTextAlignment(TextAlignment.RIGHT);
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(groupNodeLabelStyle);
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);

    // Configure Graph defaults
    // set the default node style
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.web("#FFA500")); // we use a slightly darker orange
    graph.getNodeDefaults().setStyle(nodeStyle);
    DefaultLabelStyle nodeLabelStyle = new DefaultLabelStyle();
    nodeLabelStyle.setTextAlignment(TextAlignment.LEFT);
    // use the same label defaults for folder nodes and group nodes but
    // different label defaults for normal nodes
    graph.getNodeDefaults().setLabelDefaults(new MultiplexingLabelDefaults(view));
    graph.getNodeDefaults().getLabelDefaults().setStyle(nodeLabelStyle);
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(new FreeNodeLabelModel().createDefaultParameter());
    graph.getEdgeDefaults().getLabelDefaults().setStyle(nodeLabelStyle);
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(new SmartEdgeLabelModel().createDefaultParameter());

    graphControl.setGraph(graph);
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

  /**
   * Delegates to fitGraphBounds of GraphControl
   */
  public void fitGraphBounds(){
    graphControl.fitGraphBounds();
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
   * Toggles the orthogonal editing behavior. When this is enabled, edges are created and updated orthogonal.
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
    if (event.getSource() instanceof ToggleButton && graphControl.getInputMode() instanceof GraphEditorInputMode) {
      ToggleButton selectionModeToggleButton = (ToggleButton) event.getSource();
      boolean selected = selectionModeToggleButton.isSelected();
      GraphEditorInputMode geim = (GraphEditorInputMode) graphControl.getInputMode();
      geim.getLassoSelectionInputMode().setEnabled(selected);
    }
  }

  /**
   * A {@link ICommand} that is usable from FXML and layouts the given graph.
   */
  private static final ICommand RUN_LAYOUT = ICommand.createCommand("RunLayout");

  /**
   * Determines whether the {@link #RUN_LAYOUT} can be executed.
   */
  private boolean canExecuteLayout(ICommand command, Object param, Object sender) {
    // if a layout algorithm is currently running, no other layout algorithm shall be executable for two reasons:
    // - the result of the current layout run shall be presented before executing a new layout
    // - layout algorithms are not thread safe, so calling applyLayout on a layout algorithm that currently calculates
    //   a layout may result in errors
    if (param instanceof ILayoutAlgorithm && !waitInputMode.isWaiting()) {
      // don't allow layouts for empty graphs
      IGraph graph = graphControl.getGraph();
      return graph != null && graph.getNodes().size() != 0;
    } else {
      return false;
    }
  }

  /**
   * Handles the {@link #RUN_LAYOUT}.
   */
  private boolean executeLayout(ICommand command, Object parameter, Object sender) {
    if (parameter != null && parameter instanceof ILayoutAlgorithm) {
      ILayoutAlgorithm layout = (ILayoutAlgorithm) parameter;
      graphControl.morphLayout(layout, Duration.ofMillis(500));
      return true;
    }
    return false;
  }

  /**
   * Helper that determines whether the {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNew(ICommand command, Object param, Object sender) {
    // don't allow layouts for empty graphs
    IGraph graph = graphControl.getGraph();
    return !waitInputMode.isWaiting() && graph != null && graph.getNodes().size() != 0;
  }

  /**
   * Handler for the {@link ICommand#NEW}
   */
  private boolean executeNew(ICommand command, Object param, Object sender) {
    graphControl.getGraph().clear();

    // update the can-execute-states of the commands since this is not
    // triggered by clearing the graph programmatically
    ICommand.invalidateRequerySuggested();

    return true;
  }

  /**
   * Exports the image to bitmap via GraphControl.
   */
  public void exportImage() {
    graphControl.updateContentRect(new InsetsD(5, 5, 5, 5));
    FileChooser dialog = new FileChooser();
    dialog.setTitle("Export image to file");
    final ObservableList<FileChooser.ExtensionFilter> extensionFilters = dialog.getExtensionFilters();
    extensionFilters.add(new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg", "*.jpe"));
    extensionFilters.add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
    extensionFilters.add(new FileChooser.ExtensionFilter("Bitmap Files", "*.bmp"));
    File file = dialog.showSaveDialog(graphControl.getScene().getWindow());
    if (file != null) {
      // If file extension is missing, add ".png".
      String filePath = file.getPath();
      boolean hasExtension = false;
      for (String extension : IMAGE_FILE_EXTENSIONS) {
        if (filePath.toLowerCase().endsWith("." + extension)) {
          hasExtension = true;
          break;
        }
      }
      if (!hasExtension) {
        filePath = filePath + ".png";
      }
      try {
        BitmapExportHelper.exportToBitmap(graphControl, filePath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Handler for the {@link ICommand#PRINT} that initiates the printing process.
   */
  private boolean executePrintCommand(ICommand command, Object param, Object sender) {
    CanvasPrinter canvasPrinter = new CanvasPrinter(graphControl);
    PrinterJob job = PrinterJob.createPrinterJob();
    if (job == null) {
      new Alert(Alert.AlertType.WARNING, "Could not print.", ButtonType.CLOSE).show();
      return false;
    }
    boolean success = canvasPrinter.print(job, true);
    if (success) {
      job.endJob();
    }
    return success;
  }

  /**
   * Helper that determines whether the {@link ICommand#PRINT} can be executed.
   */
  private boolean canExecutePrintCommand(ICommand command, Object param, Object sender) {
    // don't allow printing empty graphs
    IGraph graph = graphControl.getGraph();
    return !waitInputMode.isWaiting() && graph != null && graph.getNodes().size() != 0;
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
        IGraph masterGraph = view.getManager().getMasterGraph();
        if (view.getGraph().contains(node)) {
          if (!view.getGraph().isGroupNode(node)) {
            INode masterNode = view.getMasterItem(node);
            if (masterGraph.isGroupNode(masterNode)) {
              return true;
            }
          }
        } else if (masterGraph.contains(node)) {
          return masterGraph.isGroupNode(node);
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
