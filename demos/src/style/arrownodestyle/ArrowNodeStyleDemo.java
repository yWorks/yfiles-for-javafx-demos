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
package style.arrownodestyle;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.styles.ArrowNodeDirection;
import com.yworks.yfiles.graph.styles.ArrowNodeStyle;
import com.yworks.yfiles.graph.styles.ArrowStyleShape;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.LabelShape;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.NodeReshapeHandleProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import toolkit.DemoApplication;
import toolkit.Palette;
import toolkit.Themes;
import toolkit.WebViewUtils;

import java.io.IOException;

public class ArrowNodeStyleDemo extends DemoApplication {
  @FXML
  private GraphControl graphControl;
  @FXML
  private WebView help;
  
  /**
   * Initializes the demo.
   */
  public void initialize() {
    WebViewUtils.initHelp(help, this);
    initializeOptionPanel();
    initializeStyleDefaults();
    createSampleNodes();
    initializeInputMode();
  }

  /**
   * Initializes the default styles.
   */
  private void initializeStyleDefaults() {
    Palette orange = Themes.PALETTE_ORANGE;

    ArrowNodeStyle nodeStyle = new ArrowNodeStyle();
    nodeStyle.setPaint(orange.getBackgroundPaint());
    nodeStyle.setPen(new Pen(orange.getOutlinePaint(), 1));

    ExteriorLabelModel labelModel = new ExteriorLabelModel();
    labelModel.setInsets(new InsetsD(30));

    INodeDefaults nodeDefaults = graphControl.getGraph().getNodeDefaults();
    nodeDefaults.setStyle(nodeStyle);
    nodeDefaults.setSize(new SizeD(200, 100));
    nodeDefaults.setStyleInstanceSharingEnabled(false);
    nodeDefaults.getLabelDefaults().setStyle(createLabelStyle(orange));
    nodeDefaults.getLabelDefaults().setLayoutParameter(labelModel.createParameter(ExteriorLabelModel.Position.SOUTH));
  }

  /**
   * Creates a new node label style with colors from the given palette.
   */
  private ILabelStyle createLabelStyle(Palette palette) {
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setShape(LabelShape.ROUND_RECTANGLE);
    labelStyle.setBackgroundPaint(palette.getNodeLabelBackgroundPaint());
    labelStyle.setTextPaint(palette.getTextPaint());
    labelStyle.setTextAlignment(TextAlignment.LEFT);
    labelStyle.setVerticalTextAlignment(VPos.CENTER);
    labelStyle.setInsets(new InsetsD(4, 8, 4, 8));
    labelStyle.setFont(Font.font(14));
    return labelStyle;
  }

  /**
   * Creates the initial sample graph.
   */
  private void createSampleNodes() {
    // create nodes with different shapes, angles and shaft ratios
    createNodes(0, ArrowStyleShape.ARROW, Themes.PALETTE_ORANGE);
    createNodes(300, ArrowStyleShape.DOUBLE_ARROW, Themes.PALETTE_BLUE);
    createNodes(600, ArrowStyleShape.NOTCHED_ARROW, Themes.PALETTE_RED);
    createNodes(900, ArrowStyleShape.PARALLELOGRAM, Themes.PALETTE_GREEN);
    createNodes(1200, ArrowStyleShape.TRAPEZOID, Themes.PALETTE_PURPLE);
  }

  /**
   * Creates several nodes with the given shape and different angles and shaft ratios.
   * @param xOffset The x-location where to place the nodes.
   * @param shape The shape to use for the arrow.
   * @param palette The colors to use for nodes and labels.
   */
  private void createNodes(int xOffset, ArrowStyleShape shape, Palette palette) {
    double angleFactor =
        shape == ArrowStyleShape.PARALLELOGRAM ||
        shape == ArrowStyleShape.TRAPEZOID
            ? 0.5
            : 1;

    // small angle and shaft ratio pointing left
    ArrowNodeStyle style1 = new ArrowNodeStyle();
    style1.setShape(shape);
    style1.setDirection(ArrowNodeDirection.LEFT);
    style1.setAngle((angleFactor * Math.PI) / 8);
    style1.setShaftRatio(0.25);
    style1.setPaint(palette.getBackgroundPaint());
    style1.setPen(new Pen(palette.getOutlinePaint(), 1));

    // default angle and shaft ratio pointing up
    ArrowNodeStyle style2 = new ArrowNodeStyle();
    style2.setShape(shape);
    style2.setDirection(ArrowNodeDirection.UP);
    style2.setAngle((angleFactor * Math.PI) / 4);
    style2.setShaftRatio(1.0 / 3);
    style2.setPaint(palette.getBackgroundPaint());
    style2.setPen(new Pen(palette.getOutlinePaint(), 1));

    // bigger angle and shaft ratio pointing right
    ArrowNodeStyle style3 = new ArrowNodeStyle();
    style3.setShape(shape);
    style3.setDirection(ArrowNodeDirection.RIGHT);
    style3.setAngle((angleFactor * Math.PI * 3) / 8);
    style3.setShaftRatio(0.75);
    style3.setPaint(palette.getBackgroundPaint());
    style3.setPen(new Pen(palette.getOutlinePaint(), 1));


   // negative angle and max shaft ratio pointing right
    ArrowNodeStyle style4 = new ArrowNodeStyle();
    style4.setShape(shape);
    style4.setDirection(ArrowNodeDirection.RIGHT);
    style4.setAngle(((angleFactor * -Math.PI) / 8));
    style4.setShaftRatio(1);
    style4.setPaint(palette.getBackgroundPaint());
    style4.setPen(new Pen(palette.getOutlinePaint(), 1));

    ArrowNodeStyle[] styles = {style1, style2, style3, style4};

    // create a sample node for each sample style instance
    IGraph graph = graphControl.getGraph();
    int y = 0;
    for (int i = 0; i < styles.length; ++i) {
      double x = xOffset + (i == 1 ? 50 : 0);
      double width = i == 1 ? 100 : 200;
      double height = i == 1 ? 200 : 100;
      ArrowNodeStyle style = styles[i];

      ExteriorLabelModel labelModel = new ExteriorLabelModel();
      labelModel.setInsets(new InsetsD(30));

      graph.addLabel(
          graph.createNode(new RectD(x, y, width, height), style),
          styleToText(style),
          labelModel.createParameter(ExteriorLabelModel.Position.SOUTH),
          createLabelStyle(palette)
        );
      y += height + 250;
    }
  }

  /**
   * Sets up an input mode for the graphControl, and adds reshape handles.
   */
  private void initializeInputMode() {
    // reserve some space for the angle adjustment handle
    graphControl.setContentMargins(new InsetsD(20));

    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setCreateEdgeAllowed(false);
    inputMode.setAddLabelAllowed(false);
    inputMode.setEditLabelAllowed(false);
    inputMode.setSelectableItems(GraphItemTypes.NODE);
    graphControl.setInputMode(inputMode);

    // add a label to newly created node that shows the current style settings
    inputMode.addNodeCreatedListener((source, args) -> {
      INode node = args.getItem();
      graphControl.getGraph().addLabel(node, styleToText((ArrowNodeStyle) node.getStyle()));
    });

    // listen for selection changes to update the option handler for the style properties
    graphControl.getSelection().addItemSelectionChangedListener((source, args) -> {
      if (args.isItemSelected()) {
        adjustOptionPanel((INode) args.getItem());
      }
    });

    NodeDecorator nodeDecorator = graphControl.getGraph().getDecorator().getNodeDecorator();

    // add handle that enables the user to change the angle and shaft ratio of a node style
    nodeDecorator.getHandleProviderDecorator().setImplementationWrapper(
        node -> node.getStyle() instanceof ArrowNodeStyle,
        (node, delegateProvider) -> new ArrowNodeStyleHandleProvider(node, () -> {
          adjustOptionPanel(node);
          ArrowNodeStyle style = (ArrowNodeStyle) node.getStyle();
          if (node.getLabels().size() == 0) {
            graphControl.getGraph().addLabel(node, styleToText(style));
          } else {
            graphControl.getGraph().setLabelText(node.getLabels().first(), styleToText(style));
          }
        }, delegateProvider)
    );

    // only provide reshape handles for the east, south and south-east sides,
    // so they don't clash with the custom handles
    nodeDecorator.getReshapeHandleProviderDecorator().setFactory(node ->
        new NodeReshapeHandleProvider(node, node.lookup(IReshapeHandler.class),
            HandlePositions.EAST.or(HandlePositions.SOUTH).or(HandlePositions.SOUTH_EAST)));

    nodeDecorator.getSelectionDecorator().hideImplementation();
  }

  /**
   * Returns a text description of the style configuration.
   */
  private static String styleToText(ArrowNodeStyle style) {
    return
        "Shape: " + style.getShape().name() + "\n" +
        "Direction: " + style.getDirection().name() + "\n" +
        "Angle: " + Math.round(toDegrees(style.getAngle())) + "\n" +
        "Shaft Ratio: " + (Math.round(style.getShaftRatio() * 100)) / 100d;
  }

  /**
   * Returns the given angle in degrees.
   */
  private static double toDegrees(double radians) {
    return (radians * 180) / Math.PI;
  }

  /**
   * Returns the given angle in radians.
   */
  private static double toRadians(double degrees) {
    return (degrees / 180) * Math.PI;
  }

  /**
   * Center the sample graph in the visible area.
   */
  @Override
  public void onLoaded() {
    super.onLoaded();
    Platform.runLater(graphControl::fitGraphBounds);
  }

  /**
   * Maximizes the window.
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    primaryStage.setMaximized(true);
    super.start(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }


  @FXML
  private ComboBox<ArrowStyleShape> shapeBox;
  @FXML
  private ComboBox<ArrowNodeDirection> directionBox;
  @FXML
  private Slider angleSlider;
  @FXML
  private Slider shaftRatioSlider;
  private boolean optionsUpdating = false;

  /**
   * Shows the values of the given node's style in the option panel.
   */
  private void adjustOptionPanel(INode node) {
    if (node.getStyle() instanceof ArrowNodeStyle) {
      optionsUpdating = true;

      ArrowNodeStyle style = (ArrowNodeStyle) node.getStyle();
      shapeBox.getSelectionModel().select(style.getShape());
      directionBox.getSelectionModel().select(style.getDirection());
      angleSlider.setValue(Math.round(toDegrees(style.getAngle())));
      shaftRatioSlider.setValue(Math.round(style.getShaftRatio()*100));

      // update defaultArrowNodeStyle to correspond to the option panel
      if (graphControl.getGraph().getNodeDefaults().getStyle() instanceof ArrowNodeStyle) {
        ArrowNodeStyle defaultNodeStyle = (ArrowNodeStyle) graphControl.getGraph().getNodeDefaults().getStyle();
        defaultNodeStyle.setShape(style.getShape());
        defaultNodeStyle.setDirection(style.getDirection());
        defaultNodeStyle.setAngle(style.getAngle());
        defaultNodeStyle.setShaftRatio(style.getShaftRatio());
        graphControl.getGraph().getNodeDefaults().setSize(node.getLayout().toSizeD());
      }

      optionsUpdating = false;
    }
  }

  /**
   * Applies the values of the option panel to the selected nodes.
   */
  private void adjustSelectedNodes() {
    if (optionsUpdating) {
      return;
    }

    ArrowStyleShape shape = shapeBox.getSelectionModel().getSelectedItem();
    ArrowNodeDirection direction = directionBox.getSelectionModel().getSelectedItem();
    double angle = toRadians((angleSlider.getValue()));
    double shaftRatio = shaftRatioSlider.getValue() / 100d;

    IGraph graph = graphControl.getGraph();
    for (INode node : graphControl.getSelection().getSelectedNodes()) {
      if (node.getStyle() instanceof ArrowNodeStyle) {
        ArrowNodeStyle style = (ArrowNodeStyle) node.getStyle();
        applyStyleSettings(style, shape, direction, angle, shaftRatio);
        if (node.getLabels().size() == 0) {
          graph.addLabel(node, styleToText(style));
        } else {
          graph.setLabelText(node.getLabels().first(), styleToText(style));
        }
      }
    }

    ArrowNodeStyle defaultStyle = (ArrowNodeStyle) graph.getNodeDefaults().getStyle();
    applyStyleSettings(defaultStyle, shape, direction, angle, shaftRatio);

    graphControl.invalidate();
  }

  private void applyStyleSettings(
      ArrowNodeStyle style,
      ArrowStyleShape shape,
      ArrowNodeDirection direction,
      double angle,
      double shaftRatio) {
    style.setShape(shape);
    style.setDirection(direction);
    style.setAngle(angle);
    style.setShaftRatio(shaftRatio);
  }

  private void initializeOptionPanel() {
    // initialize the shape box
    shapeBox.getItems().addAll(
        ArrowStyleShape.ARROW,
        ArrowStyleShape.DOUBLE_ARROW,
        ArrowStyleShape.NOTCHED_ARROW,
        ArrowStyleShape.PARALLELOGRAM,
        ArrowStyleShape.TRAPEZOID
    );
    shapeBox.setConverter(new StringConverter<ArrowStyleShape>() {
      @Override
      public String toString(ArrowStyleShape object) {
        switch (object) {
          default:
          case ARROW:
            return "Arrow";
          case DOUBLE_ARROW:
            return "Double Arrow";
          case NOTCHED_ARROW:
            return "Notched Arrow";
          case PARALLELOGRAM:
            return "Parallelogram";
          case TRAPEZOID:
            return "Trapezoid";
        }
      }

      @Override
      public ArrowStyleShape fromString(String string) {
        switch (string) {
          default:
          case "Arrow":
            return ArrowStyleShape.ARROW;
          case "Double Arrow":
            return ArrowStyleShape.DOUBLE_ARROW;
          case "Notched Arrow":
            return ArrowStyleShape.NOTCHED_ARROW;
          case "Parallelogram":
            return ArrowStyleShape.PARALLELOGRAM;
          case "Trapezoid":
            return ArrowStyleShape.TRAPEZOID;
        }
      }
    });
    shapeBox.getSelectionModel().select(0);
    shapeBox.setOnAction(event -> adjustSelectedNodes());

    // initialize the direction box
    directionBox.getItems().addAll(
        ArrowNodeDirection.RIGHT,
        ArrowNodeDirection.DOWN,
        ArrowNodeDirection.LEFT,
        ArrowNodeDirection.UP
    );
    directionBox.setConverter(new StringConverter<ArrowNodeDirection>() {
      @Override
      public String toString(ArrowNodeDirection object) {
        switch (object) {
          default:
          case RIGHT:
            return "Right";
          case DOWN:
            return "Down";
          case LEFT:
            return "Left";
          case UP:
            return "Up";
        }
      }

      @Override
      public ArrowNodeDirection fromString(String string) {
        switch (string) {
          default:
          case "Right":
            return ArrowNodeDirection.RIGHT;
          case "Down":
            return ArrowNodeDirection.DOWN;
          case "Left":
            return ArrowNodeDirection.LEFT;
          case "Up":
            return ArrowNodeDirection.UP;
        }
      }
    });
    directionBox.getSelectionModel().select(0);
    directionBox.setOnAction(event -> adjustSelectedNodes());

    // initialize the sliders
    angleSlider.valueProperty().addListener((observable, oldValue, newValue) -> adjustSelectedNodes());
    shaftRatioSlider.valueProperty().addListener((observable, oldValue, newValue) -> adjustSelectedNodes());
  }

}
