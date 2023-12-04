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
package input.contextmenu;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.MouseButtons;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.CommandMenuItem;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.WebViewUtils;

/**
 * Implement a dynamic popup menu for the nodes and for the background of a {@link com.yworks.yfiles.view.GraphControl}.
 */
public class ContextMenuDemo extends DemoApplication {

  public GraphControl graphControl;
  public WebView helpView;

  // the menu items which are defined in the fxml file.
  public CommandMenuItem cut;
  public CommandMenuItem copy;
  public CommandMenuItem paste;
  public CommandMenuItem delete;
  public CommandMenuItem selectAll;

  private long marqueeFinishedTime;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);

    // create a sample graph that contains just 3 simple nodes which can be selected and right clicked to show a popup menu
    IGraph graph = graphControl.getGraph();
    DemoStyles.initDemoStyles(graph);
    graph.getNodeDefaults().setSize(new SizeD(40, 40));
    graph.addLabel(graph.createNode(new PointD(100, 100)), "1");
    graph.addLabel(graph.createNode(new PointD(200, 100)), "2");
    graph.addLabel(graph.createNode(new PointD(300, 100)), "3");

    // register the GraphEditorInputMode as default input mode
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    graphControl.setInputMode(inputMode);

    // specify nodes as the types of the items that should be queried a popup menu for
    inputMode.setContextMenuItems(GraphItemTypes.NODE);
    // simple implementations with static popup menus could just assign a popup menu here:
    // inputMode.getPopupMenuInputMode().setMenu(menu);
    // however, we use a more dynamic popup menu here:
    // register an event handler that is called when a popup menu is about to be shown
    inputMode.addPopulateItemContextMenuListener((source, args) -> {
      // The marquee gesture is initiated on touch devices by a long touch press.
      // However, a long touch press also triggers a context menu request that
      // opens a context menu after the marque selection has been finished. In
      // order to prevent this, we check whether at least 100 milliseconds have
      // passed between the marque selection and the context menu request. In this
      // case, we assume that the user actually wants to open the context menu.
      if (System.currentTimeMillis() - marqueeFinishedTime > 100) {
        onPopulateItemContextMenu(source, args);
      }
    });

    // updates our timer when finishing or canceling the marquee gesture
    inputMode.getMarqueeSelectionInputMode().addDragFinishedListener((source, args) -> updateMarqueeFinishedTime());
    inputMode.getMarqueeSelectionInputMode().addDragCanceledListener((source, args) -> updateMarqueeFinishedTime());
  }

  /**
   * Sets the "marquee gesture finished" time stamp to now.
   */
  private void updateMarqueeFinishedTime() {
    marqueeFinishedTime = System.currentTimeMillis();
  }

  /**
   * Called when a popup menu is about to be shown and has to be populated. Here it is possible
   * to generate different popup menus depending on the situation. In our case we create one popup menu if at least
   * one node has been hit, and another if an empty spot on the canvas has been hit.
   */
  private void onPopulateItemContextMenu(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
    INode node = args.getItem() instanceof INode ? (INode) args.getItem() : null;

    // select the node that was hit or clear the selection if no node has been hit
    updateSelection(node);

    // The return type of the following method is Object to be able to support popup menus of different Java GUI
    // toolkits (see SwtDemo and the SwingDemo). By default this is an instance of JavaFX's ContextMenu.
    ContextMenu contextMenu = (ContextMenu) args.getMenu();

    // depending on the number of selected nodes populate the context menu ...
    if (graphControl.getSelection().getSelectedNodes().size() > 0) {
      // at least one node is selected so populate the context menu with the menu items defined above
      contextMenu.getItems().add(cut);
      contextMenu.getItems().add(copy);
      contextMenu.getItems().add(delete);
    } else {
      // no node has been hit
      contextMenu.getItems().add(selectAll);
      // items shall be pasted at the popup query location, so we use this location as parameter of the PASTE
      paste.setCommandParameter(args.getQueryLocation());
      contextMenu.getItems().add(paste);
    }

    // Per default when closing the context menu by clicking on the canvas, this click can also trigger a node creation.
    // Here we register some listener to prevent the node creation in this case.
    registerContextMenuListener(contextMenu);

    // make the menu show
    args.setShowingMenuRequested(true);
    // and mark the event as handled
    args.setHandled(true);
  }

  /**
   * Helper method that updates the node selection state when the popup menu is opened on a node.
   * @param node The node or <code>null</code>.
   */
  private void updateSelection(INode node) {
    // see if no node was hit
    if (node == null) {
      // clear the whole selection
      graphControl.getSelection().clear();
      // see if the node was selected already
    } else if (!graphControl.getSelection().getSelectedNodes().isSelected(node)) {
      // no - clear the remaining selection
      graphControl.getSelection().clear();
      // and select the node
      graphControl.getSelection().getSelectedNodes().setSelected(node, true);
      // also update the current item
      graphControl.setCurrentItem(node);
    } else {
      // node was selected already so do nothing.
    }
  }

  // region Prevent node creation when closing a ContextMenu by clicking on the canvas

  /**
   * The context menu is usually hidden by either selecting one of its options or clicking on the canvas or another item.
   * This method registers listener for all these cases and prevents node creation when the context menu is closed
   * by clicking on the canvas.
   */
  private void registerContextMenuListener(ContextMenu contextMenu) {
    final GraphEditorInputMode geim = (GraphEditorInputMode) graphControl.getInputMode();

    // remember when one of the menu items was clicked
    contextMenu.setOnAction(event -> contextActionTriggered = true);

    contextMenu.setOnHidden(event -> {
      if (!contextActionTriggered) {
        // the context menu was closed by clicking on the canvas or another item so we register the
        geim.addItemClickedListener(itemClickedListener);
        geim.addCanvasClickedListener(canvasClickConsumer);
      } else {
        // one of the menu items was clicked so there is no need to prevent any further clicks
        contextActionTriggered = false;
      }
    });
  }

  /**
   * This flag indicates, that the context menu was closed by choosing one of its menu items.
   */
  private boolean contextActionTriggered = false;

  /**
   * This click event handler consumes the canvas click which closed the context menu
   */
  private IEventHandler<ClickEventArgs> canvasClickConsumer = (source, args) -> {
    // consume the event by marking the event args as handled
    // except the context menu should be opened at another location
    if (!args.getMouseButtons().equals(MouseButtons.RIGHT)) {
      args.setHandled(true);
    }
    removeContextListener();
  };

  /**
   * This item clicked event handler is triggered when the context menu is closed by clicking on another graph item.
   * In this case, the item click should be handled normally so we just remove our event handlers again.
   */
  private IEventHandler<ItemClickedEventArgs<IModelItem>> itemClickedListener = (source, args) -> removeContextListener();

  private void removeContextListener() {
    final GraphEditorInputMode geim = (GraphEditorInputMode) graphControl.getInputMode();
    geim.removeItemClickedListener(itemClickedListener);
    geim.removeCanvasClickedListener(canvasClickConsumer);
  }

  // endregion

  public static void main(String[] args) {
    launch(args);
  }
}
