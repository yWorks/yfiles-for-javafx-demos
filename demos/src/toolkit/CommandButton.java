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
package toolkit;

import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.input.ICommand;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

/**
 * A button that automatically wires up the ICommand and
 * initializes and updates it's visual representation via {@link IconProvider}.
 * It has javafx properties {@link #commandProperty()} and {@link #commandTargetProperty()}
 * to make it easily constructable via FXML. Example:
 * <p>
 *   <code><xmp><CommandButton command="IncreaseZoom" commandTarget="${graphControl}"/></xmp></code>
 * </p>
 * <p>
 *  This constructs a CommandButton that has the
 *  {@link com.yworks.yfiles.view.input.ICommand#INCREASE_ZOOM}
 *  command and the icon provided by {@link IconProvider#ZOOM_IN}.
 *  The graphControl is a {@link com.yworks.yfiles.view.GraphControl} defined in
 *  FXML via <code><xmp><GraphControl fx:id="graphControl"/></xmp></code>
 * </p>
 */
public class CommandButton extends Button {
  public static final String STYLE_CLASS = "command-button";

  private ObjectProperty<ICommand> command = new SimpleObjectProperty<>(this, "command");
  private CanExecuteChangedListener canExecuteChangedEvent;

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

      // set up visual representation
      String name = command.getName();
      try {
        ImageView graphic = IconProvider.valueOf(name);
        this.setGraphic(graphic);
        try {
          Tooltip tooltip = TooltipProvider.valueOf(name);
          this.setTooltip(tooltip);
        } catch (IllegalArgumentException e) {
          this.setTooltip(new Tooltip(name));
        }
      } catch (IllegalArgumentException e) {
        this.setText(name);
      }
      setDisable(!command.canExecute(null));
    }

    this.command.set(command);
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
    if (getCommand() != null)
      canExecuteChangedEvent.onEvent(null, null);
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

  public CommandButton() {
    super();
    getStyleClass().add(STYLE_CLASS);
    canExecuteChangedEvent = new CanExecuteChangedListener();
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
   * Listens to changes on canExecute of the command and disables or enables the button accordingly.
   */
  private class CanExecuteChangedListener implements IEventHandler {

    @Override
    public void onEvent(Object source, IEventArgs args) {
      CommandButton.this.setDisable(!getCommand().canExecute(getCommandParameter(), getCommandTarget()));
    }
  }
}
