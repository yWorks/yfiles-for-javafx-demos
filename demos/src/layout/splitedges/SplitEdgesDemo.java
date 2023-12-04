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
package layout.splitedges;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.CompositeLayoutData;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.RecursiveGroupLayout;
import com.yworks.yfiles.layout.RecursiveGroupLayoutData;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.SimplexNodePlacer;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import javafx.scene.web.WebView;

/**
 * Shows how to align edges at group nodes using {@link RecursiveGroupLayout}
 * together with {@link HierarchicLayout}.
 */
public class SplitEdgesDemo extends DemoApplication {
  /** Holds split IDs for the edges' source end. */
  public WebView helpView;
  /** Holds split IDs for the edges' source end. */
  public GraphControl graphControl;

  /** Holds split IDs for the edges' source end. */
  private IMapper<IEdge, Object> sourceSplitIds;
  /** Holds split IDs for the edges' target end. */
  private IMapper<IEdge, Object> targetSplitIds;

  /**
   * Initializes the demo.
   */
  public void initialize() {
    // create mappers for storing split IDs for edges
    createMappers();

    // configure user interaction
    initializeInputMode();

    // load a sample GraphML file with suitable split IDs
    loadGraph();

    // calculate an initial arrangement for the sample graph
    runLayout();

    WebViewUtils.initHelp(helpView, this);
  }

  /**
   * Configures user interaction.
   * Enables grouping actions and restricts selection to nodes and edges.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setSelectableItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    geim.setGroupingOperationsAllowed(true);
    graphControl.setInputMode(geim);
  }

  /**
   * Arranges the displayed graph in a recursive fashion.
   * I.e. {@link RecursiveGroupLayout} is used to arrange each group separately
   * with {@link HierarchicLayout}.
   */
  public void runLayout() {
    HierarchicLayout hierarchicLayout = new HierarchicLayout();
    hierarchicLayout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    // step 1: enable  DirectGroupContentEdgeRouting
    // this is necessary to ensure suitable routes for those split edges inside
    // the corresponding group node
    hierarchicLayout.getEdgeLayoutDescriptor().setDirectGroupContentEdgeRoutingEnabled(true);
    ((SimplexNodePlacer) hierarchicLayout.getNodePlacer()).setBarycenterModeEnabled(true);

    EdgeRouter router = new EdgeRouter();
    router.setScope(Scope.ROUTE_AFFECTED_EDGES);
    router.getDefaultEdgeLayoutDescriptor().setDirectGroupContentEdgeRoutingEnabled(true);

    RecursiveGroupLayout recursiveGroupLayout = new RecursiveGroupLayout();
    recursiveGroupLayout.setCoreLayout(hierarchicLayout);
    recursiveGroupLayout.setInterEdgeRouter(router);
    recursiveGroupLayout.setInterEdgesDpKey(router.getAffectedEdgesDpKey());

    RecursiveGroupLayoutData recursiveGroupLayoutData = new RecursiveGroupLayoutData();
    // step 2: register split IDs for edges
    // this is necessary to ensure that the two edges that make up a
    // "split inter-edge" share the same port location on the corresponding
    // group's border
    recursiveGroupLayoutData.setSourceSplitIds(sourceSplitIds);
    recursiveGroupLayoutData.setTargetSplitIds(targetSplitIds);
    HierarchicLayoutData hierarchicLayoutData = new HierarchicLayoutData();
    hierarchicLayoutData.setEdgeThickness(3);

    CompositeLayoutData compositeLayoutData = new CompositeLayoutData();
    compositeLayoutData.getItems().add(recursiveGroupLayoutData);
    compositeLayoutData.getItems().add(hierarchicLayoutData);

    graphControl.morphLayout(recursiveGroupLayout, Duration.ofMillis(700), compositeLayoutData);
  }

  /**
   * Creates the mappers used for storing split IDs for edges.
   * @see RecursiveGroupLayoutData#getSourceSplitIds()
   * @see RecursiveGroupLayoutData#getTargetSplitIds()
   */
  private void createMappers() {
    targetSplitIds = new Mapper<>();
    sourceSplitIds = new Mapper<>();
  }

  /**
   * Loads a sample graph. Populates {@link #sourceSplitIds source} and
   * {@link #targetSplitIds target} split IDs from data in the GraphML file.
   */
  private void loadGraph() {
    GraphMLIOHandler graphMLIOHandler = graphControl.getGraphMLIOHandler();
    graphMLIOHandler.addInputMapper(IEdge.class, Object.class, "sourceSplitIds" , sourceSplitIds);
    graphMLIOHandler.addInputMapper(IEdge.class, Object.class, "targetSplitIds" , targetSplitIds);

    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
