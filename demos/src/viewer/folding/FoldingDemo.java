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
package viewer.folding;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.WebViewUtils;

/**
 * This demo shows how to enable collapsing and expanding of group nodes.
 */
public class FoldingDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Enables folding and creates a folding view.
   * @return The folding view graph.
   * @param masterGraph The master graph to create a folding view graph for.
   */
  private IGraph enableFolding(IGraph masterGraph) {
    // creates the folding manager
    FoldingManager manager = new FoldingManager(masterGraph);

    // creates a folding view that manages the folded graph
    IFoldingView foldingView = manager.createFoldingView();

    // create undo units for expand/collapse and enter/exit, too
    foldingView.setNavigationalUndoUnitsEnqueuingEnabled(true);

    // return the view graph
    return foldingView.getGraph();
  }

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // create and initialize the master graph
    IGraph masterGraph = initializeMasterGraph();

    // enable abilities for folding
    IGraph viewGraph = enableFolding(masterGraph);

    // assign the folding view graph to the graph control
    graphControl.setGraph(viewGraph);

    // initializes the input modes
    initializeInputMode();

    // show the help text
    WebViewUtils.initHelp(helpView, this);
  }

  private IGraph initializeMasterGraph() {
    // create the unfolded master graph
    DefaultGraph masterGraph = new DefaultGraph();

    // set default styles for newly created graph elements
    DemoStyles.initDemoStyles(masterGraph, true);

    // create an initial sample graph
    createInitialGraph(masterGraph);

    // enable undo after the initial graph was populated since we don't want to allow undoing that
    masterGraph.setUndoEngineEnabled(true);

    return masterGraph;
  }

  /**
   * Creates an initial sample graph.
   */
  private void createInitialGraph(IGraph graph) {
    INode node1 = graph.createNode(new PointD(110, 20));
    INode node2 = graph.createNode(new PointD(145, 95));
    INode node3 = graph.createNode(new PointD(75, 95));
    INode node4 = graph.createNode(new PointD(30, 175));
    INode node5 = graph.createNode(new PointD(100, 175));

    INode groupNode = graph.groupNodes(node1, node2, node3);
    graph.addLabel(groupNode, "Group 1");

    graph.createEdge(node1, node2);
    graph.createEdge(node1, node3);
    graph.createEdge(node3, node4);
    graph.createEdge(node3, node5);
    graph.createEdge(node1, node5);
  }

  /**
   * Creates and initializes the input mode.
   */
  private void initializeInputMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    // enables grouping operations such as grouping selected nodes moving nodes into group nodes
    mode.setGroupingOperationsAllowed(true);

    // clear graph when executing NEW command
    mode.getKeyboardInputMode().addCommandBinding(ICommand.NEW,
        (command, parameter, source) -> {
          graphControl.getGraph().clear();
          graphControl.fitGraphBounds();
          return true;
        },
        (command, parameter, source) ->
            graphControl.getGraph().getNodes().size() != 0);

    graphControl.setInputMode(mode);
  }

  /**
   * Adjusts the view by the first start of the demo.
   */
  @Override
  protected void onLoaded() {
    super.onLoaded();
    graphControl.fitGraphBounds();
  }

  public static void main( String[] args ) {
    launch(args);
  }
}
