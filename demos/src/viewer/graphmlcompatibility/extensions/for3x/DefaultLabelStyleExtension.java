/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.graphmlcompatibility.extensions.for3x;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextWrapping;
import javafx.geometry.NodeOrientation;
import javafx.geometry.VPos;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;

/**
 * Handles the yFiles for JavaFX 3.0.x version of GraphML element
 * <code>&lt;DefaultLabelStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class DefaultLabelStyleExtension extends MarkupExtension {
  private boolean autoFlippingEnabled;
  private Paint backgroundPaint;
  private Pen backgroundPen;
  private NodeOrientation flowDirection;
  private Font font;
  private FontSmoothingType fontSmoothingType;
  private InsetsD insets;
  private double lineSpacing;
  private boolean strikethrough;
  private TextAlignment textAlignment;
  private boolean textClippingEnabled;
  private Paint textPaint;
  private TextWrapping textWrapping;
  private boolean underline;
  private VPos verticalTextAlignment;

  public DefaultLabelStyleExtension() {
    DefaultLabelStyle prototype = new DefaultLabelStyle();
    autoFlippingEnabled = prototype.isAutoFlippingEnabled();
    backgroundPaint = prototype.getBackgroundPaint();
    backgroundPen = prototype.getBackgroundPen();
    flowDirection = NodeOrientation.LEFT_TO_RIGHT;
    font = prototype.getFont();
    fontSmoothingType = prototype.getFontSmoothingType();
    insets = prototype.getInsets();
    lineSpacing = prototype.getLineSpacing();
    strikethrough = prototype.isStrikethrough();
    textAlignment = prototype.getTextAlignment();
    textClippingEnabled = prototype.isTextClippingEnabled();
    textPaint = prototype.getTextPaint();
    textWrapping = prototype.getTextWrapping();
    underline = prototype.isUnderline();
    verticalTextAlignment = prototype.getVerticalTextAlignment();
  }

  /**
   * Handles the GraphML alias <code>AutoFlip</code> used in yFiles for
   * JavaFX 3.0.x for property <code>AutoFlippingEnabled</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isAutoFlippingEnabled()
   */
  public boolean isAutoFlip() {
    return isAutoFlippingEnabled();
  }

  /**
   * Handles the GraphML alias <code>AutoFlip</code> used in yFiles for
   * JavaFX 3.0.x for property <code>AutoFlippingEnabled</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setAutoFlippingEnabled(boolean)
   */
  public void setAutoFlip( boolean value ) {
    setAutoFlippingEnabled(value);
  }

  public boolean isAutoFlippingEnabled() {
    return autoFlippingEnabled;
  }

  public void setAutoFlippingEnabled( boolean value ) {
    autoFlippingEnabled = value;
  }

  /**
   * Handles the GraphML alias <code>BackgroundBrush</code> used in yFiles for
   * JavaFX 3.0.x for property <code>BackgroundPaint</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isAutoFlippingEnabled()
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
    backgroundPaint = value;
  }

  public Pen getBackgroundPen() {
    return backgroundPen;
  }

  public void setBackgroundPen( Pen value ) {
    backgroundPen = value;
  }

  /**
   * Handles the GraphML alias <code>ClipText</code> used in yFiles for
   * JavaFX 3.0.x for property <code>TextClippingEnabled</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isTextClippingEnabled()
   */
  public boolean isClipText() {
    return isTextClippingEnabled();
  }

  /**
   * Handles the GraphML alias <code>ClipText</code> used in yFiles for
   * JavaFX 3.0.x for property <code>TextClippingEnabled</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setTextClippingEnabled(boolean)
   */
  public void setClipText( boolean value ) {
    setTextClippingEnabled(value);
  }

  public NodeOrientation getFlowDirection() {
    return flowDirection;
  }

  public void setFlowDirection( NodeOrientation value ) {
    flowDirection = value;
  }

  public Font getFont() {
    return font;
  }

  public void setFont( Font value ) {
    this.font = value;
  }

  public FontSmoothingType getFontSmoothingType() {
    return fontSmoothingType;
  }

  public void setFontSmoothingType( FontSmoothingType value ) {
    this.fontSmoothingType = value;
  }

  public InsetsD getInsets() {
    return insets;
  }

  public void setInsets( InsetsD value ) {
    insets = value;
  }

  public double getLineSpacing() {
    return lineSpacing;
  }

  public void setLineSpacing( double value ) {
    lineSpacing = value;
  }

  public boolean isStrikethrough() {
    return strikethrough;
  }

  public void setStrikethrough( boolean value ) {
    strikethrough = value;
  }

  public TextAlignment getTextAlignment() {
    return textAlignment;
  }

  public void setTextAlignment( TextAlignment value ) {
    this.textAlignment = value;
  }

  /**
   * Handles the GraphML alias <code>TextBrush</code> used in yFiles for
   * JavaFX 3.0.x for property <code>TextPaint</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getTextPaint()
   */
  public Paint getTextBrush() {
    return getTextPaint();
  }

  /**
   * Handles the GraphML alias <code>TextBrush</code> used in yFiles for
   * JavaFX 3.0.x for property <code>TextPaint</code>.
   * yFiles for JavaFX 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setTextPaint(Paint)
   */
  public void setTextBrush( Paint value ) {
    setTextPaint(value);
  }

  public boolean isTextClippingEnabled() {
    return textClippingEnabled;
  }

  public void setTextClippingEnabled( boolean value ) {
    textClippingEnabled = value;
  }

  public Paint getTextPaint() {
    return textPaint;
  }

  public void setTextPaint( Paint value ) {
    textPaint = value;
  }

  public TextWrapping getTextWrapping() {
    return textWrapping;
  }

  public void setTextWrapping( TextWrapping value ) {
    this.textWrapping = value;
  }

  public boolean isUnderline() {
    return underline;
  }

  public void setUnderline( boolean value ) {
    underline = value;
  }

  public VPos getVerticalTextAlignment() {
    return verticalTextAlignment;
  }

  public void setVerticalTextAlignment( VPos value ) {
    verticalTextAlignment = value;
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setAutoFlippingEnabled(isAutoFlippingEnabled());
    style.setBackgroundPaint(getBackgroundPaint());
    style.setBackgroundPen(getBackgroundPen());
    style.setFont(getFont());
    style.setFontSmoothingType(getFontSmoothingType());
    style.setInsets(getInsets());
    style.setLineSpacing(getLineSpacing());
    style.setStrikethrough(isStrikethrough());
    style.setTextAlignment(getTextAlignment());
    style.setTextClippingEnabled(isTextClippingEnabled());
    style.setTextPaint(getTextPaint());
    style.setTextWrapping(getTextWrapping());
    style.setUnderline(isUnderline());
    style.setVerticalTextAlignment(getVerticalTextAlignment());
    return style;
  }
}
