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

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextWrapping;
import javafx.geometry.VPos;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;

/**
 * Handles the yFiles for JavaFX 2.0.x version of GraphML element
 * <code>&lt;SimpleLabelStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class SimpleLabelStyleExtension extends MarkupExtension {
  private boolean autoFlipping;
  private Paint backgroundPaint;
  private Pen backgroundPen;
  private boolean clippingText;
  private TextFormat textFormat;
  private Paint textPaint;
  private VPos verticalTextAlignment;

  public SimpleLabelStyleExtension() {
    DefaultLabelStyle prototype = new DefaultLabelStyle();
    autoFlipping = prototype.isAutoFlippingEnabled();
    backgroundPaint = prototype.getBackgroundPaint();
    backgroundPen = prototype.getBackgroundPen();
    clippingText = prototype.isTextClippingEnabled();
    textPaint = prototype.getTextPaint();
    verticalTextAlignment = prototype.getVerticalTextAlignment();

    textFormat = new TextFormat(
            prototype.getFont(),
            prototype.getTextAlignment(),
            prototype.getFontSmoothingType(),
            prototype.isStrikethrough(),
            prototype.isUnderline(),
            0,
            prototype.getLineSpacing());
  }

  /**
   * Handles the GraphML alias <code>AutoFlip</code> used in yFiles for
   * JavaFX 2.0.x for property <code>AutoFlipping</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isAutoFlipping()
   */
  public boolean isAutoFlip() {
    return isAutoFlipping();
  }

  /**
   * Handles the GraphML alias <code>AutoFlip</code> used in yFiles for
   * JavaFX 2.0.x for property <code>AutoFlipping</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setAutoFlipping(boolean)
   */
  public void setAutoFlip( boolean value ) {
    setAutoFlipping(value);
  }

  public boolean isAutoFlipping() {
    return autoFlipping;
  }

  public void setAutoFlipping( boolean value ) {
    this.autoFlipping = value;
  }

  /**
   * Handles the GraphML alias <code>BackgroundBrush</code> used in yFiles for
   * JavaFX 3.0.x for property <code>BackgroundPaint</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getBackgroundPaint()
   */
  public Paint getBackgroundBrush() {
    return getBackgroundPaint();
  }

  /**
   * Handles the GraphML alias <code>BackgroundBrush</code> used in yFiles for
   * JavaFX 3.0.x for property <code>BackgroundPaint</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setBackgroundPaint(Paint)
   */
  public void setBackgroundBrush( Paint value ) {
    setBackgroundPaint(value);
  }

  public Paint getBackgroundPaint() {
    return backgroundPaint;
  }

  public void setBackgroundPaint( Paint value ) {
    this.backgroundPaint = value;
  }

  public Pen getBackgroundPen() {
    return backgroundPen;
  }

  public void setBackgroundPen( Pen value ) {
    this.backgroundPen = value;
  }

  /**
   * Handles the GraphML alias <code>ClipText</code> used in yFiles for
   * JavaFX 2.0.x for property <code>ClippingText</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isClippingText()
   */
  public boolean isClipText() {
    return isClippingText();
  }

  /**
   * Handles the GraphML alias <code>ClipText</code> used in yFiles for
   * JavaFX 2.0.x for property <code>ClippingText</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setClippingText(boolean)
   */
  public void setClipText( boolean value ) {
    setClippingText(value);
  }

  public boolean isClippingText() {
    return clippingText;
  }

  public void setClippingText( boolean value ) {
    this.clippingText = value;
  }

  /**
   * Handles the GraphML alias <code>TextBrush</code> used in yFiles for
   * JavaFX 2.0.x for property <code>TextPaint</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getTextPaint()
   */
  public Paint getTextBrush() {
    return getTextPaint();
  }

  /**
   * Handles the GraphML alias <code>TextBrush</code> used in yFiles for
   * JavaFX 2.0.x for property <code>TextPaint</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setTextPaint(Paint)
   */
  public void setTextBrush( Paint value ) {
    setTextPaint(value);
  }

  public TextFormat getTextFormat() {
    return textFormat;
  }

  public void setTextFormat( TextFormat value ) {
    this.textFormat = value;
  }

  public Paint getTextPaint() {
    return textPaint;
  }

  public void setTextPaint( Paint value ) {
    this.textPaint = value;
  }

  public VPos getVerticalTextAlignment() {
    return verticalTextAlignment;
  }

  public void setVerticalTextAlignment( VPos value ) {
    this.verticalTextAlignment = value;
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setBackgroundPaint(getBackgroundPaint());
    VPos verticalTextAlignment = getVerticalTextAlignment();
    if (verticalTextAlignment != null) {
      style.setVerticalTextAlignment(verticalTextAlignment);
    }
    style.setTextClippingEnabled(isClippingText());
    Pen backgroundPen = getBackgroundPen();
    if (backgroundPen != null) {
      style.setBackgroundPen(backgroundPen);
    }
    style.setAutoFlippingEnabled(isAutoFlipping());
    Paint textPaint = getTextPaint();
    if (textPaint != null) {
      style.setTextPaint(textPaint);
    }

    TextFormat format = getTextFormat();
    if (format != null) {
      Font font = format.getFont();
      if (font != null) {
        style.setFont(font);
      }
      FontSmoothingType smoothingType = format.getFontSmoothingType();
      if (smoothingType != null) {
        style.setFontSmoothingType(smoothingType);
      }
      style.setLineSpacing(format.getLineSpacing());
      TextAlignment textAlignment = format.getTextAlignment();
      if (textAlignment != null) {
        style.setTextAlignment(textAlignment);
      }
      style.setStrikethrough(format.isStrikethrough());
      style.setUnderline(format.isUnderline());
      double wrappingWidth = format.getWrappingWidth();
      if (wrappingWidth > 0) {
        style.setTextWrapping(TextWrapping.WRAP);
      }
    }

    return style;
  }
}
