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
package style.simplecustomstyle;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.util.Random;

/**
 * This demo show how to create and use a relatively simple, non-interactive custom style for nodes, labels, edges, and
 * ports, as well as a custom arrow.
 */
public class SimpleCustomStyleDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;

  private final Random random = new Random();

  /**
   * Creates a sample graph, specifies the default styles and sets the default input mode.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    // initialize the input mode
    graphControl.setInputMode(createEditorMode());

    // initialize the default styles for newly created graph items
    initializeDefaultStyles();

    // create some graph elements with the above defined styles
    createSampleGraph();
  }

  /**
   * Called when the stage is shown and the {@link GraphControl} is already resized to its preferred size.
   * The graph is moved to the center of the <code>GraphControl</code>.
   */
  public void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Creates the default input mode for the GraphControl, a {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    // enable label editing
    mode.setEditLabelAllowed(true);
    return mode;
  }

  /**
   * Initializes the default styles that are used as templates for newly created graph items.
   */
  private void initializeDefaultStyles() {
    IGraph graph = graphControl.getGraph();

    // create a new style and use it as default node style
    graph.getNodeDefaults().setStyle(new MySimpleNodeStyle());

    // create a new style and use it as default edge style
    graph.getEdgeDefaults().setStyle(new MySimpleEdgeStyle());
    graph.getEdgeDefaults().getPortDefaults().setStyle(new MySimplePortStyle());

    // create a new style and use it as default label style
    graph.getNodeDefaults().getLabelDefaults().setStyle(new MyDefaultLabelStyle());
    ExteriorLabelModel labelModel = new ExteriorLabelModel();
    labelModel.setInsets(new InsetsD(15));
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(labelModel.createParameter(ExteriorLabelModel.Position.NORTH));
    graph.getEdgeDefaults().getLabelDefaults().setStyle(new MyDefaultLabelStyle());

    // create a new style and use it as default port style
    graph.getNodeDefaults().getPortDefaults().setStyle(new MySimplePortStyle());
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Creates the initial sample graph.
   */
  private void createSampleGraph() {
    IGraph graph = graphControl.getGraph();

    INode n0 = graph.createNode(new PointD(291, 433));
    INode n1 = graph.createNode(new PointD(396, 398));
    INode n2 = graph.createNode(new PointD(462, 308));
    INode n3 = graph.createNode(new PointD(462, 197));
    INode n4 = graph.createNode(new PointD(396, 107));
    INode n5 = graph.createNode(new PointD(291, 73));
    INode n6 = graph.createNode(new PointD(185, 107));
    INode n7 = graph.createNode(new PointD(119, 197));
    INode n8 = graph.createNode(new PointD(119, 308));
    INode n9 = graph.createNode(new PointD(185, 398));

    n0.setTag(Color.rgb(108, 0, 255, 1));
    n1.setTag(Color.rgb(210, 255, 0, 1));
    n2.setTag(Color.rgb(0, 72, 255, 1));
    n3.setTag(Color.rgb(255, 0, 84, 1));
    n4.setTag(Color.rgb(255, 30, 0, 1));
    n5.setTag(Color.rgb(0, 42, 255, 1));
    n6.setTag(Color.rgb(114, 255, 0, 1));
    n7.setTag(Color.rgb(216, 0, 255, 1));
    n8.setTag(Color.rgb(36, 255, 0, 1));
    n9.setTag(Color.rgb(216, 0, 255, 1));

    ExteriorLabelModel labelModel = new ExteriorLabelModel();
    labelModel.setInsets(new InsetsD(15));

    graph.addLabel(n0, "Node 0", labelModel.createParameter(ExteriorLabelModel.Position.SOUTH));
    graph.addLabel(n1, "Node 1", labelModel.createParameter(ExteriorLabelModel.Position.SOUTH_EAST));
    graph.addLabel(n2, "Node 2", labelModel.createParameter(ExteriorLabelModel.Position.EAST));
    graph.addLabel(n3, "Node 3", labelModel.createParameter(ExteriorLabelModel.Position.EAST));
    graph.addLabel(n4, "Node 4", labelModel.createParameter(ExteriorLabelModel.Position.NORTH_EAST));
    graph.addLabel(n5, "Node 5", labelModel.createParameter(ExteriorLabelModel.Position.NORTH));
    graph.addLabel(n6, "Node 6", labelModel.createParameter(ExteriorLabelModel.Position.NORTH_WEST));
    graph.addLabel(n7, "Node 7", labelModel.createParameter(ExteriorLabelModel.Position.WEST));
    graph.addLabel(n8, "Node 8", labelModel.createParameter(ExteriorLabelModel.Position.WEST));
    graph.addLabel(n9, "Node 9", labelModel.createParameter(ExteriorLabelModel.Position.SOUTH_WEST));

    graph.createEdge(n0, n4);
    graph.createEdge(n6, n0);
    graph.createEdge(n6, n5);
    graph.createEdge(n5, n2);
    graph.createEdge(n3, n7);
    graph.createEdge(n9, n4);
  }

  /**
   * Modifies the sharedNodeStyle instance. This will immediately affect the rendering of all selected nodes.
   */
  public void handleModifySharedStyleButton() {
    // modify the tag
    graphControl.getSelection().getSelectedNodes().forEach(
        node -> node.setTag(Color.hsb(random.nextInt(360), 1, 1, 1)));

    // and invalidate the view as the graph cannot know that we changed the styles
    graphControl.invalidate();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
