/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.imageexport;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.MutableRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.utils.ObservableCollection;
import com.yworks.yfiles.view.ContextConfigurator;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.RectangleIndicatorInstaller;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandleInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.MoveInputMode;
import com.yworks.yfiles.view.input.RectangleReshapeHandleProvider;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for yFiles for JavaFX image export demos. It uses a tabbed pane to support switching between the
 * {@link com.yworks.yfiles.view.GraphControl} that contains the graph to export and a preview of the export result.
 */
public abstract class AbstractImageExportDemo extends DemoApplication {
  public WebView helpView;
  public GraphControl graphControl;

  // rectangle that indicates the region that gets exported
  private MutableRectangle exportRect;

  // Settings container
  public ComboBox<SizeMode> sizeBox;
  public CheckBox showDecorations;
  public CheckBox exportRectContent;
  public TextField topMarginField;
  public TextField rightMarginField;
  public TextField bottomMarginField;
  public TextField leftMarginField;
  public TextField widthField;
  public TextField heightField;
  public TextField scaleField;
  public CheckBox transparent;
  public ColorPicker backgroundColor;
  public TabPane tabPane;

  // dialog to select a file where to save the image
  private FileChooser dialog;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph is available. Most importantly,
   * the GraphControl instance is initialized.
   * <p>
   *   In this demo however, the main initialization step is done in {@link #onLoaded()}.
   * </p>
   */
  public void initialize() {
    // setup the help text on the left side.
    WebViewUtils.initHelp(helpView, this);

    sizeBox.getItems().addAll(SizeMode.values());
    sizeBox.getSelectionModel().selectFirst();

    // update the print preview when the zoom or any control has been changed
    graphControl.zoomProperty().addListener((observable, oldValue, newValue) -> updatePreview());
    sizeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePreview());
    showDecorations.selectedProperty().addListener((observable, oldValue, newValue) -> updatePreview());
    exportRectContent.selectedProperty().addListener((observable, oldValue, newValue) -> updatePreview());
    transparent.selectedProperty().addListener((observable, oldValue, newValue) -> updatePreview());
    backgroundColor.setOnAction(event -> updatePreview());
    Arrays.asList(topMarginField, rightMarginField, bottomMarginField, leftMarginField, widthField, heightField, scaleField)
        .forEach(textField -> textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
          if (!newValue) {
            updatePreview();
          }
        }));
  }

  /**
   * Called when the stage is shown and the {@link GraphControl} is already resized to its preferred size.
   */
  public void onLoaded() {
    // build the input modes and wire up the exporting rectangle
    initializeInputModes();
    // manage the background color of the GraphControl
    bindBackgroundColor();
    // construct a sample graph
    initializeGraph();
  }

  /**
   * Binds the background color of the GraphControl to the color of the ColorPicker.
   */
  private void bindBackgroundColor() {
    backgroundColor.valueProperty().addListener((observableValue, oldColor, newColor) -> {
      // the Color's toString method returns something like '0x001122FF', but CSS needs the format '#001122FF'
      graphControl.setStyle("-fx-background-color: #" + newColor.toString().substring(2) + ";");
    });
  }

  /**
   * Initializes a {@link com.yworks.yfiles.view.input.GraphEditorInputMode} and the rectangle that indicates the region to
   * export.
   */
  protected void initializeInputModes() {
    // create a GraphEditorInputMode instance
    GraphEditorInputMode editMode = new GraphEditorInputMode();
    // and install the edit mode into the canvas.
    graphControl.setInputMode(editMode);

    // create the model for the export rectangle
    exportRect = new MutableRectangle(0, 0, 100, 100);
    // now add our rectangle to the input mode group which will be rendered on top of the graph items
    RectangleIndicatorInstaller indicatorInstaller = new RectangleIndicatorInstaller(exportRect, RectangleIndicatorInstaller.SELECTION_TEMPLATE_KEY);
    indicatorInstaller.addCanvasObject(graphControl.getCanvasContext(), graphControl.getInputModeGroup(), exportRect);

    // add view modes that handle the resizing and movement of the export rectangle
    addExportRectInputModes(editMode);
  }

  /**
   * Adds the view modes that handle the resizing and movement of the export rectangle.
   */
  private void addExportRectInputModes(GraphEditorInputMode inputMode){
    // create handles for interactively resizing the export rectangle
    RectangleReshapeHandleProvider rectangleHandles = new RectangleReshapeHandleProvider(exportRect);
    rectangleHandles.setMinimumSize(new SizeD(10, 10));
    // create a input mode that renders the handles and deals with mouse gestures to drag the handles
    HandleInputMode exportHandleInputMode = new HandleInputMode();
    // specify certain handles the input mode should manage
    IInputModeContext inputModeContext = exportHandleInputMode.getInputModeContext();
    exportHandleInputMode.setHandles(new ObservableCollection<>(
        Arrays.asList(
            rectangleHandles.getHandle(inputModeContext, HandlePositions.NORTH_EAST),
            rectangleHandles.getHandle(inputModeContext, HandlePositions.NORTH_WEST),
            rectangleHandles.getHandle(inputModeContext, HandlePositions.SOUTH_EAST),
            rectangleHandles.getHandle(inputModeContext, HandlePositions.SOUTH_WEST))));

    // create a mode that allows for dragging the export rectangle at the sides
    MoveInputMode moveInputMode = new MoveInputMode();
    // assign the exportRect as moveable so that it will be repositioned during dragging
    moveInputMode.setPositionHandler(new ExportRectanglePositionHandler(exportRect));
    // define the hit test that determines where the user can begin dragging the rectangle
    moveInputMode.setHitTestable((context, location) -> {
      GeneralPath path = new GeneralPath(5);
      path.appendRectangle(exportRect, false);
      return path.pathContains(location, context.getHitTestRadius());
    });

    // add the HandleInputMode to the graph editor mode
    exportHandleInputMode.setPriority(1);
    inputMode.add(exportHandleInputMode);
    // Add the MoveInputMode to the graph editor mode:
    // The MoveInputMode that controls the node dragging behavior have a higher priority than the
    // MoveInputMode that is responsible for moving the rectangle around.
    moveInputMode.setPriority(inputMode.getMoveInputMode().getPriority() + 1);
    inputMode.add(moveInputMode);
  }

  /**
   * Initializes a simple sample graph and makes the export rectangle enclose a part of it.
   */
  protected void initializeGraph() {
    IGraph graph = graphControl.getGraph();

    initializeGraphDefaults(graph);

    // create sample graph
    graph.addLabel(graph.createNode(new PointD(30, 30)), "Node");
    INode node = graph.createNode(new PointD(90, 30));
    graph.createEdge(node, graph.createNode(new PointD(90, 90)));

    // fit the graph bounds now to enclose the current graph
    graphControl.fitGraphBounds();
    // initially set the export rect to enclose part of the graph's contents
    exportRect.reshape(graphControl.getContentRect());
    // create graph elements that are outside the current content rect (and export rect)
    graph.createEdge(node, graph.createNode(new PointD(200, 30)));
    // now fit the graph bounds again to make the whole graph visible
    graphControl.fitGraphBounds();
    // the export rect still encloses the same part of the graph as before
  }

  /**
   * Initializes the default node and edge style.
   */
  private void initializeGraphDefaults(IGraph graph) {
    // initialize default node style
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);

    // initialize default edge style
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setTargetArrow(IArrow.DEFAULT);
    graph.getEdgeDefaults().setStyle(edgeStyle);
  }

  /**
   * Determines and sets the scale for exporting to the given {@link ContextConfigurator}.
   * @param configurator ContextConfigurator where to set the scale
   *
   */
  private void setScale(ContextConfigurator configurator) {
    double scale = Double.parseDouble(this.scaleField.getText());
    // consider the zoom level
    double zoomedScale = scale * graphControl.getZoom();

    // look if a fixed size has been specified
    switch (sizeBox.getSelectionModel().getSelectedItem()) {
      case SPECIFY_WIDTH :
        int newWidth = Integer.parseInt(widthField.getText());
        zoomedScale = configurator.calculateScaleForWidth(scale * newWidth);
        break;
      case SPECIFY_HEIGHT:
        int newHeight = Integer.parseInt(heightField.getText());
        zoomedScale = configurator.calculateScaleForHeight(scale * newHeight);
        break;
    }
    configurator.setScale(zoomedScale);
  }

  /**
   * Exports the graph or part of it to an image that is shown in the preview panel.
   */
  protected void updatePreview() {
  }

  /**
   * Returns a ContextConfigurator that considers the export rectangle and margins.
   */
  protected ContextConfigurator createContextConfigurator() {
    // check if the rectangular region or the whole view port should be printed
    boolean useRectangle = exportRectContent.isSelected();

    // create a configurator with the settings of the option panel
    ContextConfigurator configurator;
    if (useRectangle) {
      configurator = new ContextConfigurator(exportRect.toRectD().getEnlarged(-1));
    } else {
      List<PointD> corners = new ArrayList<>(4);
      corners.add(graphControl.toWorldCoordinates(new PointD(0,0)));
      corners.add(graphControl.toWorldCoordinates(new PointD(graphControl.getInnerSize().width,0)));
      corners.add(graphControl.toWorldCoordinates(new PointD(0,graphControl.getInnerSize().height)));
      corners.add(graphControl.toWorldCoordinates(new PointD(graphControl.getInnerSize().width,graphControl.getInnerSize().height)));
      configurator = new ContextConfigurator(corners);
    }
    configurator.setProjection(graphControl.getProjection());
    setScale(configurator);
    // parse and set the margins
    double top = Double.parseDouble(topMarginField.getText());
    double right = Double.parseDouble(rightMarginField.getText());
    double bottom = Double.parseDouble(bottomMarginField.getText());
    double left = Double.parseDouble(leftMarginField.getText());
    configurator.setMargins(new InsetsD(top, right, bottom, left));
    return configurator;
  }

  /**
   * Returns the component to export from. For exporting an 'undecorated' image, we use a new one.
   */
  protected GraphControl getExportingGraphControl() {
    GraphControl control = graphControl;
    // check whether decorations (selection, handles, ...) should be hidden
    if (!showDecorations.isSelected()) {
      // if so, create a new GraphControl with the same graph
      control = new GraphControl();
      control.resize(graphControl.getWidth(), graphControl.getHeight());
      control.setGraph(graphControl.getGraph());
      control.setViewPoint(graphControl.getViewPoint());
      control.setBackground(graphControl.getBackground());
      control.setNodeOrientation(graphControl.getEffectiveNodeOrientation());
      control.setProjection(graphControl.getProjection());
      control.updateImmediately();
    }
    return control;
  }

  /**
   * Exports the image to a file.
   */
  public void saveToFile() {
    // determine the file to save the graph as image
    FileChooser dialog = getImageSaveDialog();
    File file = dialog.showSaveDialog(helpView.getScene().getWindow());
    if (file == null) {
      // user has canceled saving the graph as image
      return;
    }

    String filename = file.getAbsolutePath();
    saveToFile(filename);
  }

  /**
   * Lazy-loads a FileChooser that is pre-configured to save an image.
   */
  private FileChooser getImageSaveDialog(){
    if (dialog == null){
      // make a new FileChooser, set the title to something meaningful
      dialog = new FileChooser();
      dialog.setTitle("Save As Image...");
    }
    // update the extension filters.
    dialog.getExtensionFilters().clear();
    dialog.getExtensionFilters().addAll(getExtensionFilters());
    return dialog;
  }

  /**
   * Saves the graph or part of it as an image using the settings provided by the option panel and the specified filename.
   * @param filename The name of the file to export the image to.
   */
  protected abstract void saveToFile(String filename);

  /**
   * Returns the file filter used in the file chooser.
   */
  protected abstract FileChooser.ExtensionFilter[] getExtensionFilters();

  /**
   * An enum for bounds options.
   */
  private enum SizeMode {
    USE_ORIGINAL_SIZE("Use Original Size"),
    SPECIFY_WIDTH("Specify Width"),
    SPECIFY_HEIGHT("Specify Height");

    private String description;

    SizeMode(String description) {
      this.description = description;
    }

    @Override
    public String toString() {
      return description;
    }
  }
}
