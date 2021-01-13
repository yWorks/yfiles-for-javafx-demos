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
package complete.tableeditor;

import com.yworks.yfiles.geometry.InsetsD;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Helper class to bundle common visualization parameters for {@link com.yworks.yfiles.graph.IStripe} in {@link StripeDescriptor}.
 */
public class StripeDescriptor {
  // the paint to fill the background of a stripe with
  private Paint backgroundPaint = Color.TRANSPARENT;
  // the paint to fill the inset of a stripe with
  private Paint insetPaint = Color.TRANSPARENT;
  // the paint to draw the border of a stripe with
  private Paint borderPaint = Color.BLACK;
  // the thickness of a stripe's border
  private InsetsD borderThickness = new InsetsD(1);

  /**
   * Initializes a new <code>StripeDescriptor</code> instance.
   */
  public StripeDescriptor() {
  }

  /**
   * Initializes a new <code>StripeDescriptor</code> instance with the given background and insets paint.
   */
  public StripeDescriptor(Paint backgroundPaint, Paint insetPaint) {
    this.backgroundPaint = backgroundPaint;
    this.insetPaint = insetPaint;
  }

  /**
   * Returns the background {@link Paint} for a stripe
   */
  public Paint getBackgroundPaint() {
    return backgroundPaint;
  }

  /**
   * Sets the background {@link Paint} for a stripe
   */
  public void setBackgroundPaint(Paint backgroundPaint) {
    this.backgroundPaint = backgroundPaint;
  }

  /**
   * Returns the inset {@link Paint} for a stripe
   */
  public Paint getInsetPaint() {
    return insetPaint;
  }

  /**
   * Sets the inset {@link Paint} for a stripe
   */
  public void setInsetPaint(Paint insetPaint) {
    this.insetPaint = insetPaint;
  }

  /**
   * Returns the border {@link Paint} for a stripe
   */
  public Paint getBorderPaint() {
    return borderPaint;
  }

  /**
   * Sets the border {@link Paint} for a stripe
   */
  public void setBorderPaint(Paint borderPaint) {
    this.borderPaint = borderPaint;
  }

  /**
   * Returns the thickness of the border for a stripe
   */
  public InsetsD getBorderThickness() {
    return borderThickness;
  }

  /**
   * Sets the thickness of the border  for a stripe
   */
  public void setBorderThickness(InsetsD borderThickness) {
    this.borderThickness = borderThickness;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StripeDescriptor that = (StripeDescriptor) o;

    if (backgroundPaint != null ? !backgroundPaint.equals(that.backgroundPaint) : that.backgroundPaint != null) return false;
    if (borderPaint != null ? !borderPaint.equals(that.borderPaint) : that.borderPaint != null) return false;
    if (borderThickness != null ? !borderThickness.equals(that.borderThickness) : that.borderThickness != null) return false;
    if (insetPaint != null ? !insetPaint.equals(that.insetPaint) : that.insetPaint != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = backgroundPaint != null ? backgroundPaint.hashCode() : 0;
    result = 31 * result + (insetPaint != null ? insetPaint.hashCode() : 0);
    result = 31 * result + (borderPaint != null ? borderPaint.hashCode() : 0);
    result = 31 * result + (borderThickness != null ? borderThickness.hashCode() : 0);
    return result;
  }
}
