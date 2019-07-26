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
package complete.hierarchicgrouping;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;

/**
 * Uses the size constraints provided by the labels to further constrain the size of nodes.
 */
class LabelSizeConstraintProvider implements INodeSizeConstraintProvider {
  private static final int COLLAPSE_BUTTON_WIDTH = 14;
  private final INodeSizeConstraintProvider delegateProvider;

  /**
   * Initializes a new <code>LabelSizeConstraintProvider</code> instance.
   * @param delegateProvider the wrapped <code>ISizeConstraintProvider</code>
   */
  LabelSizeConstraintProvider(INodeSizeConstraintProvider delegateProvider) {
    this.delegateProvider = delegateProvider;
  }

  @Override
  public SizeD getMinimumSize(INode item) {
    SizeD result = delegateProvider != null ? delegateProvider.getMinimumSize(item) : SizeD.EMPTY;
    for (ILabel label : item.getLabels()) {
      INodeSizeConstraintProvider provider = label.getLayoutParameter().getModel().lookup(INodeSizeConstraintProvider.class);
      if (provider != null) {
        result = max(result, provider.getMinimumSize(item));
      }
    }
    // Respect the width of the collapse/expand button so that the label doesn't overlap it
    return new SizeD(result.getWidth() + COLLAPSE_BUTTON_WIDTH, result.getHeight());
  }

  @Override
  public SizeD getMaximumSize(INode item) {
    return delegateProvider != null ? delegateProvider.getMaximumSize(item) : SizeD.INFINITE;
  }

  @Override
  public RectD getMinimumEnclosedArea(INode item) {
    return delegateProvider != null ? delegateProvider.getMinimumEnclosedArea(item) : RectD.EMPTY;
  }

  private static SizeD max( SizeD s1, SizeD s2 ) {
    return new SizeD(Math.max(s1.width, s2.width), Math.max(s1.height, s2.height));
  }
}
