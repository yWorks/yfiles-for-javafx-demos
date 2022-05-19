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
package analysis.networkflows;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;


/**
 * An edge style visualizing a network flow.
 */
public class NetworkFlowEdgeStyle extends AbstractEdgeStyle {

  /**
   * Creates the visual for an edge.
   *
   * @param context The render context.
   * @param edge    The edge to which this style instance is assigned.
   * @return The new visual.
   * @see AbstractEdgeStyle#createVisual(IRenderContext, IEdge)
   */
  @Override
  protected Node createVisual(IRenderContext context, IEdge edge) {
    NetworkFlowEdgeVisual visual = new NetworkFlowEdgeVisual();
    visual.update(
        getPath(edge),
        isSelected(context, edge),
        (EdgeData) edge.getTag());
    return visual;
  }

  /**
   * Re-renders the edge using the old visual for performance reasons.
   *
   * @param context   The render context.
   * @param oldVisual The old visual.
   * @param edge      The edge to which this style instance is assigned.
   * @return The updated visual
   * @see AbstractEdgeStyle#updateVisual
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, IEdge edge) {
    if (oldVisual instanceof NetworkFlowEdgeVisual) {
      ((NetworkFlowEdgeVisual) oldVisual).update(
          getPath(edge),
          isSelected(context, edge),
          (EdgeData) edge.getTag());
    }
    return createVisual(context, edge);
  }

  /**
   * Creates a {@link GeneralPath} from the edge's bends.
   *
   * @param edge The edge to create the path for.
   * @return {@link GeneralPath} following the edge.
   * @see AbstractEdgeStyle#getPath
   */
  @Override
  protected GeneralPath getPath(IEdge edge) {
    PointD sp = edge.getSourcePort().getLocation();
    PointD tp = edge.getTargetPort().getLocation();
    PointD portOffset = new PointD(5, 0);

    GeneralPath path = new GeneralPath();
    path.moveTo(sp);
    path.lineTo(PointD.add(sp, portOffset));
    for (IBend bend : edge.getBends()) {
      path.lineTo(bend.getLocation());
    }
    path.lineTo(PointD.subtract(tp, portOffset));
    path.lineTo(tp);
    return path;
  }

  /**
   * Determines whether the visual representation of the edge has been hit at the given location.
   *
   * @param context The render context.
   * @param point   The coordinates of the query in the world coordinate system.
   * @param edge    The given edge.
   * @return True, if the edge has been hit, false otherwise.
   * @see AbstractEdgeStyle#isHit
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD point, IEdge edge) {
    double radius = 0;
    double sourcePortX = edge.getSourcePort().getLocation().getX();
    double targetPortX = edge.getTargetPort().getLocation().getX();

    int capacity = getCapacity(edge);
    double x = point.getX();
    // edge has capacity and point is horizontally between source and target port
    if (capacity > 0 && Math.min(sourcePortX, targetPortX) <= x && x <= Math.max(sourcePortX, targetPortX)) {
      radius = capacity * 0.5;
    }

    return getPath(edge).pathContains(point, context.getHitTestRadius() + radius);
  }

  private boolean isSelected(IRenderContext context, IEdge edge) {
    return ((GraphControl) context.getCanvasControl()).getSelection().isSelected(edge);
  }

  private static int getCapacity(IEdge edge) {
    EdgeData flowData = (EdgeData) edge.getTag();
    return flowData == null ? 0 : flowData.getCapacity();
  }

  private static final class NetworkFlowEdgeVisual extends VisualGroup {
    private static final Color BACKGROUND_COLOR = Color.rgb(220, 220, 220);
    private static final Color FOREGROUND_COLOR = Color.DARKBLUE;
    private static final Color SELECTION_COLOR = Color.INDIANRED;

    private GeneralPath edgePath;
    private final Path backgroundPath;
    private final Path foregroundPath;

    private boolean selected;
    private int capacity;
    private double flow;

    NetworkFlowEdgeVisual() {
      edgePath = new GeneralPath();
      backgroundPath = new Path();
      backgroundPath.setStroke(BACKGROUND_COLOR);
      backgroundPath.setStrokeLineCap(StrokeLineCap.BUTT);
      this.add(backgroundPath);
      foregroundPath = new Path();
      foregroundPath.setStroke(FOREGROUND_COLOR);
      foregroundPath.setStrokeLineCap(StrokeLineCap.BUTT);
      this.add(foregroundPath);
    }

    void update(GeneralPath edgePath, boolean selected, EdgeData flowData) {
      if (!this.edgePath.isEquivalentTo(edgePath)) {
        this.edgePath = edgePath;
        edgePath.updatePath(backgroundPath, null);
        edgePath.updatePath(foregroundPath, null);
      }

      int newCapacity = flowData.getCapacity();
      double newFlow = flowData.getFlow();

      if (this.selected != selected || this.capacity != newCapacity) {
        backgroundPath.setStroke(selected ? SELECTION_COLOR : BACKGROUND_COLOR);
        backgroundPath.setStrokeWidth(selected ? Math.max(newCapacity + 2, 2) : Math.max(newCapacity, 1));
      }

      if (this.capacity != newCapacity || this.flow != newFlow) {
        foregroundPath.setStrokeWidth(newCapacity == 0 ? 0 : newFlow);
      }

      this.selected = selected;
      this.capacity = newCapacity;
      this.flow = newFlow;
    }
  }
}