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
package viewer.levelofdetail;

import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;
import javafx.scene.web.WebView;

/**
 * Demonstrates how to change the level of detail for node visualizations when
 * zooming in and out.
 */
public class LevelOfDetailDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the demo application.
   */
  public void initialize() {
    //includes the "resources/help.html"
    WebViewUtils.initHelp(helpView, this);

    // initialize the input mode
    createViewerInputMode();

    // loads an initial sample graph
    loadGraph();
  }

  /**
   * Loads an initial sample graph.
   */
  private void loadGraph() {
    try {
      GraphMLIOHandler reader = graphControl.getGraphMLIOHandler();
      reader.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/LevelOfDetail/1.0", Employee.class);
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Creates an input mode that prevents interactive modifications of the demo's
   * sample graph but allows panning, zooming, and scrolling.
   */
  private void createViewerInputMode() {
    graphControl.setInputMode(new GraphViewerInputMode());
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
