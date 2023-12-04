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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IColumn;
import com.yworks.yfiles.graph.IRow;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.styles.AbstractStripeStyle;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;

import complete.tableeditor.BorderedGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * Custom stripe style that alternates the visualizations for the leaf nodes and uses a different style for all parent
 * stripes.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class AlternatingLeafStripeStyle extends AbstractStripeStyle {
  private StripeDescriptor evenLeafDescriptor;

  /**
   * Visualization for all leaf stripes that have an even index.
   * @return The EvenLeafDescriptor.
   * @see #setEvenLeafDescriptor(StripeDescriptor)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final StripeDescriptor getEvenLeafDescriptor() {
    return this.evenLeafDescriptor;
  }

  /**
   * Visualization for all leaf stripes that have an even index.
   * @param value The EvenLeafDescriptor to set.
   * @see #getEvenLeafDescriptor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final void setEvenLeafDescriptor( StripeDescriptor value ) {
    this.evenLeafDescriptor = value;
  }

  private StripeDescriptor parentDescriptor;

  /**
   * Visualization for all stripes that are not leafs.
   * @return The ParentDescriptor.
   * @see #setParentDescriptor(StripeDescriptor)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final StripeDescriptor getParentDescriptor() {
    return this.parentDescriptor;
  }

  /**
   * Visualization for all stripes that are not leafs.
   * @param value The ParentDescriptor to set.
   * @see #getParentDescriptor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final void setParentDescriptor( StripeDescriptor value ) {
    this.parentDescriptor = value;
  }

  private StripeDescriptor oddLeafDescriptor;

  /**
   * Visualization for all leaf stripes that have an odd index.
   * @return The OddLeafDescriptor.
   * @see #setOddLeafDescriptor(StripeDescriptor)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final StripeDescriptor getOddLeafDescriptor() {
    return this.oddLeafDescriptor;
  }

  /**
   * Visualization for all leaf stripes that have an odd index.
   * @param value The OddLeafDescriptor to set.
   * @see #getOddLeafDescriptor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final void setOddLeafDescriptor( StripeDescriptor value ) {
    this.oddLeafDescriptor = value;
  }


  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Node createVisual( IRenderContext context, IStripe stripe ) {
    RectD layout = stripe.getLayout().toRectD();
    VisualGroup cc = new VisualGroup();
    InsetsD stripeInsets;

    StripeDescriptor descriptor;
    //Depending on the stripe type, we need to consider horizontal or vertical insets
    if (stripe instanceof IColumn) {
      IColumn col = (IColumn)stripe;
      InsetsD actualInsets = col.getActualInsets();
      stripeInsets = InsetsD.fromLTRB(0, actualInsets.top, 0, actualInsets.bottom);
    } else {
      IRow row = (IRow)stripe;
      InsetsD actualInsets = row.getActualInsets();
      stripeInsets = InsetsD.fromLTRB(actualInsets.left, 0, actualInsets.right, 0);
    }

    InsetsD actualBorderThickness;

    if (stripe.getChildStripes().iterator().hasNext()) {
      //Parent stripe - use the parent descriptor
      descriptor = getParentDescriptor();
      actualBorderThickness = descriptor.getBorderThickness();
    } else {
      int index;
      if (stripe instanceof IColumn) {
        //Get all leaf columns
        index = getIndex(stripe, stripe.getTable().getRootColumn().getLeaves());
        descriptor = index % 2 == 0 ? getEvenLeafDescriptor() : getOddLeafDescriptor();
        actualBorderThickness = descriptor.getBorderThickness();
      } else {
        index = getIndex(stripe, stripe.getTable().getRootRow().getLeaves());
        descriptor = index % 2 == 0 ? getEvenLeafDescriptor() : getOddLeafDescriptor();
        actualBorderThickness = descriptor.getBorderThickness();
      }
    }

    BorderedGroup innerArea = new BorderedGroup(layout.getWidth(), layout.getHeight());
    innerArea.setBackgroundPaint(descriptor.getBackgroundPaint());
    innerArea.setBorderPaint(descriptor.getInsetPaint());
    innerArea.setThickness(stripeInsets);

    BorderedGroup outerBorder = new BorderedGroup(layout.getWidth(), layout.getHeight());
    outerBorder.setBackgroundPaint(Color.TRANSPARENT);
    outerBorder.setBorderPaint(descriptor.getBorderPaint());
    outerBorder.setThickness(actualBorderThickness);

    cc.add(innerArea);
    cc.add(outerBorder);
    cc.setLayoutX(layout.getX());
    cc.setLayoutY(layout.getY());
    cc.setUserData(createRenderDataCache(context, descriptor, stripe, stripeInsets));
    return cc;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Node updateVisual( IRenderContext context, Node oldVisual, IStripe stripe ) {
    RectD layout = stripe.getLayout().toRectD();
    InsetsD stripeInsets;
    //Check if values have changed - then update everything
    StripeDescriptor descriptor;
    if (stripe instanceof IColumn) {
      IColumn col = (IColumn)stripe;
      InsetsD actualInsets = col.getActualInsets();
      stripeInsets = InsetsD.fromLTRB(0, actualInsets.top, 0, actualInsets.bottom);
    } else {
      IRow row = (IRow)stripe;
      InsetsD actualInsets = row.getActualInsets();
      stripeInsets = InsetsD.fromLTRB(actualInsets.left, 0, actualInsets.right, 0);
    }

    InsetsD actualBorderThickness;

    if (stripe.getChildStripes().iterator().hasNext()) {
      descriptor = getParentDescriptor();
      actualBorderThickness = descriptor.getBorderThickness();
    } else {
      int index;
      if (stripe instanceof IColumn) {
        //Get all leaf columns
        index = getIndex(stripe, stripe.getTable().getRootColumn().getLeaves());
        descriptor = index % 2 == 0 ? getEvenLeafDescriptor() : getOddLeafDescriptor();
        actualBorderThickness = descriptor.getBorderThickness();
      } else {
        index = getIndex(stripe, stripe.getTable().getRootRow().getLeaves());
        descriptor = index % 2 == 0 ? getEvenLeafDescriptor() : getOddLeafDescriptor();
        actualBorderThickness = descriptor.getBorderThickness();
      }
    }

    // get the data with wich the oldvisual was created
    RenderDataCache oldCache = (RenderDataCache) oldVisual.getUserData();
    // get the data for the new visual
    RenderDataCache newCache = createRenderDataCache(context, descriptor, stripe, stripeInsets);

    // check if something changed except for the location of the node
    if (!newCache.equals(oldCache)) {
      // something changed - just re-render the visual
      return createVisual(context, stripe);
    }

    if (oldVisual instanceof VisualGroup) {
      VisualGroup container = (VisualGroup) oldVisual;
      if (container.getNullableChildren().size() == 2) {
        Node child1 = container.getNullableChildren().get(0);
        Node child2 = container.getNullableChildren().get(1);
        if (child1 instanceof BorderedGroup && child2 instanceof BorderedGroup) {
          BorderedGroup innerArea = (BorderedGroup) child1;
          innerArea.setWidth(layout.getWidth());
          innerArea.setHeight(layout.getHeight());
          innerArea.setThickness(stripeInsets);

          BorderedGroup outerBorder = (BorderedGroup) child2;
          outerBorder.setWidth(layout.getWidth());
          outerBorder.setHeight(layout.getHeight());

          container.setLayoutX(layout.getX());
          container.setLayoutY(layout.getY());
          return container;
        }
      }
    }
    // the given node didn't match the one created before, cannot update it.
    return createVisual(context, stripe);
  }

  private static int getIndex(IStripe row, Iterable<IStripe> leafs) {
    int index;
    index = -1;
    int i = 0;
    for(IStripe leaf: leafs) {
      if(leaf == row) {
        index = i;
        break;
      }
      ++i;
    }
    return index;
  }


  /**
   * Helper class to cache rendering related data.
   */
  private static final class RenderDataCache {
    private final StripeDescriptor descriptor;

    private final InsetsD insets;

    private final IStripe stripe;

    public final StripeDescriptor getDescriptor() {
      return descriptor;
    }

    public final InsetsD getInsets() {
      return insets;
    }

    public final IStripe getStripe() {
      return stripe;
    }

    public RenderDataCache( StripeDescriptor descriptor, IStripe stripe, InsetsD insets ) {
      this.descriptor = descriptor;
      this.stripe = stripe;
      this.insets = insets;
    }

    public final boolean equals( RenderDataCache other ) {
      return other.getDescriptor().equals(getDescriptor()) && InsetsD.equals(other.getInsets(), getInsets()) && other.getStripe() == getStripe();
    }

    @Override
    public boolean equals( Object obj ) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof RenderDataCache)) {
        return false;
      }
      return equals((RenderDataCache)obj);
    }

  }

  private static RenderDataCache createRenderDataCache( IRenderContext context, StripeDescriptor descriptor, IStripe stripe, InsetsD insets ) {
    return new RenderDataCache(descriptor, stripe, insets);
  }

}
