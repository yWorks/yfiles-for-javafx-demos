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
package com.yworks.yedlite.parts;


import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Pen;

import org.eclipse.swt.widgets.Composite;

import javafx.scene.paint.Color;

/**
 * A palette view part that populates its palette with nodes with style {@link ShapeNodeStyle}.
 */
public class ShapeNodePalettePart extends PaletteViewPart {

  @SuppressWarnings("unchecked")
  @Override
  protected void populatePalette(Composite composite) {
    populateNodePalette(composite, PaletteViewPart.NORMAL_NODES_SELECTION_TYPE,
        new NamedStyle<INodeStyle>("Rectangle", "Rectangle",
            newShapeNodeStyle(ShapeNodeShape.RECTANGLE, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("RoundRectangle", "Rounded Rectangle",
            newShapeNodeStyle(ShapeNodeShape.ROUND_RECTANGLE, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Ellipse", "Ellipse",
            newShapeNodeStyle(ShapeNodeShape.ELLIPSE, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Hexagon", "Hexagon",
            newShapeNodeStyle(ShapeNodeShape.HEXAGON, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Octagon", "Octagon",
            newShapeNodeStyle(ShapeNodeShape.OCTAGON, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("ShearedRectangle", "Sheared Rectangle",
            newShapeNodeStyle(ShapeNodeShape.SHEARED_RECTANGLE, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("ShearedRectangle2", "Sheared Rectangle",
            newShapeNodeStyle(ShapeNodeShape.SHEARED_RECTANGLE2, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Star5", "Star",
            newShapeNodeStyle(ShapeNodeShape.STAR5, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Star6", "Star",
            newShapeNodeStyle(ShapeNodeShape.STAR6, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Star8", "Star",
            newShapeNodeStyle(ShapeNodeShape.STAR8, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Trapez", "Trapez",
            newShapeNodeStyle(ShapeNodeShape.TRAPEZ, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Trapez2", "Trapez",
            newShapeNodeStyle(ShapeNodeShape.TRAPEZ2, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Triangle", "Triangle",
            newShapeNodeStyle(ShapeNodeShape.TRIANGLE, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Triangle2", "Triangle",
            newShapeNodeStyle(ShapeNodeShape.TRIANGLE2, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("FatArrow", "Fat Arrow",
            newShapeNodeStyle(ShapeNodeShape.FAT_ARROW, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("FatArrow2", "Fat Arrow",
            newShapeNodeStyle(ShapeNodeShape.FAT_ARROW2, Pen.getBlack(), Color.ORANGE)),
        new NamedStyle<INodeStyle>("Diamond", "Diamond",
            newShapeNodeStyle(ShapeNodeShape.DIAMOND, Pen.getBlack(), Color.ORANGE)));
  }

  private static ShapeNodeStyle newShapeNodeStyle(
          ShapeNodeShape shape, Pen pen, Color color
  ) {
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setPen(pen);
    style.setPaint(color);
    style.setShape(shape);
    return style;
  }
}
