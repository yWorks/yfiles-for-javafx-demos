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
package integration.swing;

import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;


/**
 * This demo shows how to integrate yFiles for JavaFX in a Swing application.
 * <ul>
 *   <li> A toolbar that provides Swing buttons to change the zoom level of the GraphControl that is a JavaFX control
 *     as well as Swing buttons for undo/redo functionality. </li>
 *   <li> A right click on a node shown in the GraphControl opens a Swing context menu and allows the user to delete the
 *     clicked node from the GraphControl. </li>
 *   <li> On the left side a Swing palette offers nodes with different styles that can be dragged into the GraphControl.
 *   </li>
 * </ul>
 * <p>
 * JavaFX data should be accessed only on the JavaFX User thread. Whenever JavaFX data should be changed, the code must
 * be wrapped into a Runnable object and call the <code>Platform.runLater</code> method.
 * </p>
 * <p>
 * Swing data should be changed only on the EDT. To ensure that the code is implemented on the EDT, it must be wrapped
 * into a Runnable object and call the <cod>EventQueue.invokeLater</cod> method.
 * </p>
 * <p>
 * To simplify matters the drag data is provided in text format: we convert an enum constant to text when dragging
 * starts and convert the text back to the enum constant when the node is dropped.
 * </p>
 */
public class SwingDemo {
  private GraphControl graphControl;
  // handles drop events from a Swing component
  private SwingNodeDropInputMode dropMode;

  /**
   * Initializes and shows the user interface.
   */
  private void initialize() {
    initializeLnF();

    // component to embed JavaFX content into Swing applications
    JFXPanel fxPanel = new JFXPanel();

    // the JavaFX part of the user interface must be initialized on the JavaFX thread
    Platform.runLater(() -> initializeFX(fxPanel));
  }

  /**
   * Initializes the Swing part of the user interface.
   * @param fxPanel the panel containing the JavaFX scene
   */
  private void initializeSwing(JFXPanel fxPanel) {
    JFrame frame = new JFrame("Swing Integration Demo - yFiles for JavaFX");
    frame.setIconImages(Arrays.asList(
        getImageIcon("logo_29.png").getImage(),
        getImageIcon("logo_36.png").getImage(),
        getImageIcon("logo_48.png").getImage(),
        getImageIcon("logo_57.png").getImage(),
        getImageIcon("logo_129.png").getImage()
    ));

    Container contentPane = frame.getContentPane();
    contentPane.add(createToolBar(), BorderLayout.NORTH);
    contentPane.add(createHelpPane(), BorderLayout.EAST);
    contentPane.add(createPalette(), BorderLayout.WEST);
    contentPane.add(fxPanel, BorderLayout.CENTER);

    frame.setSize(1365, 768);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  /**
   * Creates and populates a toolbar with buttons for undo/redo functionality and changing the given GraphControl's zoom
   * level.
   */
  private JToolBar createToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.add(createButton("plus2-16.png", ICommand.INCREASE_ZOOM, null, graphControl));
    toolBar.add(createButton("minus2-16.png", ICommand.DECREASE_ZOOM, null, graphControl));
    toolBar.add(createButton("fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphControl));
    toolBar.addSeparator();
    toolBar.add(createButton("undo-16.png", ICommand.UNDO, null, graphControl));
    toolBar.add(createButton("redo-16.png", ICommand.REDO, null, graphControl));
    return toolBar;
  }

  /**
   * Creates a {@link javax.swing.JButton} wired up with the given {@link ICommand}.
   * @param icon      the icon to show on the button
   * @param command   the command to execute when the button is hit
   * @param parameter the parameter for the execution of the command
   * @param target    the target to execute the command on
   */
  private static JButton createButton(String icon, ICommand command, Object parameter, Control target) {
    JButton button = new JButton();
    button.setIcon(getImageIcon(icon));
    button.setToolTipText(command.toString());

    // execute the command when the button is selected
    button.addActionListener(e ->
        // Execute the command on the JavaFX thread. This call is most likely to alter the
        // JavaFX scene graph which is only allowed on the JavaFX application thread.
        Platform.runLater(() -> command.execute(parameter, target)));

    // enable/disable depending of the command's state
    command.addCanExecuteChangedListener((source, args) -> {
      // Find out if the command can be executed on the JavaFX thread. Note that it is normally safe
      // to call the canExecute method on the EDT, because in most cases this call will just
      // gather information and not alter the JavaFX scene graph. So this is highly precautious.
      boolean newCanExecuteState = command.canExecute(parameter, target);
      // set the state of the Swing button on the EDT
      EventQueue.invokeLater(() -> button.setEnabled(newCanExecuteState));
    });
    return button;
  }

  /**
   * Creates the pane that shows a help text about the demo.
   */
  private JComponent createHelpPane() {
    URL url = SwingDemo.class.getResource("resources/help.html");
    try {
      JEditorPane editorPane = url != null
          ? new JEditorPane(url)
          : new JEditorPane("text/plain", "Could not resolve help text. Please ensure that your build process or IDE adds the " +
                    "help.html file to the class path.");
      editorPane.setEditable(false);
      editorPane.setPreferredSize(new Dimension(300, 250));
      return new JScrollPane(editorPane);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Creates a palette containing the nodes which may be dragged into the GraphControl.
   */
  private JComponent createPalette() {
    JList<NodeTemplate> palette = new JList<>();
    palette.setListData(NodeTemplate.values());
    palette.setCellRenderer(new NodeTemplateCellRenderer());
    palette.setDragEnabled(true);
    // set a TransferHandler that returns the name of the selected NodeTemplate
    // to be transferred in the drag from the palette to the GraphControl
    palette.setTransferHandler(new TransferHandler() {
      @Override
      public int getSourceActions(JComponent c) {
        return COPY;
      }

      @Override
      protected Transferable createTransferable(JComponent c) {
        NodeTemplate nodeTemplate = palette.getSelectedValue();
        // JFXPanel does not allow us to get the drag data while dragging over. However,
        // since we need the data for the preview, we store it in a separate variable.
        // set the name of the enum constant of the selected template as drag data
        dropMode.setDragData(nodeTemplate.name());

        // since our GraphControl has its own preview we prevent the image of
        // the template to be shown as preview by setting the event image to a
        // blank image
        setDragImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        return new StringSelection(nodeTemplate.name());
      }

      @Override
      protected void exportDone(JComponent source, Transferable data, int action) {
        dropMode.setDragData(null);
      }
    });
    return new JScrollPane(palette);
  }

  /**
   * Initializes the JavaFX part of the user interface.
   * @param fxPanel the panel to add the scene to
   */
  private void initializeFX(JFXPanel fxPanel) {
    // create a scene graph with a GraphControl instance
    graphControl = new GraphControl();
    Scene scene = new Scene(graphControl);
    fxPanel.setScene(scene);

    // initialize the input mode and enable context menu for nodes
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setContextMenuInputMode(new SwingContextMenuInputMode(fxPanel));
    inputMode.setContextMenuItems(GraphItemTypes.NODE);
    inputMode.addPopulateItemContextMenuListener(this::populateNodeContextMenu);

    // activate drag and drop from the palette
    dropMode = new SwingNodeDropInputMode();
    // we identify the group nodes during a drag by the type of its style
    dropMode.setIsGroupNodePredicate(node -> node.getStyle() instanceof PanelNodeStyle);
    dropMode.setTransferMode(TransferMode.COPY);
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

    // the JavaFX part of the UI is now complete
    EventQueue.invokeLater(() -> initializeSwing(fxPanel));
  }

  /**
   *  Populates the context menu for nodes.
   */
  private void populateNodeContextMenu(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
    if (args.getItem() instanceof INode) {
      INode node = (INode) args.getItem();
      // The return type of the following method is Object to be able to support context menus of different Java GUI
      // toolkits. By default this is an instance of JavaFX ContextMenu, but our SwingContextMenuInputMode specifies
      // the Swing JPopupMenu to be used as context menu control.
      JPopupMenu contextMenu = (JPopupMenu) args.getMenu();
      // add an Action tha the Swing context menu on the EDT
      ThreadUtils.invokeAndWait(() -> SwingDemo.this.addDeleteNodeAction(contextMenu, node));
      args.setHandled(true);
    }
  }

  /**
   * Adds a {@link javax.swing.Action} to the given {@link javax.swing.JPopupMenu} that enables the
   * user to delete the given node.
   * @param contextMenu the context menu to add the action
   * @param node        the node to delete with the context menu
   */
  private void addDeleteNodeAction(JPopupMenu contextMenu, INode node) {
    Action deleteNodeAction = new AbstractAction("Delete node", getImageIcon("delete3-16.png")) {
      @Override
      public void actionPerformed(ActionEvent e) {
        // remove the node from the GraphControl on the JavaFX thread
        Platform.runLater(() -> graphControl.getGraph().remove(node));
      }
    };
    contextMenu.add(deleteNodeAction);
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
   * Finds an {@link ImageIcon} resource with the specified name.
   */
  private static ImageIcon getImageIcon(String name) {
    return new ImageIcon(SwingDemo.class.getResource("/toolkit/resources/" + name));
  }

  /**
   * Initialize the system look and feel for Windows and macOS.
   * <p>
   * On Linux the default Java look and feel is used because synchronous calls
   * to Swing's event dispatch thread in {@link SwingContextMenuInputMode}'s
   * methods (which are required for yFiles for Java's context menu related
   * features to work properly) result in dead locks when using the system look
   * and feel on GTK-based desktop environments like the popular GNOME desktop
   * environment.
   * </p>
   */
  private static void initializeLnF() {
    if (ThreadUtils.macOSX || ThreadUtils.windows) {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    EventQueue.invokeLater((new SwingDemo())::initialize);
  }
}
