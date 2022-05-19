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
package com.yworks.yedlite.parts;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Scale;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.BevelNodeStyle;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;

/**
 * A view that depicts the properties of the selected element and enables the user to change them.
 */
public class DescriptionViewPart {
  @Inject
  IEclipseContext ctx;

  private GraphControl graphControl;
  private Composite parent;
  private IModelItem selectedItem;
  Composite empty;
  NodeProperties nodePropertiesComposite;
  EdgeProperties edgePropertiesComposite;
  StackLayout viewLayout;

  Label lblNoItemSelected;

  @PostConstruct
  public void createPartControl(Composite parent, @Translation Messages messages) {
    // create the label with a simple default text
    this.parent = parent;
    this.graphControl = ContextUtils.getGraphControl(ctx);
    viewLayout = new StackLayout();
    parent.setLayout(viewLayout);

    empty = new Composite(parent, SWT.NONE);
    empty.setLayout(new RowLayout(SWT.VERTICAL));

    lblNoItemSelected = new Label(empty, SWT.NONE);
    setText(lblNoItemSelected, messages.PropertiesPart_NoItemSelected);
    lblNoItemSelected.pack();

    nodePropertiesComposite = new NodeProperties(parent, SWT.NONE, messages);
    edgePropertiesComposite = new EdgeProperties(parent, SWT.NONE, messages);

    viewLayout.topControl = empty;

    doLayout(parent);

    ctx.runAndTrack(new RunAndTrack() {

      @Override
      public boolean changed(IEclipseContext context) {
        graphControl = ContextUtils.getGraphControl(ctx);
        return true;
      }
    });
  }

  @Inject
  public void translate(@Translation Messages messages) {
    if (lblNoItemSelected != null) {
      setText(lblNoItemSelected, messages.PropertiesPart_NoItemSelected);
    }
    if (nodePropertiesComposite != null) {
      nodePropertiesComposite.translate(messages);
    }
    if (edgePropertiesComposite != null) {
      edgePropertiesComposite.translate(messages);
    }
  }

  /**
   * Listen to selection events of the editor part.
   * @param item the selected item.
   */
  @Inject
  @Optional
  private void listenForSelectionEvents(@UIEventTopic(GraphEditorPart.ITEM_SELECTED_EVENT) IModelItem item) {
    if (parent.getDisplay().getThread() == Thread.currentThread()) {
      updateDescription(item);
    } else {
      parent.getDisplay().syncExec(new Runnable() {
        public void run() {
          updateDescription(item);
        }
      });
    }
  }

  /**
   * Listen to selection events of the editor part.
   * @param item the selected item.
   */
  @Inject
  @Optional
  private void listenForDeselectionEvents(@UIEventTopic(GraphEditorPart.ITEM_DESELECTED_EVENT) IModelItem item) {
    if (selectedItem == item) {
      viewLayout.topControl = empty;
      doLayout(parent);
    }
  }

  /**
   * Updates the description of the label of the specified model item.
   */
  private void updateDescription(IModelItem item) {
    this.selectedItem = item;
    if (item instanceof INode) {
      viewLayout.topControl = nodePropertiesComposite.getContainer();
      doLayout(parent);
      nodePropertiesComposite.setNode((INode) item);
    } else if (item instanceof IEdge) {
      viewLayout.topControl = edgePropertiesComposite.getContainer();
      doLayout(parent);
      edgePropertiesComposite.setEdge((IEdge) item);
    }
  }

  private static RGB colorToRGB(Color paint) {
    return new RGB((int)(paint.getRed()*255), (int)(paint.getGreen()*255), (int)(paint.getBlue()*255));
  }

  private static Font newFont( final Font font, final int size, final int style ) {
    final FontData[] fd = font.getFontData();
    if (fd.length < 1) {
      return font;
    } else {
      final FontData oldFd = fd[0];
      final FontData newFd = new FontData(oldFd.getName(), oldFd.getHeight() + size, oldFd.getStyle() | style);
      return new Font(font.getDevice(), newFd);
    }
  }


  static void doLayout(Composite composite) {
    composite.layout();
  }

  static void setText(Group group, String text) {
    group.setText(text);
    group.requestLayout();
  }

  static void setText(Label label, String text) {
    label.setText(text);
    label.requestLayout();
  }



  /**
   * The view that contains the properties for pens.
   */
  class PenProperties {
    private Group container;
    private Composite colorFieldComposite;

    private Scale penThicknessScale;
    private ColorFieldEditor penColorFieldEditor;
    private Combo dashCombo;

    private DashStyle[] dashStyles = {DashStyle.getDash(), DashStyle.getDashDot(), DashStyle.getDashDotDot(), DashStyle.getDot(), DashStyle.getSolid()};
    private Pen currentPen = new Pen();

    private Label penThicknessLabel;

    private Label penDashStyleLabel;
    private Label colorFieldLabel;

    public PenProperties(Composite parent, int style, @Translation Messages messages) {
      container = new Group(parent, style);
      container.setLayout(new GridLayout(3, false));
      container.setLayoutData(newGridData(true, 3));

      setText(container, messages.PropertiesPart_Pen);

      colorFieldLabel = new Label(this.container, SWT.NONE);
      setText(colorFieldLabel, messages.PropertiesPart_PenColor);
      colorFieldLabel.setLayoutData(newGridData(true, 2));

      colorFieldComposite = new Composite(container, SWT.NONE);
      colorFieldComposite.setLayoutData(newGridData(false, 1));
      colorFieldComposite.setLayout(new GridLayout(2, false));

      penColorFieldEditor = new ColorFieldEditor("name", "", colorFieldComposite);
      penColorFieldEditor.getColorSelector().addListener(new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
          RGB newColor = (RGB) event.getNewValue();
          Paint newPaint = Color.rgb(newColor.red, newColor.green, newColor.blue);
          Pen newPen = currentPen.cloneCurrentValue();
          newPen.setPaint(newPaint);
          currentPen = newPen;
          onPenChanged(currentPen);
        }
      });

      penThicknessLabel = new Label(container, SWT.NONE);
      setText(penThicknessLabel, messages.PropertiesPart_PenThickness);
      penThicknessLabel.setLayoutData(newGridData(false, 1));

      penThicknessScale = new Scale(container, SWT.NONE);
      penThicknessScale.setPageIncrement(1);
      penThicknessScale.setMinimum(1);
      penThicknessScale.setMaximum(5);
      penThicknessScale.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Pen newPen = currentPen.cloneCurrentValue();
          newPen.setThickness(penThicknessScale.getSelection());
          currentPen = newPen;
          onPenChanged(currentPen);
        }
      });
      penThicknessScale.setLayoutData(newGridData(true, 2));

      penDashStyleLabel = new Label(container, SWT.NONE);
      setText(penDashStyleLabel, messages.PropertiesPart_PenDashStyle);
      penDashStyleLabel.setLayoutData(newGridData(false, 1));

      dashCombo = new Combo(container, SWT.READ_ONLY);
      dashCombo.setItems("Dash", "DashDot", "DashDotDot", "Dot", "Solid");
      dashCombo.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Pen newPen = currentPen.cloneCurrentValue();
          newPen.setDashStyle(dashStyles[dashCombo.getSelectionIndex()]);
          currentPen = newPen;
          onPenChanged(currentPen);
        }
      });
      dashCombo.setLayoutData(newGridData(true, 2));
    }

    private List<IEventHandler<ItemEventArgs<Pen>>> listeners = new ArrayList<>();

    public void addPenChangedListener(IEventHandler<ItemEventArgs<Pen>> listener) {
      listeners.add(listener);
    }

    private void onPenChanged(Pen newPen){
      ItemEventArgs<Pen> itemEventArgs = new ItemEventArgs<Pen>(newPen);
      for (IEventHandler<ItemEventArgs<Pen>> listener : listeners) {
        listener.onEvent(this, itemEventArgs);
      }
    }

    public Group getContainer() {
      return container;
    }

    public Pen getCurrentPen() {
      return currentPen;
    }

    public void setCurrentPen(Pen currentPen) {
      if (this.currentPen != currentPen) {
        if (this.currentPen == null) {
          recursiveSetEnabled(getContainer(), true);
        }
        this.currentPen = currentPen;
        if (currentPen == null) {
          recursiveSetEnabled(getContainer(), false);
          return;
        }
        updateFields();
      };
    }

    public void updateFields() {
      penColorFieldEditor.getColorSelector().setColorValue(colorToRGB((Color)currentPen.getPaint()));
      penThicknessScale.setSelection((int)currentPen.getThickness());
      DashStyle dashStyle = currentPen.getDashStyle();
      for (int i = 0; i<dashStyles.length; i++) {
        if (dashStyle == dashStyles[i]) {
          dashCombo.select(i);
          break;
        }
      }
    }

    void translate(@Translation Messages messages) {
      setText(container, messages.PropertiesPart_Pen);
      setText(colorFieldLabel, messages.PropertiesPart_PenColor);
      setText(penThicknessLabel, messages.PropertiesPart_PenThickness);
      setText(penDashStyleLabel, messages.PropertiesPart_PenDashStyle);
    }
  }

  private static Label newLabel(Composite parent, String text, boolean headline) {
    final Label label = new Label(parent, SWT.LEFT);
    if (headline) {
      label.setFont(newFont(label.getFont(), 2, SWT.BOLD));
    }
    label.setText(text);
    return label;
  }

  private static GridData newGridData(boolean hFill, int hSpan ) {
    final GridData gbc = new GridData();
    gbc.verticalAlignment = SWT.CENTER;
    gbc.horizontalAlignment = hFill ? SWT.FILL : SWT.BEGINNING;
    gbc.grabExcessHorizontalSpace = hFill;
    gbc.grabExcessVerticalSpace = false;
    gbc.horizontalSpan = hSpan;
    gbc.verticalSpan = 1;
    return gbc;
  }

  /**
   * The view that contains the properties for edges.
   */
  class EdgeProperties {
    private Composite container;
    private Text text;
    private Label heading;
    private Scale smoothingScale;
    private Combo sourceArrowTypeCombo;
    private Combo targetArrowTypeCombo;
    private Scale sourceArrowScalingScale;
    private Scale targetArrowScalingScale;
    private Scale sourceArrowCropLengthScale;
    private Scale targetArrowCropLengthScale;

    private IEdge edge;
    private PolylineEdgeStyle edgeStyle;

    private ArrowType[] arrowTypes = {ArrowType.DEFAULT, ArrowType.SHORT, ArrowType.SIMPLE, ArrowType.CIRCLE, ArrowType.CROSS, ArrowType.DIAMOND, ArrowType.NONE};
    private PenProperties penProperties;
    private PenProperties targetArrowPenProperties;
    private PenProperties sourceArrowPenProperties;
    private Group sourceArrowGeometryPropertiesContainer;
    private Group targetArrowGeometryPropertiesContainer;
    private ScrolledComposite scrollContainer;

    Label lblLabel;
    Label smoothingText;
    private Group sourceArrowContainer;
    private Group targetArrowContainer;
    private Label sourceArrowTypeLabel;
    private Label sourceArrowCropLengthLabel;
    private Label targetArrowTypeLabel;
    private Label targetArrowScalingLabel;
    private Label targetArrowCropLengthLabel;
    private Label sourceArrowScalingLabel;

    public EdgeProperties(Composite parent, int style, @Translation Messages messages) {
      scrollContainer = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
      scrollContainer.setExpandHorizontal(true);
      scrollContainer.setExpandVertical(true);
      scrollContainer.addListener( SWT.Resize, event -> {
        int width = scrollContainer.getClientArea().width;
        scrollContainer.setMinSize( container.computeSize( width, SWT.DEFAULT ) );
      } );

      container = new Composite(scrollContainer, style);
      container.setLayout(new GridLayout(1, false));
      container.setLayoutData(newGridData(true, 1));
      scrollContainer.setContent(container);

      heading = newLabel(container, messages.PropertiesPart_EdgeProperties, true);
      Label sep = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
      sep.setLayoutData(newGridData(true, 1));

      Composite content = new Composite(container, SWT.NONE);
      content.setLayout(new GridLayout(3, false));
      content.setLayoutData(newGridData(true, 1));

      lblLabel = new Label(content, SWT.NONE);
      setText(lblLabel, messages.PropertiesPart_LabelText);
      lblLabel.setLayoutData(newGridData(false, 1));

      text = new Text(content, SWT.BORDER);
      text.addFocusListener(new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
          if (edge.getLabels().size() > 0) {
            ILabel label = edge.getLabels().getItem(0);
            graphControl.getGraph().setLabelText(label, text.getText());
          } else {
            graphControl.getGraph().addLabel(edge, text.getText());
          }
        }
        @Override
        public void focusGained(FocusEvent e) {}
      });
      text.setLayoutData(newGridData(true, 2));

      smoothingText = new Label(content, SWT.NONE);
      setText(smoothingText, messages.PropertiesPart_Smoothing);
      smoothingText.setLayoutData(newGridData(false, 1));

      smoothingScale = new Scale(content, SWT.NONE);
      smoothingScale.setPageIncrement(5);
      smoothingScale.setMaximum(20);
      smoothingScale.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          ((PolylineEdgeStyle)edge.getStyle()).setSmoothingLength(smoothingScale.getSelection());
          graphControl.invalidate();
        }
      });
      smoothingScale.setLayoutData(newGridData(true, 2));

      penProperties = new PenProperties(content, style, messages);
      penProperties.addPenChangedListener(new IEventHandler<ItemEventArgs<Pen>>() {
        @Override
        public void onEvent(Object source, ItemEventArgs<Pen> args) {
          edgeStyle.setPen(args.getItem());
          graphControl.invalidate();
        }
      });

      sourceArrowContainer = new Group(content, style);
      setText(sourceArrowContainer, messages.PropertiesPart_SourceArrow);
      sourceArrowContainer.setLayout(new GridLayout(3, false));
      sourceArrowContainer.setLayoutData(newGridData(true, 3));

      sourceArrowTypeLabel = new Label(sourceArrowContainer, SWT.NONE);
      setText(sourceArrowTypeLabel, messages.PropertiesPart_ArrowType);
      sourceArrowTypeLabel.setLayoutData(newGridData(false, 1));

      sourceArrowTypeCombo = new Combo(sourceArrowContainer, SWT.READ_ONLY);
      for (ArrowType type : arrowTypes) {
        sourceArrowTypeCombo.add(type.toString());
      }
      sourceArrowTypeCombo.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          ArrowType arrowType = arrowTypes[sourceArrowTypeCombo.getSelectionIndex()];
          if (arrowType == ArrowType.NONE) {
            edgeStyle.setSourceArrow(IArrow.NONE);
            sourceArrowPenProperties.setCurrentPen(null);
            recursiveSetEnabled(sourceArrowGeometryPropertiesContainer, false);
          } else {
            edgeStyle.setSourceArrow(new Arrow(arrowType, edgeStyle.getPen(), edgeStyle.getPen().getPaint()));
            sourceArrowPenProperties.setCurrentPen(edgeStyle.getPen());
            recursiveSetEnabled(sourceArrowGeometryPropertiesContainer, true);
          }
          graphControl.invalidate();
        }
      });
      sourceArrowTypeCombo.setLayoutData(newGridData(true, 2));

      sourceArrowPenProperties = new PenProperties(sourceArrowContainer, style, messages);
      sourceArrowPenProperties.addPenChangedListener(new IEventHandler<ItemEventArgs<Pen>>() {
        @Override
        public void onEvent(Object source, ItemEventArgs<Pen> args) {
          Arrow newArrow = copyArrow(edgeStyle.getSourceArrow());
          newArrow.setPen(args.getItem());
          newArrow.setPaint(args.getItem().getPaint());
          edgeStyle.setSourceArrow(newArrow);
          graphControl.invalidate();
        }
      });

      sourceArrowGeometryPropertiesContainer = new Group(sourceArrowContainer, style);
      setText(sourceArrowGeometryPropertiesContainer, messages.PropertiesPart_ArrowGeometry);
      sourceArrowGeometryPropertiesContainer.setLayout(new GridLayout(3, false));
      sourceArrowGeometryPropertiesContainer.setLayoutData(newGridData(true, 3));

      sourceArrowCropLengthLabel = new Label(sourceArrowGeometryPropertiesContainer, SWT.NONE);
      setText(sourceArrowCropLengthLabel, messages.PropertiesPart_ArrowCropLength);
      sourceArrowCropLengthLabel.setLayoutData(newGridData(false, 1));

      sourceArrowCropLengthScale = new Scale(sourceArrowGeometryPropertiesContainer, SWT.NONE);
      sourceArrowCropLengthScale.setPageIncrement(5);
      sourceArrowCropLengthScale.setMaximum(20);
      sourceArrowCropLengthScale.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Arrow newArrow = copyArrow(edgeStyle.getSourceArrow());
          newArrow.setCropLength(sourceArrowCropLengthScale.getSelection());
          edgeStyle.setSourceArrow(newArrow);
          graphControl.invalidate();
        }
      });
      sourceArrowCropLengthScale.setLayoutData(newGridData(true, 2));

      sourceArrowScalingLabel = new Label(sourceArrowGeometryPropertiesContainer, SWT.NONE);
      setText(sourceArrowScalingLabel, messages.PropertiesPart_ArrowScaling);
      sourceArrowScalingLabel.setLayoutData(newGridData(false, 1));

      sourceArrowScalingScale = new Scale(sourceArrowGeometryPropertiesContainer, SWT.NONE);
      sourceArrowScalingScale.setPageIncrement(1);
      sourceArrowScalingScale.setMinimum(10);
      sourceArrowScalingScale.setMaximum(30);
      sourceArrowScalingScale.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Arrow newArrow = copyArrow(edgeStyle.getSourceArrow());
          newArrow.setScale(sourceArrowScalingScale.getSelection()/10d);
          edgeStyle.setSourceArrow(newArrow);
          graphControl.invalidate();
        }
      });
      sourceArrowScalingScale.setLayoutData(newGridData(true, 2));

      targetArrowContainer = new Group(content, style);
      setText(targetArrowContainer, messages.PropertiesPart_TargetArrow);
      targetArrowContainer.setLayout(new GridLayout(3, false));
      targetArrowContainer.setLayoutData(newGridData(true, 3));

      targetArrowTypeLabel = new Label(targetArrowContainer, SWT.NONE);
      setText(targetArrowTypeLabel, messages.PropertiesPart_ArrowType);
      targetArrowTypeLabel.setLayoutData(newGridData(false, 1));

      targetArrowTypeCombo = new Combo(targetArrowContainer, SWT.READ_ONLY);
      for (ArrowType type : arrowTypes) {
        targetArrowTypeCombo.add(type.toString());
      }

      targetArrowTypeCombo.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          ArrowType arrowType = arrowTypes[targetArrowTypeCombo.getSelectionIndex()];
          if (arrowType == ArrowType.NONE) {
            edgeStyle.setTargetArrow(IArrow.NONE);
            targetArrowPenProperties.setCurrentPen(null);
            recursiveSetEnabled(targetArrowGeometryPropertiesContainer, false);
          } else {
            Arrow newArrow = edgeStyle.getTargetArrow() != null
                ? copyArrow(edgeStyle.getTargetArrow())
                : new Arrow(ArrowType.DEFAULT, edgeStyle.getPen(), edgeStyle.getPen().getPaint());
            newArrow.setType(arrowTypes[targetArrowTypeCombo.getSelectionIndex()]);

            edgeStyle.setTargetArrow(newArrow);
            targetArrowPenProperties.setCurrentPen(newArrow.getPen());
            recursiveSetEnabled(targetArrowGeometryPropertiesContainer, true);
          }
          graphControl.invalidate();
        }
      });
      targetArrowTypeCombo.setLayoutData(newGridData(true, 2));

      targetArrowPenProperties = new PenProperties(targetArrowContainer, style, messages);
      targetArrowPenProperties.addPenChangedListener(new IEventHandler<ItemEventArgs<Pen>>() {
        @Override
        public void onEvent(Object source, ItemEventArgs<Pen> args) {
          Arrow newArrow = copyArrow(edgeStyle.getTargetArrow());
          newArrow.setPen(args.getItem());
          newArrow.setPaint(args.getItem().getPaint());
          edgeStyle.setTargetArrow(newArrow);
          graphControl.invalidate();
        }
      });

      targetArrowGeometryPropertiesContainer = new Group(targetArrowContainer, style);
      setText(targetArrowGeometryPropertiesContainer, messages.PropertiesPart_ArrowGeometry);
      targetArrowGeometryPropertiesContainer.setLayout(new GridLayout(3, false));
      targetArrowGeometryPropertiesContainer.setLayoutData(newGridData(true, 3));

      targetArrowCropLengthLabel = new Label(targetArrowGeometryPropertiesContainer, SWT.NONE);
      setText(targetArrowCropLengthLabel, messages.PropertiesPart_ArrowCropLength);
      targetArrowCropLengthLabel.setLayoutData(newGridData(false, 1));

      targetArrowCropLengthScale = new Scale(targetArrowGeometryPropertiesContainer, SWT.NONE);
      targetArrowCropLengthScale.setPageIncrement(5);
      targetArrowCropLengthScale.setMaximum(20);
      targetArrowCropLengthScale.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Arrow newArrow = copyArrow(edgeStyle.getTargetArrow());
          newArrow.setCropLength(targetArrowCropLengthScale.getSelection());
          edgeStyle.setTargetArrow(newArrow);
          graphControl.invalidate();
        }
      });
      targetArrowCropLengthScale.setLayoutData(newGridData(true, 2));

      targetArrowScalingLabel = new Label(targetArrowGeometryPropertiesContainer, SWT.NONE);
      setText(targetArrowScalingLabel, messages.PropertiesPart_ArrowScaling);
      targetArrowScalingLabel.setLayoutData(newGridData(false, 1));

      targetArrowScalingScale = new Scale(targetArrowGeometryPropertiesContainer, SWT.NONE);
      targetArrowScalingScale.setPageIncrement(1);
      targetArrowScalingScale.setMinimum(10);
      targetArrowScalingScale.setMaximum(30);
      targetArrowScalingScale.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Arrow newArrow = copyArrow(edgeStyle.getTargetArrow());
          newArrow.setScale(targetArrowScalingScale.getSelection()/10d);
          edgeStyle.setTargetArrow(newArrow);
          graphControl.invalidate();
        }
      });
      targetArrowScalingScale.setLayoutData(newGridData(true, 2));
    }

    private Arrow copyArrow(IArrow oldArrow) {
      if (oldArrow instanceof Arrow) {
        return ((Arrow) oldArrow).clone();
      }
      ArrowType arrowType = ArrowType.NONE;
      if (oldArrow == IArrow.CIRCLE) {
        arrowType = ArrowType.CIRCLE;
      } else if (oldArrow == IArrow.CROSS) {
        arrowType = ArrowType.CROSS;
      } else if (oldArrow == IArrow.DEFAULT) {
        arrowType = ArrowType.DEFAULT;
      } else if (oldArrow == IArrow.DIAMOND) {
        arrowType = ArrowType.DIAMOND;
      } else if (oldArrow == IArrow.SHORT) {
        arrowType = ArrowType.SHORT;
      } else if (oldArrow == IArrow.SIMPLE) {
        arrowType = ArrowType.SIMPLE;
      } else if (oldArrow == IArrow.TRIANGLE) {
        arrowType = ArrowType.TRIANGLE;
      }
      return new Arrow(arrowType, edgeStyle.getPen(), edgeStyle.getPen().getPaint(), oldArrow.getCropLength(), 1);
    }

    public Composite getContainer() {
      return scrollContainer;
    }

    public IEdge getEdge() {
      return edge;
    }

    public void setEdge(IEdge edge) {
      this.edge = edge;
      updateFields();
    }

    private void updateFields() {
      edgeStyle = (PolylineEdgeStyle)edge.getStyle();
      smoothingScale.setSelection((int)edgeStyle.getSmoothingLength());
      Pen pen = edgeStyle.getPen();
      penProperties.setCurrentPen(pen);
      trySetArrowProperties(edgeStyle.getSourceArrow(), sourceArrowPenProperties, sourceArrowTypeCombo, sourceArrowGeometryPropertiesContainer);
      trySetArrowProperties(edgeStyle.getTargetArrow(), targetArrowPenProperties, targetArrowTypeCombo, targetArrowGeometryPropertiesContainer);
      //Scale cropLengthScale, Scale scalingScale
      updateGeometry(edgeStyle.getSourceArrow(), sourceArrowCropLengthScale, sourceArrowScalingScale);
      updateGeometry(edgeStyle.getTargetArrow(), targetArrowCropLengthScale, targetArrowScalingScale);
    }

    private void updateGeometry(IArrow arrow, Scale cropLengthScale, Scale scalingScale) {
      cropLengthScale.setSelection((int) arrow.getCropLength());
      if (arrow instanceof Arrow) {
        scalingScale.setSelection((int)(((Arrow) arrow).getScale()*10));
      } else {
        scalingScale.setSelection(10);
      }
    }

    private void trySetArrowProperties(IArrow arrow, PenProperties properties, Combo combo, Group geometry) {
      ArrowType type = ArrowType.NONE;
      Pen pen = Pen.getBlack();
      if (arrow == IArrow.CIRCLE) {
        type = ArrowType.CIRCLE;
      } else if (arrow == IArrow.CROSS) {
        type = ArrowType.CROSS;
      } else if (arrow == IArrow.DEFAULT) {
        type = ArrowType.DEFAULT;
      } else if (arrow == IArrow.DIAMOND) {
        type = ArrowType.DIAMOND;
      } else if (arrow == IArrow.SHORT) {
        type = ArrowType.SHORT;
      } else if (arrow == IArrow.SIMPLE) {
        type = ArrowType.SIMPLE;
      } else if (arrow == IArrow.TRIANGLE) {
        type = ArrowType.TRIANGLE;
      } else if (arrow instanceof Arrow) {
        Arrow extension = (Arrow) arrow;
        type = extension.getType();
        pen = extension.getPen();
      }

      if (type != ArrowType.NONE) {
        properties.setCurrentPen(pen);
        recursiveSetEnabled(geometry, true);
        for (int i = 0; i<arrowTypes.length; i++) {
          if (type == arrowTypes[i]) {
            combo.select(i);
            break;
          }
        }
      } else {
        // arrow is NONE
        combo.select(arrowTypes.length-1);
        properties.setCurrentPen(null);
        recursiveSetEnabled(geometry, false);
      }
    }

    void translate(@Translation Messages messages) {
      heading.setText(messages.PropertiesPart_EdgeProperties);
      setText(lblLabel, messages.PropertiesPart_LabelText);
      setText(smoothingText, messages.PropertiesPart_Smoothing);
      setText(sourceArrowTypeLabel, messages.PropertiesPart_ArrowType);
      setText(targetArrowTypeLabel, messages.PropertiesPart_ArrowType);
      setText(sourceArrowContainer, messages.PropertiesPart_SourceArrow);
      setText(targetArrowContainer, messages.PropertiesPart_TargetArrow);
      setText(targetArrowCropLengthLabel, messages.PropertiesPart_ArrowCropLength);
      setText(sourceArrowCropLengthLabel, messages.PropertiesPart_ArrowCropLength);
      setText(sourceArrowScalingLabel, messages.PropertiesPart_ArrowScaling);
      setText(targetArrowScalingLabel, messages.PropertiesPart_ArrowScaling);

      penProperties.translate(messages);
      targetArrowPenProperties.translate(messages);
      sourceArrowPenProperties.translate(messages);
      setText(sourceArrowGeometryPropertiesContainer, messages.PropertiesPart_ArrowGeometry);
      setText(targetArrowGeometryPropertiesContainer, messages.PropertiesPart_ArrowGeometry);
    }
  }

  private static void recursiveSetEnabled(Control ctrl, boolean enabled) {
    if (ctrl instanceof Composite) {
      Composite comp = (Composite) ctrl;
      for (Control c : comp.getChildren())
        recursiveSetEnabled(c, enabled);
      comp.setEnabled(enabled);
    } else {
      ctrl.setEnabled(enabled);
    }
  }

  /**
   * The view that contains the properties for nodes.
   */
  class NodeProperties {
    private ScrolledComposite scrollContainer;
    private Label heading;
    private Text text;
    private Composite content;
    private Composite nodeStylePropertiesContainer;
    private StackLayout nodeStylePropertiesContainerLayout;

    private INode node;
    private ShinyPlateProperties shinyPlateProperties;
    private ShapeNodeProperties shapeNodeProperties;
    private PanelNodeProperties panelNodeProperties;
    private BevelNodeProperties bevelNodeProperties;
    private CollapsibleNodeStyleProperties collapsibleProperties;
    private Label lblLabel;

    public NodeProperties(Composite parent, int style, @Translation Messages messages) {
      scrollContainer = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
      scrollContainer.setExpandHorizontal(true);
      scrollContainer.setExpandVertical(true);
      scrollContainer.addListener( SWT.Resize, event -> {
        int width = scrollContainer.getClientArea().width;
        scrollContainer.setMinSize( content.computeSize( width, SWT.DEFAULT ) );
      });

      content = new Composite(scrollContainer, style);
      content.setLayout(new GridLayout(1, false));
      content.setLayoutData(newGridData(true, 1));
      scrollContainer.setContent(content);

      heading = newLabel(content, messages.PropertiesPart_NodeProperties, true);
      heading.setLayoutData(newGridData(true, 1));
      Label sep = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
      sep.setLayoutData(newGridData(true, 1));

      Composite container = new Composite(content, SWT.NONE);
      container.setLayout(new GridLayout(3, false));
      container.setLayoutData(newGridData(true, 1));

      lblLabel = new Label(container, SWT.NONE);
      setText(lblLabel, messages.PropertiesPart_LabelText);
      lblLabel.setLayoutData(newGridData(false, 1));

      text = new Text(container, SWT.BORDER);
      text.addFocusListener(new FocusListener() {

        @Override
        public void focusLost(FocusEvent e) {
          if (node.getLabels().size() > 0) {
            ILabel label = node.getLabels().getItem(0);
            graphControl.getGraph().setLabelText(label, text.getText());
          } else {
            graphControl.getGraph().addLabel(node, text.getText());
          }
        }
        @Override
        public void focusGained(FocusEvent e) {}
      });
      text.setLayoutData(newGridData(true, 2));

      nodeStylePropertiesContainer = new Composite(container, SWT.NONE);
      nodeStylePropertiesContainerLayout = new StackLayout();
      nodeStylePropertiesContainer.setLayout(nodeStylePropertiesContainerLayout);
      nodeStylePropertiesContainer.setLayoutData(newGridData(true, 3));

      shinyPlateProperties = new ShinyPlateProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
      shapeNodeProperties = new ShapeNodeProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
      panelNodeProperties = new PanelNodeProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
      bevelNodeProperties = new BevelNodeProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
      collapsibleProperties = new CollapsibleNodeStyleProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
    }

    public Composite getContainer() {
      return scrollContainer;
    }

    public INode getNode() {
      return node;
    }

    public void setNode(INode node) {
      this.node = node;
      if (node.getLabels().size() > 0) {
        text.setText(node.getLabels().getItem(0).getText());
      } else {
        text.setText("");
      }
      if (node.getStyle() instanceof ShinyPlateNodeStyle) {
        nodeStylePropertiesContainerLayout.topControl = shinyPlateProperties.getContainer();
        shinyPlateProperties.setNodeStyle((ShinyPlateNodeStyle)node.getStyle());
      } else if (node.getStyle() instanceof ShapeNodeStyle) {
        nodeStylePropertiesContainerLayout.topControl = shapeNodeProperties.getContainer();
        shapeNodeProperties.setNodeStyle((ShapeNodeStyle)node.getStyle());
      } else if (node.getStyle() instanceof PanelNodeStyle) {
        nodeStylePropertiesContainerLayout.topControl = panelNodeProperties.getContainer();
        panelNodeProperties.setNodeStyle((PanelNodeStyle)node.getStyle());
      } else if (node.getStyle() instanceof BevelNodeStyle) {
        nodeStylePropertiesContainerLayout.topControl = bevelNodeProperties.getContainer();
        bevelNodeProperties.setNodeStyle((BevelNodeStyle)node.getStyle());
      } else if (node.getStyle() instanceof CollapsibleNodeStyleDecorator) {
        nodeStylePropertiesContainerLayout.topControl = collapsibleProperties.getContainer();
        collapsibleProperties.setNodeStyle((CollapsibleNodeStyleDecorator)node.getStyle());
      }
      doLayout(nodeStylePropertiesContainer);
    }

    void translate(@Translation Messages messages) {
      heading.setText(messages.PropertiesPart_NodeProperties);
      setText(lblLabel, messages.PropertiesPart_LabelText);

      shinyPlateProperties.translate(messages);
      shapeNodeProperties.translate(messages);
      panelNodeProperties.translate(messages);
      bevelNodeProperties.translate(messages);
      collapsibleProperties.translate(messages);
    }
  }

  interface INodeStyleProperties<T extends INodeStyle> {
    public Group getContainer();
    public T getNodeStyle();
    public void setNodeStyle(T nodeStyle);
  }

  class ShapeNodeProperties implements INodeStyleProperties<ShapeNodeStyle> {

    private ColorFieldEditor colorFieldEditor;
    private Combo shapeCombo;

    private Group container;

    private ShapeNodeStyle nodeStyle;
    private ShapeNodeShape[] availableShapes;
    private Label shapeLabel;
    private Composite colorFieldComposite;
    private Label colorFieldLabel;

    public ShapeNodeProperties(Composite parent, int style, @Translation Messages messages) {
      this.container = new Group(parent, style);
      this.container.setLayout(new GridLayout(3, false));
      setText(this.container, messages.PropertiesPart_ShapeNode);

      colorFieldLabel = new Label(this.container, SWT.NONE);
      setText(colorFieldLabel, messages.PropertiesPart_FillColor);
      colorFieldLabel.setLayoutData(newGridData(true, 2));

      colorFieldComposite = new Composite(container, SWT.NONE);
      colorFieldComposite.setLayoutData(newGridData(false, 1));
      colorFieldComposite.setLayout(new GridLayout(2, false));

      colorFieldEditor = new ColorFieldEditor("name", "", colorFieldComposite);
      colorFieldEditor.getColorSelector().addListener(new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
          RGB newColor = (RGB) event.getNewValue();
          Paint newPaint = Color.rgb(newColor.red, newColor.green, newColor.blue);
          nodeStyle.setPaint(newPaint);
          graphControl.invalidate();
        }
      });

      shapeLabel = new Label(this.container, SWT.NONE);
      setText(shapeLabel, messages.PropertiesPart_Shape);
      shapeLabel.setLayoutData(newGridData(false, 1));

      shapeCombo = new Combo(this.container, SWT.READ_ONLY);
      availableShapes = ShapeNodeShape.values();
      for (int i = 0; i<availableShapes.length; i++) {
        shapeCombo.add(availableShapes[i].toString());
      }
      shapeCombo.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          nodeStyle.setShape(ShapeNodeShape.valueOf(shapeCombo.getText()));
          graphControl.invalidate();
        }
      });
      shapeCombo.setLayoutData(newGridData(true, 2));
    }

    public Group getContainer() {
      return container;
    }

    public ShapeNodeStyle getNodeStyle() {
      return nodeStyle;
    }

    public void setNodeStyle(ShapeNodeStyle nodeStyle) {
      if (this.nodeStyle != nodeStyle) {
        this.nodeStyle = nodeStyle;
        updateFields();
      }
    }

    private void updateFields() {
      Color paint = (Color)nodeStyle.getPaint();
      colorFieldEditor.getColorSelector().setColorValue(colorToRGB(paint));
      shapeCombo.select(shapeToArrayIndex(nodeStyle.getShape()));
    }

    private int shapeToArrayIndex(ShapeNodeShape shape) {
      for (int i = 0; i<availableShapes.length; i++) {
        if (availableShapes[i].equals(shape)) {
          return i;
        }
      }
      return -1;
    }

    void translate(@Translation Messages messages) {
      setText(container, messages.PropertiesPart_ShapeNode);
      setText(colorFieldLabel, messages.PropertiesPart_FillColor);
      setText(shapeLabel, messages.PropertiesPart_Shape);
    }
  }

  class ShinyPlateProperties implements INodeStyleProperties<ShinyPlateNodeStyle> {

    private ColorFieldEditor colorFieldEditor;
    private Button dropShadowCheckButton;

    private Group container;

    private ShinyPlateNodeStyle nodeStyle;
    private Label dropShadowLabel;
    private Composite colorFieldComposite;
    private Label colorFieldLabel;

    public ShinyPlateProperties(Composite parent, int style, @Translation Messages messages) {
      this.container = new Group(parent, style);
      this.container.setLayout(new GridLayout(3, false));
      setText(this.container, messages.PropertiesPart_ShinyPlateNode);

      colorFieldLabel = new Label(this.container, SWT.NONE);
      setText(colorFieldLabel, messages.PropertiesPart_FillColor);
      colorFieldLabel.setLayoutData(newGridData(true, 2));

      colorFieldComposite = new Composite(container, SWT.NONE);
      colorFieldComposite.setLayoutData(newGridData(false, 1));
      colorFieldComposite.setLayout(new GridLayout(2, false));

      colorFieldEditor = new ColorFieldEditor("name", "", colorFieldComposite);
      colorFieldEditor.getColorSelector().addListener(new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
          RGB newColor = (RGB) event.getNewValue();
          Paint newPaint = Color.rgb(newColor.red, newColor.green, newColor.blue);
          nodeStyle.setPaint(newPaint);
          graphControl.invalidate();
        }
      });

      dropShadowLabel = new Label(this.container, SWT.NONE);
      setText(dropShadowLabel, messages.PropertiesPart_DropShadow);
      dropShadowLabel.setLayoutData(newGridData(true, 2));

      dropShadowCheckButton = new Button(this.container, SWT.CHECK);
      dropShadowCheckButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          nodeStyle.setShadowDrawingEnabled(dropShadowCheckButton.getSelection());
          graphControl.invalidate();
        }
      });
      GridData gbc = newGridData(false, 1);
      gbc.horizontalAlignment = SWT.END;
      dropShadowCheckButton.setLayoutData(gbc);
    }

    public Group getContainer() {
      return container;
    }

    public ShinyPlateNodeStyle getNodeStyle() {
      return nodeStyle;
    }


    public void setNodeStyle(ShinyPlateNodeStyle nodeStyle) {
      if (this.nodeStyle != nodeStyle) {
        this.nodeStyle = nodeStyle;
        updateFields();
      }
    }

    private void updateFields() {
      Color paint = (Color)nodeStyle.getPaint();
      colorFieldEditor.getColorSelector().setColorValue(colorToRGB(paint));
      dropShadowCheckButton.setSelection(nodeStyle.isShadowDrawingEnabled());
    }

    void translate(@Translation Messages messages) {
      setText(container, messages.PropertiesPart_ShinyPlateNode);
      setText(colorFieldLabel, messages.PropertiesPart_FillColor);
      setText(dropShadowLabel, messages.PropertiesPart_DropShadow);
    }
  }

  class PanelNodeProperties implements INodeStyleProperties<PanelNodeStyle> {

    private ColorFieldEditor colorFieldEditor;
    private ColorFieldEditor labelColorFieldEditor;

    private Group container;

    private PanelNodeStyle nodeStyle;
    private Composite colorFieldComposite;
    private Composite labelcolorFieldComposite;
    private Label colorFieldLabel;
    private Label labelColorFieldLabel;

    public PanelNodeProperties(Composite parent, int style, @Translation Messages messages) {
      this.container = new Group(parent, style);
      this.container.setLayout(new GridLayout(3, false));
      setText(this.container, messages.PropertiesPart_PanelNode);

      colorFieldLabel = new Label(this.container, SWT.NONE);
      setText(colorFieldLabel, messages.PropertiesPart_FillColor);
      colorFieldLabel.setLayoutData(newGridData(true, 2));

      colorFieldComposite = new Composite(container, SWT.NONE);
      colorFieldComposite.setLayoutData(newGridData(false, 1));
      colorFieldComposite.setLayout(new GridLayout(2, false));

      colorFieldEditor = new ColorFieldEditor("name", "", colorFieldComposite);
      colorFieldEditor.getColorSelector().addListener(new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
          RGB newColor = (RGB) event.getNewValue();
          Color newPaint = Color.rgb(newColor.red, newColor.green, newColor.blue);
          nodeStyle.setColor(newPaint);
          graphControl.invalidate();
        }
      });

      labelColorFieldLabel = new Label(this.container, SWT.NONE);
      setText(labelColorFieldLabel, messages.PropertiesPart_LabelInsetsColor);
      labelColorFieldLabel.setLayoutData(newGridData(true, 2));

      labelcolorFieldComposite = new Composite(container, SWT.NONE);
      labelcolorFieldComposite.setLayoutData(newGridData(false, 1));
      labelcolorFieldComposite.setLayout(new GridLayout(2, false));

      labelColorFieldEditor = new ColorFieldEditor("name", "", labelcolorFieldComposite);
      labelColorFieldEditor.getColorSelector().addListener(new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
          RGB newColor = (RGB) event.getNewValue();
          Color newPaint = Color.rgb(newColor.red, newColor.green, newColor.blue);
          nodeStyle.setLabelInsetsColor(newPaint);
          graphControl.invalidate();
        }
      });
    }

    public Group getContainer() {
      return container;
    }

    public PanelNodeStyle getNodeStyle() {
      return nodeStyle;
    }


    public void setNodeStyle(PanelNodeStyle nodeStyle) {
      if (this.nodeStyle != nodeStyle) {
        this.nodeStyle = nodeStyle;
        updateFields();
      }
    }

    private void updateFields() {
      colorFieldEditor.getColorSelector().setColorValue(colorToRGB(nodeStyle.getColor()));
      labelColorFieldEditor.getColorSelector().setColorValue(colorToRGB(nodeStyle.getLabelInsetsColor()));
    }

    void translate(@Translation Messages messages) {
      setText(container, messages.PropertiesPart_PanelNode);
      setText(colorFieldLabel, messages.PropertiesPart_FillColor);
      setText(labelColorFieldLabel, messages.PropertiesPart_LabelInsetsColor);
    }
  }

  class BevelNodeProperties implements INodeStyleProperties<BevelNodeStyle> {

    private ColorFieldEditor colorFieldEditor;
    private Button dropShadowCheckButton;
    private Spinner insetSpinner;
    private Spinner radiusSpinner;

    private Group container;

    private BevelNodeStyle nodeStyle;
    private Label dropShadowLabel;
    private Label insetLabel;
    private Label radiusLabel;;
    private Composite colorFieldComposite;
    private Label colorFieldLabel;

    public BevelNodeProperties(Composite parent, int style, @Translation Messages messages) {
      this.container = new Group(parent, style);
      this.container.setLayout(new GridLayout(3, false));
      setText(this.container, messages.PropertiesPart_BevelNode);

      colorFieldLabel = new Label(this.container, SWT.NONE);
      setText(colorFieldLabel, messages.PropertiesPart_FillColor);
      colorFieldLabel.setLayoutData(newGridData(true, 2));

      colorFieldComposite = new Composite(container, SWT.NONE);
      colorFieldComposite.setLayoutData(newGridData(false, 1));
      colorFieldComposite.setLayout(new GridLayout(2, false));

      colorFieldEditor = new ColorFieldEditor("name", "", colorFieldComposite);
      colorFieldEditor.getColorSelector().addListener(new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
          RGB newColor = (RGB) event.getNewValue();
          Color newPaint = Color.rgb(newColor.red, newColor.green, newColor.blue);
          nodeStyle.setColor(newPaint);
          graphControl.invalidate();
        }
      });

      dropShadowLabel = new Label(this.container, SWT.NONE);
      setText(dropShadowLabel, messages.PropertiesPart_DropShadow);
      dropShadowLabel.setLayoutData(newGridData(true, 2));

      dropShadowCheckButton = new Button(this.container, SWT.CHECK);
      dropShadowCheckButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          nodeStyle.setShadowDrawingEnabled(dropShadowCheckButton.getSelection());
          graphControl.invalidate();
        }
      });
      GridData gbc = newGridData(false, 1);
      gbc.horizontalAlignment = SWT.END;
      dropShadowCheckButton.setLayoutData(gbc);

      insetLabel = new Label(this.container, SWT.NONE);
      setText(insetLabel, messages.PropertiesPart_Inset);
      insetLabel.setLayoutData(newGridData(false, 1));

      insetSpinner = new Spinner(this.container, SWT.BORDER);
      insetSpinner.setDigits(1);
      insetSpinner.setMinimum(0);
      insetSpinner.setMaximum(200);
      insetSpinner.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          int selection = insetSpinner.getSelection();
          nodeStyle.setInset((selection / 10d));
          graphControl.invalidate();
        }
      });
      insetSpinner.setLayoutData(newGridData(true, 2));

      radiusLabel = new Label(this.container, SWT.NONE);
      setText(radiusLabel, messages.PropertiesPart_Radius);
      radiusLabel.setLayoutData(newGridData(false, 1));

      radiusSpinner = new Spinner(this.container, SWT.BORDER);
      radiusSpinner.setDigits(1);
      radiusSpinner.setMinimum(0);
      radiusSpinner.setMaximum(200);
      radiusSpinner.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          int selection = radiusSpinner.getSelection();
          nodeStyle.setRadius((selection / 10d));
          graphControl.invalidate();
        }
      });
      radiusSpinner.setLayoutData(newGridData(true, 2));
    }

    public Group getContainer() {
      return container;
    }

    public BevelNodeStyle getNodeStyle() {
      return nodeStyle;
    }

    public void setNodeStyle(BevelNodeStyle nodeStyle) {
      if (this.nodeStyle != nodeStyle) {
        this.nodeStyle = nodeStyle;
        updateFields();
      }
    }

    private void updateFields() {
      colorFieldEditor.getColorSelector().setColorValue(colorToRGB(nodeStyle.getColor()));
      insetSpinner.setSelection((int)(nodeStyle.getInset()*10));
      radiusSpinner.setSelection((int)(nodeStyle.getRadius()*10d));
      dropShadowCheckButton.setSelection(nodeStyle.isShadowDrawingEnabled());
    }

    void translate(@Translation Messages messages) {
      setText(container, messages.PropertiesPart_BevelNode);
      setText(colorFieldLabel, messages.PropertiesPart_FillColor);
      setText(dropShadowLabel, messages.PropertiesPart_DropShadow);
      setText(insetLabel, messages.PropertiesPart_Inset);
      setText(radiusLabel, messages.PropertiesPart_Inset);
    }
  }

  class CollapsibleNodeStyleProperties implements INodeStyleProperties<CollapsibleNodeStyleDecorator> {

    private Group container;
    private Composite nodeStylePropertiesContainer;
    private StackLayout nodeStylePropertiesContainerLayout;
    private Spinner insetSpinner;

    private CollapsibleNodeStyleDecorator nodeStyle;

    private ShinyPlateProperties shinyPlateProperties;
    private ShapeNodeProperties shapeNodeProperties;
    private PanelNodeProperties panelNodeProperties;
    private BevelNodeProperties bevelNodeProperties;

    ILabelModelParameter[] availableInteriorLabelModelParameters = new ILabelModelParameter[] {
        InteriorLabelModel.NORTH_WEST,
        InteriorLabelModel.NORTH,
        InteriorLabelModel.NORTH_EAST,
        InteriorLabelModel.WEST,
        InteriorLabelModel.CENTER,
        InteriorLabelModel.EAST,
        InteriorLabelModel.SOUTH_WEST,
        InteriorLabelModel.SOUTH,
        InteriorLabelModel.SOUTH_EAST
    };

    ILabelModelParameter[] availableExteriorLabelModelParameters = new ILabelModelParameter[] {
        ExteriorLabelModel.NORTH_WEST,
        ExteriorLabelModel.NORTH,
        ExteriorLabelModel.NORTH_EAST,
        ExteriorLabelModel.WEST,
        ExteriorLabelModel.EAST,
        ExteriorLabelModel.SOUTH_WEST,
        ExteriorLabelModel.SOUTH,
        ExteriorLabelModel.SOUTH_EAST
    };
    private Combo buttonLocationModelCombo;
    private Combo buttonLocationParameterCombo;
    private Label insetsLabel;
    private Label buttonLocationModelLabel;
    private Label buttonLocationParameterLabel;

    public CollapsibleNodeStyleProperties(Composite parent, int style, @Translation Messages messages) {
      container = new Group(parent, style);
      container.setLayout(new GridLayout(3, false));
      setText(container, messages.PropertiesPart_CollapsibleNode);

      insetsLabel = new Label(this.container, SWT.NONE);
      setText(insetsLabel, messages.PropertiesPart_Inset);
      insetsLabel.setLayoutData(newGridData(false, 1));

      insetSpinner = new Spinner(this.container, SWT.BORDER);
      insetSpinner.setDigits(1);
      insetSpinner.setMinimum(0);
      insetSpinner.setMaximum(200);
      insetSpinner.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          int selection = insetSpinner.getSelection();
          nodeStyle.setInsets(new InsetsD((selection / 10d)));
          graphControl.invalidate();
        }
      });
      insetSpinner.setLayoutData(newGridData(true, 2));

      buttonLocationModelLabel = new Label(this.container, SWT.NONE);
      setText(buttonLocationModelLabel, messages.PropertiesPart_ButtonLocationModel);
      buttonLocationModelLabel.setLayoutData(newGridData(false, 1));

      buttonLocationModelCombo = new Combo(this.container, SWT.READ_ONLY);
      buttonLocationModelCombo.setItems("Interior", "Exterior");
      buttonLocationModelCombo.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          int selectionIndex = buttonLocationModelCombo.getSelectionIndex();
          // adjust the content of the other combobox according to the selection
          fillModelCombo(selectionIndex);
        }
      });
      buttonLocationModelCombo.setLayoutData(newGridData(true, 2));

      buttonLocationParameterLabel = new Label(this.container, SWT.NONE);
      setText(buttonLocationParameterLabel, messages.PropertiesPart_ButtonLocationParameter);
      buttonLocationParameterLabel.setLayoutData(newGridData(false, 1));

      buttonLocationParameterCombo = new Combo(this.container, SWT.READ_ONLY);
      buttonLocationParameterCombo.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          int modelSelectionIndex = buttonLocationModelCombo.getSelectionIndex();
          int parameterSelectionIndex = buttonLocationParameterCombo.getSelectionIndex();
          if (modelSelectionIndex == 0) {
            // 0 means interior
            nodeStyle.setButtonPlacement(availableInteriorLabelModelParameters[parameterSelectionIndex]);
          } else if (modelSelectionIndex == 1){
            // 1 means exterior
            nodeStyle.setButtonPlacement(availableExteriorLabelModelParameters[parameterSelectionIndex]);
          }
          graphControl.invalidate();
        }
      });
      buttonLocationParameterCombo.setLayoutData(newGridData(true, 2));

      nodeStylePropertiesContainer = new Composite(container, SWT.NONE);
      nodeStylePropertiesContainerLayout = new StackLayout();
      nodeStylePropertiesContainer.setLayout(nodeStylePropertiesContainerLayout);
      nodeStylePropertiesContainer.setLayoutData(newGridData(true, 3));

      shinyPlateProperties = new ShinyPlateProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
      shapeNodeProperties = new ShapeNodeProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
      panelNodeProperties = new PanelNodeProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
      bevelNodeProperties = new BevelNodeProperties(nodeStylePropertiesContainer, SWT.NONE, messages);
    }

    private void fillModelCombo(int selectionIndex) {
      if (selectionIndex == 0) {
        // 0 means interior
        buttonLocationParameterCombo.setItems(new String[0]);
        for (int i = 0; i<availableInteriorLabelModelParameters.length; i++) {
          buttonLocationParameterCombo.add(availableInteriorLabelModelParameters[i].toString());
        }
      } else if (selectionIndex == 1){
        // 1 means exterior
        buttonLocationParameterCombo.setItems(new String[0]);
        for (int i = 0; i<availableExteriorLabelModelParameters.length; i++) {
          buttonLocationParameterCombo.add(availableExteriorLabelModelParameters[i].toString());
        }
      }
    }

    private void selectParameter(ILabelModelParameter parameter) {
      ILabelModelParameter[] parameters = null;
      if (parameter.getModel() instanceof InteriorLabelModel) {
        parameters = availableInteriorLabelModelParameters;
      } else if (parameter.getModel() instanceof ExteriorLabelModel) {
        parameters = availableExteriorLabelModelParameters;
      }
      if (parameters != null) {
        for (int i = 0; i<parameters.length; i++) {
          if (parameters[i].equals(parameter)) {
            buttonLocationParameterCombo.select(i);
            return;
          }
        }
      }

    }

    @Override
    public Group getContainer() {
      return container;
    }

    @Override
    public CollapsibleNodeStyleDecorator getNodeStyle() {
      return this.nodeStyle;
    }

    @Override
    public void setNodeStyle(CollapsibleNodeStyleDecorator nodeStyle) {
      if (this.nodeStyle != nodeStyle) {
        this.nodeStyle = nodeStyle;
        updateFields(nodeStyle);
      }
    }

    private void updateFields(CollapsibleNodeStyleDecorator nodeStyle) {
      insetSpinner.setSelection((int)(nodeStyle.getInsets().getLeft()*10));
      INodeStyle wrapped = nodeStyle.getWrapped();
      if (wrapped instanceof ShinyPlateNodeStyle) {
        nodeStylePropertiesContainerLayout.topControl = shinyPlateProperties.getContainer();
        shinyPlateProperties.setNodeStyle((ShinyPlateNodeStyle)wrapped);
      } else if (wrapped instanceof ShapeNodeStyle) {
        nodeStylePropertiesContainerLayout.topControl = shapeNodeProperties.getContainer();
        shapeNodeProperties.setNodeStyle((ShapeNodeStyle)wrapped);
      } else if (wrapped instanceof PanelNodeStyle) {
        nodeStylePropertiesContainerLayout.topControl = panelNodeProperties.getContainer();
        panelNodeProperties.setNodeStyle((PanelNodeStyle)wrapped);
      } else if (wrapped instanceof BevelNodeStyle) {
        nodeStylePropertiesContainerLayout.topControl = bevelNodeProperties.getContainer();
        bevelNodeProperties.setNodeStyle((BevelNodeStyle)wrapped);
      }
      ILabelModelParameter buttonLocationParameter = nodeStyle.getButtonPlacement();
      if (buttonLocationParameter.getModel() instanceof InteriorLabelModel && buttonLocationModelCombo.getSelectionIndex() != 0) {
        buttonLocationModelCombo.select(0);
        fillModelCombo(0);
      } else if (buttonLocationParameter.getModel() instanceof ExteriorLabelModel && buttonLocationModelCombo.getSelectionIndex() != 1) {
        buttonLocationModelCombo.select(1);
        fillModelCombo(1);
      }
      selectParameter(buttonLocationParameter);
      doLayout(nodeStylePropertiesContainer);
    }

    void translate(@Translation Messages messages) {
      setText(container, messages.PropertiesPart_CollapsibleNode);
      setText(insetsLabel, messages.PropertiesPart_Inset);
      setText(buttonLocationModelLabel, messages.PropertiesPart_ButtonLocationModel);
      setText(buttonLocationParameterLabel, messages.PropertiesPart_ButtonLocationParameter);

      bevelNodeProperties.translate(messages);
      panelNodeProperties.translate(messages);
      shapeNodeProperties.translate(messages);
      shinyPlateProperties.translate(messages);
    }
  }
}
