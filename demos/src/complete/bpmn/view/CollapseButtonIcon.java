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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.input.ICommand;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Translate;


class CollapseButtonIcon extends AbstractIcon {

  private static final String STYLESHEET_COLLAPSE_BUTTON = CollapseButtonIcon.class.getPackage().getName().replace(".","/")+"/CollapseButton.css";
  private static final String STYLE_CLASS_COLLAPSE_BUTTON = "collapse-button";

  private final IIcon collapsedIcon;

  private final IIcon expandedIcon;

  private final Paint iconBrush;
  
  private INode node;

  public CollapseButtonIcon( INode node, Paint iconBrush ) {
    this.node = node;
    this.iconBrush = iconBrush;
    collapsedIcon = IconFactory.createStaticSubState(SubState.COLLAPSED, iconBrush);
    expandedIcon = IconFactory.createStaticSubState(SubState.EXPANDED, iconBrush);
  }

  @Override
  public Node createVisual( IRenderContext context ) {
    Button toggleButton = createButton(context, node);
    updateButton(context, toggleButton);
    return toggleButton;
  }

  @Override
  public Node updateVisual( IRenderContext context, Node oldVisual ) {
    Button button = (oldVisual instanceof Button) ? (Button)oldVisual : null;
    if (button == null) {
      return createVisual(context);
    }
    updateButton(context, button);
    return button;
  }

  protected Button createButton(IRenderContext context, INode item) {
    Button toggleButton = new Button();
    toggleButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    toggleButton.getStylesheets().add(STYLESHEET_COLLAPSE_BUTTON);
    toggleButton.getStyleClass().add(STYLE_CLASS_COLLAPSE_BUTTON);
    toggleButton.setMaxSize(getBounds().getWidth(), getBounds().getHeight());
    toggleButton.setPrefSize(getBounds().getWidth(), getBounds().getHeight());
    toggleButton.setManaged(false);
    toggleButton.autosize();

    // update visualization after the button has been toggled.
    toggleButton.setOnAction(event -> {
      ICommand.TOGGLE_EXPANSION_STATE.execute(item, context.getCanvasControl());
      updateButtonGraphic(context, toggleButton, isExpanded(context, item));
    });
    return toggleButton;
  }

  private void updateButton(IRenderContext context, Button toggleButton) {
    collapsedIcon.setBounds(new RectD(PointD.ORIGIN, getBounds().toSizeD()));
    expandedIcon.setBounds(new RectD(PointD.ORIGIN, getBounds().toSizeD()));
    updateButtonGraphic(context, toggleButton, isExpanded(context, node));
  }

  private boolean isExpanded(IRenderContext context, INode item) {
    boolean expanded = true;
    CanvasControl canvas = context != null ? context.getCanvasControl() : null;

    if (canvas != null) {
      IGraph graph = canvas.lookup(IGraph.class);
      if (graph != null) {
        IFoldingView foldingView = graph.lookup(IFoldingView.class);
        if (foldingView != null && foldingView.getGraph().contains(item)) {
          expanded = foldingView.isExpanded(item);
        }
      }
    }
    return expanded;
  }

  private void updateButtonGraphic(IRenderContext context, Button button, boolean expanded) {
    Group group = button.getGraphic() instanceof Group ? (Group) button.getGraphic() : null;

    if (group == null || group.getChildren().size() != 1) {
      group = new Group();
      Node icon = expanded ? expandedIcon.createVisual(context) : collapsedIcon.createVisual(context);
      group.getChildren().add(icon);
      button.setGraphic(group);
    } else {
      Node newIcon = expanded ? expandedIcon.createVisual(context) : collapsedIcon.createVisual(context);
      group.getChildren().set(0, newIcon);
    }
    Translate layout;
    if (button.getTransforms().size() == 1) {
      layout = (Translate) button.getTransforms().get(0);
    } else {
      button.getTransforms().add(layout = new Translate());
    }
    layout.setX(getBounds().getX());
    layout.setY(getBounds().getY());
  }
}
