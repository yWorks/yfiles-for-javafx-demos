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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;

/**
 * Handles the yFiles for JavaFX 2.0.x version of GraphML element
 * <code>&lt;PolylineEdgeStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class PolylineEdgeStyleExtension extends MarkupExtension {
  private Pen pen;
  private double smoothing;
  private IArrow sourceArrow;
  private IArrow targetArrow;

  public PolylineEdgeStyleExtension() {
    PolylineEdgeStyle prototype = new PolylineEdgeStyle();
    this.pen = prototype.getPen();
    this.smoothing = prototype.getSmoothingLength();
    this.sourceArrow = prototype.getSourceArrow();
    this.targetArrow = prototype.getTargetArrow();
  }

  public Pen getPen() {
    return pen;
  }

  public void setPen( final Pen pen ) {
    this.pen = pen;
  }

  public double getSmoothing() {
    return smoothing;
  }

  public void setSmoothing( final double smoothing ) {
    this.smoothing = smoothing;
  }

  public IArrow getSourceArrow() {
    return sourceArrow;
  }

  public void setSourceArrow( final IArrow sourceArrow ) {
    this.sourceArrow = sourceArrow;
  }

  public IArrow getTargetArrow() {
    return targetArrow;
  }

  public void setTargetArrow( final IArrow targetArrow ) {
    this.targetArrow = targetArrow;
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    PolylineEdgeStyle style = new PolylineEdgeStyle();
    style.setPen(getPen());
    style.setSmoothingLength(getSmoothing());
    style.setTargetArrow(getTargetArrow());
    style.setSourceArrow(getSourceArrow());
    return style;
  }
}
