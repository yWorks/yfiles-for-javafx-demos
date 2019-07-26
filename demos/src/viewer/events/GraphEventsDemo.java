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
package viewer.events;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.BendEventArgs;
import com.yworks.yfiles.graph.EdgeEventArgs;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphClipboard;
import com.yworks.yfiles.graph.GraphCopier;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IBendLocationChangedHandler;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.INodeLayoutChangedHandler;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.ItemChangedEventArgs;
import com.yworks.yfiles.graph.ItemCopiedEventArgs;
import com.yworks.yfiles.graph.LabelEventArgs;
import com.yworks.yfiles.graph.LookupDecorator;
import com.yworks.yfiles.graph.NodeEventArgs;
import com.yworks.yfiles.graph.PortEventArgs;
import com.yworks.yfiles.graph.UndoEngine;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.utils.PropertyChangedEventArgs;
import com.yworks.yfiles.view.CanvasEvent;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ItemSelectionChangedEventArgs;
import com.yworks.yfiles.view.Mouse2DEvent;
import com.yworks.yfiles.view.RenderContextEvent;
import com.yworks.yfiles.view.Touch2DEvent;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.HandleInputMode;
import com.yworks.yfiles.view.input.HoveredItemChangedEventArgs;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.InputModeEventArgs;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.ItemTappedEventArgs;
import com.yworks.yfiles.view.input.LabelEditingEventArgs;
import com.yworks.yfiles.view.input.LabelTextValidatingEventArgs;
import com.yworks.yfiles.view.input.MoveInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import com.yworks.yfiles.view.input.PopulateMenuEventArgs;
import com.yworks.yfiles.view.input.QueryItemToolTipEventArgs;
import com.yworks.yfiles.view.input.SelectionEventArgs;
import com.yworks.yfiles.view.input.TapEventArgs;
import com.yworks.yfiles.view.input.TextEventArgs;
import com.yworks.yfiles.view.input.ToolTipQueryEventArgs;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Demonstrates the various events provided by the {@link IGraph},
 * the {@link GraphControl} and the input modes.
 */
public class GraphEventsDemo extends DemoApplication {

  private GraphEditorInputMode editorMode;
  private FoldingManager manager;
  private GraphViewerInputMode viewerMode;

  // components of the application, defined in FXML
  public GraphControl graphControl;
  public WebView helpView;
  public CheckBox logInputModeEvents;
  public CheckBox logNavigationModeEvents;
  public CheckBox logClickModeEvents;
  public CheckBox logTapModeEvents;
  public CheckBox logMoveModeEvents;
  public CheckBox logMoveViewportModeEvents;
  public CheckBox logHandleModeEvents;
  public CheckBox logMouseHoverModeEvents;
  public CheckBox logTextEditorModeEvents;
  public CheckBox logContextMenuModeEvents;
  public CheckBox logCreateBendModeEvents;
  public CheckBox logCreateEdgeModeEvents;
  public CheckBox logItemHoverModeEvents;
  public CheckBox logMoveLabelModeEvents;
  public CheckBox logClipboardEvents;
  public CheckBox logUndoEvents;
  public CheckBox logClipboardCopierEvents;
  public CheckBox logMouseEvents;
  public CheckBox logTouchEvents;
  public CheckBox logKeyEvents;
  public CheckBox logSelectionEvents;
  public CheckBox logViewportEvents;
  public CheckBox logRenderEvents;
  public CheckBox logGraphControlEvents;
  public CheckBox logNodeEvents;
  public CheckBox logEdgeEvents;
  public CheckBox logLabelEvents;
  public CheckBox logPortEvents;
  public CheckBox logBendEvents;
  public CheckBox logNodeBoundsEvents;
  public CheckBox logGraphRenderEvents;
  public CheckBox logAllInputEvents;
  public CheckBox logAllGraphControlEvents;
  public CheckBox logAllGraphEvents;
  public ToggleButton toggleEditingButton;
  public CheckBox groupEvents;
  public ListView<ILogEntry> eventList;

  public void onLoaded() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);

    // initialize the yfiles functionality (editor and viewer stuff)
    enableFolding();
    initializeGraph();
    initializeInputModes();
    setupToolTips();
    setupContextMenu();
    enableUndo();

    // enable file IO so we can load a sample graph
    graphControl.setFileIOEnabled(true);

    // load sample graph
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // move the viewport so the graph is centered
    graphControl.fitGraphBounds();

    // initialize the list in the event log
    eventList.setCellFactory(listview -> new MessageListCell<>());
    // set the list of log entries as the list for the list view.
    eventList.setItems(entries);

    // enable some buttons per default
    logInputModeEvents.setSelected(true);
    toggleEditingButton.setSelected(true);
  }


  private void enableFolding() {
    IGraph graph = graphControl.getGraph();

    // enabled changing ports
    LookupDecorator decorator = graph.getDecorator().getEdgeDecorator().getEdgeReconnectionPortCandidateProviderDecorator();
    decorator.setImplementation(IEdgeReconnectionPortCandidateProvider.ALL_NODE_CANDIDATES);

    manager = new FoldingManager(graph);
    graphControl.setGraph(manager.createFoldingView().getGraph());
  }

  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);

    PanelNodeStyle groupStyle = new PanelNodeStyle();
    groupStyle.setColor(Color.rgb(214, 229, 248, 1));
    groupStyle.setLabelInsetsColor(Color.rgb(214, 229, 248, 1));
    groupStyle.setInsets(new InsetsD(18, 5, 5, 5));
    INodeDefaults groupNodeDefaults = graph.getGroupNodeDefaults();
    groupNodeDefaults.setStyle(new CollapsibleNodeStyleDecorator(groupStyle));
    groupNodeDefaults.getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setTextAlignment(TextAlignment.RIGHT);
    groupNodeDefaults.getLabelDefaults().setStyle(labelStyle);
  }

  private void initializeInputModes() {
    editorMode = new GraphEditorInputMode();
    OrthogonalEdgeEditingContext oec = new OrthogonalEdgeEditingContext();
    oec.setEnabled(false);
    oec.setPortMovingEnabled(true);
    editorMode.setOrthogonalEdgeEditingContext(oec);
    editorMode.setGroupingOperationsAllowed(true);
    editorMode.getItemHoverInputMode().setHoverItems(GraphItemTypes.ALL);

    viewerMode = new GraphViewerInputMode();
    viewerMode.getItemHoverInputMode().setHoverItems(GraphItemTypes.ALL);

    // add command binding for 'new'
    editorMode.getKeyboardInputMode().addCommandBinding(ICommand.NEW, this::executeNew, this::canExecuteNew);

    graphControl.setInputMode(editorMode);
  }

  private void setupToolTips() {
    editorMode.setToolTipItems(GraphItemTypes.NODE);
    editorMode.addQueryItemToolTipListener((sender, args) -> {
      args.setToolTip(new Tooltip("ToolTip for " + args.getItem()));
      args.setHandled(true);
    });

    viewerMode.setToolTipItems(GraphItemTypes.NODE);
    viewerMode.addQueryItemToolTipListener((sender, args) -> {
      args.setToolTip(new Tooltip("ToolTip for " + args.getItem()));
      args.setHandled(true);
    });
  }

  private void setupContextMenu() {
    editorMode.setContextMenuItems(GraphItemTypes.NODE);
    editorMode.addPopulateItemContextMenuListener((sender, args) -> {
      ((ContextMenu) args.getMenu()).getItems().add(new MenuItem("Dummy Item"));
      args.setHandled(true);
    });

    viewerMode.setContextMenuItems(GraphItemTypes.NODE);
    viewerMode.addPopulateItemContextMenuListener((sender, args) -> {
      ((ContextMenu) args.getMenu()).getItems().add(new MenuItem("Dummy Item"));
      args.setHandled(true);
    });
  }

  private void enableUndo() {
    manager.getMasterGraph().setUndoEngineEnabled(true);
  }

  /**
   * Helper that determines whether the {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNew(ICommand command, Object param, Object sender) {
    // don't allow layouts for empty graphs
    IGraph graph = graphControl.getGraph();
    return graph != null && graph.getNodes().size() != 0;
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




  IEventHandler<ClickEventArgs> geimOnCanvasClicked = (source, args) -> log("GraphEditorInputMode CanvasClicked");

  IEventHandler<TapEventArgs> geimOnCanvasTapped = (source, args) -> log("GraphEditorInputMode CanvasTapped");

  IEventHandler<ItemEventArgs<? extends IModelItem>> geimOnDeletedItem = (source, args) -> log("GraphEditorInputMode DeletedItem");

  IEventHandler<SelectionEventArgs<IModelItem>> geimOnDeletedSelection = (source, args) -> log("GraphEditorInputMode DeletedSelection");

  IEventHandler<SelectionEventArgs<IModelItem>> geimOnDeletingSelection = (source, args) -> log("GraphEditorInputMode DeletingSelection");

  IEventHandler<ItemClickedEventArgs<IModelItem>> geimOnItemClicked = (source, args) -> log("GraphEditorInputMode ItemClicked " + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<ItemClickedEventArgs<IModelItem>> geimOnItemDoubleClicked = (source, args) -> log("GraphEditorInputMode ItemDoubleClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<ItemClickedEventArgs<IModelItem>> geimOnItemLeftClicked = (source, args) -> log("GraphEditorInputMode ItemLeftClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<ItemClickedEventArgs<IModelItem>> geimOnItemLeftDoubleClicked = (source, args) -> log("GraphEditorInputMode ItemLeftDoubleClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<ItemClickedEventArgs<IModelItem>> geimOnItemRightClicked = (source, args) -> log("GraphEditorInputMode ItemRightClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<ItemClickedEventArgs<IModelItem>> geimOnItemRightDoubleClicked = (source, args) -> log("GraphEditorInputMode ItemRightDoubleClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<ItemTappedEventArgs<IModelItem>> geimOnItemTapped = (source, args) -> log("GraphEditorInputMode ItemTapped" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<ItemTappedEventArgs<IModelItem>> geimOnItemDoubleTapped = (source, args) -> log("GraphEditorInputMode ItemDoubleTapped" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<LabelEditingEventArgs> geimOnLabelAdding = (source, args) -> log("GraphEditorInputMode LabelAdding");

  IEventHandler<LabelEventArgs> geimOnLabelAdded = (source, args) -> log("GraphEditorInputMode LabelAdded");

  IEventHandler<LabelEditingEventArgs> geimOnLabelEditing = (source, args) -> log("GraphEditorInputMode LabelEditing");

  IEventHandler<LabelEventArgs> geimOnLabelTextChanged = (source, args) -> log("GraphEditorInputMode LabelTextChanged");

  IEventHandler<LabelEventArgs> geimOnLabelTextEditingStarted = (source, args) -> log("GraphEditorInputMode LabelTextEditingStarted");

  IEventHandler<LabelEventArgs> geimOnLabelTextEditingCanceled = (source, args) -> log("GraphEditorInputMode LabelTextEditingCanceled");

  IEventHandler<SelectionEventArgs<IModelItem>> geimOnMultiSelectionFinished = (source, args) -> log("GraphEditorInputMode MultiSelectionFinished");

  IEventHandler<SelectionEventArgs<IModelItem>> geimOnMultiSelectionStarted = (source, args) -> log("GraphEditorInputMode MultiSelectionStarted");

  IEventHandler<ItemEventArgs<INode>> geimOnNodeCreated = (source, args) -> log("GraphEditorInputMode NodeCreated");

  IEventHandler<NodeEventArgs> geimOnNodeReparented = (source, args) -> log("GraphEditorInputMode NodeReparented");

  IEventHandler<EdgeEventArgs> geimOnEdgePortsChanged = (source, args) -> log("GraphEditorInputMode Edge " + args.getItem() + " Ports Changed from " + args.getSourcePort() + "->" + args.getTargetPort() + " to "  + args.getItem().getSourcePort()+ "->" + args.getItem().getTargetPort());

  IEventHandler<PopulateItemContextMenuEventArgs<IModelItem>> geimOnPopulateItemContextMenu = (source, args) -> log("GraphEditorInputMode PopulateItemContextMenu" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<QueryItemToolTipEventArgs<IModelItem>> geimOnQueryItemToolTip = (source, args) -> log("GraphEditorInputMode QueryItemToolTip" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventHandler<LabelTextValidatingEventArgs> geimOnValidateLabelText = (source, args) -> log("GraphEditorInputMode ValidateLabelText");

  IEventHandler<IEventArgs> geimOnElementsCopied = (source, args) -> log("GraphEditorInputMode ElementsCopied");

  IEventHandler<IEventArgs> geimOnElementsCut = (source, args) -> log("GraphEditorInputMode ElementsCut");

  IEventHandler<IEventArgs> geimOnElementsPasted = (source, args) -> log("GraphEditorInputMode ElementsPasted");




  IEventHandler<ClickEventArgs> gvimOnCanvasClicked = (source, args) -> log("GraphViewerInputMode CanvasClicked");

  IEventHandler<TapEventArgs> gvimOnCanvasTapped = (source, args) -> log("GraphViewerInputMode CanvasTapped");

  IEventHandler<ItemClickedEventArgs<IModelItem>> gvimOnItemClicked = (source, args) -> log("GraphViewerInputMode ItemClicked");

  IEventHandler<ItemClickedEventArgs<IModelItem>> gvimOnItemDoubleClicked = (source, args) -> log("GraphViewerInputMode ItemDoubleClicked");

  IEventHandler<ItemClickedEventArgs<IModelItem>> gvimOnItemLeftClicked = (source, args) -> log("GraphViewerInputMode ItemLeftClicked");

  IEventHandler<ItemClickedEventArgs<IModelItem>> gvimOnItemLeftDoubleClicked = (source, args) -> log("GraphViewerInputMode ItemLeftDoubleClicked");

  IEventHandler<ItemClickedEventArgs<IModelItem>> gvimOnItemRightClicked = (source, args) -> log("GraphViewerInputMode ItemRightClicked");

  IEventHandler<ItemClickedEventArgs<IModelItem>> gvimOnItemRightDoubleClicked = (source, args) -> log("GraphViewerInputMode ItemRightDoubleClicked");

  IEventHandler<ItemTappedEventArgs<IModelItem>> gvimOnItemTapped = (source, args) -> log("GraphViewerInputMode ItemTapped");

  IEventHandler<ItemTappedEventArgs<IModelItem>> gvimOnItemDoubleTapped = (source, args) -> log("GraphViewerInputMode ItemDoubleTapped");

  IEventHandler<SelectionEventArgs<IModelItem>> gvimOnMultiSelectionFinished = (source, args) -> log("GraphViewerInputMode MultiSelectionFinished");

  IEventHandler<SelectionEventArgs<IModelItem>> gvimOnMultiSelectionStarted = (source, args) -> log("GraphViewerInputMode MultiSelectionStarted");

  IEventHandler<PopulateItemContextMenuEventArgs<IModelItem>> gvimOnPopulateItemContextMenu = (source, args) -> log("GraphViewerInputMode PopulateItemContextMenu");

  IEventHandler<QueryItemToolTipEventArgs<IModelItem>> gvimOnQueryItemToolTip = (source, args) -> log("GraphViewerInputMode QueryItemToolTip");

  IEventHandler<IEventArgs> gvimOnElementsCopied = (source, args) -> log("GraphViewerInputMode ElementsCopied");




  IEventHandler<ItemEventArgs<INode>> navigationInputModeOnGroupCollapsed = (source, evt) -> log("NavigationInputMode Group Collapsed: " + evt.getItem(), "GroupCollapsed");

  IEventHandler<ItemEventArgs<INode>> navigationInputModeOnGroupCollapsing = (source, evt) -> log("NavigationInputMode Group Collapsing: " + evt.getItem(), "Group Collapsing");

  IEventHandler<ItemEventArgs<INode>> navigationInputModeOnGroupEntered = (source, evt) -> log("NavigationInputMode Group Entered: " + evt.getItem(), "Group Entered");

  IEventHandler<ItemEventArgs<INode>> navigationInputModeOnGroupEntering = (source, evt) -> log("NavigationInputMode Group Entering: " + evt.getItem(), "Group Entering");

  IEventHandler<ItemEventArgs<INode>> navigationInputModeOnGroupExited = (source, evt) -> log("NavigationInputMode Group Exited: " + evt.getItem(), "Group Exited");

  IEventHandler<ItemEventArgs<INode>> navigationInputModeOnGroupExiting = (source, evt) -> log("NavigationInputMode Group Exiting: " + evt.getItem(), "Group Exiting");

  IEventHandler<ItemEventArgs<INode>> navigationInputModeOnGroupExpanded = (source, evt) -> log("NavigationInputMode Group Expanded: " + evt.getItem(), "Group Expanded");

  IEventHandler<ItemEventArgs<INode>> navigationInputModeOnGroupExpanding = (source, evt) -> log("NavigationInputMode Group Expanding: " + evt.getItem(), "Group Expanding");



  IEventHandler<ClickEventArgs> clickInputModeOnClicked = (source, args) -> log("ClickInputMode Clicked");

  IEventHandler<ClickEventArgs> clickInputModeOnDoubleClicked = (source, args) -> log("ClickInputMode Double Clicked");

  IEventHandler<ClickEventArgs> clickInputModeOnLeftClicked = (source, args) -> log("ClickInputMode Left Clicked");

  IEventHandler<ClickEventArgs> clickInputModeOnLeftDoubleClicked = (source, args) -> log("ClickInputMode Left Double Clicked");

  IEventHandler<ClickEventArgs> clickInputModeOnRightClicked = (source, args) -> log("ClickInputMode Right Clicked");

  IEventHandler<ClickEventArgs> clickInputModeOnRightDoubleClicked = (source, args) -> log("ClickInputMode Right Double Clicked");



  EventHandler<KeyEvent> canvasControlOnKeyPressed = (event) -> log1("CanvasControl KeyPressed");

  EventHandler<KeyEvent> canvasControlOnKeyReleased = (event) -> log1("CanvasControl KeyReleased");

  EventHandler<KeyEvent> canvasControlOnKeyTyped = (event) -> log1("CanvasControl KeyTyped");



  IEventHandler<TapEventArgs> tapInputModeOnDoubleTapped = (source, args) -> log("TapInputMode Double Tapped");

  IEventHandler<TapEventArgs> tapInputModeOnTapped = (source, args) -> log("TapInputMode Tapped");




  IEventHandler<InputModeEventArgs> inputModeOnDragCanceled = (source, args) -> log(source.getClass().getName() + " DragCanceled", "DragCanceled");

  IEventHandler<InputModeEventArgs> inputModeOnDragCanceling = (source, args) -> log(source.getClass().getName() + " DragCanceling", "DragCanceling");

  IEventHandler<InputModeEventArgs> inputModeOnDragFinished = (source, args) -> log(source.getClass().getName() + " DragFinished", "DragFinished");

  IEventHandler<InputModeEventArgs> inputModeOnDragFinishing = (source, args) -> log(source.getClass().getName() + " DragFinishing" + getAffectedItems(source), "DragFinishing");

  IEventHandler<InputModeEventArgs> inputModeOnDragged = (source, args) -> log(source.getClass().getName() + " Dragged", "Dragged");

  IEventHandler<InputModeEventArgs> inputModeOnDragging = (source, args) -> log(source.getClass().getName() + " Dragging", "Dragging");

  IEventHandler<InputModeEventArgs> inputModeOnDragStarted = (source, args) -> log(source.getClass().getName() + " DragStarted" + getAffectedItems(source), "DragStarted");

  private static String getAffectedItems(Object sender) {
    IEnumerable<IModelItem> items = null;

    if (sender instanceof MoveInputMode) {
      MoveInputMode mim = (MoveInputMode)sender;
      items = mim.getAffectedItems();
    }
    if (sender instanceof HandleInputMode) {
      HandleInputMode him = (HandleInputMode)sender;
      items = him.getAffectedItems();
    }
    if (items != null) {
      int nodeCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof INode).count();
      int edgeCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof IEdge).count();
      int bendCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof IBend).count();
      int labelCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof ILabel).count();
      int portCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof IPort).count();
      return String.format(" (%1$d items: %2$d nodes, %3$d bends, %4$d edges, %5$d labels, %6$d ports)", (int) items.stream().count(),
          nodeCount, bendCount, edgeCount, labelCount, portCount);
    } else {
      return "";
    }
  }

  IEventHandler<InputModeEventArgs> inputModeOnDragStarting = (source, args) -> log(source.getClass().getName() + " DragStarting", "DragStarting");



  IEventHandler<ToolTipQueryEventArgs> mouseHoverInputModeOnQueryToolTip = (source, args) -> log("MouseHoverInputMode QueryToolTip");



  IEventHandler<TextEventArgs> textEditorInputModeOnEditingCanceled = (source, args) -> log("TextEditorInputMode Editing Canceled");

  IEventHandler<TextEventArgs> textEditorInputModeOnEditingStarted = (source, args) -> log("TextEditorInputMode Editing Started");

  IEventHandler<TextEventArgs> textEditorInputModeOnTextEdited = (source, args) -> log("TextEditorInputMode Text Edited");



  IEventHandler<PopulateMenuEventArgs> contextMenuInputModeOnPopulateMenu = (source, args) -> log("ContextMenuInputMode Populate Menu");



  IEventHandler<BendEventArgs> createBendInputModeOnBendCreated = (source, args) -> log("CreateBendInputMode Bend Created");



  IEventHandler<EdgeEventArgs> createEdgeInputModeOnEdgeCreated = (source, args) -> log("CreateEdgeInputMode Edge Created");

  IEventHandler<EdgeEventArgs> createEdgeInputModeOnEdgeCreationStarted = (source, args) -> log("CreateEdgeInputMode Edge Creation Started");

  IEventHandler<InputModeEventArgs> createEdgeInputModeOnGestureCanceled = (source, args) -> log("CreateEdgeInputMode Gesture Canceled");

  IEventHandler<InputModeEventArgs> createEdgeInputModeOnGestureCanceling = (source, args) -> log("CreateEdgeInputMode Gesture Canceling");

  IEventHandler<InputModeEventArgs> createEdgeInputModeOnGestureFinished = (source, args) -> log("CreateEdgeInputMode Gesture Finished");

  IEventHandler<InputModeEventArgs> createEdgeInputModeOnGestureFinishing = (source, args) -> log("CreateEdgeInputMode Gesture Finishing");

  IEventHandler<InputModeEventArgs> createEdgeInputModeOnGestureStarted = (source, args) -> log("CreateEdgeInputMode Gesture Started");

  IEventHandler<InputModeEventArgs> createEdgeInputModeOnGestureStarting = (source, args) -> log("CreateEdgeInputMode Gesture Starting");

  IEventHandler<InputModeEventArgs> createEdgeInputModeOnMoved = (source, args) -> log("CreateEdgeInputMode Moved");

  IEventHandler<InputModeEventArgs> createEdgeInputModeOnMoving = (source, args) -> log("CreateEdgeInputMode Moving");

  IEventHandler<ItemEventArgs<IPort>> createEdgeInputModeOnPortAdded = (source, evt) -> log("CreateEdgeInputMode Port Added");

  IEventHandler<ItemEventArgs<IPortCandidate>> createEdgeInputModeOnSourcePortCandidateChanged = (source, evt) -> log("CreateEdgeInputMode Source Port Candidate Changed");

  IEventHandler<ItemEventArgs<IPortCandidate>> createEdgeInputModeOnTargetPortCandidateChanged = (source, evt) -> log("CreateEdgeInputMode Target Port Candidate Changed");



  IEventHandler<HoveredItemChangedEventArgs> itemHoverInputModeOnHoveredItemChanged = (source, args) -> log("HoverInputMode Item changed from " + args.getOldItem() + " to " + (args.getItem() != null ? args.getItem().toString() : "null"), "HoveredItemChanged");



  IEventHandler<ItemCopiedEventArgs<IGraph>> clipboardOnGraphCopiedToClipboard = (source, args) -> log("Graph copied to Clipboard");

  IEventHandler<ItemCopiedEventArgs<INode>> clipboardOnNodeCopiedToClipboard = (source, args) -> log("Node " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<IEdge>> clipboardOnEdgeCopiedToClipboard = (source, args) -> log("Edge " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<IPort>> clipboardOnPortCopiedToClipboard = (source, args) -> log("Port " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<ILabel>> clipboardOnLabelCopiedToClipboard = (source, args) -> log("Label " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<Object>> clipboardOnObjectCopiedToClipboard = (source, args) -> log("Object " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<IGraph>> clipboardOnGraphCopiedFromClipboard = (source, args) -> log("Graph copied from Clipboard");

  IEventHandler<ItemCopiedEventArgs<INode>> clipboardOnNodeCopiedFromClipboard = (source, args) -> log("Node " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<IEdge>> clipboardOnEdgeCopiedFromClipboard = (source, args) -> log("Edge " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<IPort>> clipboardOnPortCopiedFromClipboard = (source, args) -> log("Port " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<ILabel>> clipboardOnLabelCopiedFromClipboard = (source, args) -> log("Label " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<Object>> clipboardOnObjectCopiedFromClipboard = (source, args) -> log("Object " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<IGraph>> clipboardOnGraphDuplicated = (source, args) -> log("Graph duplicated.");

  IEventHandler<ItemCopiedEventArgs<INode>> clipboardOnNodeDuplicated = (source, args) -> log("Node " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<IEdge>> clipboardOnEdgeDuplicated = (source, args) -> log("Edge " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<IPort>> clipboardOnPortDuplicated = (source, args) -> log("Port " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<ILabel>> clipboardOnLabelDuplicated = (source, args) -> log("Label " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventHandler<ItemCopiedEventArgs<Object>> clipboardOnObjectDuplicated = (source, args) -> log("Object " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventHandler<IEventArgs> clipboardOnCut = (source, args) -> log("Clipboard operation: Cut");

  IEventHandler<IEventArgs> clipboardOnCopy = (source, args) -> log("Clipboard operation: Copy");

  IEventHandler<IEventArgs> clipboardOnPaste = (source, args) -> log("Clipboard operation: Paste");



  IEventHandler<IEventArgs> undoEngineOnUnitUndone = (source, args) -> log("Undo performed");

  IEventHandler<IEventArgs> undoEngineOnUnitRedone = (source, args) -> log("Redo performed");





  EventHandler<Mouse2DEvent> controlOnMouse2DClicked = (args) -> log("GraphControl Mouse2DClicked");

  EventHandler<Mouse2DEvent> controlOnMouse2DDragged = (args) -> log("GraphControl Mouse2DDragged");

  EventHandler<Mouse2DEvent> controlOnMouse2DEntered = (args) -> log("GraphControl Mouse2DEntered");

  EventHandler<Mouse2DEvent> controlOnMouse2DExited = (args) -> log("GraphControl Mouse2DExited");

  EventHandler<Mouse2DEvent> controlOnMouse2DMoved = (args) -> log("GraphControl Mouse2DMoved");

  EventHandler<Mouse2DEvent> controlOnMouse2DPressed = (args) -> log("GraphControl Mouse2DPressed");

  EventHandler<Mouse2DEvent> controlOnMouse2DReleased = (args) -> log("GraphControl Mouse2DReleased");

  EventHandler<Mouse2DEvent> controlOnMouse2DWheelTurned = (args) -> log("GraphControl Mouse2DWheelTurned");



  EventHandler<Touch2DEvent> controlOnTouch2DDown = (args) -> log("GraphControl Touch2DDown");

  EventHandler<Touch2DEvent> controlOnTouch2DEntered = (args) -> log("GraphControl Touch2DEntered");

  EventHandler<Touch2DEvent> controlOnTouch2DExited = (args) -> log("GraphControl Touch2DExited");

  EventHandler<Touch2DEvent> controlOnTouch2DLongPressed = (args) -> log("GraphControl Touch2DLongPressed");

  EventHandler<Touch2DEvent> controlOnTouch2DLostCapture = (args) -> log("GraphControl Touch2DLostCapture");

  EventHandler<Touch2DEvent> controlOnTouch2DMoved = (args) -> log("GraphControl Touch2DMoved");

  EventHandler<Touch2DEvent> controlOnTouch2DTapped = (args) -> log("GraphControl Touch2DTapped");

  EventHandler<Touch2DEvent> controlOnTouch2DUp = (args) -> log("GraphControl Touch2DUp");



  IEventHandler<ItemSelectionChangedEventArgs<IModelItem>> onItemSelectionChanged = (source, evt) -> {
    if (evt.isItemSelected()) {
      log("GraphControl Item Selected: " + evt.getItem(), "ItemSelected");
    } else {
      log("GraphControl Item Deselected: " + evt.getItem(), "ItemDeselected");
    }
  };



  ChangeListener<Number> controlOnMouseWheelZoomFactorChanged = (observableValue, oldValue, newValue) -> log("GraphControl MouseWheelZoomFactorChanged");

  ChangeListener<RectD> controlOnViewportChanged = (observableValue, oldValue, newValue) -> log("GraphControl ViewportChanged");

  ChangeListener<Number> controlOnZoomChanged = (observableValue, oldValue, newValue) -> log("GraphControl ZoomChanged");



  EventHandler<RenderContextEvent> controlOnPrepareRenderContext = (args) -> log("GraphControl PrepareRenderContext");

  EventHandler<CanvasEvent> controlOnUpdatedVisual = (args) -> log("GraphControl UpdatedVisual");

  EventHandler<CanvasEvent> controlOnUpdatingVisual = (args) -> log("GraphControl UpdatingVisual");



  ChangeListener<IModelItem> controlOnCurrentItemChanged = (observableValue, oldValue, newValue) -> log("GraphControl CurrentItemChanged");

  ChangeListener<IGraph> controlOnGraphChanged = (observableValue, oldValue, newValue) -> log("GraphControl GraphChanged");

  ChangeListener<IInputMode> controlOnInputModeChanged = (observableValue, oldValue, newValue) -> log("GraphControl InputModeChanged");





  IEventHandler<ItemChangedEventArgs<INode, INodeStyle>> onNodeStyleChanged = (source, args) -> log("Node Style Changed: " + args.getItem(), "NodeStyleChanged");

  IEventHandler<ItemChangedEventArgs<INode, Object>> onNodeTagChanged = (source, args) -> log("Node Tag Changed: " + args.getItem(), "NodeTagChanged");

  IEventHandler<ItemEventArgs<INode>> onNodeCreated = (source, args) -> log("Node Created: " + args.getItem(), "NodeCreated");

  IEventHandler<NodeEventArgs> onNodeRemoved = (source, args) -> log("Node Removed: " + args.getItem(), "NodeRemoved");

  IEventHandler<NodeEventArgs> onIsGroupNodeChanged = (source, args) -> log("GroupNode Status Changed: " + args.getItem(), "IsGroupNodeChanged");

  IEventHandler<NodeEventArgs> onParentChanged = (source, args) -> log("Parent Changed: " + args.getItem(), "ParentChanged");



  IEventHandler<ItemChangedEventArgs<IEdge, IEdgeStyle>> onEdgeStyleChanged = (source, args) -> log("Edge Style Changed: " + args.getItem(), "EdgeStyleChanged");

  IEventHandler<EdgeEventArgs> onEdgePortsChanged = (source, args) -> log("Edge Ports Changed: " + args.getItem(), "EdgePortsChanged");

  IEventHandler<ItemChangedEventArgs<IEdge, Object>> onEdgeTagChanged = (source, args) -> log("Edge Tag Changed: " + args.getItem(), "EdgeTagChanged");

  IEventHandler<ItemEventArgs<IEdge>> onEdgeCreated = (source, args) -> log("Edge Created: " + args.getItem(), "EdgeCreated");

  IEventHandler<EdgeEventArgs> onEdgeRemoved = (source, args) -> log("Edge Removed: " + args.getItem(), "EdgeRemoved");



  IEventHandler<ItemEventArgs<ILabel>> onLabelAdded = (source, args) -> log("Label Added: " + args.getItem(), "LabelAdded");

  IEventHandler<ItemChangedEventArgs<ILabel, ILabelModelParameter>> onLabelModelParameterChanged = (source, args) -> log("Label Layout Parameter Changed: " + args.getItem(), "LabelLayoutParameterChanged");

  IEventHandler<ItemChangedEventArgs<ILabel, ILabelStyle>> onLabelStyleChanged = (source, args) -> log("Label Style Changed: " + args.getItem(), "LabelStyleChanged");

  IEventHandler<ItemChangedEventArgs<ILabel, SizeD>> onLabelPreferredSizeChanged = (source, args) -> log("Label Preferrred Size Changed: " + args.getItem(), "LabelPreferredSizeChanged");

  IEventHandler<ItemChangedEventArgs<ILabel, Object>> onLabelTagChanged = (source, args) -> log("Label Tag Changed: " + args.getItem(), "LabelTagChanged");

  IEventHandler<ItemChangedEventArgs<ILabel, String>> onLabelTextChanged = (source, args) -> log("Label Text Changed: " + args.getItem(), "LabelTextChanged");

  IEventHandler<LabelEventArgs> onLabelRemoved = (source, args) -> log("Label Removed: " + args.getItem(), "LabelRemoved");



  IEventHandler<ItemEventArgs<IPort>> onPortAdded = (source, args) -> log("Port Added: " + args.getItem(), "PortAdded");

  IEventHandler<ItemChangedEventArgs<IPort, IPortLocationModelParameter>> onPortLocationParameterChanged = (source, args) -> log("Port Location Parameter Changed: " + args.getItem(), "PortLocationParameterChanged");

  IEventHandler<ItemChangedEventArgs<IPort, IPortStyle>> onPortStyleChanged = (source, args) -> log("Port Style Changed: " + args.getItem(), "PortStyleChanged");

  IEventHandler<ItemChangedEventArgs<IPort, Object>> onPortTagChanged = (source, args) -> log("Port Tag Changed: " + args.getItem(), "PortTagChanged");

  IEventHandler<PortEventArgs> onPortRemoved = (source, args) -> log("Port Removed: " + args.getItem(), "PortRemoved");



  IEventHandler<ItemEventArgs<IBend>> onBendAdded = (source, args) -> log("Bend Added: " + args.getItem(), "BendAdded");

  IBendLocationChangedHandler onBendLocationChanged = (source, bend, oldLocation) -> log("Bend Location Changed: " + bend + "; " + oldLocation, "BendLocationChanged");

  IEventHandler<ItemChangedEventArgs<IBend, Object>> onBendTagChanged = (source, args) -> log("Bend Tag Changed: " + args.getItem(), "BendTagChanged");

  IEventHandler<BendEventArgs> onBendRemoved = (source, args) -> log("Bend Removed: " + args.getItem(), "BendRemoved");



  INodeLayoutChangedHandler onNodeLayoutChanged = (source, node, oldLayout) -> log("Node Layout Changed");



  IEventHandler<IEventArgs> onDisplaysInvalidated = (source, args) -> log("Displays Invalidated");





  public void toggleInputModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerInputModeEventListener();
    } else {
      deregisterInputModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerInputModeEventListener() {
    editorMode.addCanvasClickedListener(geimOnCanvasClicked);
    editorMode.addCanvasTappedListener(geimOnCanvasTapped);
    editorMode.addDeletedItemListener(geimOnDeletedItem);
    editorMode.addDeletedSelectionListener(geimOnDeletedSelection);
    editorMode.addDeletingSelectionListener(geimOnDeletingSelection);
    editorMode.addItemClickedListener(geimOnItemClicked);
    editorMode.addItemDoubleClickedListener(geimOnItemDoubleClicked);
    editorMode.addItemLeftClickedListener(geimOnItemLeftClicked);
    editorMode.addItemLeftDoubleClickedListener(geimOnItemLeftDoubleClicked);
    editorMode.addItemRightClickedListener(geimOnItemRightClicked);
    editorMode.addItemRightDoubleClickedListener(geimOnItemRightDoubleClicked);
    editorMode.addItemTappedListener(geimOnItemTapped);
    editorMode.addItemDoubleTappedListener(geimOnItemDoubleTapped);
    editorMode.addLabelAddingListener(geimOnLabelAdding);
    editorMode.addLabelAddedListener(geimOnLabelAdded);
    editorMode.addLabelEditingListener(geimOnLabelEditing);
    editorMode.addLabelTextChangedListener(geimOnLabelTextChanged);
    editorMode.addLabelTextEditingCanceledListener(geimOnLabelTextEditingCanceled);
    editorMode.addLabelTextEditingStartedListener(geimOnLabelTextEditingStarted);
    editorMode.addMultiSelectionFinishedListener(geimOnMultiSelectionFinished);
    editorMode.addMultiSelectionStartedListener(geimOnMultiSelectionStarted);
    editorMode.addNodeCreatedListener(geimOnNodeCreated);
    editorMode.addNodeReparentedListener(geimOnNodeReparented);
    editorMode.addEdgePortsChangedListener(geimOnEdgePortsChanged);
    editorMode.addPopulateItemContextMenuListener(geimOnPopulateItemContextMenu);
    editorMode.addQueryItemToolTipListener(geimOnQueryItemToolTip);
    editorMode.addValidateLabelTextListener(geimOnValidateLabelText);
    editorMode.addElementsCopiedListener(geimOnElementsCopied);
    editorMode.addElementsCutListener(geimOnElementsCut);
    editorMode.addElementsPastedListener(geimOnElementsPasted);
    viewerMode.addCanvasClickedListener(gvimOnCanvasClicked);
    viewerMode.addCanvasTappedListener(gvimOnCanvasTapped);
    viewerMode.addItemClickedListener(gvimOnItemClicked);
    viewerMode.addItemDoubleClickedListener(gvimOnItemDoubleClicked);
    viewerMode.addItemLeftClickedListener(gvimOnItemLeftClicked);
    viewerMode.addItemLeftDoubleClickedListener(gvimOnItemLeftDoubleClicked);
    viewerMode.addItemRightClickedListener(gvimOnItemRightClicked);
    viewerMode.addItemRightDoubleClickedListener(gvimOnItemRightDoubleClicked);
    viewerMode.addItemTappedListener(gvimOnItemTapped);
    viewerMode.addItemDoubleTappedListener(gvimOnItemDoubleTapped);
    viewerMode.addMultiSelectionFinishedListener(gvimOnMultiSelectionFinished);
    viewerMode.addMultiSelectionStartedListener(gvimOnMultiSelectionStarted);
    viewerMode.addPopulateItemContextMenuListener(gvimOnPopulateItemContextMenu);
    viewerMode.addQueryItemToolTipListener(gvimOnQueryItemToolTip);
    viewerMode.addElementsCopiedListener(gvimOnElementsCopied);
  }

  private void deregisterInputModeEventListener() {
    editorMode.removeCanvasClickedListener(geimOnCanvasClicked);
    editorMode.removeCanvasTappedListener(geimOnCanvasTapped);
    editorMode.removeDeletedItemListener(geimOnDeletedItem);
    editorMode.removeDeletedSelectionListener(geimOnDeletedSelection);
    editorMode.removeDeletingSelectionListener(geimOnDeletingSelection);
    editorMode.removeItemClickedListener(geimOnItemClicked);
    editorMode.removeItemDoubleClickedListener(geimOnItemDoubleClicked);
    editorMode.removeItemLeftClickedListener(geimOnItemLeftClicked);
    editorMode.removeItemLeftDoubleClickedListener(geimOnItemLeftDoubleClicked);
    editorMode.removeItemRightClickedListener(geimOnItemRightClicked);
    editorMode.removeItemRightDoubleClickedListener(geimOnItemRightDoubleClicked);
    editorMode.removeItemTappedListener(geimOnItemTapped);
    editorMode.removeItemDoubleTappedListener(geimOnItemDoubleTapped);
    editorMode.removeLabelAddingListener(geimOnLabelAdding);
    editorMode.removeLabelAddedListener(geimOnLabelAdded);
    editorMode.removeLabelEditingListener(geimOnLabelEditing);
    editorMode.removeLabelTextChangedListener(geimOnLabelTextChanged);
    editorMode.removeLabelTextEditingCanceledListener(geimOnLabelTextEditingCanceled);
    editorMode.removeLabelTextEditingStartedListener(geimOnLabelTextEditingStarted);
    editorMode.removeMultiSelectionFinishedListener(geimOnMultiSelectionFinished);
    editorMode.removeMultiSelectionStartedListener(geimOnMultiSelectionStarted);
    editorMode.removeNodeCreatedListener(geimOnNodeCreated);
    editorMode.removeNodeReparentedListener(geimOnNodeReparented);
    editorMode.removeEdgePortsChangedListener(geimOnEdgePortsChanged);
    editorMode.removePopulateItemContextMenuListener(geimOnPopulateItemContextMenu);
    editorMode.removeQueryItemToolTipListener(geimOnQueryItemToolTip);
    editorMode.removeValidateLabelTextListener(geimOnValidateLabelText);
    editorMode.removeElementsCopiedListener(geimOnElementsCopied);
    editorMode.removeElementsCutListener(geimOnElementsCut);
    editorMode.removeElementsPastedListener(geimOnElementsPasted);
    viewerMode.removeCanvasClickedListener(gvimOnCanvasClicked);
    viewerMode.removeCanvasTappedListener(gvimOnCanvasTapped);
    viewerMode.removeItemClickedListener(gvimOnItemClicked);
    viewerMode.removeItemDoubleClickedListener(gvimOnItemDoubleClicked);
    viewerMode.removeItemLeftClickedListener(gvimOnItemLeftClicked);
    viewerMode.removeItemLeftDoubleClickedListener(gvimOnItemLeftDoubleClicked);
    viewerMode.removeItemRightClickedListener(gvimOnItemRightClicked);
    viewerMode.removeItemRightDoubleClickedListener(gvimOnItemRightDoubleClicked);
    viewerMode.removeItemTappedListener(gvimOnItemTapped);
    viewerMode.removeItemDoubleTappedListener(gvimOnItemDoubleTapped);
    viewerMode.removeMultiSelectionFinishedListener(gvimOnMultiSelectionFinished);
    viewerMode.removeMultiSelectionStartedListener(gvimOnMultiSelectionStarted);
    viewerMode.removePopulateItemContextMenuListener(gvimOnPopulateItemContextMenu);
    viewerMode.removeQueryItemToolTipListener(gvimOnQueryItemToolTip);
    viewerMode.removeElementsCopiedListener(gvimOnElementsCopied);
  }

  public void toggleNavigationModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerNavigationModeEventListener();
    } else {
      deregisterNavigationModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerNavigationModeEventListener() {
    editorMode.getNavigationInputMode().addGroupCollapsedListener(navigationInputModeOnGroupCollapsed);
    editorMode.getNavigationInputMode().addGroupCollapsingListener(navigationInputModeOnGroupCollapsing);
    editorMode.getNavigationInputMode().addGroupEnteredListener(navigationInputModeOnGroupEntered);
    editorMode.getNavigationInputMode().addGroupEnteringListener(navigationInputModeOnGroupEntering);
    editorMode.getNavigationInputMode().addGroupExitedListener(navigationInputModeOnGroupExited);
    editorMode.getNavigationInputMode().addGroupExitingListener(navigationInputModeOnGroupExiting);
    editorMode.getNavigationInputMode().addGroupExpandedListener(navigationInputModeOnGroupExpanded);
    editorMode.getNavigationInputMode().addGroupExpandingListener(navigationInputModeOnGroupExpanding);

    viewerMode.getNavigationInputMode().addGroupCollapsedListener(navigationInputModeOnGroupCollapsed);
    viewerMode.getNavigationInputMode().addGroupCollapsingListener(navigationInputModeOnGroupCollapsing);
    viewerMode.getNavigationInputMode().addGroupEnteredListener(navigationInputModeOnGroupEntered);
    viewerMode.getNavigationInputMode().addGroupEnteringListener(navigationInputModeOnGroupEntering);
    viewerMode.getNavigationInputMode().addGroupExitedListener(navigationInputModeOnGroupExited);
    viewerMode.getNavigationInputMode().addGroupExitingListener(navigationInputModeOnGroupExiting);
    viewerMode.getNavigationInputMode().addGroupExpandedListener(navigationInputModeOnGroupExpanded);
    viewerMode.getNavigationInputMode().addGroupExpandingListener(navigationInputModeOnGroupExpanding);
  }

  private void deregisterNavigationModeEventListener() {
    editorMode.getNavigationInputMode().removeGroupCollapsedListener(navigationInputModeOnGroupCollapsed);
    editorMode.getNavigationInputMode().removeGroupCollapsingListener(navigationInputModeOnGroupCollapsing);
    editorMode.getNavigationInputMode().removeGroupEnteredListener(navigationInputModeOnGroupEntered);
    editorMode.getNavigationInputMode().removeGroupEnteringListener(navigationInputModeOnGroupEntering);
    editorMode.getNavigationInputMode().removeGroupExitedListener(navigationInputModeOnGroupExited);
    editorMode.getNavigationInputMode().removeGroupExitingListener(navigationInputModeOnGroupExiting);
    editorMode.getNavigationInputMode().removeGroupExpandedListener(navigationInputModeOnGroupExpanded);
    editorMode.getNavigationInputMode().removeGroupExpandingListener(navigationInputModeOnGroupExpanding);

    viewerMode.getNavigationInputMode().removeGroupCollapsedListener(navigationInputModeOnGroupCollapsed);
    viewerMode.getNavigationInputMode().removeGroupCollapsingListener(navigationInputModeOnGroupCollapsing);
    viewerMode.getNavigationInputMode().removeGroupEnteredListener(navigationInputModeOnGroupEntered);
    viewerMode.getNavigationInputMode().removeGroupEnteringListener(navigationInputModeOnGroupEntering);
    viewerMode.getNavigationInputMode().removeGroupExitedListener(navigationInputModeOnGroupExited);
    viewerMode.getNavigationInputMode().removeGroupExitingListener(navigationInputModeOnGroupExiting);
    viewerMode.getNavigationInputMode().removeGroupExpandedListener(navigationInputModeOnGroupExpanded);
    viewerMode.getNavigationInputMode().removeGroupExpandingListener(navigationInputModeOnGroupExpanding);
  }

  public void toggleClickModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerClickModeEventListener();
    } else {
      deregisterClickModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerClickModeEventListener() {
    editorMode.getClickInputMode().addClickedListener(clickInputModeOnClicked);
    editorMode.getClickInputMode().addDoubleClickedListener(clickInputModeOnDoubleClicked);
    editorMode.getClickInputMode().addLeftClickedListener(clickInputModeOnLeftClicked);
    editorMode.getClickInputMode().addLeftDoubleClickedListener(clickInputModeOnLeftDoubleClicked);
    editorMode.getClickInputMode().addRightClickedListener(clickInputModeOnRightClicked);
    editorMode.getClickInputMode().addRightDoubleClickedListener(clickInputModeOnRightDoubleClicked);

    viewerMode.getClickInputMode().addClickedListener(clickInputModeOnClicked);
    viewerMode.getClickInputMode().addDoubleClickedListener(clickInputModeOnDoubleClicked);
    viewerMode.getClickInputMode().addLeftClickedListener(clickInputModeOnLeftClicked);
    viewerMode.getClickInputMode().addLeftDoubleClickedListener(clickInputModeOnLeftDoubleClicked);
    viewerMode.getClickInputMode().addRightClickedListener(clickInputModeOnRightClicked);
    viewerMode.getClickInputMode().addRightDoubleClickedListener(clickInputModeOnRightDoubleClicked);
  }

  private void deregisterClickModeEventListener() {
    editorMode.getClickInputMode().removeClickedListener(clickInputModeOnClicked);
    editorMode.getClickInputMode().removeDoubleClickedListener(clickInputModeOnDoubleClicked);
    editorMode.getClickInputMode().removeLeftClickedListener(clickInputModeOnLeftClicked);
    editorMode.getClickInputMode().removeLeftDoubleClickedListener(clickInputModeOnLeftDoubleClicked);
    editorMode.getClickInputMode().removeRightClickedListener(clickInputModeOnRightClicked);
    editorMode.getClickInputMode().removeRightDoubleClickedListener(clickInputModeOnRightDoubleClicked);

    viewerMode.getClickInputMode().removeClickedListener(clickInputModeOnClicked);
    viewerMode.getClickInputMode().removeDoubleClickedListener(clickInputModeOnDoubleClicked);
    viewerMode.getClickInputMode().removeLeftClickedListener(clickInputModeOnLeftClicked);
    viewerMode.getClickInputMode().removeLeftDoubleClickedListener(clickInputModeOnLeftDoubleClicked);
    viewerMode.getClickInputMode().removeRightClickedListener(clickInputModeOnRightClicked);
    viewerMode.getClickInputMode().removeRightDoubleClickedListener(clickInputModeOnRightDoubleClicked);
  }

  public void toggleKeyEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {
    if (newValue) {
      registerKeyEventListener();
    } else {
      deregisterKeyEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerKeyEventListener() {
    graphControl.addEventFilter(KeyEvent.KEY_PRESSED, canvasControlOnKeyPressed);
    graphControl.addEventFilter(KeyEvent.KEY_RELEASED, canvasControlOnKeyReleased);
    graphControl.addEventFilter(KeyEvent.KEY_TYPED, canvasControlOnKeyTyped);
  }

  private void deregisterKeyEventListener() {
    graphControl.removeEventFilter(KeyEvent.KEY_PRESSED, canvasControlOnKeyPressed);
    graphControl.removeEventFilter(KeyEvent.KEY_RELEASED, canvasControlOnKeyReleased);
    graphControl.removeEventFilter(KeyEvent.KEY_TYPED, canvasControlOnKeyTyped);
  }

  public void toggleTapModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerTapModeEventListener();
    } else {
      deregisterTapModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerTapModeEventListener() {
    editorMode.getTapInputMode().addDoubleTappedListener(tapInputModeOnDoubleTapped);
    editorMode.getTapInputMode().addTappedListener(tapInputModeOnTapped);
    viewerMode.getTapInputMode().addDoubleTappedListener(tapInputModeOnDoubleTapped);
    viewerMode.getTapInputMode().addTappedListener(tapInputModeOnTapped);
  }

  private void deregisterTapModeEventListener() {
    editorMode.getTapInputMode().removeDoubleTappedListener(tapInputModeOnDoubleTapped);
    editorMode.getTapInputMode().removeTappedListener(tapInputModeOnTapped);
    viewerMode.getTapInputMode().removeDoubleTappedListener(tapInputModeOnDoubleTapped);
    viewerMode.getTapInputMode().removeTappedListener(tapInputModeOnTapped);
  }

  public void toggleMoveModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerMoveModeEventListener();
    } else {
      deregisterMoveModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerMoveModeEventListener() {
    editorMode.getMoveInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveInputMode().addDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveInputMode().addDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveInputMode().addDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getMoveInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterMoveModeEventListener() {
    editorMode.getMoveInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveInputMode().removeDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveInputMode().removeDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getMoveInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleMoveViewportModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerMoveViewportModeEventListener();
    } else {
      deregisterMoveViewportModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerMoveViewportModeEventListener() {
    editorMode.getMoveViewportInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveViewportInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveViewportInputMode().addDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveViewportInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveViewportInputMode().addDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveViewportInputMode().addDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveViewportInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getMoveViewportInputMode().addDraggingListener(inputModeOnDragging);

    viewerMode.getMoveViewportInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    viewerMode.getMoveViewportInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    viewerMode.getMoveViewportInputMode().addDragFinishedListener(inputModeOnDragFinished);
    viewerMode.getMoveViewportInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    viewerMode.getMoveViewportInputMode().addDragStartedListener(inputModeOnDragStarted);
    viewerMode.getMoveViewportInputMode().addDragStartingListener(inputModeOnDragStarting);

    viewerMode.getMoveViewportInputMode().addDraggedListener(inputModeOnDragged);
    viewerMode.getMoveViewportInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterMoveViewportModeEventListener() {
    editorMode.getMoveViewportInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveViewportInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveViewportInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveViewportInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveViewportInputMode().removeDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveViewportInputMode().removeDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveViewportInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getMoveViewportInputMode().removeDraggingListener(inputModeOnDragging);

    viewerMode.getMoveViewportInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    viewerMode.getMoveViewportInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    viewerMode.getMoveViewportInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    viewerMode.getMoveViewportInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    viewerMode.getMoveViewportInputMode().removeDragStartedListener(inputModeOnDragStarted);
    viewerMode.getMoveViewportInputMode().removeDragStartingListener(inputModeOnDragStarting);

    viewerMode.getMoveViewportInputMode().removeDraggedListener(inputModeOnDragged);
    viewerMode.getMoveViewportInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleHandleModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerHandleModeEventListener();
    } else {
      deregisterHandleModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerHandleModeEventListener() {
    editorMode.getHandleInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getHandleInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getHandleInputMode().addDragFinishedListener(inputModeOnDragFinished);
    editorMode.getHandleInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getHandleInputMode().addDragStartedListener(inputModeOnDragStarted);
    editorMode.getHandleInputMode().addDragStartingListener(inputModeOnDragStarting);

    editorMode.getHandleInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getHandleInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterHandleModeEventListener() {
    editorMode.getHandleInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getHandleInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getHandleInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    editorMode.getHandleInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getHandleInputMode().removeDragStartedListener(inputModeOnDragStarted);
    editorMode.getHandleInputMode().removeDragStartingListener(inputModeOnDragStarting);

    editorMode.getHandleInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getHandleInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleMouseHoverModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerMouseHoverModeEventListener();
    } else {
      deregisterMouseHoverModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerMouseHoverModeEventListener() {
    editorMode.getMouseHoverInputMode().addQueryToolTipListener(mouseHoverInputModeOnQueryToolTip);
    viewerMode.getMouseHoverInputMode().addQueryToolTipListener(mouseHoverInputModeOnQueryToolTip);
  }

  private void deregisterMouseHoverModeEventListener() {
    editorMode.getMouseHoverInputMode().removeQueryToolTipListener(mouseHoverInputModeOnQueryToolTip);
    viewerMode.getMouseHoverInputMode().removeQueryToolTipListener(mouseHoverInputModeOnQueryToolTip);
  }

  public void toggleTextEditorModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerTextEditorModeEventListener();
    } else {
      deregisterTextEditorModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerTextEditorModeEventListener() {
    editorMode.getTextEditorInputMode().addEditingCanceledListener(textEditorInputModeOnEditingCanceled);
    editorMode.getTextEditorInputMode().addEditingStartedListener(textEditorInputModeOnEditingStarted);
    editorMode.getTextEditorInputMode().addTextEditedListener(textEditorInputModeOnTextEdited);
  }

  private void deregisterTextEditorModeEventListener() {
    editorMode.getTextEditorInputMode().removeEditingCanceledListener(textEditorInputModeOnEditingCanceled);
    editorMode.getTextEditorInputMode().removeEditingStartedListener(textEditorInputModeOnEditingStarted);
    editorMode.getTextEditorInputMode().removeTextEditedListener(textEditorInputModeOnTextEdited);
  }

  public void toggleContextMenuModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerContextMenuModeEventListener();
    } else {
      deregisterContextMenuModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerContextMenuModeEventListener() {
    editorMode.getContextMenuInputMode().addPopulateMenuListener(contextMenuInputModeOnPopulateMenu);
    viewerMode.getContextMenuInputMode().addPopulateMenuListener(contextMenuInputModeOnPopulateMenu);
  }

  private void deregisterContextMenuModeEventListener() {
    editorMode.getContextMenuInputMode().removePopulateMenuListener(contextMenuInputModeOnPopulateMenu);
    viewerMode.getContextMenuInputMode().removePopulateMenuListener(contextMenuInputModeOnPopulateMenu);
  }

  public void toggleCreateBendModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerCreateBendModeEventListener();
    } else {
      deregisterCreateBendModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerCreateBendModeEventListener() {
    editorMode.getCreateBendInputMode().addBendCreatedListener(createBendInputModeOnBendCreated);
    editorMode.getCreateBendInputMode().addDragCanceledListener(inputModeOnDragCanceled);

    editorMode.getCreateBendInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getCreateBendInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterCreateBendModeEventListener() {
    editorMode.getCreateBendInputMode().removeBendCreatedListener(createBendInputModeOnBendCreated);
    editorMode.getCreateBendInputMode().removeDragCanceledListener(inputModeOnDragCanceled);

    editorMode.getCreateBendInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getCreateBendInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleCreateEdgeModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerCreateEdgeModeEventListener();
    } else {
      deregisterCreateEdgeModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerCreateEdgeModeEventListener() {
    editorMode.getCreateEdgeInputMode().addEdgeCreatedListener(createEdgeInputModeOnEdgeCreated);
    editorMode.getCreateEdgeInputMode().addEdgeCreationStartedListener(createEdgeInputModeOnEdgeCreationStarted);
    editorMode.getCreateEdgeInputMode().addGestureCanceledListener(createEdgeInputModeOnGestureCanceled);
    editorMode.getCreateEdgeInputMode().addGestureCancelingListener(createEdgeInputModeOnGestureCanceling);
    editorMode.getCreateEdgeInputMode().addGestureFinishedListener(createEdgeInputModeOnGestureFinished);
    editorMode.getCreateEdgeInputMode().addGestureFinishingListener(createEdgeInputModeOnGestureFinishing);
    editorMode.getCreateEdgeInputMode().addGestureStartedListener(createEdgeInputModeOnGestureStarted);
    editorMode.getCreateEdgeInputMode().addGestureStartingListener(createEdgeInputModeOnGestureStarting);
    editorMode.getCreateEdgeInputMode().addMovedListener(createEdgeInputModeOnMoved);
    editorMode.getCreateEdgeInputMode().addMovingListener(createEdgeInputModeOnMoving);
    editorMode.getCreateEdgeInputMode().addPortAddedListener(createEdgeInputModeOnPortAdded);
    editorMode.getCreateEdgeInputMode().addSourcePortCandidateChangedListener(createEdgeInputModeOnSourcePortCandidateChanged);
    editorMode.getCreateEdgeInputMode().addTargetPortCandidateChangedListener(createEdgeInputModeOnTargetPortCandidateChanged);
  }

  private void deregisterCreateEdgeModeEventListener() {
    editorMode.getCreateEdgeInputMode().removeEdgeCreatedListener(createEdgeInputModeOnEdgeCreated);
    editorMode.getCreateEdgeInputMode().removeEdgeCreationStartedListener(createEdgeInputModeOnEdgeCreationStarted);
    editorMode.getCreateEdgeInputMode().removeGestureCanceledListener(createEdgeInputModeOnGestureCanceled);
    editorMode.getCreateEdgeInputMode().removeGestureCancelingListener(createEdgeInputModeOnGestureCanceling);
    editorMode.getCreateEdgeInputMode().removeGestureFinishedListener(createEdgeInputModeOnGestureFinished);
    editorMode.getCreateEdgeInputMode().removeGestureFinishingListener(createEdgeInputModeOnGestureFinishing);
    editorMode.getCreateEdgeInputMode().removeGestureStartedListener(createEdgeInputModeOnGestureStarted);
    editorMode.getCreateEdgeInputMode().removeGestureStartingListener(createEdgeInputModeOnGestureStarting);
    editorMode.getCreateEdgeInputMode().removeMovedListener(createEdgeInputModeOnMoved);
    editorMode.getCreateEdgeInputMode().removeMovingListener(createEdgeInputModeOnMoving);
    editorMode.getCreateEdgeInputMode().removePortAddedListener(createEdgeInputModeOnPortAdded);
    editorMode.getCreateEdgeInputMode().removeSourcePortCandidateChangedListener(createEdgeInputModeOnSourcePortCandidateChanged);
    editorMode.getCreateEdgeInputMode().removeTargetPortCandidateChangedListener(createEdgeInputModeOnTargetPortCandidateChanged);
  }

  public void toggleItemHoverModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerItemHoverModeEventListener();
    } else {
      deregisterItemHoverModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerItemHoverModeEventListener() {
    editorMode.getItemHoverInputMode().addHoveredItemChangedListener(itemHoverInputModeOnHoveredItemChanged);
    viewerMode.getItemHoverInputMode().addHoveredItemChangedListener(itemHoverInputModeOnHoveredItemChanged);
  }

  private void deregisterItemHoverModeEventListener() {
    editorMode.getItemHoverInputMode().removeHoveredItemChangedListener(itemHoverInputModeOnHoveredItemChanged);
    viewerMode.getItemHoverInputMode().removeHoveredItemChangedListener(itemHoverInputModeOnHoveredItemChanged);
  }

  public void toggleMoveLabelModeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerMoveLabelModeEventListener();
    } else {
      deregisterMoveLabelModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerMoveLabelModeEventListener() {
    editorMode.getMoveLabelInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveLabelInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveLabelInputMode().addDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveLabelInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveLabelInputMode().addDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveLabelInputMode().addDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveLabelInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getMoveLabelInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterMoveLabelModeEventListener() {
    editorMode.getMoveLabelInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveLabelInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveLabelInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveLabelInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveLabelInputMode().removeDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveLabelInputMode().removeDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveLabelInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getMoveLabelInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleClipboardEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerClipboardEventListener();
    } else {
      deregisterClipboardEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerClipboardEventListener() {
    GraphClipboard clipboard = graphControl.getClipboard();
    clipboard.addElementsCutListener(clipboardOnCut);
    clipboard.addElementsCopiedListener(clipboardOnCopy);
    clipboard.addElementsPastedListener(clipboardOnPaste);
  }

  private void deregisterClipboardEventListener() {
    GraphClipboard clipboard = graphControl.getClipboard();
    clipboard.removeElementsCutListener(clipboardOnCut);
    clipboard.removeElementsCopiedListener(clipboardOnCopy);
    clipboard.removeElementsPastedListener(clipboardOnPaste);
  }

  public void toggleUndoEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerUndoEventListener();
    } else {
      deregisterUndoEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerUndoEventListener() {
    UndoEngine undoEngine = graphControl.getGraph().getUndoEngine();
    undoEngine.addUnitUndoneListener(undoEngineOnUnitUndone);
    undoEngine.addUnitRedoneListener(undoEngineOnUnitRedone);
  }

  private void deregisterUndoEventListener() {
    UndoEngine undoEngine = graphControl.getGraph().getUndoEngine();
    undoEngine.removeUnitUndoneListener(undoEngineOnUnitUndone);
    undoEngine.removeUnitRedoneListener(undoEngineOnUnitRedone);
  }



  public void toggleClipboardCopierEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerClipboardCopierEventListener();
    } else {
      deregisterClipboardCopierEventListener();
      logAllGraphControlEvents.setSelected(false);
    }
  }

  private void registerClipboardCopierEventListener() {
    GraphClipboard clipboard = graphControl.getClipboard();
    GraphCopier toClipboardCopier = clipboard.getToClipboardCopier();
    toClipboardCopier.addGraphCopiedListener(clipboardOnGraphCopiedToClipboard);
    toClipboardCopier.addNodeCopiedListener(clipboardOnNodeCopiedToClipboard);
    toClipboardCopier.addEdgeCopiedListener(clipboardOnEdgeCopiedToClipboard);
    toClipboardCopier.addPortCopiedListener(clipboardOnPortCopiedToClipboard);
    toClipboardCopier.addLabelCopiedListener(clipboardOnLabelCopiedToClipboard);
    toClipboardCopier.addObjectCopiedListener(clipboardOnObjectCopiedToClipboard);

    GraphCopier fromClipboardCopier = clipboard.getFromClipboardCopier();
    fromClipboardCopier.addGraphCopiedListener(clipboardOnGraphCopiedFromClipboard);
    fromClipboardCopier.addNodeCopiedListener(clipboardOnNodeCopiedFromClipboard);
    fromClipboardCopier.addEdgeCopiedListener(clipboardOnEdgeCopiedFromClipboard);
    fromClipboardCopier.addPortCopiedListener(clipboardOnPortCopiedFromClipboard);
    fromClipboardCopier.addLabelCopiedListener(clipboardOnLabelCopiedFromClipboard);
    fromClipboardCopier.addObjectCopiedListener(clipboardOnObjectCopiedFromClipboard);

    GraphCopier duplicateCopier = clipboard.getDuplicateCopier();
    duplicateCopier.addGraphCopiedListener(clipboardOnGraphDuplicated);
    duplicateCopier.addNodeCopiedListener(clipboardOnNodeDuplicated);
    duplicateCopier.addEdgeCopiedListener(clipboardOnEdgeDuplicated);
    duplicateCopier.addPortCopiedListener(clipboardOnPortDuplicated);
    duplicateCopier.addLabelCopiedListener(clipboardOnLabelDuplicated);
    duplicateCopier.addObjectCopiedListener(clipboardOnObjectDuplicated);
  }

  private void deregisterClipboardCopierEventListener() {
    GraphClipboard clipboard = graphControl.getClipboard();
    GraphCopier toClipboardCopier = clipboard.getToClipboardCopier();
    toClipboardCopier.removeGraphCopiedListener(clipboardOnGraphCopiedToClipboard);
    toClipboardCopier.removeNodeCopiedListener(clipboardOnNodeCopiedToClipboard);
    toClipboardCopier.removeEdgeCopiedListener(clipboardOnEdgeCopiedToClipboard);
    toClipboardCopier.removePortCopiedListener(clipboardOnPortCopiedToClipboard);
    toClipboardCopier.removeLabelCopiedListener(clipboardOnLabelCopiedToClipboard);
    toClipboardCopier.removeObjectCopiedListener(clipboardOnObjectCopiedToClipboard);

    GraphCopier fromClipboardCopier = clipboard.getFromClipboardCopier();
    fromClipboardCopier.removeGraphCopiedListener(clipboardOnGraphCopiedFromClipboard);
    fromClipboardCopier.removeNodeCopiedListener(clipboardOnNodeCopiedFromClipboard);
    fromClipboardCopier.removeEdgeCopiedListener(clipboardOnEdgeCopiedFromClipboard);
    fromClipboardCopier.removePortCopiedListener(clipboardOnPortCopiedFromClipboard);
    fromClipboardCopier.removeLabelCopiedListener(clipboardOnLabelCopiedFromClipboard);
    fromClipboardCopier.removeObjectCopiedListener(clipboardOnObjectCopiedFromClipboard);

    GraphCopier duplicateCopier = clipboard.getDuplicateCopier();
    duplicateCopier.removeGraphCopiedListener(clipboardOnGraphDuplicated);
    duplicateCopier.removeNodeCopiedListener(clipboardOnNodeDuplicated);
    duplicateCopier.removeEdgeCopiedListener(clipboardOnEdgeDuplicated);
    duplicateCopier.removePortCopiedListener(clipboardOnPortDuplicated);
    duplicateCopier.removeLabelCopiedListener(clipboardOnLabelDuplicated);
    duplicateCopier.removeObjectCopiedListener(clipboardOnObjectDuplicated);
  }

  public void toggleGraphControlMouseEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerGraphControlMouseEventListener();
    } else {
      deregisterGraphControlMouseEventListener();
      logAllGraphControlEvents.setSelected(false);
    }
  }

  private void registerGraphControlMouseEventListener() {
    graphControl.addEventHandler(Mouse2DEvent.CLICKED, controlOnMouse2DClicked);
    graphControl.addEventHandler(Mouse2DEvent.ENTERED, controlOnMouse2DEntered);
    graphControl.addEventHandler(Mouse2DEvent.EXITED, controlOnMouse2DExited);
    graphControl.addEventHandler(Mouse2DEvent.PRESSED, controlOnMouse2DPressed);
    graphControl.addEventHandler(Mouse2DEvent.RELEASED, controlOnMouse2DReleased);
    graphControl.addEventHandler(Mouse2DEvent.WHEEL_TURNED, controlOnMouse2DWheelTurned);
    graphControl.addEventHandler(Mouse2DEvent.DRAGGED, controlOnMouse2DDragged);
    graphControl.addEventHandler(Mouse2DEvent.MOVED, controlOnMouse2DMoved);
  }

  private void deregisterGraphControlMouseEventListener() {
    graphControl.removeEventHandler(Mouse2DEvent.CLICKED, controlOnMouse2DClicked);
    graphControl.removeEventHandler(Mouse2DEvent.ENTERED, controlOnMouse2DEntered);
    graphControl.removeEventHandler(Mouse2DEvent.EXITED, controlOnMouse2DExited);
    graphControl.removeEventHandler(Mouse2DEvent.PRESSED, controlOnMouse2DPressed);
    graphControl.removeEventHandler(Mouse2DEvent.RELEASED, controlOnMouse2DReleased);
    graphControl.removeEventHandler(Mouse2DEvent.WHEEL_TURNED, controlOnMouse2DWheelTurned);
    graphControl.removeEventHandler(Mouse2DEvent.DRAGGED, controlOnMouse2DDragged);
    graphControl.removeEventHandler(Mouse2DEvent.MOVED, controlOnMouse2DMoved);
  }

  public void toggleGraphControlTouchEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerGraphControlTouchEventListener();
    } else {
      deregisterGraphControlTouchEventListener();
      logAllGraphControlEvents.setSelected(false);
    }
  }

  private void registerGraphControlTouchEventListener() {
    graphControl.addEventHandler(Touch2DEvent.DOWN, controlOnTouch2DDown);
    graphControl.addEventHandler(Touch2DEvent.ENTERED, controlOnTouch2DEntered);
    graphControl.addEventHandler(Touch2DEvent.EXITED, controlOnTouch2DExited);
    graphControl.addEventHandler(Touch2DEvent.LONG_PRESS, controlOnTouch2DLongPressed);
    graphControl.addEventHandler(Touch2DEvent.LOST_CAPTURE, controlOnTouch2DLostCapture);
    graphControl.addEventHandler(Touch2DEvent.TAPPED, controlOnTouch2DTapped);
    graphControl.addEventHandler(Touch2DEvent.UP, controlOnTouch2DUp);
    graphControl.addEventHandler(Touch2DEvent.MOVED, controlOnTouch2DMoved);
  }

  private void deregisterGraphControlTouchEventListener() {
    graphControl.removeEventHandler(Touch2DEvent.DOWN, controlOnTouch2DDown);
    graphControl.removeEventHandler(Touch2DEvent.ENTERED, controlOnTouch2DEntered);
    graphControl.removeEventHandler(Touch2DEvent.EXITED, controlOnTouch2DExited);
    graphControl.removeEventHandler(Touch2DEvent.LONG_PRESS, controlOnTouch2DLongPressed);
    graphControl.removeEventHandler(Touch2DEvent.LOST_CAPTURE, controlOnTouch2DLostCapture);
    graphControl.removeEventHandler(Touch2DEvent.TAPPED, controlOnTouch2DTapped);
    graphControl.removeEventHandler(Touch2DEvent.UP, controlOnTouch2DUp);
    graphControl.removeEventHandler(Touch2DEvent.MOVED, controlOnTouch2DMoved);
  }

  public void toggleSelectionEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerSelectionEventListener();
    } else {
      deregisterSelectionEventListener();
      logAllGraphControlEvents.setSelected(false);
    }
  }

  private void registerSelectionEventListener() {
    graphControl.getSelection().addItemSelectionChangedListener(onItemSelectionChanged);
  }

  private void deregisterSelectionEventListener() {
    graphControl.getSelection().removeItemSelectionChangedListener(onItemSelectionChanged);
  }

  public void toggleGraphControlViewportEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerGraphControlViewportEventListener();
    } else {
      deregisterGraphControlViewportEventListener();
      logAllGraphControlEvents.setSelected(false);
    }
  }

  private void registerGraphControlViewportEventListener() {
    graphControl.mouseWheelZoomFactorProperty().addListener(controlOnMouseWheelZoomFactorChanged);
    graphControl.viewportProperty().addListener(controlOnViewportChanged);
    graphControl.zoomProperty().addListener(controlOnZoomChanged);
  }

  private void deregisterGraphControlViewportEventListener() {
    graphControl.mouseWheelZoomFactorProperty().removeListener(controlOnMouseWheelZoomFactorChanged);
    graphControl.viewportProperty().removeListener(controlOnViewportChanged);
    graphControl.zoomProperty().removeListener(controlOnZoomChanged);
  }

  public void toggleGraphControlRenderEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerGraphControlRenderEventListener();
    } else {
      deregisterGraphControlRenderEventListener();
      logAllGraphControlEvents.setSelected(false);
    }
  }

  private void registerGraphControlRenderEventListener() {
    graphControl.addEventHandler(RenderContextEvent.PREPARE, controlOnPrepareRenderContext);
    graphControl.addEventHandler(CanvasEvent.UPDATED, controlOnUpdatedVisual);
    graphControl.addEventHandler(CanvasEvent.UPDATING, controlOnUpdatingVisual);
  }

  private void deregisterGraphControlRenderEventListener() {
    graphControl.removeEventHandler(RenderContextEvent.PREPARE, controlOnPrepareRenderContext);
    graphControl.removeEventHandler(CanvasEvent.UPDATED, controlOnUpdatedVisual);
    graphControl.removeEventHandler(CanvasEvent.UPDATING, controlOnUpdatingVisual);
  }

  public void toggleGraphControlEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerGraphControlEventListener();
    } else {
      deregisterGraphControlEventListener();
      logAllGraphControlEvents.setSelected(false);
    }
  }

  private void registerGraphControlEventListener() {
    graphControl.currentItemProperty().addListener(controlOnCurrentItemChanged);
    graphControl.graphProperty().addListener(controlOnGraphChanged);
    graphControl.inputModeProperty().addListener(controlOnInputModeChanged);
  }

  private void deregisterGraphControlEventListener() {
    graphControl.currentItemProperty().removeListener(controlOnCurrentItemChanged);
    graphControl.graphProperty().removeListener(controlOnGraphChanged);
    graphControl.inputModeProperty().removeListener(controlOnInputModeChanged);
  }



  public void toggleNodeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerNodeEventListener();
    } else {
      deregisterNodeEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerNodeEventListener() {
    graphControl.getGraph().addNodeStyleChangedListener(onNodeStyleChanged);
    graphControl.getGraph().addNodeTagChangedListener(onNodeTagChanged);
    graphControl.getGraph().addNodeCreatedListener(onNodeCreated);
    graphControl.getGraph().addNodeRemovedListener(onNodeRemoved);
    graphControl.getGraph().addIsGroupNodeChangedListener(onIsGroupNodeChanged);
    graphControl.getGraph().addParentChangedListener(onParentChanged);
  }

  private void deregisterNodeEventListener() {
    graphControl.getGraph().removeNodeStyleChangedListener(onNodeStyleChanged);
    graphControl.getGraph().removeNodeTagChangedListener(onNodeTagChanged);
    graphControl.getGraph().removeNodeCreatedListener(onNodeCreated);
    graphControl.getGraph().removeNodeRemovedListener(onNodeRemoved);
    graphControl.getGraph().removeIsGroupNodeChangedListener(onIsGroupNodeChanged);
    graphControl.getGraph().removeParentChangedListener(onParentChanged);
  }

  public void toggleEdgeEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerEdgeEventListener();
    } else {
      deregisterEdgeEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerEdgeEventListener() {
    graphControl.getGraph().addEdgeStyleChangedListener(onEdgeStyleChanged);
    graphControl.getGraph().addEdgePortsChangedListener(onEdgePortsChanged);
    graphControl.getGraph().addEdgeTagChangedListener(onEdgeTagChanged);
    graphControl.getGraph().addEdgeCreatedListener(onEdgeCreated);
    graphControl.getGraph().addEdgeRemovedListener(onEdgeRemoved);
  }

  private void deregisterEdgeEventListener() {
    graphControl.getGraph().removeEdgeStyleChangedListener(onEdgeStyleChanged);
    graphControl.getGraph().removeEdgePortsChangedListener(onEdgePortsChanged);
    graphControl.getGraph().removeEdgeTagChangedListener(onEdgeTagChanged);
    graphControl.getGraph().removeEdgeCreatedListener(onEdgeCreated);
    graphControl.getGraph().removeEdgeRemovedListener(onEdgeRemoved);
  }

  public void toggleLabelEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerLabelEventListener();
    } else {
      deregisterLabelEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerLabelEventListener() {
    graphControl.getGraph().addLabelAddedListener(onLabelAdded);
    graphControl.getGraph().addLabelRemovedListener(onLabelRemoved);
    graphControl.getGraph().addLabelLayoutParameterChangedListener(onLabelModelParameterChanged);
    graphControl.getGraph().addLabelStyleChangedListener(onLabelStyleChanged);
    graphControl.getGraph().addLabelPreferredSizeChangedListener(onLabelPreferredSizeChanged);
    graphControl.getGraph().addLabelTagChangedListener(onLabelTagChanged);
    graphControl.getGraph().addLabelTextChangedListener(onLabelTextChanged);
  }

  private void deregisterLabelEventListener() {
    graphControl.getGraph().removeLabelAddedListener(onLabelAdded);
    graphControl.getGraph().removeLabelRemovedListener(onLabelRemoved);
    graphControl.getGraph().removeLabelLayoutParameterChangedListener(onLabelModelParameterChanged);
    graphControl.getGraph().removeLabelStyleChangedListener(onLabelStyleChanged);
    graphControl.getGraph().removeLabelPreferredSizeChangedListener(onLabelPreferredSizeChanged);
    graphControl.getGraph().removeLabelTagChangedListener(onLabelTagChanged);
    graphControl.getGraph().removeLabelTextChangedListener(onLabelTextChanged);
  }

  public void togglePortEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerPortEventListener();
    } else {
      deregisterPortEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerPortEventListener() {
    graphControl.getGraph().addPortAddedListener(onPortAdded);
    graphControl.getGraph().addPortLocationParameterChangedListener(onPortLocationParameterChanged);
    graphControl.getGraph().addPortStyleChangedListener(onPortStyleChanged);
    graphControl.getGraph().addPortTagChangedListener(onPortTagChanged);
    graphControl.getGraph().addPortRemovedListener(onPortRemoved);
  }

  private void deregisterPortEventListener() {
    graphControl.getGraph().removePortAddedListener(onPortAdded);
    graphControl.getGraph().removePortLocationParameterChangedListener(onPortLocationParameterChanged);
    graphControl.getGraph().removePortStyleChangedListener(onPortStyleChanged);
    graphControl.getGraph().removePortTagChangedListener(onPortTagChanged);
    graphControl.getGraph().removePortRemovedListener(onPortRemoved);
  }

  public void toggleBendEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerBendEventListener();
    } else {
      deregisterBendEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerBendEventListener() {
    graphControl.getGraph().addBendAddedListener(onBendAdded);
    graphControl.getGraph().addBendLocationChangedListener(onBendLocationChanged);
    graphControl.getGraph().addBendTagChangedListener(onBendTagChanged);
    graphControl.getGraph().addBendRemovedListener(onBendRemoved);
  }

  private void deregisterBendEventListener() {
    graphControl.getGraph().removeBendAddedListener(onBendAdded);
    graphControl.getGraph().removeBendLocationChangedListener(onBendLocationChanged);
    graphControl.getGraph().removeBendTagChangedListener(onBendTagChanged);
    graphControl.getGraph().removeBendRemovedListener(onBendRemoved);
  }

  public void toggleNodeBoundsEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerNodeBoundsEventListener();
    } else {
      deregisterNodeBoundsEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerNodeBoundsEventListener() {
    graphControl.getGraph().addNodeLayoutChangedListener(onNodeLayoutChanged);
  }

  private void deregisterNodeBoundsEventListener() {
    graphControl.getGraph().removeNodeLayoutChangedListener(onNodeLayoutChanged);
  }

  public void toggleGraphRenderEventListener(BooleanProperty observable, boolean oldValue, boolean newValue) {

    if (newValue) {
      registerGraphRenderEventListener();
    } else {
      deregisterGraphRenderEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerGraphRenderEventListener() {
    graphControl.getGraph().addDisplaysInvalidatedListener(onDisplaysInvalidated);
  }

  private void deregisterGraphRenderEventListener() {
    graphControl.getGraph().removeDisplaysInvalidatedListener(onDisplaysInvalidated);
  }




  public void clearButtonClick() {
    clearLog();
  }

  public void toggleEditing(BooleanProperty observable, boolean oldValue, boolean newValue) {
    if (newValue) {
      graphControl.setInputMode(editorMode);
    } else {
      graphControl.setInputMode(viewerMode);
    }
  }

  public void toggleOrthogonalEditing(BooleanProperty observable, boolean oldValue, boolean newValue) {
    editorMode.getOrthogonalEdgeEditingContext().setEnabled(newValue);
  }

  public void toggleLassoSelection(BooleanProperty observable, boolean oldValue, boolean newValue) {
    editorMode.getLassoSelectionInputMode().setEnabled(newValue);
    viewerMode.getLassoSelectionInputMode().setEnabled(newValue);
  }

  public void toggleAllInputEventListener() {
    boolean selected = logAllInputEvents.isSelected();
    logInputModeEvents.setSelected(selected);
    logNavigationModeEvents.setSelected(selected);
    logClickModeEvents.setSelected(selected);
    logTapModeEvents.setSelected(selected);
    logMoveModeEvents.setSelected(selected);
    logMoveViewportModeEvents.setSelected(selected);
    logHandleModeEvents.setSelected(selected);
    logMouseHoverModeEvents.setSelected(selected);
    logTextEditorModeEvents.setSelected(selected);
    logContextMenuModeEvents.setSelected(selected);
    logCreateBendModeEvents.setSelected(selected);
    logCreateEdgeModeEvents.setSelected(selected);
    logItemHoverModeEvents.setSelected(selected);
    logMoveLabelModeEvents.setSelected(selected);
    logClipboardEvents.setSelected(selected);
    logUndoEvents.setSelected(selected);
  }

  public void toggleAllGraphControlEventListener() {
    boolean selected = logAllGraphControlEvents.isSelected();
    logClipboardCopierEvents.setSelected(selected);
    logMouseEvents.setSelected(selected);
    logTouchEvents.setSelected(selected);
    logKeyEvents.setSelected(selected);
    logSelectionEvents.setSelected(selected);
    logViewportEvents.setSelected(selected);
    logRenderEvents.setSelected(selected);
    logGraphControlEvents.setSelected(selected);
  }

  public void toggleAllGraphEventListener() {
    boolean selected = logAllGraphEvents.isSelected();
    logNodeEvents.setSelected(selected);
    logEdgeEvents.setSelected(selected);
    logLabelEvents.setSelected(selected);
    logPortEvents.setSelected(selected);
    logBendEvents.setSelected(selected);
    logNodeBoundsEvents.setSelected(selected);
    logGraphRenderEvents.setSelected(selected);
  }



  private final ObservableList<ILogEntry> entries = FXCollections.observableArrayList();

  private void log(String message) {
    log(message, null);
  }

  private void log(String message, String type) {
    if (type == null) {
      type = message;
    }
    Message msg = new Message(LocalDateTime.now(), message, type);

    entries.add(0, msg);

    if (groupEvents.isSelected()) {
      mergeEventListener();
    }
  }

  private boolean log1(String message) {
    log(message);
    return true;
  }

  private void mergeEventListener() {
    mergeWithLatestGroup();
    createNewGroup();
  }

  private void mergeWithLatestGroup() {
    Optional<ILogEntry> latestGroupOpt = entries.stream().filter(m -> m instanceof MessageGroup).findFirst();
    if (!latestGroupOpt.isPresent()) {
      return;
    }

    MessageGroup latestGroup = (MessageGroup) latestGroupOpt.get();

    ArrayList<Message> precedingEvents = new ArrayList<>();
    for (ILogEntry next : this.entries) {
      if (!(next instanceof Message)) {
        break;
      }
      precedingEvents.add((Message) next);
    }

    int groupCount = latestGroup.getRepeatedMessages().size();
    if (precedingEvents.size() < groupCount) {
      return;
    }

    Stream<String> precedingTypes = precedingEvents.stream().map(Message::getType);

    Stream<String> groupTypes = latestGroup.getRepeatedMessages().stream().map(Message::getType);


    // Merge into group
    if (sequenceEquals(groupTypes, precedingTypes)) {
      latestGroup.getRepeatedMessages().clear();
      precedingEvents.forEach(latestGroup.getRepeatedMessages()::add);
      latestGroup.setRepeatCount(latestGroup.getRepeatCount()+1);
      for (int i = this.entries.indexOf(latestGroup) - 1; i >= 0; i--) {
        this.entries.remove(i);
      }
    }
  }

  /**
   * Convenience method that compares two streams for equality.
   */
  static boolean sequenceEquals(Stream<?> s1, Stream<?> s2) {
    Iterator<?> iter1 = s1.iterator(), iter2 = s2.iterator();
    while(iter1.hasNext() && iter2.hasNext())
      if (!(iter1.next().equals(iter2.next())))
        return false;
    return !iter1.hasNext() && !iter2.hasNext();
  }

  private void createNewGroup() {

    ArrayList<Message> ungroupedEvents = new ArrayList<>();
    for (ILogEntry next : this.entries) {
      if (!(next instanceof Message)) {
        break;
      }
      ungroupedEvents.add((Message) next);
    }

    for (int start = ungroupedEvents.size() - 1; start >= 1; start--) {
      for (int length = 1; start - 2 * length + 1 >= 0; length++) {
        Stream<String> types = ungroupedEvents.subList(start - length + 1, start + 1).stream().map(Message::getType);
        List<Message> preceding = ungroupedEvents.subList(start - 2 * length + 1, start - length + 1);
        Stream<String> precedingTypes = preceding.stream().map(Message::getType);
        if (sequenceEquals(types, precedingTypes)) {
          MessageGroup group = new MessageGroup();
          group.setRepeatCount(2);
          preceding.forEach(group.getRepeatedMessages()::add);
          this.entries.add(start + 1, group);
          for (int i = start; i >= start - 2 * length + 1; i--) {
            this.entries.remove(i);
          }
          return;
        }
      }
    }
  }

  private void clearLog() {
    entries.clear();
  }



  /**
   * A simply interface for different log entries. There are two: Simple messages, and message groups.
   */
  public interface ILogEntry {}

  public class Message implements ILogEntry {
    public LocalDateTime timeStamp;
    public String text;
    public String type;

    public Message(final LocalDateTime timeStamp, final String text, final String type) {
      this.timeStamp = timeStamp;
      this.text = text;
      this.type = type;
    }

    public LocalDateTime getTimeStamp() {
      return timeStamp;
    }

    public void setTimeStamp(final LocalDateTime timeStamp) {
      this.timeStamp = timeStamp;
    }

    public String getText() {
      return text;
    }

    public void setText(final String text) {
      this.text = text;
    }

    public String getType() {
      return type;
    }

    public void setType(final String type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return timeStamp.format(DateTimeFormatter.ISO_TIME) + " " + text;
    }
  }

  public class MessageGroup implements ILogEntry {
    private final ObservableList<Message> repeatedMessages = FXCollections.observableArrayList();
    private int repeatCount;

    public MessageGroup() {
      repeatedMessages.addListener((ListChangeListener<Message>) c -> OnPropertyChanged("RepeatedMessages"));
    }

    public ObservableList<Message> getRepeatedMessages() {
      return repeatedMessages;
    }

    public int getRepeatCount() {
      return repeatCount;
    }

    public void setRepeatCount(final int repeatCount) {
      this.repeatCount = repeatCount;
      OnPropertyChanged("RepeatCount");
    }

    public IEventHandler<PropertyChangedEventArgs> PropertyChanged;

    private void OnPropertyChanged(String propertyName) {
      if (PropertyChanged != null) {
        PropertyChanged.onEvent(this, new PropertyChangedEventArgs(propertyName));
      }
    }
  }


  /**
   * A cell renderer for the event log that creates a TitledPane for message groups
   * that contains another ListView with the messages of that group.
   * Normal messages are simply displayed as strings.
   */
  private class MessageListCell<T extends ILogEntry> extends ListCell<T> {
    // a good estimation of the height of a single row in the list view. needed for the calculation of the preferred height.
    final int ROW_HEIGHT = 24;
    @Override
    protected void updateItem(final T item, final boolean empty) {
      super.updateItem(item, empty);
      if (empty) {
        // nothing to display, reset everything.
        setText(null);
        setGraphic(null);
        setTooltip(null);
      } else {
        if (item instanceof Message) {
          // if its a simple message, display it as a string and remove any previously set graphics. Also set up a tooltip.
          setText(item.toString());
          setGraphic(null);
          setTooltip(new Tooltip(getText()));
        } else if (item instanceof MessageGroup) {
          MessageGroup messageGroup = (MessageGroup) item;
          ObservableList<Message> repeatedMessages = messageGroup.getRepeatedMessages();
          if (repeatedMessages.size() == 1) {
            setText(repeatedMessages.get(0) + " ("+messageGroup.getRepeatCount()+")");
            setTooltip(new Tooltip(getText()));
            setGraphic(null);
          } else {
            // If its a  message group containing more messages, we create a TitledPane that is collapsible,
            // display some information in the header and set a ListView containing the messages of that group in it.
            setText(null);
            setTooltip(null);
            TitledPane titledPane = new TitledPane();
            titledPane.setStyle("-fx-border-color: darkgray;");
            titledPane.setAnimated(false);
            titledPane.setText(repeatedMessages.size() + " Events, repeated " + messageGroup.getRepeatCount() + " times");
            titledPane.setExpanded(true);
            ListView<Message> messageListView = new ListView<>(repeatedMessages);
            // calculate the preferred height for the ListView based on the number of items in it.
            messageListView.setPrefHeight(repeatedMessages.size() * ROW_HEIGHT + 2);
            repeatedMessages.addListener((ListChangeListener<Message>) c -> messageListView.setPrefHeight(repeatedMessages.size() * ROW_HEIGHT + 2));
            messageListView.setCellFactory(listview -> new MessageListCell<>());
            titledPane.setContent(messageListView);
            setGraphic(titledPane);
          }
        }
      }
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
