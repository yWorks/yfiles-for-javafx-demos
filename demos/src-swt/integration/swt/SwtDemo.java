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
package integration.swt;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This demo shows how to integrate yFiles for JavaFX in a Standard Widget Toolkit (SWT) application.
 * <ul>
 *   <li> A toolbar that provides SWT buttons to change the zoom level of the GraphControl that is a JavaFX control
 *     as well as SWT buttons for undo/redo functionality. </li>
 *   <li> A right click on a node shown in the GraphControl opens a SWT context menu and allows the user to delete the
 *     clicked node from the GraphControl. </li>
 *   <li> On the left side a SWT palette offers nodes with different styles that can be dragged into the GraphControl.
 *   </li>
 * </ul>
 * <p>
 * In JavaFX, the code that creates and manipulates JavaFX classes runs in the JavaFX User thread. In SWT, code that
 * creates and manipulates SWT widgets runs in the event loop thread. When JavaFX is embedded in SWT, these two threads
 * are the same. This means that there are no restrictions when calling methods defined in one toolkit from the other.
 * </p>
 * <p>
 * To simplify matters the drag data is provided in text format: we convert an enum constant to text when dragging
 * starts and convert the text back to the enum constant when the node is dropped. Have a look at
 * <a href="http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet319.java">snippet 319</a>
 * from the Eclipse resource web page that shows how to implement drag and drop with a custom data transfer type from
 * SWT to AWT/Swing.
 * </p>
 * <p>
 * Note that you need to add a <code>swt.jar</code> to the classpath/libraries/dependencies to run this demo. The
 * <code>swt.jar</code> must be the same bit version as the JVM on which the demo runs.The <code>swt.jar</code> can be
 * downloaded form the <a href="http://www.eclipse.org/swt/" target="_blank">SWT homepage</a>. You can use the Ant build
 * script located in the current folder that downloads the <code>swt.jar</code> on demand and run this demo.
 * </p>
 * <p>
 * Note that you need to add <code>javafx-swt.jar</code> to the classpath/libraries/dependencies to run this demo with
 * Java 9.
 * </p>
 * <p>
 * Note that the IDE IntelliJ IDEA marks the usages of {@link FXCanvas} as incorrect. This seems to be a problem of the
 * IDE. The demo can be compiled and run anyway.
 * </p>
 */
public class SwtDemo {
  private static final int PALETTE_WIDTH = 180;
  private static final int PALETTE_HEIGHT = 35;

  private GraphControl graphControl;
  // handles drop events from a SWT component
  private SwtNodeDropInputMode dropMode;

  /**
   * Initializes and shows the user interface.
   */
  private void initialize() {
    // initialize the SWT part of the user interface
    Display display = new Display();
    Shell shell = new Shell(display);
    initializeSwt(shell);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
  }

  /**
   * Initializes the SWT part of the user interface.
   * @param shell the SWT window
   */
  private void initializeSwt(Shell shell) {
    // create a window with title and icon
    shell.setText("SWT Integration Demo - yFiles for JavaFX");
    shell.setImage(loadImage(shell.getDisplay(), "logo_128.png"));
    shell.setLayout(new GridLayout(3, false));
    shell.setSize(1400, 850);

    // create a (still empty) toolbar on the top
    // the toolbar is populated later on when the application's GraphControl
    // instance is available
    ToolBar toolBar = new ToolBar(shell, SWT.HORIZONTAL);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    toolBar.setLayoutData(gridData);

    // create a palette on the left side
    Table palette = new Table(shell, SWT.SINGLE);
    gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.widthHint = PALETTE_WIDTH;
    palette.setLayoutData(gridData);

    // initialize the Java FX part of the user interface
    // create a component to embed JavaFX content into SWT applications
    FXCanvas fxCanvas = new FXCanvas(shell, SWT.NONE);
    initializeFX(fxCanvas);
    gridData = new GridData(GridData.FILL_BOTH);
    fxCanvas.setLayoutData(gridData);
    // the toolbar can be populated with buttons for undo/redo functionality and
    // changing the GraphControl's zoom level as soon as the application's
    // GraphControl instance has ben created
    populateToolBar(toolBar);
    populatePalette(palette);

    // create a Text control showing the help text on the right side
    Text helpPane = createHelpPane(shell);
    gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.widthHint = 300;
    helpPane.setLayoutData(gridData);
  }

  /**
   * Populates the palette containing the nodes which may be dragged into the GraphComponent.
   * @param palette the table to populate with nodes
   */
  private Table populatePalette(Table palette) {
    for (NodeTemplate template : NodeTemplate.values()) {
      // create a table row for each node template
      TableItem item = new TableItem(palette, SWT.CENTER);
      // show an image and a description of the node template in the palette
      ImageData imageData = SWTFXUtils.fromFXImage(template.createImage(), null);
      if (imageData != null) {
        item.setImage(new Image(palette.getDisplay(), imageData));
      }
      item.setText(template.description());
      // set the node template as data of the table row
      // we use this later to generate a drag data
      item.setData(template.name());
    }

    // specify the size of the table cells (seems to be necessary for SWT 4.4 on Linux)
    palette.addListener(SWT.MeasureItem, event -> {
      event.width = Math.max(PALETTE_WIDTH, event.width);
      event.height = Math.max(PALETTE_HEIGHT, event.height);
    });

    // enable the palette as a drag source
    DragSource dragSource = new DragSource(palette, DND.DROP_COPY);
    // provide drag data in text format
    Transfer[] transfers = new Transfer[]{TextTransfer.getInstance()};
    dragSource.setTransfer(transfers);
    dragSource.addDragListener(new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
        // SWT does not allow us to get the drag data while dragging over. However,
        // since we need the data for the preview, we store it in a separate variable.
        // set the name of the enum constant of the selected template as drag data
        dropMode.setDragData(palette.getSelection()[0].getData());

        // since our GraphControl has its own preview we prevent the image of
        // the template to be shown as preview by setting the event image to a
        // blank image
        event.image = new Image(palette.getDisplay(), 1, 1);
      }

      @Override
      public void dragSetData(DragSourceEvent event) {
        // set the name of the enum constant of the selected template as drag data
        event.data = palette.getSelection()[0].getData();
      }

      @Override
      public void dragFinished(DragSourceEvent event) {
        event.data = null;
        dropMode.setDragData(null);
      }
    });

    return palette;
  }

  /**
   * Populates the toolbar with buttons for undo/redo functionality and changing
   * the given GraphControl's zoom level.
   * @param toolBar the toolbar to add the buttons to
   */
  private void populateToolBar(ToolBar toolBar) {
    addButton(toolBar, "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphControl);
    addButton(toolBar, "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphControl);
    addButton(toolBar, "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphControl);
    new ToolItem(toolBar, SWT.SEPARATOR);
    addButton(toolBar, "undo-16.png", ICommand.UNDO, null, graphControl);
    addButton(toolBar, "redo-16.png", ICommand.REDO, null, graphControl);
  }

  /**
   * Adds a {@link ToolItem} wired up with the given {@link com.yworks.yfiles.view.input.ICommand} to the given toolbar.
   * @param toolBar   the toolbar to add the button to
   * @param icon      the icon to show on the button
   * @param command   the command to execute when the button is hit
   * @param parameter the parameter for the execution of the command
   * @param target    the target to execute the command on
   */
  private static void addButton(ToolBar toolBar, String icon, ICommand command, Object parameter, Control target) {
    ToolItem button = new ToolItem(toolBar, SWT.PUSH);
    button.setImage(loadImage(toolBar.getDisplay(), icon));
    button.setToolTipText(command.toString());

    // execute the command when the button is selected
    button.addListener(SWT.Selection, event -> command.execute(parameter, target));

    // enable/disable depending of the command's state
    command.addCanExecuteChangedListener((source, args) -> {
      // avoid accessing SWT control if they are disposed (when exiting the application)
      if (!button.isDisposed()) {
        button.setEnabled(command.canExecute(parameter, target));
      }
    });
  }

  /**
   * Initializes the JavaFX part of the user interface.
   * @param fxCanvas the canvas to add the scene to
   */
  private void initializeFX(FXCanvas fxCanvas) {
    graphControl = new GraphControl();
    Scene scene = new Scene(graphControl);
    fxCanvas.setScene(scene);

    // Initialize the input mode and enable context menu for nodes.
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setContextMenuInputMode(new SwtContextMenuInputMode(fxCanvas));
    inputMode.setContextMenuItems(GraphItemTypes.NODE);
    inputMode.addPopulateItemContextMenuListener(this::populateNodeContextMenu);

    // activate drag and drop from the palette
    dropMode = new SwtNodeDropInputMode();
    // we identify the group nodes during a drag by the type of its style
    dropMode.setIsGroupNodePredicate(node -> node.getStyle() instanceof PanelNodeStyle);
    dropMode.setTransferMode(TransferMode.COPY);
    // Enables preview during drag operations.
    // Keep in mind that, prior to JDK 8u40, due to an issue in the DnD handling in FXCanvas (RT-37906),
    // using the preview feature of the yFiles library may cause problems when dropping a dragged node
    // outside of the GraphControl.
    // To prevent this, the native SWT preview, which shows a simple image, can also be used at this point.
    // To do this, just disable the option here instead (setShowPreview(false);) and adjust the
    // TableHandler.dragStart method.
    dropMode.setPreviewEnabled(true);
    dropMode.setEnabled(true);
    inputMode.setNodeDropInputMode(dropMode);

    // enable grouping and undo support
    inputMode.setGroupingOperationsAllowed(true);

    graphControl.setInputMode(inputMode);

    // load a sample graph and configure the default node style after the GraphControl has got its size
    graphControl.heightProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (newValue.doubleValue() > 0) {
          graphControl.heightProperty().removeListener(this);
          initializeGraph();
        }
      }
    });
  }

  /**
   *  Populates the context menu for nodes.
   */
  private void populateNodeContextMenu(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
    if (args.getItem() instanceof INode) {
      INode node = (INode) args.getItem();
      // The return type of the following method is Object to be able to support context menus of different Java GUI
      // toolkits. By default this is an instance of JavaFX ContextMenu, but our SwtContextMenuInputMode specifies
      // a SWT Menu to be used as context menu control.
      Menu contextMenu = (Menu) args.getMenu();
      // add menu item to delete the clicked node on SWT event thread
      addDeleteNodeMenuItem(contextMenu, node);
      args.setHandled(true);
    }
  }

  /**
   * Adds a {@link org.eclipse.swt.widgets.MenuItem} to the given {@link org.eclipse.swt.widgets.Menu context menu}
   * that enables the user to delete the given node.
   * @param contextMenu the context menu to add the menu item
   * @param node        the node to delete with the context menu
   */
  private void addDeleteNodeMenuItem(Menu contextMenu, INode node) {
    MenuItem deleteItem = new MenuItem(contextMenu, SWT.PUSH);
    deleteItem.setText("Delete node");
    deleteItem.setImage(loadImage(contextMenu.getDisplay(), "delete3-16.png"));
    deleteItem.addListener(SWT.Selection, e -> graphControl.getGraph().remove(node));
  }

  /**
   * Creates the pane that shows a help text about the demo.
   * @param shell the SWT window
   */
  private static Text createHelpPane(Shell shell) {
    Text text = new Text(shell, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
    try {
      URI file = SwtDemo.class.getResource("resources/help.html").toURI();
      String help = new String(Files.readAllBytes(Paths.get(file)));
      text.setText(help);
    } catch (Exception e) {
      text.setText("Could not resolve help text. Please ensure that your build process or IDE adds the " +
                "help.html file to the class path.");
    }
    return text;
  }

  /**
   * Enables grouping and undo support, loads a sample graph and initializes the default node style.
   */
  private void initializeGraph() {
    // enable undo support
    graphControl.getGraph().setUndoEngineEnabled(true);

    // load the sample graph
    try {
      String filename = getClass().getResource("resources/example.graphml").toExternalForm();
      graphControl.importFromGraphML(filename);
      ShinyPlateNodeStyle style = new ShinyPlateNodeStyle();
      style.setPaint(Color.ORANGE);
      graphControl.getGraph().getNodeDefaults().setStyle(style);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads an {@link org.eclipse.swt.graphics.Image} of the given resource file.
   */
  private static Image loadImage(Device device, String fileName) {
    return new Image(device, SwtDemo.class.getResourceAsStream("/resources/" + fileName));
  }

  public static void main(String[] args) {
    (new SwtDemo()).initialize();
  }
}
