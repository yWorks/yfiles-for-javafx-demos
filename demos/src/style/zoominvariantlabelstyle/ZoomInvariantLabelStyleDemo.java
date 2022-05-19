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
package style.zoominvariantlabelstyle;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.SelectionIndicatorManager;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.function.Consumer;

/**
 * Demonstrates zoom-invariant label rendering.
 */
public class ZoomInvariantLabelStyleDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;
  public ComboBox modes;
  public Label maxZoomLbl;
  public Slider maxZoomSlider;
  public Label maxZoomValue;
  public Label minZoomLbl;
  public Slider minZoomSlider;
  public Label minZoomValue;
  public Label zoomValue;

  /**
   * Configures user interaction and creates a sample diagram.
   */
  public void initialize() {
    initializeControls();


    // prevent adding, removing, or editing elements but support selecting
    // elements and panning the view
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setSelectableItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE).or(GraphItemTypes.LABEL));
    graphControl.setInputMode(gvim);


    // load a sample graph
    loadGraph();
  }

  private void initializeControls() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);


    // display the current zoom factor of the demo's graph control
    graphControl.zoomProperty().addListener(new SetLabelText(zoomValue, 2));

    // initialize slider for changing the minimum zoom value for styles
    // ZoomInvariantBelowThresholdLabelStyle and ZoomInvariantOutsideRangeLabelStyle
    minZoomSlider.valueProperty().addListener(new SetLabelText(minZoomValue, 1));
    minZoomSlider.valueProperty().addListener(new SliderValueHandler(this::onMinZoomChanged));
    minZoomSlider.valueChangingProperty().addListener(new SliderValueChangingHandler(this::onMinZoomChanged));
    Consumer<Boolean> minZoomControlsState = enabled -> {
      boolean state = enabled == Boolean.FALSE;
      minZoomLbl.setDisable(state);
      minZoomSlider.setDisable(state);
      minZoomValue.setDisable(state);
    };
    minZoomControlsState.accept(Boolean.TRUE);

    // initialize slider for changing the maximum zoom value for styles
    // ZoomInvariantAboveThresholdLabelStyle and ZoomInvariantOutsideRangeLabelStyle
    maxZoomSlider.valueProperty().addListener(new SetLabelText(maxZoomValue, 1));
    maxZoomSlider.valueProperty().addListener(new SliderValueHandler(this::onMaxZoomChanged));
    maxZoomSlider.valueChangingProperty().addListener(new SliderValueChangingHandler(this::onMaxZoomChanged));
    Consumer<Boolean> maxZoomControlsState = enabled -> {
      boolean state = enabled == Boolean.FALSE;
      maxZoomLbl.setDisable(state);
      maxZoomSlider.setDisable(state);
      maxZoomValue.setDisable(state);
    };
    maxZoomControlsState.accept(Boolean.FALSE);

    // initialize drop-down list for choosing label styles with different
    // rendering behavior
    modes.getItems().addAll(Mode.values());
    modes.setValue(Mode.FIXED_BELOW_THRESHOLD);
    modes.valueProperty().addListener((property, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }

      Mode mode = (Mode) newValue;

      setLabelStyles(mode, minZoomSlider.getValue(), maxZoomSlider.getValue());

      switch (mode) {
        case FIXED_ABOVE_THRESHOLD:
          minZoomControlsState.accept(Boolean.FALSE);
          maxZoomControlsState.accept(Boolean.TRUE);
          break;
        case FIXED_BELOW_THRESHOLD:
          minZoomControlsState.accept(Boolean.TRUE);
          maxZoomControlsState.accept(Boolean.FALSE);
          break;
        case INVARIANT_OUTSIDE_RANGE:
          minZoomControlsState.accept(Boolean.TRUE);
          maxZoomControlsState.accept(Boolean.TRUE);
          break;
        default:
          minZoomControlsState.accept(Boolean.FALSE);
          maxZoomControlsState.accept(Boolean.FALSE);
          break;
      }
    });
  }

  /**
   * Centers the sample diagram in the visible area.
   */
  @Override
  protected void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Set the label styles appropriate for the given label rendering policy.
   */
  private void setLabelStyles( Mode mode, double minZoom, double maxZoom ) {
    IGraph graph = graphControl.getGraph();
    IGraphSelection selection = graphControl.getSelection();
    SelectionIndicatorManager<IModelItem> manager = graphControl.getSelectionIndicatorManager();

    for (ILabel label : graph.getLabels()) {
      boolean updateSelectionHighlight = selection.isSelected(label);
      if (updateSelectionHighlight) {
        manager.removeSelection(label);
      }

      graph.setStyle(label, newLabelStyle(mode, minZoom, maxZoom));

      if (updateSelectionHighlight) {
        manager.addSelection(label);
      }
    }
  }

  /**
   * Updates the maximum zoom value of the used label styles.
   */
  private void onMaxZoomChanged( Number value ) {
    if (value == null) {
      return;
    }

    double newValue = value.doubleValue();
    for (ILabel label : graphControl.getGraph().getLabels()) {
      ILabelStyle style = label.getStyle();
      if (style instanceof ZoomInvariantOutsideRangeLabelStyle) {
        ((ZoomInvariantOutsideRangeLabelStyle) style).setMaxZoom(newValue);
      } else if (style instanceof ZoomInvariantAboveThresholdLabelStyle) {
        ((ZoomInvariantAboveThresholdLabelStyle) style).setMaxZoom(newValue);
      }
    }
    graphControl.updateImmediately();
  }

  /**
   * Updates the minimum zoom value of the used label styles.
   */
  private void onMinZoomChanged( Number value ) {
    if (value == null) {
      return;
    }

    double newValue = value.doubleValue();
    for (ILabel label : graphControl.getGraph().getLabels()) {
      ILabelStyle style = label.getStyle();
      if (style instanceof ZoomInvariantOutsideRangeLabelStyle) {
        ((ZoomInvariantOutsideRangeLabelStyle) style).setMinZoom(newValue);
      } else if (style instanceof ZoomInvariantBelowThresholdLabelStyle) {
        ((ZoomInvariantBelowThresholdLabelStyle) style).setMinZoom(newValue);
      }
    }
    graphControl.updateImmediately();
  }

  /**
   * Loads a sample graph.
   */
  private void loadGraph() {
    GraphMLIOHandler graphMLIOHandler = graphControl.getGraphMLIOHandler();
    graphMLIOHandler.addXamlNamespaceMapping(
      "http://www.yworks.com/yfiles-for-javafx/demos/zoominvariantlabelstyle/1.0",
      AbstractZoomInvariantLabelStyle.class);

    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void main( String[] args) {
    launch(args);
  }

  /**
   * Create a new label style instance that demonstrates the given policy
   * for zoom-invariant label rendering.
   */
  private static ILabelStyle newLabelStyle( Mode mode, double minZoom, double maxZoom ) {
    switch (mode) {
      case DEFAULT:
        return new DefaultLabelStyle();
      case FIXED_ABOVE_THRESHOLD:
        ZoomInvariantAboveThresholdLabelStyle invariantAboveStyle =
          new ZoomInvariantAboveThresholdLabelStyle();
        invariantAboveStyle.setMaxZoom(maxZoom);
        return invariantAboveStyle;
      case FIXED_BELOW_THRESHOLD:
        ZoomInvariantBelowThresholdLabelStyle invariantBelowStyle =
          new ZoomInvariantBelowThresholdLabelStyle();
        invariantBelowStyle.setMinZoom(minZoom);
        return invariantBelowStyle;
      case INVARIANT_OUTSIDE_RANGE:
        ZoomInvariantOutsideRangeLabelStyle invariantRangeStyle =
          new ZoomInvariantOutsideRangeLabelStyle();
        invariantRangeStyle.setMaxZoom(maxZoom);
        invariantRangeStyle.setMinZoom(minZoom);
        return invariantRangeStyle;
      case FIT_OWNER:
        return new FitOwnerLabelStyle();
      default:
        throw new IllegalArgumentException();
    }
  }



  private static class SliderValueHandler implements ChangeListener<Number> {
    private final Consumer<Number> onValueChanged;

    SliderValueHandler( Consumer<Number> onValueChanged ) {
      this.onValueChanged = onValueChanged;
    }

    @Override
    public void changed(
      ObservableValue<? extends Number> property, Number oldValue, Number newValue
    ) {
      if (newValue == null) {
        return;
      }

      Slider slider = (Slider) ((ReadOnlyProperty) property).getBean();
      if (!slider.isValueChanging()) {
        onValueChanged.accept(newValue);
      }
    }
  }

  private static class SliderValueChangingHandler implements ChangeListener<Boolean> {
    private final Consumer<Number> onValueChanged;

    SliderValueChangingHandler( Consumer<Number> onValueChanged ) {
      this.onValueChanged = onValueChanged;
    }

    @Override
    public void changed(
      ObservableValue<? extends Boolean> property, Boolean oldValue, Boolean newValue
    ) {
      if (Boolean.FALSE.equals(newValue)) {
        Slider slider = (Slider) ((ReadOnlyProperty) property).getBean();
        onValueChanged.accept(slider.getValue());
      }
    }
  }

  /**
   * Displays decimal values as label text.
   */
  private static class SetLabelText implements ChangeListener<Number> {
    private final Label label;
    private final NumberFormat format;

    SetLabelText( Label label, int fractionDigits ) {
      this.label = label;
      this.format = newFormat(fractionDigits);
    }

    @Override
    public void changed(
      ObservableValue<? extends Number> observable, Number oldValue, Number newValue
    ) {
      if (newValue != null) {
        label.setText(format.format(newValue.doubleValue()));
      }
    }

    private static NumberFormat newFormat( int fractionDigits ) {
      NumberFormat nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(fractionDigits);
      nf.setMinimumFractionDigits(fractionDigits);
      return nf;
    }
  }

  /**
   * Enumerates the supported zoom-invariant label rendering policies.
   */
  enum Mode {
    DEFAULT("Default Label Style"),
    FIXED_ABOVE_THRESHOLD("Fixed above maximum zoom"),
    FIXED_BELOW_THRESHOLD("Fixed below minimum zoom"),
    INVARIANT_OUTSIDE_RANGE("Fixed when outside specified range"),
    FIT_OWNER("Fit into the label's owner");


    private final String description;

    Mode( String description ) {
      this.description = description;
    }


    @Override
    public String toString() {
      return description;
    }
  }
}
