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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * An {@link IArrow} implementation using an {@link IIcon} for the visualization.
 */
class IconArrow implements IArrow, IVisualCreator, IBoundsProvider {
  // these variables hold the state for the flyweight pattern
  // they are populated in GetPaintable and used in the implementations of the IVisualCreator interface.
  private PointD anchor = new PointD();

  private PointD direction = new PointD();

  private final IIcon icon;


  public IconArrow( IIcon icon ) {
    this.icon = icon;
  }



  private double length;

  /**
   * Returns the length of the arrow, i.e. the distance from the arrow's tip to the position where the visual representation
   * of the edge's path should begin.
   * @return The Length.
   * @see #setLength(double)
   */
  public final double getLength() {
    return this.length;
  }

  /**
   * Returns the length of the arrow, i.e. the distance from the arrow's tip to the position where the visual representation
   * of the edge's path should begin.
   * @param value The Length to set.
   * @see #getLength()
   */
  public final void setLength( double value ) {
    this.length = value;
  }

  private double cropLength;

  /**
   * Gets the cropping length associated with this instance.
   * <p>
   * This value is used by
   * {@link com.yworks.yfiles.graph.styles.IEdgeStyle}s to let the edge appear to end shortly before its actual target.
   * </p>
   * @return The CropLength.
   * @see #setCropLength(double)
   */
  public final double getCropLength() {
    return this.cropLength;
  }

  /**
   * Sets the cropping length associated with this instance.
   * <p>
   * This value is used by
   * {@link com.yworks.yfiles.graph.styles.IEdgeStyle}s to let the edge appear to end shortly before its actual target.
   * </p>
   * @param value The CropLength to set.
   * @see #getCropLength()
   */
  public final void setCropLength( double value ) {
    this.cropLength = value;
  }

  private SizeD bounds = new SizeD();

  /**
   * Gets the bounds of the arrow icon.
   * @return The Bounds.
   * @see #setBounds(SizeD)
   */
  public final SizeD getBounds() {
    return this.bounds;
  }

  /**
   * Sets the bounds of the arrow icon.
   * @param value The Bounds to set.
   * @see #getBounds()
   */
  public final void setBounds( SizeD value ) {
    this.bounds = value;
  }



  /**
   * Gets an {@link IVisualCreator} implementation that will create the {@link Node} for this arrow at the given location
   * using the given direction for the given edge.
   * @param edge the edge this arrow belongs to
   * @param atSource whether this will be the source arrow
   * @param anchor the anchor point for the tip of the arrow
   * @param direction the direction the arrow is pointing in
   * @return Itself as a flyweight.
   */
  public final IVisualCreator getVisualCreator( IEdge edge, boolean atSource, PointD anchor, PointD direction ) {
    this.anchor = anchor;
    this.direction = direction;
    return this;
  }

  /**
   * Gets an {@link IBoundsProvider} implementation that can yield this arrow's bounds if painted at the given location using
   * the given direction for the given edge.
   * @param edge the edge this arrow belongs to
   * @param atSource whether this will be the source arrow
   * @param anchor the anchor point for the tip of the arrow
   * @param directionVector the direction the arrow is pointing in
   * @return an implementation of the {@link IBoundsProvider} interface that can subsequently be used to query the bounds. Clients
   * will always call this method before using the implementation and may not cache the instance returned. This allows for
   * applying the flyweight design pattern to implementations.
   */
  public final IBoundsProvider getBoundsProvider( IEdge edge, boolean atSource, PointD anchor, PointD directionVector ) {
    this.anchor = anchor;
    this.direction = directionVector;
    return this;
  }



  /**
   * This method is called by the framework to create a {@link Node} that will be included into the {@link IRenderContext}.
   * @param context The context that describes where the visual will be used.
   * @return The arrow visual to include in the canvas object visual tree./>.
   * @see #updateVisual(IRenderContext, Node)
   */
  public final Node createVisual( IRenderContext context ) {
    icon.setBounds(new RectD(-getBounds().width, -getBounds().height / 2, getBounds().width, getBounds().height));
    VisualGroup visualGroup = new VisualGroup();
    visualGroup.add(icon.createVisual(context));
    // Rotate arrow and move it to correct position
    visualGroup.getTransforms().clear();
    visualGroup.getTransforms().add(new Affine(direction.x, -direction.y, anchor.x, direction.y, direction.x, anchor.y));
    return visualGroup;
  }

  /**
   * This method updates or replaces a previously created {@link Node} for inclusion in the {@link IRenderContext}.
   * <p>
   * The {@link com.yworks.yfiles.view.CanvasControl} uses this method to give implementations a chance to update an existing
   * Visual that has previously been created by the same instance during a call to
   * {@link IVisualCreator#createVisual(IRenderContext)}. Implementation may update the {@code oldVisual} and return that
   * same reference, or create a new visual and return the new instance or {@code null}.
   * </p>
   * @param context The context that describes where the visual will be used in.
   * @param oldVisual The visual instance that had been returned the last time the {@link IVisualCreator#createVisual(IRenderContext)} method
   * was called on this instance.
   * @return {@code oldVisual}, if this instance modified the visual, or a new visual that should replace the existing one in the
   * canvas object visual tree.
   * @see IVisualCreator#createVisual(IRenderContext)
   * @see com.yworks.yfiles.view.ICanvasObjectDescriptor
   * @see com.yworks.yfiles.view.CanvasControl
   */
  public final Node updateVisual( IRenderContext context, Node oldVisual ) {
    VisualGroup p = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    if (p != null) {
      p.getTransforms().clear();
      p.getTransforms().add(new Affine(direction.x, -direction.y, anchor.x, direction.y, direction.x, anchor.y));
      return p;
    }
    return ((IVisualCreator)this).createVisual(context);
  }



  /**
   * Returns the bounds of the arrow for the current flyweight configuration.
   */
  public final RectD getBounds( ICanvasContext context ) {
    return new RectD(anchor.x - getBounds().width, anchor.y - getBounds().height / 2, getBounds().width, getBounds().height);
  }

}
