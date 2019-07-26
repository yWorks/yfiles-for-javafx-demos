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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Represents the yFiles for JavaFX 2.0.x version of GraphML element
 * <code>&lt;TextFormat&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class TextFormat {
  private Font font;
  private TextAlignment textAlignment;
  private FontSmoothingType fontSmoothingType;
  private boolean strikethrough;
  private boolean underline;
  private double wrappingWidth;
  private double lineSpacing;

  /**
   * Initializes a new <code>TextFormat</code> instance with default values.
   */
  public TextFormat() {
    this(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 12),
         TextAlignment.LEFT, FontSmoothingType.GRAY, false, false, 0, 0);
  }

  /**
   * Initializes a new <code>TextFormat</code> instance with the given settings.
   */
  public TextFormat(
          Font font,
          TextAlignment textAlignment,
          FontSmoothingType fontSmoothingType,
          boolean strikethrough,
          boolean underline,
          double wrappingWidth,
          double lineSpacing
  ) {
    this.font = font;
    this.textAlignment = textAlignment;
    this.fontSmoothingType = fontSmoothingType;
    this.strikethrough = strikethrough;
    this.underline = underline;
    this.wrappingWidth = wrappingWidth;
    this.lineSpacing = lineSpacing;
  }

  /**
   * Returns the Font that is used for the text.
   * <p>The default value is the default font of the current UI environment.</p>
   */
  @DefaultValue(stringValue = "System;12.0;Regular Normal",classValue = Font.class,valueType = DefaultValue.ValueType.STRING_CONVERTED_TYPE)
  public Font getFont() {
    return font;
  }

  /**
   * Sets the Font to use for the text.
   * @param font The JavaFX Font to set.
   */
  public void setFont(Font font) {
    this.font = font;
  }

  /**
   * Returns the horizontal TextAlignment that is used for the text.
   * <p>The default value is <code>TextAlignment.LEFT</code></p>
   */
  @DefaultValue(stringValue = "LEFT", valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = TextAlignment.class)
  public TextAlignment getTextAlignment() {
    return textAlignment;
  }

  /**
   * Sets the horizontal TextAlignment to use for the text.
   * @param textAlignment The JavaFX TextAlignment to set.
   */
  public void setTextAlignment(TextAlignment textAlignment) {
    this.textAlignment = textAlignment;
  }

  /**
   * Returns the smoothing type for the Font that is used for the text.
   * <p>The default value is <code>FontSmoothingType.GRAY</code></p>
   */
  @DefaultValue(stringValue = "GRAY", valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = FontSmoothingType.class)
  public FontSmoothingType getFontSmoothingType() {
    return fontSmoothingType;
  }

  /**
   * Sets he smoothing type for the Font that to use for the text.
   * @param fontSmoothingType The JavaFX FontSmoothingType to set.
   */
  public void setFontSmoothingType(FontSmoothingType fontSmoothingType) {
    this.fontSmoothingType = fontSmoothingType;
  }

  /**
   * Indicates whether or not each line of text should have a line through it.
   * <p>The default value is <code>false</code></p>
   */
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public boolean isStrikethrough() {
    return strikethrough;
  }

  /**
   * Sets whether or not each line of text should have a line through it.
   */
  public void setStrikethrough(boolean strikethrough) {
    this.strikethrough = strikethrough;
  }

  /**
   * Indicates whether or not the text is underlined.
   * <p>The default value is <code>false</code></p>
   */
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public boolean isUnderline() {
    return underline;
  }

  /**
   * Sets whether or not the text is underlined.
   */
  public void setUnderline(boolean underline) {
    this.underline = underline;
  }

  /**
   * Returns the value for the wrappingWidth property of the JavaFX text.
   * <p>The default value is <code>0</code>.</p>
   */
  @DefaultValue(doubleValue = 0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public double getWrappingWidth() {
    return wrappingWidth;
  }

  /**
   * Sets the value for the wrappingWidth property of the JavaFX text.
   * <p>The default value is <code>0</code>.</p>
   */
  public void setWrappingWidth(final double wrappingWidth) {
    this.wrappingWidth = wrappingWidth;
  }

  /**
   * Returns the value for the lineSpacing property of the JavaFX text.
   * <p>The default value is <code>0</code>.</p>
   */
  @DefaultValue(doubleValue = 0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public double getLineSpacing() {
    return lineSpacing;
  }

  /**
   * Sets the value for the lineSpacing property of the JavaFX text.
   * <p>The default value is <code>0</code>.</p>
   */
  public void setLineSpacing(final double lineSpacing) {
    this.lineSpacing = lineSpacing;
  }
}
