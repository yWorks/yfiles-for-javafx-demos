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
package style.templatestyle;

import com.yworks.yfiles.graph.styles.NodeTemplate;
import com.yworks.yfiles.graph.INode;
import javafx.scene.control.Label;

/**
 * The controller class for the visual representation of customers. It is instantiated by an
 * {@link javafx.fxml.FXMLLoader} while parsing and creating the {@code Customer.fxml} file. The loader calls
 * {@link #initialize()} on startup, where the main wiring up of the UI is done.
 * <p>
 *   This controller holds a reference to the {@link NodeTemplate} top element of the {@code Customer.fxml}, which in
 *   turn holds a reference to the {@link INode}, i.e. the item to display. So, the item's current state can be used for
 *   visualizing selection for example.
 * </p>
 *
 * @see javafx.fxml.FXMLLoader
 * @see <code>Customer.fxml</code>
 */
public class CustomerController {
  /** The top element of {@code Customer.fxml} that amongst others holds a reference to the associated item. */
  public NodeTemplate customerNode;
  /** The label element that displays the customer's name. */
  public Label nameLabel;

  /**
   * Initializes the controller. The visualization of the current item is updated according to its current selection
   * state.
   *
   * <p>
   *   This is called when the FXMLLoader instantiates the scene graph. At the time this method is called, all nodes in
   *   the scene graph is available. Most importantly, the GraphControl instance is initialized.
   * </p>
   */
  public void initialize() {
    // update style classes to visualize the selection state
    // because after removing a style class the resulting style isn't reliably updated, another style class is added
    // that overwrites the removed style class
    customerNode.itemSelectedProperty().addListener((observableValue, oldSelected, newSelected) -> {
      if (oldSelected != newSelected) {
        if (newSelected) {
          customerNode.getStyleClass().remove("unfocusedNode");
          customerNode.getStyleClass().add("focusedNode");
          nameLabel.getStyleClass().remove("unfocusedLabelText");
          nameLabel.getStyleClass().add("focusedLabelText");
        } else {
          customerNode.getStyleClass().remove("focusedNode");
          customerNode.getStyleClass().add("unfocusedNode");
          nameLabel.getStyleClass().remove("focusedLabelText");
          nameLabel.getStyleClass().add("unfocusedLabelText");
        }
        customerNode.getCanvas().invalidate();
      }
    });
  }
}
