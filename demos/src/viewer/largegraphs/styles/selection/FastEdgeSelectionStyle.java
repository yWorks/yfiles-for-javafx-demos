/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.largegraphs.styles.selection;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.Arrays;

/**
 * Edge style that is used as a zoom-invariant selection decorator.
 * <p>
 * This style essentially displays a path along the edge and scales its stroke thickness and brush by 1 / zoom level.
 * This means that positioning considerations can still be done in world coordinates and the path doesn't require a
 * series of transformations to end up where it should be. The brush is scaled because the default selection
 * decoration uses a pixel checkerboard pattern which would otherwise be scaled with the zoom level.
 * </p>
 * <p>
 * This style caches the scaled pen to avoid creating a new pen for every invocation of
 * {@link #updateVisual(IRenderContext, Node, IEdge)}. Thus, this style cannot be shared over multiple
 * {@link com.yworks.yfiles.view.GraphControl} instances because the zoom level might differ. If the pen's paint
 * is simply a solid color the scaling step can be omitted.
 * </p>
 */
public class FastEdgeSelectionStyle extends AbstractEdgeStyle {

  /**
   * The cached instance of the scaled pen.
   */
  private Pen scaledPen;

  /**
   * The scale at which the cached pen was scaled.
   */
  private double scaledPenScale;

  private Pen pen;

  /**
   * Gets the pen used to draw the rectangle outline.
   */
  public Pen getPen() {
    return pen;
  }

  /**
   * Sets the pen used to draw the rectangle outline.
   */
  public void setPen(Pen pen) {
    this.pen = pen;
  }

  /**
   * Initializes a new instance with the given pen.
   * @param pen The pen used to draw the path.
   */
  public FastEdgeSelectionStyle(Pen pen) {
    this.pen = pen;
  }

  // region Style

  @Override
  protected Node createVisual(IRenderContext context, IEdge edge) {
    double scale = 1 / context.getZoom();
    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    PointD[] bendLocations = getBendLocations(edge);

    Group path = createGeometry(source, target, bendLocations);
    path.setUserData(new EdgeInfo(source, target, bendLocations));
    updatePen(path, scale);

    return path;
  }

  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, IEdge edge) {
    if (!(oldVisual.getUserData() instanceof EdgeInfo)) {
      return createVisual(context, edge);
    }

    Group path = (Group) oldVisual;
    EdgeInfo edgeInfo = (EdgeInfo) path.getUserData();

    double scale = 1 / context.getZoom();
    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    PointD[] bendLocations = getBendLocations(edge);


    if (source.equals(edgeInfo.source) &&
        target.equals(edgeInfo.target) &&
        Arrays.equals(bendLocations, edgeInfo.bendLocations)) {
    } else {
      path = updateGeometry(path, source, target, bendLocations);
      path.setUserData(new EdgeInfo(source, target, bendLocations));
    }
    updatePen(path, scale);

    return path;
  }

  /**
   * Creates the selection path's geometry.
   * @param source        The source port location.
   * @param target        The target port location.
   * @param bendLocations The locations of the edge's bends.
   * @return The selection path's geometry.
   */
  private Group createGeometry(PointD source, PointD target, PointD[] bendLocations) {
    return updateGeometry(new Group(), source, target, bendLocations);
  }

  /**
   * Updates the selection path's geometry.
   * @param path          The path to update.
   * @param source        The source port location.
   * @param target        The target port location.
   * @param bendLocations The locations of the edge's bends.
   * @return The selection path's geometry.
   */
  private Group updateGeometry(Group path, PointD source, PointD target, PointD[] bendLocations) {
    int n = 0;

    PointD last = source;
    for (PointD bend : bendLocations) {
      addLine(path, n++, last, bend);
      last = bend;
    }
    addLine(path, n++, last, target);

    // delete all superfluous lines
    if (path.getChildren().size() > n) {
      path.getChildren().remove(n, path.getChildren().size());
    }

    return path;
  }

  /**
   * Sets the line at the given index of the group to the source and target position. If there is no line at the given
   * index a new line will be created.
   * @param group  The group containing the line.
   * @param idx    The index of the line.
   * @param source The source position.
   * @param target The target position.
   */
  private void addLine(Group group, int idx, PointD source, PointD target) {
    if (group.getChildren().size() > idx) {
      Line line = (Line) group.getChildren().get(idx);
      line.setStartX(source.x);
      line.setStartY(source.y);
      line.setEndX(target.x);
      line.setEndY(target.y);
    } else {
      Line line = new Line(source.x, source.y, target.x, target.y);
      group.getChildren().add(line);
    }
  }

  /**
   * Re-creates the scaled stroke brush if necessary and sets it on the rectangle.
   * @param group The lines whose stroke pen will be updated.
   * @param scale The scale. This is 1 / zoom level.
   */
  private void updatePen(Group group, double scale) {
    if (scale != scaledPenScale || scaledPen == null) {
      // If the cached brush no longer matches the scale, re-create it.
      scaledPen = pen.cloneCurrentValue();
      scaledPen.setThickness(pen.getThickness() * scale);
      scaledPen.freeze();
      scaledPenScale = scale;
    }

    for (Node child : group.getChildren()) {
      scaledPen.styleShape((Shape) child);
    }
  }

  /**
   * Gets a list of bend locations from an edge.
   *
   * @param edge The edge.
   * @return A list of the edge's bend locations, or an empty list if there are no bends.
   */
  private static PointD[] getBendLocations(IEdge edge) {
    IListEnumerable<IBend> bends = edge.getBends();
    int count = bends.size();
    PointD[] points = new PointD[count];
    for (int i = 0; i < count; i++) {
      points[i] = bends.getItem(i).getLocation().toPointD();
    }
    return points;
  }

  // endregion

  /**
   * Helper structure to keep information about the edge.
   */
  private static class EdgeInfo {
    /**
     * A list of bend locations in the edge.
     */
    final PointD[] bendLocations;

    /**
     * The source port location.
     */
    final PointD source;

    /**
     * The target port location.
     */
    final PointD target;

    /**
     * Initializes a new instance of the EdgeInfo structure, using the given source and target port locations and the
     * given list of bend locations.
     * @param source        The source port location.
     * @param target        The target port location.
     * @param bendLocations A list of bend locations.
     */
    public EdgeInfo(PointD source, PointD target, PointD[] bendLocations) {
      this.source = source;
      this.target = target;
      this.bendLocations = bendLocations;
    }
  }
}