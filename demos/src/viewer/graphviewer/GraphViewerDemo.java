/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.graphviewer;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.IMapperRegistry;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GraphOverviewControl;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.view.ModifierKeys;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.StyleDecorationZoomPolicy;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.HoveredItemChangedEventArgs;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.NavigationInputMode;
import com.yworks.yfiles.view.input.QueryItemToolTipEventArgs;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Demonstrates how to display different graphs with additional information using a {@link
 * com.yworks.yfiles.view.GraphControl}.
 * It manages the different views on the graph including information about single nodes.
 */
public class GraphViewerDemo extends DemoApplication {
  public GraphControl graphControl;
  public ComboBox<String> graphChooserBox;
  public Button previousButton;
  public Button nextButton;
  public Label graphDescriptionLabel;
  public GraphOverviewControl graphOverviewControl;
  public WebView webView;
  public Label nodeLabelTextBlock;
  public Label nodeDescriptionTextBlock;
  public Hyperlink nodeUrlButton;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph is available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // enable folding
    final FoldingManager manager = new FoldingManager();
    // replace the displayed graph with a managed view
    graphControl.setGraph(manager.createFoldingView().getGraph());

    final IGraph graph = graphControl.getGraph();

    graphControl.setFileIOEnabled(true);

    // we register and create mappers for nodes and the graph to hold information about
    // the tooltips, descriptions, and associated urls
    final IMapperRegistry masterRegistry = graph.getFoldingView().getManager().getMasterGraph().getMapperRegistry();
    masterRegistry.createWeakMapper(INode.class, String.class, "ToolTip");
    masterRegistry.createWeakMapper(INode.class, String.class, "Description");
    masterRegistry.createWeakMapper(INode.class, String.class, "Url");
    masterRegistry.createWeakMapper(IGraph.class, String.class, "GraphDescription");

    // whenever the currentItem property on the graph changes, we want to get notified...
    // show the properties of the focused nodes in the properties view on the left
    graphControl.currentItemProperty().addListener((observableValue, iModelItem, iModelItem2) -> onCurrentItemChanged());

    // we have a viewer application, so we can use the GraphViewerInputMode
    GraphViewerInputMode graphViewerInputMode = new GraphViewerInputMode();
    graphViewerInputMode.setToolTipItems(GraphItemTypes.LABEL_OWNER);
    graphViewerInputMode.setClickableItems(GraphItemTypes.NODE);
    graphViewerInputMode.setFocusableItems(GraphItemTypes.NODE);
    graphViewerInputMode.setSelectableItems(GraphItemTypes.NONE);
    graphViewerInputMode.setMarqueeSelectableItems(GraphItemTypes.NONE);

    // when the mouse hovers for a longer time over an item we may optionally display a
    // tooltip. Use this callback for querying the tooltip contents.
    graphViewerInputMode.addQueryItemToolTipListener(this::onQueryItemToolTip);
    // if we click on an item we want to perform a custom action, so register a callback
    graphViewerInputMode.addItemClickedListener(this::onItemClicked);

    final NavigationInputMode navigationInputMode = graphViewerInputMode.getNavigationInputMode();
    // we want to enable the user to collapse and expand groups interactively, even though we
    // are just a "viewer" application
    navigationInputMode.setCollapseGroupAllowed(true);
    navigationInputMode.setExpandGroupAllowed(true);
    // we don't have selection enabled and thus the commands should use the "currentItem"
    // property instead - this property is changed when clicking on items or navigating via
    // the keyboard.
    navigationInputMode.setUsingCurrentItemForCommandsEnabled(true);
    // after expand/collapse/enter/exit operations - don't perform a fitContent operation to adjust
    // reachable area
    navigationInputMode.setFittingContentAfterGroupActionsEnabled(false);

    // also if someone clicked on an empty area we want to perform a custom group action
    graphViewerInputMode.getClickInputMode().addClickedListener(this::onClickInputModeOnClicked);

    // configure the mouse hover highlighting of elements
    initializeHighlighting(graphViewerInputMode);

    // initialize controls
    graphControl.setInputMode(graphViewerInputMode);

    graphChooserBox.getItems().addAll("computer-network", "movies", "family-tree", "hierarchy", "nesting",
        "social-network", "uml-diagram", "large-tree");

    graphOverviewControl.setGraphControl(graphControl);
    graphOverviewControl.setHorizontalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    graphOverviewControl.setVerticalScrollBarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    WebViewUtils.initHelp(webView, this);

    graphControl.fitGraphBounds();
  }

  /**
   * Adds highlighting to the graph elements whenever an element is hovered with the mouse.
   * For this, it registers style decorators for nodes and edges to the decorator
   * of the GraphControls graph instance.
   * @see IGraph#getDecorator()
   * @see NodeStyleDecorationInstaller
   * @see EdgeStyleDecorationInstaller
   * @see GraphViewerInputMode#getItemHoverInputMode()
   * @see com.yworks.yfiles.view.input.ItemHoverInputMode
   */
  private void initializeHighlighting(GraphViewerInputMode graphViewerInputMode) {
    // we want to create a non-default nice highlight styling
    // create a semi-transparent orange pen first
    Color orangeRed = Color.ORANGERED;
    Pen orangePen = new Pen(Color.color(orangeRed.getRed(), orangeRed.getGreen(), orangeRed.getBlue(), 0.7), 3);

    // now decorate the nodes and edges with custom highlight styles
    GraphDecorator decorator = this.graphControl.getGraph().getDecorator();

    // nodes should be given a rectangular orange rectangle highlight shape
    ShapeNodeStyle highlightShape = new ShapeNodeStyle();
    highlightShape.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    highlightShape.setPen(orangePen);
    // specifying a null paint has the important side effect of making the
    // interior of the rectangular shape "mouse transparent"
    highlightShape.setPaint(null);

    NodeStyleDecorationInstaller nodeStyleHighlight = new NodeStyleDecorationInstaller();
    nodeStyleHighlight.setNodeStyle(highlightShape);
    // that should be slightly larger than the real node
    nodeStyleHighlight.setMargins(new InsetsD(5));
    // but have a fixed size in the view coordinates
    nodeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);

    // register it as the default implementation for all nodes
    decorator.getNodeDecorator().getHighlightDecorator().setImplementation(nodeStyleHighlight);

    // a similar style for the edges...
    EdgeStyleDecorationInstaller edgeStyleHighlight = new EdgeStyleDecorationInstaller();
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(orangePen);
    // we cheat a little with transparent source / target arrows to make the highlight edge a little shorter than necessary.
    IArrow transparentArrow = new Arrow(ArrowType.NONE, null, Color.TRANSPARENT, 5, 1);
    edgeStyle.setSourceArrow(transparentArrow);
    edgeStyle.setTargetArrow(transparentArrow);
    edgeStyleHighlight.setEdgeStyle(edgeStyle);
    edgeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
    decorator.getEdgeDecorator().getHighlightDecorator().setImplementation(edgeStyleHighlight);


    // we want to get reports of the mouse being hovered over nodes and edges
    // first enable queries
    graphViewerInputMode.getItemHoverInputMode().setEnabled(true);
    // set the items to be reported
    graphViewerInputMode.getItemHoverInputMode().setHoverItems(GraphItemTypes.EDGE.or(GraphItemTypes.NODE));
    // if there are other items (most importantly labels) in front of edges or nodes
    // they should be discarded, rather than be reported as "null"
    graphViewerInputMode.getItemHoverInputMode().setInvalidItemsDiscardingEnabled(false);
    // whenever the currently hovered item changes call our method
    graphViewerInputMode.getItemHoverInputMode().addHoveredItemChangedListener(this::onHoveredItemChanged);
  }

  /**
   * Called when stage is shown because it needs the scene to be ready.
   */
  public void onLoaded() {
    graphChooserBox.getSelectionModel().select(0);
  }

  /**
   * Updates all description when another element of the graph gets focused.
   */
  private void onCurrentItemChanged() {
    final IModelItem currentItem = graphControl.getCurrentItem();
    if (currentItem instanceof INode) {
      final INode node = (INode) currentItem;
      nodeDescriptionTextBlock.setText(getDescriptionMapper().getValue(node));
      nodeLabelTextBlock.setText(node.getLabels().size() > 0 ? node.getLabels().getItem(0).getText() : "");
      final String url = getUrlMapper().getValue(node);
      if (url != null) {
        nodeUrlButton.setText(url);
        nodeUrlButton.setDisable(false);
      } else {
        nodeUrlButton.setText("");
        nodeUrlButton.setDisable(true);
      }
    } else {
      nodeDescriptionTextBlock.setText("");
      nodeLabelTextBlock.setText("");
      nodeUrlButton.setText("");
      nodeUrlButton.setDisable(true);
    }
  }

  /**
   * Called when the mouse hovers over a different item.
   * This method will be called whenever the mouse moves over a different item. We show a highlight indicator
   * to make it easier for the user to understand the graph's structure.
   */
  private void onHoveredItemChanged(Object sender, HoveredItemChangedEventArgs hoveredItemChangedEventArgs) {
    // we use the highlight manager of the GraphComponent to highlight related items
    HighlightIndicatorManager<IModelItem> manager = this.graphControl.getHighlightIndicatorManager();

    // first remove previous highlights, if present
    manager.clearHighlights();
    // then see where we are hovering over, now
    IModelItem newItem = hoveredItemChangedEventArgs.getItem();
    if (newItem != null) {
      // we highlight the item itself
      manager.addHighlight(newItem);
      if (newItem instanceof INode) {
        // and if it's a node, we highlight all adjacent edges, too
        this.graphControl.getGraph().edgesAt((INode)newItem).forEach(manager::addHighlight);
      } else if (newItem instanceof IEdge) {
        // if it's an edge - we highlight the adjacent nodes
        IEdge edge = (IEdge) newItem;
        manager.addHighlight(edge.getSourceNode());
        manager.addHighlight(edge.getTargetNode());
      }
    }
  }

  /**
   * Shows a tooltip for a node that is hovered for a certain amount of time.
   */
  private void onQueryItemToolTip(Object sender, QueryItemToolTipEventArgs<IModelItem> queryItemToolTipEventArgs) {
    if (queryItemToolTipEventArgs.getItem() instanceof INode && !queryItemToolTipEventArgs.isHandled()) {
      final INode node = (INode) queryItemToolTipEventArgs.getItem();
      final IMapper<INode, String> descriptionMapper = getDescriptionMapper();
      final String toolTipText = getToolTipMapper().getValue(node) != null
          ? getToolTipMapper().getValue(node)
          : (descriptionMapper != null ? descriptionMapper.getValue(node) : null);
      if (toolTipText != null) {
        queryItemToolTipEventArgs.setToolTip(new Tooltip(toolTipText));
        queryItemToolTipEventArgs.setHandled(true);
      }
    }
  }

  /**
   * Shows the content of a group or a dialog with information about the current node depending on what modifier was
   * pressed.
   */
  private void onItemClicked(Object sender, ItemClickedEventArgs<IModelItem> event) {
    if (event.getItem() instanceof INode) {
      graphControl.setCurrentItem(event.getItem());
      if (isEnterExitModifiers(graphControl.getLastMouse2DEvent().getModifiers())) {
        if (ICommand.ENTER_GROUP.canExecute(event.getItem(), graphControl)) {
          ICommand.ENTER_GROUP.execute(event.getItem(), graphControl);
          event.setHandled(true);
        }
      }
    }
  }

  /**
   * Returns from the content of a group/folder to the outer view when clicking on void space in the graph while
   * pressing modifiers shift and control.
   */
  private void onClickInputModeOnClicked(Object sender, ClickEventArgs args) {
    if (!graphControl.getGraphModelManager().hitElementsAt(args.getLocation()).enumerator().moveNext()) { // nothing hit
      if (isEnterExitModifiers(args.getModifiers())) {
        if (ICommand.EXIT_GROUP.canExecute(null, graphControl) && !args.isHandled()) {
          ICommand.EXIT_GROUP.execute(null, graphControl);
          args.setHandled(true);
        }
      }
    }
  }

  /**
   * Determines if the given modifier keys state represents the modifier
   * keys for entering a group node or exiting a folder node.
   */
  private static boolean isEnterExitModifiers( ModifierKeys state) {
    ModifierKeys modifiers = ModifierKeys.SHIFT.or(ModifierKeys.SHORTCUT);
    return state.and(modifiers).equals(modifiers);
  }

  /**
   * Reads a sample graph from a graphml file according currently selected entry in the graph chooser box.
   */
  private void readSampleGraph() {
    try {
      final String fileName = String.format("resources/%s.graphml",
          graphChooserBox.getSelectionModel().getSelectedItem());
      graphControl.getGraph().clear();
      final GraphMLIOHandler ioHandler = new GraphMLIOHandler();
      ioHandler.addRegistryInputMapper(INode.class, String.class, "Description");
      ioHandler.addRegistryInputMapper(INode.class, String.class, "ToolTip");
      ioHandler.addRegistryInputMapper(INode.class, String.class, "Url");
      ioHandler.addRegistryInputMapper(IGraph.class, String.class, "GraphDescription");
      ioHandler.read(graphControl.getGraph(), getClass().getResource(fileName).toExternalForm());

      graphDescriptionLabel.setText(
          getGraphDescriptionMapper().getValue(graphControl.getGraph().getFoldingView().getManager().getMasterGraph()));

      graphControl.fitGraphBounds(new InsetsD(10));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Updates the current graph and the buttons in the toolbar when the selected graph changes.
   */
  public void graphChanged() {
    readSampleGraph();
    updateButtons();
  }

  /**
   * Selects the previous graph in the list and updates the buttons in the toolbar.
   */
  public void previousButtonClicked() {
    final SingleSelectionModel<String> selectionModel = graphChooserBox.getSelectionModel();
    selectionModel.select(selectionModel.getSelectedIndex() - 1);
    updateButtons();
  }

  /**
   * Selects the next graph in the list and updates the buttons in the toolbar.
   */
  public void nextButtonClicked() {
    final SingleSelectionModel<String> selectionModel = graphChooserBox.getSelectionModel();
    selectionModel.select(selectionModel.getSelectedIndex() + 1);
    updateButtons();
  }

  /**
   * Updates the toolbar buttons. Buttons that would leave the range of samples are disabled.
   */
  private void updateButtons() {
    final SingleSelectionModel<String> selectionModel = graphChooserBox.getSelectionModel();
    nextButton.setDisable(selectionModel.getSelectedIndex() == graphChooserBox.getItems().size() - 1);
    previousButton.setDisable(selectionModel.getSelectedIndex() == 0);
  }

  /**
   * Opens a browser with the current url when the {@link Hyperlink} gets clicked.
   */
  public void nodeUrlLinkClicked() {
    BrowserLauncher launcher = BrowserLauncher.create();
    if (launcher != null && launcher.canLaunch()) {
      try {
        launcher.openLink(new URI(nodeUrlButton.getText()));
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Returns an {@link IMapper} that returns a graph description text or <code>null</code> for a key.
   */
  private IMapper<IGraph, String> getGraphDescriptionMapper() {
    return graphControl.getGraph().getMapperRegistry().getMapper(IGraph.class, String.class, "GraphDescription");
  }

  /**
   * Returns an {@link IMapper} that returns a description text or <code>null</code> for a key.
   */
  private IMapper<INode, String> getDescriptionMapper() {
    return graphControl.getGraph().getMapperRegistry().getMapper(INode.class, String.class, "Description");
  }

  /**
   * Returns an {@link IMapper} that returns a tooltip text or <code>null</code> for a key.
   */
  private IMapper<INode, String> getToolTipMapper() {
    return graphControl.getGraph().getMapperRegistry().getMapper(INode.class, String.class, "ToolTip");
  }

  /**
   * Returns an {@link IMapper} that returns an url string or <code>null</code> for a key.
   */
  private IMapper<INode, String> getUrlMapper() {
    return graphControl.getGraph().getMapperRegistry().getMapper(INode.class, String.class, "Url");
  }

  public static void main(String[] args) {
    launch(args);
  }
}
