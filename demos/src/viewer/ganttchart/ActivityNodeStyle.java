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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;


/**
 * Creates stadium-shaped visualizations for activity nodes.
 * The visualizations include hatched sections for lead and follow-up time.
 */
public class ActivityNodeStyle extends AbstractNodeStyle {

  /**
   * Creates a stadium-shaped visualization for the given activity node.
   * The visualization includes hatched sections for lead and follow-up time.
   */
  @Override
  protected Node createVisual(IRenderContext context, INode node ) {
    Activity activity = (Activity) node.getTag();
    IRectangle layout = node.getLayout();

    Color nodeColor = activity.getTask().getColor();

    // create the outline for an activity node (with lead and follow up time)
    double r = Math.min(layout.getWidth(), layout.getHeight());
    Rectangle shape = new Rectangle(layout.getX(), layout.getY(), layout.getWidth(), layout.getHeight());
    shape.setArcWidth(r);
    shape.setArcHeight(r);
    shape.setStroke(nodeColor);
    shape.setStrokeWidth(2);
    shape.setFill(Color.TRANSPARENT);

    VisualGroup visualGroup = new VisualGroup();
    visualGroup.add(shape);

    double minX = shape.getX();
    double minY = shape.getY();
    double w = shape.getWidth();
    double h = shape.getHeight();
    double maxX = minX + w;
    double maxY = minY + h;

    double offset = 8;

    double leadTimeWidth = activity.leadTimeWidth();
    double followUpTimeWidth = activity.followUpTimeWidth();
    if (leadTimeWidth > 0 || followUpTimeWidth > 0) {
      // create the hatches in the lead and follow-up time sections
      Path hatchLine = new Path();
      hatchLine.setStrokeWidth(1);
      hatchLine.setStroke(nodeColor);

      ObservableList<PathElement> hatchLineElements = hatchLine.getElements();
      for (double x = minX - h; x < maxX; x += offset){
        hatchLineElements.add(new MoveTo(x, maxY));
        hatchLineElements.add(new LineTo(x+h, minY));
      }

      Rectangle clip = new Rectangle(minX, minY, w, h);
      clip.setArcWidth(r);
      clip.setArcHeight(r);
      visualGroup.setClip(clip);

      visualGroup.add(hatchLine);

      // fill the main section, i.e. the area between lead and follow-up time
      Rectangle mainSection = new Rectangle(
        minX + leadTimeWidth, minY,
        w - leadTimeWidth - followUpTimeWidth, h);
      mainSection.setFill(nodeColor);

      visualGroup.add(mainSection);
    } else {
      shape.setFill(nodeColor);
    }

    return visualGroup;
  }

  /**
   * Returns the stadium-shaped outline of the given activity node.
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    IRectangle nl = node.getLayout();
    double x = nl.getX();
    double y = nl.getY();
    double width = nl.getWidth();
    double height = nl.getHeight();
    double arcX = Math.min(width, height) * 0.5;
    double arcY = arcX;

    GeneralPath outline = new GeneralPath(12);
    outline.moveTo(x, y + arcY);
    outline.quadTo(x, y, x + arcX, y);
    outline.lineTo(x + width - arcX, y);
    outline.quadTo(x + width, y, x + width, y + arcY);
    outline.lineTo(x + width, y + height - arcY);
    outline.quadTo(x + width, y + height, x + width - arcX, y + height);
    outline.lineTo(x + arcX, y + height);
    outline.quadTo(x, y + height, x, y + height - arcY);
    outline.close();
    return outline;
  }
}
