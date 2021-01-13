/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package com.yworks.yedlite.toolkit.optionhandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Builds the editor component for an option configuration. 
 * @author Thomas Behr
 */
public class OptionEditor {
  static final String OPTION_KEY= "toolkit.optionhandler.OptionEditor";
  static final int SLIDER_WIDTH = 250;
  static final char DELIMITER = '_';


  private Object configuration;
  private L10n l10n;

  public Object getConfiguration() {
    return configuration;
  }

  public void setConfiguration( Object configuration ) {
    this.configuration = configuration;
  }

  public L10n getLocalization() {
    return l10n;
  }

  public void setLocalization( final L10n localization ) {
    this.l10n = localization;
  }

  public Control buildEditor( final Composite parent ) {
    final ConfigConverter converter = new ConfigConverter();
    final Object cnf = getConfiguration();
    final OptionGroup options = converter.convert(cnf);
    options.setName(cnf.getClass().getSimpleName());
    final ConstraintManager cm = new ConstraintManager();
    final Control editor = newEditorComponent(
            parent, options, 0, "", cm);
    editor.setData(OPTION_KEY, options);

    // set initial enabled/disabled states
    cm.valueChanged();

    return editor;
  }

  private Control newEditorComponent(
          final Composite parent,
          final OptionGroup group,
          final int level,
          final String prefix,
          final ConstraintManager cm
  ) {
    final int columnCount = 3;

    final GridLayout rootLayout = new GridLayout(1, false);
    final int rootStyle = level > 1 ? SWT.BORDER : SWT.NONE;
    final Composite root = new Composite(parent, rootStyle);
    root.setLayout(rootLayout);

    GridData gbc = new GridData();

    if (level > 0) {
      final String label = getLabel(group, prefix);
      if (label != null) {
        newLabel(root, label, true);
        final Label sep = new Label(root, SWT.SEPARATOR | SWT.HORIZONTAL);
        sep.setLayoutData(newGridData(true, 1));
      }
    }

    final GridLayout contentsLayout = newGridLayout(columnCount);
    final Composite contents = new Composite(root, SWT.NONE);
    contents.setLayout(contentsLayout);
    contents.setLayoutData(newGridData(true, 1));

    final String ctx = concat(prefix, group.getName());
    for (Option child : group.getChildOptions()) {
      if (child instanceof OptionGroup) {
        final Control c = newEditorComponent(contents, (OptionGroup) child, level + 1, ctx, cm);
        c.setLayoutData(newGridData(true, columnCount));
      } else {
        final Consumer<Object> setter = child.getSetter();
        if (setter != null) {
          child.setSetter(new NotifyValueChanged(setter, cm));
        }

        final Supplier<Boolean> disabled = child.getCheckDisabled();

        switch (child.getComponentType()) {
          case CHECKBOX:
            final Label checkBoxLabel = newLabel(contents, getLabel(child, ctx), false);
            final Button checkBox = new Button(contents, SWT.CHECK);
            checkBox.setData(OPTION_KEY, child);
            checkBox.setSelection(Boolean.TRUE.equals(child.getValue()));
            checkBox.addSelectionListener(new CheckBoxHandler(child));

            if (disabled != null) {
              cm.addValueChangedListener(new SetEnabled(checkBoxLabel, disabled));
              cm.addValueChangedListener(new SetEnabled(checkBox, disabled));
            }

            checkBoxLabel.setLayoutData(newGridData(true, 2));

            gbc = newGridData(false, 1);
            gbc.horizontalAlignment = SWT.END;
            checkBox.setLayoutData(gbc);
            break;
          case COMBOBOX:
            final Label comboBoxLabel = newLabel(contents, getLabel(child, ctx), false);
            final Combo comboBox = newComboBox(contents, child, ctx);
            comboBox.setData(OPTION_KEY, child);

            if (disabled != null) {
              cm.addValueChangedListener(new SetEnabled(comboBoxLabel, disabled));
              cm.addValueChangedListener(new SetEnabled(comboBox, disabled));
            }

            gbc = newGridData(false, 1);
            gbc.verticalAlignment = SWT.CENTER;
            comboBoxLabel.setLayoutData(gbc);

            comboBox.setLayoutData(newGridData(true, 2));
            break;
          case HTML_BLOCK:
            throw new UnsupportedOperationException(
                    "Component type " +
                    ComponentTypes.HTML_BLOCK +
                    " not yet supported.");
          case RADIO_BUTTON:
            throw new UnsupportedOperationException(
                    "Component type " +
                    ComponentTypes.RADIO_BUTTON +
                    " not yet supported.");
          case SLIDER:
            final String sliderLabel = getLabel(child, ctx);
            final Composite composite = newExtendedSlider(contents, child, sliderLabel);

            if (disabled != null) {
              final Control[] children = composite.getChildren();
              for (int i = 0; i < children.length; ++i) {
                cm.addValueChangedListener(new SetEnabled(children[i], disabled));
              }
            }

            composite.setLayoutData(newGridData(true, columnCount));
            break;
          default:
            break;
        }
      }
    }

    return root;
  }

  private static GridLayout newGridLayout( final int columnCount ) {
    final GridLayout contentsLayout = new GridLayout(columnCount, false);
    contentsLayout.marginWidth = 0;
    contentsLayout.marginLeft = 0;
    contentsLayout.marginRight = 0;
    return contentsLayout;
  }

  private static GridData newGridData( final boolean hFill, final int hSpan ) {
    final GridData gbc = new GridData();
    gbc.verticalAlignment = SWT.BEGINNING;
    gbc.horizontalAlignment = hFill ? SWT.FILL : SWT.BEGINNING;
    gbc.grabExcessHorizontalSpace = hFill;
    gbc.grabExcessVerticalSpace = false;
    gbc.horizontalSpan = hSpan;
    gbc.verticalSpan = 1;
    return gbc;
  }

  private Combo newComboBox(
          final Composite parent, final Option option, final String prefix
  ) {
    final Combo comboBox = new Combo(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
    final List<EnumValue> list = option.getEnumValues();
    if (!list.isEmpty()) {
      final String ctx = concat(prefix, option.getName());
      final Object value = option.getValue();
      final Object[] values = new Object[list.size()];
      final String[] items = new String[list.size()];
      int i = 0;
      int idx = 0;
      for (EnumValue ev : list) {
        values[i] = ev.getValue();
        items[i] = getLabel(ev, ctx);

        if (value == null ? values[i] == null : value.equals(values[i])) {
          idx = i;
        }

        ++i;
      }
      comboBox.setItems(items);
      comboBox.setText(items[idx]);
      comboBox.addSelectionListener(new ComboBoxHandler(option, values));
    }
    return comboBox;
  }

  private Composite newExtendedSlider(
          final Composite parent,
          final Option option,
          final String text
  ) {
    GridData gbc = new GridData();

    final GridLayout paneLayout = newGridLayout(2);
    final Composite pane = new Composite(parent, SWT.NONE);
    pane.setLayout(paneLayout);
    final Label label = newLabel(pane, text, false);
    gbc = newGridData(false, 2);
    gbc.verticalAlignment = SWT.END;
    label.setLayoutData(gbc);

    final Slider slider = new Slider(pane, SWT.HORIZONTAL);
    gbc = newGridData(true, 1);
    gbc.verticalAlignment = SWT.CENTER;
    gbc.widthHint = SLIDER_WIDTH;
    slider.setLayoutData(gbc);

    final Spinner spinner = new Spinner(pane, SWT.NONE);
    gbc = newGridData(false, 1);
    gbc.horizontalAlignment = SWT.END;
    spinner.setLayoutData(gbc);
    spinner.setData(OPTION_KEY, option);

    configure(option, slider, spinner);

    return pane;
  }

  private void configure(
          final Option option, final Slider slider, final Spinner spinner
  ) {
    final Object value = option.getValue();
    final MinMax minMax = option.getMinMax();
    if (Integer.TYPE.equals(option.getValueType())) {
      final int min = minMax == null ? 0 : (int) minMax.min();
      final int max = minMax == null ? 0 : (int) minMax.max();
      final int step = minMax == null ? 1 : (int) minMax.step();
      final int v = value instanceof Number ? ((Number) value).intValue() : min;

      final SelectionListener handler = new IntValueHandler(option, slider, spinner);

      slider.setValues(v, min, max, step, step, step);
      slider.addSelectionListener(handler);

      spinner.setValues(v, min, max, 0, step, step);
      spinner.addSelectionListener(handler);
    } else {
      final double min = minMax == null ? 0 : minMax.min();
      final double max = minMax == null ? 0 : minMax.max();
      final double step = minMax == null ? 1.0 : minMax.step();
      final double v = value instanceof Number ? ((Number) value).doubleValue() : min;

      final int smin = (int) Math.rint(min / step);
      final int smax = (int) Math.rint(max / step);
      final int sv = (int) Math.rint(v / step);

      final SelectionListener handler = new DoubleValueHandler(option, slider, spinner, step);

      slider.setIncrement(1);
      slider.setMaximum(smax);
      slider.setMinimum(smin);
      slider.setSelection(sv);
      slider.setValues(sv, smin, smax, 1, 1, 1);
      slider.addSelectionListener(handler);

      final int digits = step < 1 ? simpleLog(step) : 1;
      spinner.setValues(sv, smin, smax, digits, 1, 1);
      spinner.addSelectionListener(handler);
    }
  }


  private String getLabel( final Option option, final String prefix ) {
    final String key = concat(prefix, option.getName());
    return getLocalization().get(key);
  }

  private String getLabel( final EnumValue ev, final String prefix ) {
    final String key = prefix + "_value_" + asKey(ev.getName());
    return getLocalization().get(key);
  }


  private static String concat( final String prefix, final String name ) {
    return "".equals(prefix) ? name : prefix + DELIMITER + name;
  }

  private static String asKey( final String name ) {
    final int n = name.length();
    int count = 0;
    for (int i = 0; i < n; ++i) {
      final char ch = name.charAt(i);
      if (Character.isWhitespace(ch)) {
        ++count;
      } else {
        switch (ch) {
          case '+':
          case '-':
          case '(':
          case ')':
            ++count;
            break;
        }
      }
    }
    if (count == 0) {
      return name;
    } else if (count < n) {
      final StringBuilder sb = new StringBuilder(n);
      for (StringTokenizer st = new StringTokenizer(name, " \t\r\n+-()");
           st.hasMoreTokens();) {
        sb.append(capitalize(st.nextToken()));
      }
      return sb.toString();
    } else {
      return "";
    }
  }

  private static String capitalize( final String s ) {
    if ("".equals(s)) {
      return "";
    } else {
      return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
  }

  private static int simpleLog( final double d ) {
    return Double.toString(d).length() - 2;
  }

  private static Label newLabel(
          final Composite parent, final String text, final boolean headline
  ) {
    final Label label = new Label(parent, SWT.LEFT);
    if (headline) {
      label.setFont(newFont(label.getFont(), 2, SWT.BOLD));
    }
    label.setText(text);
    return label;
  }

  private static Font newFont( final Font font, final int size, final int style ) {
    final FontData[] fd = font.getFontData();
    if (fd.length < 1) {
      return font;
    } else {
      final FontData oldFd = fd[0];
      final FontData newFd = new FontData(
              oldFd.getName(), oldFd.getHeight() + size, oldFd.getStyle() | style);
      return new Font(font.getDevice(), newFd);
    }
  }


  private static class CheckBoxHandler implements SelectionListener {
    final Option option;

    CheckBoxHandler( final Option option ) {
      this.option = option;
    }

    @Override
    public void widgetSelected( final SelectionEvent e ) {
      final Button src = (Button) e.getSource();
      option.setValue(src.getSelection() ? Boolean.TRUE : Boolean.FALSE);
    }

    @Override
    public void widgetDefaultSelected( final SelectionEvent e ) {
    }
  }

  private static class ComboBoxHandler implements SelectionListener {
    final Option option;
    final Object[] values;

    ComboBoxHandler( final Option option, final Object[] values ) {
      this.option = option;
      this.values = values;
    }

    @Override
    public void widgetSelected( final SelectionEvent e ) {
      final Combo src = (Combo) e.getSource();
      final int idx = src.getSelectionIndex();
      option.setValue(values[idx]);
    }

    @Override
    public void widgetDefaultSelected( final SelectionEvent e ) {
    }
  }

  /**
   * Abstract base class for synchronizing values between a {@link Slider} and
   * a {@link Spinner} instance. 
   */
  private abstract static class AbstractValueHandler implements SelectionListener {
    final Option option;
    final Slider slider;
    final Spinner spinner;

    boolean armed;

    AbstractValueHandler(Option option, Slider slider, Spinner spinner) {
      this.option = option;
      this.slider = slider;
      this.spinner = spinner;
      this.armed = true;
    }

    @Override
    public void widgetSelected( final SelectionEvent e ) {
      armed = false;
      try {
        stateChangedCore(e);
      } finally {
        armed = true;
      }
    }

    @Override
    public void widgetDefaultSelected( final SelectionEvent e ) {
    }

    abstract void stateChangedCore( SelectionEvent e );
  }

  /**
   * Synchronizes the current value of a {@link Slider} and a {@link Spinner}
   * instance that display <code>double</code> values.
   */
  private static final class DoubleValueHandler extends AbstractValueHandler {
    final double step;

    DoubleValueHandler(
            final Option option,
            final Slider slider, final Spinner spinner,
            final double step
    ) {
      super(option, slider, spinner);
      this.step = step;
    }

    @Override
    void stateChangedCore( final SelectionEvent e ) {
      final Object src = e.getSource();
      if (src instanceof Slider) {
        final int sv = ((Slider) src).getSelection();
        final Double value = Double.valueOf(sv * step);
        option.setValue(value);
        spinner.setSelection(sv);
      } else if (src instanceof Spinner) {
        final int sv = ((Spinner) src).getSelection();
        final Double value = Double.valueOf(sv * step);
        option.setValue(value);
        slider.setSelection(sv);
      }
    }
  }

  /**
   * Synchronizes the current value of a {@link Slider} and a {@link Spinner}
   * instance that display <code>int</code> values.
   */
  private static final class IntValueHandler extends AbstractValueHandler {
    IntValueHandler(
            final Option option, final Slider slider, final Spinner spinner
    ) {
      super(option, slider, spinner);
    }

    @Override
    void stateChangedCore( final SelectionEvent e ) {
      final Object src = e.getSource();
      if (src instanceof Slider) {
        final int sv = ((Slider) src).getSelection();
        final Integer value = Integer.valueOf(sv);
        option.setValue(value);
        spinner.setSelection(value.intValue());
      } else if (src instanceof Spinner) {
        final int sv = ((Spinner) src).getSelection();
        final Integer value = Integer.valueOf(sv);
        option.setValue(value);
        slider.setSelection(sv);
      }
    }
  }


  /**
   * Handles enabled state changes on value changed events.
   */
  private static final class ConstraintManager {
    final List<Runnable> listeners;
    boolean enabled;

    ConstraintManager() {
      listeners = new ArrayList<Runnable>();
      enabled = true;
    }

    void addValueChangedListener( final Runnable l ) {
      listeners.add(l);
    }

    void valueChanged() {
      if (isEnabled()) {
        for (Runnable l : listeners) {
          l.run();
        }
      }
    }

    void setEnabled( final boolean enabled ) {
      this.enabled = enabled;
    }

    boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Sets the enabled state of the associated control depending on the
   * value of the associated supplier.
   */
  private static final class SetEnabled implements Runnable {
    final Supplier<Boolean> disabled;
    final Control control;

    SetEnabled( final Control control, final Supplier<Boolean> disabled ) {
      this.disabled = disabled;
      this.control = control;
    }

    @Override
    public void run() {
      setEnabled(!Boolean.TRUE.equals(disabled.get()));
    }

    void setEnabled( final boolean enabled ) {
      control.setEnabled(enabled);
    }
  }

  /**
   * Notifies the associated constraint manager whenever a new option value
   * is set.
   */
  private static final class NotifyValueChanged implements Consumer<Object> {
    final Consumer<Object> setter;
    final ConstraintManager cm;

    NotifyValueChanged(
            final Consumer<Object> setter,
            final ConstraintManager cm
    ) {
      this.setter = setter;
      this.cm = cm;
    }

    @Override
    public void accept( final Object o ) {
      setter.accept(o);
      cm.valueChanged();
    }
  }
}
