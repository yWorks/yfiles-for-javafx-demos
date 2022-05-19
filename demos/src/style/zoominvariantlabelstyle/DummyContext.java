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
package style.zoominvariantlabelstyle;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.IRenderContext;
import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 * A render context that simulates the given zoom factor for rendering.
 */
class DummyContext implements IRenderContext {
  private final IRenderContext innerContext;
  private final double zoom;
  private final Transform transform;
  private final Transform viewTransform;
  private final Transform intermediateTransform;
  private final Transform projection;

  /**
   * Initializes a new {@code DummyContext} instance for the given zoom factor.
   */
  DummyContext(IRenderContext innerContext, double zoom, Affine inverseTransform) {
    this.innerContext = innerContext;
    this.zoom = zoom;

    this.transform = inverseTransform;

    // multiply all necessary transforms with the given inverse transform to nullify the outer transform
    this.viewTransform = this.transformMatrix(this.innerContext.getViewTransform());
    this.intermediateTransform = this.transformMatrix(this.innerContext.getIntermediateTransform());
    this.projection = this.transformMatrix(this.innerContext.getProjection());
  }

  @Override
  public CanvasControl getCanvasControl() {
    return innerContext.getCanvasControl();
  }

  @Override
  public RectD getClip() {
    return this.innerContext.getClip();
  }

  @Override
  public Transform getProjection() {
    return this.projection;
  }

  @Override
  public Transform getIntermediateTransform() {
    return this.intermediateTransform;
  }

  @Override
  public Transform getViewTransform() {
    return viewTransform;
  }

  @Override
  public PointD toViewCoordinates(PointD worldPoint) {
    Point2D src = worldPoint.toPoint2D();
    Point2D tgt = transform.transform(src);
    return PointD.fromPoint2D(tgt);
  }

  @Override
  public PointD worldToIntermediateCoordinates(PointD worldPoint) {
    Point2D src = worldPoint.toPoint2D();
    Point2D tgt = intermediateTransform.transform(src);
    return PointD.fromPoint2D(tgt);
  }

  @Override
  public PointD intermediateToViewCoordinates(PointD intermediatePoint) {
    Point2D src = intermediatePoint.toPoint2D();
    Point2D tgt = projection.transform(src);
    return PointD.fromPoint2D(tgt);
  }

  @Override
  public double getZoom() {
    return zoom;
  }

  @Override
  public double getHitTestRadius() {
    return innerContext.getHitTestRadius();
  }

  @Override
  public <TLookup> TLookup lookup(Class<TLookup> type) {
    return innerContext.lookup(type);
  }

  /**
   * Multiplies the given transform with the inverse transform of the invariant label style.
   *
   * @param baseTransform The transform to concatenate with this.transform
   */
  private Transform transformMatrix(Transform baseTransform) {
    Transform transformed = (Transform) baseTransform.clone();
    transformed.createConcatenation(this.transform);
    return transformed;
  }
}
