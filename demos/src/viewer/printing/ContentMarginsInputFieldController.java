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
package viewer.printing;

import com.yworks.yfiles.geometry.InsetsD;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

/**
 * Controller class for the input fields to configure the ContentMargins property of the CanvasPrinter
 */
public class ContentMarginsInputFieldController {

  // labels that display the current settings for the canvas printer
  public NumbersOnlyTextField contentMarginsTop;
  public NumbersOnlyTextField contentMarginsRight;
  public NumbersOnlyTextField contentMarginsBottom;
  public NumbersOnlyTextField contentMarginsLeft;

  // the property that backs the content margins object
  private ObjectProperty<InsetsD> contentMargins = new SimpleObjectProperty<>(this, "contentMargins",
          new InsetsD(PrintingDemo.PAGE_HEADER_INSET, PrintingDemo.PAGE_SIDE_INITIAL_INSET, PrintingDemo.PAGE_FOOTER_INSET, PrintingDemo.PAGE_SIDE_INITIAL_INSET));

  // A shortcut for the PrintDecorations property of the SettingsController.
  private BooleanProperty printDecorations = new SimpleBooleanProperty(this, "printDecorations", true);

  public void initialize() {
    // when print decorations option has changed, update the margins to the minimum values if needed.
    printDecorations.addListener((observable1, oldValue, newValue) -> {
      // check content margins input fields for correct values - min is 50 for top and bottom
      if (Integer.parseInt(contentMarginsTop.getText()) < PrintingDemo.PAGE_HEADER_INSET) setMargins(contentMarginsTop);
      if (Integer.parseInt(contentMarginsBottom.getText()) < PrintingDemo.PAGE_FOOTER_INSET) setMargins(contentMarginsBottom);
      updateTextFields();
    });

    // when the values of the TextFields have changed, update the value of the ContentMargin
    contentMarginsLeft.setOnAction(event -> setMargins((TextField)event.getSource()));
    contentMarginsTop.setOnAction(event -> setMargins((TextField)event.getSource()));
    contentMarginsRight.setOnAction(event -> setMargins((TextField)event.getSource()));
    contentMarginsBottom.setOnAction(event -> setMargins((TextField)event.getSource()));
    contentMarginsLeft.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
      if (!isFocused) setMargins(contentMarginsLeft);
    });
    contentMarginsTop.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
      if (!isFocused) setMargins(contentMarginsTop);
    });
    contentMarginsRight.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
      if (!isFocused) setMargins(contentMarginsRight);
    });
    contentMarginsBottom.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
      if (!isFocused) setMargins(contentMarginsBottom);
    });
    updateTextFields();
  }

  /**
   * Sets the margins for the TextField that was changed
   */
  private void setMargins(final TextField textField) {
    int minValue = 0;
    int i;
    try {
      i = Integer.parseInt(textField.getText());
    } catch (NumberFormatException e) {
      updateTextFields();
      return;
    }
    if (getPrintDecorations()) {
      if (textField == contentMarginsTop) {
        minValue = PrintingDemo.PAGE_HEADER_INSET;
      } else if (textField == contentMarginsBottom) {
        minValue = PrintingDemo.PAGE_FOOTER_INSET;
      }
    }
    InsetsD contentMargins = getContentMargins();

    double result = Math.max(i, minValue);
    if (textField == contentMarginsLeft) setContentMargins(new InsetsD(contentMargins.getTop(), contentMargins.getRight(), contentMargins.getBottom(), result));
    if (textField == contentMarginsTop) setContentMargins(new InsetsD(result, contentMargins.getRight(), contentMargins.getBottom(), contentMargins.getLeft()));
    if (textField == contentMarginsRight) setContentMargins(new InsetsD(contentMargins.getTop(), result, contentMargins.getBottom(), contentMargins.getLeft()));
    if (textField == contentMarginsBottom) setContentMargins(new InsetsD(contentMargins.getTop(), contentMargins.getRight(), result, contentMargins.getLeft()));
    updateTextFields();

  }

  /**
   * Set the content of the TextFields to the values of the ContentMargins property of the CanvasPrinter.
   */
  private void updateTextFields() {
    InsetsD contentMargins = getContentMargins();
    contentMarginsLeft.setText((int)contentMargins.getLeft()+"");
    contentMarginsTop.setText((int)contentMargins.getTop()+"");
    contentMarginsRight.setText((int)contentMargins.getRight()+"");
    contentMarginsBottom.setText((int)contentMargins.getBottom()+"");
  }

  /**
   * The property that holds the content margins. This value is used to bind the content margins property of the CanvasPrinter.
   */
  public ObjectProperty<InsetsD> contentMarginsProperty() {
    return contentMargins;
  }

  /**
   * Returns the currently set margins.
   */
  public InsetsD getContentMargins() {
    return contentMargins.get();
  }

  /**
   * Sets the content margins property to the specified value.
   */
  public void setContentMargins(final InsetsD contentMargins) {
    this.contentMargins.set(contentMargins);
  }


  /**
   * Returns the property for the print decorations value. This is bound to the value of the checkbox in the SettingsController.
   */
  public BooleanProperty printDecorationsProperty() {
    return printDecorations;
  }

  /**
   * Returns whether to print with decorations or not.
   */
  public boolean getPrintDecorations() {
    return printDecorations.get();
  }

  /**
   * Sets whether to print with decorations or not.
   */
  public void setPrintDecorations(final boolean printDecorations) {
    this.printDecorations.set(printDecorations);
  }
}
