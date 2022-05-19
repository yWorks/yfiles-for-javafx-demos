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
package viewer.largegraphs.styles.fast;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailLabelStyle;

import java.util.Arrays;

/**
 * A faster edge style.
 * <p>
 * This style offers three main optimizations compared to the default {@link com.yworks.yfiles.graph.styles.PolylineEdgeStyle}:
 * <ul>
 * <li>The edge is not clipped at node boundaries.</li>
 * <li>Bends are not drawn below a configurable zoom level.</li>
 * <li>Edges are hidden completely if they are shorter than a given number of pixels on screen.</li>
 * </ul>
 * </p>
 * <p>
 * When an edge has labels, bends should not be hidden as long as the edge labels are visible. This is because edge
 * labels are attached to the conceptual edge path, which includes bends. If bends are not drawn, edge labels may look
 * out of place or even far away from the actually displayed edge path. Using {@link LevelOfDetailLabelStyle} a suitable
 * zoom level at which to display labels can easily be configured.
 * </p>
 */
public class FastEdgeStyle extends AbstractEdgeStyle {

  /**
   * Initializes a new instance of the {@link FastEdgeStyle} class with default settings.
   * <p>
   * By default bends are not drawn below a zoom level of 50&nbsp;% and edges shorter than 10 pixels are hidden.
   * </p>
   */
  public FastEdgeStyle() {
    drawBendsThreshold = 0.5;
    minimumEdgeLength = 10;
  }

  private double drawBendsThreshold;

  /**
   * Gets the minimum zoom level at which bends are drawn.
   * <p>
   * Below this zoom level the edge is only drawn as a single line between its source and target ports.
   * </p>
   */
  public double getDrawBendsThreshold() {
    return drawBendsThreshold;
  }

  /**
   * Sets the minimum zoom level at which bends are drawn.
   * <p>
   * Below this zoom level the edge is only drawn as a single line between its source and target ports.
   * </p>
   */
  public void setDrawBendsThreshold(double drawBendsThreshold) {
    this.drawBendsThreshold = drawBendsThreshold;
  }

  private double minimumEdgeLength;

  /**
   * Gets the minimum length (in pixels on screen) where edges will still be drawn.
   * <p>
   * All edges where the distance between source and target port is shorter than this will not be displayed.
   * </p>
   */
  public double getMinimumEdgeLength() {
    return minimumEdgeLength;
  }

  /**
   * Sets the minimum length (in pixels on screen) where edges will still be drawn.
   * <p>
   * All edges where the distance between source and target port is shorter than this will not be displayed.
   * </p>
   */
  public void setMinimumEdgeLength(double minimumEdgeLength) {
    this.minimumEdgeLength = minimumEdgeLength;
  }


  public FastEdgeStyle(double drawBendsThreshold, double minimumEdgeLength) {
    this.drawBendsThreshold = drawBendsThreshold;
    this.minimumEdgeLength = minimumEdgeLength;
  }

  @Override
  protected Node createVisual(IRenderContext context, IEdge edge) {
    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    double zoom = context.getZoom();
    if (!shouldDrawEdge(source, target, zoom)) {
      return null;
    }

    boolean drawBends = shouldDrawBends(zoom);
    PointD[] bendLocations = getBendLocations(edge);

    Node shape = createGeometry(source, target, drawBends, bendLocations);

    shape.setUserData(new EdgeInfo(source, target, drawBends, bendLocations));
    return shape;
  }

  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, IEdge edge) {
    if (!(oldVisual.getUserData() instanceof EdgeInfo)) {
      return createVisual(context, edge);
    }
    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    double zoom = context.getZoom();

    if (!shouldDrawEdge(source, target, zoom)) {
      return null;
    }

    EdgeInfo oldEdgeInfo = (EdgeInfo) oldVisual.getUserData();

    boolean drawBends = shouldDrawBends(zoom);
    PointD[] bendLocations = getBendLocations(edge);

    // Did anything change at all? If not, we can just re-use the old visual
    if (source.equals(oldEdgeInfo.source) &&
        target.equals(oldEdgeInfo.target) &&
        drawBends == oldEdgeInfo.drawBends &&
        Arrays.equals(bendLocations, oldEdgeInfo.bendLocations)) {
      return oldVisual;
    }

    // Otherwise re-create the geometry and update the cache
    oldVisual = updateGeometry(oldVisual, source, target, drawBends, bendLocations);
    oldVisual.setUserData(new EdgeInfo(source, target, drawBends, bendLocations));
    return oldVisual;
  }

  /**
   * Creates the edge path's geometry.
   * @param source        The source port location.
   * @param target        The target port location.
   * @param drawBends     Flag to set whether bends are included in the path or not.
   * @param bendLocations A list of bend locations.
   * @return The edge path's geometry.
   */
  private Node createGeometry(PointD source, PointD target, boolean drawBends, PointD[] bendLocations) {
    return updateGeometry(new Group(), source, target, drawBends, bendLocations);
  }

  /**
   * Updates the edge path's geometry.
   * @param node          The edge group to update;
   * @param source        The source port location.
   * @param target        The target port location.
   * @param drawBends     Flag to set whether bends are included in the path or not.
   * @param bendLocations A list of bend locations.
   * @return The edge path's geometry.
   */
  private Node updateGeometry(Node node, PointD source, PointD target, boolean drawBends, PointD[] bendLocations) {
    Group group = (Group) node;
    int n = 0;

    if (drawBends) {
      PointD last = source;
      for (PointD bend : bendLocations) {
        addLine(group, n++, last, bend);
        last = bend;
      }
      addLine(group, n++, last, target);
    } else {
      addLine(group, n++, source, target);
    }

    // delete all superfluous lines
    if (group.getChildren().size() > n) {
      group.getChildren().remove(n, group.getChildren().size());
    }

    return group;
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
      line.setStroke(Color.BLACK);
      line.setStrokeWidth(3);
      group.getChildren().add(line);
    }
  }

  @Override
  protected RectD getBounds(ICanvasContext context, IEdge edge) {
    double zoom = context.getZoom();
    if (zoom >= getDrawBendsThreshold()) {
      return super.getBounds(context, edge);
    }
    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    if (shouldDrawEdge(source, target, zoom)) {
      return new RectD(source, target);
    }
    return RectD.EMPTY;
  }



  /**
   * Determines whether the edge should be drawn at all, taking into account the value of the {@link
   * #getMinimumEdgeLength()} property.
   *
   * @param source The source port location.
   * @param target The target port location.
   * @param zoom   The current zoom level.
   * @return <code>true</code>, if the edge should be drawn, <code>false</code> otherwise.
   */
  private boolean shouldDrawEdge(PointD source, PointD target, double zoom) {
    double dx = (source.getX() - target.getX()) * zoom;
    double dy = (source.getY() - target.getY()) * zoom;

    // Minor optimization: Avoid square root
    double distSquared = dx * dx + dy * dy;
    return distSquared >= getMinimumEdgeLength() * getMinimumEdgeLength();
  }

  /**
   * Determines whether bends should be drawn, according to the value of the {@link #getDrawBendsThreshold()}
   * property.
   *
   * @param zoom The current zoom level.
   * @return <code><true/code>, if bends should be drawn, <code>false</code> if not.
   */
  private boolean shouldDrawBends(double zoom) {
    return zoom >= getDrawBendsThreshold();
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


  /**
   * Helper structure to keep information about the edge.
   */
  private static class EdgeInfo {
    /**
     * A list of bend locations in the edge.
     */
    PointD[] bendLocations;

    /**
     * A flag determining whether bends should be drawn or not.
     */
    boolean drawBends;

    /**
     * The source port location.
     */
    public PointD source;

    /**
     * The target port location.
     */
    public PointD target;

    /**
     * Initializes a new instance of the EdgeInfo structure, using the given source and target port locations, whether
     * to draw bends or not and the given list of bend locations.
     *
     * @param source        The source port location.
     * @param target        The target port location.
     * @param drawBends     A flag determining whether bends should be drawn or not.
     * @param bendLocations A list of bend locations.
     */
    public EdgeInfo(PointD source, PointD target, boolean drawBends, PointD[] bendLocations) {
      this.source = source;
      this.target = target;
      this.drawBends = drawBends;
      this.bendLocations = bendLocations;
    }
  }
}