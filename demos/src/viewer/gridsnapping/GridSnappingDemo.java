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
package viewer.gridsnapping;

import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridStyle;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.LabelSnapContext;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

/**
 * Demonstrates how to enable grid snapping functionality for graph elements.
 */
public class GridSnappingDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;

  // determines the snapping behavior of all items except labels
  private GraphSnapContext graphSnapContext;
  // determines the snapping behavior of labels
  private LabelSnapContext labelSnapContext;
  // visualizes the grid
  private GridVisualCreator grid;

  // control to change the snapping behavior
  @FXML private ComboBox<NamedEntry> gridSnapTypeComboBox;
  // control to change the grid visibility
  @FXML private ToggleButton gridToggleButton;

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // includes the help
    WebViewUtils.initHelp(helpView, this);

    // configure the interaction
    GraphEditorInputMode geim = initializeInputMode();

    // enable grid snapping
    initializeSnapping(geim);

    // create the grid visualization
    initializeGrid();

    // configure the combobox to select a grid snap type and
    // the button to toggle the grid visibility
    initializeSnapControls();

    // load a sample graph
    loadGraph();

    // enable undoability
    enableUndo();
  }

  /**
   * Initializes snapping for labels and other graph items. The default snapping behavior can easily be enabled by
   * setting the according snap context. Those snap contexts provide many options to fine tune their behavior, in
   * this case we use it to make the items snap only to the given grid but not to other graph items. Please see the
   * documentation of {@link GraphSnapContext} and {@link LabelSnapContext} for more information.
   * @param geim The input mode that should use snapping when editing the graph.
   */
  private void initializeSnapping(GraphEditorInputMode geim) {
    // enables snapping all items except labels
    // disable some of the default snapping behavior such that the graph items only snap to the grid and nowhere else
    graphSnapContext = new GraphSnapContext();
    graphSnapContext.setSnappingBendAdjacentSegmentsEnabled(false);
    graphSnapContext.setSnappingBendsToSnapLinesEnabled(false);
    graphSnapContext.setSnappingNodesToSnapLinesEnabled(false);
    graphSnapContext.setSnappingOrthogonalMovementEnabled(false);
    graphSnapContext.setSnappingPortAdjacentSegmentsEnabled(false);
    graphSnapContext.setSnappingSegmentsToSnapLinesEnabled(false);
    geim.setSnapContext(graphSnapContext);

    // enables snapping for labels
    labelSnapContext = new LabelSnapContext();
    geim.setLabelSnapContext(labelSnapContext);
  }

  /**
   * Initializes the grid snapping types combobox and the GridInfo which is the
   * actual grid to which items can snap.
   */
  private void initializeGrid() {
    // initializes GridInfo which holds the basic information about the grid
    // sets horizontal and vertical space between grid lines
    GridInfo gridInfo = new GridInfo();
    gridInfo.setHorizontalSpacing(50);
    gridInfo.setVerticalSpacing(50);

    // creates grid visualization and adds it to GraphControl
    grid = new GridVisualCreator(gridInfo);
    grid.setGridStyle(GridStyle.LINES);
    graphControl.getBackgroundGroup().addChild(grid);

    // sets constraint provider to make nodes and bends snap to grid
    graphSnapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
    graphSnapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
  }

  /**
   * Sets the behavior of the grid snapping.
   */
  private void setGridSnapType(GridSnapTypes gridSnapType) {
    if (graphSnapContext != null) {
      graphSnapContext.setGridSnapType(gridSnapType);
    }
  }

  /**
   * Sets the visualization style of the grid.
   */
  private void setGridStyle(GridStyle gridStyle) {
    grid.setGridStyle(gridStyle);
    graphControl.invalidate();
  }

  /**
   * Shows or hides the grid visualization.
   */
  private void showGrid(boolean enabled) {
    grid.setVisible(enabled);
    graphControl.invalidate();
  }

  /**
   * Initializes the input mode for the demo.
   */
  private GraphEditorInputMode initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setGroupingOperationsAllowed(true);
    graphControl.setInputMode(geim);
    return geim;
  }

  /**
   * Fills the combobox with the snapping types. Determines what happens if the user selects another snapping type
   * or toggles the grid visibility.
   */
  private void initializeSnapControls() {
    // populate the combobox with the possible GridSnapTypes
    gridSnapTypeComboBox.getItems().addAll(
        new NamedEntry("None", GridSnapTypes.NONE, GridStyle.LINES),
        new NamedEntry("Horizontal Lines", GridSnapTypes.HORIZONTAL_LINES, GridStyle.HORIZONTAL_LINES),
        new NamedEntry("Vertical Lines", GridSnapTypes.VERTICAL_LINES, GridStyle.VERTICAL_LINES),
        new NamedEntry("Lines", GridSnapTypes.LINES, GridStyle.LINES),
        new NamedEntry("Points", GridSnapTypes.GRID_POINTS, GridStyle.DOTS),
        new NamedEntry("All", GridSnapTypes.ALL, GridStyle.LINES)
    );

    // update the snapping type and the grid style when the selected item changes
    gridSnapTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      setGridSnapType(newValue.gridSnapTypes);
      setGridStyle(newValue.gridStyle);
    });

    // show or hide the grid visualization
    gridToggleButton.selectedProperty().addListener((observable, oldValue, newValue) ->
      showGrid(newValue));

    // sets the initial values
    gridSnapTypeComboBox.getSelectionModel().select(3);
    gridToggleButton.setSelected(true);
  }

  /**
   * Name-value struct for combo box entries.
   */
  private static class NamedEntry {
    final String displayName;
    final GridSnapTypes gridSnapTypes;
    final GridStyle gridStyle;

    NamedEntry(String displayName, GridSnapTypes gridSnapTypes, GridStyle gridStyle) {
      this.displayName = displayName;
      this.gridSnapTypes = gridSnapTypes;
      this.gridStyle = gridStyle;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  /**
   * Loads an initial sample graph.
   */
  private void loadGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Enables undo functionality.
   */
  private void enableUndo() {
    graphControl.getGraph().setUndoEngineEnabled(true);
  }

  /**
   * Adjusts the view by the first start of the demo
   */
  @Override
  protected void onLoaded() {
    super.onLoaded();
    graphControl.fitGraphBounds();
  }

  public static void main( String[] args ) {
    launch(args);
  }
}
