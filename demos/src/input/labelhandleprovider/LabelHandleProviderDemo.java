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
package input.labelhandleprovider;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.LabelStyleDecorationInstaller;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.Visualization;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.WebViewUtils;

import javafx.scene.web.WebView;
import java.io.IOException;

/**
 * Shows how to implement custom
 * {@link com.yworks.yfiles.view.input.IHandle handles}
 * for interactive rotation and resizing of labels.
 */
public class LabelHandleProviderDemo extends DemoApplication {
  public WebView helpView;
  public GraphControl graphControl;

  /**
   * Initializes the demo.
   */
  public void initialize() {
    // configure user interaction
    initializeInputMode();

    // configure custom label handles
    initializeHandles();

    // load a sample GraphML file
    loadGraph();

    WebViewUtils.initHelp(helpView, this);
  }


  /**
   * Configures user interaction.
   * Adjusts element hit test order for easier label selection.
   * Registers a listener that adds a label to each newly created node.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();

    // handles should be moved together with the ghost visualization of the label
    geim.getMoveLabelInputMode().setVisualization(Visualization.LIVE);

    // customize hit test order to simplify click selecting labels
    geim.setClickHitTestOrder(
            GraphItemTypes.EDGE_LABEL,
            GraphItemTypes.NODE_LABEL,
            GraphItemTypes.BEND,
            GraphItemTypes.EDGE,
            GraphItemTypes.NODE,
            GraphItemTypes.PORT,
            GraphItemTypes.ALL);

    // add a label to each created node
    geim.addNodeCreatedListener(( sender, args ) -> {
      IGraph graph = graphControl.getGraph();
      graph.addLabel(args.getItem(), "Node " + graph.getNodes().size());
    });

    graphControl.setInputMode(geim);
  }

  /**
   * Initializes the custom handles for rotating and resizing labels.
   */
  private void initializeHandles() {
    IGraph graph = graphControl.getGraph();

    // add a custom handle provider for labels
    graph.getDecorator().getLabelDecorator().getHandleProviderDecorator().setFactory(LabelHandleProvider::new);

    // for rotatable labels: use the custom selection visualization to indicate that this label can be rotated
    LabelStyleDecorationInstaller labelDecorationInstaller = new LabelStyleDecorationInstaller();
    labelDecorationInstaller.setLabelStyle(new LabelSelectionStyle());
    graph.getDecorator().getLabelDecorator().getSelectionDecorator().setImplementation(
            LabelHandleProviderDemo::isRotatable, labelDecorationInstaller);
  }

  /**
   * Determines if the given label's model supports rotated labels.
   */
  private static boolean isRotatable( ILabel label ) {
    ILabelModel model = label.getLayoutParameter().getModel();
    return model instanceof FreeNodeLabelModel ||
           model instanceof FreeEdgeLabelModel ||
           model instanceof FreeLabelModel;
  }

  /**
   * Loads a sample graph.
   */
  private void loadGraph() {
    try {
      DemoStyles.initDemoStyles(graphControl.getGraph());
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Centers the graph in the visible area.
   */
  @Override
  protected void onLoaded() {
    graphControl.fitGraphBounds();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
