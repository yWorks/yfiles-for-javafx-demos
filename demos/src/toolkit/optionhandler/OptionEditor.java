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
package toolkit.optionhandler;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.w3c.dom.Document;

/**
 * Builds the editor component for an option configuration.
 * @author Thomas Behr
 */
public class OptionEditor {
  static final int EDITOR_WIDTH = 340;
  static final int TEXT_WIDTH = 300;


  private Object configuration;

  public Object getConfiguration() {
    return configuration;
  }

  public void setConfiguration( Object configuration ) {
    this.configuration = configuration;
  }

  public Parent buildEditor() {
    ConfigConverter converter = new ConfigConverter();
    OptionGroup options = converter.convert(getConfiguration());
    ConstraintManager cm = new ConstraintManager();
    Parent editor = newEditorComponent(options, 0, cm);
    editor.setUserData(options);

    // set initial enabled/disabled states
    cm.valueChanged();

    return editor;
  }

  public void resetEditor( Parent editor ) {
    Object value = editor.getUserData();
    if (value instanceof OptionGroup) {
      final ConstraintManager cm = find((OptionGroup) value);
      cm.setEnabled(false);
      try {
        final ArrayList<Node> stack = new ArrayList<Node>();
        stack.add(editor);
        while (!stack.isEmpty()) {
          final Node node = stack.remove(stack.size() - 1);

          final Object nv = node.getUserData();
          if (nv instanceof Option) {
            final Option option = (Option) nv;
            final Object defaultValue = option.getDefaultValue();

            switch (option.getComponentType()) {
              case CHECKBOX:
                ((CheckBox) node).setSelected(Boolean.TRUE.equals(defaultValue));
                break;
              case COMBOBOX:
                ((ComboBox) node).setValue(defaultValue);
                break;
              case FORMATTED_TEXT:
                setValue((WebView) node, (String) defaultValue);
                break;
              case SLIDER:
                setValue((Spinner) node, (Number) defaultValue);
                break;
              case SPINNER:
                setValue((Spinner) node, (Number) defaultValue);
                break;
              case OPTION_GROUP:
                break;
              default:
                // RADIO_BUTTON
                // TEXT
                throw new UnsupportedOperationException(
                        "Component type " +
                        option.getComponentType() +
                        " not yet supported.");
            }
          }

          if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
              stack.add(child);
            }
          }
        }
      } finally {
        cm.setEnabled(true);
        cm.valueChanged();
      }
    }
  }


  private Parent newEditorComponent(
          OptionGroup group,
          int level,
          ConstraintManager cm
  ) {
    int columnCount = 3;
    int firstColumn = 0;

    VBox root = new VBox();
    root.setId(group.getName());
    root.setFillWidth(true);

    GridPane contents = newGridPane(Priority.SOMETIMES, Priority.ALWAYS, Priority.ALWAYS);
    if (level == 0) {
      contents.setVgap(1);
      root.setPrefWidth(EDITOR_WIDTH);
    }

    String label = group.getLabel();
    if (level > 0 && label != null) {
      Accordion titled = new Accordion();
      titled.setId(label);
      titled.getPanes().add(new TitledPane(label, contents));
      if (level > 1) {
        titled.setExpandedPane(titled.getPanes().get(0));
      }
      root.getChildren().add(titled);
    } else {
      root.getChildren().add(contents);
    }

    int row = 0;
    for (Option child : group.getChildOptions()) {
      if (child instanceof OptionGroup) {
        Parent editor = newEditorComponent((OptionGroup) child, level + 1, cm);
        contents.add(editor, firstColumn, row, columnCount, 1);
      } else {
        Consumer<Object> setter = child.getSetter();
        if (setter != null) {
          child.setSetter(new NotifyValueChanged(setter, cm));
        }

        Supplier<Boolean> disabled = child.getCheckDisabled();

        switch (child.getComponentType()) {
          case CHECKBOX:
            Control checkBoxLabel = newLabel(child.getLabel());
            CheckBox checkBox = new CheckBox();
            checkBox.setUserData(child);
            checkBox.setSelected(Boolean.TRUE.equals(child.getValue()));
            checkBox.selectedProperty().addListener(
                    (observable, oldValue, newValue) -> child.setValue(newValue));

            if (disabled != null) {
              cm.addValueChangedListener(new SetEnabled(checkBoxLabel, disabled));
              cm.addValueChangedListener(new SetEnabled(checkBox, disabled));
            }

            contents.add(checkBoxLabel, firstColumn, row, 2, 1);
            GridPane.setHalignment(checkBox, HPos.RIGHT);
            contents.add(checkBox, 2, row, 1, 1);
            break;
          case COMBOBOX:
            Control comboBoxLabel = newLabel(child.getLabel());
            ComboBox comboBox = newComboBox(child);
            comboBox.setUserData(child);

            if (disabled != null) {
              cm.addValueChangedListener(new SetEnabled(comboBoxLabel, disabled));
              cm.addValueChangedListener(new SetEnabled(comboBox, disabled));
            }

            contents.add(comboBoxLabel, firstColumn, row, 1, 1);
            GridPane.setHalignment(comboBox, HPos.RIGHT);
            contents.add(comboBox, 1, row, 2, 1);
            break;
          case FORMATTED_TEXT:
            WebView webView = WebViewFactory.INSTANCE.newWebView();
            setValue(webView, (String) child.getValue());
            webView.setUserData(child);
            contents.add(webView, firstColumn, row, columnCount, 1);
            break;
          case SLIDER:
            Parent component = newExtendedSlider(child);

            if (disabled != null) {
              for (Node node : component.getChildrenUnmodifiable()) {
                cm.addValueChangedListener(new SetEnabled(node, disabled));
              }
            }

            contents.add(component, firstColumn, row, columnCount, 1);
            break;
          case SPINNER:
            Control spinnerLabel = newLabel(child.getLabel());
            Spinner spinner = newSpinner(child);
            spinner.setUserData(child);

            if (disabled != null) {
              cm.addValueChangedListener(new SetEnabled(spinnerLabel, disabled));
              cm.addValueChangedListener(new SetEnabled(spinner, disabled));
            }

            contents.add(spinnerLabel, firstColumn, row, 1, 1);
            GridPane.setHalignment(spinner, HPos.RIGHT);
            contents.add(spinner, 1, row, 2, 1);
            break;
          case OPTION_GROUP:
            break;
          default:
            // RADIO_BUTTON
            // TEXT
            throw new UnsupportedOperationException(
                    "Component type " +
                    child.getComponentType() +
                    " not yet supported.");
        }
      }

      ++row;
    }

    return root;
  }

  private ComboBox<Object> newComboBox( Option option ) {
    HashMap<Object, String> map = new HashMap<Object, String>();
    HashMap<String, Object> inverse = new HashMap<>();
    ComboBox<Object> comboBox = new ComboBox<Object>();
    comboBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    comboBox.setEditable(false);
    comboBox.setConverter(new MappedConverter(map, inverse));
    comboBox.setCellFactory(new MappedRenderer(map));
    for (EnumValue ev : option.getEnumValues()) {
      Object value = ev.getValue();
      comboBox.getItems().add(value);

      String name = ev.getName();
      map.put(value, name);
      if (!inverse.containsKey(name)) {
        inverse.put(name, value);
      }
    }
    comboBox.setValue(option.getValue());
    comboBox.valueProperty().addListener(
            (observable, oldValue, newValue) -> option.setValue(newValue));
    return comboBox;
  }

  /**
   * Creates a compound component consisting of a {@link Slider} and a
   * {@link Spinner} instance. The values of the slider and the spinner will
   * be synchronized (i.e. changing one will also change the other).
   */
  private Parent newExtendedSlider( final Option option ) {
    Slider slider = new Slider();
    Spinner spinner = new Spinner();
    spinner.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    spinner.setUserData(option);
    configure(option, slider, spinner);

    GridPane pane = newGridPane(Priority.SOMETIMES, Priority.NEVER);
    pane.setVgap(0);
    pane.add(newLabel(option.getLabel()), 0, 0, 2, 1);
    pane.add(slider, 0, 1, 1, 1);
    GridPane.setHalignment(spinner, HPos.RIGHT);
    pane.add(spinner, 1, 1, 1, 1);
    return pane;
  }

  private void configure( Option option, Slider slider, Spinner spinner ) {
    slider.setSnapToTicks(true);

    ChangeEventLock lock = new ChangeEventLock();
    Object value = option.getValue();
    MinMax minMax = option.getMinMax();
    if (Integer.TYPE.equals(option.getValueType())) {
      int min = minMax == null ? 0 : (int) minMax.min();
      int max = minMax == null ? 0 : (int) minMax.max();
      int step = minMax == null ? 1 : (int) minMax.step();
      int v = value instanceof Number ? ((Number) value).intValue() : min;

      int span = max - min;
      span = span + step - (span % step);

      slider.setMin(min);
      slider.setMax(max);
      slider.setValue(v);
      slider.setMajorTickUnit(span + step);
      slider.setMinorTickCount(span / step);

      slider.valueProperty().addListener(new AbstractValueHandler(lock, option) {
        @Override
        void changedCore( final Number newValue ) {
          super.changedCore(Integer.valueOf(newValue.intValue()));
        }

        @Override
        void publish( Number newValue ) {
          setValue(spinner, newValue);
        }
      });

      spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, v, step));
      spinner.valueProperty().addListener(new AbstractValueHandler(lock, option) {
        @Override
        void publish( Number newValue ) {
          slider.setValue(newValue.intValue());
        }
      });
    } else {
      double min = minMax == null ? 0 : minMax.min();
      double max = minMax == null ? 0 : minMax.max();
      double step = minMax == null ? 1.0 : minMax.step();
      double v = value instanceof Number ? ((Number) value).doubleValue() : min;

      slider.setMin(min);
      slider.setMax(max);
      slider.setValue(v);

      if (step == 1) {
        int span = (int) Math.ceil(max - min);
        slider.setMajorTickUnit(span + step);
        slider.setMinorTickCount(span);
        slider.setBlockIncrement(step);
      } else {
        int ticks = (int) Math.ceil((max - min) / step);
        double span = ticks * step;
        slider.setMajorTickUnit(span + step);
        slider.setMinorTickCount(ticks);
        slider.setBlockIncrement(step);
      }

      slider.valueProperty().addListener(new AbstractValueHandler(lock, option) {
        @Override
        void publish( Number newValue ) {
          setValue(spinner, newValue);
        }
      });

      spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, v, step));
      spinner.valueProperty().addListener(new AbstractValueHandler(lock, option) {
        @Override
        void publish( Number newValue ) {
          slider.setValue(newValue.doubleValue());
        }
      });
    }
  }

  private Spinner newSpinner( final Option option ) {
    Spinner spinner = new Spinner();
    spinner.setEditable(true);
    spinner.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

    Object value = option.getValue();
    MinMax minMax = option.getMinMax();
    if (Integer.TYPE.equals(option.getValueType())) {
      int min = minMax == null ? Integer.MIN_VALUE : (int) minMax.min();
      int max = minMax == null ? Integer.MAX_VALUE : (int) minMax.max();
      int step = minMax == null ? 1 : (int) minMax.step();
      int v = value instanceof Number ? ((Number) value).intValue() : min;

      SpinnerValueFactory.IntegerSpinnerValueFactory factory =
              new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, v, 1);
      factory.setConverter(new SafeConverter<>(
              factory, oldValue -> Integer.valueOf(oldValue.intValue() - factory.getAmountToStepBy())));
      spinner.setValueFactory(factory);
    } else {
      double min = minMax == null ? -Double.MAX_VALUE : minMax.min();
      double max = minMax == null ? Double.MAX_VALUE : minMax.max();
      double step = minMax == null ? 1.0 : minMax.step();
      double v = value instanceof Number ? ((Number) value).doubleValue() : min;

      SpinnerValueFactory.DoubleSpinnerValueFactory factory =
              new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, v, 1);
      factory.setConverter(new SafeConverter<>(
              factory, oldValue -> Double.valueOf(oldValue.doubleValue() - factory.getAmountToStepBy())));
      spinner.setValueFactory(factory);
    }

    spinner.valueProperty().addListener(
            (observable, oldValue, newValue) -> option.setValue(newValue));

    return spinner;
  }

  private Labeled newLabel( String label ) {
    return new javafx.scene.control.Label(label);
  }

  private static GridPane newGridPane( Priority... horizontal ) {
    GridPane pane = new GridPane();
    pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    pane.setHgap(6);
    pane.setVgap(6);
    if (horizontal != null) {
      for (int i = 0; i < horizontal.length; ++i) {
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHgrow(horizontal[i]);
        pane.getColumnConstraints().add(constraints);
      }
    }
    return pane;
  }

  /**
   * Wraps the given HTML snippet in a <code>&lt;div&gt;</code> block element
   * that restricts the display width of the HTML snippet to the given
   * preferred width.
   */
  private static String wrap( String text, int preferredWidth ) {
    int w = preferredWidth;
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head></head><body><div style=\"width: ")
      .append(w)
      .append("\">")
      .append(text)
      .append("</div></body></html>\n");
    return sb.toString();
  }

  static void setValue( Spinner spinner, Number newValue ) {
    spinner.getValueFactory().setValue(newValue);
  }

  static void setValue( WebView webView, String newValue ) {
    webView.getEngine().loadContent(wrap(newValue, TEXT_WIDTH));
  }



  private static ConstraintManager find( final OptionGroup group ) {
    final Consumer<Object> setter = group.getSetter();
    if (setter instanceof NotifyValueChanged) {
      return ((NotifyValueChanged) setter).cm;
    }

    final ConstraintManager cm = findImpl(group);
    return cm == null ? new ConstraintManager() : cm;
  }

  private static ConstraintManager findImpl( final OptionGroup group ) {
    for (Option option : group.getChildOptions()) {
      final Consumer<Object> setter = option.getSetter();
      if (setter instanceof NotifyValueChanged) {
        return ((NotifyValueChanged) setter).cm;
      }

      if (option instanceof OptionGroup) {
        final ConstraintManager cm = findImpl((OptionGroup) option);
        if (cm != null) {
          return cm;
        }
      }
    }

    return null;
  }



  private static class MappedRenderer implements Callback<ListView<Object>, ListCell<Object>> {
    final Map<Object, String> map;

    MappedRenderer( Map<Object, String> map ) {
      this.map = map;
    }

    @Override
    public ListCell<Object> call( ListView<Object> param ) {
      return new MappedCell(map);
    }
  }

  private static class MappedCell extends ListCell<Object> {
    final Map<Object, String> map;

    MappedCell( Map<Object, String> map ) {
      this.map = map;
      setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem( Object item, boolean empty ) {
      super.updateItem(item, empty);

      if (item == null || empty) {
        setText("");
      } else {
        setText(map.get(item));
      }
    }
  }

  private static class MappedConverter extends StringConverter<Object> {
    final Map<Object, String> map;
    final Map<String, Object> inverse;

    MappedConverter( Map<Object, String> map, Map<String, Object> inverse ) {
      this.map = map;
      this.inverse = inverse;
    }

    @Override
    public String toString( final Object object ) {
      return map.get(object);
    }

    @Override
    public Object fromString( final String string ) {
      return inverse.get(string);
    }
  }



  private static final class ChangeEventLock {
    boolean locked;

    public boolean isLocked() {
      return locked;
    }

    public void setLocked( boolean locked ) {
      this.locked = locked;
    }
  }

  /**
   * Abstract base class for synchronizing values between a {@link Slider} and
   * a {@link Spinner} instance. 
   */
  private abstract static class AbstractValueHandler
          implements ChangeListener<Number> {
    final ChangeEventLock lock;
    final Option option;

    AbstractValueHandler( ChangeEventLock lock, Option option ) {
      this.lock = lock;
      this.option = option;
    }

    @Override
    public void changed(
            ObservableValue<? extends Number> observable, Number oldValue, Number newValue
    ) {
      if (lock.isLocked()) {
        return;
      }

      lock.setLocked(true);
      try {
        changedCore(newValue);
      } finally {
        lock.setLocked(false);
      }
    }

    void changedCore( Number newValue ) {
      option.setValue(newValue);
      publish(newValue);
    }

    abstract void publish( Number newValue );
  }

  /**
   * Handles non-numerical text entered in an editable spinner in a graceful
   * way instead of throwing an exception.
   */
  private static final class SafeConverter<T extends Number> extends StringConverter<T> {
    final SpinnerValueFactory<T> factory;
    final StringConverter<T> delegate;
    final Function<T, T> decrease;

    SafeConverter( SpinnerValueFactory<T> factory, Function<T, T> decrease ) {
      this.factory = factory;
      this.delegate = factory.getConverter();
      this.decrease = decrease;
    }

    @Override
    public String toString( T object ) {
      return delegate.toString(object);
    }

    @Override
    public T fromString( String string ) {
      final T oldValue = factory.getValue();
      try {
        return delegate.fromString(string);
      } catch (Exception ex) {
        return decrease.apply(oldValue);
      }
    }
  }

  /**
   * Handles enabled state changes on value changed events.
   */
  private static class ConstraintManager {
    List<Runnable> listeners;
    boolean enabled;

    ConstraintManager() {
      listeners = new ArrayList<Runnable>();
      enabled = true;
    }

    void addValueChangedListener( Runnable l ) {
      listeners.add(l);
    }

    void valueChanged() {
      if (isEnabled()) {
        for (Runnable l : listeners) {
          l.run();
        }
      }
    }

    void setEnabled( boolean enabled ) {
      this.enabled = enabled;
    }

    boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Sets the enabled state of the associated component depending on the
   * value of the associated supplier.
   */
  private static final class SetEnabled implements Runnable {
    final Supplier<Boolean> disabled;
    final Node component;

    SetEnabled( final Node component, final Supplier<Boolean> disabled ) {
      this.disabled = disabled;
      this.component = component;
    }

    @Override
    public void run() {
      setDisabled(Boolean.TRUE.equals(disabled.get()));
    }

    void setDisabled( final boolean disabled ) {
      component.setDisable(disabled);
    }
  }

  /**
   * Notifies the associated constraint manager whenever a new option value
   * is set.
   */
  private static class NotifyValueChanged implements Consumer<Object> {
    Consumer<Object> setter;
    ConstraintManager cm;

    NotifyValueChanged(
            Consumer<Object> setter,
            ConstraintManager cm
    ) {
      this.setter = setter;
      this.cm = cm;
    }

    @Override
    public void accept( Object o ) {
      setter.accept(o);
      cm.valueChanged();
    }
  }


  /**
   * Creates pre-configured {@link WebView} instances.
   */
  private static final class WebViewFactory {
    static final WebViewFactory INSTANCE = new WebViewFactory();

    final URL css;

    private WebViewFactory() {
      css = getClass().getResource("../style/help.css");
    }

    WebView newWebView() {
      WebView webView = new WebView();
      WebEngine engine = webView.getEngine();
      if (css == null) {
        engine.setUserStyleSheetLocation("data:text/plain,body {margin: 5px}");
      } else {
        engine.setUserStyleSheetLocation(css.toExternalForm());
      }
      // workaround for WebView's poor default size calculation
      engine.documentProperty().addListener(new HeightCalculator(webView, engine));
      return webView;
    }
  }

  /**
   * Calculates the preferred height for a {@link WebView} instance depending
   * on the view's current document.
   */
  private static final class HeightCalculator implements ChangeListener<Document> {
    static final int PADDING = 10;

    final WebView view;
    final WebEngine engine;

    HeightCalculator( WebView view, WebEngine engine ) {
      this.view = view;
      this.engine = engine;
    }

    @Override
    public void changed(
            ObservableValue<? extends Document> observable, Document oldValue, Document newValue
    ) {
      if (newValue == null) {
        return;
      }

      double height = getDoubleValue(engine, "height", -1);
      if (height > -1) {
        double t = getDoubleValue(engine, "margin-top", PADDING);
        double b = getDoubleValue(engine, "margin-bottom", PADDING);
        view.setPrefHeight(height + t + b + Math.max(t, b));
      }
    }

    private static double getDoubleValue(
            WebEngine engine, String pn, double defaultValue
    ) {
      try {
        Object value = engine.executeScript(
                "window.getComputedStyle(document.body).getPropertyValue('" + pn + "')");
        if (value == null) {
          return defaultValue;
        } else {
          String s = value.toString().trim();
          if (s.endsWith("px")) {
            s = s.substring(0, s.length() - 2);
          }
          return Double.parseDouble(s);
        }
      } catch (Exception ex) {
        return defaultValue;
      }
    }
  }
}
