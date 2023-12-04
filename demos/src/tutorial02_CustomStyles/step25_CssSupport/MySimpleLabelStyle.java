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
package tutorial02_CustomStyles.step25_CssSupport;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.ICommand;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import toolkit.PenCssConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  private static final int BUTTON_SIZE = 16;

  private Font font;


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
    LabelVisual labelVisual = new LabelVisual(context, label);
    labelVisual.update(label.getLayout(), label.getText(), getFont(), isButtonVisible(context));
    arrangeByLayout(context, labelVisual, label.getLayout(), true);
    return labelVisual;
  }

  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, ILabel)} is called instead.
   */
  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, ILabel label) {
    LabelVisual visual = (LabelVisual) oldVisual;
    visual.update(label.getLayout(), label.getText(), getFont(), isButtonVisible(context));
    arrangeByLayout(context, visual, label.getLayout(), true);
    return oldVisual;
  }

  /**
   * Determines whether the button is visible or not. The button is visible for zoom levels greater than or equal to 1.
   */
  private static boolean isButtonVisible(ICanvasContext context) {
    return context.getZoom() >= 1;
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

      // then use the desired size - plus rounding, insets and the button size
      return new SizeD(
          Math.ceil(0.5d + textWidth) + BUTTON_SIZE + 3 * HORIZONTAL_INSET,
          Math.max(Math.ceil(0.5d + maxTextHeight), BUTTON_SIZE) + 2 * VERTICAL_INSET);
    } else {
      return new SizeD(50 + BUTTON_SIZE + 3 * HORIZONTAL_INSET, BUTTON_SIZE + 2 * VERTICAL_INSET);
    }
  }

  /**
   * The group that defines the properties that can be styled by CSS:
   * <ul>
   *   <li>fill-color: The color to use for the fill of the background. Of type {@link Color}</li>
   *   <li>text-color: The color to use for the text. Of type {@link Color}</li>
   *   <li>border-pen: The pen to use for the stroke of the border. Of type {@link Pen}</li>
   * </ul>
   */
  private static class LabelVisual extends VisualGroup {

    //////////////// New in this sample ////////////////
    private StyleableObjectProperty<Color> fillColor;
    private StyleableObjectProperty<Color> textColor;
    private StyleableObjectProperty<Pen> borderPen;
    ////////////////////////////////////////////////////

    // the font used for rendering the label text
    private Font font;
    // the shape to draw the background of the label with
    private Rectangle background;
    // the size and position of the label
    private IOrientedRectangle labelLayout;
    // the text the label shows
    private Text text;
    // button to edit the text of the label
    private Button button;
    // flag that specifies whether the button is visible or not
    private boolean buttonVisible;

    LabelVisual(IRenderContext context, ILabel label) {
      background = new Rectangle();
      text = new Text();

      //////////////// New in this sample ////////////////
      getStyleClass().add("simple-label-style");

      background.fillProperty().bind(fillColorProperty());
      getBorderPen().styleShape(background);
      this.borderPenProperty().addListener((observable, oldPen, newPen) -> newPen.styleShape(background));
      text.fillProperty().bind(textColorProperty());
      ////////////////////////////////////////////////////

      button = new Button();
      button.setManaged(false);
      button.resize(BUTTON_SIZE, BUTTON_SIZE);
      URL imageUrl = LabelVisual.class.getResource("../../resources/edit_label.png");
      if (imageUrl != null) {
        ImageView imageView = new ImageView(imageUrl.toExternalForm());
        imageView.setFitHeight(BUTTON_SIZE * 0.6);
        imageView.setFitWidth(BUTTON_SIZE * 0.6);
        button.setGraphic(imageView);
      }
      // set button command
      button.setOnAction(event -> ICommand.EDIT_LABEL.execute(label, context.getCanvasControl()));

      // and add all to the container for the node
      this.getChildren().addAll(background, text);
    }

    /**
     * Checks if the properties of the label have been changed. If so, updates all items needed to paint the label.
     * @param layout the location and size of the label.
     * @param labelText   the text the label shows
     * @param font the font for rendering the label text with
     * @param buttonVisible flag that specifies whether the button is visible or not
     */
    public void update(IOrientedRectangle layout, String labelText, Font font, boolean buttonVisible) {
      if (!Objects.equals(layout, this.labelLayout) || !Objects.equals(text.getText(), labelText) || !Objects.equals(
          font, this.font) || this.buttonVisible != buttonVisible) {
        this.labelLayout = layout;
        this.text.setText(labelText);
        this.font = font;

        // update background shape
        background.setWidth(labelLayout.getWidth());
        background.setHeight(labelLayout.getHeight());
        background.setArcWidth(labelLayout.getWidth() / 10);
        background.setArcHeight(labelLayout.getHeight() / 10);

        // draw the text of the label in the center of the label's bounds
        if (labelText != null && !labelText.isEmpty()) {
          // update the text format
          text.setFont(font);

          // update text position
          // if the edit button is visible align left, otherwise center
          text.setX(buttonVisible ? HORIZONTAL_INSET : (labelLayout.getWidth() - text.getLayoutBounds().getWidth()) * 0.5);
          text.setY((labelLayout.getHeight() + text.getLayoutBounds().getHeight()) / 2);
          text.setTextOrigin(VPos.BOTTOM);
        }

        // draw button if visible
        if (buttonVisible) {
          if (!this.buttonVisible) {
            this.add(button);
          }
          // update button position
          button.relocate(labelLayout.getWidth() - HORIZONTAL_INSET - BUTTON_SIZE, VERTICAL_INSET);
        } else if (this.buttonVisible) {
          this.remove(button);
        }
        this.buttonVisible = buttonVisible;
      }
    }

    //////////////// New in this sample ////////////////
    public Color getFillColor() {
      return fillColor == null ? DEFAULT_FILL_COLOR : fillColor.get();
    }

    public StyleableObjectProperty<Color> fillColorProperty() {
      if (fillColor == null) {
        fillColor = new SimpleStyleableObjectProperty<>(StyleableProperties.FILL_COLOR, LabelVisual.this, "fill-color", DEFAULT_FILL_COLOR);
      }
      return fillColor;
    }

    public void setFillColor(Color fillColor) {
      this.fillColorProperty().set(fillColor);
    }

    public Color getTextColor() {
      return textColor == null ? DEFAULT_TEXT_COLOR : textColor.get();
    }

    public StyleableObjectProperty<Color> textColorProperty() {
      if (textColor == null) {
        textColor = new SimpleStyleableObjectProperty<>(StyleableProperties.TEXT_COLOR, LabelVisual.this, "text-color", DEFAULT_TEXT_COLOR);
      }
      return textColor;
    }

    public void setTextColor(Color textColor) {
      this.textColorProperty().set(textColor);
    }

    public Pen getBorderPen() {
      return borderPen == null ? DEFAULT_BORDER_PEN : borderPen.get();
    }

    public StyleableObjectProperty<Pen> borderPenProperty() {
      if (borderPen == null) {
        borderPen = new SimpleStyleableObjectProperty<>(StyleableProperties.BORDER_PEN, LabelVisual.this, "border-pen", DEFAULT_BORDER_PEN);
      }
      return borderPen;
    }

    public void setBorderPen(Pen borderPen) {
      this.borderPenProperty().set(borderPen);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return StyleableProperties.STYLEABLES;
    }  /**
     * Helper class that provides the CSS meta data.
     */
    private static class StyleableProperties {
      private static final CssMetaData<LabelVisual, Color> FILL_COLOR =
          new CssMetaData<LabelVisual, Color>("fill-color", StyleConverter.getColorConverter()) {
            @Override
            public boolean isSettable(LabelVisual styleable) {
              return styleable.fillColor == null || !styleable.fillColor.isBound();
            }

            @Override
            public StyleableProperty<Color> getStyleableProperty(LabelVisual styleable) {
              return styleable.fillColorProperty();
            }
          };

      private static final CssMetaData<LabelVisual, Color> TEXT_COLOR =
          new CssMetaData<LabelVisual, Color>("text-color", StyleConverter.getColorConverter()) {
            @Override
            public boolean isSettable(LabelVisual styleable) {
              return styleable.textColor == null || !styleable.textColor.isBound();
            }

            @Override
            public StyleableProperty<Color> getStyleableProperty(LabelVisual styleable) {
              return styleable.textColorProperty();
            }
          };

      private static final CssMetaData<LabelVisual, Pen> BORDER_PEN =
          new CssMetaData<LabelVisual, Pen>("border-pen", PenCssConverter.INSTANCE) {
            @Override
            public boolean isSettable(LabelVisual styleable) {
              return styleable.borderPen == null || !styleable.borderPen.isBound();
            }

            @Override
            public StyleableProperty<Pen> getStyleableProperty(LabelVisual styleable) {
              return styleable.borderPenProperty();
            }
          };

      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
        ArrayList<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Group.getClassCssMetaData());
        Collections.addAll(styleables, FILL_COLOR, TEXT_COLOR, BORDER_PEN);
        STYLEABLES = Collections.unmodifiableList(styleables);
      }
    }
    ////////////////////////////////////////////////////
  }
}
