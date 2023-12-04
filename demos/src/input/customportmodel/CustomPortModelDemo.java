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
package input.customportmodel;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortsHandleProvider;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.Themes;
import toolkit.WebViewUtils;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

/**
 * Demo code that shows how to implement custom {@link com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel}.
 */
public class CustomPortModelDemo extends DemoApplication {

  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);
    // initialize the graph
    initializeGraph(graphControl.getGraph());
    initializeStructure(graphControl.getGraph());

    // configure XML namespaces for I/O
    graphControl.setGraphMLIOHandler(createGraphMLIOHandler());

    // initialize the input mode
    initializeInputModes();
  }

  /**
   * Calls {@link #createEditorMode()} and registers the result as the {@link CanvasControl#getInputMode()}.
   */
  private void initializeInputModes() {
    graphControl.setInputMode(createEditorMode());
  }

  /**
   * Creates the default input mode for the GraphControl, a {@link GraphEditorInputMode}.
   */
  private IInputMode createEditorMode() {
    return new GraphEditorInputMode();
  }

  /**
   * Callback used by the decorator in {@link #createEditorMode()}.
   */
  private IPortCandidateProvider getPortCandidateProvider(INode forNode) {
    MyNodePortLocationModel model = new MyNodePortLocationModel(10);
    return IPortCandidateProvider.fromCandidates(
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.CENTER)),
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.NORTH)),
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.EAST)),
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.SOUTH)),
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.WEST)));
  }

  /**
   * Sets a custom node port model parameter instance for newly created node ports in the graph, creates example nodes
   * with ports using our model and an edge to connect the ports.
   */
  private void initializeGraph(IGraph graph) {
    DemoStyles.initDemoStyles(graph);

    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.getPortDefaults().setLocationParameter(new MyNodePortLocationModel().createParameter(PortLocation.CENTER));
    ShapeNodeStyle innerPortNodeStyle = DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.ELLIPSE, Themes.PALETTE_LIGHTBLUE);
    nodeDefaults.getPortDefaults().setStyle(new NodeStylePortStyleAdapter(innerPortNodeStyle));
    nodeDefaults.setSize(new SizeD(50, 50));

    NodeDecorator nodeDecorator = graph.getDecorator().getNodeDecorator();
    // for selected nodes show the handles
    nodeDecorator.getHandleProviderDecorator().setFactory(PortsHandleProvider::new);
    // for nodes add a custom port candidate provider implementation which uses our model
    nodeDecorator.getPortCandidateProviderDecorator().setFactory(this::getPortCandidateProvider);
  }

  /**
   * Creates the structure of the demo's sample graph.
   */
  private void initializeStructure(IGraph graph) {
    INode source = graph.createNode(new RectD(90, 90, 100, 100));
    INode target = graph.createNode(new RectD(250, 90, 100, 100));

    // creates a port using the default declared above
    IPort sourcePort = graph.addPort(source);
    // creates a port using the custom model instance
    IPort targetPort = graph.addPort(target, new MyNodePortLocationModel(10).createParameter(
            PortLocation.NORTH));

    // create an edge
    graph.createEdge(sourcePort, targetPort);
  }

  /**
   * Creates a GraphML reader/writer with a "nice", specific XML namespace
   * registered for types from this demo (or rather from
   * {@link MyNodePortLocationModel}'s package).
   */
  private GraphMLIOHandler createGraphMLIOHandler() {
    String namespace = "http://www.yworks.com/yfiles-for-javafx/CustomPortModel/1.0";
    GraphMLIOHandler handler = new GraphMLIOHandler();
    handler.addXamlNamespaceMapping(namespace, MyNodePortLocationModel.class);
    handler.addNamespace(namespace, "demo");
    return handler;
  }

  public void exit() {
    Platform.exit();
  }

  public static void main(String[] args) {
    launch(args);
  }
}