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
package input.positionhandler;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IPositionHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

/**
 * Demo code that shows how to customize the movement behavior of INodes by implementing a custom {@link
 * IPositionHandler}.
 */
public class PositionHandlerDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;

  /**
   * Registers a callback function as decorator that provides a custom
   * {@link IPositionHandler} for each node.
   * <p>
   *   This callback function is called whenever a node in the graph is queried
   *   for its <code>IPositionHandler</code>. In this case, the 'node' parameter will be set
   *   to that node and the 'delegateHandler' parameter will be set to the
   *   position handler that would have been returned without setting this
   *   function as decorator.
   * </p>
   */
  private void registerPositionHandler(Rectangle boundaryRectangle) {
    NodeDecorator nodeDecorator = graphControl.getGraph().getDecorator().getNodeDecorator();
    nodeDecorator.getPositionHandlerDecorator().setImplementationWrapper((node, delegateHandler) ->
    {
      // Obtain the tag from the node
      Object nodeTag = node.getTag();

      // Check if it is a known tag and choose the respective implementation.
      // Fallback to the default behavior otherwise.
      if (Color.DARKORANGE.equals(nodeTag)) {
        // This implementation delegates certain behavior to the default implementation
        return new OrangePositionHandler(boundaryRectangle, node, delegateHandler);
      } else if (Color.FIREBRICK.equals(nodeTag)) {
        // A simple implementation that prohibits moving
        return new RedPositionHandler();
      } else if (Color.ROYALBLUE.equals(nodeTag)) {
        // Implementation that uses two levels of delegation to create a combined behavior
        return new OrangePositionHandler(boundaryRectangle, node, new GreenPositionHandler(delegateHandler));
      } else if (Color.FORESTGREEN.equals(nodeTag)) {
        // Another implementation that delegates certain behavior to the default implementation
        return new GreenPositionHandler(delegateHandler);
      } else {
        return delegateHandler;
      }
    });
  }

  /**
   * Initializes this demo by configuring the input mode and the model item lookup and creating an example graph together
   * with an enclosing rectangle some of the nodes may not move outside.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.rgb(153, 153, 153));
    graphControl.getGraph().getNodeDefaults().setStyle(nodeStyle);

    // initialize the input mode
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // do not allow creating nodes and edges
    inputMode.setCreateNodeAllowed(false);
    inputMode.setCreateEdgeAllowed(false);
    // do not allow deleting items
    inputMode.setDeletableItems(GraphItemTypes.NONE);
    // do not allow resizing nodes
    inputMode.setShowHandleItems(GraphItemTypes.NONE);
    // do not allow duplicating items
    inputMode.setDuplicateAllowed(false);
    // do not allow label editing
    inputMode.setEditLabelAllowed(false);
    // or copy/paste
    inputMode.setClipboardOperationsAllowed(false);
    graphControl.setInputMode(inputMode);

    // initialize the boundary rectangle
    Rectangle boundaryRectangle = new Rectangle(20, 20, 480, 400);
    // create a visual representation using a black border and a transparent fill
    new Pen(Color.BLACK, 2).styleShape(boundaryRectangle);
    boundaryRectangle.setFill(Color.TRANSPARENT);
    // and add it to the graph component
    graphControl.getRootGroup().addChild(boundaryRectangle, ICanvasObjectDescriptor.VISUAL);

    // initialize the graph
    createSampleGraph(graphControl.getGraph());

    // enable Undo/Redo for all edits after the initial graph has been constructed
    DefaultGraph defaultGraph = graphControl.getGraph().lookup(DefaultGraph.class);
    defaultGraph.setUndoEngineEnabled(true);

    // register custom provider implementations
    registerPositionHandler(boundaryRectangle);
  }

  /**
   * Creates the sample graph. Each node has a different color that indicates which {@link
   * IPositionHandler} is used.
   */
  private void createSampleGraph(IGraph graph) {
    createNode(graph, new RectD(100, 100, 100, 30), Color.FIREBRICK, Color.WHITESMOKE, "Unmovable");
    createNode(graph, new RectD(300, 100, 100, 30), Color.FORESTGREEN, Color.WHITESMOKE, "One Axis");
    createNode(graph, new RectD(80, 250, 140, 40), Color.DARKORANGE, Color.BLACK, "Limited to Rectangle");
    createNode(graph, new RectD(280, 250, 140, 40), Color.ROYALBLUE, Color.WHITESMOKE, "Limited to Rectangle\nand One Axis");
  }

  /**
   * Creates a sample node for this demo.
   */
  private static void createNode(IGraph graph, RectD bounds, Color fillColor, Color textColor, String labelText) {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(fillColor);
    INode node = graph.createNode(bounds, nodeStyle, fillColor);

    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(Font.font("System", FontWeight.BOLD, 12));
    labelStyle.setTextPaint(textColor);
    graph.addLabel(node, labelText, InteriorLabelModel.CENTER, labelStyle);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
