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
package viewer.edgetoedge;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.portlocationmodels.BendAnchoredPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.EdgePathPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.SegmentRatioPortLocationModel;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.WebViewUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This application demonstrates the use of edge-to-edge connections. Edges can be created interactively
 * between nodes, nodes and edges and between two edges. Also, this application enables moving the source or
 * target of an edge to another owner.
 * Connecting the source or target of an edge to itself is prohibited since this is conceptually forbidden.
 * <p>
 * Edge-to-edge connections have to be enabled explicitly using the property
 * {@link com.yworks.yfiles.view.input.CreateEdgeInputMode#setEdgeToEdgeConnectionsAllowed(boolean)}.
 * </p>
 * <p>
 * This demo also includes customized implementations of {@link IPortCandidateProvider},
 * {@link IEdgeReconnectionPortCandidateProvider}, {@link IHitTestable}, {@link IEdgePortHandleProvider} and
 * {@link IPortLocationModel} to enable custom behavior like reconnecting an existing edge to another edge,
 * starting edge creation from an edge etc.
 * </p>
 */
public class EdgeToEdgeDemo extends DemoApplication {
  private static final int GRID_SIZE = 50;
  private static final GridInfo GRID_INFO = new GridInfo(GRID_SIZE);

  private GraphSnapContext snapContext;
  private LabelSnapContext labelSnapContext;

  public GraphControl graphControl;
  public WebView helpView;

  /**
   * Initializes the graph and the input mode.
   */
  public void initialize() {
    // initialize the graph
    initializeGraph();

    // initialize the snapcontext
    initializeSnapContext();

    // initialize the input mode
    initializeInputModes();

    WebViewUtils.initHelp(helpView, this);
  }

  /**
   * Centers the graph in the graph control.
   */
  public void onLoaded() {
    // center the graph
    graphControl.fitGraphBounds();
  }

  private void initializeSnapContext() {
    snapContext = new GraphSnapContext();
    snapContext.setEnabled(false);
    // disable grid snapping
    snapContext.setGridSnapType(GridSnapTypes.NONE);
    // add constraint provider for nodes, bends, and ports
    snapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));
    snapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));
    snapContext.setPortGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));

    // initialize label snapping
    labelSnapContext = new LabelSnapContext();
    labelSnapContext.setEnabled(false);
    // set maximum distance between the current mouse coordinates and the coordinates to which the mouse will snap
    labelSnapContext.setSnapDistance(15);
    // set the amount by which snap lines that are induced by existing edge segments are being extended
    labelSnapContext.setSnapLineExtension(100);
  }

  /**
   * Calls {@link #createEditorMode}, registers the result as the
   * {@link com.yworks.yfiles.view.CanvasControl#setInputMode(IInputMode)}
   * and enables edge-to-edge connections.
   */
  private void initializeInputModes() {
    GraphEditorInputMode inputMode = createEditorMode();
    enableEdgeToEdgeConnections(inputMode);
    graphControl.setInputMode(inputMode);
  }

  /**
   * Creates the default input mode for the GraphControl, a {@link GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance.
   */
  private GraphEditorInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    mode.setGroupingOperationsAllowed(true);
    mode.setSnapContext(snapContext);
    mode.setLabelSnapContext(labelSnapContext);
    OrthogonalEdgeEditingContext editingContext = new OrthogonalEdgeEditingContext();
    editingContext.setEnabled(false);
    mode.setOrthogonalEdgeEditingContext(editingContext);

    // randomize edge color
    mode.getCreateEdgeInputMode().addEdgeCreationStartedListener((source, args) -> setRandomEdgeColor(args.getItem()));
    return mode;
  }

  /**
   * Enables edge-to-edge connections on the input mode.
   */
  private void enableEdgeToEdgeConnections(GraphEditorInputMode mode) {
    // enable edge-to-edge
    mode.getCreateEdgeInputMode().setEdgeToEdgeConnectionsAllowed(true);

    // create bends only when shift is pressed
    mode.getCreateBendInputMode().setPressedRecognizer(IEventRecognizer.MOUSE_LEFT_PRESSED.and(IEventRecognizer.SHIFT_PRESSED));
  }

  /**
   * Initializes the graph instance setting default styles and customizing behavior.
   */
  private void initializeGraph() {
    IGraph graph = graphControl.getGraph();

    graphControl.setFileIOEnabled(true);

    // Get the default graph instance and enable undo/redo support.
    graph.setUndoEngineEnabled(true);

    // set the default node style
    graph.getNodeDefaults().setStyle(DemoStyles.createDemoNodeStyle());

    // assign default edge style
    graph.getEdgeDefaults().setStyle(new PolylineEdgeStyle());
    graph.getEdgeDefaults().setStyleInstanceSharingEnabled(false);

    // assign a port style for the ports at the edges
    ShapeNodeStyle portNodeStyle = new ShapeNodeStyle();
    portNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    portNodeStyle.setPaint(Color.BLACK);
    portNodeStyle.setPen(null);
    NodeStylePortStyleAdapter portStyle = new NodeStylePortStyleAdapter();
    portStyle.setRenderSize(new SizeD(3,3));
    graph.getEdgeDefaults().getPortDefaults().setStyle(portStyle);

    // enable edge port candidates
    graph.getDecorator().getEdgeDecorator().getPortCandidateProviderDecorator().setFactory(
        EdgeSegmentPortCandidateProvider::new);
    // set IEdgeReconnectionPortCandidateProvider to allow re-connecting edges to other edges
    graph.getDecorator().getEdgeDecorator().getEdgeReconnectionPortCandidateProviderDecorator().setImplementation(
        IEdgeReconnectionPortCandidateProvider.ALL_NODE_AND_EDGE_CANDIDATES);
    graph.getDecorator().getEdgeDecorator().getHandleProviderDecorator().setFactory(edge -> {
      PortRelocationHandleProvider handleProvider = new PortRelocationHandleProvider(null, edge);
      handleProvider.setVisualization(Visualization.LIVE);
      return handleProvider;
    });

    // load a sample graph
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a random colored pen and uses that one for the style.
   */
  private void setRandomEdgeColor(IEdge edge) {
    if (edge.getStyle() instanceof PolylineEdgeStyle) {
      PolylineEdgeStyle style = (PolylineEdgeStyle) edge.getStyle();
      Random random = new Random();
      double r = random.nextDouble();
      double g = random.nextDouble();
      double b = random.nextDouble();
      Pen pen = new Pen(new Color(r, g, b, 1d), 2);
      style.setPen(pen);
    }
  }

  /**
   * A port candidate provider that aggregates different {@link IPortLocationModel} to provide a number of port
   * candidates along each segment of the edge.
   */
  static class EdgeSegmentPortCandidateProvider extends AbstractPortCandidateProvider {
    private final IEdge edge;

    EdgeSegmentPortCandidateProvider(IEdge edge) {
      this.edge = edge;
    }

    protected IEnumerable<IPortCandidate> getPortCandidates(IInputModeContext context) {
      List<IPortCandidate> candidates = new ArrayList<>();
      // add equally distributed port candidates along the edge
      for (int i = 1; i < 10; ++i) {
        candidates.add(new DefaultPortCandidate(edge, EdgePathPortLocationModel.INSTANCE.createRatioParameter(0.1 * i)));
      }
      // add a dynamic candidate that can be used if shift is pressed to assign the exact location.
      candidates.add(new DefaultPortCandidate(edge, EdgePathPortLocationModel.INSTANCE));
      return IEnumerable.create(candidates);
    }
  }

  /**
   * Called when the snapping button is clicked and toggles snapping
   */
  public void snappingButtonClicked() {
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();

    if (inputMode.getSnapContext().isEnabled()) {

      inputMode.getSnapContext().setEnabled(false);
      inputMode.getLabelSnapContext().setEnabled(false);
    } else {

      inputMode.getSnapContext().setEnabled(true);
      inputMode.getLabelSnapContext().setEnabled(true);
    }
  }

  /**
   * Called when the orthogonal edge button is clicked and toggles creation
   * of orthogonal edges
   */
  public void orthogonalEdgeButtonClicked() {
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
    if (inputMode.getOrthogonalEdgeEditingContext().isEnabled()) {
      inputMode.getOrthogonalEdgeEditingContext().setEnabled(false);
    } else {
      inputMode.getOrthogonalEdgeEditingContext().setEnabled(true);
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
