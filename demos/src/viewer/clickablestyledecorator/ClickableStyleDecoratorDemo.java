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
package viewer.clickablestyledecorator;

import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import javafx.scene.control.Alert;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

/**
 * Shows how to handle mouse clicks in specific areas of a node's visualization.
 */
public class ClickableStyleDecoratorDemo extends DemoApplication {
  public WebView helpView;
  public GraphControl graphControl;

  /**
   * Initializes the demo.
   */
  public void initialize() {
    // initialize graph interaction
    initializeInputMode();

    // loads an initial sample graph
    loadGraph();

    graphControl.getGraph().setUndoEngineEnabled(true);

    WebViewUtils.initHelp(helpView, this);
  }

  /**
   * Loads an initial sample graph.
   */
  private void loadGraph() {
    try {
      GraphMLIOHandler reader = graphControl.getGraphMLIOHandler();
      reader.addXamlNamespaceMapping(
              "http://www.yworks.com/yfiles-for-javafx/demos/ClickableStyleDecorator/1.0",
              NodeStyleDecorator.class);
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Centers the graph in the visible area.
   */
  @Override
  public void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Configures user interaction.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();

    geim.addItemClickedListener(this::onClicked);

    graphControl.setInputMode(geim);
  }

  /**
   * Handles click events (that occur inside the decoration image of this
   * click listener's associated node).
   * <p>
   * This example implementaton opens a message dialog informing the user
   * about the handled click.
   * </p>
   */
  private void onClicked( Object src, ItemClickedEventArgs<IModelItem> args ) {
    // return if it is not a node
    if (!(args.getItem() instanceof INode)) {
      return;
    }
    INode node = (INode) args.getItem();
    // return if it is not a decorated node or if it is decorated but the click location wasn't in the decoration image
    if (!(node.getStyle() instanceof  NodeStyleDecorator) ||
        !((NodeStyleDecorator) node.getStyle()).getDecorationLayout(node).contains(args.getLocation())) {
      return;
    }
    // decorated node was clicked, show message
    args.setHandled(true);

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setHeaderText(null);
    alert.setContentText("Decorator clicked");
    alert.showAndWait();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
