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

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import java.util.List;
import javafx.scene.Node;

class CombinedIcon extends AbstractIcon {
  private final List<IIcon> icons;

  public CombinedIcon( List<IIcon> icons ) {
    this.icons = icons;
  }

  @Override
  public Node createVisual( IRenderContext context ) {
    if (getBounds() == null) {
      return null;
    }
    VisualGroup container = new VisualGroup();

    RectD iconBounds = new RectD(PointD.ORIGIN, getBounds().toSizeD());
    for (IIcon icon : icons) {
      icon.setBounds(iconBounds);
      container.add(icon.createVisual(context));
    }

    container.setLayoutX(getBounds().getX());
    container.setLayoutY(getBounds().getY());
    container.setUserData(getBounds().toRectD());

    return container;
  }

  @Override
  public Node updateVisual( IRenderContext context, Node oldVisual ) {
    VisualGroup container = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    if (container == null || container.getNullableChildren().size() != icons.size()) {
      return createVisual(context);
    }
    RectD cache = (RectD) container.getUserData();

    if (!SizeD.equals(cache.getSize(), getBounds().toSizeD())) {
      // size changed -> we have to update the icons
      RectD iconBounds = new RectD(PointD.ORIGIN, getBounds().toSizeD());
      int index = 0;
      for (IIcon pathIcon : icons) {
        pathIcon.setBounds(iconBounds);
        Node oldPathVisual = container.getNullableChildren().get(index);
        Node newPathVisual = pathIcon.updateVisual(context, oldPathVisual);
        if (!oldPathVisual.equals(newPathVisual)) {
          newPathVisual = newPathVisual != null ? newPathVisual : new VisualGroup();
          container.getNullableChildren().remove(oldPathVisual);
          container.getNullableChildren().add(index, newPathVisual);
        }
        index++;
      }
    } else if (PointD.equals(cache.getTopLeft(), getBounds().getTopLeft())) {
      // bounds didn't change at all
      return container;
    }

    container.setLayoutX(getBounds().getX());
    container.setLayoutY(getBounds().getY());
    container.setUserData(getBounds().toRectD());

    return container;
  }

}
