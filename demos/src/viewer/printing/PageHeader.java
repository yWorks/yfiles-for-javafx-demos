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
package viewer.printing;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * The header for the printed pages. Displays some information at the top, with a closing line at the bottom.
 */
public class PageHeader extends Group {

  /**
   * The width of the the header. This is mutable because the printed pages may very in width dependent on the page layout settings.
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
   * Creates a new instance and sets up the header. This instance is re-used in the printing process with varying page numbers and widths.
   */
  public PageHeader(final double height, final String author, final String title, final String date) {
    this.setManaged(false);
    // set up text nodes for the information to display
    Text authorText = new Text(author);
    Text titleText = new Text(title);
    Text dateText = new Text(date);

    // the text nodes are centered in the available space
    authorText.setLayoutY(height / 2);
    titleText.setLayoutY(height / 2);
    dateText.setLayoutY(height / 2);

    // the title is centered
    titleText.layoutXProperty().bind(widthProperty().divide(2).subtract(titleText.getBoundsInLocal().getWidth() / 2));

    // the date is placed at the right border
    dateText.layoutXProperty().bind(widthProperty().subtract(dateText.getBoundsInLocal().getWidth()));

    // the author is positioned at the left border so no need to change layoutX here

    // create the closing line that separates header from content at the bottom and bind it to the width property.
    Line line = new Line(0, height, 0, height);
    line.endXProperty().bind(this.widthProperty());

    this.getChildren().addAll(authorText, titleText, dateText, line);
  }
}
