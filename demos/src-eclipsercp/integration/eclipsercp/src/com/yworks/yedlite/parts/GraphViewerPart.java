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

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.UndoEngine;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.yworks.yedlite.popup.ContextMenuFactory;
import com.yworks.yedlite.popup.SwtContextMenuInputMode;
import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.MarqueeSelectionInputMode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The main view that contains the GraphControl and provides interactive
 * features. The view resembles the main features of the SimpleEditorDemo: The
 * graph is editable by common means (mouse and keyboard shortcuts) and the
 * tool-bar provides access to common features like cut/copy/paste, grouping,
 * snapping, performing a layout calculation and so on.
 */
public class GraphViewerPart {
  @Inject
  MDirtyable dirty;

  @Inject
  IEclipseContext ctx;

  @Inject
  EMenuService menuService;

  @Inject
  IEventBroker broker;

  @Inject
  EPartService ePartService;

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
  public static final String GRAPHCONTROL_EVENT = "GraphViewer_PartGraphControlBuilt";

  private static final String POPUP_ID = "yed-lite-eclipse-rcp.popupmenu.editor";

  private GraphControl graphControl;
  private FXCanvas canvas;
  private Object currentUndoToken;

  private IEventHandler nodeEventHandler = new IEventHandler() {
    @Override
    public void onEvent(Object source, IEventArgs args) {
      graphControl.fitGraphBounds();
    }
  };

  /**
   * We override the createPartControl method in FXViewPart canvas to hook our
   * tool-bar and menu into the view.
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

    // On selection of something in the GraphControl, inform listeners to the selection event.
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

    GraphViewerInputMode graphViewerInputMode = new GraphViewerInputMode();

    graphViewerInputMode.setClickableItems(GraphItemTypes.NODE);
    graphViewerInputMode.setFocusableItems(GraphItemTypes.NODE);
    graphViewerInputMode.setSelectableItems(GraphItemTypes.NODE);
    graphViewerInputMode.setMarqueeSelectableItems(GraphItemTypes.NODE);
    MarqueeSelectionInputMode msim = (MarqueeSelectionInputMode)graphViewerInputMode.getMarqueeSelectionInputMode();
    msim.setPressedRecognizer(IEventRecognizer.MOUSE_LEFT_PRESSED.and(IEventRecognizer.CTRL_PRESSED));

    // Disable collapsing and expanding of groups
    graphViewerInputMode.getNavigationInputMode().setCollapseGroupAllowed(true);
    graphViewerInputMode.getNavigationInputMode().setExpandGroupAllowed(true);
    graphViewerInputMode.getNavigationInputMode().setFittingContentAfterGroupActionsEnabled(false);
    graphViewerInputMode.getNavigationInputMode().setUsingCurrentItemForCommandsEnabled(true);

    graphViewerInputMode.setContextMenuInputMode(new SwtContextMenuInputMode(canvas, graphControl));
    graphViewerInputMode.setContextMenuItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    graphViewerInputMode.addPopulateItemContextMenuListener(this::populateContextMenu);

    graphControl.setInputMode(graphViewerInputMode);

    graphControl.setFileIOEnabled(true);

    graphControl.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
          @Override
          public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
            if (newValue.getWidth() > 0 && newValue.getHeight() > 0) {
              graphControl.fitGraphBounds();
              sendEvent(GRAPHCONTROL_EVENT, null);
              graphControl.boundsInParentProperty().removeListener(this);
            }
          }
    });
  }

  /**
   * Populates the context menu.
   */
  private void populateContextMenu(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
    new ContextMenuFactory(mPart, POPUP_ID).populateContextMenu(args);
  }

  private void configureGraph(GraphControl graphControl) {
    IGraph graph = ContextUtils.getGraph(ctx);
    uninstallEditListeners(graphControl.getGraph());

    graphControl.setGraph(graph);

    installEditListeners(graph);

    sendEvent(GRAPHCONTROL_EVENT, null);
  }

  /**
   * Installs listeners such that the neighborhood control is updated if the source graph is edited.
   */
  private void installEditListeners(IGraph graph) {
    if (graph == null) {
      return;
    }

    graph.addNodeCreatedListener(nodeEventHandler);
    graph.addNodeRemovedListener(nodeEventHandler);
    graph.addNodeStyleChangedListener(nodeEventHandler);

  }

  /**
   * Removes the edit listeners.
   */
  private void uninstallEditListeners(IGraph graph) {
    if (graph == null) {
      return;
    }
    graph.removeNodeCreatedListener(nodeEventHandler);
    graph.removeNodeRemovedListener(nodeEventHandler);
    graph.removeNodeStyleChangedListener(nodeEventHandler);

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
