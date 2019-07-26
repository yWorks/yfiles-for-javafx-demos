/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yedlite.ContextUtils;
import com.yworks.yedlite.layout.ConfigurationFactory;
import com.yworks.yedlite.layout.HierarchicLayoutConfig;
import com.yworks.yedlite.layout.LayoutConfiguration;
import com.yworks.yedlite.layout.OrganicLayoutConfig;
import com.yworks.yedlite.layout.OrthogonalLayoutConfig;
import com.yworks.yedlite.layout.PolylineEdgeRouterConfig;
import com.yworks.yedlite.toolkit.optionhandler.L10n;
import com.yworks.yedlite.toolkit.optionhandler.OptionEditor;
import com.yworks.yfiles.view.GraphControl;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;

/**
 * A part that shows options for a layout algorithm and allows for applying it to the graph.
 */
public class LayoutPart {

  private static final String HIERARCHIC = "Hierarchic";
  private static final String ORGANIC = "Organic";
  private static final String ORTHOGONAL = "Orthogonal";
  private static final String EDGE_ROUTER = "EdgeRouter";

  @Inject
  private IEclipseContext ctx;

  @Inject
  private MPart part;

  private LayoutConfiguration config;

  private Button applyButton;

  private ScrolledComposite scp;

  @PostConstruct
  public void createComposite(Composite parent, @Translation Messages messages) {

    GraphControl gc = ContextUtils.getGraphControl(ctx);
    createConfiguration();

    final Composite contentPane = new Composite(parent, SWT.NONE);
    contentPane.setLayout(new GridLayout(1, false));

    GridData gbc;
    gbc = new GridData(GridData.FILL_BOTH);
    gbc.grabExcessHorizontalSpace = true;
    gbc.grabExcessVerticalSpace = true;
    scp = new ScrolledComposite(contentPane, SWT.V_SCROLL | SWT.H_SCROLL);
    scp.setLayoutData(gbc);

    buildEditor(messages);

    gbc = new GridData(GridData.FILL_HORIZONTAL);
    gbc.grabExcessHorizontalSpace = true;
    gbc.grabExcessVerticalSpace = false;
    final Label sep = new Label(contentPane, SWT.SEPARATOR | SWT.HORIZONTAL);
    sep.setLayoutData(gbc);

    applyButton = new Button(contentPane, SWT.NONE);
    applyButton.setText(messages.LayoutPart_Apply);
    applyButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(final MouseEvent e) {
        if (config != null) {
          config.apply(gc, ( source, args ) -> {});
        }
      }
    });
  }

  /**
   * Takes the annotated {@link LayoutConfiguration} and builds a property panel for the configuration options.
   */
  private void buildEditor( final Messages messages ) {
    final OptionEditor optionEditor = new OptionEditor();
    optionEditor.setConfiguration(this.config);
    optionEditor.setLocalization(new Adapter(messages));
    final Control editor = optionEditor.buildEditor(scp);
    editor.setSize(editor.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    scp.setContent(editor);
    scp.addListener(SWT.Resize, new ResizeHandler(editor));
  }

  private void createConfiguration() {
    final List<String> tags = part.getTags();
    if (tags != null && !tags.isEmpty()) {
      for (String tag : tags) {
        if (HIERARCHIC.equals(tag)) {
          config = ConfigurationFactory.get(ctx.getParent().getParent(), HierarchicLayoutConfig.class);
        } else if (ORGANIC.equals(tag)) {
          config = ConfigurationFactory.get(ctx.getParent().getParent(), OrganicLayoutConfig.class);
        } else if (ORTHOGONAL.equals(tag)) {
          config = ConfigurationFactory.get(ctx.getParent().getParent(), OrthogonalLayoutConfig.class);
        } else if (EDGE_ROUTER.equals(tag)) {
          config = ConfigurationFactory.get(ctx.getParent().getParent(), PolylineEdgeRouterConfig.class);
        }
      }
    }
  }

  @Inject
  public void translate(@Translation Messages messages) {
    if (applyButton != null && !applyButton.isDisposed()) {
      applyButton.setText(messages.LayoutPart_Apply);
    }

    if (scp != null && !scp.isDisposed()) {
      final Control[] children = scp.getChildren();
      if (children != null) {
        for (int i = 0; i < children.length; ++i) {
          disposeImpl(children[i]);
        }
      }
      final Listener[] l = scp.getListeners(SWT.Resize);
      if (l != null) {
        for (int i = 0; i < l.length; ++i) {
          if (l[i] instanceof ResizeHandler) {
            scp.removeListener(SWT.Resize, l[i]);
          }
        }
      }
      buildEditor(messages);
    }
  }

  private void disposeImpl( final Control control ) {
    if (control instanceof Composite) {
      final Control[] children = ((Composite) control).getChildren();
      if (children != null) {
        for (int i = 0; i < children.length; ++i) {
          disposeImpl(children[i]);
        }
      }
    }
    control.dispose();
  }

  private static class ResizeHandler implements Listener {
    final Control editor;

    ResizeHandler( final Control editor ) {
      this.editor = editor;
    }

    @Override
    public void handleEvent( final Event e ) {
      final ScrolledComposite src = (ScrolledComposite) e.widget;
      final int width = src.getClientArea().width;
      editor.setSize(editor.computeSize(width, SWT.DEFAULT));
    }
  }

  private static final class Adapter implements L10n {
    final Messages data;

    Adapter( final Messages data ) {
      this.data = data;
    }

    @Override
    public String get( final String key ) {
      try {
        final Class<?> c = data.getClass();
        final Field field = c.getField(key);
        return (String) field.get(data);
      } catch (Exception ex) {
        return '!' + key;
      }
    }
  }
}
