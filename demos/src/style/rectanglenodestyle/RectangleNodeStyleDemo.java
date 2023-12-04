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
package style.rectanglenodestyle;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.CornerStyle;
import com.yworks.yfiles.graph.styles.Corners;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.NodeReshapeHandleProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.Palette;
import toolkit.Themes;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.util.ArrayList;

public class RectangleNodeStyleDemo extends DemoApplication {
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
   * Initializes defaults for the given graph.
   */
  private void initializeStyleDefaults() {
    // Set defaults for new nodes
    Palette gray = Themes.PALETTE75;

    RectangleNodeStyle nodeStyle = new RectangleNodeStyle();
    nodeStyle.setPaint(gray.getBackgroundPaint());
    nodeStyle.setPen(new Pen(gray.getOutlinePaint(), 1));

    INodeDefaults nodeDefaults = graphControl.getGraph().getNodeDefaults();
    nodeDefaults.setStyle(nodeStyle);
    nodeDefaults.setSize(new SizeD(300, 100));
    nodeDefaults.setStyleInstanceSharingEnabled(false);
  }

  /**
   * Creates a small sample with different node style settings.
   */
  private void createSampleNodes() {
    Palette yellow = Themes.PALETTE71;
    Palette orange = Themes.PALETTE72;
    Palette green = Themes.PALETTE73;
    Palette blue = Themes.PALETTE74;

    // Create nodes with round corners with different resizing behaviors
    createNode(new PointD(0, 0), yellow, CornerStyle.ROUND, false, 10, Corners.ALL);
    createNode(new PointD(0, 200), orange, CornerStyle.ROUND, true, 0.2, Corners.ALL);
    createNode(new PointD(0, 400), green, CornerStyle.ROUND, true, 0.5, Corners.ALL);
    createNode(new PointD(0, 600), blue, CornerStyle.ROUND, true, 0.8, Corners.BOTTOM);

    // Create nodes with cut-off corners with different resizing behaviors
    createNode(new PointD(400, 0), yellow, CornerStyle.CUT, false, 10, Corners.ALL);
    createNode(new PointD(400, 200), orange, CornerStyle.CUT, true, 0.2, Corners.ALL);
    createNode(new PointD(400, 400), green, CornerStyle.CUT, true, 0.5, Corners.ALL);
    createNode(new PointD(400, 600), blue, CornerStyle.CUT, true, 0.8, Corners.BOTTOM);
  }

  /**
   * Creates a node with a label that describes the configuration of the RectangleNodeStyle.
   * @param location The location of the node.
   * @param color The color set of the node and label.
   * @param cornerStyle Whether corners should be round or a line.
   * @param scaleCornerSize Whether the corner size should be used as absolute value or be scaled with the node size.
   * @param cornerSize The corner size.
   * @param corners Which corners are drawn with the given corner style.
   */
  private void createNode(PointD location, Palette color, CornerStyle cornerStyle, boolean scaleCornerSize, double cornerSize, Corners corners) {
    RectangleNodeStyle style = new RectangleNodeStyle();
    style.setPaint(color.getBackgroundPaint());
    style.setPen(new Pen(color.getOutlinePaint(), 1));
    style.setCornerStyle(cornerStyle);
    style.setCornerSizeScalingEnabled(scaleCornerSize);
    style.setCornerSize(cornerSize);
    style.setCorners(corners);

    INode node = graphControl.getGraph().createNode(location, style);
    addLabel(node, color);
  }

  /**
   * Adds a label that describes the owner's style configuration.
   * @param node The owner of the label.
   * @param color The color set of the label.
   */
  private void addLabel(INode node, Palette color) {
    DefaultLabelStyle style = DemoStyles.createDemoNodeLabelStyle(color);

    graphControl.getGraph().addLabel(
        node,
        styleToText((RectangleNodeStyle) node.getStyle()),
        InteriorLabelModel.CENTER,
        style
    );

  }

  /**
   * Returns a text description of the style configuration.
   */
  private static String styleToText(RectangleNodeStyle style) {
    return
        "Corner Style: " + style.getCornerStyle().name() + "\n" +
        "Corner Size Scaling: " + (style.isCornerSizeScalingEnabled() ? "RELATIVE":"ABSOLUTE") + "\n" +
        "Affected Corners: " + cornersToText(style.getCorners());
  }

  /**
   * Returns a text description of the given corner configuration.
   */
  private static String cornersToText(Corners corners) {
    Corners[] all = {
        Corners.ALL, Corners.TOP, Corners.BOTTOM, Corners.RIGHT, Corners.LEFT, Corners.TOP_LEFT, Corners.TOP_RIGHT,
        Corners.BOTTOM_LEFT, Corners.BOTTOM_RIGHT
    };

    ArrayList<String> affected = new ArrayList<>();
    for (Corners corner : all) {
      if ((corners.and(corner).equals(corner))) {
        corners = corners.and(corner.inverse());
        affected.add(corner.toString());
      }
    }
    return affected.size() > 0 ? String.join(" & ", affected) : "none";
  }

  /**
   * Sets up an input mode for the GraphControl, and adds a custom handle
   * that allows to change the corner size.
   */
  private void initializeInputMode() {
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setCreateEdgeAllowed(false);
    inputMode.setAddLabelAllowed(false);
    inputMode.setEditLabelAllowed(false);
    inputMode.setSelectableItems(GraphItemTypes.NODE);
    graphControl.setInputMode(inputMode);

    // add a label to newly created node that shows the current style settings
    inputMode.addNodeCreatedListener((source, args) -> {
      INode node = args.getItem();
      addLabel(node, Themes.PALETTE75);
    });

    // listen for selection changes to update the option handler for the style properties
    graphControl.getSelection().addItemSelectionChangedListener((source, args) -> {
      if (args.isItemSelected()) {
        adjustOptionPanel((INode) args.getItem());
      }
    });

    NodeDecorator nodeDecorator = graphControl.getGraph().getDecorator().getNodeDecorator();

    // add handle that enables the user to change the corner size of a node
    nodeDecorator.getHandleProviderDecorator().setImplementationWrapper(
        node -> node.getStyle() instanceof RectangleNodeStyle,
        (node, delegateProvider) -> new CornerSizeHandleProvider(node, () -> adjustOptionPanel(node), delegateProvider)
    );

    // only provide reshape handles for the east, south and south-east sides, so they don't clash with the corner size handle
    nodeDecorator.getReshapeHandleProviderDecorator().setFactory(node ->
        new NodeReshapeHandleProvider(node, node.lookup(IReshapeHandler.class),
            HandlePositions.EAST.or(HandlePositions.SOUTH).or(HandlePositions.SOUTH_EAST)));

    nodeDecorator.getSelectionDecorator().hideImplementation();
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
  public void start(final Stage primaryStage) throws IOException {
    primaryStage.setMaximized(true);
    super.start(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }


  @FXML
  private ComboBox<CornerStyle> cornerStyleBox;
  @FXML
  private ComboBox<Boolean> cornerSizeBox;
  @FXML
  private CheckBox topLeftCheckBox;
  @FXML
  private CheckBox topRightCheckBox;
  @FXML
  private CheckBox bottomLeftCheckBox;
  @FXML
  private CheckBox bottomRightCheckBox;
  private boolean optionsUpdating = false;

  private void adjustOptionPanel(INode node) {
    if (node.getStyle() instanceof RectangleNodeStyle) {
      optionsUpdating = true;
      RectangleNodeStyle style = (RectangleNodeStyle) node.getStyle();

      cornerStyleBox.getSelectionModel().select(style.getCornerStyle());
      cornerSizeBox.getSelectionModel().select(style.isCornerSizeScalingEnabled());

      Corners corners = style.getCorners();
      topLeftCheckBox.setSelected(corners.and(Corners.TOP_LEFT).equals(Corners.TOP_LEFT));
      topRightCheckBox.setSelected(corners.and(Corners.TOP_RIGHT).equals(Corners.TOP_RIGHT));
      bottomRightCheckBox.setSelected(corners.and(Corners.BOTTOM_RIGHT).equals(Corners.BOTTOM_RIGHT));
      bottomLeftCheckBox.setSelected(corners.and(Corners.BOTTOM_LEFT).equals(Corners.BOTTOM_LEFT));
      optionsUpdating = false;
    }
  }

  private void adjustSelectedNodes() {
    if (optionsUpdating) {
      return;
    }

    CornerStyle cornerStyle = cornerStyleBox.getSelectionModel().getSelectedItem();
    boolean scaleCornerSize = cornerSizeBox.getSelectionModel().getSelectedItem();

    Corners affectedCorners = Corners.NONE;
    if (topLeftCheckBox.isSelected()) {
      affectedCorners = affectedCorners.or(Corners.TOP_LEFT);
    }
    if (topRightCheckBox.isSelected()) {
      affectedCorners = affectedCorners.or(Corners.TOP_RIGHT);
    }
    if (bottomRightCheckBox.isSelected()) {
      affectedCorners = affectedCorners.or(Corners.BOTTOM_RIGHT);
    }
    if (bottomLeftCheckBox.isSelected()) {
      affectedCorners = affectedCorners.or(Corners.BOTTOM_LEFT);
    }

    IGraph graph = graphControl.getGraph();
    for (INode node : graphControl.getSelection().getSelectedNodes()) {
      if (node.getStyle() instanceof RectangleNodeStyle) {
        RectangleNodeStyle style = (RectangleNodeStyle) node.getStyle();
        applyStyleSettings(style, cornerStyle, scaleCornerSize, affectedCorners);
        if (node.getLabels().size() == 0) {
          graph.addLabel(node, styleToText(style));
        } else {
          graph.setLabelText(node.getLabels().first(), styleToText(style));
        }
      }
    }
    RectangleNodeStyle defaultStyle = (RectangleNodeStyle) graph.getNodeDefaults().getStyle();
    applyStyleSettings(defaultStyle, cornerStyle, scaleCornerSize, affectedCorners);
    graphControl.invalidate();
  }

  private void applyStyleSettings(RectangleNodeStyle style, CornerStyle cornerStyle, boolean scaleCornerSize, Corners corners) {
    style.setCornerStyle(cornerStyle);
    style.setCornerSizeScalingEnabled(scaleCornerSize);
    style.setCorners(corners);
  }

  private void initializeOptionPanel() {
    // initialize the corner style box
    cornerStyleBox.getItems().addAll(CornerStyle.ROUND, CornerStyle.CUT);
    cornerStyleBox.setConverter(new StringConverter<CornerStyle>() {
      @Override
      public String toString(CornerStyle object) {
        return CornerStyle.ROUND.equals(object) ? "Round" : "Cut";
      }

      @Override
      public CornerStyle fromString(String string) {
        return "Round".equals(string) ? CornerStyle.ROUND : CornerStyle.CUT;
      }
    });
    cornerStyleBox.getSelectionModel().select(0);
    cornerStyleBox.setOnAction(event -> adjustSelectedNodes());

    // initialize the corner size box
    cornerSizeBox.getItems().addAll(Boolean.FALSE, Boolean.TRUE);
    cornerSizeBox.setConverter(new StringConverter<Boolean>() {
      @Override
      public String toString(Boolean object) {
        return Boolean.TRUE.equals(object) ? "Relative" : "Absolute";
      }

      @Override
      public Boolean fromString(String string) {
        return "Relative".equals(string);
      }
    });
    cornerSizeBox.getSelectionModel().select(0);
    cornerSizeBox.setOnAction(event -> adjustSelectedNodes());

    // initialize the checkboxs
    topLeftCheckBox.setOnAction(event -> adjustSelectedNodes());
    topRightCheckBox.setOnAction(event -> adjustSelectedNodes());
    bottomLeftCheckBox.setOnAction(event -> adjustSelectedNodes());
    bottomRightCheckBox.setOnAction(event -> adjustSelectedNodes());
  }

}
