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
package style.textwrapping;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdgeDefaults;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.labelmodels.EdgePathLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.TextWrappingShape;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.control.OverrunStyle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.WebViewUtils;

import java.io.IOException;

/**
 * This demo shows different wrapping, overrun style and clipping options for labels using a DefaultLabelStyle.
 */
public class TextWrappingDemo  extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;

  /**
   * Creates a {@link DefaultLabelStyle} with the provided clipping, wrapping and overrun style.
   */
  private DefaultLabelStyle createLabelStyle(boolean clip, TextWrapping textWrapping, OverrunStyle overrunStyle) {
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setTextClippingEnabled(clip);
    style.setTextWrapping(textWrapping);
    style.setTextOverrunStyle(overrunStyle);
    style.setVerticalTextAlignment(VPos.CENTER);
    style.setFont(new Font("Dialog", 15));
    return style;
  }

  /**
   * Creates a {@link DefaultLabelStyle} with the provided overrun style, wrapping shape and insets.
   */
  private DefaultLabelStyle createShapeWrappingLabelStyle(OverrunStyle overrunStyle, TextWrappingShape wrappingShape,
                                                          InsetsD insets) {
    // always enable ellipsis and wrapping
    DefaultLabelStyle style = createLabelStyle(true, TextWrapping.WRAP, overrunStyle);
    style.setTextWrappingShape(wrappingShape);
    style.setTextWrappingPadding(5);
    style.setInsets(insets);
    return style;
  }

  /**
   * Initialize the graph with several nodes with labels using different text wrapping settings.
   */
  private void initializeGraph() {
    // label model and style for the description labels north of the nodes
    ExteriorLabelModel northLabelModel = new ExteriorLabelModel();
    northLabelModel.setInsets(new InsetsD(10));
    ILabelModelParameter northParameter = northLabelModel.createParameter(ExteriorLabelModel.Position.NORTH);
    DefaultLabelStyle northLabelStyle = new DefaultLabelStyle();
    northLabelStyle.setTextAlignment(TextAlignment.CENTER);
    IGraph graph = graphControl.getGraph();
    RectangleNodeStyle defaultNodeStyle = (RectangleNodeStyle) graph.getNodeDefaults().getStyle();

    // create nodes
    INode node1 = graph.createNode(new RectD(0, -450, 190, 200));
    INode node2 = graph.createNode(new RectD(0, -150, 190, 200));
    INode node3 = graph.createNode(new RectD(0, 150, 190, 200));
    INode node4 = graph.createNode(new RectD(250, -150, 190, 200));
    INode node5 = graph.createNode(new RectD(250, 250, 190, 200));
    INode node6 = graph.createNode(new RectD(500, -150, 190, 200));
    INode node7 = graph.createNode(new RectD(500, 150, 190, 200));
    INode node8 = graph.createNode(new RectD(750, -150, 190, 200),
        createShapeNodeStyle(ShapeNodeShape.HEXAGON, defaultNodeStyle));
    INode node9 = graph.createNode(new RectD(750, 150, 190, 200),
        createShapeNodeStyle(ShapeNodeShape.TRIANGLE2, defaultNodeStyle));
    INode node10 = graph.createNode(new RectD(1000, -150, 190, 200),
        createShapeNodeStyle(ShapeNodeShape.OCTAGON, defaultNodeStyle));
    INode node11 = graph.createNode(new RectD(1000, 150, 190, 200),
        createShapeNodeStyle(ShapeNodeShape.ELLIPSE, defaultNodeStyle));

    // use a label model that stretches the label over the full node layout, with small insets
    InteriorStretchLabelModel centerLabelModel = new InteriorStretchLabelModel();
    centerLabelModel.setInsets(new InsetsD(5));
    ILabelModelParameter centerParameter = centerLabelModel.createParameter(InteriorStretchLabelModel.Position.CENTER);

    // A label that does not wrap or clip at all. As the clip text property is set to false and no wrapping and
    // ellipsis is used, the label extends the bounds of its owner node.
    DefaultLabelStyle noWrapNoEllipsisNoClipStyle = createLabelStyle(false, TextWrapping.NO_WRAP, OverrunStyle.CLIP);
    graph.addLabel(node1, Text, centerParameter, noWrapNoEllipsisNoClipStyle);
    graph.addLabel(node1, "No Wrapping\nNo Ellipsis\nNo Clipping", northParameter, northLabelStyle);

    // A label that does not wrap or clip at all. By default, ClipText is true, so it is clipped at the given bounds.
    DefaultLabelStyle noWrapNoEllipsisStyle = createLabelStyle(true, TextWrapping.NO_WRAP, OverrunStyle.CLIP);
    graph.addLabel(node2, Text, centerParameter, noWrapNoEllipsisStyle);
    graph.addLabel(node2, "No Wrapping\nNo Ellipsis\nClipping", northParameter, northLabelStyle);

    // A label that is not wrapped but trimmed with ellipsis at the given bounds if there is not enough space.
    DefaultLabelStyle noWrapEllipsisStyle = createLabelStyle(true, TextWrapping.NO_WRAP,
        OverrunStyle.ELLIPSIS);
    graph.addLabel(node3, Text, centerParameter, noWrapEllipsisStyle);
    graph.addLabel(node3, "No Wrapping\nEllipsis\nClipping", northParameter, northLabelStyle);

    // A label that is wrapped at word boundaries but not trimmed with ellipsis. If there is not enough space, the wrapped lines
    // are placed according to the chosen vertical alignment. With 'VerticalAlignment.center' the top and bottom part
    // of the label are clipped.
    DefaultLabelStyle wrapNoEllipsisStyle = createLabelStyle(true, TextWrapping.WRAP, OverrunStyle.CLIP);
    graph.addLabel(node4, Text, centerParameter, wrapNoEllipsisStyle);
    graph.addLabel(node4, "Wrapping\nNoEllipsis\nClipping", northParameter, northLabelStyle);

    // A label that is wrapped at word boundaries but not trimmed with ellipsis or clipped.
    // Due to the label exceeding the node bounds vertically, we place the description label even further north
    ExteriorLabelModel furtherNorthLabelModel = new ExteriorLabelModel();
    furtherNorthLabelModel.setInsets(new InsetsD(110));
    ILabelModelParameter furtherNorthParameter = furtherNorthLabelModel.createParameter(ExteriorLabelModel.Position.NORTH);
    DefaultLabelStyle wrapNoEllipsisNoClipStyle = createLabelStyle(false, TextWrapping.WRAP, OverrunStyle.CLIP);
    graph.addLabel(node5, Text, centerParameter, wrapNoEllipsisNoClipStyle);
    graph.addLabel(node5, "Wrapping\nNoEllipsis\nNoClipping", furtherNorthParameter, northLabelStyle);

    // A label that is wrapped and trimmed with ellipsis at characters at the end.
    DefaultLabelStyle wrapEllipsisStyle = createLabelStyle(true, TextWrapping.WRAP, OverrunStyle.ELLIPSIS);
    graph.addLabel(node7, Text, centerParameter, wrapEllipsisStyle);
    graph.addLabel(node7, "Wrapping\nEllipsis\nClipping", northParameter, northLabelStyle);

    // A label that is wrapped and trimmed with ellipsis at word boundaries.
    DefaultLabelStyle wrapWordEllipsisStyle = createLabelStyle(true, TextWrapping.WRAP, OverrunStyle.WORD_ELLIPSIS);
    graph.addLabel(node6, Text, centerParameter, wrapWordEllipsisStyle);
    graph.addLabel(node6, "Wrapping\nWord Ellipsis\nClipping", northParameter, northLabelStyle);

    // A label that is wrapped but uses a hexagon shape to fit the text inside.
    // The TextWrappingShape can be combined with the TextWrappingPadding that keeps empty paddings inside this shape.
    DefaultLabelStyle wrapHexagonShapeStyle = createShapeWrappingLabelStyle(OverrunStyle.WORD_ELLIPSIS,
        TextWrappingShape.HEXAGON, InsetsD.EMPTY);
    graph.addLabel(node8, Text, centerParameter, wrapHexagonShapeStyle);
    graph.addLabel(node8, "Wrapping\nat Hexagon Shape\nWord Ellipsis", northParameter, northLabelStyle);

    // A label that is wrapped inside a triangular shape.
    DefaultLabelStyle wrapTriangleShapeStyle = createShapeWrappingLabelStyle(OverrunStyle.ELLIPSIS,
        TextWrappingShape.TRIANGLE2, InsetsD.EMPTY);
    graph.addLabel(node9, Text, centerParameter, wrapTriangleShapeStyle);
    graph.addLabel(node9, "Wrapping\nat Triangle Shape\nEllipsis", northParameter, northLabelStyle);

    // A label that is wrapped inside an elliptic shape.
    // In addition to the TextWrappingPadding some insets are defined for the top and bottom side
    // to keep the upper and lower part of the ellipse empty.
    InsetsD topBottomInsets = new InsetsD(40, 0, 40, 0);
    DefaultLabelStyle wrapEllipseShapeStyle = createShapeWrappingLabelStyle(OverrunStyle.ELLIPSIS,
        TextWrappingShape.ELLIPSE, topBottomInsets);
    graph.addLabel(node11, Text, centerParameter, wrapEllipseShapeStyle);
    graph.addLabel(node11, "Wrapping\nat Ellipse Shape\nwith Top/Bottom Insets\nEllipsis", northParameter,
        northLabelStyle);

    // A label that is wrapped inside an octagon shape.
    // In addition to the TextWrappingPadding some insets are defined for the top and bottom side
    // to keep the upper and lower part of the octagon empty.
    DefaultLabelStyle wrapOctagonShapeStyle = createShapeWrappingLabelStyle(OverrunStyle.WORD_ELLIPSIS,
        TextWrappingShape.OCTAGON, topBottomInsets);
    graph.addLabel(node10, Text, centerParameter, wrapOctagonShapeStyle);
    graph.addLabel(node10, "Wrapping\nat Octagon Shape\nwith Top/Bottom Insets\nWord Ellipsis", northParameter,
        northLabelStyle);
  }

  /**
   * Creates a {@link ShapeNodeShape} with the given shape that uses the same paint and pen as the given
   * {@link RectangleNodeStyle}.
   */
  private INodeStyle createShapeNodeStyle(ShapeNodeShape shape, RectangleNodeStyle defaultNodeStyle) {
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setShape(shape);
    style.setPaint(defaultNodeStyle.getPaint());
    style.setPen(defaultNodeStyle.getPen());
    return style;
  }

  // a long multi-line text to demonstrate the clipping/wrapping/overrun style behavior
  private static final String Text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n"
      + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.\n\n"
      + "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\n"
      + "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    // sets up the help text on the right side.
    WebViewUtils.initHelp(help, this);

    // initialize the settings used when creating new graph items
    initializeGraphDefaults();

    // initialize the graph
    initializeGraph();

    // initialize the input mode
    initializeInputMode();
  }

  /**
   * Sets up defaults used when creating new graph items.
   */
  private void initializeGraphDefaults() {
    DemoStyles.initDemoStyles(graphControl.getGraph());

    INodeDefaults nodeDefaults = graphControl.getGraph().getNodeDefaults();
    nodeDefaults.setSize(new SizeD(100, 80));

    // Use a label model that stretches the label over the full node layout, with small insets. The label style
    // is responsible for drawing the label in the given space. Depending on its implementation, it can either
    // ignore the given space, clip the label at the width or wrapping the text.
    // See the initializeGraph function where labels are added with different style options.
    InteriorStretchLabelModel centerLabelModel = new InteriorStretchLabelModel();
    centerLabelModel.setInsets(new InsetsD(5));
    nodeDefaults.getLabelDefaults().setLayoutParameter(
        centerLabelModel.createParameter(InteriorStretchLabelModel.Position.CENTER));

    IEdgeDefaults edgeDefaults = graphControl.getGraph().getEdgeDefaults();
    edgeDefaults.getLabelDefaults().setLayoutParameter(
        new EdgePathLabelModel(5, 0, 0, true, EdgeSides.BELOW_EDGE).createRatioParameter(0.5, EdgeSides.BELOW_EDGE));
  }

  /**
   * Allow all kind of user interactions including node resizing.
   */
  private void initializeInputMode() {
    graphControl.setInputMode(new GraphEditorInputMode());
  }

  /**
   * Maximizes the window.
   */
  @Override
  public void start(final Stage primaryStage) throws IOException {
    primaryStage.setMaximized(true);
    super.start(primaryStage);
  }

  /**
   * Center the sample graph in the visible area.
   */
  @Override
  public void onLoaded() {
    super.onLoaded();
    Platform.runLater(graphControl::fitGraphBounds);
  }
  
  public static void main(String[] args) {
    launch(args);
  }
}
