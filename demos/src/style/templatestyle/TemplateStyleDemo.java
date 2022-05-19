/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package style.templatestyle;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.TemplateLabelStyle;
import com.yworks.yfiles.graph.styles.TemplateNodeStyle;
import com.yworks.yfiles.graph.styles.TemplatePortStyle;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortCandidateValidity;
import com.yworks.yfiles.view.input.ShowPortCandidates;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This demo shows how to use {@link com.yworks.yfiles.graph.styles.TemplateNodeStyle},
 * {@link com.yworks.yfiles.graph.styles.TemplateLabelStyle} and {@link  com.yworks.yfiles.graph.styles.TemplatePortStyle} to
 * create complex node and label visualizations using FXML components.
 */
public class TemplateStyleDemo extends DemoApplication {
  public WebView helpView;
  public GraphControl graphControl;

  /**
   * A reference to the static, shared node style instance that is used for customers.
   */
  private TemplateNodeStyle customerNodeStyle;
  /**
   * A reference to the static, shared node style instance that is used for products.
   */
  private TemplateNodeStyle productNodeStyle;
  /**
   * A reference to the static, shared node style instance that is used for the labels of the edges.
   */
  private TemplateLabelStyle labelStyle;
  /**
   * A reference to the static, shared port style instance that is used for the ports.
   */
  private TemplatePortStyle portStyle;

  /**
   * Initializes the controller. Sets up the help view, input modes and styles. Also disables state visualizations of
   * yFiles for JavaFX, so the styles will visualize these states themselves.
   *
   * <p>
   *   This is called when the FXMLLoader instantiates the scene graph. At the time this method is called, all nodes in
   *   the scene graph is available. Most importantly, the GraphControl instance is initialized.
   * </p>
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(helpView, this);

    // disable all default state visualizations - the style visualize these states themselves
    graphControl.getHighlightIndicatorManager().setEnabled(false);
    graphControl.getSelectionIndicatorManager().setEnabled(false);
    graphControl.getFocusIndicatorManager().setEnabled(false);

    graphControl.getGraph().getDecorator().getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
        CustomerToProductPortCandidateProvider::new);

    // initialize input mode for navigation
    initializeInputModes();
    // initialize default style instances.
    initializeStyles();
  }

  /**
   * Run the layout after the size of the control has been determined.
   */
  protected void onLoaded() {
    // load the initial graph from file
    loadGraph();

    // alternatively create the sample model in code
//    createModel();

    adjustNodeSizes();

    Platform.runLater(graphControl::fitGraphBounds);
  }

  /**
   * Load sample graph from graphML-file.
   */
  private void loadGraph() {
    try {

      // create an IOHandler that will be used for all IO operations
      // that way we can add io IO support for custom classes in this demo that store user data for nodes and labels
      // (Customer, Product, Relation)
      final GraphMLIOHandler ioh = new GraphMLIOHandler();

      // we set the IO handler on the GraphControl, so the GraphControl's IO methods
      // will pick up our handler for use during serialization and deserialization.
      graphControl.setGraphMLIOHandler(ioh);

      // add namespace mapping for saving/loading of custom classes in this demo in graphml
      ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/TemplateStyles/1.0",
          getClass().getPackage().getName(), getClass().getClassLoader());

      graphControl.importFromGraphML(getClass().getResource("resources/example.graphml"));
    } catch (IOException e) {
      e.printStackTrace(System.err);
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Resizes all nodes so that all information on the node is completely visible in the detail level.
   */
  private void adjustNodeSizes() {
    IGraph graph = graphControl.getGraph();

    // determine the max size for all nodes...
    IRenderContext renderContext = graphControl.createRenderContext();
    final IGraphSelection selection = graphControl.getSelection();
    for (INode node : graph.getNodes()) {
      if (node.getStyle() instanceof TemplateNodeStyle) {
        TemplateNodeStyle style = (TemplateNodeStyle) node.getStyle();
        final boolean oldSelected = selection.isSelected(node);
        selection.setSelected(node, true);

        IRectangle layout = node.getLayout();
        SizeD size = style.getPreferredSize(renderContext, node);
        double newWidth = Math.max(layout.getWidth(), size.getWidth());
        double newHeight = Math.max(layout.getHeight(), size.getHeight());
        graph.setNodeLayout(node, RectD.fromCenter(layout.getCenter(), new SizeD(newWidth, newHeight)));
        selection.setSelected(node, oldSelected);
      }
    }
  }

  /**
   * Initializes the input modes. {@link GraphEditorInputMode} is modified to create default nodes with
   * {@link TemplateNodeStyle}. Edges are only created from customers to products and get a default label with
   * {@link TemplateLabelStyle}.
   */
  protected void initializeInputModes() {
    final GraphEditorInputMode graphEditorInputMode = new GraphEditorInputMode();

    // create a dummy business object and associate it with a newly created node
    graphEditorInputMode.setNodeCreator((context, graph, location, parent) -> {
      final Customer c = new Customer("Sample Customer", "Your Computer",
          new Random((int) System.currentTimeMillis()).nextInt(99999));
      final INode customerNode = graph.createNode(location, customerNodeStyle);
      customerNode.setTag(c);
      return customerNode;
    });
    graphEditorInputMode.setLabelEditableItems(GraphItemTypes.EDGE.or(GraphItemTypes.EDGE_LABEL));

    // adds a label to the newly created edge and sets up the correct business object
    // Note that only edges from Customers to Products are allowed.
    graphEditorInputMode.getCreateEdgeInputMode().setEdgeCreator((ctx, graph, spCandidate, tpCandidate, templateEdge) -> {
      if (spCandidate.getOwner().getTag() instanceof Customer && tpCandidate.getOwner().getTag() instanceof Product) {
        IPort sourcePort = spCandidate.getPort() != null ? spCandidate.getPort() : spCandidate.createPort(ctx);
        IPort targetPort = tpCandidate.getPort() != null ? tpCandidate.getPort() : tpCandidate.createPort(ctx);
        IEdge edge = graph.createEdge(sourcePort, targetPort);
        Relation relation = new Relation((Customer) spCandidate.getOwner().getTag(), (Product) tpCandidate.getOwner().getTag());
        graph.addLabel(edge, relation.toString(), new EdgeSegmentLabelModel().createDefaultParameter(), labelStyle, new SizeD(100, 40), relation);
        return edge;
      } else {
        return null;
      }
    });
    graphEditorInputMode.getCreateEdgeInputMode().setShowPortCandidates(ShowPortCandidates.ALL);

    graphEditorInputMode.setShowHandleItems(GraphItemTypes.NONE);
    graphControl.setInputMode(graphEditorInputMode);
  }

  /**
   * Initializes the styles. Default styles for nodes, labels and ports are set using template styles.
   */
  protected void initializeStyles() {
    Class resolver = getClass();

    customerNodeStyle = new TemplateNodeStyle(resolver.getResource("Customer.fxml"));
    productNodeStyle = new TemplateNodeStyle(resolver.getResource("Product.fxml"));
    labelStyle = new TemplateLabelStyle(resolver.getResource("RelationLabel.fxml"));
    portStyle = new TemplatePortStyle(resolver.getResource("Port.fxml"));
    portStyle.setRenderSize(new SizeD(5, 5));

    final IGraph graph = graphControl.getGraph();

    // set the customer node style as the default and use an initial size for all nodes
    graph.getNodeDefaults().setStyle(customerNodeStyle);
    graph.getNodeDefaults().setSize(new SizeD(160, 80));

    // set edge style
    final PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(Pen.getGray());
    edgeStyle.setTargetArrow(IArrow.TRIANGLE);
    graph.getEdgeDefaults().setStyle(edgeStyle);
    // set the relation label style as the default
    graph.getEdgeDefaults().getLabelDefaults().setStyle(labelStyle);

    // set the port style as the default
    graph.getNodeDefaults().getPortDefaults().setStyle(portStyle);
  }

  /**
   * Creates a simple model - this method is not used in this demo - instead the graph is loaded from the included
   * GraphML file.
   */
  private void createModel() {
    // remember the mapping for each object to the created node
    final IMapper<Object, INode> objectMapping = new Mapper<>();

    // set the default node style to customer style
    final IGraph graph = graphControl.getGraph();
    graph.getNodeDefaults().setStyle(customerNodeStyle);

    // create some nodes - and associate them with a tag (customer).
    List<Customer> customers = Arrays.asList(
        new Customer("Lucy Osbourne", "Arizona", 13413),
        new Customer("Stephanie Cornwell", "Oregon", 13414),
        new Customer("Mark Wright", "Michigan", 13415),
        new Customer("Ruby Turner", "South Carolina", 13416),
        new Customer("Norman Johnson", "Montana", 13417)
    );

    // then create the nodes
    customers.forEach(customer -> {
      INode node = graph.createNode();
      node.setTag(customer);
      objectMapping.setValue(customer, node);
    });

    // set the default node style to product style
    graph.getNodeDefaults().setStyle(productNodeStyle);

    // create some nodes - and associate them with a tag (product)
    List<Product> products = Arrays.asList(
        new Product("Donut Maker", 8971, "Yes"),
        new Product("Snow Boots", 8972, "Yes"),
        new Product("Cowboy Hat", 8973, "No")
    );

    // then create the nodes
    products.forEach(product -> {
      INode node = graph.createNode();
      node.setTag(product);
      objectMapping.setValue(product, node);
    });

    List<Relation> relations = Arrays.asList(
        new Relation(customers.get(0), products.get(0)),
        new Relation(customers.get(0), products.get(1)),
        new Relation(customers.get(0), products.get(2)),
        new Relation(customers.get(1), products.get(0)),
        new Relation(customers.get(1), products.get(2)),
        new Relation(customers.get(2), products.get(1)),
        new Relation(customers.get(2), products.get(2)),
        new Relation(customers.get(3), products.get(1)),
        new Relation(customers.get(4), products.get(2))
    );

    // now add the edges using the stored mapping between products/customers and INodes
    relations.forEach(relation ->  {
      IEdge edge = graph.createEdge(
          objectMapping.getValue(relation.getCustomer()),
          objectMapping.getValue(relation.getProduct()));

      // and add a label
      graph.addLabel(edge, relation.toString(), new EdgeSegmentLabelModel().createDefaultParameter(), labelStyle,
          new SizeD(100, 40), relation);
    });

    // layout the model graph
    final OrganicLayout layout = new OrganicLayout();
    layout.setMinimumNodeDistance(130);
    LayoutExecutor executor = new LayoutExecutor(graphControl, layout);
    executor.setViewportAnimationEnabled(true);
    executor.setContentRectUpdatingEnabled(true);
    // set the duration to zero because we want no view port animation for the initial graph
    executor.setDuration(Duration.ZERO);
    executor.start();
  }

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Restricts creating connections from {@link Customer}s to {@link Product}s.
   */
  private static class CustomerToProductPortCandidateProvider implements IPortCandidateProvider {
    private final INode node;

    public CustomerToProductPortCandidateProvider(INode node) {
      this.node = node;
    }

    public Iterable<IPortCandidate> getSourcePortCandidates(IInputModeContext context, IPortCandidate target) {
      return getSourcePortCandidates(context);
    }

    public Iterable<IPortCandidate> getSourcePortCandidates(IInputModeContext context) {
      DefaultPortCandidate candidate = new DefaultPortCandidate(node);
      candidate.setValidity(node.getTag() instanceof Customer ? PortCandidateValidity.VALID : PortCandidateValidity.INVALID);
      return Collections.singleton(candidate);
    }

    public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context, IPortCandidate source) {
      return getTargetPortCandidates(context);
    }

    public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context) {
      DefaultPortCandidate candidate = new DefaultPortCandidate(node);
      candidate.setValidity(node.getTag() instanceof Product ? PortCandidateValidity.VALID : PortCandidateValidity.INVALID);
      return Collections.singleton(candidate);
    }
  }
}
