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
package complete.orgchart;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;

/**
 * The style of nodes when the graph is zoomed in.
 * All data of the employee is displayed together with a status indicator and an icon.
 * <p>
 * Note that the states could be declared in fxml as well. Due to the bad performance of the FXMLLoader we decided
 * against this approach although it is more verbose this way.
 * </p>
 */
class EmployeeDetail extends StackPane {

  EmployeeDetail(final ContentControl contentControl) {
    getStyleClass().add("detail");

    GridPane gridPane = new GridPane();
    {
      gridPane.setHgap(10);
      gridPane.setVgap(5);
      gridPane.setPadding(new Insets(5));
      ColumnConstraints columnConstraintsNever = new ColumnConstraints();
      columnConstraintsNever.setHgrow(Priority.NEVER);
      ColumnConstraints columnConstraintsSomeTimes = new ColumnConstraints();
      columnConstraintsSomeTimes.setHgrow(Priority.SOMETIMES);
      gridPane.getColumnConstraints().addAll(columnConstraintsNever, columnConstraintsSomeTimes);
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setMinHeight(0);
      rowConstraints.setVgrow(Priority.SOMETIMES);
      gridPane.getRowConstraints().addAll(rowConstraints, rowConstraints, rowConstraints, rowConstraints, rowConstraints);

      // Left: an icon for the employee
      final ImageView icon = new ImageView();
      icon.imageProperty().bind(contentControl.imageProperty());
      {
        GridPane.setColumnIndex(icon, 0);
        GridPane.setRowSpan(icon, 5);
        icon.setFitHeight(80);
        icon.setPreserveRatio(true);
        gridPane.getChildren().add(icon);
      }

      // The status indicator: a circle with a colored and white border.
      final StackPane innerPane = new StackPane();
      {
        GridPane.setColumnIndex(innerPane, 2);
        GridPane.setRowSpan(innerPane, 2);
        GridPane.setHalignment(innerPane, HPos.RIGHT);

        Ellipse outerStatusColorEllipse = new Ellipse(12, 12);
        outerStatusColorEllipse.setStrokeWidth(0);
        outerStatusColorEllipse.fillProperty().bind(contentControl.statusColorProperty());
        StackPane.setAlignment(outerStatusColorEllipse, Pos.TOP_RIGHT);
        StackPane.setMargin(outerStatusColorEllipse, new Insets(3, 3, 0, 0));
        innerPane.getChildren().add(outerStatusColorEllipse);

        Ellipse whiteEllipse = new Ellipse(9, 9);
        whiteEllipse.setStrokeWidth(0);
        whiteEllipse.setFill(Color.WHITE);
        StackPane.setAlignment(whiteEllipse, Pos.TOP_RIGHT);
        StackPane.setMargin(whiteEllipse, new Insets(6, 6, 0, 0));
        innerPane.getChildren().add(whiteEllipse);

        Ellipse innerStatusColorEllipse = new Ellipse(6, 6);
        innerStatusColorEllipse.setStrokeWidth(0);
        innerStatusColorEllipse.fillProperty().bind(contentControl.statusColorProperty());

        StackPane.setAlignment(innerStatusColorEllipse, Pos.TOP_RIGHT);
        StackPane.setMargin(innerStatusColorEllipse, new Insets(9, 9, 0, 0));
        innerPane.getChildren().add(innerStatusColorEllipse);
        gridPane.getChildren().add(innerPane);
      }

      // The actual data of the employee.
      final Label displayNameLabel = new Label();
      displayNameLabel.setFont(new Font(12));
      displayNameLabel.textProperty().bind(contentControl.fullDisplayNameProperty());
      GridPane.setColumnIndex(displayNameLabel, 1);
      GridPane.setRowIndex(displayNameLabel, 0);
      gridPane.getChildren().add(displayNameLabel);

      final Label positionLabel = new Label();
      GridPane.setColumnIndex(positionLabel, 1);
      GridPane.setRowIndex(positionLabel, 1);
      positionLabel.setFont(new Font(8));
      gridPane.getChildren().add(positionLabel);

      final Label emailLabel = new Label();
      emailLabel.setFont(new Font(12));
      GridPane.setColumnIndex(emailLabel, 1);
      GridPane.setRowIndex(emailLabel, 2);
      gridPane.getChildren().add(emailLabel);

      final Label phoneLabel = new Label();
      phoneLabel.setFont(new Font(12));
      GridPane.setColumnIndex(phoneLabel, 1);
      GridPane.setRowIndex(phoneLabel, 3);
      gridPane.getChildren().add(phoneLabel);

      final Label faxLabel = new Label();
      faxLabel.setFont(new Font(12));
      GridPane.setColumnIndex(faxLabel, 1);
      GridPane.setRowIndex(faxLabel, 4);
      gridPane.getChildren().add(faxLabel);

      // add binding to react on changes in the given ContentControl
      contentControl.employeeProperty().addListener((observableValue, employee, newEmployee) -> {
        if (newEmployee != null) {
          positionLabel.textProperty().bind(newEmployee.positionProperty());
          emailLabel.textProperty().bind(newEmployee.emailProperty());
          phoneLabel.textProperty().bind(newEmployee.phoneProperty());
          faxLabel.textProperty().bind(newEmployee.faxProperty());
        }
      });

      if (contentControl.getEmployee() != null) {
        positionLabel.textProperty().bind(contentControl.getEmployee().positionProperty());
        emailLabel.textProperty().bind(contentControl.getEmployee().emailProperty());
        phoneLabel.textProperty().bind(contentControl.getEmployee().phoneProperty());
        faxLabel.textProperty().bind(contentControl.getEmployee().faxProperty());
      }
    }

    getChildren().add(gridPane);
  }
}
