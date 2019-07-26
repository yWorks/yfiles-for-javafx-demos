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
package complete.orgchart;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * The tooltip for the closely zoomed graph.
 */
public final class DetailTooltip extends GridPane {

  private static final DetailTooltip instance = new DetailTooltip();

  public static DetailTooltip getInstance() {
    return instance;
  }

  private ObjectProperty<Employee> employee = new SimpleObjectProperty<>(this, "employee", null);
  public ObjectProperty<Employee> employeeProperty(){ return employee; }
  public Employee getEmployee(){ return employee.get(); }
  public void setEmployee(Employee employee){ this.employee.set(employee); }

  private StringProperty fullDisplayName = new SimpleStringProperty(this, "fullDisplayName", "");
  public final StringProperty fullDisplayNameProperty(){return fullDisplayName;}
  public final String getFullDisplayName() {return fullDisplayName.get(); }
  public final void setFullDisplayName(String value) { fullDisplayName.set(value); }

  private ObjectProperty<Color> statusColor = new SimpleObjectProperty<>(this, "statusColor", Color.GREEN);
  public ObjectProperty<Color> statusColorProperty(){ return statusColor; }
  public Color getStatusColor(){ return statusColor.get(); }
  public void setStatusColor(Color color){ statusColor.set(color); }

  private DetailTooltip() {
    setHgap(10);
    setVgap(5);
    setPadding(new Insets(5));

    // Left side: description
    // The displayed text should not be cropped by the pane, so the elements are texts rather than labels.
    final Text nameDesc = createCellText("Name", 0, 0);
    final Text positionDesc = createCellText("Position", 0, 1);
    final Text phoneDesc = createCellText("Phone", 0, 2);
    final Text faxDesc = createCellText("Fax", 0, 3);
    final Text emailDesc = createCellText("Email", 0, 4);
    final Text businessUnitDesc = createCellText("Business Unit", 0, 5);
    final Text statusDesc = createCellText("Status", 0, 6);
    getChildren().addAll(nameDesc, positionDesc, phoneDesc, faxDesc, emailDesc, businessUnitDesc, statusDesc);

    // Right side: data. These get updated when the current item changes.
    final Text name = createCellText("", 1, 0);
    name.textProperty().bind(fullDisplayNameProperty());
    final Text position = createCellText("Position", 1, 1);
    final Text phone = createCellText("Phone", 1, 2);
    final Text fax = createCellText("Fax", 1, 3);
    final Text email = createCellText("Email", 1, 4);
    final Text businessUnit = createCellText("Business Unit", 1, 5);

    final HBox statusBox = new HBox(5);
    statusBox.setAlignment(Pos.CENTER_LEFT);
    GridPane.setColumnIndex(statusBox, 1);
    GridPane.setRowIndex(statusBox, 6);
    final Ellipse statusEllipse = new Ellipse(4, 4);
    statusEllipse.fillProperty().bind(statusColorProperty());
    final Text statusLbl = createText("Status");
    statusBox.getChildren().addAll(statusEllipse, statusLbl);

    getChildren().addAll(name, position, phone, fax, email, businessUnit, statusBox);

    // react on changes of the set employee
    final ChangeListener<EmployeeStatus> statusChangeListener = (observableValue, employeeStatus, newEmployeeStatus)
        -> statusLbl.setText(newEmployeeStatus.name());

    employeeProperty().addListener((observableValue, employee, newEmployee) -> {
      if (newEmployee != null) {
        position.textProperty().bind(newEmployee.positionProperty());
        phone.textProperty().bind(newEmployee.phoneProperty());
        fax.textProperty().bind(newEmployee.faxProperty());
        email.textProperty().bind(newEmployee.emailProperty());
        businessUnit.textProperty().bind(newEmployee.businessUnitProperty());
        newEmployee.statusProperty().addListener(statusChangeListener);
        if (newEmployee.getStatus() != null) {
          statusLbl.setText(newEmployee.getStatus().name());
        }
      }
    });

    if (getEmployee() != null) {
      Employee emp = getEmployee();
      position.setText(emp.getPosition());
      phone.setText(emp.getPhone());
      fax.setText(emp.getFax());
      email.setText(emp.getEmail());
      businessUnit.setText(emp.getBusinessUnit());
      emp.statusProperty().addListener(statusChangeListener);
      if (emp.getStatus() != null) {
        statusLbl.setText(emp.getStatus().name());
      }
    }
  }

  private static Text createCellText(String textToShow, int column, int row) {
    Text cell = createText(textToShow);
    GridPane.setColumnIndex(cell, column);
    GridPane.setRowIndex(cell, row);
    return cell;
  }

  private static Text createText(String textToShow) {
    Text text = new Text(textToShow);
    text.setFont(Font.font(12));
    return text;
  }
}
