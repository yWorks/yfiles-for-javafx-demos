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
package com.yworks.yedlite.parts;

import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.Pen;
import org.eclipse.swt.widgets.Composite;
import javafx.scene.paint.Color;

/**
 * A palette view part that populates its palette with edges.
 */
public class EdgePalettePart extends PaletteViewPart {

  @SuppressWarnings("unchecked")
  @Override
  protected void populatePalette(Composite composite) {
    populateEdgePalette(composite, EDGES_SELECTION_TYPE,
        new NamedStyle<IEdgeStyle>("Line1", "Line Thin", newPolylineEdgeStyle(DashStyle.getSolid(), 1.0)),
        new NamedStyle<IEdgeStyle>("Line2", "Line Medium", newPolylineEdgeStyle(DashStyle.getSolid(), 2.0)),
        new NamedStyle<IEdgeStyle>("Line3", "Line Thick", newPolylineEdgeStyle(DashStyle.getSolid(), 5.0)),
        new NamedStyle<IEdgeStyle>("Dashed1", "Dashed Thin", newPolylineEdgeStyle(DashStyle.getDash(), 1.0)),
        new NamedStyle<IEdgeStyle>("Dashed2", "Dashed Medium", newPolylineEdgeStyle(DashStyle.getDash(), 2.0)),
        new NamedStyle<IEdgeStyle>("Dashed3", "Dashed Thick", newPolylineEdgeStyle(DashStyle.getDash(), 5.0)),
        new NamedStyle<IEdgeStyle>("Dotted1", "Dotted Thin", newPolylineEdgeStyle(DashStyle.getDot(), 1.0)),
        new NamedStyle<IEdgeStyle>("Dotted2", "Dotted Medium", newPolylineEdgeStyle(DashStyle.getDot(), 2.0)),
        new NamedStyle<IEdgeStyle>("Dotted3", "Dotted Thick", newPolylineEdgeStyle(DashStyle.getDot(), 5.0))
    );
  }

  private static PolylineEdgeStyle newPolylineEdgeStyle(DashStyle dashStyle, double thickness) {
    final Color color = Color.BLACK;
    final Pen pen = new Pen(color);
    pen.setDashStyle(dashStyle);
    pen.setThickness(thickness);
    final PolylineEdgeStyle style = new PolylineEdgeStyle();
    style.setPen(pen);
    style.setTargetArrow(IArrow.DEFAULT);
    return style;
  }
}
