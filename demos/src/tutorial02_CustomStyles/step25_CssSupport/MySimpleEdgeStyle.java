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
package tutorial02_CustomStyles.step25_CssSupport;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.EdgeSelectionIndicatorInstaller;
import com.yworks.yfiles.view.IGraphSelection;
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
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.IEdgeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractEdgeStyle} as its base class.
 */
public class MySimpleEdgeStyle extends AbstractEdgeStyle {
    // gradient to paint the edge with
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
    edgeVisual.update(path, getPathThickness(), isSelected(context, edge));
    group.add(edgeVisual);

    //////////////// New in this sample ////////////////
    context.setDisposeCallback(edgeVisual, edgeVisual::dispose);
    ////////////////////////////////////////////////////

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
    edgeVisual.update(path, getPathThickness(), isSelected(context, edge));

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
    // create a general path from the source port over the bends to the target port of the edge
    GeneralPath path = new GeneralPath();
    path.moveTo(edge.getSourcePort().getLocation());
    for (IBend bend : edge.getBends()) {
      path.lineTo(bend.getLocation());
    }
    path.lineTo(edge.getTargetPort().getLocation());

    // crop the edge's path at its nodes

    // take the arrows into account when cropping the path
    return cropPath(edge, getSourceArrow(), getTargetArrow(), path);
  }

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
    } else {
      return super.lookup(edge, type);
    }
  }

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
   * The group that defines the properties that can be styled by CSS:
   * <ul>
   *   <li>paint: The paint to draw the edges with. Of type {@link Paint}</li>
   * </ul>
   */
  private static class EdgeVisual extends VisualGroup {
    // identity transformation
    private static final Matrix2D IDENTITY = new Matrix2D();

    //////////////// New in this sample ////////////////
    private StyleableObjectProperty<Paint> paint;
    ////////////////////////////////////////////////////

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
      animatedPaint = new AnimatedPaint();
      paintChangeListener = (observable, oldPaint, newPaint) -> updatePathPaint(newPaint, pathThickness);
      this.add(path);

      //////////////// New in this sample ////////////////
      getStyleClass().add("simple-edge-style");
      paintProperty().addListener(paintChangeListener);
      ////////////////////////////////////////////////////
    }

    /**
     * Checks if the path or the thickness of the edge has been changed. If so, updates all items needed to paint the
     * edge.
     * @param generalPath   the path of the edge
     * @param pathThickness the thickness of the edge
     * @param selected      whether or not the edge is selected
     */
    void update(GeneralPath generalPath, double pathThickness, boolean selected) {
      // update path
      if (!Objects.equals(generalPath, this.generalPath)) {
        generalPath.updatePath(path, IDENTITY);

        this.generalPath = generalPath;
      }

      // update fill
      if (pathThickness != this.pathThickness || selected != this.selected) {

        //////////////// New in this sample ////////////////
        if (this.selected && !selected) {
          // fill for non-selected state
          animatedPaint.removeListener(paintChangeListener);
          paintProperty().addListener(paintChangeListener);
        } else if (!this.selected && selected) {
          // fill for selected state
          paintProperty().removeListener(paintChangeListener);
          animatedPaint.addListener(paintChangeListener);
        }
        if (!selected) {
          updatePathPaint(getPaint(), pathThickness);
        }
        ////////////////////////////////////////////////////

        this.pathThickness = pathThickness;
        this.selected = selected;
      }
    }

    private void updatePathPaint(Paint paint, double thickness) {
      Pen pen = new Pen(paint, thickness);
      pen.styleShape(path);
    }

    //////////////// New in this sample ////////////////
    public Paint getPaint() {
      return paint == null ? DEFAULT_PAINT : paint.get();
    }

    public void setPaint(Paint pathPaint) {
      this.paint.set(pathPaint);
    }

    StyleableObjectProperty<Paint> paintProperty() {
      if (paint == null) {
        paint = new SimpleStyleableObjectProperty<>(StyleableProperties.PAINT, EdgeVisual.this, "paint", DEFAULT_PAINT);
      }
      return paint;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return StyleableProperties.STYLEABLES;
    }

    /**
     * The {@link com.yworks.yfiles.view.IDisposeVisualCallback} of this visual that removes any remaining listener.
     */
    public Node dispose(IRenderContext context, Node node, boolean disposeForGood) {
      if (disposeForGood) {
        if (paint != null) {
          paint.removeListener(paintChangeListener);
        }
        if (animatedPaint != null) {
          animatedPaint.removeListener(paintChangeListener);
        }
        return null;
      } else {
        return this;
      }
    }

    /**
     * Helper class that provides the CSS meta data.
     */
    private static class StyleableProperties {
      private static final CssMetaData<EdgeVisual, Paint> PAINT =
          new CssMetaData<EdgeVisual, Paint>("paint", StyleConverter.getPaintConverter()) {
            @Override
            public boolean isSettable(EdgeVisual styleable) {
              return styleable.paint == null || !styleable.paint.isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(EdgeVisual styleable) {
              return styleable.paintProperty();
            }
          };

      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
        ArrayList<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Group.getClassCssMetaData());
        Collections.addAll(styleables, PAINT);
        STYLEABLES = Collections.unmodifiableList(styleables);
      }
    }
    ////////////////////////////////////////////////////

    /**
     * Binding used to animate the stroke of a selected edge.
     */
    private static class AnimatedPaint extends ObjectBinding<Paint> {
      private static final double SIZE = 30.0;
      private static final int DURATION_MILLIS = 500;

      private DoubleProperty start = new SimpleDoubleProperty(this, "start", 0);

      public AnimatedPaint() {
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
