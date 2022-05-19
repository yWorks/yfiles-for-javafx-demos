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
package input.sizeconstraintprovider;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
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
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;
import com.yworks.yfiles.view.input.NodeSizeConstraintProvider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

/**
 * Demo code that shows how to customize the resizing behavior of INodes by implementing a custom {@link
 * INodeSizeConstraintProvider}.
 */
public class SizeConstraintProviderDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;

  /**
   * Registers a callback function as decorator that provides a custom {@link INodeSizeConstraintProvider}
   * for each node.
   * <p>
   * This callback function is called whenever a node in the graph is queried
   * for its <code>ISizeConstraintProvider</code>. In this case, the 'node' parameter will be set
   * to that node.
   * </p>
   */
  private void registerSizeConstraintProvider(Rectangle boundaryRectangle) {
    // One shared instance that will be used by all blue nodes
    INodeSizeConstraintProvider blueSizeConstraintProvider = new BlueSizeConstraintProvider();

    NodeDecorator nodeDecorator = graphControl.getGraph().getDecorator().getNodeDecorator();
    nodeDecorator.getSizeConstraintProviderDecorator().setFactory(
        node -> {
          // Obtain the tag from the node
          Object nodeTag = node.getTag();

          // Check if it is a known tag and choose the respective implementation.
          // Fallback to the default behavior otherwise.
          if (Color.ROYALBLUE.equals(nodeTag)) {
            return blueSizeConstraintProvider;
          } else if (Color.FORESTGREEN.equals(nodeTag)) {
            return new GreenSizeConstraintProvider();
          } else if (Color.DARKORANGE.equals(nodeTag)) {
            return new NodeSizeConstraintProvider(
                new SizeD(50, 50),
                new SizeD(300, 300),
                new RectD(boundaryRectangle.getX(), boundaryRectangle.getY(), boundaryRectangle.getWidth(),
                    boundaryRectangle.getHeight()));
          } else {
            return null;
          }
        });
  }

  /**
   * Initializes this demo by configuring the input mode and the model item lookup and creating an example graph together
   * with a boundary rectangle limiting the movement of some nodes.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.rgb(153, 153, 153));
    graphControl.getGraph().getNodeDefaults().setStyle(nodeStyle);

    // Initialize the input mode
    initializeInputMode();

    // Create the rectangle that limits the movement of some nodes
    Rectangle boundaryRectangle = new Rectangle(210, 350, 30, 30);
    // and add a Visual to the GraphControl to represent the rectangle using a black border and a transparent fill
    new Pen(Color.BLACK, 2).styleShape(boundaryRectangle);
    boundaryRectangle.setFill(Color.TRANSPARENT);
    graphControl.getRootGroup().addChild(boundaryRectangle, ICanvasObjectDescriptor.VISUAL);

    // Initialize the graph
    createSampleGraph(graphControl.getGraph());

    // Enable Undo/Redo for all edits after the initial graph has been constructed
    DefaultGraph defaultGraph = graphControl.getGraph().lookup(DefaultGraph.class);
    defaultGraph.setUndoEngineEnabled(true);

    // Register custom provider implementations
    registerSizeConstraintProvider(boundaryRectangle);
  }

  private void initializeInputMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    // do not allow for moving any graph items
    mode.setMovableItems(GraphItemTypes.NONE);
    // do not allow for creating nodes and edges
    mode.setCreateNodeAllowed(false);
    mode.setCreateEdgeAllowed(false);
    // do not allow to delete any element
    mode.setDeletableItems(GraphItemTypes.NONE);
    graphControl.setInputMode(mode);
  }

  /**
   * Creates the sample graph of this demo.
   */
  private void createSampleGraph(IGraph graph) {
    createNode(graph, new RectD(100, 100, 100, 60), Color.ROYALBLUE, Color.WHITESMOKE, "Never Shrink\n(Max 3x)");
    createNode(graph, new RectD(300, 100, 160, 30), Color.ROYALBLUE, Color.WHITESMOKE, "Never Shrink (Max 3x)");
    createNode(graph, new RectD(100, 215, 100, 30), Color.FORESTGREEN, Color.WHITESMOKE, "Enclose Label");
    createNode(graph, new RectD(300, 200, 140, 80), Color.FORESTGREEN, Color.WHITESMOKE, "Enclose Label,\nEven Large Ones");
    createNode(graph, new RectD(200, 340, 140, 140), Color.DARKORANGE, Color.BLACK, "Encompass Rectangle,\nMin and Max Size");
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