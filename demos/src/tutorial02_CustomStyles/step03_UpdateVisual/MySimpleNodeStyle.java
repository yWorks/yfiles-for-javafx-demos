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
package tutorial02_CustomStyles.step03_UpdateVisual;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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

  //////////////// New in this sample ////////////////
  private boolean reuseVisual;

  /**
   * Checks if the high-performance rendering is enabled. If set to false, the base implementation of udateVisual() is
   * used which simply calls createVisual(). This property exists only in this sample to show the performance
   * improvement when properly implementing UpdateVisual()
   */
  public boolean isReusingVisual() {
    return reuseVisual;
  }

  /**
   * Enables or disables the high-performance rendering. If set to false, the base implementation of udateVisual() is
   * used which simply calls createVisual(). This property exists only in this sample to show the performance
   * improvement when properly implementing UpdateVisual()
   */
  public void setReusingVisual(boolean value) {
    reuseVisual = value;
  }

  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, INode)} is called instead.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, INode node) {
    // if we have the high performance option enabled,
    // update the old object instead of creating a completely new one.
    if (isReusingVisual() && oldVisual instanceof VisualGroup) {
      // update the old visual that paints the node
      BallVisual ball = (BallVisual) oldVisual;
      IRectangle layout = node.getLayout();
      ball.update(getNodeColor(node), layout);

      // update the location
      ball.setLayoutX(layout.getX());
      ball.setLayoutY(layout.getY());

      return ball;
    } else {
      // either we have the high performance option disabled or the security check failed.
      // in this case, render the node from scratch (which is a costly operation)
      return createVisual(context, node);
    }
  }
  ////////////////////////////////////////////////////

  /**
   * Creates the visual for a node.
   */
  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    // create the visual that paints the node
    BallVisual ball = new BallVisual();
    IRectangle layout = node.getLayout();
    ball.update(getNodeColor(node), layout);

    // set the location
    ball.setLayoutX(layout.getX());
    ball.setLayoutY(layout.getY());

    return ball;
  }

  /**
   * A {@link VisualGroup} that paints an ellipse with a semi-transparent gradient.
   * Three reflections of two ellipses and a closed curve make the ball look shiny.
   * <p>
   * We store the shapes, the color, the position and the size of the ball as instance variables. The update method
   * checks whether size, position or color has changed. If so, the instance variables are updated. The paint method
   * now uses the instance variables to paint the node, instead of creating new instances for each run.
   * </p>
   */
  private static class BallVisual extends VisualGroup {
    // color and size of the ball
    private Color color;

    //////////////// New in this sample ////////////////
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
     * Note that we paint the ball at the origin and move the container
     * to the appropriate location.
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
      ////////////////////////////////////////////////////
    }
  }
}
