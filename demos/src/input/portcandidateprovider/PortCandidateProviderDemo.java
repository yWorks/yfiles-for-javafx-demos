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
package input.portcandidateprovider;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.ITagOwner;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortCandidateValidity;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

/**
 * Demo code that shows how to customize the port relocation feature
 * by implementing a custom {@link IPortCandidateProvider}.
 */
public class PortCandidateProviderDemo extends DemoApplication {

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

    // Initialize the input mode
    initializeInputMode();

    // Disable automatic cleanup of unconnected ports since some nodes have a predefined set of ports
    IGraph graph = graphControl.getGraph();
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);
    // set a port style that makes the pre-defined ports visible
    ShapeNodeStyle roundNodeStyle = new ShapeNodeStyle();
    roundNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    roundNodeStyle.setPaint(Color.BLACK);
    roundNodeStyle.setPen(null);
    NodeStylePortStyleAdapter simplePortStyle = new NodeStylePortStyleAdapter(roundNodeStyle);
    simplePortStyle.setRenderSize(new SizeD(3, 3));
    graph.getNodeDefaults().getPortDefaults().setStyle(simplePortStyle);
    // enable the undo feature
    graph.setUndoEngineEnabled(true);

    // register custom provider implementations
    registerPortCandidateProvider();

    // initialize the graph
    createSampleGraph();
  }

  private void initializeInputMode() {
    // set up our InputMode for this demo.
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // disable the interactive creation of nodes to focus on the sample graph
    inputMode.setCreateNodeAllowed(false);
    // also restrict the interaction to edges and bends
    GraphItemTypes edgesOrBends = GraphItemTypes.EDGE.or(GraphItemTypes.BEND);
    inputMode.setClickSelectableItems(edgesOrBends);
    inputMode.setSelectableItems(edgesOrBends);
    inputMode.setDeletableItems(edgesOrBends);
    // disable focusing of items
    inputMode.setFocusableItems(GraphItemTypes.NONE);
    // disable label editing
    inputMode.setEditLabelAllowed(false);
    // Finally, set the input mode to the graph component.
    graphControl.setInputMode(inputMode);
  }

  /**
   * Constructs a sample graph that contains nodes that demonstrates each of the custom {@link
   * IPortCandidateProvider} above.
   */
  private void createSampleGraph() {
    IGraph graph = graphControl.getGraph();

    createNode(graph, new RectD(100, 100, 80, 30), Color.FIREBRICK, "No Edge");
    createNode(graph, new RectD(350, 100, 80, 30), Color.FORESTGREEN, "Green Only");
    createNode(graph, new RectD(100, 200, 80, 30), Color.FORESTGREEN, "Green Only");
    createNode(graph, new RectD(350, 200, 80, 30), Color.FIREBRICK, "No Edge");

    // The blue nodes have predefined ports
    IPortStyle portStyle = new ColorPortStyle();

    INode blue1 = createNode(graph, new RectD(100, 300, 80, 30), Color.ROYALBLUE, "One   Port");
    graph.addPort(blue1, blue1.getLayout().getCenter(), portStyle).setTag(Color.BLACK);

    INode blue2 = createNode(graph, new RectD(350, 300, 100, 100), Color.ROYALBLUE, "Many Ports");
    // pre-define a bunch of ports at the outer border of one of the blue nodes
    AbstractPortCandidateProvider portCandidateProvider = IPortCandidateProvider.fromShapeGeometry(blue2, 0, 0.25, 0.5, 0.75);
    portCandidateProvider.setStyle(portStyle);
    portCandidateProvider.setTag(Color.BLACK);
    Iterable<IPortCandidate> candidates = portCandidateProvider.getSourcePortCandidates(
        graphControl.getInputModeContext());
    candidates.forEach(portCandidate -> {
      if (portCandidate.getValidity() != PortCandidateValidity.DYNAMIC) {
        portCandidate.createPort(graphControl.getInputModeContext());
      }
    });

    // The orange node
    createNode(graph, new RectD(100, 400, 100, 100), Color.DARKORANGE, "Dynamic Ports");

    INode n = createNode(graph, new RectD(100, 540, 100, 100), Color.PURPLE, "Individual\nPort\nConstraints");
    addIndividualPorts(graph, n);

    INode n2 = createNode(graph, new RectD(350, 540, 100, 100), Color.PURPLE, "Individual\nPort\nConstraints");
    addIndividualPorts(graph, n2);
  }

  /**
   * Convenience method to create a node with a label.
   */
  private INode createNode(IGraph graph, RectD bounds, Color color, String labelText) {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(color);
    INode node = graph.createNode(bounds, nodeStyle, color);

    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(Font.font("System", FontWeight.BOLD, 12));
    labelStyle.setTextPaint(Color.WHITE);
    graph.addLabel(node, labelText, InteriorLabelModel.CENTER, labelStyle);
    return node;

  }

  /**
   * Adds ports with different colors to the node.
   */
  private void addIndividualPorts(IGraph graph, INode node) {
    IPortStyle portStyle = new ColorPortStyle();
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.25, 0), PointD.ORIGIN), portStyle, Color.FIREBRICK);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.75, 0), PointD.ORIGIN), portStyle, Color.FORESTGREEN);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0, 0.25), PointD.ORIGIN), portStyle, Color.BLACK);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0, 0.75), PointD.ORIGIN), portStyle, Color.BLACK);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(1, 0.25), PointD.ORIGIN), portStyle, Color.ROYALBLUE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(1, 0.75), PointD.ORIGIN), portStyle, Color.DARKORANGE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.25, 1), PointD.ORIGIN), portStyle, Color.PURPLE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.75, 1), PointD.ORIGIN), portStyle, Color.PURPLE);
  }

  /**
   * Registers a callback function as decorator that provides a custom
   * {@link IPortCandidateProvider} for each node.
   * This callback function is called whenever a node in the graph is queried
   * for its <code>IPortCandidateProvider</code>. In this case, the 'node'
   * parameter will be assigned that node.
   */
  private void registerPortCandidateProvider() {
    graphControl.getGraph().getDecorator().getNodeDecorator().getPortCandidateProviderDecorator().setFactory(this::createPortCandidateProvider);
  }

  /**
   * A factory method that can be used for the lookup chain for INodes to provide custom
   * IPortCandidateProvider implementations depending on the color stored in their
   * {@link ITagOwner#getTag()}.
   * @param node the node to create an IPortCandidateProvider for
   */
  private IPortCandidateProvider createPortCandidateProvider(INode node) {
    // obtain the tag from the node
    Object nodeTag = node.getTag();

    // see if it is a known tag
    if (nodeTag instanceof Color) {
      // and decide what implementation to provide
      if (Color.FIREBRICK.equals(nodeTag)) {
        return new RedPortCandidateProvider(node);
      } else if (Color.ROYALBLUE.equals(nodeTag)) {
        return new BluePortCandidateProvider(node);
      } else if (Color.FORESTGREEN.equals(nodeTag)) {
        return new GreenPortCandidateProvider(node);
      } else if (Color.DARKORANGE.equals(nodeTag)) {
        return new OrangePortCandidateProvider(node);
      } else if (Color.PURPLE.equals(nodeTag)) {
        return new PurplePortCandidateProvider(node);
      }
    }
    // otherwise revert to default behavior
    return null;
  }

  public static void main(String[] args) {
    launch(args);
  }
}