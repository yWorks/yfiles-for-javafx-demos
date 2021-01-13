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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * The footer for the printed pages. Displays a simple line at the top and a label displaying the currently set page number on the right.
 */
public class PageFooter extends Group {

  /**
   * The width of the the footer. This is mutable because the printed pages may very in width dependent on the page layout settings.
   */
  private DoubleProperty width = new SimpleDoubleProperty(this, "width", 0);

  public double getWidth() {
    return width.get();
  }

  public DoubleProperty widthProperty() {
    return width;
  }

  public void setWidth(final double width) {
    this.width.set(width);
  }

  /**
   * The current page number.
   */
  private IntegerProperty pageNumber = new SimpleIntegerProperty(this, "pageNumber", 0);

  public int getPageNumber() {
    return pageNumber.get();
  }

  public IntegerProperty pageNumberProperty() {
    return pageNumber;
  }

  public void setPageNumber(final int pageNumber) {
    this.pageNumber.set(pageNumber);
  }

  /**
   * Creates a new instance and sets up the footer. This instance is re-used in the printing process with varying page numbers and widths.
   */
  public PageFooter(final double height) {
    this.setManaged(false);
    // the text node for our page number
    Text pageNumberLabel = new Text("Page 00");

    // position the label at the right border with a small padding.
    pageNumberLabel.layoutXProperty().bind(widthProperty().subtract(pageNumberLabel.getBoundsInLocal().getWidth() + 10));
    // center the label y-wise.
    pageNumberLabel.setLayoutY(height / 2);

    // update the text if the page number changes.
    pageNumberProperty().addListener((observable, oldValue, newValue) -> pageNumberLabel.setText("Page " + newValue));

    // create the line at the top that separates footer from content and bind it to the width property.
    Line line = new Line(0, 0, 0, 0);
    line.endXProperty().bind(this.widthProperty());

    this.getChildren().addAll(pageNumberLabel, line);
  }
}
