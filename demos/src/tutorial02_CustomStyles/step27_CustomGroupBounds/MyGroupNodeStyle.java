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
package tutorial02_CustomStyles.step27_CustomGroupBounds;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.graph.IGroupBoundsCalculator;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.utils.ImageSupport;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.ISize;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * An implementation of an {@link com.yworks.yfiles.graph.styles.INodeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractNodeStyle} as its base class.
 * <p>
 * This style is designed explicitly for group nodes. It paints the node as a file card with a tab in the upper left
 * corner. Narrow nodes have a narrow tab. If the node is wide enough, the tab also contains text.
 * </p><p>
 * This implementation uses a special {@link com.yworks.yfiles.graph.IGroupBoundsCalculator} implementation that takes
 * the node labels of the group node's child nodes into account.
 * </p>
 */
public class MyGroupNodeStyle extends AbstractNodeStyle {
  private static final Color DEFAULT_NODE_COLOR = Color.rgb(0, 130, 180, 200.0/255.0);
  // bounds of the tab shape
  private static final int TAB_HEIGHT = 16;
  private static final int TAB_WIDTH = 48;
  private static final int SMALL_TAB_WIDTH = 18;
  // radius of the outer path of the node
  private static final int OUTER_RADIUS = 5;
  // radius of the inner path of the node
  private static final int INNER_RADIUS = 3;
  // inset between the outer and inner path
  private static final int INSET = 2;

  private Color nodeColor;

  //////////////// New in this sample ////////////////
  // size of the collapse button and the corner where the button is
  private static final int BUTTON_SIZE = 14;
  private static final int CORNER_SIZE = 16;
  ////////////////////////////////////////////////////

  /**
   * Initializes a new <code>MyGroupNodeStyle</code> instance with a default node color.
   */
  public MyGroupNodeStyle() {
    nodeColor = DEFAULT_NODE_COLOR;
  }

  /**
   * Returns the fill color of the node.
   */
  public Color getNodeColor() {
    return nodeColor;
  }

  /**
   * Sets the fill color of the node.
   */
  public void setNodeColor(Color nodeColor) {
    this.nodeColor = nodeColor;
  }

  /**
   * Determines the color to use for filling the node.
   * This implementation uses {@link #getNodeColor()} unless the {@link com.yworks.yfiles.graph.ITagOwner#getTag()} of
   * the {@link com.yworks.yfiles.graph.INode} is of type {@link Color}, in which case that color overrides
   * this style's setting.
   * @param node The node to determine the color for.
   * @return The color for filling the node.
   */
  private Color getNodeColor(INode node) {
    return node.getTag() instanceof Color ? (Color)node.getTag() : getNodeColor();
  }

  /**
   * Creates the visual for a node.
   */
  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    TabVisual group = new TabVisual();
    group.update(getNodeColor(node), node.getLayout(), isCollapsed(node, (GraphControl) context.getCanvasControl()));

    // set the location
    group.setLayoutX(node.getLayout().getX());
    group.setLayoutY(node.getLayout().getY());

    return group;
  }

  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, INode)} is called instead.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, INode node) {
    TabVisual group = (TabVisual) oldVisual;
    group.update(getNodeColor(node), node.getLayout(), isCollapsed(node, (GraphControl) context.getCanvasControl()));

    // update the location
    group.setLayoutX(node.getLayout().getX());
    group.setLayoutY(node.getLayout().getY());

    return group;
  }

  /**
   * Checks whether or not a given group node is collapsed.
   */
  private static boolean isCollapsed(INode node, GraphControl graphControl) {
    if (graphControl != null) {
      IGraph foldedGraph = graphControl.getGraph();
      IFoldingView foldingView = foldedGraph.getFoldingView();
      if (foldingView != null) {
        // check if the node really is a group in the master graph
        boolean isGroupNode = foldingView.getManager().getMasterGraph().isGroupNode(foldingView.getMasterItem(node));
        // check if the node is collapsed in the view graph
        return isGroupNode && !foldedGraph.isGroupNode(node);
      }
    }
    return false;
  }

  /**
   * Determines if the given point lies in the file card shape that represents
   * the given node.
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    double x = node.getLayout().getX();
    double y = node.getLayout().getY();
    double w = node.getLayout().getWidth();
    double h = node.getLayout().getHeight();
    RectD rect = new RectD(x, y + TAB_HEIGHT, w, h - TAB_HEIGHT);
    // check main node rect
    if (rect.contains(location, context.getHitTestRadius())) {
      return true;
    }
    // check tab
    int tabWidth = useLargeTab(w) ? TAB_WIDTH : SMALL_TAB_WIDTH;
    return new RectD(x, y, tabWidth, TAB_HEIGHT).contains(location, context.getHitTestRadius());
  }

  /**
   * Returns the outline path to make connecting edges consider the tab shape.
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    GeneralPath path = createOuterPath(node.getLayout());
    path.transform(new Matrix2D(1, 0, 0, 1, node.getLayout().getX(), node.getLayout().getY()));
    return path;
  }

  /**
   * Provides lookup access to the following custom implementations:
   * <ul>
   *   <li> {@link com.yworks.yfiles.graph.IGroupBoundsCalculator} for taking the label bounds of group contents
   *   into account when calculating group node bounds.</li>
   *   <li> {@link com.yworks.yfiles.view.input.INodeInsetsProvider} for preventing group contents from overlapping with the
   *   file card tab.</li>
   *   <li> {@link com.yworks.yfiles.view.input.INodeSizeConstraintProvider} for adjusting the minimum size to ensure that the
   *   node is wide enough for the small tab and the button corner.
   *   </li>
   * </ul>
   */
  @Override
  protected Object lookup(INode node, Class type) {

    //////////////// New in this sample ////////////////
    if (type == IGroupBoundsCalculator.class) {
      // use a custom group bounds calculator that takes labels into account
      return new MyGroupBoundsCalculator();
    }
    ////////////////////////////////////////////////////

    if (type == INodeInsetsProvider.class) {
      // use a custom insets provider
      return new MyGroupInsetsProvider();
    }
    if (type == INodeSizeConstraintProvider.class) {
      // use a custom size constraint provider to constrain minimum size
      return new MySizeProvider();
    }
    return super.lookup(node, type);
  }

  /**
   * Creates the inner node path for the given node size.
   */
  private static GeneralPath createInnerPath(ISize size) {
    double w = size.getWidth();
    double h = size.getHeight();

    //////////////// New in this sample ////////////////
    // helper variables for curve coordinates
    double c = 0.551915024494;
    double sx = w - INSET - INNER_RADIUS;
    double sy = h - INSET - CORNER_SIZE;
    double b = BUTTON_SIZE * 0.5;
    double ex = w - INSET - CORNER_SIZE;
    double ey = h - INSET - b;
    double dc = c * (CORNER_SIZE - INNER_RADIUS);
    double c1x = sx-dc;
    double c1y = sy;
    double c2x = ex;
    double c2y = ey-dc;

    GeneralPath path = new GeneralPath();
    path.moveTo(INSET + INNER_RADIUS, INSET + TAB_HEIGHT);
    path.lineTo(sx, INSET + TAB_HEIGHT);
    path.quadTo(w - INSET, INSET + TAB_HEIGHT, w - INSET, INSET + TAB_HEIGHT + INNER_RADIUS);
    path.lineTo(w - INSET, sy - INNER_RADIUS);
    path.quadTo(w - INSET, sy, sx, sy);
    path.lineTo(w - INSET - b, h - INSET - CORNER_SIZE);
    path.cubicTo(c1x, c1y, c2x, c2y, ex, ey);
    path.lineTo(w - INSET - CORNER_SIZE, h - INSET - INNER_RADIUS);
    path.quadTo(ex, h - INSET, ex - INNER_RADIUS, h - INSET);
    path.lineTo(INSET + INNER_RADIUS, h - INSET);
    path.quadTo(INSET, h - INSET, INSET, ey);
    path.lineTo(INSET, INSET + TAB_HEIGHT + INNER_RADIUS);
    path.quadTo(INSET, INSET + TAB_HEIGHT, INSET + INNER_RADIUS, INSET + TAB_HEIGHT);
    ////////////////////////////////////////////////////

    path.close();
    return path;
  }

  /**
   * Creates the outer node path for the given node size.
   */
  private static GeneralPath createOuterPath(ISize size) {
    double w = size.getWidth();
    double h = size.getHeight();
    int tabWidth = useLargeTab(w) ? TAB_WIDTH : SMALL_TAB_WIDTH;
    GeneralPath path = new GeneralPath();
    path.moveTo(tabWidth + OUTER_RADIUS, TAB_HEIGHT);
    path.lineTo(w - OUTER_RADIUS, TAB_HEIGHT);
    path.quadTo(w, TAB_HEIGHT, w, TAB_HEIGHT + OUTER_RADIUS);
    path.lineTo(w, h - OUTER_RADIUS);
    path.quadTo(w, h, w - OUTER_RADIUS, h);
    path.lineTo(OUTER_RADIUS, h);
    path.quadTo(0, h, 0, h - OUTER_RADIUS);
    path.lineTo(0, OUTER_RADIUS);
    path.quadTo(0, 0, OUTER_RADIUS, 0);
    path.lineTo(-OUTER_RADIUS + tabWidth, 0);
    path.quadTo(tabWidth, 0, tabWidth, OUTER_RADIUS);
    path.lineTo(tabWidth, TAB_HEIGHT - OUTER_RADIUS);
    path.quadTo(tabWidth, TAB_HEIGHT, tabWidth + OUTER_RADIUS, TAB_HEIGHT);
    path.close();
    return path;
  }

  /**
   * Checks whether the node is wide enough to display the large tab.
   */
  private static boolean useLargeTab(double width) {
    return width >= TAB_WIDTH + 2 * OUTER_RADIUS;
  }

  /**
   * Customizes group insets to prevent group contents from overlapping with the
   * tab of the group's file card shape.
   */
  private static class MyGroupInsetsProvider implements INodeInsetsProvider {
    private static final int INSET = 6;

    public InsetsD getInsets(INode item) {

      //////////////// New in this sample ////////////////
      // use insets and respect the tab height
      return new InsetsD(TAB_HEIGHT + INSET, CORNER_SIZE - 1, CORNER_SIZE - 1, INSET);
      ////////////////////////////////////////////////////
    }
  }

  /**
   * Customizes the minimum node size to assure that the node is wide enough for the small tab and the button corner.
   */
  private static class MySizeProvider implements INodeSizeConstraintProvider {
    public SizeD getMinimumSize(INode item) {

      //////////////// New in this sample ////////////////
      // constrain minimum size to reasonable width and height
      return new SizeD(Math.max(SMALL_TAB_WIDTH + 2 * OUTER_RADIUS, CORNER_SIZE + OUTER_RADIUS),
          TAB_HEIGHT + OUTER_RADIUS + CORNER_SIZE);
      ////////////////////////////////////////////////////
    }

    public SizeD getMaximumSize(INode item) {
      // don't constrain maximum size
      return SizeD.INFINITE;
    }

    public RectD getMinimumEnclosedArea(INode item) {
      return RectD.EMPTY;
    }
  }

  //////////////// New in this sample ////////////////
  /**
   * An {@link com.yworks.yfiles.graph.IGroupBoundsCalculator} implementation that takes labels of child nodes into
   * account and also considers the group insets specified by the group's
   * {@link com.yworks.yfiles.view.input.INodeInsetsProvider}.
   */
  private static class MyGroupBoundsCalculator implements IGroupBoundsCalculator {
    public RectD calculateBounds( IGraph graph, INode groupNode ) {
      RectD bounds = RectD.EMPTY;
      Iterable<INode> children = graph.getChildren(groupNode);
      for (INode child : children) {
        // take the bounds of each child node into account
        bounds = RectD.add(bounds, child.getLayout().toRectD());
        // take the bounds of each node label of each child node into account
        bounds = RectD.add(bounds, child.getLabels().stream()
            .map(label -> label.getLayout().getBounds())
            .reduce(RectD::add).orElse(RectD.EMPTY));
      }

      // take the insets of the group node into account
      INodeInsetsProvider insetsProvider = groupNode.lookup(INodeInsetsProvider.class);
      if (insetsProvider != null) {
        InsetsD insets = insetsProvider.getInsets(groupNode);
        bounds = bounds.getEnlarged(insets);
      }

      return bounds;
    }
  }
  ////////////////////////////////////////////////////

  /**
   * A {@link VisualGroup} that paints a file card with a tab in the upper left corner.  Note that
   * we paint the file card at the origin and move the group to the current location of the node.
   */
  private static class TabVisual extends VisualGroup {
    private static final Matrix2D IDENTITY = new Matrix2D();

    // position, color and font of the text shown in the wide tab
    private static final float TEXT_POS_X = 4f;
    private static final float TEXT_POS_Y = 14f;
    private static final Color TEXT_COLOR = Color.web("#333333");
    private static final Font FONT = Font.font(14);

    // color of the file card
    private Color color;
    // size of the file card
    private SizeD size;
    // whether to paint the group node as collapsed or expanded
    private boolean collapsed;

    // inner and outer path of the file card
    private Path innerPath;
    private Path outerPath;

    // the text indicating whether it is a group or folder
    private Text text;

    private TabVisual() {
      outerPath = new Path();
      Pen.getTransparent().styleShape(outerPath);
      innerPath = new Path();
      Pen.getTransparent().styleShape(innerPath);
      text = new Text(TEXT_POS_X, TEXT_POS_Y, "");
      text.setFill(TEXT_COLOR);
      text.setFont(FONT);
      this.getChildren().addAll(outerPath, innerPath, text);
    }

    /**
     * Updates the color, size and location of the shapes painting the file card.
     * @param color  the color of the file card
     * @param layout the location and size of the file card
     * @param collapsed whether or not the group node is collapsed
     */
    void update(Color color, IRectangle layout, boolean collapsed) {
      // update the shape and fill only if color or size of the file card has been changed
      SizeD size = layout.toSizeD();
      if (!color.equals(this.color) || !size.equals(this.size) || collapsed != this.collapsed) {
        // update outer paths
        Paint outerFill = new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT,
            new Stop(0, mix(color, Color.WHITE, 0.5)),
            new Stop(1, color));
        createOuterPath(size).updatePath(outerPath, IDENTITY);
        outerPath.setFill(outerFill);

        //////////////// New in this sample ////////////////
        // update inner path only in expanded state
        if (!collapsed) {
          Paint innerFill = mix(color, Color.WHITE, 0.1);
          createInnerPath(size).updatePath(innerPath, IDENTITY);
          innerPath.setFill(innerFill);
          if (!this.getChildren().contains(innerPath)) {
            this.getChildren().add(innerPath);
          }
        } else if (this.getChildren().contains(innerPath)) {
          this.getChildren().remove(innerPath);
        }
        ////////////////////////////////////////////////////

        // update text
        if (useLargeTab(size.getWidth())) {
          text.setText(collapsed ? "Folder" : "Group");
        } else {
          text.setText("");
        }

        this.color = color;
        this.size = size;
        this.collapsed = collapsed;
      }
    }

    /**
     * Mixes two colors using the provided ratio.
     */
    public static final Color mix( Color color0, Color color1, double ratio ) {
      double iratio = 1 - ratio;
      double a = Math.min(1, Math.max(0, color0.getOpacity() * ratio + color1.getOpacity() * iratio));
      double r = Math.min(1, Math.max(0, color0.getRed() * ratio + color1.getRed() * iratio));
      double g = Math.min(1, Math.max(0, color0.getGreen() * ratio + color1.getGreen() * iratio));
      double b = Math.min(1, Math.max(0, color0.getBlue() * ratio + color1.getBlue() * iratio));
      return Color.color(r, g, b, a);
    }
  }
}
