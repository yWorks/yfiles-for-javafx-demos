/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package toolkit;

import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.KeyboardInputModeKeyBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;

import java.util.Collection;

/**
 * A MenuItem that automatically wires up the ICommand and
 * initializes and updates it's visual representation via {@link IconProvider}.
 * It has javafx properties {@link #commandProperty()} and {@link #commandTargetProperty()}
 * to make it easily constructable via FXML. Example:
 * <p>
 *   <code><xmp><CommandMenuItem command="IncreaseZoom" commandTarget="${graphControl}"/></xmp></code>
 * </p>
 * <p>
 *  This constructs a CommandMenuItem that has the
 *  {@link com.yworks.yfiles.view.input.ICommand#INCREASE_ZOOM}
 *  command and the icon provided by {@link IconProvider#ZOOM_IN}.
 *  The graphControl is a {@link com.yworks.yfiles.view.GraphControl} defined in
 *  FXML via <code><xmp><GraphControl fx:id="graphControl"/></xmp></code>
 * </p>
 */
public class CommandMenuItem extends MenuItem {

  private ObjectProperty<ICommand> command = new SimpleObjectProperty<>(this, "command");
  private CanExecuteChangedHandler canExecuteChangedEvent;
  private CommandMenuItem.CanExecuteChangeListener canExecuteChangeListener;

  public ObjectProperty<ICommand> commandProperty(){
    return command;
  }

  public ICommand getCommand(){
    return command.get();
  }

  public void setCommand (final ICommand command){
    if (command != null){
      // remove listener of old command if needed
      ICommand oldCommand = getCommand();
      if (oldCommand != null){
        oldCommand.removeCanExecuteChangedListener(canExecuteChangedEvent);
      }
      command.addCanExecuteChangedListener(canExecuteChangedEvent);
      commandParameterProperty().addListener(canExecuteChangeListener);
      commandTargetProperty().addListener(canExecuteChangeListener);
    }

    this.command.set(command);

    if (getText() == null) {
      this.setText(getCommand().getName());
    }
  }

  // command parameter property: anything (type Object)

  private ObjectProperty<Object> commandParameter = new SimpleObjectProperty<>(this, "commandParameter");

  public ObjectProperty<Object> commandParameterProperty(){
    return commandParameter;
  }

  public Object getCommandParameter(){
    return commandParameter.get();
  }

  public void setCommandParameter (Object commandParameter){
    this.commandParameter.set(commandParameter);
  }

  // command target property: a Control (most commonly a CanvasControl)

  private ObjectProperty<Control> commandTarget = new SimpleObjectProperty<>(this, "commandTarget");

  public ObjectProperty<Control> commandTargetProperty(){
    return commandTarget;
  }

  public Control getCommandTarget(){
    return commandTarget.get();
  }

  public void setCommandTarget (Control commandTarget){
    this.commandTarget.set(commandTarget);
  }

  public CommandMenuItem() {
    super();
    canExecuteChangedEvent = new CanExecuteChangedHandler();
    canExecuteChangeListener = new CanExecuteChangeListener();
    setOnAction(actionEvent -> {
      ICommand command1 = getCommand();
      Control target = getCommandTarget();
      Object parameter = getCommandParameter();
      if (command1.canExecute(parameter, target)){
        command1.execute(parameter, target);
      }
    });
  }

  /**
   * Updates the accelerator for the action associated to the menu item
   * depending on the key bindings associated to the menu item's command.
   */
  void updateAccelerator() {
    ICommand cmd = getCommand();
    if (cmd == null) {
      return;
    }

    KeyboardInputMode kim = getKeyboardInputMode(getCommandTarget());
    if (kim == null) {
      return;
    }

    Object param = getCommandParameter();
    Collection<KeyboardInputModeKeyBinding> bindings = kim.getKeyBindings(cmd);
    for (KeyboardInputModeKeyBinding binding : bindings) {
      Object bp = binding.getCommandParameter();
      if (param == null ? bp == null : param.equals(bp)) {
        setAccelerator(binding.getAccelerator());
        break;
      }
    }
  }

  private static KeyboardInputMode getKeyboardInputMode(Control control) {
    if (control instanceof GraphControl) {
      IInputMode mode = ((GraphControl) control).getInputMode();
      if (mode instanceof GraphEditorInputMode) {
        return ((GraphEditorInputMode) mode).getKeyboardInputMode();
      } else if (mode instanceof GraphViewerInputMode) {
        return ((GraphViewerInputMode) mode).getKeyboardInputMode();
      }
    }

    return null;
  }

  /**
   * Listens to changes on canExecute of the command and disables or enables the button accordingly.
   */
  private class CanExecuteChangedHandler implements IEventHandler {

    @Override
    public void onEvent(Object source, IEventArgs args) {
      CommandMenuItem.this.setDisable(!getCommand().canExecute(getCommandParameter(), getCommandTarget()));
      updateAccelerator();
    }
  }

  /**
   * Listens to changes on canExecute of the command and disables or enables the button accordingly.
   */
  private class CanExecuteChangeListener implements ChangeListener {

    @Override
    public void changed(ObservableValue observableValue, Object o, Object o2) {
      canExecuteChangedEvent.onEvent(null, null);
    }
  }
}
