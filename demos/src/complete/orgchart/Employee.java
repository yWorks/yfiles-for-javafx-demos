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

/**
 * Representation for the business model of an employee.
 * A employee consists of
 * <ul>
 *   <li>A first and last name.</li>
 *   <li>The position he or she is in.</li>
 *   <li>Contact data like phone, email and fax.</li>
 *   <li>The business unit he is part of.</li>
 *   <li>His or her current status, i.e. present, travelling etc.</li>
 *   <li>A picture or icon.</li>
 * </ul>
 * The Employee is constructed upon parsing the graphml and is defined via XML.
 */
public class Employee {
  private StringProperty name = new SimpleStringProperty(this, "name", "");
  public final StringProperty nameProperty(){return name;}
  public final String getName() {return name.get(); }
  public final void setName( String value ) { name.set(value); }

  private StringProperty firstName = new SimpleStringProperty(this, "firstName", "");
  public final StringProperty firstNameProperty(){return firstName;}
  public final String getFirstName() { return firstName.get(); }
  public final void setFirstName( String value ) { firstName.set(value); }

  private StringProperty position = new SimpleStringProperty(this, "position", "");
  public final StringProperty positionProperty(){return position;}
  public final String getPosition() { return position.get(); }
  public final void setPosition( String value ) { position.set(value); }

  private StringProperty fax = new SimpleStringProperty(this, "fax", "");
  public final StringProperty faxProperty(){return fax;}
  public final String getFax() {return fax.get(); }
  public final void setFax( String value ) { fax.set(value); }

  private StringProperty businessUnit = new SimpleStringProperty(this, "businessUnit", "");
  public final StringProperty businessUnitProperty(){return businessUnit;}
  public final String getBusinessUnit() { return businessUnit.get(); }
  public final void setBusinessUnit( String value ) { businessUnit.set(value); }

  private ObjectProperty<EmployeeStatus> status = new SimpleObjectProperty<>(this, "status", EmployeeStatus.Present);
  public ObjectProperty<EmployeeStatus> statusProperty(){return status;}
  public final EmployeeStatus getStatus() { return status.get(); }
  public final void setStatus( EmployeeStatus value ) { status.set(value); }

  private StringProperty icon = new SimpleStringProperty(this, "icon", "");
  public final StringProperty iconProperty(){return icon;}
  public final String getIcon() { return icon.get(); }
  public final void setIcon( String value ) { icon.set(value); }

  private StringProperty phone = new SimpleStringProperty(this, "phone", "");
  public final StringProperty phoneProperty(){return phone;}
  public final String getPhone() { return phone.get(); }
  public final void setPhone( String value ) { phone.set(value); }

  private StringProperty email = new SimpleStringProperty(this, "email", "");
  public final StringProperty emailProperty(){return email;}
  public final String getEmail() { return email.get(); }
  public final void setEmail( String value ) { email.set(value); }

  public Employee(){}

  public Employee( String name, String firstName, String position, String fax, String businessUnit, String icon, String phone, String email ) {
    setName(name);
    setFirstName(firstName);
    setPosition(position);
    setFax(fax);
    setBusinessUnit(businessUnit);
    setIcon(icon);
    setPhone(phone);
    setEmail(email);
  }

  @Override
  public String toString() {
    return "";
  }
}
