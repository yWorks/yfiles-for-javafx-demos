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
package tutorial02_CustomStyles.step10_CustomLabelStyle;

import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import javafx.scene.Node;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/////////////// This class is new in this sample ///////////////

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.ILabelStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractLabelStyle} as its base class.
 * The text format of the label text can be set. The label text is drawn with black letters inside a blue rounded rectangle.
 */
public class MyDefaultLabelStyle extends AbstractLabelStyle {
  // default color to fill the background of the label with
  private static final Color DEFAULT_FILL_COLOR = Color.rgb(155, 226, 255);
  // default color to draw the text of the label with
  private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
  // default pen to draw the border of the label with
  private static final Pen DEFAULT_BORDER_PEN = Pen.getSkyBlue();

  private Font font;

  /**
   * Initializes a new <code>MyDefaultLabelStyle</code> instance and sets a default font for the label.
   */
  public MyDefaultLabelStyle() {
    font = Font.font(12);
  }

  /**
   * Gets the font used for rendering the label text.
   */
  public Font getFont() {
    return font;
  }

  /**
   * Sets the font used for rendering the label text.
   */
  public void setFont(Font font) {
    this.font = font;
  }

  /**
   * Creates the visual for a label to be drawn.
   */
  @Override
  protected Node createVisual(IRenderContext context, ILabel label) {
    // create a visual that renders the label
    LabelVisual labelVisual = new LabelVisual(label, font);
    // render the label
    labelVisual.render();
    // we use the dedicated method AbstractLabelStyle#arrangeByLayout to
    // arrange an element according to a given IOrientedRectangle
    arrangeByLayout(labelVisual, label.getLayout(), true);

    return labelVisual;
  }

  /**
   * Calculates the preferred size for the given label if this style is used for rendering.
   */
  @Override
  protected SizeD getPreferredSize(ILabel label) {
    return new SizeD(80, 15);
  }

  /**
   * A {@link VisualGroup} that paints a label with text. Note that we paint the label at the
   * origin and move and rotate the graphics context to the current location and orientation of the label.
   */
  private static class LabelVisual extends VisualGroup {
    // default color to fill the background of the label with
    private static final Color DEFAULT_FILL_COLOR = Color.rgb(155, 226, 255);
    // default color to draw the text of the label with
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    // default pen to draw the border of the label with
    private static final Pen DEFAULT_BORDER_PEN = Pen.getSkyBlue();

    // the label to show
    private ILabel label;
    // the font used for rendering the label text
    private Font font;

    LabelVisual(ILabel label, Font font) {
      this.label = label;
      this.font = font;
    }

    public void render() {
        // paint the background of the label
        IOrientedRectangle labelLayout = label.getLayout();
      Rectangle rect = new Rectangle();
      rect.setWidth(labelLayout.getWidth());
      rect.setHeight(labelLayout.getHeight());
      rect.setArcWidth(labelLayout.getWidth() / 10);
      rect.setArcHeight(labelLayout.getHeight() / 10);
      rect.setFill(DEFAULT_FILL_COLOR);
      DEFAULT_BORDER_PEN.styleShape(rect);
      this.add(rect);

      // draw the text of the label in the center of the label's bounds
      String labelText = label.getText();
      if (labelText != null && !labelText.isEmpty()) {
        Text text = new Text(labelText);
        text.setFill(DEFAULT_TEXT_COLOR);
        text.setFont(font);

        // align text left
        double textPositionLeft = (labelLayout.getWidth() - text.getLayoutBounds().getWidth()) / 2;
        text.setX(textPositionLeft);
        text.setY((labelLayout.getHeight() + text.getLayoutBounds().getHeight()) / 2);
        text.setTextOrigin(VPos.BOTTOM);
        this.add(text);
      }
    }
  }
}
