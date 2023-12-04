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
package analysis.networkflows;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A NetworkFlowNodeStyle represents the flow that is regulated at the according node.
 * By setting a tag, the flow can be adjusted for this node.
 */
public class NetworkFlowNodeStyle extends AbstractNodeStyle {
  @Override
  protected Node createVisual(IRenderContext context, INode node) {
    NetworkFlowNodeVisual visual = new NetworkFlowNodeVisual();
    visual.update(context, node);
    return visual;
  }

  @Override
  protected Node updateVisual(IRenderContext context, Node oldVisual, INode node) {
    if (oldVisual instanceof NetworkFlowNodeVisual) {
      ((NetworkFlowNodeVisual) oldVisual).update(context, node);
      return oldVisual;
    }
    return createVisual(context, node);
  }

  /**
   * The visual rendering the network flow node.
   */
  private static final class NetworkFlowNodeVisual extends VisualGroup {
    private static final Color SOURCE_BORDER_COLOR = Color.YELLOWGREEN;
    private static final Color SINK_BORDER_COLOR = Color.INDIANRED;
    private static final Color BORDER_COLOR = Color.rgb(200, 200, 200);
    private static final double BORDER_STROKE = 3;
    private static final Color FLOW_COLOR_1 = Color.DARKBLUE;
    private static final Color FLOW_COLOR_2 = Color.CORNFLOWERBLUE;
    private static final Color BACKGROUND_COLOR = Color.LIGHTGRAY;
    private static final double INSET = 15;

    private final Line pipeBorderLeft;
    private final Line pipeBorderRight;
    private final Path pipePath;
    private final Rectangle infoShapeBackground;
    private final Rectangle infoShapeIncoming;
    private final Rectangle infoShapeFlow;
    private final Rectangle infoShapeBorder;
    private final Text text;

    private RectD bounds;
    private double supply;
    private double flow;
    private boolean source;
    private boolean sink;

    NetworkFlowNodeVisual() {
      pipePath = new Path();
      pipePath.setFill(BACKGROUND_COLOR);
      pipePath.setStroke(BACKGROUND_COLOR);
      this.add(pipePath);
      pipeBorderLeft = new Line(0, 0, 0, 0);
      pipeBorderLeft.setStroke(BORDER_COLOR);
      pipeBorderLeft.setStrokeWidth(BORDER_STROKE);
      this.add(pipeBorderLeft);
      pipeBorderRight = new Line();
      pipeBorderRight.setStroke(BORDER_COLOR);
      pipeBorderRight.setStrokeWidth(BORDER_STROKE);
      this.add(pipeBorderRight);

      infoShapeBackground = new Rectangle(INSET, 0, 0, 0);
      infoShapeBackground.setFill(BACKGROUND_COLOR);
      this.add(infoShapeBackground);
      infoShapeIncoming = new Rectangle(INSET, 0, 0, 0);
      this.add(infoShapeIncoming);
      infoShapeFlow = new Rectangle(INSET, 0, 0, 0);
      this.add(infoShapeFlow);
      infoShapeBorder = new Rectangle(INSET, 0, 0, 0);
      infoShapeBorder.setFill(Color.TRANSPARENT);
      infoShapeBorder.setStrokeWidth(BORDER_STROKE);
      this.add(infoShapeBorder);

      text = new Text();
      text.setFont(new Font(12));
      this.add(text);

      bounds = RectD.EMPTY;
    }


    void update(IRenderContext context, INode node) {
      bounds = node.getLayout().toRectD();
      this.setTranslateX(node.getLayout().getX());
      this.setTranslateY(node.getLayout().getY());

      NodeData flowData = (NodeData) node.getTag();
      supply = flowData.getSupply();
      flow = flowData.getFlow();

      IGraph graph = ((GraphControl) context.getCanvasControl()).getGraph();
      int inDegree = graph.inDegree(node);
      int outDegree = graph.outDegree(node);
      source = inDegree == 0 && outDegree != 0;
      sink = inDegree != 0 && outDegree == 0;

      // update pipe path
      double w = bounds.getWidth();
      double h = bounds.getHeight();

      updatePipe(w, h);

      updateInfoBox(w, h);

      updateInfoText(w, h);
    }

    private void updatePipe(double w, double h) {
      pipePath.getElements().clear();
      if (source) {
        setHalfPipePath(w, w, h);
      } else if (sink) {
        setHalfPipePath(0, w, h);
      } else {
        setFullPipePath(w, h);
      }
      pipeBorderLeft.setEndY(bounds.getHeight());
      pipeBorderLeft.setVisible(!source);
      pipeBorderRight.setStartX(bounds.getWidth());
      pipeBorderRight.setEndX(bounds.getWidth());
      pipeBorderRight.setEndY(bounds.getHeight());
      pipeBorderRight.setVisible(!sink);
    }

    private void setHalfPipePath(double endX, double w, double h) {
      double x1 = w * 0.5;
      double y1 = h * 0.333;
      double x2 = endX;
      double y2 = 5;

      pipePath.getElements().add(new MoveTo(x1, y1));
      pipePath.getElements().add(new QuadCurveTo((x1 + x2) * 0.5, y1, x2, y2));

      double x3 = endX;
      double y3 = h - 5;
      double x4 = w * 0.5;
      double y4 = h * 0.666;

      pipePath.getElements().add(new LineTo(x3, y3));
      pipePath.getElements().add(new QuadCurveTo((x3 + x4) * 0.5, y4, x4, y4));
      pipePath.getElements().add(new ClosePath());
    }

    private void setFullPipePath(double w, double h) {
      pipePath.getElements().add(new MoveTo(0, 0));
      pipePath.getElements().add(new QuadCurveTo(w * 0.5, h * 0.5, w, 0));
      pipePath.getElements().add(new LineTo(w, h));
      pipePath.getElements().add(new QuadCurveTo(w * 0.5, h * 0.5, 0, h));
      pipePath.getElements().add(new ClosePath());
    }

    private void updateInfoBox(double w, double h) {
      // update flow info
      double infoX = INSET;
      double infoWidth = w - 2 * INSET;
      final double supplyHeight = h * supply;
      infoShapeBackground.setWidth(infoWidth);
      infoShapeBackground.setHeight(h);

      // visualize incoming flow
      LinearGradient flowGradient = new LinearGradient(0, 0, 0, h, false, CycleMethod.NO_CYCLE,
          new Stop(0, FLOW_COLOR_1), new Stop(1, FLOW_COLOR_2));
      infoShapeIncoming.setFill(flowGradient);
      infoShapeIncoming.setY(h - flow);
      infoShapeIncoming.setWidth(infoWidth);
      infoShapeIncoming.setHeight(flow);

      // visualize supply/demand flow for min cost algorithm
      if (0 < supplyHeight) {
        infoShapeFlow.setFill(FLOW_COLOR_1);
        infoShapeFlow.setY(h - supplyHeight);
        infoShapeFlow.setWidth(infoWidth);
        infoShapeFlow.setHeight(supplyHeight);
        infoShapeFlow.setVisible(true);
      } else if (supplyHeight < 0) {
        infoShapeFlow.setFill(FLOW_COLOR_2);
        infoShapeFlow.setY(h - flow);
        infoShapeFlow.setWidth(infoWidth);
        infoShapeFlow.setHeight(Math.min(flow, -supplyHeight));
        infoShapeFlow.setVisible(true);
      } else {
        infoShapeFlow.setVisible(false);
      }

      // paint border
      infoShapeBorder.setWidth(infoWidth);
      infoShapeBorder.setHeight(h);
      if (source) {
        infoShapeBorder.setStroke(SOURCE_BORDER_COLOR);
      } else if (sink) {
        infoShapeBorder.setStroke(SINK_BORDER_COLOR);
      } else {
        infoShapeBorder.setStroke(BORDER_COLOR);
      }
    }

    private void updateInfoText(double w, double h) {
      // update text
      String textContent = Long.toString(Math.round(flow));
      text.setText(textContent);
      Bounds textBounds = text.getLayoutBounds();
      double textPrefWidth = textBounds.getWidth();
      double textPrefHeight = textBounds.getHeight();
      text.setFill(flow > textPrefHeight ? Color.WHITE : Color.BLACK);
      text.setTranslateX((w - textPrefWidth) * 0.5);
      text.setTranslateY(h - textPrefHeight * 0.5);
    }


  }
}
