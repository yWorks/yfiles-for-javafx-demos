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
package viewer.tooltips;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.MouseHoverInputMode;
import com.yworks.yfiles.view.input.QueryItemToolTipEventArgs;
import com.yworks.yfiles.view.input.ToolTipQueryEventArgs;
import javafx.scene.control.Tooltip;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.time.Duration;

/**
 * This demo shows how to add tooltips to graph items.
 */
public class TooltipsDemo extends DemoApplication {

  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // includes the help
    WebViewUtils.initHelp(helpView, this);

    // configure the interaction
    GraphEditorInputMode geim = initializeInputMode();

    // initializes the tooltips
    initializeTooltips(geim);

    // load a sample graph
    loadGraph();

    // enable undoability
    enableUndo();
  }

  /**
   * Dynamic tooltips are managed by {@link MouseHoverInputMode} that dispatches
   * {@link MouseHoverInputMode#addQueryToolTipListener QueryToolTip} events for
   * hovered {@link ToolTipQueryEventArgs#getQueryLocation() locations} (in world coordinates)
   * and displays the {@link ToolTipQueryEventArgs#setToolTip(Tooltip)}  tooltip set on the event args}.
   * <p>
   * The {@link ToolTipQueryEventArgs#setHandled(boolean) Handled} property of the event args is a flag which indicates
   * whether the tooltip was already set by one of possibly several tooltip providers.
   * </p>
   * <p>
   * To simplify displaying tooltips for {@link IModelItem}s, {@link GraphEditorInputMode}
   * listens to the original event, checks for an {@link IModelItem} at the query location
   * and dispatches its {@link GraphEditorInputMode#addQueryItemToolTipListener(IEventHandler)}  QueryItemToolTip}
   * event that not only contains the Handled, QueryLocation, and ToolTip properties, but also the
   * hit {@link QueryItemToolTipEventArgs#getItem() Item}.
   * </p>
   * @param geim The input mode that is used to edit the graph.
   */
  private void initializeTooltips(GraphEditorInputMode geim) {
    // customize the tooltip's behavior to our liking
    geim.getMouseHoverInputMode().setToolTipLocationOffset(new PointD(15, 15));
    geim.getMouseHoverInputMode().setDelay(Duration.ofMillis(500));
    geim.getMouseHoverInputMode().setDuration(Duration.ofSeconds(5));

    // register a listener for when a tooltip should be shown
    geim.addQueryItemToolTipListener((source, args) -> {
      if (args.isHandled()) {
        // a tooltip has already been assigned -> nothing to do
        return;
      }

      // depending on the item, creates a text for the tooltips
      String text = "";
      if (args.getItem() instanceof INode) {
        text = "Node Tooltip";
      } else if (args.getItem() instanceof IEdge) {
        text = "Edge Tooltip";
      } else if (args.getItem() instanceof IPort) {
        text = "Port Tooltip";
      } else if (args.getItem() instanceof ILabel) {
        text = "Label Tooltip";
      }

      // extends the text with label information if available
      IModelItem item = args.getItem();
      if (item instanceof INode || item instanceof IEdge || item instanceof IPort) {
        ILabelOwner owner = (ILabelOwner) item;
        if (owner.getLabels().size() > 0) {
          text += "\n" + owner.getLabels().first().getText();
        }
      } else if (item instanceof ILabel) {
        text += "\n" + ((ILabel) item).getText();
      }

      // create the tooltip
      args.setToolTip(new Tooltip(text));

      // indicate that the tooltip has been set
      args.setHandled(true);
    });
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
   * Initializes the input mode for the demo.
   */
  private GraphEditorInputMode initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setGroupingOperationsAllowed(true);
    graphControl.setInputMode(geim);
    return geim;
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
    graphControl.fitGraphBounds();
  }

  public static void main( String[] args ) {
    launch(args);
  }
}
