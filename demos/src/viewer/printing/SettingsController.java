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
package viewer.printing;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.CheckBox;

/**
 * Controller class for various settings that are configurable via checkboxes.
 */
public class SettingsController {

  // settings container
  public CheckBox usePrintRectangle;
  public CheckBox scaleUpToFitPage;
  public CheckBox scaleDownToFitPage;
  public CheckBox centerContent;
  public CheckBox printDecorations;
  public CheckBox printPageMarks;
  public NumbersOnlyTextField scaleField;

  // property that backs the content of the scaleField as double
  private DoubleProperty scale = new SimpleDoubleProperty(this, "scale", 1);

  public void initialize() {
    // create a binding for the scale property that converts the string content to double
    scaleField.textProperty().addListener((observable, oldValue, newValue) ->scale.set(newValue.isEmpty() ? 1 : Double.parseDouble(newValue)));
  }

  /**
   * Returns the selected property of the printPageMarks checkbox.
   */
  public BooleanProperty printPageMarksSelectedProperty() {
    return printPageMarks.selectedProperty();
  }

  /**
   * Returns the selected property of the printDecorations checkbox.
   */
  public BooleanProperty printDecorationsSelectedProperty() {
    return printDecorations.selectedProperty();
  }

  /**
   * Returns the selected property of the usePrintRectangle checkbox.
   */
  public BooleanProperty usePrintRectangleSelectedProperty() {
    return usePrintRectangle.selectedProperty();
  }

  /**
   * Returns the selected property of the scaleUpToFitPage checkbox.
   */
  public BooleanProperty scaleUpToFitPageSelectedProperty() {
    return scaleUpToFitPage.selectedProperty();
  }

  /**
   * Returns the selected property of the scaleDownToFitPage checkbox.
   */
  public BooleanProperty scaleDownToFitPageSelectedProperty() {
    return scaleDownToFitPage.selectedProperty();
  }

  /**
   * Returns the selected property of the centerContent checkbox.
   */
  public BooleanProperty centerContentSelectedProperty() {
    return centerContent.selectedProperty();
  }

  /**
   * Returns the property that holds the double value of the content of the scale field.
   */
  public DoubleProperty scaleProperty() {
    return scale;
  }
}
