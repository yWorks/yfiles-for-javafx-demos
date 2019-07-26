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
package complete.collapse;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.NodeTemplate;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToggleButton;

/**
 * Controller for an inner node. Manages toggling the visibility of this node's children.
 */
public class InnerNodeController {
  public NodeTemplate root;
  public ToggleButton button; 

  public void initialize() {
    // Update buttons selected state according to the current style tag.
    root.styleTagProperty().addListener(new ChangeListener<Object>() {
      @Override
      public void changed(ObservableValue<?> observableValue, Object oldTag, Object newTag) {
        final StyleTag styleTag = (StyleTag) newTag;
        button.setSelected(styleTag.getCollapsed());

        // Instead of using the onAction-method of the button we toggle the collapsed state of the node when the
        // selected state of the toggle button changes.
        button.selectedProperty().addListener((observableValue1, oldValue, newValue) -> {
          // Toggles the visibility of the children of to current graph element.
          // Also the node's style tag will change and via a binding in InnerNodeStyle.fxml the graphic gets adjusted.
          INode node = root.getItem();
          CanvasControl canvas = root.getCanvas();
          CollapsibleTreeDemo.TOGGLE_CHILDREN.execute(node, canvas);
          if (canvas instanceof GraphControl) {
            ((GraphControl) canvas).setCurrentItem(node);
            canvas.requestFocus();
          }
        });
        root.styleTagProperty().removeListener(this);
      }
    });
  }
}
