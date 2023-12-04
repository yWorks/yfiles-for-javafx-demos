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
package gradledemo;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A simple demo using yFiles for JavaFX built with Gradle.
 */
public class GradleDemo extends Application {

  @Override
  public void start(Stage stage) {
    stage.setTitle("yFiles JavaFX Gradle Demo");

    // Instantiate a GraphControl, set the InputMode and add some graph elements.
    GraphControl graphControl = new GraphControl();
    graphControl.setInputMode(new GraphViewerInputMode());
    createGraphElements(graphControl.getGraph());

    // Add the GraphControl to the Scene and show it.
    Scene scene = new Scene(graphControl, 800, 600);
    stage.setScene(scene);
    stage.show();

    // Fit the graph contents _after_ showing the Scene.
    graphControl.fitGraphBounds();
  }

  /**
   * Add some elements to the graph
   */
  public void createGraphElements(IGraph graph) {
    INode n1 = graph.createNode(new PointD(100, 100));
    INode n2 = graph.createNode(new PointD(200, 100));
    INode n3 = graph.createNode(new PointD(150, 200));

    graph.createEdge(n1, n2);
    graph.createEdge(n2, n3);
    graph.createEdge(n3, n1);
  }

  public static void main(String[] args) {
    launch();
  }
}
