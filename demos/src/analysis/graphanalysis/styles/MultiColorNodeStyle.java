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
package analysis.graphanalysis.styles;

import analysis.graphanalysis.ModelItemInfo;
import analysis.graphanalysis.NodeInfo;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Ellipse;

import java.util.ArrayList;
import java.util.List;

/**
 * Visualizes nodes as circles with possible multiple colors to indicate
 * the component or path a given node belongs to.
 */
public class MultiColorNodeStyle extends AbstractNodeStyle {
  @Override
  protected Node createVisual( IRenderContext context, INode node ) {
    Object tag = node.getTag();
    if (tag instanceof NodeInfo) {
      Visual visual = new Visual();
      visual.update(node, (NodeInfo) tag);
      return visual;
    } else {
      return null;
    }
  }

  @Override
  protected Node updateVisual( IRenderContext context, Node oldVisual, INode node ) {
    Object tag = node.getTag();
    if (tag instanceof NodeInfo) {
      Visual visual = oldVisual instanceof Visual ? (Visual) oldVisual : new Visual();
      visual.update(node, (NodeInfo) tag);
      return visual;
    } else {
      return null;
    }
  }

  /**
   * Determines whether or not the given point lies inside the given node's
   * visualization.
   * <p>
   * Necessary for calculating intersection points of nodes and incident edges.
   * </p>
   */
  @Override
  protected boolean isInside( INode node, PointD location ) {
    return GeomUtilities.ellipseContains(node.getLayout().toRectD(), location, 0);
  }

  /**
   * Returns the outline of the given node's visualization.
   * <p>
   * Necessary for calculating intersection points of nodes and incident edges.
   * </p>
   */
  @Override
  protected GeneralPath getOutline( INode node ) {
    RectD bounds = node.getLayout().toRectD();
    GeneralPath outline = new GeneralPath();
    outline.appendEllipse(bounds, false);
    return outline;
  }

  /**
   * Returns the color for the given component.
   * @param componentId The id of the component.
   * @return The color for the component.
   */
  private static Color getColorForComponent( int componentId ) {
    return ModelItemInfo.getComponentColor(componentId);
  }



  private static class Visual extends VisualGroup {
    static final Pen PEN = new Pen(Color.WHITE, 2);

    void update( INode node, NodeInfo info ) {
      IRectangle nl = node.getLayout();
      double x = nl.getX();
      double y = nl.getY();
      double width = nl.getWidth();
      double height = nl.getHeight();

      List<Integer> components = filterSelfloops(info.getNodeComponents());
      if (components.size() < 2) {
        boolean reset = size() != 1;
        if (reset) {
          clear();
        }

        Ellipse shape;
        if (reset) {
          shape = new Ellipse();
          add(shape);
        } else {
          shape = (Ellipse) get(0);
        }
        setFrame(shape, x, y, width, height);

        Color color = info.getColor();
        if (color == null) {
          color = components.isEmpty() ? Color.GRAY : getColorForComponent(components.get(0));
        }
        shape.setFill(color);
      } else {
        int count = components.size();
        boolean reset = size() != count + 1;
        if (reset) {
          clear();
        }

        double angle = 360d / count;
        for (int i = 0; i < count; ++i) {
          Arc shape;
          if (reset) {
            shape = new Arc();
            shape.setType(ArcType.ROUND);
            shape.setStartAngle(i * angle);
            shape.setLength(angle);
            add(shape);
          } else {
            shape = (Arc) get(i);
          }
          setFrame(shape, x, y, width, height);
          shape.setFill(getColorForComponent(components.get(i)));
        }

        Ellipse shape;
        if (reset) {
          shape = new Ellipse();
          add(shape);
        } else {
          shape = (Ellipse) get(count);
        }
        shape.setFill(getColorForComponent(components.get(count - 1)));
        setFrame(shape, x + 5, y + 5, width - 10, height - 10);

        PEN.styleShape(shape);
      }
    }


    /*
     * ###################################################################
     * convenience methods
     * ###################################################################
     */

    void clear() {
      getNullableChildren().clear();
    }

    Node get( int index ) {
      return getNullableChildren().get(index);
    }

    int size() {
      return getNullableChildren().size();
    }


    /*
     * ###################################################################
     * utility methods
     * ###################################################################
     */

    static List<Integer> filterSelfloops(List<Integer> components) {
      if (components == null) {
        return new ArrayList<>();
      }

      List<Integer> result = new ArrayList<>();
      for (Integer component : components) {
        if (component > -1) {
          result.add(component);
        }
      }
      return result;
    }

    private static void setFrame(
            Arc shape, double x, double y, double width, double height
    ) {
      shape.setCenterX(width * 0.5);
      shape.setRadiusX(width * 0.5);
      shape.setCenterY(height * 0.5);
      shape.setRadiusY(height * 0.5);
      shape.setLayoutX(x);
      shape.setLayoutY(y);
    }

    private static void setFrame(
            Ellipse shape, double x, double y, double width, double height
    ) {
      shape.setCenterX(width * 0.5);
      shape.setRadiusX(width * 0.5);
      shape.setCenterY(height * 0.5);
      shape.setRadiusY(height * 0.5);
      shape.setLayoutX(x);
      shape.setLayoutY(y);
    }
  }
}