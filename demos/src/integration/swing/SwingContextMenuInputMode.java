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
package integration.swing;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.input.AbstractContextMenuInputMode;

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Component;

/**
 * An implementation of {@link com.yworks.yfiles.view.input.AbstractContextMenuInputMode} interface that will
 * display a {@link javax.swing.JPopupMenu Swing context menu} when the user right clicks on the {@link
 * CanvasControl} or presses the menu key.
 * <p>
 * Note: the Swing context menu must be accessed and changed on the EDT.
 */
class SwingContextMenuInputMode extends AbstractContextMenuInputMode<JPopupMenu> {
  // the handler that invokes a callback as soon as the menu is closed
  private PopupMenuListener closedHandler;
  // the component in whose space the context menu is to appear
  private Component parent;

  /**
   * Initializes a new <code>SwingContextMenuInputMode</code> instance with a component in that the context menu is to
   * appear.
   * @param parent the component in whose space the context menu is to appear
   */
  SwingContextMenuInputMode(Component parent) {
    this.parent = parent;
  }

  @Override
  protected JPopupMenu createMenu() {
    JPopupMenu[] contextMenu = {null};
    // we create the context menu instance synchronously since we have to return the result instantly
    ThreadUtils.invokeAndWait(() -> contextMenu[0] = new JPopupMenu());
    return contextMenu[0];
  }

  @Override
  protected void showMenu(JPopupMenu menu, PointD viewLocation) {
    ThreadUtils.invokeLater(() -> menu.show(parent, (int) viewLocation.getX(), (int) viewLocation.getY()));
  }

  @Override
  protected void hideMenu(JPopupMenu menu) {
    ThreadUtils.invokeLater(() -> {
      if (menu != null && menu.isVisible()) {
        menu.setVisible(false);
      }
    });
  }

  @Override
  protected void clearMenu(JPopupMenu menu) {
    ThreadUtils.invokeAndWait(menu::removeAll);
  }

  @Override
  protected boolean isMenuEmpty(JPopupMenu menu) {
    boolean[] empty = new boolean[1];
    // we have to do the check synchronously since we have to return the result instantly
    ThreadUtils.invokeAndWait(() -> empty[0] = menu.getComponentCount() == 0);
    return empty[0];
  }

  @Override
  protected void setMenuClosedCallback(JPopupMenu menu, Runnable menuClosedCallback) {
    ThreadUtils.invokeAndWait(() -> {
      // remove the old listener
      if (closedHandler != null) {
        menu.removePopupMenuListener(closedHandler);
        closedHandler = null;
      }

      // add the new one
      if (menuClosedCallback != null) {
        closedHandler = new ClosedHandler(menuClosedCallback);
        menu.addPopupMenuListener(closedHandler);
      }
    });
  }

  /**
   * A {@link javax.swing.event.PopupMenuListener} that invokes a callback when the popup menu is closed.
   */
  private static final class ClosedHandler implements PopupMenuListener {
    private final Runnable menuClosedCallback;

    ClosedHandler(final Runnable menuClosedCallback) {
      this.menuClosedCallback = menuClosedCallback;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      menuClosedCallback.run();
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }
  }
}
