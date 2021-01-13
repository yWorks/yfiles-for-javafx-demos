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
package tutorial01_GettingStarted.step02_CreatingGraphElements;


import com.yworks.yfiles.graph.labelmodels.InsideOutsidePortLabelModel;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.WebViewUtils;

/**
 * <h1>Step 2: Creating Graph Elements.</h1>
 * <p>
 * Create the basic graph elements in yFiles: nodes, edges, bends, ports, and labels.
 * </p>
 * <p>
 * Please see the file help.html for more details.
 * </p>
 */
public class SampleApplication extends Application {

  public GraphControl graphControl;
  public WebView help;

  /**
   * Called to initialize a controller after its root element has been completely processed.
   */
  public void initialize() {
    //////////// New in this Sample ///////////////////////
    // Creates a sample graph and introduces all important graph elements present in yFiles: nodes, edges, bends, ports
    // and labels.
    populateGraph();
    ///////////////////////////////////////////////////////
  }

  //////////// New in this Sample ///////////////////////
  /**
   * Creates a sample graph and introduces all important graph elements present in yFiles. Additionally, this method
   * sets the label placement for some specific labels.
   */
  private void populateGraph() {
    IGraph graph = getGraph();

    //////////// Sample node creation ///////////////////
    // Creates two nodes with the default node size
    // The location is specified for the center
    INode node1 = graph.createNode(new PointD(50, 50));
    INode node2 = graph.createNode(new PointD(150, 50));
    // Creates a third node with a different size of 80x40
    // In this case, the location of (360,380) describes the upper left
    // corner of the node bounds
    INode node3 = graph.createNode(new RectD(360, 380, 80, 40));
    /////////////////////////////////////////////////////

    //////////// Sample edge creation ///////////////////
    // Creates some edges between the nodes
    IEdge edge1 = graph.createEdge(node1, node2);
    IEdge edge2 = graph.createEdge(node2, node3);
    /////////////////////////////////////////////////////

    //////////// Using Bends ////////////////////////////
    // Creates the first bend for edge2 at (400, 50)
    IBend bend1 = graph.addBend(edge2, new PointD(400, 50));
    /////////////////////////////////////////////////////

    //////////// Using Ports ////////////////////////////
    // Actually, edges connect "ports", not nodes directly.
    // If necessary, you can manually create ports at nodes
    // and let the edges connect to these.
    // Creates a port in the center of the node layout
    IPort port1AtNode1 = graph.addPort(node1, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);

    // Creates a port at the middle of the left border
    // Note to use absolute locations in world coordinates when placing ports using PointD.
    // The method obtains a model parameter that best matches the given port location.
    IPort port1AtNode3 = graph.addPort(node3, new PointD(node3.getLayout().getX(), node3.getLayout().getCenter().getY()));

    // Creates an edge that connects these specific ports
    IEdge edgeAtPorts = graph.createEdge(port1AtNode1, port1AtNode3);
    /////////////////////////////////////////////////////

    //////////// Sample label creation ///////////////////
    // Adds labels to several graph elements
    graph.addLabel(node1, "N 1");
    graph.addLabel(node2, "N 2");
    graph.addLabel(node3, "N 3");
    graph.addLabel(edgeAtPorts, "Edge at Ports");
    graph.addLabel(port1AtNode3, "Port at Node", new InsideOutsidePortLabelModel().createOutsideParameter());
    /////////////////////////////////////////////////////
  }
  ///////////////////////////////////////////////////////

  /**
   * Convenience method to retrieve the graph.
   */
  public IGraph getGraph() {
    return graphControl.getGraph();
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SampleApplication.fxml"));
    fxmlLoader.setController(this);
    Parent root = fxmlLoader.load();
    WebViewUtils.initHelp(help, this);

    Scene scene = new Scene(root, 1365, 768);

    stage.setTitle("Step 2 - Creating Graph Elements");
    stage.setScene(scene);
    stage.show();
  }

}
