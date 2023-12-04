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
package complete.rotatablenodes;

import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.EdgePathLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.styles.CornerStyle;
import com.yworks.yfiles.graph.styles.Corners;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.radial.RadialLayout;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.WaitInputMode;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.IconProvider;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * Demo code that shows how support for rotated node visualizations can be implemented on top of the yFiles library.
 * A custom {@link INodeStyle} implementation is used to encapsulate most of the added functionality.
 */
public class RotatableNodesDemo extends DemoApplication {

  public GraphControl graphControl;
  public WebView helpView;
  public ComboBox<SampleInfo> sampleComboBox;

  public void snappingButtonClicked(ActionEvent e) {
    if (e.getSource() instanceof ToggleButton) {
      boolean selected = ((ToggleButton) e.getSource()).isSelected();
      GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
      inputMode.getSnapContext().setEnabled(selected);
      inputMode.getLabelSnapContext().setEnabled(selected);
    }
  }

  public void orthogonalEdgeButtonClicked(ActionEvent e) {
    if (e.getSource() instanceof ToggleButton) {
      boolean selected = ((ToggleButton) e.getSource()).isSelected();
      GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();
      inputMode.getOrthogonalEdgeEditingContext().setEnabled(selected);
    }
  }

  /**
   * Initializes the demo.
   */
  public void initialize() {
    graphControl.setFileIOEnabled(true);

    initializeInputMode();
    initializeGraphML();
    initializeGraph();

    configureLayoutToolbar();

    configureSampleComboBox();
    WebViewUtils.initHelp(helpView, this);
  }


  public ToolBar toolBar;

  private void configureLayoutToolbar() {
    ComboBox<LayoutInfo> layoutComboBox = new ComboBox<>();
    layoutComboBox.setTooltip(new Tooltip("Set the layout"));
    configureLayoutComboBox(layoutComboBox);
    toolBar.getItems().add(layoutComboBox);

    Button applyLayoutButton = new Button("", new ImageView(IconProvider.RELOAD));
    applyLayoutButton.setTooltip(new Tooltip("Apply the layout"));
    applyLayoutButton.setOnAction(event -> {
      LayoutInfo layoutInfo = layoutComboBox.getSelectionModel().getSelectedItem();
      applyLayout(layoutInfo);
    });
    toolBar.getItems().add(applyLayoutButton);

    GraphEditorInputMode geim = (GraphEditorInputMode) graphControl.getInputMode();
    WaitInputMode wim = geim.getWaitInputMode();
    wim.addWaitingStartedListener((source, args) -> disableControls(layoutComboBox, applyLayoutButton, true));
    wim.addWaitingEndedListener((source, args) -> disableControls(layoutComboBox, applyLayoutButton, false));
  }

  private void disableControls(ComboBox<LayoutInfo> layoutComboBox, Button layoutButton, boolean disable) {
    layoutComboBox.setDisable(disable);
    layoutButton.setDisable(disable);
    sampleComboBox.setDisable(disable);
  }

  private void configureLayoutComboBox(ComboBox<LayoutInfo> layoutComboBox) {
    IGraph graph = graphControl.getGraph();

    SizeD size = graph.getNodeDefaults().getSize();
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setPreferredEdgeLength(1.5 * Math.max(size.getWidth(), size.getHeight()));

    TreeReductionStage treeLayout = new TreeReductionStage(new TreeLayout());
    treeLayout.setNonTreeEdgeRouter(new OrganicEdgeRouter());

    TreeReductionStage balloonLayout = new TreeReductionStage(new BalloonLayout());
    balloonLayout.setNonTreeEdgeRouter(new OrganicEdgeRouter());

    OrganicEdgeRouter organicEdgeRouter = new OrganicEdgeRouter();
    organicEdgeRouter.setEdgeNodeOverlapAllowed(false);

    layoutComboBox.getItems().addAll(
        new LayoutInfo("Layout: Hierarchic", new HierarchicLayout(), RotatedNodeLayoutStage.RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER),
        new LayoutInfo("Layout: Organic", organicLayout, RotatedNodeLayoutStage.RoutingMode.NO_ROUTING),
        new LayoutInfo("Layout: Orthogonal", new OrthogonalLayout(), RotatedNodeLayoutStage.RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER),
        new LayoutInfo("Layout: Circular", new CircularLayout(), RotatedNodeLayoutStage.RoutingMode.NO_ROUTING),
        new LayoutInfo("Layout: Tree", treeLayout, RotatedNodeLayoutStage.RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER),
        new LayoutInfo("Layout: Balloon", balloonLayout, RotatedNodeLayoutStage.RoutingMode.NO_ROUTING),
        new LayoutInfo("Layout: Radial", new RadialLayout(), RotatedNodeLayoutStage.RoutingMode.NO_ROUTING),
        new LayoutInfo("Routing: Polyline", new EdgeRouter(), RotatedNodeLayoutStage.RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER),
        new LayoutInfo("Routing: Organic", organicEdgeRouter, RotatedNodeLayoutStage.RoutingMode.NO_ROUTING)
    );
    layoutComboBox.getSelectionModel().select(7);
    layoutComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> applyLayout(newValue));
  }

  /**
   * Runs a layout algorithm which is configured to consider node rotations.
   * @param layoutInfo specifies layout algorithm and routing mode
   */
  public void applyLayout(LayoutInfo layoutInfo) {
    IGraph graph = graphControl.getGraph();

    // provide the rotated outline and layout for the layout algorithm
    graph.getMapperRegistry().createFunctionMapper(RotatedNodeLayoutStage.ROTATED_NODE_LAYOUT_DP_KEY, node -> {
      INodeStyle style = node.getStyle();
      return new RotatedNodeLayoutStage.RotatedNodeShape(style.getRenderer().getShapeGeometry(node, style).getOutline(),
          style instanceof RotatableNodeStyleDecorator
              ? ((RotatableNodeStyleDecorator) style).getRotatedLayout(node)
              : new OrientedRectangle(node.getLayout()));
    });

    // get the selected layout algorithm
    ILayoutAlgorithm layout = layoutInfo.layoutAlgorithm;

    // wrap the algorithm in RotatedNodeLayoutStage to make it aware of the node rotations
    RotatedNodeLayoutStage rotatedLayout = new RotatedNodeLayoutStage(layout);
    rotatedLayout.setEdgeRoutingMode(layoutInfo.routingMode);


    // apply the layout
    graphControl.morphLayout(rotatedLayout, Duration.ofMillis(700));

    // clean up mapper registry
    graph.getMapperRegistry().removeMapper(RotatedNodeLayoutStage.ROTATED_NODE_LAYOUT_DP_KEY);
  }

  /**
   * Entry of the layoutComboBox containing the display name and the routing mode for a given layout algorithm.
   */
  private static class LayoutInfo {
    // The name to display in the combobox.
    String displayName;

    // The layout algorithm.
    ILayoutAlgorithm layoutAlgorithm;

    // The routing mode that suits the selected layout algorithm.
    // Layout algorithm that place edge ports in the center of the node don't need to add a routing step.
    RotatedNodeLayoutStage.RoutingMode routingMode;

    LayoutInfo(String displayName, ILayoutAlgorithm layoutAlgorithm, RotatedNodeLayoutStage.RoutingMode routingMode) {
      this.displayName = displayName;
      this.layoutAlgorithm = layoutAlgorithm;
      this.routingMode = routingMode;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }


  private void configureSampleComboBox() {
    sampleComboBox.getItems().addAll(
        new SampleInfo("Sample: Sine", "sine"),
        new SampleInfo("Sample: Circle", "circle")
    );
    sampleComboBox.getSelectionModel().select(0);
    sampleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadGraph(newValue.fileName));
  }

  /**
   * Entry of the {@link #sampleComboBox} containing the file name and the display name of a sample graph.
   */
  private static class SampleInfo {
    private String fileName;
    private String displayName;

    SampleInfo(String displayName, String fileName) {
      this.fileName = fileName;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  /**
   * Initialize the interaction with the graph.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();

    //enable orthogonal edge editing
    geim.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());

    //enable snapping only for resizing nodes and only to the same size of other nodes
    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setEnabled(false);
    snapContext.setCollectingNodePairSegmentSnapLinesEnabled(false);
    snapContext.setCollectingNodePairSnapLinesEnabled(false);
    snapContext.setCollectingEdgeSnapLinesEnabled(false);
    snapContext.setCollectingNodeSnapLinesEnabled(false);
    snapContext.setCollectingPortSnapLinesEnabled(false);
    snapContext.setSnappingBendAdjacentSegmentsEnabled(false);
    snapContext.setCollectingNodeSizesEnabled(true);
    geim.setSnapContext(snapContext);

    LabelSnapContext labelSnapContext = new LabelSnapContext();
    labelSnapContext.setEnabled(false);
    geim.setLabelSnapContext(labelSnapContext);

    geim.setClipboardOperationsAllowed(true);
    geim.setGroupingOperationsAllowed(true);
    geim.getOrthogonalEdgeEditingContext().setEnabled(false);

    graphControl.setInputMode(geim);
  }

  /**
   * Initialize loading from and saving to graphml-flies.
   */
  private void initializeGraphML() {
    //initialize (de-)serialization for load/save commands
    GraphMLIOHandler graphMLHandler = new GraphMLIOHandler();
    String namespace = "http://www.yworks.com/yfiles-for-java-fx/demos/RotatableNodes/1.0";
    graphMLHandler.addXamlNamespaceMapping(namespace, "complete.rotatablenodes", getClass().getClassLoader());
    graphMLHandler.addNamespace(namespace, "demo");

    graphMLHandler.addParsedListener((o, parseEventArgs) -> {
      IGraph graph = graphControl.getGraph();

      //Iterate over every node which isn't a group node and is instance of the rotatableNodeStyleDecorator
      for (INode node : graph.getNodes()) {
        if (!graph.isGroupNode(node) && !(node.getStyle() instanceof RotatableNodeStyleDecorator)) {
          graph.setStyle(node, new RotatableNodeStyleDecorator(node.getStyle(), 0));
        }

        //iterate over every label of the current node which is instance of the corresponding decorator
        for (ILabel label : node.getLabels()) {
          ILabelModel model = label.getLayoutParameter().getModel();
          if(!(model instanceof RotatableNodeLabelModelDecorator)){
            graph.setLabelLayoutParameter(label,
                    new RotatableNodeLabelModelDecorator(model)
                            .createWrappingParameter(label.getLayoutParameter()));
          }
        }

        //iterate over every port of the current node which is instance of the corresponding decorator
        for(IPort port : node.getPorts()) {
          IPortLocationModel model = port.getLocationParameter().getModel();
          if(!(model instanceof RotatablePortLocationModelDecorator)){
            graph.setPortLocationParameter(port, RotatablePortLocationModelDecorator.INSTANCE
                    .createWrappingParameter(port.getLocationParameter()));
          }
        }
      }
    });
    graphControl.setGraphMLIOHandler(graphMLHandler);
  }

  /**
   * Initializes styles and decorators for the graph.
   */
  private void  initializeGraph() {
    FoldingManager foldingManager = new FoldingManager();
    IGraph graph = foldingManager.createFoldingView().getGraph();

    GraphDecorator decorator = graph.getDecorator();

    //for rotated nodes we need to provide port candidates that are backed by a rotatable port location model
    //if you want to support non-rotated port candidates you can just provide undecorated instances here
    decorator.getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
            node -> node.getStyle() instanceof  RotatableNodeStyleDecorator,
            RotatableNodesDemo::createPortCandidateProvider);

    decorator.getPortDecorator().getEdgePathCropperDecorator().setImplementation(new AdjustOutlinePortInsidenessEdgePathCropper());
    decorator.getNodeDecorator().getGroupBoundsCalculatorDecorator().setImplementation(new RotationAwareGroupBoundsCalculator());

    graph.getNodeDefaults().setStyle(new RotatableNodeStyleDecorator(DemoStyles.createDemoNodeStyle(), 0));
    graph.getNodeDefaults().setStyleInstanceSharingEnabled(false);
    graph.getNodeDefaults().setSize(new SizeD(100, 50));

    InteriorLabelModel coreLabelModel = new InteriorLabelModel();
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(
            new RotatableNodeLabelModelDecorator(coreLabelModel).createWrappingParameter(InteriorLabelModel.CENTER));

    //Make ports visible
    ShapeNodeStyle portStyle = new ShapeNodeStyle();
    portStyle.setShape(ShapeNodeShape.ELLIPSE);
    portStyle.setPaint(Color.rgb(0x66, 0x2b, 0x00));
    portStyle.setPen(new Pen(Color.rgb(0x66, 0x2b, 0x00), 1.5));
    graph.getNodeDefaults().getPortDefaults().setStyle(new NodeStylePortStyleAdapter(portStyle));

    //usa a rotatable port model as default
    graph.getNodeDefaults().getPortDefaults().setLocationParameter(
            new RotatablePortLocationModelDecorator().createWrappingParameter(FreeNodePortLocationModel.NODE_TOP_ANCHORED));

    graph.getGroupNodeDefaults().setStyle(DemoStyles.createDemoGroupStyle(null, true));

    EdgePathLabelModel edgePathLabelModel = new EdgePathLabelModel();
    edgePathLabelModel.setDistance(10);
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(edgePathLabelModel.createDefaultParameter());
    graph.getEdgeDefaults().getLabelDefaults().setStyle(DemoStyles.createDemoEdgeLabelStyle());
    graph.getEdgeDefaults().setStyle(DemoStyles.createDemoEdgeStyle());

    //enable undo
    foldingManager.getMasterGraph().setUndoEngineEnabled(true);

    graphControl.setGraph(graph);
  }

  /**
   * Creates an IPortCandidateProvider that considers the node's shape and rotation.
   */
  private static IPortCandidateProvider createPortCandidateProvider(INode node){
    RotatablePortLocationModelDecorator rotatedPortModel = RotatablePortLocationModelDecorator.INSTANCE;
    FreeNodePortLocationModel freeModel = FreeNodePortLocationModel.INSTANCE;

    RotatableNodeStyleDecorator rnsd = (RotatableNodeStyleDecorator) node.getStyle();
    INodeStyle wrapped = rnsd.getWrapped();
    ShapeNodeStyle sns = wrapped instanceof ShapeNodeStyle ? (ShapeNodeStyle)wrapped : null;

    if (sns != null && sns.getShape() == ShapeNodeShape.ROUND_RECTANGLE) {
      // if you are using the deprecated ShinyPlateNodeStyle or BevelNodeStyle, you could use this code path as well
      return IPortCandidateProvider.combine(
              //take all existing ports (assumed they have the correct port location model)
              IPortCandidateProvider.fromUnoccupiedPorts(node),

              //provide explicit candidates (all backed by a rotatable port location model)
              IPortCandidateProvider.fromCandidates(
                      //Port candidates at the corners that are slightly inset
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              freeModel.createParameter(new PointD(0, 0), new PointD(5, 5)))),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              freeModel.createParameter(new PointD(0, 1), new PointD(5, -5)))),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              freeModel.createParameter(new PointD(1, 0), new PointD(-5, 5)))),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              freeModel.createParameter(new PointD(1, 1), new PointD(-5, -5)))),

                      //Port candidates at the sides and center
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_CENTER_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_TOP_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_RIGHT_ANCHORED))
              ));
    }

    if (wrapped instanceof RectangleNodeStyle) {
      RectangleNodeStyle rns = (RectangleNodeStyle) wrapped;
      // Rectangle: create ports in the corners and
      double cornerSize = rns.getCornerSize() * (rns.getCornerStyle() == CornerStyle.CUT ? 0.5 : 0.3);
      return IPortCandidateProvider.combine(
              //Take all existing ports - these are assumed to have the correct port location model
              IPortCandidateProvider.fromUnoccupiedPorts(node),
              //Provide explicit candidates - these are all backed by a rotatable port location model
              IPortCandidateProvider.fromCandidates(
                      //Port candidates at the corners
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              rns.getCorners().contains(Corners.TOP_LEFT)
                                      ? freeModel.createParameter(new PointD(0, 0), new PointD(cornerSize, cornerSize))
                                      : FreeNodePortLocationModel.NODE_TOP_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              rns.getCorners().contains(Corners.BOTTOM_LEFT)
                                      ? freeModel.createParameter(new PointD(0, 1), new PointD(cornerSize, -cornerSize))
                                      : FreeNodePortLocationModel.NODE_BOTTOM_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              rns.getCorners().contains(Corners.TOP_RIGHT)
                                      ? freeModel.createParameter(new PointD(1, 0), new PointD(-cornerSize, cornerSize))
                                      : FreeNodePortLocationModel.NODE_TOP_RIGHT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                             rns.getCorners().contains(Corners.BOTTOM_RIGHT)
                                      ? freeModel.createParameter(new PointD(1, 1), new PointD(-cornerSize, -cornerSize))
                                      : FreeNodePortLocationModel.NODE_BOTTOM_RIGHT_ANCHORED)),
                      //Port candidates at the sides and the center
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(FreeNodePortLocationModel.NODE_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(FreeNodePortLocationModel.NODE_CENTER_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(FreeNodePortLocationModel.NODE_TOP_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(FreeNodePortLocationModel.NODE_RIGHT_ANCHORED))
              ));
    }

    if (sns != null && sns.getShape() == ShapeNodeShape.RECTANGLE) {
      return IPortCandidateProvider.combine(
              IPortCandidateProvider.fromUnoccupiedPorts(node),
              IPortCandidateProvider.fromCandidates(
                      //Port candidates at the corners
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_TOP_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_TOP_RIGHT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_BOTTOM_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_BOTTOM_RIGHT_ANCHORED)),

                      //Port candidates at the sides and the center
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_CENTER_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_TOP_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_RIGHT_ANCHORED))
              ));
    }

    if(sns != null) {
      //can be arbitrary shape. first create a dummy node that is not rotated
      SimpleNode dummyNode = new SimpleNode();
      dummyNode.setStyle(sns);
      dummyNode.setLayout(node.getLayout());

      AbstractPortCandidateProvider shapeProvider = IPortCandidateProvider.fromShapeGeometry(dummyNode, 0);
      Iterable<IPortCandidate> shapeCandidates = shapeProvider.getTargetPortCandidates(null);

      List<IPortCandidate> rotatingCandidates = new ArrayList<>();
      for (IPortCandidate candidate : shapeCandidates) {
        rotatingCandidates.add(new DefaultPortCandidate(node,
                rotatedPortModel.createWrappingParameter(candidate.getLocationParameter())));
      }

      return IPortCandidateProvider.combine(
              IPortCandidateProvider.fromUnoccupiedPorts(node),
              IPortCandidateProvider.fromCandidates(rotatingCandidates));

    }
    return  null;
  }

  /**
   * Loads the graph from the "resources" folder.
   */
  private void loadGraph(String graphName) {
    graphControl.getGraph().clear();

    try {
      graphControl.importFromGraphML(getClass().getResource("resources/" + graphName + ".graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Centers and arranges the graph in the graph control.
   */
  @Override
  public void onLoaded() {
    loadGraph("sine");
  }

  public static void main(String[] args) {
    launch(args);
  }
}

