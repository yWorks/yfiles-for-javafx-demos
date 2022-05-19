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
package com.yworks.yedlite.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.swt.widgets.Composite;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.algorithms.GraphConnectivity;
import com.yworks.yfiles.algorithms.NodeList;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.GraphCopier;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.graph.NodeEventArgs;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.YGraphAdapter;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.ItemSelectionChangedEventArgs;
import com.yworks.yfiles.view.StyleDecorationZoomPolicy;

/**
 * A widget that can be used together with a {@link GraphControl}
 * or an {@link IGraph} to display the neighborhood of a node.
 */
public class ContextNeighborhoodPart {

  @Inject
  IEclipseContext ctx;

  private Composite parent;

  @PostConstruct
  public void createPartControl(Composite parent) {
    FXCanvas canvas = new FXCanvas(parent, 0);
    canvas.setScene(createFxScene());
    }

  private Scene createFxScene() {
    neighborhoodControl = new GraphControl();
    neighborhoodControl.fitGraphBounds();

    setMaxDistance(1);
    setMaxSelectedNodesCount(25);
    setShowHighlight(true);
    setInsets(new InsetsD(5));
    autoUpdatesEnabled = true;

    initializeInputMode();
    initializeHighlightStyle();

    // on clicking a node, this node is selected and the source GraphControl is scrolled to center it
    clickCallback = (sender, args) -> {
      INode node = args.getItem();
          getGraphControl().setCurrentItem(node);
          getGraphControl().getSelection().clear();
          getGraphControl().getSelection().setSelected(node, true);
          PointD nodeCenter = node.getLayout().getCenter();
          getGraphControl().zoomToAnimated(nodeCenter, getGraphControl().getZoom());
        };

    ctx.getParent().runAndTrack(new RunAndTrack() {

      @Override
      public boolean changed(IEclipseContext context) {
        setGraphControl(ContextUtils.getGraphControl(context));
        return true;
      }
    });

    // fit the graph bounds as soon as the control has its size
    neighborhoodControl.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
      @Override
      public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
        if (newValue.getWidth() > 0 && newValue.getHeight() > 0) {
          neighborhoodControl.fitGraphBounds(getInsets());
          neighborhoodControl.boundsInParentProperty().removeListener(this);
        }
      }
    });

    return new Scene(new BorderPane(neighborhoodControl));
  }

  /**
   * The GraphControl of the neighborhood graph.
   */
  private GraphControl neighborhoodControl;

  /**
   * The source graph.
   */
  private IGraph graph;

  /**
   * Backing field for the GraphControl of the source graph.
   */
  private GraphControl graphControl;

  /**
   * Gets the GraphControl whose graph is displayed in this view.
   */
  public GraphControl getGraphControl() {
    return graphControl;
  }

  public void setGraphControl(GraphControl value)  {
    if (graphControl != null) {
      graphControl.graphProperty().removeListener(graphChangedListener);
      if (usingSelection) {
        uninstallItemSelectionChangedListener();
      }
    }
    graphControl = value;
    if (graph != null) {
      uninstallEditListeners();
    }
    if (graphControl != null) {
      graphControl.graphProperty().addListener(graphChangedListener);
      if (usingSelection) {
        installItemSelectionChangedListener();
      }
      selectedNodes = toList(graphControl.getSelection().getSelectedNodes());
      graph = graphControl.getGraph();
      setGraph(graph);
    } else {
      graph = null;
    }
  }

  /**
   * Sets the graph that's currently displayed by the neighborhood view.
   * @param value
   */
  private void setGraph(IGraph value){
    if (graph != null) {
      uninstallEditListeners();
    }
    graph = value;
    if (graph != null) {
      installEditListeners();
    }
    update();
  }

  /**
   * Determines the method used for neighborhood computation.
   */
  protected NeighborhoodModes getNeighborhoodMode() {
    return NeighborhoodModes.Neighborhood;
  }

  /**
   * The maximum distance.
   */
  private int maxDistance;

  /**
   * The maximum number of selected nodes.
   */
  private int maxSelectedNodesCount;

  /**
   * The maximum distance the shortest path to another node may have so that this node is still considered to be
   * in the neighborhood.
   */
  public int getMaxDistance() {
    return maxDistance;
  }

  /**
   * The maximum distance the shortest path to another node may have so that this node is still considered to be
   * in the neighborhood.
   */
  public void setMaxDistance(int maxDistance) {
    if (this.maxDistance != maxDistance) {
      this.maxDistance = maxDistance;
      update();
    }
  }

  /**
   * The maximum number of nodes that may be selected.
   */
  public int getMaxSelectedNodesCount() {
    return maxSelectedNodesCount;
  }

  /**
   * The maximum number of nodes that may be selected.
   */
  public void setMaxSelectedNodesCount(int maxSelectedNodesCount) {
    if (this.maxSelectedNodesCount != maxSelectedNodesCount){
      this.maxSelectedNodesCount = maxSelectedNodesCount;
      update();
    }
  }

  /**
   * The insets are applied to the graphControl of this view.
   */
  public InsetsD insets;

  /**
   * Determines if the current root node should be highlighted.
   */
  public boolean showHighlight;

  /**
   * Backing field for the highlight style.
   */
  private INodeStyle highlightStyle;

  /**
   * The insets are applied to the graphControl of this view.
   */
  public InsetsD getInsets() {
    return insets;
  }

  /**
   * The insets are applied to the graphControl of this view.
   */
  public void setInsets(InsetsD insets) {
    this.insets = insets;
  }

  /**
   * Determines if the current root node should be highlighted.
   */
  public boolean getShowHighlight() {
    return showHighlight;
  }

  /**
   * Sets if the current root node should be highlighted.
   */
  public void setShowHighlight(boolean showHighlight) {
    this.showHighlight = showHighlight;
  }

  /**
   * The style of the highlighted node.
   */
  public INodeStyle getHighlightStyle() {
    return highlightStyle;
  }

  /**
   * The style of the highlighted node.
   */
  public void setHighlightStyle(INodeStyle highlightStyle) {
    if (this.highlightStyle != highlightStyle) {
      this.highlightStyle = highlightStyle;
      installHighlightStyle(highlightStyle);
    }
  }

  /**
   * The currently selected nodes.
   */
  private List<INode> selectedNodes;

  /**
   * The currently selected nodes.
   */
  public List<INode> getSelectedNodes() {
    return selectedNodes;
  }

  /**
   * The currently selected nodes.
   */
  public void setSelectedNodes(List<INode> selectedNodes) {
    if (this.selectedNodes != selectedNodes) {
      this.selectedNodes = selectedNodes;
      update();
    }
  }

  /**
   * Backing field for the selection synchronization.
   */
  private boolean usingSelection = true;

  /**
   * Gets whether to automatically synchronize the {@link GraphControl}'s selection to the
   * selected nodes of the neighborhood view.
   * The default is <code>true<code/>.
   * The view is only updated automatically if auto updates
   * are enabled.
   */
  public boolean isUsingSelection() {
    return usingSelection;
  }

  /**
   * Sets whether to automatically synchronize the {@link GraphControl}'s selection to the
   * selected nodes of the neighborhood view.
   * The default is <code>true<code/>.
   * The view is only updated automatically if auto updates
   * are enabled.
   */
  public void setUsingSelection(boolean usingSelection) {
    if (this.usingSelection == usingSelection) {
      this.usingSelection = usingSelection;
      if (usingSelection) {
        if(graphControl != null) {
          selectedNodes = toList(graphControl.getSelection().getSelectedNodes());
        }
        installItemSelectionChangedListener();
      } else {
        uninstallItemSelectionChangedListener();
      }
    }

  }

  /**
   * Whether to update the neighborhood view when the graph has been edited.
   */
  public boolean autoUpdatesEnabled;

  /**
   * A callback that is invoked on a click in the neighborhood graph with the
   * node of the original graph as parameter.
   */
  public IEventHandler<ItemEventArgs<INode>> clickCallback;

  /**
   * Maps nodes in NeighborhoodControl's graph to nodes in SourceGraph.
   */
  private Map<INode, INode> originalNodes;

  /**
   * Installs listeners such that the neighborhood control is updated if the
   * source graph is edited.
   */
  private void installEditListeners() {
    if (graph == null) {
      return;
    }

    graph.addNodeCreatedListener(onElementEditedHandler);
    graph.addNodeRemovedListener(onNodeRemovedHandler);
    graph.addNodeStyleChangedListener(onElementEditedHandler);
    graph.addEdgeCreatedListener(onElementEditedHandler);
    graph.addEdgeRemovedListener(onElementEditedHandler);
    graph.addEdgeStyleChangedListener(onElementEditedHandler);
    graph.addPortAddedListener(onElementEditedHandler);
    graph.addPortRemovedListener(onElementEditedHandler);
    graph.addPortStyleChangedListener(onElementEditedHandler);
    graph.addLabelAddedListener(onElementEditedHandler);
    graph.addLabelRemovedListener(onElementEditedHandler);
    graph.addLabelStyleChangedListener(onElementEditedHandler);

    graph.addParentChangedListener(onElementEditedHandler);
    graph.addIsGroupNodeChangedListener(onElementEditedHandler);
  }

  /**
   * Removes the edit listeners.
   */
  private void uninstallEditListeners() {
    if (graph == null) {
      return;
    }
    graph.removeNodeCreatedListener(onElementEditedHandler);
    graph.removeNodeRemovedListener(onNodeRemovedHandler);
    graph.removeNodeStyleChangedListener(onElementEditedHandler);
    graph.removeEdgeCreatedListener(onElementEditedHandler);
    graph.removeEdgeRemovedListener(onElementEditedHandler);
    graph.removeEdgeStyleChangedListener(onElementEditedHandler);
    graph.removePortAddedListener(onElementEditedHandler);
    graph.removePortRemovedListener(onElementEditedHandler);
    graph.removePortStyleChangedListener(onElementEditedHandler);
    graph.removeLabelAddedListener(onElementEditedHandler);
    graph.removeLabelRemovedListener(onElementEditedHandler);
    graph.removeLabelStyleChangedListener(onElementEditedHandler);

    graph.removeParentChangedListener(onElementEditedHandler);
    graph.removeIsGroupNodeChangedListener(onElementEditedHandler);
  }

  private IEventHandler onElementEditedHandler = new IEventHandler() {
    @Override
    public void onEvent( final Object source, final IEventArgs args ) {
      if (autoUpdatesEnabled) {
        update();
      }
    }
  };
  private IEventHandler<NodeEventArgs> onNodeRemovedHandler = new IEventHandler<NodeEventArgs>(){
    @Override
    public void onEvent( final Object source, final NodeEventArgs args ) {
      if (!autoUpdatesEnabled) {
        return;
      }
      if (autoUpdatesEnabled) {
        selectedNodes = toList(graphControl.getSelection().getSelectedNodes());
      }
      update();
    }
  };

  /**
   * Called whenever the selection changes.
   */
  private void onItemSelectionChanged(Object sender, ItemEventArgs<IModelItem> itemEventArgs) {
    if(itemEventArgs.getItem() instanceof INode) {
      if (autoUpdatesEnabled) {
        IGraphSelection selection = graphControl.getSelection();
        selectedNodes = toList(selection.getSelectedNodes());
        update();
      }
    }
  }

  private IEventHandler<ItemSelectionChangedEventArgs<IModelItem>> onItemSelectionChangedtHandler = new IEventHandler<ItemSelectionChangedEventArgs<IModelItem>>() {
    @Override
    public void onEvent(Object source, ItemSelectionChangedEventArgs<IModelItem> args) {
      onItemSelectionChanged(source, args);
    }
  };

  /**
   * Installs the selection listeners.
   */
  private void installItemSelectionChangedListener() {
    if (graphControl != null) {
      graphControl.getSelection().addItemSelectionChangedListener(onItemSelectionChangedtHandler);
    }
  }

  /**
   * Uninstalls the selection listeners.
   */
  private void uninstallItemSelectionChangedListener() {
    if (graphControl != null) {
      graphControl.getSelection().removeItemSelectionChangedListener(onItemSelectionChangedtHandler);
    }
  }

  /**
   *  Called when the graph property of the source graph is changed.
   */
  private ChangeListener<IGraph> graphChangedListener = new ChangeListener<IGraph>() {
    @Override
    public void changed(ObservableValue<? extends IGraph> observable, IGraph oldValue, IGraph newValue) {
      setGraph(graphControl.getGraph());
    }
  };

  /**
   * Creates and installs the default highlight style.
   */
  private void initializeHighlightStyle() {
    // create semi transparent orange pen first
    Color orangeRed = Color.ORANGERED;
    Pen orangePen = new Pen(new Color(orangeRed.getRed(), orangeRed.getGreen(), orangeRed.getBlue(), 220/255d), 3);
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setShape(ShapeNodeShape.RECTANGLE);
    style.setPaint(Color.TRANSPARENT);
    style.setPen(orangePen);
    setHighlightStyle(style);

    // configure the highlight decoration installer
    installHighlightStyle(getHighlightStyle());
  }

  /**
   * Installs the given highlight style as node decorator.
   */
  private void installHighlightStyle(INodeStyle highlightStyle) {
    NodeStyleDecorationInstaller nodeStyleHighlight = new NodeStyleDecorationInstaller();
    nodeStyleHighlight.setNodeStyle(highlightStyle);
    // that should be slightly larger than the real node
    nodeStyleHighlight.setMargins(new InsetsD(5));
    // but have a fixed size in the view coordinates
    nodeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
    // register it as the default implementation for all nodes
    GraphDecorator decorator = neighborhoodControl.getGraph().getDecorator();
    decorator.getNodeDecorator().getHighlightDecorator().setImplementation(nodeStyleHighlight);
  }

  private void initializeInputMode() {
    // We disable focus, selection and marquee selection so the
    // control will display the plain graph without focus and
    // selection boundaries.
    GraphViewerInputMode graphViewerInputMode = new GraphViewerInputMode();

    graphViewerInputMode.setClickableItems(GraphItemTypes.NODE);
    graphViewerInputMode.setFocusableItems(GraphItemTypes.NONE);
    graphViewerInputMode.setSelectableItems(GraphItemTypes.NONE);
    graphViewerInputMode.setMarqueeSelectableItems(GraphItemTypes.NONE);

    // Disable collapsing and expanding of groups
    graphViewerInputMode.getNavigationInputMode().setCollapseGroupAllowed(false);
    graphViewerInputMode.getNavigationInputMode().setExpandGroupAllowed(false);
    graphViewerInputMode.getNavigationInputMode().setUsingCurrentItemForCommandsEnabled(true);
    graphViewerInputMode.getMoveViewportInputMode().setEnabled(false);
    graphViewerInputMode.getNavigationInputMode().setEnabled(false);

    // If an item is clicked, we want the view to show the neighborhood
    // of the clicked node, and invoke the click callback with the original
    // node.
    graphViewerInputMode.addItemClickedListener((sender, e) -> {
      if (getNeighborhoodMode() != NeighborhoodModes.FolderContents) {
        if (e.getItem() instanceof INode) {
          INode node = (INode)e.getItem();
          INode originalNode = originalNodes.get(node);
          List<INode> selected = new ArrayList<INode>();
          selected.add(originalNode);
          setSelectedNodes(selected);
          if (clickCallback != null) {
            clickCallback.onEvent(this, new ItemEventArgs<INode>(originalNode));
          }
        }
      }
    });

    neighborhoodControl.setInputMode(graphViewerInputMode);
  }

  /**
   * Updates the neighborhood view.
   * <p>
   * If {@link #autoUpdatesEnabled} is enabled, this method is called automatically after the graph has been edited.
   * It filters the source graph and calculates a layout based on the value set in {@link #getNeighborhoodMode()}.
   * </p>
   * @see #autoUpdatesEnabled
   * @see #isUsingSelection()
   */
  public void update() {
    neighborhoodControl.getGraph().clear();
    if (graph == null || selectedNodes == null || selectedNodes.size() == 0 || selectedNodes.size() > getMaxSelectedNodesCount()) {
      return;
    }

    IFoldingView foldedGraph = graph.getFoldingView();
    originalNodes = new HashMap<INode, INode>();
    Set<INode> nodesToCopy = new HashSet<INode>();

    // Use one of our analysis algorithms to find the predecessors/successors
    YGraphAdapter adapter = new YGraphAdapter(graph);

    // Create a list of start nodes.
    NodeList startNodes = new NodeList();
    for (INode node : selectedNodes) {
      startNodes.push(adapter.getCopiedNode(node));
    }

    IListEnumerable<INode> enumerable = null;

    IGraph sourceGraph = graph;
    List<INode> copiedStartNodes = new ArrayList<INode>();

    if (getNeighborhoodMode() != NeighborhoodModes.FolderContents) {

      for (INode node : selectedNodes) {
        nodesToCopy.add(node);
      }

      switch (getNeighborhoodMode()) {
      case Neighborhood:
        // Get direct and indirect neighbors of root node
        enumerable =
        adapter.createNodeEnumerable(GraphConnectivity.getNeighbors(adapter.getYGraph(), startNodes, getMaxDistance()));
        break;
      case Predecessors:
        // Get predecessors of root node
        enumerable =
        adapter.createNodeEnumerable(GraphConnectivity.getPredecessors(adapter.getYGraph(), startNodes, getMaxDistance()));
        break;
      case Successors:
        // Get successors of root node
        enumerable =
        adapter.createNodeEnumerable(GraphConnectivity.getSuccessors(adapter.getYGraph(), startNodes, getMaxDistance()));
        break;
      }

      if (enumerable != null) {
        for(INode n : enumerable) {
          nodesToCopy.add(n);
        }
      }

      // Use GraphCopier to copy the nodes inside the neighborhood into the NeighborhoodControl's graph.
      // Also, create the mapping of the copied nodes to original nodes inside the SourceGraph.
      GraphCopier graphCopier = new GraphCopier();
      graphCopier.copy(
          sourceGraph,
          item -> !(item instanceof INode) || nodesToCopy.contains((INode)item),
          neighborhoodControl.getGraph(),
          PointD.ORIGIN,
          (original, copy) -> {
            if (original instanceof INode) {
              originalNodes.put((INode)copy, (INode)original);
              if (selectedNodes.contains(original)) {
                copiedStartNodes.add((INode)copy);
              }
            }
          });

    } else {

      if (selectedNodes.size() > 1) {
        for(INode node : selectedNodes) {
          nodesToCopy.add(foldedGraph.getMasterItem(node));
        }
      }

      // Get descendants of root nodes.
      if (foldedGraph != null) {
        IGraph masterGraph = foldedGraph.getManager().getMasterGraph();
        for(INode node : selectedNodes) {
          enumerable = masterGraph.getChildren(foldedGraph.getMasterItem(node));
          if (enumerable != null) {
            for(INode n : enumerable) {
              nodesToCopy.add(n);
            }
          }
        }
        sourceGraph = masterGraph;

        // Use GraphCopier to copy the nodes inside the neighborhood into the NeighborhoodControl's graph.
        // Also, create the mapping of the copied nodes to original nodes inside the SourceGraph.
        // Include only edges that are descendants of the same root node.
        GraphCopier graphCopier = new GraphCopier();
        graphCopier.copy(
          sourceGraph, item -> {
            if (item instanceof IEdge) {
              IEdge edge = (IEdge)item;
              boolean intraComponentEdge = false;
              for(INode node : selectedNodes) {
                INode masterNode = foldedGraph.getMasterItem(node);
                if (masterGraph.getParent(edge.getSourceNode()) == masterNode &&
                    masterGraph.getParent(edge.getTargetNode()) == masterNode)
                  intraComponentEdge = true;
              }
              return intraComponentEdge;
            }
            return !(item instanceof INode) || nodesToCopy.contains((INode)item);
          },
          neighborhoodControl.getGraph(),
          PointD.ORIGIN,
            (original, copy) -> {
              if (original instanceof INode) {
                originalNodes.put((INode)copy, (INode)original);
                if (selectedNodes.contains(original)) {
                  copiedStartNodes.add((INode)copy);
                }
              }
            });
      }
    }

    // Layout the neighborhood graph using hierarchic layout.
    if (getNeighborhoodMode() == NeighborhoodModes.FolderContents) {
      if (selectedNodes.size() > 1) {
        LayoutUtilities.applyLayout(neighborhoodControl.getGraph(), new ComponentLayout());
      }
    } else{
      LayoutUtilities.applyLayout(neighborhoodControl.getGraph(), new HierarchicLayout());
    }

    // Highlight the root node in the neighborhood graph.
    if (getShowHighlight() && copiedStartNodes.size() > 0) {
      HighlightIndicatorManager<IModelItem> manager = neighborhoodControl.getHighlightIndicatorManager();
      manager.clearHighlights();
      for(INode startNode : copiedStartNodes) {
        manager.addHighlight(startNode);
      }
    }

    // Make the neighborhood graph fit inside the NeighborhoodControl.
    neighborhoodControl.fitGraphBounds(getInsets());
  }

  private static List<INode> toList( Iterable<INode> iterable ) {
    ArrayList<INode> list = new ArrayList<>();
    for (INode node : iterable) {
      list.add(node);
    }
    return list;
  }

  /**
   * Enumeration that holds the different modes of the NeighborhoodView
   */
  public enum NeighborhoodModes
  {
    /**
     * Get direct and indirect neighbors of root node.
     */
    Neighborhood,

    /**
     * Get predecessors of root node.
     */
    Predecessors,

    /**
     * Get successors of root node.
     */
    Successors,

    /**
     * Shows the folder content of folder root node.
     */
    FolderContents
  }
}
