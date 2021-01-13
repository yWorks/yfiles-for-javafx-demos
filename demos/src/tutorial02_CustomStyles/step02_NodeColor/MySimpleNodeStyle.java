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
package tutorial02_CustomStyles.step02_NodeColor;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
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

  //////////////// New in this sample ////////////////
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
  ////////////////////////////////////////////////////

  /**
   * Creates the visual for a node.
   */
  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    //////////////// New in this sample ////////////////
    // create the visual that paints the node
    IRectangle layout = node.getLayout();
    BallVisual ball = new BallVisual(getNodeColor(node), layout);
    ////////////////////////////////////////////////////
    // render the node
    ball.render();

    // set the location
    ball.setLayoutX(layout.getX());
    ball.setLayoutY(layout.getY());

    return ball;
  }

  /**
   * A {@link VisualGroup} that paints an ellipse with a semi-transparent gradient.
   * Three reflections of two ellipses and a closed curve make the ball look shiny.
   */
  private static class BallVisual extends VisualGroup {
    //the size and location of the ball to paint
    private IRectangle layout;

    //////////////// New in this sample ////////////////
    // the color of the ball
    private Color color;
    ////////////////////////////////////////////////////

    /**
     * Initializes a <code>BallVisual</code> instance with the given size, location and color.
     */
    BallVisual(Color color, IRectangle layout) {
      this.layout = layout;
      this.color = color;
    }

    /**
     * Paints the shape and the reflections of the ball.
     * Note that we paint the ball at the origin and move the container to the appropriate location.
     */
    public void render() {
      double width = layout.getWidth();
      double height = layout.getHeight();

      // create the ball's shape
      Ellipse shape = new Ellipse(width * 0.5, height * 0.5, width * 0.5, height * 0.5);

      // max and min needed for reflection effect calculation
      double max = Math.max(width, height);
      double min = Math.min(width, height);

      //////////////// New in this sample ////////////////
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
      ////////////////////////////////////////////////////

      LinearGradient fillPaint = new LinearGradient(0, 0, 0.5 / (width / max), 1 / (height / max), true, CycleMethod.NO_CYCLE, gradientStops);
      shape.setFill(fillPaint);

      // create light reflection effects
      double radius1 = min / 20;
      Ellipse reflection1 = new Ellipse(width / 5 + radius1, height / 5 + radius1, radius1, radius1);
      reflection1.setFill(Color.WHITE);

      double radius2 = min / 14;
      Ellipse reflection2 = new Ellipse(width / 4.9 + radius2, height / 4.9 + radius2, radius2, radius2);
      reflection2.setFill(Color.ALICEBLUE);

      Point2D startPoint = new Point2D(width / 2.5, height / 10 * 9);
      Point2D endPoint = new Point2D(width / 10 * 9, height / 2.5);
      Point2D ctrlPoint1 = new Point2D(startPoint.getX() + (endPoint.getX() - startPoint.getX()) / 2, height);
      Point2D ctrlPoint2 = new Point2D(width, startPoint.getY() + (endPoint.getY() - startPoint.getY()) / 2);
      Point2D ctrlPoint3 = new Point2D(ctrlPoint1.getX(), ctrlPoint1.getY() - height / 10);
      Point2D ctrlPoint4 = new Point2D(ctrlPoint2.getX() - width / 10, ctrlPoint2.getY());

      Path reflection3 = new Path(
          new MoveTo(startPoint.getX(), startPoint.getY()),
          new CubicCurveTo(ctrlPoint1.getX(), ctrlPoint1.getY(), ctrlPoint2.getX(), ctrlPoint2.getY(), endPoint.getX(), endPoint.getY()),
          new CubicCurveTo(ctrlPoint4.getX(), ctrlPoint4.getY(), ctrlPoint3.getX(), ctrlPoint3.getY(), startPoint.getX(), startPoint.getY()),
          new ClosePath());
      reflection3.setFill(Color.ALICEBLUE);
      reflection3.setStroke(null);

      // and add all to the container for the node
      this.getChildren().addAll(shape, reflection1, reflection2, reflection3);
    }
  }
}