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
package integration.swt;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.CornerStyle;
import com.yworks.yfiles.graph.styles.Corners;
import com.yworks.yfiles.graph.styles.GroupNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import toolkit.DemoStyles;
import toolkit.Themes;

/**
 * An enum for node templates to populate the palette with and which may be dragged into the GraphControl.
 */
enum NodeTemplate {
  RECTANGLE("Rectangle") {
    INode createNode() {
      ShapeNodeStyle style = DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.RECTANGLE, Themes.PALETTE_LIGHTBLUE);
      return createNode(style);
    }
  },
  ROUNDED_RECTANGLE("Rounded Rectangle") {
    INode createNode() {
      ShapeNodeStyle style = DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.ROUND_RECTANGLE,
          Themes.PALETTE_LIGHTBLUE);
      return createNode(style);
    }
  },
  STAR("Star") {
    INode createNode() {
      ShapeNodeStyle style = DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.STAR5, Themes.PALETTE_LIGHTBLUE);
      return createNode(style);
    }
  },
  CUT_CORNERS("Cut Corners") {
    INode createNode() {
      RectangleNodeStyle style = DemoStyles.createDemoNodeStyle(Themes.PALETTE_LIGHTBLUE);
      style.setCornerStyle(CornerStyle.CUT);
      style.setCorners(Corners.TOP);
      style.setCornerSize(10);
      return createNode(style);
    }
  },
  GROUP("Group") {
    INode createNode() {
      GroupNodeStyle style = DemoStyles.createDemoGroupStyle(Themes.PALETTE_LIGHTBLUE);
      INode node = getGraph().createGroupNode(null, new RectD(0, 0, 90, 70));
      getGraph().addLabel(node, "Group Node");
      return node;
    }
  };

  // name of the template
  private final String description;
  // node that applies the template
  private final INode node;

  // the graph in which the template nodes live
  private static IGraph graph;

  NodeTemplate(String description) {
    this.description = description;
    this.node = createNode();
  }

  /**
   * Returns the graph in which the template nodes live.
   */
  static IGraph getGraph() {
    if (graph == null) {
      graph = new DefaultGraph();
      DemoStyles.initDemoStyles(graph, Themes.PALETTE_LIGHTBLUE);
    }
    return graph;
  }

  /**
   * Returns a node that applies the template.
   */
  public INode node() {
    return node;
  }

  /**
   * Returns a description of the template.
   */
  public String description() {
    return description;
  }

  /**
   * Creates a node that applies the template.
   */
  abstract INode createNode();

  /**
   * Creates a node with the given style and a size of 30x30.
   */
  INode createNode(INodeStyle style) {
    return createNode(style, new RectD(0, 0, 30, 30));
  }

  /**
   * Creates a node with the given style and the given bounds.
   */
  INode createNode(INodeStyle style, RectD bounds) {
    return getGraph().createNode(bounds, style);
  }

  /**
   * Creates an image showing a node applying the template.
   */
  public Image createImage() {
    // determine the dimension of the image

    // holds all visuals needed to paint a certain node
    VisualGroup container = new VisualGroup();

    // create a visual that paints the node according to the node's style
    INodeStyle nodeStyle = node.getStyle();
    IVisualCreator nodeVisualCreator = nodeStyle.getRenderer().getVisualCreator(node, nodeStyle);
    IRenderContext renderContext = new GraphControl().createRenderContext();
    container.add(nodeVisualCreator.createVisual(renderContext));

    // create a visual for each node label from the label's style
    // we assume that all labels are within the node's bounds
    node.getLabels().stream()
        .map(label -> label.getStyle().getRenderer().getVisualCreator(label, label.getStyle()).createVisual(renderContext))
        .forEach(container::add);

    // render visual in an image
    SnapshotParameters params = new SnapshotParameters();
    IRectangle bounds = node.getLayout();
    params.setViewport(
        new Rectangle2D(bounds.getX() - 3, bounds.getY() - 3, bounds.getWidth() + 6, bounds.getHeight() + 6));
    params.setFill(Color.TRANSPARENT);
    return container.snapshot(params, null);
  }
}
