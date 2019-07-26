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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.MatrixOrder;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;

class PathIcon extends AbstractIcon {
  private Paint paint;

  public final Paint getPaint() {
    return this.paint;
  }

  public final void setPaint( Paint value ) {
    this.paint = value;
  }

  private Pen pen;

  public final Pen getPen() {
    return this.pen;
  }

  public final void setPen( Pen value ) {
    this.pen = value;
  }

  private GeneralPath path;

  final GeneralPath getPath() {
    return this.path;
  }

  final void setPath( GeneralPath value ) {
    this.path = value;
  }

  @Override
  public Node createVisual( IRenderContext context ) {
    VisualGroup container = new VisualGroup();

    Matrix2D matrix2D = new Matrix2D();
    matrix2D.scale(Math.max(0, getBounds().getWidth()), Math.max(0, getBounds().getHeight()), MatrixOrder.PREPEND);

    Path visual = getPath().createPath(matrix2D);
    if (getPaint() != null) {
      visual.setFill(getPaint());
    }
    if (getPen() != null) {
      getPen().styleShape(visual);
    }

    container.add(visual);

    container.setLayoutX(getBounds().getX());
    container.setLayoutY(getBounds().getY());
    container.setUserData(new PathIconState(getBounds().getWidth(), getBounds().getHeight(), getPen(), getPaint()));
    return container;
  }

  @Override
  public Node updateVisual( IRenderContext context, Node oldVisual ) {
    VisualGroup container = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    if (container == null || container.getNullableChildren().size() != 1) {
      return createVisual(context);
    }
    Node tmp = container.getNullableChildren().get(0);
    Path path = (tmp instanceof Path) ? (Path)tmp : null;
    PathIconState lastState = (PathIconState) container.getUserData();
    if (path == null || lastState == null || lastState.width != getBounds().getWidth() || lastState.height != getBounds().getHeight()) {
      return createVisual(context);
    }

    if (!Objects.equals(lastState.pen, getPen())) {
      path.setStroke(getPen().getPaint());
    }
    if (!Objects.equals(lastState.paint, getPaint())) {
      path.setFill(getPaint());
    }

    // arrange visual
    container.setLayoutX(getBounds().getX());
    container.setLayoutY(getBounds().getY());
    container.setUserData(new PathIconState(getBounds().getWidth(), getBounds().getHeight(), getPen(), getPaint()));
    return container;
  }

}
