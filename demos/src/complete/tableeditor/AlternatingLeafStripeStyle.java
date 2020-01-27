/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.tableeditor;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IColumn;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.styles.AbstractStripeStyle;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.stream.Collectors;

/**
 * Custom stripe style that alternates the visualizations for the leaf nodes and uses a different style for all parent stripes.
 */
public class AlternatingLeafStripeStyle extends AbstractStripeStyle {
  // visualization for all leaf stripes that have an even index
  private StripeDescriptor evenLeafDescriptor;
  // visualization for all leaf stripes that have an odd index
  private StripeDescriptor oddLeafDescriptor;
  // visualization for all stripes that are not leaves
  private StripeDescriptor parentDescriptor;

  /**
   * Initializes a new instance of the {@link AlternatingLeafStripeStyle} class.
   */
  public AlternatingLeafStripeStyle() {
  }

  /**
   * Initializes a new <code>AlternatingLeafStripeStyle</code> instance with the given descriptor for even leaf, odd
   * leaf and parent descriptors.
   */
  public AlternatingLeafStripeStyle(StripeDescriptor descriptor) {
    this(descriptor, descriptor, descriptor);
  }

  /**
   * Returns the visualization for all leaf stripes that have an even index.
   */
  public StripeDescriptor getEvenLeafDescriptor() {
    return evenLeafDescriptor;
  }

  /**
   * Sets the visualization for all leaf stripes that have an even index.
   */
  public void setEvenLeafDescriptor(StripeDescriptor descriptor) {
    this.evenLeafDescriptor = descriptor;
  }

  /**
   * Returns the visualization for all leaf stripes that have an odd index.
   */
  public StripeDescriptor getOddLeafDescriptor() {
    return oddLeafDescriptor;
  }

  /**
   * Sets the visualization for all leaf stripes that have an odd index.
   */
  public void setOddLeafDescriptor(StripeDescriptor descriptor) {
    this.oddLeafDescriptor = descriptor;
  }

  /**
   * Returns the visualization for all stripes that are not leaves.
   */
  public StripeDescriptor getParentDescriptor() {
    return parentDescriptor;
  }

  /**
   * Sets the visualization for all stripes that are not leaves.
   */
  public void setParentDescriptor(StripeDescriptor descriptor) {
    this.parentDescriptor = descriptor;
  }

  /**
   * Initializes a new <code>AlternatingLeafStripeStyle</code> instance with the given even leaf, odd leaf and parent
   * descriptors.
   */
  public AlternatingLeafStripeStyle(StripeDescriptor evenLeafDescriptor, StripeDescriptor oddLeafDescriptor, StripeDescriptor parentDescriptor) {
    this.evenLeafDescriptor = evenLeafDescriptor;
    this.oddLeafDescriptor = oddLeafDescriptor;
    this.parentDescriptor = parentDescriptor;
  }

  @Override
  protected Node createVisual(IRenderContext context, IStripe stripe) {
    IRectangle layout = stripe.getLayout();

    VisualGroup container = new VisualGroup();

    InsetsD stripeInsets = getStripeInsets(stripe);
    StripeDescriptor descriptor = getStripeDescriptor(stripe);
    InsetsD actualBorderThickness = descriptor.getBorderThickness();

    BorderedGroup innerArea = new BorderedGroup(layout.getWidth(), layout.getHeight());
    innerArea.setBackgroundPaint(descriptor.getBackgroundPaint());
    innerArea.setBorderPaint(descriptor.getInsetPaint());
    innerArea.setThickness(stripeInsets);

    BorderedGroup outerBorder = new BorderedGroup(layout.getWidth(), layout.getHeight());
    outerBorder.setBackgroundPaint(Color.TRANSPARENT);
    outerBorder.setBorderPaint(descriptor.getBorderPaint());
    outerBorder.setThickness(actualBorderThickness);

    container.add(innerArea);
    container.add(outerBorder);

    container.setLayoutX(layout.getX());
    container.setLayoutY(layout.getY());

    return container;
  }

  @Override
  protected Node updateVisual(IRenderContext renderContext, Node oldVisual, IStripe stripe) {
    if (oldVisual instanceof VisualGroup) {
      VisualGroup container = (VisualGroup) oldVisual;
      if (container.getNullableChildren().size() == 2) {
        Node child1 = container.getNullableChildren().get(0);
        Node child2 = container.getNullableChildren().get(1);
        if (child1 instanceof BorderedGroup && child2 instanceof BorderedGroup) {
          // update the old node's properties with the current values
          IRectangle layout = stripe.getLayout();

          //Depending on the stripe type, we need to consider horizontal or vertical insets
          InsetsD stripeInsets = getStripeInsets(stripe);

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
    return createVisual(renderContext, stripe);
  }

  /**
   * Returns the {@link complete.tableeditor.StripeDescriptor} for the given stripe.
   */
  private StripeDescriptor getStripeDescriptor(IStripe stripe) {
    if (stripe.getChildStripes().iterator().hasNext()) {
      // parent stripe - use the parent descriptor
      return parentDescriptor;
    } else {
      // get all leaf stripes
      IEnumerable<IStripe> leaves = stripe instanceof IColumn ?
          stripe.getTable().getRootColumn().getLeaves() :
          stripe.getTable().getRootRow().getLeaves();
      // determine the index of the given stripe
      int index = leaves.stream().collect(Collectors.toList()).indexOf(stripe);
      // use the descriptor depending on the index
      return index % 2 == 0 ? evenLeafDescriptor : oddLeafDescriptor;
    }
  }

  /**
   * Returns the insets of the given stripe. Depending on the stripe's type, we need to consider horizontal or vertical
   * insets.
   */
  private static InsetsD getStripeInsets(IStripe stripe) {
    if (stripe instanceof IColumn) {
      return InsetsD.fromLTRB(0, stripe.getActualInsets().getTop(), 0, stripe.getActualInsets().getBottom());
    } else {
      return InsetsD.fromLTRB(stripe.getActualInsets().getLeft(), 0, stripe.getActualInsets().getRight(), 0);
    }
  }
}
