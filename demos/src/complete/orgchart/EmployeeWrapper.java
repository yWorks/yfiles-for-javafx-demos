/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.graphml.GraphML;

/**
 * A wrapper for {@link Employee} that holds several additional information about the
 * Employee:
 * <ul>
 *   <li>If he is an assistant</li>
 *   <li>If he is vacant.</li>
 *   <li>The {@link EmployeeLayout} to use for the Employee</li>
 * </ul>
 */
@GraphML(contentProperty = "Employee")
public class EmployeeWrapper {

  private Employee employee;
  private boolean assistant;
  private EmployeeLayout layout;

  public EmployeeWrapper() {
    employee = new Employee();
    setAssistant(false);
    setLayout(EmployeeLayout.None);
  }

  public EmployeeWrapper(Employee employee) {
    this.employee = employee;
    setAssistant(false);
    setLayout(EmployeeLayout.None);
  }

  public final Employee getEmployee() {
    return employee;
  }

  public final void setEmployee( Employee value ) {
      employee = value;
  }

  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAssistant() {
    return assistant;
  }

  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAssistant( boolean value ) {
    assistant = value;
  }

  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EmployeeLayout.class, stringValue = "None")
  public final EmployeeLayout getLayout() {
    return layout;
  }

  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EmployeeLayout.class, stringValue = "None")
  public final void setLayout( EmployeeLayout value ) {
    layout = value;
  }
}
