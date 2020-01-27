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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import java.util.List;
import javafx.scene.Node;

class LineUpIcon extends AbstractIcon {
  private final List<IIcon> icons;

  private final SizeD innerIconSize;

  private final double gap;

  private final SizeD combinedSize;

  public LineUpIcon( List<IIcon> icons, SizeD innerIconSize, double gap ) {
    this.icons = icons;
    this.innerIconSize = innerIconSize;
    this.gap = gap;

    double combinedWidth = icons.size() * innerIconSize.width + (icons.size() - 1) * gap;
    combinedSize = new SizeD(combinedWidth, innerIconSize.height);
  }

  @Override
  public Node createVisual( IRenderContext context ) {
    if (getBounds() == null) {
      return null;
    }

    VisualGroup container = new VisualGroup();

    double offset = 0;
    for (IIcon pathIcon : icons) {
      pathIcon.setBounds(new RectD(offset, 0, innerIconSize.width, innerIconSize.height));
      container.add(pathIcon.createVisual(context));
      offset += innerIconSize.width + gap;
    }
    container.setLayoutX(getBounds().getX());
    container.setLayoutY(getBounds().getY());
    container.setUserData(getBounds().getTopLeft());
    return container;
  }

  @Override
  public Node updateVisual( IRenderContext context, Node oldVisual ) {
    VisualGroup container = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    if (container == null || container.getNullableChildren().size() != icons.size()) {
      return createVisual(context);
    }

    PointD cache = (PointD) container.getUserData();
    if (!PointD.equals(cache, getBounds().getTopLeft())) {
      container.setLayoutX(getBounds().getX());
      container.setLayoutY(getBounds().getY());
      container.setUserData(getBounds().getTopLeft());
    }
    for (int i = 0; i < icons.size(); i++) {
      Node oldIconVisual = container.getNullableChildren().get(i);
      Node newIconVisual = icons.get(i).updateVisual(context, oldIconVisual);
      if (oldIconVisual != newIconVisual) {
        container.getNullableChildren().set(i, newIconVisual);
      }
    }
    return container;
  }

  @Override
  public void setBounds( IRectangle bounds ) {
    super.setBounds(RectD.fromCenter(bounds.getCenter(), combinedSize));
  }

}
