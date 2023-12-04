/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package tutorial02_CustomStyles.step12_HighPerformanceRenderingOfLabels;

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

import java.util.Objects;

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.ILabelStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractLabelStyle} as its base class.
 * The text format of the label text can be set. The label text is drawn with black letters inside a blue rounded rectangle.
 */
public class MySimpleLabelStyle extends AbstractLabelStyle {
  // default color to fill the background of the label with
  private static final Color DEFAULT_FILL_COLOR = Color.rgb(155, 226, 255);
  // default color to draw the text of the label with
  private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
  // default pen to draw the border of the label with
  private static final Pen DEFAULT_BORDER_PEN = Pen.getSkyBlue();

  // measures for drawing the label
  private static final int HORIZONTAL_INSET = 4;
  private static final int VERTICAL_INSET = 2;

  private Font font;

  //////////////// New in this sample ////////////////
  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, ILabel)} is called instead.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, ILabel label) {
    LabelVisual visual = (LabelVisual) oldVisual;
    visual.update(label.getLayout(), label.getText(), getFont());
    arrangeByLayout(context, visual, label.getLayout(), true);
    return oldVisual;
  }
  ////////////////////////////////////////////////////

  /**
   * Initializes a new instance of the {@link AbstractLabelStyle} class using the
   * default font of the current UI environment.
   */
  public MySimpleLabelStyle() {
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
    LabelVisual labelVisual = new LabelVisual();
    labelVisual.update(label.getLayout(), label.getText(), getFont());
    arrangeByLayout(context, labelVisual, label.getLayout(), true);
    return labelVisual;
  }

  /**
   * Calculates the preferred size for the given label if this style is used for rendering.
   * The size is calculated from the label's text.
   */
  @Override
  protected SizeD getPreferredSize(ILabel label) {
    // return size of the text plus some inset space
    String labelText = label.getText();

    if (labelText != null && !labelText.isEmpty()) {
      // calculate the bounds of the text with the given text format
      Text text = new Text(labelText);
      text.setFont(font);
      double textWidth = text.getLayoutBounds().getWidth();
      double maxTextHeight = text.getLayoutBounds().getHeight();

      // then use the desired size - plus rounding and insets
      return new SizeD(
          Math.ceil(0.5d + textWidth) + 2 * HORIZONTAL_INSET,
          Math.ceil(0.5d + maxTextHeight) + 2 * VERTICAL_INSET);
    } else {
      return new SizeD(2 * HORIZONTAL_INSET, 2 * VERTICAL_INSET + font.getSize());
    }
  }

  /**
   * A {@link VisualGroup} that paints a label with text. Note that we paint the label at the origin
   * and move and rotate the container to the current location and orientation of the label. We store the
   * background shape, the text, the position and the size of the label as instance variables.
   * The {@link #update(IOrientedRectangle, String, Font)} method checks
   * whether these values have been changed. If so, the instance variables are updated.
   */
  private static class LabelVisual extends VisualGroup {
    // the font used for rendering the label text
    private Font font;

    //////////////// New in this sample ////////////////
    // the shape to draw the background of the label with
    private Rectangle background;
    // the size and position of the label
    private IOrientedRectangle labelLayout;
    // the text the label shows
    private Text text;

    LabelVisual() {
      background = new Rectangle();
      text = new Text();

      // and add all to the container for the node
      this.getChildren().addAll(background, text);
    }

    /**
     * Checks if the properties of the label have been changed. If so, updates all items needed to paint the label.
     * @param layout the location and size of the label.
     * @param labelText   the text the label shows
     * @param font the text format for rendering the label text with
     */
    public void update(IOrientedRectangle layout, String labelText, Font font) {
      if (!Objects.equals(layout, this.labelLayout) || !Objects.equals(text.getText(), labelText) || !Objects.equals(
          font, this.font)) {
        this.labelLayout = layout;
        this.text.setText(labelText);
        this.font = font;

        // update background shape
        background.setWidth(labelLayout.getWidth());
        background.setHeight(labelLayout.getHeight());
        background.setArcWidth(labelLayout.getWidth() / 10);
        background.setArcHeight(labelLayout.getHeight() / 10);
        background.setFill(DEFAULT_FILL_COLOR);
        DEFAULT_BORDER_PEN.styleShape(background);

        // draw the text of the label in the center of the label's bounds
        if (labelText != null && !labelText.isEmpty()) {
          // update the text format
          text.setFill(DEFAULT_TEXT_COLOR);
          text.setFont(font);

          // update text position
          double textPositionLeft = (labelLayout.getWidth() - text.getLayoutBounds().getWidth()) / 2;
          text.setX(textPositionLeft);
          text.setY((labelLayout.getHeight() + text.getLayoutBounds().getHeight()) / 2);
          text.setTextOrigin(VPos.BOTTOM);
        }
      }
    }
    ////////////////////////////////////////////////////
  }
}
