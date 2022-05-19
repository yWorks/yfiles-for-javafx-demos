/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package input.labelhandleprovider;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;


/**
 * Custom selection style implementation for rotatable labels.
 * Draws a box around labels and adds a line to the corresponding rotation handle.
 */
public class LabelSelectionStyle extends AbstractLabelStyle {
  @Override
  protected Node createVisual( IRenderContext context, ILabel label ) {
    IOrientedRectangle lb = label.getLayout();
    Pen borderPen = new Pen(Color.LIGHTGRAY, 3);

    VisualGroup group = new VisualGroup();
    // add the selection outline
    Path path = createPath(lb);
    borderPen.styleShape(path);
    group.add(path);

    // calculate the path to the rotateHandle and add it to the visual group
    double distToHandle = 20;
    double x1 = lb.getAnchorX() + lb.getUpX() * lb.getHeight() - lb.getUpY() * lb.getWidth() * 0.5;
    double y1 = lb.getAnchorY() + lb.getUpY() * lb.getHeight() + lb.getUpX() * lb.getWidth() * 0.5;
    double x2 = x1 + lb.getUpX() * distToHandle;
    double y2 = y1 + lb.getUpY() * distToHandle;
    Line line = new Line(x1, y1, x2, y2);
    borderPen.styleShape(line);
    group.add(line);

    return group;
  }

  @Override
  protected SizeD getPreferredSize( ILabel label ) {
    IOrientedRectangle lb = label.getLayout();
    return new SizeD(lb.getWidth(), lb.getHeight());
  }

  /**
   * Creates the path of the selection outline.
   */
  private static Path createPath( IOrientedRectangle lb ) {
    Path path = new Path();
    path.getElements().add(new MoveTo(lb.getAnchorX(), lb.getAnchorY()));

    double x2 = lb.getAnchorX() + lb.getUpX() * lb.getHeight();
    double y2 = lb.getAnchorY() + lb.getUpY() * lb.getHeight();
    path.getElements().add(new LineTo(x2, y2));

    double x3 = x2 - lb.getUpY() * lb.getWidth();
    double y3 = y2 + lb.getUpX() * lb.getWidth();
    path.getElements().add(new LineTo(x3, y3));

    double x4 = x3 - lb.getUpX() * lb.getHeight();
    double y4 = y3 - lb.getUpY() * lb.getHeight();
    path.getElements().add(new LineTo(x4, y4));

    path.getElements().add(new ClosePath());
    return path;
  }
}
