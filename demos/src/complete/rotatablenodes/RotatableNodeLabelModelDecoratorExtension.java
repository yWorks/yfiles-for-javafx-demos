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
package complete.rotatablenodes;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.MarkupExtension;

/**
 * Markup extension that helps (de-)serializing see {@link RotatableNodeLabelModelDecorator}
 */
@GraphML(contentProperty = "Wrapped")
public class RotatableNodeLabelModelDecoratorExtension extends MarkupExtension {

  /**
   * Is the {@link RotatableNodeStyleDecorator#getAngle()} rotation of the label owner considered? Default true.
   */
  private boolean nodeRotationEnabled = true;

  public ILabelModel wrapped;


  @Override
  public Object provideValue(ILookup iLookup) {
    RotatableNodeLabelModelDecorator extension = new RotatableNodeLabelModelDecorator(wrapped);
    extension.setNodeRotationEnabled(nodeRotationEnabled);
    return extension;
  }

  /**
   * Returns if the rotation of the label owner is considered.
   */
  public boolean getNodeRotationEnabled() {
    return nodeRotationEnabled;
  }

  /**
   * Sets if the rotation of the label owner is considered.
   */
  void setNodeRotationEnabled(boolean nodeRotationEnabled) {
    this.nodeRotationEnabled = nodeRotationEnabled;
  }

  /**
   * Returns the wrapped label model.
   */
  public ILabelModel getWrapped() {
    return wrapped;
  }

  /**
   * Sets the wrapped label model.
   */
  public void setWrapped(ILabelModel wrapped) {
    this.wrapped = wrapped;
  }
}
