/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package integration.swt;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import javafx.scene.input.DragEvent;

/**
 * Handles drop events for node templates.
 * This mode assumes that drag and drop operations transfer name of an
 * {@link integration.swt.NodeTemplate} constant as plain text that
 * may be used for retrieving a INode instance.
 */
class SwtNodeDropInputMode extends NodeDropInputMode {
  // holds the name of the NodeTemplate that is currently dragged
  private Object dragData;

  /**
   * Sets the name of the NodeTemplate that is currently dragged.
   */
  void setDragData(Object dragData) {
    this.dragData = dragData;
  }

  /**
   * Accepts drag events if plain text is transferred that identifies
   * a node template.
   */
  @Override
  protected boolean acceptDrag(DragEvent e) {
    CanvasControl canvasControl = getInputModeContext().getCanvasControl();
    if (!isEnabled() || canvasControl == null) {
      return false;
    }
    return e.getDragboard().hasString() && getDraggedItem() != null;
  }

  /**
   * Retrieves the node template that is identified by the currently
   * transferred text.
   * @return an INode instance that determines the visual representation
   * of the new node created on drop or <code>null</code> if there is no
   * INode instance corresponding to the currently transferred text.
   */
  @Override
  protected INode getDraggedItem() {
    // get the text that has been transferred
    if (dragData instanceof String) {
      // determine the enum constant of NodeTemplate the text is representing
      NodeTemplate template = NodeTemplate.valueOf((String) dragData);
      // returns the node that applies the template
      return template.node();
    } else {
      return null;
    }
  }
}
