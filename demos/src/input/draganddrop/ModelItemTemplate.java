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
package input.draganddrop;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.PixelImageExporter;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * An enum for {@link IModelItem} templates to populate the palette with and
 * which may be dragged into the GraphControl.
 */
public enum ModelItemTemplate {
  GROUP_NODE_VALID_PARENT(DragAndDropDemo.createGroupNode(getGraph(), 0, 0, true)),
  GROUP_NODE_INVALID_PARENT(DragAndDropDemo.createGroupNode(getGraph(), 0, 0, false)),
  NORMAL_NODE_VALID_PARENT(DragAndDropDemo.createNode(getGraph(), 0, 0, true)),
  NORMAL_NODE_INVALID_PARENT(DragAndDropDemo.createNode(getGraph(), 0, 0, false)),
  NODE_LABEL(DragAndDropDemo.createLabel(getGraph(), "Node Label", InteriorLabelModel.CENTER)),
  EDGE_LABEL(DragAndDropDemo.createLabel(getGraph(), "Edge Label", FreeNodeLabelModel.INSTANCE.createDefaultParameter())),
  PORT(DragAndDropDemo.createPort(getGraph(), FreeNodePortLocationModel.NODE_CENTER_ANCHORED));

  private static final int MAX_SIZE = 70;
  // the graph in which the template nodes live
  private static IGraph graph;

  // node that applies the template
  private final IModelItem item;

  // an image showing a node applying the template
  private Node image;

  /**
   * Returns the graph in which the template nodes live.
   */
  static IGraph getGraph() {
    if (graph == null) {
      graph = new DefaultGraph();
    }
    return graph;
  }

  ModelItemTemplate( IModelItem item ) {
    this.item = item;
  }

  /**
   * Returns a node that applies the template.
   */
  public IModelItem getItem() {
    return item;
  }

  /**
   * Returns an image showing a node applying the template.
   */
  public Node getImage() {
    if (image == null) {
      image = createImage();
    }
    return image;
  }

  /**
   * Creates an image showing a node applying the template.
   */
  private Node createImage() {
    INode node = getNode();

    // create a GraphControl instance and add a copy of the given node with its labels
    GraphControl graphControl = new GraphControl();
    IGraph graph = graphControl.getGraph();
    RectD newLayout = new RectD(0, 0, Math.min(MAX_SIZE, node.getLayout().getWidth()), Math.min(MAX_SIZE, node.getLayout().getHeight()));
    INode newNode = graph.createNode(newLayout, node.getStyle(), node.getTag());
    node.getLabels().forEach(label ->
        graph.addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle(), label.getPreferredSize(), label.getTag()));
    node.getPorts().forEach(port ->
        graph.addPort(newNode, port.getLocationParameter(), port.getStyle()));

    // render the graph component in an image
    graphControl.updateContentRect();
    PixelImageExporter pixelImageExporter = new PixelImageExporter(graphControl.getContentRect().getEnlarged(2));
    pixelImageExporter.setBackgroundFill(Color.TRANSPARENT);
    WritableImage image = pixelImageExporter.exportToBitmap(graphControl);

    // put the image into a pane to center it
    StackPane pane = new StackPane();
    pane.getChildren().add(new ImageView(image));
    return pane;
  }

  /**
   * Applies the attributes of the template to the given node.
   * @param graph The graph where the given node live.
   * @param node The node to apply the template.
   * @param location The location of the given node.
   */
  public void apply( IGraph graph, INode node, PointD location ) {
    INode templateNode = getNode();
    graph.setStyle(node, templateNode.getStyle());
    graph.setNodeLayout(node, RectD.fromCenter(location, templateNode.getLayout().toSizeD()));
    graph.setIsGroupNode(node, templateNode.getStyle() instanceof PanelNodeStyle);
    node.setTag(templateNode.getTag());
    templateNode.getLabels().forEach(
        label -> graph.addLabel(node, label.getText(), label.getLayoutParameter(), label.getStyle()));
  }

  private INode getNode() {
    IModelItem item = getItem();
    if (item instanceof ILabel) {
      return (INode) ((ILabel) item).getOwner();
    } else if (item instanceof IPort) {
      return (INode) ((IPort) item).getOwner();
    } else {
      return (INode) item;
    }
  }
}
