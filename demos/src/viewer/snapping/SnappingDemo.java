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
package viewer.snapping;

import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.LabelSnapContext;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

/**
 * Demonstrates how to enable snapping functionality for graph elements.
 */
public class SnappingDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // includes the help
    WebViewUtils.initHelp(helpView, this);

    // initializes the input modes and
    // enable snapping for labels and other graph items
    initializeSnapping();

    // loads a sample graph
    loadGraph();

    // enable undoability
    enableUndo();
  }

  /**
   * Initializes snapping for labels and other graph items. The default snapping behavior can easily be enabled by
   * setting a properly configured snap context. A snap context provides many options to fine tune its behavior.
   * Please see the documentation of {@link GraphSnapContext} and {@link LabelSnapContext} for more information.
   */
  private void initializeSnapping() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    graphControl.setInputMode(geim);

    // enables snapping all items except labels
    geim.setSnapContext(new GraphSnapContext());
    // enables snapping for labels
    geim.setLabelSnapContext(new LabelSnapContext());

    // enables grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    geim.setGroupingOperationsAllowed(true);
  }

  /**
   * Loads an initial sample graph.
   */
  private void loadGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Enables undo and redo functionality.
   */
  private void enableUndo() {
    graphControl.getGraph().setUndoEngineEnabled(true);
  }

  /**
   * Centers the demo's sample graph inside the visible area.
   */
  @Override
  protected void onLoaded() {
    graphControl.fitGraphBounds();
  }

  public static void main( String[] args ) {
    launch(args);
  }
}
