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
package input.singleselection;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.KeyboardInputModeBinding;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.WebViewUtils;

import java.io.IOException;

/**
 * Configure {@link com.yworks.yfiles.view.input.GraphEditorInputMode"/> to enable single selection
 * mode for interaction.
 * <p>
 * All default gestures that result in more than one item selected at a time are either switched
 * off or changed so only one item gets selected. This requires some configuration that is done in
 * {@link SingleSelectionDemo#enableSingleSelection(boolean)}. This method also restores the default selection behavior if
 * single selection is disabled.
 * </p>
 */
public class SingleSelectionDemo extends DemoApplication {
  public GraphControl graphControl;
  public WebView help;
  public ToggleButton toggleSelectionModeButton;

  // the previously set multi-selection recognizer
  private IEventRecognizer oldMultiSelectionRecognizer;
  //the previously set select pasted items
  private GraphItemTypes oldPasteItems;

  // custom command binding for 'toggle item selection'
  private KeyboardInputModeBinding customToggleSelectionBinding;

  /**
   * Initializes this demo by configuring the graph defaults and the input mode, enabling single selection
   * and loading the sample graph.
   */
  public void initialize() {
    // setup the help text on the right side.
    WebViewUtils.initHelp(help, this);

    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.DARKORANGE);
    graphControl.getGraph().getNodeDefaults().setStyle(nodeStyle);

    GraphEditorInputMode mode = new GraphEditorInputMode();
    graphControl.setInputMode(mode);
    oldPasteItems = mode.getPasteSelectableItems();

    // initially enable single selection
    enableSingleSelection(true);

    // loads the example graph
    loadSampleGraph();
  }

  /**
   * Called right after stage is loaded.
   * In JavaFX, nodes don't have a width or height until the stage is displayed and the scene graph is calculated.
   * As {@link #initialize()} is called right after a node is created, but before displayed, we have to update
   * the view port later.
   */
  public void onLoaded() {
    graphControl.fitGraphBounds();
  }

  /**
   * Loads a sample graph from GraphML for this demo.
   */
  private void loadSampleGraph() {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/example.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Enables or disables the single selection feature.
   */
  private void enableSingleSelection(boolean enable) {
    GraphEditorInputMode mode = (GraphEditorInputMode) graphControl.getInputMode();
    if (enable) {
      // remember old recognizer so we can restore it later
      oldMultiSelectionRecognizer = mode.getMultiSelectionRecognizer();

      // disable marquee selection
      mode.getMarqueeSelectionInputMode().setEnabled(false);
      // disable multi selection with Ctrl-Click
      mode.setMultiSelectionRecognizer(IEventRecognizer.NEVER);

      // deactivate command that can lead to multi selection
      mode.getAvailableCommands().remove(ICommand.TOGGLE_ITEM_SELECTION);
      mode.getAvailableCommands().remove(ICommand.SELECT_ALL);

      // remove the default commands that are responsible to extend the selection via keyboard
      mode.getNavigationInputMode().getAvailableCommands().remove(ICommand.EXTEND_SELECTION_LEFT);
      mode.getNavigationInputMode().getAvailableCommands().remove(ICommand.EXTEND_SELECTION_RIGHT);
      mode.getNavigationInputMode().getAvailableCommands().remove(ICommand.EXTEND_SELECTION_UP);
      mode.getNavigationInputMode().getAvailableCommands().remove(ICommand.EXTEND_SELECTION_DOWN);

      // add custom binding for toggle item selection
      customToggleSelectionBinding = mode.getKeyboardInputMode().addCommandBinding(ICommand.TOGGLE_ITEM_SELECTION,
          this::executedToggleItemSelection, this::canExecuteToggleItemSelection);

      // disable selection of (possibly multiple) items
      oldPasteItems = mode.getPasteSelectableItems();
      mode.setPasteSelectableItems(GraphItemTypes.NONE);

      // also clear the selection - even though the setup works when more than one item is selected, it looks a bit strange
      graphControl.getSelection().clear();
    } else {
      // restore old settings
      mode.getMarqueeSelectionInputMode().setEnabled(true);
      mode.setMultiSelectionRecognizer(oldMultiSelectionRecognizer);
      mode.setPasteSelectableItems(oldPasteItems);

      // re-activate commands
      mode.getAvailableCommands().add(ICommand.TOGGLE_ITEM_SELECTION);
      mode.getAvailableCommands().add(ICommand.SELECT_ALL);

      // re-insert the default commands to extend the selection
      mode.getNavigationInputMode().getAvailableCommands().add(ICommand.EXTEND_SELECTION_LEFT);
      mode.getNavigationInputMode().getAvailableCommands().add(ICommand.EXTEND_SELECTION_RIGHT);
      mode.getNavigationInputMode().getAvailableCommands().add(ICommand.EXTEND_SELECTION_UP);
      mode.getNavigationInputMode().getAvailableCommands().add(ICommand.EXTEND_SELECTION_DOWN);

      // remove custom binding for toggle item selection
      customToggleSelectionBinding.remove();
    }
  }

  /**
   * Checks whether an <code>IModelItem</code> can be determined whose selection state can be toggled.
   */
  private boolean canExecuteToggleItemSelection(ICommand command, Object parameter, Object sender) {
    // if we have an item, the command can be executed
    IModelItem modelItem = parameter instanceof IModelItem ? (IModelItem) parameter : graphControl.getCurrentItem();
    return modelItem != null;
  }

  /**
   * Custom command handler that allows toggling the selection state of an item respecting the single selection policy.
   */
  private boolean executedToggleItemSelection(ICommand command, Object parameter, Object sender) {
    // get the item
    IModelItem modelItem = parameter instanceof IModelItem ? (IModelItem) parameter : graphControl.getCurrentItem();
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphControl.getInputMode();

    // check if it allowed to be selected
    if (modelItem != null && graphControl.getGraph().contains(modelItem) && inputMode.getSelectableItems().is(modelItem)) {
      boolean isSelected = inputMode.getGraphSelection().isSelected(modelItem);
      if (isSelected) {
        // the item is selected and needs to be unselected - just clear the selection
        inputMode.getGraphSelection().clear();
      } else {
        // the items is unselected - unselect all other items and select the currentItem
        inputMode.getGraphSelection().clear();
        inputMode.setSelected(modelItem, true);
      }
      return true;
    }
    return false;
  }

  public void toggleSelectionMode() {
    enableSingleSelection(toggleSelectionModeButton.isSelected());
  }

  public static void main(String[] args) {
    launch(args);
  }
}
