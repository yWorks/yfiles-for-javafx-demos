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
package viewer.renderingorder;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.*;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.*;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GraphModelManager;
import com.yworks.yfiles.view.HierarchicNestingPolicy;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.LabelLayerPolicy;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.PortLayerPolicy;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.WebViewUtils;

/**
 * This demo shows the effect of different render policies on the z-order of nodes, edges, labels and ports.
 */
public class RenderingOrderDemo extends DemoApplication {
  public WebView helpView;
  public GraphControl graphControl;
  public ComboBox<String> renderingOrderComboBox;

  //default Node height and width
  private static final int DEFAULT_NODE_SIZE = 50;

  //default preferred size for the boundary rectangles
  private static final SizeD DEFAULT_PREF_RECTANGLE_SIZE = new SizeD(340, 30);

  //default pen for drawing the boundary rectangles
  private Pen defaultRectanglePen;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   */
  public void initialize() {
    initializeInputMode();
    initializeGraph();

    createGraph();

    WebViewUtils.initHelp(helpView, this);
  }

  /**
   * Applies the rendering order that was selected in the renderingOrderComboBox.
   */
  public void renderingOrderBoxChanged() {
    //no graph -> do nothing
    if (graphControl == null) {
      return;
    }

    //reset to default
    GraphModelManager graphModelManager = graphControl.getGraphModelManager();
    graphModelManager.setLabelLayerPolicy(LabelLayerPolicy.SEPARATE_LAYER);
    graphModelManager.setPortLayerPolicy(PortLayerPolicy.SEPARATE_LAYER);
    graphModelManager.setHierarchicNestingPolicy(HierarchicNestingPolicy.NODES_AND_EDGES);
    graphModelManager.getEdgeGroup().below(graphModelManager.getNodeGroup());

    switch (renderingOrderComboBox.getSelectionModel().getSelectedIndex()) {
      case 0:
        //default -> do nothing
        break;
      case 1:
        //at owner
        graphModelManager.setLabelLayerPolicy(LabelLayerPolicy.AT_OWNER);
        graphModelManager.setPortLayerPolicy(PortLayerPolicy.AT_OWNER);
        break;
      case 2:
        //edges on top
        graphModelManager.setLabelLayerPolicy(LabelLayerPolicy.AT_OWNER);
        graphModelManager.setPortLayerPolicy(PortLayerPolicy.AT_OWNER);
        graphModelManager.setHierarchicNestingPolicy(HierarchicNestingPolicy.NODES);
        graphModelManager.getEdgeGroup().above(graphModelManager.getNodeGroup());
        break;
      case 3:
        //group nodes
        graphModelManager.setHierarchicNestingPolicy(HierarchicNestingPolicy.GROUP_NODES);
        break;
      case 4:
        //none
        graphModelManager.setHierarchicNestingPolicy(HierarchicNestingPolicy.NONE);
        break;
    }
  }


  /**
   * Generates the sample graph.
   */
  private void createGraph() {
    createOverlappingLabelSample(new PointD(-290, 0));
    createOverlappingNodeSample(new PointD(10, 0));
    createOverlappingEdgeSample(new PointD(370, 0));
    createNestedGroupSample(new PointD(800, 0));
  }

  /**
   * Resets the graph to the initial sample.
   */
  public void resetGraph() {
    for (ICanvasObject border : graphControl.getBackgroundGroup().toList()) {
      border.remove();
    }
    graphControl.getGraph().clear();
    createGraph();
    graphControl.fitGraphBounds();
  }

  /**
   * Centers and arranges the graph in the graph control.
   */
  @Override
  public void onLoaded() {
    //center the graph to prevent the initial layout fading in from the top left corner
    graphControl.setContentMargins(new InsetsD(50));
    graphControl.fitGraphBounds();
  }

  /**
   * Configures the inputMode to support interactive grouping and to add ports to interactively created nodes.
   */
  private void initializeInputMode() {
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setGroupingOperationsAllowed(true);

    //add NodeCreated Listener which adds ports to a new node
    inputMode.addNodeCreatedListener((o, args) -> {
      IGraph graph = graphControl.getGraph();
      INode node = args.getItem();

      graph.addPort(node, FreeNodePortLocationModel.NODE_TOP_ANCHORED);
      graph.addPort(node, FreeNodePortLocationModel.NODE_RIGHT_ANCHORED);
      graph.addPort(node, FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED);
      graph.addPort(node, FreeNodePortLocationModel.NODE_LEFT_ANCHORED);
    });

    graphControl.setInputMode(inputMode);
  }

  /**
   * Initializes graph defaults for nodes, labels and ports.
   */
  private void initializeGraph() {
    //configure node defaults
    INodeDefaults nodeDefaults = graphControl.getGraph().getNodeDefaults();

    //set default node color as slightly transparent dark orange
    RectangleNodeStyle defaultNodeStyle = DemoStyles.createDemoNodeStyle();
    defaultNodeStyle.setPaint(Color.rgb(255, 140, 0, 238d/255));
    defaultNodeStyle.setPen(Pen.getWhite());

    nodeDefaults.setStyle(defaultNodeStyle);

    //set default node size
    SizeD defaultNodeSize = new SizeD(40, 40);
    nodeDefaults.setSize(defaultNodeSize);

    //set label text alignment and trimming
    DefaultLabelStyle defaultLabelStyle = DemoStyles.createDemoNodeLabelStyle();
    defaultLabelStyle.setVerticalTextAlignment(VPos.CENTER);
    defaultLabelStyle.setTextWrapping(TextWrapping.WRAP);
    nodeDefaults.getLabelDefaults().setStyle(defaultLabelStyle);

    //set default label position
    nodeDefaults.getLabelDefaults().setLayoutParameter(InteriorLabelModel.CENTER);

    //Set default port color blue and shape to ellipse
    ShapeNodeStyle defaultPortStyle = new ShapeNodeStyle();
    defaultPortStyle.setPaint(Color.ROYALBLUE);
    defaultPortStyle.setShape(ShapeNodeShape.ELLIPSE);
    defaultPortStyle.setPen(Pen.getDarkBlue());

    NodeStylePortStyleAdapter portStyleAdapter = new NodeStylePortStyleAdapter(defaultPortStyle);

    nodeDefaults.getPortDefaults().setStyle(portStyleAdapter);

    //setting node group styles
    INodeDefaults groupNodeDefaults = graphControl.getGraph().getGroupNodeDefaults();

    //set default group node style
    GroupNodeStyle defaultGroupNodeStyle = DemoStyles.createDemoGroupStyle();
    groupNodeDefaults.setStyle(defaultGroupNodeStyle);

    //set defaults for group node labels
    ILabelStyle defaultGroupLabelStyle = DemoStyles.createDemoGroupLabelStyle();
    groupNodeDefaults.getLabelDefaults().setStyle(defaultGroupLabelStyle);
    groupNodeDefaults.getLabelDefaults().setLayoutParameter(new GroupNodeLabelModel().createDefaultParameter());

    //configure edge label defaults
    graphControl.getGraph().getEdgeDefaults().getLabelDefaults().setLayoutParameter(
            new SmartEdgeLabelModel().createParameterFromSource(0, 5, 0));

    DefaultLabelStyle defaultEdgeLabelStyle = DemoStyles.createDemoEdgeLabelStyle();
    graphControl.getGraph().getEdgeDefaults().getLabelDefaults().setStyle(defaultEdgeLabelStyle);

    //set default pen used for boundary rectangles
    defaultRectanglePen = new Pen(Color.GRAY, 3);
    defaultRectanglePen.setDashStyle(DashStyle.getDot());
  }

  /**
   * Creates a sample graph with overlapping exterior node labels.
   */
  private void createOverlappingLabelSample(PointD origin) {
    final IGraph graph = graphControl.getGraph();

    //add two nodes with label
    INode firstNode = graph.createNode(new RectD(origin.x, origin.y + 50,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE), graph.getNodeDefaults().getStyle());
    INode secondNode = graph.createNode(new RectD(origin.x + 60, origin.y + 80,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE), graph.getNodeDefaults().getStyle());

    graph.addLabel(firstNode, "External Node Label 1", ExteriorLabelModel.SOUTH);
    graph.addLabel(secondNode, "External Node Label 2", ExteriorLabelModel.SOUTH);

    createRectangleBorder(new PointD(origin.x - 50, origin.y - 20), new SizeD(210, 250),
            "Try 'Default'");
  }

  /**
   * Creates a sample graph with overlapping nodes.
   */
  private void createOverlappingNodeSample(PointD origin) {
    IGraph graph = graphControl.getGraph();

    //create overlapping nodes with labels
    INode back = graph.createNode(new RectD(origin.x, origin.y + 20,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE), graph.getNodeDefaults().getStyle());

    INode middle = graph.createNode(new RectD(origin.x + 20, origin.y + 35,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE), graph.getNodeDefaults().getStyle());

    INode front = graph.createNode(new RectD(origin.x + 40, origin.y + 50,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE), graph.getNodeDefaults().getStyle());

    graph.addLabel(back, "Back", InteriorLabelModel.CENTER);
    graph.addLabel(middle, "Middle", InteriorLabelModel.CENTER);
    graph.addLabel(front, "Front", InteriorLabelModel.CENTER);

    //create overlapping nodes with ports
    INode backWithPorts = graph.createNode(new RectD(origin.x + 120, origin.y + 20,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE), graph.getNodeDefaults().getStyle());

    INode middleWithPorts = graph.createNode(new RectD(origin.x + 140, origin.y + 35,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE), graph.getNodeDefaults().getStyle());

    INode frontWithPorts = graph.createNode(new RectD(origin.x + 160, origin.y + 50,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE), graph.getNodeDefaults().getStyle());

    //now add the ports to the corresponding nodes
    INode[] nodeList = {backWithPorts, middleWithPorts, frontWithPorts};
    for (INode node : nodeList) {
      graph.addPort(node, FreeNodePortLocationModel.NODE_TOP_ANCHORED);
      graph.addPort(node, FreeNodePortLocationModel.NODE_RIGHT_ANCHORED);
      graph.addPort(node, FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED);
      graph.addPort(node, FreeNodePortLocationModel.NODE_LEFT_ANCHORED);
    }

    //create edges
    IEdge edge1 = graph.createEdge(back, backWithPorts);
    graph.addBend(edge1, new PointD(origin.x + 25, origin.y + 185));
    graph.addBend(edge1, new PointD(origin.x + 145, origin.y + 185));
    graph.addLabel(edge1, "Edge Label", new SmartEdgeLabelModel().createParameterFromSource(1));
    graph.setRelativePortLocation(edge1.getSourcePort(), new PointD(0, 25));

    IEdge edge2 = graph.createEdge(front, frontWithPorts);
    graph.addBend(edge2, new PointD(origin.x + 65, origin.y + 190));
    graph.addBend(edge2, new PointD(origin.x + 185, origin.y + 190));
    graph.setRelativePortLocation(edge2.getSourcePort(), new PointD(0, 25));

    //add the border rectangle
    createRectangleBorder(new PointD(origin.x - 50, origin.y - 20),
            new SizeD(310, 250), "Try 'Labels/Ports At Owner'");
  }

  /**
   * Creates a sample graph with overlapping edges.
   */
  private void createOverlappingEdgeSample(PointD origin) {
    IGraph graph = graphControl.getGraph();

    //add nodes
    INode sourceNode = graph.createNode(new RectD(origin.x, origin.y + 60,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE));
    INode targetNode1 = graph.createNode(new RectD(origin.x + 250, origin.y + 60,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE));
    INode targetNode2 = graph.createNode(new RectD(origin.x + 122.5, origin.y + 130,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE));
    INode groupNode = graph.createGroupNode(null, new RectD(origin.x + 85, origin.y, 125, 200));

    //add targetNode2 to the group groupNode
    graph.setParent(targetNode2, groupNode);

    //add edges
    IEdge edge1 = graph.createEdge(sourceNode, targetNode1);
    IEdge edge2 = graph.createEdge(sourceNode, targetNode2);
    graph.addBend(edge2, new PointD(origin.x + 25, origin.y + 155));

    //add corresponding labels
    graph.addLabel(groupNode, "Group Node");
    graph.addLabel(edge1, "Edge Label");

    //create the rectangle around the sample
    createRectangleBorder(new PointD(origin.x - 20, origin.y - 20),
            new SizeD(340, 250), "Try 'Edges on top' or 'Group Nodes'");
  }

  /**
   * Creates a sample graph with nested groups.
   */
  private void createNestedGroupSample(PointD origin) {
    IGraph graph = graphControl.getGraph();

    //add nodes with labels, set parent if needed and add edges
    INode root = graph.createGroupNode(null, new RectD(origin.x, origin.y, 230, 220));
    graph.addLabel(root, "Outer Group Node");

    INode outerChild1 = graph.createNode(new RectD(origin.x + 145, origin.y + 30,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE));
    graph.addLabel(outerChild1, "Outer\nChild");
    graph.setParent(outerChild1, root);

    INode outerChild2 = graph.createNode(new RectD(origin.x + 40, origin.y + 140,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE));
    graph.addLabel(outerChild2, "Outer\nChild");
    graph.setParent(outerChild2, root);

    IEdge edge1 = graph.createEdge(outerChild1, outerChild2);
    graph.addBend(edge1, new PointD(origin.x + 65, origin.y + 55));

    INode childGroup = graph.createGroupNode(root, new RectD(origin.x + 20, origin.y + 50, 150, 150));
    graph.addLabel(childGroup, "Inner Group Node");

    INode innerNode1 = graph.createNode(childGroup, new RectD(origin.x + 40, origin.y + 80,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE));
    graph.addLabel(innerNode1, "Inner\nChild");

    INode innerNode2 = graph.createNode(childGroup, new RectD(origin.x + 100, origin.y + 140,
            DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE));
    graph.addLabel(innerNode2, "Inner\nChild");

    IEdge edge2 = graph.createEdge(innerNode1, innerNode2);
    graph.addBend(edge2, new PointD(origin.x + 125, origin.y + 105));

    createRectangleBorder(new PointD(origin.x - 20, origin.y - 20), new SizeD(280, 250),
            "Try different settings");
  }

  /**
   * Creates a boundary rectangle for a sample graph in this demo.
   *
   * @param origin the position where to draw
   * @param size   the size of the rectangle
   * @param title  the content of the label above the rectangle
   */
  private void createRectangleBorder(PointD origin, SizeD size, String title) {
    //use a dummy node as boundary rectangle
    SimpleNode rect = new SimpleNode();

    //define the layout
    RectD rectLayout = new RectD(origin.x, origin.y, size.width, size.height);

    //define the style
    ShapeNodeStyle rectStyle = new ShapeNodeStyle();
    rectStyle.setShape(ShapeNodeShape.RECTANGLE);
    rectStyle.setPen(defaultRectanglePen);
    rectStyle.setPaint(Color.TRANSPARENT);

    rect.setStyle(rectStyle);
    rect.setLayout(rectLayout);

    //use a dummy label for the title of the boundary rectangle
    SimpleLabel rectLabel = new SimpleLabel(rect, title, ExteriorLabelModel.NORTH);

    DefaultLabelStyle rectLabelStyle = new DefaultLabelStyle();
    rectLabelStyle.setTextPaint(Color.GRAY);
    rectLabelStyle.setTextAlignment(TextAlignment.CENTER);
    rectLabelStyle.setFont(Font.font("Dialog", FontWeight.BOLD, 18));

    rectLabel.setStyle(rectLabelStyle);
    rectLabel.setPreferredSize(DEFAULT_PREF_RECTANGLE_SIZE);

    //add the boundary rectangle and its title label to the background
    ICanvasObjectGroup background = graphControl.getBackgroundGroup();
    background.addChild(rect, ICanvasObjectDescriptor.ALWAYS_DIRTY_LOOKUP);
    background.addChild(rectLabel, ICanvasObjectDescriptor.ALWAYS_DIRTY_LOOKUP);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
