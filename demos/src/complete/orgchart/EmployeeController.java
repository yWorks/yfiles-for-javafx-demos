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
package complete.orgchart;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.NodeTemplate;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.ICommand;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * The controller class for the visual representation of the employees. This class is instantiated by the {@link javafx.fxml.FXMLLoader}
 * upon parsing and creating the employee.fxml file. The loader calls {@link #initialize()} on startup,
 * where the main wiring up of the UI is done.
 * <p>
 *   The controller holds a reference to the {@link NodeTemplate} top element of the employee.fxml, which in turn holds a reference to
 *   the INode, e.g. the item to display, and a reference to the {@link CanvasControl} in which the node is displayed.
 * </p>
 * <p>
 *   the controller is essentially responsible for keeping the model (the CanvasControl and the INode and it's employee)
 *   and the view (the NodeControl) synced. The controller chooses which {@link ContentControl} to use for which zoom level
 *   and which widget to display.
 * </p>
 * @see OrgChartDemo
 * @see javafx.fxml.FXMLLoader
 * @see "Employee.fxml"
 */
public class EmployeeController {

  public HBox childButtons;
  /**
   * The root element of the visual representation of the employee.
   * The root has following sub-elements:
   * <ul>
   *   <li>A background {@link javafx.scene.layout.StackPane} that is used for the visual hover effect of the node and has no content itself, only a css style..</li>
   *   <li>A {@link ContentControl} out of three different available. The content gets exchanged dependent on the zoom level.</li>
   *   <li>A {@link javafx.scene.control.Tooltip}. The visual representation of the tooltip gets exchanged along with the content.</li>
   *   <li>A set of buttons that gets visible when the node is hovered.</li>
   * </ul>
   */
  public ContentControl root;

  /**
   * A styled StackPane that gets transparent when the node is hovered, thus creating a hover effect
   */
  public StackPane foregroundPane;

  /*
    Buttons for the interaction with the graph structure.
   */
  public Button showParentButton;
  public Button hideParentButton;
  public Button showChildrenButton;
  public Button hideChildrenButton;

  public VBox buttons;
  /*
   * The different styles for the content. See Employee.fxml
   */
  private StackPane detail;
  private StackPane intermediate;
  private StackPane overview;

  // The Tooltip that is used for the node.
  public Tooltip tooltip = OrgChartDemo.getTooltipInstance();

  /**
   *A property that is bound to the {@link CanvasControl#zoomProperty()}.
   */
  private ObjectProperty<ZoomLevel> zoomLevel = new SimpleObjectProperty<>(this, "zoomLevel", ZoomLevel.Overview);
  public ZoomLevel getZoomLevel(){ return zoomLevel.get(); }
  public void setZoomLevel(ZoomLevel level){ zoomLevel.set(level); }
  public ObjectProperty<ZoomLevel> zoomLevelProperty(){ return zoomLevel; }

  // a local reference to the CanvasControl that is provided by the NodeControl#canvasProperty()
  public CanvasControl canvas;


  public void initialize() {

    /* Setup the visual representation and hook up ChangeListeners. */
    addButtons();

    // when the mouse enters the node, fade away the foregroundPane which has a linear gradient and make the background visible.
    root.setOnMouseEntered(mouseEvent -> {
      OrgChartDemo.updateTooltip(root);
      FadeTransition ft = new FadeTransition(Duration.millis(300), foregroundPane);
      ft.setFromValue(1);
      ft.setToValue(0);
      ft.play();
    });
    root.setOnMouseMoved(mouseEvent -> OrgChartDemo.updateTooltip(root));
    // when the mouse exits the node, fade in the foregroundPane which has a linear gradient and hide the background.
    root.setOnMouseExited(mouseEvent -> {
      FadeTransition ft = new FadeTransition(Duration.millis(300), foregroundPane);
      ft.setFromValue(0);
      ft.setToValue(1);
      ft.play();
    });

    // make the buttons visible on hover.
    root.hoverProperty().addListener((observableValue, wasHover, isHover) -> {
      if (isHover) {
        showButtons();
      } else {
        hideButtons();
      }
    });

    // change the style of the foregroundPane and the background when the INode is focused.
    root.itemFocusedProperty().addListener((observableValue, oldFocused, newFocused) -> {
      if (newFocused) {
        root.getStyleClass().clear();
        root.getStyleClass().add("hoverFocusedStyle");
        foregroundPane.getStyleClass().clear();
        foregroundPane.getStyleClass().add("normalFocusedStyle");
      } else {
        root.getStyleClass().clear();
        root.getStyleClass().add("hoverUnfocusedStyle");
        foregroundPane.getStyleClass().clear();
        foregroundPane.getStyleClass().add("normalUnfocusedStyle");
      }
    });

    // set up initially the content.
    updateContent(getZoomLevel());

    // make the foregroundPane exactly as wide and tall as the node.
    foregroundPane.minHeightProperty().bind(root.heightProperty());
    foregroundPane.minWidthProperty().bind(root.widthProperty());

    // wire up the buttons to execute the commands that are in OrgChartDemo
    showParentButton.setOnAction(actionEvent -> OrgChartDemo.SHOW_PARENT.execute(root.getItem(), canvas));
    OrgChartDemo.SHOW_PARENT.addCanExecuteChangedListener((source, args) ->
        setEnabled(showParentButton, OrgChartDemo.SHOW_PARENT));
    hideParentButton.setOnAction(actionEvent -> OrgChartDemo.HIDE_PARENT.execute(root.getItem(), canvas));
    OrgChartDemo.HIDE_PARENT.addCanExecuteChangedListener((source, args) ->
        setEnabled(hideParentButton, OrgChartDemo.HIDE_PARENT));
    showChildrenButton.setOnAction(actionEvent -> OrgChartDemo.SHOW_CHILDREN.execute(root.getItem(), canvas));
    OrgChartDemo.SHOW_CHILDREN.addCanExecuteChangedListener((source, args) ->
        setEnabled(showChildrenButton, OrgChartDemo.SHOW_CHILDREN));
    hideChildrenButton.setOnAction(actionEvent -> OrgChartDemo.HIDE_CHILDREN.execute(root.getItem(), canvas));
    OrgChartDemo.HIDE_CHILDREN.addCanExecuteChangedListener((source, args) ->
        setEnabled(hideChildrenButton, OrgChartDemo.HIDE_CHILDREN));

    // hide all buttons per default
    hideButtons();

    // when the zoom level changes, exchange the content if needed.
    zoomLevelProperty().addListener((observableValue, oldZoomLevel, newZoomLevel) -> {
      if (oldZoomLevel != newZoomLevel) {
        updateContent(newZoomLevel);
      }
    });

    // one-time listener that waits for the canvas control to show up and removes itself when this happens.
    root.canvasProperty().addListener(new ChangeListener<CanvasControl>() {
      @Override
      public void changed(ObservableValue<? extends CanvasControl> observableValue, CanvasControl wasNull, CanvasControl initialValue) {
        if (initialValue != null){
          canvas = initialValue;
          canvas.zoomProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number1) {
              if(!number.equals(number1)){
                double zoom = number1.doubleValue();
                ZoomLevel newZoomLevel = zoomToZoomLevel(zoom);
                if (!getZoomLevel().equals(newZoomLevel)) {
                  setZoomLevel(newZoomLevel);
                }
              }
            }
          });
          setZoomLevel(zoomToZoomLevel(canvas.getZoom()));
          root.canvasProperty().removeListener(this);
        }
      }
    });

    // one-time listener that waits for the item to show up and removes itself when this happens.
    root.contentProperty().addListener(new ChangeListener<Object>() {
      @Override
      public void changed(ObservableValue<?> observableValue, Object oldEmployeeWrapper, Object newEmployeeWrapper) {
        if (newEmployeeWrapper != null){
          Employee employee = ((EmployeeWrapper) newEmployeeWrapper).getEmployee();
          root.setEmployee(employee);
          root.setStatusColor(OrgChartDemo.statusToColor(employee.getStatus()));
          root.setImage(ImageProvider.valueOf(employee.getIcon()));

          String fullName = employee.getFirstName()+" "+ employee.getName();
          String shortName = toShortName(fullName);
          root.setShortDisplayName(shortName);
          root.setFullDisplayName(fullName);

          root.contentProperty().removeListener(this);
        }
      }
    });

    // install the Tooltip to the root node.
    Tooltip.install(root, tooltip);
  }

  private void addButtons() {
    // Buttons that appear when the mouse hovers a node, if they are executable. The styling of the button is
    // done via css.

    buttons = new VBox(1);
    buttons.setAlignment(Pos.BOTTOM_RIGHT);
    buttons.getStyleClass().add("detail");
    buttons.getStylesheets().add("/complete/orgchart/Buttons.css");
    buttons.setPadding(new Insets(0, 5, 5, 0));

    HBox parentBox = new HBox(1);
    parentBox.setAlignment(Pos.BOTTOM_RIGHT);
    showParentButton = new Button("\u2227");
    showParentButton.getStyleClass().add("parentbutton");
    showParentButton.setMinSize(15, 15);
    showParentButton.setTooltip(new Tooltip("Show supervisor"));

    hideParentButton = new Button("\u2228");
    hideParentButton.getStyleClass().add("parentbutton");
    hideParentButton.setMinSize(15, 15);
    hideParentButton.setTooltip(new Tooltip("Hide supervisor"));
    parentBox.getChildren().addAll(showParentButton, hideParentButton);

    HBox childBox = new HBox(1);
    childBox.setAlignment(Pos.BOTTOM_RIGHT);
    showChildrenButton = new Button("+");
    showChildrenButton.getStyleClass().add("childbutton");
    showChildrenButton.setMinSize(15, 15);
    showChildrenButton.setTooltip(new Tooltip("Show subordinate"));

    hideChildrenButton = new Button("\u2013");
    hideChildrenButton.getStyleClass().add("childbutton");
    hideChildrenButton.setMinSize(15, 15);
    hideChildrenButton.setTooltip(new Tooltip("Hide subordinate"));
    childBox.getChildren().addAll(showChildrenButton, hideChildrenButton);

    buttons.getChildren().addAll(parentBox, childBox);
    root.getChildren().add(buttons);
  }

  /**
   * Makes the buttons which are executable visible.
   */
  private void showButtons() {
    showParentButton.setVisible(true);
    hideParentButton.setVisible(true);
    showChildrenButton.setVisible(true);
    hideChildrenButton.setVisible(true);
    setEnabled(showParentButton, OrgChartDemo.SHOW_PARENT);
    setEnabled(hideParentButton, OrgChartDemo.HIDE_PARENT);
    setEnabled(showChildrenButton, OrgChartDemo.SHOW_CHILDREN);
    setEnabled(hideChildrenButton, OrgChartDemo.HIDE_CHILDREN);
  }

  /**
   * Enables and disables the given button depending on the given command's
   * <code>canExecute</code> state.
   */
  private void setEnabled(Button button, ICommand command) {
    INode node = root.getItem();
    if (((GraphControl) canvas).getGraph().contains(node)) {
      button.setDisable(!command.canExecute(node, canvas));
    } else {
      button.setDisable(true);
    }
  }

  /**
   * Hides all buttons.
   */
  private void hideButtons() {
    showParentButton.setVisible(false);
    hideParentButton.setVisible(false);
    showChildrenButton.setVisible(false);
    hideChildrenButton.setVisible(false);
  }

  /**
   * Converts a double-valued zoom into a {@link ZoomLevel}.
   * @return
   * <ul>
   *   <li>{@link ZoomLevel#Detail} if the zoom is greater than 0.7</li>
   *   <li>{@link ZoomLevel#Intermediate} if the zoom is greater than 0.2 and smaller than 0.7</li>
   *   <li>{@link ZoomLevel#Overview} if the zoom is smaller than 0.2</li>
   * </ul>
   */
  private ZoomLevel zoomToZoomLevel(double zoom) {
    if (zoom > 0.7){
      return ZoomLevel.Detail;
    } else if (zoom > 0.3){
      return ZoomLevel.Intermediate;
    } else {
      return ZoomLevel.Overview;
    }
  }

  // converts a string like "Johann Wolfgang Goethe" into "J. Goethe"
  public String toShortName(String fullName) {
    String[] names = fullName.split(" ");
    String shortName;
    if (names.length > 1) {
      shortName = names[0].substring(0, 1) + ". " + names[names.length - 1];
    } else {
      shortName = names[0];
    }
    return shortName;
  }

  private static final String STATE_DETAIL = "detailState";
  private static final String STATE_INTERMEDIATE = "intermediateState";
  private static final String STATE_OVERVIEW = "overviewState";

  private String currentState;

  // adds the zoom-level dependent styleClass  to the root
  private void updateContent(ZoomLevel newZoomLevel) {
    if (currentState != null) {
      // remove the style class of the last zoomLevel
      root.getStyleClass().remove(currentState);
    }
    root.getChildren().remove(overview);
    root.getChildren().remove(intermediate);
    root.getChildren().remove(detail);
    Node content;
    if (newZoomLevel == ZoomLevel.Overview){
      if (overview == null) {
        // lazy loading of the overview state
        overview = new EmployeeOverview(root);
      }
      content = overview;
      currentState = STATE_OVERVIEW;
      tooltip.setGraphic(RoughTooltip.getInstance());
    } else  if (newZoomLevel == ZoomLevel.Intermediate){
      if (intermediate == null) {
        // lazy loading of the intermediate state
        intermediate = new EmployeeIntermediate(root);
      }
      content = intermediate;
      currentState = STATE_INTERMEDIATE;
      tooltip.setGraphic(RoughTooltip.getInstance());
    } else{
      if (detail == null) {
        // lazy loading of the detail state
        detail = new EmployeeDetail(root);
      }
      content = detail;
      currentState = STATE_DETAIL;
      tooltip.setGraphic(DetailTooltip.getInstance());
    }
    root.getChildren().add(1, content);
    // set current state as style class
    root.getStyleClass().add(currentState);
  }

  /**
   * Returns the root object of this.
   */
  public NodeTemplate getRoot() {
    return root;
  }
}
