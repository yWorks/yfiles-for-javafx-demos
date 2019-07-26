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

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

class RectIcon extends AbstractIcon {
  private double cornerRadius;

  final double getCornerRadius() {
    return this.cornerRadius;
  }

  final void setCornerRadius( double value ) {
    this.cornerRadius = value;
  }

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
    Rectangle rectangle = new Rectangle(bounds.getWidth(), bounds.getHeight());
    rectangle.setArcWidth(getCornerRadius());
    rectangle.setArcHeight(getCornerRadius());
    updateRectangle(rectangle);
    rectangle.setLayoutX(bounds.getX());
    rectangle.setLayoutY(bounds.getY());

    container.add(rectangle);
    container.setUserData(new PathIconState(bounds.getWidth(), bounds.getHeight(), getPen(), getPaint()));
    return container;
  }

  @Override
  public Node updateVisual( IRenderContext context, Node oldVisual ) {
    VisualGroup container = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    if (container == null || container.getNullableChildren().size() != 1) {
      return createVisual(context);
    }
    Rectangle rectangle = (Rectangle) container.getNullableChildren().get(0);
    PathIconState lastState = (PathIconState) container.getUserData();

    if (rectangle == null || lastState == null) {
      container.getNullableChildren().clear();
      return createVisual(context);
    }

    IRectangle bounds = getBounds();
    if (!lastState.equals(bounds.getWidth(), bounds.getHeight(), getPen(), getPaint())) {
      updateRectangle(rectangle);
      container.setUserData(new PathIconState(bounds.getWidth(), bounds.getHeight(), getPen(), getPaint()));
    }

    // arrange
    rectangle.setLayoutX(bounds.getX());
    rectangle.setLayoutY(bounds.getY());
    return container;
  }

  private void updateRectangle(Rectangle rectangle) {
    IRectangle bounds = getBounds();
    rectangle.setWidth(bounds.getWidth());
    rectangle.setHeight(bounds.getHeight());
    rectangle.setFill(getPaint());
    if (pen != null) {
      pen.styleShape(rectangle);
    }
  }
}
