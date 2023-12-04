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
package input.lensinputmode;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Projections;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.WebViewUtils;

/**
 * Shows how to use a special {@link LensInputMode} to magnify the currently hovered-over part of the graph.
 */
public class LensInputModeDemo extends DemoApplication {
  @FXML private GraphControl graphControl;
  @FXML private WebView help;

  /**
   * The {@link LensInputMode} displaying the "magnifying glass".
   */
  @FXML private LensInputMode lensInputMode;

  /**
   * Initializes this demo by configuring the graph defaults, adding the {@link LensInputMode} and
   * populating the graph.
   */
  public void initialize() {
    // set up the help text on the right side
    WebViewUtils.initHelp(help, this);

    GraphEditorInputMode graphEditorInputMode = new GraphEditorInputMode();
    graphEditorInputMode.add(lensInputMode);
    graphControl.setInputMode(graphEditorInputMode);

    IGraph graph = graphControl.getGraph();

    // Set nicer styles ...
    DemoStyles.initDemoStyles(graph);

    // ... and create the sample graph
    initializeGraph(graph);

    // Finally, enable undo and redo
    graph.setUndoEngineEnabled(true);
  }

  /**
   * Centers the graph in the graph control.
   * Called right after stage is loaded.
   * In JavaFX, nodes do not have a width or height until the stage is displayed and the scene graph is calculated.
   * As {@link #initialize()} is called right after a node is created, but before displayed, we have to update
   * the view port later.
   */
  public void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Creates the demo's sample graph.
   */
  private void initializeGraph(IGraph graph) {
    INode[] nodes = new INode[16];
    int count = 0;
    for (int i = 1; i < 5; i++) {
      nodes[count++] = graph.createNode(new PointD(50 + 40*i, 260));
      nodes[count++] = graph.createNode(new PointD(50 + 40*i, 40));
      nodes[count++] = graph.createNode(new PointD(40, 50 + 40*i));
      nodes[count++] = graph.createNode(new PointD(260, 50 + 40*i));
    }

    for (int i = 0; i < nodes.length; i++) {
      graph.addLabel(nodes[i], "" + i);
    }

    graph.createEdge(nodes[0], nodes[1]);
    graph.createEdge(nodes[5], nodes[4]);
    graph.createEdge(nodes[2], nodes[3]);
    graph.createEdge(nodes[7], nodes[6]);
    graph.createEdge(nodes[2 + 8], nodes[3 + 8]);
    graph.createEdge(nodes[7 + 8], nodes[6 + 8]);
    graph.createEdge(nodes[0 + 8], nodes[1 + 8]);
    graph.createEdge(nodes[5 + 8], nodes[4 + 8]);
  }

  /**
   * Toggles the projection of the demo's graph view in response to
   * corresponding events from the demo's UI.
   */
  @FXML
  private void toggleProjection(ActionEvent event) {
    boolean useProjection = ((ToggleButton) event.getSource()).isSelected();
    graphControl.setProjection(useProjection ? Projections.getIsometric() : Projections.getIdentity());
  }

  public static void main(String[] args) {
    launch(args);
  }
}
