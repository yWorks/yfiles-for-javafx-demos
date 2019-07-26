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
package complete.collapse;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

/**
 * Representation of the current state of a node. Stores if the children of the according node are collapsed or
 * expanded. Also provides an icon to display on the node.
 */
public class StyleTag {
  private BooleanProperty collapsed = new SimpleBooleanProperty(this, "collapsed", false);

  /**
   * Gets the value of property collapsed.
   * @return The collapsed state of the associated node.
   */
  public boolean getCollapsed() {
    return collapsed.get();
  }

  /**
   * Returns a property containing the collapsed state of the associated node.
   * @return The collapsed property.
   */
  public BooleanProperty collapsedProperty() {
    return collapsed;
  }

  /**
   * Sets the value of property collapsed.
   * @param collapsed The collapsed state of the associated node.
   */
  public void setCollapsed(boolean collapsed) {
    this.collapsed.set(collapsed);
  }

  private ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic", getGraphic(getCollapsed()));

  /**
   * Gets the value of property graphic.
   * @return The graphic for the associated node.
   */
  public Node getGraphic() {
    return graphic.get();
  }

  /**
   * Returns a property containing the graphic for the associated node.
   * @return The graphic property.
   */
  public ObjectProperty graphicProperty() {
    return graphic;
  }

  /**
   * Sets the value of property graphic.
   * @param graphic The graphic for the associated node.
   */
  public void setGraphic(Node graphic) {
    this.graphic.set(graphic);
  }

  public StyleTag(boolean collapsed) {
    // update the current graphic when the selected state changes.
    collapsedProperty().addListener((observableValue, oldCollapsed, newCollapsed) -> setGraphic(getGraphic(newCollapsed)));

    // set the initial collapsed state.
    setCollapsed(collapsed);
  }
  
  private Node collapsedGraphic;
  private Node expandedGraphic;

  /**
   * Returns a graphic node according to the collapsed state of the associated node.
   */
  private Node getGraphic(boolean collapsed) {
    if (collapsed) {
      if (collapsedGraphic == null) {
        collapsedGraphic = loadGraphic("PlusIcon.fxml");
      }
      return collapsedGraphic;
    } else {
      if (expandedGraphic == null) {
        expandedGraphic = loadGraphic("MinusIcon.fxml");
      }
      return expandedGraphic;
    }
  }

  /**
   * Loads a graphic node from the given source.
   */
  private Node loadGraphic(String source) {
    try {
      final FXMLLoader fxmlLoader = new FXMLLoader(
          getClass().getResource(source));
      return (Node) fxmlLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
