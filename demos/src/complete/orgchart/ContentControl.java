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

import com.yworks.yfiles.graph.styles.NodeTemplate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Extension of a NodeTemplate that provides additional properties used to visualize the employees.
 */
public class ContentControl extends NodeTemplate {
  private ObjectProperty<Color> statusColor = new SimpleObjectProperty<>(this, "statusColor", Color.GREEN);
  public ObjectProperty<Color> statusColorProperty(){ return statusColor; }
  public Color getStatusColor(){ return statusColor.get(); }
  public void setStatusColor(Color color){ statusColor.set(color); }

  private ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image", null);
  public ObjectProperty<Image> imageProperty(){ return image; }
  public Image getImage(){ return image.get(); }
  public void setImage(Image color){ image.set(color); }

  private ObjectProperty<Employee> employee = new SimpleObjectProperty<>(this, "employee", null);
  public ObjectProperty<Employee> employeeProperty(){ return employee; }
  public Employee getEmployee(){ return employee.get(); }
  public void setEmployee(Employee employee){ this.employee.set(employee); }

  private StringProperty shortDisplayName = new SimpleStringProperty(this, "shortDisplayName", "");
  public final StringProperty shortDisplayNameProperty(){return shortDisplayName;}
  public final String getShortDisplayName() {return shortDisplayName.get(); }
  public final void setShortDisplayName(String value) { shortDisplayName.set(value); }

  private StringProperty fullDisplayName = new SimpleStringProperty(this, "fullDisplayName", "");
  public final StringProperty fullDisplayNameProperty(){return fullDisplayName;}
  public final String getFullDisplayName() {return fullDisplayName.get(); }
  public final void setFullDisplayName( String value ) { fullDisplayName.set(value); }
}
