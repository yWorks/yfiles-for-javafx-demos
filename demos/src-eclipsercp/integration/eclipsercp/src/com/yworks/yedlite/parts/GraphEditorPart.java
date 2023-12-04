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
package com.yworks.yedlite.parts;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.yworks.yedlite.popup.ContextMenuFactory;
import com.yworks.yedlite.popup.SwtContextMenuInputMode;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.UndoEngine;
import com.yworks.yfiles.graph.styles.GroupNodeStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yedlite.dragdrop.SwtEdgeDropInputMode;
import com.yworks.yedlite.dragdrop.SwtNodeDropInputMode;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.LabelSnapContext;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * The main view that contains the GraphControl and provides interactive
 * features. The view resembles the main features of the SimpleEditorDemo: The
 * graph is editable by common means (mouse and keyboard shortcuts) and the
 * toolbar provides access to common features like cut/copy/paste, grouping,
 * snapping, performing a layout calculation and so on.
 */
public class GraphEditorPart {
  @Inject
  MDirtyable dirty;

  @Inject
  IEclipseContext ctx;

  @Inject
  EMenuService menuService;

  @Inject
  IEventBroker broker;

  @Inject
  MPart mPart;

  /**
   * The identifier for the event that is fired when an item is selected in the
   * GraphControl.
   */
  public static final String ITEM_SELECTED_EVENT = "ItemSelected";

  /**
   * The identifier for the event that is fired when an item is deselected in
   * the GraphControl.
   */
  public static final String ITEM_DESELECTED_EVENT = "ItemDeselected";

  /**
   * The identifier for the event that is fired when the GraphControl is
   * shown.
   */
  public static final String GRAPHCONTROL_EVENT = "GraphControlBuilt";

  private static final String POPUP_ID = "yed-lite-eclipse-rcp.popupmenu.editor";

  private static final String EMPTY_POPUP_ID = "yed-lite-eclipse-rcp.popupmenu.editor.empty";
  
  private GraphControl graphControl;
  private FXCanvas canvas;
  private Object currentUndoToken;

  /**
   * We override the createPartControl method in FXViewPart canvas to hook our
   * toolbar and menu into the view.
   */
  @PostConstruct
  public void initPart(Composite parent) {
    canvas = new FXCanvas(parent, SWT.NONE);
    canvas.setScene(createFxScene());

    menuService.registerContextMenu(canvas, POPUP_ID);
  }

  /**
   * Cleanup on disposal, i.e. clear the undo engine, reset the currentToken
   * and remove the GraphControl and graph from the context.
   */
  @PreDestroy
  public void preDestroy() {
    // clear the currentUndoToken
    currentUndoToken = null;

    // remove the GraphControl used by this part from the context
    ContextUtils.setGraphControl(ctx, null);
  }

  private Scene createFxScene() {
      graphControl = new GraphControl();

    configureGraphControl(graphControl);
    configureGraph(graphControl);
    ContextUtils.setGraphControl(ctx, graphControl);

    // On selection of something in the GraphControl, inform listeners to
    // the selection event.
    // A listener in this demo is the DescriptionViewPart, for example.
    graphControl.getSelection().addItemSelectionChangedListener(
            ( source, args ) -> sendEvent(args.isItemSelected() ? ITEM_SELECTED_EVENT : ITEM_DESELECTED_EVENT, args.getItem()));

    UndoEngine undoEngine = graphControl.getGraph().getUndoEngine();
    undoEngine.addPropertyChangedListener(
            ( source, args ) -> dirty.setDirty(currentUndoToken != null && !currentUndoToken.equals(undoEngine.getToken())));
    currentUndoToken = undoEngine.getToken();

    return new Scene(new BorderPane(graphControl));
  }

  private void configureGraphControl(GraphControl graphControl) {
    // the GraphControl has a transparent background by default, set it to white
    graphControl.setStyle("-fx-background-color: white");

    // create the GraphEditorInputMode that is configured to use the snap
    // context and the appropriate editing context
    GraphEditorInputMode mode = new GraphEditorInputMode();
    mode.setGroupingOperationsAllowed(true);
    mode.setContextMenuInputMode(new SwtContextMenuInputMode(canvas, graphControl));
    mode.setContextMenuItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    mode.addPopulateItemContextMenuListener(this::populateContextMenu);
    mode.setSnapContext(new GraphSnapContext());
    mode.setLabelSnapContext(new LabelSnapContext());

      // suppress context menus while creating edges
    CreateEdgeInputMode ceim = mode.getCreateEdgeInputMode();
      ceim.addGestureStartingListener((source, args) -> menuService.registerContextMenu(canvas, EMPTY_POPUP_ID));
      ceim.addGestureCanceledListener((source, args) -> menuService.registerContextMenu(canvas, POPUP_ID));
      ceim.addGestureFinishedListener((source, args) -> menuService.registerContextMenu(canvas, POPUP_ID));

    // support edge drops
    final SwtNodeDropInputMode ndim = new SwtNodeDropInputMode();
    ndim.addItemCreatedListener((source, args) -> onFocus());
    mode.setNodeDropInputMode(ndim);
    final SwtEdgeDropInputMode edim = new SwtEdgeDropInputMode();
    edim.setPriority(ndim.getPriority() + 1);
    mode.add(edim);

    graphControl.setInputMode(mode);
    graphControl.setFileIOEnabled(true);
  }

  /**
   * Populates the context menu.
   */
  private void populateContextMenu(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
    new ContextMenuFactory(mPart, POPUP_ID).populateContextMenu(args);
  }

  private void configureGraph(GraphControl graphControl) {
    // Enable folding
    IFoldingView view = new FoldingManager().createFoldingView();
    IGraph graph = view.getGraph();

    // Enable undoability:
    // get the master graph instance and enable undoability support.
    view.getManager().getMasterGraph().setUndoEngineEnabled(true);

    // Configure grouping:
    // configure the group node style.
    // GroupNodeStyle is a nice style especially suited for group nodes
    Color groupNodeColor = Color.web("#CFE2F8FF");
    GroupNodeStyle decoratedStyle = new GroupNodeStyle();
    decoratedStyle.setContentAreaPaint(groupNodeColor);
    decoratedStyle.setTabBackgroundPaint(groupNodeColor);
    CollapsibleNodeStyleDecorator groupStyle = new CollapsibleNodeStyleDecorator(decoratedStyle);
    graph.getGroupNodeDefaults().setStyle(groupStyle);

    // set a different label style and parameter
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setTextAlignment(TextAlignment.LEFT);
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(labelStyle);
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);

    // Configure graph defaults:
    // set the default node style
    RectangleNodeStyle style = new RectangleNodeStyle();
    style.setPaint(Color.ORANGE);
    graph.getNodeDefaults().setStyle(style);
    graph.getNodeDefaults().setStyleInstanceSharingEnabled(false);

    // Configure edge defaults:
    // edge styles shall not be shared so that edges can be styled individually
    graph.getEdgeDefaults().setStyleInstanceSharingEnabled(false);

    graphControl.setGraph(graph);
  }

  @Focus
  public void onFocus() {
    canvas.setFocus();
    graphControl.requestFocus();
  }

  @Persist
  public void save(MPart part, Shell shell) {
    File currentFile = (File) ctx.get(ContextUtils.CURRENT_FILE);

    if (currentFile == null) {
      final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
      dialog.setFilterExtensions(new String[] { "*.graphml" });
      String path = dialog.open();
      if (path != null) {
        currentFile = new File(path);
      } else {
        currentFile = null;
      }
    }

    try {
      GraphControl graphControl = ContextUtils.getGraphControl(ctx);

      if (currentFile != null) {
        String path = currentFile.getAbsolutePath();
        graphControl.exportToGraphML(path);
        part.setLabel(currentFile.getName());
        part.setTooltip(path);

        ctx.set(ContextUtils.CURRENT_FILE, currentFile);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    currentUndoToken = graphControl.getGraph().getUndoEngine().getToken();
    dirty.setDirty(false);
  }

  /**
   * Fires an event using the eclipse framework with the provided arguments.
   *
   * @param eventID the identifier of the event.
   * @param property the information that is carried with the event.
   */
  private void sendEvent(String eventID, Object property) {
    broker.post(eventID, property);
  }
}
