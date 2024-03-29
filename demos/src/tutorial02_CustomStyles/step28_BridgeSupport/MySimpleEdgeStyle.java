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
package tutorial02_CustomStyles.step28_BridgeSupport;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.BridgeManager;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.EdgeSelectionIndicatorInstaller;
import com.yworks.yfiles.view.IBridgeCreator;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.IObstacleProvider;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.ISelectionIndicatorInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.Objects;


/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.IEdgeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractEdgeStyle} as its base class.
 */
public class MySimpleEdgeStyle extends AbstractEdgeStyle {
  // default gradient to paint the edge with
  private static final Paint DEFAULT_PAINT = new LinearGradient(0, 0, 1, 1, true, CycleMethod.REPEAT,
      new Stop(0, Color.rgb(200, 255, 255, 0.6)),
      new Stop(0.5, Color.rgb(0, 130, 180, 0.8)),
      new Stop(1, Color.rgb(150, 255, 255, 0.6)));

  private double pathThickness;
  private IArrow sourceArrow;
  private IArrow targetArrow;

  /**
   * Returns the arrow at the beginning of the edge.
   */
  public IArrow getSourceArrow() {
    return sourceArrow;
  }

  /**
   * Specifies the arrow at the beginning of the edge.
   */
  public void setSourceArrow(IArrow arrow) {
    this.sourceArrow = arrow;
  }

  /**
   * Returns the arrow at the end of the edge.
   */
  public IArrow getTargetArrow() {
    return targetArrow;
  }

  /**
   * Specifies the arrow at the end of the edge.
   */
  public void setTargetArrow(IArrow arrow) {
    this.targetArrow = arrow;
  }

  /**
   * Initializes a new <code>MySimpleEdgeStyle</code>instance using {@link MySimpleArrow} at both ends of the edge and
   * an edge thickness of 3.
   */
  public MySimpleEdgeStyle() {
    this(new MySimpleArrow(), 3);
  }

  /**
   * Initializes a new <code>MySimpleEdgeStyle</code>instance using the given arrow at both ends of the edge and the
   * given edge thickness.
   */
  public MySimpleEdgeStyle(IArrow arrow, double pathThickness) {
    this(arrow, arrow, pathThickness);
  }
  /**
   * Initializes a new <code>MySimpleEdgeStyle</code>instance using the given arrows and the given edge thickness.
   */
  public MySimpleEdgeStyle(IArrow sourceArrow, IArrow targetArrow, double pathThickness) {
    setSourceArrow(sourceArrow);
    setTargetArrow(targetArrow);
    setPathThickness(pathThickness);
  }

  /**
   * Gets the thickness of the edge. The default is 3.0.
   */
  public double getPathThickness() {
    return pathThickness;
  }

  /**
   * Sets the thickness of the edge. The default is 3.0.
   */
  public void setPathThickness(double pathThickness) {
    this.pathThickness = pathThickness;
  }

  /**
   * Creates the visual for an edge.
   */
  @Override
  protected Node createVisual(IRenderContext context, IEdge edge) {
    // create a group that holds all visuals needed to paint the edge
    VisualGroup group = new VisualGroup();
    GeneralPath path = getPath(edge);

    // create the visual that paints the edge path
    EdgeVisual edgeVisual = new EdgeVisual();
    edgeVisual.update(context, path, getPathThickness(), isSelected(context, edge), getObstacleHash(context));
    group.add(edgeVisual);

    // add visuals that paint the arrows
    addArrows(context, group, edge, path, getSourceArrow(), getTargetArrow());

    return group;
  }

  /**
   * Re-renders the edge using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, IEdge)} is called instead.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, IEdge edge) {
    if (!(oldVisual instanceof VisualGroup)) {
      return createVisual(context, edge);
    }

    VisualGroup group = (VisualGroup) oldVisual;
    GeneralPath path = getPath(edge);

    // update the visual that paints the edge path
    EdgeVisual edgeVisual = (EdgeVisual) group.getChildren().get(0);
    edgeVisual.update(context, path, getPathThickness(), isSelected(context, edge), getObstacleHash(context));

    // since the edge's ends might have changed their positions or orientations, we also have to update the visuals
    // painting the arrows
    updateArrows(context, group, edge, path, getSourceArrow(), getTargetArrow());

    return group;
  }

  /**
   * Creates a {@link com.yworks.yfiles.geometry.GeneralPath} from the segments of the given edge.
   */
  @Override
  protected GeneralPath getPath(IEdge edge) {
    //////////////// New in this sample ////////////////
    // We move the path creation to an extra method so we can use it in obstacle provider, too.
    GeneralPath path = createPath(edge);
    ////////////////////////////////////////////////////

    // crop the edge's path at its nodes
    // take the arrows into account when cropping the path
    return cropPath(edge, getSourceArrow(), getTargetArrow(), path);
  }

  //////////////// New in this sample ////////////////
  /**
   * Creates a general path for the locations of the ports and the bends of the edge.
   */
  private static GeneralPath createPath(IEdge edge) {
    // create a general path from the source port over the bends to the target port of the edge
    GeneralPath path = new GeneralPath();
    path.moveTo(edge.getSourcePort().getLocation());
    for (IBend bend : edge.getBends()) {
      path.lineTo(bend.getLocation());
    }
    path.lineTo(edge.getTargetPort().getLocation());
    return path;
  }

  /**
   * Decorates a given path with bridges.
   * All work is delegated to method {@link BridgeManager#addBridges(IRenderContext, GeneralPath, IBridgeCreator)}.
   * @param path    the path to decorate
   * @param context the render context
   * @return a copy of the given path with bridges
   */
  private static GeneralPath createPathWithBridges(GeneralPath path, IRenderContext context) {
    BridgeManager manager = getBridgeManager(context);
    // if there is a bridge manager registered: use it to add the bridges to the path
    return manager == null ? path : manager.addBridges(context, path, null);
  }

  /**
   * Queries the context's lookup for a {@link BridgeManager} instance.
   * @param context the context to get the bridge manager from
   * @return the BridgeManager for the given context instance or null
   */
  private static BridgeManager getBridgeManager(IRenderContext context) {
    return context == null ? null : context.lookup(BridgeManager.class);
  }

  /**
   * Gets an obstacle hash from the context. The obstacle hash changes if any obstacle has changed on the entire graph.
   * The hash is used to avoid re-rendering the edge if nothing has changed. This method gets the obstacle hash from a
   * {@link BridgeManager} instance.
   * @param context the context to get the obstacle hash for
   * @return a hash value which represents the state of the obstacles
   */
  private static long getObstacleHash(IRenderContext context) {
    BridgeManager manager = getBridgeManager(context);
    // get the BridgeManager from the context's lookup. If there is one
    // get a hash value which represents the current state of the obstacles.
    return manager == null ? 42 : manager.getObstacleHash(context);
  }
  ////////////////////////////////////////////////////

  /**
   * Determines if the visual representation of the edge has been hit at the given location.
   * This implementation includes {@link #getPathThickness()} and the given
   * context's <code>HitTestRadius</code> in the calculation.
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD location, IEdge edge) {
    // use the convenience method in GeneralPath
    return getPath(edge).pathContains(location, context.getHitTestRadius() + getPathThickness() * 0.5);
  }

  /**
   * Returns whether the edge is selected or not.
   */
  private boolean isSelected(IRenderContext context, IEdge edge) {
    // we acquire the CanvasComponent instance from the render context and
    // fetch an IGraphSelection instance using its lookup. We can be sure
    // that those instances actually exist in this demo because we know that
    // our canvas is a GraphControl.
    // This is equivalent to casting the canvas to GraphControl and calling
    // the method GraphControl#getSelection() from it.
    IGraphSelection selection = context.getCanvasControl().lookup(IGraphSelection.class);
    return selection.isSelected(edge);
  }

  /**
   * Provides a custom implementation of the {@link com.yworks.yfiles.view.ISelectionIndicatorInstaller} interface
   * that is better suited to this style.
   */
  @Override
  protected Object lookup(IEdge edge, Class type) {
    if (type == ISelectionIndicatorInstaller.class) {
      return new MySelectionInstaller();
      //////////////// New in this sample ////////////////
    } else if (type == IObstacleProvider.class) {
      // provide the IObstacleProvider implementation
      return new BasicEdgeObstacleProvider(edge);
      ////////////////////////////////////////////////////
    } else {
      return super.lookup(edge, type);
    }
  }

  //////////////// New in this sample ////////////////
  /**
   * An {@link com.yworks.yfiles.view.IObstacleProvider} that provides the path of the current edge as obstacle.
   */
  private static class BasicEdgeObstacleProvider implements IObstacleProvider {
    private IEdge edge;

    BasicEdgeObstacleProvider(IEdge edge) {
    this.edge = edge;
  }

    /**
     * Returns this edge's path as obstacle. Generally spoken, an obstacle is a path for which other edges might have to
     * draw bridges when crossing it.
     */
    public GeneralPath getObstacles(IRenderContext context) {
      // simply delegate to createPath
      return createPath(edge);
    }
  }
  ////////////////////////////////////////////////////

  /**
   * This customized {@link com.yworks.yfiles.view.ISelectionIndicatorInstaller} overrides the pen property to be
   * transparent, so that no edge path is rendered if the edge is selected.
   */
  private static class MySelectionInstaller extends EdgeSelectionIndicatorInstaller {
    @Override
    protected Pen getPen(CanvasControl canvas, IEdge edge) {
      return Pen.getTransparent();
    }
  }

  /**
   * A {@link VisualGroup} that paints the line of the edge. We store the path, the thickness and
   * the selection state of the edge as well as a hash value that represents the state of the obstacles as instance
   * variables.
   * The {@link #update(IRenderContext, GeneralPath, double, boolean, long)} method checks whether these values have
   * been changed. If so, the instance variables are updated.
   */
  private static class EdgeVisual extends VisualGroup {
    // identity transformation
    private static final Matrix2D IDENTITY = new Matrix2D();

    // the shape to paint the line of the edge
    private Path path;
    // stores the segments of the edge; used to create and update the path
    private GeneralPath generalPath;
    // the thickness of the edge
    private double pathThickness;
    // whether or not the edge is currently selected; used to determine the color of the edge
    private boolean selected;
    // paint used for selected edges
    private AnimatedPaint animatedPaint;
    private ChangeListener<Paint> paintChangeListener;

    EdgeVisual() {
      this.path = new Path();
      path.setFill(Color.TRANSPARENT);
      animatedPaint = new AnimatedPaint();
      paintChangeListener = (observable, oldPaint, newPaint) -> updatePathPaint(newPaint, pathThickness);
      this.add(path);
    }

    //////////////// New in this sample ////////////////
    // The obstacle hash represents the state of the obstacles in the graph.
    // If the hash has been changed then the position or number of obstacles has changed.
    // That means the position or number of the bridges on the edge might have to be changed accordingly.
    // Thus, the edge might have to be re-rendered.
    private long obstacleHash;

    /**
     * Checks if the path or the thickness of the edge has been changed. If so, updates all items needed to paint the
     * edge.
     * @param generalPath   the path of the edge
     * @param pathThickness the thickness of the edge
     * @param selected      whether or not the edge is selected
     * @param obstacleHash  represents the state of the obstacles in the graph
     */
    void update(IRenderContext context, GeneralPath generalPath, double pathThickness, boolean selected, long obstacleHash) {
      // update path
      if (!Objects.equals(generalPath, this.generalPath) || obstacleHash != this.obstacleHash) {
        GeneralPath pathWithBridges = createPathWithBridges(generalPath, context);
        pathWithBridges.updatePath(path, IDENTITY);
        ////////////////////////////////////////////////////

        this.generalPath = generalPath;
        this.obstacleHash = obstacleHash;
      }

      // update fill
      if (pathThickness != this.pathThickness || selected != this.selected) {
        if (!selected) {
          // fill for non-selected state
          animatedPaint.removeListener(paintChangeListener);
          updatePathPaint(DEFAULT_PAINT, pathThickness);
        } else if (!this.selected) {
          // fill for selected state
          animatedPaint.addListener(paintChangeListener);
        }

        this.pathThickness = pathThickness;
        this.selected = selected;
      }
    }

    private void updatePathPaint(Paint paint, double thickness) {
      Pen pen = new Pen(paint, thickness);
      pen.styleShape(path);
    }

    /**
     * Binding used to animate the stroke of a selected edge.
     */
    private static class AnimatedPaint extends ObjectBinding<Paint> {
      private static final double SIZE = 30.0;
      private static final int DURATION_MILLIS = 500;

      private DoubleProperty start = new SimpleDoubleProperty(this, "start", 0);

      AnimatedPaint() {
        super.bind(start);

        Timeline timeline = new Timeline();
        timeline.setAutoReverse(false);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, new KeyValue(start, 0.0)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DURATION_MILLIS), new KeyValue(start, SIZE)));
        timeline.play();
      }

      @Override
      protected Paint computeValue() {
        double startValue = this.start.get();
        return new LinearGradient(startValue, startValue, startValue + SIZE, startValue + SIZE, false, CycleMethod.REPEAT,
            new Stop(0, Color.rgb(255, 215, 0, 1.0)),
            new Stop(0.5, Color.rgb(255, 245, 30, 1.0)),
            new Stop(1, Color.rgb(255, 215, 0, 1.0)));
      }
    }
  }
}
