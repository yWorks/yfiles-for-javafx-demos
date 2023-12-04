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
package builder.graphbuilder;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.builder.EdgeCreator;
import com.yworks.yfiles.graph.builder.GraphBuilder;
import com.yworks.yfiles.graph.builder.GraphBuilderItemEventArgs;
import com.yworks.yfiles.graph.builder.LabelCreator;
import com.yworks.yfiles.graph.builder.NodeCreator;
import com.yworks.yfiles.graph.builder.NodesSource;
import com.yworks.yfiles.graph.builder.TreeBuilder;
import com.yworks.yfiles.graph.builder.TreeNodesSource;
import com.yworks.yfiles.graph.labelmodels.EdgePathLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.GroupNodeStyle;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.hierarchic.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.WebViewUtils;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Shows how to use the {@link GraphBuilder} for quickly populating a graph from
 * a data source. The GraphBuilder constructs the example graphs from XML data.
 */
public class GraphBuilderDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView helpView;
  public ComboBox<String> exampleComboBox;

  /**
   * Initializes the controller.
   */
  public void initialize() {
    exampleComboBox.getSelectionModel().selectFirst();
    exampleComboBox.getSelectionModel().selectedIndexProperty().addListener(
        (observable, oldValue, newValue) -> {
          switch (newValue.intValue()) {
            case 0:
              createOrganizationGraph();
              break;
            case 1:
              createClassesGraph();
              break;
          }
        });

    // set up the help text on the left side.
    WebViewUtils.initHelp(helpView, this);

    // initialize input mode
    graphControl.setInputMode(createInputMode());

    initializeGraphDefaults();
  }

  /**
   * Creates an initial example diagram when the demo application becomes
   * visible for the first time.
   */
  @Override
  protected void onLoaded() {
    createOrganizationGraph();
  }

  /**
   * Creates a viewer input mode.
   */
  private static IInputMode createInputMode() {
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setSelectableItems(GraphItemTypes.NONE);
    gvim.setFocusableItems(GraphItemTypes.NONE);
    return gvim;
  }

  /**
   * Sets shared default styles for the graph.
   */
  void initializeGraphDefaults() {
    IGraph graph = graphControl.getGraph();

    // initialize demo styles
    DemoStyles.initDemoStyles(graph);
    // set insets and bigger text size for demo group node label styles
    DefaultLabelStyle groupLabelStyle = (DefaultLabelStyle) graph.getGroupNodeDefaults().getLabelDefaults().getStyle();
    groupLabelStyle.setInsets(new InsetsD(2));
    groupLabelStyle.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 24));
    // increase tab height of GroupNodeStyle so the increased group node labels fit into the header
    ((GroupNodeStyle) graph.getGroupNodeDefaults().getStyle()).setTabHeight(28);
  }


  /**
   * Creates a simple organizational chart.
   */
  void createOrganizationGraph() {
    Document document = parse("resources/organizationmodel.xml");
    if (document != null) {
      Element data = document.getDocumentElement();

      HashMap<String, Element> businessUnits = new HashMap<>();
      for (Element unit : XmlUtils.getDescendantsByTagName(data, "businessunit")) {
        businessUnits.put(unit.getAttribute("name"), unit);
      }

      graphControl.getGraph().clear();
      TreeBuilder builder = new TreeBuilder(graphControl.getGraph());
      // nodes, edges, and groups are obtained from XML elements in the model
      TreeNodesSource<Element> employeeSource = builder.createRootNodesSource(
          XmlUtils.getChildrenByTagName(data, "employee"));
      // as is the hierarchy of employees
      employeeSource.addChildNodesSource(element -> XmlUtils.getChildrenByTagName(element, "employee"), employeeSource);

      // mapping nodes to groups is done via an attribute on the employee
      employeeSource.setParentIdProvider(element -> businessUnits.get(element.getAttribute("businessUnit")));
      // choose the node size so that the labels fit
      NodeCreator<Element> employeeNodeCreator = employeeSource.getNodeCreator();
      employeeNodeCreator.setLayoutProvider(element -> {
        double width = 7 * Math.max(element.getAttribute("name").length(), element.getAttribute("position").length());
        return new RectD(0, 0, width, 40);
      });
      // take the name attribute as node name
      LabelCreator<Element> nodeNameLabels = employeeNodeCreator.createLabelBinding(
          element -> element.getAttribute("name"));
      InteriorStretchLabelModel nameLabelModel = new InteriorStretchLabelModel();
      nameLabelModel.setInsets(new InsetsD(5));
      nodeNameLabels.getDefaults().setLayoutParameter(
          nameLabelModel.createParameter(InteriorStretchLabelModel.Position.CENTER));
      DefaultLabelStyle nodeNameLabelStyle = DemoStyles.createDemoNodeLabelStyle();
      nodeNameLabelStyle.setInsets(InsetsD.EMPTY);
      nodeNameLabelStyle.setVerticalTextAlignment(VPos.TOP);
      nodeNameLabels.getDefaults().setStyle(nodeNameLabelStyle);

      LabelCreator<Element> nodePositionLabels = employeeNodeCreator.createLabelBinding(
          element -> element.getAttribute("position"));
      InteriorStretchLabelModel positionLabelModel = new InteriorStretchLabelModel();
      positionLabelModel.setInsets(new InsetsD(20, 5, 5, 5));
      nodePositionLabels.getDefaults().setLayoutParameter(
          positionLabelModel.createParameter(InteriorStretchLabelModel.Position.CENTER));
      DefaultLabelStyle nodePositionLabelStyle = DemoStyles.createDemoNodeLabelStyle();
      nodePositionLabelStyle.setInsets(InsetsD.EMPTY);
      nodePositionLabels.getDefaults().setStyle(nodePositionLabelStyle);

      // create the group nodes from the business units
      TreeNodesSource<Element> businessunitSource = builder.createRootGroupNodesSource(
          XmlUtils.getDescendantsByTagName(data, "businessunit"));
      // group nesting is determined by nesting of the XML elements
      businessunitSource.setParentIdProvider(element -> {
        Node node = element.getParentNode();
        return node != null && node.getNodeType() == Node.ELEMENT_NODE ? node : null;
      });
      LabelCreator<Element> groupLabelCreator = businessunitSource.getNodeCreator().createLabelBinding(
          element -> element.getAttribute("name"));
      ILabelDefaults groupLabelDefaults = groupLabelCreator.getDefaults();
      // configure default placement for group node labels
      // i.e. group node labels should be placed inside and close to the upper
      // left corner of their associated group node
      groupLabelDefaults.setLayoutParameter(InteriorLabelModel.NORTH_WEST);

      // Add descriptive labels to edges
      LabelCreator<Element> edgeLabels = employeeSource.getParentEdgeCreator().createLabelBinding(
          target -> target.getAttribute("position"));
      edgeLabels.setDefaults(graphControl.getGraph().getEdgeDefaults().getLabelDefaults());
      edgeLabels.getDefaults().setLayoutParameter(
          new EdgePathLabelModel(0, 0, 0, false, EdgeSides.ON_EDGE).createDefaultParameter());

      builder.buildGraph();

      HierarchicLayout algorithm = new HierarchicLayout();
      EdgeLayoutDescriptor eld = new EdgeLayoutDescriptor();
      eld.setMinimumLength(50);
      eld.setMinimumDistance(20);
      algorithm.setEdgeLayoutDescriptor(eld);
      algorithm.setIntegratedEdgeLabelingEnabled(true);
      graphControl.morphLayout(algorithm, Duration.ofMillis(1000));
    }
  }

  /**
   * Creates a simple class hierarchy diagram.
   */
  void createClassesGraph() {
    Document document = parse("resources/classesmodel.xml");

    if (document != null) {
      Element data = document.getDocumentElement();

      HashMap<String, Element> classes = new HashMap<>();
      for (Element _class : XmlUtils.getDescendantsByTagName(data, "class")) {
        classes.put(_class.getAttribute("name"), _class);
      }

      // nodes and edges are obtained from XML elements in the model
      Predicate<Element> hasChildren = element -> XmlUtils.getChildrenByTagName(element, "class").iterator().hasNext();
      Iterable<Element> childNodeData = XmlUtils.getDescendantsByTagName(data, "class",
          element -> !hasChildren.test(element));
      Iterable<Element> groupNodeData = XmlUtils.getDescendantsByTagName(data, "class", hasChildren);

      // nodes are grouped by their parent
      Function<Element, Object> parentProvider = element -> {
        Node node = element.getParentNode();
        return node != null && node.getNodeType() == Node.ELEMENT_NODE && "class".equals(
            node.getNodeName()) ? node : null;
      };

      // create GraphBuilder and configure node and edge sources
      GraphBuilder builder = new GraphBuilder(graphControl.getGraph());
      // add and configure NodeSource for group nodes
      NodesSource<Element> groupSource = builder.createGroupNodesSource(groupNodeData);
      groupSource.setParentIdProvider(parentProvider);

      // add and configure NodeSource for leaf nodes
      NodesSource<Element> nodesSource = builder.createNodesSource(childNodeData);
      nodesSource.setParentIdProvider(parentProvider);

      // add and configure (group) node labels
      // the node label is either ClassName, interface ClassName, or ClassName extends OtherClass
      Function<Element, String> nodeLabelProvider = element -> {
        String name = element.getAttribute("name");
        String superType = element.getAttribute("extends");
        boolean isInterface = "interface".equals(element.getAttribute("type"));
        return (isInterface ? "interface" : "") + name + (superType.isEmpty() ? "" : " extends " + superType);
      };
      LabelCreator<Element> nodeLabelCreator = nodesSource.getNodeCreator().createLabelBinding(nodeLabelProvider);

      LabelCreator<Element> groupNodeLabelCreator = groupSource.getNodeCreator().createLabelBinding(nodeLabelProvider);
      groupNodeLabelCreator.getDefaults().setLayoutParameter(InteriorLabelModel.NORTH_WEST);

      // ensure that nodes are large enough to hold their labels
      IEventHandler<GraphBuilderItemEventArgs<ILabel, Element>> adjustNodeSizeToLabel = (source, args) -> {
        ILabel l1 = args.getItem();
        INode node = (INode) l1.getOwner();
        // determine optimal node size
        IRectangle nl = node.getLayout();
        SizeD size1 = l1.getPreferredSize();
        SizeD bestSize = new SizeD(
            Math.max(nl.getWidth(), size1.getWidth() + 15),
            Math.max(nl.getHeight(), size1.getHeight() + 12));
        // Set node to that size. Location is irrelevant here, since we're running a layout anyway
        args.getGraph().setNodeLayout(node, new RectD(PointD.ORIGIN, bestSize));
      };
      nodeLabelCreator.addLabelAddedListener(adjustNodeSizeToLabel);
      groupNodeLabelCreator.addLabelAddedListener(adjustNodeSizeToLabel);

      // edges are drawn for classes with an "extends" attribute
      // between the class itself and the class which is provided by the "extends" attribute
      EdgeCreator<Element> edgeCreator = builder.createEdgesSource(XmlUtils.getDescendantsByTagName(data, "class"),
          element -> element,
          element -> classes.get(element.getAttribute("extends"))).getEdgeCreator();

      // edge label "Source extends Target"
      edgeCreator.createLabelBinding(element -> {
        Element superType = classes.get(element.getAttribute("extends"));
        return element.getAttribute("name") + " extends " + superType.getAttribute("name");
      });
      edgeCreator.getDefaults().setLabelDefaults(graphControl.getGraph().getEdgeDefaults().getLabelDefaults());

      graphControl.getGraph().clear();
      builder.buildGraph();

      HierarchicLayout algorithm = new HierarchicLayout();
      algorithm.setLayoutOrientation(LayoutOrientation.BOTTOM_TO_TOP);
      algorithm.setIntegratedEdgeLabelingEnabled(true);
      algorithm.setMinimumLayerDistance(30);
      algorithm.setNodeLabelConsiderationEnabled(false);
      graphControl.morphLayout(algorithm, Duration.ofMillis(1000));
    }
  }


  /**
   * Parses the content of the resource at the given relative resource path as
   * an XML document.
   */
  private Document parse(String path) {
    URL resource = getClass().getResource(path);
    if (resource == null) {
      return null;
    } else {
      return XmlUtils.parse(resource);
    }
  }


  public static void main(String[] args) {
    launch(args);
  }
}
