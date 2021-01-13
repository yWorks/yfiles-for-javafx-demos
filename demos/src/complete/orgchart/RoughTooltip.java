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
package complete.orgchart;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * The tooltip for overview and intermediate zoom levels.
 */
public class RoughTooltip extends VBox {

  private static final RoughTooltip instance = new RoughTooltip();

  public static RoughTooltip getInstance() {
    return instance;
  }

  private ObjectProperty<Employee> employee = new SimpleObjectProperty<>(this, "employee", null);
  private Text statusLbl;

  public ObjectProperty<Employee> employeeProperty(){ return employee; }
  public Employee getEmployee(){ return employee.get(); }
  public void setEmployee(Employee employee){ this.employee.set(employee); }

  private StringProperty shortDisplayName = new SimpleStringProperty(this, "shortDisplayName", "");
  public final StringProperty shortDisplayNameProperty(){return shortDisplayName;}
  public final String getShortDisplayName() {return shortDisplayName.get(); }
  public final void setShortDisplayName(String value) { shortDisplayName.set(value); }

  private RoughTooltip() {

    final Text nameLbl = createText("");
    final Text positionLbl = createText("");

    final HBox box = new HBox();
    final Text statusTextLbl = createText("Status: ");
    statusLbl = createText("");
    box.getChildren().addAll(statusTextLbl, statusLbl);

    getChildren().addAll(nameLbl, positionLbl, box);

    nameLbl.textProperty().bind(shortDisplayNameProperty());
    employeeProperty().addListener((observableValue, employee, newEmployee) -> {
      positionLbl.textProperty().bind(newEmployee.positionProperty());
      newEmployee.statusProperty().addListener(getStatusChangeListener());
      if (newEmployee.getStatus() != null) {
        statusLbl.setText(newEmployee.getStatus().name());
      }
    });

    if (getEmployee() != null) {
      Employee employee = getEmployee();
      positionLbl.textProperty().bind(employee.positionProperty());
      employee.statusProperty().addListener(getStatusChangeListener());
      if (employee.getStatus() != null) {
        statusLbl.setText(employee.getStatus().name());
      }
    }
  }

  private ChangeListener<EmployeeStatus> getStatusChangeListener() {
    return (observableValue, employeeStatus, newStatus) -> statusLbl.setText(newStatus.name());
  }

  private static Text createText(String textToShow) {
    Text text = new Text(textToShow);
    text.setFont(Font.font(12));
    return text;
  }
}
