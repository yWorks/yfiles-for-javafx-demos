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
package input.customlabelmodel;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

/**
 * This demo shows how to create and use a custom {@link ILabelModel} that provides either continuous or discrete label
 * positions directly outside the node border.
 */
public class CustomLabelModelDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // includes the help
    WebViewUtils.initHelp(helpView, this);

    // initializes the input mode
    createInputMode();

    // configure XML namespaces for I/O
    initializeGraphMLIOHandler();

    // initializes a sample graph
    initializeGraph();
  }

  /**
   * Initializes the GraphML reader/writer with a "nice", specific XML namespace
   * registered for types from this demo
   */
  private void initializeGraphMLIOHandler() {
    String namespace = "http://www.yworks.com/yfiles-for-javafx/CustomPortModel/1.0";
    GraphMLIOHandler handler = graphControl.getGraphMLIOHandler();
    handler.addXamlNamespaceMapping(namespace, MyNodeLabelLocationModel.class);
    handler.addNamespace(namespace, "demo");
  }

  /**
   * Sets a custom node label model parameter instance for newly created
   * node labels in the graph, creates an example node with a label using
   * the default parameter and another node with a label without restrictions
   * on the number of possible placements.
   */
  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();

    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
    INode node1 = graph.createNode(new RectD(250, 90, 100, 100));
    INode node2 = graph.createNode(new RectD(90, 90, 100, 100));

    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(new MyNodeLabelLocationModel().createDefaultParameter());
    graph.addLabel(node1, "Click and Drag To Snap");

    MyNodeLabelLocationModel myNodeLabelLocationModel = new MyNodeLabelLocationModel();
    myNodeLabelLocationModel.setCandidateCount(0);
    myNodeLabelLocationModel.setOffset(20);
    graph.addLabel(node2, "Click and Drag", myNodeLabelLocationModel.createDefaultParameter());
  }

  /**
   * Creates the input mode
   */
  private void createInputMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    graphControl.setInputMode(mode);
  }

  public void exit() {
    Platform.exit();
  }

  /**
   * Adjusts the view by the first start of the demo
   */
  @Override
  protected void onLoaded() {
    graphControl.fitGraphBounds();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
