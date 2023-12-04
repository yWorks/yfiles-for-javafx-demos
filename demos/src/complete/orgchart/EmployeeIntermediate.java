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
package complete.orgchart;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * The style of nodes when the zoom level is intermediate. It displays the name
 * of the employee, his/her position and in the top right corner the rectangular status indicator
 * <p>
 * Note that the states could be declared in fxml as well. Due to the bad performance of the FXMLLoader we decided
 * against this approach although it is more verbose this way.
 * </p>
 */
class EmployeeIntermediate extends StackPane {

  EmployeeIntermediate(final ContentControl control) {
    getStyleClass().add("intermediate");
    setPadding(new Insets(5));

    // the status is indicated by a rectangle using the status color as fill color
    final Rectangle statusRect = new Rectangle(0, 0, 26, 26);
    {
      statusRect.setArcHeight(8);
      statusRect.setArcWidth(8);
      statusRect.fillProperty().bind(control.statusColorProperty());
      StackPane.setAlignment(statusRect, Pos.TOP_RIGHT);
      getChildren().add(statusRect);
    }

    // a short display name and the position are arranged in a VBox
    final VBox box = new VBox(8);
    {
      final Label displayNameLbl = new Label();
      {
        displayNameLbl.setFont(new Font(30));
        displayNameLbl.textProperty().bind(control.shortDisplayNameProperty());
        box.getChildren().add(displayNameLbl);
      }

      final Label positionLbl = new Label();
      {
        positionLbl.setFont(new Font(22));
        box.getChildren().add(positionLbl);
      }
      getChildren().add(box);

      // listen to changes of the ContentControl.employee property
      control.employeeProperty().addListener((observableValue, employee, newEmployee) -> {
        if (newEmployee != null) {
          positionLbl.textProperty().bind(newEmployee.positionProperty());
        }
      });
      if (control.getEmployee() != null) {
        positionLbl.textProperty().bind(control.getEmployee().positionProperty());
      }
    }
  }
}
