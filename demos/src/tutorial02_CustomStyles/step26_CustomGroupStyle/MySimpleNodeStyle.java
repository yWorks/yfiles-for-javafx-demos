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
package tutorial02_CustomStyles.step26_CustomGroupStyle;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleEdge;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.SimplePort;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyleRenderer;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.INodeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractNodeStyle} as its base class.
 * This style visualizes nodes as balls with a semi-transparent gradient and
 * several reflections to achieve a shiny look.
 */
public class MySimpleNodeStyle extends AbstractNodeStyle {
  private static final Color DEFAULT_COLOR = Color.rgb(0, 130, 180, 0.8);

  private Color nodeColor;

  /**
   * Initializes a new <code>MySimpleNodeStyle</code> instance with a default node color.
   */
  public MySimpleNodeStyle() {
    nodeColor = DEFAULT_COLOR;
  }

  /**
   * Gets the fill color of the node.
   */
  public Color getNodeColor() {
    return nodeColor;
  }

  /**
   * Determines the color to use for filling the node. This implementation uses the {@link
   * MySimpleNodeStyle#getNodeColor()} unless the {@link com.yworks.yfiles.graph.INode#getTag()}}
   * is of type {@link java.awt.Color}, in which case that color overrides this style's setting.
   * @param node the node to determine the color for
   * @return the color for filling the node
   */
  public Color getNodeColor(INode node) {
    return node.getTag() instanceof Color ? (Color) node.getTag() : getNodeColor();
  }

  /**
   * Sets the fill color of the node.
   */
  public void setNodeColor(Color nodeColor) {
    this.nodeColor = nodeColor;
  }

  /**
   * Creates the visual for a node.
   */
  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    // create a group that holds all visuals needed to paint the node
    VisualGroup group = new VisualGroup();

    // create the visual that paints the drop shadow
    DropShadowVisual shadow = new DropShadowVisual();
    shadow.update(node.getLayout().toSizeD());
    group.add(shadow);

    // create the visual that paints the ball
    BallVisual ball = new BallVisual();
    ball.update(getNodeColor(node), node.getLayout());
    group.add(ball);

    // create visuals that paint edge-like connectors from the node to its labels
    LabelEdgesGroup labelEdges = new LabelEdgesGroup();
    List<PointD> labelLocations = getLabelLocations(node);
    labelEdges.update(context, node, labelLocations);
    group.add(labelEdges);

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
    if (!(oldVisual instanceof VisualGroup)) {
      return createVisual(context, node);
    }

    VisualGroup group = (VisualGroup) oldVisual;

    // update the visual that paints the drop shadow
    DropShadowVisual shadow = (DropShadowVisual) group.getChildren().get(0);
    shadow.update(node.getLayout().toSizeD());

    // update the visual that paints the ball
    BallVisual ball = (BallVisual) group.getChildren().get(1);
    ball.update(getNodeColor(node), node.getLayout());

    // update the visuals that paint edge-like connectors from the node to its labels
    LabelEdgesGroup labelEdges = (LabelEdgesGroup) group.getChildren().get(2);
    List<PointD> labelLocations = getLabelLocations(node);
    labelEdges.update(context, node, labelLocations);

    // update the location
    group.setLayoutX(node.getLayout().getX());
    group.setLayoutY(node.getLayout().getY());

    return group;
  }

  /**
   * Returns the center points of labels to draw edge-like connectors for, relative the node's top left corner.
   */
  private List<PointD> getLabelLocations(INode node) {
    List<PointD> labelLocations = new ArrayList<>();
    for (ILabel label : node.getLabels()) {
      PointD labelCenter = label.getLayout().getCenter();
      PointD nodeTopLeft = node.getLayout().getTopLeft();
      labelLocations.add(PointD.subtract(labelCenter, nodeTopLeft));
    }
    return labelLocations;
  }

  /**
   * Checks if the given point lies inside the given node.
   * This methods uses exact geometric calculations for this check.
   * This is e.g. important for intersection calculation.
   */
  @Override
  protected boolean isInside(INode node, PointD point) {
    return GeomUtilities.ellipseContains(node.getLayout().toRectD(), point, 0);
  }

  /**
   * Gets the outline of the node, an ellipse in this case.
   * This allows for e.g. correct edge path intersection calculation.
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    RectD rect = node.getLayout().toRectD();
    GeneralPath outline = new GeneralPath();
    outline.appendEllipse(rect, false);
    return outline;
  }

  /**
   * Performs hit testing. This implementation takes the given context's
   * <code>HitTestRadius</code> into account.
   * @return <code>true</code> if the given location is inside node
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    return GeomUtilities.ellipseContains(node.getLayout().toRectD(), location, context.getHitTestRadius());
  }

  /**
   * Checks if a node is inside a certain box. Considers HitTestRadius.
   * @return <code>true</code> if the box intersects the elliptical shape of the node. Also <code>true</code> if box
   *         lies completely inside node.
   */
  @Override
  protected boolean isInBox(IInputModeContext context, RectD box, INode node) {
    if (!super.isInBox(context, box, node)) {
      // not even the bounds of the node are in the box
      return false;
    }

    double eps = context.getHitTestRadius();

    GeneralPath outline = getOutline(node);
    if (outline == null) return false;

    if (outline.intersects(box, eps)) {
      // the node and the box intersect
      return true;
    }
    if (outline.pathContains(box.getTopLeft(), eps) && outline.pathContains(box.getBottomRight(), eps)) {
      // the box is completely within the node
      return true;
    }
    // the node's bounds are completely within the box
    IRectangle nodeBounds = node.getLayout();
    return box.contains(nodeBounds.getTopLeft())
        && box.contains(nodeBounds.getBottomRight());
  }

  /**
   * Gets the bounding box of the node.
   * This is used for bounding box calculations and includes the visual shadow.
   */
  @Override
  protected RectD getBounds(ICanvasContext context, INode node) {
    IRectangle bounds = node.getLayout();
    // expand bounds to include drop shadow: position offset + blur size
    int extension = DropShadowVisual.POSITION_OFFSET + DropShadowVisual.BLUR_SIZE;
    return new RectD(bounds.getX(), bounds.getY(), bounds.getWidth() + extension, bounds.getHeight() + extension);
  }

  /**
   * Overridden to take the edge-like connectors to the labels into account.
   * Otherwise the edge-like connectors might not be painted if the node is outside of the clipping bounds.
   */
  @Override
  protected boolean isVisible(ICanvasContext context, RectD clip, INode node) {
    if (super.isVisible(context, clip, node)) {
      return true;
    }

    // check for labels connection lines
    RectD enlargedClip = clip.getEnlarged(10);
    PointD nodeCenter = node.getLayout().getCenter();
    return node.getLabels().stream().anyMatch(label -> enlargedClip.intersectsLine(nodeCenter, label.getLayout().getCenter()));
  }

  /**
   * A {@link VisualGroup} that paints the drop shadow of a ball.
   * We use one pre-rendered image of a semi-transparent ellipse as shadow for all balls.
   * The image is blurred with a gaussian convolution to get a more realistic drop shadow effect.
   * When painting we move and scale the image so that it fits to the location and size of
   * its shadow-casting ball.
   */
  private static class DropShadowVisual extends VisualGroup {
    // the pre-rendered image of a semi-transparent ellipse
    private static final WritableImage IMAGE;
    // size of the pre-rendered image
    private static final int IMAGE_SIZE = 32;
    // size of the semi-transparent ellipse
    private static final int ELLIPSE_SIZE = 20;
    // distance between the border of the image and the ellipse,
    // used to determine the translation of the image to match the location of its shadow-casting ball
    private static final int BORDER_SIZE = (IMAGE_SIZE - ELLIPSE_SIZE) / 2;
    // the light source that creates the shadow is on the top left,
    // thus the shadow is moved to the right and downward regarding its shadow-casting ball
    private static final int POSITION_OFFSET = 2;
    // ratio between image and ellipse size,
    // used to determine the scaling of the image to match the size of its shadow-casting ball
    private static final double SCALE_FACTOR = IMAGE_SIZE / (double) ELLIPSE_SIZE;
    // semi-transparent color of the shadow
    private static final Color SHADOW_COLOR = Color.rgb(0, 0, 0, 0.5);
    // parameters of the gaussian convolution used to blur the image
    private static final int BLUR_SIZE = 4;

    static {
      // create an image used as drop shadow of a ball
      IMAGE = new WritableImage(IMAGE_SIZE, IMAGE_SIZE);

      final int centerX = IMAGE_SIZE / 2;
      final int centerY = IMAGE_SIZE / 2;
      final int radius = ELLIPSE_SIZE / 2;

      // create an ellipse with blur effect as drop shadow of the nodes
      Ellipse ellipse = new Ellipse(centerX, centerY, radius, radius);
      ellipse.setFill(SHADOW_COLOR);
      ellipse.setEffect(new GaussianBlur(BLUR_SIZE));

      // take an image of the drop shadow ellipse
      SnapshotParameters snapshotParameters = new SnapshotParameters();
      snapshotParameters.setFill(Color.TRANSPARENT);
      snapshotParameters.setViewport(new Rectangle2D(0, 0, IMAGE_SIZE, IMAGE_SIZE));
      ellipse.snapshot(snapshotParameters, IMAGE);
    }

    private final ImageView imageView;

    DropShadowVisual() {
      imageView = new ImageView(IMAGE);
      this.add(imageView);
    }

    /**
     * Updates the size of the drop shadow.
     * @param size the size of the shadow
     */
    void update(SizeD size) {
      double imageWidth = size.getWidth() * SCALE_FACTOR;
      double imageHeight = size.getHeight() * SCALE_FACTOR;
      double imageX = POSITION_OFFSET - imageWidth * BORDER_SIZE / IMAGE_SIZE;
      double imageY = POSITION_OFFSET - imageHeight * BORDER_SIZE / IMAGE_SIZE;

      imageView.setX(imageX);
      imageView.setY(imageY);
      imageView.setFitWidth(imageWidth);
      imageView.setFitHeight(imageHeight);
    }
  }

  /**
   * A {@link VisualGroup} that paints an ellipse with a semi-transparent gradient.
   * Three reflections of two ellipses and a closed curve make the ball look shiny.
   */
  private static class BallVisual extends VisualGroup {
    // color and size of the ball
    private Color color;
    private SizeD size;

    // shapes for the ball and its reflections
    private Ellipse shape;
    private Ellipse reflection1;
    private Ellipse reflection2;
    private Path reflection3;

    // the semi-transparent gradient to fill the ball with
    private Paint fillPaint;

    BallVisual() {
      shape = new Ellipse();
      reflection1 = new Ellipse();
      reflection2 = new Ellipse();
      reflection3 = new Path();

      // and add all to the container for the node
      this.getChildren().addAll(shape, reflection1, reflection2, reflection3);
    }

    /**
     * Updates the color and size of the shape and the reflections of the ball.
     * Note that we paint the ball at the origin and move the container to
     * the appropriate location.
     * @param color  the color of the ball
     * @param layout the location and size of the ball.
     */
    public void update(Color color, IRectangle layout) {
      // update the shape and gradient only if color or size of the ball has been changed
      SizeD size = layout.toSizeD();
      if (!color.equals(this.color) || !size.equals(this.size)) {
        this.color = color;
        this.size = size;

        double width = layout.getWidth();
        double height = layout.getHeight();

        // set the size of the ball's shape
        shape.setCenterX(width * 0.5);
        shape.setCenterY(height * 0.5);
        shape.setRadiusX(width * 0.5);
        shape.setRadiusY(height * 0.5);

        // max and min needed for reflection effect calculation
        double max = Math.max(width, height);
        double min = Math.min(width, height);

        // create background gradient from specified background color
        Stop[] gradientStops = new Stop[]{
            new Stop(0, Color.color(
                Math.min(1.0, 1.4 * color.getRed()),
                Math.min(1.0, 1.4 * color.getGreen()),
                Math.min(1.0, 1.4 * color.getBlue()),
                Math.max(0, color.getOpacity() - 0.2))),
            new Stop(0.5, color),
            new Stop(1, Color.color(
                Math.min(1.0, 1.7 * color.getRed()),
                Math.min(1.0, 1.7 * color.getGreen()),
                Math.min(1.0, 1.7 * color.getBlue()),
                Math.max(0, color.getOpacity() - 0.2)))};
        fillPaint = new LinearGradient(0, 0, 0.5 / (width / max), 1 / (height / max), true, CycleMethod.NO_CYCLE, gradientStops);
        shape.setFill(fillPaint);

        // create light reflection effects
        double radius1 = min / 20;
        reflection1.setCenterX(width / 5 + radius1);
        reflection1.setCenterY(height / 5 + radius1);
        reflection1.setRadiusX(radius1);
        reflection1.setRadiusY(radius1);
        reflection1.setFill(Color.WHITE);

        double radius2 = min / 14;
        reflection2.setCenterX(width / 4.9 + radius2);
        reflection2.setCenterY(height / 4.9 + radius2);
        reflection2.setRadiusX(radius2);
        reflection2.setRadiusY(radius2);
        reflection2.setFill(Color.ALICEBLUE);

        Point2D startPoint = new Point2D(width / 2.5, height / 10 * 9);
        Point2D endPoint = new Point2D(width / 10 * 9, height / 2.5);
        Point2D ctrlPoint1 = new Point2D(startPoint.getX() + (endPoint.getX() - startPoint.getX()) / 2, height);
        Point2D ctrlPoint2 = new Point2D(width, startPoint.getY() + (endPoint.getY() - startPoint.getY()) / 2);
        Point2D ctrlPoint3 = new Point2D(ctrlPoint1.getX(), ctrlPoint1.getY() - height / 10);
        Point2D ctrlPoint4 = new Point2D(ctrlPoint2.getX() - width / 10, ctrlPoint2.getY());

        reflection3.getElements().clear();
        reflection3.getElements().addAll(
            new MoveTo(startPoint.getX(), startPoint.getY()),
            new CubicCurveTo(ctrlPoint1.getX(), ctrlPoint1.getY(), ctrlPoint2.getX(), ctrlPoint2.getY(), endPoint.getX(), endPoint.getY()),
            new CubicCurveTo(ctrlPoint4.getX(), ctrlPoint4.getY(), ctrlPoint3.getX(), ctrlPoint3.getY(), startPoint.getX(), startPoint.getY()),
            new ClosePath());
        reflection3.setFill(Color.ALICEBLUE);
        reflection3.setStroke(null);
      }
    }
  }

  /**
   * A {@link com.yworks.yfiles.view.VisualGroup} that holds all {@link LabelEdgeVisual} instances for one node.
   */
  private static class LabelEdgesGroup extends VisualGroup {
    // edge style used for all edge-like connectors
    private static final MySimpleEdgeStyle CONNECTOR_STYLE;

    // the locations of the node's labels
    private List<PointD> labelLocations;

    static {
      CONNECTOR_STYLE = new MySimpleEdgeStyle(new MySimpleArrow(), IArrow.NONE, 2);
    }

    LabelEdgesGroup() {
      this.labelLocations = new ArrayList<>();
    }

    /**
     * Updates the edge-like connectors from a node to its labels.
     */
    public void update(IRenderContext context, INode node, List<PointD> labelLocations) {
      if (this.labelLocations.equals(labelLocations)) {
        // nothing to update since the labels has not been changed
        return;
      }

      this.labelLocations = labelLocations;
      if (node.getLabels().size() > 0) {
        // create a SimpleEdge which will be used as a dummy for rendering
        SimpleEdge simpleEdge = new SimpleEdge(null, null);
        // assign the style
        simpleEdge.setStyle(CONNECTOR_STYLE);

        // create a SimpleNode which provides the source port for the edge but won't be drawn itself
        SimpleNode sourceDummyNode = new SimpleNode();
        sourceDummyNode.setLayout(new RectD(0, 0, node.getLayout().getWidth(), node.getLayout().getHeight()));
        sourceDummyNode.setStyle(node.getStyle());

        // create a SimpleNode which provides the target port for the edge but won't be drawn itself
        SimpleNode targetDummyNode = new SimpleNode();

        // set source port to the port of the node using a dummy node that is located at the origin.
        simpleEdge.setSourcePort(new SimplePort(sourceDummyNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
        // create port on target dummy node for the label target
        simpleEdge.setTargetPort(new SimplePort(targetDummyNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));

        // render one edge for each label
        int index = 0;
        for (PointD labelLocation : this.labelLocations) {
          LabelEdgeVisual visual = getLabelEdgeVisual(index);
          // let the visual update itself with the new parameters
          visual.update(context, labelLocation, simpleEdge, targetDummyNode);
          index++;
        }

        // if the number of labels has been decreased we remove spare visuals
        if (index < getChildren().size()) {
          removeSpareVisuals(index);
        }
      } else {
        getChildren().clear();
      }
    }

    /**
     * Returns the visual to render the connection to the label at in the given
     * index or creates a new visual if there is none for the given index.
     */
    private LabelEdgeVisual getLabelEdgeVisual(int index) {
      if (getChildren().size() > index && getChildren().get(index) != null) {
        return (LabelEdgeVisual) getChildren().get(index);
      } else {
        LabelEdgeVisual labelEdgeVisual = new LabelEdgeVisual();
        getChildren().add(labelEdgeVisual);
        return labelEdgeVisual;
      }

    }

    /**
     * Removes visuals that are spare from the children list.
     * @param index the index of the first spare child.
     */
    private void removeSpareVisuals(int index) {
      getChildren().subList(index, getChildren().size() - 1).clear();
    }
  }

  /**
   * A {@link VisualGroup} that paints edge-like connectors from a node to one of its labels.
   */
  private static class LabelEdgeVisual extends VisualGroup {
    // the current location of the label
    private PointD labelLocation;

    LabelEdgeVisual() {
      this.labelLocation = PointD.ORIGIN;
    }

    /**
     * Updates the visual that paints the edge-like connector from a node to one of its labels.
     */
    public void update(IRenderContext context, PointD labelLocation, SimpleEdge simpleEdge, SimpleNode targetDummyNode) {
      if (!labelLocation.equals(this.labelLocation)) {
        this.labelLocation = labelLocation;

        // move the dummy node to the location of the label
        targetDummyNode.setLayout(new RectD(labelLocation.getX(), labelLocation.getY(), 0, 0));

        // now create a new visual or update an existing one that paints an edge-like connector using
        // the style interface
        IEdgeStyleRenderer renderer = simpleEdge.getStyle().getRenderer();
        IVisualCreator creator = renderer.getVisualCreator(simpleEdge, simpleEdge.getStyle());
        if (this.getChildren().isEmpty()) {
          this.getChildren().add(creator.createVisual(context));
        } else {
          creator.updateVisual(context, this.getChildren().get(0));
        }
      }
    }
  }
}
