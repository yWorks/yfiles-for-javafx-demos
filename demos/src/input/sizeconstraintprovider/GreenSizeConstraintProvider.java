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
package input.sizeconstraintprovider;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;

/**
 * An {@link INodeSizeConstraintProvider} that returns the size of the
 * biggest label as minimum size. The maximum size is not limited.
 */
class GreenSizeConstraintProvider implements INodeSizeConstraintProvider {
  /**
   * Returns the label size to prevent the shrinking of nodes beyond their
   * label's size.
   */
  @Override
  public SizeD getMinimumSize(INode item) {
    // get the minimum sizes for all labels
    return item.getLabels().stream().map(label -> getLabelSize(item, label))
        // and take the maximum width and height
        .reduce(GreenSizeConstraintProvider::max)
            // or if there is no label, take a minimum size
        .orElse(new SizeD(1, 1));
  }

  private SizeD getLabelSize(INode item, ILabel label) {
    INodeSizeConstraintProvider labelProvider = label.lookup(INodeSizeConstraintProvider.class);
    if (labelProvider != null) {
      return labelProvider.getMinimumSize(item);
    }

    if (label.getLayoutParameter().getModel() instanceof InteriorLabelModel) {
      return label.getPreferredSize();
    }
    return new SizeD(1, 1);
  }

  /**
   * Returns the infinite size since the maximum size is not limited.
   */
  public SizeD getMaximumSize(INode item) {
    return SizeD.INFINITE;
  }

  /**
   * Returns an empty rectangle since this area is not constraint.
   */
  @Override
  public RectD getMinimumEnclosedArea(INode item) {
    return RectD.EMPTY;
  }

  private static SizeD max( SizeD s1, SizeD s2 ) {
    return new SizeD(Math.max(s1.width, s2.width), Math.max(s1.height, s2.height));
  }
}

