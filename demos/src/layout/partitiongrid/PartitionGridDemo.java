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
package layout.partitiongrid;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.PartitionGridData;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import com.yworks.yfiles.view.input.WaitInputMode;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import layout.LayoutFinishedListeners;
import toolkit.CommandButton;
import toolkit.DemoApplication;
import toolkit.DemoGroupNodeStyle;
import toolkit.DemoNodeStyle;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * The PartitionGrid application shows how a {@link com.yworks.yfiles.layout.PartitionGrid} can be used in layout
 * calculations to restrict the node positions to grid cells.
 * <p>
 * The assignment of a node to a grid column and row is visualized by the background and border color of its style
 * and can be changed via the context menu of the node.
 * </p>
 * <p>
 * A background visual using the node colors shows the PartitionGrid bounds calculated by the last layout.
 * </p>
 * <p>
 * The layout itself is triggered via {@link com.yworks.yfiles.view.input.ICommand}s that finally call the method
 * {@link #executeLayout(ICommand, Object, Object)}  executeLayout}. The configuration of the
 * {@link com.yworks.yfiles.layout.PartitionGrid} is delegated to the class {@link PartitionGridConfigurator}.
 * </p>
 */
public class PartitionGridDemo extends DemoApplication {

  /**
   * A {@link ICommand} that is usable from FXML to layout the given graph hierarchically.
   * <p>
   * The command can be triggered with the keyboard short cut Ctrl/ICommand+H
   * </p>
   */
  public static final ICommand RUN_HIERARCHIC_LAYOUT = ICommand.createCommand("RunHierarchicLayout");

  /**
   * A {@link ICommand} that is usable from FXML to layout the given graph organically.
   * <p>
   * The command can be triggered with the keyboard short cut Ctrl/ICommand+O
   * </p>
   */
  public static final ICommand RUN_ORGANIC_LAYOUT = ICommand.createCommand("RunOrganicLayout");

  public GraphControl graphControl;
  public WebView webView;
  public CheckBox fixOrderBox;
  public Slider minWidthSlider;
  public CheckBox stretchGroupBox;
  public CommandButton hierarchicLayoutCommandBtn;
  public CommandButton organicLayoutCommandBtn;

  private WaitInputMode waitInputMode;
  private Color defaultColor = Color.valueOf("#454545");
  private Pen defaultPen = new Pen(defaultColor, 1);
  private List<Color> columnColors;
  private List<Pen> rowPens;

  private PartitionGridVisualCreator partitionGridVisualCreator;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph is available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    WebViewUtils.initHelp(webView, this);
  }

  /**
   * Handles the {@link #RUN_HIERARCHIC_LAYOUT} and {@link #RUN_ORGANIC_LAYOUT} commands.
   * <p>
   * The layout calculation is triggered by a {@link LayoutExecutor} and {@link PartitionGridData} are used as
   * {@link LayoutExecutor#setLayoutData(LayoutData) LayoutData} as well for the hierarchic as for the organic layout.
   * It would also be possible to use the {@link com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData HierarchicLayoutData}
   * or {@link com.yworks.yfiles.layout.organic.OrganicLayoutData OrganicLayoutData} and configure their
   * {@link HierarchicLayoutData#getPartitionGridData() PartitionGridData properties} instead.
   * </p>
   */
  private boolean executeLayout(ICommand command, Object parameter, Object sender) {
    //check if actually a layout was handed over
    if (parameter != null && parameter instanceof ILayoutAlgorithm) {
      ILayoutAlgorithm layout = (ILayoutAlgorithm) parameter;

      // create the PartitionGridData
      PartitionGridConfigurator configurator = new PartitionGridConfigurator(graphControl.getGraph(), this::getNodeGridData);
      PartitionGridData partitionGridData = configurator.createPartitionGridData(rowPens.size(), columnColors.size(),
          fixOrderBox.isSelected(), minWidthSlider.getValue(), stretchGroupBox.isSelected());

      // set the PartitionGrid on the partitionGridVisualCreator so it can use the new layout of the rows/columns
      // for its animation
      partitionGridVisualCreator.setGrid(partitionGridData.getGrid());
      // now layout the graph using the provided layout algoritm and animate the result
      LayoutExecutor executor = new LayoutExecutor(graphControl, layout) {
        @Override
        protected IAnimation createMorphAnimation() {
          IAnimation graphMorphAnimation = super.createMorphAnimation();
          // we want to animate the graph itself as well as the partition
          // grid visualization so we use a parallel animation:
          return IAnimation.createParallelAnimation(graphMorphAnimation, partitionGridVisualCreator);
        }
      };
      executor.setDuration(Duration.ofMillis(500));
      executor.setLayoutData(partitionGridData);
      executor.setViewportAnimationEnabled(true);
      executor.addLayoutFinishedListener(LayoutFinishedListeners::handleErrors);
      executor.start();
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #RUN_HIERARCHIC_LAYOUT} can be executed.
   */
  private boolean canExecuteHierarchicLayout(ICommand command, Object parameter, Object sender) {
    return canExecuteAnyLayout();
  }

  /**
   * Determines whether the {@link #RUN_ORGANIC_LAYOUT} can be executed.
   */
  private boolean canExecuteOrganicLayout(ICommand command, Object parameter, Object sender) {
    if (!canExecuteAnyLayout()) {
      return false;
    }

    if (stretchGroupBox.isSelected()) {
      return true;
    }

    // With "stretch group nodes" turned off, the organic layout algorithm does
    // not support group nodes that contain child nodes assigned to different
    // rows or columns. In this case the organic layout button shall be
    // disabled.
    IGraph graph = graphControl.getGraph();
    for (INode node : graph.getNodes()) {
      if (graph.isGroupNode(node)) {
        // for each group node...
        int rowIndex = -1;
        int columnIndex = -1;
        boolean firstValidIndices = true;
        for (INode child : graph.getChildren(node)) {
          // ... check the NodeGridDatas of its children...
          NodeGridData nodeGridData = getNodeGridData(child);
          if (!nodeGridData.hasValidIndices()) {
            continue;
          }
          // ... and if one has a valid index, check if it has a different row/column index then the other nodes
          if (firstValidIndices) {
            rowIndex = nodeGridData.getRowIndex();
            columnIndex = nodeGridData.getColumnIndex();
            firstValidIndices = false;
          } else {
            if (rowIndex != nodeGridData.getRowIndex() || columnIndex != nodeGridData.getColumnIndex()) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  /**
   * Determines whether any layout can be executed.
   */
  private boolean canExecuteAnyLayout() {
    // if a layout algorithm is currently running, no other layout algorithm shall be executable for two reasons:
    // - the result of the current layout run shall be presented before executing a new layout
    // - layout algorithms are not thread safe, so calling applyLayout on a layout algorithm that currently calculates
    //   a layout may result in errors
    if (!waitInputMode.isWaiting()) {
      // don't allow layouts for empty graphs
      IGraph graph = graphControl.getGraph();
      return graph != null && graph.getNodes().size() != 0;
    } else {
      return false;
    }
  }

  /**
   * Triggers reevaluation of the executable state of the demo's custom
   * {@link #RUN_HIERARCHIC_LAYOUT} and {@link #RUN_ORGANIC_LAYOUT} commands.
   */
  public void onStretchChanged(ActionEvent event) {
    ICommand.invalidateRequerySuggested();
  }

  /**
   * Initializes the demo and load a sample graph.
   */
  public void onLoaded() {
    // initializes the colors and pens defining the columns and rows a node shall be assigned to.
    initializeColors();

    // initializes the visual creator for the partition grid and adds it to the background of the GraphControl.
    initializePartitionGridVisualization();

    // initialize the default styles for normal nodes and group nodes
    initializeNodeDefaults();

    // creates the default input mode for the GraphControl and registers it as the {@link CanvasControl#getInputMode()}.
    initializeInputModes();

    // loads a sample graph from GraphML for this demo.
    loadSampleGraph();
  }

  /**
   * Initializes the colors and pens defining the columns and rows a node shall be assigned to.
   */
  private void initializeColors() {
    columnColors = new ArrayList<>(4);
    columnColors.add(Color.valueOf("#ffaf00"));
    columnColors.add(Color.valueOf("#ff8800"));
    columnColors.add(Color.valueOf("#5bafe1"));
    columnColors.add(Color.valueOf("#236CB6"));

    rowPens = new ArrayList<>(3);
    rowPens.add(new Pen(Color.DARKGREEN, 2));
    rowPens.add(new Pen(Color.WHITE, 1));
    rowPens.add(new Pen(Color.DARKRED, 2));
  }

  /**
   * Initializes the visual creator for the partition grid and adds it to the background of the GraphControl.
   */
  private void initializePartitionGridVisualization() {
    partitionGridVisualCreator = new PartitionGridVisualCreator(columnColors, rowPens);
    graphControl.getBackgroundGroup().addChild(partitionGridVisualCreator, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  /**
   * Initializes the default styles for normal nodes and group nodes.
   */
  private void initializeNodeDefaults() {
    IGraph graph = graphControl.getGraph();

    DemoNodeStyle nodeStyle = new DemoNodeStyle();
    nodeStyle.setBackground(defaultColor);
    nodeStyle.setPen(defaultPen);
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getNodeDefaults().setStyleInstanceSharingEnabled(false);

    DemoGroupNodeStyle groupNodeStyle = new DemoGroupNodeStyle();
    groupNodeStyle.setBackgroundColor(Color.rgb(255, 255, 255, 0.4));
    groupNodeStyle.setBorderColor(Color.rgb(102, 102, 102, 0.4));
    graph.getGroupNodeDefaults().setStyle(groupNodeStyle);
    graph.getGroupNodeDefaults().setStyleInstanceSharingEnabled(false);
  }

  /**
   * Creates the default input mode for the GraphControl and registers it as the {@link CanvasControl#getInputMode()}.
   */
  private void initializeInputModes() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    waitInputMode = geim.getWaitInputMode();

    // enable grouping operations such as grouping selected nodes moving nodes into group nodes
    geim.setGroupingOperationsAllowed(true);

    // add our context menu creator
    geim.addPopulateItemContextMenuListener(this::createContextMenus);
    graphControl.setInputMode(geim);

    KeyboardInputMode kim = geim.getKeyboardInputMode();

    // add command bindings for the layout commands so the corresponding (can)executeLayout methods are used
    kim.addCommandBinding(RUN_HIERARCHIC_LAYOUT, this::executeLayout, this::canExecuteHierarchicLayout);
    kim.addCommandBinding(RUN_ORGANIC_LAYOUT, this::executeLayout, this::canExecuteOrganicLayout);

    // add key bindings for the layout commands so valid parameters are used when triggering the commands via keyboard short cuts
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setMinimumNodeDistance(50);
    organicLayout.setPreferredEdgeLength(100);
    kim.addKeyBinding(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN), RUN_HIERARCHIC_LAYOUT, new HierarchicLayout());
    kim.addKeyBinding(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), RUN_ORGANIC_LAYOUT, organicLayout);

    // The following line triggers a call to the can-execute-method of each registered action/binding. This is normally
    // done automatically by yFiles via input modes and on specific structural changes. But we want to have the above
    // added actions to be initially in the correct can-execute-state, so we trigger this method manually.
    ICommand.invalidateRequerySuggested();
  }

  /**
   * Fills the context menu for nodes.
   */
  private void createContextMenus(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
    //clicked item already handled or not an INode -> return
    if (args.isHandled() || !(args.getItem() instanceof INode)) {
      return;
    }

    //get clicked item
    INode node = (INode) args.getItem();

    if (!(node.getStyle() instanceof DemoNodeStyle)) {
      // for group nodes we don't provide a context menu
      return;
    }

    //create list for all contextMenuItems that should be shown
    ObservableList<MenuItem> menuItems = ((ContextMenu) args.getMenu()).getItems();

    //get the color of the clicked node
    DemoNodeStyle nodeStyle = (DemoNodeStyle) node.getStyle();
    Color background = nodeStyle.getBackground();

    //the node is unassigned if the color equals the default color (black)
    boolean isUnassignedNode = defaultColor.equals(background);

    if (!isUnassignedNode) {
      // this node currently has grid restrictions so we add an entry to switch to a 'black' node with no grid restrictions
      Rectangle colorRect = new Rectangle(20, 20, defaultColor);
      RadioMenuItem colorOption = new RadioMenuItem("Remove grid restrictions", colorRect);
      colorOption.setOnAction(event -> {
        nodeStyle.setBackground(defaultColor);
        nodeStyle.setPen(defaultPen);
        updateNodeTag(node);
        graphControl.invalidate();
        ICommand.invalidateRequerySuggested();
      });
      menuItems.add(colorOption);
      menuItems.add(new SeparatorMenuItem());
    }

    ToggleGroup columnColorGroup = new ToggleGroup();
    for (Color newColor : columnColors) {
      // we add an entry for each valid column color
      Rectangle colorRect = new Rectangle(20, 20, newColor);
      RadioMenuItem colorOption = new RadioMenuItem("Switch column", colorRect);

      //set this menuItem as selected if its color equals the clicked color
      colorOption.setSelected(newColor.equals(background));
      colorOption.setToggleGroup(columnColorGroup);
      colorOption.setOnAction(event -> {
        nodeStyle.setBackground(newColor);
        updateNodeTag(node);
        graphControl.invalidate();
        ICommand.invalidateRequerySuggested();
      });
      menuItems.add(colorOption);
    }

    if (!isUnassignedNode) {
      // this node currently has column restrictions so we add an entry for each valid row pen
      menuItems.add(new SeparatorMenuItem());
      Pen border = nodeStyle.getPen();
      ToggleGroup rowPenGroup = new ToggleGroup();

      for (Pen newPen : rowPens) {
        Rectangle penRect = new Rectangle(20, 20, Color.TRANSPARENT);
        penRect.setStroke(newPen.getPaint());
        RadioMenuItem penOption = new RadioMenuItem("Switch row", penRect);

        //set this menuItem as selected if its color equals the clicked color
        penOption.setSelected(newPen.getPaint().equals(border.getPaint()));
        penOption.setToggleGroup(rowPenGroup);
        penOption.setOnAction(event -> {
          nodeStyle.setPen(newPen);
          updateNodeTag(node);
          graphControl.invalidate();
          ICommand.invalidateRequerySuggested();
        });
        menuItems.add(penOption);
      }
    }
  }

  /**
   * Loads a sample graph from GraphML for this demo.
   */
  private void loadSampleGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/example.graphml"));
      RUN_HIERARCHIC_LAYOUT.execute(new HierarchicLayout(), graphControl);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // region Creating and updating NodeGridData

  public NodeGridData getNodeGridData(INode node) {
    if (!(node.getTag() instanceof NodeGridData)) {
      updateNodeTag(node);
    }
    return (NodeGridData) node.getTag();
  }

  private void updateNodeTag(INode node) {
    // calculate the new row and column index
    int newColumnIndex = -1;
    int newRowIndex = -1;
    if (node.getStyle() instanceof DemoNodeStyle) {
      DemoNodeStyle style = (DemoNodeStyle) node.getStyle();
      newColumnIndex = getColumnIndex(style.getBackground());
      newRowIndex = getRowIndex(style.getPen());
    }
    // update or create the NodeGridData in the node tag
    if (node.getTag() instanceof NodeGridData) {
      NodeGridData nodeGridData = (NodeGridData) node.getTag();
      nodeGridData.setColumnIndex(newColumnIndex);
      nodeGridData.setRowIndex(newRowIndex);
    } else {
      node.setTag(new NodeGridData(newRowIndex, newColumnIndex));
    }
  }

  private int getColumnIndex(Color color) {
    int index = -1;
    for (int i = 0; i < columnColors.size(); i++) {
      if (columnColors.get(i).equals(color)) {
        return i;
      }
    }
    return index;
  }

  private int getRowIndex(Pen pen) {
    int index = 1;
    for (int i = 0; i < rowPens.size(); i++) {
      if (rowPens.get(i).getPaint().equals(pen.getPaint())) {
        return i;
      }
    }
    return index;
  }

  // endregion

  public static void main(String[] args) {
    launch(args);
  }
}
