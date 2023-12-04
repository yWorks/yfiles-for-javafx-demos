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
package tutorial02_CustomStyles.step16_CustomEdgeStyle;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.EdgeSelectionIndicatorInstaller;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.ISelectionIndicatorInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Path;

import java.util.Objects;

/////////////// This class is new in this sample ///////////////

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

  /**
   * Initializes a new <code>MySimpleEdgeStyle</code>instance with an edge thickness of 3.
   */
  public MySimpleEdgeStyle() {
    setPathThickness(3);
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
    // create the visual that paints the edge path
    EdgeVisual edgeVisual = new EdgeVisual();
    edgeVisual.update(getPath(edge), getPathThickness());
    return edgeVisual;
  }

  /**
   * Re-renders the edge using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, IEdge)} is called instead.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, IEdge edge) {
    // update the visual that paints the edge path
    EdgeVisual edgeVisual = (EdgeVisual) oldVisual;
    edgeVisual.update(getPath(edge), getPathThickness());
    return oldVisual;
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
    return path;
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
   * A {@link VisualGroup} that paints the line of an edge. We store the path and the thickness of
   * the edge as instance variables.
   * The {@link #update(GeneralPath, double)} method checks whether these values have been changed.
   * If so, the instance variables are updated.
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

    EdgeVisual() {
      this.path = new Path();
      this.add(path);
    }

    /**
     * Checks if the path or the thickness of the edge has been changed. If so, updates all items needed to paint the
     * edge.
     * @param generalPath   the path of the edge
     * @param pathThickness the thickness of the edge
     */
    void update(GeneralPath generalPath, double pathThickness) {
      // update the path
      if (!Objects.equals(generalPath, this.generalPath) || pathThickness != this.pathThickness) {
        this.generalPath = generalPath;
        this.generalPath.updatePath(path, IDENTITY);
        Pen pen = new Pen(DEFAULT_PAINT, pathThickness);
        pen.styleShape(path);
      }

      this.pathThickness = pathThickness;
    }
  }
}
