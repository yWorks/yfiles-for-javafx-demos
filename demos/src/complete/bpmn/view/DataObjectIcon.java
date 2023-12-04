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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;

class DataObjectIcon extends AbstractIcon {
  private Paint paint;

  final Paint getPaint() {
    return this.paint;
  }

  final void setPaint( Paint value ) {
    this.paint = value;
  }

  private Pen pen;

  final Pen getPen() {
    return this.pen;
  }

  final void setPen( Pen value ) {
    this.pen = value;
  }

  @Override
  public Node createVisual( IRenderContext context ) {
    VisualGroup container = new VisualGroup();

    IRectangle bounds = getBounds();
    double width = bounds.getWidth();
    double height = bounds.getHeight();
    double cornerSize = Math.min(width, height) * 0.4;

    GeneralPath path = new GeneralPath();
    path.moveTo(0, 0);
    path.lineTo(width - cornerSize, 0);
    path.lineTo(width, cornerSize);
    path.lineTo(width, height);
    path.lineTo(0, height);
    path.close();
    final Path pathNode = path.createPath(new Matrix2D());
    container.add(pathNode);

    path = new GeneralPath();
    path.moveTo(width - cornerSize, 0);
    path.lineTo(width - cornerSize, cornerSize);
    path.lineTo(width, cornerSize);
    final Path pathNode2 = path.createPath(new Matrix2D());
    container.add(pathNode2);

    if (getPaint() != null) {
      pathNode.setFill(getPaint());
      pathNode2.setFill(getPaint());
    }
    if (getPen() != null) {
      getPen().styleShape(pathNode);
      getPen().styleShape(pathNode2);
    }

    container.setLayoutX(bounds.getX());
    container.setLayoutY(bounds.getY());
    container.setUserData(new PathIconState(width, height, getPen(), getPaint()));
    return container;
  }

  @Override
  public Node updateVisual( IRenderContext context, Node oldVisual ) {
    return createVisual(context);
  }

}
