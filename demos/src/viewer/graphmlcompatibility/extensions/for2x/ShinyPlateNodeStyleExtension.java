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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;
import javafx.scene.paint.Paint;

/**
 * Handles the yFiles for JavaFX 2.0.x version of GraphML element
 * <code>&lt;ShinyPlateNodeStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class ShinyPlateNodeStyleExtension extends MarkupExtension {
  private boolean drawingShadow;
  private InsetsD insets;
  private Paint paint;
  private Pen pen;
  private double radius;

  public ShinyPlateNodeStyleExtension() {
    ShinyPlateNodeStyle prototype = new ShinyPlateNodeStyle();
    this.drawingShadow = prototype.isShadowDrawingEnabled();
    this.insets = prototype.getInsets();
    this.paint = prototype.getPaint();
    this.pen = prototype.getPen();
    this.radius = prototype.getRadius();
  }

  /**
   * Handles the GraphML alias <code>Brush</code> used in yFiles for
   * JavaFX 2.0.x for property <code>Paint</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getPaint()
   */
  public Paint getBrush() {
    return getPaint();
  }

  /**
   * Handles the GraphML alias <code>Brush</code> used in yFiles for
   * JavaFX 2.0.x for property <code>Paint</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setPaint(Paint)
   */
  public void setBrush( Paint value ) {
    setPaint(value);
  }

  /**
   * Handles the GraphML alias <code>DrawShadow</code> used in yFiles for
   * JavaFX 2.0.x for property <code>DrawingShadow</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isDrawingShadow()
   */
  public boolean isDrawShadow() {
    return isDrawingShadow();
  }

  /**
   * Handles the GraphML alias <code>DrawShadow</code> used in yFiles for
   * JavaFX 2.0.x for property <code>DrawingShadow</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setDrawingShadow(boolean)
   */
  public void setDrawShadow( boolean value ) {
    setDrawingShadow(value);
  }

  public boolean isDrawingShadow() {
    return drawingShadow;
  }

  public void setDrawingShadow( boolean value ) {
    this.drawingShadow = value;
  }

  public InsetsD getInsets() {
    return insets;
  }

  public void setInsets( InsetsD value ) {
    this.insets = value;
  }

  public Paint getPaint() {
    return paint;
  }

  public void setPaint( Paint value ) {
    this.paint = value;
  }

  public Pen getPen() {
    return pen;
  }

  public void setPen( Pen value ) {
    this.pen = value;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius( double value ) {
    this.radius = value;
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    ShinyPlateNodeStyle style = new ShinyPlateNodeStyle();
    style.setInsets(getInsets());
    style.setPaint(getPaint());
    style.setPen(getPen());
    style.setRadius(getRadius());
    style.setShadowDrawingEnabled(isDrawingShadow());
    return style;
  }
}
