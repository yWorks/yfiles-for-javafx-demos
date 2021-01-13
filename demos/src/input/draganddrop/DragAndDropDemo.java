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
package input.draganddrop;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.SimpleEdge;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.SimplePort;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.VoidNodeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.DropInputMode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.IDropCreationCallback;
import com.yworks.yfiles.view.input.INodeCreationCallback;
import com.yworks.yfiles.view.input.LabelDropInputMode;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PortDropInputMode;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrates how to use classes{@link NodeDropInputMode},
 * {@link LabelDropInputMode}, and {@link PortDropInputMode}.
 * In contrast to {@link DropInputMode}, the aforementioned specialized modes
 * show a preview of the item while dragging and (in the case of nodes and
 * ports) support snapping.
 *
 * <ul>
 *   <li>
 *     To create a node, drag the desired node style from the left panel onto the
 *     canvas. See the dragged node snap to the grid positions and to other nodes.
 *   </li>
 *   <li>
 *     To create a node label, drag the node label template from the left panel
 *     onto a node in the canvas.
 *   </li>
 *   <li>
 *     To create an edge label, drag the edge label template from the left panel
 *     onto an edge in the canvas.
 *   </li>
 *   <li>
 *     To create a port, drag the port template from the left panel
 *     onto a node in the canvas.
 *   </li>
 * </ul>
 */
public class DragAndDropDemo extends DemoApplication {
  private static final WritableImage EMPTY_IMAGE = new WritableImage(1, 1);

  // components of the application, defined in FXML
  public GraphControl graphControl;
  public WebView helpView;
  public ComboBox<DragMode> dragModeComboBox;

  // the list containing the nodes to drag from
  public ListView<ModelItemTemplate> palette;

  // input mode that handles node creation through drag and drop
  private NodeDropInputMode nodeDropInputMode;
  // input mode that handles label creation through drag and drop
  private LabelDropInputMode labelDropInputMode;
  // input mode that handles port creation through drag and drop
  private PortDropInputMode portDropInputMode;

  /**
   * Initializes the controller.
   */
  public void initialize() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);
  }

  /**
   * Initializes the graph control, input modes, and palette control.
   */
  protected void onLoaded() {
    // create and configure a GraphEditorInputMode for editing the graph
    GraphEditorInputMode editorInputMode = new GraphEditorInputMode();
    // configure interactive snapping of graph elements to a grid
    configureGridSnapping(editorInputMode);
    // configure node darg and drop operations
    configureNodeDropping(editorInputMode);
    // configure label drag and drop operations
    configureLabelDropping(editorInputMode);
    // configure port drag and drop operations
    configurePortDropping(editorInputMode);
    // enable grouping operations
    editorInputMode.setGroupingOperationsAllowed(true);
    // enable support for manual creation of orthogonal edge paths
    editorInputMode.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());
    // use the style, size and labels of the currently selected palette node for newly created nodes
    editorInputMode.setNodeCreator(getPaletteNodeCreator(editorInputMode.getNodeCreator()));

    // register the GraphEditorInputMode as the main input mode
    graphControl.setInputMode(editorInputMode);

    // setup the list to drag nodes from
    configureNodePalette();

    // enables undo functionality
    graphControl.getGraph().setUndoEngineEnabled(true);

    // configures snapping and preview for drag operations
    configureDragMode();

    // populate the control with some nodes
    createSampleGraph();

  }

  /**
   * Configures snapping and preview for drag operations.
   */
  private void configureDragMode() {
    dragModeComboBox.getItems().addAll(DragMode.values());
    dragModeComboBox.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, oldItem, newItem) -> newItem.apply(nodeDropInputMode, labelDropInputMode, portDropInputMode));
    dragModeComboBox.getSelectionModel().selectFirst();
  }

  /**
   * Returns an {@link INodeCreationCallback} wrapper that creates a node using the style, size and labels of the
   * currently selected palette node.
   * @param nodeCreator the original {@link INodeCreationCallback}
   */
  private INodeCreationCallback getPaletteNodeCreator(INodeCreationCallback nodeCreator) {
    return (context, graph, location, parent) -> {
      ModelItemTemplate template = palette.getSelectionModel().getSelectedItem();
      if (template == null) {
        // there is currently no selected item in the palette,
        // thus do not create a new node 
        return null;
      }
      if (ModelItemTemplate.EDGE_LABEL == template ||
          ModelItemTemplate.NODE_LABEL == template) {
        // the currently selected item in the palette is a label template,
        // thus do not create a new node
        return null;
      }
      INode newNode = nodeCreator.createNode(context, graph, location, parent);
      template.apply(graph, newNode, location);
      return newNode;
    };
  }

  /**
   * Configures the visualisation and behavior of the grid snapping feature.
   * @param editorInputMode The GraphEditorInputMode for this application.
   */
  private void configureGridSnapping(GraphEditorInputMode editorInputMode) {
    // enable grid for comfortable snapping
    int gridWidth = 80;
    GridInfo gridInfo = new GridInfo(gridWidth);
    GridVisualCreator grid = new GridVisualCreator(gridInfo);
    graphControl.getBackgroundGroup().addChild(grid, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);

    // create and configure (grid) snapping
    GraphSnapContext context = new GraphSnapContext();
    context.setNodeToNodeDistance(30);
    context.setNodeToEdgeDistance(20);
    context.setSnappingOrthogonalMovementEnabled(false);
    context.setSnapDistance(10);
    context.setSnappingSegmentsToSnapLinesEnabled(true);
    context.setNodeGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
    context.setBendGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
    context.setSnappingBendsToSnapLinesEnabled(true);
    context.setGridSnapType(GridSnapTypes.ALL);

    editorInputMode.setSnapContext(context);
  }

  /**
   * Enables support for dragging nodes from the demo's palette onto the
   * demo's graph control.
   * @param editorInputMode the demo's main input mode.
   */
  private void configureNodeDropping(GraphEditorInputMode editorInputMode) {
    // obtain an input mode for handling node drag and drop operations
    nodeDropInputMode = editorInputMode.getNodeDropInputMode();
    // by default the mode available in GraphEditorInputMode is disabled, so first enable it
    nodeDropInputMode.setEnabled(true);

    // The palette provides normal nodes and group nodes. In this sample the
    // NodeDropInputMode should handle nodes with PanelNodeStyle as group nodes.
    nodeDropInputMode.setIsGroupNodePredicate(draggedNode -> draggedNode.getStyle() instanceof PanelNodeStyle);

    // We allow the NodeDropInputMode to convert a normal node to a group
    // node when a node has been dropped on it
    nodeDropInputMode.setNonGroupNodeAsParentAllowed(true);
    // ... but we restrict that feature to the root and nodes with its tag set to true.
    nodeDropInputMode.setIsValidParentPredicate(node -> node == null || Boolean.TRUE.equals(node.getTag()));

    // In that case the node should look like a group node, so we have to set its
    // style to the group node style, remove all labels and add the "Group Node" label.
    IDropCreationCallback<INode> originalItemCreator = nodeDropInputMode.getItemCreator();
    nodeDropInputMode.setItemCreator((context, graph, draggedItem, dropTarget, dropLocation) -> {
      // create the dropped node
      INode item = originalItemCreator.createItem(context, graph, draggedItem, dropTarget, dropLocation);
      INode parent = graph.getParent(item);
      // check if the parent of the dropped node has become a group node
      if (parent != null && !(parent.getStyle() instanceof PanelNodeStyle)) {
        // let it look like a group node
        graph.setStyle(parent, VALID_PARENT_GROUP_NODE_STYLE);

        // add the group node label and remove all other labels
        IListEnumerable<ILabel> labels = parent.getLabels();
        while (labels.size() > 0) {
          graph.remove(labels.getItem(labels.size() - 1));
        }
        graph.addLabel(parent, "Group Node", GROUP_NODE_LABEL_MODEL_PARAMETER, GROUP_NODE_LABEL_STYLE);

        graph.adjustGroupNodeLayout(parent);
      }
      return item;
    });
  }

  /**
   * Enables support for dragging labels from the demo's palette onto nodes
   * and edges in the demo's graph control.
   * @param editorInputMode the demo's main input mode.
   */
  private void configureLabelDropping( GraphEditorInputMode editorInputMode ) {
    // obtain an input mode for handling label drag and drop operations
    labelDropInputMode = editorInputMode.getLabelDropInputMode();
    // by default the mode available in GraphEditorInputMode is disabled, so first enable it
    labelDropInputMode.setEnabled(true);
    // right after a successful label drop, start the TextEditorInputMode to
    // allow users to enter meaningful text 
    labelDropInputMode.setAutoEditingLabelEnabled(true);
    // dynamically create a suitable label model parameter for dropped labels
    labelDropInputMode.setUsingBestMatchingParameterEnabled(true);
  }

  /**
   * Enables support for dragging ports from the demo's palette onto nodes
   * in the demo's graph control.
   * @param editorInputMode the demo's main input mode.
   */
  private void configurePortDropping( GraphEditorInputMode editorInputMode ) {
    // obtain an input mode for handling port drag and drop operations
    portDropInputMode = editorInputMode.getPortDropInputMode();
    // by default the mode available in GraphEditorInputMode is disabled, so first enable it
    portDropInputMode.setEnabled(true);
    // dynamically create a suitable port model parameter for dropped ports
    portDropInputMode.setUsingBestMatchingParameterEnabled(true);
  }

  /**
   * Populates the palette with several normal nodes that use different
   * node styles for visualization and a group node.
   * The elements in the palette can be dragged over and dropped into the demo's
   * <code>GraphControl</code> to create new elements of the corresponding type
   * in the displayed diagram.
   */
  private void configureNodePalette() {
    // maps transfer data (text that serves as IDs) to node templates from which
    // to create nodes on drop
    Map nodeDropData = nodeDropInputMode.getDropDataMap();
    // maps transfer data (text that serves as IDs) to label templates from which
    // to create nodes on drop
    Map labelDropData = labelDropInputMode.getDropDataMap();
    // maps transfer data (text that serves as IDs) to port templates from which
    // to create nodes on drop
    Map portDropData = portDropInputMode.getDropDataMap();

    // fill the palette with sample nodes
    for (ModelItemTemplate template : ModelItemTemplate.values()) {
      palette.getItems().add(template);

      // the name of the template is the actual data transfered in the
      // drag and drop operation - thus this name has to be registered with
      // the corresponding DropInputMode
      switch (template) {
        case NODE_LABEL:
          labelDropData.put(template.name(), template.getItem());
          break;
        case EDGE_LABEL:
          SimpleNode src = new SimpleNode();
          src.setLayout(new RectD(0, 0, 1, 1));
          SimpleNode tgt = new SimpleNode();
          tgt.setLayout(new RectD(0, 100, 1, 1));
          SimplePort p1 = new SimplePort(src, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
          SimplePort p2 = new SimplePort(tgt, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
          SimpleEdge edge = new SimpleEdge(p1, p2);

          ILabel labelTemplate = (ILabel) template.getItem();
          SimpleLabel dummyLabel = new SimpleLabel(edge, labelTemplate.getText(), FreeEdgeLabelModel.INSTANCE.createDefaultParameter());
          dummyLabel.setStyle(labelTemplate.getStyle());
          dummyLabel.setTag(labelTemplate.getTag());
          dummyLabel.setPreferredSize(labelTemplate.getPreferredSize());

          labelDropData.put(template.name(), dummyLabel);
          break;
        case PORT:
          portDropData.put(template.name(), template.getItem());
          break;
        default:
          nodeDropData.put(template.name(), template.getItem());
          break;
      }
    }

    // set a cell factory for NodeTemplates to use in the palette
    palette.setCellFactory(palette -> {
      ModelItemTemplateListCell cell = new ModelItemTemplateListCell();
      // when the user starts dragging a node in the palette, setup the DnD information
      cell.setOnDragDetected(event -> {
        // start the drag
        Dragboard db = palette.startDragAndDrop(TransferMode.ANY);
        Map<DataFormat, Object> contentMap = new HashMap<>();

        // use the name of the selected template as content.
        contentMap.put(DropInputMode.DATA_FORMAT_DROP_ID, palette.getSelectionModel().getSelectedItem().name());
        db.setContent(contentMap);
        // to prevent a semi-transparent paper appears above the dragged node on MacOSX,
        // we set the drag view to a blank image
        db.setDragView(EMPTY_IMAGE);
        event.consume();
      });

      cell.setOnDragDone(Event::consume);
      return cell;
    });

    palette.getSelectionModel().selectFirst();
  }

  private void createSampleGraph() {
    IGraph graph = graphControl.getGraph();

    // create a group node into which dragged nodes can be dropped
    INode group1 = createGroupNode(graph, 100, 150, true);
    graph.addLabel(group1, "Drop a node onto me!", ExteriorLabelModel.SOUTH);

    // create a group node into which dragged nodes cannot be dropped
    INode group2 = createGroupNode(graph, 200, 400, false);
    graph.addLabel(group2, "I don't accept dropped nodes as children!", ExteriorLabelModel.SOUTH);

    // create a node to which dragged nodes can snap
    INode node1 = createNode(graph, 400, 100, false);
    graph.addLabel(node1, "Sample Node", ExteriorLabelModel.NORTH);
    graph.addLabel(node1, "You cannot convert \nme into a group node!", ExteriorLabelModel.SOUTH);

    // create a node which can be converted to a group node automatically, if a node is dropped onto it
    INode node2 = createNode(graph, 550, 300, true);
    graph.addLabel(node2, "Sample Node", ExteriorLabelModel.NORTH);
    graph.addLabel(node2, "Drop a node onto me to \nconvert me to a group node!", ExteriorLabelModel.SOUTH);
  }

  // we can share the styles for this demo.
  public static final PanelNodeStyle VALID_PARENT_GROUP_NODE_STYLE = new PanelNodeStyle();
  public static final PanelNodeStyle INVALID_PARENT_GROUP_NODE_STYLE = new PanelNodeStyle();

  public static final ShinyPlateNodeStyle VALID_PARENT_NODE_STYLE = new ShinyPlateNodeStyle();
  public static final ShinyPlateNodeStyle INVALID_PARENT_NODE_STYLE = new ShinyPlateNodeStyle();

  public static final DefaultLabelStyle GROUP_NODE_LABEL_STYLE = new DefaultLabelStyle();
  public static final ILabelModelParameter GROUP_NODE_LABEL_MODEL_PARAMETER;


  static {
    InsetsD groupNodeInsets = new InsetsD(25, 5, 5, 5);

    VALID_PARENT_GROUP_NODE_STYLE.setInsets(groupNodeInsets);
    VALID_PARENT_GROUP_NODE_STYLE.setColor(Color.FORESTGREEN);
    VALID_PARENT_GROUP_NODE_STYLE.setLabelInsetsColor(Color.FORESTGREEN);

    INVALID_PARENT_GROUP_NODE_STYLE.setInsets(groupNodeInsets);
    INVALID_PARENT_GROUP_NODE_STYLE.setColor(Color.FIREBRICK);
    INVALID_PARENT_GROUP_NODE_STYLE.setLabelInsetsColor(Color.FIREBRICK);

    VALID_PARENT_NODE_STYLE.setPaint(Color.FORESTGREEN);
    INVALID_PARENT_NODE_STYLE.setPaint(Color.FIREBRICK);

    GROUP_NODE_LABEL_STYLE.setTextPaint(Color.WHITE);
    InteriorStretchLabelModel labelModel = new InteriorStretchLabelModel();
    labelModel.setInsets(new InsetsD(2, 5, 4, 5));
    GROUP_NODE_LABEL_MODEL_PARAMETER = labelModel.createParameter(InteriorStretchLabelModel.Position.NORTH);
  }

  /**
   * Convenience factory method that creates a group node in the given graph with a specific styling.
   */
  static INode createGroupNode(IGraph graph, double x, double y, boolean isValidParent) {
    PanelNodeStyle groupNodeStyle = isValidParent ? VALID_PARENT_GROUP_NODE_STYLE : INVALID_PARENT_GROUP_NODE_STYLE;
    INode groupNode = graph.createGroupNode(null, new RectD(x, y, 160, 130), groupNodeStyle, isValidParent);
    graph.addLabel(groupNode, "Group Node", GROUP_NODE_LABEL_MODEL_PARAMETER, GROUP_NODE_LABEL_STYLE);
    return groupNode;
  }

  /**
   * Convenience factory method that creates a normal node in the given graph with a specific styling.
   */
  static INode createNode(IGraph graph, double x, double y, boolean isValidParent) {
    ShinyPlateNodeStyle style = isValidParent ? VALID_PARENT_NODE_STYLE : INVALID_PARENT_NODE_STYLE;
    return graph.createNode(new RectD(x, y, 40, 40), style, isValidParent);
  }

  /**
   * Creates a palette label template in the given graph.
   */
  static ILabel createLabel( IGraph graph, String text, ILabelModelParameter parameter ) {
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setBackgroundPen(new Pen(Color.STEELBLUE.darker()));
    style.setBackgroundPaint(Color.STEELBLUE);
    style.setTextPaint(Color.WHITE);
    style.setInsets(new InsetsD(4, 4, 4, 4));
    INode node = graph.createNode(new RectD(0, 0, 70, 70), VoidNodeStyle.INSTANCE);
    return graph.addLabel(node, text, parameter, style);
  }

  /**
   * Creates a palette port template in the given graph.
   */
  static IPort createPort( IGraph graph, IPortLocationModelParameter parameter ) {
    ShapeNodeStyle shape = new ShapeNodeStyle();
    shape.setShape(ShapeNodeShape.ELLIPSE);
    shape.setPaint(Color.STEELBLUE);
    shape.setPen(new Pen(Color.STEELBLUE.darker()));
    NodeStylePortStyleAdapter style = new NodeStylePortStyleAdapter();
    style.setNodeStyle(shape);
    style.setRenderSize(new SizeD(8, 8));
    INode node = graph.createNode(new RectD(0, 0, 70, 70), VoidNodeStyle.INSTANCE);
    return graph.addPort(node, parameter, style);
  }

  public static void main(String[] args) {
    launch(args);
  }



  /**
   * A {@link javafx.scene.control.ListCell} that shows images for the enum
   * constants of {@link ModelItemTemplate} in a {@link ListView}.
   */
  private static final class ModelItemTemplateListCell extends ListCell<ModelItemTemplate> {
    @Override
    public void updateItem( ModelItemTemplate template, boolean empty) {
      super.updateItem(template, empty);

      if (empty) {
        // what to do if the cell has no template
        setGraphic(null);
        setText(null);
      } else {
        setGraphic(template.getImage());
        setText(null);
        switch (template) {
          case EDGE_LABEL:
            setTooltip(new Tooltip("Edge Label"));
            break;
          case NODE_LABEL:
            setTooltip(new Tooltip("Node Label"));
            break;
          case PORT:
            setTooltip(new Tooltip("Port"));
            break;
          default:
            setTooltip(null);
            break;
        }
      }
    }
  }
}
